<template>
  <div class="layout-container">
    <header class="layout-header">
      <div class="header-content">
        <h1 class="app-title">
          <router-link to="/dashboard" class="title-link">
            Gestor de Compras
          </router-link>
        </h1>
        <nav class="main-nav">
          <router-link to="/dashboard" class="nav-link">Dashboard</router-link>
          <div class="nav-dropdown">
            <button @click="toggleRelatoriosMenu" class="nav-link dropdown-button">
              Relatórios
              <span class="dropdown-arrow" :class="{ 'rotated': relatoriosMenuOpen }">▼</span>
            </button>
            <div v-if="relatoriosMenuOpen" class="dropdown-menu">
              <button @click="gerarRelatorioFornecedores" class="dropdown-item" :disabled="gerandoRelatorio">
                <span v-if="gerandoRelatorio" class="loading-spinner"></span>
                {{ gerandoRelatorio ? 'Gerando...' : 'Relatório de Fornecedores' }}
        x       </button>
            </div>
          </div>
          <!-- Adicione mais links de navegação aqui conforme necessário -->
        </nav>
        <div class="user-actions">
          <span class="welcome-text">Bem-vindo, {{ username }}!</span>
          <button @click="logout" class="logout-button">Sair</button>
        </div>
      </div>
    </header>

    <main class="layout-main">
      <div class="main-content">
        <slot />
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import relatorioService from '../services/relatorioService'

const router = useRouter()
const authStore = useAuthStore()

// Estados para o menu de relatórios
const relatoriosMenuOpen = ref(false)
const gerandoRelatorio = ref(false)

const username = computed(() => {
  return authStore.user?.name || authStore.user?.username || 'Usuário'
})

const logout = () => {
  authStore.logout()
  router.push('/login')
}

// Função para alternar o menu de relatórios
const toggleRelatoriosMenu = () => {
  relatoriosMenuOpen.value = !relatoriosMenuOpen.value
}

// Função para gerar relatório de fornecedores
const gerarRelatorioFornecedores = async () => {
  if (gerandoRelatorio.value) return

  try {
    gerandoRelatorio.value = true
    await relatorioService.gerarRelatorioFornecedores()
    relatoriosMenuOpen.value = false // Fecha o menu após gerar
  } catch (error) {
    console.error('Erro ao gerar relatório:', error)
    alert('Erro ao gerar relatório. Tente novamente.')
  } finally {
    gerandoRelatorio.value = false
  }
}

// Função para fechar menu quando clicar fora
const handleClickOutside = (event) => {
  const dropdown = event.target.closest('.nav-dropdown')
  if (!dropdown) {
    relatoriosMenuOpen.value = false
  }
}

// Adiciona listener para fechar menu ao clicar fora
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.layout-header {
  background-color: white;
  border-bottom: 1px solid #e0e0e0;
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
}

.app-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.title-link {
  color: #333;
  text-decoration: none;
  transition: color 0.3s;
}

.title-link:hover {
  color: #007bff;
}

.main-nav {
  display: flex;
  gap: 20px;
  align-items: center;
}

.nav-dropdown {
  position: relative;
}

.dropdown-button {
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: inherit;
}

.dropdown-arrow {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.dropdown-arrow.rotated {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  min-width: 200px;
  margin-top: 5px;
}

.dropdown-item {
  width: 100%;
  padding: 12px 16px;
  border: none;
  background: none;
  text-align: left;
  cursor: pointer;
  transition: background-color 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.dropdown-item:hover:not(:disabled) {
  background-color: #f8f9fa;
}

.dropdown-item:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  width: 12px;
  height: 12px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.nav-link {
  color: #666;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 4px;
  transition: background-color 0.3s, color 0.3s;
}

.nav-link:hover,
.nav-link.router-link-active {
  background-color: #007bff;
  color: white;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.welcome-text {
  color: #666;
  font-size: 14px;
}

.logout-button {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.logout-button:hover {
  background-color: #c82333;
}

.layout-main {
  padding: 20px;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
