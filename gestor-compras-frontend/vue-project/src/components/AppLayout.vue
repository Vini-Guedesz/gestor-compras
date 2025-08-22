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
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const username = computed(() => {
  return authStore.user?.name || authStore.user?.username || 'Usuário'
})

const logout = () => {
  authStore.logout()
  router.push('/login')
}
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
