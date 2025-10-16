/**
 * Serviço de Itens de Pedido
 *
 * Gerencia todas as operações relacionadas aos itens de pedido
 */

import api from './api.js'

const itemPedidoService = {
  async listarTodos() {
    try {
      console.log('🔄 Buscando itens de pedido no backend...')
      const data = await api.get('/api/itens-pedido')
      console.log('✅ Itens de pedido carregados do backend:', data.length, 'itens')

      // Se não há dados no backend, retornar array vazio ao invés de mock
      if (!data || data.length === 0) {
        console.log('⚠️ Nenhum item de pedido encontrado no backend')
        return []
      }

      return data
    } catch (error) {
      console.error('❌ Erro ao listar itens de pedido no backend:', error.message)

      // Retornar array vazio ao invés de dados mock para produção
      console.log('⚠️ Retornando array vazio devido ao erro')
      return []
    }
  },

  async buscarPorId(id) {
    try {
      console.log(`🔄 Buscando item de pedido ID ${id} no backend...`)
      const data = await api.get(`/api/itens-pedido/${id}`)
      console.log('✅ Item de pedido carregado do backend')
      return data
    } catch (error) {
      console.error(`❌ Erro ao obter item de pedido ID ${id} no backend:`, error.message)
      throw error
    }
  },

  async criar(item) {
    try {
      console.log('🔄 Criando item de pedido no backend...')
      const data = await api.post('/api/itens-pedido', item)
      console.log('✅ Item de pedido criado no backend:', data.id)
      return data
    } catch (error) {
      console.error('❌ Erro ao criar item de pedido:', error.message)
      throw error
    }
  },

  async atualizar(id, item) {
    try {
      console.log(`🔄 Atualizando item de pedido ID ${id} no backend...`)
      const data = await api.put(`/api/itens-pedido/${id}`, item)
      console.log('✅ Item de pedido atualizado no backend')
      return data
    } catch (error) {
      console.error(`❌ Erro ao atualizar item de pedido ID ${id} no backend:`, error.message)
      throw error
    }
  },

  async excluir(id) {
    try {
      console.log(`🔄 Excluindo item de pedido ID ${id} no backend...`)
      await api.delete(`/api/itens-pedido/${id}`)
      console.log('✅ Item de pedido excluído do backend')
    } catch (error) {
      console.error(`❌ Erro ao excluir item de pedido ID ${id} no backend:`, error.message)
      throw error
    }
  }
}

export default itemPedidoService
