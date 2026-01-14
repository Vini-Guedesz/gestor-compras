package com.gestordecompras.gestorcomprasbackend.security;

import com.gestordecompras.gestorcomprasbackend.config.ApiVersionConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.Arrays;

/**
 * Configuração de Segurança do Spring Security 6 com autenticação JWT.
 *
 * <p>Esta classe centraliza toda a configuração de segurança da aplicação,
 * definindo políticas de autenticação, autorização, CORS e tratamento de exceções.</p>
 *
 * <p><b>Componentes configurados:</b></p>
 * <ul>
 *   <li>Authentication Provider - DaoAuthenticationProvider com BCrypt</li>
 *   <li>Security Filter Chain - Regras de autorização por endpoint</li>
 *   <li>JWT Filter - Intercepta requisições e valida tokens</li>
 *   <li>CORS Configuration - Permite origem configurável (default: localhost:5173)</li>
 *   <li>Exception Handlers - Tratamento customizado de 401/403</li>
 * </ul>
 *
 * <p><b>Política de Endpoints:</b></p>
 * <ul>
 *   <li><b>Públicos (sem autenticação):</b> /auth/login, /swagger-ui/**, fornecedores, relatórios</li>
 *   <li><b>Apenas ADMIN:</b> /users/**, /enderecos/**, /contatos/**</li>
 *   <li><b>Autenticados (qualquer role):</b> Todos os demais endpoints</li>
 * </ul>
 *
 * <p><b>Roles disponíveis no sistema:</b></p>
 * <ul>
 *   <li><b>ADMIN:</b> Acesso total, incluindo gerenciamento de usuários e configurações</li>
 *   <li><b>USUARIO:</b> Acesso básico ao sistema</li>
 *   <li><b>COMPRADOR:</b> Permissões para criar e gerenciar pedidos de compra</li>
 *   <li><b>APROVADOR:</b> Permissões para aprovar pedidos de compra</li>
 * </ul>
 *
 * <p><b>Características de Segurança:</b></p>
 * <ul>
 *   <li>Sessões STATELESS (sem cookies de sessão)</li>
 *   <li>CSRF desabilitado (API REST stateless)</li>
 *   <li>JWT validado em cada requisição via {@link JwtFilter}</li>
 *   <li>Senhas criptografadas com BCrypt</li>
 * </ul>
 *
 * @since 1.0.0
 * @author Equipe de Desenvolvimento
 * @see JwtFilter
 * @see JwtService
 * @see UserDetailsServiceImpl
 * @see CustomAuthenticationEntryPoint
 * @see CustomAccessDeniedHandler
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtFilter jwtFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    /**
     * Origens permitidas para CORS.
     * Configurável via application.properties: {@code cors.allowed-origins}
     * Padrão: http://localhost:5173 (frontend Vue.js em desenvolvimento)
     */
    @Value("${cors.allowed-origins:http://localhost:5173}")
    private String[] allowedOrigins;

    /**
     * Construtor com injeção de todas as dependências de segurança.
     *
     * @param userDetailsService Service para carregar dados do usuário
     * @param jwtFilter Filtro para validação de JWT
     * @param authenticationEntryPoint Handler customizado para erro 401
     * @param accessDeniedHandler Handler customizado para erro 403
     */
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtFilter jwtFilter, CustomAuthenticationEntryPoint authenticationEntryPoint, CustomAccessDeniedHandler accessDeniedHandler) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    /**
     * Endpoints públicos que não requerem autenticação JWT.
     * Qualquer usuário (autenticado ou não) pode acessar.
     */
    private static final String[] PUBLIC_ENDPOINTS = {
            "/auth/login",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/error",
            "/actuator/**",  // Permitir acesso ao Actuator para monitoramento (Prometheus/Grafana)
            ApiVersionConfig.API_V1 + "/fornecedores-de-produto/**",
            ApiVersionConfig.API_V1 + "/fornecedores-de-servico/**",
            ApiVersionConfig.API_V1 + "/cotacoes/deduplication-report"
    };

    /**
     * Endpoints que requerem autenticação mas aceitam qualquer role válida.
     * Atualmente vazio - a maioria dos endpoints usa {@code anyRequest().authenticated()},
     * permitindo acesso a ADMIN, USUARIO, COMPRADOR e APROVADOR.
     */
    private static final String[] USER_ENDPOINTS = {
    };

    /**
     * Endpoints restritos apenas para usuários com role ADMIN.
     * Incluem gerenciamento de usuários, endereços e contatos.
     */
    private static final String[] ADMIN_ENDPOINTS = {
            ApiVersionConfig.API_V1 + "/users/**",
            ApiVersionConfig.API_V1 + "/enderecos/**",
            ApiVersionConfig.API_V1 + "/contatos/**"
    };

    /**
     * Endpoints POST específicos que são públicos (ex: registro de usuário).
     * Permite criar usuário sem autenticação, mas GET/PUT/DELETE requerem ADMIN.
     */
    private static final String[] PUBLIC_POST_ENDPOINTS = {
            // Nenhum endpoint POST público (criação de usuário agora restrita a ADMIN)
    };

    /**
     * Configura o provider de autenticação com BCrypt.
     *
     * <p>Utiliza {@link DaoAuthenticationProvider} para autenticação baseada em banco de dados.
     * As senhas são verificadas usando {@link PasswordEncoder} (BCrypt).</p>
     *
     * @param passwordEncoder Encoder para criptografia de senhas (BCrypt)
     * @return Provider configurado para autenticação DAO
     */
    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        // Permite distinguir entre "usuário não encontrado" e "senha incorreta"
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    /**
     * Configura a cadeia de filtros de segurança (Security Filter Chain).
     *
     * <p>Define todas as regras de autorização, filtros e handlers de exceção.
     * Esta é a configuração central de segurança da aplicação.</p>
     *
     * <p><b>Ordem de avaliação de regras:</b></p>
     * <ol>
     *   <li>OPTIONS requests - Permitidos (preflight CORS)</li>
     *   <li>/relatorios/** - Público</li>
     *   <li>POST /users - Público (registro)</li>
     *   <li>PUBLIC_ENDPOINTS - Sem autenticação</li>
     *   <li>USER_ENDPOINTS - Requer USER ou ADMIN</li>
     *   <li>ADMIN_ENDPOINTS - Requer ADMIN</li>
     *   <li>Demais endpoints - Requer autenticação JWT válida</li>
     * </ol>
     *
     * <p><b>Filtros aplicados (ordem):</b></p>
     * <ol>
     *   <li>{@link JwtFilter} - Valida JWT e popula SecurityContext</li>
     *   <li>UsernamePasswordAuthenticationFilter - Autenticação padrão (após JWT)</li>
     * </ol>
     *
     * @param http HttpSecurity builder
     * @param authenticationProvider Provider DAO configurado
     * @return SecurityFilterChain completa configurada
     * @throws Exception Se erro na configuração
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Permitir preflight CORS
                        .requestMatchers("/relatorios/**").permitAll()
                        .requestMatchers(ApiVersionConfig.API_V1 + "/relatorios/**").permitAll()
                        .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS).permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(USER_ENDPOINTS).hasAnyRole("USUARIO", "ADMIN", "COMPRADOR", "APROVADOR")
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                        .anyRequest().authenticated() // Permite qualquer role válida (ADMIN, USUARIO, COMPRADOR, APROVADOR)
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .build();
    }

    /**
     * Expõe o AuthenticationManager como bean para uso em controllers.
     *
     * <p>Necessário para autenticação manual no {@link com.gestordecompras.gestorcomprasbackend.controller.AuthController}.</p>
     *
     * @param config Configuração de autenticação do Spring Security
     * @return AuthenticationManager configurado
     * @throws Exception Se erro ao obter manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configura CORS (Cross-Origin Resource Sharing) para permitir requisições do frontend.
     *
     * <p><b>Configurações:</b></p>
     * <ul>
     *   <li><b>Origens permitidas:</b> Configurável via {@code cors.allowed-origins}</li>
     *   <li><b>Métodos permitidos:</b> GET, POST, PUT, DELETE, PATCH, OPTIONS</li>
     *   <li><b>Headers permitidos:</b> Authorization, Content-Type, X-Requested-With</li>
     *   <li><b>Headers expostos:</b> Authorization, Content-Disposition (para downloads)</li>
     *   <li><b>Credenciais:</b> Habilitado (allowCredentials = true)</li>
     *   <li><b>Max Age:</b> 3600s (cache de preflight por 1 hora)</li>
     * </ul>
     *
     * @return CorsConfigurationSource configurado
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cache preflight por 1 hora
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configura firewall HTTP com regras relaxadas para double slash.
     *
     * <p>Permite URLs com double slash codificado (%2F%2F) que podem aparecer
     * em paths de relatórios ou downloads.</p>
     *
     * @return StrictHttpFirewall configurado
     */
    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedDoubleSlash(true);
        return firewall;
    }
}


