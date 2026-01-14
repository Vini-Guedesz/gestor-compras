<template>
  <div
    v-if="show || isVisible"
    class="modal-overlay"
    @click.self="handleCancel"
    @keydown.esc="handleCancel"
  >
    <div
      ref="modalRef"
      class="modal-container"
      :class="type"
      role="dialog"
      aria-modal="true"
      :aria-labelledby="titleId"
      :aria-describedby="messageId"
      tabindex="-1"
    >
      <div class="modal-header">
        <h3 :id="titleId" class="modal-title">{{ title }}</h3>
      </div>

      <div class="modal-body">
        <div class="modal-icon" aria-hidden="true">
          <svg v-if="type === 'danger'" viewBox="0 0 24 24" width="48" height="48">
            <path fill="#f59e0b" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 15h2v2h-2v-2zm0-8h2v6h-2V9z"/>
          </svg>
          <svg v-else viewBox="0 0 24 24" width="48" height="48">
            <path fill="#3b82f6" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
          </svg>
        </div>

        <div class="modal-content">
          <p :id="messageId" class="modal-message">{{ message }}</p>
          <p v-if="details" class="modal-details">{{ details }}</p>
        </div>
      </div>

      <div class="modal-footer">
        <button
          ref="cancelButtonRef"
          class="modal-button cancel-button"
          @click="handleCancel"
          aria-label="Cancelar ação"
        >
          {{ cancelText || 'Cancelar' }}
        </button>
        <button
          ref="confirmButtonRef"
          class="modal-button confirm-button"
          :class="type"
          @click="handleConfirm"
          :aria-label="`Confirmar: ${message}`"
        >
          {{ confirmText || 'Confirmar' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * Componente Modal de Confirmação
 *
 * Usado para confirmação de ações importantes como exclusão,
 * cancelamento, etc.
 *
 * Acessibilidade:
 * - role="dialog" e aria-modal="true" para identificar como modal
 * - Focus trap para manter foco dentro do modal
 * - ESC para fechar
 * - Foco automático ao abrir
 * - Retorno do foco ao elemento que abriu quando fecha
 */
import { computed, ref, watch, nextTick, onMounted } from 'vue'
import { useModal } from '@/composables/useModal'

const props = defineProps({
  // Controla a visibilidade do modal (duas formas de controlar)
  show: {
    type: Boolean,
    default: false
  },
  isVisible: {
    type: Boolean,
    default: false
  },
  // Título do modal
  title: {
    type: String,
    default: 'Confirmação'
  },
  // Mensagem principal
  message: {
    type: String,
    default: 'Tem certeza que deseja realizar esta ação?'
  },
  // Detalhes adicionais (opcional)
  details: {
    type: String,
    default: ''
  },
  // Texto do botão de confirmar
  confirmText: {
    type: String,
    default: 'Confirmar'
  },
  // Texto do botão de cancelar
  cancelText: {
    type: String,
    default: 'Cancelar'
  },
  // Tipo do modal (altera cor/estilo): 'default', 'danger', 'warning'
  type: {
    type: String,
    default: 'default'
  }
})

const emit = defineEmits(['confirm', 'cancel'])

// Refs para elementos do modal
const modalRef = ref(null)
const confirmButtonRef = ref(null)
const cancelButtonRef = ref(null)

// IDs únicos para acessibilidade
let idCounter = 0
const titleId = `modal-title-${++idCounter}`
const messageId = `modal-message-${idCounter}`

// Elemento que tinha foco antes do modal abrir
let previousActiveElement = null

// Calcula se o modal está aberto
const isOpen = computed(() => props.show || props.isVisible)

// Usa o composable para gerenciar o scroll
useModal(isOpen)

// Gerencia foco quando modal abre/fecha
watch(isOpen, async (newValue) => {
  if (newValue) {
    // Guarda elemento que tinha foco
    previousActiveElement = document.activeElement

    // Aguarda próximo tick para garantir que modal foi renderizado
    await nextTick()

    // Foca no modal
    if (modalRef.value) {
      modalRef.value.focus()
    }

    // Adiciona listener para focus trap
    document.addEventListener('keydown', handleFocusTrap)
  } else {
    // Remove listener
    document.removeEventListener('keydown', handleFocusTrap)

    // Retorna foco ao elemento anterior
    if (previousActiveElement && previousActiveElement.focus) {
      previousActiveElement.focus()
    }
  }
})

// Focus trap: mantém foco dentro do modal
const handleFocusTrap = (e) => {
  if (e.key !== 'Tab' || !modalRef.value) return

  const focusableElements = modalRef.value.querySelectorAll(
    'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
  )

  const firstElement = focusableElements[0]
  const lastElement = focusableElements[focusableElements.length - 1]

  if (e.shiftKey) {
    // Shift + Tab: se estiver no primeiro, vai para o último
    if (document.activeElement === firstElement) {
      e.preventDefault()
      lastElement.focus()
    }
  } else {
    // Tab: se estiver no último, vai para o primeiro
    if (document.activeElement === lastElement) {
      e.preventDefault()
      firstElement.focus()
    }
  }
}

// Manipula a ação de confirmar
const handleConfirm = () => {
  emit('confirm')
}

// Manipula a ação de cancelar
const handleCancel = () => {
  emit('cancel')
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1500;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.modal-container {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  width: 90%;
  max-width: 500px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  margin: 20px;
  max-height: calc(100vh - 40px);
  overflow-y: auto;
}

/* Cabeçalho do modal */
.modal-header {
  padding: 24px 24px 0 24px;
  text-align: center;
}

.modal-title {
  font-family: inherit;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

/* Corpo do modal */
.modal-body {
  padding: 24px;
  text-align: center;
}

.modal-icon {
  margin: 0 auto 16px;
  width: 48px;
  height: 48px;
}

.modal-content {
  margin-top: 16px;
}

.modal-message {
  font-family: inherit;
  font-size: 16px;
  font-weight: 500;
  color: #374151;
  margin: 0 0 8px 0;
  line-height: 1.5;
}

.modal-details {
  font-family: inherit;
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
}

/* Rodapé do modal */
.modal-footer {
  padding: 16px 24px 24px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.modal-button {
  padding: 10px 16px;
  border-radius: 8px;
  font-family: inherit;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-button {
  background-color: #f9fafb;
  color: #4b5563;
  border: 1px solid #d1d5db;
}

.cancel-button:hover {
  background-color: #f3f4f6;
}

.confirm-button {
  background-color: #3b82f6;
  color: white;
  border: none;
}

.confirm-button:hover {
  background-color: #2563eb;
}

.confirm-button.danger {
  background-color: #ef4444;
}

.confirm-button.danger:hover {
  background-color: #dc2626;
}

.confirm-button.warning {
  background-color: #f59e0b;
}

.confirm-button.warning:hover {
  background-color: #d97706;
}

/* Responsividade */
@media (max-width: 640px) {
  .modal-container {
    width: 95%;
  }

  .modal-footer {
    flex-direction: column-reverse;
  }

  .modal-button {
    width: 100%;
  }
}
</style>
