# 🛒 Gestor de Compras

Sistema completo para gestão de compras, cotações, fornecedores e pedidos com backend em Spring Boot e frontend em Vue.js.

## 🚀 Início Rápido

### Executar Backend

```cmd
start-backend.bat
```

O backend será iniciado em:
- **API**: http://localhost:8081
- **Console H2**: http://localhost:8081/h2-console
- **Swagger**: http://localhost:8081/swagger-ui.html

### Executar Frontend

```cmd
start-frontend.bat
```

O frontend será iniciado em:
- **Interface**: http://localhost:5173

### Executar Ambos

Abra dois terminais e execute cada script separadamente, ou clique duas vezes nos arquivos `.bat`.

## 👥 Usuários de Teste

| Tipo | Email | Senha |
|------|-------|-------|
| **Administrador** | admin@gestor.com | admin123 |
| **Usuário** | user@gestor.com | user123 |

## 🗄️ Banco de Dados

### Configuração Atual: PostgreSQL

**Desenvolvimento:**
- **Host**: localhost
- **Porta**: 5432
- **Database**: gestorcomprasbackend
- **Username**: postgres
- **Password**: (configurar em `application-dev.properties`)

### Console H2 (alternativo)

Para usar H2 em memória, altere o profile em `application.properties` para:
```properties
spring.profiles.active=h2
```

Acesse o console em: http://localhost:8081/h2-console
- **JDBC URL**: `jdbc:h2:mem:gestorcompras`
- **Username**: `sa`
- **Password**: (deixe em branco)

## 📁 Estrutura do Projeto

```
gestor-compras/
├── gestor-compras-backend/        # API Spring Boot
│   ├── src/main/java/             # Código fonte Java
│   │   ├── config/                # Configurações
│   │   ├── controller/            # Endpoints REST
│   │   ├── dto/                   # Data Transfer Objects
│   │   ├── exception/             # Tratamento de erros
│   │   ├── mapper/                # MapStruct mappers
│   │   ├── model/                 # Entidades JPA
│   │   ├── repository/            # Repositórios
│   │   ├── security/              # JWT e autenticação
│   │   ├── service/               # Lógica de negócio
│   │   └── validation/            # Validadores customizados
│   ├── src/main/resources/
│   │   ├── db/migration/          # Scripts Flyway
│   │   ├── relatorios/            # JasperReports
│   │   └── application*.properties
│   └── pom.xml                    # Dependências Maven
├── gestor-compras-frontend/       # Interface Vue.js
│   └── vue-project/
│       ├── src/
│       │   ├── components/        # Componentes reutilizáveis
│       │   ├── features/          # Componentes por funcionalidade
│       │   ├── layouts/           # Layouts da aplicação
│       │   ├── router/            # Rotas Vue Router
│       │   ├── services/          # Serviços API
│       │   ├── stores/            # Estado Pinia
│       │   ├── utils/             # Utilitários
│       │   └── views/             # Páginas
│       └── package.json           # Dependências NPM
├── start-backend.bat              # Inicia o backend
├── start-frontend.bat             # Inicia o frontend
└── README.md                      # Este arquivo
```

## 🔧 Tecnologias

### Backend
- **Spring Boot** 3.3.1
- **Java** 21
- **Spring Security** com JWT (jjwt 0.11.5)
- **Spring Data JPA**
- **PostgreSQL** + H2 (alternativo)
- **Flyway** (migrations)
- **MapStruct** 1.5.5 (mapeamento de objetos)
- **JasperReports** 6.21.3 (relatórios)
- **SpringDoc OpenAPI** 2.6.0 (documentação API)
- **Maven**

### Frontend
- **Vue.js** 3.5.18 (Composition API)
- **Vite** 6.2.0 (build tool)
- **Vue Router** 4.5.1 (navegação)
- **Pinia** 3.0.3 (gerenciamento de estado)
- **Axios** 1.11.0 (cliente HTTP)
- **Tailwind CSS** 3.4.1 (estilização)

## 🏗️ Arquitetura do Sistema

### Arquitetura Geral

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend (Vue 3)                      │
│  ┌────────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │   Views    │→ │ Features │→ │Components│→ │   UI     │ │
│  └────────────┘  └──────────┘  └──────────┘  └──────────┘ │
│         ↕                ↕              ↕           ↕        │
│  ┌────────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │   Router   │  │  Stores  │  │ Services │  │  Utils   │ │
│  │ (Rotas)    │  │ (Pinia)  │  │ (API)    │  │(Helpers) │ │
│  └────────────┘  └──────────┘  └──────────┘  └──────────┘ │
└───────────────────────────┬─────────────────────────────────┘
                            │ HTTP/REST (Axios)
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    Backend (Spring Boot)                     │
│  ┌────────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │Controller  │→ │ Service  │→ │Repository│→ │   Model  │ │
│  │  (REST)    │  │(Business)│  │  (JPA)   │  │(Entities)│ │
│  └────────────┘  └──────────┘  └──────────┘  └──────────┘ │
│         ↕                ↕              ↕           ↕        │
│  ┌────────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │  Security  │  │  Mapper  │  │    DTO   │  │Exception │ │
│  │   (JWT)    │  │(MapStruct│  │ (Transfer│  │ Handler  │ │
│  └────────────┘  └──────────┘  └──────────┘  └──────────┘ │
└───────────────────────────┬─────────────────────────────────┘
                            │ JDBC
                            ↓
                    ┌───────────────┐
                    │  PostgreSQL   │
                    │   Database    │
                    └───────────────┘
```

### Camadas do Frontend

#### 1. **Views** (Páginas)
- **Localização**: `src/views/`
- **Propósito**: Páginas completas da aplicação
- **Responsabilidade**: Composição de features e layouts
- **Exemplos**: `DashboardView.vue`, `PedidosView.vue`, `LoginView.vue`

#### 2. **Features** (Módulos de Funcionalidade)
- **Localização**: `src/features/`
- **Propósito**: Componentes específicos de domínio
- **Organização**: Por funcionalidade (cotacoes, pedidos, fornecedores, dashboard)
- **Exemplos**: 
  - `features/pedidos/PedidoCard.vue`
  - `features/cotacoes/FormularioCotacao.vue`
  - `features/dashboard/MetricCard.vue`

#### 3. **Components** (Componentes Reutilizáveis)
- **Localização**: `src/components/ui/`
- **Propósito**: Componentes UI genéricos e reutilizáveis
- **Exemplos**: Botões, modais, inputs, cards

#### 4. **Services** (Camada de API)
- **Localização**: `src/services/`
- **Propósito**: Comunicação com backend via Axios
- **Padrão**: Um service por domínio
- **Documentação**: JSDoc completo com @typedef, @param, @returns
- **Principais**:
  - `authService.js` - Autenticação e tokens JWT
  - `pedidoService.js` - CRUD de pedidos
  - `cotacaoService.js` - Gestão de cotações
  - `fornecedorService.js` - Gestão de fornecedores
  - `relatorioService.js` - Geração de relatórios

#### 5. **Stores** (Estado Global - Pinia)
- **Localização**: `src/stores/`
- **Propósito**: Gerenciamento de estado compartilhado
- **Padrão**: Composition API do Pinia
- **Store Principal**: `auth.js` - Estado de autenticação global

#### 6. **Router** (Navegação)
- **Localização**: `src/router/index.js`
- **Propósito**: Configuração de rotas e guards
- **Features**:
  - Lazy loading de componentes
  - Navigation guards (beforeEach)
  - Proteção de rotas (requiresAuth, requiresGuest)
  - Redirecionamentos automáticos

#### 7. **Composables** (Lógica Reativa Reutilizável)
- **Localização**: `src/composables/`
- **Propósito**: Funções reativas compartilhadas (Composition API)
- **Principais**:
  - `useDebounce.js` - Debounce de valores e callbacks
  - `useModal.js` - Controle de scroll em modais
  - `useToast.js` - Sistema de notificações toast
  - `useMobileSidebar.js` - Estado do menu mobile

#### 8. **Utils** (Utilitários)
- **Localização**: `src/utils/`
- **Propósito**: Funções auxiliares puras
- **Principais**:
  - `pedidoUtils.js` - Formatação, validações, status
  - `genderUtils.js` - Personalização por gênero
  - `logger.js` - Logging condicional por ambiente

### Camadas do Backend

#### 1. **Controller** (Endpoints REST)
- **Localização**: `com.gestordecompras.controller`
- **Propósito**: Expor endpoints HTTP
- **Responsabilidade**: Receber requests, validar, delegar para service
- **Anotações**: `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`
- **Exemplos**: `PedidoController`, `CotacaoController`, `AuthController`

#### 2. **Service** (Lógica de Negócio)
- **Localização**: `com.gestordecompras.service`
- **Propósito**: Implementar regras de negócio
- **Responsabilidade**: Orquestrar repositórios, validações, transformações
- **Anotações**: `@Service`, `@Transactional`
- **Exemplos**: `PedidoService`, `HistoricoPedidoService`, `RelatorioService`

#### 3. **Repository** (Acesso a Dados)
- **Localização**: `com.gestordecompras.repository`
- **Propósito**: Comunicação com banco de dados
- **Responsabilidade**: Queries JPA/JPQL
- **Anotações**: `@Repository`, extends `JpaRepository`
- **Features**: Query methods, @Query customizadas

#### 4. **Model/Entity** (Entidades JPA)
- **Localização**: `com.gestordecompras.model`
- **Propósito**: Representar tabelas do banco
- **Responsabilidade**: Mapeamento objeto-relacional
- **Anotações**: `@Entity`, `@Table`, `@Id`, `@ManyToOne`, `@OneToMany`

#### 5. **DTO** (Data Transfer Objects)
- **Localização**: `com.gestordecompras.dto`
- **Propósito**: Trafegar dados entre camadas
- **Responsabilidade**: Validação, serialização JSON
- **Anotações**: `@NotNull`, `@Size`, `@Email`, `@Valid`

#### 6. **Mapper** (Conversão Entity ↔ DTO)
- **Localização**: `com.gestordecompras.mapper`
- **Propósito**: Converter entre entities e DTOs
- **Tecnologia**: MapStruct
- **Anotações**: `@Mapper`, `@Mapping`

#### 7. **Security** (Autenticação e Autorização)
- **Localização**: `com.gestordecompras.security`
- **Propósito**: JWT, filtros, configuração de segurança
- **Componentes**:
  - `JwtTokenProvider` - Geração e validação de tokens
  - `JwtAuthenticationFilter` - Interceptor de requisições
  - `SecurityConfig` - Configuração Spring Security

#### 8. **Exception** (Tratamento de Erros)
- **Localização**: `com.gestordecompras.exception`
- **Propósito**: Centralizar tratamento de exceções
- **Componentes**:
  - `GlobalExceptionHandler` - `@ControllerAdvice`
  - Custom exceptions (ResourceNotFoundException, etc)

### Fluxo de Dados

#### Fluxo Frontend → Backend (Exemplo: Criar Pedido)

```
1. View (NovoPedidoView.vue)
   └─> Usuário preenche formulário
   
2. Service (pedidoService.js)
   └─> const response = await pedidoService.create(pedidoData)
   
3. Axios Interceptor
   └─> Adiciona headers (Authorization: Bearer <token>)
   
4. HTTP POST → http://localhost:8081/api/pedidos
   
5. Backend Controller (PedidoController)
   └─> @PostMapping("/api/pedidos")
   └─> Valida DTO com @Valid
   
6. Backend Service (PedidoService)
   └─> Aplica regras de negócio
   └─> Cria histórico automático
   
7. Backend Repository (PedidoRepository)
   └─> Persiste no banco via JPA
   
8. Response DTO
   └─> Controller retorna PedidoDTO
   
9. Frontend recebe response
   └─> Service retorna data
   └─> View atualiza UI
   └─> Toast de sucesso exibido
```

#### Fluxo de Autenticação JWT

```
1. Login (LoginView.vue)
   └─> authStore.login(email, password)
   
2. authService.login()
   └─> POST /api/auth/login
   
3. Backend valida credenciais
   └─> Gera token JWT (JwtTokenProvider)
   └─> Retorna { token, user }
   
4. Frontend salva token
   └─> sessionStorage.setItem('token', token)
   └─> authStore.isAuthenticated = true
   
5. Requisições subsequentes
   └─> Axios interceptor adiciona header
   └─> Authorization: Bearer <token>
   
6. Backend valida token
   └─> JwtAuthenticationFilter
   └─> Verifica assinatura e expiração
   └─> Extrai usuário do token
   └─> Processa request
```

### Padrões de Código

#### Padrão de Nomenclatura

**Frontend:**
- **Componentes Vue**: PascalCase (`PedidoCard.vue`, `ModalConfirmacao.vue`)
- **Services**: camelCase com sufixo `Service` (`pedidoService.js`)
- **Composables**: camelCase com prefixo `use` (`useDebounce.js`)
- **Utils**: camelCase (`pedidoUtils.js`, `logger.js`)
- **Stores**: camelCase (`auth.js`)

**Backend:**
- **Classes**: PascalCase (`PedidoController`, `PedidoService`)
- **Métodos**: camelCase (`criarPedido`, `buscarPorId`)
- **Constantes**: UPPER_SNAKE_CASE (`MAX_UPLOAD_SIZE`)
- **Packages**: lowercase (`com.gestordecompras.controller`)

#### Convenções de Commit

```
feat: Nova funcionalidade
fix: Correção de bug
docs: Alteração em documentação
style: Formatação, ponto e vírgula, etc
refactor: Refatoração de código
test: Adição/alteração de testes
chore: Tarefas de build, configs, etc
```

**Exemplos:**
```
feat(pedidos): adicionar filtro por status
fix(cotacao): corrigir upload de PDF
docs(readme): atualizar guia de instalação
refactor(services): extrair lógica comum
```

### Gerenciamento de Estado (Pinia)

**Estrutura de um Store:**
```javascript
export const useAuthStore = defineStore('auth', () => {
  // States (refs reativos)
  const isAuthenticated = ref(false)
  const user = ref(null)
  
  // Actions (funções)
  const login = async (email, password) => { }
  const logout = () => { }
  
  // Getters (computed, se necessário)
  // const userName = computed(() => user.value?.name)
  
  return { isAuthenticated, user, login, logout }
})
```

**Uso em Componentes:**
```javascript
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// Acessar state
console.log(authStore.isAuthenticated)

// Chamar action
await authStore.login(email, password)
```

### Roteamento e Navegação

**Estrutura de Rota:**
```javascript
{
  path: '/pedidos/:id',
  name: 'visualizar-pedido',
  component: VisualizarPedidoView,
  meta: { 
    requiresAuth: true,
    title: 'Detalhes do Pedido'
  }
}
```

**Navigation Guard:**
```javascript
router.beforeEach(async (to, from, next) => {
  // Verificar autenticação
  await authStore.checkAuth()
  
  // Proteger rotas
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})
```

### Sistema de Comunicação com API

**Service Pattern:**
```javascript
// pedidoService.js
export const pedidoService = {
  /**
   * Lista todos os pedidos
   * @returns {Promise<Array>}
   */
  async listarTodos() {
    const response = await api.get('/api/pedidos')
    return response.data
  },
  
  /**
   * Busca pedido por ID
   * @param {number} id
   * @returns {Promise<Object>}
   */
  async buscarPorId(id) {
    const response = await api.get(`/api/pedidos/${id}`)
    return response.data
  }
}
```

**Interceptors Axios:**
```javascript
// Request interceptor - adiciona token
api.interceptors.request.use(config => {
  const token = authService.getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Response interceptor - trata erros
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      authService.logout()
      router.push('/login')
    }
    return Promise.reject(error)
  }
)
```

### Tratamento de Erros

**Frontend:**
```javascript
try {
  await pedidoService.criar(dados)
  toast.success('Pedido criado!')
} catch (error) {
  const mensagem = error.response?.data?.message || 'Erro ao criar pedido'
  toast.error(mensagem)
  logger.error('Erro:', error)
}
```

**Backend:**
```java
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(new ErrorResponse(ex.getMessage()));
  }
}
```

### Documentação de Código

**JSDoc (Frontend):**
```javascript
/**
 * @fileoverview Descrição do arquivo
 * @module path/to/module
 */

/**
 * Descrição da função
 * 
 * @function nomeDaFuncao
 * @param {type} param - Descrição
 * @returns {type} Descrição
 * 
 * @example
 * const result = nomeDaFuncao(value)
 */
```

**JavaDoc (Backend):**
```java
/**
 * Descrição da classe
 * 
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */
public class PedidoService {
  /**
   * Descrição do método
   * 
   * @param id ID do pedido
   * @return Pedido encontrado
   * @throws ResourceNotFoundException se pedido não existir
   */
  public Pedido buscarPorId(Long id) { }
}
```

## 🎯 Funcionalidades Principais

### ✅ Gestão de Rascunhos
- Criar rascunhos de pedidos sem compromisso
- Editar e gerenciar itens livremente
- Converter itens selecionados em pedidos oficiais
- Excluir rascunhos não mais necessários

### ✅ Gestão de Pedidos
- Criar e editar pedidos de compra
- Status: PENDENTE, EM_ANALISE, EM_ANDAMENTO, APROVADO, CANCELADO
- Gerenciamento individual de itens
- Histórico completo de modificações

### ✅ Gestão de Cotações
- Anexar cotações a múltiplos itens do mesmo pedido
- Upload de PDF com cotação
- Comparativo de preços por fornecedor
- Validação automática de itens

### ✅ Gestão de Fornecedores
- Cadastro de fornecedores pessoas físicas e jurídicas
- Vinculação a cotações
- Histórico de fornecimentos

### ✅ Histórico e Auditoria
- Rastreamento completo de modificações em pedidos
- Registro automático de quem/quando/o quê mudou
- Timeline visual de alterações
- Filtro por pedido ou usuário

### ✅ Relatórios
- Dashboard executivo
- Itens mais solicitados
- Comparativo de cotações
- Solicitações abertas
- Pedidos fechados

### ✅ Autenticação e Segurança
- Login com JWT
- Controle de acesso por roles (ADMIN, USER)
- Proteção de rotas no frontend e backend

## 📊 Endpoints da API

Acesse a documentação completa em: **http://localhost:8081/swagger-ui.html**

### Principais Endpoints

**Autenticação:**
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Registro

**Rascunhos:**
- `GET /api/rascunhos` - Listar todos
- `POST /api/rascunhos` - Criar novo
- `PUT /api/rascunhos/{id}` - Atualizar
- `DELETE /api/rascunhos/{id}` - Remover
- `POST /api/rascunhos/{id}/converter-para-pedido` - Converter em pedido

**Pedidos:**
- `GET /api/pedidos` - Listar todos
- `POST /api/pedidos` - Criar novo
- `PUT /api/pedidos/{id}` - Atualizar
- `DELETE /api/pedidos/{id}` - Remover

**Cotações:**
- `GET /api/cotacoes` - Listar todas
- `POST /api/cotacoes` - Criar nova
- `GET /api/cotacoes/pedido/{pedidoId}` - Por pedido

**Fornecedores:**
- `GET /api/fornecedores` - Listar todos
- `POST /api/fornecedores` - Criar novo

**Histórico:**
- `GET /api/historico-pedidos/pedido/{pedidoId}` - Histórico de um pedido
- `GET /api/historico-pedidos/usuario/{userId}` - Por usuário

**Relatórios:**
- `GET /api/relatorios/dashboard-executivo`
- `GET /api/relatorios/itens-mais-solicitados`
- `GET /api/relatorios/comparativo-cotacao/{pedidoId}`

## 🛠️ Desenvolvimento

### Pré-requisitos

- **Java 21** ou superior ([Download](https://www.oracle.com/java/technologies/downloads/))
- **Node.js 20+** e npm ([Download](https://nodejs.org/))
- **PostgreSQL 17** ([Download](https://www.postgresql.org/download/)) ou usar H2
- **Maven** (incluído via wrapper `mvnw`)
- **Git** ([Download](https://git-scm.com/))

### Instalação e Configuração

**1. Clone o repositório**
```bash
git clone https://github.com/Vini-Guedesz/gestor-compras.git
cd gestor-compras
```

**2. Configure o banco de dados PostgreSQL**

Crie o banco de dados:
```sql
CREATE DATABASE gestorcomprasbackend;
```

Edite `gestor-compras-backend/src/main/resources/application-dev.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestorcomprasbackend
spring.datasource.username=postgres
spring.datasource.password=sua-senha-aqui

# Flyway migrations
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
```

**3. Execute as migrations Flyway**
```bash
cd gestor-compras-backend
mvnw.cmd flyway:migrate
```

Isso criará automaticamente:
- Tabelas do sistema
- Relacionamentos e constraints
- Usuários de teste (admin@gestor.com, user@gestor.com)

**4. Instale as dependências do frontend**
```bash
cd gestor-compras-frontend/vue-project
npm install
```

**5. Execute a aplicação**

**Opção A - Scripts Automáticos (Windows):**
```bash
# Terminal 1 - Backend
start-backend.bat

# Terminal 2 - Frontend  
start-frontend.bat
```

**Opção B - Manual:**
```bash
# Terminal 1 - Backend
cd gestor-compras-backend
mvnw.cmd spring-boot:run

# Terminal 2 - Frontend
cd gestor-compras-frontend/vue-project
npm run dev
```

**6. Acesse a aplicação**
- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **H2 Console** (se ativo): http://localhost:8081/h2-console

### Estrutura de Ambientes

#### Desenvolvimento (dev)
- **Profile**: `spring.profiles.active=dev`
- **Banco**: PostgreSQL local
- **Porta Backend**: 8081
- **Porta Frontend**: 5173
- **Logs**: Detalhados (DEBUG)
- **CORS**: Liberado para localhost:5173

#### Produção (prod)
- **Profile**: `spring.profiles.active=prod`
- **Banco**: PostgreSQL produção
- **Build**: `npm run build` + `mvnw.cmd package`
- **Logs**: Otimizados (INFO/WARN/ERROR)
- **CORS**: Restrito ao domínio de produção

### Variáveis de Ambiente

**Backend** (`application.properties`):
```properties
# Perfil ativo
spring.profiles.active=dev

# JWT
jwt.secret=sua-chave-secreta-aqui-minimo-256-bits
jwt.expiration=86400000

# Upload de arquivos
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

**Frontend** (`.env`):
```env
# URL da API
VITE_API_URL=http://localhost:8081

# Debug
VITE_ENABLE_DEBUG=true
```

### Comandos Úteis

**Backend (Maven):**
```bash
cd gestor-compras-backend

# Compilar
mvnw.cmd clean compile

# Executar aplicação
mvnw.cmd spring-boot:run

# Executar testes
mvnw.cmd test

# Package (gera JAR)
mvnw.cmd clean package

# Pular testes
mvnw.cmd package -DskipTests

# Limpar build
mvnw.cmd clean

# Executar migrations
mvnw.cmd flyway:migrate

# Reverter migration
mvnw.cmd flyway:undo

# Info do Flyway
mvnw.cmd flyway:info
```

**Frontend (NPM):**
```bash
cd gestor-compras-frontend/vue-project

# Instalar dependências
npm install

# Desenvolvimento (hot reload)
npm run dev

# Build para produção
npm run build

# Preview do build
npm run preview

# Lint (verificar código)
npm run lint

# Lint com auto-fix
npm run lint -- --fix

# Limpar node_modules
rm -rf node_modules package-lock.json
npm install
```

### Testes

**Backend - JUnit:**
```bash
# Todos os testes
mvnw.cmd test

# Teste específico
mvnw.cmd test -Dtest=PedidoServiceTest

# Com cobertura
mvnw.cmd test jacoco:report
```

**Frontend - Vitest:**
```bash
# Executar testes (quando configurados)
npm run test

# Testes em watch mode
npm run test:watch

# Cobertura
npm run test:coverage
```

### Debugging

**Backend - IntelliJ IDEA:**
1. Abrir projeto `gestor-compras-backend`
2. Criar configuração "Spring Boot"
3. Main class: `GestorDeComprasApplication`
4. Active profiles: `dev`
5. F5 para debug

**Backend - VS Code:**
1. Instalar extensão "Debugger for Java"
2. Abrir `gestor-compras-backend`
3. F5 → "Java" → "Spring Boot App"

**Frontend - Chrome DevTools:**
1. F12 no navegador
2. Sources → Abrir arquivo `.vue`
3. Adicionar breakpoints
4. Vue DevTools extension recomendada

### Build para Produção

**Backend:**
```bash
cd gestor-compras-backend
mvnw.cmd clean package -DskipTests

# JAR gerado em:
# target/gestor-compras-backend-1.0.0.jar

# Executar JAR:
java -jar target/gestor-compras-backend-1.0.0.jar --spring.profiles.active=prod
```

**Frontend:**
```bash
cd gestor-compras-frontend/vue-project
npm run build

# Build gerado em:
# dist/

# Servir com servidor estático:
npm install -g serve
serve -s dist -p 5173
```

### Migrations Flyway

**Criar nova migration:**

1. Criar arquivo em `src/main/resources/db/migration/`
2. Nomenclatura: `V{version}__{description}.sql`
   - Exemplo: `V5__add_campo_observacoes_pedidos.sql`

```sql
-- V5__add_campo_observacoes_pedidos.sql
ALTER TABLE tb_pedidos 
ADD COLUMN observacoes TEXT;
```

3. Executar:
```bash
mvnw.cmd flyway:migrate
```

**Verificar status:**
```bash
mvnw.cmd flyway:info
```

**Rollback (cuidado em produção):**
```bash
# Flyway Community não suporta undo automático
# Criar migration reversa manual
```

## 🐛 Resolução de Problemas

### Erros Comuns e Soluções

#### 1. Porta Já Está em Uso

**Problema:** `Port 8081 is already in use` ou `Port 5173 is already in use`

**Verificar processos:**
```cmd
# Backend (porta 8081)
netstat -ano | findstr :8081

# Frontend (porta 5173)
netstat -ano | findstr :5173
```

**Matar processo:**
```cmd
taskkill /PID <numero-do-pid> /F
```

**Alternativa - Mudar porta:**

Backend (`application-dev.properties`):
```properties
server.port=8082
```

Frontend (`vite.config.js`):
```javascript
export default defineConfig({
  server: {
    port: 5174
  }
})
```

#### 2. Erro de Conexão com Banco de Dados

**Problema:** `Connection refused` ou `Authentication failed`

**Soluções:**

1. **Verificar se PostgreSQL está rodando:**
```cmd
# Windows
services.msc → PostgreSQL

# Ou via comando
psql -U postgres -d gestorcomprasbackend
```

2. **Verificar credenciais:**
```properties
# application-dev.properties
spring.datasource.username=postgres
spring.datasource.password=sua-senha-correta
```

3. **Criar banco se não existir:**
```sql
CREATE DATABASE gestorcomprasbackend;
```

4. **Testar conexão:**
```cmd
psql -U postgres -h localhost -p 5432 -d gestorcomprasbackend
```

**Alternativa - Usar H2 (desenvolvimento):**

`application.properties`:
```properties
spring.profiles.active=h2
```

Acesse: http://localhost:8081/h2-console

#### 3. Migrations Flyway Falhando

**Problema:** `Migration checksum mismatch` ou `Duplicate migration`

**Soluções:**

1. **Limpar histórico Flyway (CUIDADO - desenvolvimento apenas):**
```bash
mvnw.cmd flyway:clean
mvnw.cmd flyway:migrate
```

2. **Reparar checksums:**
```bash
mvnw.cmd flyway:repair
mvnw.cmd flyway:migrate
```

3. **Verificar status:**
```bash
mvnw.cmd flyway:info
```

#### 4. Erro 401 Unauthorized no Frontend

**Problema:** Requisições retornam 401 mesmo após login

**Soluções:**

1. **Verificar token no sessionStorage:**
```javascript
// Console do navegador (F12)
console.log(sessionStorage.getItem('token'))
```

2. **Verificar interceptor Axios:**
```javascript
// src/services/api.js
console.log('Token:', authService.getToken())
```

3. **Verificar expiração do token:**
```properties
# application.properties - aumentar validade
jwt.expiration=86400000  # 24 horas em ms
```

4. **Limpar sessão e fazer novo login:**
```javascript
sessionStorage.clear()
// Fazer login novamente
```

#### 5. CORS Error

**Problema:** `Access to XMLHttpRequest blocked by CORS policy`

**Solução:**

Backend (`SecurityConfig.java`):
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

Verificar `application-dev.properties`:
```properties
# URL do frontend
cors.allowed.origins=http://localhost:5173
```

#### 6. Erro no Frontend: `node_modules` Corrompidos

**Problema:** Erros estranhos ao executar `npm run dev`

**Solução:**
```bash
cd gestor-compras-frontend/vue-project

# Remover node_modules e lock
rm -rf node_modules package-lock.json

# Limpar cache npm
npm cache clean --force

# Reinstalar
npm install
```

#### 7. Java não Encontrado

**Problema:** `'java' is not recognized as an internal or external command`

**Soluções:**

1. **Verificar instalação:**
```cmd
java -version
javac -version
```

2. **Instalar Java 21:**
   - Download: https://www.oracle.com/java/technologies/downloads/
   - Ou usar OpenJDK: https://adoptium.net/

3. **Configurar PATH:**
   - Painel de Controle → Sistema → Variáveis de Ambiente
   - PATH → Adicionar: `C:\Program Files\Java\jdk-21\bin`

#### 8. Node.js não Encontrado

**Problema:** `'node' is not recognized` ou `'npm' is not recognized`

**Soluções:**

1. **Verificar instalação:**
```cmd
node -v
npm -v
```

2. **Instalar Node.js:**
   - Download: https://nodejs.org/ (versão LTS)
   - Inclui npm automaticamente

3. **Verificar PATH:**
```cmd
where node
where npm
```

#### 9. Erro de Upload de Arquivo (413 Payload Too Large)

**Problema:** Upload de PDF de cotação falha

**Solução:**

Backend (`application.properties`):
```properties
# Aumentar limite de upload
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB
```

#### 10. Hot Reload não Funciona (Vite)

**Problema:** Alterações no código não refletem automaticamente

**Soluções:**

1. **Reiniciar servidor:**
```bash
# Ctrl+C para parar
npm run dev
```

2. **Verificar portas:**
```bash
# Limpar cache Vite
rm -rf node_modules/.vite
npm run dev
```

3. **Verificar firewall/antivírus** (pode bloquear HMR)

#### 11. Erro de Build no Backend (Maven)

**Problema:** `Failed to execute goal org.apache.maven.plugins`

**Soluções:**

1. **Limpar repositório Maven:**
```bash
mvnw.cmd clean
rm -rf ~/.m2/repository
mvnw.cmd install
```

2. **Atualizar wrapper:**
```bash
mvnw.cmd wrapper:wrapper
```

3. **Verificar Java version:**
```bash
java -version  # Deve ser 21+
```

#### 12. Erro de Encoding UTF-8

**Problema:** Caracteres especiais aparecem como `???` ou `â€™`

**Solução:**

Backend (`application.properties`):
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestorcomprasbackend?useUnicode=true&characterEncoding=UTF-8
spring.jpa.properties.hibernate.jdbc.charset=UTF-8
```

IDE (IntelliJ/VS Code):
- File → Settings → Editor → File Encodings → UTF-8

### Logs e Debugging

**Ver logs do Backend:**
```bash
# Console onde mvnw.cmd spring-boot:run está rodando
# Ou verificar arquivo de log (se configurado):
tail -f logs/application.log
```

**Ver logs do Frontend:**
```bash
# Console do navegador (F12) → Console
# Ou terminal onde npm run dev está rodando
```

**Aumentar nível de log (debugging):**

Backend (`application-dev.properties`):
```properties
# Nível DEBUG para pacote específico
logging.level.com.gestordecompras=DEBUG

# SQL queries
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

**Verificar health da aplicação:**
```bash
# Backend health check
curl http://localhost:8081/actuator/health

# Ou no navegador
http://localhost:8081/actuator/health
```

### Suporte Adicional

**Recursos úteis:**
- 📖 **Swagger UI**: http://localhost:8081/swagger-ui.html - Documentação completa da API
- 🗄️ **H2 Console**: http://localhost:8081/h2-console - Console de banco de dados
- 📚 **Vue Docs**: https://vuejs.org/ - Documentação oficial do Vue 3
- 🍃 **Spring Docs**: https://spring.io/guides - Guias do Spring Boot

**Checklist de debug:**
1. ✅ Backend está rodando? (http://localhost:8081/actuator/health)
2. ✅ Frontend está rodando? (http://localhost:5173)
3. ✅ Banco de dados está acessível?
4. ✅ Token JWT está sendo enviado? (Network tab → Headers)
5. ✅ CORS configurado corretamente?
6. ✅ Portas corretas configuradas?
7. ✅ Variáveis de ambiente corretas?
8. ✅ Console do navegador sem erros? (F12)

**Logs importantes para reportar problemas:**
```bash
# Backend
mvnw.cmd spring-boot:run > backend.log 2>&1

# Frontend
npm run dev > frontend.log 2>&1
```

## 📈 Status do Projeto

- ✅ Autenticação JWT
- ✅ CRUD completo de usuários
- ✅ Gestão de rascunhos
- ✅ Gestão de pedidos com histórico
- ✅ Gestão de cotações (múltiplos itens)
- ✅ Gestão de fornecedores
- ✅ Histórico de auditoria
- ✅ Relatórios JasperReports
- ✅ Interface responsiva
- ✅ Migrations Flyway
- ✅ Documentação Swagger

## 📚 Documentação Adicional

### Documentação de Código

#### Frontend (JSDoc Completo)

**Services Layer** - 10 arquivos documentados:
- `authService.js` - Autenticação e gerenciamento de tokens JWT
- `api.js` - Cliente Axios com interceptors
- `pedidoService.js` - CRUD de pedidos
- `cotacaoService.js` - Gestão de cotações
- `fornecedorService.js` - Gestão de fornecedores
- `relatorioService.js` - Geração de relatórios PDF
- `rascunhoService.js` - Gestão de rascunhos
- `historicoPedidoService.js` - Histórico de auditoria
- `itemPedidoService.js` - CRUD de itens
- `cotacaoRascunhoService.js` - Cotações de rascunhos

**Composables** - 4 arquivos documentados:
- `useDebounce.js` - Debounce de valores reativos e callbacks
- `useModal.js` - Controle de scroll em modais
- `useToast.js` - Sistema global de notificações
- `useMobileSidebar.js` - Estado do menu mobile

**Utils** - 3 arquivos documentados:
- `pedidoUtils.js` - Formatação, validações, status
- `genderUtils.js` - Personalização por gênero
- `logger.js` - Sistema de logging condicional

**Stores (Pinia)** - 1 arquivo documentado:
- `auth.js` - Estado global de autenticação

**Router** - 1 arquivo documentado:
- `index.js` - Configuração de rotas e guards

**Total: 19 arquivos essenciais com JSDoc completo**

Cada arquivo possui:
- `@fileoverview` detalhado com descrição e exemplos
- `@typedef` para tipos de dados complexos
- `@function/@param/@returns` em todas as funções
- `@example` com casos de uso reais
- Comentários inline explicando lógica

#### Backend (JavaDoc)

Documentação gerada automaticamente via JavaDoc:

```bash
cd gestor-compras-backend
mvnw.cmd javadoc:javadoc

# Documentação gerada em:
# target/site/apidocs/index.html
```

### API REST - Swagger/OpenAPI

**Acesse a documentação interativa:**
http://localhost:8081/swagger-ui.html

**Recursos:**
- Teste todos os endpoints diretamente pelo navegador
- Visualize schemas de request/response
- Autenticação JWT integrada
- Exemplos de uso para cada endpoint

**Endpoints principais:**
- 🔐 `/api/auth/*` - Autenticação
- 📝 `/api/rascunhos/*` - Rascunhos
- 📦 `/api/pedidos/*` - Pedidos
- 💰 `/api/cotacoes/*` - Cotações
- 🏢 `/api/fornecedores/*` - Fornecedores
- 📊 `/api/relatorios/*` - Relatórios
- 📜 `/api/historico-pedidos/*` - Histórico

### Arquitetura Frontend

Para documentação detalhada da arquitetura Vue.js, consulte:
- **Este README** - Seção "🏗️ Arquitetura do Sistema"

### Banco de Dados

#### Schema do Banco

**Tabelas principais:**
```
tb_usuarios          - Usuários do sistema
tb_rascunhos         - Rascunhos de pedidos
tb_itens_rascunho    - Itens dos rascunhos
tb_pedidos           - Pedidos oficiais
tb_itens_pedido      - Itens dos pedidos
tb_fornecedores      - Fornecedores
tb_cotacoes          - Cotações
tb_historico_pedidos - Auditoria de alterações
flyway_schema_history - Controle de migrations
```

**Visualizar schema:**
```sql
-- PostgreSQL
\dt

-- Ou via SQL
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public';
```

**Diagrama ER:**
Ver arquivo: `docs/database-schema.md` (se existir)

#### Migrations Flyway

Localização: `src/main/resources/db/migration/`

```
V1__create_initial_schema.sql    - Schema inicial
V2__insert_test_users.sql        - Usuários de teste
V3__add_fornecedores.sql         - Tabela fornecedores
V4__add_historico_pedidos.sql    - Tabela de auditoria
```

**Verificar migrations aplicadas:**
```bash
mvnw.cmd flyway:info
```

### Guias de Desenvolvimento

#### Criar Novo Endpoint REST

**1. Criar Entity (Model):**
```java
@Entity
@Table(name = "tb_nova_entidade")
public class NovaEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nome;
    // getters, setters, construtores
}
```

**2. Criar Repository:**
```java
@Repository
public interface NovaEntidadeRepository extends JpaRepository<NovaEntidade, Long> {
    List<NovaEntidade> findByNomeContaining(String nome);
}
```

**3. Criar DTO:**
```java
public class NovaEntidadeDTO {
    @NotNull
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String nome;
    // getters, setters
}
```

**4. Criar Mapper:**
```java
@Mapper(componentModel = "spring")
public interface NovaEntidadeMapper {
    NovaEntidadeDTO toDTO(NovaEntidade entity);
    NovaEntidade toEntity(NovaEntidadeDTO dto);
}
```

**5. Criar Service:**
```java
@Service
@Transactional
public class NovaEntidadeService {
    @Autowired
    private NovaEntidadeRepository repository;
    
    @Autowired
    private NovaEntidadeMapper mapper;
    
    public List<NovaEntidadeDTO> listarTodos() {
        return repository.findAll().stream()
            .map(mapper::toDTO)
            .collect(Collectors.toList());
    }
}
```

**6. Criar Controller:**
```java
@RestController
@RequestMapping("/api/nova-entidade")
public class NovaEntidadeController {
    @Autowired
    private NovaEntidadeService service;
    
    @GetMapping
    public ResponseEntity<List<NovaEntidadeDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }
}
```

**7. Testar via Swagger:**
http://localhost:8081/swagger-ui.html

#### Criar Nova View (Frontend)

**1. Criar Service:**
```javascript
// src/services/novaEntidadeService.js
import api from './api'

export const novaEntidadeService = {
  async listarTodos() {
    const response = await api.get('/api/nova-entidade')
    return response.data
  }
}
```

**2. Criar View:**
```vue
<!-- src/views/NovaEntidadeView.vue -->
<script setup>
import { ref, onMounted } from 'vue'
import { novaEntidadeService } from '@/services/novaEntidadeService'

const items = ref([])

const carregarItens = async () => {
  try {
    items.value = await novaEntidadeService.listarTodos()
  } catch (error) {
    console.error('Erro:', error)
  }
}

onMounted(() => {
  carregarItens()
})
</script>

<template>
  <div class="nova-entidade-view">
    <h1>Nova Entidade</h1>
    <ul>
      <li v-for="item in items" :key="item.id">
        {{ item.nome }}
      </li>
    </ul>
  </div>
</template>
```

**3. Adicionar Rota:**
```javascript
// src/router/index.js
{
  path: '/nova-entidade',
  name: 'nova-entidade',
  component: () => import('../views/NovaEntidadeView.vue'),
  meta: { requiresAuth: true }
}
```

**4. Adicionar Link no Menu:**
```vue
<router-link to="/nova-entidade">Nova Entidade</router-link>
```

#### Adicionar Nova Migration

**1. Criar arquivo:**
```bash
# Nomenclatura: V{version}__{description}.sql
# Exemplo: V5__add_campo_status.sql
```

**2. Escrever SQL:**
```sql
-- V5__add_campo_status.sql
ALTER TABLE tb_pedidos 
ADD COLUMN status_detalhado VARCHAR(50);

UPDATE tb_pedidos 
SET status_detalhado = 'ATIVO' 
WHERE status = 'APROVADO';
```

**3. Executar:**
```bash
mvnw.cmd flyway:migrate
```

**4. Verificar:**
```bash
mvnw.cmd flyway:info
```

### Padrões e Boas Práticas

#### Estrutura de Commits

```
tipo(escopo): descrição curta

Descrição detalhada opcional do que foi mudado e por quê.

Refs: #123
```

**Tipos:**
- `feat`: Nova funcionalidade
- `fix`: Correção de bug
- `docs`: Alteração em documentação
- `style`: Formatação, espaçamento
- `refactor`: Refatoração de código
- `test`: Testes
- `chore`: Builds, configs

**Exemplos:**
```
feat(pedidos): adicionar filtro por data de criação
fix(cotacao): corrigir validação de upload de PDF
docs(readme): atualizar seção de instalação
refactor(services): extrair lógica comum para utils
test(pedido): adicionar testes unitários para criação
chore(deps): atualizar Vue para 3.5.18
```

#### Code Review Checklist

✅ Código segue padrões do projeto
✅ JSDoc/JavaDoc completo
✅ Testes unitários passam
✅ Sem console.log() esquecidos
✅ Variáveis e funções com nomes descritivos
✅ Tratamento de erros adequado
✅ CORS configurado (se novo endpoint)
✅ Migrations testadas (se aplicável)
✅ Sem hardcoded secrets (tokens, senhas)
✅ Documentação atualizada

#### Performance Tips

**Frontend:**
- Use `v-show` para toggles frequentes, `v-if` para condicionais raras
- Lazy load de componentes pesados
- Debounce em campos de busca
- Pagination em listas grandes
- Otimizar imagens (WebP, compressão)

**Backend:**
- Use `@Transactional` apropriadamente
- Indexes no banco para queries frequentes
- DTO projection para evitar lazy loading
- Cache com `@Cacheable` (quando aplicável)
- Pagination com `Pageable`

### Recursos Externos

**Documentação Oficial:**
- 📖 [Vue 3 Docs](https://vuejs.org/)
- 📖 [Pinia Docs](https://pinia.vuejs.org/)
- 📖 [Vue Router Docs](https://router.vuejs.org/)
- 📖 [Vite Docs](https://vitejs.dev/)
- 📖 [Tailwind CSS Docs](https://tailwindcss.com/)
- 📖 [Spring Boot Docs](https://spring.io/projects/spring-boot)
- 📖 [Spring Security Docs](https://spring.io/projects/spring-security)
- 📖 [JPA/Hibernate Docs](https://hibernate.org/orm/documentation/)
- 📖 [Flyway Docs](https://flywaydb.org/documentation/)

**Ferramentas Úteis:**
- 🔧 [Vue DevTools](https://devtools.vuejs.org/) - Extension para debug
- 🔧 [Postman](https://www.postman.com/) - Teste de APIs
- 🔧 [DBeaver](https://dbeaver.io/) - Cliente de banco de dados
- 🔧 [IntelliJ IDEA](https://www.jetbrains.com/idea/) - IDE Java
- 🔧 [VS Code](https://code.visualstudio.com/) - Editor para Vue

**Tutoriais:**
- 🎓 [Vue Mastery](https://www.vuemastery.com/)
- 🎓 [Spring Academy](https://spring.academy/)
- 🎓 [Baeldung Spring](https://www.baeldung.com/spring-tutorial)

## 📞 Suporte

### Obtendo Ajuda

**1. Consulte a documentação:**
- 📖 Este README (arquitetura, instalação, troubleshooting)
- 📖 Swagger API: http://localhost:8081/swagger-ui.html
- 📖 JSDoc dos services (comentários inline no código)

**2. Verifique os logs:**
```bash
# Backend - terminal onde mvnw.cmd está rodando
# Frontend - Console do navegador (F12)

# Aumentar nível de log:
# application-dev.properties
logging.level.com.gestordecompras=DEBUG
```

**3. Debugging passo a passo:**
- Backend: IntelliJ IDEA ou VS Code com breakpoints
- Frontend: Chrome DevTools (F12) → Sources → Adicionar breakpoints

**4. Verifique issues conhecidas:**
- Ver seção "🐛 Resolução de Problemas" deste README
- Verificar erros comuns e suas soluções

### Reportando Problemas

**Ao reportar um bug, inclua:**

✅ Descrição clara do problema
✅ Passos para reproduzir
✅ Comportamento esperado vs obtido
✅ Logs relevantes (backend + frontend console)
✅ Ambiente:
  - SO (Windows/Linux/Mac)
  - Versão Java (`java -version`)
  - Versão Node (`node -v`)
  - Browser e versão
  - PostgreSQL ou H2

**Exemplo de report:**
```
Título: Erro 500 ao criar pedido com item sem quantidade

Passos:
1. Login como admin
2. Ir para /pedidos/novo
3. Adicionar item sem preencher quantidade
4. Clicar em "Salvar"

Esperado: Validação mostrando "Quantidade obrigatória"
Obtido: Erro 500 Internal Server Error

Logs Backend:
NullPointerException at PedidoService.criarPedido:123

Ambiente:
- Windows 11
- Java 21.0.1
- Node 20.10.0
- Chrome 120
- PostgreSQL 17
```

### Contribuindo

**Para contribuir com o projeto:**

1. **Fork o repositório**
2. **Clone seu fork:**
```bash
git clone https://github.com/seu-usuario/gestor-compras.git
cd gestor-compras
git remote add upstream https://github.com/Vini-Guedesz/gestor-compras.git
```

3. **Crie uma branch para sua feature:**
```bash
git checkout -b feat/minha-feature
```

4. **Faça suas alterações seguindo os padrões:**
   - JSDoc completo para novos métodos (frontend)
   - JavaDoc para novos métodos (backend)
   - Testes unitários (quando aplicável)
   - Mensagens de commit claras

5. **Commit suas mudanças:**
```bash
git add .
git commit -m "feat(pedidos): adicionar validação de quantidade mínima"
```

6. **Push para seu fork:**
```bash
git push origin feat/minha-feature
```

7. **Abra um Pull Request:**
   - Descreva suas mudanças
   - Referencie issues relacionadas
   - Aguarde review

**Boas práticas para PRs:**
- ✅ Um PR = Uma funcionalidade
- ✅ Código testado localmente
- ✅ Sem conflitos com branch principal
- ✅ Documentação atualizada
- ✅ Commits semânticos

### FAQ - Perguntas Frequentes

**Q: Como alterar a porta do backend?**
```properties
# application-dev.properties
server.port=8082
```

**Q: Como usar H2 ao invés de PostgreSQL?**
```properties
# application.properties
spring.profiles.active=h2
```

**Q: Como aumentar o timeout de requisições?**
```javascript
// src/services/api.js
const api = axios.create({
  timeout: 30000 // 30 segundos
})
```

**Q: Como adicionar novo usuário de teste?**
```sql
-- Criar migration: V6__add_new_test_user.sql
INSERT INTO tb_usuarios (username, email, password, role) 
VALUES ('Novo User', 'novo@gestor.com', '$2a$10$...', 'USER');
```

**Q: Como desabilitar autenticação (dev apenas)?**
```java
// SecurityConfig.java - NÃO RECOMENDADO EM PRODUÇÃO
http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
```

**Q: Como fazer deploy em produção?**
```bash
# Backend
mvnw.cmd clean package -DskipTests
java -jar target/gestor-compras-backend-1.0.0.jar --spring.profiles.active=prod

# Frontend
npm run build
# Servir pasta dist/ com nginx, apache ou CDN
```

**Q: Posso usar MySQL ao invés de PostgreSQL?**

Sim, altere:
1. Dependência em `pom.xml`:
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

2. URL em `application-dev.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gestorcompras
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

### Contato

**Mantenedor:** Vini Guedes  
**Repositório:** [github.com/Vini-Guedesz/gestor-compras](https://github.com/Vini-Guedesz/gestor-compras)  
**Licença:** MIT (ou conforme especificado no projeto)

### Roadmap

**Funcionalidades Planejadas:**
- [ ] Dashboard com gráficos interativos (Chart.js)
- [ ] Notificações em tempo real (WebSocket)
- [ ] Exportação de relatórios Excel
- [ ] Upload de múltiplos arquivos
- [ ] Sistema de aprovação multinível
- [ ] Integração com e-mail (notificações)
- [ ] Modo escuro (dark theme)
- [ ] PWA (Progressive Web App)
- [ ] Testes E2E (Cypress/Playwright)
- [ ] CI/CD com GitHub Actions

**Melhorias Técnicas:**
- [ ] Cobertura de testes >80%
- [ ] Docker Compose para desenvolvimento
- [ ] Kubernetes deployment
- [ ] Redis para cache
- [ ] Elasticsearch para busca avançada
- [ ] Monitoramento com Prometheus/Grafana

---

**Versão**: 3.0.0  
**Última atualização**: Dezembro 2025  
**Status**: ✅ Em Produção  
**Documentação**: ✅ Completa (19 arquivos JSDoc + README técnico detalhado)
