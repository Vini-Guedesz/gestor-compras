<!--
  Componente de Login - Página de Autenticação
-->
<template>
  <div class="auth-page">
    <LoadingSpinner :show="isLoading" message="Fazendo login..." />

    <div class="auth-shell">
      <aside class="auth-brand" aria-hidden="true">
        <div class="auth-badge">Gestor de Compras</div>
        <h2>Compras corporativas com mais controle</h2>
        <p>
          Organize pedidos, cotações e fornecedores com um fluxo simples, seguro e padronizado.
        </p>

        <ul class="auth-features">
          <li>Processos mais rápidos e auditáveis</li>
          <li>Histórico centralizado de negociações</li>
          <li>Mais previsibilidade nas aprovações</li>
        </ul>
      </aside>

      <section class="auth-card" aria-label="Área de autenticação">
        <header class="auth-header">
          <h1 class="auth-title">Bem-vindo de volta</h1>
          <p class="auth-subtitle">Entre para acompanhar suas demandas de compras.</p>
        </header>

        <div
          v-if="errorMessage"
          class="auth-error"
          role="alert"
          aria-live="polite"
          aria-atomic="true"
        >
          {{ errorMessage }}
        </div>

        <form @submit.prevent="handleLogin" class="auth-form" aria-label="Formulário de login">
          <div class="form-group">
            <label for="email">E-mail</label>
            <input
              type="email"
              id="email"
              v-model="loginForm.email"
              required
              class="form-input"
              placeholder="seuemail@empresa.com"
              :disabled="isLoading"
              :aria-invalid="!!errorMessage"
              aria-describedby="email-hint"
              autocomplete="email"
            />
            <span id="email-hint" class="field-hint">Use o mesmo e-mail cadastrado no sistema.</span>
          </div>

          <div class="form-group">
            <label for="password">Senha</label>
            <input
              type="password"
              id="password"
              v-model="loginForm.password"
              required
              class="form-input"
              placeholder="Digite sua senha"
              :disabled="isLoading"
              :aria-invalid="!!errorMessage"
              aria-describedby="password-hint"
              autocomplete="current-password"
            />
            <span id="password-hint" class="field-hint">Mínimo recomendado de 8 caracteres.</span>
          </div>

          <button
            type="submit"
            class="auth-button"
            :disabled="isLoading"
            :aria-busy="isLoading"
            aria-label="Entrar no sistema"
          >
            <span v-if="isLoading">Entrando...</span>
            <span v-else>Acessar plataforma</span>
          </button>

          <div class="auth-footer">
            <a
              href="#"
              @click.prevent="handleForgotPassword"
              class="auth-link"
              :class="{ disabled: isLoading }"
              :tabindex="isLoading ? -1 : 0"
              aria-label="Recuperar senha esquecida"
            >
              Não consegue acessar sua conta?
            </a>
          </div>
        </form>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useToast } from '@/composables/useToast'
import LoadingSpinner from '@/components/ui/feedback/LoadingSpinner.vue'

const router = useRouter()
const authStore = useAuthStore()
const { success, warning, error: toastError } = useToast()

const loginForm = ref({
  email: '',
  password: ''
})

const isLoading = ref(false)
const errorMessage = ref('')

const handleLogin = async () => {
  if (!loginForm.value.email || !loginForm.value.password) {
    errorMessage.value = 'Informe e-mail e senha para continuar.'
    return
  }

  isLoading.value = true
  errorMessage.value = ''

  try {
    const result = await authStore.login(loginForm.value.email, loginForm.value.password)

    if (result.success) {
      router.push('/dashboard')
    } else {
      errorMessage.value = result.error
    }
  } catch {
    errorMessage.value = 'Não foi possível entrar agora. Tente novamente em instantes.'
  } finally {
    isLoading.value = false
  }
}

const handleForgotPassword = async () => {
  if (!loginForm.value.email) {
    warning('Digite seu e-mail para enviarmos as instruções de recuperação.')
    return
  }

  const result = await authStore.forgotPassword(loginForm.value.email)

  if (result.success) {
    success(result.message)
  } else {
    toastError(result.error)
  }
}
</script>

<style src="../assets/css/auth-pages.css"></style>


