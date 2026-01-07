/**
 * @fileoverview Serviço de Gerenciamento de Rascunhos de Pedidos
 *
 * Módulo responsável por todas as operações relacionadas aos rascunhos de pedidos.
 * Rascunhos permitem salvar pedidos parcialmente completos para finalização posterior,
 * além de possibilitar conversão direta para pedidos oficiais.
 *
 * @module services/rascunhoService
 * @requires ./api
 * @requires ../utils/logger
 *
 * @description
 * Este serviço implementa:
 * - CRUD completo de Rascunhos de Pedidos
 * - Listagem por usuário específico
 * - Conversão de rascunho para pedido oficial (com vinculação de cotações)
 * - Gerenciamento individual de itens do rascunho
 * - Histórico de alterações
 * - Atualização de status
 * - Validação de dados antes de salvar
 * - Método unificado salvar() (criar ou atualizar)
 *
 * @typedef {Object} Rascunho
 * @property {number} [id] - ID do rascunho
 * @property {string} [observacao] - Observação geral
 * @property {string} [status] - Status do rascunho
 * @property {Array<ItemRascunho>} itens - Lista de itens (mínimo 1)
 * @property {number} [usuarioId] - ID do usuário proprietário
 *
 * @typedef {Object} ItemRascunho
 * @property {number} [id] - ID do item
 * @property {string} nome - Nome do produto/serviço (obrigatório)
 * @property {number} quantidade - Quantidade solicitada (> 0)
 * @property {string} [descricao] - Descrição detalhada
 * @property {string} [observacao] - Observações
 *
 * @example
 * // Criar rascunho
 * const rascunho = {
 *   observacao: 'Rascunho de teste',
 *   itens: [{ nome: 'Notebook', quantidade: 2 }]
 * }
 * await rascunhoService.criar(rascunho)
 *
 * @example
 * // Converter rascunho para pedido com cotações
 * await rascunhoService.converterParaPedido(5, null, { 1: 10, 2: 11 })
 *
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

import api from './api.js'
import logger from '../utils/logger.js'

/**
 * Extrai conteúdo de respostas paginadas do Spring Data
 * @function extractContent
 * @param {Object|Array} response - Resposta da API
 * @returns {Array} Array de elementos
 */
const extractContent = (response) => {
  if (response && typeof response === 'object' && 'content' in response) {
    return response.content || []
  }
  return Array.isArray(response) ? response : []
}

/**
 * Serviço principal de gerenciamento de rascunhos
 * @namespace rascunhoService
 */
const rascunhoService = {
  /**
   * Lista todos os rascunhos
   *
   * @async
   * @function listar
   * @memberof rascunhoService
   * @returns {Promise<Array<Rascunho>>} Array de rascunhos
   * @throws {Error} Erro de comunicação com API
   *
   * @example
   * const rascunhos = await rascunhoService.listar()
   */
  async listar() {
    try {
      const data = await api.get('/api/v1/rascunhos')
      return extractContent(data)
    } catch (error) {
      logger.error('Erro ao listar rascunhos:', error.message)
      throw error
    }
  },

  /**
   * Lista rascunhos de um usuário específico
   *
   * @async
   * @function listarPorUsuario
   * @memberof rascunhoService
   * @param {number} userId - ID do usuário
   * @returns {Promise<Array<Rascunho>>} Rascunhos do usuário
   * @throws {Error} Erro de comunicação com API
   *
   * @example
   * const meusRascunhos = await rascunhoService.listarPorUsuario(10)
   */
  async listarPorUsuario(userId) {
    try {
      const data = await api.get(`/api/v1/rascunhos/usuario/${userId}`)
      return extractContent(data)
    } catch (error) {
      logger.error('Erro ao listar rascunhos do usuário:', error.message)
      throw error
    }
  },

  /**
   * Obtém um rascunho específico por ID
   *
   * @async
   * @function obterPorId
   * @memberof rascunhoService
   * @param {number} id - ID do rascunho
   * @returns {Promise<Rascunho>} Dados completos do rascunho
   * @throws {Error} Erro 404 se rascunho não encontrado
   *
   * @example
   * const rascunho = await rascunhoService.obterPorId(5)
   */
  async obterPorId(id) {
    try {
      const data = await api.get(`/api/v1/rascunhos/${id}`)
      return data
    } catch (error) {
      logger.error(`Erro ao obter rascunho ID ${id}:`, error.message)
      throw error
    }
  },

  /**
   * Cria um novo rascunho de pedido
   *
   * @async
   * @function criar
   * @memberof rascunhoService
   * @param {Rascunho} rascunho - Dados do rascunho
   * @returns {Promise<Rascunho>} Rascunho criado
   * @throws {Error} Erro de validação ou comunicação
   *
   * @example
   * const rascunho = {
   *   observacao: 'Urgente',
   *   itens: [
   *     { nome: 'Caneta', quantidade: 100 },
   *     { nome: 'Papel A4', quantidade: 50 }
   *   ]
   * }
   * await rascunhoService.criar(rascunho)
   *
   * @description
   * Validações aplicadas:
   * - Rascunho deve ter pelo menos 1 item
   * - Cada item deve ter nome (não vazio) e quantidade > 0
   */
  async criar(rascunho) {
    try {

      // Validar estrutura de dados
      if (!rascunho.itens || rascunho.itens.length === 0) {
        throw new Error('Rascunho deve ter pelo menos um item')
      }

      rascunho.itens.forEach((item, index) => {
        if (!item.nome || item.nome.trim() === '') {
          throw new Error(`Item ${index + 1}: Nome é obrigatório`)
        }
        if (!item.quantidade || item.quantidade <= 0) {
          throw new Error(`Item ${index + 1}: Quantidade deve ser maior que zero`)
        }
      })

      const data = await api.post('/api/v1/rascunhos', rascunho)
      return data
    } catch (error) {
      logger.error('Erro ao criar rascunho:', error.message)
      throw error
    }
  },

  /**
   * Atualiza um rascunho existente
   *
   * @async
   * @function atualizar
   * @memberof rascunhoService
   * @param {number} id - ID do rascunho
   * @param {Rascunho} rascunho - Dados atualizados
   * @returns {Promise<Rascunho>} Rascunho atualizado
   * @throws {Error} Erro 404 ou comunicação
   *
   * @example
   * await rascunhoService.atualizar(5, { observacao: 'Nova observação' })
   */
  async atualizar(id, rascunho) {
    try {
      const data = await api.put(`/api/v1/rascunhos/${id}`, rascunho)
      return data
    } catch (error) {
      logger.error(`Erro ao atualizar rascunho ID ${id}:`, error.message)
      throw error
    }
  },

  /**
   * Remove (deleta) um rascunho
   *
   * @async
   * @function remover
   * @memberof rascunhoService
   * @param {number} id - ID do rascunho a ser removido
   * @returns {Promise<boolean>} true se remoção bem-sucedida
   * @throws {Error} Erro 404 ou comunicação
   *
   * @example
   * await rascunhoService.remover(5)
   */
  async remover(id) {
    try {
      await api.delete(`/api/v1/rascunhos/${id}`)
      return true
    } catch (error) {
      logger.error(`Erro ao remover rascunho ID ${id}:`, error.message)
      throw error
    }
  },

  /**
   * Converte um rascunho em pedido oficial
   *
   * @async
   * @function converterParaPedido
   * @memberof rascunhoService
   * @param {number} rascunhoId - ID do rascunho
   * @param {Object.<number, Array<number>>} cotacaoParaItens - Mapa cotacaoId -> [itemIds]
   * @returns {Promise<Object>} Pedido criado
   * @throws {Error} Erro se cotacaoParaItens não fornecido
   *
   * @example
   * // Vincular cotações aos itens
   * await rascunhoService.converterParaPedido(5, { 10: [1, 2], 11: [3] })
   * // Cotação 10 contempla itens 1 e 2, Cotação 11 contempla item 3
   */
  async converterParaPedido(rascunhoId, cotacaoParaItens) {
    try {
      if (!cotacaoParaItens || Object.keys(cotacaoParaItens).length === 0) {
        throw new Error('Deve fornecer cotacaoParaItens com pelo menos uma cotação selecionada')
      }

      const payload = { cotacaoParaItens }

      const data = await api.post(`/api/v1/rascunhos/${rascunhoId}/converter-para-pedido`, payload)

      return data
    } catch (error) {
      logger.error('Erro ao converter rascunho para pedido:', error.message)
      throw error
    }
  },

  /**
   * Salva um rascunho (cria se novo, atualiza se existente)
   *
   * @async
   * @function salvar
   * @memberof rascunhoService
   * @param {Rascunho} rascunho - Dados do rascunho (com ou sem ID)
   * @returns {Promise<Rascunho>} Rascunho salvo
   * @throws {Error} Erro de validação ou comunicação
   *
   * @example
   * // Criar novo
   * const novo = { itens: [{ nome: 'Item', quantidade: 1 }] }
   * await rascunhoService.salvar(novo)
   *
   * @example
   * // Atualizar existente
   * const existente = { id: 5, observacao: 'Atualizado', itens: [...] }
   * await rascunhoService.salvar(existente)
   *
   * @description
   * Método unificado que detecta automaticamente a operação:
   * - Se rascunho.id existe: chama atualizar()
   * - Se rascunho.id é undefined/null: chama criar()
   */
  async salvar(rascunho) {
    try {

      if (rascunho.id) {
        return await this.atualizar(rascunho.id, rascunho)
      } else {
        return await this.criar(rascunho)
      }
    } catch (error) {
      logger.error('Erro ao salvar rascunho:', error)
      throw error
    }
  },

  /**
   * Adiciona um novo item a um rascunho existente
   *
   * @async
   * @function adicionarItem
   * @memberof rascunhoService
   * @param {number} rascunhoId - ID do rascunho
   * @param {ItemRascunho} item - Dados do item a adicionar
   * @returns {Promise<ItemRascunho>} Item criado
   * @throws {Error} Erro de validação ou comunicação
   *
   * @example
   * await rascunhoService.adicionarItem(5, { nome: 'Mouse', quantidade: 10 })
   */
  async adicionarItem(rascunhoId, item) {
    try {
      const data = await api.post(`/api/v1/rascunhos/${rascunhoId}/itens`, item)
      return data
    } catch (error) {
      logger.error('Erro ao adicionar item:', error.message)
      throw error
    }
  },

  /**
   * Atualiza um item específico do rascunho
   *
   * @async
   * @function atualizarItem
   * @memberof rascunhoService
   * @param {number} rascunhoId - ID do rascunho
   * @param {number} itemId - ID do item
   * @param {ItemRascunho} item - Dados atualizados do item
   * @returns {Promise<ItemRascunho>} Item atualizado
   * @throws {Error} Erro 404 ou comunicação
   *
   * @example
   * await rascunhoService.atualizarItem(5, 10, { quantidade: 20 })
   */
  async atualizarItem(rascunhoId, itemId, item) {
    try {
      const data = await api.put(`/api/v1/rascunhos/${rascunhoId}/itens/${itemId}`, item)
      return data
    } catch (error) {
      logger.error('Erro ao atualizar item:', error.message)
      throw error
    }
  },

  /**
   * Remove um item do rascunho
   *
   * @async
   * @function removerItem
   * @memberof rascunhoService
   * @param {number} rascunhoId - ID do rascunho
   * @param {number} itemId - ID do item a remover
   * @returns {Promise<*>} Resposta da API
   * @throws {Error} Erro 404 ou comunicação
   *
   * @example
   * await rascunhoService.removerItem(5, 10)
   */
  async removerItem(rascunhoId, itemId) {
    try {
      const data = await api.delete(`/api/v1/rascunhos/${rascunhoId}/itens/${itemId}`)
      return data
    } catch (error) {
      logger.error('Erro ao remover item:', error.message)
      throw error
    }
  },

  /**
   * Lista o histórico de alterações de um rascunho
   *
   * @async
   * @function listarHistorico
   * @memberof rascunhoService
   * @param {number} rascunhoId - ID do rascunho
   * @returns {Promise<Array>} Histórico de alterações
   * @throws {Error} Erro de comunicação
   *
   * @example
   * const historico = await rascunhoService.listarHistorico(5)
   */
  async listarHistorico(rascunhoId) {
    try {
      const data = await api.get(`/api/v1/rascunhos/${rascunhoId}/historico`)
      return data
    } catch (error) {
      logger.error('Erro ao listar histórico:', error.message)
      throw error
    }
  },

  /**
   * Atualiza o status de um rascunho
   *
   * @async
   * @function atualizarStatus
   * @memberof rascunhoService
   * @param {number} rascunhoId - ID do rascunho
   * @param {string} status - Novo status
   * @returns {Promise<Rascunho>} Rascunho com status atualizado
   * @throws {Error} Erro de comunicação
   *
   * @example
   * await rascunhoService.atualizarStatus(5, 'EM_REVISAO')
   */
  async atualizarStatus(rascunhoId, status) {
    try {
      const data = await api.patch(`/api/v1/rascunhos/${rascunhoId}/status?status=${status}`)
      return data
    } catch (error) {
      logger.error('Erro ao atualizar status:', error.message)
      throw error
    }
  },

  /**
   * Conta quantas cotações existem para um rascunho
   *
   * @async
   * @function contarCotacoes
   * @memberof rascunhoService
   * @param {number} rascunhoId - ID do rascunho
   * @returns {Promise<number>} Quantidade de cotações
   * @throws {Error} Erro de comunicação
   *
   * @example
   * const quantidade = await rascunhoService.contarCotacoes(5)
   */
  async contarCotacoes(rascunhoId) {
    try {
      const data = await api.get(`/api/v1/rascunhos/${rascunhoId}/cotacoes/count`)
      return data.quantidade
    } catch (error) {
      logger.error('Erro ao contar cotações:', error.message)
      throw error
    }
  },

  /**
   * Devolve um rascunho em cotação para edição
   * ATENÇÃO: Remove TODAS as cotações existentes
   *
   * @async
   * @function devolverParaEdicao
   * @memberof rascunhoService
   * @param {number} rascunhoId - ID do rascunho
   * @param {Object} dto - Dados da devolução
   * @param {string} dto.motivo - Motivo da devolução (mínimo 10 caracteres)
   * @returns {Promise<Rascunho>} Rascunho com status atualizado para ATIVO
   * @throws {Error} Erro se rascunho não estiver em EM_COTACAO ou comunicação
   *
   * @example
   * await rascunhoService.devolverParaEdicao(5, {
   *   motivo: 'Necessário ajustar quantidades dos itens'
   * })
   */
  async devolverParaEdicao(rascunhoId, dto) {
    try {
      const data = await api.post(`/api/v1/rascunhos/${rascunhoId}/devolver-para-edicao`, dto)
      return data
    } catch (error) {
      logger.error('Erro ao devolver rascunho para edição:', error.message)
      throw error
    }
  }
}

/**
 * @exports rascunhoService
 * @default
 */
export default rascunhoService
