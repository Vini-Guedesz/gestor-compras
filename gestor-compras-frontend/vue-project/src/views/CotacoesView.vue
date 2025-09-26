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
            <h1 class="welcome-title">Gestão de Cotações 💰</h1>
            <p class="welcome-subtitle">
              Gerencie solicitações de cotações e compare propostas de fornecedores
            </p>
          </div>
          <div class="action-buttons">
            <button class="action-button secondary" @click="exportarRelatorio" :disabled="gerandoRelatorio">
              <svg class="action-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="white" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
              </svg>
              <span v-if="gerandoRelatorio" class="loading-content">
                <span class="loading-spinner"></span>
                Gerando...
              </span>
              <span v-else>Gerar Relatório</span>
            </button>
            <button class="action-button" @click="abrirFormularioNova">
              <svg class="action-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="white" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" />
              </svg>
              Nova Cotação
            </button>
          </div>
        </div>
      </div>

      <!-- Indicadores (Cards de Métricas Rápidas) -->
      <div class="metrics-section">
        <div class="metrics-grid">
          <!-- Cotações Abertas -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon total">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"/>
                </svg>
              </div>
              <span class="metric-label">Cotações Abertas</span>
            </div>
            <div class="metric-value">{{ resumo.abertas }}</div>
            <div class="metric-growth positive">{{ novasCotacoesMes }} novas este mês</div>
          </div>

          <!-- Em Análise -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon active">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
              <span class="metric-label">Em Análise</span>
            </div>
            <div class="metric-value">{{ resumo.aguardando }}</div>
            <div class="metric-growth neutral">Aguardando propostas</div>
          </div>

          <!-- Finalizadas -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon rating">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
              <span class="metric-label">Finalizadas</span>
            </div>
            <div class="metric-value">{{ resumo.finalizadas }}</div>
            <div class="metric-growth positive">{{ percentualFinalizadas }}% de sucesso</div>
          </div>

          <!-- Vencidas -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon value">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
                </svg>
              </div>
              <span class="metric-label">Vencidas</span>
            </div>
            <div class="metric-value">{{ resumo.vencidas }}</div>
            <div class="metric-growth negative">Requer atenção</div>
          </div>
        </div>
      </div>

      <!-- Controles e Filtros -->
      <div class="controls-section">
        <div class="search-container">
          <input
            type="text"
            v-model="termoBusca"
            placeholder="Buscar cotações por descrição, ID ou observações..."
            class="search-input"
            @input="buscarCotacoes"
          >
          <svg class="search-icon" viewBox="0 0 24 24" width="20" height="20">
            <path fill="currentColor" d="M9.5,3A6.5,6.5 0 0,1 16,9.5C16,11.11 15.41,12.59 14.44,13.73L14.71,14H15.5L20.5,19L19,20.5L14,15.5V14.71L13.73,14.44C12.59,15.41 11.11,16 9.5,16A6.5,6.5 0 0,1 3,9.5A6.5,6.5 0 0,1 9.5,3M9.5,5C7,5 5,7 5,9.5C5,12 7,14 9.5,14C12,14 14,12 14,9.5C14,7 12,5 9.5,5Z"/>
          </svg>
        </div>

        <div class="filter-controls">
          <select v-model="filtros.status" class="filter-select" @change="aplicarFiltros">
            <option value="">Todos os Status</option>
            <option value="enviada">Enviada</option>
            <option value="em-analise">Em Análise</option>
            <option value="selecionada">Selecionada</option>
            <option value="aprovada">Aprovada</option>
            <option value="cancelada">Cancelada</option>
            <option value="vencida">Vencida</option>
          </select>

          <select v-model="filtros.periodo" class="filter-select" @change="aplicarFiltros">
            <option value="">Todos os Períodos</option>
            <option value="semana">Última semana</option>
            <option value="mes">Último mês</option>
            <option value="trimestre">Último trimestre</option>
          </select>

          <button @click="limparFiltros" class="clear-filters-btn">
            <svg viewBox="0 0 24 24" width="16" height="16">
              <path fill="currentColor" d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z"/>
            </svg>
            Limpar
          </button>
        </div>
      </div>

      <!-- Tabela de Cotações -->
      <div class="data-section">
        <div class="section-header">
          <h2 class="section-title">Lista de Cotações</h2>
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
                <th @click="ordenar('id')" class="sortable">
                  ID
                  <svg v-if="ordenacao.campo === 'id'" class="sort-icon" viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" :d="ordenacao.direcao === 'asc' ? 'M7,10L12,15L17,10H7Z' : 'M7,15L12,10L17,15H7Z'"/>
                  </svg>
                </th>
                <th @click="ordenar('descricao')" class="sortable">
                  Descrição
                  <svg v-if="ordenacao.campo === 'descricao'" class="sort-icon" viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" :d="ordenacao.direcao === 'asc' ? 'M7,10L12,15L17,10H7Z' : 'M7,15L12,10L17,15H7Z'"/>
                  </svg>
                </th>
                <th>Fornecedores</th>
                <th @click="ordenar('status')" class="sortable">
                  Status
                  <svg v-if="ordenacao.campo === 'status'" class="sort-icon" viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" :d="ordenacao.direcao === 'asc' ? 'M7,10L12,15L17,10H7Z' : 'M7,15L12,10L17,15H7Z'"/>
                  </svg>
                </th>
                <th @click="ordenar('dataLimite')" class="sortable">
                  Prazo
                  <svg v-if="ordenacao.campo === 'dataLimite'" class="sort-icon" viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" :d="ordenacao.direcao === 'asc' ? 'M7,10L12,15L17,10H7Z' : 'M7,15L12,10L17,15H7Z'"/>
                  </svg>
                </th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="cotacao in cotacoesPaginadas" :key="cotacao.id" class="table-row">
                <td>
                  <span class="id-badge">{{ String(cotacao.id).padStart(3, '0') }}</span>
                </td>
                <td>
                  <div class="description-cell">
                    <div class="description-title">{{ cotacao.descricao }}</div>
                    <div class="description-subtitle">{{ cotacao.centroCusto?.nome }}</div>
                  </div>
                </td>
                <td>
                  <div class="suppliers-cell">
                    <span class="suppliers-count">{{ cotacao.numeroFornecedores }}</span>
                    <div class="suppliers-avatars">
                      <div
                        v-for="fornecedor in cotacao.fornecedores?.slice(0, 3)"
                        :key="fornecedor.id"
                        class="supplier-avatar"
                        :title="fornecedor.nomeFantasia"
                      >
                        {{ fornecedor.nomeFantasia.charAt(0) }}
                      </div>
                      <div v-if="cotacao.numeroFornecedores > 3" class="suppliers-more">
                        +{{ cotacao.numeroFornecedores - 3 }}
                      </div>
                    </div>
                  </div>
                </td>
                <td>
                  <span :class="['status-badge', `status-${cotacao.status}`]">
                    {{ getStatusTexto(cotacao.status) }}
                  </span>
                </td>
                <td>
                  <div class="deadline-cell" :class="{ 'deadline-expired': isPrazoVencido(cotacao.dataLimite) }">
                    <div class="deadline-date">{{ formatarData(cotacao.dataLimite) }}</div>
                    <div class="deadline-remaining">{{ getDiasRestantes(cotacao.dataLimite) }}</div>
                  </div>
                </td>
                <td>
                  <div class="actions-cell">
                    <button @click="visualizarCotacao(cotacao.id)" class="action-btn view" title="Visualizar">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M12,9A3,3 0 0,0 9,12A3,3 0 0,0 12,15A3,3 0 0,0 15,12A3,3 0 0,0 12,9M12,17A5,5 0 0,1 7,12A5,5 0 0,1 12,7A5,5 0 0,1 17,12A5,5 0 0,1 12,17M12,4.5C7,4.5 2.73,7.61 1,12C2.73,16.39 7,19.5 12,19.5C17,19.5 21.27,16.39 23,12C21.27,7.61 17,4.5 12,4.5Z"/>
                      </svg>
                    </button>
                    <button
                      v-if="podeEditar(cotacao.status)"
                      @click="editarCotacao(cotacao)"
                      class="action-btn edit"
                      title="Editar"
                    >
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M20.71,7.04C21.1,6.65 21.1,6 20.71,5.63L18.37,3.29C18,2.9 17.35,2.9 16.96,3.29L15.12,5.12L18.87,8.87M3,17.25V21H6.75L17.81,9.93L14.06,6.18L3,17.25Z"/>
                      </svg>
                    </button>
                    <button
                      v-if="podeComparar(cotacao.status)"
                      @click="compararCotacao(cotacao.id)"
                      class="action-btn compare"
                      title="Comparar Propostas"
                    >
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M19,3H5C3.9,3 3,3.9 3,5V19C3,20.1 3.9,21 5,21H19C20.1,21 21,20.1 21,19V5C21,3.9 20.1,3 19,3M19,19H5V5H19V19M7,10H9V17H7V10M11,7H13V17H11V7M15,13H17V17H15V13Z"/>
                      </svg>
                    </button>
                    <button @click="deletarCotacao(cotacao.id)" class="action-btn delete" title="Excluir">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M19,4H15.5L14.5,3H9.5L8.5,4H5V6H19M6,19A2,2 0 0,0 8,21H16A2,2 0 0,0 18,19V7H6V19Z"/>
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
          <div v-for="cotacao in cotacoesPaginadas" :key="cotacao.id" class="cotacao-card">
            <div class="card-header">
              <span class="card-id">{{ String(cotacao.id).padStart(3, '0') }}</span>
              <span :class="['card-status', `status-${cotacao.status}`]">
                {{ getStatusTexto(cotacao.status) }}
              </span>
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ cotacao.descricao }}</h3>
              <p class="card-subtitle">{{ cotacao.centroCusto?.nome }}</p>
              <div class="card-meta">
                <div class="meta-item">
                  <span class="meta-label">Fornecedores:</span>
                  <span class="meta-value">{{ cotacao.numeroFornecedores }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">Prazo:</span>
                  <span class="meta-value" :class="{ expired: isPrazoVencido(cotacao.dataLimite) }">
                    {{ formatarData(cotacao.dataLimite) }}
                  </span>
                </div>
              </div>
            </div>
            <div class="card-actions">
              <button @click="visualizarCotacao(cotacao.id)" class="card-action-btn primary">
                Visualizar
              </button>
              <button
                v-if="podeEditar(cotacao.status)"
                @click="editarCotacao(cotacao)"
                class="card-action-btn secondary"
              >
                Editar
              </button>
              <button
                v-if="podeComparar(cotacao.status)"
                @click="compararCotacao(cotacao.id)"
                class="card-action-btn warning"
              >
                Comparar
              </button>
            </div>
          </div>
        </div>

        <!-- Paginação -->
        <div v-if="totalPaginas > 1" class="pagination">
          <div class="pagination-info">
            Mostrando {{ (paginaAtual - 1) * itensPorPagina + 1 }} -
            {{ Math.min(paginaAtual * itensPorPagina, totalItens) }} de {{ totalItens }} cotações
          </div>
          <div class="pagination-controls">
            <button
              @click="irParaPagina(paginaAtual - 1)"
              :disabled="paginaAtual === 1"
              class="pagination-btn"
            >
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M15.41,16.58L10.83,12L15.41,7.41L14,6L8,12L14,18L15.41,16.58Z"/>
              </svg>
            </button>

            <button
              v-for="pagina in paginasVisiveis"
              :key="pagina"
              @click="irParaPagina(pagina)"
              :class="['pagination-number', { active: pagina === paginaAtual }]"
            >
              {{ pagina }}
            </button>

            <button
              @click="irParaPagina(paginaAtual + 1)"
              :disabled="paginaAtual === totalPaginas"
              class="pagination-btn"
            >
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M8.59,16.58L13.17,12L8.59,7.41L10,6L16,12L10,18L8.59,16.58Z"/>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Loading Spinner - REMOVIDO -->
      <!--
      <div v-if="carregando" class="loading-overlay">
        <div class="loading-content">
          <div class="loading-spinner"></div>
          <p>Carregando cotações...</p>
        </div>
      </div>
      -->
    </main>

    <!-- Modal de Nova Cotação -->
    <CotacaoForm
      :isVisible="showCotacaoForm"
      :cotacao="cotacaoSelecionada"
      @close="fecharFormulario"
      @save="salvarCotacao"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import DashboardHeader from '../components/DashboardHeader.vue'
import DashboardSidebar from '../components/DashboardSidebar.vue'
import CotacaoForm from '../components/CotacaoForm.vue'
import { cotacaoService } from '../services/cotacaoService.js'

const router = useRouter()

// Estado reativo
const gerandoRelatorio = ref(false)
const showCotacaoForm = ref(false)
const cotacaoSelecionada = ref(null)
const cotacoes = ref([])
const termoBusca = ref('')
const visualizacao = ref('tabela')
const paginaAtual = ref(1)
const itensPorPagina = ref(10)

// Filtros
const filtros = ref({
  status: '',
  periodo: ''
})

// Ordenação
const ordenacao = ref({
  campo: 'id',
  direcao: 'desc'
})

// Dados calculados
const novasCotacoesMes = ref(8)
const percentualFinalizadas = ref(75)

// Computadas
const resumo = computed(() => {
  const abertas = cotacoes.value.filter(c => ['enviada', 'em-analise'].includes(c.status)).length
  const aguardando = cotacoes.value.filter(c => c.status === 'em-analise').length
  const finalizadas = cotacoes.value.filter(c => ['selecionada', 'aprovada', 'finalizada'].includes(c.status)).length
  const vencidas = cotacoes.value.filter(c => c.status === 'vencida').length

  return { abertas, aguardando, finalizadas, vencidas }
})

const cotacoesFiltradas = computed(() => {
  let resultado = [...cotacoes.value]

  // Aplicar busca textual
  if (termoBusca.value) {
    const termo = termoBusca.value.toLowerCase()
    resultado = resultado.filter(c =>
      c.descricao.toLowerCase().includes(termo) ||
      c.id.toString().includes(termo) ||
      c.centroCusto?.nome.toLowerCase().includes(termo)
    )
  }

  // Aplicar filtros
  if (filtros.value.status) {
    resultado = resultado.filter(c => c.status === filtros.value.status)
  }

  if (filtros.value.periodo) {
    const hoje = new Date()
    const filtroData = new Date()

    switch (filtros.value.periodo) {
      case 'semana':
        filtroData.setDate(hoje.getDate() - 7)
        break
      case 'mes':
        filtroData.setMonth(hoje.getMonth() - 1)
        break
      case 'trimestre':
        filtroData.setMonth(hoje.getMonth() - 3)
        break
    }

    resultado = resultado.filter(c => new Date(c.dataCriacao) >= filtroData)
  }

  // Aplicar ordenação
  resultado.sort((a, b) => {
    let valorA = a[ordenacao.value.campo]
    let valorB = b[ordenacao.value.campo]

    if (ordenacao.value.campo === 'dataLimite' || ordenacao.value.campo === 'dataCriacao') {
      valorA = new Date(valorA)
      valorB = new Date(valorB)
    }

    if (typeof valorA === 'string') {
      valorA = valorA.toLowerCase()
      valorB = valorB.toLowerCase()
    }

    if (ordenacao.value.direcao === 'asc') {
      return valorA < valorB ? -1 : valorA > valorB ? 1 : 0
    } else {
      return valorA > valorB ? -1 : valorA < valorB ? 1 : 0
    }
  })

  return resultado
})

const totalItens = computed(() => cotacoesFiltradas.value.length)
const totalPaginas = computed(() => Math.ceil(totalItens.value / itensPorPagina.value))

const cotacoesPaginadas = computed(() => {
  const inicio = (paginaAtual.value - 1) * itensPorPagina.value
  const fim = inicio + itensPorPagina.value
  return cotacoesFiltradas.value.slice(inicio, fim)
})

const paginasVisiveis = computed(() => {
  const total = totalPaginas.value
  const atual = paginaAtual.value
  const delta = 2
  const range = []

  for (let i = Math.max(1, atual - delta); i <= Math.min(total, atual + delta); i++) {
    range.push(i)
  }

  return range
})

// Métodos
const carregarCotacoes = async () => {
  try {
    console.log('🔄 Carregando cotações...')
    // Chamar o serviço real de cotações
    const response = await cotacaoService.listar()

    // Verificar estrutura da resposta
    if (response && Array.isArray(response)) {
      cotacoes.value = response
    } else if (response && response.data && Array.isArray(response.data)) {
      cotacoes.value = response.data
    } else {
      console.warn('⚠️ Resposta do backend não é um array válido:', response)
      cotacoes.value = []
    }

    console.log('✅ Cotações carregadas:', cotacoes.value.length)
  } catch (error) {
    console.error('❌ Erro ao carregar cotações:', error)
    cotacoes.value = []
  }
}

const buscarCotacoes = () => {
  paginaAtual.value = 1
}

const aplicarFiltros = () => {
  paginaAtual.value = 1
}

const limparFiltros = () => {
  filtros.value = {
    status: '',
    periodo: ''
  }
  termoBusca.value = ''
  paginaAtual.value = 1
}

const ordenar = (campo) => {
  if (ordenacao.value.campo === campo) {
    ordenacao.value.direcao = ordenacao.value.direcao === 'asc' ? 'desc' : 'asc'
  } else {
    ordenacao.value.campo = campo
    ordenacao.value.direcao = 'asc'
  }
}

const irParaPagina = (pagina) => {
  if (pagina >= 1 && pagina <= totalPaginas.value) {
    paginaAtual.value = pagina
  }
}

const getStatusTexto = (status) => {
  const textos = {
    'enviada': 'Enviada',
    'em-analise': 'Em Análise',
    'selecionada': 'Selecionada',
    'aprovada': 'Aprovada',
    'cancelada': 'Cancelada',
    'vencida': 'Vencida',
    'finalizada': 'Finalizada'
  }
  return textos[status] || status
}

const formatarData = (data) => {
  return new Date(data).toLocaleDateString('pt-BR')
}

const getDiasRestantes = (dataLimite) => {
  const hoje = new Date()
  const limite = new Date(dataLimite)
  const diferenca = Math.ceil((limite - hoje) / (1000 * 60 * 60 * 24))

  if (diferenca < 0) return 'Vencido'
  if (diferenca === 0) return 'Hoje'
  if (diferenca === 1) return '1 dia'
  return `${diferenca} dias`
}

const isPrazoVencido = (dataLimite) => {
  return new Date(dataLimite) < new Date()
}

const podeEditar = (status) => {
  return ['enviada'].includes(status)
}

const podeComparar = (status) => {
  return ['em-analise'].includes(status)
}

// Ações
const abrirFormularioNova = () => {
  cotacaoSelecionada.value = null
  showCotacaoForm.value = true
}

const editarCotacao = (cotacao) => {
  cotacaoSelecionada.value = cotacao
  showCotacaoForm.value = true
}

const fecharFormulario = () => {
  showCotacaoForm.value = false
  cotacaoSelecionada.value = null
}

const salvarCotacao = (dadosCotacao) => {
  console.log('Salvando cotação:', dadosCotacao)
  // Aqui você implementaria a lógica de salvamento
  // Exemplo: chamar o service de cotação

  // Recarregar a lista de cotações
  carregarCotacoes()
}

const visualizarCotacao = (id) => {
  router.push(`/cotacoes/${id}`)
}

const compararCotacao = (id) => {
  router.push(`/cotacoes/${id}/comparacao`)
}

const deletarCotacao = (id) => {
  if (confirm('Tem certeza que deseja excluir esta cotação?')) {
    cotacoes.value = cotacoes.value.filter(c => c.id !== id)
  }
}

const exportarRelatorio = async () => {
  try {
    gerandoRelatorio.value = true
    // Simulação de geração de relatório
    await new Promise(resolve => setTimeout(resolve, 2000))
    // Aqui chamaria o serviço de relatório
    console.log('Relatório gerado com sucesso!')
  } catch (error) {
    console.error('Erro ao gerar relatório:', error)
  } finally {
    gerandoRelatorio.value = false
  }
}

// Lifecycle
onMounted(() => {
  carregarCotacoes()
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

.loading-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.loading-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
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

.metric-growth.negative {
  color: #ef4444;
}

/* Seção de busca e controles */
.controls-section {
  display: flex;
  gap: 16px;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  margin-bottom: 32px;
  flex-wrap: wrap;
}

.search-container {
  flex: 1;
  position: relative;
  min-width: 300px;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
}

.search-input {
  width: 100%;
  padding: 12px 12px 12px 44px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.filter-controls {
  display: flex;
  gap: 12px;
  align-items: center;
}

.filter-select {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  min-width: 140px;
}

.clear-filters-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: white;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
}

.clear-filters-btn:hover {
  background: #f9fafb;
  border-color: #9ca3af;
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

.data-table th.sortable {
  cursor: pointer;
  user-select: none;
  transition: background-color 0.2s;
}

.data-table th.sortable:hover {
  background: #f3f4f6;
}

.sort-icon {
  margin-left: 8px;
  vertical-align: middle;
}

.data-table td {
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
}

.table-row:hover {
  background: #f9fafb;
}

.id-badge {
  background: #f3f4f6;
  color: #374151;
  padding: 4px 12px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 0.875rem;
}

.description-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.description-title {
  font-weight: 600;
  color: #111827;
}

.description-subtitle {
  font-size: 0.875rem;
  color: #6b7280;
}

.suppliers-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.suppliers-count {
  font-weight: 600;
  color: #374151;
  font-size: 0.875rem;
}

.suppliers-avatars {
  display: flex;
  align-items: center;
}

.supplier-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1F285F, #4338ca);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 600;
  margin-left: -8px;
  border: 2px solid white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.supplier-avatar:first-child {
  margin-left: 0;
}

.suppliers-more {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e5e7eb;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 600;
  margin-left: -8px;
  border: 2px solid white;
}

/* Status badges */
.status-badge {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.status-enviada { background: #dbeafe; color: #1d4ed8; }
.status-em-analise { background: #fef3c7; color: #d97706; }
.status-selecionada { background: #dcfce7; color: #166534; }
.status-aprovada { background: #dcfce7; color: #166534; }
.status-cancelada { background: #fee2e2; color: #dc2626; }
.status-vencida { background: #fee2e2; color: #dc2626; }
.status-finalizada { background: #dcfce7; color: #166534; }

.deadline-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.deadline-date {
  font-weight: 500;
  color: #374151;
}

.deadline-remaining {
  font-size: 0.75rem;
  color: #6b7280;
}

.deadline-expired .deadline-date,
.deadline-expired .deadline-remaining {
  color: #dc2626;
  font-weight: 600;
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

.action-btn.edit:hover {
  background: #dbeafe;
  color: #1d4ed8;
  border-color: #3b82f6;
}

.action-btn.compare:hover {
  background: #fef3c7;
  color: #d97706;
  border-color: #f59e0b;
}

.action-btn.delete:hover {
  background: #fee2e2;
  color: #dc2626;
  border-color: #f87171;
}

/* Cards */
.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  padding: 20px;
}

.cotacao-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
}

.cotacao-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px -3px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-id {
  background: #f3f4f6;
  color: #374151;
  padding: 4px 12px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 0.875rem;
}

.card-status {
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.card-body {
  margin-bottom: 20px;
}

.card-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #111827;
  margin: 0 0 8px 0;
}

.card-subtitle {
  color: #6b7280;
  margin: 0 0 16px 0;
}

.card-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
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

.meta-value.expired {
  color: #dc2626;
}

.card-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
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
  min-width: 80px;
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

.card-action-btn.warning {
  background: #fef3c7;
  color: #d97706;
  border: 1px solid #f59e0b;
}

.card-action-btn.warning:hover {
  background: #fed7aa;
}

/* Paginação */
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
}

.pagination-info {
  color: #6b7280;
  font-size: 0.875rem;
}

.pagination-controls {
  display: flex;
  gap: 8px;
}

.pagination-btn,
.pagination-number {
  background: white;
  border: 1px solid #d1d5db;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.875rem;
}

.pagination-btn:hover:not(:disabled),
.pagination-number:hover {
  background: #f3f4f6;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-number.active {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

/* Loading - REMOVIDO */
/*
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
*/

/* Responsividade */
@media (max-width: 768px) {
  .welcome-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .controls-section {
    flex-direction: column;
    align-items: stretch;
  }

  .search-container {
    min-width: auto;
  }

  .filter-controls {
    flex-wrap: wrap;
  }

  .filter-select {
    min-width: 120px;
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

  .pagination {
    flex-direction: column;
    gap: 16px;
  }

  .table-container {
    overflow-x: auto;
  }
}

@media (max-width: 480px) {
  .data-table th,
  .data-table td {
    padding: 12px 8px;
  }

  .actions-cell {
    flex-direction: column;
    gap: 4px;
  }

  .card-actions {
    flex-direction: column;
  }

  .main-content {
    padding: 16px;
  }
}
</style>
