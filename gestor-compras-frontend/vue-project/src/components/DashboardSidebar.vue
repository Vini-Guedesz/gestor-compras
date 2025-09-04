<template>
  <aside class="sidebar">
    <!-- Menu Principal -->
    <nav class="main-nav">
      <div class="nav-section">
        <h3 class="nav-title">Menu Principal</h3>
        <ul class="nav-list">
          <li class="nav-item">
            <router-link to="/dashboard" class="nav-link" :class="{ active: isActive('/dashboard') }">
              <svg class="nav-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"/>
              </svg>
              <span>Dashboard</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link to="/pedidos" class="nav-link" :class="{ active: isActive('/pedidos') }">
              <svg class="nav-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M7 4V2C7 1.45 7.45 1 8 1H16C16.55 1 17 1.45 17 2V4H20C20.55 4 21 4.45 21 5S20.55 6 20 6H19V19C19 20.1 18.1 21 17 21H7C5.9 21 5 20.1 5 19V6H4C3.45 6 3 5.55 3 5S3.45 4 4 4H7ZM9 3V4H15V3H9ZM7 6V19H17V6H7Z"/>
              </svg>
              <span>Pedidos de Compra</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link to="/fornecedores" class="nav-link" :class="{ active: isActive('/fornecedores') }">
              <svg class="nav-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M16 4c0-1.11.89-2 2-2s2 .89 2 2-.89 2-2 2-2-.89-2-2zm4 18v-6h2.5l-2.54-7.63A2 2 0 0 0 18.04 7H16c-.8 0-1.54.37-2.01.99L12 10l2.01-2.01C14.54 7.37 15.2 7 16 7h2.04c1.23 0 2.18 1.24 1.92 2.63l2.54 7.63H20v6h-4z"/>
              </svg>
              <span>Fornecedores</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link to="/perfil" class="nav-link" :class="{ active: isActive('/perfil') }">
              <svg class="nav-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
              </svg>
              <span>Perfil</span>
            </router-link>
          </li>

          
        </ul>
      </div>
    </nav>

    <!-- Seção Status -->
    <div class="status-section">
      <h3 class="status-title">Status</h3>
      <div class="status-filters">
        <button
          class="status-filter"
          :class="{ active: activeStatus === 'pendentes' }"
          @click="setActiveStatus('pendentes')"
        >
          <div class="status-indicator pending"></div>
          <span>Pendentes</span>
          <span class="status-count">{{ statusCounts.pendentes }}</span>
        </button>

        <button
          class="status-filter"
          :class="{ active: activeStatus === 'aprovados' }"
          @click="setActiveStatus('aprovados')"
        >
          <div class="status-indicator approved"></div>
          <span>Aprovados</span>
          <span class="status-count">{{ statusCounts.aprovados }}</span>
        </button>

        <button
          class="status-filter"
          :class="{ active: activeStatus === 'urgentes' }"
          @click="setActiveStatus('urgentes')"
        >
          <div class="status-indicator urgent"></div>
          <span>Urgentes</span>
          <span class="status-count">{{ statusCounts.urgentes }}</span>
        </button>
      </div>
    </div>

    <!-- Seção de Logout -->
    <div class="logout-section">
      <button class="logout-button" @click="handleLogout">
        <svg class="logout-icon" viewBox="0 0 24 24" width="20" height="20">
          <path fill="currentColor" d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.59L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"/>
        </svg>
        <span>Sair</span>
      </button>
    </div>

    <!-- Modal de Logout -->
    <LogoutModal
      :show="showLogoutModal"
      @confirm="confirmLogout"
      @cancel="cancelLogout"
    />
  </aside>
</template>

<script setup>
/**
 * Componente DashboardSidebar - Menu lateral da aplicação
 *
 * Funcionalidades:
 * - Navegação principal
 * - Filtros de status
 * - Botão de logout
 */

import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import LogoutModal from './LogoutModal.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const activeStatus = ref('pendentes')
const showLogoutModal = ref(false)

const statusCounts = ref({
  pendentes: 23,
  aprovados: 156,
  urgentes: 8
})

/**
 * Verifica se uma rota está ativa
 */
const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}

/**
 * Define o status ativo para filtros
 */
const setActiveStatus = (status) => {
  activeStatus.value = status
  // Emitir evento ou chamar função para filtrar dados
  console.log('Status ativo:', status)
}

/**
 * Realiza o logout do usuário
 */
const handleLogout = () => {
  showLogoutModal.value = true
}

/**
 * Confirma o logout após o usuário aceitar no modal
 */
const confirmLogout = async () => {
  try {
    // Realiza o logout
    authStore.logout()

    // Fecha o modal
    showLogoutModal.value = false

    // Redireciona para a página de login
    router.push('/login')

    console.log('Logout realizado com sucesso')
  } catch (error) {
    console.error('Erro ao fazer logout:', error)
    alert('Erro ao sair da aplicação. Tente novamente.')
  }
}

/**
 * Cancela o logout
 */
const cancelLogout = () => {
  showLogoutModal.value = false
}
</script>

<style scoped>
.sidebar {
  width: 280px;
  background: white;
  border-right: 1px solid #e0e6ed;
  height: calc(100vh - 70px);
  overflow-y: auto;
  position: fixed;
  left: 0;
  top: 70px;
  z-index: 100;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.05);
  padding-bottom: 80px; /* Espaço para o botão de logout */
}

/* Menu Principal */
.main-nav {
  padding: 24px 0;
}

.nav-section {
  margin-bottom: 32px;
}

.nav-title {
  font-family: Arial, sans-serif;
  font-size: 16px;
  font-weight: 600;
  color: #1F285F;
  margin: 0 0 16px 24px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.nav-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.nav-item {
  margin-bottom: 4px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  color: #6b7280;
  text-decoration: none;
  font-family: Arial, sans-serif;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  border-radius: 0;
  position: relative;
}

.nav-link:hover {
  background: #f8f9fa;
  color: #1F285F;
}

.nav-link.active {
  background: #EAF0FC;
  color: #1F285F;
  font-weight: 600;
}

.nav-link.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: #1F285F;
}

.nav-icon {
  flex-shrink: 0;
  color: inherit;
}

/* Seção Status */
.status-section {
  padding: 24px;
  border-top: 1px solid #e0e6ed;
  background: #f8f9fa;
  margin-top: auto;
}

.status-title {
  font-family: Arial, sans-serif;
  font-size: 16px;
  font-weight: 600;
  color: #1F285F;
  margin-bottom: 16px;
  margin-top: 0;
}

.status-filters {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.status-filter {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: white;
  border: 1px solid #e0e6ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: Arial, sans-serif;
  font-size: 14px;
  color: #374151;
  width: 100%;
  text-align: left;
}

.status-filter:hover {
  background: #f3f4f6;
  border-color: #d1d5db;
}

.status-filter.active {
  background: #EAF0FC;
  border-color: #1F285F;
  color: #1F285F;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-indicator.pending {
  background: #f59e0b;
}

.status-indicator.approved {
  background: #10b981;
}

.status-indicator.urgent {
  background: #ef4444;
}

.status-filter.active .status-indicator {
  background: #1F285F;
}

.status-count {
  margin-left: auto;
  background: #e5e7eb;
  color: #374151;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  min-width: 24px;
  text-align: center;
}

.status-filter.active .status-count {
  background: #1F285F;
  color: white;
}

/* Seção de Logout */
.logout-section {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
  background: white;
}

.logout-button {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 12px 16px;
  background: none;
  border: none;
  border-radius: 8px;
  color: #dc2626;
  font-family: Arial, sans-serif;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.logout-button:hover {
  background: #fef2f2;
  color: #b91c1c;
}

.logout-button:active {
  transform: translateY(1px);
}

.logout-icon {
  color: currentColor;
  transition: transform 0.2s ease;
}

.logout-button:hover .logout-icon {
  transform: translateX(2px);
}

/* Scrollbar customizada */
.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.sidebar::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.sidebar::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Responsividade */
@media (max-width: 1024px) {
  .sidebar {
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }

  .sidebar.open {
    transform: translateX(0);
  }
}
</style>
