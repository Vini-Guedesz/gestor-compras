/**
 * Utilitários compartilhados para Pedidos e Cotações
 */

/**
 * Verifica se um item possui cotação associada
 * @param {number} itemId - ID do item
 * @param {Array} cotacoes - Lista de cotações
 * @returns {boolean}
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
 * Obtém os IDs dos itens de uma cotação
 * @param {Object} cotacao - Objeto da cotação
 * @returns {Array<number>}
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
 * Formata data para exibição no padrão brasileiro
 * @param {string|Date} data - Data a ser formatada
 * @returns {string}
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
 * Formata data e hora para exibição no padrão brasileiro
 * @param {string|Date} data - Data a ser formatada
 * @returns {string}
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
 * Formata preço para exibição no padrão brasileiro
 * @param {number|string} preco - Preço a ser formatado
 * @returns {string}
 */
export const formatarPreco = (preco) => {
  if (!preco && preco !== 0) return '0,00'
  return parseFloat(preco).toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

/**
 * Formata moeda completa (R$)
 * @param {number|string} valor - Valor a ser formatado
 * @returns {string}
 */
export const formatarMoeda = (valor) => {
  if (!valor && valor !== 0) return 'R$ 0,00'
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(valor)
}

/**
 * Configuração de labels e classes para status de pedidos
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
 * Obtém o label de um status
 * @param {string} status - Status do pedido
 * @returns {string}
 */
export const getStatusLabel = (status) => {
  return statusConfig[status]?.label || status || 'Indefinido'
}

/**
 * Obtém a classe CSS de um status
 * @param {string} status - Status do pedido
 * @returns {string}
 */
export const getStatusClass = (status) => {
  return statusConfig[status]?.class || 'status-default'
}

/**
 * Obtém a cor de um status
 * @param {string} status - Status do pedido
 * @returns {string}
 */
export const getStatusColor = (status) => {
  return statusConfig[status]?.color || '#6b7280'
}

/**
 * Calcula o total de quantidade de itens em um pedido
 * @param {Object} pedido - Objeto do pedido
 * @returns {number}
 */
export const calcularQuantidadeItens = (pedido) => {
  if (!pedido?.itens || !Array.isArray(pedido.itens)) return 0
  return pedido.itens.reduce((total, item) => total + (item.quantidade || 1), 0)
}

/**
 * Verifica se um pedido pode ser editado
 * @param {Object} pedido - Objeto do pedido
 * @returns {boolean}
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
 * Verifica se um pedido pode ser excluído
 * @param {Object} pedido - Objeto do pedido
 * @returns {boolean}
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
 * Verifica se um pedido pode ser aprovado
 * @param {Object} pedido - Objeto do pedido
 * @returns {boolean}
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
