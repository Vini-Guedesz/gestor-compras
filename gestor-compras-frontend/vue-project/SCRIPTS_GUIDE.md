# Scripts do Package.json - Explicação Detalhada

Este arquivo explica o que cada script do `package.json` faz e quando utilizá-los.

## 📦 Scripts Principais

### Aplicação Completa (NOVO!)
```bash
npm run start:full
# ou
npm run start:app
```
- **O que faz**: Inicia backend (Spring Boot) e frontend (Vue.js) simultaneamente
- **Portas**: Backend na 8081, Frontend na 5173
- **Inclui**: Banco H2 em memória, APIs REST, interface web
- **Características**: 
  - Cores diferenciadas nos logs (Backend: cyan, Frontend: green)
  - Ambos os serviços em paralelo
  - Hot reload automático no frontend
  - LiveReload no backend
- **Quando usar**: Para desenvolvimento com stack completo

### Backend Isolado
```bash
npm run backend
```
- **O que faz**: Inicia apenas o backend Spring Boot
- **Porta**: 8081
- **Quando usar**: Para desenvolvimento focado na API

### Desenvolvimento Frontend
```bash
npm run dev
```
- **O que faz**: Inicia o servidor de desenvolvimento do Vite
- **Porta**: Geralmente roda na porta 5173
- **Características**: 
  - Hot reload automático
  - Compilação rápida
  - Source maps para debug
- **Quando usar**: Durante o desenvolvimento diário

### Build de Produção
```bash
npm run build
```
- **O que faz**: Cria uma versão otimizada para produção
- **Output**: Pasta `dist/` com arquivos minificados
- **Otimizações**:
  - Minificação de código
  - Tree shaking (remoção de código não usado)
  - Code splitting
  - Compressão de assets
- **Quando usar**: Antes de fazer deploy para produção

### Preview da Produção
```bash
npm run preview
```
- **O que faz**: Inicia um servidor local para testar o build de produção
- **Porta**: Geralmente roda na porta 4173
- **Quando usar**: Para testar a versão de produção localmente antes do deploy

## 🧪 Scripts de Teste

### Testes Unitários
```bash
npm run test:unit
```
- **Framework**: Vitest
- **O que testa**: Componentes individuais e funções
- **Modo watch**: Roda automaticamente quando arquivos mudam
- **Quando usar**: Durante desenvolvimento para garantir que componentes funcionam

### Testes E2E (End-to-End)
```bash
npm run test:e2e
```
- **Framework**: Cypress
- **O que testa**: Fluxos completos da aplicação
- **Modo**: Execução headless (sem interface gráfica)
- **Pré-requisito**: Build de produção funcionando
- **Quando usar**: Antes de releases para validar fluxos críticos

### Testes E2E em Desenvolvimento
```bash
npm run test:e2e:dev
```
- **Framework**: Cypress
- **Modo**: Interface gráfica interativa
- **Porta**: 4173
- **Quando usar**: Para desenvolver e debugar testes E2E

## 🔧 Scripts de Qualidade de Código

### Linting
```bash
npm run lint
```
- **O que faz**: Executa todos os linters em sequência
- **Inclui**: 
  - `oxlint`: Linter rápido focado em correções
  - `eslint`: Linter completo com regras customizáveis
- **Auto-fix**: Corrige automaticamente problemas simples
- **Quando usar**: Antes de commits para manter código padronizado

### Linting Individual
```bash
npm run lint:oxlint   # Linter rápido
npm run lint:eslint   # Linter completo
```

### Formatação
```bash
npm run format
```
- **Ferramenta**: Prettier
- **O que faz**: Formata código seguindo padrões definidos
- **Escopo**: Arquivos na pasta `src/`
- **Quando usar**: Para manter consistência visual do código

## 📚 Dependências Principais

### Produção (dependencies)
- **axios**: Cliente HTTP para comunicação com API
- **pinia**: Gerenciamento de estado global
- **vue**: Framework principal
- **vue-router**: Sistema de rotas

### Desenvolvimento (devDependencies)
- **@eslint/js**: Regras base do ESLint
- **vite**: Build tool e dev server
- **vitest**: Framework de testes unitários
- **cypress**: Framework de testes E2E
- **prettier**: Formatador de código

## 🔄 Fluxo de Desenvolvimento Recomendado

### Desenvolvimento Diário
1. `npm run dev` - Inicia desenvolvimento
2. Edite código com hot reload automático
3. `npm run test:unit` - Execute testes durante desenvolvimento

### Antes de Commit
1. `npm run lint` - Verifique/corrija problemas de código
2. `npm run format` - Formate código
3. `npm run test:unit` - Execute testes unitários

### Antes de Release
1. `npm run build` - Crie build de produção
2. `npm run preview` - Teste build localmente
3. `npm run test:e2e` - Execute testes E2E
4. Faça deploy da pasta `dist/`

## 🚀 Comandos de Deploy

### Build e Preview
```bash
# Cria build otimizado
npm run build

# Testa build localmente
npm run preview
```

### Verificação Completa
```bash
# Execute todos os checks de qualidade
npm run lint && npm run test:unit && npm run build && npm run test:e2e
```

## ⚙️ Configurações Importantes

### Versão do Node.js
- **Suportadas**: ^20.19.0 || >=22.12.0
- **Recomendada**: Use Node.js 20 LTS para estabilidade

### Tipo de Módulo
- **type**: "module" - Usa ES modules por padrão
- **Vantagem**: Sintaxe import/export nativa

### Ambiente Privado
- **private**: true - Evita publicação acidental no npm

## 🐛 Troubleshooting

### Problemas Comuns

**Erro de porta ocupada:**
```bash
# Encontre processo usando a porta
netstat -an | findstr :5173

# Ou use porta diferente
npm run dev -- --port 3000
```

**Problemas de cache:**
```bash
# Limpe cache do Vite
npx vite build --force

# Limpe node_modules e reinstale
rm -rf node_modules package-lock.json
npm install
```

**Testes E2E falhando:**
```bash
# Reinstale Cypress
npm run prepare

# Execute com debug
npm run test:e2e:dev
```

## 🚀 Scripts de Execução Rápida (NOVO!)

### Para rodar a aplicação completa

**Opção 1: Através do NPM (Recomendado)**
```bash
# No diretório gestor-compras-frontend/vue-project
npm run start:full
```

**Opção 2: Scripts do diretório raiz**
```powershell
# Execute no diretório raiz do projeto
.\run-app.ps1    # PowerShell
# ou
.\run-app.bat    # Command Prompt/Batch
```

### O que acontece quando você executa
- ✅ **Backend** inicia na porta 8081 (Spring Boot + H2)
- ✅ **Frontend** inicia na porta 5173 (Vue.js + Vite)
- ✅ **Banco H2** configurado em memória
- ✅ **Usuários de teste** criados automaticamente
- ✅ **APIs REST** disponíveis
- ✅ **Hot reload** ativo em ambos

### URLs importantes
- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8081
- **Console H2**: http://localhost:8081/h2-console
  - URL do banco: `jdbc:h2:mem:gestorcompras`
  - Usuário: `sa`
  - Senha: (vazio)

### Usuários de teste
- **Admin**: admin@gestor.com / admin123
- **Usuário**: user@gestor.com / user123

### Para parar a aplicação
- Pressione `Ctrl + C` no terminal onde está rodando
- Ou feche a janela do terminal

---

Esta documentação deve ser mantida atualizada conforme o projeto evolui.
