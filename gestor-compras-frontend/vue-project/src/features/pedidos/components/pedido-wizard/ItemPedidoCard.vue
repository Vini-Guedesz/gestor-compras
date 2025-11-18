<template>
  <div class="item-card-aprovacao">
    <div class="card-header">
      <div class="header-left">
        <span class="item-numero">Item #{{ numero }}</span>
        <h4 class="item-nome">{{ item.nome }}</h4>
      </div>
      <div class="cotacoes-badge" :class="cotacoesCount > 0 ? 'has-cotacoes' : 'no-cotacoes'">
        <svg viewBox="0 0 24 24" width="16" height="16">
          <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1.41 16.09V20h-2.67v-1.93c-1.71-.36-3.16-1.46-3.27-3.4h1.96c.1 1.05.82 1.87 2.65 1.87 1.96 0 2.4-.98 2.4-1.59 0-.83-.44-1.61-2.67-2.14-2.48-.6-4.18-1.62-4.18-3.67 0-1.72 1.39-2.84 3.11-3.21V4h2.67v1.95c1.86.45 2.79 1.86 2.85 3.39H14.3c-.05-1.11-.64-1.87-2.22-1.87-1.5 0-2.4.68-2.4 1.64 0 .84.65 1.39 2.67 1.91s4.18 1.39 4.18 3.91c-.01 1.83-1.38 2.83-3.12 3.16z"/>
        </svg>
        {{ cotacoesCount }} {{ cotacoesCount === 1 ? 'cotação' : 'cotações' }}
      </div>
    </div>

    <div class="card-body">
      <div class="info-row">
        <div class="info-col">
          <span class="info-label">Quantidade:</span>
          <span class="info-value">{{ item.quantidade }}x</span>
        </div>
        <div v-if="item.id" class="info-col">
          <span class="info-label">ID:</span>
          <span class="info-value">#{{ item.id }}</span>
        </div>
      </div>

      <div v-if="item.descricao" class="info-row full">
        <span class="info-label">Produto/Serviço:</span>
        <span class="info-value">{{ item.descricao }}</span>
      </div>

      <div v-if="item.observacao" class="info-row full">
        <span class="info-label">Observações:</span>
        <span class="info-value">{{ item.observacao }}</span>
      </div>
    </div>

    <div class="card-footer">
      <button
        type="button"
        @click="$emit('add-cotacao')"
        class="btn-action btn-add"
      >
        <svg viewBox="0 0 24 24" width="16" height="16">
          <path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
        </svg>
        Adicionar Cotação
      </button>

      <button
        type="button"
        @click="$emit('view-cotacoes')"
        class="btn-action btn-view"
        :disabled="cotacoesCount === 0"
      >
        <svg viewBox="0 0 24 24" width="16" height="16">
          <path fill="currentColor" d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
        </svg>
        Ver Cotações ({{ cotacoesCount }})
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ItemCardAprovacao',
  props: {
    item: {
      type: Object,
      required: true
    },
    numero: {
      type: Number,
      required: true
    },
    cotacoesCount: {
      type: Number,
      default: 0
    }
  },
  emits: ['add-cotacao', 'view-cotacoes']
}
</script>

<style scoped>
.item-card-aprovacao {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.2s;
}

.item-card-aprovacao:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* Header */
.card-header {
  background: #f9fafb;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  border-bottom: 1px solid #e5e7eb;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-numero {
  background: #3b82f6;
  color: white;
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 0.75rem;
  font-weight: 600;
  width: fit-content;
}

.item-nome {
  font-size: 1rem;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.cotacoes-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

.cotacoes-badge.has-cotacoes {
  background: #d1fae5;
  color: #059669;
}

.cotacoes-badge.no-cotacoes {
  background: #fee2e2;
  color: #dc2626;
}

/* Body */
.card-body {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  gap: 24px;
}

.info-row.full {
  flex-direction: column;
  gap: 4px;
}

.info-col {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 500;
}

.info-value {
  font-size: 0.875rem;
  color: #111827;
  font-weight: 500;
}

/* Footer */
.card-footer {
  padding: 16px;
  background: #f9fafb;
  border-top: 1px solid #e5e7eb;
  display: flex;
  gap: 12px;
}

.btn-action {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add {
  background: #3b82f6;
  color: white;
}

.btn-add:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.btn-view {
  background: white;
  color: #6b7280;
  border: 1px solid #d1d5db;
}

.btn-view:hover:not(:disabled) {
  background: #f3f4f6;
  color: #374151;
}

.btn-view:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Responsive */
@media (max-width: 768px) {
  .card-footer {
    flex-direction: column;
  }

  .btn-action {
    width: 100%;
  }
}
</style>
