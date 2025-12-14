/**
 * @fileoverview Serviço de Geração e Download de Relatórios
 * 
 * Módulo responsável pela geração e download de relatórios em formato PDF.
 * Utiliza instância dedicada do Axios configurada para recebimento de dados
 * binários (blob) com timeout estendido para operações mais demoradas.
 * 
 * @module services/relatorioService
 * @requires axios
 * @requires ../utils/logger
 * 
 * @description
 * Este serviço implementa:
 * - Geração de relatórios de fornecedores (Produto e Serviço)
 * - Geração de relatórios de solicitações de pedido
 * - Geração de relatórios de cotações
 * - Geração de relatórios comparativos de cotações
 * - Geração de relatório executivo (dashboard)
 * - Download automático de arquivos PDF gerados
 * - Interceptação de requisições para autenticação via JWT
 * - Tratamento especializado de erros de geração
 * 
 * @example
 * // Gerar relatório de fornecedores
 * await relatorioService.gerarRelatorioFornecedores()
 * 
 * @example
 * // Gerar relatório de pedidos com filtro
 * await relatorioService.gerarRelatorioPedidos({ status: 'APROVADO' })
 * 
 * @example
 * // Gerar relatório comparativo de cotações
 * await relatorioService.gerarRelatorioComparativoCotacoes(123)
 * 
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

import axios from 'axios'
import logger from '../utils/logger.js'

// Configuração da URL base da API
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'

/**
 * Cliente Axios configurado para download de relatórios em PDF
 * 
 * @const {axios.AxiosInstance} relatorioClient
 * @description
 * Instância específica do Axios com:
 * - responseType: 'blob' para receber dados binários (PDF)
 * - timeout: 30 segundos (relatórios podem demorar mais que requisições normais)
 * - baseURL: Configurada via variável de ambiente VITE_API_BASE_URL
 */
const relatorioClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000, // 30 segundos para relatórios que podem demorar mais
  responseType: 'blob' // Importante para receber dados binários (PDF)
})

/**
 * Interceptador para adicionar token de autenticação JWT
 * @description
 * Adiciona automaticamente o token do sessionStorage em todas as requisições de relatórios
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
 * Serviço principal de gerenciamento de relatórios
 * @namespace relatorioService
 */
const relatorioService = {
  /**
   * Gera e faz download automático do relatório de fornecedores em PDF
   * 
   * @async
   * @function gerarRelatorioFornecedores
   * @memberof relatorioService
   * @returns {Promise<boolean>} true se download bem-sucedido
   * @throws {Error} Erro ao gerar relatório
   * 
   * @example
   * await relatorioService.gerarRelatorioFornecedores()
   * // Arquivo 'relatorio-fornecedores.pdf' será baixado automaticamente
   * 
   * @description
   * Gera relatório PDF de todos os fornecedores (produto e serviço) e
   * inicia download automático no navegador.
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
      logger.error('Erro ao gerar relatório de fornecedores:', error)
      throw new Error('Erro ao gerar relatório. Tente novamente.')
    }
  },

  /**
   * Visualiza o relatório de fornecedores em PDF em nova aba do navegador
   * 
   * @async
   * @function visualizarRelatorioFornecedores
   * @memberof relatorioService
   * @returns {Promise<boolean>} true se visualização bem-sucedida
   * @throws {Error} Erro ao visualizar relatório
   * 
   * @example
   * await relatorioService.visualizarRelatorioFornecedores()
   * // Nova aba será aberta com o PDF
   * 
   * @description
   * Gera relatório PDF e abre em nova aba do navegador sem fazer download.
   * URL do blob é liberada após 1 segundo.
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
      logger.error('Erro ao visualizar relatório de fornecedores:', error)
      throw new Error('Erro ao visualizar relatório. Tente novamente.')
    }
  },

  /**
   * Gera e faz download do relatório de todos os itens de pedido em PDF
   * 
   * @async
   * @function gerarRelatorioItensPedido
   * @memberof relatorioService
   * @returns {Promise<boolean>} true se download bem-sucedido
   * @throws {Error} Erro ao gerar relatório
   * 
   * @example
   * await relatorioService.gerarRelatorioItensPedido()
   * // Arquivo 'relatorio-itens-pedido.pdf' será baixado
   * 
   * @description
   * Gera relatório consolidado de todos os itens de pedidos cadastrados.
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
      logger.error('Erro ao gerar relatório de itens de pedido:', error)
      throw new Error('Erro ao gerar relatório. Tente novamente.')
    }
  },

  /**
   * Visualiza o relatório de itens de pedido em PDF em nova aba
   * 
   * @async
   * @function visualizarRelatorioItensPedido
   * @memberof relatorioService
   * @returns {Promise<boolean>} true se visualização bem-sucedida
   * @throws {Error} Erro ao visualizar relatório
   * 
   * @example
   * await relatorioService.visualizarRelatorioItensPedido()
   * 
   * @description
   * Abre relatório de itens de pedido em nova aba do navegador.
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
      logger.error('Erro ao visualizar relatório de itens de pedido:', error)
      throw new Error('Erro ao visualizar relatório. Tente novamente.')
    }
  },

  /**
   * Gera e faz download do relatório detalhado de um item de pedido específico
   * 
   * @async
   * @function gerarRelatorioItemPedido
   * @memberof relatorioService
   * @param {number} id - ID do item de pedido
   * @returns {Promise<boolean>} true se download bem-sucedido
   * @throws {Error} Erro ao gerar relatório ou item não encontrado
   * 
   * @example
   * await relatorioService.gerarRelatorioItemPedido(123)
   * // Arquivo 'relatorio-item-pedido-123.pdf' será baixado
   * 
   * @description
   * Gera relatório detalhado de um único item de pedido com todas as informações.
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
      logger.error(`Erro ao gerar relatório do item de pedido ${id}:`, error)
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
      logger.error(`Erro ao visualizar relatório do item de pedido ${id}:`, error)
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
      logger.error('Erro ao gerar relatório de itens para cotação:', error)
      throw new Error('Erro ao gerar relatório. Tente novamente.')
    }
  },

  /**
   * Visualiza relatório de itens para cotação em nova aba do navegador
   * 
   * @async
   * @function visualizarRelatorioItensParaCotacao
   * @memberof relatorioService
   * @param {number} solicitacaoId - ID da solicitação/pedido
   * @param {Array<number>} [itensIds=[]] - IDs dos itens a incluir (vazio = todos)
   * @returns {Promise<boolean>} true se visualização bem-sucedida
   * @throws {Error} Erro ao visualizar relatório
   * 
   * @example
   * // Visualizar todos os itens do pedido 10
   * await relatorioService.visualizarRelatorioItensParaCotacao(10)
   * 
   * @example
   * // Visualizar apenas itens específicos
   * await relatorioService.visualizarRelatorioItensParaCotacao(10, [1, 2, 3])
   * 
   * @description
   * Útil para enviar relatório aos fornecedores durante processo de cotação.
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
      logger.error('Erro ao visualizar relatório de itens para cotação:', error)
      throw new Error('Erro ao visualizar relatório. Tente novamente.')
    }
  },

  /**
   * Visualiza relatório de itens para cotação de um rascunho em nova aba
   * 
   * @async
   * @function visualizarRelatorioItensParaCotacaoRascunho
   * @memberof relatorioService
   * @param {number} rascunhoId - ID do rascunho de pedido
   * @param {Array<number>} [itensIds=[]] - IDs dos itens a incluir (vazio = todos)
   * @returns {Promise<boolean>} true se visualização bem-sucedida
   * @throws {Error} Erro ao visualizar relatório
   * 
   * @example
   * await relatorioService.visualizarRelatorioItensParaCotacaoRascunho(5, [1, 2])
   * 
   * @description
   * Similar a visualizarRelatorioItensParaCotacao, mas para pedidos em rascunho.
   * Permite pré-visualização antes de finalizar o pedido.
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
      logger.error('Erro ao visualizar relatório de itens para cotação (rascunho):', error)
      throw new Error('Erro ao visualizar relatório. Tente novamente.')
    }
  }
}

/**
 * @exports relatorioService
 * @default
 */
export default relatorioService
