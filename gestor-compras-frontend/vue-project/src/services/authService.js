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
        senha // Backend espera 'senha', não 'password'
      })


      // Verifica se o backend retornou o token
      if (response && response.token) {
        // Armazena o token no sessionStorage (mais seguro que localStorage)
        // sessionStorage é limpo ao fechar a aba e não persiste entre abas
        sessionStorage.setItem('authToken', response.token)

        return {
          success: true,
          token: response.token
        }
      } else {
        throw new Error('Resposta inválida do servidor - token não encontrado')
      }
    } catch (error) {

      // Remove token inválido se existir
      sessionStorage.removeItem('authToken')

      let errorMessage = 'Erro ao fazer login'

      if (error.response) {
        // Erro de resposta da API
        switch (error.response.status) {
          case 401:
          case 403:
            errorMessage = 'Email ou senha incorretos'
            break
          case 500:
            errorMessage = 'Erro interno do servidor'
            break
          default:
            errorMessage = error.response.data?.message || error.response.data?.error || 'Erro do servidor'
        }
      } else if (error.request) {
        // Erro de conexão
        errorMessage = 'Erro de conexão com o servidor. Verifique se o backend está rodando.'
      } else {
        errorMessage = error.message
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

      // Extrai role do JWT (formato: [{"authority": "ROLE_ADMIN"}])
      let userRole = 'USER'
      if (payload.roles && Array.isArray(payload.roles)) {
        const roleObj = payload.roles[0]
        if (roleObj && roleObj.authority) {
          // Remove o prefixo "ROLE_" se existir
          userRole = roleObj.authority.replace('ROLE_', '')
        }
      }

      return {
        success: true,
        user: {
          email: payload.sub,           // Subject é o email (usado para login)
          nome: payload.nome || payload.sub,  // Nome completo da pessoa (fallback para email se não existir)
          role: userRole                // ADMIN ou USER
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
   * @param {string} _email - Email para recuperação
   * @returns {Promise<Object>} Resultado da operação
   */
  async forgotPassword(_email) {
    try {
      // Por enquanto, apenas simula o envio
      // Quando o endpoint estiver disponível no backend, substitua por:
      // const response = await api.post('/auth/forgot-password', { email })

      await new Promise(resolve => setTimeout(resolve, 1000)) // Simula delay

      return {
        success: true,
        message: 'Se o email estiver cadastrado, você receberá as instruções para recuperação da senha.'
      }
    } catch {
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
    sessionStorage.removeItem('authToken')
  },

  /**
   * Obtém o token do sessionStorage
   * @returns {string|null} Token JWT ou null se não existir
   */
  getToken() {
    return sessionStorage.getItem('authToken')
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
    } catch {
      this.logout() // Remove token inválido
      return false
    }
  }
}
