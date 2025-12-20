/**
 * @fileoverview Composable para Gerenciamento de Modais de Erro
 *
 * Sistema global para exibir erros de forma elegante e consistente,
 * substituindo window.alert() por modais estilizados e acessíveis.
 *
 * @module composables/useErrorModal
 * @requires vue
 *
 * @description
 * Este composable implementa:
 * - Sistema global de modais de erro
 * - Suporte a diferentes tipos: error, warning, info
 * - Detalhamento de erros com lista expandível
 * - Callbacks para confirmação e cancelamento
 * - Estado compartilhado entre componentes
 *
 * @example
 * // Uso básico
 * import { useErrorModal } from '@/composables/useErrorModal'
 *
 * const { showError, showWarning, showInfo } = useErrorModal()
 *
 * // Erro simples
 * showError('Não foi possível salvar os dados')
 *
 * // Erro com detalhes
 * showError('Erro de validação', {
 *   details: ['Campo email é obrigatório', 'Senha deve ter 8 caracteres']
 * })
 *
 * @example
 * // Com callback de confirmação
 * const { showWarning } = useErrorModal()
 *
 * showWarning('Deseja realmente excluir este item?', {
 *   confirmText: 'Excluir',
 *   cancelText: 'Cancelar',
 *   onConfirm: () => {
 *     console.log('Item excluído')
 *   }
 * })
 *
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

import { ref } from 'vue'

/**
 * Estado global do modal de erro
 * @type {import('vue').Ref<Object>}
 * @private
 */
const modalState = ref({
  isVisible: false,
  type: 'error',
  title: '',
  message: '',
  details: [],
  confirmText: 'Confirmar',
  cancelText: 'Cancelar',
  closeText: 'Fechar',
  onConfirm: null,
  onCancel: null,
  onClose: null
})

/**
 * @typedef {Object} ErrorModalOptions
 * @property {string} [title] - Título do modal
 * @property {Array<string>} [details] - Lista de erros detalhados
 * @property {string} [confirmText] - Texto do botão de confirmação
 * @property {string} [cancelText] - Texto do botão de cancelamento
 * @property {string} [closeText] - Texto do botão de fechar
 * @property {Function} [onConfirm] - Callback ao confirmar
 * @property {Function} [onCancel] - Callback ao cancelar
 * @property {Function} [onClose] - Callback ao fechar
 */

/**
 * Composable de gerenciamento de modais de erro
 *
 * @function useErrorModal
 * @returns {Object} Objeto com métodos de controle de modais
 * @returns {import('vue').Ref<Object>} returns.modalState - Estado reativo do modal
 * @returns {Function} returns.showError - Exibe modal de erro
 * @returns {Function} returns.showWarning - Exibe modal de aviso
 * @returns {Function} returns.showInfo - Exibe modal de informação
 * @returns {Function} returns.hideModal - Fecha o modal
 * @returns {Function} returns.parseError - Extrai mensagem de erro formatada
 *
 * @description
 * Fornece controle completo sobre sistema de modais de erro.
 * Estado global garante que apenas um modal seja exibido por vez.
 */
export function useErrorModal() {
  /**
   * Exibe modal de erro
   *
   * @function showError
   * @param {string} message - Mensagem de erro
   * @param {ErrorModalOptions} [options={}] - Opções de configuração
   *
   * @example
   * showError('Falha ao conectar com servidor')
   *
   * @example
   * showError('Erro de validação', {
   *   title: 'Dados Inválidos',
   *   details: ['Email inválido', 'Senha muito curta']
   * })
   */
  const showError = (message, options = {}) => {
    modalState.value = {
      isVisible: true,
      type: 'error',
      title: options.title || 'Erro',
      message,
      details: options.details || [],
      confirmText: options.confirmText || 'Confirmar',
      cancelText: options.cancelText || 'Cancelar',
      closeText: options.closeText || 'Fechar',
      onConfirm: options.onConfirm || null,
      onCancel: options.onCancel || null,
      onClose: options.onClose || null
    }
  }

  /**
   * Exibe modal de aviso
   *
   * @function showWarning
   * @param {string} message - Mensagem de aviso
   * @param {ErrorModalOptions} [options={}] - Opções de configuração
   *
   * @example
   * showWarning('Esta ação não pode ser desfeita', {
   *   confirmText: 'Continuar',
   *   onConfirm: () => deleteItem()
   * })
   */
  const showWarning = (message, options = {}) => {
    modalState.value = {
      isVisible: true,
      type: 'warning',
      title: options.title || 'Atenção',
      message,
      details: options.details || [],
      confirmText: options.confirmText || 'Confirmar',
      cancelText: options.cancelText || 'Cancelar',
      closeText: options.closeText || 'Fechar',
      onConfirm: options.onConfirm || null,
      onCancel: options.onCancel || null,
      onClose: options.onClose || null
    }
  }

  /**
   * Exibe modal informativo
   *
   * @function showInfo
   * @param {string} message - Mensagem informativa
   * @param {ErrorModalOptions} [options={}] - Opções de configuração
   *
   * @example
   * showInfo('Arquivo enviado com sucesso')
   */
  const showInfo = (message, options = {}) => {
    modalState.value = {
      isVisible: true,
      type: 'info',
      title: options.title || 'Informação',
      message,
      details: options.details || [],
      confirmText: options.confirmText || 'Confirmar',
      cancelText: options.cancelText || 'Cancelar',
      closeText: options.closeText || 'Fechar',
      onConfirm: options.onConfirm || null,
      onCancel: options.onCancel || null,
      onClose: options.onClose || null
    }
  }

  /**
   * Fecha o modal de erro
   *
   * @function hideModal
   *
   * @example
   * hideModal()
   */
  const hideModal = () => {
    modalState.value.isVisible = false
  }

  /**
   * Extrai e formata mensagem de erro de diferentes fontes
   *
   * @function parseError
   * @param {Error|string|Object} error - Erro a ser parseado
   * @returns {Object} Objeto com message e details formatados
   * @returns {string} returns.message - Mensagem principal do erro
   * @returns {Array<string>} returns.details - Lista de erros detalhados
   *
   * @example
   * const { message, details } = parseError(error)
   * showError(message, { details })
   *
   * @description
   * Trata diferentes formatos de erro:
   * - Objetos Error nativos do JavaScript
   * - Strings simples
   * - Objetos de erro da API com múltiplas mensagens
   * - Erros de validação do Spring Boot
   */
  const parseError = (error) => {
    let message = 'Ocorreu um erro inesperado'
    let details = []

    if (typeof error === 'string') {
      // Erro é uma string simples
      message = error
    } else if (error instanceof Error) {
      // Erro nativo do JavaScript
      message = error.message

      // Verifica se tem stack trace (apenas em dev)
      if (import.meta.env.DEV && error.stack) {
        const stackLines = error.stack.split('\n').slice(1, 4)
        details = stackLines.map(line => line.trim())
      }
    } else if (error && typeof error === 'object') {
      // Erro de API estruturado
      message = error.message || message

      // Tenta extrair detalhes de diferentes formatos
      if (error.details && Array.isArray(error.details)) {
        details = error.details
      } else if (error.errors && Array.isArray(error.errors)) {
        details = error.errors
      } else if (error.fieldErrors && Array.isArray(error.fieldErrors)) {
        details = error.fieldErrors.map(e => `${e.field}: ${e.message}`)
      }
    }

    // Quebra mensagens multi-linha em detalhes
    if (message.includes('\n')) {
      const lines = message.split('\n')
      message = lines[0]
      details = [...details, ...lines.slice(1).filter(l => l.trim())]
    }

    return { message, details }
  }

  return {
    modalState,
    showError,
    showWarning,
    showInfo,
    hideModal,
    parseError
  }
}
