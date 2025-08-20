<template>
  <div class="login-container">
    <LoadingSpinner :show="isLoading" message="Fazendo login..." />

    <div class="login-box">
      <h2 class="login-title">Login</h2>

      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">Login:</label>
          <input
            type="text"
            id="username"
            v-model="loginForm.username"
            required
            class="form-input"
            placeholder="Digite seu login"
            :disabled="isLoading"
          />
        </div>

        <div class="form-group">
          <label for="password">Senha:</label>
          <input
            type="password"
            id="password"
            v-model="loginForm.password"
            required
            class="form-input"
            placeholder="Digite sua senha"
            :disabled="isLoading"
          />
        </div>

        <button type="submit" class="login-button" :disabled="isLoading">
          <span v-if="isLoading">Entrando...</span>
          <span v-else>Acessar</span>
        </button>

        <div class="forgot-password">
          <a href="#" @click.prevent="handleForgotPassword" class="forgot-link" :class="{ disabled: isLoading }">
            Esqueceu sua senha?
          </a>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import LoadingSpinner from '../components/LoadingSpinner.vue'

const router = useRouter()
const authStore = useAuthStore()

const loginForm = ref({
  username: '',
  password: ''
})

const isLoading = ref(false)
const errorMessage = ref('')

const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    errorMessage.value = 'Por favor, preencha todos os campos'
    return
  }

  isLoading.value = true
  errorMessage.value = ''

  try {
    const result = await authStore.login(loginForm.value.username, loginForm.value.password)

    if (result.success) {
      router.push('/dashboard')
    } else {
      errorMessage.value = result.error
    }
  } catch {
    errorMessage.value = 'Erro inesperado. Tente novamente.'
  } finally {
    isLoading.value = false
  }
}

const handleForgotPassword = async () => {
  if (!loginForm.value.username) {
    alert('Por favor, digite seu login/e-mail no campo acima primeiro.')
    return
  }

  const result = await authStore.forgotPassword(loginForm.value.username)

  if (result.success) {
    alert(result.message)
  } else {
    alert(result.error)
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.login-box {
  background: white;
  border: 2px solid #e0e0e0;
  border-radius: 40px;
  padding: 40px;
  width: 580px;
  height: 400px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #1F285F;
  font-size: 24px;
  font-weight: 600;
  font-family: Arial, sans-serif;
}

.error-message {
  background-color: #f8d7da;
  color: #721c24;
  padding: 12px;
  border-radius: 10px;
  margin-bottom: 20px;
  font-size: 14px;
  font-family: Arial, sans-serif;
  border: 1px solid #f5c6cb;
}

.login-form {
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #1F285F;
  font-weight: 500;
  font-family: Arial, sans-serif;
}

.form-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 10px;
  font-size: 16px;
  font-family: Arial, sans-serif;
  color: #1F285F;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #1F285F;
  box-shadow: 0 0 0 2px rgba(31, 40, 95, 0.25);
}

.login-button {
  background-color: #1F285F;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 500;
  font-family: Arial, sans-serif;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-bottom: 15px;
}

.login-button:hover {
  background-color: #162038;
}

.login-button:active {
  background-color: #0f1729;
}

.login-button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.forgot-password {
  text-align: center;
}

.forgot-link {
  color: #1F285F;
  text-decoration: none;
  font-size: 14px;
  font-family: Arial, sans-serif;
  transition: color 0.3s;
}

.forgot-link:hover {
  color: #162038;
  text-decoration: underline;
}

.forgot-link.disabled {
  color: #6c757d;
  cursor: not-allowed;
  pointer-events: none;
}
</style>
