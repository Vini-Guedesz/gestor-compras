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
          <li class="nav-item">
            <router-link to="/dashboard" class="nav-link" :class="{ active: isActive('/dashboard') }" @click="handleNavClick">
              <svg class="nav-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"/>
              </svg>
              <span>Dashboard</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link to="/pedidos" class="nav-link" :class="{ active: isActive('/pedidos') }" @click="handleNavClick">
              <svg class="nav-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M7 4V2C7 1.45 7.45 1 8 1H16C16.55 1 17 1.45 17 2V4H20C20.55 4 21 4.45 21 5S20.55 6 20 6H19V19C19 20.1 18.1 21 17 21H7C5.9 21 5 20.1 5 19V6H4C3.45 6 3 5.55 3 5S3.45 4 4 4H7ZM9 3V4H15V3H9ZM7 6V19H17V6H7Z"/>
              </svg>
              <span>Pedidos de Compra</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link to="/cotacoes" class="nav-link" :class="{ active: isActive('/cotacoes') }" @click="handleNavClick">
              <svg class="nav-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              <span>Cotações</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link to="/fornecedores" class="nav-link" :class="{ active: isActive('/fornecedores') }" @click="handleNavClick">
              <svg class="nav-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M16 4c0-1.11.89-2 2-2s2 .89 2 2-.89 2-2 2-2-.89-2-2zm4 18v-6h2.5l-2.54-7.63A2 2 0 0 0 18.04 7H16c-.8 0-1.54.37-2.01.99L12 10l2.01-2.01C14.54 7.37 15.2 7 16 7h2.04c1.23 0 2.18 1.24 1.92 2.63l2.54 7.63H20v6h-4z"/>
              </svg>
              <span>Fornecedores</span>
            </router-link>
          </li>

          <li class="nav-item">
            <router-link to="/usuarios" class="nav-link" :class="{ active: isActive('/usuarios') }" @click="handleNavClick">
              <svg class="nav-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
              </svg>
              <span>Usuários</span>
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
 * - Navegação principal
 * - Filtros de status
 */

import { useRoute } from 'vue-router'
import { useMobileSidebar } from '@/composables/useMobileSidebar'

const route = useRoute()
const { isMobileSidebarOpen, closeSidebar } = useMobileSidebar()

/**
 * Verifica se uma rota está ativa
 */
const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}

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
@media (max-width: 768px) {
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
