<template>
  <div class="dashboard-layout" :class="{ 'sidebar-collapsed': isCollapsed }">
    <!-- Header -->
    <DashboardHeader />

    <!-- Sidebar -->
    <DashboardSidebar />

    <!-- Conteúdo Principal -->
    <main class="main-content">
      <!-- Breadcrumb -->
      <div class="breadcrumb">
        <router-link to="/dashboard" class="breadcrumb-home" aria-label="Início">
          <svg viewBox="0 0 24 24" width="16" height="16">
            <path fill="currentColor" d="M12 3l9 8h-3v9h-5v-6H11v6H6v-9H3l9-8z"/>
          </svg>
        </router-link>
        <span class="breadcrumb-separator">/</span>
        <span class="breadcrumb-current">Fornecedores</span>
      </div>
      <!-- Mensagem de Boas-vindas -->
      <div class="welcome-section">
        <div class="welcome-header">
          <div class="welcome-content">
            <h1 class="welcome-title">Gestão de Fornecedores 📋</h1>
            <p class="welcome-subtitle">
              Gerencie cadastro e avaliação de fornecedores de produtos e serviços
            </p>
          </div>
          <div class="action-buttons">
            <button
              v-if="permissions.canCreateFornecedor"
              class="action-button"
              @click="abrirFormularioNovo"
            >
              <svg class="action-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="white" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" />
              </svg>
              Novo Fornecedor
            </button>
          </div>
        </div>
      </div>

      <!-- Indicadores (Cards de Métricas Rápidas) -->
      <div class="metrics-section">
        <div class="metrics-grid">
          <!-- Total de Fornecedores -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon total">
                <Icon name="users" type="metric" :size="24" fill="white" />
              </div>
              <span class="metric-label">Total de Fornecedores</span>
            </div>
            <div class="metric-value">{{ totalFornecedores }}</div>
            <div class="metric-growth positive">{{ novosFornecedoresMes }} novos este mês</div>
          </div>

          <!-- Fornecedores Ativos -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon active">
                <Icon name="active" type="metric" :size="24" fill="white" />
              </div>
              <span class="metric-label">Fornecedores Ativos</span>
            </div>
            <div class="metric-value">{{ fornecedoresAtivos }}</div>
            <div class="metric-growth neutral">{{ percentualAtivos }}% do total</div>
          </div>

          <!-- Fornecedores de Produto -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon rating">
                <Icon name="product" type="metric" :size="24" fill="white" />
              </div>
              <span class="metric-label">Fornecedores de Produto</span>
            </div>
            <div class="metric-value">{{ fornecedoresProduto.length }}</div>
            <div class="metric-growth positive">Produtos</div>
          </div>

          <!-- Fornecedores de Serviço -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon value">
                <Icon name="service" type="metric" :size="24" fill="white" />
              </div>
              <span class="metric-label">Fornecedores de Serviço</span>
            </div>
            <div class="metric-value">{{ fornecedoresServico.length }}</div>
            <div class="metric-growth positive">Serviços</div>
          </div>
        </div>
      </div>

      <!-- Filtros de Pesquisa -->
      <div class="search-section">
        <div class="chip-bar">
          <div class="chip-group">
            <span class="chip-label">Tipo</span>
            <button class="chip" :class="{ active: filtroTipo === '' }" @click="filtroTipo = ''; filtrarFornecedores()" aria-pressed="filtroTipo === ''">
              Todos
            </button>
            <button class="chip" :class="{ active: filtroTipo === 'produto' }" @click="filtroTipo = 'produto'; filtrarFornecedores()" aria-pressed="filtroTipo === 'produto'">
              Produto
            </button>
            <button class="chip" :class="{ active: filtroTipo === 'servico' }" @click="filtroTipo = 'servico'; filtrarFornecedores()" aria-pressed="filtroTipo === 'servico'">
              Serviço
            </button>
          </div>
          <div class="chip-group">
            <span class="chip-label">Status</span>
            <button class="chip" :class="{ active: filtroStatus === '' }" @click="filtroStatus = ''; filtrarFornecedores()" aria-pressed="filtroStatus === ''">
              Todos
            </button>
            <button class="chip" :class="{ active: filtroStatus === 'ativo' }" @click="filtroStatus = 'ativo'; filtrarFornecedores()" aria-pressed="filtroStatus === 'ativo'">
              Ativo
            </button>
            <button class="chip" :class="{ active: filtroStatus === 'inativo' }" @click="filtroStatus = 'inativo'; filtrarFornecedores()" aria-pressed="filtroStatus === 'inativo'">
              Inativo
            </button>
            <button class="chip" :class="{ active: filtroStatus === 'suspenso' }" @click="filtroStatus = 'suspenso'; filtrarFornecedores()" aria-pressed="filtroStatus === 'suspenso'">
              Suspenso
            </button>
          </div>
        </div>
        <div class="search-container">
          <div class="search-input-container">
            <svg class="search-icon" viewBox="0 0 24 24" width="20" height="20">
              <path fill="currentColor"
                d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z" />
            </svg>
            <input type="text" v-model="searchQuery" placeholder="Pesquisar por nome, CNPJ, e-mail..."
              class="search-input" @input="filtrarFornecedores" />
          </div>
          <div class="search-actions">
            <button class="filter-button" @click="limparFiltros">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor"
                  d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
              </svg>
              Limpar
            </button>
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="isLoading" class="loading-section">
        <div class="loading-spinner-large"></div>
        <p>Carregando fornecedores...</p>
      </div>

      <!-- Lista de Fornecedores (Tabela) -->
      <div v-else class="table-section">
        <!-- Versão Desktop: Tabela -->
        <div class="table-container desktop-only">
          <table class="suppliers-table" v-if="fornecedoresFiltrados.length > 0">
            <thead>
              <tr>
                <th class="col-id">ID</th>
                <th>Fornecedor</th>
                <th>Tipo</th>
                <th>Documento</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="fornecedor in fornecedoresFiltrados" :key="fornecedor.id" class="table-row">
                <td class="id-cell">
                  <span class="fornecedor-id">F-{{ fornecedor.id }}</span>
                </td>
                <td class="supplier-cell">
                  <div class="supplier-info">
                    <span class="supplier-name">{{ fornecedor.razaoSocial }}</span>
                  </div>
                </td>
                <td>
                  <span class="type-tag" :class="getTipoClass(fornecedor.tipo)">
                    {{ getTipoLabel(fornecedor.tipo) }}
                  </span>
                </td>
                <td class="document-cell">
                  <span class="document">{{ formatarDocumento(fornecedor) }}</span>
                </td>
                <td>
                  <span class="status-badge" :class="getStatusClass(fornecedor.status || 'ativo')">
                    {{ getStatusLabel(fornecedor.status || 'ativo') }}
                  </span>
                </td>
                <td class="actions-cell">
                  <div class="action-buttons">
                    <button
                      v-if="permissions.canEditFornecedor"
                      class="action-btn edit"
                      @click="editarFornecedor(fornecedor)"
                      title="Editar"
                    >
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                      </svg>
                    </button>
                    <button
                      v-if="permissions.canViewFornecedor"
                      class="action-btn view"
                      @click="visualizarFornecedor(fornecedor)"
                      title="Visualizar Detalhes"
                    >
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" />
                      </svg>
                    </button>
                    <button
                      v-if="permissions.canDeleteFornecedor"
                      class="action-btn delete"
                      @click="confirmarExclusao(fornecedor)"
                      title="Excluir"
                    >
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
                      </svg>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>

          <!-- Estado vazio -->
          <div v-else class="empty-state">
            <svg class="empty-icon" viewBox="0 0 24 24" width="64" height="64">
              <path fill="currentColor"
                d="M12 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S2 17.523 2 12 6.477 2 12 2zm0 1.5a8.5 8.5 0 1 0 0 17 8.5 8.5 0 0 0 0-17zM12 7a5 5 0 1 1 0 10 5 5 0 0 1 0-10zm0 1.5a3.5 3.5 0 1 0 0 7 3.5 3.5 0 0 0 0-7z" />
            </svg>
            <h3>Nenhum fornecedor encontrado</h3>
            <p>Não há fornecedores que correspondam aos filtros aplicados.</p>
            <button class="btn-primary" @click="abrirFormularioNovo">
              Cadastrar Primeiro Fornecedor
            </button>
          </div>
        </div>

        <!-- Versão Mobile: Cards -->
        <div class="fornecedores-cards mobile-only">
          <div v-if="fornecedoresFiltrados.length > 0" class="cards-container">
            <div v-for="fornecedor in fornecedoresFiltrados" :key="fornecedor.id" class="fornecedor-card">
              <div class="card-header">
                <div class="card-header-left">
                  <span class="fornecedor-id-mobile">F-{{ fornecedor.id }}</span>
                  <span class="fornecedor-nome-mobile">{{ fornecedor.razaoSocial }}</span>
                  <span class="document-mobile">{{ formatarDocumento(fornecedor) }}</span>
                </div>
                <div class="card-header-right">
                  <span class="type-tag" :class="getTipoClass(fornecedor.tipo)">
                    {{ getTipoLabel(fornecedor.tipo) }}
                  </span>
                  <span class="status-badge" :class="getStatusClass(fornecedor.status || 'ativo')">
                    {{ getStatusLabel(fornecedor.status || 'ativo') }}
                  </span>
                </div>
              </div>

              <div class="card-actions">
                <button
                  v-if="permissions.canViewFornecedor"
                  class="action-btn-mobile view"
                  @click="visualizarFornecedor(fornecedor)"
                  title="Visualizar"
                >
                  <svg viewBox="0 0 24 24" width="18" height="18">
                    <path fill="currentColor"
                      d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" />
                  </svg>
                  Ver
                </button>

                <button
                  v-if="permissions.canEditFornecedor"
                  class="action-btn-mobile edit"
                  @click="editarFornecedor(fornecedor)"
                  title="Editar"
                >
                  <svg viewBox="0 0 24 24" width="18" height="18">
                    <path fill="currentColor"
                      d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                  </svg>
                  Editar
                </button>

                <button
                  v-if="permissions.canDeleteFornecedor"
                  class="action-btn-mobile delete"
                  @click="confirmarExclusao(fornecedor)"
                  title="Excluir"
                >
                  <svg viewBox="0 0 24 24" width="18" height="18">
                    <path fill="currentColor"
                      d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
                  </svg>
                  Excluir
                </button>
              </div>
            </div>
          </div>

          <!-- Estado vazio mobile -->
          <div v-else class="empty-state">
            <svg class="empty-icon" viewBox="0 0 24 24" width="64" height="64">
              <path fill="currentColor"
                d="M12 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S2 17.523 2 12 6.477 2 12 2zm0 1.5a8.5 8.5 0 1 0 0 17 8.5 8.5 0 0 0 0-17zM12 7a5 5 0 1 1 0 10 5 5 0 0 1 0-10zm0 1.5a3.5 3.5 0 1 0 0 7 3.5 3.5 0 0 0 0-7z" />
            </svg>
            <h3>Nenhum fornecedor encontrado</h3>
            <p>Não há fornecedores que correspondam aos filtros aplicados.</p>
            <button class="btn-primary" @click="abrirFormularioNovo">
              Cadastrar Primeiro Fornecedor
            </button>
          </div>
        </div>
      </div>

      <!-- CTA mobile -->
      <button
        v-if="permissions.canCreateFornecedor"
        class="mobile-fab"
        @click="abrirFormularioNovo"
        aria-label="Novo Fornecedor"
      >
        <svg viewBox="0 0 24 24" width="20" height="20">
          <path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" />
        </svg>
        Novo Fornecedor
      </button>

      <!-- Modal de Fornecedor -->
      <FornecedorForm
        :isVisible="showFornecedorForm"
        :fornecedor="fornecedorEditando"
        @close="fecharFormulario"
        @save="salvarFornecedor"
      />

      <!-- Modal de Confirmação de Exclusão -->
      <ConfirmModal
        :isVisible="showConfirmModal"
        title="Confirmar Exclusão"
        :message="`Tem certeza que deseja excluir o fornecedor '${fornecedorParaExcluir?.razaoSocial}'?`"
        confirmText="Excluir"
        confirmClass="btn-danger"
        @confirm="excluirFornecedor"
        @cancel="showConfirmModal = false"
      />
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, defineAsyncComponent } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from '@/composables/useToast'
import { usePermissions } from '@/composables/usePermissions'
import { useSidebar } from '@/composables/useSidebar'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import Icon from '@/components/ui/Icon.vue'
// Lazy loading para componentes pesados
const FornecedorForm = defineAsyncComponent(() => import('@/features/fornecedores/components/FornecedorForm.vue'))
const ConfirmModal = defineAsyncComponent(() => import('@/components/ui/modals/ConfirmModal.vue'))
import fornecedorService from '@/services/fornecedorService.js'
import relatorioService from '@/services/relatorioService.js'
import logger from '@/utils/logger.js'

// Router
const route = useRoute()
const router = useRouter()
const { success, error: toastError } = useToast()
const { isCollapsed } = useSidebar()

// Permissões baseadas em roles
const { permissions } = usePermissions()

// Estados reativo
const isLoading = ref(true)
const showFornecedorForm = ref(false)
const showConfirmModal = ref(false)
const fornecedorEditando = ref(null)
const fornecedorParaExcluir = ref(null)

// Dados
const fornecedoresProduto = ref([])
const fornecedoresServico = ref([])

// Estados
const searchQuery = ref('')
const filtroTipo = ref('')
const filtroStatus = ref('')

// Computeds
const todosFornecedores = computed(() => {
  const produtoComTipo = fornecedoresProduto.value.map(f => ({ ...f, tipo: 'produto' }))
  const servicoComTipo = fornecedoresServico.value.map(f => ({ ...f, tipo: 'servico' }))
  return [...produtoComTipo, ...servicoComTipo]
})

const fornecedoresFiltrados = computed(() => {
  let resultado = todosFornecedores.value

  // Filtro por texto
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase().trim()
    resultado = resultado.filter(f => {
      const idPadronizado = `f-${f.id}`.toLowerCase()
      const idNumerico = f.id?.toString()
      // Permite buscar por: "1", "F-1", razão social, CNPJ, email ou cidade
      return idNumerico?.includes(query) ||
             idPadronizado.includes(query) ||
             f.razaoSocial?.toLowerCase().includes(query) ||
             f.cnpj?.includes(query) ||
             f.contato?.email?.toLowerCase().includes(query) ||
             f.endereco?.cidade?.toLowerCase().includes(query)
    })
  }

  // Filtro por tipo
  if (filtroTipo.value) {
    resultado = resultado.filter(f => f.tipo === filtroTipo.value)
  }

  // Filtro por status
  if (filtroStatus.value) {
    resultado = resultado.filter(f => (f.status || 'ativo') === filtroStatus.value)
  }

  return resultado
})

const totalFornecedores = computed(() => todosFornecedores.value.length)
const fornecedoresAtivos = computed(() =>
  todosFornecedores.value.filter(f => (f.status || 'ativo') === 'ativo').length
)
const percentualAtivos = computed(() =>
  totalFornecedores.value > 0
    ? Math.round((fornecedoresAtivos.value / totalFornecedores.value) * 100)
    : 0
)
const novosFornecedoresMes = computed(() => {
  return Math.floor(totalFornecedores.value * 0.15)
})

// Métodos de formatação
const formatarDocumento = (fornecedor) => {
  if (!fornecedor.cnpj) return 'Não informado'
  return formatarCNPJ(fornecedor.cnpj)
}

const formatarCNPJ = (cnpj) => {
  if (!cnpj) return ''
  const numbers = cnpj.replace(/\D/g, '')
  return numbers.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, '$1.$2.$3/$4-$5')
}

const formatarTelefone = (telefone) => {
  if (!telefone) return ''
  const numbers = telefone.replace(/\D/g, '')
  if (numbers.length === 11) {
    return numbers.replace(/^(\d{2})(\d{5})(\d{4})$/, '($1) $2-$3')
  } else if (numbers.length === 10) {
    return numbers.replace(/^(\d{2})(\d{4})(\d{4})$/, '($1) $2-$3')
  }
  return telefone
}

const formatarCEP = (cep) => {
  if (!cep) return ''
  const numbers = cep.replace(/\D/g, '')
  return numbers.replace(/^(\d{5})(\d{3})$/, '$1-$2')
}

// Métodos de classificação
const getTipoClass = (tipo) => {
  const classes = {
    produto: 'produto',
    servico: 'servico'
  }
  return classes[tipo] || 'produto'
}

const getTipoLabel = (tipo) => {
  const labels = {
    produto: 'Produto',
    servico: 'Serviço'
  }
  return labels[tipo] || 'Produto'
}

const getStatusClass = (status) => {
  const classes = {
    ativo: 'active',
    inativo: 'inactive',
    suspenso: 'suspended'
  }
  return classes[status] || 'active'
}

const getStatusLabel = (status) => {
  const labels = {
    ativo: 'Ativo',
    inativo: 'Inativo',
    suspenso: 'Suspenso'
  }
  return labels[status] || 'Ativo'
}

// Métodos de ação
const carregarFornecedores = async () => {
  isLoading.value = true
  try {
    const [produtoResponse, servicoResponse] = await Promise.all([
      fornecedorService.listarFornecedoresProduto(),
      fornecedorService.listarFornecedoresServico()
    ])

    fornecedoresProduto.value = produtoResponse || []
    fornecedoresServico.value = servicoResponse || []
  } catch (error) {
    logger.error('Erro ao carregar fornecedores:', error)
  } finally {
    isLoading.value = false
  }
}

const abrirFormularioNovo = () => {
  fornecedorEditando.value = null
  showFornecedorForm.value = true
}

const editarFornecedor = (fornecedor) => {
  fornecedorEditando.value = fornecedor
  showFornecedorForm.value = true
}

const fecharFormulario = () => {
  showFornecedorForm.value = false
  fornecedorEditando.value = null
}

const salvarFornecedor = async (dadosFornecedor) => {
  try {

    if (fornecedorEditando.value) {
      // Atualizar fornecedor existente
      if (dadosFornecedor.tipo === 'produto') {
        await fornecedorService.atualizarFornecedorProduto(fornecedorEditando.value.id, dadosFornecedor)
      } else {
        await fornecedorService.atualizarFornecedorServico(fornecedorEditando.value.id, dadosFornecedor)
      }
    } else {
      // Criar novo fornecedor
      let resultado
      if (dadosFornecedor.tipo === 'produto') {
        resultado = await fornecedorService.criarFornecedorProduto(dadosFornecedor)
      } else {
        resultado = await fornecedorService.criarFornecedorServico(dadosFornecedor)
      }
    }

    await carregarFornecedores()
    fecharFormulario()

    // Mostrar mensagem de sucesso
    success(fornecedorEditando.value ? 'Fornecedor atualizado com sucesso!' : 'Fornecedor cadastrado com sucesso!')

  } catch (error) {
    logger.error('❌ Erro ao salvar fornecedor:', error)

    // Mostrar erro mais específico para o usuário
    let mensagemErro = 'Erro ao salvar fornecedor.'

    // Tratamento especial para erros de validação
    if (error.type === 'VALIDATION_ERROR') {
      mensagemErro = `Erro de validação: ${error.message}`
      toastError(mensagemErro, { duration: 7000 })
      return // Não fechar o formulário para permitir correções
    }

    // Outros tipos de erro
    if (error.message.includes('CORS')) {
      mensagemErro = 'Erro de configuração: O backend não está configurado para aceitar requisições do frontend.'
    } else if (error.message.includes('ECONNREFUSED') || error.message.includes('Network Error')) {
      mensagemErro = 'Erro de conexão: Verifique se o backend está rodando na porta correta (8080).'
    } else if (error.message.includes('401')) {
      mensagemErro = 'Erro de autenticação: Faça login novamente.'
    } else if (error.message.includes('400')) {
      mensagemErro = 'Dados inválidos: Verifique se todos os campos obrigatórios estão preenchidos corretamente.'
    } else if (error.message.includes('409')) {
      mensagemErro = 'Conflito: Já existe um fornecedor com este CNPJ.'
    } else if (error.message.includes('500')) {
      mensagemErro = 'Erro interno do servidor. Tente novamente em alguns instantes.'
    } else if (error.message) {
      mensagemErro = error.message
    }

    toastError(mensagemErro, { duration: 7000 })
  }
}

const confirmarExclusao = (fornecedor) => {
  fornecedorParaExcluir.value = fornecedor
  showConfirmModal.value = true
}

const excluirFornecedor = async () => {
  if (!fornecedorParaExcluir.value) return

  try {
    const fornecedor = fornecedorParaExcluir.value

    if (fornecedor.tipo === 'produto') {
      await fornecedorService.removerFornecedorProduto(fornecedor.id)
    } else {
      await fornecedorService.removerFornecedorServico(fornecedor.id)
    }

    await carregarFornecedores()
  } catch (error) {
    logger.error('Erro ao excluir fornecedor:', error)
  } finally {
    showConfirmModal.value = false
    fornecedorParaExcluir.value = null
  }
}

const visualizarFornecedor = (fornecedor) => {
  router.push({
    name: 'visualizar-fornecedor',
    params: { id: fornecedor.id },
    query: { tipo: fornecedor.tipo }
  })
}

const filtrarFornecedores = () => {
  // A filtragem é feita automaticamente via computed
}

const limparFiltros = () => {
  searchQuery.value = ''
  filtroTipo.value = ''
  filtroStatus.value = ''
}

// Lifecycle
onMounted(() => {
  carregarFornecedores()

  // Verificar se deve abrir modal de cadastrar fornecedor (ação rápida do dashboard)
  if (route.query.action === 'novo-fornecedor') {
    showFornecedorForm.value = true
  }
})
</script>

<style scoped>
/* Importar layout global */
@import '../assets/css/layout.css';
@import '../assets/css/tables-modern.css';

/* Welcome Section */
.welcome-section {
  margin-bottom: 32px;
  padding: 24px 0;
}

.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
}

.welcome-content {
  flex: 1;
}

.welcome-title {
  font-family: inherit;
  font-size: 28px;
  font-weight: 700;
  color: #1F285F;
  margin: 0 0 8px 0;
  line-height: 1.3;
}

.welcome-subtitle {
  font-family: inherit;
  font-size: 16px;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
}

.action-buttons {
  display: flex;
  gap: 12px;
  align-items: center;
}

.action-button {
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 8px !important;
  background: #3b82f6 !important;
  color: white !important;
  border: none !important;
  padding: 12px 24px !important;
  border-radius: 8px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  font-family: inherit !important;
  cursor: pointer !important;
  transition: all 0.2s !important;
  white-space: nowrap !important;
  min-height: 44px !important;
  min-width: 180px !important;
  line-height: 1.5 !important;
}

.action-button.secondary {
  background: #6b7280;
}

.action-button.secondary:hover {
  background: #4b5563;
}

.action-button:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.action-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.action-icon {
  flex-shrink: 0;
}

.loading-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.loading-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Métricas */
.metrics-section {
  margin-bottom: 32px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.metric-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px -3px rgba(0, 0, 0, 0.1);
}

.metric-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.metric-icon svg {
  width: 24px;
  height: 24px;
}

.metric-icon.total {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
}

.metric-icon.active {
  background: linear-gradient(135deg, #10b981, #047857);
}

.metric-icon.rating {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.metric-icon.value {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
}

.metric-label {
  font-size: 0.875rem;
  color: #6b7280;
  font-weight: 500;
}

.metric-value {
  font-size: 2rem;
  font-weight: 700;
  color: #111827;
  margin-bottom: 8px;
}

.metric-growth {
  font-size: 0.875rem;
  font-weight: 500;
}

.metric-growth.positive {
  color: #10b981;
}

.metric-growth.neutral {
  color: #6b7280;
}

/* Seção de busca */
.search-section {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 32px;
}

.search-container {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input-container {
  flex: 1;
  position: relative;
  min-width: 300px;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
}

.search-input {
  width: 100%;
  padding: 12px 12px 12px 44px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  transition: all 0.2s;
  background: white;
  min-width: 0;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-actions {
  display: flex;
  gap: 12px;
}

.filter-select {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  min-width: 140px;
}

.filter-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: white;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-button:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

/* Loading */
.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px;
  color: #6b7280;
}

.loading-spinner-large {
  width: 48px;
  height: 48px;
  border: 4px solid #e5e7eb;
  border-top: 4px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Tabela */
.table-section {
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.table-container {
  overflow-x: auto;
}

.suppliers-table {
  width: 100%;
  border-collapse: collapse;
}

.suppliers-table th {
  background: #f9fafb;
  padding: 16px;
  text-align: left;
  font-weight: 600;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
  font-size: 0.875rem;
}

.suppliers-table td {
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
}

.table-row:hover {
  background: #f9fafb;
}

/* Coluna de ID */
.col-id {
  width: 100px;
}

.id-cell {
  padding-right: 12px;
}

.fornecedor-id {
  font-weight: 600;
  color: #1F285F;
  font-size: 0.875rem;
  font-family: 'Courier New', monospace;
}

.supplier-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.supplier-name {
  font-weight: 600;
  color: #111827;
}

.supplier-code {
  font-size: 0.75rem;
  color: #6b7280;
}

.type-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.type-tag.produto {
  background: #dbeafe;
  color: #1d4ed8;
}

.type-tag.servico {
  background: #d1fae5;
  color: #047857;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.status-badge.active {
  background: #d1fae5;
  color: #047857;
}

.status-badge.inactive {
  background: #fee2e2;
  color: #dc2626;
}

.status-badge.suspended {
  background: #fef3c7;
  color: #d97706;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.edit {
  background: #dbeafe;
  color: #1d4ed8;
}

.action-btn.edit:hover {
  background: #bfdbfe;
}

.action-btn.view {
  background: #e0e7ff;
  color: #4338ca;
}

.action-btn.view:hover {
  background: #c7d2fe;
}

.action-btn.delete {
  background: #fee2e2;
  color: #dc2626;
}

.action-btn.delete:hover {
  background: #fecaca;
}

/* Estado vazio */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 32px;
  text-align: center;
}

.empty-icon {
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 8px 0;
}

.empty-state p {
  color: #6b7280;
  margin: 0 0 24px 0;
}

.btn-primary {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

/* Mobile Cards Layout */
.fornecedores-cards {
  display: none;
}

.cards-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.fornecedor-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f3f4f6;
}

.card-header-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}

.card-header-right {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: flex-end;
}

.fornecedor-id-mobile {
  font-weight: 600;
  font-size: 0.875rem;
  color: #1F285F;
  font-family: 'Courier New', monospace;
  margin-bottom: 4px;
}

.fornecedor-nome-mobile {
  font-weight: 600;
  font-size: 1rem;
  color: #1f2937;
}

.document-mobile {
  font-size: 0.8125rem;
  color: #6b7280;
}

.card-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.action-btn-mobile {
  flex: 1;
  min-width: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 14px;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
  background: white;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  color: #6b7280;
}

.action-btn-mobile:hover {
  background: #f3f4f6;
  color: #374151;
}

.action-btn-mobile.view {
  color: #3b82f6;
  border-color: #3b82f6;
}

.action-btn-mobile.view:hover {
  background: #dbeafe;
}

.action-btn-mobile.edit {
  color: #f59e0b;
  border-color: #f59e0b;
}

.action-btn-mobile.edit:hover {
  background: #fef3c7;
}

.action-btn-mobile.delete {
  color: #ef4444;
  border-color: #ef4444;
}

.action-btn-mobile.delete:hover {
  background: #fee2e2;
}

/* Visibility toggles */
.desktop-only {
  display: block;
}

.mobile-only {
  display: none;
}

/* Floating Action Button (mobile) */
.mobile-fab {
  display: none;
  position: fixed;
  right: 16px;
  bottom: 16px;
  background: #1F285F;
  color: white;
  border: none;
  border-radius: 999px;
  padding: 12px 18px;
  font-size: 0.875rem;
  font-weight: 600;
  box-shadow: 0 10px 20px rgba(31, 40, 95, 0.3);
  z-index: 1000;
  gap: 8px;
  align-items: center;
}

.mobile-fab svg {
  display: block;
}

/* Responsividade */
@media (max-width: 1024px) {
  .welcome-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .search-container {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .search-actions {
    flex-wrap: wrap;
  }

  .filter-select {
    flex: 1;
    min-width: 140px;
  }

  /* Toggle entre table e cards */
  .desktop-only {
    display: none !important;
  }

  .mobile-only {
    display: block !important;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding-bottom: 88px;
  }

  .welcome-section {
    margin-bottom: 24px;
    padding: 16px 0;
  }

  .welcome-title {
    font-size: 22px;
  }

  .welcome-subtitle {
    font-size: 14px;
  }

  .action-button {
    padding: 10px 16px;
    font-size: 13px;
  }

  .metrics-section {
    margin-bottom: 24px;
  }

  .metrics-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .mobile-fab {
    display: inline-flex;
  }

  .metric-card {
    padding: 14px;
  }

  .metric-header {
    gap: 8px;
    margin-bottom: 12px;
  }

  .metric-icon {
    width: 36px;
    height: 36px;
  }

  .metric-icon svg {
    width: 20px;
    height: 20px;
  }

  .metric-value {
    font-size: 1.375rem;
  }

  .metric-label {
    font-size: 0.6875rem;
  }

  .metric-growth {
    font-size: 0.6875rem;
  }

  .search-section {
    padding: 16px;
    margin-bottom: 20px;
  }

  .search-container {
    gap: 12px;
  }

  .search-actions {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 10px;
    width: 100%;
  }

  .filter-select {
    min-width: auto;
    width: 100%;
  }

  .filter-button {
    grid-column: 1 / -1;
    width: 100%;
    justify-content: center;
  }

  /* Cards mobile */
  .fornecedores-cards {
    padding: 0;
  }

  .cards-container {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .fornecedor-card {
    padding: 14px;
  }

  .card-header {
    flex-direction: column;
    gap: 12px;
  }

  .card-header-left {
    gap: 6px;
  }

  .card-header-right {
    flex-direction: row;
    gap: 8px;
  }

  .card-actions {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .action-btn-mobile {
    flex: 1;
    min-width: 80px;
  }
}

@media (max-width: 480px) {
  .welcome-section {
    margin-bottom: 16px;
    padding: 12px 0;
  }

  .welcome-title {
    font-size: 20px;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .metric-card {
    padding: 14px;
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 12px;
  }

  .metric-header {
    flex: 0 0 auto;
    margin-bottom: 0;
    flex-direction: row;
    align-items: center;
  }

  .metric-icon {
    width: 36px;
    height: 36px;
  }

  .metric-label {
    display: block;
    font-size: 0.75rem;
  }

  .metric-value {
    font-size: 1.25rem;
    margin-bottom: 0;
  }

  .metric-growth {
    margin-top: 2px;
  }

  .search-container {
    gap: 12px;
  }

  .search-input {
    padding: 10px 10px 10px 40px;
  }

  .search-actions {
    grid-template-columns: 1fr;
  }

  .filter-select {
    padding: 10px;
  }

  .fornecedores-cards {
    padding: 0;
  }

  .fornecedor-card {
    padding: 12px;
    gap: 10px;
  }

  .fornecedor-id-mobile {
    font-size: 0.75rem;
  }

  .fornecedor-nome-mobile {
    font-size: 0.875rem;
  }

  .document-mobile {
    font-size: 0.75rem;
  }

  .type-tag,
  .status-badge {
    padding: 3px 8px;
    font-size: 0.65rem;
  }

  .card-actions {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .action-btn-mobile {
    width: 100%;
    padding: 10px 12px;
    font-size: 0.8125rem;
    justify-content: center;
  }

  .empty-state {
    padding: 32px 16px;
  }

  .empty-icon {
    width: 48px;
    height: 48px;
  }

  .empty-state h3 {
    font-size: 1rem;
  }

  .empty-state p {
    font-size: 0.875rem;
  }
}

/* Ajustes para telas muito pequenas (360px e menor) */
@media (max-width: 380px) {
  .search-section {
    padding: 6px;
    margin-bottom: 8px;
  }

  /* Welcome Section - Compacto */
  .welcome-section {
    margin-bottom: 12px;
    padding: 6px 0;
  }

  .welcome-header {
    gap: 10px;
    flex-direction: column;
    align-items: stretch;
  }

  .welcome-content {
    min-width: 0;
  }

  .welcome-title {
    font-size: 15px;
    word-break: break-word;
    overflow-wrap: break-word;
    line-height: 1.2;
  }

  .welcome-subtitle {
    font-size: 11px;
    line-height: 1.3;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .action-buttons {
    width: 100%;
    flex-direction: column;
  }

  .action-button {
    padding: 10px 12px !important;
    font-size: 12px !important;
    min-width: auto !important;
    min-height: 40px !important;
    width: 100% !important;
    gap: 6px !important;
    border-radius: 6px !important;
  }

  .action-icon {
    width: 16px !important;
    height: 16px !important;
    flex-shrink: 0;
  }

  /* Métricas - Layout horizontal compacto */
  .metrics-section {
    margin-bottom: 12px;
  }

  .metrics-grid {
    gap: 8px;
  }

  .metric-card {
    padding: 10px;
    gap: 10px;
    flex-direction: row;
    align-items: center;
  }

  .metric-header {
    flex-shrink: 0;
    margin-bottom: 0;
    gap: 8px;
  }

  .metric-icon {
    width: 32px;
    height: 32px;
    flex-shrink: 0;
  }

  .metric-icon svg {
    width: 16px;
    height: 16px;
  }

  .metric-label {
    font-size: 0.7rem;
    line-height: 1.2;
    max-width: 80px;
  }

  .metric-value {
    font-size: 1.125rem;
    margin-bottom: 0;
  }

  .metric-growth {
    font-size: 0.65rem;
    margin-top: 2px;
  }

  /* Busca - Compacto */
  .search-section {
    padding: 10px;
    margin-bottom: 10px;
  }

  .search-container {
    gap: 10px;
  }

  .search-input-container {
    width: 100%;
  }

  .search-input {
    padding: 10px 10px 10px 36px;
    font-size: 13px;
    border-radius: 6px;
  }

  .search-icon {
    width: 16px;
    height: 16px;
    left: 10px;
  }

  .search-actions {
    gap: 8px;
    display: flex;
    flex-direction: column;
    width: 100%;
  }

  .filter-select {
    padding: 10px 8px;
    font-size: 12px;
    min-width: auto;
    width: 100%;
    border-radius: 6px;
  }

  .filter-button {
    padding: 10px 12px;
    font-size: 12px;
    gap: 6px;
    width: 100%;
    justify-content: center;
    border-radius: 6px;
  }

  .filter-button svg {
    width: 14px;
    height: 14px;
  }

  /* Table Section */
  .table-section {
    border-radius: 8px;
    overflow: hidden;
  }

  /* Cards de Fornecedores - Layout Otimizado */
  .cards-container {
    gap: 10px;
  }

  .fornecedor-card {
    padding: 12px;
    gap: 10px;
    border-radius: 8px;
  }

  .card-header {
    gap: 8px;
    padding-bottom: 10px;
    flex-direction: column;
  }

  .card-header-left {
    gap: 4px;
    min-width: 0;
  }

  .card-header-right {
    gap: 6px;
    flex-direction: row;
    flex-wrap: wrap;
    align-items: center;
    justify-content: flex-start;
  }

  .fornecedor-id-mobile {
    font-size: 0.7rem;
  }

  .fornecedor-nome-mobile {
    font-size: 0.8125rem;
    word-break: break-word;
    overflow-wrap: break-word;
    line-height: 1.3;
  }

  .document-mobile {
    font-size: 0.7rem;
    word-break: break-all;
  }

  .type-tag,
  .status-badge {
    padding: 3px 8px;
    font-size: 0.65rem;
    white-space: nowrap;
  }

  /* Botões de Ação - Grid de 2 colunas */
  .card-actions {
    gap: 6px;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
  }

  .action-btn-mobile {
    padding: 10px 8px;
    font-size: 0.75rem;
    gap: 4px;
    min-width: 0;
    flex: none;
    width: 100%;
    border-radius: 6px;
  }

  .action-btn-mobile:last-child:nth-child(odd) {
    grid-column: 1 / -1;
  }

  .action-btn-mobile svg {
    width: 14px;
    height: 14px;
    flex-shrink: 0;
  }

  /* Empty State */
  .empty-state {
    padding: 24px 12px;
  }

  .empty-icon {
    width: 40px;
    height: 40px;
  }

  .empty-state h3 {
    font-size: 0.875rem;
  }

  .empty-state p {
    font-size: 0.75rem;
    margin-bottom: 16px;
  }

  .btn-primary {
    padding: 10px 16px;
    font-size: 0.8125rem;
    width: 100%;
  }
}
</style>



