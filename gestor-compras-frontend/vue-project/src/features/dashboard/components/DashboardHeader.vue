<template>
  <header class="dashboard-header">
    <div class="header-content">
      <div class="left-section">
        <button class="menu-toggle icon-button" @click="toggleMobileSidebar" aria-label="Abrir menu">
          <svg viewBox="0 0 24 24" width="24" height="24">
            <path fill="currentColor" d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/>
          </svg>
        </button>

        <button class="sidebar-toggle desktop-only icon-button" @click="toggleCollapse" aria-label="Alternar menu">
          <svg viewBox="0 0 24 24" width="24" height="24">
            <path fill="currentColor" d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/>
          </svg>
        </button>

        <div class="logo-section">
          <div class="brand-mark" aria-hidden="true"></div>
          <div class="logo">
            <span class="logo-text">Gestor de Compras</span>
            <span class="logo-subtext">Painel principal</span>
          </div>
        </div>
      </div>

      <div class="search-section">
        <div class="search-shell">
          <GlobalSearch />
        </div>
      </div>

      <div class="user-section">
        <div
          class="user-info"
          :class="{ open: isUserMenuOpen }"
          @click="toggleUserMenu"
          v-click-outside="closeUserMenu"
        >
          <div class="user-avatar">
            <img :src="userAvatar" :alt="userName" loading="lazy" width="40" height="40" />
            <span class="avatar-status" aria-hidden="true"></span>
          </div>

          <div class="user-details">
            <span class="user-name">{{ userName }}</span>
            <span class="user-role">{{ userRole }}</span>
          </div>

          <svg class="dropdown-arrow" viewBox="0 0 24 24" width="16" height="16" :class="{ rotated: isUserMenuOpen }">
            <path fill="currentColor" d="M7 10l5 5 5-5z"/>
          </svg>

          <div v-if="isUserMenuOpen" class="user-dropdown" @click.stop>
            <div class="dropdown-user-summary">
              <strong>{{ userName }}</strong>
              <span>{{ userRole }}</span>
            </div>

            <div class="dropdown-item" @click="viewProfile">
              <svg class="item-icon" viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
              </svg>
              <span>Meu Perfil</span>
            </div>

            <div class="dropdown-item" @click="openSettings">
              <svg class="item-icon" viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M19.14 12.94c.04-.31.06-.63.06-.94s-.02-.63-.07-.94l2.03-1.58a.49.49 0 0 0 .12-.61l-1.92-3.32a.5.5 0 0 0-.59-.22l-2.39.96a7.5 7.5 0 0 0-1.62-.94l-.36-2.54a.5.5 0 0 0-.48-.41h-3.84a.5.5 0 0 0-.47.41l-.36 2.54a7.35 7.35 0 0 0-1.62.94l-2.39-.96a.5.5 0 0 0-.59.22L2.74 8.87a.5.5 0 0 0 .12.61l2.03 1.58c-.04.31-.07.63-.07.94s.02.63.07.94l-2.03 1.58a.5.5 0 0 0-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.49.38 1.03.7 1.62.94l.36 2.54c.04.24.23.41.47.41h3.84c.24 0 .44-.17.48-.41l.36-2.54c.59-.24 1.12-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32a.5.5 0 0 0-.12-.61l-2.02-1.58zM12 15.6A3.6 3.6 0 1 1 12 8.4a3.6 3.6 0 0 1 0 7.2z"/>
              </svg>
              <span>Configurações</span>
            </div>

            <div class="dropdown-item" @click="openAbout">
              <svg class="item-icon" viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M11 9h2V7h-2m1 13C7.59 20 4 16.41 4 12S7.59 4 12 4s8 3.59 8 8-3.59 8-8 8m0-18a10 10 0 0 0-10 10 10 10 0 0 0 10 10 10 10 0 0 0 10-10A10 10 0 0 0 12 2m-1 15h2v-6h-2v6z"/>
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

    <LogoutModal :show="showLogoutModal" @confirm="confirmLogout" @cancel="cancelLogout" />
  </header>
</template>

<script setup>
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

const { toggleSidebar } = useMobileSidebar()
const { toggleCollapse, setCollapsed } = useSidebar()

const userName = computed(() => authStore.user?.nome || authStore.user?.email || 'Usuário')
const userRole = computed(() => getUserRole(authStore.user))
const userAvatar = computed(() => {
  return `data:image/svg+xml;base64,${btoa(`
    <svg width="40" height="40" viewBox="0 0 40 40" xmlns="http://www.w3.org/2000/svg">
      <circle cx="20" cy="20" r="20" fill="#1F285F"/>
      <circle cx="20" cy="16" r="6" fill="white"/>
      <path d="M20 24c-6 0-11 3-11 7v3h22v-3c0-4-5-7-11-7z" fill="white"/>
    </svg>
  `)}`
})

const toggleUserMenu = () => {
  isUserMenuOpen.value = !isUserMenuOpen.value
}

const closeUserMenu = () => {
  isUserMenuOpen.value = false
}

const viewProfile = () => {
  isUserMenuOpen.value = false
  setCollapsed(true)
  router.push('/perfil')
}

const openSettings = () => {
  isUserMenuOpen.value = false
  router.push('/configuracoes')
}

const openAbout = () => {
  isUserMenuOpen.value = false
  router.push('/sobre')
}

const handleLogout = () => {
  isUserMenuOpen.value = false
  showLogoutModal.value = true
}

const confirmLogout = async () => {
  try {
    showLogoutModal.value = false
    authStore.logout()
    await router.replace('/login')
  } catch (error) {
    logger.error('Erro ao fazer logout:', error)
    toastError('Erro ao sair da aplicação. Tente novamente.')
  }
}

const cancelLogout = () => {
  showLogoutModal.value = false
}

const toggleMobileSidebar = () => {
  toggleSidebar()
}

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
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid #e2e8f0;
  height: 72px;
  position: sticky;
  top: 0;
  z-index: 1000;
  backdrop-filter: blur(10px);
  box-shadow: 0 10px 25px rgba(15, 23, 42, 0.05);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
  max-width: 100%;
  min-width: 0;
  gap: 14px;
}

.left-section {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.icon-button {
  border: 1px solid #e2e8f0;
  color: #1f285f;
  background: linear-gradient(180deg, #ffffff, #f8fafc);
  cursor: pointer;
  padding: 8px;
  border-radius: 12px;
  transition: all 0.2s ease;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
}

.icon-button:hover {
  background: #eef2ff;
  border-color: #c7d2fe;
  transform: translateY(-1px);
}

.icon-button:active {
  transform: translateY(0);
}

.menu-toggle {
  display: none;
}

.sidebar-toggle {
  display: inline-flex;
  margin-right: 4px;
}

.logo-section {
  flex: 0 0 auto;
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-mark {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(145deg, #1f285f 0%, #3f519f 100%);
  box-shadow: 0 10px 18px rgba(31, 40, 95, 0.28);
  position: relative;
}

.brand-mark::before {
  content: '';
  position: absolute;
  inset: 8px;
  border-radius: 6px;
  border: 2px solid rgba(255, 255, 255, 0.8);
}

.logo {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.logo-text {
  font-size: 1.05rem;
  font-weight: 700;
  color: #1f285f;
  line-height: 1.1;
  white-space: nowrap;
}

.logo-subtext {
  color: #64748b;
  font-size: 0.72rem;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  font-weight: 600;
  line-height: 1.1;
}

.search-section {
  flex: 1;
  max-width: 620px;
  margin: 0 22px;
  min-width: 0;
}

.search-shell {
  background: linear-gradient(180deg, #ffffff, #f8fafc);
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 4px;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.85);
}

.user-section {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  min-width: 0;
}

.user-info {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 7px 12px 7px 8px;
  border-radius: 14px;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.2s ease;
}

.user-info:hover,
.user-info.open {
  background: #f8fafc;
  border-color: #e2e8f0;
}

.user-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #dbeafe;
  position: relative;
}

.avatar-status {
  position: absolute;
  right: 0;
  bottom: 2px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #22c55e;
  border: 2px solid #fff;
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
  min-width: 0;
}

.user-name {
  font-size: 0.87rem;
  font-weight: 700;
  color: #1f285f;
  line-height: 1.15;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 140px;
}

.user-role {
  font-size: 0.75rem;
  color: #64748b;
  line-height: 1.15;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 140px;
}

.dropdown-arrow {
  color: #64748b;
  transition: transform 0.2s ease;
}

.dropdown-arrow.rotated {
  transform: rotate(180deg);
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  box-shadow: 0 18px 35px rgba(15, 23, 42, 0.14);
  min-width: 240px;
  z-index: 1000;
  overflow: hidden;
  animation: dropdownFadeIn 0.15s ease-out;
}

.dropdown-user-summary {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 12px 16px;
  background: linear-gradient(180deg, #f8fafc, #ffffff);
  border-bottom: 1px solid #eef2f7;
}

.dropdown-user-summary strong {
  font-size: 0.86rem;
  color: #1f285f;
  line-height: 1.2;
}

.dropdown-user-summary span {
  font-size: 0.75rem;
  color: #64748b;
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
  padding: 11px 16px;
  color: #334155;
  font-size: 0.86rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s ease;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
}

.dropdown-item:hover {
  background-color: #f8fafc;
  color: #1f285f;
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

@media (max-width: 1024px) {
  .search-section {
    margin: 0 12px;
    max-width: 430px;
  }

  .header-content {
    padding: 0 14px;
  }

  .sidebar-toggle {
    display: none;
  }

  .menu-toggle {
    display: inline-flex;
  }

  .logo-subtext {
    display: none;
  }
}

@media (max-width: 768px) {
  .search-section {
    display: none;
  }

  .user-details,
  .dropdown-arrow {
    display: none;
  }

  .header-content {
    padding: 0 12px;
    gap: 10px;
  }

  .logo-text {
    font-size: 0.98rem;
  }

  .brand-mark {
    width: 30px;
    height: 30px;
  }

  .user-info {
    padding: 4px;
    border-radius: 10px;
  }

  .user-dropdown {
    min-width: 210px;
  }
}

@media (max-width: 480px) {
  .logo-text {
    font-size: 0.9rem;
  }

  .brand-mark {
    width: 28px;
    height: 28px;
  }

  .header-content {
    padding: 0 10px;
  }
}
</style>


