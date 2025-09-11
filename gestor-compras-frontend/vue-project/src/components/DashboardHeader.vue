<template>
  <header class="dashboard-header">
    <div class="header-content">
      <!-- Logo -->
      <div class="logo-section">
        <div class="logo">
          <span class="logo-text">Gestor Compras</span>
        </div>
      </div>

      <!-- Barra de Busca Central -->
      <div class="search-section">
        <div class="search-container">
          <svg class="search-icon" viewBox="0 0 24 24" width="20" height="20">
            <path fill="currentColor" d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
          </svg>
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Pesquisar pedidos e fornecedores..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
        </div>
      </div>

      <!-- Ícones e Usuário -->
      <div class="user-section">
        <!-- Notificações -->
        <div class="notification-icon" @click="toggleNotifications">
          <svg viewBox="0 0 24 24" width="24" height="24">
            <path fill="currentColor" d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z"/>
          </svg>
          <span v-if="notificationCount" class="notification-badge">{{ notificationCount }}</span>
        </div>

        <!-- Relatórios -->
        <div class="reports-dropdown" v-click-outside="closeReportsMenu">
          <div class="reports-icon" @click="toggleReportsMenu" :class="{ active: isReportsMenuOpen }">
            <svg viewBox="0 0 24 24" width="24" height="24">
              <path fill="currentColor" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
            </svg>
          </div>

          <!-- Dropdown Menu de Relatórios -->
          <div v-if="isReportsMenuOpen" class="reports-dropdown-menu">
            <div class="dropdown-item" @click="gerarRelatorioFornecedores" :class="{ disabled: gerandoRelatorio }">
              <svg class="item-icon" viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M16 4c0-1.11.89-2 2-2s2 .89 2 2-.89 2-2 2-2-.89-2-2zm4 18v-6h2.5l-2.54-7.63A2 2 0 0 0 18.04 7H16c-.8 0-1.54.37-2.01.99L12 10l2.01-2.01C14.54 7.37 15.2 7 16 7h2.04c1.23 0 2.18 1.24 1.92 2.63l2.54 7.63H20v6h-4z"/>
              </svg>
              <span v-if="gerandoRelatorio" class="loading-content">
                <span class="loading-spinner"></span>
                Gerando...
              </span>
              <span v-else>Relatório de Fornecedores</span>
            </div>
          </div>
        </div>

        <!-- Configurações -->
        <div class="settings-icon" @click="openSettings">
          <svg viewBox="0 0 24 24" width="24" height="24">
            <path fill="currentColor" d="M19.14,12.94c0.04-0.3,0.06-0.61,0.06-0.94c0-0.32-0.02-0.64-0.07-0.94l2.03-1.58c0.18-0.14,0.23-0.41,0.12-0.61 l-1.92-3.32c-0.12-0.22-0.37-0.29-0.59-0.22l-2.39,0.96c-0.5-0.38-1.03-0.7-1.62-0.94L14.4,2.81c-0.04-0.24-0.24-0.41-0.48-0.41 h-3.84c-0.24,0-0.43,0.17-0.47,0.41L9.25,5.35C8.66,5.59,8.12,5.92,7.63,6.29L5.24,5.33c-0.22-0.08-0.47,0-0.59,0.22L2.74,8.87 C2.62,9.08,2.66,9.34,2.86,9.48l2.03,1.58C4.84,11.36,4.82,11.69,4.82,12s0.02,0.64,0.07,0.94l-2.03,1.58 c-0.18,0.14-0.23,0.41-0.12,0.61l1.92,3.32c0.12,0.22,0.37,0.29,0.59,0.22l2.39-0.96c0.5,0.38,1.03,0.7,1.62,0.94l0.36,2.54 c0.05,0.24,0.24,0.41,0.48,0.41h3.84c0.24,0,0.44-0.17,0.47-0.41l0.36-2.54c0.59-0.24,1.13-0.56,1.62-0.94l2.39,0.96 c0.22,0.08,0.47,0,0.59-0.22l1.92-3.32c0.12-0.22,0.07-0.47-0.12-0.61L19.14,12.94z M12,15.6c-1.98,0-3.6-1.62-3.6-3.6 s1.62-3.6,3.6-3.6s3.6,1.62,3.6,3.6S13.98,15.6,12,15.6z"/>
          </svg>
        </div>

        <!-- Avatar e Info do Usuário -->
        <div class="user-info" @click="toggleUserMenu" v-click-outside="closeUserMenu">
          <div class="user-avatar">
            <img :src="userAvatar" :alt="userName" />
          </div>
          <div class="user-details">
            <span class="user-name">{{ userName }}</span>
            <span class="user-role">{{ userRole }}</span>
          </div>
          <svg class="dropdown-arrow" viewBox="0 0 24 24" width="16" height="16" :class="{ 'rotated': isUserMenuOpen }">
            <path fill="currentColor" d="M7 10l5 5 5-5z"/>
          </svg>

          <!-- Dropdown Menu do Usuário -->
          <div v-if="isUserMenuOpen" class="user-dropdown">
            <div class="dropdown-item" @click="viewProfile">
              <svg class="item-icon" viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
              </svg>
              <span>Meu Perfil</span>
            </div>

            <div class="dropdown-item" @click="openSettings">
              <svg class="item-icon" viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M19.14,12.94c0.04-0.3,0.06-0.61,0.06-0.94c0-0.32-0.02-0.64-0.07-0.94l2.03-1.58c0.18-0.14,0.23-0.41,0.12-0.61 l-1.92-3.32c-0.12-0.22-0.37-0.29-0.59-0.22l-2.39,0.96c-0.5-0.38-1.03-0.7-1.62-0.94L14.4,2.81c-0.04-0.24-0.24-0.41-0.48-0.41 h-3.84c-0.24,0-0.43,0.17-0.47,0.41L9.25,5.35C8.66,5.59,8.12,5.92,7.63,6.29L5.24,5.33c-0.22-0.08-0.47,0-0.59,0.22L2.74,8.87 C2.62,9.08,2.66,9.34,2.86,9.48l2.03,1.58C4.84,11.36,4.82,11.69,4.82,12s0.02,0.64,0.07,0.94l-2.03,1.58 c-0.18,0.14-0.23,0.41-0.12,0.61l1.92,3.32c0.12,0.22,0.37,0.29,0.59,0.22l2.39-0.96c0.5,0.38,1.03,0.7,1.62,0.94l0.36,2.54 c0.05,0.24,0.24,0.41,0.48,0.41h3.84c0.24,0,0.44-0.17,0.47-0.41l0.36-2.54c0.59-0.24,1.13-0.56,1.62-0.94l2.39,0.96 c0.22,0.08,0.47,0,0.59-0.22l1.92-3.32c0.12-0.22,0.07-0.47-0.12-0.61L19.14,12.94z M12,15.6c-1.98,0-3.6-1.62-3.6-3.6 s1.62-3.6,3.6-3.6s3.6,1.62,3.6,3.6S13.98,15.6,12,15.6z"/>
              </svg>
              <span>Configurações</span>
            </div>

            <div class="dropdown-divider"></div>

            <div class="dropdown-item logout-item" @click="handleLogout">
              <svg class="item-icon" viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.59L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"/>
              </svg>
              <span>Sair</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de Logout -->
    <LogoutModal
      :show="showLogoutModal"
      @confirm="confirmLogout"
      @cancel="cancelLogout"
    />
  </header>
</template>

<script setup>
/**
 * Componente DashboardHeader - Cabeçalho da aplicação
 *
 * Funcionalidades:
 * - Exibição da logo
 * - Barra de pesquisa
 * - Notificações
 * - Menu do usuário com logout
 * - Configurações
 */

import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import LogoutModal from './LogoutModal.vue'
import relatorioService from '../services/relatorioService'

const router = useRouter()
const authStore = useAuthStore()
const searchQuery = ref('')
const notificationCount = ref(3)
const isUserMenuOpen = ref(false)
const showLogoutModal = ref(false)

// Estados para o menu de relatórios
const isReportsMenuOpen = ref(false)
const gerandoRelatorio = ref(false)

const userName = computed(() => authStore.user?.name || 'Ana Silva')
const userRole = computed(() => authStore.user?.role || 'Gestora de Compras')
const userAvatar = computed(() => {
  // Placeholder para avatar - pode ser substituído por uma URL real
  return `data:image/svg+xml;base64,${btoa(`
    <svg width="40" height="40" viewBox="0 0 40 40" xmlns="http://www.w3.org/2000/svg">
      <circle cx="20" cy="20" r="20" fill="#1F285F"/>
      <circle cx="20" cy="16" r="6" fill="white"/>
      <path d="M20 24c-6 0-11 3-11 7v3h22v-3c0-4-5-7-11-7z" fill="white"/>
    </svg>
  `)}`
})

/**
 * Manipula a busca quando o usuário pressiona Enter
 */
const handleSearch = () => {
  console.log('Pesquisando:', searchQuery.value)
  // TODO: Implementar lógica de busca
}

/**
 * Abre/fecha o painel de notificações
 */
const toggleNotifications = () => {
  console.log('Abrir notificações')
  // TODO: Implementar painel de notificações
}

/**
 * Abre as configurações da aplicação
 */
const openSettings = () => {
  console.log('Abrir configurações')
  isUserMenuOpen.value = false
  // TODO: Implementar configurações
}

/**
 * Abre/fecha o menu dropdown do usuário
 */
const toggleUserMenu = () => {
  isUserMenuOpen.value = !isUserMenuOpen.value
}

/**
 * Fecha o menu do usuário
 */
const closeUserMenu = () => {
  isUserMenuOpen.value = false
}

/**
 * Abre a página de perfil do usuário
 */
const viewProfile = () => {
  isUserMenuOpen.value = false
  router.push('/perfil')
}

/**
 * Abre/fecha o menu de relatórios
 */
const toggleReportsMenu = () => {
  isReportsMenuOpen.value = !isReportsMenuOpen.value
}

/**
 * Fecha o menu de relatórios
 */
const closeReportsMenu = () => {
  isReportsMenuOpen.value = false
}

/**
 * Gera relatório de fornecedores
 */
const gerarRelatorioFornecedores = async () => {
  if (gerandoRelatorio.value) return

  try {
    gerandoRelatorio.value = true
    await relatorioService.gerarRelatorioFornecedores()
    isReportsMenuOpen.value = false // Fecha o menu após gerar
  } catch (error) {
    console.error('Erro ao gerar relatório:', error)
    alert('Erro ao gerar relatório. Tente novamente.')
  } finally {
    gerandoRelatorio.value = false
  }
}

/**
 * Realiza o logout do usuário
 *
 * Abre o modal de confirmação em vez de usar alert()
 */
const handleLogout = () => {
  isUserMenuOpen.value = false
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

// Diretiva customizada para fechar menu ao clicar fora
const vClickOutside = {
  beforeMount(el, binding) {
    el.clickOutsideEvent = function(event) {
      if (!(el === event.target || el.contains(event.target))) {
        binding.value()
      }
    }
    document.addEventListener('click', el.clickOutsideEvent)
  },
  unmounted(el) {
    document.removeEventListener('click', el.clickOutsideEvent)
  }
}
</script>

<style scoped>
.dashboard-header {
  background: white;
  border-bottom: 1px solid #e0e6ed;
  height: 70px;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 24px;
  max-width: 100%;
  min-width: 0; /* Permite que o container encolha */
}

/* Logo Section */
.logo-section {
  flex: 0 0 auto;
  min-width: 200px;
}

.logo-text {
  font-family: Arial, sans-serif;
  font-size: 20px;
  font-weight: bold;
  color: #1F285F;
}

/* Search Section */
.search-section {
  flex: 1;
  max-width: 500px;
  margin: 0 40px;
  min-width: 0; /* Permite que o flex item encolha */
}

.search-container {
  position: relative;
  width: 100%;
}

.search-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #6b7280;
  z-index: 1;
}

.search-input {
  width: 100%;
  height: 44px;
  padding: 0 20px 0 50px;
  border: 1px solid #e0e6ed;
  border-radius: 25px;
  font-family: Arial, sans-serif;
  font-size: 14px;
  background: #f8f9fa;
  transition: all 0.2s ease;
}

.search-input:focus {
  outline: none;
  border-color: #1F285F;
  background: white;
  box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.1);
}

.search-input::placeholder {
  color: #9ca3af;
}

/* User Section */
.user-section {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  gap: 20px;
  min-width: 0; /* Permite que o flex item encolha */
}

.notification-icon,
.settings-icon,
.reports-icon {
  position: relative;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #f8f9fa;
  cursor: pointer;
  transition: background-color 0.2s ease;
  color: #6b7280;
}

.notification-icon:hover,
.settings-icon:hover,
.reports-icon:hover,
.reports-icon.active {
  background: #e5e7eb;
  color: #1F285F;
}

/* Reports Dropdown */
.reports-dropdown {
  position: relative;
}

.reports-dropdown-menu {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  background: white;
  border: 1px solid #e0e6ed;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  min-width: 220px;
  padding: 8px 0;
}

.reports-dropdown-menu .dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  font-size: 14px;
  color: #374151;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
}

.reports-dropdown-menu .dropdown-item:hover:not(.disabled) {
  background: #f8f9fa;
}

.reports-dropdown-menu .dropdown-item.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.reports-dropdown-menu .item-icon {
  color: #6b7280;
  flex-shrink: 0;
}

.loading-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.loading-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #1F285F;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.notification-badge {
  position: absolute;
  top: -2px;
  right: -2px;
  background: #ef4444;
  color: white;
  font-size: 10px;
  font-weight: bold;
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 25px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.user-info:hover {
  background: #f8f9fa;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #e0e6ed;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.user-name {
  font-family: Arial, sans-serif;
  font-size: 14px;
  font-weight: 600;
  color: #1F285F;
  line-height: 1.2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
}

.user-role {
  font-family: Arial, sans-serif;
  font-size: 12px;
  color: #6b7280;
  line-height: 1.2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
}

.dropdown-arrow {
  color: #6b7280;
  transition: transform 0.2s ease;
}

.dropdown-arrow.rotated {
  transform: rotate(180deg);
}

.user-info:hover .dropdown-arrow {
  transform: rotate(180deg);
}

/* User Dropdown Menu */
.user-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  min-width: 200px;
  z-index: 1000;
  overflow: hidden;
  animation: dropdownFadeIn 0.15s ease-out;
}

@keyframes dropdownFadeIn {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  color: #374151;
  font-size: 14px;
  font-family: Arial, sans-serif;
  cursor: pointer;
  transition: background-color 0.15s ease;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
}

.dropdown-item:hover {
  background-color: #f9fafb;
}

.dropdown-item.logout-item {
  color: #dc2626;
}

.dropdown-item.logout-item:hover {
  background-color: #fef2f2;
}

.item-icon {
  color: currentColor;
  flex-shrink: 0;
}

.dropdown-divider {
  height: 1px;
  background-color: #e5e7eb;
  margin: 4px 0;
}

/* Responsividade */
@media (max-width: 1024px) {
  .search-section {
    margin: 0 20px;
    max-width: 400px;
  }

  .header-content {
    padding: 0 16px;
  }
}

@media (max-width: 768px) {
  .search-section {
    display: none;
  }

  .user-details {
    display: none;
  }

  .header-content {
    padding: 0 16px;
  }

  .logo-section {
    min-width: auto;
  }

  .user-section {
    gap: 16px;
  }
}

@media (max-width: 480px) {
  .header-content {
    padding: 0 12px;
  }

  .user-section {
    gap: 12px;
  }

  .notification-icon,
  .settings-icon {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>
