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
        <button
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
              {{ pdfAberto === `${cotacao.id}-0` ? 'Fechar PDF' : 'Ver PDF' }}
            </button>
            <button
              @click="confirmarDeleteCotacao(cotacao)"
              class="btn-delete"
              :aria-label="`Remover cotação de ${cotacao.nomeFornecedor || 'Fornecedor'}`"
            >
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
              Sem cotação
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de Nova Cotação -->
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
                  @click="limparFornecedor"
                  class="clear-selection-btn"
                  title="Limpar seleção"
                >
                  <svg viewBox="0 0 24 24" width="14" height="14">
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
              <label
                v-for="item in rascunho.itens"
                :key="item.id"
                class="checkbox-item-styled"
                :class="{ 'selected': novaCotacao.itensRascunhoIds.includes(item.id) }"
              >
                <input
                  type="checkbox"
                  :value="item.id"
                  v-model="novaCotacao.itensRascunhoIds"
                  class="checkbox-hidden"
                >
                <div class="checkbox-custom">
                  <svg v-if="novaCotacao.itensRascunhoIds.includes(item.id)" viewBox="0 0 24 24" width="14" height="14">
                    <path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                  </svg>
                </div>
                <div class="checkbox-content">
                  <span class="checkbox-nome">{{ item.nome }}</span>
                  <span class="checkbox-qtd">Quantidade: {{ item.quantidade }}</span>
                </div>
              </label>
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
                min="1"
                placeholder="Ex: 15"
              >
            </div>
            <div class="form-group">
              <label>Validade da Cotação</label>
              <input
                type="date"
                v-model="novaCotacao.dataLimite"
              >
            </div>
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
                id="pdf-upload"
                multiple
              >
              <label for="pdf-upload" class="pdf-upload-label">
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
  </div>
</template>

<script>
import { ref, computed, onBeforeUnmount } from 'vue'
import cotacaoRascunhoService from '@/services/cotacaoRascunhoService.js'

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
    const showFormulario = ref(false)
    const salvando = ref(false)
    const fornecedorSelecionado = ref('')
    const arquivosPdf = ref([])
    const precoFormatado = ref('')
    const fileInput = ref(null)
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
      anexosPdf: []
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
        anexosPdf: []
      }
      showFormulario.value = true
    }

    const fecharFormulario = () => {
      showFormulario.value = false
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
            alert(`Arquivo "${file.name}" não é um PDF válido. Apenas arquivos PDF são permitidos.`)
            return
          }

          // Validar tamanho individual
          if (file.size > MAX_FILE_SIZE) {
            alert(`Arquivo "${file.name}" excede o limite de 5MB. Tamanho: ${(file.size / 1024 / 1024).toFixed(2)}MB`)
            return
          }

          // Validar tamanho total
          if (tamanhoAtual + file.size > MAX_TOTAL_SIZE) {
            alert(`Limite total de 20MB excedido. Remova alguns arquivos antes de adicionar novos.`)
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
      const nomeFornecedor = cotacao.nomeFornecedor || 'Fornecedor'
      const confirmado = confirm(`Tem certeza que deseja remover a cotação de "${nomeFornecedor}"?\n\nEsta ação não pode ser desfeita.`)
      if (confirmado) {
        emit('delete-cotacao', cotacao)
      }
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
        console.error('Erro ao carregar PDF:', error)
        erroPdf.value = error.message || 'Erro ao carregar PDF'
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
      showFormulario,
      salvando,
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
      abrirFormularioCotacao,
      fecharFormulario,
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
  background: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
}

.info-title {
  font-size: 1rem;
  font-weight: 600;
  color: #1e40af;
  margin: 0 0 12px 0;
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

.section-title {
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
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
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.btn-pdf, .btn-delete {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 0.75rem;
  cursor: pointer;
  border: 1px solid;
}

.btn-pdf {
  background: #f0f9ff;
  color: #0369a1;
  border-color: #bae6fd;
}

.btn-delete {
  background: #fef2f2;
  color: #dc2626;
  border-color: #fecaca;
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
  border-radius: 8px;
  padding: 16px;
  margin-top: 24px;
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
  padding: 10px 12px;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
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
  gap: 4px;
  color: #059669;
  font-size: 0.75rem;
  font-weight: 500;
}

.status-pendente {
  color: #f59e0b;
  font-size: 0.75rem;
}

/* Modal */
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
  z-index: 1200;
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

.form-group input,
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
  gap: 12px;
  padding: 12px;
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
  border-color: #3b82f6;
}

.checkbox-item-styled.selected {
  border-color: #10b981;
  background: #ecfdf5;
}

.checkbox-hidden {
  position: absolute;
  opacity: 0;
  cursor: pointer;
}

.checkbox-custom {
  width: 20px;
  height: 20px;
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

.pdf-upload-label:hover {
  border-color: #3b82f6;
  background: #eff6ff;
  color: #3b82f6;
}

.arquivo-selecionado {
  color: #10b981;
  font-weight: 500;
  word-break: break-all;
}

.upload-hint {
  font-size: 0.75rem;
  color: #9ca3af;
  margin-top: 4px;
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
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
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
  transition: all 0.2s;
}

.clear-selection-btn:hover {
  background: #dc2626;
  color: white;
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

.checkbox-label {
  font-size: 0.875rem;
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
