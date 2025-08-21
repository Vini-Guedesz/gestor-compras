# Frontend - Gestor de Compras

## 📋 Visão Geral

Este é o frontend da aplicação **Gestor de Compras**, desenvolvido em **Vue.js 3** com **Vite**. A aplicação oferece uma interface moderna e responsiva para gerenciamento de compras e fornecedores.

## 🚀 Tecnologias Utilizadas

- **Vue.js 3** - Framework JavaScript progressivo
- **Vite** - Build tool e dev server rápido
- **Pinia** - Gerenciamento de estado global
- **Vue Router** - Roteamento SPA
- **Axios** - Cliente HTTP
- **Composition API** - API moderna do Vue 3

## 📁 Estrutura do Projeto

```
src/
├── components/          # Componentes reutilizáveis
│   ├── AppLayout.vue       # Layout principal da aplicação
│   ├── DashboardHeader.vue # Cabeçalho do dashboard
│   ├── DashboardSidebar.vue # Menu lateral
│   ├── LoadingSpinner.vue  # Componente de loading
│   ├── MetricCard.vue      # Card de métricas
│   └── QuickActions.vue    # Ações rápidas
├── router/              # Configuração de rotas
│   └── index.js            # Definição das rotas e proteções
├── services/            # Serviços de API
│   ├── api.js              # Cliente HTTP configurado
│   └── authService.js      # Serviços de autenticação
├── stores/              # Gerenciamento de estado (Pinia)
│   ├── auth.js             # Store de autenticação
│   └── counter.js          # Store exemplo
├── views/               # Páginas/Views da aplicação
│   ├── DashboardView.vue   # Página principal (dashboard)
│   ├── FornecedoresView.vue # Gestão de fornecedores
│   └── LoginView.vue       # Página de login
├── App.vue              # Componente raiz
└── main.js              # Ponto de entrada da aplicação
```

## 🔐 Sistema de Autenticação

### Fluxo de Autenticação

1. **Login**: Usuário informa email e senha
2. **Validação**: Credenciais são enviadas para o backend
3. **Token JWT**: Backend retorna token de autenticação
4. **Persistência**: Token é salvo no localStorage
5. **Proteção de Rotas**: Router verifica autenticação antes da navegação

### Usuários de Teste

Para testar a aplicação, use as seguintes credenciais:

**Administrador:**
- Email: `admin@gestor.com`
- Senha: `admin123`

**Usuário Comum:**
- Email: `user@gestor.com`
- Senha: `user123`

## 🛣️ Sistema de Rotas

### Rotas Disponíveis

| Rota | Componente | Proteção | Descrição |
|------|------------|----------|-----------|
| `/` | - | - | Redireciona para `/login` |
| `/login` | LoginView | Apenas visitantes | Página de autenticação |
| `/dashboard` | DashboardView | Requer autenticação | Página principal |
| `/fornecedores` | FornecedoresView | Requer autenticação | Gestão de fornecedores |

### Proteção de Rotas

- **requiresAuth**: Rotas que exigem autenticação
- **requiresGuest**: Rotas apenas para usuários não autenticados
- **Redirecionamentos automáticos**: Usuários são redirecionados conforme seu status de autenticação

## 🏪 Gerenciamento de Estado (Pinia)

### Auth Store (`stores/auth.js`)

Gerencia o estado global de autenticação:

**Estados:**
- `isAuthenticated`: Boolean indicando se usuário está logado
- `user`: Objeto com dados do usuário
- `token`: Token JWT de autenticação

**Ações:**
- `login(email, password)`: Realiza login
- `logout()`: Realiza logout e limpa dados
- `checkAuth()`: Verifica se há sessão ativa salva
- `forgotPassword(email)`: Solicita recuperação de senha

## 🌐 Comunicação com API

### Configuração (`services/api.js`)

- **URL Base**: Configurável via `VITE_API_BASE_URL` (padrão: `http://localhost:8080`)
- **Timeout**: 10 segundos para requisições
- **Headers**: Automaticamente adiciona `Content-Type: application/json`
- **Autenticação**: Adiciona automaticamente `Authorization: Bearer {token}`

### Interceptadores

**Request Interceptor:**
- Adiciona token de autenticação automaticamente

**Response Interceptor:**
- Trata erro 401 (token expirado) com logout automático
- Padroniza tratamento de erros
- Redireciona para login em caso de sessão expirada

## 🎨 Componentização

### Componentes Principais

#### `LoadingSpinner.vue`
- Exibe indicador de carregamento
- Props: `show` (boolean), `message` (string)

#### `AppLayout.vue`
- Layout base para páginas autenticadas
- Inclui header e sidebar

#### `MetricCard.vue`
- Exibe métricas em formato de card
- Reutilizável para diferentes tipos de dados

### Views (Páginas)

#### `LoginView.vue`
- Formulário de autenticação
- Validação de campos
- Estados de loading e erro
- Funcionalidade "Esqueci minha senha"

#### `DashboardView.vue`
- Página principal após login
- Exibe métricas e ações rápidas
- Layout responsivo

#### `FornecedoresView.vue`
- Gestão de fornecedores
- CRUD de fornecedores (implementação futura)

## 🔧 Configuração de Desenvolvimento

### Variáveis de Ambiente

Crie arquivos `.env` para diferentes ambientes:

**`.env.development`:**
```env
VITE_API_BASE_URL=http://localhost:8080
```

**`.env.production`:**
```env
VITE_API_BASE_URL=https://api.seu-dominio.com
```

### Scripts Disponíveis

```bash
# Instalar dependências
npm install

# Servidor de desenvolvimento
npm run dev

# Build para produção
npm run build

# Preview do build
npm run preview

# Executar testes
npm run test

# Linting
npm run lint
```

## 📱 Responsividade

A aplicação foi desenvolvida com design responsivo:

- **Desktop**: Layout completo com sidebar
- **Tablet**: Layout adaptado
- **Mobile**: Menu colapsável e layout otimizado

## 🔒 Segurança

### Práticas Implementadas

1. **Tokens JWT**: Autenticação segura
2. **Proteção de Rotas**: Verificação antes da navegação
3. **Logout Automático**: Em caso de token expirado
4. **Validação de Entrada**: Nos formulários
5. **Headers Seguros**: Configuração adequada das requisições

## 🚀 Deploy

### Build para Produção

```bash
npm run build
```

O build gera os arquivos otimizados na pasta `dist/`.

### Configurações Importantes

1. Configure `VITE_API_BASE_URL` para o ambiente de produção
2. Configure CORS no backend para aceitar requests do domínio frontend
3. Configure redirecionamentos para SPA no servidor web

## 🐛 Tratamento de Erros

### Níveis de Erro

1. **Erros de Rede**: Conexão com backend indisponível
2. **Erros HTTP**: 4xx, 5xx do backend
3. **Erros de Validação**: Campos obrigatórios, formatos inválidos
4. **Erros de Autenticação**: Token inválido/expirado

### Estratégias de Tratamento

- **Interceptadores globais**: Para erros HTTP
- **Try/catch**: Para operações assíncronas
- **Mensagens amigáveis**: Para feedback do usuário
- **Fallbacks**: Para quando serviços estão indisponíveis

## 📚 Convenções do Código

### Nomenclatura

- **Componentes**: PascalCase (`LoginView.vue`)
- **Stores**: camelCase (`useAuthStore`)
- **Métodos**: camelCase (`handleLogin`)
- **Constantes**: UPPER_SNAKE_CASE (`API_BASE_URL`)

### Estrutura de Componentes

```vue
<!-- Template -->
<template>
  <!-- HTML com comentários explicativos -->
</template>

<!-- Script -->
<script setup>
// Imports
// Composables
// Estados reativos
// Métodos/funções
// Lifecycle hooks
</script>

<!-- Estilos -->
<style scoped>
/* CSS com comentários organizacionais */
</style>
```

## 🧪 Testes

### Estrutura de Testes

- **Unitários**: Componentes individuais
- **Integração**: Fluxos completos
- **E2E**: Cenários de usuário (Cypress)

### Executar Testes

```bash
# Testes unitários
npm run test:unit

# Testes E2E
npm run test:e2e
```

## 📈 Performance

### Otimizações Implementadas

1. **Lazy Loading**: Componentes carregados sob demanda
2. **Code Splitting**: Chunks separados por rota
3. **Tree Shaking**: Remoção de código não utilizado
4. **Minificação**: Build otimizado para produção
5. **Caching**: Headers adequados para assets estáticos

## 🤝 Contribuição

### Fluxo de Desenvolvimento

1. Clone o repositório
2. Instale as dependências: `npm install`
3. Crie uma branch: `git checkout -b feature/nova-funcionalidade`
4. Faça suas alterações
5. Execute os testes: `npm run test`
6. Commit suas mudanças: `git commit -m "feat: adiciona nova funcionalidade"`
7. Abra um Pull Request

### Padrões de Commit

Use [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` nova funcionalidade
- `fix:` correção de bug
- `docs:` documentação
- `style:` formatação
- `refactor:` refatoração
- `test:` testes
- `chore:` manutenção

## 📞 Suporte

Para dúvidas ou problemas:

1. Verifique a documentação
2. Consulte os logs do console do navegador
3. Verifique se o backend está rodando
4. Abra uma issue no repositório

---

**Desenvolvido com ❤️ usando Vue.js 3**
