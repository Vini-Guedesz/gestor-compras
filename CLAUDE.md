# CLAUDE.md - Documentação Completa do Sistema Gestor de Compras

> **Última Atualização:** 2025-11-27
> **Versão do Sistema:** 2.0
> **Branch Atual:** V2

---

## ÍNDICE

1. [Visão Geral do Sistema](#1-visão-geral-do-sistema)
2. [Arquitetura Backend](#2-arquitetura-backend)
3. [Arquitetura Frontend](#3-arquitetura-frontend)
4. [Fluxo de Dados Completo](#4-fluxo-de-dados-completo)
5. [Inconsistências Identificadas](#5-inconsistências-identificadas)
6. [Guia de Manutenção](#6-guia-de-manutenção)
7. [Roteiro de Melhorias](#7-roteiro-de-melhorias)

---

## 1. VISÃO GERAL DO SISTEMA

### 1.1 Propósito

Sistema web para gestão de compras que permite:
- Criação de solicitações de pedido com múltiplos itens
- Gerenciamento de rascunhos antes da finalização
- Sistema de cotações com múltiplos fornecedores
- Geração de relatórios em PDF
- Histórico completo de modificações

### 1.2 Stack Tecnológico

#### Backend
- **Framework:** Spring Boot 3.4.1
- **Linguagem:** Java 17+
- **Banco de Dados:** PostgreSQL 15+
- **ORM:** Hibernate (JPA)
- **Migrações:** Flyway 9.x
- **Segurança:** Spring Security + JWT
- **Relatórios:** JasperReports
- **Build:** Maven
- **Porta:** 8081

#### Frontend
- **Framework:** Vue.js 3.5.18
- **Build Tool:** Vite 6.0.5
- **State Management:** Pinia 3.0.3
- **HTTP Client:** Axios 1.7.9
- **Router:** Vue Router 4.5.0
- **Porta:** 5173 (dev)

#### Infraestrutura
- **Containerização:** Docker
- **Database:** docker-compose com PostgreSQL

### 1.3 Estrutura de Diretórios

```
gestor-compras/
├── gestor-compras-backend/          # API REST Spring Boot
│   ├── src/main/java/.../           # Código Java
│   ├── src/main/resources/          # application.properties, migrations, reports
│   ├── pom.xml                      # Dependências Maven
│   └── Dockerfile                   # Container backend
├── gestor-compras-frontend/         # SPA Vue.js
│   └── vue-project/
│       ├── src/                     # Código fonte Vue
│       ├── public/                  # Arquivos estáticos
│       ├── package.json             # Dependências npm
│       └── vite.config.js           # Configuração Vite
├── docker-compose.yml               # Orquestração PostgreSQL
├── CLAUDE.md                        # Este arquivo
└── README.md                        # Instruções básicas
```

---

## 2. ARQUITETURA BACKEND

### 2.1 Estrutura de Pacotes

```
com.gestordecompras.gestorcomprasbackend
├── config/                          # Configurações Spring e OpenAPI
│   ├── OpenApiConfig.java           # Swagger/OpenAPI 3.0
│   └── WebConfig.java               # CORS, converters
├── controller/                      # Controllers REST (14 classes)
│   ├── AuthController.java          # POST /auth/login
│   ├── UserController.java          # /api/users/** (ADMIN only)
│   ├── FornecedorDeProdutoController.java
│   ├── FornecedorDeServicoController.java
│   ├── SolicitacaoDePedidoController.java
│   ├── ItemPedidoController.java
│   ├── CotacaoController.java
│   ├── RascunhoController.java
│   ├── CotacaoRascunhoController.java
│   ├── HistoricoPedidoController.java
│   ├── EnderecoController.java
│   ├── ContatoController.java
│   └── relatorio/                   # Controllers de relatórios
│       ├── RelatoriosGerenciaisController.java
│       ├── FornecedorRelatorioController.java
│       └── ItemPedidoRelatorioController.java
├── dto/                             # Data Transfer Objects (40+ classes)
│   ├── contato/
│   ├── cotacao/
│   ├── endereco/
│   ├── fornecedor/
│   ├── itempedido/
│   ├── pedido/
│   ├── rascunho/
│   ├── relatorio/
│   └── user/
├── exception/                       # Tratamento de exceções
│   ├── RestExceptionHandler.java   # Handler centralizado
│   └── [Custom exceptions]
├── mapper/                          # MapStruct (9 interfaces)
│   ├── UserMapper.java
│   ├── FornecedorDeProdutoMapper.java
│   ├── SolicitacaoDePedidoMapper.java
│   ├── RascunhoMapper.java
│   ├── CotacaoMapper.java
│   └── [Outros mappers]
├── model/                           # Entidades JPA (15+ classes)
│   ├── fornecedor/
│   │   ├── Fornecedor.java          # @MappedSuperclass
│   │   ├── FornecedorDeProduto.java
│   │   └── FornecedorDeServico.java
│   ├── pedido/
│   │   ├── SolicitacaoDePedido.java
│   │   ├── ItemPedido.java
│   │   └── HistoricoPedido.java
│   ├── rascunho/
│   │   ├── Rascunho.java
│   │   ├── ItemRascunho.java
│   │   ├── NumeroItemDisponivel.java
│   │   └── HistoricoRascunho.java
│   ├── cotacao/
│   │   ├── Cotacao.java
│   │   ├── AnexoCotacao.java
│   │   └── CotacaoRascunho.java
│   ├── endereco/Endereco.java
│   ├── contato/Contato.java
│   └── user/User.java
├── repository/                      # Spring Data JPA (14 interfaces)
├── security/                        # JWT e autenticação
│   ├── SecurityConfig.java          # Configuração endpoints
│   ├── JwtService.java              # Geração/validação JWT
│   ├── JwtFilter.java               # Filtro de requisições
│   ├── UserDetailsServiceImpl.java
│   ├── CustomAuthenticationEntryPoint.java
│   └── CustomAccessDeniedHandler.java
├── service/                         # Lógica de negócio (14+ services)
│   ├── UserService.java
│   ├── FornecedorDeProdutoService.java
│   ├── FornecedorDeServicoService.java
│   ├── SolicitacaoDePedidoService.java
│   ├── ItemPedidoService.java
│   ├── CotacaoService.java
│   ├── RascunhoService.java
│   ├── CotacaoRascunhoService.java
│   ├── HistoricoPedidoService.java
│   └── HistoricoRascunhoService.java
├── validation/                      # Validadores customizados
│   ├── CEPValidator.java
│   ├── CelularValidator.java
│   └── TelefoneFixoValidator.java
└── GestorComprasBackendApplication.java
```

### 2.2 Modelo de Dados (Entidades JPA)

#### Diagrama de Relacionamentos

```
User (users)
  ↓ (1:N)
Rascunho (rascunho) ──────────────┐
  ↓ (1:N)                          ↓ (1:N)
ItemRascunho (item_rascunho)    CotacaoRascunho (cotacao_rascunho)
                                   ↓ (N:M)
                                 ItemRascunho

SolicitacaoDePedido (solicitacao_de_pedido)
  ↓ (1:N)                    ↓ (1:N)
ItemPedido (item_pedido)   Cotacao (cotacao) ───┐
                              ↓ (N:M)            ↓ (XOR)
                           ItemPedido      FornecedorDeProduto
                                           FornecedorDeServico
                                                 ↓
                                           Endereco + Contato

HistoricoPedido → SolicitacaoDePedido
HistoricoRascunho → Rascunho
```

#### Entidades Principais

**Fornecedor (Base Abstrata)**
```java
@MappedSuperclass
public abstract class Fornecedor {
    Integer id;
    String razaoSocial;
    String cnpj;
    @ManyToOne Endereco endereco;
    @ManyToOne Contato contato;
}

// Especializações:
FornecedorDeProduto { String inscricaoEstadual; }
FornecedorDeServico { String inscricaoMunicipal; }
```

**Rascunho**
```java
@Entity
public class Rascunho {
    Long id;
    @OneToMany List<ItemRascunho> itens;
    @ManyToOne User criador;
    String observacao;
    LocalDateTime dataCriacao, dataModificacao;
    @Enumerated StatusRascunho status; // ATIVO, EM_COTACAO, FINALIZADO
    Long pedidoGeradoId;
    Integer proximoNumeroItem;
    @OneToMany List<NumeroItemDisponivel> numerosDisponiveis;
}
```

**NumeroItemDisponivel** (Reuso de números)
- Quando item é removido, número volta para pool
- Garante sequência sem gaps

**SolicitacaoDePedido**
```java
@Entity
public class SolicitacaoDePedido {
    Long id;
    @OneToMany(cascade = ALL, orphanRemoval = true) List<ItemPedido> itens;
    @OneToMany(cascade = ALL, orphanRemoval = true) Set<Cotacao> cotacoes;
    @Enumerated StatusPedido status;
    String observacao;
    @PrePersist LocalDateTime dataCriacao;
}

enum StatusPedido { PENDENTE, EM_ANALISE, EM_ANDAMENTO, CANCELADO, APROVADO }
```

**Cotacao** (⚠️ Bug #5 Identificado)
```java
@Entity
public class Cotacao {
    Long id;
    @ManyToOne FornecedorDeProduto fornecedorProduto; // XOR
    @ManyToOne FornecedorDeServico fornecedorServico; // XOR
    @ManyToOne SolicitacaoDePedido solicitacaoDePedido;

    // ⚠️ PROBLEMA: Relacionamento N:N não permite preços individuais
    @ManyToMany Set<ItemPedido> itensPedido;

    BigDecimal preco; // Preço total (não por item!)
    Integer prazoEmDiasUteis;
    LocalDate dataLimite;
    byte[] anexoPdf;
    String caminhoAnexo;
    @OneToMany @OrderBy("ordem") List<AnexoCotacao> anexos;
}
```

> **Bug #5:** Relacionamento N:N direto impede armazenar preço unitário e quantidade por item.
> **Solução:** Criar entidade intermediária `CotacaoItem { cotacaoId, itemPedidoId, precoUnitario, quantidade }`.

**HistoricoPedido & HistoricoRascunho**
- Auditoria completa de modificações
- Tipos: CRIACAO, ATUALIZACAO, MUDANCA_STATUS, ADICAO_ITEM, REMOCAO_ITEM, etc.
- Armazena: quem fez, quando, o que mudou

### 2.3 Endpoints REST

#### Autenticação

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| POST | `/auth/login` | Pública | Login com email/senha → JWT token |

#### Usuários

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| GET | `/api/users` | ADMIN | Listar usuários |
| GET | `/api/users/{id}` | ADMIN | Buscar por ID |
| POST | `/api/users` | Pública | Criar usuário (registro) |
| PUT | `/api/users` | ADMIN | Atualizar usuário |
| DELETE | `/api/users/{id}` | ADMIN | Remover usuário |

#### Fornecedores

| Endpoint | Descrição |
|----------|-----------|
| `/api/fornecedores-de-produto/**` | CRUD fornecedores de produto |
| `/api/fornecedores-de-servico/**` | CRUD fornecedores de serviço |

> **Nota:** Ambos **públicos** (sem autenticação requerida).

#### Pedidos

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| GET | `/api/solicitacoes-pedido` | Pública | Listar pedidos |
| GET | `/api/solicitacoes-pedido/{id}` | Pública | Buscar pedido por ID |
| POST | `/api/solicitacoes-pedido` | Pública | Criar pedido |
| PUT | `/api/solicitacoes-pedido/{id}` | Pública | Atualizar pedido |
| DELETE | `/api/solicitacoes-pedido/{id}` | Pública | Deletar pedido |

> **⚠️ Problema de Segurança:** Endpoints públicos permitem qualquer usuário criar/modificar/deletar.

#### Rascunhos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/rascunhos` | Listar todos |
| GET | `/api/rascunhos/usuario/{userId}` | Por usuário |
| GET | `/api/rascunhos/{id}` | Buscar por ID |
| POST | `/api/rascunhos` | Criar rascunho |
| PUT | `/api/rascunhos/{id}` | Atualizar |
| DELETE | `/api/rascunhos/{id}` | Deletar |
| POST | `/api/rascunhos/{id}/converter-para-pedido` | Converter em pedido |
| POST/PUT/DELETE | `/api/rascunhos/{id}/itens/**` | Gerenciar itens |
| PATCH | `/api/rascunhos/{id}/status?status={STATUS}` | Alterar status |
| GET | `/api/rascunhos/{id}/historico` | Buscar histórico |

#### Cotações

| Método | Endpoint | Auth | Descrição |
|--------|----------|------|-----------|
| GET | `/api/cotacoes` | Autenticado | Listar cotações |
| POST | `/api/cotacoes` | Autenticado | Criar cotação |
| PUT | `/api/cotacoes/{id}` | Autenticado | Atualizar |
| DELETE | `/api/cotacoes/{id}` | Autenticado | Deletar |
| GET | `/api/cotacoes/{id}/anexo` | Autenticado | Download PDF |
| GET | `/api/cotacoes/{id}/anexo/{index}` | Autenticado | Download anexo específico |
| PATCH | `/api/cotacoes/{cotacaoId}/vincular-itens` | Autenticado | Vincular itens |

#### Cotações de Rascunho

| Endpoint | Descrição |
|----------|-----------|
| GET | `/api/rascunhos/{rascunhoId}/cotacoes` | Listar |
| POST | `/api/rascunhos/{rascunhoId}/cotacoes` | Criar |
| DELETE | `/api/rascunhos/{rascunhoId}/cotacoes/{id}` | Deletar |
| GET | `/api/rascunhos/{rascunhoId}/cotacoes/{id}/anexo` | Download PDF |

#### Relatórios

| Endpoint | Descrição |
|----------|-----------|
| GET | `/relatorios/fornecedores` | PDF de fornecedores |
| GET | `/api/relatorios/dashboard` | Dashboard executivo |
| GET | `/api/relatorios/cotacoes/comparativo?itemPedidoId={id}` | Comparativo por item |
| POST | `/api/relatorios/item-pedido` | Relatório personalizado |

### 2.4 Segurança

#### Configuração de Endpoints

```java
// Públicos (sem JWT)
- /auth/login
- /swagger-ui/**, /v3/api-docs/**
- /api/solicitacoes-pedido/**    // ⚠️ Problema
- /api/itens-pedido/**           // ⚠️ Problema
- /api/rascunhos/**              // ⚠️ Problema
- /api/fornecedores-**/**
- /relatorios/**
- /api/historico-pedidos/**

// ADMIN Only
- /api/users/** (exceto POST)
- /api/enderecos/**
- /api/contatos/**

// Autenticado (JWT obrigatório)
- /api/cotacoes/**
- Todos os outros não listados
```

#### JWT (JSON Web Token)

- **Secret:** Base64 encoded em `application-dev.properties`
- **Algoritmo:** HS512 (HMAC SHA-512)
- **Claims:** email (sub), role
- **Expiração:** Configurável
- **Filter:** `JwtFilter` extrai token do header `Authorization: Bearer <token>`

> **⚠️ Problema Crítico:** Chaves JWT hardcoded no repositório. Usar variáveis de ambiente em produção.

### 2.5 Serviços Principais

#### RascunhoService

**Funcionalidades Especiais:**
- **Reuso de números de itens:** Quando item removido, número volta para `NumeroItemDisponivel`
- **Conversão para pedido:** Valida cotações, migra itens e cotações, marca rascunho como FINALIZADO
- **Auditoria:** Registra todas as ações em `HistoricoRascunho`

```java
@Transactional
public SolicitacaoDePedidoDTO converterRascunhoParaPedido(
    Long rascunhoId,
    ConverterRascunhoParaPedidoDTO dto
) {
    // 1. Valida rascunho EM_COTACAO
    // 2. Verifica itens selecionados têm cotações
    // 3. Cria SolicitacaoDePedido
    // 4. Migra itens selecionados
    // 5. Migra cotações de rascunho para cotações de pedido
    // 6. Atualiza rascunho: FINALIZADO + pedidoGeradoId
}
```

> **⚠️ Problema:** Sem transação distribuída robusta. Se falha após criar pedido, inconsistência.

#### CotacaoService

**Validações:**
- **Bug Fix #6:** Valida que cotação tem pelo menos 1 item
- **Bug Fix #7:** Valida XOR de fornecedor (produto OU serviço, não ambos)
- **Bug Fix #10:** `@Retryable` para lidar com `OptimisticLockingFailureException`

```java
@Retryable(
    retryFor = {OptimisticLockingFailureException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 100)
)
public CotacaoDTO vincularItens(Long cotacaoId, List<Long> itensPedidoIds)
```

> **⚠️ Falta:** Anotação `@Version` em Cotacao para otimistic locking funcionar.

#### HistoricoPedidoService & HistoricoRascunhoService

Registram todas modificações:
- Criação, atualização, mudança de status
- Adição/remoção/atualização de itens
- Adição/remoção de cotações
- Conversão de rascunho

### 2.6 Repositories

#### Queries Otimizadas

**Bug Fix #1:** SolicitacaoDePedidoRepository separa queries para evitar produto cartesiano

```java
// Queries separadas:
@Query("SELECT DISTINCT s FROM SolicitacaoDePedido s LEFT JOIN FETCH s.itens WHERE s.id = :id")
Optional<SolicitacaoDePedido> findByIdWithItens(@Param("id") Long id);

@Query("SELECT DISTINCT s FROM SolicitacaoDePedido s LEFT JOIN FETCH s.cotacoes c " +
       "LEFT JOIN FETCH c.fornecedorProduto LEFT JOIN FETCH c.fornecedorServico WHERE s.id = :id")
Optional<SolicitacaoDePedido> findByIdWithCotacoes(@Param("id") Long id);
```

**Bug Fix #9:** CotacaoRepository usa `DISTINCT` + `FETCH` para evitar N+1

```java
@Query("SELECT DISTINCT c FROM Cotacao c " +
       "LEFT JOIN FETCH c.itensPedido " +
       "LEFT JOIN FETCH c.fornecedorProduto " +
       "LEFT JOIN FETCH c.fornecedorServico " +
       "LEFT JOIN FETCH c.solicitacaoDePedido")
List<Cotacao> findAllWithRelationships();
```

### 2.7 Migrações Flyway

| Versão | Arquivo | Descrição |
|--------|---------|-----------|
| V1 | create-initial-schema.sql | Users, contato, endereco |
| V2 | create-fornecedores-tables.sql | Fornecedores produto/serviço |
| V3 | create-pedidos-rascunhos-cotacoes.sql | Pedidos, rascunhos, cotações, históricos |
| V4 | create-anexo-cotacao-table.sql | AnexoCotacao |
| V5 | add-status-to-rascunho.sql | Status em rascunhos |
| V6 | create-anexo-cotacao-table.sql | Refinamento anexos |

**Índices Criados:**
- Rascunho: `idx_rascunho_user_id`, `idx_rascunho_data_criacao`
- Históricos: `idx_historico_*_data_modificacao`
- Cotações: `idx_cotacao_solicitacao_id`, `idx_cotacao_fornecedor_*`

### 2.8 Configurações

#### application-dev.properties
```properties
server.port=8081
spring.datasource.url=jdbc:postgresql://localhost:5432/gestorcomprasbackend
spring.jpa.hibernate.ddl-auto=none  # Flyway gerencia
jwt.secret=c3VwZXItc2VndXJhLWNoYXZlLWRlLWRlc2Vudm9sdmltZW50by1xdWUtbmFvLWRldmUtc2VyLWNvbXBhcnRpbGhhZGEK
```

> **⚠️ Problema:** Secret exposta no código. Usar env vars.

---

## 3. ARQUITETURA FRONTEND

### 3.1 Estrutura de Diretórios

```
src/
├── App.vue                          # Componente raiz
├── main.js                          # Entrada (Pinia + Router)
├── assets/css/layout.css           # Estilos globais
├── layouts/
│   └── AppLayout.vue                # Layout principal (header + nav)
├── router/index.js                 # Rotas + guards
├── stores/
│   └── auth.js                     # Pinia - autenticação global
├── composables/
│   ├── useToast.js                 # Notificações toast
│   ├── useModal.js                 # Lock/unlock scroll
│   └── useMobileSidebar.js         # Sidebar responsiva
├── services/                        # Camada de API
│   ├── api.js                      # Cliente HTTP Axios
│   ├── authService.js
│   ├── pedidoService.js
│   ├── cotacaoService.js
│   ├── fornecedorService.js
│   ├── rascunhoService.js
│   ├── itemPedidoService.js
│   ├── historicoPedidoService.js
│   ├── cotacaoRascunhoService.js
│   └── relatorioService.js
├── utils/
│   ├── genderUtils.js              # Mensagens por gênero
│   └── pedidoUtils.js              # Utils de pedidos
├── components/ui/                  # Componentes reutilizáveis
│   ├── Toast.vue
│   ├── ToastContainer.vue
│   ├── feedback/LoadingSpinner.vue
│   └── modals/
│       ├── ConfirmModal.vue
│       └── LogoutModal.vue
├── features/                       # Domínios funcionais
│   ├── dashboard/components/
│   │   ├── DashboardHeader.vue
│   │   ├── DashboardSidebar.vue
│   │   ├── MetricCard.vue
│   │   └── QuickActions.vue
│   ├── pedidos/components/
│   │   ├── HistoricoPedido.vue
│   │   └── pedido-wizard/
│   │       ├── PedidoWizard.vue
│   │       ├── StepCriarPedido.vue
│   │       ├── StepAdicionarCotacoes.vue
│   │       ├── StepAdicionarCotacoesRascunho.vue
│   │       ├── CotacaoInlineForm.vue
│   │       └── [outros componentes wizard]
│   ├── cotacoes/components/
│   │   └── CotacaoForm.vue
│   └── fornecedores/components/
│       └── FornecedorForm.vue
└── views/                          # Páginas (rotas)
    ├── LoginView.vue
    ├── DashboardView.vue
    ├── PedidosView.vue
    ├── NovoPedidoView.vue
    ├── VisualizarPedidoView.vue
    ├── CotacoesView.vue
    ├── FornecedoresView.vue
    ├── PerfilView.vue
    └── ConfiguracoesView.vue
```

### 3.2 Sistema de Rotas

```javascript
// Rotas Públicas
- / → Redirect /login
- /login → LoginView (requiresGuest)

// Rotas Protegidas (requiresAuth)
- /dashboard → DashboardView
- /fornecedores → FornecedoresView
- /pedidos → PedidosView
- /pedidos/novo → NovoPedidoView (criar)
- /pedidos/novo/:id → NovoPedidoView (continuar rascunho)
- /pedidos/rascunho/:id → NovoPedidoView (editar)
- /pedidos/visualizar/:id → VisualizarPedidoView
- /cotacoes → CotacoesView
- /perfil → PerfilView
- /configuracoes → ConfiguracoesView
```

**Router Guard Global:**
```javascript
router.beforeEach((to, from, next) => {
  authStore.checkAuth(); // Restaura sessão de localStorage

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login');
  } else if (to.meta.requiresGuest && authStore.isAuthenticated) {
    next('/dashboard');
  } else {
    next();
  }
});
```

### 3.3 State Management (Pinia)

#### auth.js Store

```javascript
const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref(false);
  const user = ref(null); // { email, username, role }
  const token = ref(null);

  async function login(email, password) {
    const result = await authService.login(email, password);
    if (result.success) {
      token.value = result.token;
      const validatedUser = authService.validateToken(result.token);
      user.value = validatedUser;
      isAuthenticated.value = true;
      localStorage.setItem('authToken', result.token);
      localStorage.setItem('user', JSON.stringify(validatedUser));
    }
    return result;
  }

  function logout() {
    token.value = null;
    user.value = null;
    isAuthenticated.value = false;
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  }

  function checkAuth() {
    const savedToken = localStorage.getItem('authToken');
    if (savedToken) {
      const validatedUser = authService.validateToken(savedToken);
      if (validatedUser) {
        user.value = validatedUser;
        token.value = savedToken;
        isAuthenticated.value = true;
        return true;
      }
    }
    logout();
    return false;
  }

  return { isAuthenticated, user, token, login, logout, checkAuth };
});
```

> **⚠️ Problema:** Token em localStorage (plaintext) vulnerável a XSS. Considerar sessionStorage ou httpOnly cookies.

### 3.4 Composables

#### useToast.js
```javascript
const toasts = ref([]); // Global compartilhado
let nextId = 0;

export function useToast() {
  const success = (message, options = {}) =>
    addToast({ ...options, message, type: 'success' });
  const error = (message, options = {}) =>
    addToast({ ...options, message, type: 'error' });
  const warning = (message, options = {}) =>
    addToast({ ...options, message, type: 'warning' });
  const info = (message, options = {}) =>
    addToast({ ...options, message, type: 'info' });

  return { toasts, success, error, warning, info, removeToast, clearAll };
}
```

#### useModal.js (Lock de Scroll)
```javascript
export function useModal(isOpen) {
  watch(isOpen, (newValue) => {
    if (newValue) {
      scrollPosition = window.pageYOffset;
      document.body.style.overflow = 'hidden';
      document.body.style.position = 'fixed';
      document.body.style.top = `-${scrollPosition}px`;
    } else {
      document.body.style.overflow = '';
      document.body.style.position = '';
      document.body.style.top = '';
      window.scrollTo(0, scrollPosition);
    }
  }, { immediate: true });

  onUnmounted(() => { if (isOpen.value) unlockScroll(); });
}
```

### 3.5 Services (Camada de API)

#### api.js - Cliente HTTP Central

```javascript
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
});

// Request Interceptor: Adiciona JWT
api.interceptors.request.use(config => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response Interceptor: Trata erros
api.interceptors.response.use(
  response => response,
  error => {
    // JWT expirado → logout + redirect
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }

    // Extrai mensagem de erro de múltiplos formatos
    const errorData = error.response?.data;
    if (errorData?.messages) return Promise.reject({ message: errorData.messages.join(', '), ...errorData });
    if (errorData?.errors) { /* trata array ou objeto */ }
    // ...
  }
);

export default { get, post, put, patch, delete };
```

> **⚠️ Problema:** 513 console.logs em produção (linhas 57-72, 152, etc).

#### pedidoService.js

**Métodos Principais:**
```javascript
async listar() // GET /api/solicitacoes-pedido
async obterPorId(id)
async criar(pedido) // Valida itens antes
async atualizar(id, pedido)
async remover(id)
async alterarStatus(id, novoStatus)
async aprovar(id)
async rejeitar(id, motivo)
```

**Validações:**
- Itens obrigatórios e não vazios
- Quantidade > 0
- Limites de caracteres

#### cotacaoService.js (800+ linhas)

**Métodos:**
```javascript
async listar(filtros)
async buscarPorId(id)
async criar(cotacao) // Valida fornecedor, itens, preço
async atualizar(id, cotacao)
async deletar(id)
async vincularItens(cotacaoId, itensPedidoIds) // PATCH
async obterAnexoPdf(id, pdfIndex = 0) // Fetch direto com token
async gerarRelatorioComparativo(itemPedidoId) // PDF 30s timeout
```

**Validações na Criação:**
- Fornecedor obrigatório
- Tipo fornecedor obrigatório
- Itens selecionados obrigatórios
- Preço > 0

**Upload PDF:**
```javascript
async uploadArquivoPdf(cotacaoId, arquivo) {
  if (!arquivo.type.includes('pdf')) throw Error('Apenas PDF');
  if (arquivo.size > 10 * 1024 * 1024) throw Error('Max 10MB');
  const bytes = await arquivoParaBytes(arquivo);
  const formData = new FormData();
  formData.append('pdf', new Blob([bytes], { type: 'application/pdf' }));
  return api.post(`/api/cotacoes/${cotacaoId}/anexo`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
}
```

#### fornecedorService.js

**Separação de Tipos:**
```javascript
// Produto
async listarFornecedoresProduto() // GET /api/fornecedores-de-produto
async criarFornecedorProduto(fornecedor)
async atualizarFornecedorProduto(id, fornecedor) // PUT sem /{id} no path
async removerFornecedorProduto(id)

// Serviço
async listarFornecedoresServico()
async criarFornecedorServico()
// ...

// Unificado
async listarTodos() // Promise.all produto + serviço
async listarParaCotacao() // Retorna { id, nome, tipo: 'PRODUTO'|'SERVICO' }
```

**Utils:**
```javascript
formatarCNPJ(cnpj) // XX.XXX.XXX/XXXX-XX
formatarCEP(cep) // XXXXX-XXX
formatarTelefone(telefone) // (XX) XXXXX-XXXX
validarCNPJ(cnpj) // Apenas length, não calcula dígito
validarEmail(email) // Regex básico
```

#### rascunhoService.js

**Conversão para Pedido:**
```javascript
async converterParaPedido(rascunhoId, itemIds, cotacaoParaItens) {
  const dto = {
    itemRascunhoIds: itemIds, // Legacy fallback
    cotacaoParaItens: cotacaoParaItens // Novo formato: { cotacaoId: [item1, item2] }
  };
  const response = await api.post(`/api/rascunhos/${rascunhoId}/converter-para-pedido`, dto);
  return response.data;
}
```

**Outros Métodos:**
```javascript
async adicionarItem(rascunhoId, itemDTO)
async atualizarItem(rascunhoId, itemId, itemDTO)
async removerItem(rascunhoId, itemId)
async atualizarStatus(rascunhoId, status)
async listarHistorico(rascunhoId)
```

#### relatorioService.js

**Cliente Específico:**
```javascript
const relatorioClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081',
  responseType: 'blob', // PDFs
  timeout: 30000 // 30s para relatórios pesados
});
```

**Métodos:**
```javascript
async gerarRelatorioFornecedores() // GET /relatorios/fornecedores
async visualizarRelatorioFornecedores() // Abre em nova aba
async gerarRelatorioItensPedido()
async gerarRelatorioItemPedido(id)
async gerarRelatorioItensParaCotacao({ solicitacaoId, itensIds })
```

> **⚠️ Duplicação:** `relatorioClient` e `api.js` são instâncias duplicadas.

### 3.6 Componentes UI

#### Toast System

**Toast.vue**
- Props: `message`, `type` (success|error|warning|info), `duration`, `actionText`, `onAction`
- Emits: `close`
- Auto-close após duration (default 5000ms)
- Ícones SVG por tipo
- Transições suaves

**ToastContainer.vue**
- Usa `Teleport to="body"`
- Position: `fixed top-20 right-20`
- TransitionGroup para animações

#### Modals

**ConfirmModal.vue**
- Props: `show`, `isVisible`, `title`, `message`, `details`, `confirmText`, `cancelText`, `type`
- Emits: `confirm`, `cancel`
- `useModal(isOpen)` para lock de scroll

**LogoutModal.vue**
- Modal específico para confirmação de logout
- Integração com authStore

### 3.7 Views Principais

#### LoginView.vue
```vue
<template>
  <LoadingSpinner :show="isLoading" message="Fazendo login..." />
  <form @submit.prevent="handleLogin">
    <input type="email" v-model="loginForm.email" required />
    <input type="password" v-model="loginForm.password" required />
    <button type="submit">Acessar</button>
    <a @click.prevent="handleForgotPassword">Esqueceu sua senha?</a>
  </form>
</template>

<script setup>
const { success, warning, error: toastError } = useToast();

async function handleLogin() {
  const result = await authStore.login(loginForm.value.email, loginForm.value.password);
  if (result.success) {
    router.push('/dashboard');
  } else {
    errorMessage.value = result.error;
  }
}

async function handleForgotPassword() {
  if (!loginForm.value.email) {
    warning('Digite seu e-mail primeiro.');
    return;
  }
  const result = await authStore.forgotPassword(loginForm.value.email);
  if (result.success) success(result.message);
  else toastError(result.error);
}
</script>
```

> **⚠️ Problema:** `forgotPassword()` é placeholder com delay simulado, não faz requisição real.

#### DashboardView.vue

Carrega métricas:
- Pedidos: total, pendentes
- Fornecedores: ativos, novos (estimativa 20% dos últimos IDs)
- Cotações: abertas, em análise

**MetricCard clicáveis:**
```vue
<MetricCard
  title="Pedidos"
  :metrics="[{ label: 'Total', value: totalPedidos }]"
  @action="router.push('/pedidos')"
/>
```

#### NovoPedidoView.vue

**Dois Modos:**
1. **Criar novo rascunho:** `/pedidos/novo`
2. **Editar rascunho existente:** `/pedidos/novo/:id` ou `/pedidos/rascunho/:id`

**Estados:**
- `EDITANDO_RASCUNHO`: Formulário + itens inline
- `GERENCIANDO_COTACOES`: Adicionar cotações, selecionar itens

**Fluxo:**
1. Criar rascunho vazio
2. Adicionar itens
3. Finalizar rascunho (status EM_COTACAO)
4. Adicionar cotações
5. Selecionar cotações e itens
6. Gerar pedido

> **⚠️ Problema:** Validação de cotações sem itens apenas warning, não bloqueia.

#### VisualizarPedidoView.vue

Exibe:
- Informações do pedido
- Lista de itens
- Cotações associadas (lazy-loaded)
- Histórico de modificações

### 3.8 Sistema de Design

#### Layout
- Max-width: 1200px
- Header sticky, z-index 1000
- Sidebar: 260px, sticky top 70px
- Responsive: 1024px (220px sidebar) → 768px (hide) → 480px (compact)

#### Cores
- Primary: `#1F285F` (dark blue)
- Success: `#10b981` (green)
- Error: `#ef4444` (red)
- Warning: `#f59e0b` (orange)
- Info: `#3b82f6` (blue)

#### Status Badges
- PENDENTE: orange
- APROVADO: green
- EM_ANDAMENTO: blue
- CANCELADO: red
- RASCUNHO: purple

---

## 4. FLUXO DE DADOS COMPLETO

### 4.1 Fluxo de Criação de Pedido com Rascunho

```
1. Usuário acessa /pedidos/novo
   └─> NovoPedidoView (mode: EDITANDO_RASCUNHO)

2. Preenche formulário básico
   ├─> Observação
   └─> Itens (nome, quantidade, descrição)

3. Adiciona item
   └─> POST /api/rascunhos/{id}/itens
       ├─> RascunhoService.adicionarItem()
       ├─> Obtém próximo número (reusa se disponível)
       ├─> Persiste ItemRascunho
       └─> Registra HistoricoRascunho (ADICAO_ITEM)

4. Finaliza rascunho
   └─> PATCH /api/rascunhos/{id}/status?status=EM_COTACAO
       ├─> RascunhoService.atualizarStatus()
       └─> Mode: GERENCIANDO_COTACOES

5. Adiciona cotação de rascunho
   └─> POST /api/rascunhos/{id}/cotacoes
       ├─> CotacaoRascunhoService.criar()
       ├─> Valida fornecedor, tipo, itens
       ├─> Persiste CotacaoRascunho
       └─> Registra histórico

6. Seleciona cotações e itens para pedido final
   ├─> Frontend valida: cotações com itens
   └─> POST /api/rascunhos/{id}/converter-para-pedido
       {
         cotacaoParaItens: {
           cotacao1: [item1, item2],
           cotacao2: [item3]
         }
       }

7. Conversão (RascunhoService)
   ├─> Cria SolicitacaoDePedido
   ├─> Copia itens selecionados como ItemPedido
   ├─> Migra CotacaoRascunho → Cotacao
   │   ├─> Cria Cotacao nova
   │   ├─> Vincula ItemPedido equivalentes
   │   └─> Copia anexos
   ├─> Atualiza Rascunho
   │   ├─> status = FINALIZADO
   │   └─> pedidoGeradoId = pedido.id
   ├─> Registra histórico (CONVERSAO_PEDIDO)
   └─> Retorna SolicitacaoDePedidoDTO

8. Redireciona /pedidos
```

### 4.2 Fluxo de Autenticação

```
1. POST /auth/login { email, senha }
   └─> AuthController.login()
       ├─> AuthenticationManager.authenticate()
       ├─> JwtService.gerarToken(user)
       └─> { token: "eyJ..." }

2. Frontend recebe token
   └─> authStore.login()
       ├─> authService.validateToken(token)
       │   ├─> jwt_decode(token)
       │   ├─> Extrai email, role, exp
       │   └─> Valida expiração
       ├─> localStorage.setItem('authToken', token)
       └─> isAuthenticated = true

3. Requisições subsequentes
   └─> api.interceptors.request
       ├─> const token = localStorage.getItem('authToken')
       └─> config.headers.Authorization = `Bearer ${token}`

4. Backend valida
   └─> JwtFilter.doFilterInternal()
       ├─> Extrai token do header
       ├─> JwtService.isTokenValido()
       ├─> Extrai email
       ├─> UserDetailsService.loadUserByUsername(email)
       ├─> Cria UsernamePasswordAuthenticationToken
       └─> SecurityContextHolder.setAuthentication()

5. Token expirado
   └─> api.interceptors.response
       ├─> error.response.status === 401
       ├─> localStorage.removeItem('authToken')
       └─> window.location.href = '/login'
```

### 4.3 Fluxo de Upload de PDF em Cotação

```
1. Usuário seleciona PDF
   └─> CotacaoInlineForm @change="handleFileChange"

2. Validação client-side
   ├─> if (!file.type.includes('pdf')) → warning('Apenas PDF')
   ├─> if (file.size > 10MB) → warning('Max 10MB')
   └─> Preview nome do arquivo

3. Criar cotação
   └─> POST /api/cotacoes
       {
         fornecedorProdutoId, tipoFornecedor, preco,
         itensPedidoIds, anexoPdf: null (temporariamente)
       }
       └─> Retorna cotacao { id: 123 }

4. Upload PDF
   └─> cotacaoService.uploadArquivoPdf(123, file)
       ├─> arquivoParaBytes(file) → Uint8Array
       ├─> FormData.append('pdf', Blob)
       └─> POST /api/cotacoes/123/anexo
           └─> CotacaoController.uploadAnexo()
               ├─> MultipartFile.getBytes()
               ├─> cotacao.setAnexoPdf(bytes)
               └─> repository.save()

5. Visualizar PDF
   └─> GET /api/cotacoes/123/anexo
       ├─> CotacaoController.obterAnexoPdf()
       ├─> ResponseEntity<byte[]>
       │   headers: Content-Type: application/pdf
       └─> Frontend cria Blob → window.open(URL.createObjectURL(blob))
```

---

## 5. INCONSISTÊNCIAS IDENTIFICADAS

### 5.1 Problemas Críticos (Corrigir Imediatamente)

#### Backend

1. **Segurança - Endpoints Públicos Demais**
   - **Localização:** `security/SecurityConfig.java`
   - **Problema:** `/api/solicitacoes-pedido/**`, `/api/itens-pedido/**`, `/api/rascunhos/**` estão públicos
   - **Impacto:** Qualquer pessoa pode criar/modificar/deletar pedidos e rascunhos sem autenticação
   - **Solução:** Mover para endpoints autenticados, limitar por role (ADMIN ou USER)

2. **Segurança - Chaves JWT Expostas**
   - **Localização:** `src/main/resources/application-dev.properties` linha 1, `application-prod.properties`
   - **Problema:** Secret keys hardcoded em Base64
   - **Impacto:** Qualquer pessoa com acesso ao repositório pode gerar tokens válidos
   - **Solução:** Usar `${JWT_SECRET}` de variáveis de ambiente

3. **Bug #5 - Relacionamento N:N Limitado em Cotacao**
   - **Localização:** `model/cotacao/Cotacao.java` linhas 42-57
   - **Problema:** `@ManyToMany Set<ItemPedido> itensPedido` não permite preço unitário por item
   - **Impacto:** Impossível armazenar cotações com preços diferentes por item
   - **Solução:** Criar entidade intermediária `CotacaoItem { cotacaoId, itemPedidoId, precoUnitario, quantidade }`

4. **Sem Testes (0% Coverage)**
   - **Localização:** `src/test/` vazio
   - **Problema:** Nenhum teste unitário, integração ou E2E
   - **Impacto:** Refatorações arriscadas, bugs em produção
   - **Solução:** Implementar testes com JUnit 5, Mockito, TestContainers

5. **Sem Paginação**
   - **Localização:** Todos os controllers com `findAll()`
   - **Problema:** Retorna todos os registros em memória
   - **Impacto:** Performance degradada com dados crescentes
   - **Solução:** `Page<DTO> listar(Pageable pageable)`

#### Frontend

6. **513 console.log() em Produção**
   - **Localização:** `api.js` (57-72), `cotacaoService.js` (152), `NovoPedidoView.vue`, etc.
   - **Problema:** Debugging logs com dados sensíveis
   - **Impacto:** Performance, segurança, dados expostos em console
   - **Solução:** Remover todos ou usar `if (import.meta.env.DEV) console.log()`

7. **9 Arquivos com alert() Ainda**
   - **Localização:** `DashboardHeader.vue`, `FornecedorForm.vue`, componentes wizard
   - **Problema:** Pop-ups nativos bloqueiam fluxo
   - **Impacto:** UX ruim, não estilizável
   - **Solução:** Migrar 100% para `useToast()`

8. **Token em localStorage Plaintext**
   - **Localização:** `stores/auth.js`
   - **Problema:** JWT armazenado sem criptografia
   - **Impacto:** Vulnerável a XSS (cross-site scripting)
   - **Solução:** Usar sessionStorage ou httpOnly cookies (requer mudança backend)

### 5.2 Problemas Altos (Corrigir em Curto Prazo)

9. **Validações de Negócio Faltando (Backend)**
   - **Problema:** Não valida quantidade < 0, preço < 0, data limite no passado
   - **Impacto:** Dados inválidos persistidos
   - **Solução:** Adicionar `@Min(1)` em quantidade, `@Positive` em preço, custom validator para datas

10. **Conversão Rascunho→Pedido Sem Transação Robusta**
    - **Localização:** `RascunhoService.converterRascunhoParaPedido()`
    - **Problema:** Se falha após criar pedido, inconsistência
    - **Impacto:** Dados órfãos, rascunhos corrompidos
    - **Solução:** `@Transactional(rollbackFor = Exception.class)` + compensating transactions

11. **Falta @Version para Otimistic Locking**
    - **Localização:** `model/cotacao/Cotacao.java`, `model/pedido/SolicitacaoDePedido.java`
    - **Problema:** `@Retryable` configurado mas sem `@Version` na entidade
    - **Impacto:** Retry não funciona corretamente
    - **Solução:** Adicionar `@Version Long version` em entidades mutáveis

12. **Validadores Customizados Não Aplicados (Backend)**
    - **Localização:** `validation/CEPValidator.java`, `CelularValidator.java`, `TelefoneFixoValidator.java`
    - **Problema:** Definidos mas não usados em DTOs
    - **Impacto:** Dados mal formatados aceitos
    - **Solução:** Adicionar `@CEP`, `@Celular`, `@TelefoneFixo` em DTOs

13. **Duplicação de Código - relatorioClient (Frontend)**
    - **Localização:** `relatorioService.js` e `cotacaoService.js`
    - **Problema:** Dois clientes Axios com mesma configuração
    - **Impacto:** Manutenção duplicada, timeouts inconsistentes
    - **Solução:** Unificar em `api.js` com método específico para blobs

14. **Loading States Inconsistentes (Frontend)**
    - **Localização:** `StepAdicionarCotacoesRascunho.vue`, `FornecedorForm.vue`
    - **Problema:** Algumas operações sem feedback visual
    - **Impacto:** Usuário não sabe se ação está processando
    - **Solução:** Padronizar `isLoading` + LoadingSpinner em todas operações async

15. **Tratamento de Erro Inconsistente (Frontend)**
    - **Localização:** `itemPedidoService.listarTodos()` retorna `[]` vazio em erro
    - **Problema:** Alguns services silenciam erros, outros propagam
    - **Impacto:** Dificuldade em debug, UI sem feedback
    - **Solução:** Padronizar: sempre propagar erro, tratar na view

### 5.3 Problemas Médios (Melhorias Importantes)

16. **Inconsistência de Nomenclatura**
    - **Backend:** `senha` vs `password`, `id` Integer vs Long
    - **Frontend:** `listar()` vs `listarTodos()`, `remover()` vs `excluir()` vs `deletar()`
    - **Impacto:** Confusão, bugs por case mismatch
    - **Solução:** Padronizar glossário

17. **DTOs Não Otimizados (Backend)**
    - **Problema:** `SolicitacaoDePedidoDTO` carrega tudo, sem `@JsonIgnore` em campos sensíveis
    - **Impacto:** Payload grande, potencial vazamento de dados
    - **Solução:** Criar DTOs específicos (lista vs detalhes), adicionar `@JsonIgnore` onde necessário

18. **PedidoWizard.vue Monolítico (Frontend)**
    - **Localização:** `features/pedidos/components/pedido-wizard/`
    - **Problema:** 5886+ linhas em grupo de arquivos, lógica complexa sem state machine
    - **Impacto:** Manutenção difícil, testabilidade baixa
    - **Solução:** Refatorar com Composition API, extrair lógica para composables

19. **Duplicação: StepAdicionarCotacoes vs StepAdicionarCotacoesRascunho**
    - **Localização:** `features/pedidos/components/pedido-wizard/`
    - **Problema:** 95% código duplicado
    - **Impacto:** Bugs duplicados, manutenção dobrada
    - **Solução:** Criar componente genérico com prop `tipo: 'pedido'|'rascunho'`

20. **Placeholder Functions (Frontend)**
    - **Localização:** `authService.forgotPassword()`
    - **Problema:** Apenas delay simulado, não faz requisição real
    - **Impacto:** Funcionalidade não funciona
    - **Solução:** Implementar endpoint no backend + integrar

21. **Sem Rate Limiting (Backend)**
    - **Localização:** `/auth/login`
    - **Problema:** Sem proteção contra brute force
    - **Impacto:** Vulnerabilidade a ataques
    - **Solução:** Spring Cloud Gateway ou Bucket4j

22. **Validação Incompleta em Formulários (Frontend)**
    - **Localização:** `FornecedorForm.vue`, `CotacaoForm.vue`
    - **Problema:** Valida length mas não calcula dígitos verificadores (CNPJ)
    - **Impacto:** Dados inválidos chegam no backend
    - **Solução:** Implementar algoritmo completo de validação

### 5.4 Problemas Baixos (Nice to Have)

23. **Cache de Templates JasperReports (Backend)**
    - **Problema:** Compila template a cada geração
    - **Impacto:** Performance
    - **Solução:** Cachear templates compilados

24. **@EntityGraph vs LEFT JOIN FETCH (Backend)**
    - **Problema:** Uso de LEFT JOIN FETCH pode ser otimizado
    - **Solução:** Considerar `@EntityGraph` para queries complexas

25. **Logging Estruturado (Backend)**
    - **Problema:** Logs simples sem contexto estruturado
    - **Solução:** Logback com JSON encoder, correlationId

26. **Metrics/Monitoring (Backend)**
    - **Problema:** Sem Spring Boot Actuator, Prometheus, Grafana
    - **Solução:** Implementar observability completa

27. **Acessibilidade (Frontend)**
    - **Problema:** Falta aria-labels, navegação por teclado, focus traps
    - **Impacto:** WCAG 2.1 não conformidade
    - **Solução:** Auditoria com Lighthouse, implementar ARIA

28. **Lazy Loading Incompleto (Frontend)**
    - **Problema:** Views lazy, componentes features não
    - **Impacto:** Bundle inicial maior
    - **Solução:** Lazy load componentes pesados (PedidoWizard, CotacaoForm)

---

## 6. GUIA DE MANUTENÇÃO

### 6.1 Adicionando Nova Entidade

**Backend:**

1. **Criar Entidade JPA**
   ```java
   // model/NomeEntidade.java
   @Entity
   @Table(name = "nome_tabela")
   public class NomeEntidade {
       @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
       Long id;
       // campos
   }
   ```

2. **Criar Repository**
   ```java
   // repository/NomeEntidadeRepository.java
   public interface NomeEntidadeRepository extends JpaRepository<NomeEntidade, Long> {
       // queries customizadas
   }
   ```

3. **Criar DTOs**
   ```java
   // dto/nomeentidade/NomeEntidadeDTO.java
   public record NomeEntidadeDTO(...) {}
   public record NomeEntidadeCreateDTO(...) {}
   public record NomeEntidadeUpdateDTO(...) {}
   ```

4. **Criar Mapper MapStruct**
   ```java
   // mapper/NomeEntidadeMapper.java
   @Mapper(componentModel = "spring")
   public interface NomeEntidadeMapper {
       NomeEntidadeDTO toDTO(NomeEntidade entity);
       NomeEntidade toEntity(NomeEntidadeCreateDTO dto);
       void updateEntityFromDTO(NomeEntidadeUpdateDTO dto, @MappingTarget NomeEntidade entity);
   }
   ```

5. **Criar Service**
   ```java
   // service/NomeEntidadeService.java
   @Service @Transactional
   public class NomeEntidadeService {
       private final NomeEntidadeRepository repository;
       private final NomeEntidadeMapper mapper;
       // CRUD methods
   }
   ```

6. **Criar Controller**
   ```java
   // controller/NomeEntidadeController.java
   @RestController
   @RequestMapping("/api/nome-entidades")
   @SecurityRequirement(name = "bearerAuth")
   public class NomeEntidadeController {
       private final NomeEntidadeService service;
       // endpoints REST
   }
   ```

7. **Atualizar SecurityConfig**
   - Adicionar em `PUBLIC_ENDPOINTS` ou `ADMIN_ENDPOINTS` conforme necessário

8. **Criar Migração Flyway**
   ```sql
   -- src/main/resources/db/migration/V7__create-nome-entidade-table.sql
   CREATE TABLE nome_tabela (
       id BIGSERIAL PRIMARY KEY,
       ...
   );
   CREATE INDEX idx_nome_tabela_campo ON nome_tabela(campo);
   ```

**Frontend:**

9. **Criar Service**
   ```javascript
   // services/nomeEntidadeService.js
   import api from './api';

   export default {
       async listar() { return api.get('/api/nome-entidades'); },
       async buscarPorId(id) { return api.get(`/api/nome-entidades/${id}`); },
       async criar(data) { return api.post('/api/nome-entidades', data); },
       async atualizar(id, data) { return api.put(`/api/nome-entidades/${id}`, data); },
       async excluir(id) { return api.delete(`/api/nome-entidades/${id}`); }
   };
   ```

10. **Criar View de Listagem**
    ```vue
    <!-- views/NomeEntidadeView.vue -->
    <template>
      <div>
        <LoadingSpinner :show="isLoading" />
        <div v-for="item in itens" :key="item.id">
          <!-- Exibir item -->
        </div>
      </div>
    </template>

    <script setup>
    import { ref, onMounted } from 'vue';
    import nomeEntidadeService from '@/services/nomeEntidadeService';

    const itens = ref([]);
    const isLoading = ref(false);

    async function carregar() {
      isLoading.value = true;
      try {
        const response = await nomeEntidadeService.listar();
        itens.value = response.data;
      } finally {
        isLoading.value = false;
      }
    }

    onMounted(carregar);
    </script>
    ```

11. **Adicionar Rota**
    ```javascript
    // router/index.js
    {
      path: '/nome-entidades',
      name: 'NomeEntidades',
      component: () => import('@/views/NomeEntidadeView.vue'),
      meta: { requiresAuth: true }
    }
    ```

12. **Adicionar ao Sidebar**
    ```vue
    <!-- components/DashboardSidebar.vue -->
    <router-link to="/nome-entidades">Nome Entidades</router-link>
    ```

### 6.2 Adicionando Novo Endpoint

**Backend:**

1. **Adicionar método no Service**
   ```java
   @Transactional(readOnly = true)
   public List<NomeEntidadeDTO> buscarPorCriterio(String criterio) {
       return repository.findByCriterio(criterio)
           .stream()
           .map(mapper::toDTO)
           .collect(Collectors.toList());
   }
   ```

2. **Adicionar endpoint no Controller**
   ```java
   @GetMapping("/buscar")
   public ResponseEntity<List<NomeEntidadeDTO>> buscarPorCriterio(
       @RequestParam String criterio
   ) {
       return ResponseEntity.ok(service.buscarPorCriterio(criterio));
   }
   ```

3. **Atualizar SecurityConfig se necessário**

**Frontend:**

4. **Adicionar método no Service**
   ```javascript
   async buscarPorCriterio(criterio) {
       const response = await api.get('/api/nome-entidades/buscar', {
           params: { criterio }
       });
       return response.data;
   }
   ```

5. **Usar na View**
   ```vue
   <script setup>
   async function buscar() {
       const resultados = await nomeEntidadeService.buscarPorCriterio(criterio.value);
       itens.value = resultados;
   }
   </script>
   ```

### 6.3 Adicionando Validação Customizada

**Backend:**

1. **Criar Annotation**
   ```java
   // validation/CustomValidation.java
   @Target({ElementType.FIELD})
   @Retention(RetentionPolicy.RUNTIME)
   @Constraint(validatedBy = CustomValidator.class)
   public @interface CustomValidation {
       String message() default "Validação falhou";
       Class<?>[] groups() default {};
       Class<? extends Payload>[] payload() default {};
   }
   ```

2. **Criar Validator**
   ```java
   // validation/CustomValidator.java
   public class CustomValidator implements ConstraintValidator<CustomValidation, String> {
       @Override
       public boolean isValid(String value, ConstraintValidatorContext context) {
           if (value == null) return true;
           // lógica de validação
           return true;
       }
   }
   ```

3. **Aplicar em DTO**
   ```java
   public record NomeDTO(
       @CustomValidation String campo
   ) {}
   ```

**Frontend:**

4. **Criar função de validação**
   ```javascript
   // utils/validations.js
   export function validarCustom(value) {
       if (!value) return true;
       // lógica
       return /regex/.test(value);
   }
   ```

5. **Usar em formulário**
   ```vue
   <script setup>
   import { validarCustom } from '@/utils/validations';

   const erros = ref({});

   function validarFormulario() {
       if (!validarCustom(form.campo)) {
           erros.value.campo = 'Campo inválido';
           return false;
       }
       return true;
   }
   </script>
   ```

---

## 7. ROTEIRO DE MELHORIAS

### 7.1 Prioridade Crítica (Sprint 1)

- [ ] **Segurança: Mover endpoints para autenticados**
  - Mover `/api/solicitacoes-pedido/**`, `/api/itens-pedido/**`, `/api/rascunhos/**` para autenticação obrigatória
  - Implementar roles: ADMIN, USER

- [ ] **Segurança: Externalizar JWT secret**
  - Remover secrets de `application*.properties`
  - Usar `${JWT_SECRET}` de variáveis de ambiente

- [ ] **Frontend: Remover todos console.log()**
  - Criar script: `find src -type f -name '*.vue' -o -name '*.js' | xargs sed -i '/console.log/d'`
  - Testar funcionalidades

- [ ] **Frontend: Migrar alert() restantes para Toast**
  - 9 arquivos: DashboardHeader, FornecedorForm, componentes wizard
  - Importar `useToast()`, substituir `alert()` por `warning()`, `error()`, `success()`

- [ ] **Backend: Implementar testes básicos**
  - UserServiceTest, RascunhoServiceTest, AuthControllerTest
  - Target: 30% coverage

### 7.2 Prioridade Alta (Sprint 2)

- [ ] **Backend: Adicionar paginação**
  - Atualizar repositories: `Page<Entity> findAll(Pageable pageable)`
  - Atualizar controllers: aceitar `Pageable`
  - Frontend: implementar pagination UI

- [ ] **Backend: Bug #5 - Refatorar Cotacao**
  - Criar entidade `CotacaoItem { cotacaoId, itemPedidoId, precoUnitario, quantidade }`
  - Migração Flyway
  - Atualizar service, controller, DTOs

- [ ] **Backend: Adicionar @Version para otimistic locking**
  - Adicionar `@Version Long version` em Cotacao, SolicitacaoDePedido
  - Testar `@Retryable`

- [ ] **Frontend: Unificar relatorioClient em api.js**
  - Criar método `api.getBlob(url)` com responseType: 'blob'
  - Remover duplicação em cotacaoService e relatorioService

- [ ] **Frontend: Implementar forgotPassword real**
  - Backend: criar endpoint `/auth/forgot-password` (enviar email)
  - Frontend: chamar endpoint real

### 7.3 Prioridade Média (Sprint 3)

- [ ] **Backend: Adicionar validações de negócio**
  - Quantidade `@Min(1)`, preço `@Positive`
  - Data limite no futuro: custom validator

- [ ] **Backend: Aplicar validadores customizados**
  - Adicionar `@CEP`, `@Celular`, `@TelefoneFixo` em DTOs de Endereco e Contato

- [ ] **Frontend: Refatorar PedidoWizard**
  - Extrair lógica para composables: `useWizardSteps()`, `useRascunhoState()`
  - Separar componentes por responsabilidade

- [ ] **Frontend: Unificar StepAdicionarCotacoes**
  - Criar componente genérico com prop `tipo: 'pedido'|'rascunho'`
  - Deletar versão duplicada

- [ ] **Frontend: Padronizar nomenclatura**
  - Glossário: `remover()`, `listar()`, `criar()`, `atualizar()`
  - Aplicar em todos os services

### 7.4 Prioridade Baixa (Backlog)

- [ ] **Backend: Implementar rate limiting**
  - Spring Cloud Gateway ou Bucket4j em `/auth/login`

- [ ] **Backend: Cache de templates JasperReports**
  - Configurar cache para templates compilados

- [ ] **Backend: Logging estruturado**
  - Logback JSON encoder, correlationId

- [ ] **Backend: Metrics com Actuator + Prometheus**
  - Spring Boot Actuator, endpoints `/actuator/health`, `/actuator/metrics`
  - Grafana dashboards

- [ ] **Frontend: Acessibilidade (WCAG 2.1)**
  - Adicionar aria-labels
  - Implementar focus traps em modais
  - Navegação completa por teclado

- [ ] **Frontend: Lazy loading completo**
  - Lazy load PedidoWizard, CotacaoForm, FornecedorForm

- [ ] **Frontend: Migrar localStorage para sessionStorage/httpOnly**
  - Backend: configurar JWT em httpOnly cookies
  - Frontend: remover localStorage de token

---

## APÊNDICES

### A. Glossário de Termos

| Termo | Definição |
|-------|-----------|
| **Rascunho** | Versão preliminar de pedido que pode ser editada livremente antes de finalização |
| **Solicitação de Pedido** | Pedido formal submetido para aprovação e cotação |
| **Item Pedido** | Item individual dentro de uma solicitação (nome, quantidade, descrição) |
| **Cotação** | Proposta de fornecedor com preço e prazo para um conjunto de itens |
| **Cotação de Rascunho** | Cotação vinculada a um rascunho, migrada para cotação normal na conversão |
| **Fornecedor de Produto** | Fornecedor que vende produtos físicos |
| **Fornecedor de Serviço** | Fornecedor que presta serviços |
| **Status Rascunho** | ATIVO (em edição), EM_COTACAO (cotações sendo adicionadas), FINALIZADO (convertido) |
| **Status Pedido** | PENDENTE, EM_ANALISE, EM_ANDAMENTO, APROVADO, CANCELADO |
| **NumeroItemDisponivel** | Pool de números de itens reutilizáveis em um rascunho |
| **Histórico** | Registro de auditoria de todas as modificações |

### B. Comandos Úteis

**Backend:**
```bash
# Compilar
./mvnw clean install

# Executar
./mvnw spring-boot:run

# Executar com profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Gerar classes MapStruct
./mvnw compile

# Executar Flyway migrations
./mvnw flyway:migrate
```

**Frontend:**
```bash
# Instalar dependências
npm install

# Dev server
npm run dev

# Build produção
npm run build

# Preview build
npm run preview

# Lint
npm run lint
```

**Docker:**
```bash
# Subir PostgreSQL
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar
docker-compose down

# Reset completo
docker-compose down -v
```

### C. Variáveis de Ambiente Recomendadas

**Backend (.env ou docker-compose):**
```env
JWT_SECRET=sua-chave-super-segura-aqui-com-no-minimo-256-bits
DATABASE_URL=jdbc:postgresql://localhost:5432/gestorcomprasbackend
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=admin
SERVER_PORT=8081
SPRING_PROFILES_ACTIVE=dev
```

**Frontend (.env.local):**
```env
VITE_API_BASE_URL=http://localhost:8081
```

### D. Estrutura de Commits Recomendada

```
feat: adiciona autenticação com JWT
fix: corrige bug #5 em relacionamento N:N de cotações
refactor: extrai lógica de wizard para composables
docs: atualiza CLAUDE.md com novos endpoints
test: adiciona testes para RascunhoService
perf: otimiza query de listagem de pedidos com paginação
style: padroniza nomenclatura de métodos de service
chore: atualiza dependências do Maven
```

---

**Documentação gerada em:** 2025-11-27
**Por:** Análise automatizada completa de código
**Versão:** 2.0 (Branch V2)
**Próxima Revisão:** Após implementação das melhorias críticas
