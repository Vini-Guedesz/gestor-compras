import { watch, onUnmounted } from 'vue'

/**
 * Composable para gerenciar comportamento de modais
 *
 * Bloqueia o scroll do body quando o modal está aberto
 * e restaura quando é fechado
 */
export function useModal(isOpen) {
  // Guarda a posição do scroll original
  let scrollPosition = 0

  const lockScroll = () => {
    // Salva a posição atual do scroll
    scrollPosition = window.pageYOffset || document.documentElement.scrollTop

    // Adiciona estilos para bloquear scroll
    document.body.style.overflow = 'hidden'
    document.body.style.position = 'fixed'
    document.body.style.top = `-${scrollPosition}px`
    document.body.style.width = '100%'
  }

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
