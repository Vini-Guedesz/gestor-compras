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

/**
 * Configuração do OpenAPI (Swagger) para documentação da API
 *
 * Define as informações gerais da API, esquema de segurança (JWT)
 * e detalhes de contato/licença.
 */
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
                        .version("2.0.0")
                        .description("""
                                API REST para gerenciamento de compras, fornecedores e cotações.

                                ## Funcionalidades Principais

                                - **Gestão de Usuários**: Cadastro e gerenciamento de usuários do sistema com controle de roles (USER/ADMIN)
                                - **Gestão de Fornecedores**: Cadastro de fornecedores de produtos e serviços com endereços e contatos
                                - **Gestão de Pedidos**: Criação e acompanhamento de solicitações de pedido com itens
                                - **Sistema de Rascunhos**: Criação de rascunhos de pedidos antes da finalização
                                  - Gerenciamento individual de itens (adicionar, editar, remover)
                                  - Numeração automática de itens com reutilização de números
                                  - Histórico completo de ações realizadas no rascunho
                                  - Conversão de rascunho em pedido final
                                - **Gestão de Cotações**: Registro e comparação de cotações de fornecedores
                                  - Cotações em rascunhos com múltiplos anexos PDF
                                  - Cotações finalizadas vinculadas a pedidos
                                  - Vinculação de múltiplos itens por cotação
                                - **Histórico de Pedidos**: Rastreamento completo de todas as modificações
                                  - Criação, edição e exclusão de pedidos
                                  - Mudanças de status
                                  - Adição/remoção de itens
                                  - Adição/remoção/edição de cotações
                                - **Relatórios**: Geração de relatórios em PDF (JasperReports)
                                - **Autenticação**: Sistema de autenticação via JWT

                                ## Validações Implementadas

                                - **CEP**: Validação integrada com API ViaCEP
                                - **Telefones**: Validação de telefone fixo e celular (padrão brasileiro)
                                - **CNPJ**: Validação de formato e dígitos verificadores
                                - **E-mail**: Validação de formato
                                - **Senhas**: Validação de senhas fortes
                                - **Dados de Negócio**: Validação de vínculos entre entidades (ex: item pertence ao pedido correto)

                                ## Autenticação

                                Para acessar endpoints protegidos, é necessário:
                                1. Fazer login em `/auth/login` com credenciais válidas
                                2. Copiar o token JWT retornado
                                3. Clicar no botão "Authorize" 🔓 no topo desta página
                                4. Inserir o token no formato: `Bearer {seu-token-aqui}`
                                5. Clicar em "Authorize" e depois "Close"

                                ## Fluxo de Trabalho Recomendado

                                1. **Criar Rascunho**: POST `/api/rascunhos`
                                2. **Adicionar Itens**: POST `/api/rascunhos/{id}/itens`
                                3. **Solicitar Cotações**: POST `/api/rascunhos/{rascunhoId}/cotacoes`
                                4. **Anexar PDFs**: Incluir anexos na criação da cotação (suporta múltiplos PDFs)
                                5. **Converter para Pedido**: POST `/api/rascunhos/{id}/converter-para-pedido`
                                6. **Acompanhar Histórico**: GET `/api/historico-pedidos/pedido/{pedidoId}`

                                ## Tecnologias

                                - Java 21
                                - Spring Boot 3.3.1
                                - PostgreSQL 17.4
                                - Flyway (Database Migrations)
                                - Spring Security + JWT
                                - JasperReports (PDF Generation)
                                - Bean Validation
                                - MapStruct (DTO Mapping)
                                - Lombok

                                ## Otimizações Implementadas

                                - Queries otimizadas para evitar N+1 problem
                                - Separação de queries para evitar produto cartesiano
                                - Retry logic para conflitos de concorrência (OptimisticLocking)
                                - Estratégia de diff para atualizações de coleções
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
