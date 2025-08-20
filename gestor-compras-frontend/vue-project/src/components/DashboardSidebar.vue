<template>
  <aside class="sidebar">
    <!-- Menu Principal -->
    <nav class="main-nav">
      <div class="nav-section">
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
            <router-link to="/cotacoes" class="nav-link" :class="{ active: isActive('/cotacoes') }">
              <svg class="nav-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
              </svg>
              <span>Cotações</span>
            </router-link>
          </li>
          
          <li class="nav-item">
            <router-link to="/notas-fiscais" class="nav-link" :class="{ active: isActive('/notas-fiscais') }">
              <svg class="nav-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20M8,12V14H16V12H8M8,16V18H13V16H8Z"/>
              </svg>
              <span>Notas Fiscais</span>
            </router-link>
          </li>
          
          <li class="nav-item">
            <router-link to="/relatorios" class="nav-link" :class="{ active: isActive('/relatorios') }">
              <svg class="nav-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M19,3H5C3.9,3 3,3.9 3,5V19C3,20.1 3.9,21 5,21H19C20.1,21 21,20.1 21,19V5C21,3.9 20.1,3 19,3M19,19H5V5H19V19M17,12H15V7H17V12M13,12H11V9H13V12M9,12H7V11H9V12Z"/>
              </svg>
              <span>Relatórios</span>
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
  </aside>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeStatus = ref('pendentes')

const statusCounts = ref({
  pendentes: 23,
  aprovados: 156,
  urgentes: 8
})

const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}

const setActiveStatus = (status) => {
  activeStatus.value = status
  // Emitir evento ou chamar função para filtrar dados
  console.log('Status ativo:', status)
}
</script>

<style scoped>
.sidebar {
  width: 280px;
  background: white;
  border-right: 1px solid #e0e6ed;
  height: 100vh;
  overflow-y: auto;
  position: fixed;
  left: 0;
  top: 70px;
  z-index: 100;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.05);
}

/* Menu Principal */
.main-nav {
  padding: 24px 0;
}

.nav-section {
  margin-bottom: 32px;
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
  background: linear-gradient(90deg, #1F285F 0%, rgba(31, 40, 95, 0.1) 100%);
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
  background: #1F285F;
  border-color: #1F285F;
  color: white;
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
  background: white;
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
  background: rgba(255, 255, 255, 0.2);
  color: white;
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
