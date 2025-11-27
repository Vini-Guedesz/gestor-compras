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
        </div>

        <!-- Conteúdo da Página -->
        <div class="wizard-container">
          <div class="wizard-card">
            <div class="wizard-header">
              <div class="header-content">
                <h2 class="wizard-title">{{ getTitulo() }}</h2>
                <p class="wizard-subtitle">{{ getSubtitulo() }}</p>
              </div>
            </div>

            <div class="wizard-body">
              <!-- Estado de Edição do Rascunho -->
              <div v-if="editState === 'EDITANDO_RASCUNHO'">
                <PedidoFormPage1
                  v-model="wizardData.rascunho"
                  @validation-change="page1Valid = $event"
                  @rascunho-created="onRascunhoCreated"
                />
              </div>

              <!-- Estado de Gerenciamento de Cotações -->
              <div v-if="editState === 'GERENCIANDO_COTACOES'">
                <StepCotacoesRascunho
                  :rascunho="wizardData.rascunho"
                  :cotacoes="todasCotacoes"
                  :fornecedores="fornecedores"
                  :carregando="carregandoCotacoes"
                  @save-cotacao="salvarCotacao"
                  @delete-cotacao="deletarCotacao"
                />

                <div class="step-selecao-cotacoes">
                  <div class="info-box">
                    <h4>📋 Selecione as cotações e itens para o pedido final</h4>
                    <p>Primeiro marque as cotações desejadas, depois selecione os itens de cada uma</p>
                  </div>

                  <!-- Lista de Cotações -->
                  <div v-if="todasCotacoes.length > 0" class="cotacoes-lista-selecao">
                    <div
                      v-for="cotacao in todasCotacoes"
                      :key="`cotacao-${cotacao.id}-${cotacao.fornecedorId}-${cotacao.tipoFornecedor}`"
                      class="cotacao-selecao-card"
                      :class="{ 'cotacao-selecionada': isCotacaoSelecionada(cotacao.id) }"
                    >
                      <!-- Header da Cotação -->
                      <div class="cotacao-selecao-header" @click="toggleCotacaoSelecionada(cotacao.id)">
                        <div class="cotacao-checkbox">
                          <input
                            type="checkbox"
                            :checked="isCotacaoSelecionada(cotacao.id)"
                            @click.stop="toggleCotacaoSelecionada(cotacao.id)"
                          />
                        </div>
                        <div class="cotacao-info-principal">
                          <div class="cotacao-fornecedor-nome">
                            <strong>{{ cotacao.nomeFornecedor || 'Fornecedor' }}</strong>
                            <span class="cotacao-badge">{{ cotacao.tipoFornecedor }}</span>
                            <!-- Badge de aviso se cotação selecionada sem itens -->
                            <span
                              v-if="isCotacaoSelecionada(cotacao.id) && !temItensSelecionados(cotacao.id)"
                              class="badge-warning"
                            >
                              ⚠️ Nenhum item selecionado
                            </span>
                          </div>
                          <div class="cotacao-detalhes">
                            <span class="cotacao-preco">R$ {{ formatarPreco(cotacao.preco) }}</span>
                            <span v-if="cotacao.prazoEmDiasUteis" class="cotacao-prazo">
                              {{ cotacao.prazoEmDiasUteis }} dias úteis
                            </span>
                          </div>
                        </div>
                      </div>

                      <!-- Lista de Itens (só aparece se cotação selecionada) -->
                      <div v-if="isCotacaoSelecionada(cotacao.id)" class="cotacao-itens-lista">
                        <div class="itens-header">
                          <span>Selecione os itens desta cotação:</span>
                        </div>
                        <div
                          v-for="itemId in cotacao.itensRascunhoIds"
                          :key="`item-${cotacao.id}-${itemId}`"
                          class="item-selecao"
                          :class="{ 'item-selecionado': isItemSelecionadoNaCotacao(cotacao.id, itemId) }"
                          @click="handleItemClick(cotacao.id, itemId)"
                        >
                          <div class="item-checkbox" @click.stop>
                            <input
                              type="checkbox"
                              :id="`checkbox-${cotacao.id}-${itemId}`"
                              :checked="isItemSelecionadoNaCotacao(cotacao.id, itemId)"
                              @change="handleItemCheckboxChange($event, cotacao.id, itemId)"
                            />
                          </div>
                          <div class="item-info">
                            <span class="item-nome">{{ getNomeItem(itemId) }}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- Empty State -->
                  <div v-else class="empty-state">
                    <p>Nenhuma cotação encontrada. Adicione cotações antes de gerar o pedido.</p>
                  </div>

                  <!-- Resumo da Seleção -->
                  <div v-if="cotacoesSelecionadas.length > 0" class="resumo-selecao">
                    <div class="resumo-item">
                      <span class="resumo-label">Cotações selecionadas:</span>
                      <span class="resumo-valor">{{ cotacoesSelecionadas.length }}</span>
                    </div>
                    <div class="resumo-item">
                      <span class="resumo-label">Itens selecionados:</span>
                      <span class="resumo-valor">{{ totalItensSelecionados }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="wizard-footer">
              <div class="footer-actions">
                <!-- Ações para o estado EDITANDO_RASCUNHO -->
                <template v-if="editState === 'EDITANDO_RASCUNHO'">
                  <button type="button" @click="cancelar" class="btn-secondary">
                    Cancelar
                  </button>
                  <button
                    type="button"
                    @click="finalizarRascunho"
                    class="btn-primary"
                    :disabled="!page1Valid || isLoading"
                  >
                    {{ isLoading ? 'Salvando...' : 'Finalizar Rascunho' }}
                  </button>
                </template>

                <!-- Ações para o estado GERENCIANDO_COTACOES -->
                <template v-if="editState === 'GERENCIANDO_COTACOES'">
                  <button type="button" @click="editarRascunho" class="btn-secondary">
                    Editar Rascunho
                  </button>
                  <button
                    type="button"
                    @click="gerarPedidoFinal"
                    class="btn-success"
                    :disabled="totalItensSelecionados === 0 || isLoading"
                  >
                    {{ isLoading ? 'Gerando...' : 'Gerar Pedido Final' }}
                  </button>
                </template>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import rascunhoService from '@/services/rascunhoService.js'
import fornecedorService from '@/services/fornecedorService.js'
import cotacaoRascunhoService from '@/services/cotacaoRascunhoService.js'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import PedidoFormPage1 from '@/features/pedidos/components/pedido-wizard/StepCriarPedido.vue'
import StepCotacoesRascunho from '@/features/pedidos/components/pedido-wizard/StepAdicionarCotacoesRascunho.vue'

export default {
  name: 'NovoPedidoView',
  components: {
    DashboardHeader,
    DashboardSidebar,
    PedidoFormPage1,
    StepCotacoesRascunho
  },
  setup() {
    const router = useRouter()
    const route = useRoute()

    // State
    const isSidebarOpen = ref(false)
    const editState = ref('EDITANDO_RASCUNHO') // 'EDITANDO_RASCUNHO' | 'GERENCIANDO_COTACOES'
    const isLoading = ref(false)
    const page1Valid = ref(false)

    const wizardData = ref({
      rascunho: { id: null, observacao: '', itens: [], dataCriacao: null }
    })

    const fornecedores = ref([])
    const carregandoFornecedores = ref(false)
    const todasCotacoes = ref([])
    const carregandoCotacoes = ref(false)
    const cotacoesSelecionadas = ref([]) // IDs das cotações selecionadas
    const itensPorCotacao = ref({}) // { cotacaoId: [itemId1, itemId2, ...] }

    // Computed properties
    const temCotacoes = computed(() => todasCotacoes.value.length > 0)

    const totalItensSelecionados = computed(() => {
      let total = 0
      Object.values(itensPorCotacao.value).forEach(itens => {
        total += itens.length
      })
      return total
    })

    const getTitulo = () => {
      if (editState.value === 'EDITANDO_RASCUNHO') {
        return wizardData.value.rascunho.id ? 'Editar Rascunho' : 'Novo Rascunho de Pedido'
      }
      return 'Gerenciar Cotações e Gerar Pedido'
    }

    const getSubtitulo = () => {
      if (editState.value === 'EDITANDO_RASCUNHO') {
        return 'Adicione ou modifique os itens do seu rascunho'
      }
      return 'Adicione cotações e selecione os itens para o pedido final'
    }

    // Methods
    const toggleSidebar = () => { isSidebarOpen.value = !isSidebarOpen.value }
    const closeSidebar = () => { isSidebarOpen.value = false }

    const onRascunhoCreated = (rascunhoCriado) => {
      wizardData.value.rascunho = { ...rascunhoCriado, itens: rascunhoCriado.itens || [] }
      router.replace({ path: `/pedidos/novo/${rascunhoCriado.id}` })
    }

    const finalizarRascunho = async () => {
      if (!page1Valid.value) {
        alert('Por favor, preencha todos os campos obrigatórios e adicione ao menos um item.')
        return
      }

      if (!wizardData.value.rascunho.id) {
        alert('Por favor, salve pelo menos um item antes de finalizar o rascunho.');
        return;
      }

      // Confirmar antes de finalizar
      const totalItens = wizardData.value.rascunho.itens?.length || 0
      const confirmado = confirm(
        `Deseja finalizar o rascunho e prosseguir para adicionar cotações?\n\n` +
        `Total de itens: ${totalItens}\n\n` +
        `Após finalizar, o rascunho terá o status "Em Cotação".`
      )
      if (!confirmado) return

      try {
        isLoading.value = true

        // Atualizar status para EM_COTACAO
        await rascunhoService.atualizarStatus(wizardData.value.rascunho.id, 'EM_COTACAO')

        const rascunhoSalvo = await rascunhoService.obterPorId(wizardData.value.rascunho.id);
        wizardData.value.rascunho = rascunhoSalvo

        await carregarDadosParaCotacao()
        editState.value = 'GERENCIANDO_COTACOES'
        router.replace({ query: { state: 'quotes' } })
      } catch (error) {
        console.error('Erro ao finalizar rascunho:', error)
        const mensagem = error.message || 'Erro ao salvar. Tente novamente.'
        alert(`Erro ao finalizar rascunho:\n\n${mensagem}`)
      } finally {
        isLoading.value = false
      }
    }

    const editarRascunho = async () => {
        try {
            isLoading.value = true
            // Recarregar dados do rascunho para garantir sincronização
            if (wizardData.value.rascunho.id) {
                const rascunho = await rascunhoService.obterPorId(wizardData.value.rascunho.id)
                wizardData.value.rascunho = { ...rascunho, itens: rascunho.itens || [] }
            }
            editState.value = 'EDITANDO_RASCUNHO'
            router.replace({ query: { state: 'edit' } })
        } catch (error) {
            console.error('Erro ao carregar rascunho para edição:', error)
            alert('Erro ao carregar rascunho para edição. Tente novamente.')
        } finally {
            isLoading.value = false
        }
    }

    const gerarPedidoFinal = async () => {
      // Validar seleção de cotações
      if (cotacoesSelecionadas.value.length === 0) {
        alert('Selecione pelo menos uma cotação para gerar o pedido.')
        return
      }

      // Validar seleção de itens
      if (totalItensSelecionados.value === 0) {
        alert('Selecione pelo menos um item nas cotações para gerar o pedido.')
        return
      }

      // 🔥 NOVA VALIDAÇÃO: Verificar se todas as cotações selecionadas têm itens
      const cotacoesSemItens = cotacoesSelecionadas.value.filter(cotacaoId => {
        const itens = itensPorCotacao.value[cotacaoId]
        return !itens || itens.length === 0
      })

      if (cotacoesSemItens.length > 0) {
        const nomeCotacoes = cotacoesSemItens.map(id => {
          const cotacao = todasCotacoes.value.find(c => c.id === id)
          return cotacao ? `${cotacao.nomeFornecedor}` : `Cotação ${id}`
        }).join(', ')

        alert(
          `As seguintes cotações estão marcadas mas não têm itens selecionados:\n\n` +
          `${nomeCotacoes}\n\n` +
          `Por favor, selecione pelo menos um item em cada cotação ou desmarque as cotações vazias.`
        )
        return
      }

      // Verificar se o usuário está autenticado
      const token = localStorage.getItem('authToken')
      if (!token) {
        alert('Sua sessão expirou. Por favor, faça login novamente.')
        router.push('/login')
        return
      }

      try {
        isLoading.value = true

        // Preparar dados para envio: extrair todos os itens selecionados
        const todosItensSelecionados = []
        Object.values(itensPorCotacao.value).forEach(itens => {
          todosItensSelecionados.push(...itens)
        })

        // Remover duplicatas (caso um item esteja em múltiplas cotações)
        const itensUnicos = [...new Set(todosItensSelecionados)]

        const pedidoCriado = await rascunhoService.converterParaPedido(
          wizardData.value.rascunho.id,
          itensUnicos
        )
        alert(`Pedido #${pedidoCriado.id} criado com sucesso!`)
        router.push('/pedidos')
      } catch (error) {
        console.error('Erro ao gerar pedido:', error)
        // Verificar se é erro de autenticação
        if (error.message && (error.message.includes('401') || error.message.includes('não autenticado') || error.message.includes('Sessão expirada'))) {
          alert('Sua sessão expirou. Por favor, faça login novamente.')
          router.push('/login')
          return
        }
        alert(error.message || 'Erro ao gerar pedido. Tente novamente.')
      } finally {
        isLoading.value = false
      }
    }

    const carregarDadosParaCotacao = async () => {
        await carregarFornecedores();
        await carregarCotacoesDoRascunho();
    }

    const carregarCotacoesDoRascunho = async () => {
      if (!wizardData.value.rascunho.id) return
      try {
        carregandoCotacoes.value = true

        // Limpar array completamente para forçar re-renderização do Vue
        todasCotacoes.value = []

        // Aguardar o Vue processar a limpeza
        await nextTick()

        // Buscar novas cotações do backend
        const cotacoes = await cotacaoRascunhoService.listarPorRascunho(wizardData.value.rascunho.id)

        // Criar novo array com objetos completamente novos para garantir reatividade
        todasCotacoes.value = cotacoes.map(c => ({ ...c }))

        // Aguardar o Vue processar a atualização
        await nextTick()
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

        // Pequeno delay para garantir que o backend persistiu completamente
        await new Promise(resolve => setTimeout(resolve, 100))

        await carregarCotacoesDoRascunho()
      } catch (error) {
        console.error('Erro ao salvar cotação:', error)
        const mensagem = error.message || 'Erro ao salvar cotação. Tente novamente.'
        alert(`Erro ao salvar cotação:\n\n${mensagem}`)
        throw error
      } finally {
        isLoading.value = false
      }
    }

    const deletarCotacao = async (cotacaoId) => {
      try {
        isLoading.value = true
        await cotacaoRascunhoService.deletar(wizardData.value.rascunho.id, cotacaoId)

        // Pequeno delay para garantir que o backend processou a exclusão
        await new Promise(resolve => setTimeout(resolve, 100))

        await carregarCotacoesDoRascunho()
      } catch (error) {
        console.error('Erro ao deletar cotação:', error)
        alert('Erro ao deletar cotação. Tente novamente.')
      } finally {
        isLoading.value = false
      }
    }

    const toggleCotacaoSelecionada = (cotacaoId) => {
      const index = cotacoesSelecionadas.value.indexOf(cotacaoId)
      if (index === -1) {
        // Selecionar cotação
        cotacoesSelecionadas.value.push(cotacaoId)
        // Inicializar array de itens vazio para esta cotação
        if (!itensPorCotacao.value[cotacaoId]) {
          itensPorCotacao.value[cotacaoId] = []
        }
      } else {
        // Desselecionar cotação e remover itens
        cotacoesSelecionadas.value.splice(index, 1)
        delete itensPorCotacao.value[cotacaoId]
      }
    }

    const handleItemCheckboxChange = (event, cotacaoId, itemId) => {
      // Garantir que a cotação está selecionada
      if (!cotacoesSelecionadas.value.includes(cotacaoId)) {
        // Reverter checkbox
        if (event && event.target) {
          event.target.checked = false
        }
        return
      }

      // Garantir que existe array para esta cotação
      if (!itensPorCotacao.value[cotacaoId]) {
        itensPorCotacao.value[cotacaoId] = []
      }

      const itens = itensPorCotacao.value[cotacaoId]
      const index = itens.indexOf(itemId)

      if (index === -1) {
        // VERIFICAR se item já está em outra cotação
        for (const [outraCotacaoId, outrosItens] of Object.entries(itensPorCotacao.value)) {
          if (Number(outraCotacaoId) !== Number(cotacaoId) && outrosItens.includes(itemId)) {
            // Reverter o checkbox ANTES de mostrar o alerta
            if (event && event.target) {
              event.target.checked = false
            }
            // Usar nextTick para garantir que a reversão seja aplicada
            nextTick(() => {
              alert('Este item já está selecionado em outra cotação. Um item só pode estar em uma cotação por vez.')
            })
            return
          }
        }
        // Adicionar item
        itens.push(itemId)
      } else {
        // Remover item
        itens.splice(index, 1)
      }
    }

    const handleItemClick = (cotacaoId, itemId) => {
      // Quando clica no card (não no checkbox), chama a função diretamente
      handleItemCheckboxChange(null, cotacaoId, itemId)
    }

    const toggleItemNaCotacao = (cotacaoId, itemId) => {
      // Manter para compatibilidade
      handleItemCheckboxChange(null, cotacaoId, itemId)
    }

    const isCotacaoSelecionada = (cotacaoId) => {
      return cotacoesSelecionadas.value.includes(cotacaoId)
    }

    const isItemSelecionadoNaCotacao = (cotacaoId, itemId) => {
      return itensPorCotacao.value[cotacaoId]?.includes(itemId) || false
    }

    const temItensSelecionados = (cotacaoId) => {
      const itens = itensPorCotacao.value[cotacaoId]
      return itens && itens.length > 0
    }

    const getNomeItem = (itemId) => {
      const item = wizardData.value.rascunho.itens.find(i => i.id === itemId)
      return item ? item.nome : `Item #${itemId}`
    }

    const formatarPreco = (preco) => {
      if (!preco && preco !== 0) return '0,00'
      return Number(preco).toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }

    const cancelar = () => {
      // Verificar se há seleções feitas
      const temSelecoes = cotacoesSelecionadas.value.length > 0 || totalItensSelecionados.value > 0

      if (temSelecoes) {
        const confirmacao = confirm(
          'Você tem seleções não salvas. Tem certeza que deseja cancelar?\n\n' +
          'Todas as seleções serão perdidas.'
        )
        if (!confirmacao) return
      }

      router.push('/pedidos')
    }

    const voltar = () => {
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

    const carregarRascunhoExistente = async (rascunhoId) => {
      try {
        isLoading.value = true
        const rascunho = await rascunhoService.obterPorId(rascunhoId);
        console.log('Rascunho carregado:', rascunho)

        wizardData.value.rascunho = {
          ...rascunho,
          itens: rascunho.itens || []
        }
      } catch (error) {
        console.error('Erro ao carregar rascunho:', error)
        alert('Erro ao carregar rascunho. Redirecionando...')
        router.push('/pedidos')
      } finally {
        isLoading.value = false
      }
    }

    onMounted(async () => {
      const rascunhoId = route.params.id
      const initialState = route.query.state;

      if (rascunhoId) {
        await carregarRascunhoExistente(rascunhoId)
        if (initialState === 'quotes') {
            await carregarDadosParaCotacao();
            editState.value = 'GERENCIANDO_COTACOES';
        }
      }
    })

    return {
      isSidebarOpen,
      toggleSidebar,
      closeSidebar,
      editState,
      isLoading,
      page1Valid,
      wizardData,
      fornecedores,
      carregandoFornecedores,
      todasCotacoes,
      carregandoCotacoes,
      cotacoesSelecionadas,
      itensPorCotacao,
      temCotacoes,
      totalItensSelecionados,
      getTitulo,
      getSubtitulo,
      finalizarRascunho,
      editarRascunho,
      gerarPedidoFinal,
      salvarCotacao,
      deletarCotacao,
      toggleCotacaoSelecionada,
      toggleItemNaCotacao,
      handleItemCheckboxChange,
      handleItemClick,
      isCotacaoSelecionada,
      isItemSelecionadoNaCotacao,
      temItensSelecionados,
      getNomeItem,
      formatarPreco,
      cancelar,
      voltar,
      onRascunhoCreated
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
  justify-content: flex-end;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
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

.btn-success:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 8px -1px rgba(16, 185, 129, 0.4);
}

.btn-success:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  opacity: 0.6;
  box-shadow: none;
}

/* Step Seleção de Cotações */
.step-selecao-cotacoes {
  padding: 0;
  margin-top: 24px;
}

.step-selecao-cotacoes .info-box {
  background: #f0f4ff;
  border: 1px solid #c7d2fe;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.step-selecao-cotacoes .info-box h4 {
  margin: 0 0 4px 0;
  color: #1F285F;
  font-size: 1rem;
}

.step-selecao-cotacoes .info-box p {
  margin: 0;
  color: #6b7280;
  font-size: 0.875rem;
}

/* Lista de Cotações para Seleção */
.cotacoes-lista-selecao {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cotacao-selecao-card {
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.2s;
}

.cotacao-selecao-card:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.cotacao-selecao-card.cotacao-selecionada {
  border-color: #10b981;
  background: #f0fdf4;
}

/* Header da Cotação */
.cotacao-selecao-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  cursor: pointer;
  user-select: none;
  transition: background 0.2s;
}

.cotacao-selecao-header:hover {
  background: #f9fafb;
}

.cotacao-selecionada .cotacao-selecao-header {
  background: #ecfdf5;
}

.cotacao-checkbox {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.cotacao-checkbox input {
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.cotacao-info-principal {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cotacao-fornecedor-nome {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.cotacao-fornecedor-nome strong {
  font-size: 1.0625rem;
  color: #1F285F;
}

.cotacao-badge {
  padding: 4px 10px;
  background: #dbeafe;
  color: #1e40af;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.badge-warning {
  padding: 4px 8px;
  background: #fef3c7;
  color: #92400e;
  border: 1px solid #fbbf24;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

.cotacao-detalhes {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.cotacao-preco {
  font-size: 1.125rem;
  font-weight: 700;
  color: #059669;
}

.cotacao-prazo {
  font-size: 0.875rem;
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* Lista de Itens da Cotação */
.cotacao-itens-lista {
  padding: 0 16px 16px 16px;
  background: white;
}

.cotacao-selecionada .cotacao-itens-lista {
  background: #f0fdf4;
}

.itens-header {
  padding: 12px 0 12px 0;
  border-top: 1px solid #e5e7eb;
  margin-bottom: 8px;
}

.itens-header span {
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
}

.item-selecao {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  background: #f9fafb;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  margin-bottom: 6px;
  transition: all 0.2s;
  cursor: pointer;
  user-select: none;
}

.item-selecao:hover {
  background: #f3f4f6;
  border-color: #d1d5db;
}

.item-selecao.item-selecionado {
  background: #dcfce7;
  border-color: #10b981;
}

.item-checkbox {
  display: flex;
  align-items: center;
  flex-shrink: 0;
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
  flex: 1;
  min-width: 0;
}

.item-nome {
  font-weight: 500;
  color: #374151;
  font-size: 0.9375rem;
}

/* Resumo da Seleção */
.resumo-selecao {
  margin-top: 20px;
  padding: 16px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 8px;
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.resumo-item {
  display: flex;
  gap: 8px;
  align-items: center;
}

.resumo-label {
  font-size: 0.875rem;
  color: #374151;
  font-weight: 500;
}

.resumo-valor {
  font-size: 1.125rem;
  font-weight: 700;
  color: #059669;
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
