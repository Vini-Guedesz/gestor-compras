<template>
  <div v-if="isVisible" class="modal-overlay" @click.self="tentarFechar">
    <Transition name="modal" appear>
      <div class="modal-container">
        <div class="modal-header">
          <div class="header-content">
            <h2 class="modal-title">{{ getTituloModal() }}</h2>
            <p class="modal-subtitle">{{ getSubtituloModal() }}</p>
          </div>
          <button @click="tentarFechar" class="close-button">&times;</button>
        </div>

        <WizardProgress
          :currentStep="currentPage"
          :totalSteps="3"
          :stepLabels="['Criar Pedido', 'Aprovar Itens', 'Adicionar Cotações']"
        />

        <div class="modal-body">
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

        <div class="modal-footer">
          <div class="footer-left">
            <span v-if="currentPage === 2 && wizardData.pedido.id" class="pedido-info">
              Pedido #{{ wizardData.pedido.id }}
            </span>
          </div>
          <div class="footer-actions">
            <button
              v-if="currentPage === 1"
              type="button"
              @click="tentarFechar"
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
              ← Voltar
            </button>

            <button
              v-if="currentPage === 1"
              type="button"
              @click="irParaPagina2"
              class="btn-primary"
              :disabled="!page1Valid || isLoading"
            >
              {{ isLoading ? 'Salvando...' : 'Próxima →' }}
            </button>

            <button
              v-if="currentPage === 2"
              type="button"
              @click="finalizarPedido"
              class="btn-success"
            >
              ✓ Finalizar Pedido
            </button>

            <button
              v-if="currentPage === 3"
              type="button"
              @click="salvarCotacoes"
              class="btn-primary"
              :disabled="!page3Valid || isLoading"
            >
              {{ isLoading ? 'Salvando...' : '💾 Salvar Cotações' }}
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <DrawerCotacoes
      :isVisible="showDrawer"
      :item="itemDrawer"
      :cotacoes="cotacoesDrawer"
      :carregando="carregandoDrawer"
      @close="fecharDrawer"
      @view-pdf="visualizarPdf"
    />
  </div>
</template>

<script>
import { ref, watch } from 'vue'
import { useToast } from '@/composables/useToast.js'
import pedidoService from '@/services/pedidoService.js'
import fornecedorService from '@/services/fornecedorService.js'
import cotacaoService from '@/services/cotacaoService.js'
import WizardProgress from './WizardProgressBar.vue'
import PedidoFormPage1 from './StepCriarPedido.vue'
import PedidoFormPage2 from './StepAprovarItens.vue'
import PedidoFormPage3 from './StepAdicionarCotacoes.vue'
import DrawerCotacoes from './CotacoesDrawer.vue'

export default {
  name: 'PedidoForm',
  components: {
    WizardProgress,
    PedidoFormPage1,
    PedidoFormPage2,
    PedidoFormPage3,
    DrawerCotacoes
  },
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    pedido: {
      type: Object,
      default: null
    }
  },
  emits: ['close', 'save'],
  setup(props, { emit }) {
    const { success, error: showError, warning } = useToast()
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
        warning('Por favor, preencha todos os campos obrigatórios.')
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
        showError('Erro ao salvar pedido. Tente novamente.')
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
        warning('Por favor, preencha todas as cotações corretamente.')
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

        success('Cotações salvas com sucesso!')
        await carregarCotacoesDoPedido()
        wizardData.value.cotacoes = []
        wizardData.value.itemAtual = null
        voltarPagina()
      } catch (error) {
        console.error('Erro ao salvar cotações:', error)
        showError('Erro ao salvar cotações. Tente novamente.')
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

      emit('save', wizardData.value.pedido)
      fecharModal()
    }

    const tentarFechar = () => {
      if (hasUnsavedChanges.value) {
        const confirmacao = confirm('Você possui alterações não salvas. Deseja realmente sair?')
        if (!confirmacao) return
      }
      fecharModal()
    }

    const fecharModal = () => {
      resetarWizard()
      emit('close')
    }

    const resetarWizard = () => {
      currentPage.value = 1
      wizardData.value = {
        pedido: {
          id: null,
          objetivo: '',
          status: 'PENDENTE',
          itens: [],
          dataCriacao: null
        },
        itemAtual: null,
        cotacoes: []
      }
      hasUnsavedChanges.value = false
      todasCotacoes.value = []
      fornecedores.value = []
    }

    const carregarDadosPedido = async () => {
      if (props.pedido) {
        wizardData.value.pedido = {
          id: props.pedido.id,
          objetivo: props.pedido.objetivo || '',
          status: props.pedido.status || 'PENDENTE',
          dataCriacao: props.pedido.dataCriacao,
          itens: props.pedido.itens || []
        }

        if (props.pedido.id) {
          currentPage.value = 2
          await carregarCotacoesDoPedido()
        }
      } else {
        resetarWizard()
      }
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
    }

    watch(() => props.isVisible, (newVal) => {
      if (newVal) {
        carregarDadosPedido()
      }
    })

    watch(() => wizardData.value.pedido, () => {
      hasUnsavedChanges.value = true
    }, { deep: true })

    return {
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
      tentarFechar,
      fecharModal,
      abrirDrawerCotacoes,
      fecharDrawer,
      visualizarPdf
    }
  }
}
</script>

<style scoped>
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
  z-index: 1100;
  backdrop-filter: blur(2px);
}

.modal-container {
  background: white;
  border-radius: 16px;
  width: 95%;
  max-width: 1000px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  position: relative;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  border-bottom: 1px solid #e0e6ed;
  background: #1F285F;
  color: white;
  border-radius: 16px 16px 0 0;
}

.header-content {
  flex: 1;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.modal-subtitle {
  font-size: 0.875rem;
  opacity: 0.9;
  margin: 0;
}

.close-button {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  font-size: 1.75rem;
  cursor: pointer;
  color: white;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  transition: all 0.2s;
  margin-left: 16px;
}

.close-button:hover {
  background: rgba(255, 255, 255, 0.3);
}

.modal-body {
  padding: 32px 28px;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.modal-footer {
  padding: 20px 28px;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
  border-radius: 0 0 16px 16px;
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

.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease-out;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

@media (max-width: 768px) {
  .modal-container {
    width: 100%;
    height: 100%;
    max-height: 100vh;
    border-radius: 0;
  }

  .modal-header {
    border-radius: 0;
    padding: 20px;
  }

  .modal-body {
    padding: 20px;
  }

  .modal-footer {
    border-radius: 0;
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
