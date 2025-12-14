/**
 * @fileoverview Store de Autenticação com Pinia
 *
 * Gerencia estado global de autenticação usando Pinia (Vue 3).
 * Controla login, logout, validação de tokens JWT, recuperação de senha
 * e persistência de sessão. Store reativo compartilhado pela aplicação.
 *
 * @module stores/auth
 * @requires pinia
 * @requires vue
 * @requires services/authService
 * @requires utils/logger
 *
 * @description
 * Este store Pinia implementa:
 * - Login/logout com validação JWT
 * - Persistência no sessionStorage (mais seguro que localStorage)
 * - Validação automática de tokens ao iniciar
 * - Recuperação de senha
 * - Estados reativos globais (isAuthenticated, user, token)
 * - Proteção contra chamadas simultâneas de checkAuth
 *
 * @example
 * // Em componentes Vue
 * import { useAuthStore } from '@/stores/auth'
 *
 * const authStore = useAuthStore()
 *
 * // Login
 * const result = await authStore.login('user@example.com', 'password')
 * if (result.success) {
 *   console.log('Usuário:', authStore.user)
 * }
 *
 * @example
 * // Verificar autenticação na navegação
 * import { useAuthStore } from '@/stores/auth'
 *
 * router.beforeEach(async (to, from, next) => {
 *   const authStore = useAuthStore()
 *   await authStore.checkAuth()
 *
 *   if (to.meta.requiresAuth && !authStore.isAuthenticated) {
 *     next('/login')
 *   } else {
 *     next()
 *   }
 * })
 *
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authService } from '../services/authService.js'
import logger from '../utils/logger.js'

/**
 * @typedef {Object} User
 * @property {number} id - ID do usuário
 * @property {string} username - Nome de usuário
 * @property {string} email - Email do usuário
 * @property {string} role - Role do usuário (ADMIN/USER)
 * @property {string} [genero] - Gênero do usuário (opcional)
 */

/**
 * @typedef {Object} LoginResult
 * @property {boolean} success - Se login foi bem-sucedido
 * @property {string} [error] - Mensagem de erro (se falhou)
 */

/**
 * Store Pinia de Autenticação
 *
 * @function useAuthStore
 * @returns {Object} Store com estados e actions
 * @returns {import('vue').Ref<boolean>} returns.isAuthenticated - Estado de autenticação
 * @returns {import('vue').Ref<User|null>} returns.user - Dados do usuário logado
 * @returns {import('vue').Ref<string|null>} returns.token - Token JWT
 * @returns {Function} returns.login - Função de login
 * @returns {Function} returns.logout - Função de logout
 * @returns {Function} returns.checkAuth - Validação de sessão
 * @returns {Function} returns.forgotPassword - Recuperação de senha
 *
 * @description
 * Store reativo configurado com Composition API do Pinia.
 * Todos os estados são refs reativos compartilhados globalmente.
 */
export const useAuthStore = defineStore('auth', () => {
  /**
   * Estado de autenticação
   * @type {import('vue').Ref<boolean>}
   * @description true se usuário está autenticado com token válido
   */
  const isAuthenticated = ref(false)

  /**
   * Dados do usuário logado
   * @type {import('vue').Ref<User|null>}
   * @description null quando não autenticado, objeto User quando logado
   */
  const user = ref(null)

  /**
   * Token JWT de autenticação
   * @type {import('vue').Ref<string|null>}
   * @description null quando não autenticado, string JWT quando logado
   */
  const token = ref(null)

  /**
   * Flag de controle de checkAuth
   * @type {import('vue').Ref<boolean>}
   * @private
   * @description Previne chamadas simultâneas de checkAuth() que podem causar race conditions
   */
  const authCheckInProgress = ref(false)

  /**
   * Realiza login do usuário
   *
   * @async
   * @function login
   * @param {string} email - Email do usuário
   * @param {string} password - Senha do usuário
   * @returns {Promise<LoginResult>} Resultado da operação
   *
   * @example
   * const authStore = useAuthStore()
   * const result = await authStore.login('user@example.com', 'senha123')
   *
   * if (result.success) {
   *   console.log('Logado como:', authStore.user.username)
   *   router.push('/dashboard')
   * } else {
   *   console.error('Erro:', result.error)
   * }
   *
   * @description
   * Fluxo de login:
   * 1. Chama authService.login() para validar credenciais
   * 2. Valida o token JWT recebido
   * 3. Atualiza estados reativos (isAuthenticated, user, token)
   * 4. Token persiste automaticamente no sessionStorage via authService
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
   * Realiza logout do usuário
   *
   * @function logout
   * @returns {void}
   *
   * @example
   * const authStore = useAuthStore()
   * authStore.logout()
   * router.push('/login')
   *
   * @example
   * // Em componente com toast
   * const handleLogout = () => {
   *   authStore.logout()
   *   toast.success('Logout realizado com sucesso')
   *   router.push('/login')
   * }
   *
   * @description
   * Ações executadas:
   * 1. Limpa estados reativos (isAuthenticated, user, token)
   * 2. Remove token do sessionStorage via authService
   * 3. Limpa axios interceptors (headers Authorization)
   *
   * Não é async - execução instantânea e síncrona.
   */
  const logout = () => {
    // Limpa o estado da aplicação
    isAuthenticated.value = false
    user.value = null
    token.value = null

    // Remove token usando o authService
    authService.logout()
  }

  /**
   * Verifica e restaura sessão ativa salva
   *
   * @async
   * @function checkAuth
   * @returns {Promise<boolean>} true se encontrou sessão válida
   *
   * @example
   * // Em App.vue ou router
   * import { useAuthStore } from '@/stores/auth'
   *
   * const authStore = useAuthStore()
   * await authStore.checkAuth()
   *
   * if (authStore.isAuthenticated) {
   *   console.log('Sessão restaurada:', authStore.user)
   * }
   *
   * @example
   * // No router guard
   * router.beforeEach(async (to, from, next) => {
   *   const authStore = useAuthStore()
   *   await authStore.checkAuth()
   *
   *   if (to.meta.requiresAuth && !authStore.isAuthenticated) {
   *     next('/login')
   *   } else {
   *     next()
   *   }
   * })
   *
   * @description
   * Chamado ao inicializar a aplicação para restaurar sessões válidas.
   *
   * Fluxo:
   * 1. Verifica se já está executando (evita race conditions)
   * 2. Busca token no sessionStorage via authService
   * 3. Valida token com backend
   * 4. Se válido: restaura estados (isAuthenticated, user, token)
   * 5. Se inválido: executa logout() para limpar tudo
   *
   * Proteção: authCheckInProgress previne chamadas simultâneas.
   */
  const checkAuth = async () => {
    // Evita chamadas simultâneas ou duplicadas
    if (authCheckInProgress.value) {
      return isAuthenticated.value
    }

    try {
      authCheckInProgress.value = true

      // Verifica se existe token e se é válido usando o authService
      if (authService.isAuthenticated()) {
        const savedToken = authService.getToken()

        // Valida o token para obter dados do usuário
        const tokenValidation = await authService.validateToken(savedToken)

        if (tokenValidation.success) {
          // Token válido: restaura a sessão
          token.value = savedToken
          user.value = tokenValidation.user
          isAuthenticated.value = true
          return true
        }
      }

      // Token inválido ou não existe: garante que o estado está limpo
      logout()
      return false
    } catch (error) {
      logger.error('Erro ao verificar autenticação:', error)
      logout()
      return false
    } finally {
      authCheckInProgress.value = false
    }
  }

  /**
   * Solicita recuperação de senha
   *
   * @async
   * @function forgotPassword
   * @param {string} email - Email para envio de instruções
   * @returns {Promise<Object>} Resultado da operação
   * @returns {boolean} returns.success - Se solicitação foi bem-sucedida
   * @returns {string} [returns.error] - Mensagem de erro (se falhou)
   *
   * @example
   * const authStore = useAuthStore()
   * const result = await authStore.forgotPassword('user@example.com')
   *
   * if (result.success) {
   *   toast.success('Email de recuperação enviado')
   * } else {
   *   toast.error(result.error)
   * }
   *
   * @description
   * Delega para authService.forgotPassword().
   * Backend envia email com instruções/link de reset.
   * Não modifica estados do store (não faz login).
   */
  const forgotPassword = async (email) => {
    return await authService.forgotPassword(email)
  }

  /**
   * Exportação do store
   *
   * @returns {Object} Estados reativos e actions do store
   *
   * @property {import('vue').Ref<boolean>} isAuthenticated - Se usuário está autenticado
   * @property {import('vue').Ref<User|null>} user - Dados do usuário logado
   * @property {import('vue').Ref<string|null>} token - Token JWT
   * @property {Function} login - Action de login
   * @property {Function} logout - Action de logout
   * @property {Function} checkAuth - Action de validação de sessão
   * @property {Function} forgotPassword - Action de recuperação de senha
   *
   * @description
   * Estados são reativos (Ref) e compartilhados globalmente.
   * Mudanças em qualquer componente atualizam todos os outros automaticamente.
   */
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
