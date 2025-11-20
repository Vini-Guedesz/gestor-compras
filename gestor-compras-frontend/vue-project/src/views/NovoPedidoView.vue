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
                  v-model="wizardData.pedido"
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

                <div class="step-selecao-itens">
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
                    :disabled="itensSelecionados.length === 0 || isLoading"
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
import { ref, computed, onMounted } from 'vue'
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
      rascunho: { id: null, observacao: '', itens: [], dataCriacao: null },
      pedido: { id: null, observacao: '', itens: [] } // Mantém compatibilidade com PedidoFormPage1
    })

    const fornecedores = ref([])
    const carregandoFornecedores = ref(false)
    const todasCotacoes = ref([])
    const carregandoCotacoes = ref(false)
    const itensSelecionados = ref([])

    // Computed properties
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

    const itensCotados = computed(() => {
      const idsSet = new Set()
      todasCotacoes.value.forEach(cotacao => {
        if (cotacao.itensRascunhoIds) {
          cotacao.itensRascunhoIds.forEach(id => idsSet.add(id))
        }
      })
      return wizardData.value.rascunho.itens.filter(item => idsSet.has(item.id))
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
      wizardData.value.rascunho = { ...rascunhoCriado, itens: rascunhoCriado.itens || [] };
      wizardData.value.pedido.id = rascunhoCriado.id;
      wizardData.value.pedido.itens = rascunhoCriado.itens || [];
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
        wizardData.value.rascunho = rascunhoSalvo;
        wizardData.value.pedido = { ...rascunhoSalvo, id: rascunhoSalvo.id };

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
                wizardData.value.pedido = {
                    id: rascunho.id,
                    observacao: rascunho.observacao,
                    itens: rascunho.itens || [],
                    dataCriacao: rascunho.dataCriacao
                }
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
      if (itensSelecionados.value.length === 0) {
        alert('Selecione pelo menos um item para gerar o pedido.')
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
        const pedidoCriado = await rascunhoService.converterParaPedido(
          wizardData.value.rascunho.id,
          itensSelecionados.value
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
        todasCotacoes.value = await cotacaoRascunhoService.listarPorRascunho(wizardData.value.rascunho.id)
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
        };
        wizardData.value.pedido = {
          id: rascunho.id,
          observacao: rascunho.observacao,
          itens: rascunho.itens || [],
          dataCriacao: rascunho.dataCriacao
        };
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
      itensSelecionados,
      temItensCotados,
      itensCotados,
      getTitulo,
      getSubtitulo,
      finalizarRascunho,
      editarRascunho,
      gerarPedidoFinal,
      salvarCotacao,
      deletarCotacao,
      toggleItemSelecionado,
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

/* Step Seleção de Itens */
.step-selecao-itens {
  padding: 0;
  margin-top: 24px;
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
