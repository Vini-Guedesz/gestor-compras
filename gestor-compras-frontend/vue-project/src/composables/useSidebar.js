import { ref } from 'vue'

// Estado global do sidebar (compartilhado entre componentes)
const isCollapsed = ref(false)

export function useSidebar() {
  /**
   * Alterna o estado de colapso do sidebar
   */
  const toggleCollapse = () => {
    isCollapsed.value = !isCollapsed.value
  }

  /**
   * Define o estado de colapso
   * @param {boolean} value - true para colapsado, false para expandido
   */
  const setCollapsed = (value) => {
    isCollapsed.value = value
  }

  return {
    isCollapsed,
    toggleCollapse,
    setCollapsed
  }
}
