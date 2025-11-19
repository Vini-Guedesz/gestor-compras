<template>
  <div class="page-container">
    <DashboardHeader @toggle-sidebar="toggleSidebar" />

    <div class="main-content">
      <DashboardSidebar :isOpen="isSidebarOpen" @close="closeSidebar" />

      <div class="content-area">
        <!-- Breadcrumb -->
        <div class="breadcrumb">
          <button @click="voltar" class="btn-voltar">
            <svg viewBox="0 0 24 24" width="18" height="18">
              <path fill="currentColor" d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"/>
            </svg>
            Voltar
          </button>
          <span class="breadcrumb-separator">|</span>
          <router-link to="/pedidos" class="breadcrumb-link">
            Pedidos de Compra
          </router-link>
          <span class="breadcrumb-separator">/</span>
          <span class="breadcrumb-current">Novo Pedido</span>
        </div>

        <!-- Conteúdo do Wizard -->
        <div class="wizard-container">
          <div class="wizard-card">
            <div class="wizard-header">
              <div class="header-content">
                <h2 class="wizard-title">{{ getTituloModal() }}</h2>
                <p class="wizard-subtitle">{{ getSubtituloModal() }}</p>
              </div>
            </div>

            <WizardProgress
              :currentStep="currentPage"
              :totalSteps="3"
              :stepLabels="['Criar Pedido', 'Aprovar Itens', 'Adicionar Cotações']"
            />

            <div class="wizard-body">
              <PedidoFormPage1
                v-if="currentPage === 1"
                v-model="wizardData.pedido"
                @validation-change="page1Valid = $event"
              />

              <PedidoFormPage2
                v-if="currentPage === 2"
                :pedido="wizardData.pedido"
                :cotacoes="todasCotacoes"
                :carregando="carregandoCotacoes"
                @add-cotacao="abrirPaginaCotacoes"
                @view-cotacoes="abrirDrawerCotacoes"
              />

              <PedidoFormPage3
                v-if="currentPage === 3"
                :pedido="wizardData.pedido"
                :item="wizardData.itemAtual"
                :fornecedores="fornecedores"
                :carregandoFornecedores="carregandoFornecedores"
                v-model="wizardData.cotacoes"
                @validation-change="page3Valid = $event"
              />
            </div>

            <div class="wizard-footer">
              <div class="footer-left">
                <span v-if="currentPage === 2 && wizardData.pedido.id" class="pedido-info">
                  Pedido #{{ wizardData.pedido.id }}
                </span>
              </div>
              <div class="footer-actions">
                <button
                  v-if="currentPage === 1"
                  type="button"
                  @click="cancelar"
                  class="btn-secondary"
                >
                  Cancelar
                </button>

                <button
                  v-if="currentPage > 1"
                  type="button"
                  @click="voltarPagina"
                  class="btn-secondary"
                >
                  Voltar
                </button>

                <button
                  v-if="currentPage === 1"
                  type="button"
                  @click="irParaPagina2"
                  class="btn-primary"
                  :disabled="!page1Valid || isLoading"
                >
                  {{ isLoading ? 'Salvando...' : 'Próxima' }}
                </button>

                <button
                  v-if="currentPage === 2"
                  type="button"
                  @click="finalizarPedido"
                  class="btn-success"
                >
                  Finalizar Pedido
                </button>

                <button
                  v-if="currentPage === 3"
                  type="button"
                  @click="salvarCotacoes"
                  class="btn-primary"
                  :disabled="!page3Valid || isLoading"
                >
                  {{ isLoading ? 'Salvando...' : 'Salvar Cotações' }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <DrawerCotacoes
          :isVisible="showDrawer"
          :item="itemDrawer"
          :cotacoes="cotacoesDrawer"
          :carregando="carregandoDrawer"
          @close="fecharDrawer"
          @view-pdf="visualizarPdf"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import pedidoService from '@/services/pedidoService.js'
import fornecedorService from '@/services/fornecedorService.js'
import cotacaoService from '@/services/cotacaoService.js'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import WizardProgress from '@/features/pedidos/components/pedido-wizard/WizardProgressBar.vue'
import PedidoFormPage1 from '@/features/pedidos/components/pedido-wizard/StepCriarPedido.vue'
import PedidoFormPage2 from '@/features/pedidos/components/pedido-wizard/StepAprovarItens.vue'
import PedidoFormPage3 from '@/features/pedidos/components/pedido-wizard/StepAdicionarCotacoes.vue'
import DrawerCotacoes from '@/features/pedidos/components/pedido-wizard/CotacoesDrawer.vue'

export default {
  name: 'NovoPedidoView',
  components: {
    DashboardHeader,
    DashboardSidebar,
    WizardProgress,
    PedidoFormPage1,
    PedidoFormPage2,
    PedidoFormPage3,
    DrawerCotacoes
  },
  setup() {
    const router = useRouter()
    const route = useRoute()

    // Sidebar
    const isSidebarOpen = ref(false)
    const toggleSidebar = () => { isSidebarOpen.value = !isSidebarOpen.value }
    const closeSidebar = () => { isSidebarOpen.value = false }

    // Wizard state
    const currentPage = ref(1)
    const isLoading = ref(false)
    const hasUnsavedChanges = ref(false)
    const page1Valid = ref(false)
    const page3Valid = ref(false)

    const wizardData = ref({
      pedido: {
        id: null,
        objetivo: '',
        status: 'PENDENTE',
        itens: [],
        dataCriacao: null
      },
      itemAtual: null,
      cotacoes: []
    })

    const showDrawer = ref(false)
    const itemDrawer = ref(null)
    const cotacoesDrawer = ref([])
    const carregandoDrawer = ref(false)
    const fornecedores = ref([])
    const carregandoFornecedores = ref(false)
    const todasCotacoes = ref([])
    const carregandoCotacoes = ref(false)

    const getTituloModal = () => {
      if (currentPage.value === 1) {
        return wizardData.value.pedido.id ? 'Editar Pedido' : 'Novo Pedido de Compra'
      } else if (currentPage.value === 2) {
        return 'Aprovar Itens e Gerenciar Cotações'
      } else {
        return 'Adicionar Cotações'
      }
    }

    const getSubtituloModal = () => {
      if (currentPage.value === 1) {
        return 'Preencha as informações do pedido e adicione os itens necessários'
      } else if (currentPage.value === 2) {
        return 'Revise os itens e adicione cotações de fornecedores'
      } else {
        return `Adicione cotações para: ${wizardData.value.itemAtual?.nome || ''}`
      }
    }

    const irParaPagina = (page) => {
      currentPage.value = page
    }

    const voltarPagina = () => {
      if (currentPage.value === 3) {
        wizardData.value.cotacoes = []
        wizardData.value.itemAtual = null
      }
      if (currentPage.value > 1) {
        currentPage.value--
      }
    }

    const proximaPagina = () => {
      if (currentPage.value < 3) {
        currentPage.value++
      }
    }

    const irParaPagina2 = async () => {
      if (!page1Valid.value) {
        alert('Por favor, preencha todos os campos obrigatórios.')
        return
      }

      try {
        isLoading.value = true
        const dadosParaSalvar = {
          id: wizardData.value.pedido.id,
          objetivo: wizardData.value.pedido.objetivo,
          status: wizardData.value.pedido.status,
          dataCriacao: wizardData.value.pedido.dataCriacao,
          itens: wizardData.value.pedido.itens.map(item => ({
            id: item.id || null,
            nome: item.nome,
            quantidade: item.quantidade,
            descricao: item.descricao || '',
            observacao: item.observacao || ''
          }))
        }

        const pedidoSalvo = await pedidoService.salvar(dadosParaSalvar)
        wizardData.value.pedido.id = pedidoSalvo.id
        wizardData.value.pedido.dataCriacao = pedidoSalvo.dataCriacao

        if (pedidoSalvo.itens && pedidoSalvo.itens.length > 0) {
          wizardData.value.pedido.itens = pedidoSalvo.itens
        }

        hasUnsavedChanges.value = false
        await carregarCotacoesDoPedido()
        proximaPagina()
      } catch (error) {
        console.error('Erro ao salvar pedido:', error)
        alert('Erro ao salvar pedido. Tente novamente.')
      } finally {
        isLoading.value = false
      }
    }

    const abrirPaginaCotacoes = (item) => {
      wizardData.value.itemAtual = item
      wizardData.value.cotacoes = []
      carregarFornecedores()
      irParaPagina(3)
    }

    const salvarCotacoes = async () => {
      if (!page3Valid.value) {
        alert('Por favor, preencha todas as cotações corretamente.')
        return
      }

      try {
        isLoading.value = true
        for (const cotacao of wizardData.value.cotacoes) {
          const dadosCotacao = {
            fornecedorId: cotacao.fornecedorId,
            itemPedidoId: wizardData.value.itemAtual.id,
            tipo: cotacao.tipo,
            valorUnitario: parseFloat(cotacao.valorUnitario),
            prazoEntrega: parseInt(cotacao.prazoEntrega),
            validadeCotacao: cotacao.validadeCotacao,
            arquivo: cotacao.arquivo
          }
          await cotacaoService.salvar(dadosCotacao)
        }

        alert('Cotações salvas com sucesso!')
        await carregarCotacoesDoPedido()
        wizardData.value.cotacoes = []
        wizardData.value.itemAtual = null
        voltarPagina()
      } catch (error) {
        console.error('Erro ao salvar cotações:', error)
        alert('Erro ao salvar cotações. Tente novamente.')
      } finally {
        isLoading.value = false
      }
    }

    const finalizarPedido = () => {
      const itensSemCotacao = wizardData.value.pedido.itens.filter(item => {
        const cotacoesDoItem = todasCotacoes.value.filter(c => c.itemPedidoId === item.id)
        return cotacoesDoItem.length === 0
      })

      if (itensSemCotacao.length > 0) {
        const confirmacao = confirm(
          `${itensSemCotacao.length} item(ns) ainda não possui(em) cotações. Deseja finalizar mesmo assim?`
        )
        if (!confirmacao) return
      }

      router.push('/pedidos')
    }

    const cancelar = () => {
      if (hasUnsavedChanges.value) {
        const confirmacao = confirm('Você possui alterações não salvas. Deseja realmente sair?')
        if (!confirmacao) return
      }
      router.push('/pedidos')
    }

    const voltar = () => {
      if (hasUnsavedChanges.value) {
        const confirmacao = confirm('Você possui alterações não salvas. Deseja realmente sair?')
        if (!confirmacao) return
      }
      router.push('/pedidos')
    }

    const carregarFornecedores = async () => {
      try {
        carregandoFornecedores.value = true
        const [produtos, servicos] = await Promise.all([
          fornecedorService.listarFornecedoresProduto(),
          fornecedorService.listarFornecedoresServico()
        ])
        fornecedores.value = [...produtos, ...servicos]
      } catch (error) {
        console.error('Erro ao carregar fornecedores:', error)
      } finally {
        carregandoFornecedores.value = false
      }
    }

    const carregarCotacoesDoPedido = async () => {
      if (!wizardData.value.pedido.id) return

      try {
        carregandoCotacoes.value = true
        const cotacoes = await cotacaoService.listarPorPedido(wizardData.value.pedido.id)
        todasCotacoes.value = cotacoes
      } catch (error) {
        console.error('Erro ao carregar cotações:', error)
      } finally {
        carregandoCotacoes.value = false
      }
    }

    const abrirDrawerCotacoes = async (item) => {
      itemDrawer.value = item
      carregandoDrawer.value = true
      showDrawer.value = true

      try {
        const cotacoes = await cotacaoService.listarPorItem(item.id)
        cotacoesDrawer.value = cotacoes
      } catch (error) {
        console.error('Erro ao carregar cotações do item:', error)
        cotacoesDrawer.value = []
      } finally {
        carregandoDrawer.value = false
      }
    }

    const fecharDrawer = () => {
      showDrawer.value = false
      itemDrawer.value = null
      cotacoesDrawer.value = []
    }

    const visualizarPdf = (cotacao) => {
      console.log('Visualizar PDF da cotação:', cotacao)
    }

    return {
      // Sidebar
      isSidebarOpen,
      toggleSidebar,
      closeSidebar,

      // Wizard
      currentPage,
      isLoading,
      hasUnsavedChanges,
      page1Valid,
      page3Valid,
      wizardData,
      showDrawer,
      itemDrawer,
      cotacoesDrawer,
      carregandoDrawer,
      fornecedores,
      carregandoFornecedores,
      todasCotacoes,
      carregandoCotacoes,
      getTituloModal,
      getSubtituloModal,
      irParaPagina,
      voltarPagina,
      proximaPagina,
      irParaPagina2,
      abrirPaginaCotacoes,
      salvarCotacoes,
      finalizarPedido,
      cancelar,
      voltar,
      abrirDrawerCotacoes,
      fecharDrawer,
      visualizarPdf
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.main-content {
  display: flex;
  padding-top: 64px;
}

.content-area {
  flex: 1;
  padding: 24px;
  margin-left: 250px;
}

/* Breadcrumb */
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  font-size: 0.875rem;
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
  transition: all 0.2s;
}

.btn-voltar:hover {
  background: #e5e7eb;
  border-color: #9ca3af;
}

.breadcrumb-link {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #1F285F;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.breadcrumb-link:hover {
  color: #2d3a7f;
}

.breadcrumb-separator {
  color: #d1d5db;
}

.breadcrumb-current {
  color: #6b7280;
}

/* Wizard Container */
.wizard-container {
  max-width: 1000px;
}

.wizard-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.wizard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  border-bottom: 1px solid #e0e6ed;
  background: #1F285F;
  color: white;
}

.header-content {
  flex: 1;
}

.wizard-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.wizard-subtitle {
  font-size: 0.875rem;
  opacity: 0.9;
  margin: 0;
}

.wizard-body {
  padding: 32px 28px;
  min-height: 400px;
}

.wizard-footer {
  padding: 20px 28px;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pedido-info {
  background: #e8eaf6;
  color: #1F285F;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 500;
}

.footer-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

/* Buttons */
.btn-secondary {
  padding: 10px 20px;
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: #e5e7eb;
  border-color: #9ca3af;
}

.btn-primary {
  padding: 10px 20px;
  background: #1F285F;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
  box-shadow: 0 4px 6px -1px rgba(31, 40, 95, 0.3);
}

.btn-primary:hover:not(:disabled) {
  background: #2d3a7f;
  transform: translateY(-1px);
  box-shadow: 0 6px 8px -1px rgba(31, 40, 95, 0.4);
}

.btn-primary:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  opacity: 0.6;
  box-shadow: none;
}

.btn-success {
  padding: 10px 20px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
  box-shadow: 0 4px 6px -1px rgba(16, 185, 129, 0.3);
}

.btn-success:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 8px -1px rgba(16, 185, 129, 0.4);
}

/* Responsive */
@media (max-width: 1024px) {
  .content-area {
    margin-left: 0;
  }
}

@media (max-width: 768px) {
  .content-area {
    padding: 16px;
  }

  .wizard-header {
    padding: 20px;
  }

  .wizard-body {
    padding: 20px;
  }

  .wizard-footer {
    padding: 16px;
  }

  .footer-actions {
    width: 100%;
    justify-content: stretch;
  }

  .footer-actions button {
    flex: 1;
  }
}
</style>
