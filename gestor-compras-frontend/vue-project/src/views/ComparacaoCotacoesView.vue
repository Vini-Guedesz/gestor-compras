<template>
  <div class="dashboard-layout">
    <!-- Header -->
    <DashboardHeader />

    <!-- Sidebar -->
    <DashboardSidebar />

    <!-- Conteúdo Principal -->
    <main class="main-content">
      <!-- Header da comparação -->
      <div class="welcome-section">
        <div class="welcome-header">
          <div class="welcome-content">
            <button @click="voltarParaCotacoes" class="btn-voltar">
              <svg class="icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M15 18l-6-6 6-6"/>
              </svg>
              Voltar
            </button>
            <div class="titulo-form">
              <h1 class="welcome-title">Comparação de Propostas 📊</h1>
              <p class="welcome-subtitle">
                Cotação #{{ String(cotacao?.id).padStart(3, '0') }} - {{ cotacao?.descricao }}
              </p>
            </div>
          </div>
          <div class="action-buttons">
            <button @click="exportarComparacao" class="action-button secondary">
              <svg class="action-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="white" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
              </svg>
              Exportar
            </button>
            <button @click="abrirModalDecisao" class="action-button" :disabled="!propostas.length">
              <svg class="action-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="white" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              Selecionar Vencedor
            </button>
          </div>
        </div>
      </div>

      <!-- Resumo da Cotação -->
      <div class="metrics-section">
        <div class="metrics-grid">
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon total">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                </svg>
              </div>
              <span class="metric-label">Propostas Recebidas</span>
            </div>
            <div class="metric-value">{{ propostas.length }}</div>
            <div class="metric-growth neutral">Fornecedores participantes</div>
          </div>

          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon active">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
              <span class="metric-label">Menor Preço</span>
            </div>
            <div class="metric-value">{{ menorPreco }}</div>
            <div class="metric-growth positive">Melhor oferta</div>
          </div>

          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon rating">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z"/>
                </svg>
              </div>
              <span class="metric-label">Tempo Médio</span>
            </div>
            <div class="metric-value">{{ tempoMedio }}</div>
            <div class="metric-growth neutral">Dias para entrega</div>
          </div>

          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon value">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
              <span class="metric-label">Status</span>
            </div>
            <div class="metric-value">{{ statusCotacao }}</div>
            <div class="metric-growth" :class="statusClass">{{ statusTexto }}</div>
          </div>
        </div>
      </div>

      <!-- Tabela de Comparação -->
      <div class="data-section">
        <div class="section-header">
          <h2 class="section-title">Comparativo de Propostas</h2>
          <div class="view-controls">
            <button 
              :class="['view-button', { active: visualizacao === 'tabela' }]"
              @click="visualizacao = 'tabela'"
            >
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M3,3H21A2,2 0 0,1 23,5V19A2,2 0 0,1 21,21H3A2,2 0 0,1 1,19V5A2,2 0 0,1 3,3M5,7V5H7V7H5M9,5V7H11V7H9M13,5V7H15V7H13M17,5V7H19V7H17M5,9V11H7V9H5M9,9V11H11V9H9M13,9V11H15V9H13M17,9V11H19V9H17M5,13V15H7V13H5M9,13V15H11V13H9M13,13V15H15V13H13M17,13V15H19V13H17M5,17V19H7V17H5M9,17V19H11V17H9M13,17V19H15V17H13M17,17V19H19V17H17Z"/>
              </svg>
              Tabela
            </button>
            <button 
              :class="['view-button', { active: visualizacao === 'cards' }]"
              @click="visualizacao = 'cards'"
            >
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M3,11H11V3H3M3,21H11V13H3M13,21H21V13H13M13,3V11H21V3"/>
              </svg>
              Cards
            </button>
          </div>
        </div>

        <!-- Visualização em Tabela -->
        <div v-if="visualizacao === 'tabela'" class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>Fornecedor</th>
                <th>Preço Total</th>
                <th>Prazo Entrega</th>
                <th>Condições</th>
                <th>Avaliação</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="proposta in propostas" :key="proposta.id" class="table-row" :class="{ 'highlight': proposta.melhorPreco }">
                <td>
                  <div class="fornecedor-cell">
                    <div class="fornecedor-avatar">
                      {{ proposta.fornecedor.nomeFantasia.charAt(0) }}
                    </div>
                    <div class="fornecedor-info">
                      <div class="fornecedor-nome">{{ proposta.fornecedor.nomeFantasia }}</div>
                      <div class="fornecedor-cnpj">{{ proposta.fornecedor.cnpj }}</div>
                    </div>
                  </div>
                </td>
                <td>
                  <div class="preco-cell">
                    <div class="preco-valor" :class="{ 'melhor-preco': proposta.melhorPreco }">
                      {{ formatarMoeda(proposta.precoTotal) }}
                    </div>
                    <div v-if="proposta.melhorPreco" class="badge-melhor">Melhor Preço</div>
                  </div>
                </td>
                <td>
                  <div class="prazo-cell">
                    <div class="prazo-valor">{{ proposta.prazoEntrega }} dias</div>
                    <div class="prazo-data">{{ calcularDataEntrega(proposta.prazoEntrega) }}</div>
                  </div>
                </td>
                <td>
                  <div class="condicoes-cell">
                    <div class="condicao-item">{{ proposta.condicoesPagamento }}</div>
                    <div class="garantia-item">{{ proposta.garantia }}</div>
                  </div>
                </td>
                <td>
                  <div class="avaliacao-cell">
                    <div class="rating-stars">
                      <span v-for="n in 5" :key="n" class="star" :class="{ 'filled': n <= proposta.avaliacao }">
                        ★
                      </span>
                    </div>
                    <div class="rating-text">{{ proposta.avaliacao }}/5</div>
                  </div>
                </td>
                <td>
                  <div class="actions-cell">
                    <button @click="visualizarDetalhes(proposta)" class="action-btn view" title="Ver Detalhes">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M12,9A3,3 0 0,0 9,12A3,3 0 0,0 12,15A3,3 0 0,0 15,12A3,3 0 0,0 12,9M12,17A5,5 0 0,1 7,12A5,5 0 0,1 12,7A5,5 0 0,1 17,12A5,5 0 0,1 12,17M12,4.5C7,4.5 2.73,7.61 1,12C2.73,16.39 7,19.5 12,19.5C17,19.5 21.27,16.39 23,12C21.27,7.61 17,4.5 12,4.5Z"/>
                      </svg>
                    </button>
                    <button @click="selecionarProposta(proposta)" class="action-btn select" title="Selecionar">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M9,20.42L2.79,14.21L5.62,11.38L9,14.77L18.88,4.88L21.71,7.71L9,20.42Z"/>
                      </svg>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Visualização em Cards -->
        <div v-if="visualizacao === 'cards'" class="cards-container">
          <div v-for="proposta in propostas" :key="proposta.id" class="proposta-card" :class="{ 'melhor-proposta': proposta.melhorPreco }">
            <div class="card-header">
              <div class="fornecedor-info">
                <div class="fornecedor-avatar">
                  {{ proposta.fornecedor.nomeFantasia.charAt(0) }}
                </div>
                <div>
                  <div class="fornecedor-nome">{{ proposta.fornecedor.nomeFantasia }}</div>
                  <div class="fornecedor-cnpj">{{ proposta.fornecedor.cnpj }}</div>
                </div>
              </div>
              <div v-if="proposta.melhorPreco" class="badge-melhor">
                🏆 Melhor Preço
              </div>
            </div>
            
            <div class="card-body">
              <div class="card-meta">
                <div class="meta-item">
                  <span class="meta-label">Preço Total:</span>
                  <span class="meta-value preco">{{ formatarMoeda(proposta.precoTotal) }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">Prazo:</span>
                  <span class="meta-value">{{ proposta.prazoEntrega }} dias</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">Pagamento:</span>
                  <span class="meta-value">{{ proposta.condicoesPagamento }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">Avaliação:</span>
                  <div class="rating-inline">
                    <div class="rating-stars">
                      <span v-for="n in 5" :key="n" class="star" :class="{ 'filled': n <= proposta.avaliacao }">★</span>
                    </div>
                    <span class="rating-text">{{ proposta.avaliacao }}/5</span>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="card-actions">
              <button @click="visualizarDetalhes(proposta)" class="card-action-btn secondary">
                Ver Detalhes
              </button>
              <button @click="selecionarProposta(proposta)" class="card-action-btn primary">
                Selecionar
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="carregando" class="loading-overlay">
        <div class="loading-content">
          <div class="loading-spinner"></div>
          <p>Carregando comparação...</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import DashboardHeader from '../components/DashboardHeader.vue'
import DashboardSidebar from '../components/DashboardSidebar.vue'
import cotacaoService from '../services/cotacaoService.js'

const router = useRouter()
const route = useRoute()

// Estado reativo
const carregando = ref(false)
const visualizacao = ref('tabela')

// Dados da cotação
const cotacao = ref({
  id: 1,
  descricao: 'Material de Escritório Q4 2024',
  status: 'em-analise'
})

// Propostas de exemplo
const propostas = ref([
  {
    id: 1,
    fornecedor: {
      id: 1,
      nomeFantasia: 'Papelaria Central',
      cnpj: '12.345.678/0001-90'
    },
    precoTotal: 2850.00,
    prazoEntrega: 5,
    condicoesPagamento: '30 dias',
    garantia: '12 meses',
    avaliacao: 4.5,
    melhorPreco: true
  },
  {
    id: 2,
    fornecedor: {
      id: 2,
      nomeFantasia: 'Office Max',
      cnpj: '98.765.432/0001-10'
    },
    precoTotal: 3120.00,
    prazoEntrega: 3,
    condicoesPagamento: '15 dias',
    garantia: '6 meses',
    avaliacao: 4.2,
    melhorPreco: false
  },
  {
    id: 3,
    fornecedor: {
      id: 3,
      nomeFantasia: 'Kalunga',
      cnpj: '11.222.333/0001-44'
    },
    precoTotal: 2950.00,
    prazoEntrega: 7,
    condicoesPagamento: '45 dias',
    garantia: '18 meses',
    avaliacao: 4.8,
    melhorPreco: false
  }
])

// Computadas
const menorPreco = computed(() => {
  if (!propostas.value.length) return 'R$ 0,00'
  const menor = Math.min(...propostas.value.map(p => p.precoTotal))
  return formatarMoeda(menor)
})

const tempoMedio = computed(() => {
  if (!propostas.value.length) return '0'
  const media = propostas.value.reduce((acc, p) => acc + p.prazoEntrega, 0) / propostas.value.length
  return Math.round(media)
})

const statusCotacao = computed(() => {
  const status = {
    'rascunho': 'Rascunho',
    'enviada': 'Enviada',
    'em-analise': 'Em Análise',
    'avaliacao': 'Avaliação',
    'finalizada': 'Finalizada'
  }
  return status[cotacao.value.status] || cotacao.value.status
})

const statusClass = computed(() => {
  const classes = {
    'rascunho': 'neutral',
    'enviada': 'neutral',
    'em-analise': 'positive',
    'avaliacao': 'positive',
    'finalizada': 'positive'
  }
  return classes[cotacao.value.status] || 'neutral'
})

const statusTexto = computed(() => {
  const textos = {
    'rascunho': 'Preparando',
    'enviada': 'Aguardando',
    'em-analise': 'Analisando',
    'avaliacao': 'Comparando',
    'finalizada': 'Concluída'
  }
  return textos[cotacao.value.status] || 'Em andamento'
})

// Métodos
const voltarParaCotacoes = () => {
  router.push('/cotacoes')
}

const formatarMoeda = (valor) => {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(valor)
}

const calcularDataEntrega = (dias) => {
  const hoje = new Date()
  const entrega = new Date(hoje.setDate(hoje.getDate() + dias))
  return entrega.toLocaleDateString('pt-BR')
}

const visualizarDetalhes = (proposta) => {
  console.log('Visualizar detalhes:', proposta)
  // Implementar modal de detalhes
}

const selecionarProposta = (proposta) => {
  console.log('Selecionar proposta:', proposta)
  // Implementar seleção de proposta vencedora
}

const abrirModalDecisao = () => {
  console.log('Abrir modal de decisão')
  // Implementar modal de seleção de vencedor
}

const exportarComparacao = () => {
  console.log('Exportar comparação')
  // Implementar exportação
}

const carregarComparacao = async () => {
  try {
    carregando.value = true
    // Simular carregamento de dados
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // Marcar melhor preço
    if (propostas.value.length) {
      const menorPrecoValue = Math.min(...propostas.value.map(p => p.precoTotal))
      propostas.value.forEach(p => {
        p.melhorPreco = p.precoTotal === menorPrecoValue
      })
    }
  } catch (error) {
    console.error('Erro ao carregar comparação:', error)
  } finally {
    carregando.value = false
  }
}

// Lifecycle
onMounted(() => {
  carregarComparacao()
})
</script>

<style scoped>
/* Importar layout global */
@import '../assets/css/layout.css';

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
  display: flex;
  align-items: flex-start;
  gap: 24px;
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

.btn-voltar {
  display: flex;
  align-items: center;
  gap: 8px;
  background: white;
  border: 1px solid #e5e7eb;
  padding: 12px 16px;
  border-radius: 8px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;
  font-size: 0.875rem;
}

.btn-voltar:hover {
  background: #f9fafb;
  color: #374151;
  border-color: #9ca3af;
}

.action-buttons {
  display: flex;
  gap: 12px;
  align-items: center;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #3b82f6;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.action-button.secondary {
  background: #6b7280;
}

.action-button.secondary:hover {
  background: #4b5563;
}

.action-button:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.action-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.action-icon {
  flex-shrink: 0;
}

/* Métricas */
.metrics-section {
  margin-bottom: 32px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.metric-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px -3px rgba(0, 0, 0, 0.1);
}

.metric-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.metric-icon.total {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
}

.metric-icon.active {
  background: linear-gradient(135deg, #10b981, #047857);
}

.metric-icon.rating {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.metric-icon.value {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
}

.metric-label {
  font-size: 0.875rem;
  color: #6b7280;
  font-weight: 500;
}

.metric-value {
  font-size: 2rem;
  font-weight: 700;
  color: #111827;
  margin-bottom: 8px;
}

.metric-growth {
  font-size: 0.875rem;
  font-weight: 500;
}

.metric-growth.positive {
  color: #10b981;
}

.metric-growth.neutral {
  color: #6b7280;
}

/* Seção de dados */
.data-section {
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e5e7eb;
}

.section-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.view-controls {
  display: flex;
  gap: 8px;
  background: #f3f4f6;
  padding: 4px;
  border-radius: 8px;
}

.view-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: transparent;
  border: none;
  border-radius: 6px;
  color: #6b7280;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.view-button.active {
  background: white;
  color: #1F285F;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* Tabela */
.table-container {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  background: #f9fafb;
  padding: 16px;
  text-align: left;
  font-weight: 600;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
  font-size: 0.875rem;
}

.data-table td {
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
}

.table-row:hover {
  background: #f9fafb;
}

.table-row.highlight {
  background: #f0f9ff;
  border-left: 4px solid #3b82f6;
}

.fornecedor-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.fornecedor-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1F285F, #4338ca);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.875rem;
}

.fornecedor-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.fornecedor-nome {
  font-weight: 600;
  color: #111827;
}

.fornecedor-cnpj {
  font-size: 0.75rem;
  color: #6b7280;
}

.preco-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.preco-valor {
  font-weight: 600;
  color: #111827;
  font-size: 1.125rem;
}

.preco-valor.melhor-preco {
  color: #10b981;
}

.badge-melhor {
  background: #dcfce7;
  color: #166534;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  width: fit-content;
}

.prazo-cell,
.condicoes-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.prazo-valor {
  font-weight: 500;
  color: #374151;
}

.prazo-data {
  font-size: 0.75rem;
  color: #6b7280;
}

.condicao-item,
.garantia-item {
  font-size: 0.875rem;
  color: #6b7280;
}

.avaliacao-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: flex-start;
}

.rating-stars {
  display: flex;
  gap: 2px;
}

.star {
  color: #d1d5db;
  font-size: 1.125rem;
}

.star.filled {
  color: #f59e0b;
}

.rating-text {
  font-size: 0.75rem;
  color: #6b7280;
}

.actions-cell {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: none;
  border: 1px solid #e5e7eb;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  color: #6b7280;
}

.action-btn:hover {
  background: #f3f4f6;
  color: #374151;
}

.action-btn.select:hover {
  background: #dcfce7;
  color: #166534;
  border-color: #10b981;
}

/* Cards */
.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
  padding: 20px;
}

.proposta-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
}

.proposta-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px -3px rgba(0, 0, 0, 0.1);
}

.proposta-card.melhor-proposta {
  border-color: #10b981;
  box-shadow: 0 0 0 1px #10b981;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.card-body {
  margin-bottom: 20px;
}

.card-meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.meta-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.meta-label {
  font-size: 0.875rem;
  color: #6b7280;
}

.meta-value {
  font-weight: 600;
  color: #374151;
}

.meta-value.preco {
  color: #10b981;
  font-size: 1.125rem;
}

.rating-inline {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-actions {
  display: flex;
  gap: 12px;
}

.card-action-btn {
  flex: 1;
  padding: 12px 16px;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.card-action-btn.primary {
  background: #3b82f6;
  color: white;
}

.card-action-btn.primary:hover {
  background: #2563eb;
}

.card-action-btn.secondary {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #e5e7eb;
}

.card-action-btn.secondary:hover {
  background: #e5e7eb;
}

/* Loading */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.loading-content {
  text-align: center;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e5e7eb;
  border-top: 4px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Responsividade */
@media (max-width: 768px) {
  .welcome-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .welcome-content {
    flex-direction: column;
    gap: 16px;
  }

  .action-buttons {
    justify-content: center;
  }

  .section-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .cards-container {
    grid-template-columns: 1fr;
    padding: 16px;
  }

  .table-container {
    overflow-x: auto;
  }

  .data-table th,
  .data-table td {
    padding: 12px 8px;
  }
}
</style>