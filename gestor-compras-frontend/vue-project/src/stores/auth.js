/**
 * Store de Autenticação - Gerenciamento Global do Estado de Autenticação
 *
 * Este store utiliza Pinia para gerenciar o estado global da autenticação.
 *
 * Funcionalidades principais:
 * - Login/logout de usuários
 * - Persistência da sessão no localStorage
 * - Validação automática de tokens
 * - Recuperação de senha
 *
 * Estados gerenciados:
 * - isAuthenticated: boolean - se o usuário está logado
 * - user: object - dados do usuário logado
 * - token: string - token JWT de autenticação
 */

import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authService } from '../services/authService.js'

export const useAuthStore = defineStore('auth', () => {
  // Estados reativos da autenticação
  const isAuthenticated = ref(false)  // Indica se o usuário está autenticado
  const user = ref(null)              // Dados do usuário logado
  const token = ref(null)             // Token JWT para autorização

  /**
   * Realiza o login do usuário
   *
   * @param {string} email - Email do usuário
   * @param {string} password - Senha do usuário
   * @returns {Object} Resultado da operação de login
   */
  const login = async (email, password) => {
    try {
      // Chama o serviço de autenticação para validar credenciais
      const result = await authService.login(email, password)

      if (result.success) {
        // Login bem-sucedido: valida o token recebido
        const tokenValidation = await authService.validateToken(result.token)

        if (tokenValidation.success) {
          // Token válido: atualiza o estado da aplicação
          isAuthenticated.value = true
          user.value = tokenValidation.user
          token.value = result.token

          // Persiste a sessão no localStorage para manter login entre sessões
          localStorage.setItem('auth-token', token.value)
          localStorage.setItem('user', JSON.stringify(user.value))

          return { success: true }
        } else {
          throw new Error('Token recebido é inválido')
        }
      } else {
        // Login falhou: retorna o erro do serviço
        return result
      }
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Erro ao fazer login'
      }
    }
  }

  /**
   * Realiza o logout do usuário
   *
   * - Limpa todos os estados de autenticação
   * - Remove dados do localStorage
   */
  const logout = () => {
    // Limpa o estado da aplicação
    isAuthenticated.value = false
    user.value = null
    token.value = null

    // Remove dados persistidos no navegador
    localStorage.removeItem('auth-token')
    localStorage.removeItem('user')
  }

  /**
   * Verifica se há uma sessão ativa salva
   *
   * Chamado ao inicializar a aplicação para restaurar sessões
   * válidas que foram salvas anteriormente.
   *
   * @returns {boolean} True se encontrou e validou uma sessão ativa
   */
  const checkAuth = async () => {
    // Recupera dados salvos do localStorage
    const savedToken = localStorage.getItem('auth-token')
    const savedUser = localStorage.getItem('user')

    if (savedToken && savedUser) {
      // Verifica se o token salvo ainda é válido
      const tokenValidation = await authService.validateToken(savedToken)

      if (tokenValidation.success) {
        // Token válido: restaura a sessão
        token.value = savedToken
        user.value = JSON.parse(savedUser)
        isAuthenticated.value = true
        return true
      } else {
        // Token inválido ou expirado: limpa dados salvos
        logout()
        return false
      }
    }

    return false
  }

  /**
   * Solicita recuperação de senha
   *
   * @param {string} email - Email para envio das instruções
   * @returns {Object} Resultado da operação
   */
  const forgotPassword = async (email) => {
    return await authService.forgotPassword(email)
  }

  // Expõe os estados e métodos para uso nos componentes
  return {
    // Estados reativos
    isAuthenticated,
    user,
    token,
    // Métodos de ação
    login,
    logout,
    checkAuth,
    forgotPassword
  }
})
