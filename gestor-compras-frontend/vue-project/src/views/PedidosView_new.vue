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
            <h1 class="welcome-title">Pedidos de Compra 📋</h1>
            <p class="welcome-subtitle">
              Gerencie solicitações de pedidos e acompanhe o fluxo de aprovação
            </p>
          </div>
          <button class="action-button" @click="abrirFormularioNovo">
            <svg class="action-icon" viewBox="0 0 24 24" width="20" height="20">
              <path fill="white" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" />
            </svg>
            Novo Pedido
          </button>
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
            <input type="text" v-model="searchQuery" placeholder="Pesquisar por número, requisitante, descrição..."
              class="search-input" @input="filtrarPedidos" />
          </div>
          <div class="search-actions">
            <select v-model="filtroStatus" @change="filtrarPedidos" class="filter-select">
              <option value="">Todos os status</option>
              <option value="rascunho">Rascunho</option>
              <option value="pendente">Pendente</option>
              <option value="aprovado">Aprovado</option>
              <option value="rejeitado">Rejeitado</option>
              <option value="cancelado">Cancelado</option>
            </select>
            <select v-model="filtroPeriodo" @change="filtrarPedidos" class="filter-select">
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
                <th>Requisitante</th>
                <th>Unidade</th>
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
                    <span class="pedido-numero">#{{ pedido.numero || pedido.id }}</span>
                    <span class="pedido-descricao" v-if="pedido.descricao">{{ pedido.descricao }}</span>
                  </div>
                </td>
                <td class="requisitante-cell">
                  <span class="requisitante-name">{{ pedido.requisitante || 'Não informado' }}</span>
                </td>
                <td class="unidade-cell">
                  <span class="unidade-tag">{{ pedido.unidadeFuncional || 'N/A' }}</span>
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
                    <button class="action-btn approve" @click="aprovarPedido(pedido)" title="Aprovar" v-if="podeAprovar(pedido)">
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
      />

      <!-- Modal de Confirmação de Exclusão -->
      <ConfirmModal
        :isVisible="showConfirmModal"
        title="Confirmar Exclusão"
        :message="`Tem certeza que deseja excluir o pedido #${pedidoParaExcluir?.numero || pedidoParaExcluir?.id}?`"
        confirmText="Excluir"
        confirmClass="btn-danger"
        @confirm="excluirPedido"
        @cancel="showConfirmModal = false"
      />

      <!-- Modal de Detalhes do Pedido -->
      <div v-if="showDetalhesModal" class="modal-overlay" @click="fecharDetalhes">
        <div class="detalhes-modal" @click.stop>
          <div class="detalhes-header">
            <h2>Pedido #{{ pedidoSelecionado?.numero || pedidoSelecionado?.id }}</h2>
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
                    <p><strong>Número:</strong> #{{ pedidoSelecionado?.numero || pedidoSelecionado?.id }}</p>
                    <p><strong>Status:</strong>
                      <span class="status-badge" :class="getStatusClass(pedidoSelecionado?.status)">
                        {{ getStatusLabel(pedidoSelecionado?.status) }}
                      </span>
                    </p>
                    <p><strong>Data de Criação:</strong> {{ formatarDataCompleta(pedidoSelecionado?.dataCriacao || pedidoSelecionado?.dataPedido) }}</p>
                  </div>

                  <div class="info-group">
                    <h4>Requisitante</h4>
                    <p><strong>Nome:</strong> {{ pedidoSelecionado?.requisitante || 'Não informado' }}</p>
                    <p><strong>Unidade:</strong> {{ pedidoSelecionado?.unidadeFuncional || 'Não informado' }}</p>
                    <p><strong>Objetivo:</strong> {{ pedidoSelecionado?.objetivo || 'Não informado' }}</p>
                  </div>

                  <div class="info-group full-width">
                    <h4>Observações</h4>
                    <p>{{ pedidoSelecionado?.observacoes || pedidoSelecionado?.observacao || 'Nenhuma observação informada' }}</p>
                  </div>
                </div>
              </div>

              <!-- Aba Itens -->
              <div v-if="detalhesTabAtiva === 'itens'" class="detalhes-section">
                <h4>Itens do Pedido</h4>
                <div class="itens-list" v-if="pedidoSelecionado?.itens?.length">
                  <div v-for="(item, index) in pedidoSelecionado.itens" :key="index" class="item-card">
                    <div class="item-header">
                      <span class="item-numero">Item #{{ index + 1 }}</span>
                      <span class="item-quantidade">{{ item.quantidade }}x</span>
                    </div>
                    <div class="item-details">
                      <h5>{{ item.produto || item.nome || item.descricao }}</h5>
                      <p v-if="item.justificativa"><strong>Justificativa:</strong> {{ item.justificativa }}</p>
                      <p v-if="item.observacao"><strong>Observação:</strong> {{ item.observacao }}</p>
                    </div>
                  </div>
                </div>
                <p v-else class="empty-message">Nenhum item cadastrado neste pedido.</p>
              </div>

              <!-- Aba Histórico -->
              <div v-if="detalhesTabAtiva === 'historico'" class="detalhes-section">
                <h4>Histórico de Aprovação</h4>
                <div class="historico-timeline">
                  <div class="timeline-item">
                    <div class="timeline-marker created"></div>
                    <div class="timeline-content">
                      <h5>Pedido Criado</h5>
                      <p>{{ formatarDataCompleta(pedidoSelecionado?.dataCriacao || pedidoSelecionado?.dataPedido) }}</p>
                      <span class="timeline-user">{{ pedidoSelecionado?.requisitante }}</span>
                    </div>
                  </div>

                  <div class="timeline-item" v-if="pedidoSelecionado?.status !== 'rascunho'">
                    <div class="timeline-marker submitted"></div>
                    <div class="timeline-content">
                      <h5>Enviado para Aprovação</h5>
                      <p>{{ formatarDataCompleta(pedidoSelecionado?.dataUltimaAtualizacao) }}</p>
                    </div>
                  </div>

                  <div class="timeline-item" v-if="['aprovado', 'rejeitado'].includes(pedidoSelecionado?.status)">
                    <div class="timeline-marker" :class="pedidoSelecionado?.status"></div>
                    <div class="timeline-content">
                      <h5>{{ pedidoSelecionado?.status === 'aprovado' ? 'Aprovado' : 'Rejeitado' }}</h5>
                      <p>{{ formatarDataCompleta(pedidoSelecionado?.dataUltimaAtualizacao) }}</p>
                    </div>
                  </div>
                </div>
                <p class="empty-message">Funcionalidade de histórico em desenvolvimento...</p>
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
import { pedidoService } from '@/services/pedidoService.js'

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
      { id: 'itens', label: 'Itens' },
      { id: 'historico', label: 'Histórico' }
    ])

    // Computed properties para métricas
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
      pedidos.value.filter(p => p.status === 'pendente').length
    )

    const pedidosAprovados = computed(() =>
      pedidos.value.filter(p => p.status === 'aprovado').length
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
          (pedido.numero?.toString().includes(query)) ||
          (pedido.id?.toString().includes(query)) ||
          (pedido.requisitante?.toLowerCase().includes(query)) ||
          (pedido.descricao?.toLowerCase().includes(query)) ||
          (pedido.unidadeFuncional?.toLowerCase().includes(query))
        )
      }

      // Filtro por status
      if (filtroStatus.value) {
        resultado = resultado.filter(pedido => pedido.status === filtroStatus.value)
      }

      // Filtro por período
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

    // Métodos de dados
    const carregarPedidos = async () => {
      try {
        isLoading.value = true
        const response = await pedidoService.listarTodos()

        if (response && Array.isArray(response)) {
          pedidos.value = response
        } else if (response && response.data && Array.isArray(response.data)) {
          pedidos.value = response.data
        } else {
          // Dados de exemplo se a API não estiver funcionando
          pedidos.value = [
            {
              id: 1,
              numero: '2024001',
              requisitante: 'João Silva',
              unidadeFuncional: 'TI - Infraestrutura',
              status: 'pendente',
              descricao: 'Compra de equipamentos de informática',
              objetivo: 'Renovação do parque tecnológico',
              dataCriacao: '2024-01-15T10:30:00',
              observacao: 'Urgente para o fechamento do projeto',
              itens: [
                {
                  id: 1,
                  produto: 'Notebook Dell Latitude 5520',
                  quantidade: 5,
                  justificativa: 'Para equipe de desenvolvimento',
                  observacao: 'Configuração mínima: i5, 16GB RAM, SSD 512GB'
                },
                {
                  id: 2,
                  produto: 'Monitor LG 24" Full HD',
                  quantidade: 5,
                  justificativa: 'Complemento para os notebooks'
                }
              ]
            },
            {
              id: 2,
              numero: '2024002',
              requisitante: 'Maria Santos',
              unidadeFuncional: 'RH - Treinamento',
              status: 'aprovado',
              descricao: 'Material para treinamentos',
              objetivo: 'Capacitação de funcionários',
              dataCriacao: '2024-01-10T14:20:00',
              observacao: 'Aprovado pela gerência',
              itens: [
                {
                  id: 1,
                  produto: 'Apostilas de treinamento',
                  quantidade: 50,
                  justificativa: 'Curso de segurança do trabalho'
                }
              ]
            },
            {
              id: 3,
              numero: '2024003',
              requisitante: 'Carlos Oliveira',
              unidadeFuncional: 'Manutenção',
              status: 'rascunho',
              descricao: 'Ferramentas para manutenção',
              objetivo: 'Reposição de ferramentas',
              dataCriacao: '2024-01-20T09:15:00',
              itens: [
                {
                  id: 1,
                  produto: 'Chaves de fenda variadas',
                  quantidade: 10,
                  justificativa: 'Reposição do estoque'
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
        'rascunho': 'Rascunho',
        'pendente': 'Pendente',
        'aprovado': 'Aprovado',
        'rejeitado': 'Rejeitado',
        'cancelado': 'Cancelado'
      }
      return labels[status] || status || 'Indefinido'
    }

    const getStatusClass = (status) => {
      const classes = {
        'rascunho': 'status-draft',
        'pendente': 'status-pending',
        'aprovado': 'status-approved',
        'rejeitado': 'status-rejected',
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
      // Este método existe para ser chamado nos eventos de input
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

    const editarPedido = (pedido) => {
      pedidoEditando.value = { ...pedido }
      showPedidoForm.value = true
    }

    const visualizarPedido = (pedido) => {
      pedidoSelecionado.value = pedido
      detalhesTabAtiva.value = 'info'
      showDetalhesModal.value = true
    }

    const fecharFormulario = () => {
      showPedidoForm.value = false
      pedidoEditando.value = null
    }

    const fecharDetalhes = () => {
      showDetalhesModal.value = false
      pedidoSelecionado.value = null
    }

    const salvarPedido = async (dadosPedido) => {
      try {
        isLoading.value = true

        if (pedidoEditando.value?.id) {
          // Edição
          const response = await pedidoService.atualizar(pedidoEditando.value.id, dadosPedido)
          const index = pedidos.value.findIndex(p => p.id === pedidoEditando.value.id)
          if (index !== -1) {
            pedidos.value[index] = response.data || { ...pedidoEditando.value, ...dadosPedido }
          }
        } else {
          // Criação
          const response = await pedidoService.criar(dadosPedido)
          const novoPedido = response.data || {
            id: Date.now(),
            numero: `2024${String(pedidos.value.length + 1).padStart(3, '0')}`,
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
        const dadosAtualizacao = { ...pedido, status: 'aprovado' }
        await pedidoService.atualizar(pedido.id, dadosAtualizacao)

        const index = pedidos.value.findIndex(p => p.id === pedido.id)
        if (index !== -1) {
          pedidos.value[index].status = 'aprovado'
        }

      } catch (error) {
        console.error('Erro ao aprovar pedido:', error)
        alert('Erro ao aprovar pedido. Tente novamente.')
      } finally {
        isLoading.value = false
      }
    }

    // Métodos de permissão
    const podeEditar = (pedido) => {
      return ['rascunho', 'rejeitado'].includes(pedido.status)
    }

    const podeAprovar = (pedido) => {
      return pedido.status === 'pendente'
    }

    const podeExcluir = (pedido) => {
      return ['rascunho', 'rejeitado'].includes(pedido.status)
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

      // Métodos
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
      salvarPedido,
      confirmarExclusao,
      excluirPedido,
      aprovarPedido,
      podeEditar,
      podeAprovar,
      podeExcluir
    }
  }
}
</script>

<style scoped>
/* Layout Principal */
.dashboard-layout {
  display: grid;
  grid-template-areas:
    "sidebar header"
    "sidebar main";
  grid-template-columns: 280px 1fr;
  grid-template-rows: 80px 1fr;
  height: 100vh;
  background-color: #f8fafc;
}

.main-content {
  grid-area: main;
  padding: 2rem;
  overflow-y: auto;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: calc(100vh - 80px);
}

/* Seção de Boas-vindas */
.welcome-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.welcome-content h1 {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2d3748;
  margin: 0 0 0.5rem 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-content p {
  font-size: 1.1rem;
  color: #718096;
  margin: 0;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 0.875rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.action-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
}

.action-icon {
  width: 20px;
  height: 20px;
}

/* Seção de Métricas */
.metrics-section {
  margin-bottom: 2rem;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
}

.metric-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 1.5rem;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.metric-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.metric-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
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

.metric-icon.total { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.metric-icon.pending { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.metric-icon.approved { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.metric-icon.value { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.metric-label {
  font-size: 0.9rem;
  color: #718096;
  font-weight: 500;
}

.metric-value {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.metric-growth {
  font-size: 0.85rem;
  font-weight: 500;
}

.metric-growth.positive { color: #38a169; }
.metric-growth.negative { color: #e53e3e; }
.metric-growth.neutral { color: #718096; }

/* Seção de Pesquisa */
.search-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.search-container {
  display: flex;
  gap: 1rem;
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
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  color: #a0aec0;
  z-index: 1;
}

.search-input {
  width: 100%;
  padding: 0.875rem 1rem 0.875rem 3rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: white;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-actions {
  display: flex;
  gap: 0.75rem;
  align-items: center;
  flex-wrap: wrap;
}

.filter-select {
  padding: 0.75rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  background: white;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.filter-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  background: #f7fafc;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  color: #4a5568;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.filter-button:hover {
  background: #edf2f7;
  border-color: #cbd5e0;
}

/* Loading */
.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  color: #4a5568;
}

.loading-spinner-large {
  width: 48px;
  height: 48px;
  border: 4px solid #e2e8f0;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Tabela */
.table-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.table-container {
  overflow-x: auto;
}

.pedidos-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.pedidos-table th {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.pedidos-table td {
  padding: 1rem;
  border-bottom: 1px solid #e2e8f0;
  vertical-align: top;
}

.table-row {
  transition: all 0.3s ease;
}

.table-row:hover {
  background-color: #f7fafc;
}

/* Células da tabela */
.pedido-cell .pedido-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.pedido-numero {
  font-weight: 600;
  color: #2d3748;
  font-size: 0.95rem;
}

.pedido-descricao {
  color: #718096;
  font-size: 0.85rem;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.requisitante-name {
  font-weight: 500;
  color: #4a5568;
}

.unidade-tag {
  background: #edf2f7;
  color: #4a5568;
  padding: 0.25rem 0.75rem;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 500;
}

.itens-count {
  color: #718096;
  font-size: 0.85rem;
}

.data-criacao {
  color: #4a5568;
  font-size: 0.85rem;
}

/* Status badges */
.status-badge {
  padding: 0.375rem 0.75rem;
  border-radius: 8px;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-draft {
  background: #f7fafc;
  color: #4a5568;
  border: 1px solid #e2e8f0;
}

.status-pending {
  background: #fef5e7;
  color: #d69e2e;
  border: 1px solid #fbb047;
}

.status-approved {
  background: #f0fff4;
  color: #38a169;
  border: 1px solid #68d391;
}

.status-rejected {
  background: #fed7d7;
  color: #e53e3e;
  border: 1px solid #fc8181;
}

.status-canceled {
  background: #edf2f7;
  color: #718096;
  border: 1px solid #cbd5e0;
}

/* Botões de ação */
.actions-cell {
  width: 120px;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn.view {
  background: #edf2f7;
  color: #4a5568;
}

.action-btn.view:hover {
  background: #e2e8f0;
  transform: scale(1.1);
}

.action-btn.edit {
  background: #fef5e7;
  color: #d69e2e;
}

.action-btn.edit:hover {
  background: #fbd38d;
  transform: scale(1.1);
}

.action-btn.approve {
  background: #f0fff4;
  color: #38a169;
}

.action-btn.approve:hover {
  background: #c6f6d5;
  transform: scale(1.1);
}

.action-btn.delete {
  background: #fed7d7;
  color: #e53e3e;
}

.action-btn.delete:hover {
  background: #feb2b2;
  transform: scale(1.1);
}

/* Estado vazio */
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #718096;
}

.empty-icon {
  color: #cbd5e0;
  margin-bottom: 1rem;
}

.empty-state h3 {
  font-size: 1.5rem;
  color: #4a5568;
  margin-bottom: 0.5rem;
}

.empty-state p {
  margin-bottom: 2rem;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 0.875rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
}

/* Modal de Detalhes */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(5px);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.detalhes-modal {
  background: white;
  border-radius: 16px;
  width: 100%;
  max-width: 800px;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
}

.detalhes-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 2rem;
  border-bottom: 1px solid #e2e8f0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.detalhes-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.close-button {
  background: none;
  border: none;
  color: white;
  font-size: 2rem;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.close-button:hover {
  background: rgba(255, 255, 255, 0.2);
}

.detalhes-body {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.detalhes-tabs {
  display: flex;
  border-bottom: 1px solid #e2e8f0;
  background: #f7fafc;
}

.tab-button {
  padding: 1rem 1.5rem;
  border: none;
  background: none;
  cursor: pointer;
  font-weight: 500;
  color: #718096;
  transition: all 0.3s ease;
  border-bottom: 3px solid transparent;
}

.tab-button.active {
  color: #667eea;
  border-bottom-color: #667eea;
  background: white;
}

.tab-button:hover:not(.active) {
  color: #4a5568;
  background: #edf2f7;
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
  .dashboard-layout {
    grid-template-areas:
      "header"
      "main";
    grid-template-columns: 1fr;
    grid-template-rows: 80px 1fr;
  }

  .main-content {
    padding: 1rem;
  }

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

  .filter-select {
    flex: 1;
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

  .action-buttons {
    flex-wrap: wrap;
    gap: 0.25rem;
  }

  .action-btn {
    width: 28px;
    height: 28px;
  }
}
</style>
