# Integração Frontend-Backend - Gestor de Compras

## Configuração de Login

Este projeto agora está configurado para conectar o frontend Vue.js com o backend Spring Boot.

### Configurações Realizadas

1. **Serviço de API** (`src/services/api.js`)
   - Configurado com axios
   - Interceptors para autenticação automática
   - Tratamento de erros HTTP
   - Redirecionamento automático em caso de sessão expirada

2. **Serviço de Autenticação** (`src/services/authService.js`)
   - Método de login que chama `/auth/login` do backend
   - Validação de token JWT
   - Recuperação de senha (preparado para implementação futura)

3. **Store de Autenticação** (`src/stores/auth.js`)
   - Integração com o serviço de autenticação real
   - Validação assíncrona de tokens
   - Persistência de sessão no localStorage

4. **Página de Login** (`src/views/LoginView.vue`)
   - Campo alterado de "username" para "email"
   - Integração com o backend
   - Tratamento de erros melhorado

### Como executar

#### Backend (Spring Boot)
1. Certifique-se que o PostgreSQL está rodando na porta 5432
2. Execute o backend:
   ```bash
   cd gestor-compras-backend
   ./mvnw spring-boot:run
   ```
   O backend ficará disponível em `http://localhost:8080`

#### Frontend (Vue.js)
1. Instale as dependências:
   ```bash
   cd gestor-compras-frontend/vue-project
   npm install
   ```

2. Execute o frontend:
   ```bash
   npm run dev
   ```
   O frontend ficará disponível em `http://localhost:5173`

### Estrutura da API

#### Endpoint de Login
- **URL**: `POST /auth/login`
- **Body**:
  ```json
  {
    "email": "usuario@email.com",
    "senha": "senha123"
  }
  ```
- **Resposta**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIs..."
  }
  ```

### CORS Configurado

O backend está configurado para aceitar requisições do frontend em `http://localhost:5173`.

### Variáveis de Ambiente

Criados arquivos `.env.development` e `.env.production` para configurar a URL da API:
- **Desenvolvimento**: `http://localhost:8080`
- **Produção**: Ajuste conforme necessário

### Próximos Passos

1. Criar usuários de teste no banco de dados
2. Implementar endpoint de recuperação de senha no backend
3. Adicionar validações mais robustas
4. Implementar refresh token
