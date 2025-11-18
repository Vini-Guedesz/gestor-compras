<template>
  <!-- Overlay -->
  <Transition name="drawer-overlay">
    <div
      v-if="isVisible"
      class="drawer-overlay"
      @click="fechar"
    ></div>
  </Transition>

  <!-- Drawer -->
  <Transition name="drawer">
    <div v-if="isVisible" class="drawer-container">
      <!-- Header -->
      <div class="drawer-header">
        <div class="header-content">
          <h2 class="drawer-title">
            💰 Cotações - {{ item?.nome || 'Item' }}
          </h2>
          <p class="drawer-subtitle">
            {{ cotacoes.length }} {{ cotacoes.length === 1 ? 'cotação encontrada' : 'cotações encontradas' }}
          </p>
        </div>
        <button @click="fechar" class="btn-close">
          <svg viewBox="0 0 24 24" width="24" height="24">
            <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>
      </div>

      <!-- Body -->
      <div class="drawer-body">
        <!-- Item Info -->
        <div v-if="item" class="item-info-box">
          <div class="info-row">
            <span class="info-label">Item:</span>
            <span class="info-value">{{ item.nome }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Quantidade:</span>
            <span class="info-value highlight">{{ item.quantidade }}x</span>
          </div>
          <div v-if="item.descricao" class="info-row">
            <span class="info-label">Descrição:</span>
            <span class="info-value">{{ item.descricao }}</span>
          </div>
        </div>

        <!-- Loading -->
        <div v-if="carregando" class="loading-state">
          <div class="loading-spinner"></div>
          <span>Carregando cotações...</span>
        </div>

        <!-- Lista de Cotações -->
        <div v-else-if="cotacoes.length > 0" class="cotacoes-list">
          <div
            v-for="(cotacao, index) in cotacoes"
            :key="cotacao.id"
            class="cotacao-card"
          >
            <div class="cotacao-header">
              <span class="cotacao-badge">Cotação #{{ index + 1 }}</span>
              <span
                v-if="cotacao.id === melhorCotacao?.id"
                class="best-badge"
                title="Menor preço"
              >
                ⭐ Melhor
              </span>
            </div>

            <div class="cotacao-body">
              <!-- Fornecedor -->
              <div class="cotacao-row">
                <div class="row-icon">
                  <svg viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z"/>
                  </svg>
                </div>
                <div class="row-content">
                  <span class="row-label">Fornecedor</span>
                  <span class="row-value">{{ cotacao.fornecedorNome || `#${cotacao.fornecedorId}` }}</span>
                  <span class="tipo-badge">{{ cotacao.tipoFornecedor }}</span>
                </div>
              </div>

              <!-- Preço -->
              <div class="cotacao-row">
                <div class="row-icon">
                  <svg viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/>
                  </svg>
                </div>
                <div class="row-content">
                  <span class="row-label">Valor</span>
                  <span class="row-value price">{{ formatarPreco(cotacao.preco) }}</span>
                </div>
              </div>

              <!-- Prazo -->
              <div v-if="cotacao.prazoEmDiasUteis" class="cotacao-row">
                <div class="row-icon">
                  <svg viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10 10-4.5 10-10S17.5 2 12 2zm4.2 14.2L11 13V7h1.5v5.2l4.5 2.7-.8 1.3z"/>
                  </svg>
                </div>
                <div class="row-content">
                  <span class="row-label">Prazo de Entrega</span>
                  <span class="row-value">{{ cotacao.prazoEmDiasUteis }} dias úteis</span>
                </div>
              </div>

              <!-- Validade -->
              <div v-if="cotacao.dataLimite" class="cotacao-row">
                <div class="row-icon">
                  <svg viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11zM7 10h5v5H7z"/>
                  </svg>
                </div>
                <div class="row-content">
                  <span class="row-label">Validade da Cotação</span>
                  <span class="row-value">{{ formatarData(cotacao.dataLimite) }}</span>
                </div>
              </div>

              <!-- PDF -->
              <div class="cotacao-row">
                <div class="row-icon">
                  <svg viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
                  </svg>
                </div>
                <div class="row-content">
                  <span class="row-label">Anexo PDF</span>
                  <button
                    v-if="cotacao.anexoPdf || cotacao.caminhoAnexo"
                    @click="visualizarPdf(cotacao)"
                    class="btn-view-pdf"
                  >
                    <svg viewBox="0 0 24 24" width="14" height="14">
                      <path fill="currentColor" d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                    </svg>
                    Visualizar PDF
                  </button>
                  <span v-else class="no-pdf">Sem PDF anexado</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="empty-state">
          <svg viewBox="0 0 24 24" width="64" height="64">
            <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
          </svg>
          <h3>Nenhuma cotação encontrada</h3>
          <p>Ainda não há cotações cadastradas para este item.</p>
        </div>
      </div>

      <!-- Footer -->
      <div class="drawer-footer">
        <button @click="fechar" class="btn-close-footer">
          Fechar
        </button>
      </div>
    </div>
  </Transition>
</template>

<script>
import { computed } from 'vue'

export default {
  name: 'DrawerCotacoes',
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    item: {
      type: Object,
      default: null
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
  emits: ['close', 'view-pdf'],
  setup(props, { emit }) {
    // Computed
    const melhorCotacao = computed(() => {
      if (props.cotacoes.length === 0) return null
      return props.cotacoes.reduce((menor, atual) => {
        if (!menor || (atual.preco && atual.preco < menor.preco)) {
          return atual
        }
        return menor
      }, null)
    })

    // Métodos
    const fechar = () => {
      emit('close')
    }

    const formatarPreco = (preco) => {
      if (!preco) return 'R$ 0,00'
      return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
      }).format(preco)
    }

    const formatarData = (data) => {
      if (!data) return 'N/A'
      try {
        return new Date(data).toLocaleDateString('pt-BR')
      } catch {
        return 'Data inválida'
      }
    }

    const visualizarPdf = (cotacao) => {
      emit('view-pdf', cotacao)
    }

    return {
      melhorCotacao,
      fechar,
      formatarPreco,
      formatarData,
      visualizarPdf
    }
  }
}
</script>

<style scoped>
/* Overlay */
.drawer-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1100;
  backdrop-filter: blur(2px);
}

/* Drawer Container */
.drawer-container {
  position: fixed;
  top: 0;
  right: 0;
  width: 520px;
  max-width: 90vw;
  height: 100vh;
  background: white;
  z-index: 1101;
  display: flex;
  flex-direction: column;
  box-shadow: -8px 0 24px rgba(0, 0, 0, 0.15);
}

/* Header */
.drawer-header {
  padding: 24px 28px;
  border-bottom: 1px solid #e0e6ed;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  background: #1F285F;
  color: white;
}

.header-content {
  flex: 1;
}

.drawer-title {
  font-size: 1.375rem;
  font-weight: 600;
  margin: 0 0 6px 0;
  color: white;
}

.drawer-subtitle {
  font-size: 0.875rem;
  margin: 0;
  opacity: 0.95;
  font-weight: 500;
}

.btn-close {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 16px;
}

.btn-close:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.05);
}

/* Body */
.drawer-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: #f9fafb;
}

/* Item Info */
.item-info-box {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.info-row {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  align-items: flex-start;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  font-size: 0.8125rem;
  color: #6b7280;
  font-weight: 600;
  min-width: 100px;
  flex-shrink: 0;
}

.info-value {
  font-size: 0.9375rem;
  color: #111827;
  font-weight: 500;
  flex: 1;
  word-break: break-word;
}

.info-value.highlight {
  color: #1F285F;
  font-weight: 700;
  font-size: 1rem;
}

/* Loading */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 64px 24px;
  color: #6b7280;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e5e7eb;
  border-top-color: #1F285F;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Cotações List */
.cotacoes-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cotacao-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.2s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.cotacao-card:hover {
  box-shadow: 0 4px 12px rgba(31, 40, 95, 0.15);
  transform: translateY(-2px);
  border-color: #1F285F;
}

.cotacao-header {
  background: linear-gradient(135deg, #f0f4ff 0%, #e8eaf6 100%);
  padding: 14px 20px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cotacao-badge {
  background: #1F285F;
  color: white;
  padding: 5px 14px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(31, 40, 95, 0.2);
}

.best-badge {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  color: white;
  padding: 5px 14px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(245, 158, 11, 0.2);
}

.cotacao-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.cotacao-row {
  display: flex;
  gap: 14px;
  align-items: flex-start;
}

.row-icon {
  color: #1F285F;
  flex-shrink: 0;
  margin-top: 2px;
  background: #e8eaf6;
  padding: 8px;
  border-radius: 8px;
}

.row-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.row-label {
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.row-value {
  font-size: 0.9375rem;
  color: #111827;
  font-weight: 500;
}

.row-value.price {
  color: #10b981;
  font-weight: 700;
  font-size: 1.375rem;
  letter-spacing: -0.5px;
}

.tipo-badge {
  display: inline-block;
  background: linear-gradient(135deg, #e8eaf6 0%, #f0f4ff 100%);
  color: #1F285F;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.6875rem;
  font-weight: 700;
  width: fit-content;
  margin-top: 6px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.btn-view-pdf {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: #1F285F;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 0.8125rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  width: fit-content;
  box-shadow: 0 2px 6px rgba(31, 40, 95, 0.3);
}

.btn-view-pdf:hover {
  background: #2d3a7f;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(31, 40, 95, 0.4);
}

.no-pdf {
  font-size: 0.8125rem;
  color: #9ca3af;
  font-style: italic;
  background: #f3f4f6;
  padding: 6px 12px;
  border-radius: 6px;
  width: fit-content;
}

/* Empty State */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 80px 32px;
  text-align: center;
  color: #6b7280;
  background: white;
  border-radius: 12px;
  border: 2px dashed #e5e7eb;
}

.empty-state svg {
  color: #d1d5db;
  opacity: 0.5;
}

.empty-state h3 {
  margin: 0;
  font-size: 1.125rem;
  color: #374151;
  font-weight: 600;
}

.empty-state p {
  margin: 0;
  font-size: 0.9375rem;
  color: #6b7280;
}

/* Footer */
.drawer-footer {
  padding: 20px 28px;
  border-top: 1px solid #e5e7eb;
  background: white;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
}

.btn-close-footer {
  width: 100%;
  padding: 12px 16px;
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.9375rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-close-footer:hover {
  background: #e5e7eb;
  border-color: #9ca3af;
  transform: translateY(-1px);
}

/* Transitions */
.drawer-overlay-enter-active,
.drawer-overlay-leave-active {
  transition: opacity 0.3s ease;
}

.drawer-overlay-enter-from,
.drawer-overlay-leave-to {
  opacity: 0;
}

.drawer-enter-active,
.drawer-leave-active {
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.drawer-enter-from,
.drawer-leave-to {
  transform: translateX(100%);
}

/* Scrollbar Customizado */
.drawer-body::-webkit-scrollbar {
  width: 8px;
}

.drawer-body::-webkit-scrollbar-track {
  background: #f3f4f6;
}

.drawer-body::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 4px;
}

.drawer-body::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

/* Responsive */
@media (max-width: 768px) {
  .drawer-container {
    width: 100%;
    max-width: 100vw;
  }

  .drawer-header {
    padding: 20px;
  }

  .drawer-title {
    font-size: 1.125rem;
  }

  .drawer-body {
    padding: 16px;
  }

  .item-info-box {
    padding: 16px;
  }

  .info-label {
    min-width: 80px;
    font-size: 0.75rem;
  }

  .info-value {
    font-size: 0.875rem;
  }

  .cotacao-body {
    padding: 16px;
  }

  .row-value.price {
    font-size: 1.125rem;
  }
}
</style>
