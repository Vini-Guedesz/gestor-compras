/**
 * @fileoverview Serviço de Gerenciamento de Cotações
 *
 * Módulo responsável por todas as operações CRUD e lógica de negócios relacionadas
 * às cotações de compras. Gerencia o ciclo completo de cotações, desde a criação
 * até aprovação, incluindo anexos PDF, histórico de edições e relatórios comparativos.
 *
 * @module services/cotacaoService
 * @requires ./api
 * @requires axios
 * @requires ../utils/logger
 *
 * @description
 * Este serviço implementa:
 * - CRUD completo de Cotações
 * - Vinculação de cotações com fornecedores (Produto ou Serviço)
 * - Vinculação de cotações com itens de pedido
 * - Upload e download de anexos PDF (únicos e múltiplos)
 * - Histórico de edições com auditoria
 * - Geração de relatórios comparativos de cotações
 * - Gerenciamento de propostas e seleção de vencedores
 * - Aprovação, cancelamento e reabertura de cotações
 * - Validação de dados antes de envio
 *
 * @typedef {Object} Cotacao
 * @property {number} [id] - ID da cotação
 * @property {number} fornecedorId - ID do fornecedor
 * @property {string} tipoFornecedor - Tipo do fornecedor ('PRODUTO' ou 'SERVICO')
 * @property {number} solicitacaoDePedidoId - ID do pedido vinculado
 * @property {Array<number>} itensPedidoIds - IDs dos itens do pedido cotados
 * @property {number} preco - Valor da cotação
 * @property {number} [prazoEmDiasUteis] - Prazo de entrega em dias úteis
 * @property {string} [dataLimite] - Data limite da cotação (ISO 8601)
 * @property {Array<number>|File} [anexoPdf] - Anexo PDF (bytes ou arquivo)
 * @property {string} [status] - Status da cotação
 *
 * @typedef {Object} CotacaoEditDTO
 * @property {number} [preco] - Novo preço
 * @property {number} [prazoEntrega] - Novo prazo
 * @property {Array<number>|File} [anexoPdf] - Novo anexo PDF
 * @property {string} motivoEdicao - Motivo da edição (mín. 10 caracteres)
 * @property {string} editadoPor - Responsável pela edição
 *
 * @typedef {Object} HistoricoCotacao
 * @property {number} id - ID do registro de histórico
 * @property {number} cotacaoId - ID da cotação
 * @property {string} dataEdicao - Data da edição (ISO 8601)
 * @property {string} editadoPor - Responsável pela edição
 * @property {string} motivoEdicao - Motivo da alteração
 * @property {Object} valoresAnteriores - Valores antes da edição
 * @property {Object} valoresNovos - Valores após a edição
 *
 * @example
 * // Criar nova cotação
 * const cotacao = {
 *   fornecedorId: 5,
 *   tipoFornecedor: 'PRODUTO',
 *   solicitacaoDePedidoId: 10,
 *   itensPedidoIds: [1, 2, 3],
 *   preco: 1500.00,
 *   prazoEmDiasUteis: 15
 * }
 * await cotacaoService.criar(cotacao)
 *
 * @example
 * // Listar cotações por pedido
 * const cotacoes = await cotacaoService.listarPorPedido(10)
 *
 * @example
 * // Gerar relatório comparativo
 * await cotacaoService.gerarRelatorioComparativo(itemPedidoId)
 *
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

import api from './api.js'
import axios from 'axios'
import logger from '../utils/logger.js'

const BASE_URL = '/api/v1/cotacoes'

// Configuração da URL base da API
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'

/**
 * Extrai conteúdo de respostas paginadas do Spring Data
 *
 * @function extractContent
 * @param {Object|Array<*>} response - Resposta da API
 * @returns {Array<*>} Array de elementos extraídos
 *
 * @description
 * Helper para normalizar respostas da API. O Spring Data REST retorna objetos
 * paginados com estrutura { content: [], totalPages, totalElements, ... }.
 * Esta função extrai o array 'content' ou retorna array vazio se não encontrado.
 *
 * @example
 * const response = { content: [cotacao1, cotacao2], totalPages: 1 }
 * const cotacoes = extractContent(response) // [cotacao1, cotacao2]
 */
const extractContent = (response) => {
  if (response && typeof response === 'object' && 'content' in response) {
    return response.content || []
  }
  return Array.isArray(response) ? response : []
}

/**
 * Cliente Axios específico para relatórios
 *
 * @constant {AxiosInstance} relatorioClient
 * @description
 * Instância configurada do Axios para receber dados binários (PDF).
 * Configurações:
 * - baseURL: API_BASE_URL
 * - timeout: 30 segundos (relatórios podem demorar mais)
 * - responseType: 'blob' (dados binários)
 * - Interceptor de autenticação com JWT
 */
const relatorioClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000, // 30 segundos para relatórios que podem demorar mais
  responseType: 'blob' // Importante para receber dados binários (PDF)
})

/**
 * Interceptador de requisições - adiciona token JWT
 * @description Intercepta todas as requisições para adicionar o token de autenticação no header
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
    logger.error('❌ Erro no interceptor de request:', error)
    return Promise.reject(error)
  }
)

/**
 * Interceptador de respostas - trata erros de autenticação
 * @description Intercepta respostas para tratar erro 401 (não autorizado) e redirecionar para login
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
 * Serviço principal de gerenciamento de cotações
 * @namespace cotacaoService
 */
export const cotacaoService = {
  /**
   * Lista todas as cotações com filtros opcionais
   *
   * @async
   * @function listar
   * @memberof cotacaoService
   * @param {Object} [filtros={}] - Filtros de busca
   * @param {string} [filtros.status] - Filtrar por status
   * @param {number} [filtros.fornecedor] - Filtrar por ID do fornecedor
   * @param {string} [filtros.periodo] - Filtrar por período
   * @param {string} [filtros.busca] - Termo de busca genérica
   * @param {number} [filtros.pagina] - Número da página
   * @param {number} [filtros.tamanho] - Tamanho da página
   * @returns {Promise<Array<Cotacao>>} Array de cotações
   * @throws {Error} Erro de comunicação com API
   *
   * @example
   * const cotacoes = await cotacaoService.listar({ status: 'PENDENTE' })
   */
  async listar(filtros = {}) {
    try {
      const params = new URLSearchParams()

      if (filtros.status) params.append('status', filtros.status)
      if (filtros.fornecedor) params.append('fornecedor', filtros.fornecedor)
      if (filtros.periodo) params.append('periodo', filtros.periodo)
      if (filtros.busca) params.append('busca', filtros.busca)
      if (filtros.pagina) params.append('pagina', filtros.pagina)
      if (filtros.tamanho) params.append('tamanho', filtros.tamanho)

      const url = params.toString() ? `${BASE_URL}?${params}` : BASE_URL
      const response = await api.get(url)
      return extractContent(response)
    } catch (error) {
      logger.error('Erro ao listar cotações:', error)
      throw error
    }
  },

  /**
   * Busca uma cotação específica por ID
   *
   * @async
   * @function buscarPorId
   * @memberof cotacaoService
   * @param {number} id - ID da cotação
   * @returns {Promise<Cotacao>} Dados completos da cotação
   * @throws {Error} Erro 404 se cotação não encontrada
   *
   * @example
   * const cotacao = await cotacaoService.buscarPorId(15)
   */
  async buscarPorId(id) {
    try {
      const response = await api.get(`${BASE_URL}/${id}`)
      return response
    } catch (error) {
      logger.error('Erro ao buscar cotação:', error)
      throw error
    }
  },

  /**
   * Busca cotações de um fornecedor específico
   *
   * @async
   * @function buscarPorFornecedor
   * @memberof cotacaoService
   * @param {number} fornecedorId - ID do fornecedor
   * @returns {Promise<Array<Cotacao>>} Cotações do fornecedor
   * @throws {Error} Erro de comunicação com API
   *
   * @example
   * const cotacoes = await cotacaoService.buscarPorFornecedor(5)
   */
  async buscarPorFornecedor(fornecedorId) {
    try {
      const response = await api.get(`${BASE_URL}`, {
        params: { fornecedorId }
      })
      return extractContent(response)
    } catch (error) {
      logger.error('Erro ao buscar cotações do fornecedor:', error)
      throw error
    }
  },

  /**
   * Lista todas as cotações vinculadas a um pedido
   *
   * @async
   * @function listarPorPedido
   * @memberof cotacaoService
   * @param {number} pedidoId - ID do pedido
   * @returns {Promise<Array<Cotacao>>} Cotações do pedido
   * @throws {Error} Erro ao carregar cotações
   *
   * @example
   * const cotacoes = await cotacaoService.listarPorPedido(10)
   *
   * @description
   * Bug Fix #12: Propaga erro ao invés de retornar array vazio silenciosamente
   */
  async listarPorPedido(pedidoId) {
    try {
      const response = await api.get(`${BASE_URL}/pedido/${pedidoId}`)
      return extractContent(response)
    } catch (error) {
      logger.error('❌ Erro ao buscar cotações do pedido:', error)
      // Bug Fix #12: Propagar erro ao invés de retornar array vazio silenciosamente
      throw new Error(`Erro ao carregar cotações do pedido: ${error.message}`)
    }
  },

  /**
   * Lista todas as cotações vinculadas a um item de pedido
   *
   * @async
   * @function listarPorItem
   * @memberof cotacaoService
   * @param {number} itemPedidoId - ID do item do pedido
   * @returns {Promise<Array<Cotacao>>} Cotações do item
   * @throws {Error} Erro ao carregar cotações
   *
   * @example
   * const cotacoes = await cotacaoService.listarPorItem(25)
   *
   * @description
   * Bug Fix #12: Propaga erro ao invés de retornar array vazio silenciosamente
   */
  async listarPorItem(itemPedidoId) {
    try {
      const response = await api.get(`${BASE_URL}/item/${itemPedidoId}`)
      return extractContent(response)
    } catch (error) {
      logger.error('❌ Erro ao buscar cotações do item:', error)
      // Bug Fix #12: Propagar erro ao invés de retornar array vazio silenciosamente
      throw new Error(`Erro ao carregar cotações do item: ${error.message}`)
    }
  },

  /**
   * Cria uma nova cotação
   *
   * @async
   * @function criar
   * @memberof cotacaoService
   * @param {Cotacao} dadosCotacao - Dados da cotação
   * @returns {Promise<Cotacao>} Cotação criada
   * @throws {Error} Erro de validação ou comunicação
   *
   * @example
   * const cotacao = {
   *   fornecedorId: 5,
   *   tipoFornecedor: 'PRODUTO',
   *   solicitacaoDePedidoId: 10,
   *   itensPedidoIds: [1, 2],
   *   preco: 1500.00,
   *   prazoEmDiasUteis: 15,
   *   anexoPdf: arquivoPDF  // File object
   * }
   * await cotacaoService.criar(cotacao)
   *
   * @description
   * Validações aplicadas:
   * - fornecedorId obrigatório
   * - tipoFornecedor obrigatório (PRODUTO ou SERVICO)
   * - solicitacaoDePedidoId obrigatório
   * - itensPedidoIds deve ter ao menos 1 item
   * - preco deve ser > 0
   * - anexoPdf se fornecido: apenas PDF, máx 10MB
   */
  async criar(dadosCotacao) {
    try {
      // Validação básica conforme CotacaoCreateDTO
      if (!dadosCotacao.fornecedorId) {
        throw new Error('Fornecedor é obrigatório')
      }

      if (!dadosCotacao.tipoFornecedor) {
        throw new Error('Tipo de fornecedor é obrigatório')
      }

      if (!dadosCotacao.solicitacaoDePedidoId) {
        throw new Error('ID da solicitação de pedido é obrigatório')
      }

      if (!dadosCotacao.itensPedidoIds || dadosCotacao.itensPedidoIds.length === 0) {
        throw new Error('Deve selecionar pelo menos um item do pedido')
      }

      if (!dadosCotacao.preco || dadosCotacao.preco <= 0) {
        throw new Error('Preço deve ser maior que zero')
      }


      // Preparar payload conforme CotacaoCreateDTO do backend
      const payload = {
        fornecedorId: dadosCotacao.fornecedorId,
        tipoFornecedor: dadosCotacao.tipoFornecedor, // PRODUTO ou SERVICO
        solicitacaoDePedidoId: dadosCotacao.solicitacaoDePedidoId,
        itensPedidoIds: dadosCotacao.itensPedidoIds,
        preco: parseFloat(dadosCotacao.preco),
        prazoEmDiasUteis: dadosCotacao.prazoEmDiasUteis ? parseInt(dadosCotacao.prazoEmDiasUteis) : null,
        dataLimite: dadosCotacao.dataLimite || null,
        anexoPdf: null
      }

      // Se houver anexo PDF, incluir no payload
      if (dadosCotacao.anexoPdf) {

        // Se já for um array de bytes, usar direto
        if (Array.isArray(dadosCotacao.anexoPdf)) {
          payload.anexoPdf = dadosCotacao.anexoPdf
        } else if (dadosCotacao.anexoPdf instanceof File) {
          // Validar arquivo PDF
          if (dadosCotacao.anexoPdf.type !== 'application/pdf') {
            throw new Error('Apenas arquivos PDF são permitidos')
          }

          const maxSize = 10 * 1024 * 1024 // 10MB
          if (dadosCotacao.anexoPdf.size > maxSize) {
            throw new Error('Arquivo muito grande. Máximo permitido: 10MB')
          }

          const bytesArray = await this.arquivoParaBytes(dadosCotacao.anexoPdf)
          payload.anexoPdf = bytesArray
        }
      }

      const response = await api.post(BASE_URL, payload)
      return response

    } catch (error) {
      logger.error('❌ Erro ao criar cotação:', error)
      throw error
    }
  },

  /**
   * Método de conveniência para salvar cotação (alias para criar)
   *
   * @async
   * @function salvar
   * @memberof cotacaoService
   * @param {Cotacao} dadosCotacao - Dados da cotação
   * @returns {Promise<Cotacao>} Cotação salva
   * @throws {Error} Erro de validação ou comunicação
   *
   * @example
   * await cotacaoService.salvar(dadosCotacao)
   */
  async salvar(dadosCotacao) {
    return this.criar(dadosCotacao)
  },

  /**
   * Atualiza uma cotação existente
   *
   * @async
   * @function atualizar
   * @memberof cotacaoService
   * @param {number} id - ID da cotação
   * @param {CotacaoEditDTO} dadosCotacao - Dados a serem atualizados
   * @returns {Promise<Cotacao>} Cotação atualizada
   * @throws {Error} Erro de validação ou comunicação
   *
   * @example
   * await cotacaoService.atualizar(15, {
   *   preco: 1800.00,
   *   prazoEntrega: 20
   * })
   *
   * @description
   * Apenas campos fornecidos são atualizados. Validações:
   * - preco deve ser > 0
   * - prazoEntrega deve ser ≥ 0
   * - anexoPdf se fornecido: apenas PDF, máx 10MB
   */
  async atualizar(id, dadosCotacao) {
    try {
      // Validação básica
      if (!id) {
        throw new Error('ID da cotação é obrigatório')
      }

      // Preparar dados para envio (CotacaoUpdateDTO)
      const payload = {}

      // Incluir apenas campos válidos do CotacaoUpdateDTO
      if (dadosCotacao.preco !== undefined && dadosCotacao.preco !== null) {
        payload.preco = parseFloat(dadosCotacao.preco)
        if (payload.preco <= 0) {
          throw new Error('Preço deve ser maior que zero')
        }
      }

      if (dadosCotacao.prazoEntrega !== undefined && dadosCotacao.prazoEntrega !== null) {
        payload.prazoEntrega = parseInt(dadosCotacao.prazoEntrega)
        if (payload.prazoEntrega < 0) {
          throw new Error('Prazo de entrega deve ser positivo')
        }
      }

      if (dadosCotacao.anexoPdf !== undefined) {
        // Se o anexoPdf for um arquivo, validar e converter
        if (dadosCotacao.anexoPdf instanceof File) {
          // Validar tipo
          if (dadosCotacao.anexoPdf.type !== 'application/pdf') {
            throw new Error('Apenas arquivos PDF são permitidos')
          }

          // Validar tamanho (10MB)
          const maxSize = 10 * 1024 * 1024
          if (dadosCotacao.anexoPdf.size > maxSize) {
            throw new Error('Arquivo muito grande. Máximo permitido: 10MB')
          }

          // Converter para bytes
          const bytesArray = await this.arquivoParaBytes(dadosCotacao.anexoPdf)
          payload.anexoPdf = bytesArray
        } else {
          // Se já for array de bytes ou null, usar direto
          payload.anexoPdf = dadosCotacao.anexoPdf
        }
      }

      const response = await api.put(`${BASE_URL}/${id}`, payload)
      return response

    } catch (error) {
      logger.error('❌ Erro ao atualizar cotação:', error)
      throw error
    }
  },

  /**
   * Exclui uma cotação
   *
   * @async
   * @function deleteCotacao
   * @memberof cotacaoService
   * @param {number} id - ID da cotação
   * @returns {Promise<boolean>} true se exclusão bem-sucedida
   * @throws {Error} Erro se ID não fornecido ou falha na comunicação
   *
   * @example
   * await cotacaoService.deleteCotacao(15)
   */
  async deleteCotacao(id) {
    try {
      if (!id) {
        throw new Error('ID da cotação é obrigatório')
      }

      await api.delete(`${BASE_URL}/${id}`)
      return true
    } catch (error) {
      logger.error('❌ Erro ao excluir cotação:', error)
      throw error
    }
  },

  // Deletar cotação
  async deletar(id) {
    try {
      const response = await api.delete(`${BASE_URL}/${id}`)
      return response
    } catch (error) {
      logger.error('Erro ao deletar cotação:', error)
      throw error
    }
  },

  // Enviar cotação para fornecedores
  async enviar(id, fornecedoresIds) {
    try {
      const response = await api.post(`${BASE_URL}/${id}/enviar`, {
        fornecedores: fornecedoresIds
      })
      return response.data
    } catch (error) {
      logger.error('Erro ao enviar cotação:', error)
      throw error
    }
  },

  // Obter propostas de uma cotação
  async obterPropostas(id) {
    try {
      const response = await api.get(`${BASE_URL}/${id}/propostas`)
      return response.data
    } catch (error) {
      logger.error('Erro ao obter propostas:', error)
      throw error
    }
  },

  // Selecionar proposta vencedora
  async selecionarProposta(cotacaoId, propostaId, justificativa = '') {
    try {
      const response = await api.post(`${BASE_URL}/${cotacaoId}/selecionar-proposta`, {
        propostaId,
        justificativa
      })
      return response.data
    } catch (error) {
      logger.error('Erro ao selecionar proposta:', error)
      throw error
    }
  },

  // Aprovar cotação
  async aprovar(id, observacoes = '') {
    try {
      const response = await api.post(`${BASE_URL}/${id}/aprovar`, {
        observacoes
      })
      return response.data
    } catch (error) {
      logger.error('Erro ao aprovar cotação:', error)
      throw error
    }
  },

  // Cancelar cotação
  async cancelar(id, motivo = '') {
    try {
      const response = await api.post(`${BASE_URL}/${id}/cancelar`, {
        motivo
      })
      return response.data
    } catch (error) {
      logger.error('Erro ao cancelar cotação:', error)
      throw error
    }
  },

  // Reabrir cotação
  async reabrir(id) {
    try {
      const response = await api.post(`${BASE_URL}/${id}/reabrir`)
      return response.data
    } catch (error) {
      logger.error('Erro ao reabrir cotação:', error)
      throw error
    }
  },

  // Duplicar cotação
  async duplicar(id) {
    try {
      const response = await api.post(`${BASE_URL}/${id}/duplicar`)
      return response.data
    } catch (error) {
      logger.error('Erro ao duplicar cotação:', error)
      throw error
    }
  },

  // Adicionar item à cotação
  async adicionarItem(cotacaoId, item) {
    try {
      const response = await api.post(`${BASE_URL}/${cotacaoId}/itens`, item)
      return response.data
    } catch (error) {
      logger.error('Erro ao adicionar item:', error)
      throw error
    }
  },

  // Atualizar item da cotação
  async atualizarItem(cotacaoId, itemId, item) {
    try {
      const response = await api.put(`${BASE_URL}/${cotacaoId}/itens/${itemId}`, item)
      return response.data
    } catch (error) {
      logger.error('Erro ao atualizar item:', error)
      throw error
    }
  },

  // Remover item da cotação
  async removerItem(cotacaoId, itemId) {
    try {
      const response = await api.delete(`${BASE_URL}/${cotacaoId}/itens/${itemId}`)
      return response.data
    } catch (error) {
      logger.error('Erro ao remover item:', error)
      throw error
    }
  },

  // Adicionar anexo
  async adicionarAnexo(cotacaoId, arquivo, createHistory = true) {
    try {
      const formData = new FormData()
      formData.append('files', arquivo)  // Backend espera 'files' não 'arquivo'

      // IMPORTANTE: NÃO definir Content-Type manualmente!
      // O Axios detecta FormData e configura automaticamente com o boundary correto
      const response = await api.post(`${BASE_URL}/${cotacaoId}/anexos?createHistory=${createHistory}`, formData)
      return response.data
    } catch (error) {
      logger.error('Erro ao adicionar anexo:', error)
      throw error
    }
  },

  // Remover anexo
  async removerAnexo(cotacaoId, anexoId) {
    try {
      const response = await api.delete(`${BASE_URL}/${cotacaoId}/anexos/${anexoId}`)
      return response.data
    } catch (error) {
      logger.error('Erro ao remover anexo:', error)
      throw error
    }
  },

  // Gerar relatório comparativo de cotações por item
  async gerarRelatorioComparativo(itemPedidoId) {
    try {
      if (!itemPedidoId) {
        throw new Error('ID do item do pedido é obrigatório para gerar o relatório')
      }


      const response = await relatorioClient.get(`/relatorios/comparativo-cotacao/${itemPedidoId}`)


      // Verificar se a resposta existe e tem conteúdo
      if (!response.data || response.data.size === 0) {
        logger.error('❌ Resposta vazia ou sem dados do servidor')
        throw new Error('Não foi possível gerar o relatório - resposta vazia do servidor')
      }

      // Verificar se o status da resposta é ok
      if (response.status !== 200) {
        logger.error('❌ Status HTTP não ok:', response.status, response.statusText)
        throw new Error(`Erro no servidor: ${response.status} - ${response.statusText}`)
      }

      // Criar link para download
      const blob = new Blob([response.data], { type: 'application/pdf' })
      const downloadUrl = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = `comparativo_cotacao_item_${itemPedidoId}_${new Date().toISOString().split('T')[0]}.pdf`
      link.click()
      window.URL.revokeObjectURL(downloadUrl)

      return true
    } catch (error) {
      logger.error('❌ Erro ao gerar relatório comparativo:', error)

      // Log mais detalhado do erro
      if (error.response) {
        logger.error('❌ Detalhes do erro de resposta:', {
          status: error.response.status,
          statusText: error.response.statusText,
          data: error.response.data,
          headers: error.response.headers
        })
      }

      throw error
    }
  },

  // Gerar relatório geral de cotações (usando dashboard executivo como base)
  async gerarRelatorioCotacoes() {
    try {

      const response = await relatorioClient.get('/relatorios/dashboard-executivo')


      // Verificar se a resposta existe
      if (!response.data) {
        logger.error('❌ response.data é null ou undefined')
        throw new Error('Não foi possível gerar o relatório - dados ausentes')
      }

      // Para blob, verificar o tamanho
      if (response.data instanceof Blob) {
        if (response.data.size === 0) {
          logger.error('❌ Blob está vazio (size = 0)')
          throw new Error('Não foi possível gerar o relatório - arquivo vazio')
        }
      }

      // Verificar se o status da resposta é ok
      if (response.status !== 200) {
        logger.error('❌ Status HTTP não ok:', response.status, response.statusText)
        throw new Error(`Erro no servidor: ${response.status} - ${response.statusText}`)
      }

      // Criar link para download
      const blob = new Blob([response.data], { type: 'application/pdf' })
      const downloadUrl = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = `relatorio_cotacoes_${new Date().toISOString().split('T')[0]}.pdf`

      // Adicionar ao DOM, clicar e remover
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)

      // Limpar URL após um delay
      setTimeout(() => {
        window.URL.revokeObjectURL(downloadUrl)
      }, 100)

      return true
    } catch (error) {
      logger.error('❌ Erro ao gerar relatório de cotações:', error)

      // Log mais detalhado do erro
      if (error.response) {
        logger.error('❌ Detalhes do erro de resposta:', {
          status: error.response.status,
          statusText: error.response.statusText,
          data: error.response.data,
          headers: error.response.headers
        })

        // Se a resposta for um blob de erro, tentar ler como texto
        if (error.response.data instanceof Blob) {
          try {
            const text = await error.response.data.text()
            logger.error('❌ Conteúdo do erro (blob como texto):', text)
          } catch (e) {
            logger.error('❌ Não foi possível ler o blob de erro:', e)
          }
        }
      }

      throw error
    }
  },

  // Obter estatísticas do dashboard
  async obterEstatisticas(periodo = 'mes') {
    try {
      const response = await api.get(`${BASE_URL}/estatisticas?periodo=${periodo}`)
      return response.data
    } catch (error) {
      logger.error('Erro ao obter estatísticas:', error)
      throw error
    }
  },

  // Obter histórico de uma cotação
  async obterHistorico(id) {
    try {
      const response = await api.get(`${BASE_URL}/${id}/historico`)
      return response.data
    } catch (error) {
      logger.error('Erro ao obter histórico:', error)
      throw error
    }
  },

  // Adicionar comentário à cotação
  async adicionarComentario(cotacaoId, comentario) {
    try {
      const response = await api.post(`${BASE_URL}/${cotacaoId}/comentarios`, {
        comentario
      })
      return response.data
    } catch (error) {
      logger.error('Erro ao adicionar comentário:', error)
      throw error
    }
  },

  // Notificar fornecedores sobre prazo
  async notificarPrazo(cotacaoId, tipo = 'lembrete') {
    try {
      const response = await api.post(`${BASE_URL}/${cotacaoId}/notificar`, {
        tipo
      })
      return response.data
    } catch (error) {
      logger.error('Erro ao notificar fornecedores:', error)
      throw error
    }
  },

  // Validar cotação antes do envio
  async validar(cotacao) {
    try {
      const response = await api.post(`${BASE_URL}/validar`, cotacao)
      return response.data
    } catch (error) {
      logger.error('Erro ao validar cotação:', error)
      throw error
    }
  },

  // Obter modelos de cotação
  async obterModelos() {
    try {
      const response = await api.get(`${BASE_URL}/modelos`)
      return response.data
    } catch (error) {
      logger.error('Erro ao obter modelos:', error)
      throw error
    }
  },

  // Criar cotação a partir de modelo
  async criarDeModelo(modeloId, dados = {}) {
    try {
      const response = await api.post(`${BASE_URL}/modelos/${modeloId}/criar`, dados)
      return response.data
    } catch (error) {
      logger.error('Erro ao criar cotação de modelo:', error)
      throw error
    }
  },

  // ==================== ARQUIVOS PDF ====================

  // Converter arquivo para array de bytes
  async arquivoParaBytes(arquivo) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.onload = () => {
        const arrayBuffer = reader.result
        const uint8Array = new Uint8Array(arrayBuffer)
        resolve(Array.from(uint8Array))
      }
      reader.onerror = () => reject(reader.error)
      reader.readAsArrayBuffer(arquivo)
    })
  },

  // Upload de arquivo PDF para cotação existente
  async uploadArquivoPdf(cotacaoId, arquivo) {
    try {
      if (!arquivo) {
        throw new Error('Arquivo é obrigatório')
      }

      if (arquivo.type !== 'application/pdf') {
        throw new Error('Apenas arquivos PDF são permitidos')
      }

      const maxSize = 10 * 1024 * 1024 // 10MB
      if (arquivo.size > maxSize) {
        throw new Error('Arquivo muito grande. Máximo permitido: 10MB')
      }

      const bytesArray = await this.arquivoParaBytes(arquivo)

      const response = await api.put(`${BASE_URL}/${cotacaoId}`, {
        anexoPdf: bytesArray
      })

      return response

    } catch (error) {
      logger.error('❌ Erro no upload do PDF:', error)
      throw error
    }
  },

  // Obter anexo PDF da cotação
  async obterAnexoPdf(cotacaoId, pdfIndex = 0) {
    try {

      // Obter o token de autenticação
      const token = sessionStorage.getItem('authToken')

      // Usar o endpoint com índice para consistência
      const url = `${API_BASE_URL}/api/v1/cotacoes/${cotacaoId}/anexo/${pdfIndex}`


      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': token ? `Bearer ${token}` : ''
        }
      })

      if (!response.ok) {
        throw new Error(`Erro ao buscar PDF: ${response.status}`)
      }

      const blob = await response.blob()
      return blob
    } catch (error) {
      logger.error(`Erro ao obter PDF da cotação ${cotacaoId}:`, error.message)
      throw error
    }
  },

  // Verificar se cotação tem anexo PDF
  async verificarAnexo(cotacaoId) {
    try {
      // Buscar os dados da cotação para verificar campos de anexo
      const cotacao = await this.buscarPorId(cotacaoId)

      return {
        temAnexo: !!(cotacao.caminhoAnexo || cotacao.anexoPdf),
        caminhoAnexo: cotacao.caminhoAnexo,
        temAnexoPdf: !!cotacao.anexoPdf
      }

    } catch (error) {
      logger.error('❌ Erro ao verificar anexo:', error)
      return {
        temAnexo: false,
        caminhoAnexo: null,
        temAnexoPdf: false
      }
    }
  },

  // Obter informações do anexo PDF
  async obterInfoAnexo(cotacaoId) {
    try {
      const info = await this.verificarAnexo(cotacaoId)

      if (!info.temAnexo) {
        throw new Error('Nenhum arquivo anexado a esta cotação')
      }

      return {
        disponivel: info.temAnexo,
        caminho: info.caminhoAnexo,
        tipo: info.caminhoAnexo ? 'arquivo' : 'blob',
        mensagem: info.caminhoAnexo
          ? 'Arquivo PDF disponível'
          : 'Arquivo PDF armazenado no banco de dados'
      }

    } catch (error) {
      logger.error('❌ Erro ao obter informações do anexo:', error)
      throw error
    }
  },

  // Salvar como modelo
  async salvarComoModelo(cotacaoId, nomeModelo) {
    try {
      const response = await api.post(`${BASE_URL}/${cotacaoId}/salvar-modelo`, {
        nome: nomeModelo
      })
      return response.data
    } catch (error) {
      logger.error('Erro ao salvar como modelo:', error)
      throw error
    }
  },

  // Vincular itens a uma cotação
  async vincularItens(cotacaoId, itensPedidoIds) {
    try {
      if (!cotacaoId) {
        throw new Error('ID da cotação é obrigatório')
      }

      if (!itensPedidoIds || itensPedidoIds.length === 0) {
        throw new Error('Deve fornecer pelo menos um item para vincular')
      }


      const response = await api.patch(`${BASE_URL}/${cotacaoId}/vincular-itens`, itensPedidoIds)

      return response

    } catch (error) {
      logger.error('❌ Erro ao vincular itens:', error)
      throw error
    }
  },

  /**
   * Edita uma cotação existente com auditoria
   */
  async editarCotacao(cotacaoId, editDTO) {
    try {
      if (!cotacaoId) {
        throw new Error('ID da cotação é obrigatório')
      }

      if (!editDTO.motivoEdicao || editDTO.motivoEdicao.trim().length < 10) {
        throw new Error('Motivo da edição deve ter no mínimo 10 caracteres')
      }

      if (!editDTO.editadoPor || editDTO.editadoPor.trim().length === 0) {
        throw new Error('Responsável pela edição é obrigatório')
      }

      logger.debug('🔧 Chamando API PUT /editar...')
      const response = await api.put(`${BASE_URL}/${cotacaoId}/editar`, editDTO)
      logger.debug('🔧 Response completo:', response)
      logger.debug('🔧 Response.data:', response.data)
      return response.data
    } catch (error) {
      logger.error('❌ Erro ao editar cotação:', error)
      throw error
    }
  },

  /**
   * Busca o histórico de edições de uma cotação
   *
   * @async
   * @function buscarHistorico
   * @memberof cotacaoService
   * @param {number} cotacaoId - ID da cotação
   * @returns {Promise<Array<HistoricoCotacao>>} Lista de registros de histórico
   * @throws {Error} Erro se ID não fornecido ou falha na comunicação
   *
   * @example
   * const historico = await cotacaoService.buscarHistorico(15)
   * historico.forEach(h => {
   *   console.log(`${h.dataEdicao} - ${h.editadoPor}: ${h.motivoEdicao}`)
   * })
   */
  async buscarHistorico(cotacaoId) {
    try {
      if (!cotacaoId) {
        throw new Error('ID da cotação é obrigatório')
      }

      const data = await api.get(`${BASE_URL}/${cotacaoId}/historico`)
      return data
    } catch (error) {
      logger.error('❌ Erro ao buscar histórico:', error)
      throw error
    }
  },

  /**
   * Obtém PDF anterior do histórico de cotação para visualização
   *
   * @async
   * @function obterPdfAnteriorHistorico
   * @memberof cotacaoService
   * @param {number} historicoId - ID do registro de histórico
   * @returns {Promise<Blob>} Blob do PDF anterior
   * @throws {Error} Erro ao obter PDF
   *
   * @example
   * const blob = await cotacaoService.obterPdfAnteriorHistorico(5)
   */
  async obterPdfAnteriorHistorico(historicoId) {
    try {
      if (!historicoId) {
        throw new Error('ID do histórico é obrigatório')
      }

      const response = await relatorioClient.get(`${BASE_URL}/historico/${historicoId}/pdf/anterior`)
      return response.data
    } catch (error) {
      logger.error('❌ Erro ao obter PDF anterior do histórico:', error)
      throw error
    }
  },

  /**
   * Obtém PDF novo do histórico de cotação para visualização
   *
   * @async
   * @function obterPdfNovoHistorico
   * @memberof cotacaoService
   * @param {number} historicoId - ID do registro de histórico
   * @returns {Promise<Blob>} Blob do PDF novo
   * @throws {Error} Erro ao obter PDF
   *
   * @example
   * const blob = await cotacaoService.obterPdfNovoHistorico(5)
   */
  async obterPdfNovoHistorico(historicoId) {
    try {
      if (!historicoId) {
        throw new Error('ID do histórico é obrigatório')
      }

      const response = await relatorioClient.get(`${BASE_URL}/historico/${historicoId}/pdf/novo`)
      return response.data
    } catch (error) {
      logger.error('❌ Erro ao obter PDF novo do histórico:', error)
      throw error
    }
  },

  /**
   * Baixa PDF anterior do histórico de cotação (download direto)
   *
   * @async
   * @function baixarPdfAnteriorHistorico
   * @memberof cotacaoService
   * @param {number} historicoId - ID do registro de histórico
   * @returns {Promise<void>}
   * @throws {Error} Erro ao baixar PDF
   *
   * @example
   * await cotacaoService.baixarPdfAnteriorHistorico(5)
   */
  async baixarPdfAnteriorHistorico(historicoId) {
    try {
      const blob = await this.obterPdfAnteriorHistorico(historicoId)
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `historico-${historicoId}-anterior.pdf`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    } catch (error) {
      logger.error('❌ Erro ao baixar PDF anterior do histórico:', error)
      throw error
    }
  },

  /**
   * Baixa PDF novo do histórico de cotação (download direto)
   */
  async baixarPdfNovoHistorico(historicoId) {
    try {
      const blob = await this.obterPdfNovoHistorico(historicoId)
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `historico-${historicoId}-novo.pdf`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    } catch (error) {
      logger.error('❌ Erro ao baixar PDF novo do histórico:', error)
      throw error
    }
  }
}

/**
 * @exports cotacaoService
 */
export default cotacaoService
