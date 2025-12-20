<!--
  Componente raiz da aplicação

  Este é o componente principal que envolve toda a aplicação.
  Ele define:
  - A estrutura básica do HTML
  - Os estilos globais da aplicação
  - O ponto de montagem para as diferentes páginas/rotas
-->
<script setup>
import { onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'
import { useErrorModal } from './composables/useErrorModal'
import ErrorModal from './components/ui/modals/ErrorModal.vue'

const router = useRouter()
const authStore = useAuthStore()
const { modalState, hideModal } = useErrorModal()

// Handler para o evento de logout emitido pelo interceptor da API
const handleAuthLogout = () => {
  // Limpa o estado de autenticação
  authStore.logout()

  // Redireciona para login apenas se não estiver já na página de login
  // Usa replace em vez de push para não adicionar ao histórico
  if (router.currentRoute.value.path !== '/login') {
    router.replace('/login')
  }
}

// Adiciona listener quando o componente é montado
onMounted(() => {
  window.addEventListener('auth:logout', handleAuthLogout)
})

// Remove listener quando o componente é desmontado
onUnmounted(() => {
  window.removeEventListener('auth:logout', handleAuthLogout)
})
</script>

<template>
  <div id="app">
    <!-- RouterView renderiza o componente correspondente à rota atual -->
    <!-- Cada página (Login, Dashboard, etc.) será exibida aqui -->
    <RouterView />

    <!-- Modal de erro global -->
    <ErrorModal
      :isVisible="modalState.isVisible"
      :type="modalState.type"
      :title="modalState.title"
      :message="modalState.message"
      :details="modalState.details"
      :confirmText="modalState.confirmText"
      :cancelText="modalState.cancelText"
      :closeText="modalState.closeText"
      :onConfirm="modalState.onConfirm"
      :onCancel="modalState.onCancel"
      :onClose="modalState.onClose"
      @close="hideModal"
    />
  </div>
</template>

<style>
/* Reset CSS global - remove margens e paddings padrão dos navegadores */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box; /* Inclui padding e border no cálculo da largura total */
}

/* Estilos base para o corpo da página */
body {
  font-family: Arial, sans-serif;
  background-color: #f8f9fa; /* Cor de fundo clara */
}

/* Container principal da aplicação */
#app {
  width: 100%;
  min-height: 100vh; /* Garante que a aplicação ocupe pelo menos a altura da tela */
}
</style>
