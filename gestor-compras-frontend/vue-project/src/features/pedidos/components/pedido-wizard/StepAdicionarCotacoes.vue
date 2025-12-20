<template>
  <div class="page-3-container">
    <!-- Contexto do Pedido e Item -->
    <div class="context-section">
      <!-- Pedido Info -->
      <div class="context-box pedido-context">
        <h4 class="context-title">📋 Pedido</h4>
        <div class="context-grid">
          <div class="context-item">
            <span class="context-label">Data:</span>
            <span class="context-value">{{ formatarData(pedido.dataCriacao) }}</span>
          </div>
          <div class="context-item">
            <span class="context-label">Status:</span>
            <span class="status-badge" :class="`status-${pedido.status?.toLowerCase()}`">
              {{ formatarStatus(pedido.status) }}
            </span>
          </div>
          <div class="context-item full-width">
            <span class="context-label">Objetivo:</span>
            <span class="context-value">{{ pedido.objetivo || 'Sem descrição' }}</span>
          </div>
        </div>
      </div>

      <!-- Item Info -->
      <div class="context-box item-context">
        <h4 class="context-title">📦 Item Selecionado</h4>
        <div class="item-details">
          <div class="item-row">
            <span class="item-label">Nome:</span>
            <span class="item-value">{{ item.nome }}</span>
          </div>
          <div class="item-row">
            <span class="item-label">Quantidade:</span>
            <span class="item-value highlight">{{ item.quantidade }}x</span>
          </div>
          <div v-if="item.descricao" class="item-row">
            <span class="item-label">Descrição:</span>
            <span class="item-value">{{ item.descricao }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Formulário de Cotações -->
    <div class="cotacoes-section">
      <div class="section-header">
        <h3 class="section-title">💰 Adicionar Cotações</h3>
        <div class="header-actions">
          <button
            type="button"
            @click="gerarRelatorio"
            class="btn-relatorio"
            :disabled="gerandoRelatorio || !pedido.id"
            title="Gerar relatório de itens para enviar aos fornecedores"
          >
            <svg v-if="!gerandoRelatorio" viewBox="0 0 24 24" width="16" height="16">
              <path fill="white" d="M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z"/>
            </svg>
            <span v-if="gerandoRelatorio" class="loading-spinner-small"></span>
            <span v-if="!gerandoRelatorio">Gerar Relatório</span>
          </button>
          <button
            type="button"
            @click="adicionarCotacao"
            class="btn-add-cotacao"
          >
            <svg viewBox="0 0 24 24" width="16" height="16">
              <path fill="white" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
            </svg>
            Adicionar Cotação
          </button>
        </div>
      </div>

      <!-- Loading Fornecedores -->
      <div v-if="carregandoFornecedores" class="loading-state">
        <div class="loading-spinner"></div>
        <span>Carregando fornecedores...</span>
      </div>

      <!-- Lista de Cotações -->
      <div v-else-if="cotacoes.length > 0" class="cotacoes-lista">
        <CotacaoFormItem
          v-for="(cotacao, index) in cotacoes"
          :key="index"
          :cotacao="cotacao"
          :numero="index + 1"
          :fornecedores="fornecedores"
          @update="atualizarCotacao(index, $event)"
          @remove="removerCotacao(index)"
        />
      </div>

      <div v-else class="empty-state">
        <svg viewBox="0 0 24 24" width="48" height="48">
          <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1.41 16.09V20h-2.67v-1.93c-1.71-.36-3.16-1.46-3.27-3.4h1.96c.1 1.05.82 1.87 2.65 1.87 1.96 0 2.4-.98 2.4-1.59 0-.83-.44-1.61-2.67-2.14-2.48-.6-4.18-1.62-4.18-3.67 0-1.72 1.39-2.84 3.11-3.21V4h2.67v1.95c1.86.45 2.79 1.86 2.85 3.39H14.3c-.05-1.11-.64-1.87-2.22-1.87-1.5 0-2.4.68-2.4 1.64 0 .84.65 1.39 2.67 1.91s4.18 1.39 4.18 3.91c-.01 1.83-1.38 2.83-3.12 3.16z"/>
        </svg>
        <p>Nenhuma cotação adicionada ainda.</p>
        <p class="hint">Clique em "Adicionar Cotação" para começar.</p>
      </div>
    </div>

    <!-- Resumo -->
    <div v-if="cotacoes.length > 0" class="resumo-cotacoes">
      <div class="resumo-item">
        <span class="resumo-label">Total de Cotações:</span>
        <span class="resumo-value">{{ cotacoes.length }}</span>
      </div>
      <div class="resumo-item">
        <span class="resumo-label">Cotações Completas:</span>
        <span class="resumo-value">{{ cotacoesCompletas }}</span>
      </div>
    </div>

    <!-- Mensagens de Validação -->
    <div v-if="validationErrors.length > 0" class="validation-errors">
      <div class="error-title">⚠️ Corrija os seguintes problemas:</div>
      <ul>
        <li v-for="(error, index) in validationErrors" :key="index">{{ error }}</li>
      </ul>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch, onMounted } from 'vue'
import { useToast } from '@/composables/useToast.js'
import CotacaoFormItem from './CotacaoInlineForm.vue'
import relatorioService from '@/services/relatorioService.js'
import logger from '@/utils/logger.js'

export default {
  name: 'PedidoFormPage3',
  components: {
    CotacaoFormItem
  },
  props: {
    pedido: {
      type: Object,
      required: true
    },
    item: {
      type: Object,
      required: true
    },
    fornecedores: {
      type: Array,
      default: () => []
    },
    carregandoFornecedores: {
      type: Boolean,
      default: false
    },
    modelValue: {
      type: Array,
      default: () => []
    }
  },
  emits: ['update:modelValue', 'validation-change'],
  setup(props, { emit }) {
    const { error: showError } = useToast()
    // Estado local
    const cotacoes = ref([...props.modelValue])
    const gerandoRelatorio = ref(false)

    // Computed
    const formatarData = (data) => {
      if (!data) return 'N/A'
      try {
        return new Date(data).toLocaleDateString('pt-BR')
      } catch {
        return 'Data inválida'
      }
    }

    const formatarStatus = (status) => {
      const labels = {
        'RASCUNHO': 'Rascunho',
        'PENDENTE': 'Pendente',
        'EM_ANALISE': 'Em Análise',
        'EM_ANDAMENTO': 'Em Andamento',
        'APROVADO': 'Aprovado',
        'CANCELADO': 'Cancelado'
      }
      return labels[status] || status
    }

    const cotacoesCompletas = computed(() => {
      return cotacoes.value.filter(c =>
        c.fornecedorId &&
        c.preco > 0 &&
        c.dataLimite
      ).length
    })

    const validationErrors = computed(() => {
      const errors = []

      cotacoes.value.forEach((cotacao, index) => {
        if (!cotacao.fornecedorId) {
          errors.push(`Cotação ${index + 1}: Selecione um fornecedor`)
        }
        if (!cotacao.tipoFornecedor) {
          errors.push(`Cotação ${index + 1}: Selecione o tipo de fornecedor`)
        }
        if (!cotacao.preco || cotacao.preco <= 0) {
          errors.push(`Cotação ${index + 1}: Informe um valor válido`)
        }
        if (!cotacao.dataLimite) {
          errors.push(`Cotação ${index + 1}: Informe a data de validade`)
        }
      })

      return errors
    })

    const isValid = computed(() => {
      return cotacoes.value.length > 0 && validationErrors.value.length === 0
    })

    // Métodos
    const gerarRelatorio = async () => {
      if (!props.pedido?.id) {
        showError('Erro: Pedido não encontrado')
        return
      }

      try {
        gerandoRelatorio.value = true

        // Se houver item selecionado, incluir apenas ele, senão incluir todos
        const itensIds = props.item?.id ? [props.item.id] : []

        await relatorioService.visualizarRelatorioItensParaCotacao(
          props.pedido.id,
          itensIds
        )

      } catch (error) {
        logger.error('❌ Erro ao gerar relatório:', error)
        showError('Erro ao gerar relatório. Tente novamente.')
      } finally {
        gerandoRelatorio.value = false
      }
    }

    const adicionarCotacao = () => {
      cotacoes.value.push({
        fornecedorId: null,
        tipoFornecedor: 'PRODUTO',
        preco: null,
        prazoEmDiasUteis: null,
        dataLimite: null,
        anexoPdf: null,
        pdfFile: null,
        gastoPrevisto: false,
        projeto: null
      })
    }

    const removerCotacao = (index) => {
      cotacoes.value.splice(index, 1)
    }

    const atualizarCotacao = (index, data) => {
      cotacoes.value[index] = { ...cotacoes.value[index], ...data }
    }

    // Watchers
    watch(cotacoes, (newVal) => {
      emit('update:modelValue', newVal)
    }, { deep: true })

    watch(isValid, (newVal) => {
      emit('validation-change', newVal)
    })

    // Inicializar com uma cotação vazia se não houver nenhuma
    onMounted(() => {
      if (cotacoes.value.length === 0) {
        adicionarCotacao()
      }
      emit('validation-change', isValid.value)
    })

    return {
      cotacoes,
      gerandoRelatorio,
      formatarData,
      formatarStatus,
      cotacoesCompletas,
      validationErrors,
      isValid,
      gerarRelatorio,
      adicionarCotacao,
      removerCotacao,
      atualizarCotacao
    }
  }
}
</script>

<style scoped>
/* Context Section */
.context-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 32px;
}

.context-box {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
}

.pedido-context {
  border-left: 4px solid #1F285F;
}

.item-context {
  border-left: 4px solid #10b981;
}

.context-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 12px 0;
}

.context-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.context-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.context-item.full-width {
  grid-column: 1 / -1;
}

.context-label {
  font-size: 0.7rem;
  color: #6b7280;
  text-transform: uppercase;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.context-value {
  font-size: 0.875rem;
  color: #111827;
  font-weight: 500;
}

.status-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.7rem;
  font-weight: 600;
  width: fit-content;
}

.status-rascunho { background: #f3f4f6; color: #374151; }
.status-pendente { background: #fef3c7; color: #d97706; }
.status-em_analise { background: #e0e7ff; color: #1F285F; }
.status-em_andamento { background: #e0e7ff; color: #1F285F; }
.status-aprovado { background: #d1fae5; color: #059669; }
.status-cancelado { background: #fee2e2; color: #dc2626; }

/* Item Details */
.item-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-row {
  display: flex;
  gap: 8px;
  align-items: baseline;
}

.item-label {
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 600;
  min-width: 80px;
}

.item-value {
  font-size: 0.875rem;
  color: #111827;
  font-weight: 500;
  flex: 1;
}

.item-value.highlight {
  color: #10b981;
  font-weight: 700;
}

/* Cotações Section */
.cotacoes-section {
  margin-top: 24px;
}

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
  font-weight: 600;
  color: #374151;
  margin: 0;
}

.btn-relatorio {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #10b981;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-relatorio:hover:not(:disabled) {
  background: #059669;
  transform: translateY(-1px);
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
  gap: 8px;
  background: #1F285F;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add-cotacao:hover {
  background: #2d3a7f;
  transform: translateY(-1px);
}

/* Cotações Lista */
.cotacoes-lista {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
}

/* Loading */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 48px 24px;
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

/* Empty State */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 48px 24px;
  text-align: center;
  color: #6b7280;
  background: #f9fafb;
  border: 1px dashed #d1d5db;
  border-radius: 8px;
  margin-top: 16px;
}

.empty-state svg {
  color: #d1d5db;
  margin-bottom: 8px;
}

.empty-state p {
  margin: 0;
  font-size: 0.875rem;
}

.empty-state .hint {
  font-size: 0.75rem;
  font-style: italic;
  color: #9ca3af;
}

/* Resumo */
.resumo-cotacoes {
  margin-top: 24px;
  padding: 16px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 8px;
  display: flex;
  gap: 32px;
}

.resumo-item {
  display: flex;
  gap: 8px;
  align-items: center;
}

.resumo-label {
  font-weight: 500;
  color: #374151;
  font-size: 0.875rem;
}

.resumo-value {
  font-weight: 700;
  color: #15803d;
  font-size: 0.875rem;
}

/* Validation Errors */
.validation-errors {
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  padding: 16px;
  margin-top: 16px;
}

.error-title {
  font-weight: 600;
  color: #dc2626;
  margin-bottom: 8px;
}

.validation-errors ul {
  margin: 0;
  padding-left: 20px;
  color: #b91c1c;
  font-size: 0.875rem;
}

.validation-errors li {
  margin-bottom: 4px;
}

/* Responsive */
@media (max-width: 768px) {
  .context-section {
    grid-template-columns: 1fr;
  }

  .context-grid {
    grid-template-columns: 1fr;
  }

  .section-header {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .btn-add-cotacao {
    width: 100%;
    justify-content: center;
  }

  .resumo-cotacoes {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
