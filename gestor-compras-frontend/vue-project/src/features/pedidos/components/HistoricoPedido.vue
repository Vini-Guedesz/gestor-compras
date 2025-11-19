<template>
  <div class="historico-container">
    <div class="historico-header">
      <h3 class="historico-title">
        <svg viewBox="0 0 24 24" width="20" height="20">
          <path fill="currentColor" d="M13 3a9 9 0 0 0-9 9H1l3.89 3.89.07.14L9 12H6c0-3.87 3.13-7 7-7s7 3.13 7 7-3.13 7-7 7c-1.93 0-3.68-.79-4.94-2.06l-1.42 1.42A8.954 8.954 0 0 0 13 21a9 9 0 0 0 0-18zm-1 5v5l4.28 2.54.72-1.21-3.5-2.08V8H12z"/>
        </svg>
        Histórico de Modificações
      </h3>
    </div>

    <div v-if="carregando" class="historico-loading">
      <span class="loading-spinner"></span>
      Carregando histórico...
    </div>

    <div v-else-if="historico.length === 0" class="historico-empty">
      <svg viewBox="0 0 24 24" width="32" height="32">
        <path fill="currentColor" d="M13 3a9 9 0 0 0-9 9H1l3.89 3.89.07.14L9 12H6c0-3.87 3.13-7 7-7s7 3.13 7 7-3.13 7-7 7c-1.93 0-3.68-.79-4.94-2.06l-1.42 1.42A8.954 8.954 0 0 0 13 21a9 9 0 0 0 0-18zm-1 5v5l4.28 2.54.72-1.21-3.5-2.08V8H12z"/>
      </svg>
      <p>Nenhum registro no histórico</p>
    </div>

    <div v-else class="historico-timeline">
      <div
        v-for="item in historico"
        :key="item.id"
        class="timeline-item"
      >
        <div class="timeline-marker" :style="{ backgroundColor: getCorTipo(item.tipoModificacao) }">
          <svg viewBox="0 0 24 24" width="14" height="14">
            <path fill="white" :d="getIconeTipo(item.tipoModificacao)"/>
          </svg>
        </div>

        <div class="timeline-content">
          <div class="timeline-header">
            <span class="timeline-tipo" :style="{ color: getCorTipo(item.tipoModificacao) }">
              {{ getLabelTipo(item.tipoModificacao) }}
            </span>
            <span class="timeline-data">
              {{ formatarData(item.dataModificacao) }}
            </span>
          </div>

          <div class="timeline-body">
            <div class="timeline-usuario">
              <svg viewBox="0 0 24 24" width="14" height="14">
                <path fill="currentColor" d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
              </svg>
              {{ item.nomeUsuario || 'Usuário' }}
            </div>

            <div v-if="item.campoModificado" class="timeline-campo">
              Campo: <strong>{{ item.campoModificado }}</strong>
            </div>

            <div v-if="item.valorAnterior || item.valorNovo" class="timeline-valores">
              <div v-if="item.valorAnterior" class="valor-anterior">
                <span class="valor-label">Anterior:</span>
                <span class="valor-texto">{{ item.valorAnterior }}</span>
              </div>
              <div v-if="item.valorNovo" class="valor-novo">
                <span class="valor-label">Novo:</span>
                <span class="valor-texto">{{ item.valorNovo }}</span>
              </div>
            </div>

            <div v-if="item.observacao" class="timeline-observacao">
              {{ item.observacao }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, watch, onMounted } from 'vue'
import historicoPedidoService, { tipoModificacaoConfig, formatarDataHistorico } from '@/services/historicoPedidoService.js'

export default {
  name: 'HistoricoPedido',
  props: {
    pedidoId: {
      type: [Number, String],
      required: true
    }
  },
  setup(props) {
    const historico = ref([])
    const carregando = ref(false)

    const carregarHistorico = async () => {
      if (!props.pedidoId) return

      try {
        carregando.value = true
        historico.value = await historicoPedidoService.listarPorPedido(props.pedidoId)
      } catch (error) {
        console.error('Erro ao carregar histórico:', error)
        historico.value = []
      } finally {
        carregando.value = false
      }
    }

    const getLabelTipo = (tipo) => {
      return tipoModificacaoConfig[tipo]?.label || tipo
    }

    const getCorTipo = (tipo) => {
      return tipoModificacaoConfig[tipo]?.color || '#6b7280'
    }

    const getIconeTipo = (tipo) => {
      const icones = {
        CRIACAO: 'M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z',
        ATUALIZACAO: 'M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z',
        MUDANCA_STATUS: 'M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z',
        ADICAO_ITEM: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11h-4v4h-2v-4H7v-2h4V7h2v4h4v2z',
        REMOCAO_ITEM: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11H7v-2h10v2z',
        APROVACAO: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z',
        CANCELAMENTO: 'M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z'
      }
      return icones[tipo] || icones.ATUALIZACAO
    }

    const formatarData = (data) => {
      return formatarDataHistorico(data)
    }

    watch(() => props.pedidoId, () => {
      carregarHistorico()
    })

    onMounted(() => {
      carregarHistorico()
    })

    return {
      historico,
      carregando,
      getLabelTipo,
      getCorTipo,
      getIconeTipo,
      formatarData
    }
  }
}
</script>

<style scoped>
.historico-container {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-top: 24px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.historico-header {
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.historico-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: #1F285F;
}

.historico-loading,
.historico-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #6b7280;
  gap: 12px;
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

.historico-timeline {
  position: relative;
  padding-left: 28px;
}

.historico-timeline::before {
  content: '';
  position: absolute;
  left: 10px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e5e7eb;
}

.timeline-item {
  position: relative;
  padding-bottom: 20px;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-marker {
  position: absolute;
  left: -28px;
  top: 0;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}

.timeline-content {
  background: #f9fafb;
  border-radius: 8px;
  padding: 12px 16px;
  border: 1px solid #e5e7eb;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.timeline-tipo {
  font-weight: 600;
  font-size: 0.875rem;
}

.timeline-data {
  font-size: 0.75rem;
  color: #6b7280;
}

.timeline-body {
  font-size: 0.875rem;
}

.timeline-usuario {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #374151;
  margin-bottom: 8px;
}

.timeline-campo {
  color: #6b7280;
  margin-bottom: 8px;
}

.timeline-valores {
  background: white;
  border-radius: 6px;
  padding: 8px 12px;
  margin-bottom: 8px;
  border: 1px solid #e5e7eb;
}

.valor-anterior,
.valor-novo {
  display: flex;
  gap: 8px;
  padding: 4px 0;
}

.valor-anterior {
  color: #dc2626;
}

.valor-novo {
  color: #16a34a;
}

.valor-label {
  font-weight: 500;
  min-width: 60px;
}

.valor-texto {
  word-break: break-word;
}

.timeline-observacao {
  color: #6b7280;
  font-style: italic;
  padding-top: 4px;
  border-top: 1px dashed #e5e7eb;
  margin-top: 8px;
}

@media (max-width: 768px) {
  .historico-container {
    padding: 16px;
  }

  .timeline-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>
