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
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/DashboardView.vue'
import FornecedoresView from '../views/FornecedoresView.vue'
import PedidosView from '../views/PedidosView.vue'
import PerfilView from '../views/PerfilView.vue'
import ConfiguracoesView from '../views/ConfiguracoesView.vue'
import CotacoesView from '../views/CotacoesView.vue'
import ComparacaoCotacoesView from '../views/ComparacaoCotacoesView.vue'

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
      path: '/pedidos',
      name: 'pedidos',
      component: PedidosView,
      meta: { requiresAuth: true } // Requer autenticação
    },
    {
      path: '/cotacoes',
      name: 'cotacoes',
      component: CotacoesView,
      meta: { requiresAuth: true } // Requer autenticação
    },
    {
      path: '/cotacoes/:id/comparacao',
      name: 'comparacao-cotacoes',
      component: ComparacaoCotacoesView,
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

  // Verificar se há dados de autenticação salvos no localStorage
  // Importante fazer isso antes de verificar as proteções de rota
  if (!authStore.isAuthenticated) {
    await authStore.checkAuth()
  }

  // Proteção para rotas que requerem autenticação
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    // Usuário não autenticado tentando acessar rota protegida
    next('/login')
  }
  // Proteção para rotas que são apenas para visitantes (ex: login)
  else if (to.meta.requiresGuest && authStore.isAuthenticated) {
    // Usuário já autenticado tentando acessar página de login
    next('/dashboard')
  }
  // Caso contrário, permite a navegação
  else {
    next()
  }
})

export default router
