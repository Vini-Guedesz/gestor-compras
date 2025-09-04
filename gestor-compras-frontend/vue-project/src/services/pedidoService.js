/**
 * Serviço de Pedidos
 *
 * Gerencia todas as operações relacionadas aos pedidos de compra
 */

import api from './api.js'

// Mock data para desenvolvimento
const mockPedidos = [
  {
    id: 1,
    numero: '2024-001',
    requisitante: 'João Silva',
    unidadeFuncional: 'TI',
    objetivo: 'reposicao',
    observacoes: 'Equipamentos para substituição de hardware defeituoso',
    status: 'enviado',
    dataPedido: '2024-01-15',
    dataUltimaAtualizacao: '2024-01-15',
    itens: [
      {
        id: 1,
        descricao: 'Notebook Dell Inspiron 15',
        quantidade: 2,
        unidade: 'un',
        justificativa: 'Substituição de equipamentos defeituosos'
      },
      {
        id: 2,
        descricao: 'Mouse óptico USB',
        quantidade: 5,
        unidade: 'un',
        justificativa: 'Reposição de acessórios'
      }
    ]
  },
  {
    id: 2,
    numero: '2024-002',
    requisitante: 'Maria Santos',
    unidadeFuncional: 'RH',
    objetivo: 'consumo',
    observacoes: 'Material para treinamentos',
    status: 'aprovado',
    dataPedido: '2024-01-18',
    dataUltimaAtualizacao: '2024-01-20',
    itens: [
      {
        id: 3,
        descricao: 'Papel A4 75g',
        quantidade: 10,
        unidade: 'cx',
        justificativa: 'Material para impressão de documentos de treinamento'
      }
    ]
  },
  {
    id: 3,
    numero: '2024-003',
    requisitante: 'Carlos Oliveira',
    unidadeFuncional: 'Comercial',
    objetivo: 'projeto',
    observacoes: 'Equipamentos para novo projeto',
    status: 'em_cotacao',
    dataPedido: '2024-01-22',
    dataUltimaAtualizacao: '2024-01-23',
    itens: [
      {
        id: 4,
        descricao: 'Projetor Full HD',
        quantidade: 1,
        unidade: 'un',
        justificativa: 'Apresentações do projeto'
      },
      {
        id: 5,
        descricao: 'Tela de projeção 2x2m',
        quantidade: 1,
        unidade: 'un',
        justificativa: 'Suporte para apresentações'
      }
    ]
  },
  {
    id: 4,
    numero: '2024-004',
    requisitante: 'Ana Costa',
    unidadeFuncional: 'Financeiro',
    objetivo: 'consumo',
    observacoes: 'Material de escritório',
    status: 'rascunho',
    dataPedido: '2024-01-25',
    dataUltimaAtualizacao: '2024-01-25',
    itens: [
      {
        id: 6,
        descricao: 'Canetas esferográficas',
        quantidade: 50,
        unidade: 'un',
        justificativa: 'Material para escritório'
      }
    ]
  }
]

const pedidoService = {
  async listar() {
    try {
      const data = await api.get('/api/solicitacoes-pedido')
      return data
    } catch (error) {
      console.error('Erro ao listar pedidos:', error)

      // Fallback para dados mock se a API falhar
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        console.warn('Usando dados mock - backend indisponível')
        // Simula delay da API
        await new Promise(resolve => setTimeout(resolve, 800))
        return mockPedidos
      }

      throw error
    }
  },

  async listarPedidos() {
    return this.listar()
  },

  async obterPorId(id) {
    try {
      const data = await api.get(`/api/solicitacoes-pedido/${id}`)
      return data
    } catch (error) {
      console.error(`Erro ao obter pedido ID ${id}:`, error)

      // Fallback para dados mock
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        await new Promise(resolve => setTimeout(resolve, 500))
        return mockPedidos.find(p => p.id === parseInt(id))
      }

      throw error
    }
  },

  async buscarPedido(id) {
    return this.obterPorId(id)
  },

  async criar(pedido) {
    try {
      const data = await api.post('/api/solicitacoes-pedido', pedido)
      return data
    } catch (error) {
      console.error('Erro ao criar pedido:', error)

      // Fallback para simulação
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        console.warn('Simulando criação - backend indisponível')
        await new Promise(resolve => setTimeout(resolve, 1200))
        return {
          ...pedido,
          id: Date.now(),
          status: 'pendente',
          dataCreated: new Date().toISOString().split('T')[0]
        }
      }

      throw error
    }
  },

  async criarPedido(pedido) {
    return this.criar(pedido)
  },

  async atualizar(id, pedido) {
    try {
      const data = await api.put(`/api/solicitacoes-pedido/${id}`, pedido)
      return data
    } catch (error) {
      console.error(`Erro ao atualizar pedido ID ${id}:`, error)

      // Fallback para simulação
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        console.warn('Simulando atualização - backend indisponível')
        await new Promise(resolve => setTimeout(resolve, 1000))
        return { ...pedido, id: parseInt(id) }
      }

      throw error
    }
  },

  async atualizarPedido(id, pedido) {
    return this.atualizar(id, pedido)
  },

  async remover(id) {
    try {
      await api.delete(`/api/solicitacoes-pedido/${id}`)
      return true
    } catch (error) {
      console.error(`Erro ao remover pedido ID ${id}:`, error)

      // Fallback para simulação
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        console.warn('Simulando remoção - backend indisponível')
        await new Promise(resolve => setTimeout(resolve, 800))
        return true
      }

      throw error
    }
  },

  async removerPedido(id) {
    return this.remover(id)
  },

  async cancelarPedido(id) {
    return this.remover(id)
  },

  async aprovar(id) {
    try {
      const data = await api.patch(`/api/solicitacoes-pedido/${id}/aprovar`)
      return data
    } catch (error) {
      console.error(`Erro ao aprovar pedido ID ${id}:`, error)

      // Fallback para simulação
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        console.warn('Simulando aprovação - backend indisponível')
        await new Promise(resolve => setTimeout(resolve, 1000))
        return { success: true, message: 'Pedido aprovado com sucesso' }
      }

      throw error
    }
  },

  async aprovarPedido(id) {
    return this.aprovar(id)
  },

  async rejeitar(id, motivo = '') {
    try {
      const data = await api.patch(`/api/solicitacoes-pedido/${id}/rejeitar`, { motivo })
      return data
    } catch (error) {
      console.error(`Erro ao rejeitar pedido ID ${id}:`, error)

      // Fallback para simulação
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        console.warn('Simulando rejeição - backend indisponível')
        await new Promise(resolve => setTimeout(resolve, 1000))
        return { success: true, message: 'Pedido rejeitado com sucesso' }
      }

      throw error
    }
  },

  async rejeitarPedido(id, motivo) {
    return this.rejeitar(id, motivo)
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
