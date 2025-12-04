/**
 * Serviço de Rascunhos
 *
 * Gerencia todas as operações relacionadas aos rascunhos de pedidos
 */

import api from './api.js'

/**
 * Helper para extrair dados de respostas paginadas do Spring Data
 */
const extractContent = (response) => {
  if (response && typeof response === 'object' && 'content' in response) {
    return response.content || []
  }
  return Array.isArray(response) ? response : []
}

const rascunhoService = {
  async listar() {
    try {
      const data = await api.get('/api/v1/rascunhos')
      return extractContent(data)
    } catch (error) {
      console.error('Erro ao listar rascunhos:', error.message)
      throw error
    }
  },

  async listarPorUsuario(userId) {
    try {
      const data = await api.get(`/api/v1/rascunhos/usuario/${userId}`)
      return extractContent(data)
    } catch (error) {
      console.error('Erro ao listar rascunhos do usuário:', error.message)
      throw error
    }
  },

  async obterPorId(id) {
    try {
      const data = await api.get(`/api/v1/rascunhos/${id}`)
      return data
    } catch (error) {
      console.error(`Erro ao obter rascunho ID ${id}:`, error.message)
      throw error
    }
  },

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
      console.error('Erro ao criar rascunho:', error.message)
      throw error
    }
  },

  async atualizar(id, rascunho) {
    try {
      const data = await api.put(`/api/v1/rascunhos/${id}`, rascunho)
      return data
    } catch (error) {
      console.error(`Erro ao atualizar rascunho ID ${id}:`, error.message)
      throw error
    }
  },

  async remover(id) {
    try {
      await api.delete(`/api/v1/rascunhos/${id}`)
      return true
    } catch (error) {
      console.error(`Erro ao remover rascunho ID ${id}:`, error.message)
      throw error
    }
  },

  async converterParaPedido(rascunhoId, itemIds = null, cotacaoParaItens = null) {
    try {

      const payload = {}

      // Usar novo formato se disponível
      if (cotacaoParaItens && Object.keys(cotacaoParaItens).length > 0) {
        payload.cotacaoParaItens = cotacaoParaItens
      } else if (itemIds) {
        // Fallback para formato legado
        payload.itemRascunhoIds = itemIds
      } else {
        throw new Error('Deve fornecer itemIds ou cotacaoParaItens')
      }

      const data = await api.post(`/api/v1/rascunhos/${rascunhoId}/converter-para-pedido`, payload)

      return data
    } catch (error) {
      console.error('Erro ao converter rascunho para pedido:', error.message)
      throw error
    }
  },

  // Método unificado para salvar (criar ou atualizar)
  async salvar(rascunho) {
    try {

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
      const data = await api.post(`/api/v1/rascunhos/${rascunhoId}/itens`, item)
      return data
    } catch (error) {
      console.error('Erro ao adicionar item:', error.message)
      throw error
    }
  },

  async atualizarItem(rascunhoId, itemId, item) {
    try {
      const data = await api.put(`/api/v1/rascunhos/${rascunhoId}/itens/${itemId}`, item)
      return data
    } catch (error) {
      console.error('Erro ao atualizar item:', error.message)
      throw error
    }
  },

  async removerItem(rascunhoId, itemId) {
    try {
      const data = await api.delete(`/api/v1/rascunhos/${rascunhoId}/itens/${itemId}`)
      return data
    } catch (error) {
      console.error('Erro ao remover item:', error.message)
      throw error
    }
  },

  async listarHistorico(rascunhoId) {
    try {
      const data = await api.get(`/api/v1/rascunhos/${rascunhoId}/historico`)
      return data
    } catch (error) {
      console.error('Erro ao listar histórico:', error.message)
      throw error
    }
  },

  async atualizarStatus(rascunhoId, status) {
    try {
      const data = await api.patch(`/api/v1/rascunhos/${rascunhoId}/status?status=${status}`)
      return data
    } catch (error) {
      console.error('Erro ao atualizar status:', error.message)
      throw error
    }
  }
}

export default rascunhoService
