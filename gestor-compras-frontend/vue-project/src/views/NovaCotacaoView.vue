<template>
  <div class="dashboard-layout">
    <!-- Header -->
    <DashboardHeader />

    <!-- Sidebar -->
    <DashboardSidebar />

    <!-- Conteúdo Principal -->
    <main class="main-content">
      <!-- Breadcrumb -->
      <div class="breadcrumb">
        <button @click="voltarParaLista" class="btn-voltar">
          <svg viewBox="0 0 24 24" width="18" height="18">
            <path fill="currentColor" d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"/>
          </svg>
          Voltar
        </button>
        <span class="breadcrumb-separator">|</span>
        <router-link to="/cotacoes" class="breadcrumb-link">
          Cotações
        </router-link>
        <span class="breadcrumb-separator">/</span>
        <span class="breadcrumb-current">{{ modoEdicao ? 'Editar Cotação' : 'Nova Cotação' }}</span>
      </div>

      <!-- Header do formulário -->
      <div class="welcome-section">
        <div class="welcome-header">
          <div class="welcome-content">
            <div class="titulo-form">
              <h1 class="welcome-title">{{ modoEdicao ? 'Editar Cotação' : 'Nova Cotação' }} 📋</h1>
              <p class="welcome-subtitle" v-if="modoEdicao">
                Cotação #{{ String(cotacao.id).padStart(3, '0') }} - Edição de dados
              </p>
              <p class="welcome-subtitle" v-else>
                Preencha os dados para criar uma nova solicitação de cotação
              </p>
            </div>
          </div>
        </div>
      </div>

      <!-- Barra de progresso -->
      <div class="progress-section">
        <div class="progress-container">
          <div class="progress-steps">
            <div
              v-for="(step, index) in steps"
              :key="index"
              :class="['step', {
                'active': index === etapaAtual,
                'completed': index < etapaAtual,
                'disabled': index > etapaAtual
              }]"
              @click="irParaEtapa(index)"
            >
              <div class="step-number">
                <svg v-if="index < etapaAtual" class="icon-check" viewBox="0 0 24 24" width="16" height="16">
                  <path fill="white" d="M20 6L9 17l-5-5"/>
                </svg>
                <span v-else>{{ index + 1 }}</span>
              </div>
              <div class="step-info">
                <div class="step-title">{{ step.titulo }}</div>
                <div class="step-subtitle">{{ step.subtitulo }}</div>
              </div>
            </div>
          </div>
          <div class="progress-bar">
            <div
              class="progress-fill"
              :style="{ width: `${(etapaAtual / (steps.length - 1)) * 100}%` }"
            ></div>
          </div>
        </div>
      </div>

      <!-- Conteúdo do formulário -->
      <div class="form-content">
        <!-- Etapa 1: Informações Básicas -->
        <div v-show="etapaAtual === 0" class="etapa">
          <div class="etapa-header">
            <h2>Informações Básicas</h2>
            <p>Defina as informações gerais da cotação</p>
          </div>

          <div class="form-grid">
            <div class="form-group span-2">
              <label class="form-label required">Descrição da Cotação</label>
              <input
                v-model="cotacao.descricao"
                type="text"
                class="form-input"
                placeholder="Ex: Compra de material de escritório"
                :class="{ 'error': erros.descricao }"
              >
              <span v-if="erros.descricao" class="error-message">{{ erros.descricao }}</span>
            </div>

            <div class="form-group">
              <label class="form-label required">Tipo de Cotação</label>
              <select v-model="cotacao.tipo" class="form-select" :class="{ 'error': erros.tipo }">
                <option value="">Selecione o tipo</option>
                <option value="produto">Produtos</option>
                <option value="servico">Serviços</option>
                <option value="misto">Produtos e Serviços</option>
              </select>
              <span v-if="erros.tipo" class="error-message">{{ erros.tipo }}</span>
            </div>

            <div class="form-group">
              <label class="form-label required">Centro de Custo</label>
              <select v-model="cotacao.centroCustoId" class="form-select" :class="{ 'error': erros.centroCustoId }">
                <option value="">Selecione o centro de custo</option>
                <option v-for="centro in centrosCusto" :key="centro.id" :value="centro.id">
                  {{ centro.nome }}
                </option>
              </select>
              <span v-if="erros.centroCustoId" class="error-message">{{ erros.centroCustoId }}</span>
            </div>

            <div class="form-group">
              <label class="form-label required">Data Limite</label>
              <input
                v-model="cotacao.dataLimite"
                type="datetime-local"
                class="form-input"
                :min="dataMinima"
                :class="{ 'error': erros.dataLimite }"
              >
              <span v-if="erros.dataLimite" class="error-message">{{ erros.dataLimite }}</span>
            </div>

            <div class="form-group span-2">
              <label class="form-label">Observações</label>
              <textarea
                v-model="cotacao.observacoes"
                class="form-textarea"
                placeholder="Informações adicionais sobre a cotação"
                rows="3"
              ></textarea>
            </div>
          </div>
        </div>

        <!-- Navegação -->
        <div class="form-navigation">
          <button
            v-if="etapaAtual > 0"
            @click="etapaAnterior"
            class="btn-anterior"
            type="button"
          >
            Anterior
          </button>
          <div v-else></div>

          <div class="etapa-info">
            Etapa {{ etapaAtual + 1 }} de {{ steps.length }}
          </div>

          <button
            v-if="etapaAtual < steps.length - 1"
            @click="proximaEtapa"
            class="btn-proximo"
            :disabled="!podeAvancar"
            type="button"
          >
            Próximo
          </button>
          <button
            v-else
            @click="salvarCotacao"
            class="btn-salvar"
            :disabled="!podeAvancar"
            type="button"
          >
            {{ modoEdicao ? 'Atualizar' : 'Salvar' }} Cotação
          </button>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="carregando" class="loading-overlay">
        <div class="loading-content">
          <div class="loading-spinner"></div>
          <p>{{ modoEdicao ? 'Atualizando' : 'Salvando' }} cotação...</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import cotacaoService from '../services/cotacaoService.js'

const router = useRouter()
const route = useRoute()

// Estado reativo
const carregando = ref(false)
const etapaAtual = ref(0)
const erros = ref({})

// Dados da cotação
const cotacao = ref({
  id: null,
  descricao: '',
  tipo: '',
  centroCustoId: null,
  dataLimite: '',
  observacoes: '',
  status: 'rascunho'
})

// Dados auxiliares
const centrosCusto = ref([
  { id: 1, nome: 'Administrativo' },
  { id: 2, nome: 'TI - Suprimentos' },
  { id: 3, nome: 'Segurança do Trabalho' },
  { id: 4, nome: 'Manutenção' },
  { id: 5, nome: 'Facilities' }
])

// Configuração das etapas
const steps = [
  { titulo: 'Informações', subtitulo: 'Dados básicos' },
  { titulo: 'Itens', subtitulo: 'Produtos/Serviços' },
  { titulo: 'Fornecedores', subtitulo: 'Seleção' },
  { titulo: 'Anexos', subtitulo: 'Documentos' },
  { titulo: 'Revisão', subtitulo: 'Confirmação' }
]

// Computadas
const modoEdicao = computed(() => !!route.params.id)

const dataMinima = computed(() => {
  const agora = new Date()
  agora.setHours(agora.getHours() + 1)
  return agora.toISOString().slice(0, 16)
})

const podeAvancar = computed(() => {
  switch (etapaAtual.value) {
    case 0: // Informações básicas
      return cotacao.value.descricao &&
             cotacao.value.tipo &&
             cotacao.value.centroCustoId &&
             cotacao.value.dataLimite
    case 1: // Itens (por enquanto sempre true para demo)
      return true
    case 2: // Fornecedores
      return true
    case 3: // Anexos
      return true
    case 4: // Revisão
      return true
    default:
      return false
  }
})

// Métodos
const voltarParaLista = () => {
  router.push('/cotacoes')
}

const irParaEtapa = (index) => {
  if (index <= etapaAtual.value || index === etapaAtual.value + 1) {
    etapaAtual.value = index
  }
}

const etapaAnterior = () => {
  if (etapaAtual.value > 0) {
    etapaAtual.value--
  }
}

const proximaEtapa = () => {
  if (validarEtapaAtual()) {
    etapaAtual.value++
  }
}

const validarEtapaAtual = () => {
  erros.value = {}

  switch (etapaAtual.value) {
    case 0:
      if (!cotacao.value.descricao) {
        erros.value.descricao = 'Descrição é obrigatória'
      }
      if (!cotacao.value.tipo) {
        erros.value.tipo = 'Tipo é obrigatório'
      }
      if (!cotacao.value.centroCustoId) {
        erros.value.centroCustoId = 'Centro de custo é obrigatório'
      }
      if (!cotacao.value.dataLimite) {
        erros.value.dataLimite = 'Data limite é obrigatória'
      }
      break
  }

  return Object.keys(erros.value).length === 0
}

const salvarCotacao = async () => {
  if (!validarEtapaAtual()) return

  try {
    carregando.value = true

    if (modoEdicao.value) {
      await cotacaoService.atualizar(cotacao.value.id, cotacao.value)
    } else {
      await cotacaoService.criar(cotacao.value)
    }

    router.push('/cotacoes')
  } catch (error) {
    console.error('Erro ao salvar cotação:', error)
  } finally {
    carregando.value = false
  }
}

const carregarCotacao = async () => {
  if (!modoEdicao.value) return

  try {
    carregando.value = true
    const response = await cotacaoService.obterPorId(route.params.id)
    cotacao.value = { ...cotacao.value, ...response.data }
  } catch (error) {
    console.error('Erro ao carregar cotação:', error)
    router.push('/cotacoes')
  } finally {
    carregando.value = false
  }
}

// Lifecycle
onMounted(() => {
  if (modoEdicao.value) {
    carregarCotacao()
  }
})
</script>

<style scoped>
/* Importar layout global */
@import '../assets/css/layout.css';

/* Breadcrumb */
.breadcrumb {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 24px;
  font-size: 0.875rem;
  white-space: nowrap;
  overflow-x: auto;
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
  line-height: 1;
  transition: all 0.2s;
  white-space: nowrap;
  flex-shrink: 0;
}

.btn-voltar:hover {
  background: #e5e7eb;
}

.breadcrumb-link {
  color: #1F285F;
  text-decoration: none;
  font-weight: 500;
  white-space: nowrap;
  line-height: 1;
  flex-shrink: 0;
}

.breadcrumb-link:hover {
  text-decoration: underline;
}

.breadcrumb-separator {
  color: #d1d5db;
  user-select: none;
  line-height: 1;
  flex-shrink: 0;
}

.breadcrumb-current {
  color: #6b7280;
  white-space: nowrap;
  line-height: 1;
  flex-shrink: 0;
}

/* Welcome Section personalizada para formulário */
.welcome-section {
  margin-bottom: 32px;
  padding: 24px 0;
}

.welcome-header {
  display: flex;
  align-items: flex-start;
  gap: 24px;
}

.welcome-content {
  flex: 1;
}

.welcome-title {
  font-family: Arial, sans-serif;
  font-size: 28px;
  font-weight: 700;
  color: #1F285F;
  margin: 0 0 8px 0;
  line-height: 1.3;
}

.welcome-subtitle {
  font-family: Arial, sans-serif;
  font-size: 16px;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
}

/* Seção de progresso */
.progress-section {
  margin-bottom: 32px;
}

.progress-container {
  background: white;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #e5e7eb;
}

.progress-steps {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
  position: relative;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  flex: 1;
  position: relative;
  z-index: 2;
}

.step.disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.875rem;
  margin-bottom: 8px;
  transition: all 0.2s;
}

.step:not(.active):not(.completed) .step-number {
  background: #f3f4f6;
  color: #6b7280;
  border: 2px solid #e5e7eb;
}

.step.active .step-number {
  background: #1F285F;
  color: white;
  border: 2px solid #1F285F;
}

.step.completed .step-number {
  background: #10b981;
  color: white;
  border: 2px solid #10b981;
}

.step-info {
  text-align: center;
}

.step-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 2px;
}

.step-subtitle {
  font-size: 0.75rem;
  color: #6b7280;
}

.progress-bar {
  height: 4px;
  background: #e5e7eb;
  border-radius: 2px;
  position: relative;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #1F285F, #10b981);
  border-radius: 2px;
  transition: width 0.3s ease;
}

/* Conteúdo do formulário */
.form-content {
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  margin-bottom: 32px;
}

.etapa {
  padding: 32px;
}

.etapa-header {
  margin-bottom: 32px;
  text-align: center;
}

.etapa-header h2 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #111827;
  margin: 0 0 8px 0;
}

.etapa-header p {
  color: #6b7280;
  margin: 0;
}

/* Grid de formulário */
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group.span-2 {
  grid-column: span 2;
}

.form-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.form-label.required::after {
  content: " *";
  color: #ef4444;
}

.form-input,
.form-select,
.form-textarea {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #1F285F;
  box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.1);
}

.form-input.error,
.form-select.error,
.form-textarea.error {
  border-color: #ef4444;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.error-message {
  color: #ef4444;
  font-size: 0.75rem;
  margin-top: 4px;
}

/* Navegação entre etapas */
.form-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
}

.etapa-info {
  font-size: 0.875rem;
  color: #6b7280;
  font-weight: 500;
}

.btn-anterior,
.btn-proximo,
.btn-salvar {
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  font-size: 0.875rem;
}

.btn-anterior {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
}

.btn-anterior:hover {
  background: #e5e7eb;
}

.btn-proximo,
.btn-salvar {
  background: #1F285F;
  color: white;
}

.btn-proximo:hover,
.btn-salvar:hover {
  background: #2d3a7f;
}

.btn-anterior:disabled,
.btn-proximo:disabled,
.btn-salvar:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Loading */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.loading-content {
  text-align: center;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e5e7eb;
  border-top: 4px solid #1F285F;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Responsividade */
@media (max-width: 768px) {
  .welcome-header {
    flex-direction: column;
    gap: 16px;
  }

  .progress-steps {
    flex-direction: column;
    gap: 16px;
  }

  .step {
    flex-direction: row;
    justify-content: flex-start;
    text-align: left;
  }

  .step-number {
    margin-bottom: 0;
    margin-right: 12px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-group.span-2 {
    grid-column: span 1;
  }

  .form-navigation {
    flex-direction: column;
    gap: 12px;
  }

  .btn-anterior,
  .btn-proximo,
  .btn-salvar {
    width: 100%;
  }
}
</style>
