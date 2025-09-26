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
  }
}

export default itemPedidoService
