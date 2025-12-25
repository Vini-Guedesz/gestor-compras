<template>
  <div class="cotacoes-container">
    <!-- Dados do Rascunho -->
    <div class="rascunho-info-box">
      <h3 class="info-title">Dados do Rascunho</h3>
      <div class="info-grid">
        <div class="info-item">
          <span class="info-label">ID:</span>
          <span class="info-value">#{{ rascunho.id }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">Data de Criação:</span>
          <span class="info-value">{{ formatarData(rascunho.dataCriacao) }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">Itens:</span>
          <span class="info-value">{{ rascunho.itens?.length || 0 }} item(ns)</span>
        </div>
      </div>
    </div>

    <!-- Seção de Cotações -->
    <div class="cotacoes-section">
      <div class="section-header">
        <h3 class="section-title">Cotações do Rascunho</h3>
        <div class="header-actions">
          <button
            @click="gerarRelatorio"
            class="btn-relatorio"
            :disabled="gerandoRelatorio || !rascunho.id"
            title="Gerar relatório de itens para enviar aos fornecedores"
          >
            <svg v-if="!gerandoRelatorio" viewBox="0 0 24 24" width="18" height="18">
              <path fill="currentColor" d="M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z"/>
            </svg>
            <span v-if="gerandoRelatorio" class="loading-spinner-small"></span>
            <span v-if="!gerandoRelatorio">Gerar Relatório</span>
          </button>
          <button
            v-if="permissions.canCotarRascunho"
            @click="abrirFormularioCotacao"
            class="btn-add-cotacao"
            aria-label="Adicionar nova cotação ao rascunho"
          >
            <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
              <path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
            </svg>
            Adicionar Cotação
          </button>
        </div>
      </div>

      <!-- Lista de Cotações -->
      <div v-if="carregando" class="loading-state">
        <div class="loading-spinner"></div>
        <span>Carregando cotações...</span>
      </div>

      <div v-else-if="cotacoes.length > 0" class="cotacoes-lista">
        <div v-for="cotacao in cotacoes" :key="cotacao.id" class="cotacao-card">
          <div class="cotacao-header">
            <div class="cotacao-fornecedor">
              <strong>{{ cotacao.nomeFornecedor || 'Fornecedor' }}</strong>
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
                  v-for="itemId in cotacao.itensRascunhoIds"
                  :key="itemId"
                  class="item-tag"
                >
                  {{ getNomeItem(itemId) }}
                </span>
              </div>
            </div>
          </div>

          <div class="cotacao-actions">
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
            <!-- PDF único (compatibilidade) -->
            <button
              v-else-if="cotacao.temAnexoPdf"
              @click="togglePdfViewer(cotacao, 0)"
              class="btn-pdf"
              :class="{ 'btn-pdf-active': pdfAberto === `${cotacao.id}-0` }"
            >
              <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                <path d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
              </svg>
              {{ pdfAberto === `${cotacao.id}-0` ? 'Fechar PDF' : 'Ver PDF' }}
            </button>
            <button
              v-if="permissions.canCotarRascunho"
              @click="confirmarDeleteCotacao(cotacao)"
              class="btn-delete"
              :aria-label="`Remover cotação de ${cotacao.nomeFornecedor || 'Fornecedor'}`"
            >
              <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                <path d="M19,4H15.5L14.5,3H9.5L8.5,4H5V6H19M6,19A2,2 0 0,0 8,21H16A2,2 0 0,0 18,19V7H6V19Z"/>
              </svg>
              Remover
            </button>
          </div>

          <!-- Visualizador de PDF retrátil -->
          <div v-if="pdfAberto && pdfAberto.startsWith(`${cotacao.id}-`)" class="pdf-viewer-container">
            <div v-if="carregandoPdf" class="pdf-loading">
              <div class="loading-spinner"></div>
              <span>Carregando PDF...</span>
            </div>
            <div v-else-if="erroPdf" class="pdf-error">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="#dc2626" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
              </svg>
              <span>{{ erroPdf }}</span>
            </div>
            <div v-else-if="pdfUrl" class="pdf-viewer-wrapper">
              <iframe
                :src="pdfUrl"
                class="pdf-iframe"
                frameborder="0"
              ></iframe>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <svg viewBox="0 0 24 24" width="48" height="48">
          <path fill="#9ca3af" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
        </svg>
        <p>Nenhuma cotação adicionada</p>
        <span>Clique em "Adicionar Cotação" para começar</span>
      </div>
    </div>

    <!-- Resumo dos Itens -->
    <div class="itens-resumo">
      <h3 class="section-title">Resumo dos Itens</h3>
      <div class="itens-lista">
        <div
          v-for="item in rascunho.itens"
          :key="item.id"
          class="item-resumo"
          :class="{ 'item-cotado': itemTemCotacao(item.id) }"
        >
          <div class="item-info">
            <span class="item-nome">{{ item.nome }}</span>
            <span class="item-qtd">Qtd: {{ item.quantidade }}</span>
          </div>
          <div class="item-status">
            <span v-if="itemTemCotacao(item.id)" class="status-cotado">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
              Cotado
            </span>
            <span v-else class="status-pendente">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
              </svg>
              Sem cotação
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de Nova Cotação -->
    <Teleport to="body">
      <div
        v-if="showFormulario"
        class="modal-overlay"
        @click.self="fecharFormulario"
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-titulo"
      >
        <div class="modal-cotacao" role="document">
        <div class="modal-header">
          <h3 id="modal-titulo">Nova Cotação</h3>
          <button
            @click="fecharFormulario"
            class="btn-close"
            aria-label="Fechar modal"
          >&times;</button>
        </div>

        <div class="modal-body">
          <!-- Fornecedor -->
          <div class="form-group">
            <label>Fornecedor *</label>
            <div class="fornecedor-autocomplete" :class="{ 'is-open': showFornecedorDropdown }">
              <div class="autocomplete-input-wrapper">
                <svg class="search-icon-small" viewBox="0 0 24 24" width="16" height="16">
                  <path fill="currentColor" d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
                </svg>
                <input
                  ref="fornecedorInput"
                  type="text"
                  v-model="buscaFornecedor"
                  @focus="showFornecedorDropdown = true"
                  @input="showFornecedorDropdown = true"
                  :placeholder="fornecedorSelecionadoNome || 'Pesquisar e selecionar fornecedor...'"
                  class="autocomplete-input"
                  :class="{ 'has-selection': fornecedorSelecionado }"
                >
                <button
                  v-if="fornecedorSelecionado"
                  type="button"
                  @click.stop="limparFornecedor"
                  class="clear-selection-btn"
                  title="Limpar seleção"
                >
                  <svg viewBox="0 0 24 24" width="12" height="12">
                    <path fill="currentColor" d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z"/>
                  </svg>
                </button>
                <button
                  type="button"
                  @click="showFornecedorDropdown = !showFornecedorDropdown"
                  class="dropdown-toggle-btn"
                >
                  <svg viewBox="0 0 24 24" width="16" height="16" :class="{ 'rotated': showFornecedorDropdown }">
                    <path fill="currentColor" d="M7 10l5 5 5-5z"/>
                  </svg>
                </button>
              </div>

              <!-- Dropdown de resultados -->
              <div v-if="showFornecedorDropdown" class="autocomplete-dropdown">
                <div v-if="fornecedoresProdutoFiltrados.length === 0 && fornecedoresServicoFiltrados.length === 0" class="dropdown-empty">
                  Nenhum fornecedor encontrado
                </div>
                <div v-else class="dropdown-content">
                  <!-- Fornecedores de Produto -->
                  <div v-if="fornecedoresProdutoFiltrados.length > 0" class="dropdown-group">
                    <div class="dropdown-group-label">Fornecedores de Produto</div>
                    <div
                      v-for="f in fornecedoresProdutoFiltrados"
                      :key="'p-' + f.id"
                      class="dropdown-item"
                      :class="{ 'selected': fornecedorSelecionado === 'PRODUTO-' + f.id }"
                      @click="selecionarFornecedor('PRODUTO', f)"
                    >
                      <div class="item-info">
                        <span class="item-name">{{ f.razaoSocial }}</span>
                        <span v-if="f.nomeFantasia" class="item-fantasia">{{ f.nomeFantasia }}</span>
                      </div>
                      <svg v-if="fornecedorSelecionado === 'PRODUTO-' + f.id" class="check-icon" viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                      </svg>
                    </div>
                  </div>

                  <!-- Fornecedores de Serviço -->
                  <div v-if="fornecedoresServicoFiltrados.length > 0" class="dropdown-group">
                    <div class="dropdown-group-label">Fornecedores de Serviço</div>
                    <div
                      v-for="f in fornecedoresServicoFiltrados"
                      :key="'s-' + f.id"
                      class="dropdown-item"
                      :class="{ 'selected': fornecedorSelecionado === 'SERVICO-' + f.id }"
                      @click="selecionarFornecedor('SERVICO', f)"
                    >
                      <div class="item-info">
                        <span class="item-name">{{ f.razaoSocial }}</span>
                        <span v-if="f.nomeFantasia" class="item-fantasia">{{ f.nomeFantasia }}</span>
                      </div>
                      <svg v-if="fornecedorSelecionado === 'SERVICO-' + f.id" class="check-icon" viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                      </svg>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Backdrop para fechar dropdown -->
              <div v-if="showFornecedorDropdown" class="autocomplete-backdrop" @click="showFornecedorDropdown = false"></div>
            </div>

            <!-- Fornecedor selecionado -->
            <div v-if="fornecedorSelecionado" class="fornecedor-selecionado-tag">
              <svg viewBox="0 0 24 24" width="14" height="14">
                <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
              {{ fornecedorSelecionadoNome }}
              <span class="tipo-tag">{{ novaCotacao.tipoFornecedor }}</span>
            </div>
          </div>

          <!-- Itens Contemplados -->
          <div class="form-group">
            <label>Itens Contemplados *</label>
            <div class="checkbox-lista-styled">
              <div
                v-for="item in rascunho.itens"
                :key="`cotacao-item-${item.id}`"
                class="checkbox-item-styled"
                :class="{ 'selected': novaCotacao.itensRascunhoIds.includes(item.id) }"
                @click="toggleItemCheckbox(item.id)"
              >
                <div class="checkbox-container" @click.stop>
                  <input
                    type="checkbox"
                    :checked="novaCotacao.itensRascunhoIds.includes(item.id)"
                    class="checkbox-hidden"
                    :id="`item-checkbox-${item.id}`"
                    @change="toggleItemCheckbox(item.id)"
                  >
                </div>
                <div class="checkbox-custom">
                  <svg v-if="novaCotacao.itensRascunhoIds.includes(item.id)" viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                  </svg>
                </div>
                <div class="checkbox-content">
                  <span class="checkbox-nome">{{ item.nome }}</span>
                  <span class="checkbox-qtd">Quantidade: {{ item.quantidade }}</span>
                </div>
              </div>
            </div>
            <span v-if="novaCotacao.itensRascunhoIds.length > 0" class="selected-count">
              {{ novaCotacao.itensRascunhoIds.length }} item(ns) selecionado(s)
            </span>
          </div>

          <!-- Preço -->
          <div class="form-group">
            <label>Preço Total *</label>
            <div class="preco-input-container">
              <span class="preco-prefix">R$</span>
              <input
                type="text"
                :value="precoFormatado"
                @input="handlePrecoInput($event)"
                @keypress="validarTeclaPreco($event)"
                @click.stop
                class="preco-input"
                placeholder="0,00"
                inputmode="numeric"
                required
              >
            </div>
          </div>

          <!-- Prazo -->
          <div class="form-row">
            <div class="form-group">
              <label>Prazo (dias úteis)</label>
              <input
                type="number"
                v-model="novaCotacao.prazoEmDiasUteis"
                @click.stop
                min="1"
                placeholder="Ex: 15"
              >
            </div>
            <div class="form-group">
              <label>Validade da Cotação</label>
              <input
                type="date"
                v-model="novaCotacao.dataLimite"
                @click.stop
              >
            </div>
          </div>

          <!-- Gasto Previsto -->
          <div class="form-group">
            <label>Gasto Previsto no Orçamento?</label>
            <div class="toggle-button-group">
              <button
                type="button"
                class="toggle-option"
                :class="{ 'active': !novaCotacao.gastoPrevisto }"
                @click="novaCotacao.gastoPrevisto = false"
              >
                Não
              </button>
              <button
                type="button"
                class="toggle-option"
                :class="{ 'active': novaCotacao.gastoPrevisto }"
                @click="novaCotacao.gastoPrevisto = true"
              >
                Sim
              </button>
            </div>
          </div>

          <!-- Projeto (apenas se gasto previsto) -->
          <div v-if="novaCotacao.gastoPrevisto" class="form-group">
            <label>Projeto</label>
            <input
              type="text"
              v-model="novaCotacao.projeto"
              @click.stop
              placeholder="Nome do projeto (ex: Projeto Expansão 2025)"
              maxlength="255"
            />
          </div>

          <!-- Anexos PDF -->
          <div class="form-group">
            <label>Anexos PDF</label>
            <div class="pdf-upload-container">
              <input
                type="file"
                accept=".pdf"
                @change="handleFileUpload"
                ref="fileInput"
                class="pdf-file-input"
                id="pdf-upload-rascunho"
                multiple
              >
              <label for="pdf-upload-rascunho" class="pdf-upload-label">
                <svg viewBox="0 0 24 24" width="24" height="24" aria-hidden="true">
                  <path fill="currentColor" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20M10,19L12,15H9V10H15V15L13,19H10Z"/>
                </svg>
                <span>Clique para selecionar ou arraste PDFs</span>
                <span class="upload-hint">Múltiplos arquivos • Máx. 5MB por arquivo • 20MB total</span>
              </label>
            </div>

            <!-- Lista de PDFs anexados -->
            <div v-if="arquivosPdf.length > 0" class="pdf-list">
              <div v-for="(arquivo, index) in arquivosPdf" :key="index" class="pdf-item">
                <div class="pdf-item-info">
                  <svg viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
                    <path fill="#dc2626" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
                  </svg>
                  <span class="pdf-item-name">{{ arquivo.nome }}</span>
                  <span v-if="arquivo.tamanho" class="pdf-item-size">
                    {{ (arquivo.tamanho / 1024 / 1024).toFixed(2) }}MB
                  </span>
                </div>
                <button
                  type="button"
                  @click="removerPdfIndividual(index)"
                  class="btn-remove-pdf-item"
                  :title="`Remover ${arquivo.nome}`"
                  :aria-label="`Remover arquivo ${arquivo.nome}`"
                >
                  <svg viewBox="0 0 24 24" width="14" height="14" aria-hidden="true">
                    <path fill="currentColor" d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z"/>
                  </svg>
                </button>
              </div>
              <div class="pdf-total-size">
                Total: {{ (arquivosPdf.reduce((t, a) => t + (a.tamanho || 0), 0) / 1024 / 1024).toFixed(2) }}MB de 20MB
              </div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="fecharFormulario" class="btn-cancel">Cancelar</button>
          <button
            @click="salvarCotacao"
            class="btn-save"
            :disabled="!cotacaoValida || salvando"
          >
            {{ salvando ? 'Salvando...' : 'Salvar Cotação' }}
          </button>
        </div>
      </div>
      </div>
    </Teleport>
  </div>
</template>

<script>
import { ref, computed, onBeforeUnmount, nextTick } from 'vue'
import { useToast } from '@/composables/useToast.js'
import { useErrorModal } from '@/composables/useErrorModal.js'
import { useModal } from '@/composables/useModal.js'
import { usePermissions } from '@/composables/usePermissions.js'
import cotacaoRascunhoService from '@/services/cotacaoRascunhoService.js'
import relatorioService from '@/services/relatorioService.js'
import logger from '@/utils/logger.js'

export default {
  name: 'StepAdicionarCotacoesRascunho',
  props: {
    rascunho: {
      type: Object,
      required: true
    },
    cotacoes: {
      type: Array,
      default: () => []
    },
    fornecedores: {
      type: Array,
      default: () => []
    },
    carregando: {
      type: Boolean,
      default: false
    }
  },
  emits: ['save-cotacao', 'delete-cotacao', 'view-pdf'],
  setup(props, { emit }) {
    const { success, error: showError, warning } = useToast()
    const { permissions } = usePermissions()
    const showFormulario = ref(false)

    // Bloqueia scroll do body quando modal está aberto
    useModal(showFormulario)

    const salvando = ref(false)
    const gerandoRelatorio = ref(false)
    const fornecedorSelecionado = ref('')
    const arquivosPdf = ref([])
    const precoFormatado = ref('')
    const fileInput = ref(null)
    const fornecedorInput = ref(null)
    const buscaFornecedor = ref('')
    const showFornecedorDropdown = ref(false)
    const fornecedorSelecionadoNome = ref('')

    // Visualizador de PDF
    const pdfAberto = ref(null)
    const carregandoPdf = ref(false)
    const erroPdf = ref(null)
    const pdfUrl = ref(null)

    const novaCotacao = ref({
      fornecedorId: null,
      tipoFornecedor: '',
      itensRascunhoIds: [],
      preco: null,
      prazoEmDiasUteis: null,
      dataLimite: null,
      anexosPdf: [],
      gastoPrevisto: false,
      projeto: null
    })

    const validarTeclaPreco = (event) => {
      // Bloquear letras - permitir apenas números, backspace, delete, arrows
      const char = event.key
      // Permitir teclas de controle
      if (event.ctrlKey || event.metaKey) return
      // Bloquear se não for número
      if (!/^\d$/.test(char)) {
        event.preventDefault()
      }
    }

    const handlePrecoInput = (event) => {
      // Remove tudo que não é dígito do valor digitado
      let valor = event.target.value.replace(/\D/g, '')

      // Se vazio, limpa tudo
      if (!valor) {
        precoFormatado.value = ''
        novaCotacao.value.preco = null
        event.target.value = ''
        return
      }

      // Converte para centavos e depois para reais
      let centavos = parseInt(valor)
      let reais = centavos / 100

      // Formata com duas casas decimais
      const valorFormatado = reais.toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })

      // Atualiza o ref e o input diretamente
      precoFormatado.value = valorFormatado
      event.target.value = valorFormatado

      // Salva o valor numérico
      novaCotacao.value.preco = reais
    }

    const formatarPrecoOnBlur = () => {
      // Não precisa fazer nada, já está formatado
    }

    const removerPdfIndividual = (index) => {
      arquivosPdf.value.splice(index, 1)
      novaCotacao.value.anexosPdf.splice(index, 1)
    }

    const limparTodosPdfs = () => {
      arquivosPdf.value = []
      novaCotacao.value.anexosPdf = []
      if (fileInput.value) {
        fileInput.value.value = ''
      }
    }

    const onFornecedorChange = () => {
      if (fornecedorSelecionado.value) {
        const [tipo, id] = fornecedorSelecionado.value.split('-')
        novaCotacao.value.tipoFornecedor = tipo
        novaCotacao.value.fornecedorId = parseInt(id)
      } else {
        novaCotacao.value.tipoFornecedor = ''
        novaCotacao.value.fornecedorId = null
      }
    }

    const selecionarFornecedor = (tipo, fornecedor) => {
      fornecedorSelecionado.value = `${tipo}-${fornecedor.id}`
      fornecedorSelecionadoNome.value = fornecedor.razaoSocial
      novaCotacao.value.tipoFornecedor = tipo
      novaCotacao.value.fornecedorId = fornecedor.id
      buscaFornecedor.value = ''
      showFornecedorDropdown.value = false
    }

    const limparFornecedor = () => {
      fornecedorSelecionado.value = ''
      fornecedorSelecionadoNome.value = ''
      novaCotacao.value.tipoFornecedor = ''
      novaCotacao.value.fornecedorId = null
      buscaFornecedor.value = ''
      showFornecedorDropdown.value = false

      // Remove foco do input para atualizar o placeholder
      nextTick(() => {
        if (fornecedorInput.value) {
          fornecedorInput.value.blur()
        }
      })
    }

    const fornecedoresProduto = computed(() =>
      props.fornecedores.filter(f => f.tipo === 'PRODUTO' || !f.tipo)
    )

    const fornecedoresServico = computed(() =>
      props.fornecedores.filter(f => f.tipo === 'SERVICO')
    )

    const fornecedoresProdutoFiltrados = computed(() => {
      if (!buscaFornecedor.value) return fornecedoresProduto.value
      const busca = buscaFornecedor.value.toLowerCase()
      return fornecedoresProduto.value.filter(f =>
        f.razaoSocial?.toLowerCase().includes(busca) ||
        f.nomeFantasia?.toLowerCase().includes(busca) ||
        f.cnpj?.includes(busca)
      )
    })

    const fornecedoresServicoFiltrados = computed(() => {
      if (!buscaFornecedor.value) return fornecedoresServico.value
      const busca = buscaFornecedor.value.toLowerCase()
      return fornecedoresServico.value.filter(f =>
        f.razaoSocial?.toLowerCase().includes(busca) ||
        f.nomeFantasia?.toLowerCase().includes(busca) ||
        f.cnpj?.includes(busca)
      )
    })

    const cotacaoValida = computed(() => {
      return novaCotacao.value.fornecedorId &&
             novaCotacao.value.tipoFornecedor &&
             novaCotacao.value.itensRascunhoIds.length > 0 &&
             novaCotacao.value.preco > 0
    })

    const formatarData = (data) => {
      if (!data) return 'N/A'
      try {
        return new Date(data).toLocaleDateString('pt-BR')
      } catch {
        return 'Data inválida'
      }
    }

    const formatarPreco = (preco) => {
      if (!preco) return '0,00'
      return parseFloat(preco).toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }

    const getNomeItem = (itemId) => {
      const item = props.rascunho.itens?.find(i => i.id === itemId)
      return item ? item.nome : `Item #${itemId}`
    }

    const itemTemCotacao = (itemId) => {
      return props.cotacoes.some(c =>
        c.itensRascunhoIds && c.itensRascunhoIds.includes(itemId)
      )
    }

    const gerarRelatorio = async () => {
      if (!props.rascunho?.id) {
        showError('Erro: Rascunho não encontrado')
        return
      }

      try {
        gerandoRelatorio.value = true

        // Gera relatório com todos os itens do rascunho
        await relatorioService.visualizarRelatorioItensParaCotacaoRascunho(
          props.rascunho.id,
          [] // Array vazio = todos os itens
        )

      } catch (error) {
        logger.error('❌ Erro ao gerar relatório:', error)
        showError('Erro ao gerar relatório. Tente novamente.')
      } finally {
        gerandoRelatorio.value = false
      }
    }

    const abrirFormularioCotacao = () => {
      fornecedorSelecionado.value = ''
      fornecedorSelecionadoNome.value = ''
      precoFormatado.value = ''
      arquivosPdf.value = []
      buscaFornecedor.value = ''
      showFornecedorDropdown.value = false
      novaCotacao.value = {
        fornecedorId: null,
        tipoFornecedor: '',
        itensRascunhoIds: [],
        preco: null,
        prazoEmDiasUteis: null,
        dataLimite: null,
        anexosPdf: [],
        gastoPrevisto: false,
        projeto: null
      }
      showFormulario.value = true
    }

    const fecharFormulario = () => {
      showFormulario.value = false
    }

    const toggleItemCheckbox = (itemId) => {
      const index = novaCotacao.value.itensRascunhoIds.indexOf(itemId)
      if (index > -1) {
        // Remove o item se já está selecionado
        novaCotacao.value.itensRascunhoIds.splice(index, 1)
      } else {
        // Adiciona o item se não está selecionado
        novaCotacao.value.itensRascunhoIds.push(itemId)
      }
    }

    const handleFileUpload = (event) => {
      const files = event.target.files
      const MAX_FILE_SIZE = 5 * 1024 * 1024 // 5MB por arquivo
      const MAX_TOTAL_SIZE = 20 * 1024 * 1024 // 20MB total

      if (files && files.length > 0) {
        // Calcular tamanho total atual
        let tamanhoAtual = arquivosPdf.value.reduce((total, arq) => total + (arq.bytes?.length || 0), 0)

        Array.from(files).forEach(file => {
          // Validar tipo de arquivo
          if (file.type !== 'application/pdf') {
            warning(`Arquivo "${file.name}" não é um PDF válido. Apenas arquivos PDF são permitidos.`)
            return
          }

          // Validar tamanho individual
          if (file.size > MAX_FILE_SIZE) {
            warning(`Arquivo "${file.name}" excede o limite de 5MB. Tamanho: ${(file.size / 1024 / 1024).toFixed(2)}MB`)
            return
          }

          // Validar tamanho total
          if (tamanhoAtual + file.size > MAX_TOTAL_SIZE) {
            warning(`Limite total de 20MB excedido. Remova alguns arquivos antes de adicionar novos.`)
            return
          }

          tamanhoAtual += file.size

          const reader = new FileReader()
          reader.onload = (e) => {
            // Converter para Base64 (remover prefixo data:application/pdf;base64,)
            const base64 = e.target.result.split(',')[1]
            // Converter Base64 para array de bytes
            const binaryString = atob(base64)
            const bytes = new Array(binaryString.length)
            for (let i = 0; i < binaryString.length; i++) {
              bytes[i] = binaryString.charCodeAt(i)
            }
            // Adicionar ao array de PDFs
            arquivosPdf.value.push({ nome: file.name, bytes: bytes, tamanho: file.size })
            novaCotacao.value.anexosPdf.push(bytes)
          }
          reader.readAsDataURL(file)
        })
        // Limpar o input para permitir selecionar os mesmos arquivos novamente
        if (fileInput.value) {
          fileInput.value.value = ''
        }
      }
    }

    const confirmarDeleteCotacao = (cotacao) => {
      const { showWarning } = useErrorModal()
      const nomeFornecedor = cotacao.nomeFornecedor || 'Fornecedor'

      showWarning(`Tem certeza que deseja remover a cotação de "${nomeFornecedor}"?\n\nEsta ação não pode ser desfeita.`, {
        title: 'Remover Cotação?',
        confirmText: 'Sim, remover',
        cancelText: 'Cancelar',
        onConfirm: () => {
          emit('delete-cotacao', cotacao.id)
        }
      })
    }

    const salvarCotacao = async () => {
      if (!cotacaoValida.value) return

      salvando.value = true
      try {
        emit('save-cotacao', {
          fornecedorId: parseInt(novaCotacao.value.fornecedorId),
          tipoFornecedor: novaCotacao.value.tipoFornecedor,
          itensRascunhoIds: novaCotacao.value.itensRascunhoIds,
          preco: parseFloat(novaCotacao.value.preco),
          prazoEmDiasUteis: novaCotacao.value.prazoEmDiasUteis ? parseInt(novaCotacao.value.prazoEmDiasUteis) : null,
          dataLimite: novaCotacao.value.dataLimite || null,
          gastoPrevisto: novaCotacao.value.gastoPrevisto,
          projeto: novaCotacao.value.projeto || null,
          // Manter compatibilidade: enviar primeiro PDF como anexoPdf e todos como anexosPdf
          anexoPdf: novaCotacao.value.anexosPdf.length > 0 ? novaCotacao.value.anexosPdf[0] : null,
          anexosPdf: novaCotacao.value.anexosPdf.length > 0 ? novaCotacao.value.anexosPdf : null
        })
        fecharFormulario()
      } finally {
        salvando.value = false
      }
    }

    // Função para abrir/fechar visualizador de PDF
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

      // Abrir novo PDF
      pdfAberto.value = pdfKey
      carregandoPdf.value = true
      erroPdf.value = null
      pdfUrl.value = null

      try {
        // Buscar o PDF do backend usando o endpoint específico de anexo
        const blob = await cotacaoRascunhoService.obterAnexoPdf(props.rascunho.id, cotacao.id, pdfIndex)

        if (blob && blob.size > 0) {
          // Criar URL do blob para o iframe
          pdfUrl.value = URL.createObjectURL(blob)
        } else {
          throw new Error('Nenhum PDF encontrado para esta cotação')
        }
      } catch (error) {
        logger.error('Erro ao carregar PDF:', error)

        // Mensagem mais amigável para erro 404
        if (error.message.includes('404')) {
          erroPdf.value = 'PDF não encontrado. A cotação pode não ter um anexo salvo.'
        } else {
          erroPdf.value = error.message || 'Erro ao carregar PDF'
        }

        // Fechar o visualizador em caso de erro
        pdfAberto.value = null
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

    // Limpar URLs de blob ao destruir componente
    onBeforeUnmount(() => {
      fecharPdfViewer()
    })

    return {
      permissions,
      showFormulario,
      salvando,
      gerandoRelatorio,
      fornecedorSelecionado,
      novaCotacao,
      fornecedoresProduto,
      fornecedoresServico,
      fornecedoresProdutoFiltrados,
      fornecedoresServicoFiltrados,
      cotacaoValida,
      formatarData,
      formatarPreco,
      getNomeItem,
      itemTemCotacao,
      gerarRelatorio,
      abrirFormularioCotacao,
      fecharFormulario,
      toggleItemCheckbox,
      handleFileUpload,
      salvarCotacao,
      onFornecedorChange,
      // Novas funções de preço e PDF
      precoFormatado,
      handlePrecoInput,
      validarTeclaPreco,
      formatarPrecoOnBlur,
      arquivosPdf,
      removerPdfIndividual,
      limparTodosPdfs,
      fileInput,
      fornecedorInput,
      // Busca e seleção de fornecedor
      buscaFornecedor,
      showFornecedorDropdown,
      fornecedorSelecionadoNome,
      selecionarFornecedor,
      limparFornecedor,
      // Visualizador de PDF
      pdfAberto,
      carregandoPdf,
      erroPdf,
      pdfUrl,
      togglePdfViewer,
      fecharPdfViewer,
      // Confirmação de delete
      confirmarDeleteCotacao
    }
  }
}
</script>

<style scoped>
.cotacoes-container {
  padding: 0;
}

/* Info Box */
.rascunho-info-box {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 32px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.info-title {
  font-size: 1.0625rem;
  font-weight: 700;
  color: #1F285F;
  margin: 0 0 12px 0;
  letter-spacing: -0.01em;
}

.info-grid {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  gap: 8px;
}

.info-label {
  color: #64748b;
  font-size: 0.875rem;
}

.info-value {
  color: #1e293b;
  font-weight: 500;
  font-size: 0.875rem;
}

/* Section Header */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 4px 0;
  letter-spacing: -0.01em;
}

.btn-relatorio {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-relatorio:hover:not(:disabled) {
  background: #059669;
}

.btn-relatorio:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background: #9ca3af;
}

.loading-spinner-small {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.btn-add-cotacao {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #1F285F;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-add-cotacao:hover {
  background: #2d3a7f;
}

/* Cotações Lista */
.cotacoes-lista {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
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
  font-size: 0.9375rem;
  font-weight: 600;
  color: #0f172a;
  letter-spacing: -0.01em;
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
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.btn-pdf, .btn-delete {
  padding: 8px 14px;
  border-radius: 6px;
  font-size: 0.8125rem;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.btn-pdf {
  background: #1F285F;
  color: white;
  box-shadow: 0 1px 3px rgba(31, 40, 95, 0.3);
}

.btn-pdf:hover {
  background: #2d3a7f;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(31, 40, 95, 0.4);
}

.btn-delete {
  background: #ef4444;
  color: white;
  box-shadow: 0 1px 3px rgba(239, 68, 68, 0.3);
}

.btn-delete:hover {
  background: #dc2626;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(239, 68, 68, 0.4);
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #6b7280;
}

.empty-state p {
  margin: 12px 0 4px;
  font-weight: 500;
}

.empty-state span {
  font-size: 0.875rem;
}

/* Loading */
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px;
  color: #6b7280;
}

.loading-spinner {
  width: 24px;
  height: 24px;
  border: 3px solid #e5e7eb;
  border-top-color: #1F285F;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Itens Resumo */
.itens-resumo {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  margin-top: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.itens-lista {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 12px;
}

.item-resumo {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 14px 16px;
  border-radius: 8px;
  border: 2px solid #e5e7eb;
  transition: all 0.2s;
}

.item-resumo:hover {
  border-color: #d1d5db;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.item-resumo.item-cotado {
  border-color: #86efac;
  background: #f0fdf4;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.item-nome {
  font-size: 0.875rem;
  font-weight: 500;
  color: #1e293b;
}

.item-qtd {
  font-size: 0.75rem;
  color: #64748b;
}

.status-cotado {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #059669;
  font-size: 0.8125rem;
  font-weight: 500;
  padding: 4px 10px;
  background: #d1fae5;
  border-radius: 12px;
}

.status-pendente {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #d97706;
  font-size: 0.8125rem;
  font-weight: 500;
  padding: 4px 10px;
  background: #fed7aa;
  border-radius: 12px;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100vw;
  height: 100vh;
  min-height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9998; /* Aumentado para cobrir toda a tela, mas abaixo dos toasts (9999) */
  overflow-y: auto; /* Permite scroll no modal se necessário */
}

.modal-cotacao {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.125rem;
  color: #1e293b;
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #6b7280;
  cursor: pointer;
  outline: none;
  line-height: 1;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.btn-close:hover {
  background: #f3f4f6;
  color: #374151;
}

.btn-close:focus {
  outline: none;
  box-shadow: 0 0 0 2px #e5e7eb;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.form-group label:has(input[type="checkbox"]) {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: normal;
  cursor: pointer;
}

.form-group input[type="checkbox"] {
  width: 16px;
  height: 16px;
  margin: 0;
  cursor: pointer;
  flex-shrink: 0;
}

.form-group input:not([type="checkbox"]),
.form-group select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.radio-group {
  display: flex;
  gap: 16px;
}

.radio-group label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: normal;
  cursor: pointer;
}

/* Checkbox lista estilizada */
.checkbox-lista-styled {
  max-height: 180px;
  overflow-y: auto;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px;
  background: #f9fafb;
}

.checkbox-item-styled {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: normal;
  background: white;
  border: 2px solid #e5e7eb;
  margin-bottom: 8px;
  transition: all 0.2s;
}

.checkbox-item-styled:last-child {
  margin-bottom: 0;
}

.checkbox-item-styled:hover {
  border-color: #1F285F;
}

.checkbox-item-styled.selected {
  border-color: #10b981;
  background: #ecfdf5;
}

.checkbox-container {
  position: relative;
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.checkbox-hidden {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  width: 100%;
  height: 100%;
}

.checkbox-custom {
  width: 22px;
  height: 22px;
  min-width: 22px;
  min-height: 22px;
  border: 2px solid #d1d5db;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s;
}

.checkbox-item-styled.selected .checkbox-custom {
  background: #10b981;
  border-color: #10b981;
  color: white;
}

.checkbox-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.checkbox-nome {
  font-weight: 500;
  font-size: 0.875rem;
  color: #1e293b;
}

.checkbox-qtd {
  font-size: 0.75rem;
  color: #64748b;
}

.selected-count {
  display: block;
  margin-top: 8px;
  font-size: 0.75rem;
  color: #10b981;
  font-weight: 500;
}

/* Preço input estilizado */
.preco-input-container {
  display: flex;
  align-items: center;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  overflow: hidden;
}

.preco-prefix {
  padding: 10px 12px;
  background: #f3f4f6;
  border-right: 1px solid #d1d5db;
  color: #6b7280;
  font-weight: 500;
  font-size: 0.875rem;
}

.preco-input {
  flex: 1;
  padding: 10px 12px;
  border: none;
  font-size: 0.875rem;
  outline: none;
}

.preco-input:focus {
  box-shadow: inset 0 0 0 2px rgba(59, 130, 246, 0.3);
}

/* PDF upload estilizado */
.pdf-upload-container {
  position: relative;
}

.pdf-file-input {
  position: absolute;
  width: 0;
  height: 0;
  opacity: 0;
}

.pdf-upload-label {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 24px;
  border: 2px dashed #d1d5db;
  border-radius: 8px;
  background: #f9fafb;
  cursor: pointer;
  transition: all 0.2s;
  color: #6b7280;
  text-align: center;
  font-size: 0.875rem;
}

.pdf-upload-label > span {
  display: block;
  width: 100%;
  text-align: center;
}

.pdf-upload-label:hover {
  border-color: #1F285F;
  background: #f9fafb;
  color: #1F285F;
}

.arquivo-selecionado {
  color: #10b981;
  font-weight: 500;
  word-break: break-all;
}

.upload-hint {
  display: block;
  font-size: 0.75rem;
  color: #9ca3af;
  margin-top: 4px;
  text-align: center;
  line-height: 1.5;
}

/* Lista de PDFs anexados */
.pdf-list {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.pdf-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
}

.pdf-item-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.pdf-item-name {
  font-size: 0.8125rem;
  color: #374151;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pdf-item-size {
  font-size: 0.6875rem;
  color: #9ca3af;
  white-space: nowrap;
  margin-left: 8px;
}

.pdf-total-size {
  font-size: 0.75rem;
  color: #6b7280;
  text-align: right;
  padding: 8px 0 0;
  border-top: 1px solid #e5e7eb;
  margin-top: 8px;
}

.btn-remove-pdf-item {
  width: 24px;
  height: 24px;
  border: none;
  border-radius: 50%;
  background: #fee2e2;
  color: #dc2626;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s;
}

.btn-remove-pdf-item:hover {
  background: #dc2626;
  color: white;
}

.btn-remove-pdf {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 50%;
  background: #fee2e2;
  color: #dc2626;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.btn-remove-pdf:hover {
  background: #dc2626;
  color: white;
}

/* Autocomplete de fornecedor */
.fornecedor-autocomplete {
  position: relative;
}

.autocomplete-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.autocomplete-input-wrapper .search-icon-small {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  z-index: 1;
  pointer-events: none;
  width: 16px;
  height: 16px;
}

.fornecedor-autocomplete .autocomplete-input {
  width: 100%;
  padding: 12px 70px 12px 40px !important;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  transition: all 0.2s;
  background: white;
  box-sizing: border-box;
}

.autocomplete-input:focus {
  outline: none;
  border-color: #1F285F;
  box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.1);
}

.autocomplete-input.has-selection {
  border-color: #10b981;
}

.autocomplete-input.has-selection::placeholder {
  color: #10b981;
  font-weight: 500;
}

.clear-selection-btn {
  position: absolute;
  right: 36px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #9ca3af;
  cursor: pointer;
  padding: 4px;
  line-height: 0;
  transition: color 0.2s;
}

.clear-selection-btn:hover {
  color: #dc2626;
}

.clear-selection-btn:focus {
  outline: none;
}

.dropdown-toggle-btn {
  position: absolute;
  right: 8px;
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  color: #6b7280;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.dropdown-toggle-btn svg.rotated {
  transform: rotate(180deg);
}

.autocomplete-dropdown {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
  z-index: 100;
  max-height: 280px;
  overflow-y: auto;
}

.dropdown-empty {
  padding: 16px;
  text-align: center;
  color: #6b7280;
  font-size: 0.875rem;
}

.dropdown-content {
  padding: 8px 0;
}

.dropdown-group {
  margin-bottom: 8px;
}

.dropdown-group:last-child {
  margin-bottom: 0;
}

.dropdown-group-label {
  padding: 8px 12px;
  font-size: 0.75rem;
  font-weight: 600;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  background: #f9fafb;
}

.dropdown-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  cursor: pointer;
  transition: all 0.15s;
}

.dropdown-item:hover {
  background: #f3f4f6;
}

.dropdown-item.selected {
  background: #ecfdf5;
}

.dropdown-item .item-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.dropdown-item .item-name {
  font-size: 0.875rem;
  font-weight: 500;
  color: #1e293b;
}

.dropdown-item .item-fantasia {
  font-size: 0.75rem;
  color: #64748b;
}

.dropdown-item .check-icon {
  color: #10b981;
  flex-shrink: 0;
}

.autocomplete-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 99;
}

.fornecedor-selecionado-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  padding: 6px 12px;
  background: #ecfdf5;
  border: 1px solid #10b981;
  border-radius: 6px;
  font-size: 0.8125rem;
  color: #065f46;
}

.fornecedor-selecionado-tag .tipo-tag {
  padding: 2px 6px;
  background: #10b981;
  color: white;
  border-radius: 4px;
  font-size: 0.6875rem;
  font-weight: 600;
}

/* Visualizador de PDF retrátil */
.pdf-viewer-container {
  margin-top: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
  background: #f9fafb;
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    max-height: 0;
  }
  to {
    opacity: 1;
    max-height: 600px;
  }
}

.pdf-loading,
.pdf-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  gap: 12px;
  color: #6b7280;
}

.pdf-error {
  color: #dc2626;
}

.pdf-viewer-wrapper {
  position: relative;
  width: 100%;
  height: 450px;
  background: white;
}

.pdf-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

.btn-pdf-active {
  background: #dc2626 !important;
}

.btn-pdf-active:hover {
  background: #b91c1c !important;
}

/* Manter estilos antigos para compatibilidade */
.checkbox-lista {
  max-height: 150px;
  overflow-y: auto;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 8px;
}

.checkbox-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: normal;
}

.checkbox-item:hover {
  background: #f9fafb;
}

.toggle-button-group {
  display: flex;
  gap: 8px;
  margin-top: 4px;
}

.toggle-option {
  flex: 1;
  padding: 10px 20px;
  border: 2px solid #d1d5db;
  background: white;
  color: #6b7280;
  font-size: 0.875rem;
  font-weight: 500;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.toggle-option:hover {
  border-color: #1F285F;
  color: #1F285F;
}

.toggle-option.active {
  background: #1F285F;
  border-color: #1F285F;
  color: white;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #e5e7eb;
}

.btn-cancel,
.btn-save {
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
}

.btn-cancel {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
}

.btn-save {
  background: #1F285F;
  color: white;
  border: none;
}

.btn-save:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}
</style>
