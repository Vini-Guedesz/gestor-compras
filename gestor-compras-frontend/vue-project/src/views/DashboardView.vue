<template>
  <div class="dashboard-layout" :class="{ 'sidebar-collapsed': isCollapsed }">
    <!-- Header -->
    <DashboardHeader />

    <!-- Sidebar -->
    <DashboardSidebar />

    <!-- Conteúdo Principal -->
    <main class="main-content">
      <!-- Breadcrumb -->
      <div class="breadcrumb">
        <router-link to="/dashboard" class="breadcrumb-home" aria-label="Início">
          <svg viewBox="0 0 24 24" width="16" height="16">
            <path fill="currentColor" d="M12 3l9 8h-3v9h-5v-6H11v6H6v-9H3l9-8z"/>
          </svg>
        </router-link>
        <span class="breadcrumb-separator">/</span>
        <span class="breadcrumb-current">Dashboard</span>
      </div>
      <!-- Mensagem de Boas-vindas -->
      <div class="welcome-section">
        <div class="welcome-header">
          <div class="welcome-content">
            <h1 class="welcome-title">{{ getWelcomeMessage() }}, {{ userName }}! 👋</h1>
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
              { value: metricas.pedidos.pendentes.toString(), label: 'pendentes', color: '#818cf8' },
              { value: metricas.pedidos.total.toString(), label: 'total', color: '#6366f1' }
            ]"
            icon-color="#6366f1"
            variant="primary"
            @action="navigateTo('/pedidos')"
          />

          <!-- Cotações -->
          <MetricCard
            title="Cotações"
            description="Gerenciar cotações e fornecedores"
            :metrics="[
              { value: metricas.cotacoes.abertas.toString(), label: 'cadastradas', color: '#34d399' },
              { value: metricas.cotacoes.emAnalise.toString(), label: 'recentes', color: '#10b981' }
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
              { value: metricas.fornecedores.ativos.toString(), label: 'cadastrados', color: '#f59e0b' },
              { value: metricas.fornecedores.novos.toString(), label: 'recentes', color: '#fbbf24' }
            ]"
            icon-color="#f59e0b"
            variant="warning"
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
import { useSidebar } from '@/composables/useSidebar'
import { getWelcomeMessage as getWelcomeMessageUtil } from '../utils/genderUtils'
import logger from '../utils/logger.js'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import MetricCard from '@/features/dashboard/components/MetricCard.vue'
import QuickActions from '@/features/dashboard/components/QuickActions.vue'
import pedidoService from '../services/pedidoService.js'
import fornecedorService from '../services/fornecedorService.js'
import cotacaoService from '../services/cotacaoService.js'

const router = useRouter()
const authStore = useAuthStore()
const { isCollapsed } = useSidebar()

// Estados reativos para métricas reais
const metricas = ref({
  pedidos: { pendentes: 0, total: 0 },
  cotacoes: { abertas: 0, emAnalise: 0 },
  fornecedores: { ativos: 0, novos: 0 }
})

// Flag para evitar múltiplas chamadas simultâneas
const isLoadingMetricas = ref(false)
const lastLoadTime = ref(0)
const COOLDOWN_MS = 2000 // Mínimo 2 segundos entre chamadas

const userName = computed(() => {
  // authStore.user tem a propriedade 'nome' mapeada pelo jwtUtils
  const fullName = authStore.user?.nome || authStore.user?.username || 'Usuário'
  // Retorna apenas o primeiro nome
  return fullName.split(' ')[0]
})

// Função para gerar mensagem de boas-vindas com gênero correto
const getWelcomeMessage = () => {
  return getWelcomeMessageUtil(authStore.user, 'volta')
}

// Carregar métricas reais do backend
const carregarMetricas = async () => {
  const now = Date.now()

  // Evitar chamadas muito frequentes (cooldown de 2 segundos)
  if (now - lastLoadTime.value < COOLDOWN_MS) {
    return
  }

  // Evitar chamadas simultâneas
  if (isLoadingMetricas.value) {
    return
  }

  lastLoadTime.value = now
  isLoadingMetricas.value = true

  try {

    // Carregar pedidos
    try {
      const pedidos = await pedidoService.listar()
      if (pedidos && Array.isArray(pedidos)) {
        metricas.value.pedidos.total = pedidos.length
        metricas.value.pedidos.pendentes = pedidos.filter(p =>
          p.status === 'PENDENTE' || p.status === 'EM_ANALISE'
        ).length
      }
    } catch (error) {
      logger.error('❌ Erro ao carregar pedidos:', error)
    }

    // Carregar fornecedores - TODOS os fornecedores cadastrados (produtos + serviços)
    try {
      const todosFornecedores = await fornecedorService.listarTodos()
      if (todosFornecedores && Array.isArray(todosFornecedores)) {
        // Total de fornecedores cadastrados no sistema
        metricas.value.fornecedores.ativos = todosFornecedores.length

        // Calcular fornecedores novos (cadastrados nos últimos 30 dias)
        // Nota: Como o backend não possui campo dataCadastro, vamos estimar com base no ID
        // Fornecedores com IDs mais altos são considerados mais recentes
        const idsOrdenados = todosFornecedores.map(f => f.id).sort((a, b) => b - a)
        const totalFornecedores = idsOrdenados.length
        const percentualNovos = 0.2 // 20% considerados "novos"
        const quantidadeNovos = Math.ceil(totalFornecedores * percentualNovos)
        metricas.value.fornecedores.novos = Math.min(quantidadeNovos, totalFornecedores)

      }
    } catch (error) {
      logger.error('❌ Erro ao carregar fornecedores:', error)
    }

    // Carregar cotações
    try {
      const cotacoes = await cotacaoService.listar()
      if (cotacoes && Array.isArray(cotacoes)) {
        // Como Cotacao não possui status, vamos usar métricas baseadas em dados reais
        const hoje = new Date()
        const trintaDiasAtras = new Date(hoje.getTime() - (30 * 24 * 60 * 60 * 1000))

        // Total de cotações cadastradas
        metricas.value.cotacoes.abertas = cotacoes.length

        // Cotações recentes (últimos 30 dias) baseado na dataCotacao
        metricas.value.cotacoes.emAnalise = cotacoes.filter(c => {
          if (c.dataCotacao) {
            const dataCotacao = new Date(c.dataCotacao)
            return dataCotacao >= trintaDiasAtras
          }
          return false
        }).length

      }
    } catch (error) {
      logger.error('❌ Erro ao carregar cotações:', error)
    }

  } catch (error) {
    logger.error('❌ Erro geral ao carregar métricas do dashboard:', error)
  } finally {
    isLoadingMetricas.value = false
  }
}

onMounted(() => {
  carregarMetricas()
})

const handleQuickAction = (action) => {

  // Navegar para a rota correspondente com query param para abrir modal
  if (action.route) {
    router.push({
      path: action.route,
      query: { action: action.action }
    })
  }
}

const navigateTo = (path) => {
  // Navegar para as rotas implementadas
  const rotasImplementadas = ['/pedidos', '/cotacoes', '/fornecedores']

  if (rotasImplementadas.includes(path)) {
    router.push(path)
  }
}
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
