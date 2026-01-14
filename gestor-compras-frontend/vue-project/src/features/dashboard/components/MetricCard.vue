<template>
  <div class="metric-card" :class="cardClass">
    <div class="card-header">
      <div class="card-icon" :style="{ backgroundColor: iconColor }">
        <Icon :name="iconName" type="metric" :size="24" fill="white" />
      </div>
      <div class="card-actions">
        <button class="action-btn" @click="$emit('action')">
          <svg viewBox="0 0 24 24" width="16" height="16">
            <path fill="currentColor" d="M12 16c1.671 0 3-1.331 3-3s-1.329-3-3-3-3 1.331-3 3 1.329 3 3 3z"/>
            <path fill="currentColor" d="M12 6c1.671 0 3-1.331 3-3s-1.329-3-3-3-3 1.331-3 3 1.329 3 3 3z"/>
            <path fill="currentColor" d="M12 26c1.671 0 3-1.331 3-3s-1.329-3-3-3-3 1.331-3 3 1.329 3 3 3z"/>
          </svg>
        </button>
      </div>
    </div>

    <div class="card-content">
      <h3 class="card-title">{{ title }}</h3>
      <p class="card-description">{{ description }}</p>

      <div class="metrics">
        <div class="metric-item" v-for="metric in metrics" :key="metric.label">
          <span class="metric-value" :style="{ color: metric.color || '#1F285F' }">
            {{ metric.value }}
          </span>
          <span class="metric-label">{{ metric.label }}</span>
        </div>
      </div>
    </div>

    <div class="card-footer" v-if="$slots.footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import Icon from '@/components/ui/Icon.vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  metrics: {
    type: Array,
    required: true
  },
  iconColor: {
    type: String,
    default: '#1F285F'
  },
  variant: {
    type: String,
    default: 'default',
    validator: (value) => ['default', 'primary', 'success', 'warning', 'danger'].includes(value)
  }
})

defineEmits(['action'])

const cardClass = computed(() => {
  return `variant-${props.variant}`
})

// Mapeia títulos de cards para nomes de ícones
const iconName = computed(() => {
  switch (props.title.toLowerCase()) {
    case 'pedidos de compra':
      return 'cart'
    case 'cotações':
      return 'clipboard'
    case 'fornecedores':
      return 'users'
    default:
      return 'total'
  }
})
</script>

<style scoped>
.metric-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e0e6ed;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #6366f1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.card-actions {
  opacity: 0;
  transition: opacity 0.2s ease;
}

.metric-card:hover .card-actions {
  opacity: 1;
}

.action-btn {
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.action-btn:hover {
  background: #f3f4f6;
  color: #1F285F;
}

.card-content {
  margin-bottom: 20px;
}

.card-title {
  font-family: inherit;
  font-size: 18px;
  font-weight: 600;
  color: #1F285F;
  margin: 0 0 8px 0;
}

.card-description {
  font-family: inherit;
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 20px 0;
  line-height: 1.5;
}

.metrics {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.metric-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.metric-value {
  font-family: inherit;
  font-size: 24px;
  font-weight: 700;
  line-height: 1;
}

.metric-label {
  font-family: inherit;
  font-size: 12px;
  color: #6b7280;
  text-transform: uppercase;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.card-footer {
  padding-top: 16px;
  border-top: 1px solid #e0e6ed;
}

/* Variantes */
.variant-primary {
  border-left: 4px solid #6366f1;
}

.variant-success {
  border-left: 4px solid #10b981;
}

.variant-warning {
  border-left: 4px solid #f59e0b;
}

.variant-danger {
  border-left: 4px solid #ef4444;
}

.variant-default {
  border-left: 4px solid #6366f1;
}

/* Responsividade */
@media (max-width: 768px) {
  .metric-card {
    padding: 16px;
  }

  .card-title {
    font-size: 16px;
  }

  .metric-value {
    font-size: 20px;
  }

  .metrics {
    gap: 12px;
  }
}
</style>
