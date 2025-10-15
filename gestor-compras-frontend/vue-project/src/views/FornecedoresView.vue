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
            <h1 class="welcome-title">Gestão de Fornecedores 📋</h1>
            <p class="welcome-subtitle">
              Gerencie cadastro e avaliação de fornecedores de produtos e serviços
            </p>
          </div>
          <div class="action-buttons">
            <button class="action-button secondary" @click="gerarRelatorioFornecedores" :disabled="gerandoRelatorio">
              <svg class="action-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="white" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
              </svg>
              <span v-if="gerandoRelatorio" class="loading-content">
                <span class="loading-spinner"></span>
                Gerando...
              </span>
              <span v-else>Gerar Relatório</span>
            </button>
            <button class="action-button" @click="abrirFormularioNovo">
              <svg class="action-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="white" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" />
              </svg>
              Novo Fornecedor
            </button>
          </div>
        </div>
      </div>

      <!-- Indicadores (Cards de Métricas Rápidas) -->
      <div class="metrics-section">
        <div class="metrics-grid">
          <!-- Total de Fornecedores -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon total">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white"
                    d="M16 4c0-1.11.89-2 2-2s2 .89 2 2-.89 2-2 2-2-.89-2-2zm4 18v-6h2.5l-2.54-7.63A2 2 0 0 0 18.04 7H16c-.8 0-1.54.37-2.01.99L12 10l2.01-2.01C14.54 7.37 15.2 7 16 7h2.04c1.23 0 2.18 1.24 1.92 2.63l2.54 7.63H20v6h-4z" />
                </svg>
              </div>
              <span class="metric-label">Total de Fornecedores</span>
            </div>
            <div class="metric-value">{{ totalFornecedores }}</div>
            <div class="metric-growth positive">{{ novosFornecedoresMes }} novos este mês</div>
          </div>

          <!-- Fornecedores Ativos -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon active">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white"
                    d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
                </svg>
              </div>
              <span class="metric-label">Fornecedores Ativos</span>
            </div>
            <div class="metric-value">{{ fornecedoresAtivos }}</div>
            <div class="metric-growth neutral">{{ percentualAtivos }}% do total</div>
          </div>

          <!-- Fornecedores de Produto -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon rating">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white"
                    d="M20,8H4V6H20M20,18H4V12H20M20,4H4C2.89,4 2,4.89 2,6V18A2,2 0 0,0 4,20H20A2,2 0 0,0 22,18V6C22,5.11 21.1,4 20,4Z" />
                </svg>
              </div>
              <span class="metric-label">Fornecedores de Produto</span>
            </div>
            <div class="metric-value">{{ fornecedoresProduto.length }}</div>
            <div class="metric-growth positive">Produtos</div>
          </div>

          <!-- Fornecedores de Serviço -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon value">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M19,6.41L17.59,5L7,15.59V9H5V19H15V17H8.41L19,6.41Z" />
                </svg>
              </div>
              <span class="metric-label">Fornecedores de Serviço</span>
            </div>
            <div class="metric-value">{{ fornecedoresServico.length }}</div>
            <div class="metric-growth positive">Serviços</div>
          </div>
        </div>
      </div>

      <!-- Filtros de Pesquisa -->
      <div class="search-section">
        <div class="search-container">
          <div class="search-input-container">
            <svg class="search-icon" viewBox="0 0 24 24" width="20" height="20">
              <path fill="currentColor"
                d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z" />
            </svg>
            <input type="text" v-model="searchQuery" placeholder="Pesquisar por nome, CNPJ, e-mail..."
              class="search-input" @input="filtrarFornecedores" />
          </div>
          <div class="search-actions">
            <select v-model="filtroTipo" @change="filtrarFornecedores" class="filter-select">
              <option value="">Todos os tipos</option>
              <option value="produto">Produto</option>
              <option value="servico">Serviço</option>
            </select>
            <select v-model="filtroStatus" @change="filtrarFornecedores" class="filter-select">
              <option value="">Todos os status</option>
              <option value="ativo">Ativo</option>
              <option value="inativo">Inativo</option>
              <option value="suspenso">Suspenso</option>
            </select>
            <button class="filter-button" @click="limparFiltros">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor"
                  d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
              </svg>
              Limpar
            </button>
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="isLoading" class="loading-section">
        <div class="loading-spinner-large"></div>
        <p>Carregando fornecedores...</p>
      </div>

      <!-- Lista de Fornecedores (Tabela) -->
      <div v-else class="table-section">
        <div class="table-container">
          <table class="suppliers-table" v-if="fornecedoresFiltrados.length > 0">
            <thead>
              <tr>
                <th>Fornecedor</th>
                <th>Tipo</th>
                <th>Documento</th>
                <th>Contato</th>
                <th>Cidade</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="fornecedor in fornecedoresFiltrados" :key="fornecedor.id" class="table-row">
                <td class="supplier-cell">
                  <div class="supplier-info">
                    <span class="supplier-name">{{ fornecedor.razaoSocial }}</span>
                    <span class="supplier-code" v-if="fornecedor.inscricaoEstadual">
                      IE: {{ fornecedor.inscricaoEstadual }}
                    </span>
                  </div>
                </td>
                <td>
                  <span class="type-tag" :class="getTipoClass(fornecedor.tipo)">
                    {{ getTipoLabel(fornecedor.tipo) }}
                  </span>
                </td>
                <td class="document-cell">
                  <span class="document">{{ formatarDocumento(fornecedor) }}</span>
                </td>
                <td class="contact-cell">
                  <div class="contact-info">
                    <span class="contact-email" v-if="fornecedor.contato?.email">
                      {{ fornecedor.contato.email }}
                    </span>
                    <span class="contact-phone" v-if="fornecedor.contato?.telefoneFixo">
                      📞 {{ formatarTelefone(fornecedor.contato.telefoneFixo) }}
                    </span>
                    <span class="contact-phone" v-if="fornecedor.contato?.celular">
                      📱 {{ formatarTelefone(fornecedor.contato.celular) }}
                    </span>
                  </div>
                </td>
                <td class="address-cell">
                  <span v-if="fornecedor.endereco?.cidade">
                    {{ fornecedor.endereco.cidade }}/{{ fornecedor.endereco.estado }}
                  </span>
                  <span v-else class="text-muted">Não informado</span>
                </td>
                <td>
                  <span class="status-badge" :class="getStatusClass(fornecedor.status || 'ativo')">
                    {{ getStatusLabel(fornecedor.status || 'ativo') }}
                  </span>
                </td>
                <td class="actions-cell">
                  <div class="action-buttons">
                    <button class="action-btn edit" @click="editarFornecedor(fornecedor)" title="Editar">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                      </svg>
                    </button>
                    <button class="action-btn view" @click="visualizarFornecedor(fornecedor)" title="Visualizar">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" />
                      </svg>
                    </button>
                    <button class="action-btn delete" @click="confirmarExclusao(fornecedor)" title="Excluir">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
                      </svg>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>

          <!-- Estado vazio -->
          <div v-else class="empty-state">
            <svg class="empty-icon" viewBox="0 0 24 24" width="64" height="64">
              <path fill="currentColor"
                d="M12 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S2 17.523 2 12 6.477 2 12 2zm0 1.5a8.5 8.5 0 1 0 0 17 8.5 8.5 0 0 0 0-17zM12 7a5 5 0 1 1 0 10 5 5 0 0 1 0-10zm0 1.5a3.5 3.5 0 1 0 0 7 3.5 3.5 0 0 0 0-7z" />
            </svg>
            <h3>Nenhum fornecedor encontrado</h3>
            <p>Não há fornecedores que correspondam aos filtros aplicados.</p>
            <button class="btn-primary" @click="abrirFormularioNovo">
              Cadastrar Primeiro Fornecedor
            </button>
          </div>
        </div>
      </div>

      <!-- Modal de Fornecedor -->
      <FornecedorForm
        :isVisible="showFornecedorForm"
        :fornecedor="fornecedorEditando"
        @close="fecharFormulario"
        @save="salvarFornecedor"
      />

      <!-- Modal de Confirmação de Exclusão -->
      <ConfirmModal
        :isVisible="showConfirmModal"
        title="Confirmar Exclusão"
        :message="`Tem certeza que deseja excluir o fornecedor '${fornecedorParaExcluir?.razaoSocial}'?`"
        confirmText="Excluir"
        confirmClass="btn-danger"
        @confirm="excluirFornecedor"
        @cancel="showConfirmModal = false"
      />

      <!-- Modal de Perfil do Fornecedor -->
      <div v-if="showPerfilModal" class="modal-overlay" @click="fecharPerfil">
        <div class="perfil-modal" @click.stop>
          <div class="perfil-header">
            <h2>{{ fornecedorSelecionado?.razaoSocial }}</h2>
            <button @click="fecharPerfil" class="close-button">&times;</button>
          </div>
          <div class="perfil-body">
            <div class="perfil-tabs">
              <button
                v-for="tab in perfilTabs"
                :key="tab.id"
                :class="['tab-button', { active: perfilTabAtiva === tab.id }]"
                @click="perfilTabAtiva = tab.id"
              >
                {{ tab.label }}
              </button>
            </div>

            <div class="perfil-content">
              <!-- Aba Informações -->
              <div v-if="perfilTabAtiva === 'info'" class="perfil-section">
                <div class="info-grid">
                  <div class="info-group">
                    <h4>Dados Fiscais</h4>
                    <p><strong>CNPJ:</strong> {{ formatarCNPJ(fornecedorSelecionado?.cnpj) }}</p>
                    <p v-if="fornecedorSelecionado?.inscricaoEstadual">
                      <strong>IE:</strong> {{ fornecedorSelecionado.inscricaoEstadual }}
                    </p>
                    <p v-if="fornecedorSelecionado?.inscricaoMunicipal">
                      <strong>IM:</strong> {{ fornecedorSelecionado.inscricaoMunicipal }}
                    </p>
                  </div>

                  <div class="info-group">
                    <h4>Contato</h4>
                    <p><strong>E-mail:</strong> {{ fornecedorSelecionado?.contato?.email }}</p>
                    <p v-if="fornecedorSelecionado?.contato?.telefoneFixo">
                      <strong>Telefone Fixo:</strong> {{ formatarTelefone(fornecedorSelecionado.contato.telefoneFixo) }}
                    </p>
                    <p v-if="fornecedorSelecionado?.contato?.celular">
                      <strong>Celular:</strong> {{ formatarTelefone(fornecedorSelecionado.contato.celular) }}
                    </p>
                  </div>

                  <div class="info-group">
                    <h4>Endereço</h4>
                    <p v-if="fornecedorSelecionado?.endereco">
                      {{ fornecedorSelecionado.endereco.rua }}, {{ fornecedorSelecionado.endereco.numero }}
                      <span v-if="fornecedorSelecionado.endereco.complemento">
                        - {{ fornecedorSelecionado.endereco.complemento }}
                      </span><br>
                      {{ fornecedorSelecionado.endereco.bairro }}<br>
                      {{ fornecedorSelecionado.endereco.cidade }}/{{ fornecedorSelecionado.endereco.estado }}<br>
                      CEP: {{ formatarCEP(fornecedorSelecionado.endereco.cep) }}
                    </p>
                  </div>
                </div>
              </div>

              <!-- Aba Histórico -->
              <div v-if="perfilTabAtiva === 'historico'" class="perfil-section">
                <h4>Histórico de Cotações</h4>
                <div class="historico-stats">
                  <div class="stat-card">
                    <span class="stat-value">{{ historicoCompras.total }}</span>
                    <span class="stat-label">Total de Cotações</span>
                  </div>
                  <div class="stat-card">
                    <span class="stat-value">R$ {{ historicoCompras.valor?.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) }}</span>
                    <span class="stat-label">Valor Total Cotado</span>
                  </div>
                  <div class="stat-card">
                    <span class="stat-value">{{ historicoCompras.ultimoPedido }}</span>
                    <span class="stat-label">Última Cotação</span>
                  </div>
                </div>

                <div v-if="carregandoHistorico" class="loading-message">
                  <p>Carregando histórico...</p>
                </div>

                <div v-else-if="historicoCompras.cotacoes.length === 0" class="empty-message">
                  <p>Nenhuma cotação encontrada para este fornecedor.</p>
                </div>

                <div v-else class="historico-lista">
                  <div v-for="cotacao in historicoCompras.cotacoes" :key="cotacao.id" class="cotacao-card">
                    <div class="cotacao-header">
                      <div class="cotacao-info">
                        <h5>Cotação #{{ cotacao.numero }}</h5>
                        <span class="cotacao-data">{{ cotacao.data }}</span>
                      </div>
                      <div class="cotacao-valor">
                        <span class="valor-label">Valor Cotado</span>
                        <span class="valor-amount">R$ {{ cotacao.valor?.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) }}</span>
                      </div>
                      <span :class="['status-badge', `status-${cotacao.status.toLowerCase()}`]">
                        {{ cotacao.statusTexto }}
                      </span>
                    </div>

                    <div class="cotacao-details">
                      <div class="detail-row">
                        <div class="detail-item">
                          <span class="detail-label">📦 Item do Pedido:</span>
                          <span class="detail-value">{{ cotacao.itemNome }}</span>
                        </div>
                        <div class="detail-item">
                          <span class="detail-label">📊 Quantidade:</span>
                          <span class="detail-value">{{ cotacao.itemQuantidade }} unidade(s)</span>
                        </div>
                      </div>

                      <div class="detail-row" v-if="cotacao.itemDescricao">
                        <div class="detail-item full-width">
                          <span class="detail-label">📝 Descrição do Item:</span>
                          <span class="detail-value">{{ cotacao.itemDescricao }}</span>
                        </div>
                      </div>

                      <div class="detail-row">
                        <div class="detail-item">
                          <span class="detail-label">📅 Prazo de Entrega:</span>
                          <span class="detail-value">{{ cotacao.prazoEntrega }}</span>
                        </div>
                        <div class="detail-item" v-if="cotacao.pedidoId">
                          <span class="detail-label">🔖 Pedido Relacionado:</span>
                          <span class="detail-value">#{{ cotacao.pedidoId }} - {{ cotacao.pedidoStatus }}</span>
                        </div>
                      </div>

                      <div class="detail-row" v-if="cotacao.itemObservacao">
                        <div class="detail-item full-width">
                          <span class="detail-label">💬 Observações do Item:</span>
                          <span class="detail-value">{{ cotacao.itemObservacao }}</span>
                        </div>
                      </div>

                      <div class="detail-row" v-if="cotacao.pedidoObservacao">
                        <div class="detail-item full-width">
                          <span class="detail-label">💬 Observações do Pedido:</span>
                          <span class="detail-value">{{ cotacao.pedidoObservacao }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import DashboardHeader from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'
import FornecedorForm from '@/components/FornecedorForm.vue'
import ConfirmModal from '@/components/ConfirmModal.vue'
import fornecedorService from '@/services/fornecedorService.js'
import relatorioService from '@/services/relatorioService.js'
import cotacaoService from '@/services/cotacaoService.js'
import itemPedidoService from '@/services/itemPedidoService.js'
import pedidoService from '@/services/pedidoService.js'

// Estados reativo
const isLoading = ref(true)
const showFornecedorForm = ref(false)
const showConfirmModal = ref(false)
const showPerfilModal = ref(false)
const fornecedorEditando = ref(null)
const fornecedorParaExcluir = ref(null)
const fornecedorSelecionado = ref(null)
const gerandoRelatorio = ref(false)

// Dados
const fornecedoresProduto = ref([])
const fornecedoresServico = ref([])

// Filtros
const searchQuery = ref('')
const filtroTipo = ref('')
const filtroStatus = ref('')

// Perfil modal
const perfilTabAtiva = ref('info')
const perfilTabs = ref([
  { id: 'info', label: 'Informações' },
  { id: 'historico', label: 'Histórico' }
])

// Dados do histórico (carregados dinamicamente)
const historicoCompras = ref({
  total: 0,
  valor: 0,
  ultimoPedido: '-',
  cotacoes: []
})

const carregandoHistorico = ref(false)

// Computeds
const todosFornecedores = computed(() => {
  const produtoComTipo = fornecedoresProduto.value.map(f => ({ ...f, tipo: 'produto' }))
  const servicoComTipo = fornecedoresServico.value.map(f => ({ ...f, tipo: 'servico' }))
  return [...produtoComTipo, ...servicoComTipo]
})

const fornecedoresFiltrados = computed(() => {
  let resultado = todosFornecedores.value

  // Filtro por texto
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase().trim()
    resultado = resultado.filter(f =>
      f.razaoSocial?.toLowerCase().includes(query) ||
      f.cnpj?.includes(query) ||
      f.contato?.email?.toLowerCase().includes(query) ||
      f.endereco?.cidade?.toLowerCase().includes(query)
    )
  }

  // Filtro por tipo
  if (filtroTipo.value) {
    resultado = resultado.filter(f => f.tipo === filtroTipo.value)
  }

  // Filtro por status
  if (filtroStatus.value) {
    resultado = resultado.filter(f => (f.status || 'ativo') === filtroStatus.value)
  }

  return resultado
})

const totalFornecedores = computed(() => todosFornecedores.value.length)
const fornecedoresAtivos = computed(() =>
  todosFornecedores.value.filter(f => (f.status || 'ativo') === 'ativo').length
)
const percentualAtivos = computed(() =>
  totalFornecedores.value > 0
    ? Math.round((fornecedoresAtivos.value / totalFornecedores.value) * 100)
    : 0
)
const novosFornecedoresMes = computed(() => {
  return Math.floor(totalFornecedores.value * 0.15)
})

// Métodos de formatação
const formatarDocumento = (fornecedor) => {
  if (!fornecedor.cnpj) return 'Não informado'
  return formatarCNPJ(fornecedor.cnpj)
}

const formatarCNPJ = (cnpj) => {
  if (!cnpj) return ''
  const numbers = cnpj.replace(/\D/g, '')
  return numbers.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, '$1.$2.$3/$4-$5')
}

const formatarTelefone = (telefone) => {
  if (!telefone) return ''
  const numbers = telefone.replace(/\D/g, '')
  if (numbers.length === 11) {
    return numbers.replace(/^(\d{2})(\d{5})(\d{4})$/, '($1) $2-$3')
  } else if (numbers.length === 10) {
    return numbers.replace(/^(\d{2})(\d{4})(\d{4})$/, '($1) $2-$3')
  }
  return telefone
}

const formatarCEP = (cep) => {
  if (!cep) return ''
  const numbers = cep.replace(/\D/g, '')
  return numbers.replace(/^(\d{5})(\d{3})$/, '$1-$2')
}

// Métodos de classificação
const getTipoClass = (tipo) => {
  const classes = {
    produto: 'produto',
    servico: 'servico'
  }
  return classes[tipo] || 'produto'
}

const getTipoLabel = (tipo) => {
  const labels = {
    produto: 'Produto',
    servico: 'Serviço'
  }
  return labels[tipo] || 'Produto'
}

const getStatusClass = (status) => {
  const classes = {
    ativo: 'active',
    inativo: 'inactive',
    suspenso: 'suspended'
  }
  return classes[status] || 'active'
}

const getStatusLabel = (status) => {
  const labels = {
    ativo: 'Ativo',
    inativo: 'Inativo',
    suspenso: 'Suspenso'
  }
  return labels[status] || 'Ativo'
}

// Métodos de ação
const carregarFornecedores = async () => {
  isLoading.value = true
  try {
    const [produtoResponse, servicoResponse] = await Promise.all([
      fornecedorService.listarFornecedoresProduto(),
      fornecedorService.listarFornecedoresServico()
    ])

    fornecedoresProduto.value = produtoResponse || []
    fornecedoresServico.value = servicoResponse || []
  } catch (error) {
    console.error('Erro ao carregar fornecedores:', error)
  } finally {
    isLoading.value = false
  }
}

const abrirFormularioNovo = () => {
  fornecedorEditando.value = null
  showFornecedorForm.value = true
}

const editarFornecedor = (fornecedor) => {
  fornecedorEditando.value = fornecedor
  showFornecedorForm.value = true
}

const fecharFormulario = () => {
  showFornecedorForm.value = false
  fornecedorEditando.value = null
}

const salvarFornecedor = async (dadosFornecedor) => {
  try {
    console.log('🔄 Salvando fornecedor:', dadosFornecedor)
    console.log('🔍 FornecedoresView - Dados recebidos do formulário:', JSON.stringify(dadosFornecedor, null, 2))
    console.log('🔍 FornecedoresView - contato tem numero?', 'numero' in (dadosFornecedor.contato || {}))
    console.log('🔍 FornecedoresView - endereco tem numero?', 'numero' in (dadosFornecedor.endereco || {}))
    console.log('🔍 FornecedoresView - endereco.numero =', dadosFornecedor.endereco?.numero)

    if (fornecedorEditando.value) {
      // Atualizar fornecedor existente
      if (dadosFornecedor.tipo === 'produto') {
        await fornecedorService.atualizarFornecedorProduto(fornecedorEditando.value.id, dadosFornecedor)
      } else {
        await fornecedorService.atualizarFornecedorServico(fornecedorEditando.value.id, dadosFornecedor)
      }
      console.log('✅ Fornecedor atualizado com sucesso!')
    } else {
      // Criar novo fornecedor
      let resultado
      if (dadosFornecedor.tipo === 'produto') {
        resultado = await fornecedorService.criarFornecedorProduto(dadosFornecedor)
      } else {
        resultado = await fornecedorService.criarFornecedorServico(dadosFornecedor)
      }
      console.log('✅ Fornecedor criado com sucesso!', resultado)
    }

    await carregarFornecedores()
    fecharFormulario()

    // Mostrar mensagem de sucesso
    alert(fornecedorEditando.value ? 'Fornecedor atualizado com sucesso!' : 'Fornecedor cadastrado com sucesso!')

  } catch (error) {
    console.error('❌ Erro ao salvar fornecedor:', error)

    // Mostrar erro mais específico para o usuário
    let mensagemErro = 'Erro ao salvar fornecedor.'

    if (error.message.includes('CORS')) {
      mensagemErro = 'Erro de configuração: O backend não está configurado para aceitar requisições do frontend.'
    } else if (error.message.includes('ECONNREFUSED') || error.message.includes('Network Error')) {
      mensagemErro = 'Erro de conexão: Verifique se o backend está rodando na porta correta (8080).'
    } else if (error.message.includes('401')) {
      mensagemErro = 'Erro de autenticação: Faça login novamente.'
    } else if (error.message.includes('400')) {
      mensagemErro = 'Dados inválidos: Verifique se todos os campos obrigatórios estão preenchidos.'
    } else if (error.message.includes('500')) {
      mensagemErro = 'Erro interno do servidor. Tente novamente em alguns instantes.'
    } else if (error.message) {
      mensagemErro = error.message
    }

    alert(mensagemErro)
  }
}

const confirmarExclusao = (fornecedor) => {
  fornecedorParaExcluir.value = fornecedor
  showConfirmModal.value = true
}

const excluirFornecedor = async () => {
  if (!fornecedorParaExcluir.value) return

  try {
    const fornecedor = fornecedorParaExcluir.value

    if (fornecedor.tipo === 'produto') {
      await fornecedorService.removerFornecedorProduto(fornecedor.id)
    } else {
      await fornecedorService.removerFornecedorServico(fornecedor.id)
    }

    await carregarFornecedores()
  } catch (error) {
    console.error('Erro ao excluir fornecedor:', error)
  } finally {
    showConfirmModal.value = false
    fornecedorParaExcluir.value = null
  }
}

const visualizarFornecedor = async (fornecedor) => {
  fornecedorSelecionado.value = fornecedor
  perfilTabAtiva.value = 'info'
  showPerfilModal.value = true

  // Carregar histórico de cotações do fornecedor
  await carregarHistoricoFornecedor(fornecedor.id)
}

const fecharPerfil = () => {
  showPerfilModal.value = false
  fornecedorSelecionado.value = null
  // Limpar histórico ao fechar
  historicoCompras.value = {
    total: 0,
    valor: 0,
    ultimoPedido: '-',
    cotacoes: []
  }
}

const carregarHistoricoFornecedor = async (fornecedorId) => {
  try {
    carregandoHistorico.value = true

    // Buscar todas as cotações e filtrar pelo fornecedor
    const todasCotacoes = await cotacaoService.listar()
    const cotacoes = todasCotacoes.filter(cot => cot.fornecedorId === fornecedorId)

    if (cotacoes && cotacoes.length > 0) {
      // Processar dados das cotações
      const totalCotacoes = cotacoes.length
      const valorTotal = cotacoes.reduce((acc, cot) => acc + (parseFloat(cot.preco) || 0), 0)

      // Ordenar por data (mais recente primeiro)
      const cotacoesOrdenadas = [...cotacoes].sort((a, b) =>
        new Date(b.dataCotacao) - new Date(a.dataCotacao)
      )

      const ultimaCotacao = cotacoesOrdenadas[0]
      const dataUltima = ultimaCotacao?.dataCotacao
        ? new Date(ultimaCotacao.dataCotacao).toLocaleDateString('pt-BR')
        : '-'

      // Buscar detalhes dos itens dos pedidos para cada cotação
      const cotacoesComDetalhes = await Promise.all(
        cotacoesOrdenadas.map(async (cot) => {
          let itemPedido = null
          let pedido = null

          try {
            // Buscar o item do pedido relacionado
            if (cot.itemPedidoId) {
              itemPedido = await itemPedidoService.buscarPorId(cot.itemPedidoId)

              // Se tiver o item, buscar o pedido completo
              if (itemPedido?.solicitacaoDePedidoId) {
                pedido = await pedidoService.buscarPorId(itemPedido.solicitacaoDePedidoId)
              }
            }
          } catch (err) {
            console.warn('Erro ao buscar detalhes do item:', err)
          }

          return {
            id: cot.id,
            numero: cot.id?.toString().padStart(4, '0') || '-',
            data: cot.dataCotacao
              ? new Date(cot.dataCotacao).toLocaleDateString('pt-BR')
              : '-',
            valor: parseFloat(cot.preco) || 0,
            status: 'cotacao',
            statusTexto: 'Cotação Enviada',
            itemPedidoId: cot.itemPedidoId,
            prazoEntrega: cot.prazoEntrega
              ? new Date(cot.prazoEntrega).toLocaleDateString('pt-BR')
              : '-',
            // Detalhes do item
            itemNome: itemPedido?.nome || 'Item não especificado',
            itemQuantidade: itemPedido?.quantidade || 0,
            itemDescricao: itemPedido?.descricao || '',
            itemObservacao: itemPedido?.observacao || '',
            // Detalhes do pedido
            pedidoId: pedido?.id || null,
            pedidoStatus: pedido?.status || null,
            pedidoObservacao: pedido?.observacao || '',
            pedidoData: pedido?.dataCriacao
              ? new Date(pedido.dataCriacao).toLocaleDateString('pt-BR')
              : '-'
          }
        })
      )

      historicoCompras.value = {
        total: totalCotacoes,
        valor: valorTotal,
        ultimoPedido: dataUltima,
        cotacoes: cotacoesComDetalhes
      }
    } else {
      // Nenhuma cotação encontrada
      historicoCompras.value = {
        total: 0,
        valor: 0,
        ultimoPedido: '-',
        cotacoes: []
      }
    }
  } catch (error) {
    console.error('Erro ao carregar histórico do fornecedor:', error)
    // Em caso de erro, mostrar dados vazios
    historicoCompras.value = {
      total: 0,
      valor: 0,
      ultimoPedido: '-',
      cotacoes: []
    }
  } finally {
    carregandoHistorico.value = false
  }
}

const filtrarFornecedores = () => {
  // A filtragem é feita automaticamente via computed
}

const limparFiltros = () => {
  searchQuery.value = ''
  filtroTipo.value = ''
  filtroStatus.value = ''
}

// Função para gerar relatório de fornecedores
const gerarRelatorioFornecedores = async () => {
  if (gerandoRelatorio.value) return

  try {
    gerandoRelatorio.value = true
    await relatorioService.gerarRelatorioFornecedores()
  } catch (error) {
    console.error('Erro ao gerar relatório:', error)
    alert('Erro ao gerar relatório. Tente novamente.')
  } finally {
    gerandoRelatorio.value = false
  }
}

// Lifecycle
onMounted(() => {
  carregarFornecedores()
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

/* Seção de busca */
.search-section {
  margin-bottom: 32px;
}

.search-container {
  display: flex;
  gap: 16px;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}

.search-input-container {
  flex: 1;
  position: relative;
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

.search-actions {
  display: flex;
  gap: 12px;
}

.filter-select {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  min-width: 140px;
}

.filter-button {
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

.filter-button:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

/* Loading */
.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px;
  color: #6b7280;
}

.loading-spinner-large {
  width: 48px;
  height: 48px;
  border: 4px solid #e5e7eb;
  border-top: 4px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Tabela */
.table-section {
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.table-container {
  overflow-x: auto;
}

.suppliers-table {
  width: 100%;
  border-collapse: collapse;
}

.suppliers-table th {
  background: #f9fafb;
  padding: 16px;
  text-align: left;
  font-weight: 600;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
  font-size: 0.875rem;
}

.suppliers-table td {
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
}

.table-row:hover {
  background: #f9fafb;
}

.supplier-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.supplier-name {
  font-weight: 600;
  color: #111827;
}

.supplier-code {
  font-size: 0.75rem;
  color: #6b7280;
}

.type-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.type-tag.produto {
  background: #dbeafe;
  color: #1d4ed8;
}

.type-tag.servico {
  background: #d1fae5;
  color: #047857;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.contact-email {
  font-size: 0.875rem;
  color: #111827;
}

.contact-phone {
  font-size: 0.75rem;
  color: #6b7280;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.status-badge.active {
  background: #d1fae5;
  color: #047857;
}

.status-badge.inactive {
  background: #fee2e2;
  color: #dc2626;
}

.status-badge.suspended {
  background: #fef3c7;
  color: #d97706;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.edit {
  background: #dbeafe;
  color: #1d4ed8;
}

.action-btn.edit:hover {
  background: #bfdbfe;
}

.action-btn.view {
  background: #e0e7ff;
  color: #4338ca;
}

.action-btn.view:hover {
  background: #c7d2fe;
}

.action-btn.delete {
  background: #fee2e2;
  color: #dc2626;
}

.action-btn.delete:hover {
  background: #fecaca;
}

/* Estado vazio */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 32px;
  text-align: center;
}

.empty-icon {
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 8px 0;
}

.empty-state p {
  color: #6b7280;
  margin: 0 0 24px 0;
}

.btn-primary {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

/* Modal de Perfil */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.perfil-modal {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.perfil-header {
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8fafc;
}

.perfil-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #111827;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #9ca3af;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
}

.close-button:hover {
  background: #e5e7eb;
}

.perfil-body {
  max-height: calc(90vh - 120px);
  overflow-y: auto;
}

.perfil-tabs {
  display: flex;
  border-bottom: 1px solid #e5e7eb;
  background: #f8fafc;
}

.tab-button {
  padding: 16px 24px;
  border: none;
  background: none;
  color: #6b7280;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 3px solid transparent;
}

.tab-button:hover {
  color: #374151;
  background: #f3f4f6;
}

.tab-button.active {
  color: #3b82f6;
  border-bottom-color: #3b82f6;
}

.perfil-content {
  padding: 24px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}

.info-group h4 {
  margin: 0 0 12px 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: #111827;
}

.info-group p {
  margin: 0 0 8px 0;
  color: #374151;
  line-height: 1.5;
}

.historico-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #f8fafc;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 1.5rem;
  font-weight: 700;
  color: #111827;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 0.875rem;
  color: #6b7280;
}

.empty-message {
  text-align: center;
  color: #6b7280;
  font-style: italic;
  margin: 32px 0;
}

/* Tabela de Histórico */
.historico-tabela {
  margin-top: 24px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
}

.historico-tabela table {
  width: 100%;
  border-collapse: collapse;
}

.historico-tabela thead {
  background: #f8fafc;
}

.historico-tabela th {
  padding: 12px 16px;
  text-align: left;
  font-weight: 600;
  color: #374151;
  font-size: 0.875rem;
  border-bottom: 1px solid #e5e7eb;
}

.historico-tabela td {
  padding: 12px 16px;
  color: #6b7280;
  font-size: 0.875rem;
  border-bottom: 1px solid #f3f4f6;
}

.historico-tabela tbody tr:hover {
  background: #f9fafb;
}

.historico-tabela tbody tr:last-child td {
  border-bottom: none;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
  white-space: nowrap;
}

.status-concluido {
  background: #d1fae5;
  color: #065f46;
}

.status-cancelado {
  background: #fee2e2;
  color: #991b1b;
}

.status-pendente {
  background: #fef3c7;
  color: #92400e;
}

.status-cotacao {
  background: #e0e7ff;
  color: #4338ca;
}

.loading-message {
  text-align: center;
  padding: 40px;
  color: #6b7280;
}

.loading-message p {
  font-style: italic;
}

/* Lista de Cotações Detalhada */
.historico-lista {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 24px;
}

.cotacao-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.2s;
}

.cotacao-card:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  border-color: #3b82f6;
}

.cotacao-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: #f8fafc;
  border-bottom: 1px solid #e5e7eb;
  gap: 16px;
}

.cotacao-info h5 {
  margin: 0 0 4px 0;
  font-size: 1rem;
  font-weight: 600;
  color: #111827;
}

.cotacao-data {
  font-size: 0.875rem;
  color: #6b7280;
}

.cotacao-valor {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.valor-label {
  font-size: 0.75rem;
  color: #6b7280;
  margin-bottom: 2px;
}

.valor-amount {
  font-size: 1.125rem;
  font-weight: 700;
  color: #059669;
}

.cotacao-details {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item.full-width {
  grid-column: 1 / -1;
}

.detail-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.detail-value {
  font-size: 0.875rem;
  color: #374151;
  line-height: 1.5;
}

/* Responsividade */
@media (max-width: 1024px) {
  .welcome-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .search-container {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .search-actions {
    justify-content: stretch;
  }

  .filter-select {
    flex: 1;
  }
}

@media (max-width: 768px) {
  .metrics-grid {
    grid-template-columns: 1fr;
  }

  .perfil-modal {
    width: 100%;
    height: 100vh;
    max-height: 100vh;
    border-radius: 0;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .historico-stats {
    grid-template-columns: 1fr;
  }

  .historico-tabela {
    overflow-x: auto;
  }

  .historico-tabela table {
    min-width: 500px;
  }

  .cotacao-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .cotacao-valor {
    align-items: flex-start;
  }

  .detail-row {
    grid-template-columns: 1fr;
  }

  .tab-button {
    padding: 12px 16px;
    font-size: 0.875rem;
  }
}
</style>
