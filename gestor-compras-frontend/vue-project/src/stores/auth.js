import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref(false)
  const user = ref(null)
  const token = ref(null)

  const login = async (username, password) => {
    try {
      // Aqui você fará a chamada para sua API de autenticação
      // Por enquanto, vamos simular um login bem-sucedido

      // Simulação de delay da API
      await new Promise(resolve => setTimeout(resolve, 1000))

      // Simulação de resposta da API
      if (username && password) {
        isAuthenticated.value = true
        user.value = {
          id: 1,
          username: username,
          name: 'Usuário Sistema'
        }
        token.value = 'fake-jwt-token'

        // Salvar no localStorage para persistir a sessão
        localStorage.setItem('auth-token', token.value)
        localStorage.setItem('user', JSON.stringify(user.value))

        return { success: true }
      } else {
        throw new Error('Credenciais inválidas')
      }
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Erro ao fazer login'
      }
    }
  }

  const logout = () => {
    isAuthenticated.value = false
    user.value = null
    token.value = null

    // Limpar localStorage
    localStorage.removeItem('auth-token')
    localStorage.removeItem('user')
  }

  const checkAuth = () => {
    // Verificar se há dados de autenticação salvos
    const savedToken = localStorage.getItem('auth-token')
    const savedUser = localStorage.getItem('user')

    if (savedToken && savedUser) {
      token.value = savedToken
      user.value = JSON.parse(savedUser)
      isAuthenticated.value = true
      return true
    }

    return false
  }

  const forgotPassword = async (email) => {
    try {
      // Aqui você fará a chamada para sua API de recuperação de senha
      await new Promise(resolve => setTimeout(resolve, 1000))

      return {
        success: true,
        message: 'E-mail de recuperação enviado com sucesso!'
      }
    } catch (error) {
      return {
        success: false,
        error: 'Erro ao enviar e-mail de recuperação'
      }
    }
  }

  return {
    isAuthenticated,
    user,
    token,
    login,
    logout,
    checkAuth,
    forgotPassword
  }
})
