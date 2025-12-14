/**
 * @fileoverview Serviço de Gerenciamento de Itens de Pedido
 * 
 * Módulo responsável por operações CRUD em itens de pedido individuais.
 * Complementa o pedidoService para operações específicas em itens.
 * 
 * @module services/itemPedidoService
 * @requires ./api
 * @requires ../utils/logger
 * 
 * @description
 * Este serviço implementa:
 * - CRUD completo de Itens de Pedido
 * - Listagem de todos os itens cadastrados
 * - Busca por ID específico
 * - Extração de dados de respostas paginadas
 * 
 * @example
 * const itens = await itemPedidoService.listarTodos()
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
 * Serviço de itens de pedido
 * @namespace itemPedidoService
 */
const itemPedidoService = {
  /**
   * Lista todos os itens de pedido cadastrados
   * 
   * @async
   * @function listarTodos
   * @memberof itemPedidoService
   * @returns {Promise<Array>} Array de itens de pedido
   * @throws {Error} Erro de comunicação com API
   * 
   * @example
   * const itens = await itemPedidoService.listarTodos()
   */
  async listarTodos() {
    try {
      const data = await api.get('/api/v1/itens-pedido')
      return extractContent(data)
    } catch (error) {
      logger.error('❌ Erro ao listar itens de pedido no backend:', error.message)
      throw error
    }
  },

  /**
   * Busca um item de pedido específico por ID
   * 
   * @async
   * @function buscarPorId
   * @memberof itemPedidoService
   * @param {number} id - ID do item
   * @returns {Promise<Object>} Dados do item
   * @throws {Error} Erro 404 se item não encontrado
   * 
   * @example
   * const item = await itemPedidoService.buscarPorId(123)
   */
  async buscarPorId(id) {
    try {
      const data = await api.get(`/api/v1/itens-pedido/${id}`)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao obter item de pedido ID ${id} no backend:`, error.message)
      throw error
    }
  },

  /**
   * Cria um novo item de pedido
   * 
   * @async
   * @function criar
   * @memberof itemPedidoService
   * @param {Object} item - Dados do item
   * @returns {Promise<Object>} Item criado
   * @throws {Error} Erro de validação ou comunicação
   * 
   * @example
   * const item = { nome: 'Mouse', quantidade: 10, pedidoId: 5 }
   * await itemPedidoService.criar(item)
   */
  async criar(item) {
    try {
      const data = await api.post('/api/v1/itens-pedido', item)
      return data
    } catch (error) {
      logger.error('❌ Erro ao criar item de pedido:', error.message)
      throw error
    }
  },

  /**
   * Atualiza um item de pedido existente
   * 
   * @async
   * @function atualizar
   * @memberof itemPedidoService
   * @param {number} id - ID do item
   * @param {Object} item - Dados atualizados
   * @returns {Promise<Object>} Item atualizado
   * @throws {Error} Erro 404 ou comunicação
   * 
   * @example
   * await itemPedidoService.atualizar(123, { quantidade: 20 })
   */
  async atualizar(id, item) {
    try {
      const data = await api.put(`/api/v1/itens-pedido/${id}`, item)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao atualizar item de pedido ID ${id} no backend:`, error.message)
      throw error
    }
  },

  /**
   * Exclui um item de pedido
   * 
   * @async
   * @function excluir
   * @memberof itemPedidoService
   * @param {number} id - ID do item a excluir
   * @returns {Promise<void>}
   * @throws {Error} Erro 404 ou comunicação
   * 
   * @example
   * await itemPedidoService.excluir(123)
   */
  async excluir(id) {
    try {
      await api.delete(`/api/v1/itens-pedido/${id}`)
    } catch (error) {
      logger.error(`❌ Erro ao excluir item de pedido ID ${id} no backend:`, error.message)
      throw error
    }
  }
}

/**
 * @exports itemPedidoService
 * @default
 */
export default itemPedidoService
