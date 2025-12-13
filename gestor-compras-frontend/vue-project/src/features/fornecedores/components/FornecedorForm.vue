<template>
  <Transition name="modal" appear>
    <div v-if="isVisible" class="modal-overlay" @click.self="$emit('close')">
      <div class="modal-container">
        <div class="modal-header">
          <div class="modal-header-content">
            <h2 class="modal-title">{{ fornecedor ? 'Editar Fornecedor' : 'Novo Fornecedor' }}</h2>
            <p class="modal-subtitle">
              {{ fornecedor ? 'Atualize as informações cadastrais do fornecedor' : 'Preencha os dados para cadastrar um novo fornecedor' }}
            </p>
          </div>
          <button @click="$emit('close')" class="close-button">&times;</button>
        </div>
        <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <!-- Tipo de Fornecedor -->
          <div class="form-section">
            <h3 class="section-title">
              <svg viewBox="0 0 24 24" width="20" height="20" style="margin-right: 8px;">
                <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
              Tipo de Fornecedor
            </h3>
            <div class="radio-group-cards">
              <label class="radio-card" :class="{ active: formData.tipo === 'produto' }">
                <input
                  type="radio"
                  v-model="formData.tipo"
                  value="produto"
                  @change="clearSecondaryFields"
                />
                <div class="radio-card-content">
                  <svg viewBox="0 0 24 24" width="24" height="24" class="radio-card-icon">
                    <path fill="currentColor" d="M20,8H4V6H20M20,18H4V12H20M20,4H4C2.89,4 2,4.89 2,6V18A2,2 0 0,0 4,20H20A2,2 0 0,0 22,18V6C22,5.11 21.1,4 20,4Z"/>
                  </svg>
                  <div>
                    <span class="radio-card-label">Fornecedor de Produto</span>
                    <span class="radio-card-description">Fornece produtos físicos e materiais</span>
                  </div>
                </div>
              </label>
              <label class="radio-card" :class="{ active: formData.tipo === 'servico' }">
                <input
                  type="radio"
                  v-model="formData.tipo"
                  value="servico"
                  @change="clearSecondaryFields"
                />
                <div class="radio-card-content">
                  <svg viewBox="0 0 24 24" width="24" height="24" class="radio-card-icon">
                    <path fill="currentColor" d="M12,3C10.73,3 9.6,3.8 9.18,5H3V7H4.95L2,14C1.53,16 3,17 5.5,17C8,17 9.56,16 9,14L6.05,7H9.17C9.5,7.85 10.15,8.5 11,8.83V20H2V22H22V20H13V8.82C13.85,8.5 14.5,7.85 14.82,7H17.95L15,14C14.53,16 16,17 18.5,17C21,17 22.56,16 22,14L19.05,7H21V5H14.83C14.4,3.8 13.27,3 12,3M12,5A1,1 0 0,1 13,6A1,1 0 0,1 12,7A1,1 0 0,1 11,6A1,1 0 0,1 12,5M5.5,10.25L7,14.5C7,14.5 6.5,15 5.5,15C4.5,15 4,14.5 4,14.5L5.5,10.25M18.5,10.25L20,14.5C20,14.5 19.5,15 18.5,15C17.5,15 17,14.5 17,14.5L18.5,10.25Z"/>
                  </svg>
                  <div>
                    <span class="radio-card-label">Fornecedor de Serviço</span>
                    <span class="radio-card-description">Presta serviços e mão de obra</span>
                  </div>
                </div>
              </label>
            </div>
          </div>

          <!-- Dados Gerais -->
          <div class="form-section">
            <h3 class="section-title">
              <svg viewBox="0 0 24 24" width="20" height="20" style="margin-right: 8px;">
                <path fill="currentColor" d="M14,17H7V15H14M17,13H7V11H17M17,9H7V7H17M19,3H5C3.89,3 3,3.89 3,5V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V5C21,4.89 20.1,4 19,4Z"/>
              </svg>
              Dados Gerais
            </h3>
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">Razão Social *</label>
                <input
                  type="text"
                  v-model="formData.razaoSocial"
                  @blur="handleFieldBlur('razaoSocial')"
                  class="form-input"
                  :class="{ 'invalid-field': fieldTouched.razaoSocial && fieldErrors.razaoSocial }"
                  required
                  placeholder="Nome da empresa"
                  maxlength="255"
                />
                <div v-if="fieldTouched.razaoSocial && fieldErrors.razaoSocial" class="error-message">
                  {{ fieldErrors.razaoSocial }}
                </div>
              </div>

                            <div class="form-group">
                <label class="form-label">CNPJ (Pessoa Jurídica) *</label>
                <input
                  type="text"
                  v-model="formData.cnpj"
                  @input="formatCNPJ"
                  @blur="handleFieldBlur('cnpj')"
                  class="form-input"
                  :class="{ 'invalid-field': fieldTouched.cnpj && fieldErrors.cnpj }"
                  required
                  placeholder="00.000.000/0000-00"
                  maxlength="18"
                />
                <div v-if="fieldTouched.cnpj && fieldErrors.cnpj" class="error-message">
                  {{ fieldErrors.cnpj }}
                </div>
              </div>

                            <div class="form-group">
                <label class="form-label">
                  {{ formData.tipo === 'produto' ? 'Inscrição Estadual' : 'Inscrição Municipal' }}
                </label>
                <input
                  type="text"
                  v-model="formData.inscricao"
                  class="form-input"
                  :placeholder="formData.tipo === 'produto' ? 'Inscrição estadual (opcional)' : 'Inscrição municipal (opcional)'"
                  maxlength="255"
                />
              </div>


            </div>
          </div>

          <!-- Dados de Contato -->
          <div class="form-section">
            <h3 class="section-title">
              <svg viewBox="0 0 24 24" width="20" height="20" style="margin-right: 8px;">
                <path fill="currentColor" d="M6.62,10.79C8.06,13.62 10.38,15.94 13.21,17.38L15.41,15.18C15.69,14.9 16.08,14.82 16.43,14.93C17.55,15.3 18.75,15.5 20,15.5A1,1 0 0,1 21,16.5V20A1,1 0 0,1 20,21A17,17 0 0,1 3,4A1,1 0 0,1 4,3H7.5A1,1 0 0,1 8.5,4C8.5,5.25 8.7,6.45 9.07,7.57C9.18,7.92 9.1,8.31 8.82,8.59L6.62,10.79Z"/>
              </svg>
              Dados de Contato
            </h3>
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">E-mail *</label>
                <input
                  type="email"
                  v-model="formData.contato.email"
                  @blur="handleFieldBlur('email')"
                  class="form-input"
                  :class="{ 'invalid-field': fieldTouched.email && fieldErrors.email }"
                  required
                  placeholder="contato@empresa.com"
                  maxlength="100"
                />
                <div v-if="fieldTouched.email && fieldErrors.email" class="error-message">
                  {{ fieldErrors.email }}
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">Telefone Fixo</label>
                <input
                  type="text"
                  v-model="formData.contato.telefoneFixo"
                  @input="formatTelefoneFixo"
                  @blur="handleFieldBlur('telefoneFixo')"
                  class="form-input"
                  :class="{ 'invalid-field': fieldTouched.telefoneFixo && fieldErrors.telefoneFixo }"
                  placeholder="(00) 0000-0000"
                  maxlength="14"
                />
                <div v-if="fieldTouched.telefoneFixo && fieldErrors.telefoneFixo" class="error-message">
                  {{ fieldErrors.telefoneFixo }}
                </div>
                <div v-if="!fieldErrors.telefoneFixo" class="form-hint">
                  Opcional - Formato: (00) 0000-0000
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">Celular</label>
                <input
                  type="text"
                  v-model="formData.contato.celular"
                  @input="formatCelular"
                  @blur="handleFieldBlur('celular')"
                  class="form-input"
                  :class="{ 'invalid-field': fieldTouched.celular && fieldErrors.celular }"
                  placeholder="(00) 00000-0000"
                  maxlength="15"
                />
                <div v-if="fieldTouched.celular && fieldErrors.celular" class="error-message">
                  {{ fieldErrors.celular }}
                </div>
                <div v-if="!fieldErrors.celular" class="form-hint">
                  Opcional - Formato: (00) 00000-0000
                </div>
              </div>
            </div>
          </div>

          <!-- Endereço -->
          <div class="form-section">
            <h3 class="section-title">
              <svg viewBox="0 0 24 24" width="20" height="20" style="margin-right: 8px;">
                <path fill="currentColor" d="M12,11.5A2.5,2.5 0 0,1 9.5,9A2.5,2.5 0 0,1 12,6.5A2.5,2.5 0 0,1 14.5,9A2.5,2.5 0 0,1 12,11.5M12,2A7,7 0 0,0 5,9C5,14.25 12,22 12,22C12,22 19,14.25 19,9A7,7 0 0,0 12,2Z"/>
              </svg>
              Endereço
            </h3>
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">CEP *</label>
                <input
                  type="text"
                  v-model="formData.endereco.cep"
                  @input="formatCEP"
                  @blur="buscarCEPComValidacao"
                  class="form-input"
                  :class="{ 'invalid-field': fieldTouched.cep && fieldErrors.cep }"
                  required
                  placeholder="00000-000"
                  maxlength="9"
                />
                <div v-if="fieldTouched.cep && fieldErrors.cep" class="error-message">
                  {{ fieldErrors.cep }}
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">Estado *</label>
                <select
                  v-model="formData.endereco.estado"
                  @blur="handleFieldBlur('estado')"
                  class="form-select"
                  :class="{ 'invalid-field': fieldTouched.estado && fieldErrors.estado }"
                  required
                >
                  <option value="">Selecione</option>
                  <option v-for="estado in estados" :key="estado.sigla" :value="estado.sigla">
                    {{ estado.nome }}
                  </option>
                </select>
                <div v-if="fieldTouched.estado && fieldErrors.estado" class="error-message">
                  {{ fieldErrors.estado }}
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">Cidade *</label>
                <input
                  type="text"
                  v-model="formData.endereco.cidade"
                  @blur="handleFieldBlur('cidade')"
                  class="form-input"
                  :class="{ 'invalid-field': fieldTouched.cidade && fieldErrors.cidade }"
                  required
                  placeholder="Nome da cidade"
                  maxlength="50"
                />
                <div v-if="fieldTouched.cidade && fieldErrors.cidade" class="error-message">
                  {{ fieldErrors.cidade }}
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">Bairro *</label>
                <input
                  type="text"
                  v-model="formData.endereco.bairro"
                  @blur="handleFieldBlur('bairro')"
                  class="form-input"
                  :class="{ 'invalid-field': fieldTouched.bairro && fieldErrors.bairro }"
                  required
                  placeholder="Nome do bairro"
                  maxlength="60"
                />
                <div v-if="fieldTouched.bairro && fieldErrors.bairro" class="error-message">
                  {{ fieldErrors.bairro }}
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">Rua *</label>
                <input
                  type="text"
                  v-model="formData.endereco.rua"
                  @blur="handleFieldBlur('rua')"
                  class="form-input"
                  :class="{ 'invalid-field': fieldTouched.rua && fieldErrors.rua }"
                  required
                  placeholder="Nome da rua"
                  maxlength="100"
                />
                <div v-if="fieldTouched.rua && fieldErrors.rua" class="error-message">
                  {{ fieldErrors.rua }}
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">Número *</label>
                <input
                  type="text"
                  v-model="formData.endereco.numero"
                  @blur="handleFieldBlur('numero')"
                  class="form-input"
                  :class="{ 'invalid-field': fieldTouched.numero && fieldErrors.numero }"
                  required
                  placeholder="123"
                  maxlength="10"
                />
                <div v-if="fieldTouched.numero && fieldErrors.numero" class="error-message">
                  {{ fieldErrors.numero }}
                </div>
              </div>

              <div class="form-group full-width">
                <label class="form-label">Complemento</label>
                <input
                  type="text"
                  v-model="formData.endereco.complemento"
                  class="form-input"
                  placeholder="Apto, sala, andar... (opcional)"
                  maxlength="100"
                />
                <div class="form-hint">
                  Campo opcional para informações adicionais do endereço
                </div>
              </div>
            </div>
          </div>


        </form>
      </div>
      <div class="modal-footer">
        <button @click="$emit('close')" class="btn-secondary" type="button">Cancelar</button>
        <button @click="handleSubmit" class="btn-primary" :disabled="!isFormValid" type="button">
          {{ fornecedor ? 'Atualizar' : 'Cadastrar' }}
        </button>
      </div>
    </div>
  </div>
  </Transition>
</template>

<script setup>
import { ref, computed, watch, onUnmounted } from 'vue'
import { useToast } from '@/composables/useToast.js'
import logger from '@/utils/logger.js'

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

const { success, error: showError, warning } = useToast()

// Prevenir scroll do body quando modal está aberto
watch(() => props.isVisible, (newValue) => {
  if (newValue) {
    document.body.style.overflow = 'hidden'
    document.addEventListener('keydown', handleEscKey)

    // Resetar formulário quando abrir sem fornecedor (modo criação)
    if (!props.fornecedor) {
      resetForm()
      // Resetar também os estados de validação
      Object.keys(fieldTouched.value).forEach(key => {
        fieldTouched.value[key] = false
      })
      Object.keys(fieldErrors.value).forEach(key => {
        fieldErrors.value[key] = ''
      })
    }
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



// ============================================
// DADOS DO FORMULÁRIO
// Estrutura baseada nos DTOs do backend
// ============================================
const formData = ref({
  tipo: 'produto', // 'produto' ou 'servico' - apenas para controle do frontend

  // Campos principais
  razaoSocial: '',
  cnpj: '',
  inscricao: '', // inscricaoEstadual (produto) ou inscricaoMunicipal (serviço)

  // Objeto CONTATO (ContatoCreateDTO ou ContatoUpdateDTO)
  contato: {
    id: null,           // ID - obrigatório para update
    email: '',           // OBRIGATÓRIO
    telefoneFixo: '',    // OPCIONAL - 10 ou 11 dígitos
    celular: ''          // OPCIONAL - 11 dígitos (deve começar com 9 após DDD)
  },

  // Objeto ENDERECO (EnderecoCreateDTO ou EnderecoUpdateDTO)
  endereco: {
    id: null,           // ID - obrigatório para update
    cep: '',            // OBRIGATÓRIO - 8 dígitos
    estado: '',         // OBRIGATÓRIO - sigla UF
    cidade: '',         // OBRIGATÓRIO
    bairro: '',         // OBRIGATÓRIO
    rua: '',            // OBRIGATÓRIO
    numero: '',         // OBRIGATÓRIO - número do endereço
    complemento: ''     // OPCIONAL
  }
})

// ============================================
// SISTEMA DE VALIDAÇÃO EM TEMPO REAL
// ============================================

// Estados de validação para feedback visual
const fieldErrors = ref({
  razaoSocial: '',
  cnpj: '',
  email: '',
  telefoneFixo: '',
  celular: '',
  cep: '',
  estado: '',
  cidade: '',
  bairro: '',
  rua: '',
  numero: ''
})

const fieldTouched = ref({
  razaoSocial: false,
  cnpj: false,
  email: false,
  telefoneFixo: false,
  celular: false,
  cep: false,
  estado: false,
  cidade: false,
  bairro: false,
  rua: false,
  numero: false
})

// Funções de validação individuais
const validateRazaoSocial = () => {
  if (!formData.value.razaoSocial.trim()) {
    fieldErrors.value.razaoSocial = 'A razão social é obrigatória'
    return false
  }
  fieldErrors.value.razaoSocial = ''
  return true
}

const validateCNPJ = () => {
  const cnpj = formData.value.cnpj.replace(/\D/g, '')
  if (!cnpj) {
    fieldErrors.value.cnpj = 'O CNPJ é obrigatório'
    return false
  }
  if (cnpj.length !== 14) {
    fieldErrors.value.cnpj = 'CNPJ deve ter 14 dígitos'
    return false
  }
  // Validação básica de CNPJ
  if (!isValidCNPJ(cnpj)) {
    fieldErrors.value.cnpj = 'CNPJ inválido'
    return false
  }
  fieldErrors.value.cnpj = ''
  return true
}

const validateEmail = () => {
  if (!formData.value.contato.email.trim()) {
    fieldErrors.value.email = 'O e-mail é obrigatório'
    return false
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(formData.value.contato.email)) {
    fieldErrors.value.email = 'E-mail inválido'
    return false
  }
  fieldErrors.value.email = ''
  return true
}

const validateTelefoneFixo = () => {
  const telefone = formData.value.contato.telefoneFixo.replace(/\D/g, '')
  if (telefone && telefone.length !== 10) {
    fieldErrors.value.telefoneFixo = 'Telefone fixo deve ter 10 dígitos: (00) 0000-0000'
    return false
  }
  fieldErrors.value.telefoneFixo = ''
  return true
}

const validateCelular = () => {
  const celular = formData.value.contato.celular.replace(/\D/g, '')
  if (celular && celular.length !== 11) {
    fieldErrors.value.celular = 'Celular deve ter 11 dígitos: (00) 00000-0000'
    return false
  }
  if (celular && celular.length === 11 && celular[2] !== '9') {
    fieldErrors.value.celular = 'Celular deve começar com 9 após o DDD'
    return false
  }
  fieldErrors.value.celular = ''
  return true
}

const validateCEP = () => {
  const cep = formData.value.endereco.cep.replace(/\D/g, '')
  if (!cep) {
    fieldErrors.value.cep = 'O CEP é obrigatório'
    return false
  }
  if (cep.length !== 8) {
    fieldErrors.value.cep = 'CEP deve ter 8 dígitos'
    return false
  }
  fieldErrors.value.cep = ''
  return true
}

const validateEstado = () => {
  if (!formData.value.endereco.estado) {
    fieldErrors.value.estado = 'O estado é obrigatório'
    return false
  }
  fieldErrors.value.estado = ''
  return true
}

const validateCidade = () => {
  if (!formData.value.endereco.cidade.trim()) {
    fieldErrors.value.cidade = 'A cidade é obrigatória'
    return false
  }
  fieldErrors.value.cidade = ''
  return true
}

const validateBairro = () => {
  if (!formData.value.endereco.bairro.trim()) {
    fieldErrors.value.bairro = 'O bairro é obrigatório'
    return false
  }
  fieldErrors.value.bairro = ''
  return true
}

const validateRua = () => {
  if (!formData.value.endereco.rua.trim()) {
    fieldErrors.value.rua = 'A rua é obrigatória'
    return false
  }
  fieldErrors.value.rua = ''
  return true
}

const validateNumero = () => {
  if (!formData.value.endereco.numero.trim()) {
    fieldErrors.value.numero = 'O número é obrigatório'
    return false
  }
  fieldErrors.value.numero = ''
  return true
}

// Função para validar CNPJ (algoritmo simplificado)
const isValidCNPJ = (cnpj) => {
  // Remove caracteres não numéricos
  cnpj = cnpj.replace(/\D/g, '')

  // Verifica se tem 14 dígitos
  if (cnpj.length !== 14) return false

  // Verifica se todos os dígitos são iguais
  if (/^(\d)\1+$/.test(cnpj)) return false

  return true // Validação básica - pode ser expandida
}

// Handlers para validação em tempo real
const handleFieldBlur = (fieldName) => {
  fieldTouched.value[fieldName] = true

  switch (fieldName) {
    case 'razaoSocial': validateRazaoSocial(); break
    case 'cnpj': validateCNPJ(); break
    case 'email': validateEmail(); break
    case 'telefoneFixo': validateTelefoneFixo(); break
    case 'celular': validateCelular(); break
    case 'cep': validateCEP(); break
    case 'estado': validateEstado(); break
    case 'cidade': validateCidade(); break
    case 'bairro': validateBairro(); break
    case 'rua': validateRua(); break
    case 'numero': validateNumero(); break
  }
}

// Validação geral do formulário
const isFormValid = computed(() => {
  return validateRazaoSocial() &&
         validateCNPJ() &&
         validateEmail() &&
         validateTelefoneFixo() &&
         validateCelular() &&
         validateCEP() &&
         validateEstado() &&
         validateCidade() &&
         validateBairro() &&
         validateRua() &&
         validateNumero()
})

// ============================================
// RESETAR FORMULÁRIO
// ============================================
const resetForm = () => {
  formData.value.tipo = 'produto'
  formData.value.razaoSocial = ''
  formData.value.cnpj = ''
  formData.value.inscricao = ''

  // Resetar contato
  formData.value.contato.id = null
  formData.value.contato.email = ''
  formData.value.contato.telefoneFixo = ''
  formData.value.contato.celular = ''

  // Resetar endereco
  formData.value.endereco.id = null
  formData.value.endereco.cep = ''
  formData.value.endereco.estado = ''
  formData.value.endereco.cidade = ''
  formData.value.endereco.bairro = ''
  formData.value.endereco.rua = ''
  formData.value.endereco.numero = ''
  formData.value.endereco.complemento = ''
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
  // Limita a 14 dígitos
  value = value.substring(0, 14)
  value = value.replace(/^(\d{2})(\d)/, '$1.$2')
  value = value.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
  value = value.replace(/\.(\d{3})(\d)/, '.$1/$2')
  value = value.replace(/(\d{4})(\d)/, '$1-$2')
  formData.value.cnpj = value
}

const formatTelefoneFixo = (event) => {
  let value = event.target.value.replace(/\D/g, '')
  // Limita a 10 dígitos (DDD + 8 dígitos para fixo)
  value = value.substring(0, 10)
  value = value.replace(/^(\d{2})(\d)/, '($1) $2')
  value = value.replace(/(\d{4})(\d{4})$/, '$1-$2')
  formData.value.contato.telefoneFixo = value
}

const formatCelular = (event) => {
  let value = event.target.value.replace(/\D/g, '')
  // Limita a 11 dígitos (DDD + 9 dígitos para celular)
  value = value.substring(0, 11)
  value = value.replace(/^(\d{2})(\d)/, '($1) $2')
  value = value.replace(/(\d{5})(\d{4})$/, '$1-$2')
  formData.value.contato.celular = value
}

const formatCEP = (event) => {
  let value = event.target.value.replace(/\D/g, '')
  // Limita a 8 dígitos
  value = value.substring(0, 8)
  value = value.replace(/^(\d{5})(\d)/, '$1-$2')
  formData.value.endereco.cep = value
}

// Buscar CEP via API com validação
const buscarCEPComValidacao = async () => {
  // Primeiro valida o campo
  handleFieldBlur('cep')

  const cep = formData.value.endereco.cep.replace(/\D/g, '')
  if (cep.length === 8 && !fieldErrors.value.cep) {
    try {
      const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`)
      const data = await response.json()

      if (!data.erro) {
        formData.value.endereco.rua = data.logradouro || ''
        formData.value.endereco.bairro = data.bairro || ''
        formData.value.endereco.cidade = data.localidade || ''
        formData.value.endereco.estado = data.uf || ''

        // Marca os campos como tocados se foram preenchidos automaticamente
        if (data.logradouro) fieldTouched.value.rua = true
        if (data.bairro) fieldTouched.value.bairro = true
        if (data.localidade) fieldTouched.value.cidade = true
        if (data.uf) fieldTouched.value.estado = true
      } else {
        fieldErrors.value.cep = 'CEP não encontrado'
      }
    } catch (error) {
      logger.warn('Erro ao buscar CEP:', error)
      fieldErrors.value.cep = 'Erro ao consultar CEP'
    }
  }
}

// Limpar campos específicos ao mudar tipo
const clearSecondaryFields = () => {
  formData.value.inscricao = ''
}

// ============================================
// CARREGAR DADOS DO FORNECEDOR PARA EDIÇÃO
// ============================================
const loadFornecedorData = (fornecedor) => {
  // Dados principais
  formData.value.tipo = fornecedor.tipo || 'produto'
  formData.value.razaoSocial = fornecedor.razaoSocial || ''
  formData.value.cnpj = fornecedor.cnpj || ''

  // Inscrição (depende do tipo)
  if (formData.value.tipo === 'produto') {
    formData.value.inscricao = fornecedor.inscricaoEstadual || ''
  } else {
    formData.value.inscricao = fornecedor.inscricaoMunicipal || ''
  }

  // Carregar contato (incluindo ID para update)
  if (fornecedor.contato) {
    formData.value.contato.id = fornecedor.contato.id || null
    formData.value.contato.email = fornecedor.contato.email || ''
    formData.value.contato.telefoneFixo = fornecedor.contato.telefoneFixo || ''
    formData.value.contato.celular = fornecedor.contato.celular || ''
  }

  // Carregar endereco (incluindo ID para update)
  if (fornecedor.endereco) {
    formData.value.endereco.id = fornecedor.endereco.id || null
    formData.value.endereco.cep = fornecedor.endereco.cep || ''
    formData.value.endereco.estado = fornecedor.endereco.estado || ''
    formData.value.endereco.cidade = fornecedor.endereco.cidade || ''
    formData.value.endereco.bairro = fornecedor.endereco.bairro || ''
    formData.value.endereco.rua = fornecedor.endereco.rua || ''
    formData.value.endereco.numero = fornecedor.endereco.numero || ''
    formData.value.endereco.complemento = fornecedor.endereco.complemento || ''
  }
}

// ============================================
// SUBMETER FORMULÁRIO
// Prepara os dados no formato esperado pelo backend
// ============================================
const handleSubmit = () => {

  // ===== VALIDAÇÃO: VERIFICAR MUDANÇA DE TIPO =====
  if (props.fornecedor && props.fornecedor.tipo !== formData.value.tipo) {
    logger.warn('⚠️ TENTATIVA DE MUDAR TIPO DE FORNECEDOR DETECTADA!')
    logger.warn('   Tipo original:', props.fornecedor.tipo)
    logger.warn('   Tipo atual no form:', formData.value.tipo)

    const tipoOriginal = props.fornecedor.tipo === 'produto' ? 'Produto' : 'Serviço'
    const tipoNovo = formData.value.tipo === 'produto' ? 'Produto' : 'Serviço'

    showError(
      `❌ Não é possível alterar o tipo do fornecedor!\n\n` +
      `Tipo atual: Fornecedor de ${tipoOriginal}\n` +
      `Tipo selecionado: Fornecedor de ${tipoNovo}\n\n` +
      `O tipo de fornecedor não pode ser alterado após o cadastro.\n` +
      `Isso ocorre porque fornecedores de Produto e Serviço são armazenados em tabelas diferentes.\n\n` +
      `Para manter o tipo ${tipoOriginal}, continue a edição.\n` +
      `Para criar um fornecedor de ${tipoNovo}, cadastre um novo fornecedor.`
    )

    // Reverter o tipo para o original
    formData.value.tipo = props.fornecedor.tipo
    return
  }

  // Marcar todos os campos como tocados para mostrar erros
  Object.keys(fieldTouched.value).forEach(key => {
    fieldTouched.value[key] = true
  })

  // Executar todas as validações
  const isValid = isFormValid.value

  if (!isValid) {
    // Encontrar primeiro campo com erro para focar
    const firstErrorField = Object.keys(fieldErrors.value).find(key => fieldErrors.value[key])
    if (firstErrorField) {
      logger.warn('Primeiro erro encontrado em:', firstErrorField, ':', fieldErrors.value[firstErrorField])
    }

    warning('Por favor, corrija os erros destacados no formulário antes de continuar.')
    return
  }

  // ===== ETAPA 1: LIMPAR DADOS =====

  const cnpj = formData.value.cnpj.replace(/\D/g, '')
  const telefoneFixo = formData.value.contato.telefoneFixo.replace(/\D/g, '')
  const celular = formData.value.contato.celular.replace(/\D/g, '')
  const cep = formData.value.endereco.cep.replace(/\D/g, '')

  // ===== ETAPA 2: MONTAR OBJETO CONTATO =====

  const contato = {
    email: formData.value.contato.email.trim(),
    telefoneFixo: telefoneFixo || null,
    celular: celular || null
  }

  // Se for edição (fornecedor existente), incluir o ID do contato
  if (props.fornecedor && formData.value.contato.id) {
    contato.id = formData.value.contato.id
  } else {
  }


  // ===== ETAPA 3: MONTAR OBJETO ENDERECO =====

  const endereco = {
    cep: cep,
    estado: formData.value.endereco.estado.trim(),
    cidade: formData.value.endereco.cidade.trim(),
    bairro: formData.value.endereco.bairro.trim(),
    rua: formData.value.endereco.rua.trim(),
    numero: formData.value.endereco.numero.trim(),
    complemento: formData.value.endereco.complemento.trim() || null
  }

  // Se for edição (fornecedor existente), incluir o ID do endereco
  if (props.fornecedor && formData.value.endereco.id) {
    endereco.id = formData.value.endereco.id
  } else {
  }


  // ===== ETAPA 4: MONTAR OBJETO FINAL =====

  const dadosParaEnvio = {
    razaoSocial: formData.value.razaoSocial.trim(),
    cnpj: cnpj,
    endereco: endereco,
    contato: contato,
    tipo: formData.value.tipo
  }

  // Adicionar campo de inscrição conforme o tipo
  if (formData.value.tipo === 'produto') {
    dadosParaEnvio.inscricaoEstadual = formData.value.inscricao.trim() || null
  } else {
    dadosParaEnvio.inscricaoMunicipal = formData.value.inscricao.trim() || null
  }


  // ===== ETAPA 5: VALIDAÇÕES FINAIS =====

  if ('numero' in dadosParaEnvio.contato) {
    logger.error('   ❌ ERRO: contato tem campo "numero"!')
    return
  }

  if (!('numero' in dadosParaEnvio.endereco)) {
    logger.error('   ❌ ERRO: endereco NÃO tem campo "numero"!')
    return
  }

  if (!dadosParaEnvio.endereco.numero) {
    logger.error('   ❌ ERRO: endereco.numero está vazio!')
    return
  }

  // ===== ETAPA 6: LOG FINAL E ENVIO =====

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
  align-items: flex-start;
  padding: 24px 24px 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px 12px 0 0;
}

.modal-header-content {
  flex: 1;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #111827;
  margin: 0 0 4px 0;
}

.modal-subtitle {
  font-size: 0.875rem;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
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
  display: flex;
  align-items: center;
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

.form-label:has(+ .invalid-field)::after {
  content: " - Obrigatório";
  color: #ef4444;
  font-weight: 400;
  font-size: 0.75rem;
}

/* Para labels com asterisco, destacar em vermelho quando inválido */
.form-group:has(.invalid-field) .form-label {
  color: #ef4444;
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

.invalid-field {
  border-color: #ef4444 !important;
  background-color: #fef2f2;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1) !important;
}

.invalid-field:focus {
  border-color: #ef4444 !important;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.2) !important;
}

.error-message {
  color: #ef4444;
  font-size: 0.75rem;
  margin-top: 4px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.error-message::before {
  content: "⚠️";
  font-size: 0.7rem;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.form-hint {
  color: #6b7280;
  font-size: 0.75rem;
  margin-top: 4px;
  font-style: italic;
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

/* Radio Cards - Improved Design */
.radio-group-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
  margin-top: 12px;
}

.radio-card {
  position: relative;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: block;
}

.radio-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.1);
  transform: translateY(-2px);
}

.radio-card.active {
  border-color: #3b82f6;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

.radio-card input[type="radio"] {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
}

.radio-card-content {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.radio-card-icon {
  flex-shrink: 0;
  color: #6b7280;
  transition: color 0.2s;
}

.radio-card.active .radio-card-icon {
  color: #3b82f6;
}

.radio-card-label {
  display: block;
  font-weight: 600;
  color: #111827;
  font-size: 0.9375rem;
  margin-bottom: 4px;
}

.radio-card-description {
  display: block;
  font-size: 0.8125rem;
  color: #6b7280;
  line-height: 1.4;
}

.radio-card.active .radio-card-label {
  color: #1d4ed8;
}

.radio-card.active .radio-card-description {
  color: #1e40af;
}



.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e5e7eb;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
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
  opacity: 0.6;
}

.btn-primary:disabled:hover {
  background: #9ca3af;
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
