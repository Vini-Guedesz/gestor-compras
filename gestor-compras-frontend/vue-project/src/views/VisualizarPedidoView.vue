<template>
  <div class="page-container">
    <DashboardHeader @toggle-sidebar="toggleSidebar" />

    <div class="main-content">
      <DashboardSidebar :isOpen="isSidebarOpen" @close="closeSidebar" />

      <div class="content-area">
        <!-- Breadcrumb -->
        <div class="breadcrumb">
          <button @click="voltar" class="btn-voltar">
            <svg viewBox="0 0 24 24" width="18" height="18">
              <path fill="currentColor" d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"/>
            </svg>
            Voltar
          </button>
          <span class="breadcrumb-separator">|</span>
          <router-link to="/pedidos" class="breadcrumb-link">
            Pedidos de Compra
          </router-link>
          <span class="breadcrumb-separator">/</span>
          <span class="breadcrumb-current">{{ isRascunho ? 'Visualizar Rascunho' : 'Visualizar Pedido' }}</span>
        </div>

        <!-- Loading State -->
        <div v-if="isLoading" class="loading-container">
          <div class="loading-spinner"></div>
          <span>Carregando...</span>
        </div>

        <!-- Conteúdo Unificado -->
        <div v-else class="view-container">
          <!-- Header -->
          <div class="view-header">
            <div class="header-content">
              <h2 class="view-title">{{ getTitulo() }}</h2>
              <p class="view-subtitle">Detalhes completos do {{ isRascunho ? 'rascunho' : 'pedido' }}</p>
            </div>
            <div class="header-actions">
              <span class="status-badge" :class="getStatusClass()">
                {{ pedido?.status || 'RASCUNHO' }}
              </span>
              <button v-if="isRascunho" @click="editarRascunho" class="btn-success">
                Editar Rascunho
              </button>
            </div>
          </div>

          <!-- Seção: Informações Gerais -->
          <div class="section-card">
            <h3 class="section-title">Informações Gerais</h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">ID:</span>
                <span class="info-value">#{{ isRascunho ? pedido?.rascunhoId : pedido?.id }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Data de Criação:</span>
                <span class="info-value">{{ formatarData(pedido?.dataCriacao) }}</span>
              </div>
              <div class="info-item" v-if="pedido?.observacao">
                <span class="info-label">Observação:</span>
                <span class="info-value">{{ pedido.observacao }}</span>
              </div>
            </div>
          </div>

          <!-- Seção: Itens do Pedido -->
          <div class="section-card">
            <h3 class="section-title">Itens do Pedido</h3>
            <div class="itens-lista">
              <div v-for="(item, index) in pedido?.itens" :key="item.id" class="item-card">
                <div class="item-header">
                  <span class="item-numero">Item {{ index + 1 }}</span>
                  <span class="item-status" :class="{ 'cotado': itemTemCotacao(item) }">
                    {{ itemTemCotacao(item) ? 'Cotado' : 'Sem cotação' }}
                  </span>
                </div>
                <div class="item-body">
                  <div class="item-nome">{{ item.nome }}</div>
                  <div class="item-detalhes">
                    <span>Quantidade: {{ item.quantidade }}</span>
                    <span v-if="item.descricao">{{ item.descricao }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-if="!pedido?.itens?.length" class="empty-state-small">
              <p>Nenhum item cadastrado</p>
            </div>
          </div>

          <!-- Seção: Cotações -->
          <div class="section-card">
            <h3 class="section-title">Cotações</h3>
            <div v-if="cotacoes.length > 0" class="cotacoes-lista">
              <div v-for="cotacao in cotacoes" :key="cotacao.id" class="cotacao-card">
                <div class="cotacao-header">
                  <div class="cotacao-fornecedor">
                    <strong>{{ cotacao.nomeFornecedor || cotacao.fornecedorNome || 'Fornecedor' }}</strong>
                    <span class="cotacao-tipo">{{ cotacao.tipoFornecedor }}</span>
                  </div>
                  <div class="cotacao-preco">
                    R$ {{ formatarPreco(cotacao.preco) }}
                  </div>
                </div>

                <div class="cotacao-body">
                  <div class="cotacao-info">
                    <span v-if="cotacao.prazoEmDiasUteis">
                      Prazo: {{ cotacao.prazoEmDiasUteis }} dias úteis
                    </span>
                    <span v-if="cotacao.dataLimite">
                      Validade: {{ formatarData(cotacao.dataLimite) }}
                    </span>
                  </div>

                  <div class="cotacao-itens">
                    <span class="itens-label">Itens contemplados:</span>
                    <div class="itens-tags">
                      <span
                        v-for="itemId in getItensIdsDaCotacao(cotacao)"
                        :key="itemId"
                        class="item-tag"
                      >
                        {{ getNomeItem(itemId) }}
                      </span>
                    </div>
                  </div>
                </div>

                <div class="cotacao-actions" v-if="cotacao.temAnexoPdf">
                  <button @click="togglePdfViewer(cotacao)" class="btn-pdf">
                    {{ pdfAberto === cotacao.id ? 'Fechar PDF' : 'Ver PDF' }}
                  </button>
                </div>

                <!-- PDF Viewer -->
                <div v-if="pdfAberto === cotacao.id" class="pdf-viewer-container">
                  <div v-if="carregandoPdf" class="pdf-loading">
                    <div class="loading-spinner"></div>
                    <span>Carregando PDF...</span>
                  </div>
                  <div v-else-if="pdfUrl" class="pdf-viewer-wrapper">
                    <iframe :src="pdfUrl" class="pdf-iframe" frameborder="0"></iframe>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="empty-state-small">
              <p>Nenhuma cotação registrada</p>
            </div>
          </div>

          <!-- Seção: Histórico -->
          <div class="section-card">
            <h3 class="section-title">Histórico de Modificações</h3>
            <div v-if="historico.length > 0" class="historico-lista">
              <div v-for="registro in historico" :key="registro.id" class="historico-item">
                <div class="historico-icon" :style="{ background: getTipoModificacaoColor(registro.tipoModificacao) }">
                  <svg viewBox="0 0 24 24" width="16" height="16">
                    <path fill="white" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                  </svg>
                </div>
                <div class="historico-content">
                  <div class="historico-header">
                    <span class="historico-tipo">{{ getTipoModificacaoLabel(registro.tipoModificacao) }}</span>
                    <span class="historico-data">{{ formatarDataHora(registro.dataModificacao) }}</span>
                  </div>
                  <div class="historico-usuario" v-if="registro.usuarioNome">
                    Por: {{ registro.usuarioNome }}
                  </div>
                  <div class="historico-descricao" v-if="registro.descricao">
                    {{ registro.descricao }}
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="empty-state-small">
              <p>Nenhum registro de histórico</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import pedidoService from '@/services/pedidoService.js'
import rascunhoService from '@/services/rascunhoService.js'
import cotacaoRascunhoService from '@/services/cotacaoRascunhoService.js'
import fornecedorService from '@/services/fornecedorService.js'
import historicoPedidoService, { tipoModificacaoConfig } from '@/services/historicoPedidoService.js'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'

export default {
  name: 'VisualizarPedidoView',
  components: {
    DashboardHeader,
    DashboardSidebar
  },
  setup() {
    const router = useRouter()
    const route = useRoute()

    // Sidebar
    const isSidebarOpen = ref(false)
    const toggleSidebar = () => { isSidebarOpen.value = !isSidebarOpen.value }
    const closeSidebar = () => { isSidebarOpen.value = false }

    // State
    const isLoading = ref(true)
    const pedido = ref(null)
    const cotacoes = ref([])
    const historico = ref([])
    const isRascunho = ref(false)

    // PDF Viewer
    const pdfAberto = ref(null)
    const carregandoPdf = ref(false)
    const pdfUrl = ref(null)

    const getTitulo = () => {
      if (isRascunho.value) {
        return `Rascunho #${pedido.value?.rascunhoId || ''}`
      }
      return `Pedido #${pedido.value?.id || ''}`
    }

    const getStatusClass = () => {
      const status = pedido.value?.status || 'RASCUNHO'
      return {
        'status-pendente': status === 'PENDENTE',
        'status-aprovado': status === 'APROVADO',
        'status-rejeitado': status === 'REJEITADO' || status === 'CANCELADO',
        'status-rascunho': status === 'RASCUNHO'
      }
    }

    const formatarData = (data) => {
      if (!data) return 'N/A'
      return new Date(data).toLocaleDateString('pt-BR')
    }

    const formatarDataHora = (data) => {
      if (!data) return 'N/A'
      return new Date(data).toLocaleString('pt-BR')
    }

    const formatarPreco = (preco) => {
      if (!preco) return '0,00'
      return parseFloat(preco).toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }

    const itemTemCotacao = (item) => {
      return cotacoes.value.some(c => {
        // Para rascunhos: verificar itensRascunhoIds
        if (c.itensRascunhoIds && c.itensRascunhoIds.length > 0) {
          return c.itensRascunhoIds.includes(item.id)
        }
        // Para pedidos: verificar itemPedidoId
        if (c.itemPedidoId) {
          return c.itemPedidoId === item.id
        }
        // Também verificar se a cotação está diretamente no item
        if (item.cotacoes && item.cotacoes.length > 0) {
          return true
        }
        return false
      })
    }

    const getItensIdsDaCotacao = (cotacao) => {
      // Para rascunhos
      if (cotacao.itensRascunhoIds && cotacao.itensRascunhoIds.length > 0) {
        return cotacao.itensRascunhoIds
      }
      // Para pedidos
      if (cotacao.itemPedidoId) {
        return [cotacao.itemPedidoId]
      }
      return []
    }

    const getNomeItem = (itemId) => {
      const item = pedido.value?.itens?.find(i => i.id === itemId)
      return item ? item.nome : `Item #${itemId}`
    }

    const getTipoModificacaoLabel = (tipo) => {
      return tipoModificacaoConfig[tipo]?.label || tipo
    }

    const getTipoModificacaoColor = (tipo) => {
      return tipoModificacaoConfig[tipo]?.color || '#6b7280'
    }

    const togglePdfViewer = async (cotacao) => {
      if (pdfAberto.value === cotacao.id) {
        fecharPdfViewer()
        return
      }

      if (pdfAberto.value !== null) {
        fecharPdfViewer()
      }

      pdfAberto.value = cotacao.id
      carregandoPdf.value = true

      try {
        const blob = await cotacaoRascunhoService.obterAnexoPdf(
          pedido.value.rascunhoId || pedido.value.id,
          cotacao.id
        )
        if (blob && blob.size > 0) {
          pdfUrl.value = URL.createObjectURL(blob)
        }
      } catch (error) {
        console.error('Erro ao carregar PDF:', error)
      } finally {
        carregandoPdf.value = false
      }
    }

    const fecharPdfViewer = () => {
      if (pdfUrl.value && pdfUrl.value.startsWith('blob:')) {
        URL.revokeObjectURL(pdfUrl.value)
      }
      pdfAberto.value = null
      pdfUrl.value = null
    }

    const carregarPedido = async () => {
      const id = route.params.id
      const tipo = route.query.tipo || 'pedido'

      try {
        isLoading.value = true

        if (tipo === 'rascunho') {
          isRascunho.value = true

          // Carregar rascunho
          const rascunhoCompleto = await rascunhoService.obterPorId(id)

          // Carregar cotações
          const cotacoesRascunho = await cotacaoRascunhoService.listarPorRascunho(id)

          // Carregar histórico
          const historicoRascunho = await rascunhoService.listarHistorico(id)

          // Carregar fornecedores para nomes
          const [fornecedoresProduto, fornecedoresServico] = await Promise.all([
            fornecedorService.listarProdutos().catch(() => []),
            fornecedorService.listarServicos().catch(() => [])
          ])

          const todosFornecedores = [...fornecedoresProduto, ...fornecedoresServico]
          const fornecedoresMap = {}
          todosFornecedores.forEach(f => {
            if (f?.id) fornecedoresMap[f.id] = f.razaoSocial || 'Fornecedor'
          })

          // Mapear nomes dos fornecedores nas cotações
          cotacoes.value = cotacoesRascunho.map(c => ({
            ...c,
            nomeFornecedor: fornecedoresMap[c.fornecedorId] || `Fornecedor #${c.fornecedorId}`
          }))

          pedido.value = {
            ...rascunhoCompleto,
            rascunhoId: rascunhoCompleto.id,
            status: 'RASCUNHO'
          }

          historico.value = historicoRascunho || []
        } else {
          isRascunho.value = false

          // Carregar pedido
          const pedidoCompleto = await pedidoService.obterPorId(id)
          pedido.value = pedidoCompleto

          // Carregar histórico
          const historicoPedido = await historicoPedidoService.listarPorPedido(id)
          historico.value = historicoPedido || []

          // Extrair cotações dos itens
          const todasCotacoes = []
          pedidoCompleto.itens?.forEach(item => {
            if (item.cotacoes) {
              item.cotacoes.forEach(c => {
                todasCotacoes.push({
                  ...c,
                  itemPedidoId: item.id
                })
              })
            }
          })
          cotacoes.value = todasCotacoes
        }
      } catch (error) {
        console.error('Erro ao carregar:', error)
        alert('Erro ao carregar dados. Redirecionando...')
        router.push('/pedidos')
      } finally {
        isLoading.value = false
      }
    }

    const voltar = () => {
      router.push('/pedidos')
    }

    const editarRascunho = () => {
      router.push(`/pedidos/novo/${pedido.value.rascunhoId}?step=1`)
    }

    onMounted(() => {
      carregarPedido()
    })

    onBeforeUnmount(() => {
      fecharPdfViewer()
    })

    return {
      // Sidebar
      isSidebarOpen,
      toggleSidebar,
      closeSidebar,

      // State
      isLoading,
      pedido,
      cotacoes,
      historico,
      isRascunho,

      // PDF
      pdfAberto,
      carregandoPdf,
      pdfUrl,

      // Methods
      getTitulo,
      getStatusClass,
      formatarData,
      formatarDataHora,
      getItensIdsDaCotacao,
      formatarPreco,
      itemTemCotacao,
      getNomeItem,
      getTipoModificacaoLabel,
      getTipoModificacaoColor,
      togglePdfViewer,
      voltar,
      editarRascunho
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.main-content {
  display: flex;
  padding-top: 64px;
}

.content-area {
  flex: 1;
  padding: 24px;
  margin-left: 250px;
}

/* Loading */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  gap: 16px;
  color: #6b7280;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e5e7eb;
  border-top-color: #1F285F;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Breadcrumb */
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  font-size: 0.875rem;
}

.btn-voltar {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-voltar:hover {
  background: #e5e7eb;
}

.breadcrumb-link {
  color: #1F285F;
  text-decoration: none;
  font-weight: 500;
}

.breadcrumb-separator {
  color: #d1d5db;
}

.breadcrumb-current {
  color: #6b7280;
}

/* View Container */
.view-container {
  max-width: 1000px;
}

.view-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  background: #1F285F;
  color: white;
  border-radius: 16px 16px 0 0;
  margin-bottom: 0;
}

.view-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.view-subtitle {
  font-size: 0.875rem;
  opacity: 0.9;
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.status-pendente {
  background: #fef3c7;
  color: #92400e;
}

.status-aprovado {
  background: #d1fae5;
  color: #065f46;
}

.status-rejeitado {
  background: #fee2e2;
  color: #991b1b;
}

.status-rascunho {
  background: #e0e7ff;
  color: #3730a3;
}

/* Section Cards */
.section-card {
  background: white;
  padding: 24px 28px;
  border-left: 1px solid #e5e7eb;
  border-right: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
}

.section-card:last-child {
  border-radius: 0 0 16px 16px;
}

/* Buttons */
.btn-secondary {
  padding: 10px 20px;
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: #e5e7eb;
}

.btn-primary {
  padding: 10px 20px;
  background: #1F285F;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-primary:hover {
  background: #2d3a7f;
}

.btn-success {
  padding: 10px 20px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-success:hover {
  background: #059669;
}

/* Step 1 - Dados */
.info-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e5e7eb;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 500;
  text-transform: uppercase;
}

.info-value {
  font-size: 0.875rem;
  color: #1e293b;
  font-weight: 500;
}

.itens-lista {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item-card {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  background: #f3f4f6;
  border-bottom: 1px solid #e5e7eb;
}

.item-numero {
  font-weight: 600;
  font-size: 0.875rem;
  color: #374151;
}

.item-status {
  font-size: 0.75rem;
  padding: 4px 8px;
  border-radius: 4px;
  background: #fef3c7;
  color: #92400e;
}

.item-status.cotado {
  background: #d1fae5;
  color: #065f46;
}

.item-body {
  padding: 12px 16px;
}

.item-nome {
  font-weight: 500;
  color: #1e293b;
  margin-bottom: 4px;
}

.item-detalhes {
  display: flex;
  gap: 16px;
  font-size: 0.8125rem;
  color: #6b7280;
}

/* Step 2 - Cotações */
.cotacoes-lista {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cotacao-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
}

.cotacao-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.cotacao-fornecedor strong {
  display: block;
  font-size: 0.875rem;
  color: #1e293b;
}

.cotacao-tipo {
  font-size: 0.75rem;
  color: #64748b;
  background: #f1f5f9;
  padding: 2px 8px;
  border-radius: 4px;
  margin-top: 4px;
  display: inline-block;
}

.cotacao-preco {
  font-size: 1.125rem;
  font-weight: 600;
  color: #059669;
}

.cotacao-info {
  display: flex;
  gap: 16px;
  font-size: 0.8125rem;
  color: #64748b;
  margin-bottom: 12px;
}

.cotacao-itens {
  margin-bottom: 12px;
}

.itens-label {
  font-size: 0.75rem;
  color: #64748b;
  display: block;
  margin-bottom: 6px;
}

.itens-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.item-tag {
  background: #e0e7ff;
  color: #3730a3;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
}

.cotacao-actions {
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.btn-pdf {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 0.75rem;
  cursor: pointer;
  background: #f0f9ff;
  color: #0369a1;
  border: 1px solid #bae6fd;
}

.pdf-viewer-container {
  margin-top: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.pdf-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 12px;
  color: #6b7280;
}

.pdf-viewer-wrapper {
  height: 450px;
}

.pdf-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

/* Step 3 - Histórico */
.historico-lista {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.historico-item {
  display: flex;
  gap: 12px;
}

.historico-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.historico-content {
  flex: 1;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.historico-item:last-child .historico-content {
  border-bottom: none;
}

.historico-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.historico-tipo {
  font-weight: 600;
  font-size: 0.875rem;
  color: #1e293b;
}

.historico-data {
  font-size: 0.75rem;
  color: #6b7280;
}

.historico-usuario {
  font-size: 0.8125rem;
  color: #6b7280;
  margin-bottom: 4px;
}

.historico-descricao {
  font-size: 0.8125rem;
  color: #374151;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #6b7280;
}

.empty-state p {
  margin: 12px 0 0;
}

.empty-state-small {
  text-align: center;
  padding: 20px;
  color: #9ca3af;
  font-size: 0.875rem;
}

.empty-state-small p {
  margin: 0;
}

/* Responsive */
@media (max-width: 1024px) {
  .content-area {
    margin-left: 0;
  }
}

@media (max-width: 768px) {
  .content-area {
    padding: 16px;
  }

  .view-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
  }

  .section-card {
    padding: 16px;
  }
}
</style>
