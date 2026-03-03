<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="show" class="modal-overlay" @click.self="fechar">
        <div class="modal-container">
          <!-- Header -->
          <div class="modal-header">
            <h3 class="modal-title">Editar Cotação</h3>
            <button @click="fechar" class="btn-close" type="button">
              <svg viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
              </svg>
            </button>
          </div>

          <!-- Body -->
          <div class="modal-body">
            <form @submit.prevent="salvar">
              <!-- Informações da Cotação Atual -->
              <div class="info-atual">
                <h4 class="info-titulo">Dados Atuais</h4>
                <div class="info-grid">
                  <div class="info-item">
                    <span class="info-label">Fornecedor:</span>
                    <span class="info-value">{{ cotacao.nomeFornecedor || cotacao.fornecedorNome }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">Tipo:</span>
                    <span class="info-value">{{ cotacao.tipoFornecedor }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">Preço Atual:</span>
                    <span class="info-value">R$ {{ formatarPreco(cotacao.preco) }}</span>
                  </div>
                  <div class="info-item" v-if="cotacao.prazoEmDiasUteis">
                    <span class="info-label">Prazo Atual:</span>
                    <span class="info-value">{{ cotacao.prazoEmDiasUteis }} dias úteis</span>
                  </div>
                  <div class="info-item" v-if="cotacao.dataLimite">
                    <span class="info-label">Validade Atual:</span>
                    <span class="info-value">{{ formatarData(cotacao.dataLimite) }}</span>
                  </div>
                </div>
              </div>

              <!-- Motivo da Edição (obrigatório) -->
              <div class="form-group">
                <label class="form-label">
                  Motivo da Edição <span class="required">*</span>
                </label>
                <textarea
                  v-model="form.motivoEdicao"
                  class="form-textarea"
                  placeholder="Ex: Negociação de preço com fornecedor devido a mudança no mercado..."
                  rows="3"
                  required
                  minlength="10"
                  maxlength="500"
                ></textarea>
                <span class="form-hint">Mínimo 10 caracteres. Este campo é obrigatório para auditoria.</span>
              </div>

              <!-- Editado Por (preenchido automaticamente) -->
              <div class="form-group">
                <label class="form-label">
                  Responsável pela Edição <span class="required">*</span>
                </label>
                <input
                  type="text"
                  v-model="form.editadoPor"
                  class="form-input"
                  placeholder="Nome do responsável"
                  required
                  maxlength="100"
                  readonly
                  style="background-color: #f9fafb; cursor: not-allowed;"
                />
                <span class="form-hint">Preenchido automaticamente com o usuário logado</span>
              </div>

              <!-- Edição por Item (novo modelo) -->
              <div v-if="form.itemEdicoes.length > 0" class="form-group">
                <label class="form-label">Itens da Cotação (preço unitário por item)</label>
                <div class="itens-edicao-lista">
                  <div v-for="(item, index) in form.itemEdicoes" :key="`edit-item-${item.itemPedidoId}`" class="item-edicao-card">
                    <div class="item-edicao-header">
                      <span class="item-edicao-nome">{{ item.nomeItem }}</span>
                      <span class="item-edicao-qtd">Qtd: {{ item.quantidade }}</span>
                    </div>
                    <div class="item-edicao-grid">
                      <div class="item-edicao-field">
                        <label>Preço Unitário (R$)</label>
                        <input
                          type="text"
                          :value="item.precoUnitarioFormatado"
                          @input="handleItemPrecoInput(index, $event)"
                          @blur="formatarItemPrecoFinal(index)"
                          class="form-input"
                          inputmode="decimal"
                          placeholder="0,00"
                        />
                      </div>
                      <div class="item-edicao-field">
                        <label>Subtotal</label>
                        <input
                          type="text"
                          :value="`R$ ${formatarPreco(item.precoUnitario * item.quantidade)}`"
                          class="form-input"
                          readonly
                        />
                      </div>
                    </div>
                    <div class="item-edicao-field">
                      <label>Observação do Item</label>
                      <input
                        type="text"
                        v-model="item.observacao"
                        class="form-input"
                        maxlength="255"
                        placeholder="Observação opcional para este item"
                      />
                    </div>
                  </div>
                </div>
                <div class="total-edicao-itens">
                  Total calculado pelos itens: <strong>R$ {{ formatarPreco(totalItensEditados) }}</strong>
                </div>
              </div>

              <!-- Grid: Novo Valor e Prazo -->
              <div class="form-grid" v-if="form.itemEdicoes.length === 0">
                <div class="form-group">
                  <label class="form-label">
                    Novo Valor (R$)
                  </label>
                  <input
                    type="text"
                    :value="precoFormatado"
                    @input="handlePrecoInput"
                    @blur="formatarPrecoFinal"
                    class="form-input"
                    placeholder="0,00"
                    inputmode="decimal"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">
                    Novo Prazo (dias úteis)
                  </label>
                  <input
                    type="number"
                    v-model.number="form.prazoEmDiasUteis"
                    class="form-input"
                    placeholder="15"
                    min="0"
                  />
                </div>
              </div>

              <!-- Nova Validade -->
              <div class="form-group">
                <label class="form-label">
                  Nova Data de Validade
                </label>
                <input
                  type="date"
                  v-model="form.dataLimite"
                  class="form-input"
                  :min="dataMinima"
                />
              </div>

              <!-- Upload Novos PDFs -->
              <div class="form-group">
                <label class="form-label">
                  Novos Anexos PDF (Opcional)
                </label>
                <div class="upload-container">
                  <input
                    type="file"
                    ref="fileInput"
                    @change="handleFileUpload"
                    accept=".pdf,application/pdf"
                    class="file-input"
                    id="file-upload-edit"
                    multiple
                  />
                  <label for="file-upload-edit" class="upload-label">
                    <svg viewBox="0 0 24 24" width="20" height="20">
                      <path fill="currentColor" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20M10,19L12,15H9V10H15V15L13,19H10Z"/>
                    </svg>
                    <span>{{ form.pdfFiles.length > 0 ? `${form.pdfFiles.length} arquivo(s) selecionado(s)` : 'Escolher arquivos PDF' }}</span>
                  </label>
                </div>

                <!-- Lista de arquivos selecionados -->
                <div v-if="form.pdfFiles.length > 0" class="files-list">
                  <div
                    v-for="(file, index) in form.pdfFiles"
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

              <!-- Botões -->
              <div class="modal-footer">
                <button type="button" @click="fechar" class="btn-cancel" :disabled="salvando">
                  Cancelar
                </button>
                <button type="submit" class="btn-save" :disabled="salvando || !formValido">
                  <span v-if="salvando">Salvando...</span>
                  <span v-else>Salvar Alterações</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script>
import { ref, computed, watch } from 'vue'
import { useModal } from '@/composables/useModal'
import { useAuthStore } from '@/stores/auth'
import { useErrorModal } from '@/composables/useErrorModal'
import logger from '@/utils/logger.js'

export default {
  name: 'EditCotacaoModal',
  props: {
    show: {
      type: Boolean,
      default: false
    },
    cotacao: {
      type: Object,
      required: true
    }
  },
  emits: ['close', 'save'],
  setup(props, { emit }) {
    useModal(computed(() => props.show))
    const authStore = useAuthStore()

    const fileInput = ref(null)
    const salvando = ref(false)
    const precoFormatado = ref('')

    const form = ref({
      motivoEdicao: '',
      editadoPor: '',
      preco: null,
      prazoEmDiasUteis: null,
      dataLimite: null,
      pdfFiles: [],
      itemEdicoes: []
    })

    const dataMinima = computed(() => {
      const hoje = new Date()
      return hoje.toISOString().split('T')[0]
    })

    const totalItensEditados = computed(() => {
      if (!form.value.itemEdicoes || form.value.itemEdicoes.length === 0) {
        return Number(form.value.preco || 0)
      }

      return form.value.itemEdicoes.reduce((total, item) => {
        const subtotal = Number(item.precoUnitario || 0) * Number(item.quantidade || 0)
        return total + subtotal
      }, 0)
    })

    const formValido = computed(() => {
      const baseValido = form.value.motivoEdicao.trim().length >= 10 &&
        form.value.editadoPor.trim().length > 0

      if (!baseValido) return false

      if (!form.value.itemEdicoes || form.value.itemEdicoes.length === 0) {
        return true
      }

      return form.value.itemEdicoes.every(item => Number(item.precoUnitario) > 0 && Number(item.quantidade) > 0)
    })

    // Funções de formatação de preço
    const formatarPrecoInput = (valor) => {
      // Remove tudo exceto números
      let numero = valor.replace(/\D/g, '')

      if (!numero) return ''

      // Converte para número e divide por 100 para ter centavos
      const numeroDecimal = parseFloat(numero) / 100

      // Formata com vírgula
      return numeroDecimal.toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }

    const parsePrecoInput = (valor) => {
      const numero = String(valor || '').replace(/\D/g, '')
      return numero ? parseFloat(numero) / 100 : null
    }

    const handlePrecoInput = (event) => {
      const valor = event.target.value
      precoFormatado.value = formatarPrecoInput(valor)

      // Atualiza o valor numérico no form
      form.value.preco = parsePrecoInput(valor)
    }

    const formatarPrecoFinal = () => {
      if (form.value.preco !== null && form.value.preco !== undefined) {
        precoFormatado.value = form.value.preco.toLocaleString('pt-BR', {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2
        })
      }
    }

    const handleItemPrecoInput = (index, event) => {
      const valor = event.target.value
      const formatado = formatarPrecoInput(valor)
      const preco = parsePrecoInput(valor)

      form.value.itemEdicoes[index].precoUnitarioFormatado = formatado
      form.value.itemEdicoes[index].precoUnitario = preco || 0
    }

    const formatarItemPrecoFinal = (index) => {
      const item = form.value.itemEdicoes[index]
      if (!item) return
      item.precoUnitarioFormatado = item.precoUnitario
        ? item.precoUnitario.toLocaleString('pt-BR', {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2
        })
        : ''
    }

    // Resetar form quando abrir/fechar modal
    watch(() => props.show, (newVal) => {
      if (newVal) {
        // Pegar nome do usuário logado (padronizado com backend)
        const nomeUsuario = authStore.user?.nome || authStore.user?.username || authStore.user?.email || 'Usuário'

        form.value = {
          motivoEdicao: '',
          editadoPor: nomeUsuario,
          preco: props.cotacao.preco,
          prazoEmDiasUteis: props.cotacao.prazoEmDiasUteis,
          dataLimite: props.cotacao.dataLimite,
          pdfFiles: [],
          itemEdicoes: (props.cotacao.itens || []).map(item => ({
            itemPedidoId: item.itemPedidoId,
            nomeItem: item.nomeItem || `Item #${item.itemPedidoId}`,
            quantidade: Number(item.quantidade || 0),
            precoUnitario: Number(item.precoUnitario || 0),
            precoUnitarioFormatado: formatarPrecoInput(String(item.precoUnitario || '').replace(/[^\d]/g, '')),
            observacao: item.observacao || ''
          }))
        }
        // Formatar preço inicial
        if (props.cotacao.preco) {
          precoFormatado.value = props.cotacao.preco.toLocaleString('pt-BR', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
          })
        } else {
          precoFormatado.value = ''
        }
      } else {
        // Limpar form ao fechar
        form.value = {
          motivoEdicao: '',
          editadoPor: '',
          preco: null,
          prazoEmDiasUteis: null,
          dataLimite: null,
          pdfFiles: [],
          itemEdicoes: []
        }
        precoFormatado.value = ''
      }
    })

    const fechar = () => {
      if (!salvando.value) {
        emit('close')
      }
    }

    const salvar = async () => {
      if (!formValido.value || salvando.value) return

      salvando.value = true
      try {
        const dadosEdicao = {
          id: props.cotacao.id,
          motivoEdicao: form.value.motivoEdicao.trim(),
          editadoPor: form.value.editadoPor.trim(),
          itens: form.value.itemEdicoes.map(item => ({
            itemPedidoId: item.itemPedidoId,
            precoUnitario: Number(item.precoUnitario),
            quantidade: Number(item.quantidade),
            observacao: item.observacao || null
          })),
          preco: form.value.preco,
          prazoEmDiasUteis: form.value.prazoEmDiasUteis,
          dataLimite: form.value.dataLimite,
          pdfFiles: form.value.pdfFiles
        }

        emit('save', dadosEdicao)
      } finally {
        salvando.value = false
      }
    }

    const handleFileUpload = (event) => {
      const files = Array.from(event.target.files || [])

      if (files.length === 0) return

      const arquivosValidos = []

      const { showError } = useErrorModal()
      const erros = []

      for (const file of files) {
        // Validar tipo
        if (file.type !== 'application/pdf') {
          erros.push(`${file.name}: não é um arquivo PDF válido`)
          continue
        }

        // Validar tamanho (10MB)
        if (file.size > 10 * 1024 * 1024) {
          erros.push(`${file.name}: excede o tamanho máximo de 10MB`)
          continue
        }

        arquivosValidos.push(file)
      }

      // Mostrar erros se houver
      if (erros.length > 0) {
        showError('Alguns arquivos não puderam ser adicionados', {
          title: 'Arquivos Inválidos',
          details: erros
        })
      }

      // Adicionar arquivos válidos à lista
      form.value.pdfFiles = [...form.value.pdfFiles, ...arquivosValidos]

      // Limpar input para permitir selecionar os mesmos arquivos novamente
      if (fileInput.value) {
        fileInput.value.value = ''
      }
    }

    const removerPdf = (index) => {
      form.value.pdfFiles.splice(index, 1)
    }

    const formatarTamanho = (bytes) => {
      if (bytes === 0) return '0 Bytes'
      const k = 1024
      const sizes = ['Bytes', 'KB', 'MB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
    }

    const formatarPreco = (valor) => {
      if (!valor && valor !== 0) return '0,00'
      return parseFloat(valor).toFixed(2).replace('.', ',')
    }

    const formatarData = (data) => {
      if (!data) return '-'
      const date = new Date(data + 'T00:00:00')
      return date.toLocaleDateString('pt-BR')
    }

    return {
      form,
      fileInput,
      salvando,
      dataMinima,
      formValido,
      totalItensEditados,
      precoFormatado,
      handlePrecoInput,
      formatarPrecoFinal,
      handleItemPrecoInput,
      formatarItemPrecoFinal,
      fechar,
      salvar,
      handleFileUpload,
      removerPdf,
      formatarTamanho,
      formatarPreco,
      formatarData
    }
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  padding: 20px;
}

.modal-container {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 700px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.modal-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1F285F;
  margin: 0;
}

.btn-close {
  padding: 4px;
  border: none;
  background: none;
  cursor: pointer;
  color: #6b7280;
  transition: all 0.2s;
  border-radius: 4px;
}

.btn-close:hover {
  background: #f3f4f6;
  color: #1f2937;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.info-atual {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
}

.info-titulo {
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 12px 0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 500;
}

.info-value {
  font-size: 0.875rem;
  color: #1f2937;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
  width: 100%;
}

.form-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.required {
  color: #ef4444;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
  font-family: inherit;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #1F285F;
  box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.form-hint {
  display: block;
  font-size: 0.75rem;
  color: #6b7280;
  margin-top: 4px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.itens-edicao-lista {
  display: grid;
  gap: 10px;
}

.item-edicao-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 12px;
  background: #f8fafc;
}

.item-edicao-header {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 8px;
}

.item-edicao-nome {
  font-weight: 600;
  color: #1f2937;
  font-size: 0.9rem;
}

.item-edicao-qtd {
  color: #64748b;
  font-size: 0.8rem;
}

.item-edicao-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-bottom: 8px;
}

.item-edicao-field {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-edicao-field label {
  font-size: 0.75rem;
  color: #64748b;
  font-weight: 600;
}

.total-edicao-itens {
  margin-top: 10px;
  font-size: 0.85rem;
  color: #1e293b;
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

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  flex-shrink: 0;
}

.btn-cancel {
  padding: 10px 20px;
  background: #fff;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-cancel:hover:not(:disabled) {
  background: #f9fafb;
  border-color: #9ca3af;
}

.btn-cancel:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-save {
  padding: 10px 24px;
  background: #1F285F;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-save:hover:not(:disabled) {
  background: #151d47;
  box-shadow: 0 2px 4px rgba(31, 40, 95, 0.2);
}

.btn-save:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Transitions */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .modal-container,
.modal-fade-leave-active .modal-container {
  transition: transform 0.3s ease;
}

.modal-fade-enter-from .modal-container,
.modal-fade-leave-to .modal-container {
  transform: scale(0.95);
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .info-grid {
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

  .modal-footer {
    flex-direction: column-reverse;
  }

  .btn-cancel,
  .btn-save {
    width: 100%;
  }
}
</style>
