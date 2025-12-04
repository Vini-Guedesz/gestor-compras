import { ref } from 'vue'

// Estado global compartilhado entre todas as instâncias
const toasts = ref([])
let nextId = 0

/**
 * Composable para gerenciar notificações toast
 *
 * Fornece um sistema global de notificações estilizadas
 * para substituir os alert() genéricos
 */
export function useToast() {
  /**
   * Adiciona um novo toast
   * @param {Object} options - Opções do toast
   * @param {string} options.message - Mensagem a ser exibida
   * @param {string} options.type - Tipo: 'success', 'error', 'warning', 'info'
   * @param {number} options.duration - Duração em ms (0 = sem auto-close)
   * @param {string} options.actionText - Texto do botão de ação (opcional)
   * @param {Function} options.onAction - Callback ao clicar na ação (opcional)
   */
  const addToast = (options) => {
    const id = nextId++
    const toast = {
      id,
      message: options.message || '',
      type: options.type || 'info',
      duration: options.duration !== undefined ? options.duration : 5000,
      actionText: options.actionText || null,
      onAction: options.onAction || null
    }

    toasts.value.push(toast)

    return id
  }

  /**
   * Remove um toast específico
   * @param {number} id - ID do toast a remover
   */
  const removeToast = (id) => {
    const index = toasts.value.findIndex(t => t.id === id)
    if (index !== -1) {
      toasts.value.splice(index, 1)
    }
  }

  /**
   * Atalhos para tipos específicos de toast
   */
  const success = (message, options = {}) => {
    return addToast({ ...options, message, type: 'success' })
  }

  const error = (message, options = {}) => {
    return addToast({ ...options, message, type: 'error' })
  }

  const warning = (message, options = {}) => {
    return addToast({ ...options, message, type: 'warning' })
  }

  const info = (message, options = {}) => {
    return addToast({ ...options, message, type: 'info' })
  }

  /**
   * Remove todos os toasts
   */
  const clearAll = () => {
    toasts.value = []
  }

  return {
    toasts,
    addToast,
    removeToast,
    success,
    error,
    warning,
    info,
    clearAll
  }
}
