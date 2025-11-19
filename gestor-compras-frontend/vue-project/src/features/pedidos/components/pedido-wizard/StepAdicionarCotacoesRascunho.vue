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
        <button @click="abrirFormularioCotacao" class="btn-add-cotacao">
          <svg viewBox="0 0 24 24" width="18" height="18">
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
            <button
              v-if="cotacao.temAnexoPdf"
              @click="$emit('view-pdf', cotacao)"
              class="btn-pdf"
            >
              Ver PDF
            </button>
            <button @click="$emit('delete-cotacao', cotacao)" class="btn-delete">
              Remover
            </button>
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
    <div v-if="showFormulario" class="modal-overlay" @click.self="fecharFormulario">
      <div class="modal-cotacao">
        <div class="modal-header">
          <h3>Nova Cotação</h3>
          <button @click="fecharFormulario" class="btn-close">&times;</button>
        </div>

        <div class="modal-body">
          <!-- Fornecedor -->
          <div class="form-group">
            <label>Fornecedor *</label>
            <select v-model="fornecedorSelecionado" @change="onFornecedorChange" required>
              <option value="">Selecione um fornecedor</option>
              <optgroup label="Fornecedores de Produto">
                <option
                  v-for="f in fornecedoresProduto"
                  :key="'p-' + f.id"
                  :value="'PRODUTO-' + f.id"
                >
                  {{ f.razaoSocial }}
                </option>
              </optgroup>
              <optgroup label="Fornecedores de Serviço">
                <option
                  v-for="f in fornecedoresServico"
                  :key="'s-' + f.id"
                  :value="'SERVICO-' + f.id"
                >
                  {{ f.razaoSocial }}
                </option>
              </optgroup>
            </select>
          </div>

          <!-- Itens Contemplados -->
          <div class="form-group">
            <label>Itens Contemplados *</label>
            <div class="checkbox-lista">
              <label v-for="item in rascunho.itens" :key="item.id" class="checkbox-item">
                <input
                  type="checkbox"
                  :value="item.id"
                  v-model="novaCotacao.itensRascunhoIds"
                >
                <span class="checkbox-label">
                  {{ item.nome }} (Qtd: {{ item.quantidade }})
                </span>
              </label>
            </div>
          </div>

          <!-- Preço -->
          <div class="form-group">
            <label>Preço Total *</label>
            <input
              type="number"
              v-model="novaCotacao.preco"
              min="0"
              step="0.01"
              placeholder="0,00"
              required
            >
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

          <!-- Anexo PDF -->
          <div class="form-group">
            <label>Anexo PDF</label>
            <input
              type="file"
              accept=".pdf"
              @change="handleFileUpload"
            >
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
import { ref, computed } from 'vue'

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

    const novaCotacao = ref({
      fornecedorId: null,
      tipoFornecedor: '',
      itensRascunhoIds: [],
      preco: null,
      prazoEmDiasUteis: null,
      dataLimite: null,
      anexoPdf: null
    })

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

    const fornecedoresProduto = computed(() =>
      props.fornecedores.filter(f => f.tipo === 'PRODUTO' || !f.tipo)
    )

    const fornecedoresServico = computed(() =>
      props.fornecedores.filter(f => f.tipo === 'SERVICO')
    )

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
      novaCotacao.value = {
        fornecedorId: null,
        tipoFornecedor: '',
        itensRascunhoIds: [],
        preco: null,
        prazoEmDiasUteis: null,
        dataLimite: null,
        anexoPdf: null
      }
      showFormulario.value = true
    }

    const fecharFormulario = () => {
      showFormulario.value = false
    }

    const handleFileUpload = (event) => {
      const file = event.target.files[0]
      if (file) {
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
          novaCotacao.value.anexoPdf = bytes
        }
        reader.readAsDataURL(file)
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
          anexoPdf: novaCotacao.value.anexoPdf || null
        })
        fecharFormulario()
      } finally {
        salvando.value = false
      }
    }

    return {
      showFormulario,
      salvando,
      fornecedorSelecionado,
      novaCotacao,
      fornecedoresProduto,
      fornecedoresServico,
      cotacaoValida,
      formatarData,
      formatarPreco,
      getNomeItem,
      itemTemCotacao,
      abrirFormularioCotacao,
      fecharFormulario,
      handleFileUpload,
      salvarCotacao,
      onFornecedorChange
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
