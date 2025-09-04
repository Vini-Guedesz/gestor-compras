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
            <h1 class="page-title">Módulo Financeiro 💰</h1>
            <p class="page-subtitle">Controle financeiro e fluxo de caixa das compras</p>
          </div>
          <div class="header-actions">
            <button class="btn-primary" @click="gerarRelatorio">
              <svg class="btn-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M14 2H6c-1.1 0-2 .9-2 2v16c0 1.1.89 2 2 2h12c1.1 0 2-.9 2-2V8l-6-6zm4 18H6V4h7v5h5v11z"/>
              </svg>
              Gerar Relatório
            </button>
          </div>
        </div>
      </div>

      <!-- Cards de Resumo Financeiro -->
      <div class="financial-summary">
        <div class="summary-grid">
          <div class="summary-card primary">
            <div class="card-header">
              <div class="card-icon">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="currentColor" d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/>
                </svg>
              </div>
              <div class="card-info">
                <div class="card-value">R$ 2.147.850</div>
                <div class="card-label">Saldo Disponível</div>
              </div>
            </div>
            <div class="card-trend positive">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M16 6l2.29 2.29-4.88 4.88-4-4L2 16.59 3.41 18l6-6 4 4 6.3-6.29L22 12V6z"/>
              </svg>
              +8.5% este mês
            </div>
          </div>

          <div class="summary-card warning">
            <div class="card-header">
              <div class="card-icon">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="currentColor" d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                </svg>
              </div>
              <div class="card-info">
                <div class="card-value">R$ 342.560</div>
                <div class="card-label">Pendente de Pagamento</div>
              </div>
            </div>
            <div class="card-trend neutral">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M22 12l-4-4v3H3v2h15v3l4-4z"/>
              </svg>
              23 faturas
            </div>
          </div>

          <div class="summary-card success">
            <div class="card-header">
              <div class="card-icon">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="currentColor" d="M9 16.2l-3.5-3.5c-.39-.39-1.02-.39-1.41 0-.39.39-.39 1.02 0 1.41l4.19 4.19c.39.39 1.02.39 1.41 0L20.3 7.7c.39-.39.39-1.02 0-1.41-.39-.39-1.02-.39-1.41 0L9 16.2z"/>
              </svg>
              </div>
              <div class="card-info">
                <div class="card-value">R$ 1.845.920</div>
                <div class="card-label">Pago este Mês</div>
              </div>
            </div>
            <div class="card-trend positive">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M9 16.2l-3.5-3.5c-.39-.39-1.02-.39-1.41 0-.39.39-.39 1.02 0 1.41l4.19 4.19c.39.39 1.02.39 1.41 0L20.3 7.7c.39-.39.39-1.02 0-1.41-.39-.39-1.02-.39-1.41 0L9 16.2z"/>
              </svg>
              156 pagamentos
            </div>
          </div>

          <div class="summary-card info">
            <div class="card-header">
              <div class="card-icon">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="currentColor" d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
              </svg>
              </div>
              <div class="card-info">
                <div class="card-value">R$ 4.236.330</div>
                <div class="card-label">Total Movimentado</div>
              </div>
            </div>
            <div class="card-trend positive">
              +12.3% vs mês anterior
            </div>
          </div>
        </div>
      </div>

      <!-- Gráfico de Fluxo de Caixa (Mock) -->
      <div class="chart-section">
        <div class="chart-header">
          <h2>Fluxo de Caixa - Últimos 6 Meses</h2>
          <div class="chart-legend">
            <div class="legend-item">
              <div class="legend-color" style="background: #3b82f6;"></div>
              <span>Entradas</span>
            </div>
            <div class="legend-item">
              <div class="legend-color" style="background: #ef4444;"></div>
              <span>Saídas</span>
            </div>
          </div>
        </div>
        <div class="chart-placeholder">
          <div class="chart-bars">
            <div class="bar-group">
              <div class="bar entrada" style="height: 80%;"></div>
              <div class="bar saida" style="height: 60%;"></div>
              <span class="bar-label">Jan</span>
            </div>
            <div class="bar-group">
              <div class="bar entrada" style="height: 90%;"></div>
              <div class="bar saida" style="height: 70%;"></div>
              <span class="bar-label">Fev</span>
            </div>
            <div class="bar-group">
              <div class="bar entrada" style="height: 75%;"></div>
              <div class="bar saida" style="height: 85%;"></div>
              <span class="bar-label">Mar</span>
            </div>
            <div class="bar-group">
              <div class="bar entrada" style="height: 95%;"></div>
              <div class="bar saida" style="height: 75%;"></div>
              <span class="bar-label">Abr</span>
            </div>
            <div class="bar-group">
              <div class="bar entrada" style="height: 85%;"></div>
              <div class="bar saida" style="height: 80%;"></div>
              <span class="bar-label">Mai</span>
            </div>
            <div class="bar-group">
              <div class="bar entrada" style="height: 100%;"></div>
              <div class="bar saida" style="height: 90%;"></div>
              <span class="bar-label">Jun</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Conteúdo em Construção -->
      <div class="construction-section">
        <div class="construction-card">
          <div class="construction-icon">
            <svg viewBox="0 0 24 24" width="64" height="64">
              <path fill="currentColor" d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/>
            </svg>
          </div>
          <h2 class="construction-title">Módulo Financeiro</h2>
          <p class="construction-description">
            Este módulo está em desenvolvimento. Em breve você poderá:
          </p>
          <ul class="construction-features">
            <li>Controlar fluxo de caixa em tempo real</li>
            <li>Gerenciar contas a pagar e a receber</li>
            <li>Processar notas fiscais automaticamente</li>
            <li>Gerar relatórios financeiros detalhados</li>
            <li>Configurar alertas de vencimento</li>
            <li>Integrar com sistemas contábeis</li>
            <li>Acompanhar orçamentos por departamento</li>
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
 * Página do Módulo Financeiro
 *
 * Controle financeiro e fluxo de caixa
 */
import DashboardHeader from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'

const gerarRelatorio = () => {
  console.log('Gerar relatório - funcionalidade em desenvolvimento')
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

.financial-summary {
  margin-bottom: 32px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.summary-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  border: 1px solid #e5e7eb;
  position: relative;
  overflow: hidden;
}

.summary-card.primary {
  border-left: 4px solid #3b82f6;
}

.summary-card.warning {
  border-left: 4px solid #f59e0b;
}

.summary-card.success {
  border-left: 4px solid #10b981;
}

.summary-card.info {
  border-left: 4px solid #6366f1;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.primary .card-icon {
  background: #dbeafe;
  color: #3b82f6;
}

.warning .card-icon {
  background: #fef3c7;
  color: #f59e0b;
}

.success .card-icon {
  background: #d1fae5;
  color: #10b981;
}

.info .card-icon {
  background: #e0e7ff;
  color: #6366f1;
}

.card-value {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 4px;
}

.card-label {
  font-size: 14px;
  color: #6b7280;
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
}

.card-trend.positive {
  color: #10b981;
}

.card-trend.neutral {
  color: #6b7280;
}

.chart-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  border: 1px solid #e5e7eb;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.chart-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.chart-legend {
  display: flex;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #6b7280;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.chart-placeholder {
  height: 200px;
  display: flex;
  align-items: end;
  justify-content: center;
  background: #f9fafb;
  border-radius: 8px;
  padding: 20px;
}

.chart-bars {
  display: flex;
  align-items: end;
  gap: 24px;
  height: 100%;
}

.bar-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  height: 100%;
}

.bar {
  width: 20px;
  border-radius: 4px 4px 0 0;
  transition: all 0.3s ease;
}

.bar.entrada {
  background: #3b82f6;
}

.bar.saida {
  background: #ef4444;
}

.bar-label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
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
  color: #10b981;
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

  .chart-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
}
</style>
