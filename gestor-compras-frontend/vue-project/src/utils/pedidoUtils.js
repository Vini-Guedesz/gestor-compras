/**
 * @fileoverview Utilitários de Pedidos, Cotações e Formatação
 * 
 * Funções auxiliares para manipulação de pedidos, cotações, itens e formatação
 * de dados (datas, preços, status). Centraliza lógica compartilhada entre
 * componentes de pedidos, cotações e relatórios.
 * 
 * @module utils/pedidoUtils
 * 
 * @description
 * Este módulo implementa:
 * - Verificação de cotações em itens
 * - Extração de IDs de itens de cotações
 * - Formatação de datas (brasileiro)
 * - Formatação de preços e moeda (BRL)
 * - Configuração de status com labels e cores
 * - Validação de permissões de pedidos
 * - Cálculo de totais e quantidades
 * 
 * @example
 * // Formatação
 * import { formatarMoeda, formatarData } from '@/utils/pedidoUtils'
 * 
 * formatarMoeda(1500.50) // 'R$ 1.500,50'
 * formatarData('2024-12-14') // '14/12/2024'
 * 
 * @example
 * // Status
 * import { getStatusLabel, getStatusColor } from '@/utils/pedidoUtils'
 * 
 * getStatusLabel('PENDENTE') // 'Pendente'
 * getStatusColor('APROVADO') // '#10b981'
 * 
 * @example
 * // Validações
 * import { podeEditarPedido, podeExcluirPedido } from '@/utils/pedidoUtils'
 * 
 * podeEditarPedido(pedido) // true/false
 * podeExcluirPedido(pedido) // true/false
 * 
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

/**
 * Verifica se um item possui cotação associada
 * 
 * @function itemTemCotacao
 * @param {number} itemId - ID do item a verificar
 * @param {Array<Object>} cotacoes - Lista de cotações
 * @returns {boolean} true se item está em alguma cotação
 * 
 * @example
 * const cotacoes = [
 *   { id: 1, itensPedidoIds: [10, 20, 30] },
 *   { id: 2, itensRascunhoIds: [40, 50] }
 * ]
 * 
 * itemTemCotacao(20, cotacoes) // true
 * itemTemCotacao(99, cotacoes) // false
 * 
 * @description
 * Verifica múltiplos formatos de cotação:
 * 1. itensRascunhoIds[] - Para rascunhos de cotação
 * 2. itensPedidoIds[] - Para pedidos (array múltiplo)
 * 3. itemPedidoId - Para pedidos (ID único, compatibilidade)
 */
export const itemTemCotacao = (itemId, cotacoes) => {
  if (!cotacoes || !Array.isArray(cotacoes)) return false

  return cotacoes.some(c => {
    // Para rascunhos: verificar itensRascunhoIds
    if (c.itensRascunhoIds && c.itensRascunhoIds.length > 0) {
      return c.itensRascunhoIds.includes(itemId)
    }
    // Para pedidos: verificar itensPedidoIds (array)
    if (c.itensPedidoIds && c.itensPedidoIds.length > 0) {
      return c.itensPedidoIds.includes(itemId)
    }
    // Para pedidos: verificar itemPedidoId (singular - compatibilidade)
    if (c.itemPedidoId) {
      return c.itemPedidoId === itemId
    }
    return false
  })
}

/**
 * Extrai IDs dos itens de uma cotação
 * 
 * @function getItensIdsDaCotacao
 * @param {Object} cotacao - Objeto da cotação
 * @param {number[]} [cotacao.itensRascunhoIds] - IDs de rascunhos
 * @param {number[]} [cotacao.itensPedidoIds] - IDs de pedidos (array)
 * @param {number} [cotacao.itemPedidoId] - ID de pedido (único)
 * @returns {number[]} Array com IDs dos itens (vazio se nenhum)
 * 
 * @example
 * const cotacao = { id: 1, itensPedidoIds: [10, 20, 30] }
 * getItensIdsDaCotacao(cotacao) // [10, 20, 30]
 * 
 * @example
 * const rascunho = { id: 2, itensRascunhoIds: [5, 6] }
 * getItensIdsDaCotacao(rascunho) // [5, 6]
 * 
 * @description
 * Estratégia de extração (ordem de prioridade):
 * 1. itensRascunhoIds[] (rascunhos)
 * 2. itensPedidoIds[] (pedidos múltiplos)
 * 3. itemPedidoId (pedido único)
 * 4. [] (vazio se nenhum encontrado)
 */
export const getItensIdsDaCotacao = (cotacao) => {
  if (!cotacao) return []

  // Para rascunhos
  if (cotacao.itensRascunhoIds && cotacao.itensRascunhoIds.length > 0) {
    return cotacao.itensRascunhoIds
  }
  // Para pedidos - array de IDs
  if (cotacao.itensPedidoIds && cotacao.itensPedidoIds.length > 0) {
    return cotacao.itensPedidoIds
  }
  // Para pedidos - ID único (compatibilidade)
  if (cotacao.itemPedidoId) {
    return [cotacao.itemPedidoId]
  }
  return []
}

/**
 * Formata data no padrão brasileiro
 * 
 * @function formatarData
 * @param {string|Date} data - Data a ser formatada
 * @returns {string} Data formatada como 'DD/MM/AAAA' ou 'N/A'
 * 
 * @example
 * formatarData('2024-12-14') // '14/12/2024'
 * formatarData(new Date('2024-01-01')) // '01/01/2024'
 * formatarData(null) // 'N/A'
 * formatarData('invalid') // 'Data inválida'
 * 
 * @description
 * Usa toLocaleDateString('pt-BR') para formatação nativa.
 * Retorna 'N/A' se data for null/undefined.
 * Retorna 'Data inválida' se parsing falhar.
 */
export const formatarData = (data) => {
  if (!data) return 'N/A'
  try {
    return new Date(data).toLocaleDateString('pt-BR')
  } catch {
    return 'Data inválida'
  }
}

/**
 * Formata data e hora no padrão brasileiro
 * 
 * @function formatarDataHora
 * @param {string|Date} data - Data a ser formatada
 * @returns {string} Data formatada como 'DD/MM/AAAA HH:MM:SS' ou 'N/A'
 * 
 * @example
 * formatarDataHora('2024-12-14T15:30:00') // '14/12/2024 15:30:00'
 * formatarDataHora(new Date()) // '14/12/2024 10:45:23'
 * formatarDataHora(null) // 'N/A'
 * 
 * @description
 * Usa toLocaleString('pt-BR') para incluir hora.
 * Ideal para logs, histórico, timestamps.
 */
export const formatarDataHora = (data) => {
  if (!data) return 'N/A'
  try {
    return new Date(data).toLocaleString('pt-BR')
  } catch {
    return 'Data inválida'
  }
}

/**
 * Formata número como preço (sem símbolo de moeda)
 * 
 * @function formatarPreco
 * @param {number|string} preco - Preço a ser formatado
 * @returns {string} Preço formatado como '1.234,56'
 * 
 * @example
 * formatarPreco(1234.56) // '1.234,56'
 * formatarPreco('999.99') // '999,99'
 * formatarPreco(0) // '0,00'
 * formatarPreco(null) // '0,00'
 * 
 * @description
 * Usa toLocaleString('pt-BR') com 2 casas decimais.
 * Retorna '0,00' se valor for null/undefined.
 * Ideal para exibir preços em tabelas sem R$.
 */
export const formatarPreco = (preco) => {
  if (!preco && preco !== 0) return '0,00'
  return parseFloat(preco).toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

/**
 * Formata valor como moeda brasileira completa
 * 
 * @function formatarMoeda
 * @param {number|string} valor - Valor a ser formatado
 * @returns {string} Moeda formatada como 'R$ 1.234,56'
 * 
 * @example
 * formatarMoeda(1500.50) // 'R$ 1.500,50'
 * formatarMoeda('2999.99') // 'R$ 2.999,99'
 * formatarMoeda(0) // 'R$ 0,00'
 * formatarMoeda(null) // 'R$ 0,00'
 * 
 * @description
 * Usa Intl.NumberFormat com currency: 'BRL'.
 * Inclui símbolo R$ automaticamente.
 * Ideal para totais, valores finais, relatórios.
 */
export const formatarMoeda = (valor) => {
  if (!valor && valor !== 0) return 'R$ 0,00'
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(valor)
}

/**
 * Configuração de status de pedidos
 * 
 * @constant {Object.<string, StatusConfig>}
 * 
 * @typedef {Object} StatusConfig
 * @property {string} label - Label amigável do status
 * @property {string} class - Classe CSS para estilização
 * @property {string} color - Cor hexadecimal para badges
 * 
 * @description
 * Mapeia status do backend para apresentação visual.
 * Suporta tanto uppercase (novo) quanto lowercase (legacy).
 * 
 * Status disponíveis:
 * - RASCUNHO: Pedido em edição
 * - EM_COTACAO: Itens sendo cotados
 * - RASCUNHO_FINALIZADO: Rascunho pronto para virar pedido
 * - PENDENTE: Aguardando aprovação
 * - EM_ANALISE: Em revisão
 * - EM_ANDAMENTO: Sendo processado
 * - APROVADO: Aprovado para compra
 * - CANCELADO: Cancelado pelo usuário
 * - REJEITADO: Rejeitado por gestor
 * 
 * @example
 * statusConfig['APROVADO']
 * // { label: 'Aprovado', class: 'status-approved', color: '#10b981' }
 */
export const statusConfig = {
  // Status do backend (uppercase)
  'RASCUNHO': { label: 'Rascunho', class: 'status-draft', color: '#6366f1' },
  'EM_COTACAO': { label: 'Em Cotação', class: 'status-quoting', color: '#8b5cf6' },
  'RASCUNHO_FINALIZADO': { label: 'Rascunho Finalizado', class: 'status-draft-finished', color: '#64748b' },
  'PENDENTE': { label: 'Pendente', class: 'status-pending', color: '#f59e0b' },
  'EM_ANALISE': { label: 'Em Análise', class: 'status-progress', color: '#3b82f6' },
  'EM_ANDAMENTO': { label: 'Em Andamento', class: 'status-progress', color: '#3b82f6' },
  'APROVADO': { label: 'Aprovado', class: 'status-approved', color: '#10b981' },
  'CANCELADO': { label: 'Cancelado', class: 'status-canceled', color: '#ef4444' },
  'REJEITADO': { label: 'Rejeitado', class: 'status-rejected', color: '#ef4444' },
  // Status antigos (lowercase) - compatibilidade
  'rascunho': { label: 'Rascunho', class: 'status-draft', color: '#6366f1' },
  'pendente': { label: 'Pendente', class: 'status-pending', color: '#f59e0b' },
  'em_analise': { label: 'Em Análise', class: 'status-progress', color: '#3b82f6' },
  'em_andamento': { label: 'Em Andamento', class: 'status-progress', color: '#3b82f6' },
  'aprovado': { label: 'Aprovado', class: 'status-approved', color: '#10b981' },
  'cancelado': { label: 'Cancelado', class: 'status-canceled', color: '#ef4444' }
}

/**
 * Obtém label amigável do status
 * 
 * @function getStatusLabel
 * @param {string} status - Status do pedido (ex: 'APROVADO', 'PENDENTE')
 * @returns {string} Label formatado ou 'Indefinido'
 * 
 * @example
 * getStatusLabel('APROVADO') // 'Aprovado'
 * getStatusLabel('EM_ANALISE') // 'Em Análise'
 * getStatusLabel('UNKNOWN') // 'UNKNOWN'
 * getStatusLabel(null) // 'Indefinido'
 */
export const getStatusLabel = (status) => {
  return statusConfig[status]?.label || status || 'Indefinido'
}

/**
 * Obtém classe CSS do status
 * 
 * @function getStatusClass
 * @param {string} status - Status do pedido
 * @returns {string} Classe CSS (ex: 'status-approved')
 * 
 * @example
 * getStatusClass('APROVADO') // 'status-approved'
 * getStatusClass('PENDENTE') // 'status-pending'
 * getStatusClass('UNKNOWN') // 'status-default'
 * 
 * @description
 * Use para aplicar estilos condicionais:
 * <div :class="getStatusClass(pedido.status)">{{ pedido.status }}</div>
 */
export const getStatusClass = (status) => {
  return statusConfig[status]?.class || 'status-default'
}

/**
 * Obtém cor hexadecimal do status
 * 
 * @function getStatusColor
 * @param {string} status - Status do pedido
 * @returns {string} Cor hex (ex: '#10b981' para verde)
 * 
 * @example
 * getStatusColor('APROVADO') // '#10b981' (verde)
 * getStatusColor('CANCELADO') // '#ef4444' (vermelho)
 * getStatusColor('PENDENTE') // '#f59e0b' (amarelo)
 * 
 * @description
 * Use para badges, chips, indicadores visuais:
 * <span :style="{ color: getStatusColor(status) }">{{ status }}</span>
 */
export const getStatusColor = (status) => {
  return statusConfig[status]?.color || '#6b7280'
}

/**
 * Calcula quantidade total de itens
 * 
 * @function calcularQuantidadeItens
 * @param {Object} pedido - Objeto do pedido
 * @param {Array<Object>} pedido.itens - Array de itens do pedido
 * @param {number} pedido.itens[].quantidade - Quantidade do item
 * @returns {number} Soma total de quantidades (0 se sem itens)
 * 
 * @example
 * const pedido = {
 *   itens: [
 *     { nome: 'Caneta', quantidade: 10 },
 *     { nome: 'Papel', quantidade: 5 }
 *   ]
 * }
 * calcularQuantidadeItens(pedido) // 15
 * 
 * @description
 * Soma campo quantidade de todos os itens.
 * Usa 1 como fallback se quantidade não existir.
 */
export const calcularQuantidadeItens = (pedido) => {
  if (!pedido?.itens || !Array.isArray(pedido.itens)) return 0
  return pedido.itens.reduce((total, item) => total + (item.quantidade || 1), 0)
}

/**
 * Verifica se pedido pode ser editado
 * 
 * @function podeEditarPedido
 * @param {Object} pedido - Objeto do pedido
 * @param {boolean} [pedido.isRascunho] - Se é rascunho
 * @param {boolean} [pedido.isFinalizado] - Se está finalizado
 * @param {string} [pedido.status] - Status do pedido
 * @returns {boolean} true se pode editar
 * 
 * @example
 * podeEditarPedido({ isRascunho: true }) // true
 * podeEditarPedido({ status: 'PENDENTE' }) // true
 * podeEditarPedido({ status: 'APROVADO' }) // false
 * podeEditarPedido({ isRascunho: true, isFinalizado: true }) // false
 * 
 * @description
 * Regras de edição:
 * - Rascunho finalizado: NÃO pode editar
 * - Rascunho ativo: PODE editar
 * - Pedido PENDENTE: PODE editar
 * - Outros status: NÃO pode editar
 */
export const podeEditarPedido = (pedido) => {
  if (!pedido) return false

  // Rascunhos finalizados não podem ser editados
  if (pedido.isRascunho && pedido.isFinalizado) return false

  // Rascunhos ativos podem ser editados
  if (pedido.isRascunho) return true

  // Pedidos só podem ser editados se pendentes
  return ['PENDENTE', 'pendente'].includes(pedido.status)
}

/**
 * Verifica se pedido pode ser excluído
 * 
 * @function podeExcluirPedido
 * @param {Object} pedido - Objeto do pedido
 * @returns {boolean} true se pode excluir
 * 
 * @example
 * podeExcluirPedido({ isRascunho: true }) // true
 * podeExcluirPedido({ status: 'PENDENTE' }) // true
 * podeExcluirPedido({ status: 'APROVADO' }) // false
 * podeExcluirPedido({ isRascunho: true, isFinalizado: true }) // false
 * 
 * @description
 * Regras de exclusão:
 * - Rascunho finalizado: NÃO pode excluir
 * - Rascunho ativo: PODE excluir
 * - Status RASCUNHO/PENDENTE/EM_COTACAO: PODE excluir
 * - Outros status: NÃO pode excluir
 */
export const podeExcluirPedido = (pedido) => {
  if (!pedido) return false

  // Rascunhos finalizados não podem ser excluídos
  if (pedido.isRascunho && pedido.isFinalizado) return false

  // Rascunhos e pedidos pendentes podem ser excluídos
  const statusPermitidos = ['RASCUNHO', 'PENDENTE', 'EM_COTACAO', 'rascunho', 'pendente']
  return pedido.isRascunho || statusPermitidos.includes(pedido.status)
}

/**
 * Verifica se pedido pode ser aprovado
 * 
 * @function podeAprovarPedido
 * @param {Object} pedido - Objeto do pedido
 * @returns {boolean} true se pode aprovar
 * 
 * @example
 * podeAprovarPedido({ status: 'PENDENTE' }) // true
 * podeAprovarPedido({ status: 'EM_ANALISE' }) // true
 * podeAprovarPedido({ status: 'APROVADO' }) // false
 * podeAprovarPedido({ isRascunho: true }) // false
 * 
 * @description
 * Regras de aprovação:
 * - Rascunho: NÃO pode aprovar (precisa finalizar antes)
 * - Status PENDENTE ou EM_ANALISE: PODE aprovar
 * - Outros status: NÃO pode aprovar
 */
export const podeAprovarPedido = (pedido) => {
  if (!pedido) return false
  if (pedido.isRascunho) return false

  return ['PENDENTE', 'EM_ANALISE', 'pendente', 'em_analise'].includes(pedido.status)
}

export default {
  itemTemCotacao,
  getItensIdsDaCotacao,
  formatarData,
  formatarDataHora,
  formatarPreco,
  formatarMoeda,
  statusConfig,
  getStatusLabel,
  getStatusClass,
  getStatusColor,
  calcularQuantidadeItens,
  podeEditarPedido,
  podeExcluirPedido,
  podeAprovarPedido
}
