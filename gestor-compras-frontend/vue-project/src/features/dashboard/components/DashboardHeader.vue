<template>
  <header class="dashboard-header">
    <div class="header-content">
      <div class="left-section">
        <!-- Botão Menu Mobile -->
        <button class="menu-toggle" @click="toggleMobileSidebar" aria-label="Abrir menu">
          <svg viewBox="0 0 24 24" width="24" height="24">
            <path fill="currentColor" d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/>
          </svg>
        </button>

        <!-- Botão Toggle Sidebar (Desktop) -->
        <button class="sidebar-toggle desktop-only" @click="toggleCollapse" aria-label="Alternar menu">
          <svg viewBox="0 0 24 24" width="24" height="24">
            <path fill="currentColor" d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/>
          </svg>
        </button>

        <!-- Logo -->
        <div class="logo-section">
          <div class="logo">
            <span class="logo-text">Gestor de Compras</span>
          </div>
        </div>
      </div>

      <!-- Barra de Busca Global -->
      <div class="search-section">
        <GlobalSearch />
      </div>

      <!-- Ícones e Usuário -->
      <div class="user-section">
        <!-- Avatar e Info do Usuário -->
        <div class="user-info" @click="toggleUserMenu" v-click-outside="closeUserMenu">
          <div class="user-avatar">
            <img :src="userAvatar" :alt="userName" loading="lazy" width="40" height="40" />
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
                <path fill="currentColor" d="M19.14,12.94c0.04-0.3,0.06-0.61,0.06-0.94c0-0.32-0.02-0.64-0.07-0.94l2.03-1.58c0.18-0.14,0.23-0.41,0.12-0.61 l-1.92-3.32c-0.12-0.22-0.37-0.29-0.59-0.22l-2.39,0.96c-0.5-0.38-1.03-0.7-1.62-0.94L14.4,2.81c-0.04-0.24-0.24-0.41-0.48-0.41 h-3.84c-0.24,0-0.43,0.17-0.47,0.41L9.25,5.35C8.66,5.59,8.12,5.92,7.63,6.29L5.24,5.33c-0.22-0.08-0.47,0-0.59,0.22L2.74,8.87 C2.62,9.08,2.66,9.34,2.86,9.48l2.03,1.58C4.84,11.36,4.82,11.69,4.82,12s0.02,0.64,0.07,0.94l-2.03,1.58 c-0.18,0.14-0.23,0.41-0.12,0.61l1.92,3.32c0.12,0.22,0.37,0.29,0.59,0.22l2.39-0.96c0.5,0.38,1.03-0.7-1.62-0.94l0.36,2.54 c0.05,0.24,0.24,0.41,0.48,0.41h3.84c-0.24,0,0.44-0.17,0.47-0.41l0.36-2.54c0.59-0.24,1.13-0.56,1.62-0.94l2.39,0.96 c0.22,0.08,0.47,0,0.59-0.22l1.92-3.32c0.12-0.22,0.07-0.47-0.12-0.61L19.14,12.94z M12,15.6c-1.98,0-3.6-1.62-3.6-3.6 s1.62-3.6,3.6-3.6s3.6,1.62,3.6,3.6S13.98,15.6,12,15.6z"/>
              </svg>
              <span>Configurações</span>
            </div>

            <div class="dropdown-item" @click="openAbout">
              <svg class="item-icon" viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M11,9H13V7H11M12,20C7.59,20 4,16.41 4,12C4,7.59 7.59,4 12,4C16.41,4 20,7.59 20,12C20,16.41 16.41,20 12,20M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M11,17H13V11H11V17Z"/>
              </svg>
              <span>Sobre</span>
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
import { useAuthStore } from '@/stores/auth'
import { useToast } from '@/composables/useToast'
import { getUserRole } from '@/utils/genderUtils'
import { useMobileSidebar } from '@/composables/useMobileSidebar'
import { useSidebar } from '@/composables/useSidebar'
import LogoutModal from '@/components/ui/modals/LogoutModal.vue'
import GlobalSearch from '@/components/ui/GlobalSearch.vue'
import logger from '@/utils/logger.js'

const router = useRouter()
const authStore = useAuthStore()
const { error: toastError } = useToast()
const isUserMenuOpen = ref(false)
const showLogoutModal = ref(false)

// Controle do sidebar mobile
const { toggleSidebar } = useMobileSidebar()
// Controle do sidebar desktop (retrátil)
const { toggleCollapse, setCollapsed } = useSidebar()

const userName = computed(() => authStore.user?.nome || authStore.user?.email || 'Usuário')

const userRole = computed(() => {
  return getUserRole(authStore.user)
})
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
 * Ao clicar, o menu lateral se retrai automaticamente
 */
const viewProfile = () => {
  isUserMenuOpen.value = false
  setCollapsed(true) // Retrai o menu lateral
  router.push('/perfil')
}

/**
 * Abre a página de configurações
 */
const openSettings = () => {
  isUserMenuOpen.value = false
  router.push('/configuracoes')
}

/**
 * Abre a página Sobre
 */
const openAbout = () => {
  isUserMenuOpen.value = false
  router.push('/sobre')
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

  } catch (error) {
    logger.error('Erro ao fazer logout:', error)
    toastError('Erro ao sair da aplicação. Tente novamente.')
  }
}

/**
 * Cancela o logout
 */
const cancelLogout = () => {
  showLogoutModal.value = false
}

/**
 * Toggle do sidebar mobile
 */
const toggleMobileSidebar = () => {
  toggleSidebar()
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

.left-section {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

/* Menu Toggle (Hamburguer) */
.menu-toggle {
  display: none;
  background: none;
  border: none;
  color: #1F285F;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.2s;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
}

.menu-toggle:hover {
  background: #f3f4f6;
}

.menu-toggle:active {
  background: #e5e7eb;
}

/* Sidebar Toggle (Desktop) */
.sidebar-toggle {
  display: inline-flex;
  background: none;
  border: none;
  color: #1F285F;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.2s;
  flex-shrink: 0;
  margin-right: 16px;
  align-items: center;
  justify-content: center;
}

.sidebar-toggle:hover {
  background: #f3f4f6;
}

/* Logo Section */
.logo-section {
  flex: 0 0 auto;
  min-width: 0;
  display: flex;
  align-items: center;
  height: 100%;
}

.logo-text {
  font-family: inherit;
  font-size: 20px;
  font-weight: bold;
  color: #1F285F;
  line-height: 1;
}

/* Search Section */
.search-section {
  flex: 1;
  max-width: 600px;
  margin: 0 40px;
  min-width: 0; /* Permite que o flex item encolha */
}

/* User Section */
.user-section {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  gap: 20px;
  min-width: 0; /* Permite que o flex item encolha */
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
  font-family: inherit;
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
  font-family: inherit;
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
  font-family: inherit;
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

  .sidebar-toggle {
    display: none;
  }
}

@media (max-width: 1024px) {
  .menu-toggle {
    display: flex;
    align-items: center;
    justify-content: center;
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
    gap: 12px;
  }

  .logo-section {
    flex: 1;
    display: flex;
    justify-content: flex-start;
    min-width: auto;
  }

  .logo-text {
    font-size: 18px;
  }

  .user-section {
    gap: 12px;
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
