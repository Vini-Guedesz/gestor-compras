/**
 * Serviço de Histórico de Pedidos
 *
 * Gerencia consultas ao histórico de modificações dos pedidos
 */

import api from './api.js'

const historicoPedidoService = {
  async listarPorPedido(pedidoId) {
    try {
      const data = await api.get(`/api/v1/historico-pedidos/pedido/${pedidoId}`)
      return data
    } catch (error) {
      console.error(`Erro ao buscar histórico do pedido ${pedidoId}:`, error.message)
      throw error
    }
  },

  async listarPorUsuario(userId) {
    try {
      const data = await api.get(`/api/v1/historico-pedidos/usuario/${userId}`)
      return data
    } catch (error) {
      console.error(`Erro ao buscar histórico do usuário ${userId}:`, error.message)
      throw error
    }
  }
}

// Configuração de tipos de modificação para exibição
export const tipoModificacaoConfig = {
  // Tipos de pedido
  CRIACAO: { label: 'Criação', icon: 'plus', color: '#10b981' },
  ATUALIZACAO: { label: 'Atualização', icon: 'edit', color: '#3b82f6' },
  MUDANCA_STATUS: { label: 'Mudança de Status', icon: 'refresh', color: '#f59e0b' },
  ADICAO_ITEM: { label: 'Item Adicionado', icon: 'plus-circle', color: '#10b981' },
  REMOCAO_ITEM: { label: 'Item Removido', icon: 'minus-circle', color: '#ef4444' },
  ATUALIZACAO_ITEM: { label: 'Item Atualizado', icon: 'edit', color: '#3b82f6' },
  ADICAO_COTACAO: { label: 'Cotação Adicionada', icon: 'document-add', color: '#8b5cf6' },
  REMOCAO_COTACAO: { label: 'Cotação Removida', icon: 'document-remove', color: '#ef4444' },
  EDICAO_COTACAO: { label: 'Cotação Editada', icon: 'pencil', color: '#f59e0b' },
  CANCELAMENTO: { label: 'Cancelamento', icon: 'x-circle', color: '#ef4444' },
  APROVACAO: { label: 'Aprovação', icon: 'check-circle', color: '#10b981' },
  // Tipos de rascunho
  CRIACAO_RASCUNHO: { label: 'Rascunho Criado', icon: 'plus', color: '#10b981' },
  ATUALIZACAO_OBSERVACAO: { label: 'Observação Atualizada', icon: 'edit', color: '#3b82f6' },
  CONVERSAO_PEDIDO: { label: 'Convertido para Pedido', icon: 'check-circle', color: '#10b981' }
}

export const formatarDataHistorico = (data) => {
  if (!data) return ''
  const date = new Date(data)
  return date.toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

export default historicoPedidoService
