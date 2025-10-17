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
            <h1 class="welcome-title">Pedidos de Compra</h1>
            <p class="welcome-subtitle">
              Gerencie solicitações de pedidos e acompanhe o fluxo de aprovação
            </p>
          </div>
          <div class="action-buttons">
            <button class="action-button" @click="abrirFormularioNovo">
              <svg class="action-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="white" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" />
              </svg>
              Novo Pedido
            </button>
          </div>
        </div>
      </div>

      <!-- Indicadores (Cards de Métricas Rápidas) -->
      <div class="metrics-section">
        <div class="metrics-grid">
          <!-- Total de Pedidos -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon total">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm-1 16H9V7h9v14z"/>
                </svg>
              </div>
              <span class="metric-label">Total de Pedidos</span>
            </div>
            <div class="metric-value">{{ totalPedidos }}</div>
            <div class="metric-growth positive">{{ novosPedidosMes }} novos este mês</div>
          </div>

          <!-- Pedidos Pendentes -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon pending">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                </svg>
              </div>
              <span class="metric-label">Aguardando Aprovação</span>
            </div>
            <div class="metric-value">{{ pedidosPendentes }}</div>
            <div class="metric-growth neutral">{{ percentualPendentes }}% do total</div>
          </div>

          <!-- Pedidos Aprovados -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon approved">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                </svg>
              </div>
              <span class="metric-label">Pedidos Aprovados</span>
            </div>
            <div class="metric-value">{{ pedidosAprovados }}</div>
            <div class="metric-growth positive">Aprovados</div>
          </div>

          <!-- Valor Total -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon value">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/>
                </svg>
              </div>
              <span class="metric-label">Valor em Pedidos</span>
            </div>
            <div class="metric-value">R$ {{ valorTotalFormatado }}</div>
            <div class="metric-growth positive">Este mês</div>
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
            <input type="text" v-model="searchQuery" placeholder="Pesquisar por ID..."
              class="search-input" @input="filtrarPedidos" />
          </div>
          <div class="search-actions">
            <select v-model="filtroStatus" @change="filtrarPedidos" class="form-select">
              <option value="">Todos os status</option>
              <option value="RASCUNHO">Rascunho</option>
              <option value="PENDENTE">Pendente</option>
              <option value="EM_ANALISE">Em Análise</option>
              <option value="EM_ANDAMENTO">Em Andamento</option>
              <option value="APROVADO">Aprovado</option>
              <option value="CANCELADO">Cancelado</option>
            </select>
            <select v-model="filtroPeriodo" @change="filtrarPedidos" class="form-select">
              <option value="">Todos os períodos</option>
              <option value="hoje">Hoje</option>
              <option value="semana">Esta semana</option>
              <option value="mes">Este mês</option>
              <option value="trimestre">Este trimestre</option>
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
        <p>Carregando pedidos...</p>
      </div>

      <!-- Lista de Pedidos (Tabela) -->
      <div v-else class="table-section">
        <div class="table-container">
          <table class="pedidos-table" v-if="pedidosFiltrados.length > 0">
            <thead>
              <tr>
                <th>Pedido</th>
                <th>Status</th>
                <th>Itens</th>
                <th>Data</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="pedido in pedidosFiltrados" :key="pedido.id" class="table-row">
                <td class="pedido-cell">
                  <div class="pedido-info">
                    <span class="pedido-numero">#{{ pedido.id }}</span>
                  </div>
                </td>
                <td>
                  <span class="status-badge" :class="getStatusClass(pedido.status)">
                    {{ getStatusLabel(pedido.status) }}
                  </span>
                </td>
                <td class="itens-cell">
                  <span class="itens-count">{{ getQuantidadeItens(pedido) }} item(s)</span>
                </td>
                <td class="data-cell">
                  <span class="data-criacao">{{ formatarData(pedido.dataCriacao || pedido.dataPedido) }}</span>
                </td>
                <td class="actions-cell">
                  <div class="action-buttons">
                    <button class="action-btn view" @click="visualizarPedido(pedido)" title="Visualizar">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" />
                      </svg>
                    </button>
                    <button class="action-btn edit" @click="editarPedido(pedido)" title="Editar" v-if="podeEditar(pedido)">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                      </svg>
                    </button>
                    <div class="status-actions" v-if="podeAlterarStatus(pedido)">
                      <select
                        @change="alterarStatus(pedido, $event.target.value)"
                        class="status-select"
                        :value="pedido.status"
                        title="Alterar Status"
                      >
                        <option :value="pedido.status" disabled>{{ getStatusLabel(pedido.status) }}</option>
                        <option v-for="status in getStatusDisponiveis(pedido.status)"
                                :key="status.value"
                                :value="status.value">
                          {{ status.label }}
                        </option>
                      </select>
                    </div>
                    <button class="action-btn approve" @click="aprovarPedido(pedido)" title="Aprovar" v-else-if="podeAprovar(pedido)">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                      </svg>
                    </button>
                    <button class="action-btn delete" @click="confirmarExclusao(pedido)" title="Excluir" v-if="podeExcluir(pedido)">
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
                d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm-1 16H9V7h9v14z" />
            </svg>
            <h3>Nenhum pedido encontrado</h3>
            <p>Não há pedidos que correspondam aos filtros aplicados.</p>
            <button class="btn-primary" @click="abrirFormularioNovo">
              Criar Primeiro Pedido
            </button>
          </div>
        </div>
      </div>

      <!-- Modal de Pedido -->
      <PedidoForm
        :isVisible="showPedidoForm"
        :pedido="pedidoEditando"
        @close="fecharFormulario"
        @save="salvarPedido"
        @save-draft="salvarRascunhoSemFechar"
      />

      <!-- Modal de Confirmação de Exclusão -->
      <ConfirmModal
        :isVisible="showConfirmModal"
        title="Confirmar Exclusão"
        :message="`Tem certeza que deseja excluir o pedido #${pedidoParaExcluir?.id}?`"
        confirmText="Excluir"
        confirmClass="btn-danger"
        @confirm="excluirPedido"
        @cancel="showConfirmModal = false"
      />

      <!-- Modal de Detalhes do Pedido -->
      <div v-if="showDetalhesModal" class="modal-overlay" @click="fecharDetalhes">
        <div class="detalhes-modal" @click.stop>
          <div class="detalhes-header">
            <h2>Pedido #{{ pedidoSelecionado?.id }}</h2>
            <button @click="fecharDetalhes" class="close-button">&times;</button>
          </div>
          <div class="detalhes-body">
            <div class="detalhes-tabs">
              <button
                v-for="tab in detalhesTabs"
                :key="tab.id"
                :class="['tab-button', { active: detalhesTabAtiva === tab.id }]"
                @click="detalhesTabAtiva = tab.id"
              >
                {{ tab.label }}
              </button>
            </div>

            <div class="detalhes-content">
              <!-- Aba Informações -->
              <div v-if="detalhesTabAtiva === 'info'" class="detalhes-section">
                <div class="info-grid">
                  <div class="info-group">
                    <h4>Dados do Pedido</h4>
                    <p><strong>ID:</strong> #{{ pedidoSelecionado?.id }}</p>
                    <p><strong>Status:</strong>
                      <span class="status-badge" :class="getStatusClass(pedidoSelecionado?.status)">
                        {{ getStatusLabel(pedidoSelecionado?.status) }}
                      </span>
                    </p>
                    <p><strong>Data de Criação:</strong> {{ formatarDataCompleta(pedidoSelecionado?.dataCriacao || pedidoSelecionado?.dataPedido) }}</p>
                  </div>

                  <div class="info-group full-width">
                    <h4>Descrição/Observações</h4>
                    <p>{{ pedidoSelecionado?.observacao || 'Nenhuma descrição informada' }}</p>
                  </div>
                </div>
              </div>

              <!-- Aba Itens -->
              <div v-if="detalhesTabAtiva === 'itens'" class="detalhes-section">
                <h4>Itens do Pedido</h4>

                <div v-if="carregandoItens" class="loading-message">
                  <p>Carregando itens...</p>
                </div>

                <div v-else-if="pedidoSelecionado?.itens?.length" class="itens-list-detalhada">
                  <div v-for="(item, index) in pedidoSelecionado.itens" :key="item.id || index" class="item-card-detalhado">
                    <div class="item-header-detalhado">
                      <div class="item-numero-badge">Item #{{ index + 1 }}</div>
                      <div class="item-quantidade-badge">{{ item.quantidade }}x</div>
                    </div>

                    <div class="item-content">
                      <h5 class="item-nome">{{ item.nome || item.produto || 'Item sem nome' }}</h5>

                      <div class="item-info-grid">
                        <div class="item-info" v-if="item.quantidade">
                          <span class="info-label">Quantidade:</span>
                          <span class="info-value">{{ item.quantidade }} unidade(s)</span>
                        </div>

                        <div class="item-info" v-if="item.id">
                          <span class="info-label">ID do Item:</span>
                          <span class="info-value">#{{ item.id }}</span>
                        </div>
                      </div>

                      <div class="item-descricao" v-if="item.descricao">
                        <span class="info-label">Descrição:</span>
                        <p class="info-value">{{ item.descricao }}</p>
                      </div>

                      <div class="item-observacao" v-if="item.observacao">
                        <span class="info-label">Ÿ’¬ Observações:</span>
                        <p class="info-value">{{ item.observacao }}</p>
                      </div>

                      <!-- Cotações relacionadas ao item -->
                      <div class="item-cotacoes" v-if="item.cotacoes && item.cotacoes.length > 0">
                        <span class="info-label">Ÿ’° Cotações Recebidas:</span>
                        <div class="cotacoes-mini-list">
                          <div v-for="cotacao in item.cotacoes" :key="cotacao.id" class="cotacao-mini-card">
                            <div class="cotacao-fornecedor">{{ cotacao.fornecedorNome || `Fornecedor #${cotacao.fornecedorId}` }}</div>
                            <div class="cotacao-valor">R$ {{ cotacao.preco?.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) }}</div>
                            <div class="cotacao-prazo">Entrega: {{ formatarData(cotacao.prazoEntrega) }}</div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <p v-else class="empty-message">Nenhum item cadastrado neste pedido.</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import DashboardHeader from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'
import PedidoForm from '@/components/PedidoForm.vue'
import ConfirmModal from '@/components/ConfirmModal.vue'
import pedidoService from '@/services/pedidoService.js'
import cotacaoService from '@/services/cotacaoService.js'
import fornecedorService from '@/services/fornecedorService.js'
import itemPedidoService from '@/services/itemPedidoService.js'

export default {
  name: 'PedidosView',
  components: {
    DashboardHeader,
    DashboardSidebar,
    PedidoForm,
    ConfirmModal
  },
  setup() {
    // Estados reativos
    const pedidos = ref([])
    const isLoading = ref(false)
    const searchQuery = ref('')
    const filtroStatus = ref('')
    const filtroPeriodo = ref('')

    // Como simplificamos o formulário, não precisamos mais de extração complexa

    // Modais
    const showPedidoForm = ref(false)
    const showConfirmModal = ref(false)
    const showDetalhesModal = ref(false)
    const pedidoEditando = ref(null)
    const pedidoParaExcluir = ref(null)
    const pedidoSelecionado = ref(null)

    // Detalhes modal
    const detalhesTabAtiva = ref('info')
    const detalhesTabs = ref([
      { id: 'info', label: 'Informações' },
      { id: 'itens', label: 'Itens' }
    ])

    // Estados para itens
    const carregandoItens = ref(false)

    // Computed properties para mÃ©tricas
    const totalPedidos = computed(() => pedidos.value.length)

    const novosPedidosMes = computed(() => {
      const agora = new Date()
      const inicioMes = new Date(agora.getFullYear(), agora.getMonth(), 1)
      return pedidos.value.filter(p => {
        const dataPedido = new Date(p.dataCriacao || p.dataPedido)
        return dataPedido >= inicioMes
      }).length
    })

    const pedidosPendentes = computed(() =>
      pedidos.value.filter(p => p.status === 'PENDENTE' || p.status === 'pendente').length
    )

    const pedidosAprovados = computed(() =>
      pedidos.value.filter(p => p.status === 'APROVADO' || p.status === 'aprovado').length
    )

    const percentualPendentes = computed(() => {
      if (totalPedidos.value === 0) return 0
      return Math.round((pedidosPendentes.value / totalPedidos.value) * 100)
    })

    const valorTotalFormatado = computed(() => {
      // Simulação de valor total - seria calculado baseado nos itens
      const valorSimulado = pedidos.value.length * 1500
      return valorSimulado.toLocaleString('pt-BR')
    })

    // Pedidos filtrados
    const pedidosFiltrados = computed(() => {
      let resultado = [...pedidos.value]

      // Filtro por texto
      if (searchQuery.value.trim()) {
        const query = searchQuery.value.toLowerCase()
        resultado = resultado.filter(pedido =>
          (pedido.id?.toString().includes(query))
        )
      }

      // Filtro por status
      if (filtroStatus.value) {
        resultado = resultado.filter(pedido => pedido.status === filtroStatus.value)
      }

      // Filtro por perÃ­odo
      if (filtroPeriodo.value) {
        const agora = new Date()
        let dataLimite

        switch (filtroPeriodo.value) {
          case 'hoje':
            dataLimite = new Date(agora.getFullYear(), agora.getMonth(), agora.getDate())
            break
          case 'semana':
            dataLimite = new Date(agora.getTime() - 7 * 24 * 60 * 60 * 1000)
            break
          case 'mes':
            dataLimite = new Date(agora.getFullYear(), agora.getMonth(), 1)
            break
          case 'trimestre':
            dataLimite = new Date(agora.getFullYear(), Math.floor(agora.getMonth() / 3) * 3, 1)
            break
        }

        if (dataLimite) {
          resultado = resultado.filter(pedido => {
            const dataPedido = new Date(pedido.dataCriacao || pedido.dataPedido)
            return dataPedido >= dataLimite
          })
        }
      }

      // Ordenar por data de criação (mais recentes primeiro)
      resultado.sort((a, b) => {
        const dataA = new Date(a.dataCriacao || a.dataPedido || 0)
        const dataB = new Date(b.dataCriacao || b.dataPedido || 0)
        return dataB - dataA
      })

      return resultado
    })

    // MÃ©todos de dados
    const carregarPedidos = async () => {
      try {
        isLoading.value = true
        console.log('Ÿ”„ Carregando pedidos...')

        // Buscar pedidos e itens em paralelo
        const [response, todosItens] = await Promise.all([
          pedidoService.listarTodos(),
          itemPedidoService.listarTodos()
        ])

        console.log('Itens carregados:', todosItens.length)
        console.log('Exemplo de item:', todosItens[0])

        // Processar dados do backend - estrutura simplificada
        if (response && Array.isArray(response)) {
          pedidos.value = response.map(pedido => {
            // Filtrar itens que pertencem a este pedido
            const itensDoPedido = todosItens.filter(item => {
              // O ItemPedido tem uma referÃªncia solicitacaoDePedido com id
              return item.solicitacaoDePedido?.id === pedido.id
            })

            console.log(`Pedido #${pedido.id}: ${itensDoPedido.length} itens`)

            return {
              ...pedido,
              // Adicionar os itens ao pedido
              itens: itensDoPedido,
              // Usar observacao diretamente como descrição
              descricao: pedido.observacao || 'Sem descrição',
              // Garantir que dataCriacao seja tratada corretamente
              dataPedido: pedido.dataCriacao || pedido.dataPedido
            }
          })
          console.log('DEBUG - Pedidos carregados:', pedidos.value.length)
        } else if (response && response.data && Array.isArray(response.data)) {
          pedidos.value = response.data.map(pedido => {
            const itensDoPedido = todosItens.filter(item => {
              return item.solicitacaoDePedido?.id === pedido.id
            })

            return {
              ...pedido,
              itens: itensDoPedido,
              descricao: pedido.observacao || 'Sem descrição',
              dataPedido: pedido.dataCriacao || pedido.dataPedido
            }
          })
          console.log('DEBUG - Pedidos carregados:', pedidos.value.length)
        } else {
          // Dados de exemplo se a API nÃ£o estiver funcionando
          pedidos.value = [
            {
              id: 1,
              status: 'pendente',
              descricao: 'Compra de equipamentos de informática',
              objetivo: 'Renovação do parque tecnológico',
              dataCriacao: '2024-01-15T10:30:00',
              observacao: 'Urgente para o fechamento do projeto',
              itens: [
                {
                  id: 1,
                  nome: 'Notebook Dell Latitude 5520',
                  quantidade: 5,
                  descricao: 'Para equipe de desenvolvimento',
                  observacao: 'Configuração mínima: i5, 16GB RAM, SSD 512GB'
                },
                {
                  id: 2,
                  nome: 'Monitor LG 24" Full HD',
                  quantidade: 5,
                  descricao: 'Complemento para os notebooks'
                }
              ]
            },
            {
              id: 2,
              status: 'aprovado',
              dataCriacao: '2024-01-10T14:20:00',
              observacao: 'Aprovado pela gerÃªncia',
              itens: [
                {
                  id: 1,
                  nome: 'Apostilas de treinamento',
                  quantidade: 50,
                  descricao: 'Curso de seguranÃ§a do trabalho'
                }
              ]
            },
            {
              id: 3,
              status: 'pendente',
              observacao: 'Ferramentas para manutenção - Reposição de ferramentas',
              dataCriacao: '2024-01-20T09:15:00',
              itens: [
                {
                  id: 1,
                  nome: 'Chaves de fenda variadas',
                  quantidade: 10,
                  descricao: 'Reposição do estoque'
                }
              ]
            }
          ]
        }
      } catch (error) {
        console.error('Erro ao carregar pedidos:', error)
        // Usar dados de exemplo em caso de erro
        pedidos.value = []
      } finally {
        isLoading.value = false
      }
    }

    // Métodos de formatação
    const formatarData = (data) => {
      if (!data) return 'N/A'
      try {
        return new Date(data).toLocaleDateString('pt-BR')
      } catch {
        return 'Data inválida'
      }
    }

    const formatarDataCompleta = (data) => {
      if (!data) return 'N/A'
      try {
        return new Date(data).toLocaleString('pt-BR')
      } catch {
        return 'Data inválida'
      }
    }

    const getStatusLabel = (status) => {
      const labels = {
        // Status do backend (uppercase)
        'RASCUNHO': 'Rascunho',
        'PENDENTE': 'Pendente',
        'EM_ANALISE': 'Em Análise',
        'EM_ANDAMENTO': 'Em Andamento',
        'APROVADO': 'Aprovado',
        'CANCELADO': 'Cancelado',
        // Status antigos (lowercase) - compatibilidade
        'rascunho': 'Rascunho',
        'pendente': 'Pendente',
        'em_analise': 'Em Análise',
        'em_andamento': 'Em Andamento',
        'aprovado': 'Aprovado',
        'cancelado': 'Cancelado'
      }
      return labels[status] || status || 'Indefinido'
    }

    const getStatusClass = (status) => {
      const classes = {
        // Status do backend (uppercase)
        'RASCUNHO': 'status-draft',
        'PENDENTE': 'status-pending',
        'EM_ANALISE': 'status-progress',
        'EM_ANDAMENTO': 'status-progress',
        'APROVADO': 'status-approved',
        'CANCELADO': 'status-canceled',
        // Status antigos (lowercase) - compatibilidade
        'rascunho': 'status-draft',
        'pendente': 'status-pending',
        'em_analise': 'status-progress',
        'em_andamento': 'status-progress',
        'aprovado': 'status-approved',
        'cancelado': 'status-canceled'
      }
      return classes[status] || 'status-default'
    }

    const getQuantidadeItens = (pedido) => {
      if (!pedido.itens || !Array.isArray(pedido.itens)) return 0
      return pedido.itens.reduce((total, item) => total + (item.quantidade || 1), 0)
    }

    // Métodos de ação
    const filtrarPedidos = () => {
      // A filtragem é feita automaticamente pelo computed
      // Este mÃ©todo existe para ser chamado nos eventos de input
    }

    const limparFiltros = () => {
      searchQuery.value = ''
      filtroStatus.value = ''
      filtroPeriodo.value = ''
    }

    const abrirFormularioNovo = () => {
      pedidoEditando.value = null
      showPedidoForm.value = true
    }

    const editarPedido = async (pedido) => {
      try {
        console.log('Editando pedido:', pedido)

        // Buscar o pedido completo do backend para garantir que os itens estejam atualizados
        console.log('Buscando pedido completo do backend...')
        const pedidoCompleto = await pedidoService.obterPorId(pedido.id)
        console.log('Pedido completo carregado para edição:', pedidoCompleto)
        console.log('Itens do pedido:', pedidoCompleto.itens)

        // Usar o pedido completo do backend
        pedidoEditando.value = pedidoCompleto
        showPedidoForm.value = true
      } catch (error) {
        console.error('Erro ao carregar pedido completo:', error)
        // Fallback para o pedido da lista
        pedidoEditando.value = { ...pedido }
        showPedidoForm.value = true
      }
    }

    const visualizarPedido = async (pedido) => {
      console.log('Visualizando pedido:', pedido)

      try {
        // Buscar o pedido completo do backend para garantir que os itens sejam carregados
        console.log('Buscando pedido completo do backend...')
        const pedidoCompleto = await pedidoService.obterPorId(pedido.id)
        console.log('Pedido completo carregado:', pedidoCompleto)
        console.log('Itens do pedido:', pedidoCompleto.itens)

        // Usar o pedido completo do backend
        pedidoSelecionado.value = pedidoCompleto
        detalhesTabAtiva.value = 'info'
        showDetalhesModal.value = true

        // Carregar detalhes das cotacoes para os itens
        await carregarDetalhesPedido(pedidoSelecionado.value)
      } catch (error) {
        console.error('Erro ao carregar pedido completo:', error)
        // Fallback para o pedido da lista
        pedidoSelecionado.value = { ...pedido, itens: pedido.itens ? [...pedido.itens] : [] }
        detalhesTabAtiva.value = 'info'
        showDetalhesModal.value = true
      }
    }

    const fecharFormulario = () => {
      showPedidoForm.value = false
      pedidoEditando.value = null
    }

    const fecharDetalhes = () => {
      showDetalhesModal.value = false
      pedidoSelecionado.value = null
    }

    const carregarDetalhesPedido = async (pedido) => {
      console.log('carregarDetalhesPedido - Pedido recebido:', pedido)
      console.log('Itens do pedido:', pedido?.itens)

      if (!pedido || !pedido.itens || pedido.itens.length === 0) {
        console.log('Pedido sem itens ou inválido')
        return
      }

      try {
        carregandoItens.value = true
        console.log('Carregando cotações...')

        // Buscar todas as cotaÃ§Ãµes
        const todasCotacoes = await cotacaoService.listar()
        console.log('œ… Cotações carregadas:', todasCotacoes.length)

        // Buscar todos os fornecedores para mapear nomes
        const fornecedoresProduto = await fornecedorService.listarProdutos()
        const fornecedoresServico = await fornecedorService.listarServicos()
        const todosFornecedores = [...fornecedoresProduto, ...fornecedoresServico]
        console.log('DEBUG - Fornecedores carregados:', todosFornecedores.length)

        // Criar mapa de fornecedores por ID
        const fornecedoresMap = {}
        todosFornecedores.forEach(f => {
          fornecedoresMap[f.id] = f.razaoSocial || 'Fornecedor desconhecido'
        })

        // IDs dos itens deste pedido
        const idsItens = pedido.itens.map(item => item.id).filter(id => id != null)
        console.log('IDs dos itens:', idsItens)

        // Filtrar cotaÃ§Ãµes relacionadas aos itens do pedido
        const cotacoesRelacionadas = todasCotacoes.filter(cot =>
          idsItens.includes(cot.itemPedidoId)
        )
        console.log('œ… Cotações relacionadas:', cotacoesRelacionadas.length)

        // Enriquecer cotaÃ§Ãµes com nome do fornecedor
        const cotacoesComFornecedor = cotacoesRelacionadas.map(cot => ({
          ...cot,
          fornecedorNome: fornecedoresMap[cot.fornecedorId] || `Fornecedor #${cot.fornecedorId}`
        }))

        // Adicionar cotaÃ§Ãµes aos itens
        pedido.itens.forEach(item => {
          if (item.id) {
            const cotacoesDoItem = cotacoesComFornecedor.filter(cot => cot.itemPedidoId === item.id)
            item.cotacoes = cotacoesDoItem
            console.log(`Item ${item.id} - ${item.nome}: ${cotacoesDoItem.length} cotaÃ§Ãµes`)
          }
        })

        console.log('Detalhes carregados com sucesso!')

      } catch (error) {
        console.error('Erro ao carregar detalhes do pedido:', error)
      } finally {
        carregandoItens.value = false
      }
    }

    const salvarRascunhoSemFechar = async (dadosPedido) => {
      try {
        isLoading.value = true

        if (pedidoEditando.value?.id) {
          // Edição - atualizar rascunho existente
          const response = await pedidoService.atualizar(pedidoEditando.value.id, dadosPedido)
          const index = pedidos.value.findIndex(p => p.id === pedidoEditando.value.id)
          if (index !== -1) {
            const pedidoAtualizado = response.data || { ...pedidoEditando.value, ...dadosPedido }
            pedidos.value[index] = pedidoAtualizado

            // IMPORTANTE: Não atualizar pedidoEditando.value aqui para não perder itens não salvos no formulário
            // O formulário mantém seu próprio estado e só será recarregado quando o modal for fechado e reaberto
            console.log('Pedido atualizado na lista, formulário mantém estado local')
          }
        } else {
          // Criação - criar novo rascunho
          const response = await pedidoService.criar(dadosPedido)
          const novoPedido = response.data || {
            id: Date.now(),
            dataCriacao: new Date().toISOString(),
            ...dadosPedido
          }
          pedidos.value.unshift(novoPedido)

          // Para criação, atualizar pedidoEditando com o ID retornado, mas preservando os itens do formulário
          // Apenas atualizar o ID para futuras operações serem update em vez de create
          if (pedidoEditando.value) {
            pedidoEditando.value.id = novoPedido.id
          } else {
            pedidoEditando.value = novoPedido
          }
        }

        // NÃO fechar o formulário - mantém modal aberto
        console.log('Rascunho salvo com sucesso, modal permanece aberto')

      } catch (error) {
        console.error('Erro ao salvar rascunho:', error)
        alert('Erro ao salvar rascunho. Tente novamente.')
      } finally {
        isLoading.value = false
      }
    }

    const salvarPedido = async (dadosPedido) => {
      try {
        isLoading.value = true

        if (pedidoEditando.value?.id) {
          // EdiÃ§Ã£o
          const response = await pedidoService.atualizar(pedidoEditando.value.id, dadosPedido)
          const index = pedidos.value.findIndex(p => p.id === pedidoEditando.value.id)
          if (index !== -1) {
            pedidos.value[index] = response.data || { ...pedidoEditando.value, ...dadosPedido }
          }
        } else {
          // CriaÃ§Ã£o
          const response = await pedidoService.criar(dadosPedido)
          const novoPedido = response.data || {
            id: Date.now(),

            dataCriacao: new Date().toISOString(),
            ...dadosPedido
          }
          pedidos.value.unshift(novoPedido)
        }

        fecharFormulario()

      } catch (error) {
        console.error('Erro ao salvar pedido:', error)
        alert('Erro ao salvar pedido. Tente novamente.')
      } finally {
        isLoading.value = false
      }
    }

    const confirmarExclusao = (pedido) => {
      pedidoParaExcluir.value = pedido
      showConfirmModal.value = true
    }

    const excluirPedido = async () => {
      try {
        if (pedidoParaExcluir.value?.id) {
          await pedidoService.excluir(pedidoParaExcluir.value.id)
          const index = pedidos.value.findIndex(p => p.id === pedidoParaExcluir.value.id)
          if (index !== -1) {
            pedidos.value.splice(index, 1)
          }
        }
      } catch (error) {
        console.error('Erro ao excluir pedido:', error)
        alert('Erro ao excluir pedido. Tente novamente.')
      } finally {
        showConfirmModal.value = false
        pedidoParaExcluir.value = null
      }
    }

    const aprovarPedido = async (pedido) => {
      try {
        isLoading.value = true
        // Usar o enum correto do backend: APROVADO
        const dadosAtualizacao = {
          ...pedido,
          status: 'APROVADO'
        }
        await pedidoService.atualizar(pedido.id, dadosAtualizacao)

        const index = pedidos.value.findIndex(p => p.id === pedido.id)
        if (index !== -1) {
          pedidos.value[index].status = 'APROVADO'
        }

        alert('Pedido aprovado com sucesso!')
      } catch (error) {
        console.error('Erro ao aprovar pedido:', error)
        alert('Erro ao aprovar pedido. Tente novamente.')
      } finally {
        isLoading.value = false
      }
    }

    // MÃ©todos de permissÃ£o
    const podeEditar = (pedido) => {
      // Permite editar APENAS pedidos com status RASCUNHO
      return pedido.status === 'RASCUNHO'
    }

    const podeAprovar = (pedido) => {
      // SÃ³ pode aprovar pedidos pendentes
      return ['PENDENTE'].includes(pedido.status)
    }

    const podeExcluir = (pedido) => {
      // Pode excluir pedidos rascunho ou pendentes
      return ['RASCUNHO', 'PENDENTE'].includes(pedido.status)
    }

    const podeAlterarStatus = (pedido) => {
      // Pode alterar status de pedidos que nÃ£o estÃ£o finalizados
      return !['APROVADO', 'CANCELADO'].includes(pedido.status)
    }

    const getStatusDisponiveis = (statusAtual) => {
      const todosStatus = [
        { value: 'PENDENTE', label: 'Pendente' },
        { value: 'EM_ANALISE', label: 'Em Análise' },
        { value: 'EM_ANDAMENTO', label: 'Em Andamento' },
        { value: 'APROVADO', label: 'Aprovado' },
        { value: 'CANCELADO', label: 'Cancelado' }
      ]

      // Filtrar status disponíveis baseado no status atual
      switch (statusAtual) {
        case 'RASCUNHO':
          return todosStatus.filter(s => ['PENDENTE', 'CANCELADO'].includes(s.value))
        case 'PENDENTE':
          return todosStatus.filter(s => ['EM_ANALISE', 'APROVADO', 'CANCELADO'].includes(s.value))
        case 'EM_ANALISE':
          return todosStatus.filter(s => ['EM_ANDAMENTO', 'APROVADO', 'CANCELADO'].includes(s.value))
        case 'EM_ANDAMENTO':
          return todosStatus.filter(s => ['APROVADO', 'CANCELADO'].includes(s.value))
        default:
          return []
      }
    }

    const alterarStatus = async (pedido, novoStatus) => {
      try {
        isLoading.value = true

        // ConfirmaÃ§Ã£o antes de alterar
        const confirmacao = confirm(
          `Tem certeza que deseja alterar o status do pedido #${pedido.id} para "${getStatusLabel(novoStatus)}"?`
        )

        if (!confirmacao) {
          // Reset do select
          document.querySelector(`select[value="${novoStatus}"]`).value = pedido.status
          return
        }

        await pedidoService.alterarStatus(pedido.id, novoStatus)

        const index = pedidos.value.findIndex(p => p.id === pedido.id)
        if (index !== -1) {
          pedidos.value[index].status = novoStatus
        }

        alert(`Status do pedido alterado para "${getStatusLabel(novoStatus)}" com sucesso!`)
      } catch (error) {
        console.error('Erro ao alterar status do pedido:', error)
        alert('Erro ao alterar status do pedido. Tente novamente.')
      } finally {
        isLoading.value = false
      }
    }

    // Lifecycle
    onMounted(() => {
      carregarPedidos()
    })

    return {
      // Estados
      pedidos,
      isLoading,
      searchQuery,
      filtroStatus,
      filtroPeriodo,

      // Modais
      showPedidoForm,
      showConfirmModal,
      showDetalhesModal,
      pedidoEditando,
      pedidoParaExcluir,
      pedidoSelecionado,

      // Detalhes
      detalhesTabAtiva,
      detalhesTabs,

      // Computed
      totalPedidos,
      novosPedidosMes,
      pedidosPendentes,
      pedidosAprovados,
      percentualPendentes,
      valorTotalFormatado,
      pedidosFiltrados,

      // MÃ©todos
      carregarPedidos,
      formatarData,
      formatarDataCompleta,
      getStatusLabel,
      getStatusClass,
      getQuantidadeItens,
      filtrarPedidos,
      limparFiltros,
      abrirFormularioNovo,
      editarPedido,
      visualizarPedido,
      fecharFormulario,
      fecharDetalhes,
      salvarRascunhoSemFechar,
      salvarPedido,
      confirmarExclusao,
      excluirPedido,
      aprovarPedido,
      podeEditar,
      podeAprovar,
      podeExcluir,
      podeAlterarStatus,
      getStatusDisponiveis,
      alterarStatus
    }
  }
}
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

.action-icon {
  flex-shrink: 0;
}

.action-button:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

/* MÃ©tricas */
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
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
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
  flex-shrink: 0;
}

.metric-icon.total { background: #1F285F; }
.metric-icon.pending { background: #f59e0b; }
.metric-icon.approved { background: #10b981; }
.metric-icon.value { background: #3b82f6; }

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

.metric-growth.positive { color: #10b981; }
.metric-growth.negative { color: #ef4444; }
.metric-growth.neutral { color: #6b7280; }

/* SeÃ§Ã£o de Pesquisa */
.search-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 32px;
  border: 1px solid #e5e7eb;
}

.search-container {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input-container {
  flex: 1;
  min-width: 300px;
  position: relative;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  z-index: 1;
}

.search-input {
  width: 100%;
  padding: 12px 16px 12px 40px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  transition: all 0.2s;
  background: white;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

/* Estilos padronizados para form-select */
.form-select {
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background: white;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 180px;
}

.form-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.filter-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f9fafb;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.875rem;
}

.filter-button:hover {
  background: #f3f4f6;
  border-color: #9ca3af;
}

/* Loading */
.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 32px;
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  color: #374151;
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
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Tabela */
.table-section {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
}

.table-container {
  overflow-x: auto;
}

.pedidos-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.875rem;
}

.pedidos-table th {
  background: #f8fafc;
  color: #374151;
  padding: 16px;
  text-align: left;
  font-weight: 600;
  font-size: 0.875rem;
  border-bottom: 1px solid #e5e7eb;
}

.pedidos-table td {
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: top;
}

.table-row {
  transition: all 0.2s ease;
}

.table-row:hover {
  background-color: #f9fafb;
}

/* CÃ©lulas da tabela */
.pedido-cell .pedido-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.pedido-numero {
  font-weight: 600;
  color: #111827;
  font-size: 0.875rem;
}







.itens-count {
  color: #6b7280;
  font-size: 0.875rem;
}

.data-criacao {
  color: #374151;
  font-size: 0.875rem;
}

/* Status badges */
.status-badge {
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 500;
}

.status-draft {
  background: #f3f4f6;
  color: #374151;
}

.status-pending {
  background: #fef3c7;
  color: #d97706;
}

.status-approved {
  background: #d1fae5;
  color: #059669;
}

.status-rejected {
  background: #fee2e2;
  color: #dc2626;
}

.status-canceled {
  background: #f3f4f6;
  color: #6b7280;
}

.status-progress {
  background: #dbeafe;
  color: #2563eb;
}

.status-default {
  background: #f3f4f6;
  color: #374151;
}

/* BotÃµes de aÃ§Ã£o */
.actions-cell {
  width: 120px;
}

.actions-cell .action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
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
  transition: all 0.2s ease;
}

.action-btn.view {
  background: #f3f4f6;
  color: #374151;
}

.action-btn.view:hover {
  background: #e5e7eb;
}

.action-btn.edit {
  background: #fef3c7;
  color: #d97706;
}

.action-btn.edit:hover {
  background: #fde68a;
}

.action-btn.approve {
  background: #d1fae5;
  color: #059669;
}

.action-btn.approve:hover {
  background: #a7f3d0;
}

.action-btn.delete {
  background: #fee2e2;
  color: #dc2626;
}

.action-btn.delete:hover {
  background: #fecaca;
}

/* Status Actions */
.status-actions {
  position: relative;
}

.status-select {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 6px 8px;
  font-size: 12px;
  font-weight: 500;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 120px;
}

.status-select:hover {
  background: #e2e8f0;
  border-color: #cbd5e1;
}

.status-select:focus {
  outline: none;
  border-color: #3b82f6;
  background: white;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* Estado vazio */
.empty-state {
  text-align: center;
  padding: 64px 32px;
  color: #6b7280;
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}

.empty-icon {
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 1.25rem;
  color: #374151;
  margin-bottom: 8px;
}

.empty-state p {
  margin-bottom: 32px;
}

.btn-primary {
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-primary:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

/* Modal de Detalhes */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
}

.detalhes-modal {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 800px;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
}

.detalhes-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  background: #f8fafc;
}

.detalhes-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #111827;
}

.close-button {
  background: none;
  border: none;
  color: #9ca3af;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.close-button:hover {
  background: #e5e7eb;
  color: #374151;
}

.detalhes-body {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.detalhes-tabs {
  display: flex;
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
}

.tab-button {
  padding: 16px 24px;
  border: none;
  background: none;
  cursor: pointer;
  font-weight: 500;
  color: #6b7280;
  transition: all 0.2s ease;
  border-bottom: 2px solid transparent;
}

.tab-button.active {
  color: #3b82f6;
  border-bottom-color: #3b82f6;
  background: white;
}

.tab-button:hover:not(.active) {
  color: #374151;
  background: #f3f4f6;
}

.detalhes-content {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
}

.detalhes-section h4 {
  margin: 0 0 1rem 0;
  color: #2d3748;
  font-size: 1.25rem;
  font-weight: 600;
}

.info-grid {
  display: grid;
  gap: 2rem;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
}

.info-group {
  background: #f7fafc;
  padding: 1.5rem;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.info-group.full-width {
  grid-column: 1 / -1;
}

.info-group h4 {
  margin: 0 0 1rem 0;
  color: #2d3748;
  font-size: 1.1rem;
  font-weight: 600;
}

.info-group p {
  margin: 0.5rem 0;
  color: #4a5568;
  line-height: 1.6;
}

.info-group strong {
  color: #2d3748;
  font-weight: 600;
}

/* Itens List */
.itens-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.item-card {
  background: #f7fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 1.5rem;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.item-numero {
  background: #667eea;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 600;
}

.item-quantidade {
  background: #edf2f7;
  color: #4a5568;
  padding: 0.25rem 0.75rem;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 600;
}

.item-details h5 {
  margin: 0 0 0.5rem 0;
  color: #2d3748;
  font-size: 1.1rem;
  font-weight: 600;
}

.item-details p {
  margin: 0.5rem 0;
  color: #4a5568;
  line-height: 1.6;
}

.item-details strong {
  color: #2d3748;
  font-weight: 600;
}

/* Timeline */
.historico-timeline {
  position: relative;
  padding-left: 2rem;
}

.historico-timeline::before {
  content: '';
  position: absolute;
  left: 15px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e2e8f0;
}

.timeline-item {
  position: relative;
  margin-bottom: 2rem;
}

.timeline-marker {
  position: absolute;
  left: -2rem;
  top: 0;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 3px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.timeline-marker.created { background: #667eea; }
.timeline-marker.submitted { background: #f6ad55; }
.timeline-marker.aprovado { background: #68d391; }
.timeline-marker.rejeitado { background: #fc8181; }

.timeline-content {
  background: #f7fafc;
  padding: 1.5rem;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.timeline-content h5 {
  margin: 0 0 0.5rem 0;
  color: #2d3748;
  font-weight: 600;
}

.timeline-content p {
  margin: 0 0 0.5rem 0;
  color: #4a5568;
  font-size: 0.9rem;
}

.timeline-user {
  color: #718096;
  font-size: 0.8rem;
  font-style: italic;
}

.empty-message {
  text-align: center;
  color: #718096;
  font-style: italic;
  padding: 2rem;
}

/* Responsividade */
@media (max-width: 1024px) {
  .welcome-header {
    flex-direction: column;
    align-items: stretch;
    text-align: center;
  }

  .search-container {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input-container {
    min-width: auto;
  }

  .search-actions {
    justify-content: stretch;
  }

  .form-select {
    flex: 1;
    min-width: auto;
  }
}

@media (max-width: 768px) {
  .welcome-content h1 {
    font-size: 2rem;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
  }

  .table-container {
    overflow-x: scroll;
  }

  .pedidos-table {
    min-width: 800px;
  }

  .detalhes-modal {
    margin: 1rem;
    max-width: none;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .detalhes-tabs {
    overflow-x: auto;
  }

  .tab-button {
    white-space: nowrap;
    min-width: 120px;
  }
}

@media (max-width: 640px) {
  .main-content {
    padding: 0.5rem;
  }

  .welcome-section,
  .search-section,
  .table-section {
    margin-bottom: 1rem;
    padding: 1rem;
  }

  .metric-card {
    padding: 1rem;
  }

  .metric-value {
    font-size: 2rem;
  }

  .actions-cell .action-buttons {
    flex-wrap: wrap;
    gap: 0.25rem;
  }

  .action-btn {
    width: 28px;
    height: 28px;
  }

  .itens-list-detalhada,
  .item-card-detalhado {
    margin-bottom: 0.75rem;
  }

  .item-info-grid {
    grid-template-columns: 1fr;
  }

  .cotacoes-mini-list {
    flex-direction: column;
  }

  .atividade-grupo {
    margin-bottom: 1rem;
  }

  .historico-stats {
    grid-template-columns: 1fr;
  }
}

/* Estilos para Itens Detalhados */
.itens-list-detalhada {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
}

.item-card-detalhado {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.2s;
}

.item-card-detalhado:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  border-color: #3b82f6;
}

.item-header-detalhado {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #f8fafc;
  border-bottom: 1px solid #e5e7eb;
}

.item-numero-badge {
  background: #3b82f6;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

.item-quantidade-badge {
  background: #10b981;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

.item-content {
  padding: 16px;
}

.item-nome {
  font-size: 1rem;
  font-weight: 600;
  color: #111827;
  margin: 0 0 12px 0;
}

.item-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  margin-bottom: 12px;
}

.item-info,
.item-descricao,
.item-observacao {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.info-value {
  font-size: 0.875rem;
  color: #374151;
  line-height: 1.5;
}

/* Cotações Mini Card */
.item-cotacoes {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.cotacoes-mini-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.cotacao-mini-card {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 200px;
}

.cotacao-fornecedor {
  font-size: 0.75rem;
  font-weight: 600;
  color: #374151;
}

.cotacao-valor {
  font-size: 0.875rem;
  font-weight: 700;
  color: #059669;
}

.cotacao-prazo {
  font-size: 0.75rem;
  color: #6b7280;
}

/* HistÃ³rico */
.loading-message {
  text-align: center;
  padding: 40px;
  color: #6b7280;
  font-style: italic;
}

.historico-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
  margin: 24px 0;
}

.stat-card-small {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-icon {
  font-size: 2rem;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-info .stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #111827;
  line-height: 1;
}

.stat-info .stat-label {
  font-size: 0.75rem;
  color: #6b7280;
  margin-top: 4px;
}

/* Atividades */
.historico-atividades {
  margin-top: 24px;
}

.historico-atividades h5 {
  font-size: 1rem;
  font-weight: 600;
  color: #111827;
  margin-bottom: 16px;
}

.atividade-grupo {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.atividade-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e5e7eb;
}

.atividade-header strong {
  font-size: 0.875rem;
  color: #111827;
}

.atividade-count {
  background: #3b82f6;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.75rem;
  font-weight: 600;
}

.atividades-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.atividade-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.atividade-marker {
  width: 12px;
  height: 12px;
  background: #3b82f6;
  border-radius: 50%;
  margin-top: 4px;
  flex-shrink: 0;
}

.atividade-content {
  flex: 1;
}

.atividade-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.atividade-fornecedor {
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
}

.atividade-data {
  font-size: 0.75rem;
  color: #6b7280;
}

.atividade-detalhes {
  display: flex;
  gap: 16px;
  font-size: 0.75rem;
  color: #6b7280;
}

.atividade-valor {
  color: #059669;
  font-weight: 600;
}

.timeline-status {
  display: inline-block;
  margin-top: 4px;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.75rem;
  background: #e5e7eb;
  color: #374151;
}
</style>
