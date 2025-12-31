<template>
    <div class="perfil-view">
        <!-- Header -->
        <DashboardHeader />

        <!-- Sidebar -->
        <DashboardSidebar />

        <!-- Conteúdo Principal -->
        <div class="main-content">
            <div class="perfil-container">
                <!-- Card Principal do Perfil -->
                <div class="profile-main-card">
                    <!-- Banner Gradiente -->
                    <div class="profile-banner">
                        <div class="banner-pattern"></div>
                    </div>

                    <!-- Conteúdo do Perfil -->
                    <div class="profile-body">
                        <!-- Avatar -->
                        <div class="avatar-wrapper">
                            <div class="avatar-ring">
                                <img :src="userAvatar" :alt="formData.nome" class="user-avatar" loading="eager" />
                            </div>
                            <div class="status-indicator online"></div>
                        </div>

                        <!-- Info do Usuário -->
                        <div class="user-info-section">
                            <h1 class="user-name">{{ formData.nome }}</h1>
                            <p class="user-email">{{ formData.email }}</p>
                            <span class="role-badge" :class="'role-' + formData.funcao.toLowerCase()">
                                <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                                    <path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm0 10.99h7c-.53 4.12-3.28 7.79-7 8.94V12H5V6.3l7-3.11v8.8z"/>
                                </svg>
                                {{ formatRole(formData.funcao) }}
                            </span>
                        </div>

                        <!-- Estatísticas Rápidas -->
                        <div class="quick-stats">
                            <div class="stat-item">
                                <div class="stat-icon">
                                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                                    </svg>
                                </div>
                                <div class="stat-content">
                                    <span class="stat-value">Ativo</span>
                                    <span class="stat-label">Status</span>
                                </div>
                            </div>
                            <div class="stat-divider"></div>
                            <div class="stat-item">
                                <div class="stat-icon">
                                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                                    </svg>
                                </div>
                                <div class="stat-content">
                                    <span class="stat-value">{{ dataAtual }}</span>
                                    <span class="stat-label">Membro desde</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Card de Informações -->
                <div class="info-card">
                    <div class="card-header">
                        <div class="header-left">
                            <div class="header-icon">
                                <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                                </svg>
                            </div>
                            <div>
                                <h2 class="card-title">Informações Pessoais</h2>
                                <p class="card-subtitle">Seus dados cadastrados no sistema</p>
                            </div>
                        </div>
                        <button class="btn-edit" @click="toggleEditMode" v-if="!isEditMode">
                            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
                            </svg>
                            Editar
                        </button>
                        <div class="edit-actions" v-else>
                            <button class="btn-cancel" @click="cancelarEdicao">Cancelar</button>
                            <button class="btn-save" @click="salvarPerfil">
                                <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M5 13l4 4L19 7"/>
                                </svg>
                                Salvar
                            </button>
                        </div>
                    </div>

                    <div class="card-body">
                        <div class="info-grid">
                            <!-- Nome -->
                            <div class="info-item">
                                <div class="info-icon">
                                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                                    </svg>
                                </div>
                                <div class="info-content">
                                    <label class="info-label">Nome Completo</label>
                                    <input type="text" v-model="formData.nome" class="info-input" disabled />
                                    <span class="info-hint">
                                        <svg viewBox="0 0 24 24" width="12" height="12" fill="currentColor">
                                            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 15h2v-6h-2v6zm0-8h2V7h-2v2z"/>
                                        </svg>
                                        Gerenciado pelo administrador
                                    </span>
                                </div>
                            </div>

                            <!-- Email -->
                            <div class="info-item">
                                <div class="info-icon">
                                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
                                    </svg>
                                </div>
                                <div class="info-content">
                                    <label class="info-label">E-mail</label>
                                    <input type="email" v-model="formData.email" class="info-input" disabled />
                                    <span class="info-hint">
                                        <svg viewBox="0 0 24 24" width="12" height="12" fill="currentColor">
                                            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 15h2v-6h-2v6zm0-8h2V7h-2v2z"/>
                                        </svg>
                                        Usado para login no sistema
                                    </span>
                                </div>
                            </div>

                            <!-- Telefone -->
                            <div class="info-item" :class="{ 'editable': isEditMode }">
                                <div class="info-icon">
                                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"/>
                                    </svg>
                                </div>
                                <div class="info-content">
                                    <label class="info-label">Telefone</label>
                                    <input
                                        type="tel"
                                        v-model="formData.telefone"
                                        class="info-input"
                                        :class="{ 'editable': isEditMode }"
                                        :disabled="!isEditMode"
                                        placeholder="(00) 00000-0000"
                                    />
                                    <span class="info-hint editable-hint" v-if="isEditMode">
                                        <svg viewBox="0 0 24 24" width="12" height="12" fill="currentColor">
                                            <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
                                        </svg>
                                        Você pode editar este campo
                                    </span>
                                </div>
                            </div>

                            <!-- Função/Role -->
                            <div class="info-item">
                                <div class="info-icon">
                                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/>
                                    </svg>
                                </div>
                                <div class="info-content">
                                    <label class="info-label">Função no Sistema</label>
                                    <div class="role-display">
                                        <span class="role-tag" :class="'role-' + formData.funcao.toLowerCase()">
                                            {{ formatRole(formData.funcao) }}
                                        </span>
                                    </div>
                                    <span class="info-hint">
                                        <svg viewBox="0 0 24 24" width="12" height="12" fill="currentColor">
                                            <path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4z"/>
                                        </svg>
                                        Define suas permissões de acesso
                                    </span>
                                </div>
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
import { ref, onMounted, computed } from 'vue'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import { useAuthStore } from '../stores/auth.js'
import logger from '../utils/logger.js'

const authStore = useAuthStore()

// Estados reativo
const isEditMode = ref(false)
const showNotification = ref(false)
const notificationType = ref('success')
const notificationMessage = ref('')

// Dados do formulário
const formData = ref({
    nome: authStore.user?.nome || 'Usuário',
    email: authStore.user?.email || '',
    telefone: '',
    funcao: authStore.user?.role || 'USER'
})

// Dados originais para cancelamento
const originalData = ref({})

// Avatar com iniciais do nome
const userAvatar = computed(() => {
    const nome = formData.value.nome || 'Usuario'
    const iniciais = nome.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase()
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(iniciais)}&background=1F285F&color=fff&size=150&font-size=0.4&bold=true`
})

// Data atual formatada
const dataAtual = computed(() => {
    return new Date().toLocaleDateString('pt-BR', { month: 'short', year: 'numeric' })
})

// Formatar role para exibição
const formatRole = (role) => {
    const roles = {
        'ADMIN': 'Administrador',
        'COMPRADOR': 'Comprador',
        'APROVADOR': 'Aprovador',
        'USUARIO': 'Usuário',
        'USER': 'Usuário'
    }
    return roles[role] || role
}

// Métodos
const toggleEditMode = () => {
    originalData.value = { ...formData.value }
    isEditMode.value = true
}

const cancelarEdicao = () => {
    formData.value = { ...originalData.value }
    isEditMode.value = false
}

const salvarPerfil = async () => {
    try {
        showNotificationMessage('success', 'Perfil atualizado com sucesso!')
        isEditMode.value = false
    } catch (error) {
        logger.error('Erro ao atualizar perfil:', error)
        showNotificationMessage('error', 'Erro ao atualizar perfil. Tente novamente.')
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
    if (authStore.user) {
        formData.value.nome = authStore.user.nome || 'Usuário'
        formData.value.email = authStore.user.email || ''
        formData.value.funcao = authStore.user.role || 'USER'
    }
})
</script>

<style scoped>
.perfil-view {
    min-height: 100vh;
    background: #f8fafc;
}

.main-content {
    margin-left: 240px;
    margin-top: 70px;
    padding: 32px;
    min-height: calc(100vh - 70px);
}

.perfil-container {
    max-width: 800px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 24px;
}

/* Card Principal do Perfil */
.profile-main-card {
    background: white;
    border-radius: 24px;
    overflow: hidden;
    box-shadow:
        0 4px 6px -1px rgba(0, 0, 0, 0.1),
        0 2px 4px -1px rgba(0, 0, 0, 0.06),
        0 20px 25px -5px rgba(0, 0, 0, 0.05);
}

.profile-banner {
    height: 120px;
    background: linear-gradient(135deg, #1F285F 0%, #2d3a7c 50%, #3d4d99 100%);
    position: relative;
    overflow: hidden;
}

.banner-pattern {
    position: absolute;
    inset: 0;
    background-image:
        radial-gradient(circle at 20% 50%, rgba(255,255,255,0.1) 0%, transparent 50%),
        radial-gradient(circle at 80% 20%, rgba(255,255,255,0.1) 0%, transparent 40%),
        radial-gradient(circle at 40% 80%, rgba(255,255,255,0.05) 0%, transparent 40%);
}

.profile-body {
    padding: 0 32px 32px;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: -50px;
    position: relative;
}

/* Avatar */
.avatar-wrapper {
    position: relative;
    margin-bottom: 16px;
}

.avatar-ring {
    width: 110px;
    height: 110px;
    border-radius: 50%;
    padding: 4px;
    background: linear-gradient(135deg, #1F285F, #3d4d99);
    box-shadow: 0 8px 24px rgba(31, 40, 95, 0.3);
}

.user-avatar {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    border: 4px solid white;
    object-fit: cover;
}

.status-indicator {
    position: absolute;
    bottom: 8px;
    right: 8px;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    border: 3px solid white;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.status-indicator.online {
    background: linear-gradient(135deg, #10b981, #34d399);
}

/* Info do Usuário */
.user-info-section {
    text-align: center;
    margin-bottom: 24px;
}

.user-name {
    font-size: 28px;
    font-weight: 700;
    color: #1f2937;
    margin: 0 0 4px;
    letter-spacing: -0.5px;
}

.user-email {
    font-size: 15px;
    color: #6b7280;
    margin: 0 0 12px;
}

.role-badge {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 6px 14px;
    border-radius: 20px;
    font-size: 13px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.role-badge.role-admin {
    background: linear-gradient(135deg, #fef3c7, #fde68a);
    color: #92400e;
}

.role-badge.role-comprador {
    background: linear-gradient(135deg, #dbeafe, #bfdbfe);
    color: #1e40af;
}

.role-badge.role-aprovador {
    background: linear-gradient(135deg, #d1fae5, #a7f3d0);
    color: #065f46;
}

.role-badge.role-usuario,
.role-badge.role-user {
    background: linear-gradient(135deg, #e5e7eb, #d1d5db);
    color: #374151;
}

/* Estatísticas Rápidas */
.quick-stats {
    display: flex;
    align-items: center;
    gap: 24px;
    padding: 20px 32px;
    background: #f9fafb;
    border-radius: 16px;
    width: 100%;
    max-width: 400px;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 12px;
    flex: 1;
}

.stat-icon {
    width: 44px;
    height: 44px;
    border-radius: 12px;
    background: white;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #1F285F;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.stat-content {
    display: flex;
    flex-direction: column;
}

.stat-value {
    font-size: 15px;
    font-weight: 600;
    color: #1f2937;
}

.stat-label {
    font-size: 12px;
    color: #9ca3af;
}

.stat-divider {
    width: 1px;
    height: 40px;
    background: #e5e7eb;
}

/* Card de Informações */
.info-card {
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
    background: linear-gradient(to right, #fafafa, white);
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
    background: linear-gradient(135deg, #EAF0FC, #d6e4f7);
    display: flex;
    align-items: center;
    justify-content: center;
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

.btn-edit {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
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

.btn-edit:hover {
    background: #2d3a7c;
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(31, 40, 95, 0.4);
}

.edit-actions {
    display: flex;
    gap: 12px;
}

.btn-cancel {
    padding: 10px 20px;
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
    padding: 10px 20px;
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

.btn-save:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(16, 185, 129, 0.4);
}

.card-body {
    padding: 28px;
}

.info-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 24px;
}

.info-item {
    display: flex;
    gap: 16px;
    padding: 20px;
    background: #f9fafb;
    border-radius: 14px;
    border: 1px solid #f3f4f6;
    transition: all 0.2s;
}

.info-item.editable {
    background: #EAF0FC;
    border-color: #1F285F;
}

.info-icon {
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

.info-content {
    flex: 1;
    min-width: 0;
}

.info-label {
    display: block;
    font-size: 12px;
    font-weight: 600;
    color: #9ca3af;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    margin-bottom: 6px;
}

.info-input {
    width: 100%;
    padding: 0;
    border: none;
    background: transparent;
    font-size: 15px;
    font-weight: 500;
    color: #1f2937;
    outline: none;
}

.info-input:disabled {
    color: #374151;
    cursor: default;
}

.info-input.editable {
    padding: 8px 12px;
    background: white;
    border: 2px solid #1F285F;
    border-radius: 8px;
    cursor: text;
}

.info-input.editable:focus {
    box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.2);
}

.info-hint {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 11px;
    color: #9ca3af;
    margin-top: 6px;
}

.info-hint.editable-hint {
    color: #1F285F;
}

.role-display {
    margin-bottom: 4px;
}

.role-tag {
    display: inline-flex;
    padding: 6px 12px;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 600;
}

.role-tag.role-admin {
    background: linear-gradient(135deg, #fef3c7, #fde68a);
    color: #92400e;
}

.role-tag.role-comprador {
    background: linear-gradient(135deg, #dbeafe, #bfdbfe);
    color: #1e40af;
}

.role-tag.role-aprovador {
    background: linear-gradient(135deg, #d1fae5, #a7f3d0);
    color: #065f46;
}

.role-tag.role-usuario,
.role-tag.role-user {
    background: linear-gradient(135deg, #e5e7eb, #d1d5db);
    color: #374151;
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

    .profile-body {
        padding: 0 20px 24px;
    }

    .quick-stats {
        flex-direction: column;
        gap: 16px;
        padding: 20px;
    }

    .stat-divider {
        width: 100%;
        height: 1px;
    }

    .stat-item {
        width: 100%;
        justify-content: center;
    }

    .card-header {
        flex-direction: column;
        gap: 16px;
        align-items: stretch;
    }

    .header-left {
        justify-content: center;
        text-align: center;
        flex-direction: column;
    }

    .btn-edit,
    .edit-actions {
        width: 100%;
    }

    .btn-edit {
        justify-content: center;
    }

    .edit-actions {
        flex-direction: column;
    }

    .btn-cancel,
    .btn-save {
        width: 100%;
        justify-content: center;
    }

    .info-grid {
        grid-template-columns: 1fr;
        gap: 16px;
    }

    .card-body {
        padding: 20px;
    }

    .notification {
        left: 16px;
        right: 16px;
        min-width: auto;
    }
}

@media (max-width: 480px) {
    .user-name {
        font-size: 24px;
    }

    .profile-banner {
        height: 100px;
    }

    .avatar-ring {
        width: 90px;
        height: 90px;
    }

    .info-item {
        padding: 16px;
    }
}
</style>
