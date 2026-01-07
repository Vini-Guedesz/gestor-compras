<template>
    <div class="sobre-view">
        <!-- Header -->
        <DashboardHeader />

        <!-- Sidebar -->
        <DashboardSidebar />

        <!-- Conteúdo Principal -->
        <div class="main-content">
            <div class="sobre-container">
                <!-- Card Principal -->
                <div class="about-main-card">
                    <!-- Banner -->
                    <div class="about-banner">
                        <div class="banner-pattern"></div>
                    </div>

                    <!-- Conteúdo -->
                    <div class="about-body">
                        <!-- Logo -->
                        <div class="logo-wrapper">
                            <div class="logo-ring">
                                <div class="logo-inner">
                                    <svg viewBox="0 0 24 24" width="40" height="40">
                                        <path fill="#1F285F" d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm-1 16H9V7h9v14z"/>
                                    </svg>
                                </div>
                            </div>
                        </div>

                        <!-- Info Principal -->
                        <div class="about-info-section">
                            <h1 class="app-name">Gestor de Compras</h1>
                            <p class="app-tagline">Sistema de Gestão de Pedidos e Cotações</p>
                            <span class="version-badge">v{{ appVersion }}</span>
                        </div>

                        <!-- Info Cards -->
                        <div class="info-cards">
                            <div class="info-card-item">
                                <div class="info-card-icon">
                                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
                                    </svg>
                                </div>
                                <div class="info-card-content">
                                    <span class="info-card-value">{{ gitBranch }}</span>
                                    <span class="info-card-label">Branch</span>
                                </div>
                            </div>

                            <div class="info-card-item">
                                <div class="info-card-icon">
                                    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2">
                                        <circle cx="12" cy="12" r="10"/>
                                        <path d="M12 6v6l4 2"/>
                                    </svg>
                                </div>
                                <div class="info-card-content">
                                    <span class="info-card-value">{{ environment }}</span>
                                    <span class="info-card-label">Ambiente</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Histórico de Versões -->
                <div class="about-card">
                    <div class="card-header">
                        <div class="header-left">
                            <div class="header-icon">
                                <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                                </svg>
                            </div>
                            <div>
                                <h2 class="card-title">Histórico de Versões</h2>
                                <p class="card-subtitle">Atualizações recentes</p>
                            </div>
                        </div>
                    </div>

                    <div class="card-body">
                        <div class="releases-list">
                            <!-- v3.2.0 -->
                            <div class="release-item current">
                                <div class="release-header">
                                    <span class="release-version">v3.2.0</span>
                                    <span class="release-badge current">Atual</span>
                                </div>
                                <p class="release-description">
                                    Corrigido bug de tipo de fornecedor no modal de cotacao.
                                    Corrigido bug de cotacoes duplicadas na conversao de rascunho para pedido.
                                </p>
                            </div>

                            <!-- v3.1.0 -->
                            <div class="release-item">
                                <div class="release-header">
                                    <span class="release-version">v3.1.0</span>
                                </div>
                                <p class="release-description">
                                    Redesign das telas de Perfil, Configuracoes e Sobre.
                                    Padronizacao visual e melhorias de responsividade.
                                </p>
                            </div>

                            <!-- v3.0.0 -->
                            <div class="release-item">
                                <div class="release-header">
                                    <span class="release-version">v3.0.0</span>
                                    <span class="release-badge major">Major</span>
                                </div>
                                <p class="release-description">
                                    Interface redesenhada, sistema de abas, layout responsivo
                                    e controle granular de permissoes.
                                </p>
                            </div>

                            <!-- v2.0.0 -->
                            <div class="release-item">
                                <div class="release-header">
                                    <span class="release-version">v2.0.0</span>
                                </div>
                                <p class="release-description">
                                    Sistema de cotações, workflow de aprovações e
                                    gestão de fornecedores.
                                </p>
                            </div>

                            <!-- v1.0.0 -->
                            <div class="release-item">
                                <div class="release-header">
                                    <span class="release-version">v1.0.0</span>
                                </div>
                                <p class="release-description">
                                    Lançamento inicial com gestão básica de pedidos de compra.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Footer -->
                <div class="about-footer">
                    <p class="copyright">© 2025 Gestor de Compras. Todos os direitos reservados.</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import packageInfo from '../../package.json'

// Informações do sistema
const appVersion = ref(packageInfo.version)
const gitBranch = ref('V3')
const environment = ref(import.meta.env.MODE === 'production' ? 'Produção' : 'Desenvolvimento')
</script>

<style scoped>
.sobre-view {
    min-height: 100vh;
    background: #f8fafc;
}

.main-content {
    margin-left: 240px;
    margin-top: 70px;
    padding: 32px;
    min-height: calc(100vh - 70px);
}

.sobre-container {
    max-width: 600px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 24px;
}

/* Card Principal */
.about-main-card {
    background: white;
    border-radius: 24px;
    overflow: hidden;
    box-shadow:
        0 4px 6px -1px rgba(0, 0, 0, 0.1),
        0 2px 4px -1px rgba(0, 0, 0, 0.06),
        0 20px 25px -5px rgba(0, 0, 0, 0.05);
}

.about-banner {
    height: 100px;
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

.about-body {
    padding: 0 32px 32px;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: -40px;
    position: relative;
}

/* Logo */
.logo-wrapper {
    margin-bottom: 20px;
}

.logo-ring {
    width: 90px;
    height: 90px;
    border-radius: 50%;
    padding: 4px;
    background: linear-gradient(135deg, #1F285F, #3d4d99);
    box-shadow: 0 8px 24px rgba(31, 40, 95, 0.3);
}

.logo-inner {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    background: white;
    display: flex;
    align-items: center;
    justify-content: center;
}

/* Info Principal */
.about-info-section {
    text-align: center;
    margin-bottom: 24px;
}

.app-name {
    font-size: 26px;
    font-weight: 700;
    color: #1f2937;
    margin: 0 0 6px;
    letter-spacing: -0.5px;
}

.app-tagline {
    font-size: 14px;
    color: #6b7280;
    margin: 0 0 16px;
}

.version-badge {
    display: inline-flex;
    align-items: center;
    padding: 8px 18px;
    background: linear-gradient(135deg, #1F285F, #3d4d99);
    color: white;
    border-radius: 20px;
    font-size: 13px;
    font-weight: 600;
    box-shadow: 0 4px 12px rgba(31, 40, 95, 0.3);
}

/* Info Cards */
.info-cards {
    display: flex;
    gap: 16px;
    width: 100%;
    max-width: 350px;
}

.info-card-item {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px;
    background: #f9fafb;
    border-radius: 14px;
    border: 1px solid #f3f4f6;
}

.info-card-icon {
    width: 40px;
    height: 40px;
    border-radius: 10px;
    background: white;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #1F285F;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.info-card-content {
    display: flex;
    flex-direction: column;
}

.info-card-value {
    font-size: 14px;
    font-weight: 600;
    color: #1f2937;
}

.info-card-label {
    font-size: 11px;
    color: #9ca3af;
}

/* Card de Releases */
.about-card {
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
    padding: 20px 24px;
    border-bottom: 1px solid #f3f4f6;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 14px;
}

.header-icon {
    width: 44px;
    height: 44px;
    border-radius: 12px;
    background: linear-gradient(135deg, #EAF0FC, #d6e4f7);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #1F285F;
}

.card-title {
    font-size: 16px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 2px;
}

.card-subtitle {
    font-size: 12px;
    color: #9ca3af;
    margin: 0;
}

.card-body {
    padding: 24px;
}

/* Lista de Releases */
.releases-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.release-item {
    padding: 16px;
    background: #f9fafb;
    border-radius: 12px;
    border: 1px solid #f3f4f6;
    transition: all 0.2s;
}

.release-item.current {
    background: #EAF0FC;
    border-color: #1F285F;
}

.release-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 8px;
}

.release-version {
    font-size: 15px;
    font-weight: 700;
    color: #1F285F;
    font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Roboto Mono', monospace;
}

.release-badge {
    padding: 3px 10px;
    border-radius: 12px;
    font-size: 10px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.release-badge.current {
    background: linear-gradient(135deg, #10b981, #059669);
    color: white;
}

.release-badge.major {
    background: linear-gradient(135deg, #1F285F, #3d4d99);
    color: white;
}

.release-description {
    font-size: 13px;
    color: #6b7280;
    line-height: 1.6;
    margin: 0;
}

/* Footer */
.about-footer {
    text-align: center;
    padding: 8px 0;
}

.copyright {
    font-size: 12px;
    color: #9ca3af;
    margin: 0;
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

    .about-body {
        padding: 0 20px 24px;
    }

    .app-name {
        font-size: 22px;
    }

    .info-cards {
        flex-direction: column;
        max-width: 100%;
    }
}

@media (max-width: 480px) {
    .about-banner {
        height: 80px;
    }

    .logo-ring {
        width: 80px;
        height: 80px;
    }

    .app-name {
        font-size: 20px;
    }

    .release-item {
        padding: 14px;
    }
}
</style>
