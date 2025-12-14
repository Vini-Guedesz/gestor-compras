/**
 * @fileoverview Serviço de Histórico de Pedidos
 * 
 * Módulo responsável por consultas ao histórico de modificações dos pedidos.
 * Permite auditoria completa de todas as alterações realizadas em pedidos.
 * 
 * @module services/historicoPedidoService
 * @requires ./api
 * @requires ../utils/logger
 * 
 * @description
 * Este serviço implementa:
 * - Listagem de histórico por pedido específico
 * - Listagem de histórico por usuário
 * - Configuração de tipos de modificação com labels e ícones
 * - Formatação de datas para exibição
 * 
 * @example
 * const historico = await historicoPedidoService.listarPorPedido(123)
 * 
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

import api from './api.js'
import logger from '../utils/logger.js'

/**
 * Serviço de histórico de pedidos
 * @namespace historicoPedidoService
 */
const historicoPedidoService = {
  /**
   * Lista histórico de modificações de um pedido específico
   * 
   * @async
   * @function listarPorPedido
   * @memberof historicoPedidoService
   * @param {number} pedidoId - ID do pedido
   * @returns {Promise<Array>} Histórico de modificações
   * @throws {Error} Erro de comunicação com API
   * 
   * @example
   * const historico = await historicoPedidoService.listarPorPedido(123)
   * historico.forEach(h => console.log(h.tipoModificacao, h.data))
   */
  async listarPorPedido(pedidoId) {
    try {
      const data = await api.get(`/api/v1/historico-pedidos/pedido/${pedidoId}`)
      return data
    } catch (error) {
      logger.error(`Erro ao buscar histórico do pedido ${pedidoId}:`, error.message)
      throw error
    }
  },

  /**
   * Lista histórico de modificações realizadas por um usuário
   * 
   * @async
   * @function listarPorUsuario
   * @memberof historicoPedidoService
   * @param {number} userId - ID do usuário
   * @returns {Promise<Array>} Histórico de ações do usuário
   * @throws {Error} Erro de comunicação com API
   * 
   * @example
   * const historico = await historicoPedidoService.listarPorUsuario(10)
   */
  async listarPorUsuario(userId) {
    try {
      const data = await api.get(`/api/v1/historico-pedidos/usuario/${userId}`)
      return data
    } catch (error) {
      logger.error(`Erro ao buscar histórico do usuário ${userId}:`, error.message)
      throw error
    }
  }
}

/**
 * Configuração de tipos de modificação para exibição
 * 
 * @const {Object.<string, {label: string, icon: string, color: string}>}
 * @memberof historicoPedidoService
 * 
 * @description
 * Mapeia tipos de modificação para informações de apresentação:
 * - label: Texto descritivo
 * - icon: Nome do ícone
 * - color: Cor em hexadecimal
 */
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

/**
 * Formata data para exibição no histórico
 * 
 * @function formatarDataHistorico
 * @param {string|Date} data - Data a formatar (ISO 8601 ou objeto Date)
 * @returns {string} Data formatada (dd/mm/aaaa hh:mm) ou string vazia
 * 
 * @example
 * formatarDataHistorico('2024-01-15T10:30:00Z') // '15/01/2024 10:30'
 */
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

/**
 * @exports historicoPedidoService
 * @exports tipoModificacaoConfig
 * @exports formatarDataHistorico
 */
export default historicoPedidoService
