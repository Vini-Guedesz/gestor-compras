<!--
  Componente LogoutModal - Modal de confirmação de logout

  Modal elegante para confirmar o logout do usuário, substituindo
  o alert() nativo por uma interface mais amigável.

  Props:
  - show (Boolean): Controla a visibilidade do modal

  Events:
  - confirm: Emitido quando o usuário confirma o logout
  - cancel: Emitido quando o usuário cancela o logout
-->
<template>
  <!-- Overlay do modal -->
  <div v-if="show" class="modal-overlay" @click="handleCancel">
    <div class="modal-container" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">Confirmar Logout</h3>
      </div>

      <div class="modal-body">
        <div class="modal-icon">
          <svg viewBox="0 0 24 24" width="48" height="48">
            <path fill="#f59e0b" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 15h2v2h-2v-2zm0-8h2v6h-2V9z"/>
          </svg>
        </div>

        <div class="modal-content">
          <p class="modal-message">
            Tem certeza que deseja sair da aplicação?
          </p>
          <p class="modal-submessage">
            Você precisará fazer login novamente para acessar o sistema.
          </p>
        </div>
      </div>

      <div class="modal-footer">
        <button class="modal-button cancel-button" @click="handleCancel">
          Cancelar
        </button>
        <button class="modal-button confirm-button" @click="handleConfirm">
          Sim, Sair
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * Modal de confirmação de logout
 *
 * Proporciona uma experiência mais profissional que o alert() nativo
 */

// Props do componente
defineProps({
  show: {
    type: Boolean,
    default: false,
    required: true
  }
})

// Events emitidos pelo componente
const emit = defineEmits(['confirm', 'cancel'])

/**
 * Manipula a confirmação do logout
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
</script>

<style scoped>
/*
  Estilos do Modal de Logout

  Design moderno e acessível com animações suaves
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
  max-height: 90vh;
  overflow: hidden;
  animation: slideIn 0.2s ease-out;
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

/* Cabeçalho do modal */
.modal-header {
  padding: 24px 24px 0 24px;
  text-align: center;
}

.modal-title {
  font-family: Arial, sans-serif;
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
  font-family: Arial, sans-serif;
  font-size: 16px;
  font-weight: 500;
  color: #374151;
  margin: 0 0 8px 0;
  line-height: 1.5;
}

.modal-submessage {
  font-family: Arial, sans-serif;
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  line-height: 1.4;
}

/* Rodapé do modal */
.modal-footer {
  padding: 0 24px 24px 24px;
  display: flex;
  gap: 12px;
  justify-content: center;
}

/* Botões do modal */
.modal-button {
  flex: 1;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-family: Arial, sans-serif;
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
