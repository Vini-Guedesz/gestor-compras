<template>
  <div class="cotacao-form-item">
    <div class="form-header">
      <span class="form-badge">Cotação #{{ numero }}</span>
      <button
        type="button"
        @click="$emit('remove')"
        class="btn-remove"
        title="Remover cotação"
      >
        <svg viewBox="0 0 24 24" width="16" height="16">
          <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
        </svg>
      </button>
    </div>

    <div class="form-body">
      <!-- Fornecedor -->
      <div class="form-group">
        <label class="form-label">
          Fornecedor <span class="required">*</span>
        </label>
        <select
          :value="localCotacao.fornecedorId"
          @change="updateField('fornecedorId', parseInt($event.target.value))"
          class="form-select"
          required
        >
          <option :value="null" disabled>Selecione um fornecedor</option>
          <option
            v-for="fornecedor in fornecedores"
            :key="fornecedor.id"
            :value="fornecedor.id"
          >
            {{ fornecedor.razaoSocial }} - {{ fornecedor.tipo }}
          </option>
        </select>
      </div>

      <!-- Tipo de Fornecedor -->
      <div class="form-group">
        <label class="form-label">
          Tipo <span class="required">*</span>
        </label>
        <div class="radio-group">
          <label class="radio-label">
            <input
              type="radio"
              :value="'PRODUTO'"
              :checked="localCotacao.tipoFornecedor === 'PRODUTO'"
              @change="updateField('tipoFornecedor', 'PRODUTO')"
            />
            <span>Produto</span>
          </label>
          <label class="radio-label">
            <input
              type="radio"
              :value="'SERVICO'"
              :checked="localCotacao.tipoFornecedor === 'SERVICO'"
              @change="updateField('tipoFornecedor', 'SERVICO')"
            />
            <span>Serviço</span>
          </label>
        </div>
      </div>

      <!-- Grid: Valor e Prazo -->
      <div class="form-grid">
        <div class="form-group">
          <label class="form-label">
            Valor (R$) <span class="required">*</span>
          </label>
          <input
            type="number"
            :value="localCotacao.preco"
            @input="updateField('preco', parseFloat($event.target.value))"
            class="form-input"
            placeholder="0,00"
            step="0.01"
            min="0"
            required
          />
        </div>

        <div class="form-group">
          <label class="form-label">
            Prazo (dias úteis)
          </label>
          <input
            type="number"
            :value="localCotacao.prazoEmDiasUteis"
            @input="updateField('prazoEmDiasUteis', parseInt($event.target.value))"
            class="form-input"
            placeholder="15"
            min="0"
          />
        </div>
      </div>

      <!-- Validade -->
      <div class="form-group">
        <label class="form-label">
          Data de Validade <span class="required">*</span>
        </label>
        <input
          type="date"
          :value="localCotacao.dataLimite"
          @input="updateField('dataLimite', $event.target.value)"
          class="form-input"
          :min="dataMinima"
          required
        />
      </div>

      <!-- Upload PDF -->
      <div class="form-group">
        <label class="form-label">
          Anexo PDF (Opcional)
        </label>
        <div class="upload-container">
          <input
            type="file"
            ref="fileInput"
            @change="handleFileUpload"
            accept=".pdf,application/pdf"
            class="file-input"
            id="file-upload"
          />
          <label :for="`file-upload-${numero}`" class="upload-label">
            <svg viewBox="0 0 24 24" width="20" height="20">
              <path fill="currentColor" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20M10,19L12,15H9V10H15V15L13,19H10Z"/>
            </svg>
            <span v-if="!localCotacao.pdfFile">Escolher arquivo PDF</span>
            <span v-else class="file-name">{{ localCotacao.pdfFile.name }}</span>
          </label>
          <button
            v-if="localCotacao.pdfFile"
            type="button"
            @click="removerPdf"
            class="btn-remove-file"
            title="Remover arquivo"
          >
            ✕
          </button>
        </div>
        <span v-if="localCotacao.pdfFile" class="file-info">
          {{ formatarTamanho(localCotacao.pdfFile.size) }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'CotacaoFormItem',
  props: {
    cotacao: {
      type: Object,
      required: true
    },
    numero: {
      type: Number,
      required: true
    },
    fornecedores: {
      type: Array,
      default: () => []
    }
  },
  emits: ['update', 'remove'],
  setup(props, { emit }) {
    const fileInput = ref(null)
    const localCotacao = ref({ ...props.cotacao })

    // Computed
    const dataMinima = computed(() => {
      const hoje = new Date()
      return hoje.toISOString().split('T')[0]
    })

    // Métodos
    const updateField = (field, value) => {
      localCotacao.value[field] = value
      emit('update', localCotacao.value)
    }

    const handleFileUpload = async (event) => {
      const file = event.target.files[0]
      if (!file) return

      // Validar tipo
      if (file.type !== 'application/pdf') {
        alert('Por favor, selecione apenas arquivos PDF')
        event.target.value = ''
        return
      }

      // Validar tamanho (max 10MB)
      const maxSize = 10 * 1024 * 1024 // 10MB
      if (file.size > maxSize) {
        alert('O arquivo deve ter no máximo 10MB')
        event.target.value = ''
        return
      }

      // Converter para byte array
      try {
        const arrayBuffer = await file.arrayBuffer()
        const byteArray = new Uint8Array(arrayBuffer)

        localCotacao.value.pdfFile = file
        localCotacao.value.anexoPdf = Array.from(byteArray)

        emit('update', localCotacao.value)
      } catch (error) {
        console.error('Erro ao processar PDF:', error)
        alert('Erro ao processar o arquivo PDF')
      }
    }

    const removerPdf = () => {
      localCotacao.value.pdfFile = null
      localCotacao.value.anexoPdf = null
      if (fileInput.value) {
        fileInput.value.value = ''
      }
      emit('update', localCotacao.value)
    }

    const formatarTamanho = (bytes) => {
      if (bytes === 0) return '0 Bytes'
      const k = 1024
      const sizes = ['Bytes', 'KB', 'MB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
    }

    return {
      fileInput,
      localCotacao,
      dataMinima,
      updateField,
      handleFileUpload,
      removerPdf,
      formatarTamanho
    }
  }
}
</script>

<style scoped>
.cotacao-form-item {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

/* Header */
.form-header {
  background: #f9fafb;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e5e7eb;
}

.form-badge {
  background: #10b981;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

.btn-remove {
  background: none;
  border: none;
  color: #ef4444;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-remove:hover {
  background: #fee2e2;
}

/* Body */
.form-body {
  padding: 16px;
}

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

.required {
  color: #ef4444;
}

.form-input,
.form-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

/* Radio Group */
.radio-group {
  display: flex;
  gap: 16px;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 0.875rem;
  color: #374151;
}

.radio-label input[type="radio"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

/* Upload Container */
.upload-container {
  display: flex;
  gap: 8px;
  align-items: center;
}

.file-input {
  display: none;
}

.upload-label {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border: 1px dashed #d1d5db;
  border-radius: 6px;
  background: #f9fafb;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.875rem;
  color: #6b7280;
}

.upload-label:hover {
  border-color: #10b981;
  background: #f0fdf4;
  color: #10b981;
}

.file-name {
  color: #10b981;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.btn-remove-file {
  padding: 8px 12px;
  background: #fee2e2;
  color: #dc2626;
  border: 1px solid #fecaca;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-remove-file:hover {
  background: #fecaca;
}

.file-info {
  display: block;
  font-size: 0.75rem;
  color: #6b7280;
  margin-top: 4px;
  font-style: italic;
}

/* Responsive */
@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .upload-container {
    flex-direction: column;
    align-items: stretch;
  }

  .btn-remove-file {
    width: 100%;
  }
}
</style>
