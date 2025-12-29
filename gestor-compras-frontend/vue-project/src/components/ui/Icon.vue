<template>
  <svg :viewBox="viewBox" :width="size" :height="size" :class="iconClass">
    <path :fill="fill" :d="iconPath" />
  </svg>
</template>

<script setup>
import { computed, defineOptions } from 'vue'
import { getMetricIcon, getTabIcon, getActionIcon } from '@/config/iconConfig'
import { getIconPath as getMenuIcon } from '@/config/menuConfig'

defineOptions({
  name: 'Icon'
})

const props = defineProps({
  /**
   * Nome do ícone
   * @type {String}
   */
  name: {
    type: String,
    required: true
  },

  /**
   * Tipo de ícone (metric, tab, action, menu)
   * @type {String}
   */
  type: {
    type: String,
    default: 'metric',
    validator: (value) => ['metric', 'tab', 'action', 'menu'].includes(value)
  },

  /**
   * Tamanho do ícone em pixels
   * @type {Number|String}
   */
  size: {
    type: [Number, String],
    default: 24
  },

  /**
   * Cor de preenchimento
   * @type {String}
   */
  fill: {
    type: String,
    default: 'currentColor'
  },

  /**
   * ViewBox do SVG
   * @type {String}
   */
  viewBox: {
    type: String,
    default: '0 0 24 24'
  },

  /**
   * Classes CSS adicionais
   * @type {String}
   */
  iconClass: {
    type: String,
    default: ''
  }
})

/**
 * Obtém o path SVG correto baseado no tipo de ícone
 */
const iconPath = computed(() => {
  switch (props.type) {
    case 'metric':
      return getMetricIcon(props.name)
    case 'tab':
      return getTabIcon(props.name)
    case 'action':
      return getActionIcon(props.name)
    case 'menu':
      return getMenuIcon(props.name)
    default:
      return getMetricIcon(props.name)
  }
})
</script>

<style scoped>
svg {
  display: inline-block;
  vertical-align: middle;
}
</style>
