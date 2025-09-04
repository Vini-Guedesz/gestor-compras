<template>
  <div v-if="isVisible" class="modal-overlay">
    <div class="modal-container">
      <div class="modal-header">
        <h2 class="modal-title">{{ fornecedor ? 'Editar Fornecedor' : 'Novo Fornecedor' }}</h2>
        <button @click="$emit('close')" class="close-button">&times;</button>
      </div>
      <div class="modal-body">
        <p>Formulário de fornecedor aqui.</p>
      </div>
      <div class="modal-footer">
        <button @click="$emit('close')" class="btn-secondary">Cancelar</button>
        <button @click="$emit('save')" class="btn-primary">Salvar</button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  fornecedor: {
    type: Object,
    default: null
  }
})

defineEmits(['close', 'save'])
</script>

<style scoped>
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
  z-index: 1100;
}

.modal-container {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  box-shadow: 0 10px 25px -3px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #111827;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #9ca3af;
}

.modal-body {
  padding: 24px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
}

.btn-primary {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover {
  background: #2563eb;
}

.btn-secondary {
  background: #e5e7eb;
  color: #374151;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: #d1d5db;
}
</style>
