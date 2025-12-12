package com.gestordecompras.gestorcomprasbackend.security;

import com.gestordecompras.gestorcomprasbackend.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Serviço para geração e validação de tokens JWT (JSON Web Token).
 *
 * <p>Gerencia o ciclo completo de tokens JWT para autenticação stateless:</p>
 * <ul>
 *   <li>Geração de tokens após login bem-sucedido</li>
 *   <li>Validação de tokens em requisições subsequentes</li>
 *   <li>Extração de claims (email, nome, roles, expiração)</li>
 * </ul>
 *
 * <p><b>Especificações Técnicas:</b></p>
 * <ul>
 *   <li><b>Algoritmo:</b> HS256 (HMAC SHA-256)</li>
 *   <li><b>Expiração:</b> 1 hora (3600 segundos)</li>
 *   <li><b>Secret Key:</b> Configurável via application.properties ({@code jwt.secret})</li>
 * </ul>
 *
 * <p><b>Claims armazenadas no token:</b></p>
 * <ul>
 *   <li><b>sub</b> (subject) - Email do usuário (usado para login)</li>
 *   <li><b>nome</b> - Nome completo do usuário</li>
 *   <li><b>roles</b> - Authorities/perfis de acesso (USER/ADMIN)</li>
 *   <li><b>iat</b> (issued at) - Data/hora de emissão</li>
 *   <li><b>exp</b> (expiration) - Data/hora de expiração</li>
 * </ul>
 *
 * <p><b>IMPORTANTE - Segurança:</b></p>
 * <ul>
 *   <li>Secret key DEVE ser configurada via variável de ambiente em produção</li>
 *   <li>NUNCA commitar secrets no código ou application.properties</li>
 *   <li>Secret deve ter no mínimo 256 bits (32 caracteres)</li>
 * </ul>
 *
 * @since 1.0.0
 * @author Equipe de Desenvolvimento
 * @see JwtFilter
 * @see SecurityConfig
 * @see io.jsonwebtoken.Jwts
 */
@Service
public class JwtService {

    /**
     * Chave secreta para assinatura e validação de tokens JWT.
     * Gerada a partir da configuração {@code jwt.secret}.
     */
    private final SecretKey key;

    /**
     * Construtor que inicializa a chave secreta a partir da configuração.
     *
     * <p>A secret key é convertida para bytes UTF-8 e usada para gerar
     * uma {@link SecretKey} compatível com HMAC-SHA256.</p>
     *
     * @param secretKey Secret key configurada em application.properties
     */
    public JwtService(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Gera um token JWT para o usuário autenticado.
     *
     * <p>Cria um token assinado contendo informações do usuário como claims.
     * O token é válido por 1 hora a partir da emissão.</p>
     *
     * <p><b>Claims incluídas:</b></p>
     * <ul>
     *   <li><b>sub:</b> Email do usuário (identificador único)</li>
     *   <li><b>nome:</b> Nome completo do usuário</li>
     *   <li><b>roles:</b> Authorities/perfis (USER, ADMIN)</li>
     *   <li><b>iat:</b> Timestamp de emissão</li>
     *   <li><b>exp:</b> Timestamp de expiração (iat + 1 hora)</li>
     * </ul>
     *
     * <p><b>Nota Importante:</b> {@code UserDetails.getUsername()} retorna o <b>email</b>
     * do usuário, não o nome, pois a implementação {@link User} usa email como username.</p>
     *
     * @param userDetails Detalhes do usuário autenticado (Spring Security)
     * @return Token JWT assinado pronto para uso no header Authorization
     */
    public String gerarToken(UserDetails userDetails) {
        // Cast para User para acessar o nome
        // UserDetails.getUsername() retorna o email (login)
        User user = (User) userDetails;

        return Jwts.builder()
                .setSubject(userDetails.getUsername())  // Email (usado para autenticação)
                .claim("nome", user.getNome())          // Nome completo da pessoa
                .claim("roles", userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrai o username (email) do token JWT.
     *
     * <p>Parseia o token e retorna o claim 'subject' que contém o email do usuário.</p>
     *
     * @param token Token JWT a ser parseado
     * @return Email do usuário extraído do token
     * @throws io.jsonwebtoken.JwtException se token inválido ou malformado
     */
    public String extrairUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valida se o token JWT é válido para o usuário fornecido.
     *
     * <p><b>Verificações realizadas:</b></p>
     * <ol>
     *   <li>Assinatura válida (secret key correta)</li>
     *   <li>Subject (email) corresponde ao usuário</li>
     *   <li>Token não expirado (exp > now)</li>
     * </ol>
     *
     * <p>Captura e trata exceções de forma silenciosa retornando false.</p>
     *
     * @param token Token JWT a validar
     * @param userDetails Detalhes do usuário para comparação
     * @return true se token válido, false caso contrário
     */
    public boolean tokenValido(String token, UserDetails userDetails) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject().equals(userDetails.getUsername())
                    && claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
