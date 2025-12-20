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
            type="text"
            :value="precoFormatado"
            @input="handlePrecoInput"
            @blur="formatarPrecoFinal"
            class="form-input"
            placeholder="0,00"
            inputmode="decimal"
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

      <!-- Gasto Previsto -->
      <div class="form-group">
        <label class="checkbox-label">
          <input
            type="checkbox"
            :checked="localCotacao.gastoPrevisto"
            @change="updateField('gastoPrevisto', $event.target.checked)"
            class="form-checkbox"
          />
          <span>Gasto Previsto no Orçamento</span>
        </label>
        <small class="form-hint">
          Marque se este gasto já estava previsto no orçamento
        </small>
      </div>

      <!-- Projeto (apenas se gasto previsto) -->
      <div v-if="localCotacao.gastoPrevisto" class="form-group">
        <label class="form-label">
          Projeto
        </label>
        <input
          type="text"
          :value="localCotacao.projeto"
          @input="updateField('projeto', $event.target.value)"
          class="form-input"
          placeholder="Nome do projeto (ex: Projeto Expansão 2025)"
          maxlength="255"
        />
        <small class="form-hint">
          Informe o nome do projeto ao qual este gasto pertence
        </small>
      </div>

      <!-- Upload PDFs -->
      <div class="form-group">
        <label class="form-label">
          Anexos PDF (Opcional)
        </label>
        <div class="upload-container">
          <input
            type="file"
            ref="fileInput"
            @change="handleFileUpload"
            accept=".pdf,application/pdf"
            class="file-input"
            :id="`file-upload-${numero}`"
            multiple
          />
          <label :for="`file-upload-${numero}`" class="upload-label">
            <svg viewBox="0 0 24 24" width="20" height="20">
              <path fill="currentColor" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20M10,19L12,15H9V10H15V15L13,19H10Z"/>
            </svg>
            <span>{{ localCotacao.pdfFiles?.length > 0 ? `${localCotacao.pdfFiles.length} arquivo(s) selecionado(s)` : 'Escolher arquivos PDF' }}</span>
          </label>
        </div>

        <!-- Lista de arquivos selecionados -->
        <div v-if="localCotacao.pdfFiles?.length > 0" class="files-list">
          <div
            v-for="(file, index) in localCotacao.pdfFiles"
            :key="index"
            class="file-item"
          >
            <div class="file-item-info">
              <svg viewBox="0 0 24 24" width="16" height="16" class="file-icon">
                <path fill="currentColor" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
              </svg>
              <span class="file-item-name">{{ file.name }}</span>
              <span class="file-item-size">{{ formatarTamanho(file.size) }}</span>
            </div>
            <button
              type="button"
              @click="removerPdf(index)"
              class="btn-remove-file-small"
              title="Remover arquivo"
            >
              ✕
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useToast } from '@/composables/useToast.js'
import logger from '@/utils/logger.js'

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
    const { success, error: showError, warning } = useToast()
    const fileInput = ref(null)
    const localCotacao = ref({
      ...props.cotacao,
      pdfFiles: props.cotacao.pdfFiles || []
    })
    const precoFormatado = ref('')

    // Computed
    const dataMinima = computed(() => {
      const hoje = new Date()
      return hoje.toISOString().split('T')[0]
    })

    // Inicializar preço formatado
    if (props.cotacao.preco) {
      precoFormatado.value = props.cotacao.preco.toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }

    // Funções de formatação de preço
    const formatarPrecoInput = (valor) => {
      let numero = valor.replace(/\D/g, '')
      if (!numero) return ''
      const numeroDecimal = parseFloat(numero) / 100
      return numeroDecimal.toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }

    const handlePrecoInput = (event) => {
      const valor = event.target.value
      precoFormatado.value = formatarPrecoInput(valor)
      const numero = valor.replace(/\D/g, '')
      localCotacao.value.preco = numero ? parseFloat(numero) / 100 : null
      emit('update', localCotacao.value)
    }

    const formatarPrecoFinal = () => {
      if (localCotacao.value.preco !== null && localCotacao.value.preco !== undefined) {
        precoFormatado.value = localCotacao.value.preco.toLocaleString('pt-BR', {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2
        })
      }
    }

    // Métodos
    const updateField = (field, value) => {
      localCotacao.value[field] = value
      emit('update', localCotacao.value)
    }

    const handleFileUpload = async (event) => {
      const files = Array.from(event.target.files || [])

      if (files.length === 0) return

      const arquivosValidos = []

      for (const file of files) {
        // Validar tipo
        if (file.type !== 'application/pdf') {
          warning(`${file.name} não é um arquivo PDF válido`)
          continue
        }

        // Validar tamanho (10MB)
        if (file.size > 10 * 1024 * 1024) {
          warning(`${file.name} excede o tamanho máximo de 10MB`)
          continue
        }

        arquivosValidos.push(file)
      }

      // Inicializar array se não existir
      if (!localCotacao.value.pdfFiles) {
        localCotacao.value.pdfFiles = []
      }

      // Adicionar arquivos válidos à lista
      localCotacao.value.pdfFiles = [...localCotacao.value.pdfFiles, ...arquivosValidos]

      // Converter para byte arrays (manter compatibilidade)
      try {
        const anexosPromises = localCotacao.value.pdfFiles.map(async (file) => {
          const arrayBuffer = await file.arrayBuffer()
          return Array.from(new Uint8Array(arrayBuffer))
        })
        const anexos = await Promise.all(anexosPromises)
        localCotacao.value.anexoPdf = anexos[0] // Manter primeiro para compatibilidade
      } catch (error) {
        logger.error('Erro ao processar PDFs:', error)
        showError('Erro ao processar os arquivos PDF')
      }

      // Limpar input
      if (fileInput.value) {
        fileInput.value.value = ''
      }

      emit('update', localCotacao.value)
    }

    const removerPdf = (index) => {
      if (!localCotacao.value.pdfFiles) return

      localCotacao.value.pdfFiles.splice(index, 1)

      // Se removeu todos, limpar anexoPdf também
      if (localCotacao.value.pdfFiles.length === 0) {
        localCotacao.value.anexoPdf = null
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
      precoFormatado,
      handlePrecoInput,
      formatarPrecoFinal,
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
  width: 100%;
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
  width: 100%;
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
  min-width: 0;
}

.upload-label:hover {
  border-color: #10b981;
  background: #f0fdf4;
  color: #10b981;
}

.upload-label svg {
  flex-shrink: 0;
}

.upload-label span {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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

/* Lista de arquivos */
.files-list {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  transition: all 0.2s;
}

.file-item:hover {
  background: #f3f4f6;
  border-color: #d1d5db;
}

.file-item-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.file-icon {
  flex-shrink: 0;
  color: #10b981;
}

.file-item-name {
  flex: 1;
  font-size: 0.875rem;
  color: #374151;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

.file-item-size {
  flex-shrink: 0;
  font-size: 0.7rem;
  color: #6b7280;
  font-style: italic;
  margin-left: auto;
  padding-left: 8px;
}

.btn-remove-file-small {
  flex-shrink: 0;
  padding: 4px 6px;
  background: #fee2e2;
  color: #dc2626;
  border: 1px solid #fecaca;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.75rem;
  font-weight: 500;
  line-height: 1;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
}

.btn-remove-file-small:hover {
  background: #fecaca;
  border-color: #fca5a5;
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

  .btn-remove-file,
  .btn-remove-file-small {
    width: 100%;
  }

  .file-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .file-item-info {
    width: 100%;
  }
}
</style>
