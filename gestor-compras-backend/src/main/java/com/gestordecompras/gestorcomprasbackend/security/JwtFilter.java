package com.gestordecompras.gestorcomprasbackend.security;

import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.security.JwtService;
import com.gestordecompras.gestorcomprasbackend.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Adicionado import
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Filtro de autenticação JWT que intercepta todas as requisições HTTP.
 *
 * <p>Este filtro é executado <b>antes</b> do {@link UsernamePasswordAuthenticationFilter}
 * na cadeia de filtros do Spring Security. Extende {@link OncePerRequestFilter} para
 * garantir execução única por requisição.</p>
 *
 * <p><b>Fluxo de execução:</b></p>
 * <ol>
 *   <li>Extrai header Authorization da requisição</li>
 *   <li>Valida formato "Bearer {token}"</li>
 *   <li>Extrai token JWT e parseia para obter email do usuário</li>
 *   <li>Carrega dados completos do usuário via {@link UserDetailsServiceImpl}</li>
 *   <li>Valida token (assinatura, expiração, subject)</li>
 *   <li>Se válido, popula {@link SecurityContextHolder} com autenticação</li>
 *   <li>Continua cadeia de filtros</li>
 * </ol>
 *
 * <p><b>Casos tratados:</b></p>
 * <ul>
 *   <li>Sem header Authorization → continua sem autenticar (endpoints públicos)</li>
 *   <li>Header sem "Bearer" → continua sem autenticar</li>
 *   <li>Token inválido/expirado → log debug + continua sem autenticar</li>
 *   <li>Token válido → autentica e continua</li>
 * </ul>
 *
 * <p><b>Thread Safety:</b> SecurityContextHolder usa ThreadLocal, seguro para múltiplas requisições.</p>
 *
 * @since 1.0.0
 * @author Equipe de Desenvolvimento
 * @see JwtService
 * @see SecurityConfig
 * @see OncePerRequestFilter
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final SecretKey key;

    /**
     * Construtor com injeção de dependências.
     *
     * @param jwtService Serviço para validação e extração de dados do JWT
     * @param userDetailsService Service para carregar dados do usuário do banco
     * @param secretKey Secret key para parsear JWT e extrair claims
     */
    public JwtFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService, @Value("${jwt.secret}") String secretKey) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Processa cada requisição HTTP para validar token JWT e autenticar usuário.
     *
     * <p>Executado automaticamente para todas as requisições antes dos demais filtros de segurança.
     * Garante que apenas requisições com token JWT válido sejam autenticadas.</p>
     *
     * <p><b>Lógica de autenticação:</b></p>
     * <ol>
     *   <li>Verifica presença de header Authorization com formato "Bearer {token}"</li>
     *   <li>Se ausente ou formato inválido, continua sem autenticar (permite endpoints públicos)</li>
     *   <li>Extrai token e parseia para obter email do usuário</li>
     *   <li>Verifica se usuário ainda não autenticado na requisição atual (evita reprocessamento)</li>
     *   <li>Carrega dados completos do usuário do banco de dados</li>
     *   <li>Valida token (assinatura, expiração, match de email)</li>
     *   <li>Se válido, cria {@link UsernamePasswordAuthenticationToken} com authorities do usuário</li>
     *   <li>Popula {@link SecurityContextHolder} para disponibilizar autenticação em toda a requisição</li>
     * </ol>
     *
     * <p><b>Tratamento de erros:</b></p>
     * <ul>
     *   <li>Token expirado → log debug, requisição continua sem autenticar (retorna 401 se endpoint protegido)</li>
     *   <li>Token malformado → log debug, requisição continua sem autenticar</li>
     *   <li>Usuário não encontrado → log debug, requisição continua sem autenticar</li>
     * </ul>
     *
     * @param request Requisição HTTP sendo processada
     * @param response Resposta HTTP
     * @param filterChain Cadeia de filtros para continuar processamento
     * @throws ServletException Se erro no processamento do filtro
     * @throws IOException Se erro de I/O
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Se não há header Authorization ou não começa com "Bearer ", continua sem autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extrai token removendo prefixo "Bearer "
            String token = authHeader.substring(7);
            String email = jwtService.extrairUsername(token);

            // Autentica apenas se email válido e usuário ainda não autenticado
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                User user = (User) userDetails;

                // Extrai timestamp de emissão do token
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                Date tokenIssuedAt = claims.getIssuedAt();
                LocalDateTime tokenIssuedTime = LocalDateTime.ofInstant(tokenIssuedAt.toInstant(), ZoneId.systemDefault());

                // Verifica se token foi emitido antes da última modificação do usuário
                boolean tokenEmitidoAntesDeModificacao = user.getLastModifiedAt() != null
                        && tokenIssuedTime.isBefore(user.getLastModifiedAt());

                // Verifica se token é válido E se usuário está ativo E se token não foi invalidado por edição
                if (jwtService.tokenValido(token, userDetails) && userDetails.isEnabled() && !tokenEmitidoAntesDeModificacao) {
                    // Cria token de autenticação com authorities do usuário
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // Adiciona detalhes da requisição (IP, session, etc)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Popula SecurityContext para disponibilizar em toda a requisição
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else if (!userDetails.isEnabled()) {
                    logger.debug("Usuário desativado tentou acessar o sistema: {}", email);
                } else if (tokenEmitidoAntesDeModificacao) {
                    logger.debug("Token invalidado - usuário foi modificado após emissão do token: {}", email);
                }
            }
        } catch (Exception e) {
            logger.debug("Token inválido ou malformado: {}", e.getMessage());
        }

        // Continua cadeia de filtros (autenticado ou não)
        filterChain.doFilter(request, response);
    }
}



