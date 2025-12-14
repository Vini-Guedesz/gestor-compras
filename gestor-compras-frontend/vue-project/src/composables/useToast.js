/**
 * @fileoverview Composable de Sistema de Notificações Toast
 * 
 * Sistema global de notificações estilizadas (toast) para substituir alert()
 * e fornecer feedback visual consistente ao usuário. Suporta múltiplos tipos,
 * auto-fechamento configurável, ações customizadas e gerenciamento de fila.
 * 
 * @module composables/useToast
 * @requires vue
 * 
 * @description
 * Este composable implementa:
 * - Sistema global de notificações toast
 * - 4 tipos: success, error, warning, info
 * - Auto-fechamento configurável
 * - Ações customizadas com callbacks
 * - Fila de múltiplos toasts
 * - Remoção manual ou automática
 * - Estado compartilhado entre componentes
 * 
 * @example
 * // Uso básico
 * import { useToast } from '@/composables/useToast'
 * 
 * const { success, error, warning, info } = useToast()
 * 
 * // Notificações simples
 * success('Pedido criado com sucesso!')
 * error('Erro ao salvar dados')
 * warning('Este campo é obrigatório')
 * info('Carregando informações...')
 * 
 * @example
 * // Com opções avançadas
 * const { addToast } = useToast()
 * 
 * addToast({
 *   message: 'Arquivo enviado',
 *   type: 'success',
 *   duration: 3000,
 *   actionText: 'Desfazer',
 *   onAction: () => {
 *     console.log('Desfazer clicado')
 *   }
 * })
 * 
 * @example
 * // Renderização de toasts (em App.vue ou layout)
 * import { useToast } from '@/composables/useToast'
 * 
 * const { toasts, removeToast } = useToast()
 * 
 * // Template
 * <div v-for="toast in toasts" :key="toast.id">
 *   <div :class="`toast-${toast.type}`">
 *     {{ toast.message }}
 *     <button @click="removeToast(toast.id)">×</button>
 *   </div>
 * </div>
 * 
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

import { ref } from 'vue'

/**
 * Estado global de toasts compartilhado
 * @type {import('vue').Ref<Array<Toast>>}
 * @private
 */
const toasts = ref([])

/**
 * Contador de IDs para toasts
 * @type {number}
 * @private
 */
let nextId = 0

/**
 * @typedef {Object} Toast
 * @property {number} id - ID único do toast
 * @property {string} message - Mensagem a ser exibida
 * @property {'success'|'error'|'warning'|'info'} type - Tipo do toast
 * @property {number} duration - Duração em ms (0 = sem auto-close)
 * @property {string|null} actionText - Texto do botão de ação
 * @property {Function|null} onAction - Callback ao clicar na ação
 */

/**
 * @typedef {Object} ToastOptions
 * @property {string} message - Mensagem a ser exibida
 * @property {'success'|'error'|'warning'|'info'} [type='info'] - Tipo do toast
 * @property {number} [duration=5000] - Duração em ms (0 = sem auto-close)
 * @property {string} [actionText] - Texto do botão de ação
 * @property {Function} [onAction] - Callback ao clicar na ação
 */

/**
 * Composable de gerenciamento de notificações toast
 * 
 * @function useToast
 * @returns {Object} Objeto com métodos de controle de toasts
 * @returns {import('vue').Ref<Array<Toast>>} returns.toasts - Array reativo de toasts ativos
 * @returns {Function} returns.addToast - Adiciona novo toast customizado
 * @returns {Function} returns.removeToast - Remove toast específico por ID
 * @returns {Function} returns.success - Atalho para toast de sucesso
 * @returns {Function} returns.error - Atalho para toast de erro
 * @returns {Function} returns.warning - Atalho para toast de aviso
 * @returns {Function} returns.info - Atalho para toast de informação
 * @returns {Function} returns.clearAll - Remove todos os toasts
 * 
 * @description
 * Fornece controle completo sobre sistema de notificações toast.
 * Estado global garante que toasts sejam visíveis independente
 * de onde foram criados na aplicação.
 */
export function useToast() {
  /**
   * Adiciona um novo toast à fila
   * 
   * @function addToast
   * @param {ToastOptions} options - Opções de configuração do toast
   * @returns {number} ID do toast criado (para remoção manual)
   * 
   * @example
   * const toastId = addToast({
   *   message: 'Operação concluída',
   *   type: 'success',
   *   duration: 3000
   * })
   * 
   * // Remover manualmente depois
   * setTimeout(() => removeToast(toastId), 1000)
   * 
   * @example
   * // Com ação customizada
   * addToast({
   *   message: 'Item excluído',
   *   type: 'warning',
   *   duration: 0, // Não fecha automaticamente
   *   actionText: 'Desfazer',
   *   onAction: () => restoreItem()
   * })
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
   * Remove um toast específico da fila
   * 
   * @function removeToast
   * @param {number} id - ID do toast a ser removido
   * 
   * @example
   * const id = success('Salvo!')
   * 
   * // Remover após 2 segundos
   * setTimeout(() => removeToast(id), 2000)
   */
  const removeToast = (id) => {
    const index = toasts.value.findIndex(t => t.id === id)
    if (index !== -1) {
      toasts.value.splice(index, 1)
    }
  }

  /**
   * Cria toast de sucesso (verde)
   * 
   * @function success
   * @param {string} message - Mensagem de sucesso
   * @param {Omit<ToastOptions, 'message'|'type'>} [options={}] - Opções adicionais
   * @returns {number} ID do toast criado
   * 
   * @example
   * success('Pedido criado com sucesso!')
   * success('Salvo!', { duration: 2000 })
   */
  const success = (message, options = {}) => {
    return addToast({ ...options, message, type: 'success' })
  }

  /**
   * Cria toast de erro (vermelho)
   * 
   * @function error
   * @param {string} message - Mensagem de erro
   * @param {Omit<ToastOptions, 'message'|'type'>} [options={}] - Opções adicionais
   * @returns {number} ID do toast criado
   * 
   * @example
   * error('Erro ao salvar dados')
   * error('Conexão perdida', { duration: 0 })
   */
  const error = (message, options = {}) => {
    return addToast({ ...options, message, type: 'error' })
  }

  /**
   * Cria toast de aviso (amarelo/laranja)
   * 
   * @function warning
   * @param {string} message - Mensagem de aviso
   * @param {Omit<ToastOptions, 'message'|'type'>} [options={}] - Opções adicionais
   * @returns {number} ID do toast criado
   * 
   * @example
   * warning('Este campo é obrigatório')
   * warning('Sessão expirando em 5 minutos')
   */
  const warning = (message, options = {}) => {
    return addToast({ ...options, message, type: 'warning' })
  }

  /**
   * Cria toast de informação (azul)
   * 
   * @function info
   * @param {string} message - Mensagem informativa
   * @param {Omit<ToastOptions, 'message'|'type'>} [options={}] - Opções adicionais
   * @returns {number} ID do toast criado
   * 
   * @example
   * info('Carregando dados...')
   * info('Nova versão disponível', { actionText: 'Atualizar' })
   */
  const info = (message, options = {}) => {
    return addToast({ ...options, message, type: 'info' })
  }

  /**
   * Remove todos os toasts ativos
   * 
   * @function clearAll
   * 
   * @example
   * // Limpar todos ao fazer logout
   * const handleLogout = () => {
   *   clearAll()
   *   logout()
   * }
   * 
   * @example
   * // Limpar antes de mostrar toast crítico
   * clearAll()
   * error('Erro crítico do sistema')
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
