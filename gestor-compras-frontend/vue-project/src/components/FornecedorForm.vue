<template>
  <Transition name="modal" appear>
    <div v-if="isVisible" class="modal-overlay" @click.self="$emit('close')">
      <div class="modal-container">
        <div class="modal-header">
          <h2 class="modal-title">{{ fornecedor ? 'Editar Fornecedor' : 'Novo Fornecedor' }}</h2>
          <button @click="$emit('close')" class="close-button">&times;</button>
        </div>
        <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <!-- Tipo de Fornecedor -->
          <div class="form-section">
            <h3 class="section-title">Tipo de Fornecedor</h3>
            <div class="radio-group">
              <label class="radio-option">
                <input
                  type="radio"
                  v-model="formData.tipo"
                  value="produto"
                  @change="clearSecondaryFields"
                />
                <span class="radio-label">Fornecedor de Produto</span>
              </label>
              <label class="radio-option">
                <input
                  type="radio"
                  v-model="formData.tipo"
                  value="servico"
                  @change="clearSecondaryFields"
                />
                <span class="radio-label">Fornecedor de Serviço</span>
              </label>
            </div>
          </div>

          <!-- Dados Gerais -->
          <div class="form-section">
            <h3 class="section-title">Dados Gerais</h3>
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">Razão Social *</label>
                <input
                  type="text"
                  v-model="formData.razaoSocial"
                  class="form-input"
                  required
                  placeholder="Nome da empresa"
                />
              </div>

              <div class="form-group">
                <label class="form-label">CNPJ *</label>
                <input
                  type="text"
                  v-model="formData.cnpj"
                  class="form-input"
                  required
                  placeholder="00.000.000/0000-00"
                  @input="formatCNPJ"
                />
              </div>

              <div class="form-group">
                <label class="form-label">
                  {{ formData.tipo === 'produto' ? 'Inscrição Estadual' : 'Inscrição Municipal' }}
                </label>
                <input
                  type="text"
                  v-model="formData.inscricao"
                  class="form-input"
                  :placeholder="formData.tipo === 'produto' ? 'Inscrição Estadual' : 'Inscrição Municipal'"
                />
              </div>

              <div class="form-group">
                <label class="form-label">Status</label>
                <select v-model="formData.status" class="form-select">
                  <option value="ativo">Ativo</option>
                  <option value="inativo">Inativo</option>
                </select>
              </div>
            </div>
          </div>

          <!-- Dados de Contato -->
          <div class="form-section">
            <h3 class="section-title">Dados de Contato</h3>
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">E-mail *</label>
                <input
                  type="email"
                  v-model="formData.contato.email"
                  class="form-input"
                  required
                  placeholder="contato@empresa.com"
                />
              </div>

              <div class="form-group">
                <label class="form-label">Telefone *</label>
                <input
                  type="text"
                  v-model="formData.contato.numero"
                  class="form-input"
                  required
                  placeholder="(00) 00000-0000"
                  @input="formatTelefone"
                />
              </div>
            </div>
          </div>

          <!-- Endereço -->
          <div class="form-section">
            <h3 class="section-title">Endereço</h3>
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">CEP *</label>
                <input
                  type="text"
                  v-model="formData.endereco.cep"
                  class="form-input"
                  required
                  placeholder="00000-000"
                  @input="formatCEP"
                  @blur="buscarCEP"
                />
              </div>

              <div class="form-group">
                <label class="form-label">Estado *</label>
                <select v-model="formData.endereco.estado" class="form-select" required>
                  <option value="">Selecione</option>
                  <option v-for="estado in estados" :key="estado.sigla" :value="estado.sigla">
                    {{ estado.nome }}
                  </option>
                </select>
              </div>

              <div class="form-group">
                <label class="form-label">Cidade *</label>
                <input
                  type="text"
                  v-model="formData.endereco.cidade"
                  class="form-input"
                  required
                  placeholder="Nome da cidade"
                />
              </div>

              <div class="form-group">
                <label class="form-label">Bairro *</label>
                <input
                  type="text"
                  v-model="formData.endereco.bairro"
                  class="form-input"
                  required
                  placeholder="Nome do bairro"
                />
              </div>

              <div class="form-group">
                <label class="form-label">Rua *</label>
                <input
                  type="text"
                  v-model="formData.endereco.rua"
                  class="form-input"
                  required
                  placeholder="Nome da rua"
                />
              </div>

              <div class="form-group">
                <label class="form-label">Número *</label>
                <input
                  type="text"
                  v-model="formData.endereco.numero"
                  class="form-input"
                  required
                  placeholder="123"
                />
              </div>

              <div class="form-group full-width">
                <label class="form-label">Complemento</label>
                <input
                  type="text"
                  v-model="formData.endereco.complemento"
                  class="form-input"
                  placeholder="Apto, sala, andar..."
                />
              </div>
            </div>
          </div>

          <!-- Categorias e Formas de Pagamento -->
          <div class="form-section">
            <h3 class="section-title">Informações Comerciais</h3>
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">Categorias</label>
                <div class="checkbox-group">
                  <label v-for="categoria in categorias" :key="categoria" class="checkbox-option">
                    <input
                      type="checkbox"
                      v-model="formData.categorias"
                      :value="categoria"
                    />
                    <span class="checkbox-label">{{ categoria }}</span>
                  </label>
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">Formas de Pagamento</label>
                <div class="checkbox-group">
                  <label v-for="forma in formasPagamento" :key="forma" class="checkbox-option">
                    <input
                      type="checkbox"
                      v-model="formData.formasPagamento"
                      :value="forma"
                    />
                    <span class="checkbox-label">{{ forma }}</span>
                  </label>
                </div>
              </div>
            </div>
          </div>

          <!-- Dados Bancários -->
          <div class="form-section">
            <h3 class="section-title">Dados Bancários</h3>
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">Banco</label>
                <input
                  type="text"
                  v-model="formData.dadosBancarios.banco"
                  class="form-input"
                  placeholder="Nome do banco"
                />
              </div>

              <div class="form-group">
                <label class="form-label">Agência</label>
                <input
                  type="text"
                  v-model="formData.dadosBancarios.agencia"
                  class="form-input"
                  placeholder="0000"
                />
              </div>

              <div class="form-group">
                <label class="form-label">Conta</label>
                <input
                  type="text"
                  v-model="formData.dadosBancarios.conta"
                  class="form-input"
                  placeholder="00000-0"
                />
              </div>

              <div class="form-group">
                <label class="form-label">PIX</label>
                <input
                  type="text"
                  v-model="formData.dadosBancarios.pix"
                  class="form-input"
                  placeholder="Chave PIX"
                />
              </div>
            </div>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button @click="$emit('close')" class="btn-secondary" type="button">Cancelar</button>
        <button @click="handleSubmit" class="btn-primary" :disabled="!isFormValid">
          {{ fornecedor ? 'Atualizar' : 'Cadastrar' }}
        </button>
      </div>
    </div>
  </div>
  </Transition>
</template>

<script setup>
import { ref, computed, watch, onUnmounted } from 'vue'

const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  fornecedor: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'save'])

// Prevenir scroll do body quando modal está aberto
watch(() => props.isVisible, (newValue) => {
  if (newValue) {
    document.body.style.overflow = 'hidden'
    document.addEventListener('keydown', handleEscKey)
  } else {
    document.body.style.overflow = ''
    document.removeEventListener('keydown', handleEscKey)
  }
})

// Função para fechar modal com ESC
const handleEscKey = (event) => {
  if (event.key === 'Escape') {
    emit('close')
  }
}

// Limpar no unmount
onUnmounted(() => {
  document.body.style.overflow = ''
  document.removeEventListener('keydown', handleEscKey)
})

// Estados brasileiros
const estados = ref([
  { sigla: 'AC', nome: 'Acre' },
  { sigla: 'AL', nome: 'Alagoas' },
  { sigla: 'AP', nome: 'Amapá' },
  { sigla: 'AM', nome: 'Amazonas' },
  { sigla: 'BA', nome: 'Bahia' },
  { sigla: 'CE', nome: 'Ceará' },
  { sigla: 'DF', nome: 'Distrito Federal' },
  { sigla: 'ES', nome: 'Espírito Santo' },
  { sigla: 'GO', nome: 'Goiás' },
  { sigla: 'MA', nome: 'Maranhão' },
  { sigla: 'MT', nome: 'Mato Grosso' },
  { sigla: 'MS', nome: 'Mato Grosso do Sul' },
  { sigla: 'MG', nome: 'Minas Gerais' },
  { sigla: 'PA', nome: 'Pará' },
  { sigla: 'PB', nome: 'Paraíba' },
  { sigla: 'PR', nome: 'Paraná' },
  { sigla: 'PE', nome: 'Pernambuco' },
  { sigla: 'PI', nome: 'Piauí' },
  { sigla: 'RJ', nome: 'Rio de Janeiro' },
  { sigla: 'RN', nome: 'Rio Grande do Norte' },
  { sigla: 'RS', nome: 'Rio Grande do Sul' },
  { sigla: 'RO', nome: 'Rondônia' },
  { sigla: 'RR', nome: 'Roraima' },
  { sigla: 'SC', nome: 'Santa Catarina' },
  { sigla: 'SP', nome: 'São Paulo' },
  { sigla: 'SE', nome: 'Sergipe' },
  { sigla: 'TO', nome: 'Tocantins' }
])

// Categorias disponíveis
const categorias = ref([
  'Equipamentos de TI',
  'Material de Escritório',
  'Móveis e Decoração',
  'Serviços de Limpeza',
  'Manutenção Predial',
  'Consultoria',
  'Treinamento',
  'Segurança',
  'Alimentação',
  'Transporte'
])

// Formas de pagamento
const formasPagamento = ref([
  'À vista',
  'Boleto 30 dias',
  'Boleto 60 dias',
  'PIX',
  'Transferência bancária',
  'Cartão de crédito'
])

// Dados do formulário
const formData = ref({
  tipo: 'produto',
  razaoSocial: '',
  cnpj: '',
  inscricao: '',
  status: 'ativo',
  contato: {
    email: '',
    numero: ''
  },
  endereco: {
    cep: '',
    estado: '',
    cidade: '',
    bairro: '',
    rua: '',
    numero: '',
    complemento: ''
  },
  categorias: [],
  formasPagamento: [],
  dadosBancarios: {
    banco: '',
    agencia: '',
    conta: '',
    pix: ''
  }
})

// Validação do formulário
const isFormValid = computed(() => {
  return formData.value.razaoSocial &&
         formData.value.cnpj &&
         formData.value.contato.email &&
         formData.value.contato.numero &&
         formData.value.endereco.cep &&
         formData.value.endereco.estado &&
         formData.value.endereco.cidade &&
         formData.value.endereco.bairro &&
         formData.value.endereco.rua &&
         formData.value.endereco.numero
})

// Resetar formulário
const resetForm = () => {
  formData.value = {
    tipo: 'produto',
    razaoSocial: '',
    cnpj: '',
    inscricao: '',
    status: 'ativo',
    contato: {
      email: '',
      numero: ''
    },
    endereco: {
      cep: '',
      estado: '',
      cidade: '',
      bairro: '',
      rua: '',
      numero: '',
      complemento: ''
    },
    categorias: [],
    formasPagamento: [],
    dadosBancarios: {
      banco: '',
      agencia: '',
      conta: '',
      pix: ''
    }
  }
}

// Watchers para carregar dados do fornecedor editado
watch(() => props.fornecedor, (newFornecedor) => {
  if (newFornecedor) {
    loadFornecedorData(newFornecedor)
  } else {
    resetForm()
  }
}, { immediate: true })

// Funções de formatação
const formatCNPJ = (event) => {
  let value = event.target.value.replace(/\D/g, '')
  value = value.replace(/^(\d{2})(\d)/, '$1.$2')
  value = value.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
  value = value.replace(/\.(\d{3})(\d)/, '.$1/$2')
  value = value.replace(/(\d{4})(\d)/, '$1-$2')
  formData.value.cnpj = value
}

const formatTelefone = (event) => {
  let value = event.target.value.replace(/\D/g, '')
  value = value.replace(/^(\d{2})(\d)/, '($1) $2')
  value = value.replace(/(\d{4,5})(\d{4})$/, '$1-$2')
  formData.value.contato.numero = value
}

const formatCEP = (event) => {
  let value = event.target.value.replace(/\D/g, '')
  value = value.replace(/^(\d{5})(\d)/, '$1-$2')
  formData.value.endereco.cep = value
}

// Buscar CEP via API
const buscarCEP = async () => {
  const cep = formData.value.endereco.cep.replace(/\D/g, '')
  if (cep.length === 8) {
    try {
      const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`)
      const data = await response.json()

      if (!data.erro) {
        formData.value.endereco.rua = data.logradouro
        formData.value.endereco.bairro = data.bairro
        formData.value.endereco.cidade = data.localidade
        formData.value.endereco.estado = data.uf
      }
    } catch (error) {
      console.warn('Erro ao buscar CEP:', error)
    }
  }
}

// Limpar campos específicos ao mudar tipo
const clearSecondaryFields = () => {
  formData.value.inscricao = ''
}

// Carregar dados do fornecedor para edição
const loadFornecedorData = (fornecedor) => {
  formData.value.tipo = fornecedor.tipo || 'produto'
  formData.value.razaoSocial = fornecedor.razaoSocial || ''
  formData.value.cnpj = fornecedor.cnpj || ''
  formData.value.inscricao = fornecedor.inscricaoEstadual || fornecedor.inscricaoMunicipal || ''
  formData.value.status = fornecedor.status || 'ativo'

  if (fornecedor.contato) {
    formData.value.contato.email = fornecedor.contato.email || ''
    formData.value.contato.numero = fornecedor.contato.numero || ''
  }

  if (fornecedor.endereco) {
    formData.value.endereco = { ...fornecedor.endereco }
  }

  formData.value.categorias = fornecedor.categorias || []
  formData.value.formasPagamento = fornecedor.formasPagamento || []
  formData.value.dadosBancarios = fornecedor.dadosBancarios || {
    banco: '',
    agencia: '',
    conta: '',
    pix: ''
  }
}

// Submeter formulário
const handleSubmit = () => {
  if (!isFormValid.value) {
    return
  }

  // Preparar dados para envio conforme DTOs do backend
  const dadosParaEnvio = {
    razaoSocial: formData.value.razaoSocial,
    cnpj: formData.value.cnpj.replace(/\D/g, ''), // Remove formatação
    contato: {
      email: formData.value.contato.email,
      numero: formData.value.contato.numero.replace(/\D/g, '') // Remove formatação
    },
    endereco: {
      cep: formData.value.endereco.cep.replace(/\D/g, ''), // Remove formatação
      estado: formData.value.endereco.estado,
      cidade: formData.value.endereco.cidade,
      bairro: formData.value.endereco.bairro,
      rua: formData.value.endereco.rua,
      numero: formData.value.endereco.numero,
      complemento: formData.value.endereco.complemento || null
    }
  }

  // Adicionar campo específico conforme tipo
  if (formData.value.tipo === 'produto') {
    dadosParaEnvio.inscricaoEstadual = formData.value.inscricao || null
  } else {
    dadosParaEnvio.inscricaoMunicipal = formData.value.inscricao || null
  }

  // Adicionar dados extras (não obrigatórios para o backend atual)
  dadosParaEnvio.tipo = formData.value.tipo
  dadosParaEnvio.status = formData.value.status
  dadosParaEnvio.categorias = formData.value.categorias
  dadosParaEnvio.formasPagamento = formData.value.formasPagamento
  dadosParaEnvio.dadosBancarios = formData.value.dadosBancarios

  emit('save', dadosParaEnvio)
}
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
  border-radius: 12px;
  width: 95%;
  max-width: 900px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  background: #f8fafc;
  border-radius: 12px 12px 0 0;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #111827;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #9ca3af;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  transition: all 0.2s;
}

.close-button:hover {
  background: #e5e7eb;
  color: #374151;
}

.modal-body {
  padding: 24px;
  flex: 1;
  overflow-y: auto;
  min-height: 0; /* Permite que o flex item encolha */
}

.form-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #111827;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #e5e7eb;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group.full-width {
  grid-column: 1 / -1;
}

.form-label {
  font-weight: 500;
  color: #374151;
  margin-bottom: 4px;
  font-size: 0.875rem;
}

.form-input,
.form-select,
.form-textarea {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  transition: all 0.2s;
  background: white;
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

.radio-group {
  display: flex;
  gap: 24px;
  margin-top: 8px;
}

.radio-option {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 0.875rem;
}

.radio-option input[type="radio"] {
  margin: 0;
}

.radio-label {
  color: #374151;
  font-weight: 500;
}

.checkbox-group {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 8px;
  margin-top: 8px;
}

.checkbox-option {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 0.875rem;
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.2s;
}

.checkbox-option:hover {
  background: #f3f4f6;
}

.checkbox-option input[type="checkbox"] {
  margin: 0;
}

.checkbox-label {
  color: #374151;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e5e7eb;
  background: #f8fafc;
  border-radius: 0 0 12px 12px;
}

.btn-primary {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.875rem;
}

.btn-primary:hover:not(:disabled) {
  background: #2563eb;
  transform: translateY(-1px);
}

.btn-primary:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background: #e5e7eb;
  color: #374151;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.875rem;
}

.btn-secondary:hover {
  background: #d1d5db;
  transform: translateY(-1px);
}

/* Responsividade */
@media (max-width: 768px) {
  .modal-container {
    width: 100%;
    height: 100vh;
    max-height: 100vh;
    border-radius: 0;
  }

  .modal-header {
    border-radius: 0;
  }

  .modal-footer {
    border-radius: 0;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .radio-group {
    flex-direction: column;
    gap: 12px;
  }

  .checkbox-group {
    grid-template-columns: 1fr;
  }
}

/* Transições Vue */
.modal-enter-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.modal-leave-active {
  transition: all 0.2s ease-in;
}

.modal-enter-from {
  opacity: 0;
}

.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-container {
  transform: translateY(40px) scale(0.95);
  opacity: 0;
}

.modal-leave-to .modal-container {
  transform: translateY(-20px) scale(1.05);
  opacity: 0;
}
</style>
