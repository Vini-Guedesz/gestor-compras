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
              <button v-if="isRascunho && !isFinalizado" @click="editarRascunho" class="btn-success">
                Editar Rascunho
              </button>
              <button v-if="!isRascunho && podeEditarPedido" @click="editarPedido" class="btn-primary">
                Editar Pedido
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

                <div class="cotacao-actions">
                  <!-- Botões de PDF -->
                  <div class="pdf-buttons" v-if="cotacao.temAnexoPdf || cotacao.quantidadeAnexos > 0">
                    <!-- Múltiplos PDFs -->
                    <template v-if="cotacao.quantidadeAnexos > 1">
                      <button
                        v-for="index in cotacao.quantidadeAnexos"
                        :key="index"
                        @click="togglePdfViewer(cotacao, index - 1)"
                        class="btn-pdf"
                        :class="{ 'btn-pdf-active': pdfAberto === `${cotacao.id}-${index - 1}` }"
                      >
                        {{ pdfAberto === `${cotacao.id}-${index - 1}` ? 'Fechar' : `PDF ${index}` }}
                      </button>
                    </template>
                    <!-- PDF único -->
                    <button
                      v-else
                      @click="togglePdfViewer(cotacao, 0)"
                      class="btn-pdf"
                      :class="{ 'btn-pdf-active': pdfAberto === `${cotacao.id}-0` }"
                    >
                      {{ pdfAberto === `${cotacao.id}-0` ? 'Fechar PDF' : 'Ver PDF' }}
                    </button>
                  </div>

                  <!-- Botão de Excluir (apenas para rascunhos não finalizados) -->
                  <button
                    v-if="isRascunho && !isFinalizado"
                    @click="excluirCotacao(cotacao)"
                    class="btn-delete-cotacao"
                    title="Excluir cotação"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <polyline points="3 6 5 6 21 6"></polyline>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                      <line x1="10" y1="11" x2="10" y2="17"></line>
                      <line x1="14" y1="11" x2="14" y2="17"></line>
                    </svg>
                  </button>
                </div>

                <!-- PDF Viewer -->
                <div v-if="pdfAberto && pdfAberto.startsWith(`${cotacao.id}-`)" class="pdf-viewer-container">
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
                <div class="historico-icon" :style="{ background: getTipoModificacaoColor(registro.tipoAcao || registro.tipoModificacao) }">
                  <svg viewBox="0 0 24 24" width="16" height="16">
                    <path fill="white" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                  </svg>
                </div>
                <div class="historico-content">
                  <div class="historico-header">
                    <span class="historico-tipo">{{ getTipoModificacaoLabel(registro.tipoAcao || registro.tipoModificacao) }}</span>
                    <span class="historico-data">{{ formatarDataHora(registro.dataModificacao) }}</span>
                  </div>
                  <div class="historico-usuario" v-if="registro.nomeUsuario">
                    Por: {{ registro.nomeUsuario }}
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

    <!-- Modal de Edição de Pedido -->
    <div v-if="modalEdicaoAberto" class="modal-overlay" @click.self="fecharModalEdicao">
      <div class="modal-container">
        <div class="modal-header">
          <h3>Editar Pedido #{{ pedido?.id }}</h3>
          <button @click="fecharModalEdicao" class="btn-close">
            <svg viewBox="0 0 24 24" width="20" height="20">
              <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
            </svg>
          </button>
        </div>

        <div class="modal-body">
          <!-- Seleção de Item -->
          <div v-if="!itemSendoEditado" class="form-section">
            <h4>Selecione o item para editar:</h4>
            <div class="itens-selecao">
              <div
                v-for="(item, index) in pedido?.itens"
                :key="item.id"
                @click="abrirEdicaoItem(item)"
                class="item-selecao-card"
              >
                <div class="item-selecao-numero">Item {{ index + 1 }}</div>
                <div class="item-selecao-nome">{{ item.nome }}</div>
                <div class="item-selecao-qtd">Qtd: {{ item.quantidade }}</div>
                <div class="item-selecao-status" :class="{ 'tem-cotacao': itemTemCotacao(item) }">
                  {{ itemTemCotacao(item) ? 'Com cotação' : 'Sem cotação' }}
                </div>
              </div>
            </div>
          </div>

          <!-- Edição do Item -->
          <div v-else class="form-section">
            <button @click="itemSendoEditado = null" class="btn-voltar-item">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"/>
              </svg>
              Voltar para lista de itens
            </button>

            <div class="item-selecionado">
              <h4>Editando: {{ itemSendoEditado.nome }}</h4>
              <p>Quantidade: {{ itemSendoEditado.quantidade }}</p>
            </div>

            <!-- Cotações do Item -->
            <div class="cotacoes-section">
              <div class="section-header-inline">
                <h5>Cotações para este item:</h5>
                <button @click="abrirFormularioNovaCotacao" class="btn-add-small" type="button">
                  + Nova Cotação
                </button>
              </div>

              <!-- Formulário Nova Cotação (inline, similar ao rascunho) -->
              <div v-if="modoNovaCotacao" class="form-nova-cotacao-inline">
                <div class="form-row-2">
                  <div class="form-group">
                    <label>Tipo de Fornecedor *</label>
                    <div class="radio-group-inline">
                      <label class="radio-label-inline">
                        <input
                          type="radio"
                          v-model="novaCotacao.tipoFornecedor"
                          value="PRODUTO"
                          @change="carregarFornecedoresPorTipo"
                        />
                        <span>Produto</span>
                      </label>
                      <label class="radio-label-inline">
                        <input
                          type="radio"
                          v-model="novaCotacao.tipoFornecedor"
                          value="SERVICO"
                          @change="carregarFornecedoresPorTipo"
                        />
                        <span>Serviço</span>
                      </label>
                    </div>
                  </div>

                  <div class="form-group">
                    <label>Fornecedor *</label>
                    <div class="autocomplete-wrapper">
                      <input
                        v-model="buscaFornecedor"
                        @input="filtrarFornecedores"
                        @focus="mostrarListaFornecedores = true"
                        type="text"
                        class="form-input-inline"
                        placeholder="Digite para buscar..."
                        :disabled="carregandoFornecedores"
                      />
                      <div v-if="mostrarListaFornecedores && fornecedoresFiltrados.length > 0" class="autocomplete-dropdown">
                        <div
                          v-for="f in fornecedoresFiltrados"
                          :key="f.id"
                          @click="selecionarFornecedor(f)"
                          class="autocomplete-item"
                        >
                          {{ f.razaoSocial }}
                        </div>
                      </div>
                      <div v-if="mostrarListaFornecedores && buscaFornecedor && fornecedoresFiltrados.length === 0" class="autocomplete-empty">
                        Nenhum fornecedor encontrado
                      </div>
                    </div>
                  </div>
                </div>

                <div class="form-row-3">
                  <div class="form-group">
                    <label>Valor (R$) *</label>
                    <input
                      v-model="precoFormatado"
                      @input="formatarPrecoInput"
                      @blur="formatarPrecoCompleto"
                      type="text"
                      class="form-input-inline"
                      placeholder="0,00"
                    />
                  </div>

                  <div class="form-group">
                    <label>Prazo (dias úteis)</label>
                    <input v-model="novaCotacao.prazoEmDiasUteis" type="number" class="form-input-inline" placeholder="15">
                  </div>

                  <div class="form-group">
                    <label>Data Validade *</label>
                    <input v-model="novaCotacao.dataLimite" type="date" class="form-input-inline">
                  </div>
                </div>

                <div class="form-group">
                  <label>Anexo PDF (opcional)</label>
                  <div class="file-upload-container">
                    <input
                      @change="handleFileUpload"
                      type="file"
                      accept="application/pdf"
                      id="file-upload-cotacao"
                      class="file-input-hidden"
                    />
                    <label for="file-upload-cotacao" class="file-upload-label">
                      <svg viewBox="0 0 24 24" width="20" height="20" class="file-icon">
                        <path fill="currentColor" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20M10,19L12,15H9V10H15V15L13,19H10Z"/>
                      </svg>
                      <span v-if="!novaCotacao.arquivo" class="file-upload-text">
                        Escolher arquivo PDF
                      </span>
                      <span v-else class="file-upload-text selected">
                        {{ novaCotacao.arquivo.name }}
                      </span>
                    </label>
                    <button
                      v-if="novaCotacao.arquivo"
                      @click="removerArquivo"
                      type="button"
                      class="btn-remove-file"
                      title="Remover arquivo"
                    >
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
                      </svg>
                    </button>
                  </div>
                  <div v-if="novaCotacao.arquivo" class="file-info">
                    <svg viewBox="0 0 24 24" width="14" height="14" class="file-info-icon">
                      <path fill="currentColor" d="M13,9H11V7H13M13,17H11V11H13M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2Z"/>
                    </svg>
                    <span class="file-size">{{ formatarTamanhoArquivo(novaCotacao.arquivo.size) }}</span>
                  </div>
                </div>

                <div class="form-actions-inline">
                  <button @click="cancelarNovaCotacao" class="btn-cancel-inline" type="button">
                    Cancelar
                  </button>
                  <button
                    @click="criarNovaCotacao"
                    class="btn-save-inline"
                    type="button"
                    :disabled="!novaCotacao.fornecedorId || !novaCotacao.preco || !novaCotacao.dataLimite"
                  >
                    Criar Cotação
                  </button>
                </div>
              </div>

              <!-- Lista de Cotações Existentes -->
              <div v-if="carregandoCotacoes" class="loading-container-small">
                <div class="loading-spinner-small"></div>
                <span>Carregando...</span>
              </div>

              <div v-else-if="cotacoesDisponiveis.length === 0 && !modoNovaCotacao" class="empty-state-inline">
                <p>Nenhuma cotação disponível. Crie uma nova!</p>
              </div>

              <div v-else-if="!modoNovaCotacao" class="cotacoes-lista-inline">
                <div
                  v-for="cotacao in cotacoesDisponiveis"
                  :key="cotacao.id"
                  @click="selecionarCotacao(cotacao)"
                  class="cotacao-card-inline"
                  :class="{
                    'selecionada': cotacaoSelecionada?.id === cotacao.id,
                    'vinculada': cotacao.vinculadaAoItem
                  }"
                >
                  <div class="cotacao-badge-container">
                    <span v-if="cotacao.vinculadaAoItem" class="badge-vinculada">Atual</span>
                    <span v-if="cotacaoSelecionada?.id === cotacao.id" class="badge-selecionada">✓ Selecionada</span>
                  </div>

                  <div class="cotacao-info-inline">
                    <div class="fornecedor-info">
                      <strong>{{ cotacao.nomeFornecedor }}</strong>
                      <span class="tipo-badge">{{ cotacao.tipoFornecedor }}</span>
                    </div>
                    <div class="preco-info">R$ {{ formatarPreco(cotacao.preco) }}</div>
                  </div>

                  <div class="cotacao-detalhes-inline">
                    <span v-if="cotacao.prazoEmDiasUteis">Prazo: {{ cotacao.prazoEmDiasUteis }} dias</span>
                    <span v-if="cotacao.dataLimite">Validade: {{ formatarData(cotacao.dataLimite) }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Justificativa -->
            <div class="form-group" style="margin-top: 24px;">
              <label for="observacaoEdicao">Justificativa da alteração: *</label>
              <textarea
                id="observacaoEdicao"
                v-model="observacaoEdicao"
                placeholder="Informe o motivo da alteração..."
                rows="3"
                class="form-textarea-inline"
                maxlength="1000"
              ></textarea>
              <div class="char-count-inline">{{ observacaoEdicao.length }}/1000</div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="fecharModalEdicao" class="btn-secondary" :disabled="salvandoEdicao">
            Cancelar
          </button>
          <button
            v-if="itemSendoEditado"
            @click="salvarEdicao"
            class="btn-primary"
            :disabled="salvandoEdicao || !cotacaoSelecionada || !observacaoEdicao.trim()"
          >
            {{ salvandoEdicao ? 'Salvando...' : 'Vincular Cotação' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import pedidoService from '@/services/pedidoService.js'
import rascunhoService from '@/services/rascunhoService.js'
import cotacaoRascunhoService from '@/services/cotacaoRascunhoService.js'
import cotacaoService from '@/services/cotacaoService.js'
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
    const isFinalizado = ref(false)
    const isEditMode = ref(false)

    // Computed
    const podeEditarPedido = computed(() => {
      if (!pedido.value) return false
      return ['PENDENTE'].includes(pedido.value.status)
    })

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
        // Para pedidos: verificar itensPedidoIds (array)
        if (c.itensPedidoIds && c.itensPedidoIds.length > 0) {
          return c.itensPedidoIds.includes(item.id)
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
      // Para pedidos - array de IDs
      if (cotacao.itensPedidoIds && cotacao.itensPedidoIds.length > 0) {
        return cotacao.itensPedidoIds
      }
      // Para pedidos - ID único (compatibilidade)
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

    const togglePdfViewer = async (cotacao, pdfIndex = 0) => {
      const pdfKey = `${cotacao.id}-${pdfIndex}`

      // Se o PDF já está aberto, fechar
      if (pdfAberto.value === pdfKey) {
        fecharPdfViewer()
        return
      }

      // Fechar PDF anterior se houver
      if (pdfAberto.value !== null) {
        fecharPdfViewer()
      }

      pdfAberto.value = pdfKey
      carregandoPdf.value = true

      try {
        let blob
        // Usar o serviço correto baseado no tipo
        if (isRascunho.value) {
          blob = await cotacaoRascunhoService.obterAnexoPdf(
            pedido.value.rascunhoId,
            cotacao.id,
            pdfIndex
          )
        } else {
          // Para pedidos finais, usar o serviço de cotação
          blob = await cotacaoService.obterAnexoPdf(cotacao.id, pdfIndex)
        }

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

          // Atualizar isFinalizado baseado no status do rascunho
          isFinalizado.value = rascunhoCompleto.status === 'FINALIZADO'

          historico.value = historicoRascunho || []
        } else {
          isRascunho.value = false

          // Carregar pedido
          const pedidoCompleto = await pedidoService.obterPorId(id)
          pedido.value = pedidoCompleto

          // Carregar histórico
          const historicoPedido = await historicoPedidoService.listarPorPedido(id)
          historico.value = historicoPedido || []

          // Cotações agora vêm diretamente do objeto do pedido
          cotacoes.value = pedidoCompleto.cotacoes || []

          console.log('=== DEBUG: Pedido carregado ===')
          console.log('Pedido completo:', pedidoCompleto)
          console.log('Itens do pedido:', pedidoCompleto.itens)
          console.log('Cotações do pedido:', pedidoCompleto.cotacoes)
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

    // Estado do modal de edição
    const modalEdicaoAberto = ref(false)
    const itemSendoEditado = ref(null)
    const cotacaoSelecionada = ref(null)
    const observacaoEdicao = ref('')
    const cotacoesDisponiveis = ref([])
    const carregandoCotacoes = ref(false)
    const salvandoEdicao = ref(false)
    const modoNovaCotacao = ref(false)

    // PDF no modal
    const pdfModalAberto = ref(null)
    const pdfModalUrl = ref(null)
    const carregandoPdfModal = ref(false)

    // Estado da nova cotação
    const novaCotacao = ref({
      fornecedorId: null,
      tipoFornecedor: 'PRODUTO',
      preco: '',
      prazoEmDiasUteis: '',
      dataLimite: '',
      arquivo: null
    })
    const fornecedoresDisponiveis = ref([])
    const carregandoFornecedores = ref(false)

    // Autocomplete de fornecedor
    const buscaFornecedor = ref('')
    const fornecedoresFiltrados = ref([])
    const mostrarListaFornecedores = ref(false)
    const precoFormatado = ref('')

    const editarPedido = () => {
      // Abrir modal de edição
      modalEdicaoAberto.value = true
    }

    const abrirEdicaoItem = async (item) => {
      itemSendoEditado.value = item
      cotacaoSelecionada.value = null
      observacaoEdicao.value = ''

      await carregarCotacoesItem(item)
    }

    const carregarCotacoesItem = async (item) => {
      try {
        carregandoCotacoes.value = true

        console.log('=== DEBUG: Carregando cotações ===')
        console.log('Item selecionado:', item)
        console.log('Total de cotações no pedido:', cotacoes.value?.length || 0)
        console.log('Cotações já carregadas no pedido:', cotacoes.value)

        // SIMPLIFICAÇÃO: Sempre mostrar TODAS as cotações do pedido
        // O usuário pode escolher qualquer cotação para vincular ao item
        const todasCotacoes = cotacoes.value || []

        cotacoesDisponiveis.value = todasCotacoes.map(c => {
          console.log('Processando cotação:', c)
          return {
            ...c,
            nomeFornecedor: c.nomeFornecedor || c.fornecedorNome || `Fornecedor #${c.fornecedorId || 'desconhecido'}`,
            // Marcar se esta cotação já está vinculada a este item
            vinculadaAoItem: c.itensPedidoIds?.includes(item.id) || false
          }
        })

        console.log('Total de cotações disponíveis:', cotacoesDisponiveis.value.length)
        console.log('Cotações disponíveis:', cotacoesDisponiveis.value)
      } catch (error) {
        console.error('Erro ao carregar cotações:', error)
        alert('Erro ao carregar cotações disponíveis')
      } finally {
        carregandoCotacoes.value = false
      }
    }

    const carregarFornecedoresPorTipo = async () => {
      try {
        carregandoFornecedores.value = true
        novaCotacao.value.fornecedorId = null
        buscaFornecedor.value = ''
        fornecedoresFiltrados.value = []

        if (novaCotacao.value.tipoFornecedor === 'PRODUTO') {
          fornecedoresDisponiveis.value = await fornecedorService.listarProdutos()
        } else {
          fornecedoresDisponiveis.value = await fornecedorService.listarServicos()
        }

        fornecedoresFiltrados.value = fornecedoresDisponiveis.value
      } catch (error) {
        console.error('Erro ao carregar fornecedores:', error)
        alert('Erro ao carregar fornecedores')
      } finally {
        carregandoFornecedores.value = false
      }
    }

    const filtrarFornecedores = () => {
      const busca = buscaFornecedor.value.toLowerCase()
      if (!busca) {
        fornecedoresFiltrados.value = fornecedoresDisponiveis.value
      } else {
        fornecedoresFiltrados.value = fornecedoresDisponiveis.value.filter(f =>
          f.razaoSocial?.toLowerCase().includes(busca)
        )
      }
    }

    const selecionarFornecedor = (fornecedor) => {
      novaCotacao.value.fornecedorId = fornecedor.id
      buscaFornecedor.value = fornecedor.razaoSocial
      mostrarListaFornecedores.value = false
    }

    const formatarPrecoInput = (event) => {
      let valor = event.target.value

      // Remover tudo que não é número
      valor = valor.replace(/\D/g, '')

      // Converter para número e dividir por 100 para ter centavos
      const numero = parseFloat(valor) / 100

      // Atualizar o valor interno
      novaCotacao.value.preco = numero

      // Formatar para exibição
      if (numero > 0) {
        precoFormatado.value = numero.toLocaleString('pt-BR', {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2
        })
      } else {
        precoFormatado.value = ''
      }
    }

    const formatarPrecoCompleto = () => {
      const numero = parseFloat(novaCotacao.value.preco) || 0
      precoFormatado.value = numero.toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }

    const handleFileUpload = (event) => {
      const file = event.target.files[0]
      if (file) {
        if (file.type !== 'application/pdf') {
          alert('Apenas arquivos PDF são permitidos')
          event.target.value = ''
          return
        }
        if (file.size > 10 * 1024 * 1024) {
          alert('Arquivo muito grande. Máximo: 10MB')
          event.target.value = ''
          return
        }
        novaCotacao.value.arquivo = file
      }
    }

    const removerArquivo = () => {
      novaCotacao.value.arquivo = null
      const fileInput = document.getElementById('file-upload-cotacao')
      if (fileInput) {
        fileInput.value = ''
      }
    }

    const excluirCotacao = async (cotacao) => {
      if (!confirm(`Tem certeza que deseja excluir a cotação de ${cotacao.nomeFornecedor}?`)) {
        return
      }

      try {
        const rascunhoId = pedido.value.rascunhoId || route.params.id

        if (isRascunho.value) {
          await cotacaoRascunhoService.deletar(rascunhoId, cotacao.id)
        } else {
          await cotacaoService.deleteCotacao(cotacao.id)
        }

        // Recarregar a página para atualizar as cotações
        await carregarPedido()
        alert('Cotação excluída com sucesso!')
      } catch (error) {
        console.error('Erro ao excluir cotação:', error)
        alert('Erro ao excluir cotação. Tente novamente.')
      }
    }

    const formatarTamanhoArquivo = (bytes) => {
      if (bytes === 0) return '0 Bytes'
      const k = 1024
      const sizes = ['Bytes', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
    }

    const abrirFormularioNovaCotacao = async () => {
      modoNovaCotacao.value = true
      cotacaoSelecionada.value = null
      // Carregar fornecedores do tipo padrão
      await carregarFornecedoresPorTipo()
    }

    const cancelarNovaCotacao = () => {
      modoNovaCotacao.value = false
      novaCotacao.value = {
        fornecedorId: null,
        tipoFornecedor: 'PRODUTO',
        preco: '',
        prazoEmDiasUteis: '',
        dataLimite: '',
        arquivo: null
      }
      buscaFornecedor.value = ''
      precoFormatado.value = ''
      fornecedoresFiltrados.value = fornecedoresDisponiveis.value
      mostrarListaFornecedores.value = false
    }

    const selecionarCotacao = (cotacao) => {
      cotacaoSelecionada.value = cotacao
    }

    const criarNovaCotacao = async () => {
      if (!novaCotacao.value.fornecedorId || !novaCotacao.value.preco || !novaCotacao.value.dataLimite) {
        alert('Preencha todos os campos obrigatórios')
        return
      }

      if (!pedido.value || !pedido.value.id) {
        alert('Erro: ID do pedido não encontrado')
        return
      }

      if (!itemSendoEditado.value || !itemSendoEditado.value.id) {
        alert('Erro: Item não selecionado')
        return
      }

      try {
        salvandoEdicao.value = true

        // Criar dados da nova cotação - FORMATO CORRETO CONFORME BACKEND
        const dadosCotacao = {
          fornecedorId: novaCotacao.value.fornecedorId,
          tipoFornecedor: novaCotacao.value.tipoFornecedor,
          solicitacaoDePedidoId: pedido.value.id,
          itensPedidoIds: [itemSendoEditado.value.id],
          preco: parseFloat(novaCotacao.value.preco),
          prazoEmDiasUteis: novaCotacao.value.prazoEmDiasUteis ? parseInt(novaCotacao.value.prazoEmDiasUteis) : null,
          dataLimite: novaCotacao.value.dataLimite,
          anexoPdf: null
        }

        // Se houver arquivo PDF, converter para bytes
        if (novaCotacao.value.arquivo) {
          console.log('📄 Convertendo arquivo PDF para bytes...')
          const bytesArray = await cotacaoService.arquivoParaBytes(novaCotacao.value.arquivo)
          dadosCotacao.anexoPdf = bytesArray
        }

        console.log('📤 Criando nova cotação:', dadosCotacao)

        // Criar a cotação no backend
        const cotacaoCriada = await cotacaoService.criar(dadosCotacao)

        console.log('✅ Cotação criada:', cotacaoCriada)

        // Limpar formulário e fechar
        cancelarNovaCotacao()

        // Recarregar cotações do item para mostrar a nova
        await carregarCotacoesItem(itemSendoEditado.value)

        // Selecionar automaticamente a nova cotação
        cotacaoSelecionada.value = cotacaoCriada

        alert('Cotação criada com sucesso!')
      } catch (error) {
        console.error('❌ Erro ao criar cotação:', error)
        alert('Erro ao criar cotação: ' + (error.message || 'Erro desconhecido'))
      } finally {
        salvandoEdicao.value = false
      }
    }

    const togglePdfModal = async (cotacao, pdfIndex = 0) => {
      const pdfKey = `${cotacao.id}-${pdfIndex}`

      // Se o PDF já está aberto, fechar
      if (pdfModalAberto.value === pdfKey) {
        fecharPdfModal()
        return
      }

      // Fechar PDF anterior se houver
      if (pdfModalAberto.value !== null) {
        fecharPdfModal()
      }

      pdfModalAberto.value = pdfKey
      carregandoPdfModal.value = true

      try {
        let blob
        // Para pedidos finais, usar o serviço de cotação
        blob = await cotacaoService.obterAnexoPdf(cotacao.id, pdfIndex)

        if (blob && blob.size > 0) {
          pdfModalUrl.value = URL.createObjectURL(blob)
        }
      } catch (error) {
        console.error('Erro ao carregar PDF:', error)
        alert('Erro ao carregar PDF')
      } finally {
        carregandoPdfModal.value = false
      }
    }

    const fecharPdfModal = () => {
      if (pdfModalUrl.value && pdfModalUrl.value.startsWith('blob:')) {
        URL.revokeObjectURL(pdfModalUrl.value)
      }
      pdfModalAberto.value = null
      pdfModalUrl.value = null
    }

    const fecharModalEdicao = () => {
      fecharPdfModal() // Fechar PDF se estiver aberto
      modalEdicaoAberto.value = false
      itemSendoEditado.value = null
      cotacaoSelecionada.value = null
      observacaoEdicao.value = ''
      cotacoesDisponiveis.value = []
      modoNovaCotacao.value = false
      novaCotacao.value = {
        fornecedorId: null,
        tipoFornecedor: 'PRODUTO',
        preco: '',
        prazoEmDiasUteis: '',
        dataLimite: '',
        arquivo: null
      }
    }

    const salvarEdicao = async () => {
      if (!itemSendoEditado.value || !cotacaoSelecionada.value) {
        alert('Por favor, selecione uma cotação')
        return
      }

      if (!observacaoEdicao.value || observacaoEdicao.value.trim() === '') {
        alert('Por favor, informe uma justificativa para a edição')
        return
      }

      try {
        salvandoEdicao.value = true

        console.log('💾 Salvando edição...')
        console.log('Item sendo editado:', itemSendoEditado.value)
        console.log('Cotação selecionada:', cotacaoSelecionada.value)

        // Verificar se o item já tem uma cotação vinculada
        const cotacaoAtualDoItem = pedido.value.cotacoes?.find(c =>
          c.itensPedidoIds?.includes(itemSendoEditado.value.id)
        )

        console.log('Cotação atual do item:', cotacaoAtualDoItem)

        // Se está selecionando a mesma cotação que já está vinculada, não fazer nada
        if (cotacaoAtualDoItem && cotacaoAtualDoItem.id === cotacaoSelecionada.value.id) {
          console.log('⚠️ Cotação selecionada já está vinculada ao item')
          alert('Esta cotação já está vinculada ao item.')
          return
        }

        // Obter os IDs dos itens já vinculados à cotação selecionada
        const itensJaVinculados = cotacaoSelecionada.value.itensPedidoIds || []

        // Adicionar o item sendo editado se ainda não estiver vinculado
        let novosItensVinculados = [...itensJaVinculados]
        if (!novosItensVinculados.includes(itemSendoEditado.value.id)) {
          novosItensVinculados.push(itemSendoEditado.value.id)
        }

        console.log('📤 Vinculando itens à cotação:', cotacaoSelecionada.value.id, novosItensVinculados)

        // Vincular os itens à cotação usando o novo endpoint
        await cotacaoService.vincularItens(cotacaoSelecionada.value.id, novosItensVinculados)

        // Se havia uma cotação anterior vinculada ao item, remover o item dela
        if (cotacaoAtualDoItem && cotacaoAtualDoItem.id !== cotacaoSelecionada.value.id) {
          const itensRestantes = cotacaoAtualDoItem.itensPedidoIds?.filter(
            id => id !== itemSendoEditado.value.id
          ) || []

          // Se ainda há itens vinculados, atualizar a cotação antiga
          // Se não há mais itens, deixar a cotação sem itens (será vazio)
          if (itensRestantes.length > 0) {
            console.log('📤 Removendo item da cotação antiga:', cotacaoAtualDoItem.id, itensRestantes)
            await cotacaoService.vincularItens(cotacaoAtualDoItem.id, itensRestantes)
          } else {
            console.log('⚠️ Cotação antiga ficará sem itens vinculados')
            await cotacaoService.vincularItens(cotacaoAtualDoItem.id, [])
          }
        }

        // Atualizar a observação do pedido para registrar a mudança
        const pedidoAtualizado = {
          ...pedido.value,
          observacao: observacaoEdicao.value.trim()
        }

        console.log('📤 Atualizando observação do pedido')
        await pedidoService.atualizar(pedido.value.id, pedidoAtualizado)

        alert('Cotação vinculada ao item com sucesso!')

        // Recarregar dados do pedido
        await carregarPedido()

        fecharModalEdicao()
      } catch (error) {
        console.error('❌ Erro ao salvar edição:', error)
        alert('Erro ao salvar edição: ' + (error.message || 'Erro desconhecido'))
      } finally {
        salvandoEdicao.value = false
      }
    }

    onMounted(() => {
      carregarPedido()

      // Fechar dropdown ao clicar fora
      document.addEventListener('click', (e) => {
        if (!e.target.closest('.autocomplete-wrapper')) {
          mostrarListaFornecedores.value = false
        }
      })
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
      isFinalizado,
      podeEditarPedido,

      // PDF
      pdfAberto,
      carregandoPdf,
      pdfUrl,

      // Modal de edição
      modalEdicaoAberto,
      itemSendoEditado,
      cotacaoSelecionada,
      observacaoEdicao,
      cotacoesDisponiveis,
      carregandoCotacoes,
      salvandoEdicao,
      modoNovaCotacao,
      novaCotacao,
      fornecedoresDisponiveis,
      carregandoFornecedores,
      buscaFornecedor,
      fornecedoresFiltrados,
      mostrarListaFornecedores,
      precoFormatado,
      carregarFornecedoresPorTipo,
      filtrarFornecedores,
      selecionarFornecedor,
      formatarPrecoInput,
      formatarPrecoCompleto,
      handleFileUpload,
      removerArquivo,
      formatarTamanhoArquivo,
      excluirCotacao,
      criarNovaCotacao,
      abrirFormularioNovaCotacao,
      cancelarNovaCotacao,
      selecionarCotacao,
      pdfModalAberto,
      pdfModalUrl,
      carregandoPdfModal,
      togglePdfModal,
      fecharPdfModal,

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
      editarRascunho,
      editarPedido,
      abrirEdicaoItem,
      fecharModalEdicao,
      salvarEdicao
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.pdf-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.btn-pdf {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 0.75rem;
  cursor: pointer;
  background: #f0f9ff;
  color: #0369a1;
  border: 1px solid #bae6fd;
  transition: all 0.2s ease;
}

.btn-pdf:hover {
  background: #e0f2fe;
}

.btn-pdf-active {
  background: #0369a1;
  color: white;
  border-color: #0369a1;
}

.btn-delete-cotacao {
  padding: 6px 10px;
  border-radius: 4px;
  cursor: pointer;
  background: #fef2f2;
  color: #991b1b;
  border: 1px solid #fecaca;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 32px;
  height: 28px;
}

.btn-delete-cotacao:hover {
  background: #fee2e2;
  border-color: #fca5a5;
  color: #7f1d1d;
}

.btn-delete-cotacao:active {
  background: #fecaca;
}

.btn-delete-cotacao svg {
  width: 16px;
  height: 16px;
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

/* Modal de Edição */
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
  z-index: 9999;
  padding: 20px;
}

.modal-container {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 800px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e293b;
}

.btn-close {
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
}

.btn-close:hover {
  background: #f3f4f6;
  color: #1e293b;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-section h4 {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
}

.form-section h5 {
  margin: 0 0 12px 0;
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
}

/* Seleção de Itens */
.itens-selecao {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.item-selecao-card {
  background: #f9fafb;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.item-selecao-card:hover {
  border-color: #1F285F;
  background: #f3f4f6;
}

.item-selecao-numero {
  font-size: 0.75rem;
  font-weight: 600;
  color: #6b7280;
  margin-bottom: 4px;
}

.item-selecao-nome {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
}

.item-selecao-qtd {
  font-size: 0.875rem;
  color: #6b7280;
  margin-bottom: 8px;
}

.item-selecao-status {
  font-size: 0.75rem;
  padding: 4px 8px;
  border-radius: 4px;
  background: #fef3c7;
  color: #92400e;
  display: inline-block;
}

.item-selecao-status.tem-cotacao {
  background: #d1fae5;
  color: #065f46;
}

/* Botão voltar */
.btn-voltar-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
  width: fit-content;
}

.btn-voltar-item:hover {
  background: #e5e7eb;
}

/* Item Selecionado */
.item-selecionado {
  background: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 8px;
  padding: 16px;
}

.item-selecionado h4 {
  margin: 0 0 4px 0;
  color: #0369a1;
}

.item-selecionado p {
  margin: 0;
  font-size: 0.875rem;
  color: #0c4a6e;
}

/* Cotações no Modal */
.cotacoes-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cotacoes-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.btn-toggle {
  padding: 6px 12px;
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.8125rem;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-toggle:hover {
  background: #e5e7eb;
  color: #1e293b;
}

.cotacoes-lista-modal {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cotacao-selecao-card {
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.2s;
  position: relative;
}

.cotacao-selecao-card:hover {
  border-color: #1F285F;
}

.cotacao-selecao-card.selecionada {
  border-color: #10b981;
  background: #f0fdf4;
}

.cotacao-selecao-card.vinculada-atual {
  border-color: #3b82f6;
  background: #eff6ff;
}

.badge-atual {
  background: #3b82f6;
  color: white;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  margin-bottom: 12px;
  display: inline-block;
}

.cotacao-acoes-modal {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
}

.pdf-viewer-inline {
  margin-top: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.pdf-viewer-inline .pdf-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 12px;
  color: #6b7280;
}

.pdf-viewer-inline .pdf-viewer-wrapper {
  height: 400px;
}

.pdf-viewer-inline .pdf-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

.btn-pdf-mini {
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 0.75rem;
  cursor: pointer;
  background: #f0f9ff;
  color: #0369a1;
  border: 1px solid #bae6fd;
  transition: all 0.2s ease;
  font-weight: 500;
}

.btn-pdf-mini:hover {
  background: #e0f2fe;
}

.cotacao-selecao-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.cotacao-selecao-fornecedor strong {
  display: block;
  font-size: 0.875rem;
  color: #1e293b;
  margin-bottom: 4px;
}

.cotacao-selecao-tipo {
  font-size: 0.75rem;
  color: #64748b;
  background: #f1f5f9;
  padding: 2px 8px;
  border-radius: 4px;
  display: inline-block;
}

.cotacao-selecao-preco {
  font-size: 1.125rem;
  font-weight: 600;
  color: #059669;
}

.cotacao-selecao-info {
  display: flex;
  gap: 16px;
  font-size: 0.8125rem;
  color: #64748b;
}

.cotacao-selecionada-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #10b981;
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

/* Formulário de Nova Cotação */
.form-nova-cotacao {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 20px;
  margin-top: 16px;
}

.form-nova-cotacao h6 {
  margin: 0 0 16px 0;
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.form-select,
.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.form-select:focus,
.form-input:focus {
  outline: none;
  border-color: #1F285F;
  box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.1);
}

.form-select:disabled {
  background: #f3f4f6;
  cursor: not-allowed;
}

.form-nova-cotacao small {
  font-size: 0.75rem;
  color: #6b7280;
  margin-top: 4px;
  display: block;
}

/* Form Groups */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
}

.form-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  font-family: inherit;
  resize: vertical;
  transition: all 0.2s;
}

.form-textarea:focus {
  outline: none;
  border-color: #1F285F;
  box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.1);
}

.char-count {
  font-size: 0.75rem;
  color: #6b7280;
  text-align: right;
}

/* Upload de Arquivo PDF */
.file-upload-container {
  display: flex;
  gap: 8px;
  align-items: center;
}

.file-input-hidden {
  display: none;
}

.file-upload-label {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: white;
  border: 2px dashed #d1d5db;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.875rem;
  color: #6b7280;
}

.file-upload-label:hover {
  border-color: #1F285F;
  background: #f9fafb;
}

.file-upload-label:active {
  transform: scale(0.98);
}

.file-icon {
  color: #9ca3af;
  flex-shrink: 0;
  transition: color 0.3s;
}

.file-upload-label:hover .file-icon {
  color: #1F285F;
}

.file-upload-text {
  font-weight: 500;
  transition: color 0.3s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-upload-text.selected {
  color: #1F285F;
  font-weight: 600;
}

.btn-remove-file {
  padding: 8px;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.btn-remove-file:hover {
  background: #dc2626;
  transform: scale(1.05);
}

.btn-remove-file:active {
  transform: scale(0.95);
}

.file-info {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  padding: 8px 12px;
  background: #f0f9ff;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  font-size: 0.75rem;
  color: #1e40af;
}

.file-info-icon {
  color: #3b82f6;
  flex-shrink: 0;
}

.file-size {
  font-weight: 500;
}

/* Autocomplete de Fornecedor */
.autocomplete-wrapper {
  position: relative;
}

.autocomplete-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  max-height: 200px;
  overflow-y: auto;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  margin-top: 4px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.autocomplete-item {
  padding: 10px 12px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: background 0.2s;
  border-bottom: 1px solid #f3f4f6;
}

.autocomplete-item:last-child {
  border-bottom: none;
}

.autocomplete-item:hover {
  background: #f9fafb;
}

.autocomplete-empty {
  padding: 12px;
  text-align: center;
  color: #9ca3af;
  font-size: 0.875rem;
  font-style: italic;
}

/* Estilos Inline (similar ao rascunho) */
.section-header-inline {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header-inline h5 {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #111827;
}

.btn-add-small {
  padding: 8px 16px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add-small:hover {
  background: #059669;
}

/* Formulário Inline */
.form-nova-cotacao-inline {
  background: #f9fafb;
  border: 2px dashed #d1d5db;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.form-row-2 {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 12px;
  margin-bottom: 12px;
}

.form-row-3 {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 12px;
}

.radio-group-inline {
  display: flex;
  gap: 16px;
}

.radio-label-inline {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 0.875rem;
}

.radio-label-inline input[type="radio"] {
  cursor: pointer;
}

.form-select-inline,
.form-input-inline {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.form-select-inline:focus,
.form-input-inline:focus {
  outline: none;
  border-color: #1F285F;
  box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.1);
}

.form-actions-inline {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-top: 12px;
}

.btn-cancel-inline {
  padding: 8px 16px;
  background: white;
  color: #6b7280;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel-inline:hover {
  background: #f3f4f6;
}

.btn-save-inline {
  padding: 8px 16px;
  background: #1F285F;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-save-inline:hover:not(:disabled) {
  background: #161d47;
}

.btn-save-inline:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Lista de Cotações Inline */
.loading-container-small {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px;
  color: #6b7280;
  font-size: 0.875rem;
}

.loading-spinner-small {
  width: 16px;
  height: 16px;
  border: 2px solid #e5e7eb;
  border-top-color: #1F285F;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

.empty-state-inline {
  padding: 24px;
  text-align: center;
  color: #6b7280;
  font-size: 0.875rem;
  background: #f9fafb;
  border-radius: 6px;
}

.cotacoes-lista-inline {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cotacao-card-inline {
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.cotacao-card-inline:hover {
  border-color: #1F285F;
  box-shadow: 0 2px 8px rgba(31, 40, 95, 0.1);
}

.cotacao-card-inline.selecionada {
  border-color: #1F285F;
  background: #f0f4ff;
}

.cotacao-card-inline.vinculada {
  border-color: #10b981;
  background: #f0fdf4;
}

.cotacao-badge-container {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.badge-vinculada {
  padding: 2px 8px;
  background: #10b981;
  color: white;
  font-size: 0.75rem;
  font-weight: 500;
  border-radius: 4px;
}

.badge-selecionada {
  padding: 2px 8px;
  background: #1F285F;
  color: white;
  font-size: 0.75rem;
  font-weight: 500;
  border-radius: 4px;
}

.cotacao-info-inline {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.fornecedor-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.fornecedor-info strong {
  font-size: 0.875rem;
  color: #111827;
}

.tipo-badge {
  padding: 2px 6px;
  background: #e5e7eb;
  color: #374151;
  font-size: 0.75rem;
  border-radius: 3px;
}

.preco-info {
  font-size: 1rem;
  font-weight: 600;
  color: #1F285F;
}

.cotacao-detalhes-inline {
  display: flex;
  gap: 16px;
  font-size: 0.75rem;
  color: #6b7280;
}

.form-textarea-inline {
  width: 100%;
  padding: 10px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  font-family: inherit;
  resize: vertical;
  transition: all 0.2s;
}

.form-textarea-inline:focus {
  outline: none;
  border-color: #1F285F;
  box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.1);
}

.char-count-inline {
  font-size: 0.75rem;
  color: #6b7280;
  text-align: right;
  margin-top: 4px;
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

  .modal-container {
    max-width: 100%;
    max-height: 100vh;
    border-radius: 0;
  }

  .itens-selecao {
    grid-template-columns: 1fr;
  }
}
</style>
