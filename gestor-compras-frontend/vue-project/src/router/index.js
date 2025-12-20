/**
 * @fileoverview Configuração do Vue Router
 *
 * Define sistema completo de rotas da aplicação com:
 * - Lazy loading de componentes para performance
 * - Proteção de rotas baseada em autenticação
 * - Guards globais (beforeEach)
 * - Redirecionamentos automáticos
 * - Rotas dinâmicas com parâmetros
 *
 * @module router
 * @requires vue-router
 * @requires stores/auth
 *
 * @description
 * Estrutura de rotas:
 *
 * Públicas (requiresGuest):
 * - /login - Autenticação de usuário
 *
 * Protegidas (requiresAuth):
 * - /dashboard - Painel principal com métricas
 * - /fornecedores - Lista de fornecedores
 * - /fornecedores/:id - Detalhes do fornecedor
 * - /pedidos - Lista de pedidos
 * - /pedidos/novo - Criar novo pedido
 * - /pedidos/novo/:id - Continuar rascunho
 * - /pedidos/rascunho/:id - Editar rascunho
 * - /pedidos/visualizar/:id - Detalhes do pedido
 * - /cotacoes - Lista de cotações
 * - /cotacoes/:id - Detalhes da cotação
 * - /perfil - Perfil do usuário
 * - /configuracoes - Configurações do sistema
 *
 * @example
 * // Navegação programática
 * import { useRouter } from 'vue-router'
 *
 * const router = useRouter()
 * router.push('/dashboard')
 * router.push({ name: 'visualizar-pedido', params: { id: 123 } })
 *
 * @example
 * // Links declarativos
 * <router-link to="/fornecedores">Fornecedores</router-link>
 * <router-link :to="{ name: 'editar-rascunho', params: { id: rascunhoId } }">
 *   Editar Rascunho
 * </router-link>
 *
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

/**
 * Lazy loading de views para otimização de performance
 *
 * @description
 * Cada view é carregada apenas quando a rota é acessada.
 * Reduz bundle inicial e acelera tempo de carregamento.
 *
 * Sintaxe: () => import('./path/to/component.vue')
 * Webpack/Vite criam chunks separados automaticamente.
 */
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
const UsuariosView = () => import('../views/UsuariosView.vue')

/**
 * @typedef {Object} RouteMeta
 * @property {boolean} [requiresAuth] - Se rota requer autenticação
 * @property {boolean} [requiresGuest] - Se rota é só para visitantes (não logados)
 */

/**
 * Instância do Vue Router
 *
 * @constant {import('vue-router').Router}
 *
 * @description
 * Configurado com createWebHistory para URLs limpas (sem #).
 * Requer configuração de servidor para redirecionar 404 para index.html.
 */
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login' // Redireciona raiz para login
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { requiresGuest: true } // Apenas não autenticados
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
      meta: { requiresAuth: true }
    },
    {
      path: '/fornecedores/:id',
      name: 'visualizar-fornecedor',
      component: VisualizarFornecedorView,
      meta: { requiresAuth: true }
    },
    {
      path: '/pedidos',
      name: 'pedidos',
      component: PedidosView,
      meta: { requiresAuth: true }
    },
    {
      path: '/pedidos/novo',
      name: 'novo-pedido',
      component: NovoPedidoView,
      meta: { requiresAuth: true }
    },
    {
      path: '/pedidos/novo/:id',
      name: 'continuar-pedido',
      component: NovoPedidoView,
      meta: { requiresAuth: true } // Continuar rascunho existente
    },
    {
      path: '/pedidos/rascunho/:id',
      name: 'editar-rascunho',
      component: NovoPedidoView,
      meta: { requiresAuth: true }
    },
    {
      path: '/pedidos/visualizar/:id',
      name: 'visualizar-pedido',
      component: VisualizarPedidoView,
      meta: { requiresAuth: true }
    },
    {
      path: '/cotacoes',
      name: 'cotacoes',
      component: CotacoesView,
      meta: { requiresAuth: true }
    },

    {
      path: '/cotacoes/:id',
      name: 'detalhes-cotacao',
      component: CotacoesView, // Reutiliza view principal
      meta: { requiresAuth: true }
    },
    {
      path: '/perfil',
      name: 'perfil',
      component: PerfilView,
      meta: { requiresAuth: true }
    },
    {
      path: '/configuracoes',
      name: 'configuracoes',
      component: ConfiguracoesView,
      meta: { requiresAuth: true }
    },
    {
      path: '/usuarios',
      name: 'usuarios',
      component: UsuariosView,
      meta: { requiresAuth: true }
    }
  ],
})

/**
 * Navigation Guard Global - beforeEach
 *
 * @function beforeEach
 * @param {import('vue-router').RouteLocationNormalized} to - Rota destino
 * @param {import('vue-router').RouteLocationNormalized} from - Rota origem
 * @param {import('vue-router').NavigationGuardNext} next - Função de continuação
 *
 * @description
 * Executado antes de cada navegação de rota.
 *
 * Responsabilidades:
 * 1. Verificar autenticação do usuário (via authStore.checkAuth)
 * 2. Proteger rotas com meta.requiresAuth
 * 3. Redirecionar usuários não logados para /login
 * 4. Redirecionar usuários logados para /dashboard se tentarem acessar /login
 * 5. Prevenir loops de redirecionamento
 *
 * @example
 * // Rota protegida
 * {
 *   path: '/dashboard',
 *   component: DashboardView,
 *   meta: { requiresAuth: true } // Guard bloqueia se não autenticado
 * }
 *
 * @example
 * // Rota só para visitantes
 * {
 *   path: '/login',
 *   component: LoginView,
 *   meta: { requiresGuest: true } // Guard redireciona se já logado
 * }
 */
/**
 * Flag para evitar múltiplas chamadas checkAuth simultâneas
 * @private
 */
let isCheckingAuth = false
let lastAuthCheck = null

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  // Verificar sessão salva no sessionStorage apenas se necessário
  // Otimização: só verifica auth se não estiver autenticado E não estiver verificando
  if (!authStore.isAuthenticated && !isCheckingAuth) {
    isCheckingAuth = true
    try {
      await authStore.checkAuth()
      lastAuthCheck = Date.now()
    } finally {
      isCheckingAuth = false
    }
  }

  // CASO 1: Rota requer autenticação (meta.requiresAuth)
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    // Usuário não autenticado tentando acessar rota protegida
    // Previne loop: só redireciona se não estiver indo para /login
    if (to.path !== '/login') {
      next('/login')
    } else {
      next()
    }
  }
  // CASO 2: Rota só para visitantes (meta.requiresGuest)
  else if (to.meta.requiresGuest && authStore.isAuthenticated) {
    // Usuário autenticado tentando acessar /login
    // Previne loop: só redireciona se não estiver indo para /dashboard
    if (to.path !== '/dashboard') {
      next('/dashboard')
    } else {
      next()
    }
  }
  // CASO 3: Sem restrições
  else {
    next()
  }
})

/**
 * Exporta instância do router
 *
 * @exports router
 * @type {import('vue-router').Router}
 *
 * @description
 * Importar em main.js: app.use(router)
 * Usar em componentes: const router = useRouter()
 */
export default router
