# Guia de Testes - Integração Frontend-Backend

## 📋 Configuração Completa da Integração

### ✅ O que foi configurado:

#### 1. **authService.js**
- ✅ Endpoint correto: `/auth/login`
- ✅ Estrutura de dados: `{ email, password }` (campo `password` em vez de `senha`)
- ✅ Tratamento de resposta: `response.data.token`
- ✅ Gerenciamento de localStorage automático
- ✅ Métodos de logout e verificação de autenticação
- ✅ Tratamento de erros com fallback

#### 2. **fornecedorService.js**
- ✅ Endpoints corretos: `/api/fornecedores-de-produto` e `/api/fornecedores-de-servico`
- ✅ Métodos CRUD completos (listar, criar, atualizar, remover)
- ✅ Fallback para dados mock se backend indisponível
- ✅ Tratamento de erros robusto

#### 3. **pedidoService.js**
- ✅ Endpoints corretos: `/api/solicitacoes-pedido`
- ✅ Métodos completos incluindo aprovação/rejeição
- ✅ Fallback para dados mock
- ✅ Tratamento de erros

#### 4. **api.js**
- ✅ URL base configurada: `http://localhost:8081`
- ✅ Interceptador JWT automático
- ✅ Tratamento global de erros
- ✅ Headers corretos para JSON

#### 5. **auth.js (Store)**
- ✅ Integração completa com authService
- ✅ Verificação automática de autenticação
- ✅ Gerenciamento de estado reativo

## 🧪 Como Testar a Integração

### 1. **Iniciar o Backend** (Porta 8081)
```bash
cd gestor-compras-backend
./mvnw spring-boot:run
```

### 2. **Iniciar o Frontend** (Porta 5173)
```bash
cd gestor-compras-frontend/vue-project
npm run dev
```

### 3. **Testes de Autenticação**
1. Acesse: `http://localhost:5173`
2. Tente fazer login com credenciais do backend
3. Verifique no console do navegador se a requisição é feita para `localhost:8081/auth/login`
4. Se backend não disponível, deve usar dados mock automaticamente

### 4. **Testes de CRUD**
1. **Fornecedores**: Vá para "Fornecedores" e teste criar/editar/listar
2. **Pedidos**: Vá para "Pedidos" e teste criar/editar/listar
3. Verifique no console se as requisições vão para endpoints corretos

### 5. **Monitoramento no Console**
Abra DevTools (F12) > Console e procure por:
- ✅ `Usando dados mock - backend indisponível` (se backend off)
- ❌ Erros de CORS
- ❌ Erros 404 ou endpoints incorretos

## 🔧 Configurações de Ambiente

### Backend (Porta 8081)
- URL: `http://localhost:8081`
- CORS configurado para `http://localhost:5173`
- Endpoints principais:
  - `/auth/login` - Login
  - `/api/fornecedores-de-produto` - Fornecedores de Produto
  - `/api/fornecedores-de-servico` - Fornecedores de Serviço
  - `/api/solicitacoes-pedido` - Pedidos

### Frontend (Porta 5173)
- URL: `http://localhost:5173`
- API configurada para `http://localhost:8081`
- Fallback automático para dados mock

## 🎯 Próximos Passos

1. **Teste o Login**: Primeiro teste mais importante
2. **Verifique CORS**: Se houver erro, ajustar backend
3. **Teste CRUD**: Testar operações básicas
4. **Ajustar DTOs**: Se estrutura de dados diferir, ajustar services
5. **Implementar Loading States**: Para melhor UX durante requisições

## 🚨 Possíveis Problemas e Soluções

### ❌ Erro de CORS
**Solução**: Verificar se backend tem CORS configurado para `localhost:5173`

### ❌ Erro 404 nos endpoints
**Solução**: Verificar se os controllers estão com as URLs corretas

### ❌ Token JWT não funcionando
**Solução**: Verificar se o formato do token está correto no backend

### ❌ Estrutura de dados incompatível
**Solução**: Ajustar DTOs nos services para match com backend

## 📊 Status da Integração

| Serviço | Endpoint | Status | Fallback |
|---------|----------|---------|----------|
| Login | `/auth/login` | ✅ Configurado | ❌ Não |
| Fornecedores Produto | `/api/fornecedores-de-produto` | ✅ Configurado | ✅ Mock |
| Fornecedores Serviço | `/api/fornecedores-de-servico` | ✅ Configurado | ✅ Mock |
| Pedidos | `/api/solicitacoes-pedido` | ✅ Configurado | ✅ Mock |

**Status**: 🟢 Pronto para testes de integração!
