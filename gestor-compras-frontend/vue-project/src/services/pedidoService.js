/**
 * Serviço de Pedidos
 *
 * Gerencia todas as operações relacionadas aos pedidos de compra
 */

import api from './api.js'

const pedidoService = {
  async listar() {
    try {
      console.log('🔄 Buscando pedidos no backend...')
      const data = await api.get('/api/solicitacoes-pedido')
      console.log('✅ Pedidos carregados do backend:', data.length, 'pedidos')
      return data
    } catch (error) {
      console.error('❌ Erro ao listar pedidos no backend:', error.message)
      throw error
    }
  },

  async listarTodos() {
    return this.listar()
  },

  async listarPedidos() {
    return this.listar()
  },

  async obterPorId(id) {
    try {
      console.log(`🔄 Buscando pedido ID ${id} no backend...`)
      const data = await api.get(`/api/solicitacoes-pedido/${id}`)
      console.log('✅ Pedido carregado do backend')
      return data
    } catch (error) {
      console.error(`❌ Erro ao obter pedido ID ${id} no backend:`, error.message)
      throw error
    }
  },

  async buscarPedido(id) {
    return this.obterPorId(id)
  },

  async criar(pedido) {
    try {
      console.log('🔄 Criando pedido no backend...')
      console.log('📋 Dados do pedido recebidos:', JSON.stringify(pedido, null, 2))

      // Validar estrutura de dados
      if (!pedido.itens || pedido.itens.length === 0) {
        throw new Error('Pedido deve ter pelo menos um item')
      }

      pedido.itens.forEach((item, index) => {
        if (!item.nome || item.nome.trim() === '') {
          throw new Error(`Item ${index + 1}: Nome é obrigatório`)
        }
        if (!item.quantidade || item.quantidade <= 0) {
          throw new Error(`Item ${index + 1}: Quantidade deve ser maior que zero`)
        }
        // Validar limites de caracteres conforme backend
        if (item.nome && item.nome.length > 255) {
          throw new Error(`Item ${index + 1}: Nome deve ter no máximo 255 caracteres`)
        }
        if (item.descricao && item.descricao.length > 500) {
          throw new Error(`Item ${index + 1}: Descrição deve ter no máximo 500 caracteres`)
        }
        if (item.observacao && item.observacao.length > 255) {
          throw new Error(`Item ${index + 1}: Observação deve ter no máximo 255 caracteres`)
        }
      })

      // Validar observação do pedido
      if (pedido.observacao && pedido.observacao.length > 1000) {
        throw new Error('Observação do pedido deve ter no máximo 1000 caracteres')
      }

      // Usar API real do backend
      const data = await api.post('/api/solicitacoes-pedido', pedido)
      console.log('✅ Pedido criado no backend:', data.id)
      return { data: data }
    } catch (error) {
      console.error('❌ Erro ao criar pedido:', error.message)
      throw error
    }
  },

  async adicionarPedido(pedido) {
    return this.criar(pedido)
  },

  async atualizar(id, pedido) {
    try {
      console.log(`🔄 Atualizando pedido ID ${id} no backend...`)
      const data = await api.put(`/api/solicitacoes-pedido/${id}`, pedido)
      console.log('✅ Pedido atualizado no backend')
      return data
    } catch (error) {
      console.error(`❌ Erro ao atualizar pedido ID ${id} no backend:`, error.message)
      throw error
    }
  },

  async atualizarPedido(id, pedido) {
    return this.atualizar(id, pedido)
  },

  async alterarStatus(id, novoStatus) {
    try {
      console.log(`🔄 Alterando status do pedido ID ${id} para ${novoStatus}...`)

      // Primeiro busca o pedido atual
      const pedidoAtual = await this.obterPorId(id)

      // Atualiza apenas o status
      const pedidoAtualizado = {
        ...pedidoAtual,
        status: novoStatus
      }

      const data = await api.put(`/api/solicitacoes-pedido/${id}`, pedidoAtualizado)
      console.log('✅ Status do pedido alterado com sucesso')
      return data
    } catch (error) {
      console.error(`❌ Erro ao alterar status do pedido ID ${id}:`, error.message)
      throw error
    }
  },

  async remover(id) {
    try {
      console.log(`🔄 Removendo pedido ID ${id} no backend...`)
      await api.delete(`/api/solicitacoes-pedido/${id}`)
      console.log('✅ Pedido removido do backend')
      return true
    } catch (error) {
      console.error(`❌ Erro ao remover pedido ID ${id} no backend:`, error.message)
      throw error
    }
  },

  async excluir(id) {
    return this.remover(id)
  },

  async removerPedido(id) {
    return this.remover(id)
  },

  async cancelarPedido(id) {
    return this.remover(id)
  },

  async aprovar(id) {
    try {
      console.log(`🔄 Aprovando pedido ID ${id} no backend...`)
      const data = await api.patch(`/api/solicitacoes-pedido/${id}/aprovar`)
      console.log('✅ Pedido aprovado no backend')
      return data
    } catch (error) {
      console.error(`❌ Erro ao aprovar pedido ID ${id} no backend:`, error.message)
      throw error
    }
  },

  async aprovarPedido(id) {
    return this.aprovar(id)
  },

  async rejeitar(id, motivo = '') {
    try {
      console.log(`🔄 Rejeitando pedido ID ${id} no backend...`)
      const data = await api.patch(`/api/solicitacoes-pedido/${id}/rejeitar`, { motivo })
      console.log('✅ Pedido rejeitado no backend')
      return data
    } catch (error) {
      console.error(`❌ Erro ao rejeitar pedido ID ${id} no backend:`, error.message)
      throw error
    }
  },

  async rejeitarPedido(id, motivo) {
    return this.rejeitar(id, motivo)
  },

  async criarPedido(pedido) {
    return this.criar(pedido)
  }
}

// Utilitários para manipulação de pedidos
export const pedidoUtils = {
  calcularTotalItem(item) {
    return (item.quantidade || 0) * (item.preco || 0)
  },

  calcularTotalPedido(pedido) {
    if (!pedido.itens || !Array.isArray(pedido.itens)) return 0
    return pedido.itens.reduce((total, item) => total + this.calcularTotalItem(item), 0)
  },

  formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(valor || 0)
  },

  formatarData(data) {
    if (!data) return ''
    return new Date(data).toLocaleDateString('pt-BR')
  },

  statusConfig: {
    PENDENTE: { label: 'Pendente', class: 'warning' },
    APROVADO: { label: 'Aprovado', class: 'success' },
    EM_ANDAMENTO: { label: 'Em Andamento', class: 'info' },
    CANCELADO: { label: 'Cancelado', class: 'secondary' },
    // Manter compatibilidade com status antigos (lowercase)
    pendente: { label: 'Pendente', class: 'warning' },
    aprovado: { label: 'Aprovado', class: 'success' },
    rejeitado: { label: 'Rejeitado', class: 'danger' },
    em_andamento: { label: 'Em Andamento', class: 'info' },
    finalizado: { label: 'Finalizado', class: 'success' },
    cancelado: { label: 'Cancelado', class: 'secondary' }
  },

  obterStatusInfo(status) {
    return this.statusConfig[status] || { label: status, class: 'secondary' }
  }
}

// Export default único e limpo
export default pedidoService
