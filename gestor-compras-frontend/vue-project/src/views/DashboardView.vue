<template>
  <div class="dashboard-layout">
    <!-- Header -->
    <DashboardHeader />

    <!-- Sidebar -->
    <DashboardSidebar />

    <!-- Conteúdo Principal -->
    <main class="main-content">
      <!-- Mensagem de Boas-vindas -->
      <div class="welcome-section">
        <div class="welcome-header">
          <div class="welcome-content">
            <h1 class="welcome-title">Bem-vinda de volta, {{ userName }}! 👋</h1>
            <p class="welcome-subtitle">
              Aqui está um resumo das suas atividades de compras hoje.
            </p>
          </div>
          <div class="user-status">
            <div class="status-indicator online"></div>
            <span class="status-text">Online</span>
          </div>
        </div>
      </div>

      <!-- Ações Rápidas -->
      <QuickActions @action-click="handleQuickAction" />

      <!-- Cards de Métricas -->
      <div class="metrics-section">
        <h2 class="section-title">Módulos do Sistema</h2>
        <div class="metrics-grid">
          <!-- Pedidos de Compra -->
          <MetricCard
            title="Pedidos de Compra"
            description="Criar, editar e acompanhar pedidos"
            :metrics="[
              { value: '23', label: 'pendentes', color: '#f59e0b' },
              { value: '156', label: 'este mês', color: '#1F285F' }
            ]"
            icon-color="#1F285F"
            variant="primary"
            @action="navigateTo('/pedidos')"
          />

          <!-- Cotações -->
          <MetricCard
            title="Cotações"
            description="Gerenciar cotações e fornecedores"
            :metrics="[
              { value: '12', label: 'abertas', color: '#10b981' },
              { value: '5', label: 'em análise', color: '#f59e0b' }
            ]"
            icon-color="#10b981"
            variant="success"
            @action="navigateTo('/cotacoes')"
          />

          <!-- Fornecedores -->
          <MetricCard
            title="Fornecedores"
            description="Cadastro e avaliação"
            :metrics="[
              { value: '87', label: 'ativos', color: '#1F285F' },
              { value: '3', label: 'novos', color: '#10b981' }
            ]"
            icon-color="#6366f1"
            variant="default"
            @action="navigateTo('/fornecedores')"
          />

          <!-- Aprovações -->
          <MetricCard
            title="Aprovações"
            description="Central de aprovações e assinaturas"
            :metrics="[
              { value: '8', label: 'pendentes', color: '#f59e0b' },
              { value: '2', label: 'urgentes', color: '#ef4444' }
            ]"
            icon-color="#f59e0b"
            variant="warning"
            @action="navigateTo('/aprovacoes')"
          />

          <!-- Financeiro -->
          <MetricCard
            title="Financeiro"
            description="Controle de fluxo de caixa e notas fiscais"
            :metrics="[
              { value: 'R$ 2.1M', label: 'saldo', color: '#10b981' },
              { value: 'R$ 340K', label: 'pendente', color: '#f59e0b' }
            ]"
            icon-color="#10b981"
            variant="success"
            @action="navigateTo('/financeiro')"
          />

          <!-- Relatórios -->
          <MetricCard
            title="Relatórios"
            description="Análises e dashboards gerenciais"
            :metrics="[
              { value: '15', label: 'últimos', color: '#1F285F' },
              { value: '4', label: 'agendados', color: '#6366f1' }
            ]"
            icon-color="#6366f1"
            variant="default"
            @action="navigateTo('/relatorios')"
          />
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import DashboardHeader from '../components/DashboardHeader.vue'
import DashboardSidebar from '../components/DashboardSidebar.vue'
import MetricCard from '../components/MetricCard.vue'
import QuickActions from '../components/QuickActions.vue'

const router = useRouter()
const authStore = useAuthStore()

const userName = computed(() => {
  return authStore.user?.name || 'Ana'
})

const handleQuickAction = (action) => {
  console.log('Ação rápida:', action)
  // Navegar para a rota correspondente
  if (action.route) {
    navigateTo(action.route)
  }
}

const navigateTo = (path) => {
  console.log('Navegando para:', path)
  if (path === '/fornecedores') {
    router.push(path)
  } else {
    // Para outras rotas ainda não implementadas
    console.log('Rota ainda não implementada:', path)
  }
}
</script>

<style scoped>
.dashboard-layout {
  min-height: 100vh;
  background: #f8f9fa;
}

.main-content {
  margin-left: 280px;
  margin-top: 70px;
  padding: 32px;
  min-height: calc(100vh - 70px);
}

/* Welcome Section */
.welcome-section {
  margin-bottom: 32px;
  padding: 24px 0;
}

.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
}

.welcome-content {
  flex: 1;
}

.welcome-title {
  font-family: Arial, sans-serif;
  font-size: 28px;
  font-weight: 700;
  color: #1F285F;
  margin: 0 0 8px 0;
  line-height: 1.3;
}

.welcome-subtitle {
  font-family: Arial, sans-serif;
  font-size: 16px;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
}

.user-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 20px;
  font-size: 14px;
  color: #15803d;
  font-weight: 500;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #22c55e;
  animation: pulse 2s infinite;
}

.status-indicator.online {
  background: #22c55e;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(34, 197, 94, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(34, 197, 94, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(34, 197, 94, 0);
  }
}

.status-text {
  font-family: Arial, sans-serif;
}

/* Metrics Section */
.metrics-section {
  margin-top: 40px;
}

.section-title {
  font-family: Arial, sans-serif;
  font-size: 24px;
  font-weight: 600;
  color: #1F285F;
  margin: 0 0 24px 0;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 24px;
}

/* Responsividade */
@media (max-width: 1024px) {
  .main-content {
    margin-left: 0;
    padding: 20px;
  }

  .metrics-grid {
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
  }

  .welcome-title {
    font-size: 24px;
  }

  .section-title {
    font-size: 20px;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 16px;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .welcome-title {
    font-size: 22px;
  }

  .welcome-subtitle {
    font-size: 14px;
  }
}
</style>
