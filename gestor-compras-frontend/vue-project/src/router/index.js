/**
 * Configuração do sistema de rotas da aplicação
 *
 * Este arquivo define:
 * - Todas as rotas disponíveis na aplicação
 * - Proteção de rotas baseada em autenticação
 * - Redirecionamentos automáticos
 *
 * Estrutura de rotas:
 * - / → Redireciona para /login
 * - /login → Página de login (apenas para usuários não autenticados)
 * - /dashboard → Página principal (requer autenticação)
 * - /fornecedores → Gestão de fornecedores (requer autenticação)
 */

import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

// Lazy loading de rotas para melhor performance
// Cada view será carregada apenas quando necessária
const LoginView = () => import('../views/LoginView.vue')
const DashboardView = () => import('../views/DashboardView.vue')
const FornecedoresView = () => import('../views/FornecedoresView.vue')
const VisualizarFornecedorView = () => import('../views/VisualizarFornecedorView.vue')
const PedidosView = () => import('../views/PedidosView.vue')
const PerfilView = () => import('../views/PerfilView.vue')
const ConfiguracoesView = () => import('../views/ConfiguracoesView.vue')
const CotacoesView = () => import('../views/CotacoesView.vue')
const NovoPedidoView = () => import('../views/NovoPedidoView.vue')
const VisualizarPedidoView = () => import('../views/VisualizarPedidoView.vue')

// Criação do router com histórico de navegação do navegador
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login' // Redireciona a raiz para a página de login
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { requiresGuest: true } // Apenas para usuários não autenticados
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
      meta: { requiresAuth: true } // Requer autenticação
    },
    {
      path: '/fornecedores',
      name: 'fornecedores',
      component: FornecedoresView,
      meta: { requiresAuth: true } // Requer autenticação
    },
    {
      path: '/fornecedores/:id',
      name: 'visualizar-fornecedor',
      component: VisualizarFornecedorView,
      meta: { requiresAuth: true } // Requer autenticação
    },
    {
      path: '/pedidos',
      name: 'pedidos',
      component: PedidosView,
      meta: { requiresAuth: true } // Requer autenticação
    },
    {
      path: '/pedidos/novo',
      name: 'novo-pedido',
      component: NovoPedidoView,
      meta: { requiresAuth: true } // Requer autenticação
    },
    {
      path: '/pedidos/novo/:id',
      name: 'continuar-pedido',
      component: NovoPedidoView,
      meta: { requiresAuth: true } // Requer autenticação - continuar rascunho existente
    },
    {
      path: '/pedidos/rascunho/:id',
      name: 'editar-rascunho',
      component: NovoPedidoView,
      meta: { requiresAuth: true } // Requer autenticação
    },
    {
      path: '/pedidos/visualizar/:id',
      name: 'visualizar-pedido',
      component: VisualizarPedidoView,
      meta: { requiresAuth: true } // Requer autenticação
    },
    {
      path: '/cotacoes',
      name: 'cotacoes',
      component: CotacoesView,
      meta: { requiresAuth: true } // Requer autenticação
    },

    {
      path: '/cotacoes/:id',
      name: 'detalhes-cotacao',
      component: CotacoesView, // Reutiliza a view principal por enquanto
      meta: { requiresAuth: true } // Requer autenticação
    },
    {
      path: '/perfil',
      name: 'perfil',
      component: PerfilView,
      meta: { requiresAuth: true } // Requer autenticação
    },
    {
      path: '/configuracoes',
      name: 'configuracoes',
      component: ConfiguracoesView,
      meta: { requiresAuth: true } // Requer autenticação
    }
  ],
})

/**
 * Guarda de navegação global - executa antes de cada mudança de rota
 *
 * Responsabilidades:
 * - Verificar se o usuário está autenticado
 * - Proteger rotas que requerem autenticação
 * - Redirecionar usuários logados para fora da página de login
 * - Redirecionar usuários não logados para a página de login
 */
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  // Verificar se há dados de autenticação salvos no sessionStorage
  // Importante fazer isso antes de verificar as proteções de rota
  // O próprio checkAuth() agora previne chamadas duplicadas com sua flag interna
  if (!authStore.isAuthenticated) {
    await authStore.checkAuth()
  }

  // Proteção para rotas que requerem autenticação
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    // Usuário não autenticado tentando acessar rota protegida
    // Evita loop: só redireciona se não estiver indo para login
    if (to.path !== '/login') {
      next('/login')
    } else {
      next()
    }
  }
  // Proteção para rotas que são apenas para visitantes (ex: login)
  else if (to.meta.requiresGuest && authStore.isAuthenticated) {
    // Usuário já autenticado tentando acessar página de login
    // Evita loop: só redireciona se não estiver indo para dashboard
    if (to.path !== '/dashboard') {
      next('/dashboard')
    } else {
      next()
    }
  }
  // Caso contrário, permite a navegação
  else {
    next()
  }
})

export default router
