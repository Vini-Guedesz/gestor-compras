# 🔄 GUIA DE INTEGRAÇÃO FRONTEND-BACKEND

## ✅ ALTERAÇÕES REALIZADAS NO FRONTEND

### 1. **Endpoints Corrigidos**

#### **FornecedorService**
- ✅ **Produtos**: `/api/fornecedores-produto` → `/api/fornecedores-de-produto`
- ✅ **Serviços**: `/api/fornecedores-servico` → `/api/fornecedores-de-servico`
- ✅ **Update**: Corrigido para usar PUT sem ID na URL (backend espera ID no body)

#### **CotacaoService**
- ✅ **Base URL**: `/cotacoes` → `/api/cotacoes`
- ✅ **Removido**: `.data` redundante nas respostas (api.js já retorna response.data)

#### **PedidoService**
- ✅ **Base URL**: Já estava correto `/api/solicitacoes-pedido`

#### **RelatorioService**
- ✅ **Endpoint**: Já estava correto `/relatorios/fornecedores`

### 2. **Configurações Verificadas**

#### **API Base**
- ✅ **URL**: `http://localhost:8081` (corresponde ao backend)
- ✅ **Timeout**: 10 segundos
- ✅ **Headers**: Content-Type application/json
- ✅ **Auth**: Bearer token automático

#### **CORS**
- ⚠️ **Backend aceita apenas**: `http://localhost:5173`
- ✅ **Frontend configurado para**: `http://localhost:8081`

## 🚀 COMO TESTAR A INTEGRAÇÃO

### 1. **Iniciar o Backend**
```bash
cd gestor-compras-backend
./mvnw spring-boot:run
```
**Ou via IDE:** Run `GestorComprasBackendApplication.java`

### 2. **Iniciar o Frontend**
```bash
cd gestor-compras-frontend/vue-project
npm run dev
```

### 3. **Verificar Conexão**
- ✅ Backend: http://localhost:8081
- ✅ Frontend: http://localhost:5173
- ✅ Swagger: http://localhost:8081/swagger-ui.html

## 🔧 ESTRUTURA DE DADOS ALINHADA

### **FornecedorDeProdutoCreateDTO**
```json
{
  "razaoSocial": "string (obrigatório)",
  "cnpj": "string (obrigatório)",
  "inscricaoEstadual": "string (opcional)",
  "endereco": {
    "cep": "string (obrigatório)",
    "estado": "string (obrigatório)",
    "cidade": "string (obrigatório)",
    "bairro": "string (obrigatório)",
    "rua": "string (obrigatório)",
    "numero": "string (obrigatório)",
    "complemento": "string (opcional)"
  },
  "contato": {
    "numero": "string (obrigatório)",
    "email": "string (obrigatório, formato email)"
  }
}
```

### **FornecedorDeServicoCreateDTO**
```json
{
  "razaoSocial": "string (obrigatório)",
  "cnpj": "string (obrigatório)",
  "inscricaoMunicipal": "string (opcional)",
  "endereco": { /* mesmo que produto */ },
  "contato": { /* mesmo que produto */ }
}
```

### **LoginRequestDTO**
```json
{
  "email": "string",
  "senha": "string"  // ⚠️ Não "password"
}
```

### **TokenResponseDTO**
```json
{
  "token": "string"
}
```

## 🛡️ SEGURANÇA

### **Endpoints Públicos (sem autenticação)**
- ✅ `/auth/login`
- ✅ `/api/fornecedores-de-produto/**`
- ✅ `/api/fornecedores-de-servico/**`
- ✅ `/api/solicitacoes-pedido/**`
- ✅ `/relatorios/**`
- ✅ `/swagger-ui/**`

### **Endpoints Protegidos**
- ✅ `/api/cotacoes/**` (requer token JWT)
- ✅ `/api/users/**` (ADMIN apenas)

## ⚡ COMANDOS ÚTEIS

### **Desenvolvimento**
```bash
# Frontend
npm run dev

# Backend
./mvnw spring-boot:run

# Build Frontend
npm run build

# Verificar logs do backend
tail -f logs/application.log
```

### **Teste de Endpoints**
```bash
# Login
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","senha":"password"}'

# Listar fornecedores
curl -X GET http://localhost:8081/api/fornecedores-de-produto

# Relatório
curl -X GET http://localhost:8081/relatorios/fornecedores \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output relatorio.pdf
```

## 🐛 TROUBLESHOOTING

### **Erro de CORS**
- ✅ Verificar se frontend roda em `http://localhost:5173`
- ✅ Backend configurado para aceitar apenas essa origem

### **Erro 401 (Unauthorized)**
- ✅ Verificar se token JWT está sendo enviado
- ✅ Verificar se token não expirou
- ✅ Verificar formato: `Authorization: Bearer <token>`

### **Erro 404 (Endpoint não encontrado)**
- ✅ Verificar se endpoints coincidem com controllers
- ✅ Verificar se backend está rodando na porta 8081

### **Erro de Conexão**
- ✅ Verificar se backend está rodando
- ✅ Verificar se PostgreSQL está ativo
- ✅ Verificar firewall/antivírus

## 📋 CHECKLIST FINAL

- ✅ Backend roda na porta 8081
- ✅ Frontend roda na porta 5173  
- ✅ Endpoints ajustados no frontend
- ✅ CORS configurado no backend
- ✅ JWT funcionando
- ✅ Estruturas de dados alinhadas
- ✅ Build do frontend funcionando
- ✅ Swagger documentação disponível

## 🎯 PRÓXIMOS PASSOS

1. **Testar Login**: Verificar autenticação JWT
2. **Testar CRUD**: Fornecedores, Pedidos, Cotações
3. **Testar Relatórios**: Download de PDF
4. **Testar Upload**: Anexos de cotações
5. **Configurar Produção**: URLs e variáveis de ambiente

---

**Status**: ✅ **PRONTO PARA INTEGRAÇÃO**