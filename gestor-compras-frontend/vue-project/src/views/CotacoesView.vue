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
            <h1 class="page-title">Cotações 💰</h1>
            <p class="page-subtitle">Gerencie cotações de fornecedores e compare preços</p>
          </div>
          <div class="header-actions">
            <button class="btn-primary" @click="novaCotacao">
              <svg class="btn-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
              </svg>
              Nova Cotação
            </button>
          </div>
        </div>
      </div>

      <!-- Cards de Métricas -->
      <div class="metrics-section">
        <div class="metrics-grid">
          <div class="metric-card">
            <div class="metric-icon" style="background: #dbeafe; color: #2563eb;">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="currentColor" d="M9 11h-2v2h2v-2zm4 0h-2v2h2v-2zm4 0h-2v2h2v-2zm2-7h-1v-2h-2v2h-10v-2h-2v2h-1c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2v-14c0-1.1-.9-2-2-2zm0 16h-16v-11h16v11z"/>
              </svg>
            </div>
            <div class="metric-content">
              <div class="metric-value">12</div>
              <div class="metric-label">Cotações Abertas</div>
            </div>
          </div>

          <div class="metric-card">
            <div class="metric-icon" style="background: #d1fae5; color: #10b981;">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="currentColor" d="M9 16.2l-3.5-3.5c-.39-.39-1.02-.39-1.41 0-.39.39-.39 1.02 0 1.41l4.19 4.19c.39.39 1.02.39 1.41 0L20.3 7.7c.39-.39.39-1.02 0-1.41-.39-.39-1.02-.39-1.41 0L9 16.2z"/>
              </svg>
            </div>
            <div class="metric-content">
              <div class="metric-value">5</div>
              <div class="metric-label">Em Análise</div>
            </div>
          </div>

          <div class="metric-card">
            <div class="metric-icon" style="background: #fef3c7; color: #f59e0b;">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="currentColor" d="M12 2c-5.5 0-10 4.5-10 10s4.5 10 10 10 10-4.5 10-10-4.5-10-10-10zm1 15h-2v-6h2v6zm0-8h-2v-2h2v2z"/>
              </svg>
            </div>
            <div class="metric-content">
              <div class="metric-value">3</div>
              <div class="metric-label">Vencendo Hoje</div>
            </div>
          </div>

          <div class="metric-card">
            <div class="metric-icon" style="background: #e5e7eb; color: #6b7280;">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="currentColor" d="M7 4v16l5.5-3.5 5.5 3.5v-16h-11z"/>
              </svg>
            </div>
            <div class="metric-content">
              <div class="metric-value">89</div>
              <div class="metric-label">Concluídas</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Conteúdo em Construção -->
      <div class="construction-section">
        <div class="construction-card">
          <div class="construction-icon">
            <svg viewBox="0 0 24 24" width="64" height="64">
              <path fill="currentColor" d="M13 3v7.27l7 3.64V9c0-1.1-.9-2-2-2H13zm-2 0H6c-1.1 0-2 .9-2 2v4.91l7-3.64V3zM4 15v4c0 1.1.9 2 2 2h5v-7.27L4 15zm16 0l-7 1.73V22h5c1.1 0 2-.9 2-2v-4zm-7-2.27L9 11.27 4 13.18 13 12.73zM20 13.18l-7-1.45 4-1.46L20 13.18z"/>
            </svg>
          </div>
          <h2 class="construction-title">Módulo de Cotações</h2>
          <p class="construction-description">
            Esta funcionalidade está em desenvolvimento. Em breve você poderá:
          </p>
          <ul class="construction-features">
            <li>Criar e gerenciar cotações de fornecedores</li>
            <li>Comparar propostas de diferentes fornecedores</li>
            <li>Analisar histórico de preços</li>
            <li>Gerar relatórios comparativos</li>
            <li>Configurar alertas de vencimento</li>
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
 * Página de Cotações
 *
 * Módulo para gerenciamento de cotações de fornecedores
 */
import DashboardHeader from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'

// Função placeholder para nova cotação
const novaCotacao = () => {
  console.log('Nova cotação - funcionalidade em desenvolvimento')
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
  color: #6b7280;
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
}
</style>
