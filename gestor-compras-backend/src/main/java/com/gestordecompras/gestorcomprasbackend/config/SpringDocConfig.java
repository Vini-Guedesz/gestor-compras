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
                        .version("3.0.0")
                        .description("""
                                API REST para gerenciamento de compras, fornecedores e cotações.

                                ## Funcionalidades Principais

                                - **Gestão de Usuários**: Cadastro e gerenciamento de usuários com sistema de roles granular
                                  - Roles: ADMIN, COMPRADOR, USUARIO, APROVADOR
                                  - Controle de permissões por role e autoria de recursos
                                  - Sistema de modularização baseado em roles
                                - **Gestão de Fornecedores**: Cadastro de fornecedores de produtos e serviços com endereços e contatos
                                - **Gestão de Pedidos**: Criação e acompanhamento de solicitações de pedido com itens
                                - **Sistema de Rascunhos**: Criação de rascunhos de pedidos antes da finalização
                                  - Gerenciamento individual de itens (adicionar, editar, remover)
                                  - Numeração automática de itens com reutilização de números
                                  - Histórico completo de ações realizadas no rascunho
                                  - Estados: ATIVO, EM_COTACAO, FINALIZADO
                                  - Atualização automática de status ao adicionar primeira cotação
                                  - Devolução para edição com remoção automática de cotações
                                  - Controle granular de permissões por role e autoria
                                  - Conversão de rascunho em pedido final
                                - **Gestão de Cotações**: Registro e comparação de cotações de fornecedores
                                  - Cotações em rascunhos com múltiplos anexos PDF
                                  - Deduplificação inteligente de PDFs (Content-Addressable Storage)
                                  - Limpeza automática de PDFs órfãos ao remover cotações
                                  - Cotações finalizadas vinculadas a pedidos
                                  - Vinculação de múltiplos itens por cotação
                                - **Histórico de Pedidos**: Rastreamento completo de todas as modificações
                                  - Criação, edição e exclusão de pedidos
                                  - Mudanças de status
                                  - Adição/remoção de itens
                                  - Adição/remoção/edição de cotações
                                  - Devolução de rascunhos para edição
                                - **Relatórios**: Geração de relatórios em PDF (JasperReports)
                                - **Autenticação**: Sistema de autenticação via JWT

                                ## Sistema de Permissões (v3.0.0)

                                ### Rascunhos
                                - **ADMIN/COMPRADOR**: Pode criar, editar, deletar e cotar qualquer rascunho
                                - **USUARIO**: Pode criar e editar apenas seus próprios rascunhos em status ATIVO
                                - **Regra Geral**: Ninguém pode editar rascunhos com cotações (deve devolver para edição)

                                ### Devolução para Edição
                                - **ADMIN/COMPRADOR**: Pode devolver rascunhos em EM_COTACAO para edição
                                - **Efeito**: Remove TODAS as cotações e PDFs associados, volta status para ATIVO
                                - **Motivo Obrigatório**: Registro no histórico do motivo da devolução

                                ### Cotações
                                - **ADMIN/COMPRADOR**: Pode adicionar, editar e remover cotações
                                - **Primeira Cotação**: Atualiza automaticamente status do rascunho para EM_COTACAO
                                - **Avisos**: Modal de confirmação antes de adicionar primeira cotação

                                ## Validações Implementadas

                                - **CEP**: Validação integrada com API ViaCEP
                                - **Telefones**: Validação de telefone fixo e celular (padrão brasileiro)
                                - **CNPJ**: Validação de formato e dígitos verificadores
                                - **E-mail**: Validação de formato
                                - **Senhas**: Validação de senhas fortes
                                - **Dados de Negócio**: Validação de vínculos entre entidades (ex: item pertence ao pedido correto)
                                - **Permissões**: Validação de permissões baseada em roles e autoria

                                ## Autenticação

                                Para acessar endpoints protegidos, é necessário:
                                1. Fazer login em `/auth/login` com credenciais válidas
                                2. Copiar o token JWT retornado
                                3. Clicar no botão "Authorize" 🔓 no topo desta página
                                4. Inserir o token no formato: `Bearer {seu-token-aqui}`
                                5. Clicar em "Authorize" e depois "Close"

                                ## Fluxo de Trabalho Recomendado

                                ### Fluxo Normal (sem cotações)
                                1. **Criar Rascunho**: POST `/api/rascunhos`
                                2. **Adicionar Itens**: POST `/api/rascunhos/{id}/itens`
                                3. **Finalizar Rascunho**: PUT `/api/rascunhos/{id}/status` (status: EM_COTACAO)
                                4. **Converter para Pedido**: POST `/api/rascunhos/{id}/converter-para-pedido`

                                ### Fluxo com Cotações (COMPRADOR/ADMIN)
                                1. **Criar Rascunho**: POST `/api/rascunhos`
                                2. **Adicionar Itens**: POST `/api/rascunhos/{id}/itens`
                                3. **Adicionar Cotações**: POST `/api/rascunhos/{rascunhoId}/cotacoes`
                                   - Status atualiza automaticamente para EM_COTACAO na primeira cotação
                                4. **Anexar PDFs**: Incluir anexos na criação da cotação (suporta múltiplos PDFs)
                                   - PDFs duplicados são automaticamente deduplificados
                                5. **Selecionar Itens/Cotações**: Escolher quais itens e cotações vão para o pedido
                                6. **Converter para Pedido**: POST `/api/rascunhos/{id}/converter-para-pedido`

                                ### Fluxo de Devolução para Edição
                                1. **Verificar Cotações**: GET `/api/rascunhos/{id}/cotacoes/count`
                                2. **Devolver para Edição**: POST `/api/rascunhos/{id}/devolver-para-edicao`
                                   - Requer: motivo (mínimo 10 caracteres)
                                   - Remove: TODAS as cotações e PDFs associados
                                   - Atualiza: Status volta para ATIVO
                                3. **Editar Itens**: PUT `/api/rascunhos/{id}/itens/{itemId}`
                                4. **Recomeçar**: Voltar ao fluxo normal ou com cotações

                                ### Acompanhamento
                                - **Histórico do Pedido**: GET `/api/historico-pedidos/pedido/{pedidoId}`
                                - **Histórico do Rascunho**: GET `/api/historico-rascunhos/rascunho/{rascunhoId}`

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
                                - Content-Addressable Storage (CAS) para deduplificação de PDFs
                                - Limpeza automática de PDFs órfãos via JPA Entity Listeners
                                - Economia estimada: 30-78% de espaço em disco

                                ## Novidades v3.0.0

                                - ✨ Sistema de modularização baseado em roles
                                - ✨ Controle granular de permissões para rascunhos
                                - ✨ Botão "Devolver para Edição" com remoção automática de cotações
                                - ✨ Atualização automática de status ao adicionar cotação
                                - ✨ Modais de confirmação estilizados
                                - ✨ Limpeza automática de PDFs órfãos
                                - ✨ Melhorias no sistema de permissões
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
