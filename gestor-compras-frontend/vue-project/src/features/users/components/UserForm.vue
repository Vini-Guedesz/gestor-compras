<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="isVisible" class="modal-overlay" @click.self="handleClose">
        <div class="modal-container">
          <!-- Header -->
          <div class="modal-header">
            <h2 class="modal-title">
              {{ isEditMode ? 'Editar Usuário' : 'Novo Usuário' }}
            </h2>
            <button class="modal-close-btn" @click="handleClose" aria-label="Fechar">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
              </svg>
            </button>
          </div>

          <!-- Body -->
          <div class="modal-body">
            <form @submit.prevent="handleSubmit" class="user-form" autocomplete="off">
              <!-- Nome Completo -->
              <div class="form-group">
                <label for="nome" class="form-label">
                  Nome Completo <span class="required">*</span>
                </label>
                <input
                  id="nome"
                  v-model="formData.nome"
                  type="text"
                  class="form-input"
                  :class="{ 'error': errors.nome }"
                  placeholder="Digite o nome completo"
                  maxlength="100"
                  autocomplete="off"
                  required
                />
                <span v-if="errors.nome" class="error-message">{{ errors.nome }}</span>
                <small class="form-hint">Mínimo 3 caracteres, máximo 100 caracteres</small>
              </div>

              <!-- E-mail -->
              <div class="form-group">
                <label for="email" class="form-label">
                  E-mail <span class="required">*</span>
                </label>
                <input
                  id="email"
                  v-model="formData.email"
                  type="email"
                  class="form-input"
                  :class="{ 'error': errors.email }"
                  placeholder="usuario@empresa.com"
                  maxlength="255"
                  autocomplete="off"
                  autocorrect="off"
                  autocapitalize="off"
                  spellcheck="false"
                  name="email-field-disabled"
                  :readonly="!inputsReady"
                  @focus="inputsReady = true"
                  required
                />
                <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
                <small class="form-hint">O e-mail será usado para login no sistema (máximo 255 caracteres)</small>
              </div>

              <!-- Senha (apenas na criação ou se quiser alterar) -->
              <div v-if="!isEditMode || mostrarCampoSenha" class="form-group">
                <label for="senha" class="form-label">
                  Senha <span v-if="!isEditMode" class="required">*</span>
                </label>
                <div class="password-input-container">
                  <input
                    id="senha"
                    v-model="formData.senha"
                    :type="mostrarSenha ? 'text' : 'password'"
                    class="form-input"
                    :class="{ 'error': errors.senha }"
                    placeholder="Digite uma senha forte"
                    autocomplete="new-password"
                    name="password-field-disabled"
                    :readonly="!inputsReady"
                    @focus="inputsReady = true"
                    :required="!isEditMode"
                  />
                  <button
                    type="button"
                    class="toggle-password-btn"
                    @click="mostrarSenha = !mostrarSenha"
                    :aria-label="mostrarSenha ? 'Ocultar senha' : 'Mostrar senha'"
                  >
                    <svg v-if="!mostrarSenha" viewBox="0 0 24 24" width="20" height="20">
                      <path fill="currentColor" d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                    </svg>
                    <svg v-else viewBox="0 0 24 24" width="20" height="20">
                      <path fill="currentColor" d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z"/>
                    </svg>
                  </button>
                </div>
                <span v-if="errors.senha" class="error-message">{{ errors.senha }}</span>
                <small class="form-hint">
                  Mínimo 8 caracteres, incluindo maiúscula, minúscula, número e caractere especial
                </small>
              </div>

              <!-- Botão para mostrar campo de senha na edição -->
              <div v-if="isEditMode && !mostrarCampoSenha" class="form-group">
                <button type="button" class="btn-secondary" @click="mostrarCampoSenha = true">
                  Alterar Senha
                </button>
              </div>

              <!-- Função/Role -->
              <div class="form-group">
                <label for="role" class="form-label">
                  Função <span class="required">*</span>
                </label>
                <select
                  id="role"
                  v-model="formData.role"
                  class="form-select"
                  :class="{ 'error': errors.role }"
                  required
                >
                  <option value="">Selecione uma função</option>
                  <option value="ADMIN">Administrador</option>
                  <option value="USUARIO">Usuário</option>
                  <option value="COMPRADOR">Comprador</option>
                  <option value="APROVADOR">Aprovador</option>
                </select>
                <span v-if="errors.role" class="error-message">{{ errors.role }}</span>
                <small class="form-hint">{{ getRoleDescription(formData.role) }}</small>
              </div>

              <!-- Status (apenas na edição) -->
              <div v-if="isEditMode" class="form-group">
                <label class="checkbox-label">
                  <input
                    v-model="formData.ativo"
                    type="checkbox"
                    class="form-checkbox"
                  />
                  <span>Usuário ativo (pode acessar o sistema)</span>
                </label>
              </div>
            </form>
          </div>

          <!-- Footer -->
          <div class="modal-footer">
            <button type="button" class="btn-secondary" @click="handleClose">
              Cancelar
            </button>
            <button type="submit" class="btn-primary" @click="handleSubmit" :disabled="isSubmitting">
              <span v-if="!isSubmitting">{{ isEditMode ? 'Salvar Alterações' : 'Criar Usuário' }}</span>
              <span v-else class="loading-content">
                <span class="loading-spinner"></span>
                Salvando...
              </span>
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, watch, computed, onMounted } from 'vue'

const props = defineProps({
  isVisible: {
    type: Boolean,
    required: true
  },
  usuario: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'save'])

// Estado do formulário
const formData = ref({
  nome: '',
  email: '',
  senha: '',
  role: '',
  ativo: true
})

const errors = ref({})
const isSubmitting = ref(false)
const mostrarSenha = ref(false)
const mostrarCampoSenha = ref(false)
const inputsReady = ref(false)

const isEditMode = computed(() => !!props.usuario)

// Previne autocomplete ao montar o componente
onMounted(() => {
  setTimeout(() => {
    inputsReady.value = true
  }, 100)
})

// Função para resetar o formulário
const resetForm = () => {
  formData.value = {
    nome: '',
    email: '',
    senha: '',
    role: '',
    ativo: true
  }
  errors.value = {}
  mostrarSenha.value = false
  mostrarCampoSenha.value = false
}

// Watch combinado para controlar o estado do formulário
watch([() => props.isVisible, () => props.usuario], ([isVisible, usuario]) => {
  if (isVisible && !usuario) {
    // Modal aberto para novo usuário - limpa o formulário
    resetForm()
  } else if (isVisible && usuario) {
    // Modal aberto para editar - popula com dados do usuário
    formData.value = {
      nome: usuario.nome || '',
      email: usuario.email || '',
      senha: '',
      role: usuario.role || '',
      ativo: usuario.ativo !== undefined ? usuario.ativo : true
    }
    mostrarCampoSenha.value = false
  } else if (!isVisible) {
    // Modal fechado - limpa campos de senha
    mostrarCampoSenha.value = false
    mostrarSenha.value = false
  }
}, { immediate: true })

const getRoleDescription = (role) => {
  const descriptions = {
    'ADMIN': 'Acesso total ao sistema, incluindo gerenciamento de usuários',
    'USUARIO': 'Acesso básico ao sistema',
    'COMPRADOR': 'Permissões para criar e gerenciar pedidos de compra',
    'APROVADOR': 'Permissões para aprovar pedidos de compra'
  }
  return descriptions[role] || 'Selecione uma função para ver a descrição'
}

const validateForm = () => {
  errors.value = {}
  let isValid = true

  // Nome - validação completa
  if (!formData.value.nome || formData.value.nome.trim().length === 0) {
    errors.value.nome = 'O nome é obrigatório'
    isValid = false
  } else if (formData.value.nome.trim().length < 3) {
    errors.value.nome = 'O nome deve ter pelo menos 3 caracteres'
    isValid = false
  } else if (formData.value.nome.length > 100) {
    errors.value.nome = 'O nome não pode ter mais de 100 caracteres'
    isValid = false
  }

  // Email - validação completa
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!formData.value.email || formData.value.email.trim().length === 0) {
    errors.value.email = 'O e-mail é obrigatório'
    isValid = false
  } else if (!emailRegex.test(formData.value.email)) {
    errors.value.email = 'Digite um e-mail válido (exemplo: usuario@empresa.com)'
    isValid = false
  } else if (formData.value.email.length > 255) {
    errors.value.email = 'O e-mail não pode ter mais de 255 caracteres'
    isValid = false
  }

  // Senha (apenas se não estiver editando OU se estiver editando e o campo de senha estiver visível)
  if (!isEditMode.value || mostrarCampoSenha.value) {
    if (!formData.value.senha || formData.value.senha.trim().length === 0) {
      errors.value.senha = 'A senha é obrigatória'
      isValid = false
    } else if (formData.value.senha.length < 8) {
      errors.value.senha = 'A senha deve ter pelo menos 8 caracteres'
      isValid = false
    } else if (formData.value.senha.length > 100) {
      errors.value.senha = 'A senha não pode ter mais de 100 caracteres'
      isValid = false
    } else {
      // Validação completa de senha
      const senhaRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/
      if (!senhaRegex.test(formData.value.senha)) {
        errors.value.senha = 'A senha deve conter: maiúscula, minúscula, número e caractere especial (@$!%*?&)'
        isValid = false
      }
    }
  }

  // Role
  if (!formData.value.role) {
    errors.value.role = 'Selecione uma função'
    isValid = false
  }

  return isValid
}

const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  isSubmitting.value = true

  try {
    // Preparar dados para envio
    const dadosParaEnviar = {
      nome: formData.value.nome,
      email: formData.value.email,
      role: formData.value.role
    }

    // Incluir senha apenas se:
    // - Estiver criando um novo usuário OU
    // - Estiver editando E o campo de senha estiver visível E tiver conteúdo
    if (!isEditMode.value || (mostrarCampoSenha.value && formData.value.senha)) {
      dadosParaEnviar.senha = formData.value.senha
    }

    // Incluir ativo apenas se estiver editando
    if (isEditMode.value) {
      dadosParaEnviar.ativo = formData.value.ativo
    }

    emit('save', dadosParaEnviar)
  } finally {
    isSubmitting.value = false
  }
}

const handleClose = () => {
  if (!isSubmitting.value) {
    emit('close')
  }
}
</script>

<style scoped>
/* Modal Overlay */
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
  z-index: 1000;
  padding: 20px;
}

/* Modal Container */
.modal-container {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

/* Modal Header */
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1F285F;
  margin: 0;
}

.modal-close-btn {
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  box-shadow: none;
  outline: none;
}

.modal-close-btn:hover {
  background: #f3f4f6;
  color: #1F285F;
}

.modal-close-btn:focus {
  outline: 2px solid #3b82f6;
  outline-offset: 2px;
}

/* Modal Body */
.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

/* Form */
.user-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
}

.required {
  color: #dc2626;
}

.form-input,
.form-select {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-input.error,
.form-select.error {
  border-color: #dc2626;
}

.form-input.error:focus,
.form-select.error:focus {
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
}

.password-input-container {
  position: relative;
}

.toggle-password-btn {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
}

.toggle-password-btn:hover {
  color: #1F285F;
  background: #f3f4f6;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 0.875rem;
  color: #374151;
  user-select: none;
}

.form-checkbox {
  width: 16px;
  height: 16px;
  min-width: 16px;
  min-height: 16px;
  cursor: pointer;
  margin: 0;
  flex-shrink: 0;
}

.form-hint {
  font-size: 0.75rem;
  color: #6b7280;
  margin-top: -4px;
}

.error-message {
  font-size: 0.75rem;
  color: #dc2626;
  margin-top: -4px;
}

/* Modal Footer */
.modal-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 24px;
  border-top: 1px solid #e5e7eb;
}

.btn-primary,
.btn-secondary {
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #2563eb;
  transform: translateY(-1px);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
}

.btn-secondary:hover {
  background: #e5e7eb;
}

.loading-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.loading-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Modal Transitions */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-active .modal-container,
.modal-leave-active .modal-container {
  transition: transform 0.3s ease;
}

.modal-enter-from .modal-container,
.modal-leave-to .modal-container {
  transform: scale(0.95);
}

/* Responsividade */
@media (max-width: 640px) {
  .modal-container {
    max-width: 100%;
    margin: 0;
    border-radius: 0;
    max-height: 100vh;
  }

  .modal-header,
  .modal-body,
  .modal-footer {
    padding: 16px;
  }

  .modal-title {
    font-size: 1.25rem;
  }
}
</style>
