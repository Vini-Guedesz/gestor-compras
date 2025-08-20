import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/DashboardView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { requiresGuest: true }
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
      meta: { requiresAuth: true }
    }
  ],
})

// Guarda de rotas
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // Verificar se há dados de autenticação salvos ao inicializar
  if (!authStore.isAuthenticated) {
    authStore.checkAuth()
  }

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    // Rota requer autenticação mas usuário não está logado
    next('/login')
  } else if (to.meta.requiresGuest && authStore.isAuthenticated) {
    // Rota é apenas para visitantes mas usuário está logado
    next('/dashboard')
  } else {
    next()
  }
})

export default router
