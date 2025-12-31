<template>
    <div class="configuracoes-view">
        <!-- Header -->
        <DashboardHeader />

        <!-- Sidebar -->
        <DashboardSidebar />

        <!-- Conteúdo Principal -->
        <div class="main-content">
            <div class="configuracoes-container">
                <!-- Card Principal -->
                <div class="config-main-card">
                    <!-- Banner -->
                    <div class="config-banner">
                        <div class="banner-pattern"></div>
                        <div class="banner-content">
                            <div class="banner-icon">
                                <svg viewBox="0 0 24 24" width="32" height="32" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M12 15a3 3 0 100-6 3 3 0 000 6z"/>
                                    <path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-2 2 2 2 0 01-2-2v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83 0 2 2 0 010-2.83l.06-.06a1.65 1.65 0 00.33-1.82 1.65 1.65 0 00-1.51-1H3a2 2 0 01-2-2 2 2 0 012-2h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 010-2.83 2 2 0 012.83 0l.06.06a1.65 1.65 0 001.82.33H9a1.65 1.65 0 001-1.51V3a2 2 0 012-2 2 2 0 012 2v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 0 2 2 0 010 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 012 2 2 2 0 01-2 2h-.09a1.65 1.65 0 00-1.51 1z"/>
                                </svg>
                            </div>
                            <div class="banner-text">
                                <h1 class="page-title">Configurações</h1>
                                <p class="page-subtitle">Gerencie suas preferências e segurança</p>
                            </div>
                        </div>
                    </div>

                    <!-- Ações -->
                    <div class="config-actions">
                        <button class="btn-cancel" @click="cancelarEdicao" v-if="isEditMode">
                            Cancelar
                        </button>
                        <button class="btn-primary" @click="toggleEditMode">
                            <svg v-if="!isEditMode" viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
                            </svg>
                            <svg v-else viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M5 13l4 4L19 7"/>
                            </svg>
                            {{ isEditMode ? 'Salvar Alterações' : 'Editar' }}
                        </button>
                    </div>
                </div>

                <!-- Seção de Segurança -->
                <div class="config-card">
                    <div class="card-header">
                        <div class="header-left">
                            <div class="header-icon security">
                                <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                                </svg>
                            </div>
                            <div>
                                <h2 class="card-title">Segurança</h2>
                                <p class="card-subtitle">Mantenha sua conta protegida</p>
                            </div>
                        </div>
                    </div>

                    <div class="card-body">
                        <div class="security-section">
                            <button class="btn-password" @click="togglePasswordForm" :disabled="!isEditMode">
                                <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                                    <path d="M7 11V7a5 5 0 0110 0v4"/>
                                </svg>
                                {{ showPasswordForm ? 'Cancelar Alteração' : 'Alterar Senha' }}
                            </button>

                            <Transition name="slide-down">
                                <form v-if="showPasswordForm" @submit.prevent="alterarSenha" class="password-form">
                                    <div class="form-group">
                                        <label class="form-label">Senha Atual</label>
                                        <div class="input-wrapper">
                                            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
                                                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                                                <path d="M7 11V7a5 5 0 0110 0v4"/>
                                            </svg>
                                            <input type="password" v-model="passwordData.senhaAtual" class="form-input" required placeholder="Digite sua senha atual" />
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label">Nova Senha</label>
                                        <div class="input-wrapper">
                                            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
                                                <path d="M12 17a2 2 0 100-4 2 2 0 000 4z"/>
                                                <path d="M5 10V7a7 7 0 1114 0v3"/>
                                            </svg>
                                            <input type="password" v-model="passwordData.novaSenha" class="form-input" required minlength="6" placeholder="Mínimo 6 caracteres" />
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label">Confirmar Nova Senha</label>
                                        <div class="input-wrapper">
                                            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
                                                <path d="M9 12l2 2 4-4"/>
                                                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                                                <path d="M7 11V7a5 5 0 0110 0v4"/>
                                            </svg>
                                            <input type="password" v-model="passwordData.confirmarSenha" class="form-input" required minlength="6" placeholder="Repita a nova senha" />
                                        </div>
                                    </div>

                                    <div class="password-actions">
                                        <button type="submit" class="btn-save" :disabled="!senhaValida">
                                            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                                                <path d="M5 13l4 4L19 7"/>
                                            </svg>
                                            Alterar Senha
                                        </button>
                                    </div>
                                </form>
                            </Transition>
                        </div>
                    </div>
                </div>

                <!-- Preferências do Sistema -->
                <div class="config-card">
                    <div class="card-header">
                        <div class="header-left">
                            <div class="header-icon preferences">
                                <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
                                    <circle cx="12" cy="12" r="3"/>
                                    <path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-2 2 2 2 0 01-2-2v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83 0 2 2 0 010-2.83l.06-.06a1.65 1.65 0 00.33-1.82 1.65 1.65 0 00-1.51-1H3a2 2 0 01-2-2 2 2 0 012-2h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 010-2.83 2 2 0 012.83 0l.06.06a1.65 1.65 0 001.82.33H9a1.65 1.65 0 001-1.51V3a2 2 0 012-2 2 2 0 012 2v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 0 2 2 0 010 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 012 2 2 2 0 01-2 2h-.09a1.65 1.65 0 00-1.51 1z"/>
                                </svg>
                            </div>
                            <div>
                                <h2 class="card-title">Preferências</h2>
                                <p class="card-subtitle">Personalize sua experiência</p>
                            </div>
                        </div>
                    </div>

                    <div class="card-body">
                        <div class="preferences-grid">
                            <div class="preference-item">
                                <div class="preference-icon">
                                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                                        <polyline points="22,6 12,13 2,6"/>
                                    </svg>
                                </div>
                                <div class="preference-content">
                                    <label class="preference-label">Notificações por E-mail</label>
                                    <p class="preference-description">Receba atualizações importantes</p>
                                </div>
                                <label class="toggle-switch">
                                    <input type="checkbox" v-model="preferences.emailNotifications" :disabled="!isEditMode" />
                                    <span class="toggle-slider"></span>
                                </label>
                            </div>

                            <div class="preference-item">
                                <div class="preference-icon">
                                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                                        <path d="M13.73 21a2 2 0 01-3.46 0"/>
                                    </svg>
                                </div>
                                <div class="preference-content">
                                    <label class="preference-label">Notificações Push</label>
                                    <p class="preference-description">Alertas em tempo real</p>
                                </div>
                                <label class="toggle-switch">
                                    <input type="checkbox" v-model="preferences.pushNotifications" :disabled="!isEditMode" />
                                    <span class="toggle-slider"></span>
                                </label>
                            </div>

                            <div class="preference-item">
                                <div class="preference-icon">
                                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                                        <circle cx="12" cy="12" r="5"/>
                                        <line x1="12" y1="1" x2="12" y2="3"/>
                                        <line x1="12" y1="21" x2="12" y2="23"/>
                                        <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
                                        <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
                                        <line x1="1" y1="12" x2="3" y2="12"/>
                                        <line x1="21" y1="12" x2="23" y2="12"/>
                                        <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
                                        <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
                                    </svg>
                                </div>
                                <div class="preference-content">
                                    <label class="preference-label">Tema da Interface</label>
                                    <p class="preference-description">Aparência do sistema</p>
                                </div>
                                <select v-model="preferences.theme" class="form-select" :disabled="!isEditMode">
                                    <option value="light">Claro</option>
                                    <option value="dark">Escuro</option>
                                    <option value="auto">Automático</option>
                                </select>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Notificação -->
        <Transition name="slide-fade">
            <div v-if="showNotification" :class="['notification', 'notification-' + notificationType]">
                <div class="notification-icon-wrapper">
                    <svg v-if="notificationType === 'success'" viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                    </svg>
                    <svg v-else viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                    </svg>
                </div>
                <div class="notification-text">
                    <span class="notification-title">{{ notificationType === 'success' ? 'Sucesso!' : 'Atenção' }}</span>
                    <span class="notification-message">{{ notificationMessage }}</span>
                </div>
                <button class="notification-close" @click="hideNotification">
                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M6 18L18 6M6 6l12 12"/>
                    </svg>
                </button>
            </div>
        </Transition>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import logger from '../utils/logger.js'

// Estados reativo
const isEditMode = ref(false)
const showPasswordForm = ref(false)
const showNotification = ref(false)
const notificationType = ref('success')
const notificationMessage = ref('')

// Dados de senha
const passwordData = ref({
    senhaAtual: '',
    novaSenha: '',
    confirmarSenha: ''
})

// Preferências
const preferences = ref({
    emailNotifications: true,
    pushNotifications: false,
    theme: 'light'
})

// Dados originais para cancelamento
const originalPreferences = ref({})

// Computed
const senhaValida = computed(() => {
    return passwordData.value.senhaAtual &&
        passwordData.value.novaSenha &&
        passwordData.value.confirmarSenha &&
        passwordData.value.novaSenha === passwordData.value.confirmarSenha &&
        passwordData.value.novaSenha.length >= 6
})

// Métodos
const toggleEditMode = async () => {
    if (isEditMode.value) {
        await salvarConfiguracoes()
    } else {
        originalPreferences.value = { ...preferences.value }
        isEditMode.value = true
    }
}

const cancelarEdicao = () => {
    preferences.value = { ...originalPreferences.value }
    isEditMode.value = false
    showPasswordForm.value = false
    limparPasswordForm()
}

const salvarConfiguracoes = async () => {
    try {
        showNotificationMessage('success', 'Configurações atualizadas com sucesso!')
        isEditMode.value = false
    } catch (error) {
        logger.error('Erro ao atualizar configurações:', error)
        showNotificationMessage('error', 'Erro ao atualizar configurações. Tente novamente.')
    }
}

const alterarSenha = async () => {
    if (!senhaValida.value) {
        showNotificationMessage('error', 'Verifique os dados da senha')
        return
    }

    try {
        showNotificationMessage('success', 'Senha alterada com sucesso!')
        showPasswordForm.value = false
        limparPasswordForm()
    } catch (error) {
        logger.error('Erro ao alterar senha:', error)
        showNotificationMessage('error', 'Erro ao alterar senha. Verifique a senha atual.')
    }
}

const limparPasswordForm = () => {
    passwordData.value = {
        senhaAtual: '',
        novaSenha: '',
        confirmarSenha: ''
    }
}

const togglePasswordForm = () => {
    showPasswordForm.value = !showPasswordForm.value
    if (!showPasswordForm.value) {
        limparPasswordForm()
    }
}

const showNotificationMessage = (type, message) => {
    notificationType.value = type
    notificationMessage.value = message
    showNotification.value = true
    setTimeout(() => {
        hideNotification()
    }, 4000)
}

const hideNotification = () => {
    showNotification.value = false
}

// Lifecycle
onMounted(() => {
    // Carregar configurações do usuário
})
</script>

<style scoped>
.configuracoes-view {
    min-height: 100vh;
    background: #f8fafc;
}

.main-content {
    margin-left: 240px;
    margin-top: 70px;
    padding: 32px;
    min-height: calc(100vh - 70px);
}

.configuracoes-container {
    max-width: 800px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 24px;
}

/* Card Principal */
.config-main-card {
    background: white;
    border-radius: 20px;
    overflow: hidden;
    box-shadow:
        0 4px 6px -1px rgba(0, 0, 0, 0.1),
        0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.config-banner {
    height: 100px;
    background: linear-gradient(135deg, #1F285F 0%, #2d3a7c 50%, #3d4d99 100%);
    position: relative;
    display: flex;
    align-items: center;
    padding: 0 32px;
}

.banner-pattern {
    position: absolute;
    inset: 0;
    background-image:
        radial-gradient(circle at 20% 50%, rgba(255,255,255,0.1) 0%, transparent 50%),
        radial-gradient(circle at 80% 20%, rgba(255,255,255,0.1) 0%, transparent 40%);
}

.banner-content {
    display: flex;
    align-items: center;
    gap: 20px;
    position: relative;
    z-index: 1;
}

.banner-icon {
    width: 56px;
    height: 56px;
    border-radius: 14px;
    background: rgba(255,255,255,0.15);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    backdrop-filter: blur(10px);
}

.banner-text {
    color: white;
}

.page-title {
    font-size: 24px;
    font-weight: 700;
    margin: 0 0 4px;
}

.page-subtitle {
    font-size: 14px;
    opacity: 0.85;
    margin: 0;
}

.config-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding: 16px 24px;
    background: #f9fafb;
    border-top: 1px solid #f3f4f6;
}

/* Cards de Configuração */
.config-card {
    background: white;
    border-radius: 20px;
    overflow: hidden;
    box-shadow:
        0 4px 6px -1px rgba(0, 0, 0, 0.1),
        0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24px 28px;
    border-bottom: 1px solid #f3f4f6;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 16px;
}

.header-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.header-icon.security {
    background: linear-gradient(135deg, #fef3c7, #fde68a);
    color: #92400e;
}

.header-icon.preferences {
    background: linear-gradient(135deg, #EAF0FC, #d6e4f7);
    color: #1F285F;
}

.card-title {
    font-size: 18px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 2px;
}

.card-subtitle {
    font-size: 13px;
    color: #9ca3af;
    margin: 0;
}

.card-body {
    padding: 28px;
}

/* Seção de Segurança */
.security-section {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.btn-password {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 14px 24px;
    background: white;
    color: #1F285F;
    border: 2px solid #1F285F;
    border-radius: 12px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
    width: fit-content;
}

.btn-password:hover:not(:disabled) {
    background: #1F285F;
    color: white;
}

.btn-password:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.password-form {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 20px;
    padding: 24px;
    background: #f9fafb;
    border-radius: 14px;
    border: 1px solid #e5e7eb;
}

.form-group {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.form-label {
    font-size: 13px;
    font-weight: 600;
    color: #374151;
}

.input-wrapper {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    background: white;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    transition: all 0.2s;
}

.input-wrapper:focus-within {
    border-color: #1F285F;
    box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.1);
}

.input-wrapper svg {
    color: #9ca3af;
    flex-shrink: 0;
}

.input-wrapper .form-input {
    flex: 1;
    border: none;
    padding: 0;
    font-size: 14px;
    background: transparent;
    outline: none;
}

.password-actions {
    grid-column: 1 / -1;
    display: flex;
    justify-content: flex-end;
    margin-top: 8px;
}

/* Grid de Preferências */
.preferences-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
}

.preference-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: #f9fafb;
    border-radius: 14px;
    border: 1px solid #f3f4f6;
    transition: all 0.2s;
}

.preference-icon {
    width: 44px;
    height: 44px;
    border-radius: 12px;
    background: white;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #1F285F;
    flex-shrink: 0;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.preference-content {
    flex: 1;
    min-width: 0;
}

.preference-label {
    display: block;
    font-size: 14px;
    font-weight: 600;
    color: #1f2937;
    margin-bottom: 2px;
}

.preference-description {
    font-size: 12px;
    color: #9ca3af;
    margin: 0;
}

/* Toggle Switch */
.toggle-switch {
    position: relative;
    display: inline-block;
    width: 48px;
    height: 26px;
    flex-shrink: 0;
}

.toggle-switch input {
    opacity: 0;
    width: 0;
    height: 0;
}

.toggle-slider {
    position: absolute;
    cursor: pointer;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: #e5e7eb;
    transition: 0.3s;
    border-radius: 26px;
}

.toggle-slider:before {
    position: absolute;
    content: "";
    height: 20px;
    width: 20px;
    left: 3px;
    bottom: 3px;
    background-color: white;
    transition: 0.3s;
    border-radius: 50%;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

input:checked + .toggle-slider {
    background: linear-gradient(135deg, #1F285F, #3d4d99);
}

input:checked + .toggle-slider:before {
    transform: translateX(22px);
}

input:disabled + .toggle-slider {
    opacity: 0.5;
    cursor: not-allowed;
}

/* Select */
.form-select {
    padding: 10px 14px;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    font-size: 13px;
    background: white;
    color: #374151;
    cursor: pointer;
    min-width: 140px;
    transition: all 0.2s;
}

.form-select:focus {
    outline: none;
    border-color: #1F285F;
    box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.1);
}

.form-select:disabled {
    background: #f9fafb;
    color: #9ca3af;
    cursor: not-allowed;
}

/* Botões */
.btn-primary {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    background: #1F285F;
    color: white;
    border: none;
    border-radius: 10px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
    box-shadow: 0 4px 12px rgba(31, 40, 95, 0.3);
}

.btn-primary:hover {
    background: #2d3a7c;
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(31, 40, 95, 0.4);
}

.btn-cancel {
    padding: 12px 24px;
    background: white;
    color: #6b7280;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
}

.btn-cancel:hover {
    background: #f9fafb;
    border-color: #d1d5db;
}

.btn-save {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 12px 24px;
    background: linear-gradient(135deg, #10b981, #059669);
    color: white;
    border: none;
    border-radius: 10px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.btn-save:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(16, 185, 129, 0.4);
}

.btn-save:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

/* Notificação */
.notification {
    position: fixed;
    top: 90px;
    right: 24px;
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px 20px;
    background: white;
    border-radius: 16px;
    box-shadow:
        0 10px 40px rgba(0, 0, 0, 0.15),
        0 4px 12px rgba(0, 0, 0, 0.1);
    z-index: 1000;
    min-width: 320px;
    border-left: 4px solid;
}

.notification-success {
    border-left-color: #10b981;
}

.notification-error {
    border-left-color: #ef4444;
}

.notification-icon-wrapper {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.notification-success .notification-icon-wrapper {
    background: #d1fae5;
    color: #059669;
}

.notification-error .notification-icon-wrapper {
    background: #fee2e2;
    color: #dc2626;
}

.notification-text {
    display: flex;
    flex-direction: column;
    flex: 1;
}

.notification-title {
    font-size: 14px;
    font-weight: 600;
    color: #1f2937;
}

.notification-message {
    font-size: 13px;
    color: #6b7280;
}

.notification-close {
    background: none;
    border: none;
    color: #9ca3af;
    cursor: pointer;
    padding: 4px;
    border-radius: 6px;
    transition: all 0.2s;
}

.notification-close:hover {
    background: #f3f4f6;
    color: #6b7280;
}

/* Transições */
.slide-fade-enter-active {
    transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
    transition: all 0.2s ease-in;
}

.slide-fade-enter-from {
    transform: translateX(100%);
    opacity: 0;
}

.slide-fade-leave-to {
    transform: translateX(100%);
    opacity: 0;
}

.slide-down-enter-active {
    transition: all 0.3s ease-out;
}

.slide-down-leave-active {
    transition: all 0.2s ease-in;
}

.slide-down-enter-from {
    transform: translateY(-20px);
    opacity: 0;
}

.slide-down-leave-to {
    transform: translateY(-20px);
    opacity: 0;
}

/* Responsividade */
@media (max-width: 1024px) {
    .main-content {
        margin-left: 0;
        padding: 20px;
    }
}

@media (max-width: 768px) {
    .main-content {
        padding: 16px;
    }

    .config-banner {
        padding: 0 20px;
    }

    .banner-content {
        gap: 16px;
    }

    .banner-icon {
        width: 48px;
        height: 48px;
    }

    .page-title {
        font-size: 20px;
    }

    .config-actions {
        flex-direction: column;
    }

    .btn-primary,
    .btn-cancel {
        width: 100%;
        justify-content: center;
    }

    .card-header {
        padding: 20px;
    }

    .card-body {
        padding: 20px;
    }

    .preferences-grid {
        grid-template-columns: 1fr;
    }

    .preference-item {
        flex-wrap: wrap;
    }

    .preference-content {
        flex: 1 1 100%;
        order: -1;
        margin-bottom: 12px;
    }

    .preference-icon {
        order: 0;
    }

    .password-form {
        padding: 16px;
    }

    .notification {
        left: 16px;
        right: 16px;
        min-width: auto;
    }
}

@media (max-width: 480px) {
    .preference-item {
        padding: 16px;
    }

    .header-icon {
        width: 40px;
        height: 40px;
    }

    .preference-icon {
        width: 36px;
        height: 36px;
    }
}
</style>
