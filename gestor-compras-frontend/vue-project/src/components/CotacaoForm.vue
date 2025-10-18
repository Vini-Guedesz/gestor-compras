<template>
  <Transition name="modal" appear>
    <div v-if="isVisible" class="modal-overlay" @click.self="$emit('close')">
      <div class="modal-container">
        <div class="modal-header">
          <h2 class="modal-title">{{ cotacao ? 'Editar Cotação' : 'Nova Cotação' }}</h2>
          <button @click="$emit('close')" class="close-button">&times;</button>
        </div>
        <div class="modal-body">
          <!-- Alertas -->
          <div v-if="mensagemAlerta" :class="['alert', tipoAlerta]">
            {{ mensagemAlerta }}
          </div>

          <form @submit.prevent="handleSubmit">
            <!-- Dados da Cotação -->
            <div class="form-section">
              <h3 class="section-title">Dados da Cotação</h3>
              <div class="form-grid">
                <!-- Tipo de Fornecedor -->
                <div class="form-group">
                  <label class="form-label">Tipo de Fornecedor *</label>
                  <select v-model="tipoFornecedor" class="form-select" required @change="handleTipoFornecedorChange">
                    <option value="">Selecione o tipo...</option>
                    <option value="PRODUTO">Fornecedor de Produto</option>
                    <option value="SERVICO">Fornecedor de Serviço</option>
                  </select>
                </div>

                <!-- Fornecedor (filtrado por tipo) -->
                <div class="form-group">
                  <label class="form-label">Fornecedor *</label>
                  <select v-model="formData.fornecedorId" class="form-select" required :disabled="!tipoFornecedor">
                    <option value="">{{ tipoFornecedor ? 'Selecione um fornecedor...' : 'Primeiro selecione o tipo' }}</option>
                    <option
                      v-for="fornecedor in fornecedoresFiltrados"
                      :key="fornecedor.id"
                      :value="fornecedor.id"
                    >
                      {{ fornecedor.razaoSocial }} - {{ fornecedor.cnpj }}
                      <span class="badge">{{ fornecedor.tipo }}</span>
                    </option>
                  </select>
                </div>

                <!-- Item do Pedido -->
                <div class="form-group">
                  <label class="form-label">Item do Pedido *</label>
                  <select v-model="formData.itemPedidoId" class="form-select" required>
                    <option value="">Selecione um item...</option>
                    <option
                      v-for="item in itensDisponiveis"
                      :key="item.id"
                      :value="item.id"
                    >
                      {{ item.nome || 'Item sem nome' }} (Qtd: {{ item.quantidade || 0 }})
                      {{ item.descricao ? ` - ${item.descricao}` : '' }}
                    </option>
                  </select>
                  <small class="form-hint">{{ itensDisponiveis.length }} itens disponíveis</small>
                </div>

                <!-- Preço -->
                <div class="form-group">
                  <label class="form-label">Preço (R$) *</label>
                  <input
                    type="number"
                    v-model.number="formData.preco"
                    class="form-input"
                    required
                    min="0.01"
                    step="0.01"
                    placeholder="0,00"
                  />
                </div>

                <!-- Prazo de Entrega -->
                <div class="form-group">
                  <label class="form-label">Prazo de Entrega (dias)</label>
                  <input
                    type="number"
                    v-model.number="formData.prazoEntrega"
                    class="form-input"
                    min="1"
                    placeholder="Ex: 30"
                  />
                  <small class="form-hint">
                    Prazo em dias corridos
                    <span v-if="formData.prazoEntrega && formData.prazoEntrega > 0" class="data-calculada">
                      (Entrega: {{ calcularDataEntrega(formData.prazoEntrega) }})
                    </span>
                  </small>
                </div>
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
              :disabled="carregando || !formularioValido"
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
const mensagemAlerta = ref('')
const tipoAlerta = ref('error') // 'error', 'success', 'warning'
const tipoFornecedor = ref('')

// Dados do formulário - alinhado com CotacaoCreateDTO/CotacaoUpdateDTO
const formData = ref({
  fornecedorId: null,
  itemPedidoId: null,
  preco: null,
  prazoEntrega: null
})

// Lista de fornecedores e itens
const fornecedoresDisponiveis = ref([])
const itensDisponiveis = ref([])

// Computadas
const fornecedoresFiltrados = computed(() => {
  if (!tipoFornecedor.value) return []
  return fornecedoresDisponiveis.value.filter(f => f.tipo === tipoFornecedor.value)
})

const formularioValido = computed(() => {
  return formData.value.fornecedorId &&
         formData.value.itemPedidoId &&
         formData.value.preco &&
         formData.value.preco > 0
})

// Métodos de UI
const mostrarAlerta = (mensagem, tipo = 'error') => {
  mensagemAlerta.value = mensagem
  tipoAlerta.value = tipo
  setTimeout(() => {
    mensagemAlerta.value = ''
  }, 5000)
}



// Converter data para número de dias a partir de hoje
const calcularDiasParaEntrega = (dataEntrega) => {
  if (!dataEntrega) return null

  const hoje = new Date()
  const entrega = new Date(dataEntrega)

  // Zerrar as horas para comparação correta
  hoje.setHours(0, 0, 0, 0)
  entrega.setHours(0, 0, 0, 0)

  const diffTime = entrega.getTime() - hoje.getTime()
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

  return diffDays > 0 ? diffDays : null
}

// Calcular data de entrega a partir de dias
const calcularDataEntrega = (dias) => {
  if (!dias || dias <= 0) return ''

  const hoje = new Date()
  const dataEntrega = new Date(hoje)
  dataEntrega.setDate(hoje.getDate() + parseInt(dias))

  return dataEntrega.toLocaleDateString('pt-BR')
}

// Métodos de fornecedor
const handleTipoFornecedorChange = () => {
  formData.value.fornecedorId = null // Reset fornecedor quando tipo muda
}



const handleSubmit = async () => {
  if (carregando.value) {
    console.log('⚠️ Já está processando uma cotação, aguarde...')
    return
  }

  try {
    carregando.value = true
    mensagemAlerta.value = ''

    // Validações do formulário
    if (!formularioValido.value) {
      mostrarAlerta('Por favor, preencha todos os campos obrigatórios', 'error')
      return
    }

    if (!tipoFornecedor.value) {
      mostrarAlerta('Por favor, selecione um tipo de fornecedor', 'error')
      return
    }

    console.log('🚀 Iniciando envio de cotação...')
    console.log('📋 Dados do formulário:', formData.value)

    // Preparar dados com tipos corretos para o backend
    // Converter prazo de entrega de dias para data
    let prazoEntregaData = null
    if (formData.value.prazoEntrega && formData.value.prazoEntrega > 0) {
      const hoje = new Date()
      const dataEntrega = new Date(hoje)
      dataEntrega.setDate(hoje.getDate() + parseInt(formData.value.prazoEntrega))
      prazoEntregaData = dataEntrega.toISOString().split('T')[0] // Formato YYYY-MM-DD

      console.log('📅 Conversão prazo:', {
        diasInformados: formData.value.prazoEntrega,
        hoje: hoje.toISOString().split('T')[0],
        dataEntrega: prazoEntregaData
      })
    }

    const dadosParaSalvar = {
      fornecedorId: parseInt(formData.value.fornecedorId),
      itemPedidoId: parseInt(formData.value.itemPedidoId),
      preco: parseFloat(formData.value.preco),
      prazoEntrega: prazoEntregaData
    }

    // Validar conversões
    if (isNaN(dadosParaSalvar.fornecedorId) || dadosParaSalvar.fornecedorId <= 0) {
      mostrarAlerta('Fornecedor inválido', 'error')
      return
    }

    if (isNaN(dadosParaSalvar.itemPedidoId) || dadosParaSalvar.itemPedidoId <= 0) {
      mostrarAlerta('Item do pedido inválido', 'error')
      return
    }

    if (isNaN(dadosParaSalvar.preco) || dadosParaSalvar.preco <= 0) {
      mostrarAlerta('Preço inválido', 'error')
      return
    }

    console.log(' Enviando dados para a view pai:', dadosParaSalvar)

    // Emitir os dados para a view pai processar
    emit('save', dadosParaSalvar)

    mostrarAlerta('Cotação enviada com sucesso!', 'success')

    // Aguardar um pouco para mostrar a mensagem antes de fechar
    setTimeout(() => {
      emit('close')
    }, 1000)

  } catch (error) {
    console.error('❌ Erro ao processar cotação:', error)
    mostrarAlerta('Erro interno ao processar cotação', 'error')
  } finally {
    carregando.value = false
  }
}

// Carregar dados
const carregarFornecedores = async () => {
  try {
    console.log('🔄 Carregando fornecedores para cotação...')
    const fornecedores = await fornecedorService.listarParaCotacao()
    fornecedoresDisponiveis.value = fornecedores || []
    console.log('✅ Fornecedores carregados:', fornecedores?.length || 0)

    if (!fornecedores || fornecedores.length === 0) {
      mostrarAlerta('Nenhum fornecedor encontrado no sistema', 'warning')
    }
  } catch (error) {
    console.error('❌ Erro ao carregar fornecedores:', error)
    fornecedoresDisponiveis.value = []
    mostrarAlerta('Erro ao carregar fornecedores. Verifique a conexão.', 'error')
  }
}

const carregarItens = async () => {
  try {
    console.log('🔄 Carregando itens de pedido...')
    const itens = await itemPedidoService.listarTodos()
    itensDisponiveis.value = itens || []
    console.log('✅ Itens carregados:', itens?.length || 0)

    if (!itens || itens.length === 0) {
      mostrarAlerta('Nenhum item de pedido encontrado', 'warning')
    }
  } catch (error) {
    console.error('❌ Erro ao carregar itens:', error)
    itensDisponiveis.value = []
    mostrarAlerta('Erro ao carregar itens. Verifique a conexão.', 'error')
  }
}

const inicializarFormulario = () => {
  if (props.cotacao) {
    // Editando cotação existente - converter data de volta para dias
    const diasParaEntrega = calcularDiasParaEntrega(props.cotacao.prazoEntrega)

    formData.value = {
      fornecedorId: props.cotacao.fornecedorId,
      itemPedidoId: props.cotacao.itemPedidoId,
      preco: props.cotacao.preco,
      prazoEntrega: diasParaEntrega
    }

    console.log('📝 Editando cotação - Prazo original:', props.cotacao.prazoEntrega, 'Convertido para dias:', diasParaEntrega)
    // TODO: Determinar tipo de fornecedor baseado no fornecedorId
  } else {
    // Nova cotação
    formData.value = {
      fornecedorId: null,
      itemPedidoId: null,
      preco: null,
      prazoEntrega: null
    }
    tipoFornecedor.value = ''
  }
}

// Lifecycle
onMounted(async () => {
  console.log('🔄 Inicializando formulário de cotação...')
  await Promise.all([carregarFornecedores(), carregarItens()])
  inicializarFormulario()
})

// Watchers
watch(() => props.isVisible, (newVal) => {
  if (newVal) {
    inicializarFormulario()
    mensagemAlerta.value = ''
  }
})

watch(() => props.cotacao, () => {
  if (props.isVisible) {
    inicializarFormulario()
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

/* Alertas */
.alert {
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  font-size: 0.875rem;
  font-weight: 500;
}

.alert.error {
  background-color: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.alert.success {
  background-color: #f0f9ff;
  color: #059669;
  border: 1px solid #a7f3d0;
}

.alert.warning {
  background-color: #fffbeb;
  color: #d97706;
  border: 1px solid #fed7aa;
}

/* Badge para tipo de fornecedor */
.badge {
  display: inline-block;
  padding: 2px 6px;
  background: #e5e7eb;
  color: #374151;
  font-size: 0.75rem;
  border-radius: 4px;
  margin-left: 8px;
}



.form-hint {
  color: #6b7280;
  font-size: 0.75rem;
  margin-top: 4px;
  font-style: italic;
}

.data-calculada {
  color: #059669;
  font-weight: 500;
  font-style: normal;
}
</style>
