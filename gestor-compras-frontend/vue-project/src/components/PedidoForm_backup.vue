<template>
  <div class="pedido-form">
    <div class="form-header">
      <h2 class="form-title">{{ pedido?.id ? 'Editar Pedido' : 'Novo Pedido' }}</h2>
    </div>

    <form @submit.prevent="handleSubmit">
      <div class="form-grid">
        <!-- Dados Básicos -->
        <div class="form-section">
          <h3 class="section-title">Dados Básicos</h3>

          <div class="form-group">
            <label class="form-label">Requisitante</label>
            <input
              type="text"
              v-model="formData.requisitante"
              class="form-input"
              required
              placeholder="Nome do solicitante"
            />
          </div>

          <div class="form-group">
            <label class="form-label">Unidade Funcional</label>
            <select v-model="formData.unidadeFuncional" class="form-select" required>
              <option value="">Selecione uma unidade</option>
              <option value="TI">Tecnologia da Informação</option>
              <option value="RH">Recursos Humanos</option>
              <option value="Financeiro">Financeiro</option>
              <option value="Comercial">Comercial</option>
              <option value="Operações">Operações</option>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">Objetivo</label>
            <select v-model="formData.objetivo" class="form-select" required>
              <option value="">Selecione um objetivo</option>
              <option value="reposicao">Reposição</option>
              <option value="consumo">Consumo</option>
              <option value="projeto">Projeto</option>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">Observações</label>
            <textarea
              v-model="formData.observacoes"
              class="form-textarea"
              placeholder="Informações adicionais sobre o pedido"
              rows="4"
            ></textarea>
          </div>
        </div>

        <!-- Itens do Pedido -->
        <div class="form-section">
          <div class="section-header">
            <h3 class="section-title">Itens do Pedido</h3>
            <button type="button" class="btn-add-item" @click="adicionarItem">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
              </svg>
              Adicionar Item
            </button>
          </div>

          <div v-if="formData.itens.length === 0" class="empty-items">
            <p>Nenhum item adicionado ao pedido.</p>
            <button type="button" class="btn-outline" @click="adicionarItem">
              Adicionar Primeiro Item
            </button>
          </div>

          <div v-else class="itens-list">
            <div v-for="(item, index) in formData.itens" :key="index" class="item-card">
              <div class="item-header">
                <h4 class="item-title">Item #{{ index + 1 }}</h4>
                <button type="button" class="btn-remove-item" @click="removerItem(index)" title="Remover item">
                  <svg viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
                  </svg>
                </button>
              </div>

              <div class="item-form">
                <div class="form-group">
                  <label class="form-label">Descrição</label>
                  <input
                    type="text"
                    v-model="item.descricao"
                    class="form-input"
                    required
                    placeholder="Descrição do item"
                  />
                </div>

                <div class="form-row">
                  <div class="form-group">
                    <label class="form-label">Quantidade</label>
                    <input
                      type="number"
                      v-model="item.quantidade"
                      class="form-input"
                      required
                      min="1"
                      step="1"
                    />
                  </div>

                  <div class="form-group">
                    <label class="form-label">Unidade</label>
                    <select v-model="item.unidade" class="form-select" required>
                      <option value="un">Unidade</option>
                      <option value="kg">Kilograma</option>
                      <option value="m">Metro</option>
                      <option value="l">Litro</option>
                      <option value="cx">Caixa</option>
                    </select>
                  </div>
                </div>

                <div class="form-group">
                  <label class="form-label">Justificativa</label>
                  <textarea
                    v-model="item.justificativa"
                    class="form-textarea"
                    placeholder="Por que este item é necessário?"
                    rows="2"
                  ></textarea>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="btn-secondary" @click="cancelar">Cancelar</button>
        <button type="submit" class="btn-primary" :disabled="!formValido">
          {{ pedido?.id ? 'Atualizar Pedido' : 'Criar Pedido' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
/**
 * Componente de Formulário de Pedido
 *
 * Usado para criar novos pedidos ou editar pedidos existentes
 */
import { ref, computed, onMounted } from 'vue';

const props = defineProps({
  pedido: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['save', 'cancel']);

// Estado do formulário
const formData = ref({
  requisitante: '',
  unidadeFuncional: '',
  objetivo: '',
  observacoes: '',
  itens: []
});

// Verificação básica de validação do formulário
const formValido = computed(() => {
  return (
    formData.value.requisitante &&
    formData.value.unidadeFuncional &&
    formData.value.objetivo &&
    formData.value.itens.length > 0
  );
});

// Adicionar um novo item vazio
const adicionarItem = () => {
  formData.value.itens.push({
    descricao: '',
    quantidade: 1,
    unidade: 'un',
    justificativa: ''
  });
};

// Remover um item pelo índice
const removerItem = (index) => {
  formData.value.itens.splice(index, 1);
};

// Enviar o formulário
const handleSubmit = () => {
  if (!formValido.value) return;

  // Emitir evento com os dados do formulário
  emit('save', { ...formData.value });
};

// Cancelar edição
const cancelar = () => {
  emit('cancel');
};

// Preencher o formulário se for edição
onMounted(() => {
  if (props.pedido) {
    formData.value = {
      requisitante: props.pedido.requisitante || '',
      unidadeFuncional: props.pedido.unidadeFuncional || '',
      objetivo: props.pedido.objetivo || '',
      observacoes: props.pedido.observacoes || '',
      itens: props.pedido.itens ? [...props.pedido.itens] : []
    };
  }

  // Se não houver itens, adicionar um item vazio
  if (formData.value.itens.length === 0) {
    adicionarItem();
  }
});
</script>

<style scoped>
.pedido-form {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  border: 1px solid #e5e7eb;
  margin-bottom: 24px;
}

.form-header {
  margin-bottom: 24px;
}

.form-title {
  font-size: 24px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
  margin-bottom: 32px;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 16px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.form-input,
.form-select,
.form-textarea {
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.empty-items {
  background: #f9fafb;
  border: 1px dashed #d1d5db;
  border-radius: 8px;
  padding: 24px;
  text-align: center;
  color: #6b7280;
}

.itens-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.item-card {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.item-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin: 0;
}

.btn-add-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #dbeafe;
  color: #2563eb;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add-item:hover {
  background: #bfdbfe;
}

.btn-remove-item {
  background: #fee2e2;
  color: #dc2626;
  border: none;
  border-radius: 6px;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-remove-item:hover {
  background: #fecaca;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.btn-primary,
.btn-secondary,
.btn-outline {
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.btn-primary {
  background: #3b82f6;
  color: white;
  border: none;
}

.btn-primary:hover:not(:disabled) {
  background: #2563eb;
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-secondary {
  background: white;
  color: #374151;
  border: 1px solid #d1d5db;
}

.btn-secondary:hover {
  background: #f9fafb;
}

.btn-outline {
  background: white;
  color: #3b82f6;
  border: 1px solid #3b82f6;
}

.btn-outline:hover {
  background: #eff6ff;
}

@media (max-width: 1024px) {
  .form-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }
}

@media (max-width: 640px) {
  .form-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn-primary,
  .btn-secondary {
    width: 100%;
  }
}
</style>
