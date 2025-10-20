<template>
    <div class="perfil-view">
        <!-- Header -->
        <DashboardHeader />

        <!-- Sidebar -->
        <DashboardSidebar />

        <!-- Conteúdo Principal -->
        <div class="main-content">
            <div class="perfil-container">
                <div class="perfil-header">
                    <div class="header-content">
                        <div class="header-info">
                            <h1 class="page-title">Meu Perfil</h1>
                            <p class="page-subtitle">Visualize e edite suas informações pessoais</p>
                        </div>
                        <div class="header-actions">
                            <button class="btn-secondary" @click="cancelarEdicao" v-if="isEditMode">
                                Cancelar
                            </button>
                            <button class="btn-primary" @click="toggleEditMode">
                                {{ isEditMode ? 'Salvar Alterações' : 'Editar Perfil' }}
                            </button>
                        </div>
                    </div>
                </div>

                <div class="perfil-content">
                    <!-- Avatar e Informações Básicas -->
                    <div class="profile-card">
                        <div class="avatar-section">
                            <div class="avatar-container">
                                <img :src="userAvatar" :alt="formData.nome" class="user-avatar" loading="eager" width="120" height="120" />
                                <button v-if="isEditMode" class="avatar-edit-btn" @click="changeAvatar">
                                    <svg viewBox="0 0 24 24" width="16" height="16">
                                        <path fill="currentColor"
                                            d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                                    </svg>
                                </button>
                            </div>
                            <div class="user-basic-info">
                                <h2 class="user-name">{{ formData.nome }}</h2>
                                <p class="user-email">{{ formData.email }}</p>
                                <p class="user-role">{{ formData.funcao }}</p>
                            </div>
                        </div>
                    </div>

                    <!-- Formulário de Dados Pessoais -->
                    <div class="section-card">
                        <div class="section-header">
                            <h3 class="section-title">Dados Pessoais</h3>
                            <p class="section-subtitle">Mantenha suas informações atualizadas</p>
                        </div>

                        <form @submit.prevent="salvarPerfil" class="profile-form">
                            <div class="form-grid">
                                <div class="form-group">
                                    <label class="form-label">Nome Completo</label>
                                    <input type="text" v-model="formData.nome" class="form-input"
                                        :disabled="!isEditMode" required />
                                </div>

                                <div class="form-group">
                                    <label class="form-label">E-mail (Login)</label>
                                    <input type="email" v-model="formData.email" class="form-input"
                                        :disabled="!isEditMode" required />
                                </div>

                                <div class="form-group">
                                    <label class="form-label">Telefone</label>
                                    <input type="tel" v-model="formData.telefone" class="form-input"
                                        :disabled="!isEditMode" placeholder="(00) 00000-0000" />
                                </div>

                                <div class="form-group">
                                    <label class="form-label">Unidade Funcional</label>
                                    <select v-model="formData.unidadeFuncional" class="form-select"
                                        :disabled="!isEditMode" required>
                                        <option value="">Selecione a unidade</option>
                                        <option value="TI">Tecnologia da Informação</option>
                                        <option value="Compras">Compras</option>
                                        <option value="Financeiro">Financeiro</option>
                                        <option value="RH">Recursos Humanos</option>
                                        <option value="Operações">Operações</option>
                                        <option value="Comercial">Comercial</option>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label class="form-label">Função/Perfil</label>
                                    <input type="text" v-model="formData.funcao" class="form-input" disabled />
                                    <small class="form-hint">Este campo é definido pelo administrador</small>
                                </div>
                            </div>
                        </form>
                    </div>


                </div>
            </div>
        </div>

        <!-- Notificação -->
        <div v-if="showNotification" :class="['notification', 'notification-' + notificationType]">
            <div class="notification-content">
                <div class="notification-icon">
                    <svg v-if="notificationType === 'success'" viewBox="0 0 24 24" width="20" height="20">
                        <path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z" />
                    </svg>
                    <svg v-else viewBox="0 0 24 24" width="20" height="20">
                        <path fill="currentColor"
                            d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z" />
                    </svg>
                </div>
                <span>{{ notificationMessage }}</span>
            </div>
            <button class="notification-close" @click="hideNotification">×</button>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import DashboardHeader from '../components/DashboardHeader.vue'
import DashboardSidebar from '../components/DashboardSidebar.vue'
import { useAuthStore } from '../stores/auth.js'

const authStore = useAuthStore()

// Estados reativo
const isEditMode = ref(false)
const showNotification = ref(false)
const notificationType = ref('success')
const notificationMessage = ref('')

// Dados do formulário - usar dados reais do usuário logado
const formData = ref({
    nome: authStore.user?.name || '',
    email: authStore.user?.email || '',
    telefone: authStore.user?.telefone || '',
    unidadeFuncional: authStore.user?.unidadeFuncional || '',
    funcao: authStore.user?.funcao || authStore.user?.role || ''
})

// Dados originais para cancelamento
const originalData = ref({})

// Avatar padrão
const userAvatar = ref('https://ui-avatars.com/api/?name=Usuario&background=3b82f6&color=fff&size=100')

// Métodos
const toggleEditMode = async () => {
    if (isEditMode.value) {
        // Salvar alterações
        await salvarPerfil()
    } else {
        // Entrar em modo de edição
        originalData.value = { ...formData.value }
        isEditMode.value = true
    }
}

const cancelarEdicao = () => {
    formData.value = { ...originalData.value }
    isEditMode.value = false
}

const salvarPerfil = async () => {
    try {
        // Aqui você faria a chamada para a API
        // await perfilService.atualizarPerfil(formData.value)

        showNotificationMessage('success', 'Perfil atualizado com sucesso!')
        isEditMode.value = false
    } catch (error) {
        console.error('Erro ao atualizar perfil:', error)
        showNotificationMessage('error', 'Erro ao atualizar perfil. Tente novamente.')
    }
}

const changeAvatar = () => {
    // Implementar upload de avatar
    showNotificationMessage('info', 'Funcionalidade de upload de avatar em desenvolvimento')
}

const showNotificationMessage = (type, message) => {
    notificationType.value = type
    notificationMessage.value = message
    showNotification.value = true
    setTimeout(() => {
        hideNotification()
    }, 5000)
}

const hideNotification = () => {
    showNotification.value = false
}

// Lifecycle
onMounted(() => {
    // Carregar dados do usuário da store ou API
    if (authStore.user) {
        formData.value.nome = authStore.user.name || 'Usuário'
        formData.value.email = authStore.user.email || 'joao.silva@empresa.com'
    }
})
</script>

<style scoped>
.perfil-view {
    min-height: 100vh;
    background-color: #f8fafc;
}

.main-content {
    margin-left: 240px;
    margin-top: 70px;
    padding: 24px;
}

.perfil-container {
    max-width: 1200px;
    margin: 0 auto;
}

.perfil-header {
    background: white;
    border-radius: 12px;
    border: 1px solid #e5e7eb;
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.header-content {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 24px;
}

.page-title {
    font-size: 28px;
    font-weight: 700;
    color: #111827;
    margin: 0 0 8px 0;
}

.page-subtitle {
    font-size: 16px;
    color: #6b7280;
    margin: 0;
}

.header-actions {
    display: flex;
    gap: 12px;
}

.perfil-content {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.profile-card {
    background: white;
    border-radius: 12px;
    border: 1px solid #e5e7eb;
    padding: 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.avatar-section {
    display: flex;
    align-items: center;
    gap: 24px;
}

.avatar-container {
    position: relative;
}

.user-avatar {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    border: 4px solid #e5e7eb;
}

.avatar-edit-btn {
    position: absolute;
    bottom: 0;
    right: 0;
    background: #3b82f6;
    color: white;
    border: none;
    border-radius: 50%;
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border: 2px solid white;
}

.avatar-edit-btn:hover {
    background: #2563eb;
}

.user-basic-info .user-name {
    font-size: 24px;
    font-weight: 600;
    color: #111827;
    margin: 0 0 4px 0;
}

.user-basic-info .user-email {
    font-size: 16px;
    color: #6b7280;
    margin: 0 0 4px 0;
}

.user-basic-info .user-role {
    font-size: 14px;
    color: #3b82f6;
    font-weight: 500;
    margin: 0;
}

.section-card {
    background: white;
    border-radius: 12px;
    border: 1px solid #e5e7eb;
    padding: 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.section-header {
    margin-bottom: 24px;
}

.section-title {
    font-size: 20px;
    font-weight: 600;
    color: #111827;
    margin: 0 0 4px 0;
}

.section-subtitle {
    font-size: 14px;
    color: #6b7280;
    margin: 0;
}

.profile-form {
    width: 100%;
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
}

.form-group {
    display: flex;
    flex-direction: column;
}

.form-label {
    font-size: 14px;
    font-weight: 500;
    color: #374151;
    margin-bottom: 6px;
}

.form-input,
.form-select {
    padding: 12px 16px;
    border: 1px solid #d1d5db;
    border-radius: 8px;
    font-size: 14px;
    transition: border-color 0.2s, box-shadow 0.2s;
    background: white;
}

.form-input:focus,
.form-select:focus {
    outline: none;
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-input:disabled,
.form-select:disabled {
    background-color: #f9fafb;
    color: #6b7280;
    cursor: not-allowed;
}

.form-hint {
    font-size: 12px;
    color: #6b7280;
    margin-top: 4px;
}



.btn-primary,
.btn-secondary,
.btn-outline {
    padding: 12px 24px;
    border-radius: 8px;
    font-weight: 500;
    cursor: pointer;
    border: none;
    transition: all 0.2s;
    font-size: 14px;
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
}

.btn-primary:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.btn-secondary {
    background: white;
    color: #374151;
    border: 1px solid #d1d5db;
}

.btn-secondary:hover {
    background: #f9fafb;
}

.btn-outline {
    background: white;
    color: #3b82f6;
    border: 1px solid #3b82f6;
}

.btn-outline:hover:not(:disabled) {
    background: #eff6ff;
}

.btn-outline:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.notification {
    position: fixed;
    top: 90px;
    right: 24px;
    background: white;
    border-radius: 8px;
    padding: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    border-left: 4px solid #3b82f6;
    z-index: 1000;
    min-width: 300px;
    animation: slideIn 0.3s ease-out;
}

.notification-success {
    border-left-color: #10b981;
}

.notification-error {
    border-left-color: #ef4444;
}

.notification-content {
    display: flex;
    align-items: center;
    gap: 12px;
}

.notification-icon {
    flex-shrink: 0;
}

.notification-close {
    background: none;
    border: none;
    font-size: 20px;
    cursor: pointer;
    color: #6b7280;
    padding: 0;
    margin-left: auto;
}

@keyframes slideIn {
    from {
        transform: translateX(100%);
        opacity: 0;
    }

    to {
        transform: translateX(0);
        opacity: 1;
    }
}

@media (max-width: 1024px) {
    .main-content {
        margin-left: 0;
        margin-top: 70px;
        padding: 16px;
    }
}

@media (max-width: 768px) {
    .header-content {
        flex-direction: column;
        align-items: stretch;
        gap: 16px;
    }

    .header-actions {
        justify-content: stretch;
    }

    .btn-primary,
    .btn-secondary {
        flex: 1;
        justify-content: center;
    }

    .avatar-section {
        flex-direction: column;
        text-align: center;
        gap: 16px;
    }

    .form-grid {
        grid-template-columns: 1fr;
    }


}
</style>
