/**
 * @fileoverview Composable de Controle do Sidebar Mobile
 * 
 * Gerencia o estado de abertura/fechamento do sidebar em dispositivos móveis.
 * Utiliza estado global compartilhado entre todos os componentes que o importam,
 * permitindo controle sincronizado do menu lateral.
 * 
 * @module composables/useMobileSidebar
 * @requires vue
 * 
 * @description
 * Este composable implementa:
 * - Estado global compartilhado (singleton pattern)
 * - Toggle do sidebar (abrir/fechar)
 * - Abertura explícita do sidebar
 * - Fechamento explícito do sidebar
 * - Sincronização automática entre componentes
 * 
 * @example
 * // No DashboardHeader (botão menu)
 * import { useMobileSidebar } from '@/composables/useMobileSidebar'
 * 
 * const { toggleSidebar } = useMobileSidebar()
 * 
 * <button @click="toggleSidebar">Menu</button>
 * 
 * @example
 * // No DashboardSidebar
 * import { useMobileSidebar } from '@/composables/useMobileSidebar'
 * 
 * const { isMobileSidebarOpen, closeSidebar } = useMobileSidebar()
 * 
 * <aside :class="{ 'mobile-open': isMobileSidebarOpen }">
 *   <button @click="closeSidebar">Fechar</button>
 * </aside>
 * 
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

import { ref } from 'vue'

/**
 * Estado global compartilhado para o sidebar mobile
 * @type {import('vue').Ref<boolean>}
 * @private
 * @description
 * Ref reativo compartilhado entre todas as instâncias do composable.
 * Mantido fora da função para garantir singleton pattern.
 */
const isMobileSidebarOpen = ref(false)

/**
 * Composable para controlar o sidebar mobile
 * 
 * @function useMobileSidebar
 * @returns {Object} Objeto com estado e métodos de controle
 * @returns {import('vue').Ref<boolean>} returns.isMobileSidebarOpen - Estado reativo do sidebar
 * @returns {Function} returns.toggleSidebar - Alterna estado (abre/fecha)
 * @returns {Function} returns.closeSidebar - Fecha o sidebar
 * @returns {Function} returns.openSidebar - Abre o sidebar
 * 
 * @example
 * // Uso completo
 * import { useMobileSidebar } from '@/composables/useMobileSidebar'
 * 
 * const { 
 *   isMobileSidebarOpen, 
 *   toggleSidebar, 
 *   closeSidebar, 
 *   openSidebar 
 * } = useMobileSidebar()
 * 
 * // No template
 * <button @click="toggleSidebar">Toggle Menu</button>
 * <div v-if="isMobileSidebarOpen" @click="closeSidebar">Overlay</div>
 * 
 * @description
 * Retorna o mesmo estado global independente de onde for chamado,
 * garantindo sincronização entre DashboardHeader, DashboardSidebar
 * e qualquer outro componente que precise controlar o menu mobile.
 */
export function useMobileSidebar() {
  /**
   * Alterna o estado do sidebar (abre se fechado, fecha se aberto)
   * @function toggleSidebar
   */
  const toggleSidebar = () => {
    isMobileSidebarOpen.value = !isMobileSidebarOpen.value
  }

  /**
   * Fecha o sidebar (define como false)
   * @function closeSidebar
   */
  const closeSidebar = () => {
    isMobileSidebarOpen.value = false
  }

  /**
   * Abre o sidebar (define como true)
   * @function openSidebar
   */
  const openSidebar = () => {
    isMobileSidebarOpen.value = true
  }

  return {
    isMobileSidebarOpen,
    toggleSidebar,
    closeSidebar,
    openSidebar
  }
}
