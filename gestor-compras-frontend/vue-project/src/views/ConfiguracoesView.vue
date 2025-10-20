<template>
    <div class="configuracoes-view">
        <!-- Header -->
        <DashboardHeader />

        <!-- Sidebar -->
        <DashboardSidebar />

        <!-- Conteúdo Principal -->
        <div class="main-content">
            <div class="configuracoes-container">
                <div class="configuracoes-header">
                    <div class="header-content">
                        <div class="header-info">
                            <h1 class="page-title">Configurações</h1>
                            <p class="page-subtitle">Gerencie suas configurações de segurança e preferências do sistema</p>
                        </div>
                        <div class="header-actions">
                            <button class="btn-secondary" @click="cancelarEdicao" v-if="isEditMode">
                                Cancelar
                            </button>
                            <button class="btn-primary" @click="toggleEditMode">
                                {{ isEditMode ? 'Salvar Alterações' : 'Editar Configurações' }}
                            </button>
                        </div>
                    </div>
                </div>

                <div class="configuracoes-content">
                    <!-- Card Único com todas as configurações -->
                    <div class="section-card">
                        <!-- Seção de Segurança -->
                        <div class="section-group">
                            <div class="section-header">
                                <h3 class="section-title">Segurança</h3>
                                <p class="section-subtitle">Mantenha sua conta segura</p>
                            </div>

                            <div class="password-section">
                                <button class="btn-outline" @click="togglePasswordForm" :disabled="!isEditMode">
                                    {{ showPasswordForm ? 'Cancelar Alteração' : 'Alterar Senha' }}
                                </button>

                                <form v-if="showPasswordForm" @submit.prevent="alterarSenha" class="password-form">
                                    <div class="form-group">
                                        <label class="form-label">Senha Atual</label>
                                        <input type="password" v-model="passwordData.senhaAtual" class="form-input"
                                            required />
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label">Nova Senha</label>
                                        <input type="password" v-model="passwordData.novaSenha" class="form-input" required
                                            minlength="6" />
                                    </div>

                                    <div class="form-group">
                                        <label class="form-label">Confirmar Nova Senha</label>
                                        <input type="password" v-model="passwordData.confirmarSenha" class="form-input"
                                            required minlength="6" />
                                    </div>

                                    <div class="password-actions">
                                        <button type="submit" class="btn-primary" :disabled="!senhaValida">
                                            Alterar Senha
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <!-- Divisor -->
                        <div class="section-divider"></div>

                        <!-- Preferências do Sistema -->
                        <div class="section-group">
                            <div class="section-header">
                                <h3 class="section-title">Preferências do Sistema</h3>
                                <p class="section-subtitle">Personalize sua experiência</p>
                            </div>

                            <div class="preferences-form">
                                <div class="preference-item">
                                    <div class="preference-info">
                                        <label class="preference-label">Notificações por E-mail</label>
                                        <p class="preference-description">Receba atualizações importantes por e-mail</p>
                                    </div>
                                    <div class="preference-control">
                                        <label class="toggle-switch">
                                            <input type="checkbox" v-model="preferences.emailNotifications"
                                                :disabled="!isEditMode" />
                                            <span class="toggle-slider"></span>
                                        </label>
                                    </div>
                                </div>

                                <div class="preference-item">
                                    <div class="preference-info">
                                        <label class="preference-label">Notificações Push</label>
                                        <p class="preference-description">Receba notificações em tempo real</p>
                                    </div>
                                    <div class="preference-control">
                                        <label class="toggle-switch">
                                            <input type="checkbox" v-model="preferences.pushNotifications"
                                                :disabled="!isEditMode" />
                                            <span class="toggle-slider"></span>
                                        </label>
                                    </div>
                                </div>

                                <div class="preference-item">
                                    <div class="preference-info">
                                        <label class="preference-label">Tema da Interface</label>
                                        <p class="preference-description">Escolha entre tema claro ou escuro</p>
                                    </div>
                                    <div class="preference-control">
                                        <select v-model="preferences.theme" class="form-select" :disabled="!isEditMode">
                                            <option value="light">Claro</option>
                                            <option value="dark">Escuro</option>
                                            <option value="auto">Automático</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="preference-item">
                                    <div class="preference-info">
                                        <label class="preference-label">Idioma</label>
                                        <p class="preference-description">Idioma da interface</p>
                                    </div>
                                    <div class="preference-control">
                                        <select v-model="preferences.language" class="form-select" :disabled="!isEditMode">
                                            <option value="pt-BR">Português (Brasil)</option>
                                            <option value="en-US">English (US)</option>
                                            <option value="es-ES">Español</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="preference-item">
                                    <div class="preference-info">
                                        <label class="preference-label">Formato de Data</label>
                                        <p class="preference-description">Como as datas são exibidas no sistema</p>
                                    </div>
                                    <div class="preference-control">
                                        <select v-model="preferences.dateFormat" class="form-select" :disabled="!isEditMode">
                                            <option value="DD/MM/YYYY">DD/MM/AAAA</option>
                                            <option value="MM/DD/YYYY">MM/DD/AAAA</option>
                                            <option value="YYYY-MM-DD">AAAA-MM-DD</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="preference-item">
                                    <div class="preference-info">
                                        <label class="preference-label">Formato de Moeda</label>
                                        <p class="preference-description">Como os valores monetários são exibidos</p>
                                    </div>
                                    <div class="preference-control">
                                        <select v-model="preferences.currencyFormat" class="form-select" :disabled="!isEditMode">
                                            <option value="BRL">Real (R$ 1.234,56)</option>
                                            <option value="USD">Dólar ($ 1,234.56)</option>
                                            <option value="EUR">Euro (€ 1.234,56)</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Divisor -->
                        <div class="section-divider"></div>

                        <!-- Configurações de Privacidade -->
                        <div class="section-group">
                            <div class="section-header">
                                <h3 class="section-title">Privacidade</h3>
                                <p class="section-subtitle">Controle suas informações pessoais</p>
                            </div>

                            <div class="preferences-form">
                                <div class="preference-item">
                                    <div class="preference-info">
                                        <label class="preference-label">Perfil Público</label>
                                        <p class="preference-description">Permitir que outros usuários vejam seu perfil</p>
                                    </div>
                                    <div class="preference-control">
                                        <label class="toggle-switch">
                                            <input type="checkbox" v-model="preferences.publicProfile"
                                                :disabled="!isEditMode" />
                                            <span class="toggle-slider"></span>
                                        </label>
                                    </div>
                                </div>

                                <div class="preference-item">
                                    <div class="preference-info">
                                        <label class="preference-label">Histórico de Atividades</label>
                                        <p class="preference-description">Salvar histórico das suas ações no sistema</p>
                                    </div>
                                    <div class="preference-control">
                                        <label class="toggle-switch">
                                            <input type="checkbox" v-model="preferences.activityHistory"
                                                :disabled="!isEditMode" />
                                            <span class="toggle-slider"></span>
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>
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
import { ref, computed, onMounted } from 'vue'
import DashboardHeader from '../components/DashboardHeader.vue'
import DashboardSidebar from '../components/DashboardSidebar.vue'

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
    theme: 'light',
    language: 'pt-BR',
    dateFormat: 'DD/MM/YYYY',
    currencyFormat: 'BRL',
    publicProfile: true,
    activityHistory: true
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
        // Salvar alterações
        await salvarConfiguracoes()
    } else {
        // Entrar em modo de edição
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
        // Aqui você faria a chamada para a API
        // await configuracoesService.atualizarConfiguracoes(preferences.value)

        showNotificationMessage('success', 'Configurações atualizadas com sucesso!')
        isEditMode.value = false
    } catch (error) {
        console.error('Erro ao atualizar configurações:', error)
        showNotificationMessage('error', 'Erro ao atualizar configurações. Tente novamente.')
    }
}

const alterarSenha = async () => {
    if (!senhaValida.value) {
        showNotificationMessage('error', 'Verifique os dados da senha')
        return
    }

    try {
        // Aqui você faria a chamada para a API
        // await authService.alterarSenha(passwordData.value)

        showNotificationMessage('success', 'Senha alterada com sucesso!')
        showPasswordForm.value = false
        limparPasswordForm()
    } catch (error) {
        console.error('Erro ao alterar senha:', error)
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
    }, 5000)
}

const hideNotification = () => {
    showNotification.value = false
}

// Lifecycle
onMounted(() => {
    // Carregar configurações do usuário da store ou API
    // Se necessário, buscar dados das configurações do usuário
})
</script>

<style scoped>
.configuracoes-view {
    min-height: 100vh;
    background-color: #f8fafc;
}

.main-content {
    margin-left: 240px;
    margin-top: 70px;
    padding: 24px;
}

.configuracoes-container {
    max-width: 1200px;
    margin: 0 auto;
}

.configuracoes-header {
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

.configuracoes-content {
    display: flex;
    flex-direction: column;
}

.section-card {
    background: white;
    border-radius: 12px;
    border: 1px solid #e5e7eb;
    padding: 32px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.section-group {
    margin-bottom: 0;
}

.section-group:last-child {
    margin-bottom: 0;
}

.section-divider {
    height: 1px;
    background: linear-gradient(to right, transparent, #e5e7eb 20%, #e5e7eb 80%, transparent);
    margin: 32px 0;
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

.password-section {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.password-form {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    padding: 20px;
    background: #f9fafb;
    border-radius: 8px;
    border: 1px solid #e5e7eb;
}

.password-actions {
    grid-column: 1 / -1;
    display: flex;
    justify-content: flex-end;
}

.preferences-form {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.preference-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    background: #f9fafb;
}

.preference-info {
    flex: 1;
}

.preference-label {
    font-size: 14px;
    font-weight: 500;
    color: #374151;
    margin: 0 0 4px 0;
}

.preference-description {
    font-size: 12px;
    color: #6b7280;
    margin: 0;
}

.preference-control {
    display: flex;
    align-items: center;
}

.toggle-switch {
    position: relative;
    display: inline-block;
    width: 44px;
    height: 24px;
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
    background-color: #cbd5e1;
    transition: 0.3s;
    border-radius: 24px;
}

.toggle-slider:before {
    position: absolute;
    content: "";
    height: 18px;
    width: 18px;
    left: 3px;
    bottom: 3px;
    background-color: white;
    transition: 0.3s;
    border-radius: 50%;
}

input:checked+.toggle-slider {
    background-color: #3b82f6;
}

input:checked+.toggle-slider:before {
    transform: translateX(20px);
}

input:disabled+.toggle-slider {
    opacity: 0.5;
    cursor: not-allowed;
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

    .preference-item {
        flex-direction: column;
        align-items: stretch;
        gap: 12px;
    }

    .preference-control {
        justify-content: flex-end;
    }
}
</style>
