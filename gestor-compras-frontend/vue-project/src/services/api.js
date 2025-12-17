/**
 * @fileoverview Cliente HTTP centralizado usando Axios
 * @module services/api
 * @description
 * Cliente HTTP configurado com Axios para comunicação com o backend.
 * Gerencia autenticação automática via JWT, tratamento global de erros
 * e interceptadores de requisição/resposta.
 *
 * @example
 * import api from '@/services/api'
 *
 * // GET request
 * const users = await api.get('/users')
 *
 * // POST request
 * const newUser = await api.post('/users', { name: 'João' })
 *
 * // PUT request
 * const updated = await api.put('/users/1', { name: 'João Silva' })
 *
 * // DELETE request
 * await api.delete('/users/1')
 *
 * @author Equipe de Desenvolvimento
 * @version 1.0.0
 * @since 2024
 *
 * @requires axios
 * @requires utils/logger
 *
 * @see {@link https://axios-http.com/docs/intro|Axios Documentation}
 */

import axios from 'axios'
import logger from '../utils/logger.js'

/**
 * URL base da API obtida de variável de ambiente
 * @constant {string}
 * @default 'http://localhost:8081'
 */
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'

/**
 * Flag para evitar múltiplos redirects simultâneos para login
 * @private
 * @type {boolean}
 */
let isRedirecting = false

/**
 * Instância configurada do Axios
 * @constant {AxiosInstance}
 * @private
 *
 * @property {string} baseURL - URL base para todas as requisições
 * @property {Object} headers - Headers padrão
 * @property {string} headers.Content-Type - Tipo de conteúdo JSON
 * @property {number} timeout - Timeout de 10 segundos
 */
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000 // 10 segundos
})

/**
 * Interceptador de Requisições
 *
 * Executa antes de cada requisição ser enviada.
 * Adiciona automaticamente o token de autenticação
 * no header Authorization quando disponível.
 */
apiClient.interceptors.request.use(
  (config) => {
    // Recupera o token do sessionStorage (consistente com authService.js)
    const token = sessionStorage.getItem('authToken')
    if (token) {
      // Adiciona o token no header de autorização
      config.headers.Authorization = `Bearer ${token}`
    }

    // IMPORTANTE: Se for FormData, remover Content-Type para Axios configurar automaticamente
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

/**
 * Interceptador de Respostas
 *
 * Executa após cada resposta ser recebida.
 * Trata erros globalmente e implementa comportamentos
 * automáticos como logout em caso de token expirado.
 */
apiClient.interceptors.response.use(
  (response) => {
    // Resposta bem-sucedida: retorna sem modificações
    return response
  },
  (error) => {
    // Tratamento específico para erro 401 (Não Autorizado) ou JWT expirado
    if (error.response?.status === 401 ||
        (error.response?.data && typeof error.response.data === 'string' && error.response.data.includes('JWT expired'))) {

      // Evitar múltiplos redirects simultâneos
      if (isRedirecting) {
        return Promise.reject(new Error('Sessão expirada. Faça login novamente.'))
      }

      isRedirecting = true

      // Token provavelmente expirado: limpa dados de autenticação do sessionStorage
      sessionStorage.removeItem('authToken')

      // Emite evento customizado para notificar a aplicação sobre logout
      // O router guard irá detectar a falta de autenticação e redirecionar suavemente
      window.dispatchEvent(new CustomEvent('auth:logout', {
        detail: { reason: 'token_expired' }
      }))

      // Reseta a flag após um pequeno delay
      setTimeout(() => {
        isRedirecting = false
      }, 1000)

      return Promise.reject(new Error('Sessão expirada. Faça login novamente.'))
    }

    // Tratamento de outros erros HTTP
    if (error.response) {
      // Erro retornado pela API (4xx, 5xx)

      // Verifica se é um erro de JWT expirado (verificação adicional)
      if (typeof error.response.data === 'string' && error.response.data.includes('JWT expired')) {
        if (!isRedirecting) {
          isRedirecting = true
          sessionStorage.removeItem('authToken')
          window.dispatchEvent(new CustomEvent('auth:logout', {
            detail: { reason: 'token_expired' }
          }))
          setTimeout(() => {
            isRedirecting = false
          }, 1000)
        }
        return Promise.reject(new Error('Sessão expirada. Faça login novamente.'))
      }

      // Tenta extrair mensagem de erro de diferentes formatos possíveis
      let message = ''
      let detailedErrors = []

      // Formato do nosso RestExceptionHandler: campo "messages" com array de strings
      if (error.response.data?.messages && Array.isArray(error.response.data.messages)) {
        detailedErrors = error.response.data.messages
      }

      // Formato Spring Boot Validation Error (MethodArgumentNotValidException)
      if (error.response.data?.errors) {
        if (Array.isArray(error.response.data.errors)) {
          // Array de erros
          detailedErrors = error.response.data.errors.map(err => {
            if (typeof err === 'string') return err
            if (err.field && err.defaultMessage) {
              return `${err.field}: ${err.defaultMessage}`
            }
            if (err.message) return err.message
            return JSON.stringify(err)
          })
        } else if (typeof error.response.data.errors === 'object') {
          // Objeto com erros por campo
          detailedErrors = Object.entries(error.response.data.errors).map(
            ([field, msg]) => `${field}: ${msg}`
          )
        }
      }

      // Formato com fieldErrors (Spring Boot)
      if (error.response.data?.fieldErrors && Array.isArray(error.response.data.fieldErrors)) {
        detailedErrors = error.response.data.fieldErrors.map(err =>
          `${err.field}: ${err.message || err.defaultMessage}`
        )
      }

      // Mensagem principal
      message = error.response.data?.message || error.response.data?.error || error.response.data?.title

      // Se for string simples
      if (!message && typeof error.response.data === 'string') {
        message = error.response.data
      }

      // Monta mensagem final
      if (detailedErrors.length > 0) {
        logger.error('❌ Erros de validação detalhados:', detailedErrors)
        message = `Erro de validação:\n${detailedErrors.join('\n')}`
      } else if (!message) {
        message = `Erro HTTP ${error.response.status}`
      }

      logger.error('📛 Mensagem final do erro:', message)
      return Promise.reject(new Error(message))
    } else if (error.request) {
      // Erro de rede (servidor não respondeu)
      const errorMessage = error.message || error.code
      if (errorMessage.includes('CORS') || errorMessage.includes('Access-Control')) {
        return Promise.reject(new Error('Erro de CORS: Backend não configurado para aceitar requisições do frontend.'))
      } else if (error.code === 'ECONNREFUSED') {
        return Promise.reject(new Error('Erro de conexão: Backend não está rodando ou não está acessível.'))
      } else if (errorMessage.includes('Network Error')) {
        return Promise.reject(new Error('Erro de rede: Verifique sua conexão ou se o backend está rodando.'))
      }
      return Promise.reject(new Error('Erro de conexão com o servidor. Verifique se o backend está rodando.'))
    } else {
      // Outros tipos de erro
      return Promise.reject(new Error(error.message || 'Erro inesperado'))
    }
  }
)

/**
 * Objeto API com métodos simplificados para requisições HTTP
 * @namespace api
 * @description
 * Wrapper em torno do Axios que simplifica as requisições HTTP,
 * retornando diretamente os dados sem precisar acessar response.data
 */
const api = {
  /**
   * Realiza requisição HTTP GET
   *
   * @async
   * @function get
   * @memberof api
   * @param {string} endpoint - Caminho do endpoint (ex: '/users', '/pedidos/123')
   * @returns {Promise<*>} Dados da resposta (response.data)
   * @throws {Error} Erro tratado pelos interceptadores
   *
   * @example
   * // Buscar lista de usuários
   * const users = await api.get('/api/v1/users')
   *
   * // Buscar usuário específico
   * const user = await api.get('/api/v1/users/123')
   *
   * @description
   * Método GET para buscar recursos do servidor.
   * O token JWT é adicionado automaticamente pelo interceptador.
   */
  async get(endpoint) {
    const response = await apiClient.get(endpoint)
    return response.data
  },

  /**
   * Realiza requisição HTTP POST
   *
   * @async
   * @function post
   * @memberof api
   * @param {string} endpoint - Caminho do endpoint
   * @param {Object|FormData} data - Dados a serem enviados no corpo da requisição
   * @returns {Promise<*>} Dados da resposta (response.data)
   * @throws {Error} Erro tratado pelos interceptadores
   *
   * @example
   * // Criar novo usuário
   * const newUser = await api.post('/api/v1/users', {
   *   nome: 'João Silva',
   *   email: 'joao@email.com'
   * })
   *
   * // Enviar FormData (upload de arquivo)
   * const formData = new FormData()
   * formData.append('file', file)
   * const result = await api.post('/api/v1/upload', formData)
   *
   * @description
   * Método POST para criar novos recursos no servidor.
   * Suporta tanto JSON quanto FormData (para uploads).
   */
  async post(endpoint, data) {
    const response = await apiClient.post(endpoint, data)
    return response.data
  },

  /**
   * Realiza requisição HTTP PATCH
   *
   * @async
   * @function patch
   * @memberof api
   * @param {string} endpoint - Caminho do endpoint
   * @param {Object} data - Dados parciais a serem atualizados
   * @returns {Promise<*>} Dados da resposta (response.data)
   * @throws {Error} Erro tratado pelos interceptadores
   *
   * @example
   * // Atualizar apenas o nome do usuário
   * const updated = await api.patch('/api/v1/users/123', {
   *   nome: 'João Silva Atualizado'
   * })
   *
   * @description
   * Método PATCH para atualização parcial de recursos.
   * Envia apenas os campos que precisam ser atualizados.
   */
  async patch(endpoint, data) {
    const response = await apiClient.patch(endpoint, data)
    return response.data
  },

  /**
   * Realiza requisição HTTP PUT
   *
   * @async
   * @function put
   * @memberof api
   * @param {string} endpoint - Caminho do endpoint
   * @param {Object} data - Dados completos do recurso
   * @returns {Promise<*>} Dados da resposta (response.data)
   * @throws {Error} Erro tratado pelos interceptadores
   *
   * @example
   * // Atualizar usuário completo
   * const updated = await api.put('/api/v1/users/123', {
   *   id: 123,
   *   nome: 'João Silva',
   *   email: 'joao@email.com',
   *   telefone: '11999999999'
   * })
   *
   * @description
   * Método PUT para atualização completa de recursos.
   * Deve enviar todos os campos do recurso, não apenas os alterados.
   */
  async put(endpoint, data) {
    const response = await apiClient.put(endpoint, data)
    return response.data
  },

  /**
   * Realiza requisição HTTP DELETE
   *
   * @async
   * @function delete
   * @memberof api
   * @param {string} endpoint - Caminho do endpoint
   * @returns {Promise<*|boolean>} Dados da resposta ou true para 204 No Content
   * @throws {Error} Erro tratado pelos interceptadores
   *
   * @example
   * // Deletar usuário
   * await api.delete('/api/v1/users/123')
   *
   * // Deletar com retorno de dados
   * const result = await api.delete('/api/v1/pedidos/456')
   * console.log('Pedido deletado:', result)
   *
   * @description
   * Método DELETE para remover recursos do servidor.
   * Retorna true automaticamente se o servidor responder com status 204 (No Content).
   * Caso contrário, retorna os dados da resposta.
   */
  async delete(endpoint) {
    const response = await apiClient.delete(endpoint)
    // Status 204 No Content não tem body, retornar true para indicar sucesso
    if (response.status === 204) {
      return true
    }
    return response.data
  }
}

/**
 * @exports api
 * @default
 */
export default api
