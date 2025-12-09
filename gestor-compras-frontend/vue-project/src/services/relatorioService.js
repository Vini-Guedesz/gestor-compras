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
import logger from '../utils/logger.js'

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
    const token = sessionStorage.getItem('authToken')
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
      sessionStorage.removeItem('authToken')
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
  },

  /**
   * Gera e faz download do relatório de itens de pedido
   * @returns {Promise<void>}
   */
  async gerarRelatorioItensPedido() {
    try {
      const response = await relatorioClient.get('/relatorios/itens-pedido')

      // Cria um blob com os dados do PDF
      const blob = new Blob([response.data], { type: 'application/pdf' })

      // Cria uma URL temporária para o blob
      const url = window.URL.createObjectURL(blob)

      // Cria um elemento <a> temporário para fazer o download
      const link = document.createElement('a')
      link.href = url
      link.download = 'relatorio-itens-pedido.pdf'

      // Adiciona o elemento ao DOM, clica nele e depois remove
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)

      // Libera a URL temporária
      window.URL.revokeObjectURL(url)

      return true
    } catch (error) {
      console.error('Erro ao gerar relatório de itens de pedido:', error)
      throw new Error('Erro ao gerar relatório. Tente novamente.')
    }
  },

  /**
   * Visualiza o relatório de itens de pedido em uma nova aba
   * @returns {Promise<void>}
   */
  async visualizarRelatorioItensPedido() {
    try {
      const response = await relatorioClient.get('/relatorios/itens-pedido')

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
      console.error('Erro ao visualizar relatório de itens de pedido:', error)
      throw new Error('Erro ao visualizar relatório. Tente novamente.')
    }
  },

  /**
   * Gera e faz download do relatório de um item de pedido específico
   * @param {number} id - ID do item de pedido
   * @returns {Promise<void>}
   */
  async gerarRelatorioItemPedido(id) {
    try {
      const response = await relatorioClient.get(`/relatorios/itens-pedido/${id}`)

      // Cria um blob com os dados do PDF
      const blob = new Blob([response.data], { type: 'application/pdf' })

      // Cria uma URL temporária para o blob
      const url = window.URL.createObjectURL(blob)

      // Cria um elemento <a> temporário para fazer o download
      const link = document.createElement('a')
      link.href = url
      link.download = `relatorio-item-pedido-${id}.pdf`

      // Adiciona o elemento ao DOM, clica nele e depois remove
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)

      // Libera a URL temporária
      window.URL.revokeObjectURL(url)

      return true
    } catch (error) {
      console.error(`Erro ao gerar relatório do item de pedido ${id}:`, error)
      throw new Error('Erro ao gerar relatório. Tente novamente.')
    }
  },

  /**
   * Visualiza o relatório de um item de pedido específico em uma nova aba
   * @param {number} id - ID do item de pedido
   * @returns {Promise<void>}
   */
  async visualizarRelatorioItemPedido(id) {
    try {
      const response = await relatorioClient.get(`/relatorios/itens-pedido/${id}`)

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
      console.error(`Erro ao visualizar relatório do item de pedido ${id}:`, error)
      throw new Error('Erro ao visualizar relatório. Tente novamente.')
    }
  },

  /**
   * Gera e faz download do relatório de itens para cotação
   * @param {number} solicitacaoId - ID da solicitação/pedido
   * @param {Array<number>} itensIds - IDs dos itens a serem incluídos (vazio para todos)
   * @returns {Promise<void>}
   */
  async gerarRelatorioItensParaCotacao(solicitacaoId, itensIds = []) {
    try {
      const payload = {
        solicitacaoId: solicitacaoId,
        itensIds: itensIds
      }

      const response = await relatorioClient.post('/relatorios/itens-para-cotacao', payload)

      // Cria um blob com os dados do PDF
      const blob = new Blob([response.data], { type: 'application/pdf' })

      // Cria uma URL temporária para o blob
      const url = window.URL.createObjectURL(blob)

      // Cria um elemento <a> temporário para fazer o download
      const link = document.createElement('a')
      link.href = url
      link.download = `itens-para-cotacao-${solicitacaoId}.pdf`

      // Adiciona o elemento ao DOM, clica nele e depois remove
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)

      // Libera a URL temporária
      window.URL.revokeObjectURL(url)

      return true
    } catch (error) {
      console.error('Erro ao gerar relatório de itens para cotação:', error)
      throw new Error('Erro ao gerar relatório. Tente novamente.')
    }
  },

  /**
   * Visualiza o relatório de itens para cotação em uma nova aba
   * @param {number} solicitacaoId - ID da solicitação/pedido
   * @param {Array<number>} itensIds - IDs dos itens a serem incluídos (vazio para todos)
   * @returns {Promise<void>}
   */
  async visualizarRelatorioItensParaCotacao(solicitacaoId, itensIds = []) {
    try {
      const payload = {
        solicitacaoId: solicitacaoId,
        itensIds: itensIds
      }

      const response = await relatorioClient.post('/relatorios/itens-para-cotacao', payload)

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
      console.error('Erro ao visualizar relatório de itens para cotação:', error)
      throw new Error('Erro ao visualizar relatório. Tente novamente.')
    }
  },

  /**
   * Visualiza o relatório de itens para cotação de um rascunho em uma nova aba
   * @param {number} rascunhoId - ID do rascunho
   * @param {Array<number>} itensIds - IDs dos itens a serem incluídos (vazio para todos)
   * @returns {Promise<void>}
   */
  async visualizarRelatorioItensParaCotacaoRascunho(rascunhoId, itensIds = []) {
    try {
      const payload = {
        solicitacaoId: rascunhoId,
        itensIds: itensIds
      }

      const response = await relatorioClient.post(
        '/relatorios/itens-para-cotacao-rascunho',
        payload
      )

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
      console.error('Erro ao visualizar relatório de itens para cotação (rascunho):', error)
      throw new Error('Erro ao visualizar relatório. Tente novamente.')
    }
  }
}

export default relatorioService
