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
                  maxlength="255"
                />
              </div>

                            <div class="form-group">
                <label class="form-label">CNPJ *</label>
                <input
                  type="text"
                  v-model="formData.cnpj"
                  @input="formatCNPJ"
                  class="form-input"
                  :class="{ 'invalid-field': formData.cnpj && !isCNPJValid }"
                  required
                  placeholder="00.000.000/0000-00"
                  maxlength="18"
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
                  :placeholder="formData.tipo === 'produto' ? 'Inscrição estadual (opcional)' : 'Inscrição municipal (opcional)'"
                  maxlength="255"
                />
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
                  :class="{ 'invalid-field': formData.contato.email && !isEmailValid }"
                  required
                  placeholder="contato@empresa.com"
                  maxlength="100"
                />
              </div>

              <div class="form-group">
                <label class="form-label">Telefone Fixo</label>
                <input
                  type="text"
                  v-model="formData.contato.telefoneFixo"
                  class="form-input"
                  :class="{ 'invalid-field': formData.contato.telefoneFixo && !isTelefoneFixoValid }"
                  placeholder="(00) 0000-0000"
                  maxlength="14"
                  @input="formatTelefoneFixo"
                />
              </div>

              <div class="form-group">
                <label class="form-label">Celular</label>
                <input
                  type="text"
                  v-model="formData.contato.celular"
                  class="form-input"
                  :class="{ 'invalid-field': formData.contato.celular && !isCelularValid }"
                  placeholder="(00) 00000-0000"
                  maxlength="15"
                  @input="formatCelular"
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
                  @input="formatCEP"
                  @blur="buscarCEP"
                  class="form-input"
                  :class="{ 'invalid-field': formData.endereco.cep && !isCEPValid }"
                  required
                  placeholder="00000-000"
                  maxlength="9"
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
                  maxlength="50"
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
                  maxlength="60"
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
                  maxlength="100"
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
                  maxlength="10"
                />
              </div>              <div class="form-group full-width">
                <label class="form-label">Complemento</label>
                <input
                  type="text"
                  v-model="formData.endereco.complemento"
                  class="form-input"
                  placeholder="Apto, sala, andar..."
                  maxlength="100"
                />
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
  
  // Objeto CONTATO (ContatoCreateDTO)
  contato: {
    email: '',           // OBRIGATÓRIO
    telefoneFixo: '',    // OPCIONAL - 10 ou 11 dígitos
    celular: ''          // OPCIONAL - 11 dígitos (deve começar com 9 após DDD)
  },
  
  // Objeto ENDERECO (EnderecoCreateDTO)
  endereco: {
    cep: '',            // OBRIGATÓRIO - 8 dígitos
    estado: '',         // OBRIGATÓRIO - sigla UF
    cidade: '',         // OBRIGATÓRIO
    bairro: '',         // OBRIGATÓRIO
    rua: '',            // OBRIGATÓRIO
    numero: '',         // OBRIGATÓRIO - número do endereço
    complemento: ''     // OPCIONAL
  }
})

// Validação do formulário
const isTelefoneFixoValid = computed(() => {
  if (!formData.value.contato.telefoneFixo) return true;
  const telefone = formData.value.contato.telefoneFixo.replace(/\D/g, '');
  return telefone.length === 10; // (00) 0000-0000
});

const isCelularValid = computed(() => {
  if (!formData.value.contato.celular) return true;
  const celular = formData.value.contato.celular.replace(/\D/g, '');
  return celular.length === 11 && celular.charAt(2) === '9'; // (00) 9XXXX-XXXX
});

const isCNPJValid = computed(() => {
  const cnpj = formData.value.cnpj.replace(/\D/g, '');
  
  // Verifica se tem 14 dígitos
  if (cnpj.length !== 14) return false;
  
  // Verifica se todos os dígitos são iguais (CNPJ inválido)
  if (/^(\d)\1{13}$/.test(cnpj)) return false;
  
  // Validação do dígito verificador
  let tamanho = cnpj.length - 2;
  let numeros = cnpj.substring(0, tamanho);
  let digitos = cnpj.substring(tamanho);
  let soma = 0;
  let pos = tamanho - 7;
  
  for (let i = tamanho; i >= 1; i--) {
    soma += numeros.charAt(tamanho - i) * pos--;
    if (pos < 2) pos = 9;
  }
  
  let resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
  if (resultado != digitos.charAt(0)) return false;
  
  tamanho = tamanho + 1;
  numeros = cnpj.substring(0, tamanho);
  soma = 0;
  pos = tamanho - 7;
  
  for (let i = tamanho; i >= 1; i--) {
    soma += numeros.charAt(tamanho - i) * pos--;
    if (pos < 2) pos = 9;
  }
  
  resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
  return resultado == digitos.charAt(1);
});

const isCEPValid = computed(() => {
  const cep = formData.value.endereco.cep.replace(/\D/g, '');
  return cep.length === 8;
});

const isEmailValid = computed(() => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(formData.value.contato.email);
});

const isFormValid = computed(() => {
  return formData.value.razaoSocial &&
         formData.value.razaoSocial.length <= 255 &&
         isCNPJValid.value &&
         isEmailValid.value &&
         formData.value.contato.email.length <= 100 &&
         isTelefoneFixoValid.value &&
         isCelularValid.value &&
         isCEPValid.value &&
         formData.value.endereco.estado &&
         formData.value.endereco.cidade &&
         formData.value.endereco.cidade.length <= 50 &&
         formData.value.endereco.bairro &&
         formData.value.endereco.bairro.length <= 60 &&
         formData.value.endereco.rua &&
         formData.value.endereco.rua.length <= 100 &&
         formData.value.endereco.numero &&
         formData.value.endereco.numero.length <= 10 &&
         (!formData.value.inscricao || formData.value.inscricao.length <= 255) &&
         (!formData.value.endereco.complemento || formData.value.endereco.complemento.length <= 100)
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
  formData.value.contato.email = ''
  formData.value.contato.telefoneFixo = ''
  formData.value.contato.celular = ''
  
  // Resetar endereco
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

  // Carregar contato
  if (fornecedor.contato) {
    formData.value.contato.email = fornecedor.contato.email || ''
    formData.value.contato.telefoneFixo = fornecedor.contato.telefoneFixo || ''
    formData.value.contato.celular = fornecedor.contato.celular || ''
  }

  // Carregar endereco
  if (fornecedor.endereco) {
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
  console.log('='.repeat(60))
  console.log('📝 INICIANDO ENVIO DO FORMULÁRIO')
  console.log('='.repeat(60))

  // Validar formulário com mensagens específicas
  if (!isFormValid.value) {
    let mensagemErro = 'Corrija os seguintes erros:\n\n';
    
    if (!formData.value.razaoSocial) mensagemErro += '• Razão Social é obrigatória\n';
    if (!isCNPJValid.value) mensagemErro += '• CNPJ inválido (verifique o formato e dígitos)\n';
    if (!isEmailValid.value) mensagemErro += '• E-mail deve ter formato válido\n';
    if (!isTelefoneFixoValid.value) mensagemErro += '• Telefone fixo deve ter 10 dígitos\n';
    if (!isCelularValid.value) mensagemErro += '• Celular deve ter 11 dígitos e começar com 9\n';
    if (!isCEPValid.value) mensagemErro += '• CEP deve ter 8 dígitos\n';
    if (!formData.value.endereco.estado) mensagemErro += '• Estado é obrigatório\n';
    if (!formData.value.endereco.cidade) mensagemErro += '• Cidade é obrigatória\n';
    if (!formData.value.endereco.bairro) mensagemErro += '• Bairro é obrigatório\n';
    if (!formData.value.endereco.rua) mensagemErro += '• Rua é obrigatória\n';
    if (!formData.value.endereco.numero) mensagemErro += '• Número é obrigatório\n';
    
    alert(mensagemErro);
    return
  }

  // ===== ETAPA 1: LIMPAR DADOS =====
  console.log('\n📋 ETAPA 1: Limpando dados...')
  
  const cnpj = formData.value.cnpj.replace(/\D/g, '')
  const telefoneFixo = formData.value.contato.telefoneFixo.replace(/\D/g, '')
  const celular = formData.value.contato.celular.replace(/\D/g, '')
  const cep = formData.value.endereco.cep.replace(/\D/g, '')

  // ===== ETAPA 2: MONTAR OBJETO CONTATO =====
  console.log('\n📞 ETAPA 2: Montando objeto CONTATO...')
  
  const contato = {
    email: formData.value.contato.email.trim(),
    telefoneFixo: telefoneFixo || null,
    celular: celular || null
  }
  
  console.log('   Contato criado:', contato)
  console.log('   ⚠️ contato tem "numero"?', 'numero' in contato, '(deve ser false)')

  // ===== ETAPA 3: MONTAR OBJETO ENDERECO =====
  console.log('\n🏠 ETAPA 3: Montando objeto ENDERECO...')
  
  const endereco = {
    cep: cep,
    estado: formData.value.endereco.estado.trim(),
    cidade: formData.value.endereco.cidade.trim(),
    bairro: formData.value.endereco.bairro.trim(),
    rua: formData.value.endereco.rua.trim(),
    numero: formData.value.endereco.numero.trim(),
    complemento: formData.value.endereco.complemento.trim() || null
  }
  
  console.log('   Endereco criado:', endereco)
  console.log('   ✅ endereco tem "numero"?', 'numero' in endereco, '(deve ser true)')
  console.log('   ✅ endereco.numero =', endereco.numero)

  // ===== ETAPA 4: MONTAR OBJETO FINAL =====
  console.log('\n📦 ETAPA 4: Montando objeto FINAL para envio...')
  
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
  
  console.log(`   ${formData.value.tipo === 'produto' ? 'inscricaoEstadual' : 'inscricaoMunicipal'}:`, 
              formData.value.inscricao.trim() || null)

  // ===== ETAPA 5: VALIDAÇÕES FINAIS =====
  console.log('\n✅ ETAPA 5: Validações finais...')
  
  if ('numero' in dadosParaEnvio.contato) {
    console.error('   ❌ ERRO: contato tem campo "numero"!')
    return
  }
  
  if (!('numero' in dadosParaEnvio.endereco)) {
    console.error('   ❌ ERRO: endereco NÃO tem campo "numero"!')
    return
  }
  
  if (!dadosParaEnvio.endereco.numero) {
    console.error('   ❌ ERRO: endereco.numero está vazio!')
    return
  }

  // ===== ETAPA 6: LOG FINAL E ENVIO =====
  console.log('\n🚀 ETAPA 6: Enviando dados...')
  console.log('\n📤 PAYLOAD FINAL:')
  console.log(JSON.stringify(dadosParaEnvio, null, 2))
  console.log('\n' + '='.repeat(60))
  
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

.invalid-field {
  border-color: #ef4444;
}

.error-message {
  color: #ef4444;
  font-size: 0.75rem;
  margin-top: 4px;
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
