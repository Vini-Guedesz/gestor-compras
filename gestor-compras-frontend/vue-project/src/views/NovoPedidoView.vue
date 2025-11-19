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
              :stepLabels="['Criar Rascunho', 'Adicionar Cotações', 'Gerar Pedido']"
            />

            <div class="wizard-body">
              <PedidoFormPage1
                v-if="currentPage === 1"
                v-model="wizardData.pedido"
                @validation-change="page1Valid = $event"
              />

              <StepCotacoesRascunho
                v-if="currentPage === 2"
                :rascunho="wizardData.rascunho"
                :cotacoes="todasCotacoes"
                :fornecedores="fornecedores"
                :carregando="carregandoCotacoes"
                @save-cotacao="salvarCotacao"
                @delete-cotacao="deletarCotacao"
              />

              <!-- Step 3: Seleção de itens para pedido final -->
              <div v-if="currentPage === 3" class="step-selecao-itens">
                <div class="info-box">
                  <h4>Selecione os itens para o pedido final</h4>
                  <p>Apenas itens com cotação podem ser selecionados</p>
                </div>

                <div class="itens-lista">
                  <div
                    v-for="item in itensCotados"
                    :key="item.id"
                    class="item-selecao"
                    :class="{ 'selecionado': itensSelecionados.includes(item.id) }"
                    @click="toggleItemSelecionado(item.id)"
                  >
                    <div class="item-checkbox">
                      <input
                        type="checkbox"
                        :checked="itensSelecionados.includes(item.id)"
                        @change.stop="toggleItemSelecionado(item.id)"
                      />
                    </div>
                    <div class="item-info">
                      <span class="item-nome">{{ item.nome }}</span>
                      <span class="item-quantidade">Qtd: {{ item.quantidade }}</span>
                    </div>
                  </div>
                </div>

                <div v-if="itensCotados.length === 0" class="empty-state">
                  <p>Nenhum item com cotação encontrado.</p>
                </div>
              </div>
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
                  @click="irParaPagina3"
                  class="btn-primary"
                  :disabled="!temItensCotados"
                >
                  Selecionar Itens para Pedido
                </button>

                <button
                  v-if="currentPage === 3"
                  type="button"
                  @click="gerarPedidoFinal"
                  class="btn-success"
                  :disabled="itensSelecionados.length === 0 || isLoading"
                >
                  {{ isLoading ? 'Gerando...' : 'Gerar Pedido Final' }}
                </button>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import rascunhoService from '@/services/rascunhoService.js'
import fornecedorService from '@/services/fornecedorService.js'
import cotacaoRascunhoService from '@/services/cotacaoRascunhoService.js'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import WizardProgress from '@/features/pedidos/components/pedido-wizard/WizardProgressBar.vue'
import PedidoFormPage1 from '@/features/pedidos/components/pedido-wizard/StepCriarPedido.vue'
import StepCotacoesRascunho from '@/features/pedidos/components/pedido-wizard/StepAdicionarCotacoesRascunho.vue'

export default {
  name: 'NovoPedidoView',
  components: {
    DashboardHeader,
    DashboardSidebar,
    WizardProgress,
    PedidoFormPage1,
    StepCotacoesRascunho
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
      rascunho: {
        id: null,
        observacao: '',
        itens: [],
        dataCriacao: null
      },
      pedido: {
        id: null,
        observacao: '',
        status: 'PENDENTE',
        itens: [],
        dataCriacao: null
      },
      itemAtual: null,
      cotacoes: []
    })

    const fornecedores = ref([])
    const carregandoFornecedores = ref(false)
    const todasCotacoes = ref([])
    const carregandoCotacoes = ref(false)
    const itensSelecionados = ref([])

    // Computed para verificar se há itens cotados
    const temItensCotados = computed(() => {
      if (todasCotacoes.value.length === 0) return false
      const itensCotados = new Set()
      todasCotacoes.value.forEach(cotacao => {
        if (cotacao.itensRascunhoIds) {
          cotacao.itensRascunhoIds.forEach(id => itensCotados.add(id))
        }
      })
      return itensCotados.size > 0
    })

    // Computed para obter itens cotados
    const itensCotados = computed(() => {
      const idsSet = new Set()
      todasCotacoes.value.forEach(cotacao => {
        if (cotacao.itensRascunhoIds) {
          cotacao.itensRascunhoIds.forEach(id => idsSet.add(id))
        }
      })
      return wizardData.value.rascunho.itens.filter(item => idsSet.has(item.id))
    })

    const getTituloModal = () => {
      if (currentPage.value === 1) {
        return wizardData.value.rascunho.id ? 'Editar Rascunho' : 'Novo Rascunho de Pedido'
      } else if (currentPage.value === 2) {
        return 'Adicionar Cotações aos Itens'
      } else {
        return 'Selecionar Itens para Pedido Final'
      }
    }

    const getSubtituloModal = () => {
      if (currentPage.value === 1) {
        return 'Preencha as informações e adicione os itens necessários'
      } else if (currentPage.value === 2) {
        return 'Adicione cotações de fornecedores para cada item do rascunho'
      } else {
        return 'Selecione os itens cotados que deseja incluir no pedido final'
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

        if (wizardData.value.rascunho.id) {
          // Rascunho já existe - apenas atualizar observação se necessário
          // Os itens já são gerenciados individualmente pelo StepCriarPedido
          const rascunhoAtual = await rascunhoService.obterPorId(wizardData.value.rascunho.id)

          // Atualizar observação se mudou
          if (rascunhoAtual.observacao !== wizardData.value.pedido.observacao) {
            await rascunhoService.atualizar(wizardData.value.rascunho.id, {
              observacao: wizardData.value.pedido.observacao || ''
            })
          }

          // Recarregar rascunho atualizado
          const rascunhoSalvo = await rascunhoService.obterPorId(wizardData.value.rascunho.id)
          wizardData.value.rascunho.itens = rascunhoSalvo.itens
          wizardData.value.pedido.itens = rascunhoSalvo.itens
        } else {
          // Criar novo rascunho com os itens
          const dadosRascunho = {
            observacao: wizardData.value.pedido.observacao || wizardData.value.pedido.objetivo || '',
            itens: wizardData.value.pedido.itens.map(item => ({
              nome: item.nome,
              quantidade: item.quantidade,
              descricao: item.descricao || '',
              observacao: item.observacao || ''
            }))
          }

          const rascunhoSalvo = await rascunhoService.criar(dadosRascunho)
          wizardData.value.rascunho.id = rascunhoSalvo.id
          wizardData.value.rascunho.dataCriacao = rascunhoSalvo.dataCriacao
          wizardData.value.rascunho.itens = rascunhoSalvo.itens
          wizardData.value.pedido.itens = rascunhoSalvo.itens

          // Atualizar URL com o ID do rascunho para persistência
          router.replace({
            path: `/pedidos/novo/${rascunhoSalvo.id}`,
            query: { step: '2' }
          })
        }

        hasUnsavedChanges.value = false
        await carregarCotacoesDoRascunho()
        await carregarFornecedores()
        proximaPagina()
      } catch (error) {
        console.error('Erro ao salvar rascunho:', error)
        alert('Erro ao salvar. Tente novamente.')
      } finally {
        isLoading.value = false
      }
    }

    const irParaPagina3 = () => {
      // Selecionar todos os itens cotados por padrão
      itensSelecionados.value = itensCotados.value.map(item => item.id)

      // Atualizar URL com step 3
      if (wizardData.value.rascunho.id) {
        router.replace({
          path: `/pedidos/novo/${wizardData.value.rascunho.id}`,
          query: { step: '3' }
        })
      }

      proximaPagina()
    }

    const gerarPedidoFinal = async () => {
      if (itensSelecionados.value.length === 0) {
        alert('Selecione pelo menos um item para gerar o pedido.')
        return
      }

      try {
        isLoading.value = true

        // Converter rascunho em pedido com os itens selecionados
        const pedidoCriado = await rascunhoService.converterParaPedido(
          wizardData.value.rascunho.id,
          itensSelecionados.value
        )

        alert(`Pedido #${pedidoCriado.id} criado com sucesso!`)
        router.push('/pedidos')
      } catch (error) {
        console.error('Erro ao gerar pedido:', error)
        alert(error.message || 'Erro ao gerar pedido. Tente novamente.')
      } finally {
        isLoading.value = false
      }
    }

    const carregarCotacoesDoRascunho = async () => {
      if (!wizardData.value.rascunho.id) return

      try {
        carregandoCotacoes.value = true
        const cotacoes = await cotacaoRascunhoService.listarPorRascunho(wizardData.value.rascunho.id)
        todasCotacoes.value = cotacoes
      } catch (error) {
        console.error('Erro ao carregar cotações:', error)
      } finally {
        carregandoCotacoes.value = false
      }
    }

    const salvarCotacao = async (dadosCotacao) => {
      try {
        isLoading.value = true
        await cotacaoRascunhoService.criar(wizardData.value.rascunho.id, dadosCotacao)
        await carregarCotacoesDoRascunho()
      } catch (error) {
        console.error('Erro ao salvar cotação:', error)
        alert(error.message || 'Erro ao salvar cotação. Tente novamente.')
        throw error
      } finally {
        isLoading.value = false
      }
    }

    const deletarCotacao = async (cotacaoId) => {
      try {
        isLoading.value = true
        await cotacaoRascunhoService.deletar(wizardData.value.rascunho.id, cotacaoId)
        await carregarCotacoesDoRascunho()
      } catch (error) {
        console.error('Erro ao deletar cotação:', error)
        alert('Erro ao deletar cotação. Tente novamente.')
      } finally {
        isLoading.value = false
      }
    }

    const toggleItemSelecionado = (itemId) => {
      const index = itensSelecionados.value.indexOf(itemId)
      if (index === -1) {
        itensSelecionados.value.push(itemId)
      } else {
        itensSelecionados.value.splice(index, 1)
      }
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

    const carregarRascunhoExistente = async (rascunhoId, stepInicial = null) => {
      try {
        isLoading.value = true
        console.log('Carregando rascunho existente:', rascunhoId)

        const rascunho = await rascunhoService.obterPorId(rascunhoId)

        wizardData.value.rascunho = {
          id: rascunho.id,
          observacao: rascunho.observacao,
          itens: rascunho.itens || [],
          dataCriacao: rascunho.dataCriacao
        }

        wizardData.value.pedido = {
          id: rascunho.id,
          observacao: rascunho.observacao,
          status: 'RASCUNHO',
          itens: rascunho.itens || [],
          dataCriacao: rascunho.dataCriacao
        }

        console.log('Rascunho carregado:', wizardData.value.rascunho)

        // Se houver step na URL, ir para a etapa correta
        if (stepInicial) {
          const step = parseInt(stepInicial)
          if (step === 2 || step === 3) {
            // Carregar dados necessários para as etapas posteriores
            await carregarFornecedores()
            await carregarCotacoesDoRascunho()
            currentPage.value = step

            // Se for step 3, pré-selecionar itens cotados
            if (step === 3) {
              itensSelecionados.value = itensCotados.value.map(item => item.id)
            }
          }
        }
      } catch (error) {
        console.error('Erro ao carregar rascunho:', error)
        alert('Erro ao carregar rascunho. Redirecionando...')
        router.push('/pedidos')
      } finally {
        isLoading.value = false
      }
    }

    // Carregar rascunho existente se houver ID na rota
    onMounted(() => {
      const rascunhoId = route.params.id
      const stepInicial = route.query.step

      if (rascunhoId) {
        carregarRascunhoExistente(rascunhoId, stepInicial)
      }
    })

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
      fornecedores,
      carregandoFornecedores,
      todasCotacoes,
      carregandoCotacoes,
      itensSelecionados,
      temItensCotados,
      itensCotados,
      getTituloModal,
      getSubtituloModal,
      irParaPagina,
      voltarPagina,
      proximaPagina,
      irParaPagina2,
      irParaPagina3,
      gerarPedidoFinal,
      salvarCotacao,
      deletarCotacao,
      toggleItemSelecionado,
      cancelar,
      voltar
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

/* Step 3 - Seleção de Itens */
.step-selecao-itens {
  padding: 0;
}

.step-selecao-itens .info-box {
  background: #f0f4ff;
  border: 1px solid #c7d2fe;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.step-selecao-itens .info-box h4 {
  margin: 0 0 4px 0;
  color: #1F285F;
  font-size: 1rem;
}

.step-selecao-itens .info-box p {
  margin: 0;
  color: #6b7280;
  font-size: 0.875rem;
}

.itens-lista {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-selecao {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f9fafb;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.item-selecao:hover {
  background: #f3f4f6;
  border-color: #d1d5db;
}

.item-selecao.selecionado {
  background: #e8f5e9;
  border-color: #10b981;
}

.item-checkbox input {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.item-nome {
  font-weight: 500;
  color: #374151;
}

.item-quantidade {
  font-size: 0.75rem;
  color: #6b7280;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #6b7280;
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
