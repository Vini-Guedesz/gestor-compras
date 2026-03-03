/**
 * @fileoverview Serviço de Autenticação
 * @module services/authService
 * @description
 * Serviço responsável por gerenciar a autenticação de usuários no sistema.
 * Utiliza JWT (JSON Web Tokens) para autenticação e sessionStorage para persistência.
 *
 * @example
 * import { authService } from '@/services/authService'
 *
 * // Login
 * const result = await authService.login('usuario@email.com', 'senha123')
 * if (result.success) {
 *   console.log('Token:', result.token)
 * }
 *
 * // Verificar autenticação
 * if (authService.isAuthenticated()) {
 *   console.log('Usuário está autenticado')
 * }
 *
 * // Logout
 * authService.logout()
 *
 * @author Equipe de Desenvolvimento
 * @version 1.0.0
 * @since 2024
 */

import api from './api.js'
import { isTokenValid, extractUserInfo } from '../utils/jwtUtils.js'

/**
 * @typedef {Object} LoginResponse
 * @property {boolean} success - Indica se o login foi bem-sucedido
 * @property {string} [token] - Token JWT retornado (presente se success=true)
 * @property {string} [error] - Mensagem de erro (presente se success=false)
 */

/**
 * @typedef {Object} UserInfo
 * @property {string} email - Email do usuário
 * @property {string} nome - Nome completo do usuário
 * @property {string} role - Role do usuário (ADMIN ou USER)
 */

/**
 * @typedef {Object} ValidationResponse
 * @property {boolean} success - Indica se a validação foi bem-sucedida
 * @property {UserInfo} [user] - Informações do usuário (presente se success=true)
 * @property {string} [error] - Mensagem de erro (presente se success=false)
 */

/**
 * Serviço de autenticação
 * @namespace authService
 */
export const authService = {
  /**
   * Realiza o login do usuário no sistema
   *
   * @async
   * @function login
   * @memberof authService
   * @param {string} email - Email do usuário (formato: usuario@dominio.com)
   * @param {string} senha - Senha do usuário (mínimo 6 caracteres)
   * @returns {Promise<LoginResponse>} Objeto contendo o resultado do login
   * @throws {Error} Lança erro se houver problema de rede ou servidor
   *
   * @example
   * const result = await authService.login('admin@gestor.com', 'admin123')
   * if (result.success) {
   *   console.log('Login realizado com sucesso!')
   *   console.log('Token:', result.token)
   * } else {
   *   console.error('Erro:', result.error)
   * }
   *
   * @description
   * Esta função:
   * 1. Envia credenciais para o endpoint /auth/login
   * 2. Armazena o token JWT no sessionStorage em caso de sucesso
   * 3. Remove qualquer token anterior em caso de falha
   * 4. Retorna mensagens de erro apropriadas para cada situação
   *
   * Códigos de erro HTTP tratados:
   * - 401/403: Credenciais incorretas
   * - 500: Erro interno do servidor
   * - Sem resposta: Problema de conexão
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
        // Usa mensagem específica do backend se disponível
        const backendMessage = error.response.data?.message
        if (backendMessage) {
          errorMessage = backendMessage
        } else {
          // Fallback para mensagens genéricas por status
          switch (error.response.status) {
            case 401:
            case 403:
              errorMessage = 'Email ou senha incorretos'
              break
            case 500:
              errorMessage = 'Erro interno do servidor'
              break
            default:
              errorMessage = error.response.data?.error || 'Erro do servidor'
          }
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
   * Valida um token JWT e extrai informações do usuário
   *
   * @async
   * @function validateToken
   * @memberof authService
   * @param {string} token - Token JWT a ser validado
   * @returns {Promise<ValidationResponse>} Objeto contendo o resultado da validação
   *
   * @example
   * const token = sessionStorage.getItem('authToken')
   * const validation = await authService.validateToken(token)
   *
   * if (validation.success) {
   *   console.log('Usuário:', validation.user.nome)
   *   console.log('Email:', validation.user.email)
   *   console.log('Role:', validation.user.role)
   * } else {
   *   console.error('Token inválido:', validation.error)
   * }
   *
   * @description
   * Esta função:
   * 1. Verifica o formato do token JWT (deve ter 3 partes separadas por '.')
   * 2. Decodifica o payload do token (base64)
   * 3. Valida a data de expiração (campo 'exp')
   * 4. Extrai informações do usuário (email, nome, role)
   * 5. Retorna os dados do usuário em caso de sucesso
   *
   * Nota: Esta validação é local (client-side) e não verifica a assinatura do token.
   * A validação completa é feita pelo backend em cada requisição.
   *
   * Estrutura do payload JWT esperada:
   * - sub: email do usuário
   * - nome: nome completo
   * - exp: timestamp de expiração
   * - roles: array de objetos com authority (ex: [{"authority": "ROLE_ADMIN"}])
   */
  async validateToken(token) {
    try {
      // Usa utilitário centralizado para validar token
      if (!isTokenValid(token)) {
        throw new Error('Token inválido ou expirado')
      }

      // Extrai informações do usuário usando utilitário centralizado
      const userInfo = extractUserInfo(token)

      if (!userInfo) {
        throw new Error('Não foi possível extrair informações do token')
      }

      return {
        success: true,
        user: userInfo
      }
    } catch (error) {
      return {
        success: false,
        error: error.message || 'Token inválido'
      }
    }
  },

  /**
   * Solicita recuperação de senha via email
   *
   * @async
   * @function forgotPassword
   * @memberof authService
   * @param {string} _email - Email para recuperação de senha
   * @returns {Promise<{success: boolean, message?: string, error?: string}>} Resultado da operação
   *
   * @example
   * const result = await authService.forgotPassword('usuario@email.com')
   * if (result.success) {
   *   console.log(result.message)
   * }
   *
   * @description
   * Função placeholder para implementação futura da recuperação de senha.
   * Atualmente simula o envio e retorna mensagem genérica.
   *
   * TODO: Implementar integração com endpoint real do backend quando disponível.
   * Endpoint esperado: POST /auth/forgot-password
   *
   * @note Esta função está preparada para quando o backend implementar
   * o sistema de recuperação de senha por email.
   */
  async forgotPassword(_email) {
    try {
      // Por enquanto, apenas simula o envio
      // Quando o endpoint estiver disponível no backend, substitua por:
      // const response = await api.post('/auth/forgot-password', { email })

      await new Promise(resolve => setTimeout(resolve, 1000)) // Simula delay

      return {
        success: true,
        message: 'Se o e-mail estiver cadastrado, você receberá as instruções de recuperação em instantes.'
      }
    } catch {
      return {
        success: false,
        error: 'Não foi possível solicitar a recuperação de senha no momento.'
      }
    }
  },

  /**
   * Remove o token de autenticação e realiza logout
   *
   * @function logout
   * @memberof authService
   * @returns {void}
   *
   * @example
   * authService.logout()
   * console.log('Usuário deslogado')
   *
   * @description
   * Remove o token JWT armazenado no sessionStorage.
   * Após o logout, o usuário precisará fazer login novamente.
   *
   * Nota: Esta função não faz chamada ao backend, apenas remove
   * o token localmente. Os tokens JWT são stateless.
   */
  logout() {
    sessionStorage.removeItem('authToken')
  },

  /**
   * Recupera o token JWT do sessionStorage
   *
   * @function getToken
   * @memberof authService
   * @returns {string|null} Token JWT ou null se não existir
   *
   * @example
   * const token = authService.getToken()
   * if (token) {
   *   console.log('Token encontrado:', token)
   * } else {
   *   console.log('Usuário não autenticado')
   * }
   *
   * @description
   * Retorna o token armazenado no sessionStorage.
   * Retorna null se não houver token (usuário não autenticado).
   */
  getToken() {
    return sessionStorage.getItem('authToken')
  },

  /**
   * Verifica se o usuário está autenticado com token válido
   *
   * @function isAuthenticated
   * @memberof authService
   * @returns {boolean} true se o usuário possui token válido, false caso contrário
   *
   * @example
   * if (authService.isAuthenticated()) {
   *   console.log('Usuário autenticado')
   *   // Permitir acesso à área protegida
   * } else {
   *   console.log('Usuário não autenticado')
   *   // Redirecionar para login
   * }
   *
   * @description
   * Verifica se existe um token válido no sessionStorage.
   *
   * Validações realizadas:
   * 1. Verifica se o token existe
   * 2. Decodifica o payload do token
   * 3. Verifica se o token não expirou (campo 'exp')
   * 4. Remove o token automaticamente se estiver inválido ou expirado
   *
   * Esta função é útil para:
   * - Proteger rotas no Vue Router
   * - Verificar autenticação antes de requisições
   * - Exibir/ocultar elementos baseado no estado de autenticação
   *
   * @note Esta é uma validação client-side. O backend sempre valida
   * o token em cada requisição.
   */
  isAuthenticated() {
    const token = this.getToken()
    if (!token) return false

    try {
      // Usa utilitário centralizado para validar token
      const valid = isTokenValid(token)

      if (!valid) {
        console.warn('Token inválido ou expirado')
        this.logout() // Remove token inválido
        return false
      }

      return true
    } catch (error) {
      console.warn('Erro ao validar token:', error.message)
      this.logout() // Remove token inválido
      return false
    }
  }
}





