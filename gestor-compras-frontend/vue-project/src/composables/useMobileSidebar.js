import { ref } from 'vue'

// Estado global compartilhado para o sidebar mobile
const isMobileSidebarOpen = ref(false)

export function useMobileSidebar() {
  const toggleSidebar = () => {
    isMobileSidebarOpen.value = !isMobileSidebarOpen.value
  }

  const closeSidebar = () => {
    isMobileSidebarOpen.value = false
  }

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
