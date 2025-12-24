<template>
  <!-- Overlay para mobile -->
  <div v-if="isMobileSidebarOpen" class="sidebar-overlay" @click="closeSidebar"></div>

  <aside class="sidebar" :class="{ 'mobile-open': isMobileSidebarOpen }">
    <!-- Botão Fechar (Mobile) -->
    <button class="close-sidebar-btn" @click="closeSidebar" aria-label="Fechar menu">
      <svg viewBox="0 0 24 24" width="24" height="24">
        <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
      </svg>
    </button>

    <!-- Menu Principal -->
    <nav class="main-nav">
      <div class="nav-section">
        <h3 class="nav-title">Menu Principal</h3>
        <ul class="nav-list">
          <li v-for="item in visibleMenuItems" :key="item.id" class="nav-item">
            <router-link
              :to="item.route"
              class="nav-link"
              :class="{ active: isActive(item.route) }"
              @click="handleNavClick"
              :title="item.description"
            >
              <svg class="nav-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" :d="getIconPath(item.icon)"/>
              </svg>
              <span>{{ item.label }}</span>
            </router-link>
          </li>
        </ul>
      </div>
    </nav>
  </aside>
</template>

<script setup>
/**
 * Componente DashboardSidebar - Menu lateral da aplicação
 *
 * Funcionalidades:
 * - Navegação principal com controle de acesso por roles
 * - Menu dinâmico filtrado baseado em permissões
 * - Suporte responsivo para mobile
 *
 * @version 3.0.0 - Adicionado sistema de permissões baseado em roles
 */

import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useMobileSidebar } from '@/composables/useMobileSidebar'
import { usePermissions } from '@/composables/usePermissions'
import { menuItems, getIconPath } from '@/config/menuConfig'

const route = useRoute()
const { isMobileSidebarOpen, closeSidebar } = useMobileSidebar()
const { filterMenuByRole } = usePermissions()

/**
 * Menu items visíveis para o usuário atual
 * Filtrado automaticamente baseado no role do usuário
 */
const visibleMenuItems = computed(() => {
  return filterMenuByRole(menuItems)
})

/**
 * Verifica se uma rota está ativa
 */
const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}

/**
 * Fecha sidebar no mobile após navegação
 */
const handleNavClick = () => {
  if (isMobileSidebarOpen.value) {
    closeSidebar()
  }
}
</script>

<style scoped>
/* Overlay para mobile */
.sidebar-overlay {
  display: none;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 999;
}

/* Botão fechar sidebar (mobile) */
.close-sidebar-btn {
  display: none;
  position: absolute;
  top: 16px;
  right: 16px;
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.2s;
  z-index: 1;
}

.close-sidebar-btn:hover {
  background: #f3f4f6;
  color: #1F285F;
}

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
  transition: transform 0.3s ease-in-out;
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
  .sidebar-overlay {
    display: block;
  }

  .close-sidebar-btn {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .sidebar {
    transform: translateX(-100%);
    z-index: 1000;
    top: 0;
    height: 100vh;
  }

  .sidebar.mobile-open {
    transform: translateX(0);
  }

  .main-nav {
    padding-top: 60px;
  }
}
</style>
