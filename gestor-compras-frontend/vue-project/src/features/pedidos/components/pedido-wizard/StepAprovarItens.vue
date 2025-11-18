<template>
  <div class="page-2-container">
    <!-- Dados do Pedido -->
    <div class="pedido-info-box">
      <h3 class="info-title">📋 Dados do Pedido</h3>
      <div class="info-grid">
        <div class="info-item">
          <span class="info-label">Data de Criação:</span>
          <span class="info-value">{{ formatarData(pedido.dataCriacao) }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">ID do Pedido:</span>
          <span class="info-value">#{{ pedido.id }}</span>
        </div>
        <div class="info-item full-width">
          <span class="info-label">Objetivo:</span>
          <span class="info-value">{{ pedido.objetivo || 'Sem descrição' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">Status:</span>
          <span class="status-badge" :class="`status-${pedido.status?.toLowerCase()}`">
            {{ formatarStatus(pedido.status) }}
          </span>
        </div>
      </div>
    </div>

    <!-- Lista de Itens -->
    <div class="itens-section">
      <h3 class="section-title">📦 Itens do Pedido ({{ pedido.itens?.length || 0 }})</h3>

      <div v-if="carregando" class="loading-state">
        <div class="loading-spinner"></div>
        <span>Carregando itens...</span>
      </div>

      <div v-else-if="pedido.itens && pedido.itens.length > 0" class="itens-lista">
        <ItemCardAprovacao
          v-for="(item, index) in pedido.itens"
          :key="item.id || index"
          :item="item"
          :numero="index + 1"
          :cotacoesCount="contarCotacoes(item.id)"
          @add-cotacao="emitAddCotacao(item)"
          @view-cotacoes="emitViewCotacoes(item)"
        />
      </div>

      <div v-else class="empty-state">
        <p>Nenhum item encontrado neste pedido.</p>
      </div>
    </div>
  </div>
</template>

<script>
import ItemCardAprovacao from './ItemPedidoCard.vue'

export default {
  name: 'PedidoFormPage2',
  components: {
    ItemCardAprovacao
  },
  props: {
    pedido: {
      type: Object,
      required: true
    },
    cotacoes: {
      type: Array,
      default: () => []
    },
    carregando: {
      type: Boolean,
      default: false
    }
  },
  emits: ['add-cotacao', 'view-cotacoes'],
  setup(props, { emit }) {
    // Métodos
    const formatarData = (data) => {
      if (!data) return 'N/A'
      try {
        return new Date(data).toLocaleString('pt-BR')
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

    const contarCotacoes = (itemId) => {
      if (!itemId || !props.cotacoes) return 0
      return props.cotacoes.filter(c => c.itemPedidoId === itemId).length
    }

    const emitAddCotacao = (item) => {
      emit('add-cotacao', item)
    }

    const emitViewCotacoes = (item) => {
      emit('view-cotacoes', item)
    }

    return {
      formatarData,
      formatarStatus,
      contarCotacoes,
      emitAddCotacao,
      emitViewCotacoes
    }
  }
}
</script>

<style scoped>
/* Pedido Info */
.pedido-info-box {
  background: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 32px;
}

.info-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e40af;
  margin: 0 0 16px 0;
}

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
  color: #6b7280;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-value {
  font-size: 0.875rem;
  color: #111827;
  font-weight: 500;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  width: fit-content;
}

.status-rascunho {
  background: #f3f4f6;
  color: #374151;
}

.status-pendente {
  background: #fef3c7;
  color: #d97706;
}

.status-em_analise {
  background: #dbeafe;
  color: #2563eb;
}

.status-em_andamento {
  background: #dbeafe;
  color: #2563eb;
}

.status-aprovado {
  background: #d1fae5;
  color: #059669;
}

.status-cancelado {
  background: #fee2e2;
  color: #dc2626;
}

/* Itens Section */
.itens-section {
  margin-top: 32px;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e5e7eb;
}

.itens-lista {
  display: flex;
  flex-direction: column;
  gap: 16px;
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
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 48px 24px;
  color: #6b7280;
  font-style: italic;
  background: #f9fafb;
  border: 1px dashed #d1d5db;
  border-radius: 8px;
}

/* Responsive */
@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
