<template>
  <div class="page-1-container">
    <!-- Indicador de alterações não salvas -->
    <div v-if="hasUnsavedChanges" class="unsaved-warning">
      <svg viewBox="0 0 24 24" width="20" height="20">
        <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
      </svg>
      <span>Você tem alterações não salvas</span>
    </div>

    <!-- Informações do Pedido -->
    <div class="form-section">
      <h3 class="section-title">📋 Informações do Rascunho</h3>

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
          Observação / Descrição do Pedido
        </label>
        <textarea
          id="objetivo"
          v-model="formData.observacao"
          class="form-textarea"
          placeholder="Descreva o objetivo do pedido, justificativa, observações gerais..."
          rows="4"
          maxlength="1000"
          @input="markAsUnsaved"
        ></textarea>
        <span class="form-hint">{{ formData.observacao?.length || 0 }}/1000 caracteres</span>
      </div>
    </div>

    <!-- Itens do Pedido -->
    <div class="form-section">
      <div class="section-header">
        <h3 class="section-title">📦 Itens do Rascunho</h3>
        <button type="button" @click="adicionarNovoItem" class="btn-add-item">
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
          :key="item.id || `new-${index}`"
          class="item-card"
          :class="{ 'item-unsaved': item._unsaved, 'item-saving': item._saving }"
        >
          <div class="item-header">
            <div class="item-badge-container">
              <span class="item-badge">Item #{{ item.numeroItem || index + 1 }}</span>
              <span v-if="item._unsaved" class="item-status unsaved">● Não salvo</span>
              <span v-else-if="item.id" class="item-status saved">✓ Salvo</span>
            </div>
            <div class="item-actions">
              <button
                type="button"
                @click="salvarItem(index)"
                class="btn-save-item"
                :disabled="!itemValido(item) || item._saving"
                title="Salvar este item"
              >
                {{ item._saving ? 'Salvando...' : 'Salvar' }}
              </button>
              <button
                type="button"
                @click="removerItem(index)"
                class="btn-remove"
                :disabled="item._saving"
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
                  @input="markItemAsUnsaved(index)"
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
                  @input="markItemAsUnsaved(index)"
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
                @input="markItemAsUnsaved(index)"
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
                @input="markItemAsUnsaved(index)"
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
        <div class="resumo-item">
          <span class="resumo-label">Itens Salvos:</span>
          <span class="resumo-value">{{ itensSalvos }} / {{ formData.itens.length }}</span>
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
import rascunhoService from '@/services/rascunhoService.js'

export default {
  name: 'PedidoFormPage1',
  props: {
    modelValue: {
      type: Object,
      required: true
    }
  },
  emits: ['update:modelValue', 'validation-change', 'rascunho-created'],
  setup(props, { emit }) {
    const formData = ref({ ...props.modelValue })
    const hasUnsavedChanges = ref(false)

    // Observar mudanças no modelValue (quando rascunho é carregado)
    watch(() => props.modelValue, (newValue) => {
      if (newValue && newValue.id) {
        formData.value = {
          ...newValue,
          itens: (newValue.itens || []).map(item => ({
            ...item,
            _unsaved: false,
            _saving: false
          }))
        }
        hasUnsavedChanges.value = false
      }
    }, { deep: true })

    const dataFormatada = computed(() => {
      if (formData.value.dataCriacao) {
        return new Date(formData.value.dataCriacao).toLocaleString('pt-BR')
      }
      return new Date().toLocaleString('pt-BR')
    })

    const quantidadeTotal = computed(() => {
      return formData.value.itens.reduce((total, item) => total + (item.quantidade || 0), 0)
    })

    const itensSalvos = computed(() => {
      return formData.value.itens.filter(item => item.id && !item._unsaved).length
    })

    const validationErrors = computed(() => {
      const errors = []

      if (formData.value.observacao && formData.value.observacao.length > 1000) {
        errors.push('Observação deve ter no máximo 1000 caracteres')
      }

      if (formData.value.itens.length === 0) {
        errors.push('Adicione pelo menos um item ao rascunho')
      }

      formData.value.itens.forEach((item, index) => {
        const itemNum = item.numeroItem || index + 1
        if (!item.nome?.trim()) {
          errors.push(`Item ${itemNum}: Nome é obrigatório`)
        }
        if (!item.quantidade || item.quantidade <= 0) {
          errors.push(`Item ${itemNum}: Quantidade deve ser maior que zero`)
        }
      })

      return errors
    })

    const isValid = computed(() => validationErrors.value.length === 0)

    const itemValido = (item) => {
      return item.nome?.trim() && item.quantidade > 0
    }

    const markAsUnsaved = () => {
      hasUnsavedChanges.value = true
    }

    const markItemAsUnsaved = (index) => {
      formData.value.itens[index]._unsaved = true
      hasUnsavedChanges.value = true
    }

    const criarRascunhoComItem = async (itemData) => {
      const novoRascunho = await rascunhoService.criar({
        observacao: formData.value.observacao || '',
        itens: [itemData]
      })
      formData.value.id = novoRascunho.id
      formData.value.dataCriacao = novoRascunho.dataCriacao
      emit('rascunho-created', novoRascunho)
      return novoRascunho
    }

    const adicionarNovoItem = () => {
      formData.value.itens.push({
        nome: '',
        quantidade: 1,
        descricao: '',
        observacao: '',
        _unsaved: true
      })
      hasUnsavedChanges.value = true
    }

    const salvarItem = async (index) => {
      const item = formData.value.itens[index]
      if (!itemValido(item)) return

      item._saving = true

      try {
        const itemData = {
          nome: item.nome,
          quantidade: item.quantidade,
          descricao: item.descricao || '',
          observacao: item.observacao || ''
        }

        let rascunhoAtualizado

        if (!formData.value.id) {
          // Primeiro item - criar rascunho com o item
          rascunhoAtualizado = await criarRascunhoComItem(itemData)
        } else if (item.id) {
          // Atualizar item existente
          rascunhoAtualizado = await rascunhoService.atualizarItem(
            formData.value.id,
            item.id,
            itemData
          )
        } else {
          // Adicionar novo item ao rascunho existente
          rascunhoAtualizado = await rascunhoService.adicionarItem(
            formData.value.id,
            itemData
          )
        }

        // Atualizar lista de itens com dados do servidor
        formData.value.itens = rascunhoAtualizado.itens.map(serverItem => ({
          ...serverItem,
          _unsaved: false,
          _saving: false
        }))

        hasUnsavedChanges.value = formData.value.itens.some(i => i._unsaved)

      } catch (error) {
        console.error('Erro ao salvar item:', error)
        alert(error.message || 'Erro ao salvar item')
      } finally {
        item._saving = false
      }
    }

    const removerItem = async (index) => {
      const item = formData.value.itens[index]

      if (item.id && formData.value.id) {
        // Item já salvo no servidor - remover via API
        if (!confirm('Deseja remover este item do rascunho?')) return

        try {
          const rascunhoAtualizado = await rascunhoService.removerItem(
            formData.value.id,
            item.id
          )

          formData.value.itens = rascunhoAtualizado.itens.map(serverItem => ({
            ...serverItem,
            _unsaved: false,
            _saving: false
          }))

        } catch (error) {
          console.error('Erro ao remover item:', error)
          alert(error.message || 'Erro ao remover item')
        }
      } else {
        // Item ainda não salvo - bloquear remoção
        alert('Salve o item antes de removê-lo.')
        return
      }

      hasUnsavedChanges.value = formData.value.itens.some(i => i._unsaved)
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
      hasUnsavedChanges,
      dataFormatada,
      quantidadeTotal,
      itensSalvos,
      validationErrors,
      isValid,
      itemValido,
      markAsUnsaved,
      markItemAsUnsaved,
      adicionarNovoItem,
      salvarItem,
      removerItem
    }
  }
}
</script>

<style scoped>
/* Unsaved Warning */
.unsaved-warning {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #fef3c7;
  border: 1px solid #f59e0b;
  border-radius: 8px;
  color: #92400e;
  font-size: 0.875rem;
  font-weight: 500;
  margin-bottom: 20px;
}

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

.btn-save-item {
  padding: 6px 12px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-save-item:hover:not(:disabled) {
  background: #059669;
}

.btn-save-item:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.btn-remove {
  padding: 6px 12px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 0.75rem;
  cursor: pointer;
  transition: all 0.2s;
  background: white;
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
  transition: all 0.2s;
}

.item-card.item-unsaved {
  border-color: #f59e0b;
  background: #fffbeb;
}

.item-card.item-saving {
  opacity: 0.7;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.item-badge-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.item-badge {
  background: #3b82f6;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

.item-status {
  font-size: 0.7rem;
  font-weight: 500;
}

.item-status.unsaved {
  color: #f59e0b;
}

.item-status.saved {
  color: #10b981;
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
  flex-wrap: wrap;
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
