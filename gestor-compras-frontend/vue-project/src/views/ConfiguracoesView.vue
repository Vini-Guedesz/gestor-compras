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
                        <div class="header-actions" v-if="abaAtiva !== 'sobre'">
                            <button class="btn-secondary" @click="cancelarEdicao" v-if="isEditMode">
                                Cancelar
                            </button>
                            <button class="btn-primary" @click="toggleEditMode">
                                {{ isEditMode ? 'Salvar Alterações' : 'Editar Configurações' }}
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Abas de Navegação -->
                <div class="tabs-container">
                    <button
                        class="tab-button"
                        :class="{ active: abaAtiva === 'geral' }"
                        @click="abaAtiva = 'geral'"
                    >
                        <svg viewBox="0 0 24 24" width="18" height="18">
                            <path fill="currentColor" d="M12,15.5A3.5,3.5 0 0,1 8.5,12A3.5,3.5 0 0,1 12,8.5A3.5,3.5 0 0,1 15.5,12A3.5,3.5 0 0,1 12,15.5M19.43,12.97C19.47,12.65 19.5,12.33 19.5,12C19.5,11.67 19.47,11.34 19.43,11L21.54,9.37C21.73,9.22 21.78,8.95 21.66,8.73L19.66,5.27C19.54,5.05 19.27,4.96 19.05,5.05L16.56,6.05C16.04,5.66 15.5,5.32 14.87,5.07L14.5,2.42C14.46,2.18 14.25,2 14,2H10C9.75,2 9.54,2.18 9.5,2.42L9.13,5.07C8.5,5.32 7.96,5.66 7.44,6.05L4.95,5.05C4.73,4.96 4.46,5.05 4.34,5.27L2.34,8.73C2.21,8.95 2.27,9.22 2.46,9.37L4.57,11C4.53,11.34 4.5,11.67 4.5,12C4.5,12.33 4.53,12.65 4.57,12.97L2.46,14.63C2.27,14.78 2.21,15.05 2.34,15.27L4.34,18.73C4.46,18.95 4.73,19.03 4.95,18.95L7.44,17.94C7.96,18.34 8.5,18.68 9.13,18.93L9.5,21.58C9.54,21.82 9.75,22 10,22H14C14.25,22 14.46,21.82 14.5,21.58L14.87,18.93C15.5,18.67 16.04,18.34 16.56,17.94L19.05,18.95C19.27,19.03 19.54,18.95 19.66,18.73L21.66,15.27C21.78,15.05 21.73,14.78 21.54,14.63L19.43,12.97Z"/>
                        </svg>
                        Geral
                    </button>
                    <button
                        class="tab-button"
                        :class="{ active: abaAtiva === 'sobre' }"
                        @click="abaAtiva = 'sobre'"
                    >
                        <svg viewBox="0 0 24 24" width="18" height="18">
                            <path fill="currentColor" d="M11,9H13V7H11M12,20C7.59,20 4,16.41 4,12C4,7.59 7.59,4 12,4C16.41,4 20,7.59 20,12C20,16.41 16.41,20 12,20M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M11,17H13V11H11V17Z"/>
                        </svg>
                        Sobre
                    </button>
                </div>

                <div class="configuracoes-content">
                    <!-- Aba Geral: Configurações -->
                    <div v-if="abaAtiva === 'geral'" class="section-card">
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

                    <!-- Aba Sobre: Informações do Sistema -->
                    <div v-if="abaAtiva === 'sobre'" class="section-card">
                        <div class="about-section">
                            <div class="about-header">
                                <div class="app-logo">
                                    <svg viewBox="0 0 24 24" width="64" height="64">
                                        <path fill="#1F285F" d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm-1 16H9V7h9v14z"/>
                                    </svg>
                                </div>
                                <h2 class="app-name">Gestor de Compras</h2>
                                <p class="app-tagline">Sistema de Gestão de Pedidos e Cotações</p>
                            </div>

                            <div class="about-info">
                                <div class="info-grid-about">
                                    <div class="info-item-about">
                                        <span class="info-label-about">Versão</span>
                                        <span class="info-value-about">{{ appVersion }}</span>
                                    </div>
                                    <div class="info-item-about">
                                        <span class="info-label-about">Ambiente</span>
                                        <span class="info-value-about">{{ environment }}</span>
                                    </div>
                                    <div class="info-item-about">
                                        <span class="info-label-about">Framework</span>
                                        <span class="info-value-about">Vue.js {{ vueVersion }}</span>
                                    </div>
                                    <div class="info-item-about">
                                        <span class="info-label-about">Build Date</span>
                                        <span class="info-value-about">{{ buildDate }}</span>
                                    </div>
                                </div>
                            </div>

                            <div class="section-divider"></div>

                            <div class="about-description">
                                <h3 class="subsection-title">Sobre o Sistema</h3>
                                <p class="description-text">
                                    O Gestor de Compras é um sistema completo para gerenciamento de pedidos,
                                    cotações e fornecedores. Desenvolvido com as mais modernas tecnologias web,
                                    oferece uma interface intuitiva e responsiva para otimizar seus processos de compra.
                                </p>
                            </div>

                            <div class="section-divider"></div>

                            <div class="about-features">
                                <h3 class="subsection-title">Recursos Principais</h3>
                                <div class="features-grid">
                                    <div class="feature-item">
                                        <svg viewBox="0 0 24 24" width="24" height="24">
                                            <path fill="#10b981" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z"/>
                                        </svg>
                                        <span>Gestão de Pedidos de Compra</span>
                                    </div>
                                    <div class="feature-item">
                                        <svg viewBox="0 0 24 24" width="24" height="24">
                                            <path fill="#10b981" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z"/>
                                        </svg>
                                        <span>Sistema de Cotações</span>
                                    </div>
                                    <div class="feature-item">
                                        <svg viewBox="0 0 24 24" width="24" height="24">
                                            <path fill="#10b981" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z"/>
                                        </svg>
                                        <span>Cadastro de Fornecedores</span>
                                    </div>
                                    <div class="feature-item">
                                        <svg viewBox="0 0 24 24" width="24" height="24">
                                            <path fill="#10b981" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z"/>
                                        </svg>
                                        <span>Workflow de Aprovações</span>
                                    </div>
                                    <div class="feature-item">
                                        <svg viewBox="0 0 24 24" width="24" height="24">
                                            <path fill="#10b981" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z"/>
                                        </svg>
                                        <span>Controle de Usuários</span>
                                    </div>
                                    <div class="feature-item">
                                        <svg viewBox="0 0 24 24" width="24" height="24">
                                            <path fill="#10b981" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z"/>
                                        </svg>
                                        <span>Dashboard e Relatórios</span>
                                    </div>
                                </div>
                            </div>

                            <div class="section-divider"></div>

                            <div class="about-tech">
                                <h3 class="subsection-title">Tecnologias Utilizadas</h3>
                                <div class="tech-stack">
                                    <div class="tech-item">
                                        <span class="tech-name">Frontend</span>
                                        <span class="tech-value">Vue.js 3, Vite, Pinia</span>
                                    </div>
                                    <div class="tech-item">
                                        <span class="tech-name">Backend</span>
                                        <span class="tech-value">Java Spring Boot</span>
                                    </div>
                                    <div class="tech-item">
                                        <span class="tech-name">Banco de Dados</span>
                                        <span class="tech-value">PostgreSQL</span>
                                    </div>
                                </div>
                            </div>

                            <div class="about-footer">
                                <p class="copyright">© 2025 Gestor de Compras. Todos os direitos reservados.</p>
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
import { version } from 'vue'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import logger from '../utils/logger.js'
import packageInfo from '../../package.json'

// Estados reativo
const abaAtiva = ref('geral')
const isEditMode = ref(false)
const showPasswordForm = ref(false)
const showNotification = ref(false)
const notificationType = ref('success')
const notificationMessage = ref('')

// Informações do sistema
const appVersion = ref(packageInfo.version)
const vueVersion = ref(version)
const environment = ref(import.meta.env.MODE === 'production' ? 'Produção' : 'Desenvolvimento')
const buildDate = ref(new Date().toLocaleDateString('pt-BR'))

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
        // Aqui você faria a chamada para a API
        // await authService.alterarSenha(passwordData.value)

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
    margin: 0 auto 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    max-width: 1000px;
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
    max-width: 1000px;
    margin: 0 auto;
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

/* Abas de Navegação */
.tabs-container {
    display: flex;
    gap: 8px;
    margin-bottom: 24px;
    max-width: 1000px;
    margin-left: auto;
    margin-right: auto;
}

.tab-button {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 12px 20px;
    background: white;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    font-size: 0.9375rem;
    font-weight: 500;
    color: #6b7280;
    cursor: pointer;
    transition: all 0.2s;
}

.tab-button:hover {
    background: #f9fafb;
    border-color: #d1d5db;
}

.tab-button.active {
    background: #1F285F;
    color: white;
    border-color: #1F285F;
}

.tab-button.active svg path {
    fill: white;
}

.tab-button svg {
    flex-shrink: 0;
}

/* Seção Sobre */
.about-section {
    padding: 32px;
}

.about-header {
    text-align: center;
    padding-bottom: 32px;
}

.app-logo {
    margin: 0 auto 16px;
    width: 64px;
    height: 64px;
}

.app-name {
    font-size: 2rem;
    font-weight: 700;
    color: #1F285F;
    margin: 0 0 8px 0;
}

.app-tagline {
    font-size: 1rem;
    color: #6b7280;
    margin: 0;
}

.about-info {
    margin-bottom: 32px;
}

.info-grid-about {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
}

.info-item-about {
    display: flex;
    flex-direction: column;
    gap: 6px;
    padding: 16px;
    background: #f9fafb;
    border-radius: 8px;
    border: 1px solid #e5e7eb;
}

.info-label-about {
    font-size: 0.8125rem;
    color: #6b7280;
    font-weight: 500;
}

.info-value-about {
    font-size: 1.125rem;
    color: #1f2937;
    font-weight: 600;
}

.subsection-title {
    font-size: 1.125rem;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 16px 0;
}

.about-description {
    margin-bottom: 32px;
}

.description-text {
    font-size: 0.9375rem;
    color: #6b7280;
    line-height: 1.6;
    margin: 0;
}

.about-features {
    margin-bottom: 32px;
}

.features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 16px;
}

.feature-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    background: #f9fafb;
    border-radius: 8px;
    border: 1px solid #e5e7eb;
}

.feature-item svg {
    flex-shrink: 0;
}

.feature-item span {
    font-size: 0.9375rem;
    color: #374151;
}

.about-tech {
    margin-bottom: 32px;
}

.tech-stack {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.tech-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    background: #f9fafb;
    border-radius: 8px;
    border: 1px solid #e5e7eb;
}

.tech-name {
    font-size: 0.9375rem;
    font-weight: 600;
    color: #1f2937;
}

.tech-value {
    font-size: 0.9375rem;
    color: #6b7280;
}

.about-footer {
    text-align: center;
    padding-top: 32px;
    border-top: 1px solid #e5e7eb;
}

.copyright {
    font-size: 0.875rem;
    color: #9ca3af;
    margin: 0;
}

@media (max-width: 768px) {
    .tabs-container {
        flex-direction: column;
    }

    .info-grid-about {
        grid-template-columns: 1fr;
    }

    .features-grid {
        grid-template-columns: 1fr;
    }

    .about-section {
        padding: 20px;
    }

    .app-name {
        font-size: 1.5rem;
    }

    .tech-item {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
    }
}
</style>
