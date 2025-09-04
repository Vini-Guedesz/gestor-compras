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
            <h1 class="page-title">Central de Aprovações ✅</h1>
            <p class="page-subtitle">Gerencie aprovações de pedidos e documentos</p>
          </div>
          <div class="header-actions">
            <div class="notification-badge">
              <svg class="btn-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z"/>
              </svg>
              <span class="badge-count">8</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Cards de Métricas -->
      <div class="metrics-section">
        <div class="metrics-grid">
          <div class="metric-card urgent">
            <div class="metric-icon">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="currentColor" d="M1 21h4v-6h14v6h4v-8l-11-6-11 6v8zm12-10h8v2h-8v-2z"/>
              </svg>
            </div>
            <div class="metric-content">
              <div class="metric-value">2</div>
              <div class="metric-label">Urgentes</div>
            </div>
            <div class="metric-trend urgent">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M16 6l2.29 2.29-4.88 4.88-4-4L2 16.59 3.41 18l6-6 4 4 6.3-6.29L22 12V6z"/>
              </svg>
            </div>
          </div>

          <div class="metric-card warning">
            <div class="metric-icon">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="currentColor" d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
              </svg>
            </div>
            <div class="metric-content">
              <div class="metric-value">6</div>
              <div class="metric-label">Pendentes</div>
            </div>
            <div class="metric-trend neutral">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M22 12l-4-4v3H3v2h15v3l4-4z"/>
              </svg>
            </div>
          </div>

          <div class="metric-card success">
            <div class="metric-icon">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="currentColor" d="M9 16.2l-3.5-3.5c-.39-.39-1.02-.39-1.41 0-.39.39-.39 1.02 0 1.41l4.19 4.19c.39.39 1.02.39 1.41 0L20.3 7.7c.39-.39.39-1.02 0-1.41-.39-.39-1.02-.39-1.41 0L9 16.2z"/>
              </svg>
            </div>
            <div class="metric-content">
              <div class="metric-value">24</div>
              <div class="metric-label">Aprovadas Hoje</div>
            </div>
            <div class="metric-trend positive">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M16 6l2.29 2.29-4.88 4.88-4-4L2 16.59 3.41 18l6-6 4 4 6.3-6.29L22 12V6z"/>
              </svg>
            </div>
          </div>

          <div class="metric-card info">
            <div class="metric-icon">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
            </div>
            <div class="metric-content">
              <div class="metric-value">156</div>
              <div class="metric-label">Total do Mês</div>
            </div>
            <div class="metric-trend positive">
              +12%
            </div>
          </div>
        </div>
      </div>

      <!-- Conteúdo em Construção -->
      <div class="construction-section">
        <div class="construction-card">
          <div class="construction-icon">
            <svg viewBox="0 0 24 24" width="64" height="64">
              <path fill="currentColor" d="M9 16.2l-3.5-3.5c-.39-.39-1.02-.39-1.41 0-.39.39-.39 1.02 0 1.41l4.19 4.19c.39.39 1.02.39 1.41 0L20.3 7.7c.39-.39.39-1.02 0-1.41-.39-.39-1.02-.39-1.41 0L9 16.2z"/>
            </svg>
          </div>
          <h2 class="construction-title">Central de Aprovações</h2>
          <p class="construction-description">
            Este módulo está em desenvolvimento. Em breve você poderá:
          </p>
          <ul class="construction-features">
            <li>Visualizar todos os pedidos pendentes de aprovação</li>
            <li>Aprovar ou rejeitar pedidos com justificativas</li>
            <li>Configurar fluxos de aprovação automáticos</li>
            <li>Receber notificações em tempo real</li>
            <li>Gerenciar delegações de aprovação</li>
            <li>Acompanhar histórico de aprovações</li>
          </ul>

          <div class="preview-section">
            <h3>Prévia dos Recursos</h3>
            <div class="preview-cards">
              <div class="preview-card">
                <div class="preview-header">
                  <span class="preview-badge urgent">URGENTE</span>
                  <span class="preview-date">Hoje, 14:30</span>
                </div>
                <h4>Pedido #2024-005</h4>
                <p>Equipamentos de TI - R$ 25.400,00</p>
                <div class="preview-actions">
                  <button class="btn-approve disabled">Aprovar</button>
                  <button class="btn-reject disabled">Rejeitar</button>
                </div>
              </div>

              <div class="preview-card">
                <div class="preview-header">
                  <span class="preview-badge normal">NORMAL</span>
                  <span class="preview-date">Ontem, 09:15</span>
                </div>
                <h4>Pedido #2024-004</h4>
                <p>Material de escritório - R$ 1.250,00</p>
                <div class="preview-actions">
                  <button class="btn-approve disabled">Aprovar</button>
                  <button class="btn-reject disabled">Rejeitar</button>
                </div>
              </div>
            </div>
          </div>

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
 * Página de Aprovações
 *
 * Central de aprovações para pedidos e documentos
 */
import DashboardHeader from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'
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

.notification-badge {
  position: relative;
  background: #fef3c7;
  color: #f59e0b;
  padding: 12px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.badge-count {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #ef4444;
  color: white;
  font-size: 12px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

.metrics-section {
  margin-bottom: 32px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
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
  position: relative;
}

.metric-card.urgent {
  border-left: 4px solid #ef4444;
}

.metric-card.warning {
  border-left: 4px solid #f59e0b;
}

.metric-card.success {
  border-left: 4px solid #10b981;
}

.metric-card.info {
  border-left: 4px solid #3b82f6;
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.urgent .metric-icon {
  background: #fee2e2;
  color: #ef4444;
}

.warning .metric-icon {
  background: #fef3c7;
  color: #f59e0b;
}

.success .metric-icon {
  background: #d1fae5;
  color: #10b981;
}

.info .metric-icon {
  background: #dbeafe;
  color: #3b82f6;
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
  display: flex;
  align-items: center;
  gap: 4px;
}

.metric-trend.urgent {
  color: #ef4444;
}

.metric-trend.positive {
  color: #10b981;
}

.metric-trend.neutral {
  color: #6b7280;
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
  max-width: 800px;
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

.preview-section {
  margin: 32px 0;
  padding: 24px;
  background: #f9fafb;
  border-radius: 12px;
}

.preview-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 16px;
  text-align: left;
}

.preview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.preview-card {
  background: white;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  text-align: left;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.preview-badge {
  font-size: 10px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 4px;
}

.preview-badge.urgent {
  background: #fee2e2;
  color: #ef4444;
}

.preview-badge.normal {
  background: #dbeafe;
  color: #3b82f6;
}

.preview-date {
  font-size: 12px;
  color: #6b7280;
}

.preview-card h4 {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 8px;
}

.preview-card p {
  color: #6b7280;
  font-size: 14px;
  margin-bottom: 16px;
}

.preview-actions {
  display: flex;
  gap: 8px;
}

.btn-approve,
.btn-reject {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  border: none;
  cursor: not-allowed;
  opacity: 0.6;
}

.btn-approve.disabled {
  background: #d1fae5;
  color: #10b981;
}

.btn-reject.disabled {
  background: #fee2e2;
  color: #ef4444;
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

  .preview-cards {
    grid-template-columns: 1fr;
  }
}
</style>
