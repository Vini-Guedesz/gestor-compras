<template>
  <div class="dashboard-layout">
    <!-- Header -->
    <DashboardHeader />

    <!-- Sidebar -->
    <DashboardSidebar />

    <!-- Conteúdo Principal -->
    <main class="main-content">
      <!-- Mensagem de Boas-vindas -->
      <div class="welcome-section">
        <div class="welcome-header">
          <div class="welcome-content">
            <h1 class="welcome-title">Gestão de Fornecedores 📋</h1>
            <p class="welcome-subtitle">
              Gerencie cadastro e avaliação de fornecedores de produtos e serviços
            </p>
          </div>
          <button class="action-button" @click="abrirFormularioNovo">
            <svg class="action-icon" viewBox="0 0 24 24" width="20" height="20">
              <path fill="white" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" />
            </svg>
            Novo Fornecedor
          </button>
        </div>
      </div>

      <!-- Indicadores (Cards de Métricas Rápidas) -->
      <div class="metrics-section">
        <div class="metrics-grid">
          <!-- Total de Fornecedores -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon total">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white"
                    d="M16 4c0-1.11.89-2 2-2s2 .89 2 2-.89 2-2 2-2-.89-2-2zm4 18v-6h2.5l-2.54-7.63A2 2 0 0 0 18.04 7H16c-.8 0-1.54.37-2.01.99L12 10l2.01-2.01C14.54 7.37 15.2 7 16 7h2.04c1.23 0 2.18 1.24 1.92 2.63l2.54 7.63H20v6h-4z" />
                </svg>
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
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white"
                    d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
                </svg>
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
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white"
                    d="M20,8H4V6H20M20,18H4V12H20M20,4H4C2.89,4 2,4.89 2,6V18A2,2 0 0,0 4,20H20A2,2 0 0,0 22,18V6C22,5.11 21.1,4 20,4Z" />
                </svg>
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
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M19,6.41L17.59,5L7,15.59V9H5V19H15V17H8.41L19,6.41Z" />
                </svg>
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
            <select v-model="filtroTipo" @change="filtrarFornecedores" class="filter-select">
              <option value="">Todos os tipos</option>
              <option value="produto">Produto</option>
              <option value="servico">Serviço</option>
            </select>
            <select v-model="filtroStatus" @change="filtrarFornecedores" class="filter-select">
              <option value="">Todos os status</option>
              <option value="ativo">Ativo</option>
              <option value="inativo">Inativo</option>
              <option value="suspenso">Suspenso</option>
            </select>
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
        <div class="table-container">
          <table class="suppliers-table" v-if="fornecedoresFiltrados.length > 0">
            <thead>
              <tr>
                <th>Fornecedor</th>
                <th>Tipo</th>
                <th>Documento</th>
                <th>Contato</th>
                <th>Cidade</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="fornecedor in fornecedoresFiltrados" :key="fornecedor.id" class="table-row">
                <td class="supplier-cell">
                  <div class="supplier-info">
                    <span class="supplier-name">{{ fornecedor.razaoSocial }}</span>
                    <span class="supplier-code" v-if="fornecedor.inscricaoEstadual">
                      IE: {{ fornecedor.inscricaoEstadual }}
                    </span>
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
                <td class="contact-cell">
                  <div class="contact-info">
                    <span class="contact-email" v-if="fornecedor.contato?.email">
                      {{ fornecedor.contato.email }}
                    </span>
                    <span class="contact-phone" v-if="fornecedor.contato?.telefone">
                      {{ formatarTelefone(fornecedor.contato.telefone) }}
                    </span>
                  </div>
                </td>
                <td class="address-cell">
                  <span v-if="fornecedor.endereco?.cidade">
                    {{ fornecedor.endereco.cidade }}/{{ fornecedor.endereco.estado }}
                  </span>
                  <span v-else class="text-muted">Não informado</span>
                </td>
                <td>
                  <span class="status-badge" :class="getStatusClass(fornecedor.status || 'ativo')">
                    {{ getStatusLabel(fornecedor.status || 'ativo') }}
                  </span>
                </td>
                <td class="actions-cell">
                  <div class="action-buttons">
                    <button class="action-btn edit" @click="editarFornecedor(fornecedor)" title="Editar">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                      </svg>
                    </button>
                    <button class="action-btn view" @click="visualizarFornecedor(fornecedor)" title="Visualizar">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" />
                      </svg>
                    </button>
                    <button class="action-btn delete" @click="confirmarExclusao(fornecedor)" title="Excluir">
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
            <div class="empty-icon">
              <svg viewBox="0 0 24 24" width="48" height="48">
                <path fill="currentColor"
                  d="M16 4c0-1.11.89-2 2-2s2 .89 2 2-.89 2-2 2-2-.89-2-2zm4 18v-6h2.5l-2.54-7.63A2 2 0 0 0 18.04 7H16c-.8 0-1.54.37-2.01.99L12 10l2.01-2.01C14.54 7.37 15.2 7 16 7h2.04c1.23 0 2.18 1.24 1.92 2.63l2.54 7.63H20v6h-4z" />
              </svg>
            </div>
            <h3>Nenhum fornecedor encontrado</h3>
            <p>{{ searchQuery ? 'Tente ajustar os filtros de pesquisa.' : 'Comece cadastrando seu primeiro fornecedor.'
            }}</p>
            <button v-if="!searchQuery" class="btn-primary" @click="abrirFormularioNovo">
              Cadastrar Primeiro Fornecedor
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- Modal de Formulário -->
    <FornecedorForm :is-visible="showForm" :fornecedor="fornecedorEditando" @close="fecharFormulario"
      @save="salvarFornecedor" />

    <!-- Modal de Confirmação -->
    <ConfirmModal :is-visible="showConfirmDelete" type="danger" title="Excluir Fornecedor"
      :message="`Tem certeza que deseja excluir o fornecedor '${fornecedorParaExcluir?.razaoSocial}'?`"
      details="Esta ação não pode ser desfeita. Todos os dados do fornecedor serão perdidos permanentemente."
      confirm-text="Excluir" cancel-text="Cancelar" @confirm="excluirFornecedor" @cancel="cancelarExclusao" />

    <!-- Toast de Notificações -->
    <div v-if="notification.show" class="notification" :class="notification.type">
      <div class="notification-content">
        <svg viewBox="0 0 24 24" width="20" height="20">
          <path fill="currentColor" :d="getNotificationIcon" />
        </svg>
        <span>{{ notification.message }}</span>
      </div>
      <button @click="hideNotification" class="notification-close">
        <svg viewBox="0 0 24 24" width="16" height="16">
          <path fill="currentColor"
            d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
        </svg>
      </button>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import DashboardHeader from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'
import FornecedorForm from '@/components/FornecedorForm.vue'
import ConfirmModal from '@/components/ConfirmModal.vue'
import { fornecedorProdutoService, fornecedorServicoService, fornecedorUtils } from '@/services/fornecedorService.js'

export default {
  name: 'FornecedoresView',
  components: {
    DashboardHeader,
    DashboardSidebar,
    FornecedorForm,
    ConfirmModal
  },
  setup() {
    // Estados reativo
    const isLoading = ref(true)
    const showForm = ref(false)
    const showConfirmDelete = ref(false)
    const fornecedorEditando = ref(null)
    const fornecedorParaExcluir = ref(null)

    // Dados
    const fornecedoresProduto = ref([])
    const fornecedoresServico = ref([])

    // Filtros
    const searchQuery = ref('')
    const filtroTipo = ref('')
    const filtroStatus = ref('')

    // Notificações
    const notification = reactive({
      show: false,
      type: 'success',
      message: ''
    })

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
        resultado = resultado.filter(f =>
          f.razaoSocial?.toLowerCase().includes(query) ||
          f.cnpj?.includes(query) ||
          f.contato?.email?.toLowerCase().includes(query) ||
          f.endereco?.cidade?.toLowerCase().includes(query)
        )
      }

      // Filtro por tipo
      if (filtroTipo.value) {
        resultado = resultado.filter(f => f.tipo === filtroTipo.value)
      }

      // Filtro por status (assumindo que todos são ativos por padrão)
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
      // Aqui você pode implementar lógica para calcular novos fornecedores do mês
      return Math.floor(totalFornecedores.value * 0.1)
    })

    // Ícone de notificação
    const getNotificationIcon = computed(() => {
      const icons = {
        success: 'M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z',
        error: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v4z',
        warning: 'M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z'
      }
      return icons[notification.type] || icons.success
    })

    // Métodos
    const carregarFornecedores = async () => {
      isLoading.value = true
      try {
        const [produtoResponse, servicoResponse] = await Promise.all([
          fornecedorProdutoService.listar(),
          fornecedorServicoService.listar()
        ])

        fornecedoresProduto.value = produtoResponse || []
        fornecedoresServico.value = servicoResponse || []
      } catch (error) {
        console.error('Erro ao carregar fornecedores:', error)
        showNotification('error', 'Erro ao carregar fornecedores')
      } finally {
        isLoading.value = false
      }
    }

    const abrirFormularioNovo = () => {
      fornecedorEditando.value = null
      showForm.value = true
    }

    const editarFornecedor = (fornecedor) => {
      fornecedorEditando.value = fornecedor
      showForm.value = true
    }

    const fecharFormulario = () => {
      showForm.value = false
      fornecedorEditando.value = null
    }

    const salvarFornecedor = async () => {
      try {
        showNotification('success', fornecedorEditando.value
          ? 'Fornecedor atualizado com sucesso!'
          : 'Fornecedor cadastrado com sucesso!'
        )
        await carregarFornecedores()
      } catch (error) {
        console.error('Erro ao salvar fornecedor:', error)
        showNotification('error', 'Erro ao salvar fornecedor')
      }
    }

    const confirmarExclusao = (fornecedor) => {
      fornecedorParaExcluir.value = fornecedor
      showConfirmDelete.value = true
    }

    const cancelarExclusao = () => {
      showConfirmDelete.value = false
      fornecedorParaExcluir.value = null
    }

    const excluirFornecedor = async () => {
      if (!fornecedorParaExcluir.value) return

      try {
        const fornecedor = fornecedorParaExcluir.value

        if (fornecedor.tipo === 'produto') {
          await fornecedorProdutoService.remover(fornecedor.id)
        } else {
          await fornecedorServicoService.remover(fornecedor.id)
        }

        showNotification('success', 'Fornecedor excluído com sucesso!')
        await carregarFornecedores()
      } catch (error) {
        console.error('Erro ao excluir fornecedor:', error)
        showNotification('error', 'Erro ao excluir fornecedor')
      } finally {
        cancelarExclusao()
      }
    }

    const visualizarFornecedor = (fornecedor) => {
      // Implementar modal de visualização ou navegar para página de detalhes
      console.log('Visualizar fornecedor:', fornecedor)
    }

    const filtrarFornecedores = () => {
      // A filtragem é feita automaticamente via computed
    }

    const limparFiltros = () => {
      searchQuery.value = ''
      filtroTipo.value = ''
      filtroStatus.value = ''
    }

    const showNotification = (type, message) => {
      notification.type = type
      notification.message = message
      notification.show = true

      setTimeout(() => {
        hideNotification()
      }, 5000)
    }

    const hideNotification = () => {
      notification.show = false
    }

    // Métodos de formatação e utilidades
    const formatarDocumento = (fornecedor) => {
      if (fornecedor.cnpj) {
        return fornecedorUtils.mascaraCNPJ(fornecedor.cnpj)
      }
      return 'Não informado'
    }

    const formatarTelefone = (telefone) => {
      if (!telefone) return ''
      return fornecedorUtils.mascaraTelefone(telefone)
    }

    const getTipoClass = (tipo) => {
      const classes = {
        produto: 'type-produto',
        servico: 'type-servico'
      }
      return classes[tipo] || 'type-default'
    }

    const getTipoLabel = (tipo) => {
      const labels = {
        produto: 'Produto',
        servico: 'Serviço'
      }
      return labels[tipo] || 'Não definido'
    }

    const getStatusClass = (status) => {
      const classes = {
        ativo: 'status-active',
        inativo: 'status-inactive',
        suspenso: 'status-suspended'
      }
      return classes[status] || 'status-active'
    }

    const getStatusLabel = (status) => {
      const labels = {
        ativo: 'Ativo',
        inativo: 'Inativo',
        suspenso: 'Suspenso'
      }
      return labels[status] || 'Ativo'
    }

    // Lifecycle
    onMounted(() => {
      carregarFornecedores()
    })

    return {
      // Estados
      isLoading,
      showForm,
      showConfirmDelete,
      fornecedorEditando,
      fornecedorParaExcluir,

      // Dados
      fornecedoresProduto,
      fornecedoresServico,

      // Filtros
      searchQuery,
      filtroTipo,
      filtroStatus,

      // Notificações
      notification,

      // Computeds
      todosFornecedores,
      fornecedoresFiltrados,
      totalFornecedores,
      fornecedoresAtivos,
      percentualAtivos,
      novosFornecedoresMes,
      getNotificationIcon,

      // Métodos
      carregarFornecedores,
      abrirFormularioNovo,
      editarFornecedor,
      fecharFormulario,
      salvarFornecedor,
      confirmarExclusao,
      cancelarExclusao,
      excluirFornecedor,
      visualizarFornecedor,
      filtrarFornecedores,
      limparFiltros,
      showNotification,
      hideNotification,
      formatarDocumento,
      formatarTelefone,
      getTipoClass,
      getTipoLabel,
      getStatusClass,
      getStatusLabel
    }
  }
}
</script>

<style scoped>
/* Estilos do layout dashboard */
.dashboard-layout {
  display: grid;
  grid-template-areas:
    "header header"
    "sidebar main";
  grid-template-columns: 260px 1fr;
  grid-template-rows: 70px 1fr;
  height: 100vh;
  background: #f8fafc;
}

.main-content {
  grid-area: main;
  padding: 32px;
  overflow-y: auto;
  background: #f8fafc;
}

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

.action-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #3b82f6;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.action-button:hover {
  background: #2563eb;
  transform: translateY(-1px);
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
  margin-bottom: 32px;
}

.search-container {
  display: flex;
  gap: 16px;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}

.search-input-container {
  flex: 1;
  position: relative;
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

.type-produto {
  background: #dbeafe;
  color: #1d4ed8;
}

.type-servico {
  background: #d1fae5;
  color: #047857;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.contact-email {
  font-size: 0.875rem;
  color: #111827;
}

.contact-phone {
  font-size: 0.75rem;
  color: #6b7280;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.status-active {
  background: #d1fae5;
  color: #047857;
}

.status-inactive {
  background: #fee2e2;
  color: #dc2626;
}

.status-suspended {
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
  text-align: center;
  padding: 64px 32px;
  color: #6b7280;
}

.empty-icon {
  margin-bottom: 16px;
  color: #9ca3af;
}

.empty-state h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 8px 0;
}

.empty-state p {
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
}

/* Notificações */
.notification {
  position: fixed;
  top: 24px;
  right: 24px;
  z-index: 1200;
  padding: 16px 20px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 10px 25px -3px rgba(0, 0, 0, 0.1);
  max-width: 400px;
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }

  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.notification.success {
  background: #d1fae5;
  color: #047857;
  border: 1px solid #a7f3d0;
}

.notification.error {
  background: #fee2e2;
  color: #dc2626;
  border: 1px solid #fca5a5;
}

.notification.warning {
  background: #fef3c7;
  color: #d97706;
  border: 1px solid #fcd34d;
}

.notification-content {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.notification-close {
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  border-radius: 4px;
  opacity: 0.7;
  transition: opacity 0.2s;
}

.notification-close:hover {
  opacity: 1;
}

.text-muted {
  color: #9ca3af;
  font-style: italic;
}

/* Responsividade */
@media (max-width: 1024px) {
  .dashboard-layout {
    grid-template-columns: 1fr;
    grid-template-areas:
      "header"
      "main";
  }

  .main-content {
    padding: 20px;
  }

  .search-container {
    flex-direction: column;
    align-items: stretch;
  }

  .search-actions {
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .welcome-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .welcome-title {
    font-size: 24px;
  }

  .welcome-subtitle {
    font-size: 14px;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
  }

  .table-container {
    font-size: 0.875rem;
  }

  .suppliers-table th,
  .suppliers-table td {
    padding: 12px 8px;
  }

  .notification {
    right: 16px;
    left: 16px;
    max-width: none;
  }
}
</style>
