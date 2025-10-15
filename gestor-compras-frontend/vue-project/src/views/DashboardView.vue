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
              { value: pedidosStats.total.toString(), label: 'total', color: '#1F285F' },
              { value: pedidosStats.pendentes.toString(), label: 'pendentes', color: '#f59e0b' }
            ]"
            icon-color="#1F285F"
            variant="primary"
            @action="navigateTo('/pedidos')"
          />

          <!-- Cotações -->
          <MetricCard
            title="Cotações"
            description="Gerenciar cotações de fornecedores"
            :metrics="[
              { value: cotacoesStats.total.toString(), label: 'total', color: '#10b981' },
              { value: cotacoesStats.ativas.toString(), label: 'ativas', color: '#f59e0b' }
            ]"
            icon-color="#10b981"
            variant="success"
            @action="navigateTo('/cotacoes')"
          />

          <!-- Fornecedores -->
          <MetricCard
            title="Fornecedores"
            description="Cadastro e gestão de fornecedores"
            :metrics="[
              { value: fornecedoresStats.total.toString(), label: 'cadastrados', color: '#6366f1' },
              { value: fornecedoresStats.ativos.toString(), label: 'ativos', color: '#10b981' }
            ]"
            icon-color="#6366f1"
            variant="default"
            @action="navigateTo('/fornecedores')"
          />
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import DashboardHeader from '../components/DashboardHeader.vue'
import DashboardSidebar from '../components/DashboardSidebar.vue'
import MetricCard from '../components/MetricCard.vue'
import QuickActions from '../components/QuickActions.vue'

// Importar services para dados reais
import pedidoService from '../services/pedidoService.js'
import fornecedorService from '../services/fornecedorService.js'
import { cotacaoService } from '../services/cotacaoService.js'

const router = useRouter()
const authStore = useAuthStore()

// Estados para estatísticas reais
const pedidosStats = ref({
  total: 0,
  pendentes: 0
})

const cotacoesStats = ref({
  total: 0,
  ativas: 0
})

const fornecedoresStats = ref({
  total: 0,
  ativos: 0
})

const userName = computed(() => {
  return authStore.user?.name || 'Usuário'
})

// Função para carregar estatísticas reais
const carregarEstatisticas = async () => {
  try {
    console.log('📊 Carregando estatísticas do dashboard...')
    
    // Carregar pedidos
    try {
      const pedidos = await pedidoService.listar()
      pedidosStats.value.total = pedidos?.length || 0
      pedidosStats.value.pendentes = pedidos?.filter(p => p.status === 'PENDENTE' || p.status === 'EM_ANALISE')?.length || 0
      console.log('✅ Estatísticas de pedidos carregadas:', pedidosStats.value)
    } catch (error) {
      console.warn('⚠️ Erro ao carregar pedidos:', error.message)
    }

    // Carregar cotações  
    try {
      const cotacoes = await cotacaoService.listar()
      cotacoesStats.value.total = cotacoes?.length || 0
      cotacoesStats.value.ativas = cotacoes?.filter(c => c.status === 'ATIVA' || c.status === 'EM_ANDAMENTO')?.length || 0
      console.log('✅ Estatísticas de cotações carregadas:', cotacoesStats.value)
    } catch (error) {
      console.warn('⚠️ Erro ao carregar cotações:', error.message)
    }

    // Carregar fornecedores
    try {
      const fornecedores = await fornecedorService.listarFornecedoresProduto()
      fornecedoresStats.value.total = fornecedores?.length || 0
      fornecedoresStats.value.ativos = fornecedores?.filter(f => f.ativo !== false)?.length || 0
      console.log('✅ Estatísticas de fornecedores carregadas:', fornecedoresStats.value)
    } catch (error) {
      console.warn('⚠️ Erro ao carregar fornecedores:', error.message)
    }

  } catch (error) {
    console.error('❌ Erro geral ao carregar estatísticas:', error)
  }
}

const handleQuickAction = (action) => {
  console.log('Ação rápida:', action)
  // Navegar para a rota correspondente
  if (action.route) {
    navigateTo(action.route)
  }
}

const navigateTo = (path) => {
  console.log('Navegando para:', path)
  
  // Definir rotas disponíveis
  const rotasDisponiveis = ['/pedidos', '/cotacoes', '/fornecedores']
  
  if (rotasDisponiveis.includes(path)) {
    router.push(path)
  } else {
    console.log('Rota ainda não implementada:', path)
    // Mostrar mensagem amigável para o usuário
    alert('Esta funcionalidade estará disponível em breve!')
  }
}

// Carregar dados ao montar o componente
onMounted(() => {
  carregarEstatisticas()
})
</script>

<style scoped>
/* Importar layout global */
@import '../assets/css/layout.css';

/* Override específico da DashboardView */
.dashboard-layout {
  background: #f8f9fa;
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
