package com.gestordecompras.gestorcomprasbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info()
                        .title("API Gestor de Compras")
                        .version("1.0.0")
                        .description("""
                                API REST para gerenciamento de compras e fornecedores.

                                ## Funcionalidades Principais

                                - **Gestão de Usuários**: Cadastro e gerenciamento de usuários do sistema
                                - **Gestão de Fornecedores**: Cadastro de fornecedores de produtos e serviços
                                - **Gestão de Pedidos**: Criação e acompanhamento de solicitações de pedido
                                - **Gestão de Cotações**: Registro e comparação de cotações de fornecedores
                                - **Relatórios**: Geração de relatórios em PDF (JasperReports)
                                - **Autenticação**: Sistema de autenticação via JWT

                                ## Validações Implementadas

                                - Validação de CEP integrada com API ViaCEP
                                - Validação de telefone fixo e celular (padrão brasileiro)
                                - Validação de CNPJ
                                - Validação de e-mail
                                - Validação de senhas fortes

                                ## Autenticação

                                Para acessar endpoints protegidos, é necessário:
                                1. Fazer login em `/auth/login` com credenciais válidas
                                2. Copiar o token JWT retornado
                                3. Clicar no botão "Authorize" no topo desta página
                                4. Inserir o token no formato: `Bearer {seu-token-aqui}`

                                ## Tecnologias

                                - Spring Boot 3.3.1
                                - PostgreSQL 17.4
                                - Flyway (Migrations)
                                - Spring Security + JWT
                                - JasperReports
                                - Bean Validation
                                """)
                        .contact(new Contact()
                                .name("Time de Desenvolvimento")
                                .email("dev@gestorcompras.com")
                                .url("https://github.com/Vini-Guedesz/gestor-compras"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
