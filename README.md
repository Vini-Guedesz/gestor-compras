# 🛒 Gestor de Compras

Sistema completo para gestão de compras com backend em Spring Boot e frontend em Vue.js.

## 🚀 Início Rápido

### Executar Aplicação Completa (Recomendado)

**Opção 1: Script PowerShell (Windows)**
```powershell
.\run-app.ps1
```

**Opção 2: Script Batch (Windows)**
```cmd
.\run-app.bat
```

**Opção 3: Via NPM**
```bash
cd gestor-compras-frontend/vue-project
npm run start:full
```

### O que é iniciado automaticamente

- ✅ **Backend (Spring Boot)** → `http://localhost:8081`
- ✅ **Frontend (Vue.js)** → `http://localhost:5173`
- ✅ **Banco H2 em memória** → `http://localhost:8081/h2-console`
- ✅ **Usuários de teste** pré-configurados

## 👥 Usuários de Teste

| Tipo | Email | Senha |
|------|-------|-------|
| **Administrador** | admin@gestor.com | admin123 |
| **Usuário** | user@gestor.com | user123 |

## 🗄️ Banco de Dados

### Console H2
- **URL**: http://localhost:8081/h2-console
- **JDBC URL**: `jdbc:h2:mem:gestorcompras`
- **Username**: `sa`
- **Password**: (deixe em branco)

## 📁 Estrutura do Projeto

```
gestor-compras/
├── 📁 gestor-compras-backend/     # API Spring Boot
│   ├── src/main/java/             # Código fonte Java
│   ├── src/main/resources/        # Configurações
│   └── mvnw.cmd                   # Maven Wrapper
├── 📁 gestor-compras-frontend/    # Interface Vue.js
│   └── vue-project/
│       ├── src/                   # Código fonte Vue
│       ├── package.json           # Dependências NPM
│       └── SCRIPTS_GUIDE.md       # Guia detalhado
├── 🚀 run-app.ps1                 # Script PowerShell
├── 🚀 run-app.bat                 # Script Batch
└── 📋 README.md                   # Este arquivo
```

## 🛠️ Desenvolvimento Individual

### Backend apenas
```bash
cd gestor-compras-backend
.\mvnw.cmd spring-boot:run
```

### Frontend apenas
```bash
cd gestor-compras-frontend/vue-project
npm run dev
```

## 📊 Portas e URLs

| Serviço | Porta | URL |
|---------|-------|-----|
| **Frontend** | 5173 | http://localhost:5173 |
| **Backend API** | 8081 | http://localhost:8081 |
| **Console H2** | 8081 | http://localhost:8081/h2-console |

## 🔧 Tecnologias

### Backend
- **Spring Boot 3.5.4**
- **Java 21**
- **Spring Security** (JWT)
- **Spring Data JPA**
- **H2 Database** (desenvolvimento)
- **Maven**

### Frontend
- **Vue.js 3**
- **Vite** (build tool)
- **Vue Router**
- **Pinia** (state management)
- **Axios** (HTTP client)

## 📖 Documentação Detalhada

Para informações detalhadas sobre scripts, comandos e desenvolvimento:
- 📄 [Frontend Scripts Guide](gestor-compras-frontend/vue-project/SCRIPTS_GUIDE.md)

## ⚡ Comandos Úteis

### Parar aplicação
```bash
Ctrl + C  # No terminal onde está rodando
```

### Reinstalar dependências
```bash
cd gestor-compras-frontend/vue-project
rm -rf node_modules package-lock.json
npm install
```

### Limpar build backend
```bash
cd gestor-compras-backend
.\mvnw.cmd clean
```

## 🐛 Problemas Comuns

### Porta ocupada
```powershell
# Verificar processo na porta 8081 (backend)
netstat -an | findstr :8081

# Verificar processo na porta 5173 (frontend)
netstat -an | findstr :5173
```

### Java não encontrado
- Certifique-se de ter **Java 21** ou superior instalado
- Verifique: `java -version`

### Node.js não encontrado
- Certifique-se de ter **Node.js 20+** instalado
- Verifique: `node -v`

## 📈 Status do Projeto

- ✅ Autenticação JWT
- ✅ CRUD de usuários
- ✅ Interface responsiva
- ✅ Banco em memória (desenvolvimento)
- ⏳ Gestão de fornecedores (em desenvolvimento)
- ⏳ Gestão de compras (em desenvolvimento)

---

**Versão**: 1.0.0  
**Última atualização**: Agosto 2025
