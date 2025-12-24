<template>
    <div class="sobre-view">
        <!-- Header -->
        <DashboardHeader />

        <!-- Sidebar -->
        <DashboardSidebar />

        <!-- Conteúdo Principal -->
        <div class="main-content">
            <div class="sobre-container">
                <div class="sobre-header">
                    <div class="header-content">
                        <div class="header-info">
                            <h1 class="page-title">Sobre o Sistema</h1>
                            <p class="page-subtitle">Informações sobre a versão e tecnologias utilizadas</p>
                        </div>
                    </div>
                </div>

                <div class="sobre-content">
                    <div class="section-card">
                        <div class="about-section">
                            <div class="about-header-section">
                                <div class="app-logo">
                                    <svg viewBox="0 0 24 24" width="64" height="64">
                                        <path fill="#1F285F" d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm-1 16H9V7h9v14z"/>
                                    </svg>
                                </div>
                                <h2 class="app-name">Gestor de Compras</h2>
                                <p class="app-tagline">Sistema de Gestão de Pedidos e Cotações</p>
                                <div class="version-badge">Versão {{ appVersion }}</div>
                            </div>

                            <div class="about-info">
                                <div class="info-grid-about">
                                    <div class="info-item-about">
                                        <span class="info-label-about">Versão</span>
                                        <span class="info-value-about">{{ appVersion }}</span>
                                    </div>
                                    <div class="info-item-about">
                                        <span class="info-label-about">Branch</span>
                                        <span class="info-value-about">{{ gitBranch }}</span>
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
                                        <span class="info-label-about">Build Tool</span>
                                        <span class="info-value-about">Vite</span>
                                    </div>
                                    <div class="info-item-about">
                                        <span class="info-label-about">Data de Build</span>
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
                                <h3 class="subsection-title">Stack Tecnológica</h3>
                                <div class="tech-stack">
                                    <div class="tech-category">
                                        <h4 class="tech-category-title">Frontend</h4>
                                        <div class="tech-items">
                                            <span class="tech-tag">Vue.js 3</span>
                                            <span class="tech-tag">Vite</span>
                                            <span class="tech-tag">Pinia</span>
                                            <span class="tech-tag">Vue Router</span>
                                            <span class="tech-tag">Axios</span>
                                        </div>
                                    </div>
                                    <div class="tech-category">
                                        <h4 class="tech-category-title">Backend</h4>
                                        <div class="tech-items">
                                            <span class="tech-tag">Java 17</span>
                                            <span class="tech-tag">Spring Boot 3</span>
                                            <span class="tech-tag">Spring Security</span>
                                            <span class="tech-tag">Spring Data JPA</span>
                                        </div>
                                    </div>
                                    <div class="tech-category">
                                        <h4 class="tech-category-title">Banco de Dados</h4>
                                        <div class="tech-items">
                                            <span class="tech-tag">PostgreSQL 14+</span>
                                            <span class="tech-tag">Flyway</span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="section-divider"></div>

                            <div class="changelog">
                                <h3 class="subsection-title">Changelog - V3</h3>
                                <div class="changelog-content">
                                    <div class="changelog-section">
                                        <h4 class="changelog-version">Versão 3.0.0 (Atual)</h4>
                                        <ul class="changelog-list">
                                            <li>✨ Interface completamente redesenhada e responsiva</li>
                                            <li>🎨 Sistema de abas nas configurações</li>
                                            <li>📱 Layout de cards para mobile em todas as listas</li>
                                            <li>🎯 Centralização de wizards e formulários</li>
                                            <li>🔧 Correção de bugs de sobreposição da sidebar</li>
                                            <li>📊 Melhorias no dashboard e visualizações</li>
                                            <li>⚡ Otimizações de performance</li>
                                        </ul>
                                    </div>
                                </div>
                            </div>

                            <div class="about-footer">
                                <p class="copyright">© 2025 Gestor de Compras V{{ appVersion }}. Todos os direitos reservados.</p>
                                <p class="build-info">Build: {{ buildDate }} | Branch: {{ gitBranch }}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { version } from 'vue'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import packageInfo from '../../package.json'

// Informações do sistema
const appVersion = ref(packageInfo.version)
const vueVersion = ref(version)
const gitBranch = ref('V3')
const environment = ref(import.meta.env.MODE === 'production' ? 'Produção' : 'Desenvolvimento')
const buildDate = ref(new Date().toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
}))
</script>

<style scoped>
.sobre-view {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    background: #f5f7fa;
}

.main-content {
    flex: 1;
    margin-left: 280px;
    margin-top: 70px;
    padding: 24px;
    transition: margin-left 0.3s ease-in-out;
}

.sobre-container {
    max-width: 1200px;
    margin: 0 auto;
}

.sobre-header {
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
    align-items: center;
    gap: 24px;
}

.header-info {
    flex: 1;
}

.page-title {
    font-size: 2rem;
    font-weight: 700;
    color: #1F285F;
    margin: 0 0 8px 0;
}

.page-subtitle {
    font-size: 1rem;
    color: #6b7280;
    margin: 0;
}

.sobre-content {
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

/* Seção Sobre */
.about-section {
    padding: 0;
}

.about-header-section {
    text-align: center;
    padding-bottom: 32px;
}

.app-logo {
    margin: 0 auto 16px;
    width: 64px;
    height: 64px;
}

.app-name {
    font-size: 2.5rem;
    font-weight: 700;
    color: #1F285F;
    margin: 0 0 8px 0;
}

.app-tagline {
    font-size: 1.125rem;
    color: #6b7280;
    margin: 0 0 16px 0;
}

.version-badge {
    display: inline-block;
    background: linear-gradient(135deg, #1F285F 0%, #3b4a8f 100%);
    color: white;
    padding: 8px 20px;
    border-radius: 20px;
    font-size: 0.875rem;
    font-weight: 600;
    box-shadow: 0 2px 8px rgba(31, 40, 95, 0.3);
}

.about-info {
    margin-bottom: 32px;
}

.info-grid-about {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 16px;
}

.info-item-about {
    display: flex;
    flex-direction: column;
    gap: 6px;
    padding: 16px;
    background: #f9fafb;
    border-radius: 8px;
    border: 1px solid #e5e7eb;
    text-align: center;
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

.section-divider {
    height: 1px;
    background: #e5e7eb;
    margin: 32px 0;
}

.subsection-title {
    font-size: 1.25rem;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 16px 0;
}

.about-description {
    margin-bottom: 32px;
}

.description-text {
    font-size: 1rem;
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
    gap: 12px;
}

.feature-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 16px;
    background: #f9fafb;
    border-radius: 8px;
    border: 1px solid #e5e7eb;
    transition: all 0.2s;
}

.feature-item:hover {
    background: #f3f4f6;
    transform: translateX(4px);
}

.feature-item svg {
    flex-shrink: 0;
}

.feature-item span {
    font-size: 0.9375rem;
    color: #374151;
    font-weight: 500;
}

.about-tech {
    margin-bottom: 32px;
}

.tech-stack {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.tech-category {
    padding: 20px;
    background: #f9fafb;
    border-radius: 8px;
    border: 1px solid #e5e7eb;
}

.tech-category-title {
    font-size: 1rem;
    font-weight: 600;
    color: #1F285F;
    margin: 0 0 12px 0;
}

.tech-items {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

.tech-tag {
    display: inline-block;
    padding: 6px 14px;
    background: white;
    border: 1px solid #d1d5db;
    border-radius: 6px;
    font-size: 0.875rem;
    color: #374151;
    font-weight: 500;
}

.changelog {
    margin-bottom: 32px;
}

.changelog-content {
    background: #f9fafb;
    border-radius: 8px;
    border: 1px solid #e5e7eb;
    padding: 20px;
}

.changelog-section {
    margin-bottom: 0;
}

.changelog-version {
    font-size: 1.125rem;
    font-weight: 600;
    color: #1F285F;
    margin: 0 0 12px 0;
}

.changelog-list {
    list-style: none;
    padding: 0;
    margin: 0;
}

.changelog-list li {
    padding: 8px 0;
    color: #374151;
    font-size: 0.9375rem;
    line-height: 1.6;
}

.about-footer {
    text-align: center;
    padding-top: 24px;
}

.copyright {
    font-size: 0.9375rem;
    color: #6b7280;
    font-weight: 500;
    margin: 0 0 4px 0;
}

.build-info {
    font-size: 0.8125rem;
    color: #9ca3af;
    margin: 0;
}

@media (max-width: 1024px) {
    .main-content {
        margin-left: 0;
    }
}

@media (max-width: 768px) {
    .main-content {
        padding: 12px;
    }

    .page-title {
        font-size: 1.5rem;
    }

    .app-name {
        font-size: 1.75rem;
    }

    .info-grid-about {
        grid-template-columns: repeat(2, 1fr);
    }

    .features-grid {
        grid-template-columns: 1fr;
    }

    .section-card {
        padding: 20px;
    }
}

@media (max-width: 480px) {
    .info-grid-about {
        grid-template-columns: 1fr;
    }

    .header-content {
        flex-direction: column;
        align-items: flex-start;
    }
}
</style>
