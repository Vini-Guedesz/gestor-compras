<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="show" class="modal-overlay" @click.self="fechar">
        <div class="modal-container">
          <!-- Header -->
          <div class="modal-header">
            <div>
              <h3 class="modal-title">Histórico de Edições</h3>
              <p class="modal-subtitle">Cotação #{{ cotacao.id }}</p>
            </div>
            <button @click="fechar" class="btn-close" type="button">
              <svg viewBox="0 0 24 24" width="20" height="20">
                <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
              </svg>
            </button>
          </div>

          <!-- Body -->
          <div class="modal-body">
            <!-- Loading State -->
            <div v-if="carregando" class="loading-state">
              <div class="loading-spinner"></div>
              <span>Carregando histórico...</span>
            </div>

            <!-- Empty State -->
            <div v-else-if="historico.length === 0" class="empty-state">
              <svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <p>Nenhuma edição registrada para esta cotação</p>
            </div>

            <!-- Histórico List -->
            <div v-else class="historico-lista">
              <div v-for="(item, index) in historico" :key="item.id" class="historico-item">
                <!-- Version Badge -->
                <div class="version-badge">
                  <span class="version-number">v{{ item.versao }}</span>
                </div>

                <!-- Timeline Connector -->
                <div v-if="index < historico.length - 1" class="timeline-connector"></div>

                <!-- Content Card -->
                <div class="historico-card">
                  <!-- Header -->
                  <div class="historico-header">
                    <div class="historico-info">
                      <span class="historico-titulo">Versão {{ item.versao }}</span>
                      <span class="historico-data">{{ formatarDataHora(item.dataEdicao) }}</span>
                    </div>
                    <div class="historico-autor">
                      <svg viewBox="0 0 20 20" width="16" height="16" fill="currentColor">
                        <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"/>
                      </svg>
                      <span>{{ item.editadoPor || 'Sistema' }}</span>
                    </div>
                  </div>

                  <!-- Motivo -->
                  <div class="historico-motivo">
                    <div class="motivo-label">
                      <svg viewBox="0 0 20 20" width="14" height="14" fill="currentColor">
                        <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/>
                      </svg>
                      Motivo:
                    </div>
                    <p class="motivo-texto">{{ item.motivoEdicao }}</p>
                  </div>

                  <!-- Alterações -->
                  <div class="alteracoes-grid">
                    <!-- Preço (só se mudou) -->
                    <div v-if="houveMudancaPreco(item)" class="alteracao-item">
                      <div class="alteracao-label">Preço</div>
                      <div class="alteracao-valores">
                        <span class="valor-anterior">R$ {{ formatarPreco(item.precoAnterior) }}</span>
                        <svg viewBox="0 0 20 20" width="16" height="16" fill="currentColor" class="arrow-icon">
                          <path fill-rule="evenodd" d="M12.293 5.293a1 1 0 011.414 0l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-2.293-2.293a1 1 0 010-1.414z" clip-rule="evenodd"/>
                        </svg>
                        <span class="valor-novo">R$ {{ formatarPreco(item.precoNovo) }}</span>
                      </div>
                      <div v-if="item.precoAnterior && item.precoNovo" class="alteracao-diff" :class="getDiferencaClass(item.precoAnterior, item.precoNovo)">
                        {{ calcularDiferenca(item.precoAnterior, item.precoNovo) }}
                      </div>
                    </div>

                    <!-- Prazo (só se mudou) -->
                    <div v-if="houveMudancaPrazo(item)" class="alteracao-item">
                      <div class="alteracao-label">Prazo</div>
                      <div class="alteracao-valores">
                        <span class="valor-anterior">{{ item.prazoEmDiasUteisAnterior || '-' }} dias</span>
                        <svg viewBox="0 0 20 20" width="16" height="16" fill="currentColor" class="arrow-icon">
                          <path fill-rule="evenodd" d="M12.293 5.293a1 1 0 011.414 0l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-2.293-2.293a1 1 0 010-1.414z" clip-rule="evenodd"/>
                        </svg>
                        <span class="valor-novo">{{ item.prazoEmDiasUteisNovo || '-' }} dias</span>
                      </div>
                    </div>

                    <!-- Data Limite (só se mudou) -->
                    <div v-if="houveMudancaDataLimite(item)" class="alteracao-item">
                      <div class="alteracao-label">Validade</div>
                      <div class="alteracao-valores">
                        <span class="valor-anterior">{{ formatarData(item.dataLimiteAnterior) }}</span>
                        <svg viewBox="0 0 20 20" width="16" height="16" fill="currentColor" class="arrow-icon">
                          <path fill-rule="evenodd" d="M12.293 5.293a1 1 0 011.414 0l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-2.293-2.293a1 1 0 010-1.414z" clip-rule="evenodd"/>
                        </svg>
                        <span class="valor-novo">{{ formatarData(item.dataLimiteNovo) }}</span>
                      </div>
                    </div>
                  </div>

                  <!-- Mensagem se nenhum campo mudou -->
                  <div v-if="!houveMudancaPreco(item) && !houveMudancaPrazo(item) && !houveMudancaDataLimite(item) && !houveMudancaPdf(item)" class="sem-alteracoes">
                    <svg viewBox="0 0 20 20" width="16" height="16" fill="currentColor">
                      <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/>
                    </svg>
                    <span>Nenhuma alteração registrada nesta versão</span>
                  </div>

                  <!-- PDFs (só se houver mudança no anexo) -->
                  <div v-if="houveMudancaPdf(item)" class="historico-pdfs">
                    <div class="pdfs-label">Anexos:</div>
                    <div class="pdfs-buttons">
                      <button
                        v-if="item.temAnexoAnterior"
                        @click="visualizarPdfHistorico(item.id, 'anterior')"
                        class="btn-pdf"
                        type="button"
                      >
                        <svg viewBox="0 0 20 20" width="16" height="16" fill="currentColor">
                          <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z" clip-rule="evenodd"/>
                        </svg>
                        {{ pdfAberto === `${item.id}-anterior` ? 'Fechar PDF Anterior' : 'Ver PDF Anterior' }}
                      </button>
                      <button
                        v-if="item.temAnexoNovo"
                        @click="visualizarPdfHistorico(item.id, 'novo')"
                        class="btn-pdf btn-pdf-novo"
                        type="button"
                      >
                        <svg viewBox="0 0 20 20" width="16" height="16" fill="currentColor">
                          <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z" clip-rule="evenodd"/>
                        </svg>
                        {{ pdfAberto === `${item.id}-novo` ? 'Fechar PDF Novo' : 'Ver PDF Novo' }}
                      </button>
                    </div>
                  </div>

                  <!-- Viewer de PDF -->
                  <div v-if="pdfAberto === `${item.id}-anterior` || pdfAberto === `${item.id}-novo`" class="pdf-viewer">
                    <div v-if="carregandoPdf" class="pdf-loading">
                      <div class="loading-spinner"></div>
                      <span>Carregando PDF...</span>
                    </div>
                    <iframe v-else-if="pdfUrl" :src="pdfUrl" class="pdf-iframe"></iframe>
                    <div v-else class="pdf-error">Erro ao carregar PDF</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script>
import { ref, computed, watch } from 'vue'
import { useModal } from '@/composables/useModal'
import { useToast } from '@/composables/useToast'
import logger from '@/utils/logger.js'
import cotacaoService from '@/services/cotacaoService'

export default {
  name: 'HistoricoCotacaoModal',
  props: {
    show: {
      type: Boolean,
      default: false
    },
    cotacao: {
      type: Object,
      required: true
    }
  },
  emits: ['close'],
  setup(props, { emit }) {
    useModal(computed(() => props.show))
    const { error: showError } = useToast()

    const carregando = ref(false)
    const historico = ref([])
    const pdfAberto = ref(null)
    const pdfUrl = ref(null)
    const carregandoPdf = ref(false)

    watch(() => props.show, async (newVal) => {
      if (newVal) {
        await carregarHistorico()
      } else {
        // Limpar PDF ao fechar modal
        fecharPdfViewer()
      }
    })

    const carregarHistorico = async () => {
      carregando.value = true
      try {
        logger.debug('🔍 Buscando histórico da cotação:', props.cotacao.id)
        const data = await cotacaoService.buscarHistorico(props.cotacao.id)
        logger.debug('📦 Dados recebidos do backend:', data)
        logger.debug('📊 Tipo dos dados:', typeof data, 'É array?', Array.isArray(data))

        if (Array.isArray(data)) {
          historico.value = data
          logger.debug('✅ Histórico carregado com sucesso:', historico.value.length, 'itens')
        } else {
          console.warn('⚠️ Dados não são array:', data)
          historico.value = []
        }
      } catch (error) {
        console.error('❌ Erro ao carregar histórico:', error)
        showError('Erro ao carregar histórico de edições')
        historico.value = []
      } finally {
        carregando.value = false
      }
    }

    const fechar = () => {
      emit('close')
    }

    const formatarPreco = (valor) => {
      if (!valor && valor !== 0) return '0,00'
      return parseFloat(valor).toFixed(2).replace('.', ',')
    }

    const formatarData = (data) => {
      if (!data) return '-'
      const date = new Date(data + 'T00:00:00')
      return date.toLocaleDateString('pt-BR')
    }

    const formatarDataHora = (dataHora) => {
      if (!dataHora) return '-'
      const date = new Date(dataHora)
      return date.toLocaleString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const calcularDiferenca = (anterior, novo) => {
      if (!anterior || !novo) return ''
      const diff = ((novo - anterior) / anterior) * 100
      const sinal = diff > 0 ? '+' : ''
      return `${sinal}${diff.toFixed(1)}%`
    }

    const getDiferencaClass = (anterior, novo) => {
      if (!anterior || !novo) return ''
      return novo > anterior ? 'diff-aumentou' : 'diff-diminuiu'
    }

    const houveMudancaPreco = (item) => {
      if (item.precoAnterior === null && item.precoNovo === null) return false
      if (item.precoAnterior === null || item.precoNovo === null) return true
      return parseFloat(item.precoAnterior) !== parseFloat(item.precoNovo)
    }

    const houveMudancaPrazo = (item) => {
      if (item.prazoEmDiasUteisAnterior === null && item.prazoEmDiasUteisNovo === null) return false
      if (item.prazoEmDiasUteisAnterior === null || item.prazoEmDiasUteisNovo === null) return true
      return item.prazoEmDiasUteisAnterior !== item.prazoEmDiasUteisNovo
    }

    const houveMudancaDataLimite = (item) => {
      if (item.dataLimiteAnterior === null && item.dataLimiteNovo === null) return false
      if (item.dataLimiteAnterior === null || item.dataLimiteNovo === null) return true
      return item.dataLimiteAnterior !== item.dataLimiteNovo
    }

    const houveMudancaPdf = (item) => {
      // Se foi anexado um novo PDF nesta edição, considera que houve mudança
      // Também considera mudança se o motivo indica adição de anexos
      return item.temAnexoNovo === true || (item.motivoEdicao && item.motivoEdicao.includes('Adicionado') && item.motivoEdicao.includes('anexo'))
    }

    const visualizarPdfHistorico = async (historicoId, tipo) => {
      const pdfKey = `${historicoId}-${tipo}`

      // Se o mesmo PDF já está aberto, fechar
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
        if (tipo === 'anterior') {
          blob = await cotacaoService.obterPdfAnteriorHistorico(historicoId)
        } else if (tipo === 'novo') {
          blob = await cotacaoService.obterPdfNovoHistorico(historicoId)
        }

        if (blob && blob.size > 0) {
          pdfUrl.value = URL.createObjectURL(blob)
        } else {
          showError('PDF não encontrado ou vazio')
          fecharPdfViewer()
        }
      } catch (error) {
        console.error('Erro ao visualizar PDF do histórico:', error)
        showError('Erro ao carregar PDF do histórico')
        fecharPdfViewer()
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
      carregandoPdf.value = false
    }

    return {
      carregando,
      historico,
      pdfAberto,
      pdfUrl,
      carregandoPdf,
      fechar,
      formatarPreco,
      formatarData,
      formatarDataHora,
      calcularDiferenca,
      getDiferencaClass,
      houveMudancaPreco,
      houveMudancaPrazo,
      houveMudancaDataLimite,
      houveMudancaPdf,
      visualizarPdfHistorico
    }
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  padding: 20px;
}

.modal-container {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 900px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.modal-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1F285F;
  margin: 0 0 4px 0;
}

.modal-subtitle {
  font-size: 0.875rem;
  color: #6b7280;
  margin: 0;
}

.btn-close {
  padding: 4px;
  border: none;
  background: none;
  cursor: pointer;
  color: #6b7280;
  transition: all 0.2s;
  border-radius: 4px;
}

.btn-close:hover {
  background: #f3f4f6;
  color: #1f2937;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

/* Loading & Empty States */
.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #6b7280;
  gap: 16px;
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

.empty-state svg {
  color: #d1d5db;
}

/* Histórico Lista */
.historico-lista {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.historico-item {
  display: grid;
  grid-template-columns: 60px 1fr;
  gap: 16px;
  position: relative;
}

.version-badge {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 4px;
}

.version-number {
  background: #1F285F;
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.timeline-connector {
  position: absolute;
  left: 30px;
  top: 36px;
  bottom: -24px;
  width: 2px;
  background: #e5e7eb;
}

/* Histórico Card */
.historico-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 20px;
}

.historico-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.historico-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.historico-titulo {
  font-size: 1rem;
  font-weight: 600;
  color: #1f2937;
}

.historico-data {
  font-size: 0.75rem;
  color: #6b7280;
}

.historico-autor {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #f9fafb;
  border-radius: 6px;
  font-size: 0.875rem;
  color: #374151;
}

/* Motivo */
.historico-motivo {
  background: #fef3c7;
  border: 1px solid #fde68a;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 16px;
}

.motivo-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.75rem;
  font-weight: 600;
  color: #92400e;
  margin-bottom: 6px;
}

.motivo-texto {
  font-size: 0.875rem;
  color: #78350f;
  margin: 0;
  line-height: 1.5;
}

/* Alterações Grid */
.alteracoes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.alteracao-item {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 12px;
}

.alteracao-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #6b7280;
  margin-bottom: 8px;
}

.alteracao-valores {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.875rem;
}

.valor-anterior {
  color: #991b1b;
  text-decoration: line-through;
  font-weight: 500;
}

.arrow-icon {
  color: #6b7280;
  flex-shrink: 0;
}

.valor-novo {
  color: #065f46;
  font-weight: 600;
}

.alteracao-diff {
  margin-top: 6px;
  font-size: 0.75rem;
  font-weight: 600;
}

.diff-aumentou {
  color: #991b1b;
}

.diff-diminuiu {
  color: #065f46;
}

/* Mensagem sem alterações */
.sem-alteracoes {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  color: #6b7280;
  font-size: 0.875rem;
  font-style: italic;
}

.sem-alteracoes svg {
  flex-shrink: 0;
}

/* PDFs */
.historico-pdfs {
  border-top: 1px solid #e5e7eb;
  padding-top: 16px;
}

.pdfs-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #6b7280;
  margin-bottom: 8px;
}

.pdfs-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.btn-pdf {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: #6366f1;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-pdf:hover {
  background: #4f46e5;
  box-shadow: 0 2px 4px rgba(99, 102, 241, 0.3);
}

.btn-pdf svg {
  flex-shrink: 0;
}

.btn-pdf-novo {
  background: #10b981;
}

.btn-pdf-novo:hover {
  background: #059669;
}

/* PDF Viewer */
.pdf-viewer {
  margin-top: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
  background: #f9fafb;
}

.pdf-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #6b7280;
  gap: 16px;
}

.pdf-iframe {
  width: 100%;
  height: 600px;
  border: none;
  display: block;
}

.pdf-error {
  padding: 40px 20px;
  text-align: center;
  color: #dc2626;
  font-weight: 500;
}

/* Transitions */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .modal-container,
.modal-fade-leave-active .modal-container {
  transition: transform 0.3s ease;
}

.modal-fade-enter-from .modal-container,
.modal-fade-leave-to .modal-container {
  transform: scale(0.95);
}

@media (max-width: 768px) {
  .historico-item {
    grid-template-columns: 50px 1fr;
    gap: 12px;
  }

  .timeline-connector {
    left: 25px;
  }

  .alteracoes-grid {
    grid-template-columns: 1fr;
  }

  .historico-header {
    flex-direction: column;
  }
}
</style>
