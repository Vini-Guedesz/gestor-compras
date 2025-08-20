<template>
  <div class="quick-actions">
    <h3 class="actions-title">Ações Rápidas</h3>
    <div class="actions-grid">
      <button 
        v-for="action in actions" 
        :key="action.id"
        class="action-button"
        :class="action.variant"
        @click="handleAction(action)"
      >
        <div class="action-icon" :style="{ backgroundColor: action.color }">
          <svg v-if="action.icon" viewBox="0 0 24 24" width="20" height="20">
            <path fill="white" :d="getIconPath(action.icon)" />
          </svg>
        </div>
        <div class="action-content">
          <span class="action-title">{{ action.title }}</span>
          <span class="action-description">{{ action.description }}</span>
        </div>
        <svg class="action-arrow" viewBox="0 0 24 24" width="16" height="16">
          <path fill="currentColor" d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"/>
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const actions = ref([
  {
    id: 'novo-pedido',
    title: 'Novo Pedido',
    description: 'Criar novo pedido de compra',
    icon: 'plus',
    color: '#1F285F',
    variant: 'primary',
    route: '/pedidos/novo'
  },
  {
    id: 'nova-cotacao',
    title: 'Nova Cotação',
    description: 'Solicitar cotação de fornecedores',
    icon: 'document',
    color: '#10b981',
    variant: 'success',
    route: '/cotacoes/nova'
  },
  {
    id: 'cadastrar-fornecedor',
    title: 'Cadastrar Fornecedor',
    description: 'Adicionar novo fornecedor',
    icon: 'user-plus',
    color: '#f59e0b',
    variant: 'warning',
    route: '/fornecedores/novo'
  }
])

const emit = defineEmits(['action-click'])

const handleAction = (action) => {
  emit('action-click', action)
  console.log('Ação clicada:', action)
}

const getIconPath = (iconName) => {
  const icons = {
    'plus': 'M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z',
    'document': 'M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z',
    'user-plus': 'M15,14C12.33,14 7,15.33 7,18V20H23V18C23,15.33 17.67,14 15,14M6,10V7H4V10H1V12H4V15H6V12H9V10M15,12A4,4 0 0,0 19,8A4,4 0 0,0 15,4A4,4 0 0,0 11,8A4,4 0 0,0 15,12Z'
  }
  return icons[iconName] || icons['plus']
}
</script>

<style scoped>
.quick-actions {
  margin-bottom: 32px;
}

.actions-title {
  font-family: Arial, sans-serif;
  font-size: 20px;
  font-weight: 600;
  color: #1F285F;
  margin: 0 0 20px 0;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: white;
  border: 1px solid #e0e6ed;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.action-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  border-color: #1F285F;
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.action-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.action-title {
  font-family: Arial, sans-serif;
  font-size: 16px;
  font-weight: 600;
  color: #1F285F;
  line-height: 1.2;
}

.action-description {
  font-family: Arial, sans-serif;
  font-size: 14px;
  color: #6b7280;
  line-height: 1.3;
}

.action-arrow {
  color: #9ca3af;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.action-button:hover .action-arrow {
  color: #1F285F;
  transform: translateX(4px);
}

/* Variantes */
.action-button.primary:hover {
  background: linear-gradient(135deg, #1F285F 0%, rgba(31, 40, 95, 0.05) 100%);
}

.action-button.success:hover {
  background: linear-gradient(135deg, #10b981 0%, rgba(16, 185, 129, 0.05) 100%);
}

.action-button.warning:hover {
  background: linear-gradient(135deg, #f59e0b 0%, rgba(245, 158, 11, 0.05) 100%);
}

/* Responsividade */
@media (max-width: 768px) {
  .actions-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .action-button {
    padding: 16px;
    gap: 12px;
  }
  
  .action-icon {
    width: 40px;
    height: 40px;
  }
  
  .action-title {
    font-size: 14px;
  }
  
  .action-description {
    font-size: 13px;
  }
}
</style>
