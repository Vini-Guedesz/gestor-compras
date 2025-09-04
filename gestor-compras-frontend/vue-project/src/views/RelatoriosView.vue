<template>
  <div class="dashboard-layout">
    <!-- Header -->
    <DashboardHeader />

    <!-- Sidebar -->
    <DashboardSidebar />

    <!-- Conteúdo Principal -->
    <main class="main-content">
      <!-- Cabeçalho da Página -->
      <div class="page-header">
        <div class="header-content">
          <div class="title-section">
            <h1 class="page-title">Relatórios 📊</h1>
            <p class="page-subtitle">Central de relatórios e análises do sistema</p>
          </div>
          <div class="header-actions">
            <button class="btn-primary" @click="criarRelatorioPersonalizado">
              <svg class="btn-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
              </svg>
              Criar Relatório
            </button>
          </div>
        </div>
      </div>

      <!-- Métricas de Relatórios -->
      <div class="metrics-section">
        <div class="metrics-grid">
          <div class="metric-card">
            <div class="metric-icon" style="background: #dbeafe; color: #3b82f6;">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="currentColor" d="M14 2H6c-1.1 0-2 .9-2 2v16c0 1.1.89 2 2 2h12c1.1 0 2-.9 2-2V8l-6-6zm4 18H6V4h7v5h5v11z"/>
              </svg>
            </div>
            <div class="metric-content">
              <div class="metric-value">156</div>
              <div class="metric-label">Relatórios Gerados</div>
            </div>
            <div class="metric-trend positive">+12 este mês</div>
          </div>

          <div class="metric-card">
            <div class="metric-icon" style="background: #d1fae5; color: #10b981;">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="currentColor" d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
              </svg>
            </div>
            <div class="metric-content">
              <div class="metric-value">8</div>
              <div class="metric-label">Agendados</div>
            </div>
            <div class="metric-trend neutral">Automáticos</div>
          </div>

          <div class="metric-card">
            <div class="metric-icon" style="background: #fef3c7; color: #f59e0b;">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="currentColor" d="M12 2c-5.5 0-10 4.5-10 10s4.5 10 10 10 10-4.5 10-10-4.5-10-10-10zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
            </div>
            <div class="metric-content">
              <div class="metric-value">23</div>
              <div class="metric-label">Favoritos</div>
            </div>
            <div class="metric-trend positive">Mais usados</div>
          </div>

          <div class="metric-card">
            <div class="metric-icon" style="background: #e5e7eb; color: #6b7280;">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="currentColor" d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
              </svg>
            </div>
            <div class="metric-content">
              <div class="metric-value">4.2GB</div>
              <div class="metric-label">Dados Processados</div>
            </div>
            <div class="metric-trend positive">+15% este mês</div>
          </div>
        </div>
      </div>

      <!-- Tipos de Relatórios -->
      <div class="reports-section">
        <h2 class="section-title">Tipos de Relatórios Disponíveis</h2>
        <div class="reports-grid">
          <!-- Relatórios de Pedidos -->
          <div class="report-category">
            <div class="category-header">
              <div class="category-icon pedidos">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="currentColor" d="M7 4V2C7 1.45 7.45 1 8 1H16C16.55 1 17 1.45 17 2V4H20C20.55 4 21 4.45 21 5S20.55 6 20 6H19V19C19 20.1 18.1 21 17 21H7C5.9 21 5 20.1 5 19V6H4C3.45 6 3 5.55 3 5S3.45 4 4 4H7Z"/>
                </svg>
              </div>
              <h3>Relatórios de Pedidos</h3>
            </div>
            <div class="report-items">
              <div class="report-item">
                <span class="report-name">Pedidos por Status</span>
                <button class="btn-generate" @click="gerarRelatorio('pedidos-status')">Gerar</button>
              </div>
              <div class="report-item">
                <span class="report-name">Pedidos por Período</span>
                <button class="btn-generate" @click="gerarRelatorio('pedidos-periodo')">Gerar</button>
              </div>
              <div class="report-item">
                <span class="report-name">Tempo de Aprovação</span>
                <button class="btn-generate" @click="gerarRelatorio('tempo-aprovacao')">Gerar</button>
              </div>
            </div>
          </div>

          <!-- Relatórios Financeiros -->
          <div class="report-category">
            <div class="category-header">
              <div class="category-icon financeiro">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="currentColor" d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/>
                </svg>
              </div>
              <h3>Relatórios Financeiros</h3>
            </div>
            <div class="report-items">
              <div class="report-item">
                <span class="report-name">Fluxo de Caixa</span>
                <button class="btn-generate" @click="gerarRelatorio('fluxo-caixa')">Gerar</button>
              </div>
              <div class="report-item">
                <span class="report-name">Contas a Pagar</span>
                <button class="btn-generate" @click="gerarRelatorio('contas-pagar')">Gerar</button>
              </div>
              <div class="report-item">
                <span class="report-name">Análise de Gastos</span>
                <button class="btn-generate" @click="gerarRelatorio('analise-gastos')">Gerar</button>
              </div>
            </div>
          </div>

          <!-- Relatórios de Fornecedores -->
          <div class="report-category">
            <div class="category-header">
              <div class="category-icon fornecedores">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="currentColor" d="M16 4c0-1.11.89-2 2-2s2 .89 2 2-.89 2-2 2-2-.89-2-2zm4 18v-6h2.5l-2.54-7.63A2.5 2.5 0 0 0 17.5 7H16c-.8 0-1.5.4-1.94 1.01L12 10l-2.06-1.99C9.5 7.4 8.8 7 8 7H6.5a2.5 2.5 0 0 0-2.46 1.37L1.5 16H4v6h2v-6h1.5v6h2v-6H12v6h2v-6h1.5v6h2z"/>
                </svg>
              </div>
              <h3>Relatórios de Fornecedores</h3>
            </div>
            <div class="report-items">
              <div class="report-item">
                <span class="report-name">Performance de Fornecedores</span>
                <button class="btn-generate" @click="gerarRelatorio('performance-fornecedores')">Gerar</button>
              </div>
              <div class="report-item">
                <span class="report-name">Comparativo de Preços</span>
                <button class="btn-generate" @click="gerarRelatorio('comparativo-precos')">Gerar</button>
              </div>
              <div class="report-item">
                <span class="report-name">Avaliação de Fornecedores</span>
                <button class="btn-generate" @click="gerarRelatorio('avaliacao-fornecedores')">Gerar</button>
              </div>
            </div>
          </div>

          <!-- Relatórios Operacionais -->
          <div class="report-category">
            <div class="category-header">
              <div class="category-icon operacional">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="currentColor" d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                </svg>
              </div>
              <h3>Relatórios Operacionais</h3>
            </div>
            <div class="report-items">
              <div class="report-item">
                <span class="report-name">Tempo de Processo</span>
                <button class="btn-generate" @click="gerarRelatorio('tempo-processo')">Gerar</button>
              </div>
              <div class="report-item">
                <span class="report-name">Eficiência por Departamento</span>
                <button class="btn-generate" @click="gerarRelatorio('eficiencia-departamento')">Gerar</button>
              </div>
              <div class="report-item">
                <span class="report-name">Indicadores de Performance</span>
                <button class="btn-generate" @click="gerarRelatorio('kpis')">Gerar</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Conteúdo em Construção -->
      <div class="construction-section">
        <div class="construction-card">
          <div class="construction-icon">
            <svg viewBox="0 0 24 24" width="64" height="64">
              <path fill="currentColor" d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
            </svg>
          </div>
          <h2 class="construction-title">Central de Relatórios</h2>
          <p class="construction-description">
            Este módulo está em desenvolvimento. Em breve você poderá:
          </p>
          <ul class="construction-features">
            <li>Gerar relatórios automáticos com dados em tempo real</li>
            <li>Criar dashboards personalizados</li>
            <li>Agendar relatórios recorrentes</li>
            <li>Exportar dados em múltiplos formatos (PDF, Excel, CSV)</li>
            <li>Configurar alertas baseados em métricas</li>
            <li>Compartilhar relatórios com stakeholders</li>
            <li>Análises preditivas e insights inteligentes</li>
          </ul>
          <div class="construction-actions">
            <button class="btn-outline" @click="$router.push('/dashboard')">
              Voltar ao Dashboard
            </button>
            <button class="btn-secondary" @click="$router.push('/pedidos')">
              Ir para Pedidos
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
/**
 * Página de Relatórios
 *
 * Central de relatórios e análises
 */
import DashboardHeader from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'

const criarRelatorioPersonalizado = () => {
  console.log('Criar relatório personalizado - funcionalidade em desenvolvimento')
}

const gerarRelatorio = (tipo) => {
  console.log(`Gerar relatório: ${tipo} - funcionalidade em desenvolvimento`)
}
</script>

<style scoped>
.dashboard-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  grid-template-rows: auto 1fr;
  grid-template-areas:
    "sidebar header"
    "sidebar main";
  min-height: 100vh;
  background: #f8fafc;
}

.main-content {
  grid-area: main;
  padding: 24px;
  overflow-y: auto;
}

.page-header {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  border: 1px solid #e5e7eb;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section h1 {
  font-size: 28px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 8px 0;
}

.title-section p {
  color: #6b7280;
  margin: 0;
  font-size: 16px;
}

.metrics-section {
  margin-bottom: 32px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.metric-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  border: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  gap: 16px;
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.metric-value {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 4px;
}

.metric-label {
  font-size: 14px;
  color: #6b7280;
}

.metric-trend {
  margin-left: auto;
  font-size: 12px;
  font-weight: 500;
  color: #6b7280;
}

.metric-trend.positive {
  color: #10b981;
}

.metric-trend.neutral {
  color: #6b7280;
}

.reports-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 24px;
}

.reports-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.report-category {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  border: 1px solid #e5e7eb;
}

.category-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.category-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.category-icon.pedidos {
  background: #dbeafe;
  color: #3b82f6;
}

.category-icon.financeiro {
  background: #d1fae5;
  color: #10b981;
}

.category-icon.fornecedores {
  background: #fef3c7;
  color: #f59e0b;
}

.category-icon.operacional {
  background: #e0e7ff;
  color: #6366f1;
}

.category-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.report-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.report-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
}

.report-name {
  font-size: 14px;
  color: #374151;
}

.btn-generate {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-generate:hover {
  background: #e5e7eb;
}

.construction-section {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.construction-card {
  background: white;
  border-radius: 16px;
  padding: 48px;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
  border: 1px solid #e5e7eb;
  max-width: 600px;
}

.construction-icon {
  color: #6366f1;
  margin-bottom: 24px;
}

.construction-title {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 16px;
}

.construction-description {
  color: #6b7280;
  font-size: 16px;
  margin-bottom: 24px;
}

.construction-features {
  text-align: left;
  margin: 24px 0;
  padding-left: 24px;
}

.construction-features li {
  color: #374151;
  margin-bottom: 8px;
  font-size: 14px;
}

.construction-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 32px;
}

.btn-primary,
.btn-secondary,
.btn-outline {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  border: none;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover {
  background: #2563eb;
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

.btn-outline:hover {
  background: #eff6ff;
}

.btn-icon {
  width: 16px;
  height: 16px;
}

@media (max-width: 1024px) {
  .dashboard-layout {
    grid-template-columns: 1fr;
    grid-template-areas:
      "header"
      "main";
  }

  .construction-card {
    margin: 0 16px;
    padding: 32px 24px;
  }

  .construction-actions {
    flex-direction: column;
  }

  .reports-grid {
    grid-template-columns: 1fr;
  }
}
</style>
