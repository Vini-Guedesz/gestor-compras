<template>
  <div class="layout-container">
    <!-- Skip to main content link (acessibilidade) -->
    <a href="#main-content" class="skip-to-main">Pular para o conteúdo principal</a>

    <ToastContainer />
    <header class="layout-header" :class="{ 'header-collapsed': isNavCollapsed }" role="banner">
      <div class="header-content">
        <div class="header-left">
          <button
            @click="toggleNav"
            class="nav-toggle-button"
            :title="isNavCollapsed ? 'Expandir menu' : 'Retrair menu'"
            :aria-label="isNavCollapsed ? 'Expandir menu de navegação' : 'Retrair menu de navegação'"
            :aria-expanded="!isNavCollapsed"
          >
            <svg v-if="isNavCollapsed" viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
              <path fill="currentColor" d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/>
            </svg>
            <svg v-else viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
              <path fill="currentColor" d="M3 18h13v-2H3v2zm0-5h10v-2H3v2zm0-7v2h13V6H3zm18 9.59L17.42 12 21 8.41 19.59 7l-5 5 5 5L21 15.59z"/>
            </svg>
          </button>
          <h1 class="app-title">
            <router-link to="/dashboard" class="title-link" aria-label="Ir para dashboard">
              <span v-if="!isNavCollapsed">Gestor de Compras</span>
              <span v-else class="title-short">GC</span>
            </router-link>
          </h1>
        </div>
        <nav
          class="main-nav"
          v-if="!isNavCollapsed"
          role="navigation"
          aria-label="Menu principal"
        >
          <router-link to="/dashboard" class="nav-link">Dashboard</router-link>
          <div class="nav-dropdown">
            <button
              @click="toggleRelatoriosMenu"
              class="nav-link dropdown-button"
              :aria-expanded="relatoriosMenuOpen"
              aria-haspopup="true"
              aria-label="Menu de relatórios"
            >
              Relatórios
              <span class="dropdown-arrow expand-icon" :class="{ 'rotated': relatoriosMenuOpen }" aria-hidden="true">▼</span>
            </button>
            <div
              v-if="relatoriosMenuOpen"
              class="dropdown-menu"
              role="menu"
              aria-label="Opções de relatórios"
            >
              <button
                @click="gerarRelatorioFornecedores"
                class="dropdown-item"
                :disabled="gerandoRelatorio"
                :aria-busy="gerandoRelatorio"
                role="menuitem"
                aria-label="Gerar relatório de fornecedores em PDF"
              >
                <span v-if="gerandoRelatorio" class="loading-spinner" aria-hidden="true"></span>
                {{ gerandoRelatorio ? 'Gerando...' : 'Relatório de Fornecedores' }}
              </button>
            </div>
          </div>
        </nav>
        <div class="user-actions" role="region" aria-label="Informações do usuário">
          <span class="welcome-text" aria-live="polite">{{ getWelcomeMessage() }}, {{ username }}!</span>
          <button @click="logout" class="logout-button" aria-label="Sair do sistema">Sair</button>
        </div>
      </div>
    </header>

    <main id="main-content" class="layout-main" role="main" aria-label="Conteúdo principal">
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
import { getWelcomeMessage as getWelcomeMessageUtil } from '../utils/genderUtils'
import relatorioService from '../services/relatorioService'
import { useToast } from '../composables/useToast'
import ToastContainer from '../components/ui/ToastContainer.vue'

const router = useRouter()
const authStore = useAuthStore()
const { error: toastError } = useToast()

// Estados para o menu de relatórios
const relatoriosMenuOpen = ref(false)
const gerandoRelatorio = ref(false)

// Estado para controlar se o menu está colapsado
const isNavCollapsed = ref(false)

// Recupera estado do localStorage ao montar
const loadNavState = () => {
  const saved = localStorage.getItem('navCollapsed')
  if (saved !== null) {
    isNavCollapsed.value = saved === 'true'
  }
}

// Alterna estado do menu
const toggleNav = () => {
  isNavCollapsed.value = !isNavCollapsed.value
  localStorage.setItem('navCollapsed', isNavCollapsed.value.toString())
}

const username = computed(() => {
  return authStore.user?.nome || 'Usuário'
})

// Função para gerar mensagem de boas-vindas com gênero correto
const getWelcomeMessage = () => {
  return getWelcomeMessageUtil(authStore.user, 'simples')
}

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
    toastError('Erro ao gerar relatório. Tente novamente.')
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
  loadNavState()
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
  transition: all 0.3s ease;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-toggle-button {
  background: transparent;
  border: none;
  color: #666;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.nav-toggle-button:hover {
  background-color: #f3f4f6;
  color: #007bff;
}

.header-collapsed .header-content {
  max-width: 100%;
}

.header-collapsed .main-nav {
  display: none;
}

.app-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.title-short {
  font-weight: 700;
  letter-spacing: 1px;
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
