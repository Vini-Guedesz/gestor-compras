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

// Configuração da URL base da API usando variável de ambiente
// Se VITE_API_BASE_URL não estiver definida, usa localhost:8080
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

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
    // Recupera o token do localStorage
    const token = localStorage.getItem('auth-token')
    if (token) {
      // Adiciona o token no header de autorização
      config.headers.Authorization = `Bearer ${token}`
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
    // Tratamento específico para erro 401 (Não Autorizado)
    if (error.response?.status === 401) {
      // Token provavelmente expirado: limpa dados de autenticação
      localStorage.removeItem('auth-token')
      localStorage.removeItem('user')
      // Redireciona para login
      window.location.href = '/login'
      return Promise.reject(new Error('Sessão expirada. Faça login novamente.'))
    }

    // Tratamento de outros erros HTTP
    if (error.response) {
      // Erro retornado pela API (4xx, 5xx)
      const message = error.response.data?.message || `Erro HTTP: ${error.response.status}`
      return Promise.reject(new Error(message))
    } else if (error.request) {
      // Erro de rede (servidor não respondeu)
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
   * @returns {Promise} Dados da resposta
   */
  async delete(endpoint) {
    const response = await apiClient.delete(endpoint)
    return response.data
  }
}

export default api
