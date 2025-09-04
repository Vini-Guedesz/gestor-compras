import api from './api.js'

export const authService = {
  /**
   * Realiza o login do usuário
   * @param {string} email - Email do usuário
   * @param {string} senha - Senha do usuário
   * @returns {Promise<Object>} Resposta da API com o token
   */
  async login(email, senha) {
    try {
      const response = await api.post('/auth/login', {
        email,
        password: senha // Backend espera 'password', não 'senha'
      })

      // Verifica se o backend retornou o token
      if (response.data && response.data.token) {
        // Armazena o token no localStorage para futuras requisições
        localStorage.setItem('authToken', response.data.token)

        return {
          success: true,
          token: response.data.token
        }
      } else {
        throw new Error('Resposta inválida do servidor')
      }
    } catch (error) {
      // Remove token inválido se existir
      localStorage.removeItem('authToken')

      let errorMessage = 'Erro ao fazer login'

      if (error.response) {
        // Erro de resposta da API (4xx, 5xx)
        errorMessage = error.response.data?.message || error.response.data?.error || 'Credenciais inválidas'
      } else if (error.request) {
        // Erro de conexão
        errorMessage = 'Erro de conexão com o servidor'
      }

      return {
        success: false,
        error: errorMessage
      }
    }
  },

  /**
   * Verifica se o token é válido
   * @param {string} token - Token JWT
   * @returns {Promise<Object>} Informações do usuário se válido
   */
  async validateToken(token) {
    try {
      // Verifica se o token está no formato correto
      if (!token || !token.includes('.')) {
        throw new Error('Token inválido')
      }

      // Decodifica o payload do JWT (sem verificar assinatura, apenas para pegar informações)
      const payload = JSON.parse(atob(token.split('.')[1]))

      // Verifica se o token não expirou
      const now = Math.floor(Date.now() / 1000)
      if (payload.exp && payload.exp < now) {
        throw new Error('Token expirado')
      }

      return {
        success: true,
        user: {
          id: payload.sub,
          email: payload.sub, // O subject geralmente é o email/username
          name: payload.name || 'Usuário'
        }
      }
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Token inválido'
      }
    }
  },

  /**
   * Solicita recuperação de senha (placeholder para implementação futura)
   * @param {string} email - Email para recuperação
   * @returns {Promise<Object>} Resultado da operação
   */
  async forgotPassword(email) {
    try {
      // Por enquanto, apenas simula o envio
      // Quando o endpoint estiver disponível no backend, substitua por:
      // const response = await api.post('/auth/forgot-password', { email })

      await new Promise(resolve => setTimeout(resolve, 1000)) // Simula delay

      return {
        success: true,
        message: 'Se o email estiver cadastrado, você receberá as instruções para recuperação da senha.'
      }
    } catch (error) {
      return {
        success: false,
        error: 'Erro ao solicitar recuperação de senha'
      }
    }
  },

  /**
   * Realiza o logout do usuário
   */
  logout() {
    localStorage.removeItem('authToken')
  },

  /**
   * Obtém o token do localStorage
   * @returns {string|null} Token JWT ou null se não existir
   */
  getToken() {
    return localStorage.getItem('authToken')
  },

  /**
   * Verifica se o usuário está logado
   * @returns {boolean} True se possui token válido
   */
  isAuthenticated() {
    const token = this.getToken()
    if (!token) return false

    try {
      // Verifica se o token não expirou
      const payload = JSON.parse(atob(token.split('.')[1]))
      const now = Math.floor(Date.now() / 1000)

      if (payload.exp && payload.exp < now) {
        this.logout() // Remove token expirado
        return false
      }

      return true
    } catch (error) {
      this.logout() // Remove token inválido
      return false
    }
  }
}
