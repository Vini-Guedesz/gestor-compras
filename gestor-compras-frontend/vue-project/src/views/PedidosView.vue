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

          <!-- Valor Total - REMOVIDO (valor simulado/mockado) -->
          <!-- TODO: Implementar cálculo real baseado nos valores dos itens dos pedidos -->
          <!--
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
          -->
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
                <th class="col-pedido">Pedido</th>
                <th class="col-status">Status</th>
                <th class="col-data">Data</th>
                <th class="col-acoes">Ações</th>
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
                <td class="data-cell">
                  <span class="data-criacao">{{ formatarData(pedido.dataCriacao || pedido.dataPedido) }}</span>
                </td>
                <td class="actions-td">
                  <div class="actions-cell">
                    <div class="status-actions" :class="{ 'btn-hidden': !podeAlterarStatus(pedido) }">
                      <select
                        @change="alterarStatus(pedido, $event.target.value)"
                        class="status-select"
                        :value="pedido.status"
                        title="Alterar Status"
                        :disabled="!podeAlterarStatus(pedido)"
                      >
                        <option :value="pedido.status" disabled>{{ getStatusLabel(pedido.status) }}</option>
                        <option v-for="status in getStatusDisponiveis(pedido.status)"
                                :key="status.value"
                                :value="status.value">
                          {{ status.label }}
                        </option>
                      </select>
                    </div>
                    <button
                      class="action-btn approve"
                      @click="aprovarPedido(pedido)"
                      title="Aprovar"
                      :class="{ 'btn-hidden': !podeAprovar(pedido) || podeAlterarStatus(pedido) }"
                      :disabled="!podeAprovar(pedido) || podeAlterarStatus(pedido)"
                    >
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                      </svg>
                    </button>
                    <button class="action-btn view" @click="visualizarPedido(pedido)" title="Visualizar">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" />
                      </svg>
                    </button>
                    <button
                      class="action-btn edit"
                      @click="editarPedido(pedido)"
                      title="Editar"
                      :class="{ 'btn-hidden': !podeEditar(pedido) }"
                      :disabled="!podeEditar(pedido)"
                    >
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                      </svg>
                    </button>
                    <button
                      class="action-btn delete"
                      @click="confirmarExclusao(pedido)"
                      title="Excluir"
                      :class="{ 'btn-hidden': !podeExcluir(pedido) }"
                      :disabled="!podeExcluir(pedido)"
                    >
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
            <p>Não há pedidos cadastrados ou que correspondam aos filtros aplicados.</p>
            <button class="btn-primary" @click="abrirFormularioNovo">
              Criar Primeiro Pedido
            </button>
          </div>
        </div>
      </div>

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
          <!-- Header -->
          <div class="detalhes-header">
            <div class="header-title-group">
              <h2>Detalhes do Pedido #{{ String(pedidoSelecionado?.id).padStart(3, '0') }}</h2>
              <span class="status-badge" :class="getStatusClass(pedidoSelecionado?.status)">
                {{ getStatusLabel(pedidoSelecionado?.status) }}
              </span>
            </div>
            <button @click="fecharDetalhes" class="close-button">&times;</button>
          </div>

          <div class="detalhes-body" v-if="pedidoSelecionado">
            <!-- Informações do Pedido -->
            <div class="detalhe-section">
              <h3 class="section-title">📋 Informações do Pedido</h3>
              <div class="info-grid">
                <div class="info-item">
                  <span class="info-label">ID do Pedido</span>
                  <span class="info-value">#{{ pedidoSelecionado.id }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Data de Criação</span>
                  <span class="info-value">{{ formatarDataCompleta(pedidoSelecionado.dataCriacao || pedidoSelecionado.dataPedido) }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Quantidade de Itens</span>
                  <span class="info-value highlight">{{ pedidoSelecionado.itens?.length || 0 }} item(ns)</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Status</span>
                  <span class="info-value" :class="getStatusClass(pedidoSelecionado.status)">
                    {{ getStatusLabel(pedidoSelecionado.status) }}
                  </span>
                </div>
                <div v-if="pedidoSelecionado.objetivo" class="info-item full-width">
                  <span class="info-label">Objetivo do Pedido</span>
                  <span class="info-value">{{ pedidoSelecionado.objetivo }}</span>
                </div>
                <div v-if="pedidoSelecionado.observacao" class="info-item full-width">
                  <span class="info-label">Observações Adicionais</span>
                  <span class="info-value">{{ pedidoSelecionado.observacao }}</span>
                </div>
              </div>
            </div>

            <!-- Itens do Pedido -->
            <div class="detalhe-section">
              <h3 class="section-title">📦 Itens Solicitados</h3>

              <div v-if="carregandoItens" class="loading-state">
                <div class="loading-spinner-small"></div>
                <span>Carregando itens...</span>
              </div>

              <div v-else-if="pedidoSelecionado?.itens?.length" class="itens-lista">
                <div v-for="(item, index) in pedidoSelecionado.itens" :key="item.id || index" class="item-detalhado">
                  <div class="item-header-info">
                    <span class="item-badge">Item {{ index + 1 }}</span>
                    <h4 class="item-nome">{{ item.nome || item.produto || 'Item sem nome' }}</h4>
                  </div>

                  <div class="info-grid">
                    <div class="info-item">
                      <span class="info-label">Quantidade</span>
                      <span class="info-value highlight">{{ item.quantidade }}x</span>
                    </div>
                    <div class="info-item">
                      <span class="info-label">ID do Item</span>
                      <span class="info-value">#{{ item.id }}</span>
                    </div>
                    <div v-if="item.descricao" class="info-item full-width">
                      <span class="info-label">Descrição</span>
                      <span class="info-value">{{ item.descricao }}</span>
                    </div>
                    <div v-if="item.observacao" class="info-item full-width">
                      <span class="info-label">Observações</span>
                      <span class="info-value">{{ item.observacao }}</span>
                    </div>
                  </div>

                  <!-- Cotações deste Item -->
                  <div v-if="item.cotacoes && item.cotacoes.length > 0" class="cotacoes-subsection">
                    <h5 class="subsection-title">💰 Cotações Recebidas ({{ item.cotacoes.length }})</h5>
                    <div class="cotacoes-cards">
                      <div v-for="cotacao in item.cotacoes" :key="cotacao.id" class="cotacao-wrapper">
                        <div class="cotacao-card">
                          <div class="cotacao-fornecedor-info">
                            <div class="fornecedor-detalhes">
                              <div class="fornecedor-nome">{{ cotacao.fornecedorNome || `Fornecedor #${cotacao.fornecedorId}` }}</div>
                              <div class="fornecedor-id">ID: #{{ cotacao.fornecedorId }}</div>
                            </div>
                          </div>

                          <div class="cotacao-valores-grid">
                            <div class="valor-item">
                              <span class="valor-label">Preço</span>
                              <span class="valor-preco">R$ {{ cotacao.preco?.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) }}</span>
                            </div>
                            <div class="valor-item" v-if="cotacao.prazoEmDiasUteis">
                              <span class="valor-label">Prazo</span>
                              <span class="valor-text">{{ cotacao.prazoEmDiasUteis }} dias úteis</span>
                            </div>
                            <div class="valor-item" v-if="cotacao.dataLimite">
                              <span class="valor-label">Validade</span>
                              <span class="valor-text">{{ formatarData(cotacao.dataLimite) }}</span>
                            </div>
                          </div>

                          <div class="cotacao-acao">
                            <button
                              v-if="cotacao.anexoPdf || cotacao.caminhoAnexo"
                              @click="togglePdfViewer(cotacao.id)"
                              :class="['btn-ver-pdf-small', { 'active': pdfAberto === cotacao.id }]"
                              :title="pdfAberto === cotacao.id ? 'Fechar PDF' : 'Visualizar PDF da cotação'"
                            >
                              <svg v-if="pdfAberto !== cotacao.id" viewBox="0 0 24 24" width="16" height="16">
                                <path fill="currentColor" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20M10,19L12,15H9V10H15V15L13,19H10Z"/>
                              </svg>
                              <svg v-else viewBox="0 0 24 24" width="16" height="16">
                                <path fill="currentColor" d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z"/>
                              </svg>
                              {{ pdfAberto === cotacao.id ? 'Fechar' : 'Ver PDF' }}
                            </button>
                            <span v-else class="sem-pdf">Sem PDF</span>
                          </div>
                        </div>

                        <!-- PDF Inline -->
                        <div v-if="pdfAberto === cotacao.id" class="pdf-container">
                          <div v-if="carregandoPdf" class="loading-state">
                            <div class="loading-spinner-small"></div>
                            <span>Carregando PDF...</span>
                          </div>
                          <div v-else-if="erroPdf" class="error-state">
                            <svg viewBox="0 0 24 24" width="24" height="24">
                              <path fill="currentColor" d="M13,13H11V7H13M13,17H11V15H13M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2Z"/>
                            </svg>
                            <span>{{ erroPdf }}</span>
                          </div>
                          <iframe
                            v-else-if="pdfUrl"
                            :src="pdfUrl"
                            class="pdf-iframe"
                            frameborder="0"
                          ></iframe>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div v-else class="no-cotacoes">
                    <svg viewBox="0 0 24 24" width="20" height="20">
                      <path fill="currentColor" d="M13,9H11V7H13M13,17H11V11H13M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2Z"/>
                    </svg>
                    <span>Nenhuma cotação recebida para este item</span>
                  </div>
                </div>
              </div>

              <p v-else class="empty-message">Nenhum item cadastrado neste pedido.</p>
            </div>

            <!-- Histórico de Modificações -->
            <HistoricoPedido
              v-if="pedidoSelecionado?.id"
              :pedidoId="pedidoSelecionado.id"
              :rascunhoId="pedidoSelecionado.rascunhoId"
              :isRascunho="pedidoSelecionado.isRascunho"
              :historicoData="pedidoSelecionado.historico"
            />
          </div>
        </div>
      </div>

    </main>
  </div>
</template>

<script>
import { ref, onMounted, computed, defineAsyncComponent } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from '@/composables/useToast'
import { useErrorModal } from '@/composables/useErrorModal'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
// Lazy loading para componentes pesados
// PedidoForm removido - usando navegação para views dedicadas
const ConfirmModal = defineAsyncComponent(() => import('@/components/ui/modals/ConfirmModal.vue'))
const HistoricoPedido = defineAsyncComponent(() => import('@/features/pedidos/components/HistoricoPedido.vue'))
import pedidoService from '@/services/pedidoService.js'
import cotacaoService from '@/services/cotacaoService.js'
import cotacaoRascunhoService from '@/services/cotacaoRascunhoService.js'
import itemPedidoService from '@/services/itemPedidoService.js'
import relatorioService from '@/services/relatorioService.js'
import rascunhoService from '@/services/rascunhoService.js'
import logger from '@/utils/logger.js'

export default {
  name: 'PedidosView',
  components: {
    DashboardHeader,
    DashboardSidebar,
    ConfirmModal,
    HistoricoPedido
  },
  setup() {
    // Router
    const route = useRoute()
    const router = useRouter()
    const { success, warning, error: toastError } = useToast()

    // Estados reativos
    const pedidos = ref([])
    const isLoading = ref(false)
    const searchQuery = ref('')
    const filtroStatus = ref('')
    const filtroPeriodo = ref('')

    // Como simplificamos o formulário, não precisamos mais de extração complexa

    // Modais
    const showConfirmModal = ref(false)
    const showDetalhesModal = ref(false)
    const pedidoParaExcluir = ref(null)
    const pedidoSelecionado = ref(null)

    // Estados para itens
    const carregandoItens = ref(false)

    // Estados para visualizador de PDF inline
    const pdfAberto = ref(null) // ID da cotação com PDF aberto
    const carregandoPdf = ref(false)
    const erroPdf = ref(null)
    const pdfUrl = ref(null)

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
      pedidos.value.filter(p => ['PENDENTE', 'EM_ANALISE', 'pendente'].includes(p.status)).length
    )

    const pedidosAprovados = computed(() =>
      pedidos.value.filter(p => p.status === 'APROVADO' || p.status === 'aprovado').length
    )

    const percentualPendentes = computed(() => {
      if (totalPedidos.value === 0) return 0
      return Math.round((pedidosPendentes.value / totalPedidos.value) * 100)
    })

    // REMOVIDO: Valor simulado/mockado - implementar cálculo real baseado nos itens
    /*
    const valorTotalFormatado = computed(() => {
      // Simulação de valor total - seria calculado baseado nos itens
      const valorSimulado = pedidos.value.length * 1500
      return valorSimulado.toLocaleString('pt-BR')
    })
    */

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

    // Métodos de dados
    const carregarPedidos = async () => {
      try {
        isLoading.value = true

        // Buscar pedidos, itens e rascunhos em paralelo
        const [response, todosItens, rascunhos] = await Promise.all([
          pedidoService.listarTodos(),
          itemPedidoService.listarTodos(),
          rascunhoService.listar()
        ])


        let listaPedidos = []

        // Processar pedidos do backend
        if (response && Array.isArray(response)) {
          listaPedidos = response.map(pedido => {
            const itensDoPedido = todosItens.filter(item => {
              return item.solicitacaoDePedido?.id === pedido.id
            })

            return {
              ...pedido,
              itens: itensDoPedido,
              descricao: pedido.observacao || 'Sem descrição',
              dataPedido: pedido.dataCriacao || pedido.dataPedido,
              isRascunho: false
            }
          })
        } else if (response && response.data && Array.isArray(response.data)) {
          listaPedidos = response.data.map(pedido => {
            const itensDoPedido = todosItens.filter(item => {
              return item.solicitacaoDePedido?.id === pedido.id
            })

            return {
              ...pedido,
              itens: itensDoPedido,
              descricao: pedido.observacao || 'Sem descrição',
              dataPedido: pedido.dataCriacao || pedido.dataPedido,
              isRascunho: false
            }
          })
        }

        // Adicionar rascunhos à lista com status baseado no status real
        const rascunhosFormatados = rascunhos.map(rascunho => {
          let status = 'RASCUNHO'
          if (rascunho.status === 'FINALIZADO') {
            status = 'RASCUNHO_FINALIZADO'
          } else if (rascunho.status === 'EM_COTACAO') {
            status = 'EM_COTACAO'
          }

          return {
            id: `R-${rascunho.id}`,
            rascunhoId: rascunho.id,
            status: status,
            statusRascunho: rascunho.status || 'ATIVO',
            pedidoGeradoId: rascunho.pedidoGeradoId,
            dataCriacao: rascunho.dataCriacao,
            dataPedido: rascunho.dataCriacao,
            observacao: rascunho.observacao,
            descricao: rascunho.observacao || 'Sem descrição',
            itens: rascunho.itens || [],
            isRascunho: true,
            isFinalizado: rascunho.status === 'FINALIZADO'
          }
        })

        // Combinar pedidos e rascunhos
        pedidos.value = [...listaPedidos, ...rascunhosFormatados]

      } catch (error) {
        logger.error('❌ Erro ao carregar pedidos do backend:', error)
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
        // Novos status do workflow
        'EM_NEGOCIACAO': 'Em Negociação',
        'PENDENTE_APROVACAO': 'Pendente de Aprovação',
        'APROVADO': 'Aprovado',
        'CANCELADO': 'Cancelado',
        // Status do backend (uppercase)
        'RASCUNHO': 'Rascunho',
        'ATIVO': 'Ativo',
        'EM_COTACAO': 'Em Cotação',
        'FINALIZADO': 'Rascunho Finalizado',
        'RASCUNHO_FINALIZADO': 'Rascunho Finalizado',
        'PENDENTE': 'Pendente',
        'EM_ANALISE': 'Em Análise',
        'EM_ANDAMENTO': 'Em Andamento',
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
        // Novos status do workflow
        'EM_NEGOCIACAO': 'status-negotiating',
        'PENDENTE_APROVACAO': 'status-pending-approval',
        'APROVADO': 'status-approved',
        'CANCELADO': 'status-canceled',
        // Status do backend (uppercase)
        'RASCUNHO': 'status-draft',
        'ATIVO': 'status-active',
        'EM_COTACAO': 'status-quoting',
        'FINALIZADO': 'status-draft-finished',
        'RASCUNHO_FINALIZADO': 'status-draft-finished',
        'PENDENTE': 'status-pending',
        'EM_ANALISE': 'status-progress',
        'EM_ANDAMENTO': 'status-progress',
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
      router.push('/pedidos/novo')
    }

    const editarPedido = async (pedido) => {
      try {

        // Verificar se é um rascunho finalizado - não permitir edição
        if (pedido.isRascunho && pedido.isFinalizado) {
          warning('Este rascunho já foi convertido em pedido e não pode ser editado.')
          return
        }

        // Verificar se é um rascunho - redirecionar para o wizard de rascunho
        if (pedido.isRascunho) {
          // Determinar o estado baseado no status do rascunho
          // ATIVO = edição de itens, EM_COTACAO = gerenciar cotações
          const state = pedido.statusRascunho === 'EM_COTACAO' ? 'quotes' : 'edit'
          router.push(`/pedidos/novo/${pedido.rascunhoId}?state=${state}`)
          return
        }

        // Para pedidos normais (PENDENTE, etc.) - redirecionar para view de visualização/edição
        router.push(`/pedidos/visualizar/${pedido.id}?mode=edit`)
      } catch (error) {
        logger.error('Erro ao redirecionar para edição:', error)
        toastError('Erro ao abrir pedido para edição.')
      }
    }

    const visualizarPedido = (pedido) => {

      // Verificar se é um rascunho
      if (pedido.isRascunho) {
        // Navegar para a view de visualização de rascunho
        router.push({
          path: `/pedidos/visualizar/${pedido.rascunhoId}`,
          query: { tipo: 'rascunho' }
        })
      } else {
        // Navegar para a view de visualização de pedido
        router.push({
          path: `/pedidos/visualizar/${pedido.id}`,
          query: { tipo: 'pedido' }
        })
      }
    }

    const fecharDetalhes = () => {
      showDetalhesModal.value = false
      pedidoSelecionado.value = null
    }

    // Funções do visualizador de PDF inline
    const togglePdfViewer = async (cotacaoId) => {
      // Se o PDF já está aberto, fechar
      if (pdfAberto.value === cotacaoId) {
        fecharPdfViewer()
        return
      }

      // Fechar PDF anterior se houver
      if (pdfAberto.value !== null) {
        fecharPdfViewer()
      }

      // Abrir novo PDF
      pdfAberto.value = cotacaoId
      carregandoPdf.value = true
      erroPdf.value = null
      pdfUrl.value = null

      try {
        // Buscar o PDF do backend - verificar se é rascunho ou pedido
        let response
        if (pedidoSelecionado.value?.isRascunho) {
          response = await cotacaoRascunhoService.obterPorId(
            pedidoSelecionado.value.rascunhoId,
            cotacaoId
          )
        } else {
          response = await cotacaoService.buscarPorId(cotacaoId)
        }

        if (response && response.anexoPdf) {

          // Tentar diferentes conversões
          let byteArray

          if (Array.isArray(response.anexoPdf)) {
            byteArray = new Uint8Array(response.anexoPdf)
          } else if (response.anexoPdf instanceof Uint8Array) {
            byteArray = response.anexoPdf
          } else if (response.anexoPdf instanceof ArrayBuffer) {
            byteArray = new Uint8Array(response.anexoPdf)
          } else if (typeof response.anexoPdf === 'string') {
            // Pode ser base64
            try {
              const binaryString = atob(response.anexoPdf)
              byteArray = new Uint8Array(binaryString.length)
              for (let i = 0; i < binaryString.length; i++) {
                byteArray[i] = binaryString.charCodeAt(i)
              }
            } catch (e) {
              logger.error('Falha ao decodificar base64:', e)
              throw new Error('Formato de PDF inválido (string não base64)')
            }
          } else if (typeof response.anexoPdf === 'object') {
            // Pode ser um objeto com propriedades numéricas
            const keys = Object.keys(response.anexoPdf)
            byteArray = new Uint8Array(keys.length)
            keys.forEach((key, index) => {
              byteArray[index] = response.anexoPdf[key]
            })
          } else {
            logger.error('❌ Tipo desconhecido:', typeof response.anexoPdf)
            throw new Error(`Formato de PDF inválido (tipo: ${typeof response.anexoPdf})`)
          }


          // Verificar se parece um PDF válido (começa com %PDF)
          const pdfHeader = String.fromCharCode(byteArray[0], byteArray[1], byteArray[2], byteArray[3])

          if (!pdfHeader.includes('%PDF') && byteArray.length > 0) {
            logger.warn('⚠️ Arquivo não parece ser um PDF válido!')
          }

          // Criar Blob com tipo MIME correto
          const blob = new Blob([byteArray], { type: 'application/pdf' })

          // Criar URL do blob para o iframe
          const url = URL.createObjectURL(blob)
          pdfUrl.value = url
        } else if (response && response.caminhoAnexo) {
          // Se houver caminho de anexo, usar URL direta
          pdfUrl.value = response.caminhoAnexo
        } else {
          logger.error('❌ Nenhum PDF encontrado')
          throw new Error('Nenhum PDF encontrado para esta cotação')
        }
      } catch (error) {
        logger.error('❌ Erro ao carregar PDF:', error)
        logger.error('Stack:', error.stack)
        erroPdf.value = error.message || 'Erro ao carregar PDF. Tente novamente.'
      } finally {
        carregandoPdf.value = false
      }
    }

    const fecharPdfViewer = () => {
      // Limpar URL do blob para liberar memória
      if (pdfUrl.value && pdfUrl.value.startsWith('blob:')) {
        URL.revokeObjectURL(pdfUrl.value)
      }

      pdfAberto.value = null
      carregandoPdf.value = false
      erroPdf.value = null
      pdfUrl.value = null
    }

    const confirmarExclusao = (pedido) => {
      pedidoParaExcluir.value = pedido
      showConfirmModal.value = true
    }

    const excluirPedido = async () => {
      try {
        if (pedidoParaExcluir.value?.id) {
          const id = pedidoParaExcluir.value.id

          // Verificar se é um rascunho (ID começa com "R-")
          if (typeof id === 'string' && id.startsWith('R-')) {
            // Extrair o ID numérico do rascunho
            const rascunhoId = parseInt(id.replace('R-', ''))
            await rascunhoService.remover(rascunhoId)
          } else {
            // É um pedido normal
            await pedidoService.excluir(id)
          }

          const index = pedidos.value.findIndex(p => p.id === id)
          if (index !== -1) {
            pedidos.value.splice(index, 1)
          }
        }
      } catch (error) {
        logger.error('Erro ao excluir:', error)
        toastError('Erro ao excluir. Tente novamente.')
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

        success('Pedido aprovado com sucesso!')
      } catch (error) {
        logger.error('Erro ao aprovar pedido:', error)
        toastError('Erro ao aprovar pedido. Tente novamente.')
      } finally {
        isLoading.value = false
      }
    }

    // Métodos de permissão
    const podeEditar = (pedido) => {
      // Permite editar rascunhos ATIVOS, EM_COTACAO ou pedidos PENDENTES
      if (pedido.isFinalizado) {
        return false
      }
      return ['RASCUNHO', 'EM_COTACAO', 'PENDENTE'].includes(pedido.status)
    }

    const podeAprovar = (pedido) => {
      // Só pode aprovar pedidos pendentes ou em análise
      return ['PENDENTE', 'EM_ANALISE'].includes(pedido.status)
    }

    const podeExcluir = (pedido) => {
      // Pode excluir pedidos rascunho (não finalizados) ou pendentes
      if (pedido.isFinalizado) {
        return false
      }
      return ['RASCUNHO', 'EM_COTACAO', 'PENDENTE'].includes(pedido.status)
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
      const { showWarning } = useErrorModal()

      showWarning(
        `Tem certeza que deseja alterar o status do pedido #${pedido.id} para "${getStatusLabel(novoStatus)}"?`,
        {
          title: 'Alterar Status do Pedido?',
          confirmText: 'Sim, alterar',
          cancelText: 'Cancelar',
          onConfirm: async () => {
            try {
              isLoading.value = true

              await pedidoService.alterarStatus(pedido.id, novoStatus)

              const index = pedidos.value.findIndex(p => p.id === pedido.id)
              if (index !== -1) {
                pedidos.value[index].status = novoStatus
              }

              success(`Status do pedido alterado para "${getStatusLabel(novoStatus)}" com sucesso!`)
            } catch (error) {
              logger.error('Erro ao alterar status do pedido:', error)
              toastError('Erro ao alterar status do pedido. Tente novamente.')
            } finally {
              isLoading.value = false
            }
          },
          onCancel: () => {
            // Reset do select
            const selectElement = document.querySelector(`select[value="${novoStatus}"]`)
            if (selectElement) {
              selectElement.value = pedido.status
            }
          }
        }
      )
    }

    // Lifecycle
    onMounted(() => {
      carregarPedidos()

      // Verificar se deve redirecionar para novo pedido (ação rápida do dashboard)
      if (route.query.action === 'novo-pedido') {
        router.push('/pedidos/novo')
      }
    })

    return {
      // Estados
      pedidos,
      isLoading,
      searchQuery,
      filtroStatus,
      filtroPeriodo,

      // Modais
      showConfirmModal,
      showDetalhesModal,
      pedidoParaExcluir,
      pedidoSelecionado,

      // Estados para itens
      carregandoItens,

      // Visualizador de PDF inline
      pdfAberto,
      carregandoPdf,
      erroPdf,
      pdfUrl,
      togglePdfViewer,
      fecharPdfViewer,

      // Computed
      totalPedidos,
      novosPedidosMes,
      pedidosPendentes,
      pedidosAprovados,
      percentualPendentes,
      // valorTotalFormatado, // REMOVIDO - valor simulado
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
      fecharDetalhes,
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
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 8px !important;
  background: #3b82f6 !important;
  color: white !important;
  border: none !important;
  padding: 12px 24px !important;
  border-radius: 8px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  font-family: Arial, sans-serif !important;
  cursor: pointer !important;
  transition: all 0.2s !important;
  white-space: nowrap !important;
  min-height: 44px !important;
  min-width: 180px !important;
  line-height: 1.5 !important;
}

.action-button.secondary {
  background: #6b7280;
}

.action-button.secondary:hover {
  background: #4b5563;
}

.action-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.action-button:disabled:hover {
  transform: none;
}

.action-icon {
  flex-shrink: 0;
}

.action-button:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.loading-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
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
  table-layout: fixed;
}

/* Larguras específicas das colunas */
.pedidos-table .col-pedido {
  width: 100px;
}

.pedidos-table .col-status {
  width: 200px;
}

.pedidos-table .col-data {
  width: 140px;
}

.pedidos-table .col-acoes {
  width: auto;
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

.pedidos-table th.col-acoes {
  text-align: right;
  padding: 16px;
}

.pedidos-table td {
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
}

.pedidos-table td.actions-td {
  text-align: right;
  padding: 16px;
}

.table-row {
  transition: all 0.2s ease;
}

.table-row:hover {
  background-color: #f9fafb;
}

/* CÃ©lulas da tabela */
.pedido-cell {
  padding-right: 8px !important;
}

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

.data-cell {
  padding-right: 12px !important;
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
  display: inline-block;
  white-space: nowrap;
}

.status-draft {
  background: #f3f4f6;
  color: #374151;
}

.status-quoting {
  background: #fef3c7;
  color: #b45309;
}

.status-draft-finished {
  background: #e0e7ff;
  color: #3730a3;
}

/* Novos status do workflow */
.status-negotiating {
  background: #dbeafe;
  color: #1d4ed8;
  font-weight: 600;
}

.status-pending-approval {
  background: #fef3c7;
  color: #d97706;
  font-weight: 600;
}

.status-active {
  background: #d1fae5;
  color: #047857;
  font-weight: 600;
}

/* Status antigos */
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
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: flex-end;
  width: 100%;
}

.action-btn {
  background: none;
  border: 1px solid #e5e7eb;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  color: #6b7280;
  flex-shrink: 0;
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

.action-btn.approve:hover {
  background: #d1fae5;
  color: #059669;
  border-color: #10b981;
}

.action-btn.delete:hover {
  background: #fee2e2;
  color: #dc2626;
  border-color: #f87171;
}

/* Ocultar botões mas manter o espaço */
.btn-hidden {
  visibility: hidden;
  pointer-events: none;
}

/* Status Actions */
.status-actions {
  position: relative;
  flex-shrink: 0;
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
  width: 120px;
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

/* Seções de Detalhes - Padrão do Projeto */
.detalhe-section {
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.detalhe-section:last-child {
  border-bottom: none;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #111827;
  margin: 0 0 16px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.detalhes-body {
  overflow-y: auto;
  max-height: calc(90vh - 80px);
}

/* Info Grid - Layout de Informações */
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #6b7280;
}

.info-value {
  font-size: 0.9375rem;
  color: #111827;
  font-weight: 500;
}

.info-value.highlight {
  color: #3b82f6;
  font-weight: 600;
}

/* Header do Modal */
.header-title-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* Lista de Itens */
.itens-lista {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.item-detalhado {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
}

.item-header-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.item-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.item-nome {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: #111827;
}

/* Cotações Subsection */
.cotacoes-subsection {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 2px solid #e5e7eb;
}

.subsection-title {
  font-size: 1rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 16px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.cotacoes-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cotacao-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.cotacao-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  transition: all 0.2s;
}

.cotacao-card:hover {
  border-color: #d1d5db;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* Informações do Fornecedor */
.cotacao-fornecedor-info {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
}

.fornecedor-detalhes {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.fornecedor-nome {
  font-size: 0.9375rem;
  font-weight: 600;
  color: #111827;
}

.fornecedor-id {
  font-size: 0.75rem;
  font-weight: 500;
  color: #6b7280;
}

/* Valores da Cotação */
.cotacao-valores-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 12px;
}

.valor-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.valor-label {
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #6b7280;
}

.valor-preco {
  font-size: 1.125rem;
  font-weight: 700;
  color: #10b981;
}

.valor-text {
  font-size: 0.875rem;
  color: #374151;
  font-weight: 500;
}

/* Ações da Cotação */
.cotacao-acao {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding-top: 4px;
}

.btn-ver-pdf-small {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 0.8125rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-ver-pdf-small:hover {
  background: #2563eb;
  box-shadow: 0 2px 6px rgba(59, 130, 246, 0.4);
}

.btn-ver-pdf-small.active {
  background: #dc2626;
}

.btn-ver-pdf-small.active:hover {
  background: #b91c1c;
  box-shadow: 0 2px 6px rgba(220, 38, 38, 0.4);
}

.btn-ver-pdf-small svg {
  flex-shrink: 0;
}

.sem-pdf {
  font-size: 0.8125rem;
  color: #9ca3af;
  font-style: italic;
}

/* Container do PDF */
.pdf-container {
  width: 100%;
  background: white;
  border: 2px solid #3b82f6;
  border-top: none;
  border-radius: 0 0 8px 8px;
  overflow: hidden;
  margin-top: -8px;
}

.pdf-iframe {
  width: 100%;
  height: 600px;
  border: none;
  display: block;
}

/* Estados de Loading e Erro */
.loading-state,
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px 20px;
  text-align: center;
  color: #6b7280;
}

.loading-spinner-small {
  width: 32px;
  height: 32px;
  border: 3px solid #e5e7eb;
  border-top: 3px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.error-state svg {
  color: #dc2626;
}

.error-state span {
  font-size: 0.875rem;
}

/* Sem Cotações */
.no-cotacoes {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 32px 20px;
  color: #9ca3af;
  font-size: 0.875rem;
  font-style: italic;
  text-align: center;
  background: #f9fafb;
  border-radius: 8px;
  margin-top: 12px;
}

.no-cotacoes svg {
  opacity: 0.5;
}

/* Responsividade */
@media (max-width: 1024px) {
  .main-content {
    margin-left: 0;
  }

  .welcome-header {
    flex-direction: column;
    align-items: stretch;
    text-align: center;
    gap: 16px;
  }

  .action-buttons {
    justify-content: center;
  }

  .search-container {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .search-input-container {
    min-width: auto;
    width: 100%;
  }

  .search-actions {
    justify-content: stretch;
    flex-direction: column;
  }

  .form-select {
    flex: 1;
    min-width: auto;
    width: 100%;
  }

  .metrics-grid {
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  }

  .pedidos-table .col-status {
    width: 180px;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 12px;
  }

  .welcome-content h1 {
    font-size: 1.75rem;
  }

  .welcome-subtitle {
    font-size: 0.875rem;
  }

  .action-button {
    padding: 10px 18px;
    font-size: 0.875rem;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .metric-card {
    padding: 16px;
  }

  .metric-value {
    font-size: 2rem;
  }

  .table-container {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }

  .pedidos-table {
    min-width: 650px;
  }

  .pedidos-table .col-pedido {
    width: 80px;
  }

  .pedidos-table .col-status {
    width: 160px;
  }

  .pedidos-table .col-data {
    width: 120px;
  }

  .pedidos-table th,
  .pedidos-table td {
    padding: 12px 8px;
    font-size: 0.8125rem;
  }

  .status-badge {
    padding: 3px 10px;
    font-size: 0.6875rem;
  }

  .action-btn {
    padding: 6px;
  }

  .detalhes-modal {
    margin: 1rem;
    max-width: none;
    width: calc(100% - 2rem);
  }

  .detalhes-header h2 {
    font-size: 1.25rem;
  }

  .info-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .cotacao-valores-grid {
    grid-template-columns: 1fr;
  }

  .pdf-iframe {
    height: 400px;
  }
}

@media (max-width: 640px) {
  .main-content {
    padding: 8px;
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

  .detalhe-section {
    padding: 16px;
  }

  .item-detalhado {
    padding: 16px;
  }

  .item-header-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .cotacao-card {
    padding: 12px;
  }

  .btn-ver-pdf-small {
    width: 100%;
    justify-content: center;
  }

  .pdf-iframe {
    height: 300px;
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
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
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

/* Estilos para filtro de itens */
.itens-header-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 16px;
}

.itens-header-section h4 {
  margin: 0;
}

.filtro-itens-container {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.filtro-input-wrapper {
  position: relative;
  flex: 1;
  min-width: 250px;
  max-width: 400px;
}

.filtro-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  pointer-events: none;
}

.filtro-item-input {
  width: 100%;
  padding: 10px 40px 10px 36px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  transition: all 0.2s;
  background: white;
}

.filtro-item-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.filtro-item-input::placeholder {
  color: #9ca3af;
}

.limpar-filtro-btn {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: #f3f4f6;
  border: none;
  border-radius: 4px;
  padding: 4px;
  cursor: pointer;
  color: #6b7280;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.limpar-filtro-btn:hover {
  background: #e5e7eb;
  color: #374151;
}

.filtro-resultado-count {
  font-size: 0.875rem;
  color: #6b7280;
  font-weight: 500;
  white-space: nowrap;
}

.btn-secondary-small {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 12px;
}

.btn-secondary-small:hover {
  background: #e5e7eb;
  border-color: #9ca3af;
}

.empty-hint {
  font-size: 0.875rem;
  color: #9ca3af;
  margin-top: 8px;
  margin-bottom: 16px;
  font-style: italic;
}

/* Destaque nos resultados da busca */
.highlight-match {
  background: linear-gradient(to bottom, transparent 0%, transparent 50%, #fef3c7 50%, #fef3c7 100%);
  padding: 2px 4px;
  border-radius: 3px;
  transition: all 0.2s;
}

@media (max-width: 640px) {
  .filtro-itens-container {
    flex-direction: column;
    align-items: stretch;
  }

  .filtro-input-wrapper {
    max-width: none;
  }

  .filtro-resultado-count {
    text-align: center;
  }
}

/* ===========================================
   NOVAS SEÇÕES - ITENS E COTAÇÕES
   =========================================== */

/* Seção de Itens */
.secao-itens, .secao-cotacoes {
  margin-bottom: 24px;
}

.secao-header {
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e5e7eb;
}

.secao-header h4 {
  font-size: 1.25rem;
  font-weight: 600;
  color: #111827;
  margin: 0 0 4px 0;
}

.secao-subtitle {
  font-size: 0.875rem;
  color: #6b7280;
  font-style: italic;
}

.secao-separador {
  height: 1px;
  background: linear-gradient(to right, transparent, #e5e7eb, transparent);
  margin: 32px 0;
}

/* Lista de Itens Simples */
.itens-simples-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item-simples-card {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 16px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  transition: all 0.2s;
}

.item-simples-card:hover {
  border-color: #d1d5db;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.item-simples-numero {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 32px;
  height: 32px;
  background: #3b82f6;
  color: white;
  border-radius: 50%;
  font-weight: 600;
  font-size: 0.875rem;
}

.item-simples-info {
  flex: 1;
}

.item-simples-nome {
  font-size: 1rem;
  font-weight: 600;
  color: #111827;
  margin-bottom: 6px;
}

.item-simples-detalhes {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 0.875rem;
  color: #6b7280;
}

.item-simples-quantidade {
  font-weight: 500;
  color: #059669;
}

.item-simples-descricao {
  font-style: italic;
}

/* Cotações por Item */
.cotacoes-por-item-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.cotacao-item-card {
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.2s;
}

.cotacao-item-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 4px 6px rgba(59, 130, 246, 0.1);
}

.cotacao-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #1F285F, #2563eb);
  color: white;
}

.cotacao-item-titulo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cotacao-item-numero {
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.cotacao-item-nome {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
}

.cotacao-item-badge {
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 500;
}

/* Lista de Fornecedores */
.cotacoes-fornecedores-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
}

.cotacao-fornecedor-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.cotacao-fornecedor-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  transition: all 0.2s;
}

.cotacao-fornecedor-card:hover {
  background: white;
  border-color: #3b82f6;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.1);
}

.cotacao-fornecedor-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cotacao-fornecedor-header {
  display: flex;
  gap: 12px;
  align-items: center;
}

.cotacao-fornecedor-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1F285F, #2563eb);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.25rem;
  flex-shrink: 0;
}

.cotacao-fornecedor-dados {
  flex: 1;
}

.cotacao-fornecedor-nome {
  font-size: 1rem;
  font-weight: 600;
  color: #111827;
  margin-bottom: 2px;
}

.cotacao-fornecedor-id {
  font-size: 0.75rem;
  color: #6b7280;
}

.cotacao-fornecedor-valores {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.cotacao-valor-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.cotacao-label {
  font-size: 0.75rem;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 500;
}

.cotacao-preco {
  font-size: 1.125rem;
  font-weight: 700;
  color: #059669;
}

.cotacao-prazo-texto, .cotacao-validade {
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
}

.cotacao-acoes {
  display: flex;
  align-items: center;
}

.btn-ver-pdf {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-ver-pdf:hover {
  background: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(59, 130, 246, 0.2);
}

.btn-ver-pdf.active {
  background: #dc2626;
}

.btn-ver-pdf.active:hover {
  background: #b91c1c;
}

.btn-ver-pdf svg {
  flex-shrink: 0;
}

/* Visualizador de PDF Inline */
.pdf-viewer-inline {
  width: 100%;
  background: white;
  border: 2px solid #3b82f6;
  border-top: none;
  border-radius: 0 0 8px 8px;
  overflow: hidden;
}

.pdf-iframe-inline {
  width: 100%;
  height: 600px;
  border: none;
  display: block;
}

.loading-pdf-inline,
.error-pdf-inline {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px 20px;
  text-align: center;
  min-height: 200px;
}

.loading-pdf-inline .loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e5e7eb;
  border-top: 4px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.error-pdf-inline svg {
  color: #dc2626;
}

.error-pdf-inline p {
  color: #6b7280;
  margin: 0;
  font-size: 0.875rem;
}

.sem-pdf-texto {
  font-size: 0.875rem;
  color: #9ca3af;
  font-style: italic;
}

.empty-cotacoes-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #9ca3af;
  text-align: center;
}

.empty-cotacoes-message svg {
  margin-bottom: 12px;
  opacity: 0.5;
}

.empty-cotacoes-message p {
  margin: 0;
  font-size: 0.875rem;
  font-style: italic;
}

/* Animação para loading spinner */
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Responsive */
@media (max-width: 768px) {
  .cotacao-fornecedor-card {
    flex-direction: column;
    align-items: stretch;
  }

  .cotacao-fornecedor-valores {
    gap: 12px;
  }

  .cotacao-acoes {
    justify-content: stretch;
  }

  .btn-ver-pdf {
    width: 100%;
    justify-content: center;
  }

  .item-simples-card {
    flex-direction: column;
  }

  .item-simples-numero {
    align-self: flex-start;
  }

  .pdf-iframe-inline {
    height: 400px;
  }
}
</style>
