<template>
  <div v-if="isMobileSidebarOpen" class="sidebar-overlay" @click="closeSidebar"></div>

  <aside class="sidebar" :class="{ 'mobile-open': isMobileSidebarOpen, collapsed: isCollapsed }">
    <button class="close-sidebar-btn" @click="closeSidebar" aria-label="Fechar menu">
      <svg viewBox="0 0 24 24" width="24" height="24">
        <path fill="currentColor" d="M19 6.41 17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
      </svg>
    </button>

    <nav class="main-nav">
      <div class="nav-section">
        <div class="nav-header" v-if="showLabels">
          <h3 class="nav-title">Menu Principal</h3>
          <span class="nav-pill">Navegação</span>
        </div>

        <ul class="nav-list">
          <li v-for="item in visibleMenuItems" :key="item.id" class="nav-item">
            <router-link
              :to="item.route"
              class="nav-link"
              :class="{ active: isActive(item.route) }"
              @click="handleNavClick"
              :title="isCollapsed ? item.label : item.description"
              :data-tooltip="item.label"
              :aria-label="item.label"
            >
              <span class="nav-icon-wrap">
                <svg class="nav-icon" viewBox="0 0 24 24" width="20" height="20">
                  <path fill="currentColor" :d="getIconPath(item.icon)"/>
                </svg>
              </span>
              <span v-if="showLabels" class="nav-label">{{ item.label }}</span>
            </router-link>
          </li>
        </ul>
      </div>
    </nav>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useMobileSidebar } from '@/composables/useMobileSidebar'
import { usePermissions } from '@/composables/usePermissions'
import { useSidebar } from '@/composables/useSidebar'
import { menuItems, getIconPath } from '@/config/menuConfig'

const route = useRoute()
const { isMobileSidebarOpen, closeSidebar } = useMobileSidebar()
const { filterMenuByRole } = usePermissions()
const { isCollapsed } = useSidebar()

const visibleMenuItems = computed(() => {
  return filterMenuByRole(menuItems)
})

const showLabels = computed(() => {
  return !isCollapsed.value || isMobileSidebarOpen.value
})

const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}

const handleNavClick = () => {
  if (isMobileSidebarOpen.value) {
    closeSidebar()
  }
}
</script>

<style scoped>
.sidebar-overlay {
  display: none;
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.45);
  backdrop-filter: blur(2px);
  z-index: 999;
}

.close-sidebar-btn {
  display: none;
  position: absolute;
  top: 14px;
  right: 14px;
  background: #fff;
  border: 1px solid #dbe2ea;
  color: #475569;
  cursor: pointer;
  padding: 8px;
  border-radius: 10px;
  transition: all 0.2s;
  z-index: 2;
}

.close-sidebar-btn:hover {
  background: #f8fafc;
  color: #1f285f;
  border-color: #c7d2fe;
}

.sidebar {
  width: 280px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  border-right: 1px solid #e2e8f0;
  height: calc(100vh - 72px);
  overflow-y: auto;
  position: fixed;
  left: 0;
  top: 72px;
  z-index: 100;
  box-shadow: 8px 0 24px rgba(15, 23, 42, 0.05);
  transition: width 0.3s ease-in-out, transform 0.3s ease-in-out;
}

.sidebar.collapsed {
  width: 88px;
}

.main-nav {
  padding: 20px 12px;
}

.nav-section {
  margin-bottom: 20px;
}

.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 0 10px 14px;
}

.nav-title {
  font-size: 0.8rem;
  font-weight: 700;
  color: #1f285f;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.nav-pill {
  font-size: 0.68rem;
  font-weight: 600;
  color: #475569;
  background: #eef2ff;
  border: 1px solid #dbe4ff;
  border-radius: 999px;
  padding: 4px 9px;
}

.nav-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.nav-item {
  margin-bottom: 6px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  color: #64748b;
  text-decoration: none;
  font-size: 0.9rem;
  font-weight: 600;
  transition: all 0.2s ease;
  border-radius: 12px;
  position: relative;
}

.nav-icon-wrap {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9;
  color: currentColor;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.nav-icon {
  color: inherit;
}

.nav-label {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.nav-link:hover {
  background: #eef2ff;
  color: #1f285f;
}

.nav-link:hover .nav-icon-wrap {
  background: #dde5ff;
}

.nav-link.active {
  background: linear-gradient(135deg, #1f285f, #334084);
  color: #ffffff;
  box-shadow: 0 12px 20px rgba(31, 40, 95, 0.24);
}

.nav-link.active .nav-icon-wrap {
  background: rgba(255, 255, 255, 0.2);
}

.sidebar.collapsed .nav-link {
  justify-content: center;
  padding: 10px;
  position: relative;
}

.sidebar.collapsed .nav-link::after {
  content: attr(data-tooltip);
  position: absolute;
  left: 100%;
  top: 50%;
  transform: translateY(-50%);
  margin-left: 12px;
  background: #0f172a;
  color: #fff;
  padding: 7px 10px;
  border-radius: 8px;
  font-size: 0.75rem;
  font-weight: 600;
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.2s ease, transform 0.2s ease;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.2);
  z-index: 12;
}

.sidebar.collapsed .nav-link:hover::after {
  opacity: 1;
  transform: translateY(-50%) translateX(2px);
}

.sidebar.collapsed .nav-icon-wrap {
  width: 36px;
  height: 36px;
}

.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-track {
  background: #edf2f7;
}

.sidebar::-webkit-scrollbar-thumb {
  background: #c7ced8;
  border-radius: 999px;
}

.sidebar::-webkit-scrollbar-thumb:hover {
  background: #aeb7c3;
}

@media (max-width: 1024px) {
  .sidebar-overlay {
    display: block;
  }

  .close-sidebar-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
  }

  .sidebar {
    transform: translateX(-100%);
    z-index: 1000;
    top: 0;
    height: 100vh;
    width: 300px;
    box-shadow: 10px 0 30px rgba(15, 23, 42, 0.18);
  }

  .sidebar.mobile-open {
    transform: translateX(0);
  }

  .main-nav {
    padding-top: 62px;
  }

  .sidebar.collapsed {
    width: 300px;
  }

  .sidebar.collapsed .nav-link {
    justify-content: flex-start;
    padding: 10px 12px;
  }
}

@media (max-width: 480px) {
  .sidebar {
    width: 100%;
    max-width: 340px;
  }

  .nav-link {
    padding: 12px;
    font-size: 0.92rem;
  }

  .nav-icon-wrap {
    width: 36px;
    height: 36px;
  }

  .close-sidebar-btn {
    top: 12px;
    right: 12px;
  }
}
</style>


