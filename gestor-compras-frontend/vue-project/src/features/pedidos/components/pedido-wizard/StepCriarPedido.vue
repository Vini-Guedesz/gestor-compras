<template>
  <div class="page-1-container">
    <!-- Informações do Pedido -->
    <div class="form-section">
      <h3 class="section-title">📋 Informações do Pedido</h3>

      <div class="form-group">
        <label class="form-label">Data de Criação</label>
        <input
          type="text"
          :value="dataFormatada"
          class="form-input"
          readonly
          disabled
        />
      </div>

      <div class="form-group">
        <label for="objetivo" class="form-label">
          Objetivo / Descrição do Pedido
        </label>
        <textarea
          id="objetivo"
          v-model="formData.objetivo"
          class="form-textarea"
          placeholder="Descreva o objetivo do pedido, justificativa, observações gerais..."
          rows="4"
          maxlength="1000"
        ></textarea>
        <span class="form-hint">{{ formData.objetivo?.length || 0 }}/1000 caracteres</span>
      </div>

      <div class="form-group">
        <label for="status" class="form-label">Status do Pedido</label>
        <select
          id="status"
          v-model="formData.status"
          class="form-select"
        >
          <option value="RASCUNHO">Rascunho</option>
          <option value="PENDENTE">Pendente</option>
        </select>
        <span class="form-hint">
          Rascunho: pode ser editado posteriormente | Pendente: aguarda aprovação
        </span>
      </div>
    </div>

    <!-- Itens do Pedido -->
    <div class="form-section">
      <div class="section-header">
        <h3 class="section-title">📦 Itens do Pedido</h3>
        <button type="button" @click="adicionarItem" class="btn-add-item">
          <svg viewBox="0 0 24 24" width="16" height="16">
            <path fill="white" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
          </svg>
          Adicionar Item
        </button>
      </div>

      <!-- Lista de Itens -->
      <div v-if="formData.itens.length > 0" class="itens-lista">
        <div
          v-for="(item, index) in formData.itens"
          :key="index"
          class="item-card"
        >
          <div class="item-header">
            <span class="item-badge">Item #{{ index + 1 }}</span>
            <div class="item-actions">
              <button
                type="button"
                @click="moverItemParaCima(index)"
                class="btn-move"
                :disabled="index === 0"
                title="Mover para cima"
              >
                ▲
              </button>
              <button
                type="button"
                @click="moverItemParaBaixo(index)"
                class="btn-move"
                :disabled="index === formData.itens.length - 1"
                title="Mover para baixo"
              >
                ▼
              </button>
              <button
                type="button"
                @click="removerItem(index)"
                class="btn-remove"
                :disabled="formData.itens.length === 1"
                title="Remover item"
              >
                Remover
              </button>
            </div>
          </div>

          <div class="item-body">
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">
                  Nome do Item <span class="required">*</span>
                </label>
                <input
                  type="text"
                  v-model="item.nome"
                  class="form-input"
                  placeholder="Ex: Notebook Dell Inspiron 15"
                  maxlength="255"
                  required
                />
              </div>

              <div class="form-group">
                <label class="form-label">
                  Quantidade <span class="required">*</span>
                </label>
                <input
                  type="number"
                  v-model.number="item.quantidade"
                  class="form-input"
                  placeholder="1"
                  min="1"
                  required
                />
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">Descrição / Especificações</label>
              <textarea
                v-model="item.descricao"
                class="form-textarea"
                placeholder="Descrição detalhada do item, especificações técnicas..."
                rows="2"
                maxlength="500"
              ></textarea>
            </div>

            <div class="form-group">
              <label class="form-label">Observações</label>
              <textarea
                v-model="item.observacao"
                class="form-textarea"
                placeholder="Observações adicionais sobre este item..."
                rows="2"
                maxlength="255"
              ></textarea>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <p>Nenhum item adicionado. Clique em "Adicionar Item" para começar.</p>
      </div>

      <!-- Resumo -->
      <div v-if="formData.itens.length > 0" class="resumo-box">
        <div class="resumo-item">
          <span class="resumo-label">Total de Itens:</span>
          <span class="resumo-value">{{ formData.itens.length }}</span>
        </div>
        <div class="resumo-item">
          <span class="resumo-label">Quantidade Total:</span>
          <span class="resumo-value">{{ quantidadeTotal }}</span>
        </div>
      </div>
    </div>

    <!-- Mensagens de Validação -->
    <div v-if="validationErrors.length > 0" class="validation-errors">
      <div class="error-title">⚠️ Corrija os seguintes problemas:</div>
      <ul>
        <li v-for="(error, index) in validationErrors" :key="index">{{ error }}</li>
      </ul>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue'

export default {
  name: 'PedidoFormPage1',
  props: {
    modelValue: {
      type: Object,
      required: true
    }
  },
  emits: ['update:modelValue', 'validation-change'],
  setup(props, { emit }) {
    // Form data local (sincronizado com parent via v-model)
    const formData = ref({ ...props.modelValue })

    // Computed
    const dataFormatada = computed(() => {
      return new Date().toLocaleString('pt-BR')
    })

    const quantidadeTotal = computed(() => {
      return formData.value.itens.reduce((total, item) => total + (item.quantidade || 0), 0)
    })

    const validationErrors = computed(() => {
      const errors = []

      // Validar objetivo
      if (formData.value.objetivo && formData.value.objetivo.length > 1000) {
        errors.push('Objetivo deve ter no máximo 1000 caracteres')
      }

      // Deve ter pelo menos 1 item
      if (formData.value.itens.length === 0) {
        errors.push('Adicione pelo menos um item ao pedido')
      }

      // Validar cada item
      formData.value.itens.forEach((item, index) => {
        if (!item.nome?.trim()) {
          errors.push(`Item ${index + 1}: Nome é obrigatório`)
        }
        if (!item.quantidade || item.quantidade <= 0) {
          errors.push(`Item ${index + 1}: Quantidade deve ser maior que zero`)
        }
        if (item.nome && item.nome.length > 255) {
          errors.push(`Item ${index + 1}: Nome deve ter no máximo 255 caracteres`)
        }
        if (item.descricao && item.descricao.length > 500) {
          errors.push(`Item ${index + 1}: Descrição deve ter no máximo 500 caracteres`)
        }
        if (item.observacao && item.observacao.length > 255) {
          errors.push(`Item ${index + 1}: Observação deve ter no máximo 255 caracteres`)
        }
      })

      return errors
    })

    const isValid = computed(() => validationErrors.value.length === 0)

    // Métodos
    const adicionarItem = () => {
      formData.value.itens.push({
        nome: '',
        quantidade: 1,
        descricao: '',
        observacao: ''
      })
    }

    const removerItem = (index) => {
      if (formData.value.itens.length > 1) {
        formData.value.itens.splice(index, 1)
      }
    }

    const moverItemParaCima = (index) => {
      if (index > 0) {
        const item = formData.value.itens[index]
        formData.value.itens.splice(index, 1)
        formData.value.itens.splice(index - 1, 0, item)
      }
    }

    const moverItemParaBaixo = (index) => {
      if (index < formData.value.itens.length - 1) {
        const item = formData.value.itens[index]
        formData.value.itens.splice(index, 1)
        formData.value.itens.splice(index + 1, 0, item)
      }
    }

    // Watchers
    watch(formData, (newVal) => {
      emit('update:modelValue', newVal)
    }, { deep: true })

    watch(isValid, (newVal) => {
      emit('validation-change', newVal)
    })

    // Emit initial validation state
    emit('validation-change', isValid.value)

    return {
      formData,
      dataFormatada,
      quantidadeTotal,
      validationErrors,
      isValid,
      adicionarItem,
      removerItem,
      moverItemParaCima,
      moverItemParaBaixo
    }
  }
}
</script>

<style scoped>
/* Form Sections */
.form-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e5e7eb;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

/* Form Elements */
.form-group {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
  font-size: 0.875rem;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-input:disabled {
  background: #f3f4f6;
  color: #6b7280;
  cursor: not-allowed;
}

.form-textarea {
  resize: vertical;
  font-family: inherit;
}

.form-hint {
  display: block;
  font-size: 0.75rem;
  color: #6b7280;
  margin-top: 4px;
  font-style: italic;
}

.required {
  color: #ef4444;
}

.form-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
}

/* Buttons */
.btn-add-item {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #3b82f6;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add-item:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.btn-move,
.btn-remove {
  padding: 4px 8px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 0.75rem;
  cursor: pointer;
  transition: all 0.2s;
  background: white;
}

.btn-move {
  color: #3b82f6;
  min-width: 32px;
}

.btn-move:hover:not(:disabled) {
  background: #dbeafe;
  border-color: #3b82f6;
}

.btn-move:disabled {
  color: #9ca3af;
  cursor: not-allowed;
  opacity: 0.5;
}

.btn-remove {
  color: #ef4444;
}

.btn-remove:hover:not(:disabled) {
  background: #fee2e2;
  border-color: #ef4444;
}

.btn-remove:disabled {
  color: #9ca3af;
  cursor: not-allowed;
  opacity: 0.5;
}

/* Itens */
.itens-lista {
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

.item-badge {
  background: #3b82f6;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

.item-actions {
  display: flex;
  gap: 8px;
}

/* Resumo */
.resumo-box {
  margin-top: 24px;
  padding: 16px;
  background: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 8px;
  display: flex;
  gap: 32px;
}

.resumo-item {
  display: flex;
  gap: 8px;
}

.resumo-label {
  font-weight: 500;
  color: #374151;
  font-size: 0.875rem;
}

.resumo-value {
  font-weight: 700;
  color: #1e40af;
  font-size: 0.875rem;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 48px 24px;
  color: #6b7280;
  font-style: italic;
  background: #f9fafb;
  border: 1px dashed #d1d5db;
  border-radius: 8px;
}

/* Validation Errors */
.validation-errors {
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  padding: 16px;
  margin-top: 16px;
}

.error-title {
  font-weight: 600;
  color: #dc2626;
  margin-bottom: 8px;
}

.validation-errors ul {
  margin: 0;
  padding-left: 20px;
  color: #b91c1c;
  font-size: 0.875rem;
}

.validation-errors li {
  margin-bottom: 4px;
}

/* Responsive */
@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .resumo-box {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
