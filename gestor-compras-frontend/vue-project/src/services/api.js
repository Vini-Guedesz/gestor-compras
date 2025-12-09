/**
 * Serviço de API - Cliente HTTP centralizado
 *
 * Este arquivo configura um cliente HTTP usando Axios para comunicação
 * com o backend da aplicação.
 *
 * Funcionalidades principais:
 * - Configuração centralizada da URL base da API
 * - Interceptadores para adicionar automaticamente tokens de autenticação
 * - Tratamento global de erros HTTP
 * - Métodos simplificados para requisições HTTP
 *
 * Configurações:
 * - URL base: obtida de variável de ambiente ou fallback para localhost:8080
 * - Timeout: 10 segundos para evitar requisições infinitas
 * - Headers padrão: Content-Type application/json
 */

import axios from 'axios'
import logger from '../utils/logger.js'

// Configuração da URL base da API usando variável de ambiente
// Se VITE_API_BASE_URL não estiver definida, usa localhost:8081 (porta do backend)
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'

// Flag para evitar múltiplos redirects para login
let isRedirecting = false

/**
 * Instância configurada do Axios
 *
 * Configurações aplicadas:
 * - URL base para todas as requisições
 * - Headers padrão para JSON
 * - Timeout para evitar requisições que não respondem
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

    // DEBUG: Log do body sendo enviado
    if (config.data && config.url.includes('fornecedores')) {
      if (config.data.contato) {
      }
      if (config.data.endereco) {
      }
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
 * API wrapper - métodos simplificados para requisições HTTP
 *
 * Encapsula os métodos do Axios retornando diretamente os dados
 * da resposta, sem a necessidade de acessar response.data
 */
const api = {
  /**
   * Requisição GET
   * @param {string} endpoint - Endpoint da API (ex: '/users')
   * @returns {Promise} Dados da resposta
   */
  async get(endpoint) {
    const response = await apiClient.get(endpoint)
    return response.data
  },

  /**
   * Requisição POST
   * @param {string} endpoint - Endpoint da API
   * @param {Object} data - Dados a serem enviados no corpo da requisição
   * @returns {Promise} Dados da resposta
   */
  async post(endpoint, data) {
    const response = await apiClient.post(endpoint, data)
    return response.data
  },

  /**
   * Requisição PATCH
   * @param {string} endpoint - Endpoint da API
   * @param {Object} data - Dados a serem enviados no corpo da requisição
   * @returns {Promise} Dados da resposta
   */
  async patch(endpoint, data) {
    const response = await apiClient.patch(endpoint, data)
    return response.data
  },

  /**
   * Requisição PUT
   * @param {string} endpoint - Endpoint da API
   * @param {Object} data - Dados a serem enviados no corpo da requisição
   * @returns {Promise} Dados da resposta
   */
  async put(endpoint, data) {
    const response = await apiClient.put(endpoint, data)
    return response.data
  },

  /**
   * Requisição DELETE
   * @param {string} endpoint - Endpoint da API
   * @returns {Promise} Dados da resposta (ou true se 204 No Content)
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

export default api
