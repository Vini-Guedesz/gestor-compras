# Gestor de Compras - Frontend

Single Page Application (SPA) desenvolvida com Vue.js 3 para gestão de compras.

## 🚀 Funcionalidades

- **Autenticação**: Sistema de login seguro com validação
- **Dashboard**: Tela principal com visão geral do sistema
- **SPA**: Navegação fluida sem recarregamento de página
- **Responsivo**: Interface adaptável a diferentes dispositivos
- **Estado Global**: Gerenciamento de estado com Pinia

## 🛠️ Tecnologias Utilizadas

- **Vue.js 3** - Framework JavaScript progressivo
- **Vue Router** - Roteamento oficial do Vue.js
- **Pinia** - Gerenciamento de estado
- **Vite** - Build tool e desenvolvimento
- **CSS3** - Estilização moderna

## 📦 Estrutura do Projeto

```
src/
├── components/          # Componentes reutilizáveis
│   ├── AppLayout.vue   # Layout principal da aplicação
│   └── LoadingSpinner.vue # Componente de loading
├── views/              # Páginas da aplicação
│   ├── LoginView.vue   # Tela de login
│   └── DashboardView.vue # Tela principal
├── stores/             # Gerenciamento de estado (Pinia)
│   └── auth.js         # Store de autenticação
├── router/             # Configuração de rotas
│   └── index.js        # Rotas da aplicação
├── App.vue             # Componente raiz
└── main.js             # Ponto de entrada da aplicação
```

## 🏃‍♂️ Como Executar

### Pré-requisitos
- Node.js (versão 20.19.0 ou superior)
- npm ou yarn

### Instalação e Execução

1. **Clone o repositório**
   ```bash
   git clone [url-do-repositorio]
   cd vue-project
   ```

2. **Instale as dependências**
   ```bash
   npm install
   ```

3. **Execute o servidor de desenvolvimento**
   ```bash
   npm run dev
   ```

4. **Acesse a aplicação**
   - Abra o navegador em `http://localhost:5173` (ou a porta indicada no terminal)

### Scripts Disponíveis

```bash
# Desenvolvimento
npm run dev

# Build para produção
npm run build

# Preview da build de produção
npm run preview

# Testes unitários
npm run test:unit

# Testes E2E
npm run test:e2e

# Linting
npm run lint

# Formatação de código
npm run format
```

## 🔐 Sistema de Autenticação

### Login de Teste
Para testar a aplicação, use qualquer combinação de usuário e senha. O sistema está configurado para aceitar qualquer credencial não vazia.

### Funcionalidades de Autenticação
- ✅ Validação de campos obrigatórios
- ✅ Estados de loading durante o login
- ✅ Mensagens de erro claras
- ✅ Persistência de sessão (localStorage)
- ✅ Proteção de rotas
- ✅ Logout seguro
- ✅ Recuperação de senha (simulada)

## 🎨 Características da Interface

### Tela de Login
- **Design limpo**: Fundo branco com caixa centralizada
- **Campos de entrada**: Login e senha com validação
- **Botão de acesso**: Estilizado e responsivo
- **Recuperação de senha**: Link funcional abaixo do botão
- **Feedback visual**: Estados de loading e mensagens de erro

### Tela Principal (Dashboard)
- **Header fixo**: Navegação e informações do usuário
- **Cards informativos**: Visão geral dos dados do sistema
- **Layout responsivo**: Grid adaptável
- **Navegação**: Sistema de rotas protegidas

## 🔧 Desenvolvimento

### Estrutura de Componentes
- **LoginView**: Tela de autenticação
- **DashboardView**: Tela principal do sistema
- **AppLayout**: Layout base com header e navegação
- **LoadingSpinner**: Componente reutilizável de loading

### Gerenciamento de Estado
O store de autenticação (`stores/auth.js`) gerencia:
- Estado de autenticação do usuário
- Dados do usuário logado
- Token de autenticação
- Funções de login/logout
- Persistência de sessão

### Rotas Protegidas
- `/login` - Acesso apenas para usuários não autenticados
- `/dashboard` - Requer autenticação
- Redirecionamento automático baseado no estado de autenticação

## 🚧 Próximos Passos

1. **Integração com API**: Conectar com backend real
2. **Módulos adicionais**: Gestão de fornecedores, produtos, pedidos
3. **Permissões**: Sistema de roles e permissões
4. **Relatórios**: Dashboards com gráficos e métricas
5. **Notificações**: Sistema de alertas e notificações

## 📱 Responsividade

A aplicação é totalmente responsiva e funciona bem em:
- 💻 Desktop (1200px+)
- 📱 Tablet (768px - 1199px)
- 📱 Mobile (320px - 767px)

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.
