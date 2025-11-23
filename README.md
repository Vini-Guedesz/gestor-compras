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
- **Vue.js** 3.5.18
- **Vite** (build tool)
- **Vue Router** 4.5.1
- **Pinia** 3.0.3 (gerenciamento de estado)
- **Axios** 1.11.0 (cliente HTTP)
- **Tailwind CSS** (estilização)

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

- **Java 21** ou superior
- **Node.js 20+** e npm
- **PostgreSQL 17** (ou usar H2)
- **Maven** (incluído via wrapper)

### Instalação

**1. Clone o repositório**
```bash
git clone <url-do-repositorio>
cd gestor-compras
```

**2. Configure o banco de dados**

Edite `gestor-compras-backend/src/main/resources/application-dev.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestorcomprasbackend
spring.datasource.username=postgres
spring.datasource.password=sua-senha
```

**3. Execute as migrations**
```bash
cd gestor-compras-backend
mvnw.cmd flyway:migrate
```

**4. Instale as dependências do frontend**
```bash
cd gestor-compras-frontend/vue-project
npm install
```

**5. Execute a aplicação**

Terminal 1 (Backend):
```bash
start-backend.bat
```

Terminal 2 (Frontend):
```bash
start-frontend.bat
```

### Comandos Úteis

**Backend:**
```bash
# Compilar
cd gestor-compras-backend
mvnw.cmd clean compile

# Executar testes
mvnw.cmd test

# Limpar build
mvnw.cmd clean

# Executar migrations
mvnw.cmd flyway:migrate
```

**Frontend:**
```bash
cd gestor-compras-frontend/vue-project

# Instalar dependências
npm install

# Desenvolvimento
npm run dev

# Build para produção
npm run build

# Preview do build
npm run preview

# Lint
npm run lint
```

## 🐛 Resolução de Problemas

### Porta ocupada

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

### Erro de conexão com banco

1. Verifique se o PostgreSQL está rodando
2. Confirme as credenciais em `application-dev.properties`
3. Teste a conexão: `psql -U postgres -d gestorcomprasbackend`

Alternativamente, use H2 em memória alterando o profile para `h2`.

### Erro no frontend (node_modules)

```bash
cd gestor-compras-frontend/vue-project
rm -rf node_modules package-lock.json
npm install
```

### Java não encontrado

Verifique a instalação:
```cmd
java -version
```

Baixe o Java 21: https://www.oracle.com/java/technologies/downloads/

### Node.js não encontrado

Verifique a instalação:
```cmd
node -v
npm -v
```

Baixe o Node.js: https://nodejs.org/

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

- **API Swagger**: http://localhost:8081/swagger-ui.html
- **Arquitetura Frontend**: [gestor-compras-frontend/vue-project/ARQUITETURA.md](./gestor-compras-frontend/vue-project/ARQUITETURA.md)

## 📞 Suporte

Para dúvidas ou problemas:
1. Consulte a documentação Swagger
2. Verifique os logs do backend no terminal
3. Verifique o console do navegador (F12) para erros do frontend

---

**Versão**: 2.0.0
**Última atualização**: Novembro 2025
**Status**: ✅ Em Produção
