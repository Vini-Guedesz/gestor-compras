<template>
  <Transition name="modal" appear>
    <div v-if="isVisible" class="modal-overlay" @click.self="$emit('close')">
      <div class="modal-container">
        <div class="modal-header">
          <h2 class="modal-title">{{ cotacao ? 'Editar Cotação' : 'Nova Cotação' }}</h2>
          <button @click="$emit('close')" class="close-button">&times;</button>
        </div>
        <div class="modal-body">
          <form>
            <!-- Dados da Cotação -->
            <div class="form-section">
              <h3 class="section-title">Dados da Cotação</h3>
              <div class="form-grid">
                <div class="form-group">
                  <label class="form-label">Fornecedor *</label>
                  <select v-model="formData.fornecedorId" class="form-select" required>
                    <option value="">Selecione um fornecedor...</option>
                    <option
                      v-for="fornecedor in fornecedoresDisponiveis"
                      :key="fornecedor.id"
                      :value="fornecedor.id"
                    >
                      {{ fornecedor.razaoSocial }} - {{ fornecedor.cnpj }}
                    </option>
                  </select>
                </div>

                <div class="form-group">
                  <label class="form-label">Item do Pedido *</label>
                  <select v-model="formData.itemPedidoId" class="form-select" required>
                    <option value="">Selecione um item...</option>
                    <option
                      v-for="item in itensDisponiveis"
                      :key="item.id"
                      :value="item.id"
                    >
                      {{ item.nome }} (Qtd: {{ item.quantidade }}) - {{ item.descricao }}
                    </option>
                  </select>
                  <small class="form-hint">TODO: Carregar itens do pedido do backend</small>
                </div>

                <div class="form-group">
                  <label class="form-label">Preço (R$) *</label>
                  <input
                    type="number"
                    v-model="formData.preco"
                    class="form-input"
                    required
                    min="0.01"
                    step="0.01"
                    placeholder="0,00"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Prazo de Entrega</label>
                  <input
                    type="date"
                    v-model="formData.prazoEntrega"
                    class="form-input"
                    :min="dataMinima"
                  />
                </div>
              </div>
            </div>

            <!-- Anexos e Documentos -->
            <div class="form-section">
              <h3 class="section-title">Anexo PDF</h3>
              <div class="form-group">
                <label class="form-label">Anexar Documento PDF</label>
                <input
                  ref="fileInput"
                  type="file"
                  accept=".pdf"
                  @change="handleFileSelect"
                  class="form-input"
                />
                <small class="form-hint">Máximo 5MB</small>
              </div>


            </div>
          </form>
        </div>

        <!-- Footer do Modal -->
        <div class="modal-footer">
          <button type="button" @click="$emit('close')" class="btn-secondary">
            Cancelar
          </button>
          <div class="footer-actions">
            <button
              type="button"
              @click.stop="handleSubmit"
              class="btn-primary"
              :disabled="carregando"
            >
              <span v-if="carregando" class="loading-spinner"></span>
              {{ cotacao ? 'Atualizar Cotação' : 'Criar Cotação' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import fornecedorService from '../services/fornecedorService.js'
import itemPedidoService from '../services/itemPedidoService.js'

// Props
const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  cotacao: {
    type: Object,
    default: null
  }
})

// Emits
const emit = defineEmits(['close', 'save'])

// Estado reativo
const carregando = ref(false)

// Dados do formulário - alinhado com CotacaoCreateDTO/CotacaoUpdateDTO
const formData = ref({
  fornecedorId: null,
  itemPedidoId: null,
  preco: null,
  prazoEntrega: null
})

// Lista de fornecedores e itens (será carregada do backend)
const fornecedoresDisponiveis = ref([])
const itensDisponiveis = ref([])

// Computadas
const dataMinima = computed(() => {
  const hoje = new Date()
  return hoje.toISOString().split('T')[0]
})

// Métodos
const handleFileSelect = (event) => {
  const file = event.target.files[0]
  if (file) {
    if (file.type !== 'application/pdf') {
      alert('Apenas arquivos PDF são aceitos')
      return
    }

    if (file.size > 5 * 1024 * 1024) { // 5MB
      alert('Arquivo muito grande. Máximo 5MB')
      return
    }

    // Aqui você pode processar o arquivo PDF
    // TODO: Implementar upload do arquivo para o servidor
  }
}

const handleSubmit = async () => {
  // Proteção contra execução múltipla
  if (carregando.value) {
    console.log('⚠️ Já está processando uma cotação, aguarde...')
    return
  }

  console.log('🚀 Iniciando criação de cotação...')
  console.log('📋 Dados do formulário:', formData.value)

  try {
    carregando.value = true

    // Validações dos dados obrigatórios
    if (!formData.value.fornecedorId) {
      alert('Por favor, selecione um fornecedor')
      return
    }

    if (!formData.value.itemPedidoId) {
      alert('Por favor, selecione um item do pedido')
      return
    }

    if (!formData.value.preco || formData.value.preco <= 0) {
      alert('Preço é obrigatório e deve ser maior que zero')
      return
    }

    // Preparar dados com tipos corretos para o backend
    const fornecedorIdInt = parseInt(formData.value.fornecedorId)
    const itemPedidoIdInt = parseInt(formData.value.itemPedidoId)
    const precoFloat = parseFloat(formData.value.preco)

    // Validar conversões
    if (isNaN(fornecedorIdInt) || fornecedorIdInt <= 0) {
      alert('Fornecedor inválido')
      return
    }

    if (isNaN(itemPedidoIdInt) || itemPedidoIdInt <= 0) {
      alert('Item do pedido inválido')
      return
    }

    if (isNaN(precoFloat) || precoFloat <= 0) {
      alert('Preço inválido')
      return
    }

    const dadosParaSalvar = {
      fornecedorId: fornecedorIdInt,           // Integer no backend
      itemPedidoId: itemPedidoIdInt,           // Long no backend
      preco: precoFloat,                       // BigDecimal no backend
      prazoEntrega: formData.value.prazoEntrega || null  // LocalDate no backend (nullable)
    }

    console.log('📤 Enviando dados para a view pai:', dadosParaSalvar)

    // Emitir os dados para a view pai processar
    emit('save', dadosParaSalvar)
    emit('close')

  } catch (error) {
    console.error('❌ Erro detalhado ao salvar cotação:', error)
    console.error('❌ Erro response:', error.response)
    console.error('❌ Erro message:', error.message)

    let mensagemErro = 'Erro ao salvar cotação. Tente novamente.'

    if (error.response) {
      // Erro da resposta HTTP
      mensagemErro = `Erro ${error.response.status}: ${error.response.data?.message || error.response.statusText}`
    } else if (error.request) {
      // Erro de rede/conexão
      mensagemErro = 'Erro de conexão. Verifique se o backend está rodando.'
    } else {
      // Erro interno
      mensagemErro = `Erro interno: ${error.message}`
    }

    alert(mensagemErro)
  } finally {
    carregando.value = false
  }
}

// Carregar dados ao montar o componente
onMounted(async () => {
  try {
    console.log('🔄 Carregando dados para o formulário de cotação...')

    // Carregar fornecedores - função correta é listarTodos()
    try {
      const fornecedores = await fornecedorService.listarTodos()
      fornecedoresDisponiveis.value = fornecedores || []
      console.log('✅ Fornecedores carregados:', fornecedores?.length || 0)

      if (!fornecedores || fornecedores.length === 0) {
        console.warn('⚠️ Nenhum fornecedor encontrado no sistema')
      }
    } catch (error) {
      console.error('❌ Erro ao carregar fornecedores:', error)
      fornecedoresDisponiveis.value = []
      alert('Erro ao carregar fornecedores. Verifique se o backend está funcionando.')
    }

    // Carregar itens de pedido disponíveis
    try {
      const itens = await itemPedidoService.listarTodos()
      itensDisponiveis.value = itens || []
      console.log('✅ Itens de pedido carregados:', itens?.length || 0)

      if (!itens || itens.length === 0) {
        console.warn('⚠️ Nenhum item de pedido encontrado no sistema')
      }
    } catch (error) {
      console.error('❌ Erro ao carregar itens de pedido:', error)
      itensDisponiveis.value = []
      alert('Erro ao carregar itens de pedido. Verifique se o backend está funcionando.')
    }

    // Se editando, preencher formulário
    if (props.cotacao) {
      formData.value.fornecedorId = props.cotacao.fornecedorId
      formData.value.itemPedidoId = props.cotacao.itemPedidoId
      formData.value.preco = props.cotacao.preco
      formData.value.prazoEntrega = props.cotacao.prazoEntrega
    }
  } catch (error) {
    console.error('❌ Erro geral ao carregar dados:', error)
  }
})

// Watchers para resetar form quando modal abre/fecha
watch(() => props.isVisible, (newVal) => {
  if (newVal && !props.cotacao) {
    // Resetar formulário quando abrindo modal para nova cotação
    formData.value = {
      fornecedorId: null,
      itemPedidoId: null,
      preco: null,
      prazoEntrega: null
    }
  }
})
</script>

<style scoped>
/* Modal Base */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
  overflow-y: auto;
}

.modal-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  width: 100%;
  max-width: 800px;
  max-height: calc(100vh - 40px);
  margin: 20px auto;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 24px;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.close-button:hover {
  background: #f3f4f6;
  color: #374151;
}

.modal-body {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
  border-radius: 0 0 12px 12px;
}

.footer-actions {
  display: flex;
  gap: 12px;
}

/* Form Sections */
.form-section {
  margin-bottom: 32px;
}

.form-subsection {
  margin-bottom: 24px;
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #111827;
  margin: 0 0 16px 0;
}

.subsection-title {
  font-size: 1rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 12px 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

/* Form Elements */
.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
}

.form-input,
.form-select,
.form-textarea {
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

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.field-help {
  font-size: 0.75rem;
  color: #6b7280;
  margin-top: 4px;
}

/* Checkboxes */
.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.checkbox-option {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.checkbox-option input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.checkbox-label {
  font-size: 0.875rem;
  color: #374151;
  cursor: pointer;
}

/* File Upload */
.file-upload-area {
  border: 2px dashed #d1d5db;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.file-upload-area:hover {
  border-color: #3b82f6;
  background: #f9fafb;
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.upload-icon {
  margin-bottom: 8px;
}

.upload-help {
  font-size: 0.75rem;
  color: #6b7280;
}

.anexos-lista {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.anexo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
}

.anexo-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.anexo-nome {
  font-weight: 500;
  color: #374151;
}

.anexo-tamanho {
  font-size: 0.75rem;
  color: #6b7280;
}

.btn-remove-anexo {
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.btn-remove-anexo:hover {
  background: #fee2e2;
  color: #dc2626;
}

/* Warning Messages */
.warning-message {
  background: #fef3c7;
  color: #d97706;
  padding: 12px;
  border-radius: 6px;
  border: 1px solid #fbbf24;
  font-size: 0.875rem;
  margin-top: 12px;
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
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add-item:hover {
  background: #2563eb;
}

.btn-primary {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-primary:hover {
  background: #2563eb;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  padding: 12px 24px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: #e5e7eb;
}

.btn-outline {
  background: white;
  color: #3b82f6;
  border: 1px solid #3b82f6;
  padding: 12px 24px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-outline:hover {
  background: #eff6ff;
}

.btn-add-first {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  padding: 12px 24px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add-first:hover {
  background: #e5e7eb;
}

/* Items */
.items-container {
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

.item-number {
  font-weight: 600;
  color: #374151;
}

.btn-remove-item {
  background: #fee2e2;
  color: #dc2626;
  border: none;
  padding: 6px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-remove-item:hover {
  background: #fecaca;
}

.item-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* Empty States */
.empty-items,
.empty-fornecedores {
  text-align: center;
  padding: 40px 20px;
  color: #6b7280;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

/* Fornecedores */
.fornecedores-lista {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.fornecedor-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.fornecedor-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1F285F, #4338ca);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.875rem;
}

.fornecedor-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.fornecedor-nome {
  font-weight: 600;
  color: #111827;
}

.fornecedor-cnpj {
  font-size: 0.75rem;
  color: #6b7280;
}

.btn-remove-fornecedor {
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.btn-remove-fornecedor:hover {
  background: #fee2e2;
  color: #dc2626;
}

/* Loading */
.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Modal Transition */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-container,
.modal-leave-to .modal-container {
  transform: translateY(-20px) scale(1.05);
  opacity: 0;
}

/* Responsividade */
@media (max-width: 768px) {
  .modal-overlay {
    padding: 10px;
  }

  .modal-container {
    margin: 10px auto;
    max-height: calc(100vh - 20px);
  }

  .modal-header,
  .modal-body,
  .modal-footer {
    padding: 16px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .section-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .modal-footer {
    flex-direction: column;
    gap: 12px;
  }

  .footer-actions {
    width: 100%;
    justify-content: space-between;
  }
}

.form-hint {
  color: #6b7280;
  font-size: 0.75rem;
  margin-top: 4px;
  font-style: italic;
}
</style>
