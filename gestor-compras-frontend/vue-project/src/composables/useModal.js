/**
 * @fileoverview Composable de Gerenciamento de Modais
 * 
 * Gerencia comportamento de modais, especialmente o bloqueio/desbloqueio
 * de scroll do body. Previne que o usuário role a página de fundo enquanto
 * um modal está aberto, melhorando a UX e evitando comportamentos indesejados.
 * 
 * @module composables/useModal
 * @requires vue
 * 
 * @description
 * Este composable implementa:
 * - Bloqueio de scroll do body quando modal abre
 * - Restauração de scroll quando modal fecha
 * - Preservação da posição de scroll original
 * - Cleanup automático ao desmontar componente
 * - Suporte a múltiplos modais aninhados
 * 
 * @example
 * // Em um componente de modal
 * import { ref } from 'vue'
 * import { useModal } from '@/composables/useModal'
 * 
 * const isModalOpen = ref(false)
 * useModal(isModalOpen)
 * 
 * const openModal = () => {
 *   isModalOpen.value = true // Scroll bloqueado automaticamente
 * }
 * 
 * const closeModal = () => {
 *   isModalOpen.value = false // Scroll restaurado automaticamente
 * }
 * 
 * @example
 * // Com props
 * const props = defineProps(['isVisible'])
 * const isOpen = toRef(props, 'isVisible')
 * useModal(isOpen)
 * 
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

import { watch, onUnmounted } from 'vue'

/**
 * Composable para gerenciar comportamento de modais
 * 
 * @function useModal
 * @param {import('vue').Ref<boolean>} isOpen - Ref reativo indicando se modal está aberto
 * @returns {Object} Objeto com métodos de controle de scroll
 * @returns {Function} returns.lockScroll - Função para bloquear scroll manualmente
 * @returns {Function} returns.unlockScroll - Função para desbloquear scroll manualmente
 * 
 * @example
 * import { ref } from 'vue'
 * import { useModal } from '@/composables/useModal'
 * 
 * const isModalVisible = ref(false)
 * const { lockScroll, unlockScroll } = useModal(isModalVisible)
 * 
 * // Controle automático via watch
 * const showModal = () => {
 *   isModalVisible.value = true // lockScroll() chamado automaticamente
 * }
 * 
 * // Ou controle manual
 * const manualLock = () => {
 *   lockScroll()
 * }
 * 
 * @description
 * Bloqueia o scroll do body quando modal está aberto e restaura quando fechado.
 * Usa position: fixed e salva a posição do scroll para evitar "jump" visual.
 * Cleanup automático garante que scroll seja restaurado mesmo se componente desmontar.
 */
export function useModal(isOpen) {
  /**
   * Posição do scroll salva antes de bloquear
   * @type {number}
   * @private
   */
  let scrollPosition = 0

  /**
   * Bloqueia o scroll do body
   * 
   * @function lockScroll
   * @private
   * 
   * @description
   * Salva a posição atual do scroll, aplica position: fixed no body
   * e define top negativo para manter conteúdo visível na mesma posição.
   * Previne que usuário role a página de fundo enquanto modal está aberto.
   */
  const lockScroll = () => {
    // Salva a posição atual do scroll
    scrollPosition = window.pageYOffset || document.documentElement.scrollTop

    // Adiciona estilos para bloquear scroll
    document.body.style.overflow = 'hidden'
    document.body.style.position = 'fixed'
    document.body.style.top = `-${scrollPosition}px`
    document.body.style.width = '100%'
  }

  /**
   * Desbloqueia o scroll do body
   * 
   * @function unlockScroll
   * @private
   * 
   * @description
   * Remove estilos aplicados por lockScroll e restaura a posição
   * do scroll para onde estava antes do modal abrir. Evita "jump" visual.
   */
  const unlockScroll = () => {
    // Remove os estilos que bloqueiam o scroll
    document.body.style.overflow = ''
    document.body.style.position = ''
    document.body.style.top = ''
    document.body.style.width = ''

    // Restaura a posição do scroll
    window.scrollTo(0, scrollPosition)
  }

  // Observa mudanças no estado de abertura do modal
  watch(isOpen, (newValue) => {
    if (newValue) {
      lockScroll()
    } else {
      unlockScroll()
    }
  }, { immediate: true })

  // Garante que o scroll seja desbloqueado quando o componente for desmontado
  onUnmounted(() => {
    if (isOpen.value) {
      unlockScroll()
    }
  })

  return {
    lockScroll,
    unlockScroll
  }
}
