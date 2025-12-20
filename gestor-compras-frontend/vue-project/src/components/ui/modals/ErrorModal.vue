<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="isVisible" class="modal-overlay" @click.self="handleClose">
        <div class="error-modal" role="dialog" aria-labelledby="error-title" aria-modal="true">
          <div class="modal-header" :class="`header-${type}`">
            <div class="icon-container">
              <!-- Ícone de erro -->
              <svg v-if="type === 'error'" viewBox="0 0 24 24" width="48" height="48" class="modal-icon">
                <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
              </svg>
              <!-- Ícone de aviso -->
              <svg v-else-if="type === 'warning'" viewBox="0 0 24 24" width="48" height="48" class="modal-icon">
                <path fill="currentColor" d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/>
              </svg>
              <!-- Ícone de info -->
              <svg v-else viewBox="0 0 24 24" width="48" height="48" class="modal-icon">
                <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"/>
              </svg>
            </div>
            <h2 id="error-title" class="modal-title">{{ title }}</h2>
          </div>

          <div class="modal-body">
            <p class="error-message">{{ message }}</p>
            <div v-if="details && details.length > 0" class="error-details">
              <details>
                <summary class="details-toggle">Ver detalhes</summary>
                <ul class="details-list">
                  <li v-for="(detail, index) in details" :key="index">{{ detail }}</li>
                </ul>
              </details>
            </div>
          </div>

          <div class="modal-footer">
            <!-- Modo confirmação: mostra botões de confirmar e cancelar -->
            <template v-if="onConfirm">
              <button
                @click="handleCancel"
                class="btn btn-secondary"
              >
                {{ cancelText }}
              </button>
              <button
                @click="handleConfirm"
                class="btn btn-primary"
                autofocus
              >
                {{ confirmText }}
              </button>
            </template>
            <!-- Modo simples: apenas botão de fechar -->
            <template v-else>
              <button
                @click="handleClose"
                class="btn btn-primary"
                autofocus
              >
                {{ closeText }}
              </button>
            </template>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'

const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  type: {
    type: String,
    default: 'error',
    validator: (value) => ['error', 'warning', 'info'].includes(value)
  },
  title: {
    type: String,
    default: 'Erro'
  },
  message: {
    type: String,
    required: true
  },
  details: {
    type: Array,
    default: () => []
  },
  confirmText: {
    type: String,
    default: 'Confirmar'
  },
  cancelText: {
    type: String,
    default: 'Cancelar'
  },
  closeText: {
    type: String,
    default: 'Fechar'
  },
  onConfirm: {
    type: Function,
    default: null
  },
  onCancel: {
    type: Function,
    default: null
  },
  onClose: {
    type: Function,
    default: null
  }
})

const emit = defineEmits(['close'])

const handleClose = () => {
  if (typeof props.onClose === 'function') {
    props.onClose()
  }
  emit('close')
}

const handleConfirm = () => {
  if (typeof props.onConfirm === 'function') {
    props.onConfirm()
  }
  handleClose()
}

const handleCancel = () => {
  if (typeof props.onCancel === 'function') {
    props.onCancel()
  }
  handleClose()
}

const handleEscape = (event) => {
  if (event.key === 'Escape' && props.isVisible) {
    handleClose()
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleEscape)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleEscape)
})
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(2px);
}

.error-modal {
  background: white;
  border-radius: 16px;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    transform: translateY(-50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-header {
  padding: 32px 24px 24px;
  text-align: center;
  border-bottom: 1px solid #e5e7eb;
}

.header-error {
  background: linear-gradient(135deg, #fee2e2 0%, #fef2f2 100%);
}

.header-warning {
  background: linear-gradient(135deg, #fef3c7 0%, #fffbeb 100%);
}

.header-info {
  background: linear-gradient(135deg, #dbeafe 0%, #eff6ff 100%);
}

.icon-container {
  margin-bottom: 16px;
  display: flex;
  justify-content: center;
}

.modal-icon {
  animation: iconPulse 0.6s ease-out;
}

.header-error .modal-icon {
  color: #ef4444;
}

.header-warning .modal-icon {
  color: #f59e0b;
}

.header-info .modal-icon {
  color: #3b82f6;
}

@keyframes iconPulse {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.modal-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
}

.modal-body {
  padding: 24px;
  max-height: 50vh;
  overflow-y: auto;
}

.error-message {
  margin: 0;
  font-size: 16px;
  line-height: 1.6;
  color: #4b5563;
  text-align: center;
}

.error-details {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.details-toggle {
  cursor: pointer;
  color: #6b7280;
  font-size: 14px;
  font-weight: 500;
  user-select: none;
  transition: color 0.2s;
}

.details-toggle:hover {
  color: #374151;
}

.details-list {
  margin: 12px 0 0 20px;
  padding: 0;
  list-style-type: disc;
}

.details-list li {
  margin: 8px 0;
  color: #6b7280;
  font-size: 14px;
  line-height: 1.5;
}

.modal-footer {
  padding: 20px 24px;
  background: #f9fafb;
  border-top: 1px solid #e5e7eb;
  display: flex;
  gap: 12px;
  justify-content: center;
}

.btn {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 100px;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover {
  background: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.btn-primary:active {
  transform: translateY(0);
}

.btn-secondary {
  background: white;
  color: #6b7280;
  border: 1px solid #d1d5db;
}

.btn-secondary:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

/* Transitions */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .error-modal,
.modal-leave-active .error-modal {
  transition: transform 0.3s ease;
}

.modal-enter-from .error-modal,
.modal-leave-to .error-modal {
  transform: scale(0.9) translateY(-20px);
}

/* Scrollbar customizado */
.modal-body::-webkit-scrollbar {
  width: 6px;
}

.modal-body::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.modal-body::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.modal-body::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* Responsivo */
@media (max-width: 640px) {
  .error-modal {
    width: 95%;
    max-height: 90vh;
  }

  .modal-header {
    padding: 24px 16px 16px;
  }

  .modal-title {
    font-size: 20px;
  }

  .modal-body {
    padding: 16px;
  }

  .error-message {
    font-size: 14px;
  }

  .modal-footer {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}
</style>
