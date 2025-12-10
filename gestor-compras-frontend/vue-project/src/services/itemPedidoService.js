/**
 * Serviço de Itens de Pedido
 *
 * Gerencia todas as operações relacionadas aos itens de pedido
 */

import api from './api.js'
import logger from '../utils/logger.js'

/**
 * Helper para extrair dados de respostas paginadas do Spring Data
 */
const extractContent = (response) => {
  if (response && typeof response === 'object' && 'content' in response) {
    return response.content || []
  }
  return Array.isArray(response) ? response : []
}

const itemPedidoService = {
  async listarTodos() {
    try {
      const data = await api.get('/api/v1/itens-pedido')
      return extractContent(data)
    } catch (error) {
      logger.error('❌ Erro ao listar itens de pedido no backend:', error.message)
      throw error
    }
  },

  async buscarPorId(id) {
    try {
      const data = await api.get(`/api/v1/itens-pedido/${id}`)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao obter item de pedido ID ${id} no backend:`, error.message)
      throw error
    }
  },

  async criar(item) {
    try {
      const data = await api.post('/api/v1/itens-pedido', item)
      return data
    } catch (error) {
      logger.error('❌ Erro ao criar item de pedido:', error.message)
      throw error
    }
  },

  async atualizar(id, item) {
    try {
      const data = await api.put(`/api/v1/itens-pedido/${id}`, item)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao atualizar item de pedido ID ${id} no backend:`, error.message)
      throw error
    }
  },

  async excluir(id) {
    try {
      await api.delete(`/api/v1/itens-pedido/${id}`)
    } catch (error) {
      logger.error(`❌ Erro ao excluir item de pedido ID ${id} no backend:`, error.message)
      throw error
    }
  }
}

export default itemPedidoService
