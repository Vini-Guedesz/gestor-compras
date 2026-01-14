<!--
  Componente de Login - Página de Autenticação

  Este componente implementa a interface de login da aplicação.

  Funcionalidades:
  - Formulário de login com validação
  - Exibição de erros de autenticação
  - Loading state durante a autenticação
  - Funcionalidade "Esqueci minha senha"
  - Redirecionamento automático após login bem-sucedido

  Estados gerenciados:
  - loginForm: dados do formulário (email e senha)
  - isLoading: indica se está processando o login
  - errorMessage: mensagem de erro para exibir ao usuário
-->
<template>
  <div class="login-container">
    <!-- Componente de loading que aparece durante o processo de autenticação -->
    <LoadingSpinner :show="isLoading" message="Fazendo login..." />

    <div class="login-box">
      <h2 class="login-title">Login</h2>

      <!-- Exibição condicional de mensagens de erro -->
      <div
        v-if="errorMessage"
        class="error-message"
        role="alert"
        aria-live="polite"
        aria-atomic="true"
      >
        {{ errorMessage }}
      </div>

      <!-- Formulário de login com prevenção de submit padrão -->
      <form @submit.prevent="handleLogin" class="login-form" aria-label="Formulário de login">
        <!-- Campo de email -->
        <div class="form-group">
          <label for="email">Email:</label>
          <input
            type="email"
            id="email"
            v-model="loginForm.email"
            required
            class="form-input"
            placeholder="Digite seu email"
            :disabled="isLoading"
            :aria-invalid="!!errorMessage"
            aria-describedby="email-hint"
            autocomplete="email"
          />
          <span id="email-hint" class="sr-only">Digite seu endereço de email para fazer login</span>
        </div>

        <!-- Campo de senha -->
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
            :aria-invalid="!!errorMessage"
            aria-describedby="password-hint"
            autocomplete="current-password"
          />
          <span id="password-hint" class="sr-only">Digite sua senha para fazer login</span>
        </div>

        <!-- Botão de submit com estado de loading -->
        <button
          type="submit"
          class="login-button"
          :disabled="isLoading"
          :aria-busy="isLoading"
          aria-label="Entrar no sistema"
        >
          <span v-if="isLoading">Entrando...</span>
          <span v-else>Acessar</span>
        </button>

        <!-- Link para recuperação de senha -->
        <div class="forgot-password">
          <a
            href="#"
            @click.prevent="handleForgotPassword"
            class="forgot-link"
            :class="{ disabled: isLoading }"
            :tabindex="isLoading ? -1 : 0"
            aria-label="Recuperar senha esquecida"
          >
            Esqueceu sua senha?
          </a>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
/**
 * Lógica do Componente de Login
 *
 * Utiliza a Composition API do Vue 3 com <script setup>
 * para uma sintaxe mais concisa e reativa.
 */

import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useToast } from '@/composables/useToast'
import LoadingSpinner from '@/components/ui/feedback/LoadingSpinner.vue'

// Instâncias dos serviços necessários
const router = useRouter()       // Para navegação entre páginas
const authStore = useAuthStore() // Para gerenciamento do estado de autenticação
const { success, warning, error: toastError } = useToast()

// Estados reativos do componente
const loginForm = ref({
  email: '',
  password: ''
})

const isLoading = ref(false)    // Indica se está processando o login
const errorMessage = ref('')    // Mensagem de erro para exibir

/**
 * Manipula o envio do formulário de login
 *
 * Fluxo:
 * 1. Valida se os campos estão preenchidos
 * 2. Ativa o estado de loading
 * 3. Chama o store de autenticação para fazer login
 * 4. Redireciona para dashboard em caso de sucesso
 * 5. Exibe erro em caso de falha
 */
const handleLogin = async () => {
  // Validação básica dos campos
  if (!loginForm.value.email || !loginForm.value.password) {
    errorMessage.value = 'Por favor, preencha todos os campos'
    return
  }

  // Inicia o processo de login
  isLoading.value = true
  errorMessage.value = ''

  try {
    // Chama o método de login do store
    const result = await authStore.login(loginForm.value.email, loginForm.value.password)

    if (result.success) {
      // Login bem-sucedido: redireciona para o dashboard
      router.push('/dashboard')
    } else {
      // Login falhou: exibe a mensagem de erro
      errorMessage.value = result.error
    }
  } catch {
    // Erro inesperado: exibe mensagem genérica
    errorMessage.value = 'Erro inesperado. Tente novamente.'
  } finally {
    // Sempre desativa o loading ao final
    isLoading.value = false
  }
}

/**
 * Manipula a funcionalidade "Esqueci minha senha"
 *
 * Utiliza o email digitado no formulário para enviar
 * instruções de recuperação de senha.
 */
const handleForgotPassword = async () => {
  // Verifica se o email foi preenchido
  if (!loginForm.value.email) {
    warning('Por favor, digite seu e-mail no campo acima primeiro.')
    return
  }

  // Chama o serviço de recuperação de senha
  const result = await authStore.forgotPassword(loginForm.value.email)

  // Exibe o resultado ao usuário
  if (result.success) {
    success(result.message)
  } else {
    toastError(result.error)
  }
}
</script>

<style scoped>
/*
  Estilos do Componente de Login

  Utiliza CSS scoped para garantir que os estilos só se apliquem
  a este componente, evitando vazamentos de estilo.

  Design responsivo e centrado para uma boa experiência do usuário.
*/

/* Container principal - ocupa toda a tela e centraliza o conteúdo */
.login-container {
  min-height: 100vh;
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  font-family: inherit;
}

/* Caixa de login principal */
.login-box {
  background: white;
  border: 2px solid #e0e0e0;
  border-radius: 40px;
  padding: 40px;
  width: 100%;
  max-width: 580px;
  min-height: 400px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
  overflow: hidden;
}

/* Título da página de login */
.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #1F285F;
  font-size: 24px;
  font-weight: 600;
  font-family: inherit;
}

/* Estilo para mensagens de erro */
.error-message {
  background-color: #f8d7da;
  color: #721c24;
  padding: 12px;
  border-radius: 10px;
  margin-bottom: 20px;
  font-size: 14px;
  font-family: inherit;
  border: 1px solid #f5c6cb;
}

/* Container do formulário */
.login-form {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* Grupo de campos do formulário */
.form-group {
  margin-bottom: 20px;
  width: 100%;
}

/* Labels dos campos */
.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #1F285F;
  font-weight: 500;
  font-family: inherit;
}

/* Campos de input */
.form-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 10px;
  font-size: 16px;
  font-family: inherit;
  color: #1F285F;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

/* Estado de foco dos inputs */
.form-input:focus {
  outline: none;
  border-color: #1F285F;
  box-shadow: 0 0 0 2px rgba(31, 40, 95, 0.25);
}

/* Botão de login */
.login-button {
  background-color: #1F285F;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 500;
  font-family: inherit;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-bottom: 20px;
  width: 100%;
  box-sizing: border-box;
}

/* Estados do botão (hover, active, disabled) */
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

/* Container do link "Esqueceu a senha" */
.forgot-password {
  text-align: center;
  margin-top: 10px;
  width: 100%;
}

/* Link "Esqueceu a senha" */
.forgot-link {
  color: #1F285F;
  text-decoration: none;
  font-size: 14px;
  font-family: inherit;
  transition: color 0.3s;
}

/* Estado hover do link */
.forgot-link:hover {
  color: #162038;
  text-decoration: underline;
}

/* Estado desabilitado do link (durante loading) */
.forgot-link.disabled {
  color: #6c757d;
  cursor: not-allowed;
  pointer-events: none;
}

/* Classe para leitores de tela (oculta visualmente mas acessível) */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}

/* Responsividade para telas menores */
@media (max-width: 768px) {
  .login-container {
    padding: 10px;
  }

  .login-box {
    width: 100%;
    max-width: 400px;
    padding: 30px 20px;
    min-height: auto;
  }

  .login-title {
    font-size: 20px;
    margin-bottom: 25px;
  }

  .form-group {
    margin-bottom: 15px;
  }

  .form-input {
    padding: 10px;
    font-size: 14px;
  }

  .login-button {
    padding: 12px 20px;
    font-size: 14px;
  }

  .forgot-link {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .login-box {
    padding: 20px 15px;
    border-radius: 20px;
  }

  .login-title {
    font-size: 18px;
  }

  .form-input {
    padding: 8px;
    font-size: 14px;
  }
}
</style>
