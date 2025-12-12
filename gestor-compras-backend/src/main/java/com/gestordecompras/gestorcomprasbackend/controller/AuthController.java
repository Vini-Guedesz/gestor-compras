package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.dto.LoginRequestDTO;
import com.gestordecompras.gestorcomprasbackend.dto.TokenResponseDTO;
import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST para autenticação de usuários.
 *
 * <p>Oferece endpoint público para login e geração de tokens JWT.
 * Este é o único controller que não requer autenticação prévia.</p>
 *
 * <p><b>Endpoints principais:</b></p>
 * <ul>
 *   <li>POST /auth/login - Autenticar usuário e retornar JWT</li>
 * </ul>
 *
 * <p><b>Autenticação:</b> Público (sem JWT necessário)</p>
 *
 * @since 1.0.0
 * @author Equipe de Desenvolvimento
 * @see JwtService
 * @see LoginRequestDTO
 * @see TokenResponseDTO
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "API para autenticação de usuários")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param authManager Manager de autenticação do Spring Security
     * @param jwtService Serviço para geração de tokens JWT
     */
    public AuthController(AuthenticationManager authManager, JwtService jwtService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    /**
     * Realiza login de usuário e retorna token JWT.
     *
     * <p>Autentica usuário via email e senha. Em caso de sucesso, gera um token JWT
     * que deve ser usado em requisições subsequentes no header Authorization.</p>
     *
     * <p><b>Fluxo de autenticação:</b></p>
     * <ol>
     *   <li>Cria token de autenticação com email/senha</li>
     *   <li>Spring Security valida credenciais contra base de dados</li>
     *   <li>Se válido, carrega dados completos do usuário</li>
     *   <li>Gera token JWT com email e role do usuário</li>
     *   <li>Retorna token para cliente</li>
     * </ol>
     *
     * <p><b>Exemplo de uso:</b></p>
     * <pre>
     * POST /auth/login
     * Content-Type: application/json
     *
     * {
     *   "email": "usuario@email.com",
     *   "senha": "senha123"
     * }
     *
     * Response 200:
     * {
     *   "token": "eyJhbGciOiJIUzUxMiJ9..."
     * }
     * </pre>
     *
     * @param request DTO contendo email e senha do usuário
     * @return ResponseEntity com TokenResponseDTO contendo JWT gerado
     * @throws org.springframework.security.core.AuthenticationException se credenciais inválidas (retorna 401)
     */
    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica um usuário e retorna um token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso, token JWT retornado"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.senha());

        var authentication = authManager.authenticate(authToken);
        var usuario = (User) authentication.getPrincipal();

        var jwt = jwtService.gerarToken(usuario);

        return ResponseEntity.ok(new TokenResponseDTO(jwt));
    }
}

