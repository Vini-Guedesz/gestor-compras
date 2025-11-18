# Arquitetura do Frontend - Gestor de Compras

## 📁 Estrutura Híbrida (Feature-Based + UI)

Este projeto adota uma arquitetura híbrida que combina organização por funcionalidades (features) com componentes UI genéricos.

### 🎯 Princípios

1. **Separação por Domínio**: Componentes relacionados a funcionalidades específicas do negócio ficam agrupados em `features/`
2. **Reutilização de UI**: Componentes genéricos e reutilizáveis ficam em `components/ui/`
3. **Imports Absolutos**: Todos os imports usam o alias `@/` para melhor manutenibilidade
4. **Lazy Loading**: Componentes pesados são carregados sob demanda com `defineAsyncComponent`

---

## 📂 Estrutura de Pastas

```
src/
├── features/                    # Funcionalidades de negócio
│   ├── pedidos/
│   │   └── components/
│   │       ├── PedidoForm.vue        # Container principal do wizard
│   │       ├── WizardProgress.vue    # Indicador de progresso
│   │       ├── Page1.vue             # Página 1: Criação
│   │       ├── Page2.vue             # Página 2: Aprovação
│   │       ├── Page3.vue             # Página 3: Cotações
│   │       ├── ItemCard.vue          # Card de item
│   │       ├── CotacaoFormItem.vue   # Form de cotação
│   │       └── DrawerCotacoes.vue    # Drawer lateral
│   │
│   ├── cotacoes/
│   │   └── components/
│   │       └── CotacaoForm.vue
│   │
│   ├── fornecedores/
│   │   └── components/
│   │       └── FornecedorForm.vue
│   │
│   └── dashboard/
│       └── components/
│           ├── DashboardHeader.vue
│           ├── DashboardSidebar.vue
│           ├── MetricCard.vue
│           └── QuickActions.vue
│
├── components/                  # Componentes UI genéricos
│   └── ui/
│       ├── modals/
│       │   ├── ConfirmModal.vue
│       │   └── LogoutModal.vue
│       └── feedback/
│           └── LoadingSpinner.vue
│
├── layouts/                     # Layouts da aplicação
│   └── AppLayout.vue
│
├── views/                       # Páginas/Rotas
│   ├── LoginView.vue
│   ├── DashboardView.vue
│   ├── PedidosView.vue
│   ├── CotacoesView.vue
│   ├── FornecedoresView.vue
│   ├── NovaCotacaoView.vue
│   ├── PerfilView.vue
│   └── ConfiguracoesView.vue
│
├── services/                    # Camada de serviços (API)
│   ├── api.js
│   ├── authService.js
│   ├── pedidoService.js
│   ├── cotacaoService.js
│   ├── fornecedorService.js
│   ├── itemPedidoService.js
│   └── relatorioService.js
│
├── stores/                      # Estado global (Pinia)
│   └── auth.js
│
├── router/                      # Rotas
│   └── index.js
│
├── composables/                 # Composables Vue
│   └── useMobileSidebar.js
│
├── utils/                       # Utilitários
│   └── genderUtils.js
│
├── assets/                      # Recursos estáticos
│   └── css/
│
├── App.vue                      # Componente raiz
└── main.js                      # Entry point
```

---

## 🔗 Padrão de Imports

### ✅ Correto (usar sempre)
```javascript
// Imports absolutos com alias @/
import PedidoForm from '@/features/pedidos/components/PedidoForm.vue'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import ConfirmModal from '@/components/ui/modals/ConfirmModal.vue'
import LoadingSpinner from '@/components/ui/feedback/LoadingSpinner.vue'
import { useAuthStore } from '@/stores/auth'
import pedidoService from '@/services/pedidoService.js'
```

### ❌ Evitar (imports relativos)
```javascript
// NÃO usar caminhos relativos
import PedidoForm from '../components/PedidoForm.vue'
import DashboardHeader from '../../components/DashboardHeader.vue'
```

---

## 🚀 Lazy Loading

Componentes pesados devem ser carregados sob demanda:

```javascript
import { defineAsyncComponent } from 'vue'

// Carregamento preguiçoso
const PedidoForm = defineAsyncComponent(() => 
  import('@/features/pedidos/components/PedidoForm.vue')
)

const ConfirmModal = defineAsyncComponent(() => 
  import('@/components/ui/modals/ConfirmModal.vue')
)
```

### Quando usar Lazy Loading?
- ✅ Modais
- ✅ Formulários complexos
- ✅ Componentes grandes com muita lógica
- ❌ Componentes pequenos e leves
- ❌ Componentes usados em toda a aplicação (Header, Sidebar)

---

## 📋 Guia de Onde Colocar Novos Componentes

### `/features/{dominio}/components/`
Componentes específicos de uma funcionalidade de negócio:
- Formulários de entidades específicas
- Listas/Tabelas de dados específicos
- Cards de exibição de informações de negócio
- Componentes que fazem chamadas de API específicas
- Sub-componentes relacionados ficam no mesmo nível

**Exemplo**: 
- `PedidoForm.vue` - Componente principal
- `Page1.vue`, `Page2.vue` - Sub-componentes do wizard
- `ItemCard.vue` - Card usado pelo PedidoForm
- `CotacaoForm.vue` - Componente independente

### `/components/ui/`
Componentes genéricos e reutilizáveis:
- Modais genéricos
- Botões customizados
- Inputs customizados
- Spinners de carregamento
- Tooltips, badges, alerts
- Componentes sem lógica de negócio

**Exemplo**: `ConfirmModal.vue`, `LoadingSpinner.vue`

### `/layouts/`
Estruturas de layout da aplicação:
- Layouts de página (com header, sidebar, footer)
- Wrappers de autenticação
- Templates de página

**Exemplo**: `AppLayout.vue`

---

## 🎨 Wizard Multi-Página (Pedidos)

O sistema de pedidos utiliza um **wizard de 3 páginas** com estrutura simples e direta:

**Componentes do Wizard:**
- `PedidoForm.vue` - Container principal do wizard
- `WizardProgress.vue` - Indicador visual de progresso (3 steps)
- `Page1.vue` - Página 1: Criação do pedido + adicionar itens
- `Page2.vue` - Página 2: Aprovar/rejeitar itens
- `Page3.vue` - Página 3: Adicionar cotações aos itens
- `ItemCard.vue` - Card visual de cada item (usado na Page2)
- `CotacaoFormItem.vue` - Formulário de cotação (usado na Page3)
- `DrawerCotacoes.vue` - Drawer lateral para visualizar cotações

Todos os componentes ficam no mesmo nível em `/features/pedidos/components/`, sem subpastas.

### Página 1 - Criação do Pedido
- Criar pedido com informações básicas
- Adicionar itens ao pedido
- Componente: `Page1.vue`

### Página 2 - Aprovação de Itens
- Visualizar itens adicionados em cards
- Aprovar/rejeitar itens
- Componente: `Page2.vue`
- Sub-componente: `ItemCard.vue`

### Página 3 - Adicionar Cotações
- Adicionar cotações para itens aprovados
- Visualizar cotações existentes em drawer lateral
- Componente: `Page3.vue`
- Sub-componentes: `CotacaoFormItem.vue`, `DrawerCotacoes.vue`

### Progresso Visual
- Componente: `WizardProgress.vue`
- Indica visualmente em qual etapa o usuário está
- Permite navegação entre páginas

---

## 🔄 Serviços e API

Todos os serviços seguem um padrão consistente:

```javascript
// services/pedidoService.js
const pedidoService = {
  listar() { /* GET /pedidos */ },
  buscarPorId(id) { /* GET /pedidos/:id */ },
  salvar(pedido) { /* POST ou PUT /pedidos */ },
  excluir(id) { /* DELETE /pedidos/:id */ }
}
```

### Métodos Atualizados
- **pedidoService**: `salvar(pedido)` - unifica create/update
- **cotacaoService**: `listarPorPedido()`, `listarPorItem()`, `salvar()`
- Todos os serviços usam nomes de campos corretos do backend

---

## ✅ Benefícios da Arquitetura Híbrida

1. **Escalabilidade**: Fácil adicionar novas features sem bagunçar a estrutura
2. **Manutenibilidade**: Código relacionado fica junto
3. **Reutilização**: Componentes UI genéricos podem ser usados em qualquer feature
4. **Clareza**: Fica claro onde cada componente deve ficar
5. **Performance**: Lazy loading reduz bundle inicial
6. **Colaboração**: Múltiplos devs podem trabalhar em features diferentes sem conflitos

---

## 📝 Checklist para Novos Componentes

Antes de criar um novo componente, pergunte:

1. **É específico de uma funcionalidade?** → `/features/{dominio}/components/`
2. **É genérico e reutilizável?** → `/components/ui/`
3. **É um layout de página?** → `/layouts/`
4. **É uma página/rota?** → `/views/`
5. **É pesado?** → Use `defineAsyncComponent`
6. **Usa imports absolutos?** → Sempre use `@/`

**Regra Simples**: Não crie subpastas dentro de `components/` a menos que seja absolutamente necessário para organização por tipo (modals, cards, etc).

---

## 🔍 Migração Completa

Todos os componentes foram migrados para a nova estrutura:
- ✅ 8 componentes de pedidos em estrutura **flat** (sem subpastas desnecessárias)
- ✅ 1 componente de cotações
- ✅ 1 componente de fornecedores
- ✅ 4 componentes de dashboard
- ✅ 3 componentes UI (modais + spinner)
- ✅ 1 layout
- ✅ Todos os imports atualizados
- ✅ Arquivos antigos removidos
- ✅ Estrutura simples e direta

**Resultado**: ✅ Nenhum erro de compilação!

---

## 📚 Filosofia da Estrutura

### ✅ **Simplicidade > Complexidade**

**Estrutura Flat (o que usamos):**
```
features/pedidos/components/
├── PedidoForm.vue
├── Page1.vue
├── Page2.vue
├── ItemCard.vue
└── CotacaoFormItem.vue
```

**Vantagens:**
- ✅ Simples de navegar
- ✅ Fácil de encontrar arquivos
- ✅ Imports diretos: `import Page1 from './Page1.vue'`
- ✅ Menos hierarquia = menos confusão
- ✅ Perfeito para projetos de médio porte

**Quando criar subpastas:**
- Quando tiver **mais de 15-20 componentes** no mesmo domínio
- Quando precisar separar por **tipo** (modals, cards, forms)
- Quando componentes forem **verdadeiramente independentes**

### ❌ **Evitar Over-Engineering**

Não crie pastas apenas "porque parece organizado". Prefira estrutura flat até que a complexidade justifique subpastas.
