/**
 * Serviço de Rascunhos
 *
 * Gerencia todas as operações relacionadas aos rascunhos de pedidos
 */

import api from './api.js'

const rascunhoService = {
  async listar() {
    try {
      console.log('Buscando rascunhos no backend...')
      const data = await api.get('/api/rascunhos')
      console.log('Rascunhos carregados:', data.length)
      return data
    } catch (error) {
      console.error('Erro ao listar rascunhos:', error.message)
      throw error
    }
  },

  async listarPorUsuario(userId) {
    try {
      console.log(`Buscando rascunhos do usuário ${userId}...`)
      const data = await api.get(`/api/rascunhos/usuario/${userId}`)
      console.log('Rascunhos do usuário carregados:', data.length)
      return data
    } catch (error) {
      console.error('Erro ao listar rascunhos do usuário:', error.message)
      throw error
    }
  },

  async obterPorId(id) {
    try {
      console.log(`Buscando rascunho ID ${id}...`)
      const data = await api.get(`/api/rascunhos/${id}`)
      console.log('Rascunho carregado')
      return data
    } catch (error) {
      console.error(`Erro ao obter rascunho ID ${id}:`, error.message)
      throw error
    }
  },

  async criar(rascunho) {
    try {
      console.log('Criando rascunho no backend...')

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

      const data = await api.post('/api/rascunhos', rascunho)
      console.log('Rascunho criado:', data.id)
      return data
    } catch (error) {
      console.error('Erro ao criar rascunho:', error.message)
      throw error
    }
  },

  async atualizar(id, rascunho) {
    try {
      console.log(`Atualizando rascunho ID ${id}...`)
      const data = await api.put(`/api/rascunhos/${id}`, rascunho)
      console.log('Rascunho atualizado')
      return data
    } catch (error) {
      console.error(`Erro ao atualizar rascunho ID ${id}:`, error.message)
      throw error
    }
  },

  async remover(id) {
    try {
      console.log(`Removendo rascunho ID ${id}...`)
      await api.delete(`/api/rascunhos/${id}`)
      console.log('Rascunho removido')
      return true
    } catch (error) {
      console.error(`Erro ao remover rascunho ID ${id}:`, error.message)
      throw error
    }
  },

  async converterParaPedido(rascunhoId, itemIds) {
    try {
      console.log(`Convertendo rascunho ${rascunhoId} para pedido...`)
      console.log('Itens selecionados:', itemIds)

      const data = await api.post(`/api/rascunhos/${rascunhoId}/converter-para-pedido`, {
        itemRascunhoIds: itemIds
      })

      console.log('Pedido criado a partir do rascunho:', data.id)
      return data
    } catch (error) {
      console.error('Erro ao converter rascunho para pedido:', error.message)
      throw error
    }
  },

  // Método unificado para salvar (criar ou atualizar)
  async salvar(rascunho) {
    try {
      console.log('Salvando rascunho...', rascunho.id ? `(ID: ${rascunho.id})` : '(novo)')

      if (rascunho.id) {
        return await this.atualizar(rascunho.id, rascunho)
      } else {
        return await this.criar(rascunho)
      }
    } catch (error) {
      console.error('Erro ao salvar rascunho:', error)
      throw error
    }
  },

  // Métodos para gerenciamento individual de itens

  async adicionarItem(rascunhoId, item) {
    try {
      console.log(`Adicionando item ao rascunho ${rascunhoId}...`)
      const data = await api.post(`/api/rascunhos/${rascunhoId}/itens`, item)
      console.log('Item adicionado')
      return data
    } catch (error) {
      console.error('Erro ao adicionar item:', error.message)
      throw error
    }
  },

  async atualizarItem(rascunhoId, itemId, item) {
    try {
      console.log(`Atualizando item ${itemId} do rascunho ${rascunhoId}...`)
      const data = await api.put(`/api/rascunhos/${rascunhoId}/itens/${itemId}`, item)
      console.log('Item atualizado')
      return data
    } catch (error) {
      console.error('Erro ao atualizar item:', error.message)
      throw error
    }
  },

  async removerItem(rascunhoId, itemId) {
    try {
      console.log(`Removendo item ${itemId} do rascunho ${rascunhoId}...`)
      const data = await api.delete(`/api/rascunhos/${rascunhoId}/itens/${itemId}`)
      console.log('Item removido')
      return data
    } catch (error) {
      console.error('Erro ao remover item:', error.message)
      throw error
    }
  },

  async listarHistorico(rascunhoId) {
    try {
      console.log(`Buscando histórico do rascunho ${rascunhoId}...`)
      const data = await api.get(`/api/rascunhos/${rascunhoId}/historico`)
      console.log('Histórico carregado:', data.length, 'registros')
      return data
    } catch (error) {
      console.error('Erro ao listar histórico:', error.message)
      throw error
    }
  },

  async atualizarStatus(rascunhoId, status) {
    try {
      console.log(`Atualizando status do rascunho ${rascunhoId} para ${status}...`)
      const data = await api.patch(`/api/rascunhos/${rascunhoId}/status?status=${status}`)
      console.log('Status atualizado')
      return data
    } catch (error) {
      console.error('Erro ao atualizar status:', error.message)
      throw error
    }
  }
}

export default rascunhoService
