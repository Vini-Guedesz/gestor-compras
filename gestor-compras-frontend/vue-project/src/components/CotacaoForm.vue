<template>
  <Transition name="modal" appear>
    <div v-if="isVisible" class="modal-overlay" @click.self="$emit('close')">
      <div class="modal-container">
        <div class="modal-header">
          <h2 class="modal-title">{{ cotacao ? 'Editar Cotação' : 'Nova Cotação' }}</h2>
          <button @click="$emit('close')" class="close-button">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="handleSubmit">
            <!-- Dados Básicos da Cotação -->
            <div class="form-section">
              <h3 class="section-title">Dados Básicos da Cotação</h3>
              <div class="form-grid">
                <div class="form-group">
                  <label class="form-label">Número da Cotação *</label>
                  <input
                    type="text"
                    v-model="numeroCotacaoGerado"
                    class="form-input"
                    required
                    placeholder="Ex: COT-2025-001"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Descrição *</label>
                  <input
                    type="text"
                    v-model="formData.descricao"
                    class="form-input"
                    required
                    placeholder="Ex: Toners e cartuchos para impressoras HP"
                    minlength="10"
                    maxlength="200"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Data da Solicitação *</label>
                  <input
                    type="date"
                    v-model="formData.dataSolicitacao"
                    class="form-input"
                    required
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Prazo Limite para Resposta *</label>
                  <input
                    type="date"
                    v-model="formData.dataLimiteResposta"
                    class="form-input"
                    required
                    :min="dataMinima"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Status</label>
                  <select v-model="formData.status" class="form-select">
                    <option value="enviada">Enviada</option>
                    <option value="em-analise">Em Análise</option>
                    <option value="finalizada">Finalizada</option>
                  </select>
                </div>
              </div>
            </div>

            <!-- Formas de Pagamento (RF10, RF20, RF21, RF22) -->
            <div class="form-section">
              <h3 class="section-title">Formas de Pagamento</h3>

              <div class="form-group">
                <label class="form-label">Formas de Pagamento Aceitas *</label>
                <div class="checkbox-group">
                  <label v-for="forma in formasPagamentoOptions" :key="forma.value" class="checkbox-option">
                    <input
                      type="checkbox"
                      :value="forma.value"
                      v-model="formData.formasPagamentoAceitas"
                    />
                    <span class="checkbox-label">{{ forma.label }}</span>
                  </label>
                </div>
              </div>

              <div class="form-grid" style="margin-top: 24px;">
                <div class="form-group">
                  <label class="form-label">Condição de Pagamento *</label>
                  <select v-model="formData.condicoesPagamento.tipo" class="form-select" required>
                    <option value="">Selecione uma condição</option>
                    <option value="a_vista">À Vista</option>
                    <option value="15_dias">15 Dias</option>
                    <option value="30_dias">30 Dias</option>
                    <option value="45_dias">45 Dias</option>
                    <option value="60_dias">60 Dias</option>
                    <option value="parcelado">Parcelado</option>
                  </select>
                </div>

                <div v-if="formData.condicoesPagamento.tipo === 'parcelado'" class="form-group">
                  <label class="form-label">Número de Parcelas *</label>
                  <input
                    type="number"
                    v-model="formData.condicoesPagamento.numeroParcelas"
                    class="form-input"
                    required
                    min="2"
                    max="12"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Moeda *</label>
                  <select v-model="formData.moeda" class="form-select" required>
                    <option value="BRL">Real (R$)</option>
                    <option value="USD">Dólar (US$)</option>
                  </select>
                </div>
              </div>

              <div class="form-group" style="margin-top: 20px;">
                <label class="form-label">Observações sobre Pagamento</label>
                <textarea
                  v-model="formData.observacoesPagamento"
                  class="form-textarea"
                  rows="3"
                  maxlength="500"
                  placeholder="Condições específicas de pagamento, descontos, etc."
                  style="margin-top: 8px;"
                ></textarea>
              </div>
            </div>

            <!-- Itens da Cotação -->
            <div class="form-section">
              <div class="section-header">
                <h3 class="section-title">Itens da Cotação</h3>
                <button type="button" @click="adicionarItem" class="btn-add-item">
                  <svg viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" d="M19,13H13V19H11V13H5V11H11V5H13V11H19V13Z"/>
                  </svg>
                  Adicionar Item
                </button>
              </div>

              <div class="items-container">
                <div v-for="(item, index) in formData.itens" :key="index" class="item-card">
                  <div class="item-header">
                    <span class="item-number">Item {{ index + 1 }}</span>
                    <button type="button" @click="removerItem(index)" class="btn-remove-item">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M9,3V4H4V6H5V19A2,2 0 0,0 7,21H17A2,2 0 0,0 19,19V6H20V4H15V3H9M7,6H17V19H7V6M9,8V17H11V8H9M13,8V17H15V8H13Z"/>
                      </svg>
                    </button>
                  </div>

                  <div class="item-content">
                    <div class="form-grid">
                      <div class="form-group">
                        <label class="form-label">Descrição *</label>
                        <input
                          type="text"
                          v-model="item.descricao"
                          class="form-input"
                          required
                          placeholder="Ex: Cartucho HP 667XL Preto Original"
                          minlength="5"
                        />
                      </div>

                      <div class="form-group">
                        <label class="form-label">Quantidade *</label>
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
                        <label class="form-label">Unidade de Medida *</label>
                        <select v-model="item.unidadeMedida" class="form-select" required>
                          <option value="UN">Unidade (UN)</option>
                          <option value="CX">Caixa (CX)</option>
                          <option value="PCT">Pacote (PCT)</option>
                          <option value="KG">Quilograma (KG)</option>
                          <option value="L">Litro (L)</option>
                          <option value="M">Metro (M)</option>
                          <option value="M2">Metro² (M²)</option>
                          <option value="M3">Metro³ (M³)</option>
                        </select>
                      </div>
                    </div>

                    <div class="form-group">
                      <label class="form-label">Especificações Técnicas</label>
                      <textarea
                        v-model="item.especificacoesTecnicas"
                        class="form-textarea"
                        rows="2"
                        maxlength="500"
                        placeholder="Marca, modelo, características específicas..."
                      ></textarea>
                    </div>

                    <div class="form-group">
                      <label class="form-label">Observações do Item</label>
                      <textarea
                        v-model="item.observacoes"
                        class="form-textarea"
                        rows="2"
                        maxlength="200"
                        placeholder="Observações específicas deste item..."
                      ></textarea>
                    </div>
                  </div>
                </div>

                <div v-if="!formData.itens.length" class="empty-items">
                  <div class="empty-icon">📋</div>
                  <p>Nenhum item adicionado ainda</p>
                  <button type="button" @click="adicionarItem" class="btn-add-first">
                    Adicionar Primeiro Item
                  </button>
                </div>
              </div>
            </div>            <!-- Fornecedores -->
            <div class="form-section">
              <div class="section-header">
                <h3 class="section-title">Fornecedores Convidados (Mínimo 3)</h3>
              </div>

              <!-- Seleção de Fornecedores -->
              <div class="form-group">
                <label class="form-label">Selecionar Fornecedores *</label>
                <select
                  v-model="fornecedorSelecionado"
                  @change="adicionarFornecedor"
                  class="form-select"
                >
                  <option value="">Escolha um fornecedor...</option>
                  <option
                    v-for="fornecedor in fornecedoresDisponiveis.filter(f => !formData.fornecedores.find(invited => invited.id === f.id))"
                    :key="fornecedor.id"
                    :value="fornecedor"
                  >
                    {{ fornecedor.nomeFantasia }} - {{ fornecedor.cnpj }}
                  </option>
                </select>
              </div>

              <!-- Critérios de Seleção -->
              <div class="form-subsection">
                <h4 class="subsection-title">Critérios de Seleção</h4>
                <div class="form-grid">
                  <div class="form-group">
                    <label class="form-label">Categorias</label>
                    <div class="checkbox-group">
                      <label v-for="categoria in categoriasOptions" :key="categoria" class="checkbox-option">
                        <input
                          type="checkbox"
                          :value="categoria"
                          v-model="formData.criteriosSelecao.categorias"
                        />
                        <span class="checkbox-label">{{ categoria }}</span>
                      </label>
                    </div>
                  </div>
                </div>
              </div>

              <div class="fornecedores-lista">
                <div v-for="fornecedor in formData.fornecedores" :key="fornecedor.id" class="fornecedor-item">
                  <div class="fornecedor-avatar">
                    {{ fornecedor.nomeFantasia.charAt(0) }}
                  </div>
                  <div class="fornecedor-info">
                    <div class="fornecedor-nome">{{ fornecedor.nomeFantasia }}</div>
                    <div class="fornecedor-cnpj">{{ fornecedor.cnpj }}</div>
                  </div>
                  <button type="button" @click="removerFornecedor(fornecedor.id)" class="btn-remove-fornecedor">
                    <svg viewBox="0 0 24 24" width="16" height="16">
                      <path fill="currentColor" d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z"/>
                    </svg>
                  </button>
                </div>

                <div v-if="formData.fornecedores.length < 3" class="warning-message">
                  ⚠️ Selecione pelo menos 3 fornecedores para uma cotação válida
                </div>

                <div v-if="!formData.fornecedores.length" class="empty-fornecedores">
                  <div class="empty-icon">👥</div>
                  <p>Selecione fornecedores da lista acima</p>
                  <button type="button" @click="scrollToFornecedorSelect" class="btn-add-first">
                    Selecionar Fornecedores
                  </button>
                </div>
              </div>
            </div>

            <!-- Anexos e Documentos (RF14) -->
            <div class="form-section">
              <h3 class="section-title">Anexos e Documentos</h3>

              <div class="form-group">
                <label class="form-label">Anexar Documentos</label>
                <div class="file-upload-area" @click="$refs.fileInput.click()" @dragover.prevent @drop.prevent="handleFileDrop">
                  <input
                    ref="fileInput"
                    type="file"
                    multiple
                    accept=".pdf,.doc,.docx,.xls,.xlsx,.jpg,.png"
                    @change="handleFileSelect"
                    style="display: none"
                  />
                  <div class="upload-content">
                    <svg viewBox="0 0 24 24" width="48" height="48" class="upload-icon">
                      <path fill="#6b7280" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
                    </svg>
                    <p>Clique ou arraste arquivos aqui</p>
                    <p class="upload-help">PDF, DOC, XLS, JPG, PNG (máx. 5MB cada)</p>
                  </div>
                </div>

                <div v-if="formData.anexos.length" class="anexos-lista">
                  <div v-for="(anexo, index) in formData.anexos" :key="index" class="anexo-item">
                    <div class="anexo-info">
                      <span class="anexo-nome">{{ anexo.name }}</span>
                      <span class="anexo-tamanho">{{ formatarTamanhoArquivo(anexo.size) }}</span>
                    </div>
                    <button type="button" @click="removerAnexo(index)" class="btn-remove-anexo">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z"/>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Observações Gerais (RF13) -->
            <div class="form-section">
              <h3 class="section-title">Observações Gerais</h3>
              <div class="form-group">
                <label class="form-label">Observações Gerais da Cotação</label>
                <textarea
                  v-model="formData.observacoesGerais"
                  class="form-textarea"
                  rows="4"
                  maxlength="1000"
                  placeholder="Informações importantes sobre prazos, condições especiais, critérios de avaliação, etc."
                ></textarea>
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
            <button type="submit" @click="handleSubmit" class="btn-primary" :disabled="carregando">
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

// Dados do formulário
const formData = ref({
  // Dados Básicos
  numeroCotacao: '', // Gerado automaticamente
  descricao: '',
  dataSolicitacao: new Date().toISOString().split('T')[0],
  dataLimiteResposta: '',
  status: 'enviada',

  // Fornecedores (mínimo 3)
  fornecedores: [],
  criteriosSelecao: {
    categorias: []
  },

  // Itens da cotação
  itens: [],

  // Formas de Pagamento (RF10, RF20, RF21, RF22)
  formasPagamentoAceitas: [],
  condicoesPagamento: {
    tipo: '',
    numeroParcelas: null
  },
  moeda: 'BRL',

    // Observações e Especificações (RF13)
  observacoesGerais: '',
  observacoesPagamento: '',

  // Anexos (RF14)
  anexos: []
})

// Lista de fornecedores disponíveis (simulação)
const fornecedoresDisponiveis = ref([
  {
    id: 1,
    nomeFantasia: 'Papelaria Central',
    cnpj: '12.345.678/0001-90'
  },
  {
    id: 2,
    nomeFantasia: 'Office Max',
    cnpj: '98.765.432/0001-10'
  },
  {
    id: 3,
    nomeFantasia: 'Kalunga',
    cnpj: '11.222.333/0001-44'
  },
  {
    id: 4,
    nomeFantasia: 'TechSupply',
    cnpj: '11.222.333/0001-55'
  },
  {
    id: 5,
    nomeFantasia: 'PrintCenter',
    cnpj: '11.222.333/0001-66'
  }
])

// Opções para forms
const formasPagamentoOptions = ref([
  { value: 'boleto', label: 'Boleto Bancário' },
  { value: 'ted', label: 'TED' },
  { value: 'pix', label: 'PIX' },
  { value: 'cartao_credito', label: 'Cartão de Crédito' },
  { value: 'cartao_debito', label: 'Cartão de Débito' },
  { value: 'deposito', label: 'Depósito Bancário' },
  { value: 'cheque', label: 'Cheque' }
])

const categoriasOptions = ref([
  'Material de Escritório',
  'Equipamentos',
  'Serviços',
  'Consumíveis'
])

// Computadas
const numeroCotacaoGerado = computed(() => {
  const ano = new Date().getFullYear()
  const numero = String(Math.floor(Math.random() * 9999) + 1).padStart(4, '0')
  return `COT-${ano}-${numero}`
})

const dataMinima = computed(() => {
  const amanha = new Date()
  amanha.setDate(amanha.getDate() + 2) // Mínimo 2 dias no futuro
  return amanha.toISOString().split('T')[0]
})

// Métodos
const adicionarItem = () => {
  formData.value.itens.push({
    sequencia: formData.value.itens.length + 1,
    descricao: '',
    quantidade: 1,
    unidadeMedida: 'UN',
    especificacoesTecnicas: '',
    observacoes: ''
  })
}

const removerItem = (index) => {
  formData.value.itens.splice(index, 1)
  // Renumerar sequências
  formData.value.itens.forEach((item, idx) => {
    item.sequencia = idx + 1
  })
}

// Nova variável para seleção de fornecedor
const fornecedorSelecionado = ref('')

const adicionarFornecedor = () => {
  if (fornecedorSelecionado.value && typeof fornecedorSelecionado.value === 'object') {
    const fornecedor = fornecedorSelecionado.value
    const jaAdicionado = formData.value.fornecedores.find(f => f.id === fornecedor.id)

    if (!jaAdicionado) {
      formData.value.fornecedores.push(fornecedor)
    }

    // Limpar seleção
    fornecedorSelecionado.value = ''
  }
}

const scrollToFornecedorSelect = () => {
  // Scroll para o campo de seleção de fornecedores
  const elemento = document.querySelector('select[v-model="fornecedorSelecionado"]')
  if (elemento) {
    elemento.scrollIntoView({ behavior: 'smooth', block: 'center' })
    elemento.focus()
  }
}

const removerFornecedor = (fornecedorId) => {
  const index = formData.value.fornecedores.findIndex(f => f.id === fornecedorId)
  if (index > -1) {
    formData.value.fornecedores.splice(index, 1)
  }
}

const handleFileSelect = (event) => {
  const files = Array.from(event.target.files)
  adicionarAnexos(files)
}

const handleFileDrop = (event) => {
  const files = Array.from(event.dataTransfer.files)
  adicionarAnexos(files)
}

const adicionarAnexos = (files) => {
  const arquivosValidos = files.filter(file => {
    const tiposAceitos = ['.pdf', '.doc', '.docx', '.xls', '.xlsx', '.jpg', '.png']
    const extensao = '.' + file.name.split('.').pop().toLowerCase()
    const tamanhoMaximo = 5 * 1024 * 1024 // 5MB

    if (!tiposAceitos.includes(extensao)) {
      alert(`Arquivo ${file.name}: tipo não permitido`)
      return false
    }

    if (file.size > tamanhoMaximo) {
      alert(`Arquivo ${file.name}: tamanho máximo de 5MB excedido`)
      return false
    }

    return true
  })

  if (formData.value.anexos.length + arquivosValidos.length > 10) {
    alert('Máximo de 10 arquivos permitidos')
    return
  }

  formData.value.anexos.push(...arquivosValidos)
}

const removerAnexo = (index) => {
  formData.value.anexos.splice(index, 1)
}

const formatarTamanhoArquivo = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const handleSubmit = async () => {
  try {
    carregando.value = true

    // Validações dos requisitos RF09-RF14
    if (!formData.value.descricao.trim() || formData.value.descricao.length < 10) {
      alert('Descrição é obrigatória e deve ter pelo menos 10 caracteres')
      return
    }

    if (!formData.value.dataLimiteResposta) {
      alert('Data limite para resposta é obrigatória')
      return
    }

    // Validar data limite (mínimo 2 dias no futuro)
    const dataLimite = new Date(formData.value.dataLimiteResposta)
    const dataMinima = new Date()
    dataMinima.setDate(dataMinima.getDate() + 2)

    if (dataLimite < dataMinima) {
      alert('A data limite deve ser pelo menos 2 dias após hoje')
      return
    }

    if (!formData.value.itens.length) {
      alert('Adicione pelo menos um item à cotação')
      return
    }

    // Validar itens
    for (const item of formData.value.itens) {
      if (!item.descricao.trim() || item.descricao.length < 5) {
        alert(`Item ${item.sequencia}: Descrição deve ter pelo menos 5 caracteres`)
        return
      }
      if (!item.quantidade || item.quantidade < 1) {
        alert(`Item ${item.sequencia}: Quantidade deve ser maior que zero`)
        return
      }
    }

    if (formData.value.fornecedores.length < 3) {
      alert('Selecione pelo menos 3 fornecedores para uma cotação válida')
      return
    }

    if (!formData.value.formasPagamentoAceitas.length) {
      alert('Selecione pelo menos uma forma de pagamento')
      return
    }

    if (!formData.value.condicoesPagamento.tipo) {
      alert('Selecione uma condição de pagamento')
      return
    }

    if (formData.value.condicoesPagamento.tipo === 'parcelado' && !formData.value.condicoesPagamento.numeroParcelas) {
      alert('Número de parcelas é obrigatório para pagamento parcelado')
      return
    }

    // Simular salvamento
    await new Promise(resolve => setTimeout(resolve, 1000))

    console.log('Salvando cotação:', formData.value)

    emit('save', formData.value)
    emit('close')

  } catch (error) {
    console.error('Erro ao salvar cotação:', error)
    alert('Erro ao salvar cotação')
  } finally {
    carregando.value = false
  }
}

const preencherFormulario = () => {
  if (props.cotacao) {
    formData.value = { ...props.cotacao }
  } else {
    // Resetar formulário para nova cotação
    formData.value = {
      // Dados Básicos
      numeroCotacao: '',
      descricao: '',
      dataSolicitacao: new Date().toISOString().split('T')[0],
      dataLimiteResposta: '',
      status: 'enviada',

      // Fornecedores
      fornecedores: [],
      criteriosSelecao: {
        categorias: []
      },

      // Itens
      itens: [],

      // Formas de Pagamento
      formasPagamentoAceitas: [],
      condicoesPagamento: {
        tipo: '',
        numeroParcelas: null
      },
      moeda: 'BRL',

      // Observações e Especificações
      observacoesGerais: '',
      observacoesPagamento: '',

      // Anexos
      anexos: []
    }
  }
}

// Watchers
watch(() => props.isVisible, (newVal) => {
  if (newVal) {
    preencherFormulario()
  }
})

// Lifecycle
onMounted(() => {
  if (props.isVisible) {
    preencherFormulario()
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
</style>
