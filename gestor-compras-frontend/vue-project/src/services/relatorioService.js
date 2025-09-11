/**
 * Serviço de Relatórios
 *
 * Este arquivo contém funções específicas para geração e download
 * de relatórios da aplicação.
 *
 * Funcionalidades:
 * - Geração de relatório de fornecedores em PDF
 * - Download automático de arquivos PDF
 */

import axios from 'axios'

// Configuração da URL base da API
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'

/**
 * Cria uma instância específica do Axios para relatórios
 * com configuração para receber dados binários (PDF)
 */
const relatorioClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000, // 30 segundos para relatórios que podem demorar mais
  responseType: 'blob' // Importante para receber dados binários (PDF)
})

/**
 * Interceptador para adicionar token de autenticação
 */
relatorioClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

/**
 * Interceptador de resposta para tratar erros
 */
relatorioClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken')
      localStorage.removeItem('user')
      window.location.href = '/login'
      return Promise.reject(new Error('Sessão expirada. Faça login novamente.'))
    }
    return Promise.reject(error)
  }
)

/**
 * Serviço de Relatórios
 */
const relatorioService = {
  /**
   * Gera e faz download do relatório de fornecedores
   * @returns {Promise<void>}
   */
  async gerarRelatorioFornecedores() {
    try {
      const response = await relatorioClient.get('/relatorios/fornecedores')

      // Cria um blob com os dados do PDF
      const blob = new Blob([response.data], { type: 'application/pdf' })

      // Cria uma URL temporária para o blob
      const url = window.URL.createObjectURL(blob)

      // Cria um elemento <a> temporário para fazer o download
      const link = document.createElement('a')
      link.href = url
      link.download = 'relatorio-fornecedores.pdf'

      // Adiciona o elemento ao DOM, clica nele e depois remove
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)

      // Libera a URL temporária
      window.URL.revokeObjectURL(url)

      return true
    } catch (error) {
      console.error('Erro ao gerar relatório de fornecedores:', error)
      throw new Error('Erro ao gerar relatório. Tente novamente.')
    }
  },

  /**
   * Visualiza o relatório de fornecedores em uma nova aba
   * @returns {Promise<void>}
   */
  async visualizarRelatorioFornecedores() {
    try {
      const response = await relatorioClient.get('/relatorios/fornecedores')

      // Cria um blob com os dados do PDF
      const blob = new Blob([response.data], { type: 'application/pdf' })

      // Cria uma URL temporária para o blob
      const url = window.URL.createObjectURL(blob)

      // Abre o PDF em uma nova aba
      window.open(url, '_blank')

      // Libera a URL após um tempo para permitir que o navegador carregue
      setTimeout(() => {
        window.URL.revokeObjectURL(url)
      }, 1000)

      return true
    } catch (error) {
      console.error('Erro ao visualizar relatório de fornecedores:', error)
      throw new Error('Erro ao visualizar relatório. Tente novamente.')
    }
  }
}

export default relatorioService
