<!--
  Componente LogoutModal - Modal de confirmaÃ§Ã£o de logout

  Modal elegante para confirmar o logout do usuÃ¡rio, substituindo
  o alert() nativo por uma interface mais amigÃ¡vel.

  Props:
  - show (Boolean): Controla a visibilidade do modal

  Events:
  - confirm: Emitido quando o usuÃ¡rio confirma o logout
  - cancel: Emitido quando o usuÃ¡rio cancela o logout
-->
<template>
  <Teleport to="body">
    <!-- Overlay do modal -->
    <div
      v-if="show"
      class="modal-overlay"
      @click.self="handleCancel"
    >
    <div
      ref="modalRef"
      class="modal-container"
      @click.stop
      role="dialog"
      aria-modal="true"
      :aria-labelledby="titleId"
      :aria-describedby="messageId"
      tabindex="-1"
    >
      <div class="modal-header">
        <h3 :id="titleId" class="modal-title">Confirmar Logout</h3>
      </div>

      <div class="modal-body">
        <div class="modal-icon" aria-hidden="true">
          <svg viewBox="0 0 24 24" width="48" height="48">
            <path fill="#f59e0b" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 15h2v2h-2v-2zm0-8h2v6h-2V9z"/>
          </svg>
        </div>

        <div class="modal-content">
          <p :id="messageId" class="modal-message">
            Tem certeza que deseja sair da aplicação?
          </p>
          <p class="modal-submessage">
            Você precisará fazer login novamente para acessar o sistema.
          </p>
        </div>
      </div>

      <div class="modal-footer">
        <button
          ref="cancelButtonRef"
          class="modal-button cancel-button"
          @click="handleCancel"
          aria-label="Cancelar e permanecer logado"
        >
          Cancelar
        </button>
        <button
          ref="confirmButtonRef"
          class="modal-button confirm-button"
          @click="handleConfirm"
          aria-label="Confirmar logout e sair do sistema"
        >
          Sim, Sair
        </button>
      </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
/**
 * Modal de confirmaÃ§Ã£o de logout
 *
 * Proporciona uma experiÃªncia mais profissional que o alert() nativo
 *
 * Acessibilidade:
 * - role="dialog" e aria-modal="true" para identificar como modal
 * - Focus trap para manter foco dentro do modal
 * - ESC para fechar
 * - Foco automÃ¡tico ao abrir
 * - Retorno do foco ao elemento que abriu quando fecha
 */
import { computed, ref, watch, nextTick, onUnmounted } from 'vue'
import { useModal } from '@/composables/useModal'

// Props do componente
const props = defineProps({
  show: {
    type: Boolean,
    default: false,
    required: true
  }
})

// Events emitidos pelo componente
const emit = defineEmits(['confirm', 'cancel'])

// Refs para elementos do modal
const modalRef = ref(null)
const confirmButtonRef = ref(null)
const cancelButtonRef = ref(null)

// IDs Ãºnicos para acessibilidade
let idCounter = 0
const titleId = `logout-modal-title-${++idCounter}`
const messageId = `logout-modal-message-${idCounter}`

// Elemento que tinha foco antes do modal abrir
let previousActiveElement = null

// Calcula se o modal estÃ¡ aberto
const isOpen = computed(() => props.show)

// Usa o composable para gerenciar o scroll
useModal(isOpen)

// Gerencia foco quando modal abre/fecha
watch(isOpen, async (newValue) => {
  if (newValue) {
    // Guarda elemento que tinha foco
    previousActiveElement = document.activeElement

    // Aguarda prÃ³ximo tick para garantir que modal foi renderizado
    await nextTick()

    // Foca no modal
    if (modalRef.value) {
      modalRef.value.focus()
    }

    // Adiciona listener para focus trap
    document.addEventListener('keydown', handleFocusTrap)
    document.addEventListener('keydown', handleEscape)
  } else {
    // Remove listener
    document.removeEventListener('keydown', handleFocusTrap)
    document.removeEventListener('keydown', handleEscape)

    // Retorna foco ao elemento anterior
    if (previousActiveElement && previousActiveElement.focus) {
      previousActiveElement.focus()
    }
  }
})

// Focus trap: mantÃ©m foco dentro do modal
const handleFocusTrap = (e) => {
  if (e.key !== 'Tab' || !modalRef.value) return

  const focusableElements = modalRef.value.querySelectorAll(
    'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
  )

  const firstElement = focusableElements[0]
  const lastElement = focusableElements[focusableElements.length - 1]

  if (e.shiftKey) {
    // Shift + Tab: se estiver no primeiro, vai para o Ãºltimo
    if (document.activeElement === firstElement) {
      e.preventDefault()
      lastElement.focus()
    }
  } else {
    // Tab: se estiver no Ãºltimo, vai para o primeiro
    if (document.activeElement === lastElement) {
      e.preventDefault()
      firstElement.focus()
    }
  }
}

const handleEscape = (e) => {
  if (e.key === 'Escape' && isOpen.value) {
    handleCancel()
  }
}

/**
 * Manipula a confirmaÃ§Ã£o do logout
 */
const handleConfirm = () => {
  emit('confirm')
}

/**
 * Manipula o cancelamento do logout
 */
const handleCancel = () => {
  emit('cancel')
}

onUnmounted(() => {
  document.removeEventListener('keydown', handleFocusTrap)
  document.removeEventListener('keydown', handleEscape)
})
</script>

<style scoped>
/*
  Estilos do Modal de Logout

  Design moderno e acessÃ­vel com animaÃ§Ãµes suaves
*/

/* Overlay que cobre toda a tela */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  animation: fadeIn 0.15s ease-out;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* Container do modal */
.modal-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
  max-width: 400px;
  width: 90%;
  max-height: calc(100vh - 40px);
  overflow-y: auto;
  animation: slideIn 0.2s ease-out;
  margin: 20px;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* CabeÃ§alho do modal */
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

.modal-submessage {
  font-family: inherit;
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  line-height: 1.4;
}

/* RodapÃ© do modal */
.modal-footer {
  padding: 0 24px 24px 24px;
  display: flex;
  gap: 12px;
  justify-content: center;
}

/* BotÃµes do modal */
.modal-button {
  flex: 1;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-family: inherit;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 100px;
}

.cancel-button {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
}

.cancel-button:hover {
  background: #e5e7eb;
  border-color: #9ca3af;
}

.confirm-button {
  background: #dc2626;
  color: white;
}

.confirm-button:hover {
  background: #b91c1c;
}

.modal-button:active {
  transform: translateY(1px);
}

.modal-button:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* Responsividade */
@media (max-width: 480px) {
  .modal-container {
    margin: 16px;
    width: calc(100% - 32px);
  }

  .modal-footer {
    flex-direction: column;
  }

  .modal-button {
    width: 100%;
  }
}
</style>

