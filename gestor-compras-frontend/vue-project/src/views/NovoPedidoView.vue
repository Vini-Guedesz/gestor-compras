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
          <span class="breadcrumb-current">{{ isEditando ? 'Editar Rascunho' : 'Novo Pedido' }}</span>
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
                      @click="toggleCotacaoSelecionada(cotacao.id)"
                    >
                      <!-- Header da Cotação -->
                      <div class="cotacao-selecao-header">
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
                            <!-- Contador de itens selecionados -->
                            <span
                              v-else-if="isCotacaoSelecionada(cotacao.id) && temItensSelecionados(cotacao.id)"
                              class="badge-success"
                            >
                              ✓ {{ contarItensSelecionados(cotacao.id) }} {{ contarItensSelecionados(cotacao.id) === 1 ? 'item' : 'itens' }}
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
                          :class="{
                            'item-selecionado': isItemSelecionadoNaCotacao(cotacao.id, itemId),
                            'item-erro': itemComErro.cotacaoId === cotacao.id && itemComErro.itemId === itemId
                          }"
                          @click.stop="handleItemClick(cotacao.id, itemId)"
                        >
                          <div class="item-checkbox-visual" :class="{ 'checked': isItemSelecionadoNaCotacao(cotacao.id, itemId) }">
                            <svg v-if="isItemSelecionadoNaCotacao(cotacao.id, itemId)" viewBox="0 0 24 24" width="14" height="14">
                              <path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z"/>
                            </svg>
                          </div>
                          <div class="item-info">
                            <span class="item-nome">{{ getNomeItem(itemId) }}</span>
                            <span v-if="itemComErro.cotacaoId === cotacao.id && itemComErro.itemId === itemId" class="item-erro-msg">
                              ⚠️ Item já selecionado em outra cotação
                            </span>
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
                      <svg viewBox="0 0 24 24" width="20" height="20" class="resumo-icon">
                        <path fill="currentColor" d="M9 11H7v2h2v-2m4 0h-2v2h2v-2m4 0h-2v2h2v-2m2-7h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2m0 16H5V9h14v11z"/>
                      </svg>
                      <span class="resumo-label">Cotações selecionadas:</span>
                      <span class="resumo-valor">{{ cotacoesSelecionadas.length }}</span>
                    </div>
                    <div class="resumo-item">
                      <svg viewBox="0 0 24 24" width="20" height="20" class="resumo-icon">
                        <path fill="currentColor" d="M19,3H14.82C14.4,1.84 13.3,1 12,1C10.7,1 9.6,1.84 9.18,3H5A2,2 0 0,0 3,5V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V5A2,2 0 0,0 19,3M12,3A1,1 0 0,1 13,4A1,1 0 0,1 12,5A1,1 0 0,1 11,4A1,1 0 0,1 12,3M7,7H17V5H19V19H5V5H7V7M12,17V15H17V17H12M12,11V9H17V11H12M8,12V9H10V12H8M8,17V14H10V17H8Z"/>
                      </svg>
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
                  <button type="button" @click="abrirModalDevolucao" class="btn-warning">
                    Devolver para Edição
                  </button>
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

    <!-- Modal Devolução para Edição -->
    <div v-if="modalDevolucaoAberto" class="modal-overlay" @click="fecharModalDevolucao">
      <div class="modal-container-small" @click.stop>
        <div class="modal-header">
          <h3>Devolver Rascunho para Edição</h3>
          <button @click="fecharModalDevolucao" class="btn-close">×</button>
        </div>
        <div class="modal-body">
          <p class="modal-description">
            ⚠️ Ao devolver o rascunho para edição, o status voltará para ATIVO e o criador poderá fazer alterações novamente.
            <br><br>
            <strong>ATENÇÃO:</strong> Todas as cotações existentes serão <strong>removidas permanentemente</strong> para evitar inconsistências com possíveis edições nos itens.
          </p>
          <div class="form-group">
            <label class="form-label">Motivo da Devolução *</label>
            <textarea
              v-model="motivoDevolucao"
              class="form-textarea"
              rows="4"
              placeholder="Descreva o motivo da devolução (mínimo 10 caracteres)..."
              maxlength="500"
              required
            ></textarea>
            <small class="form-hint">{{ motivoDevolucao.length }}/500 caracteres</small>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="fecharModalDevolucao" class="btn-cancel">Cancelar</button>
          <button
            @click="confirmarDevolucao"
            class="btn-warning"
            :disabled="motivoDevolucao.length < 10 || devolvendo"
          >
            {{ devolvendo ? 'Devolvendo...' : 'Devolver para Edição' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useToast } from '@/composables/useToast'
import { useErrorModal } from '@/composables/useErrorModal'
import rascunhoService from '@/services/rascunhoService.js'
import fornecedorService from '@/services/fornecedorService.js'
import cotacaoRascunhoService from '@/services/cotacaoRascunhoService.js'
import logger from '@/utils/logger.js'
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
    const { success, warning, error: toastError } = useToast()

    // State
    const isSidebarOpen = ref(false)
    const editState = ref('EDITANDO_RASCUNHO') // 'EDITANDO_RASCUNHO' | 'GERENCIANDO_COTACOES'
    const isLoading = ref(false)
    const page1Valid = ref(false)

    const wizardData = ref({
      rascunho: { id: null, observacao: '', itens: [], dataCriacao: null }
    })

    // Para mostrar aviso de item já selecionado (cotacaoId + itemId)
    const itemComErro = ref({ cotacaoId: null, itemId: null })

    const fornecedores = ref([])
    const carregandoFornecedores = ref(false)
    const todasCotacoes = ref([])
    const carregandoCotacoes = ref(false)
    const cotacoesSelecionadas = ref([]) // IDs das cotações selecionadas
    const itensPorCotacao = ref({}) // { cotacaoId: [itemId1, itemId2, ...] }

    // Devolução para Edição
    const modalDevolucaoAberto = ref(false)
    const motivoDevolucao = ref('')
    const devolvendo = ref(false)

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

    const isEditando = computed(() => {
      return wizardData.value.rascunho.id !== null
    })

    // Methods
    const toggleSidebar = () => { isSidebarOpen.value = !isSidebarOpen.value }
    const closeSidebar = () => { isSidebarOpen.value = false }

    const onRascunhoCreated = (rascunhoCriado) => {
      wizardData.value.rascunho = { ...rascunhoCriado, itens: rascunhoCriado.itens || [] }
      router.replace({ path: `/pedidos/novo/${rascunhoCriado.id}` })
    }

    const finalizarRascunho = async () => {
      if (!page1Valid.value) {
        warning('Por favor, preencha todos os campos obrigatórios e adicione ao menos um item.')
        return
      }

      if (!wizardData.value.rascunho.id) {
        warning('Por favor, salve pelo menos um item antes de finalizar o rascunho.');
        return;
      }

      // Confirmar antes de finalizar
      const { showWarning } = useErrorModal()
      const totalItens = wizardData.value.rascunho.itens?.length || 0

      showWarning(
        `Total de itens: ${totalItens}\n\nApós finalizar, o rascunho terá o status "Em Cotação".`,
        {
          title: 'Finalizar Rascunho?',
          confirmText: 'Sim, finalizar',
          cancelText: 'Cancelar',
          onConfirm: async () => {
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
              logger.error('Erro ao finalizar rascunho:', error)
              const mensagem = error.message || 'Erro ao salvar. Tente novamente.'
              toastError(`Erro ao finalizar rascunho: ${mensagem}`, { duration: 7000 })
            } finally {
              isLoading.value = false
            }
          }
        }
      )
    }

    const abrirModalDevolucao = async () => {
      try {
        // Verificar quantas cotações existem
        const quantidadeCotacoes = await rascunhoService.contarCotacoes(wizardData.value.rascunho.id)

        if (quantidadeCotacoes > 0) {
          const confirmacao = window.confirm(
            `ATENÇÃO: Este rascunho possui ${quantidadeCotacoes} cotação(ões).\n\n` +
            `Ao devolver para edição, TODAS as cotações serão REMOVIDAS permanentemente.\n\n` +
            `Deseja continuar?`
          )

          if (!confirmacao) {
            return
          }
        }

        motivoDevolucao.value = ''
        modalDevolucaoAberto.value = true
      } catch (err) {
        logger.error('Erro ao verificar cotações:', err)
        toastError('Erro ao verificar cotações do rascunho')
      }
    }

    const fecharModalDevolucao = () => {
      modalDevolucaoAberto.value = false
      motivoDevolucao.value = ''
    }

    const confirmarDevolucao = async () => {
      if (motivoDevolucao.value.length < 10) {
        toastError('O motivo deve ter pelo menos 10 caracteres')
        return
      }

      devolvendo.value = true
      try {
        await rascunhoService.devolverParaEdicao(wizardData.value.rascunho.id, {
          motivo: motivoDevolucao.value
        })

        success('Rascunho devolvido para edição com sucesso!')
        fecharModalDevolucao()

        // Atualizar estado e redirecionar para edição
        editState.value = 'EDITANDO_RASCUNHO'
        router.replace({ query: { state: 'edit' } })

        // Recarregar dados do rascunho
        const rascunho = await rascunhoService.obterPorId(wizardData.value.rascunho.id)
        wizardData.value.rascunho = { ...rascunho, itens: rascunho.itens || [] }
      } catch (err) {
        logger.error('Erro ao devolver rascunho:', err)
        toastError(err.message || 'Erro ao devolver rascunho para edição')
      } finally {
        devolvendo.value = false
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
            logger.error('Erro ao carregar rascunho para edição:', error)
            toastError('Erro ao carregar rascunho para edição. Tente novamente.')
        } finally {
            isLoading.value = false
        }
    }

    const gerarPedidoFinal = async () => {
      // Validar seleção de cotações
      if (cotacoesSelecionadas.value.length === 0) {
        warning('Selecione pelo menos uma cotação para gerar o pedido.')
        return
      }

      // Validar seleção de itens
      if (totalItensSelecionados.value === 0) {
        warning('Selecione pelo menos um item nas cotações para gerar o pedido.')
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

        warning(
          `As seguintes cotações estão marcadas mas não têm itens selecionados: ${nomeCotacoes}. ` +
          `Por favor, selecione pelo menos um item em cada cotação ou desmarque as cotações vazias.`,
          { duration: 8000 }
        )
        return
      }

      // Verificar se o usuário está autenticado
      const token = sessionStorage.getItem('authToken')
      if (!token) {
        toastError('Sua sessão expirou. Por favor, faça login novamente.')
        router.push('/login')
        return
      }

      try {
        isLoading.value = true

        // Preparar dados para envio: criar mapeamento de cotação → itens
        // IMPORTANTE: Enviar APENAS as cotações que foram SELECIONADAS
        const cotacaoParaItens = {}

        // Iterar apenas sobre as cotações SELECIONADAS
        cotacoesSelecionadas.value.forEach(cotacaoId => {
          const itens = itensPorCotacao.value[cotacaoId]
          if (itens && itens.length > 0) {
            cotacaoParaItens[cotacaoId] = itens
          }
        })

        const pedidoCriado = await rascunhoService.converterParaPedido(
          wizardData.value.rascunho.id,
          null,  // itemRascunhoIds (legado) = null
          cotacaoParaItens  // Novo formato: mapeamento cotação → itens
        )
        success(`Pedido #${pedidoCriado.id} criado com sucesso!`)
        router.push('/pedidos')
      } catch (error) {
        logger.error('Erro ao gerar pedido:', error)
        // Verificar se é erro de autenticação
        if (error.message && (error.message.includes('401') || error.message.includes('não autenticado') || error.message.includes('Sessão expirada'))) {
          toastError('Sua sessão expirou. Por favor, faça login novamente.')
          router.push('/login')
          return
        }
        toastError(error.message || 'Erro ao gerar pedido. Tente novamente.', { duration: 7000 })
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
        logger.error('Erro ao carregar cotações:', error)
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
        logger.error('Erro ao salvar cotação:', error)
        const mensagem = error.message || 'Erro ao salvar cotação. Tente novamente.'
        toastError(`Erro ao salvar cotação: ${mensagem}`, { duration: 7000 })
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
        logger.error('Erro ao deletar cotação:', error)
        toastError('Erro ao deletar cotação. Tente novamente.')
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
              warning('Este item já está selecionado em outra cotação. Um item só pode estar em uma cotação por vez.')
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
      // Garantir que a cotação está selecionada
      if (!cotacoesSelecionadas.value.includes(cotacaoId)) {
        return
      }

      // Criar cópia do objeto inteiro para forçar reatividade
      const novoItensPorCotacao = { ...itensPorCotacao.value }

      // Garantir que existe array para esta cotação
      if (!novoItensPorCotacao[cotacaoId]) {
        novoItensPorCotacao[cotacaoId] = []
      }

      const itens = [...novoItensPorCotacao[cotacaoId]]
      const index = itens.indexOf(itemId)

      if (index === -1) {
        // Verificar se item já está em outra cotação
        for (const [cotId, items] of Object.entries(novoItensPorCotacao)) {
          if (Number(cotId) !== cotacaoId && items.includes(itemId)) {
            // Mostrar aviso visual inline apenas na cotação atual
            itemComErro.value = { cotacaoId, itemId }
            setTimeout(() => {
              itemComErro.value = { cotacaoId: null, itemId: null }
            }, 3000)

            // Também tentar mostrar toast (caso funcione)
            warning('⚠️ Este item já está selecionado em outra cotação. Um item só pode estar em uma cotação por vez.', {
              duration: 6000
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

      // Atualizar array
      novoItensPorCotacao[cotacaoId] = itens

      // Substituir objeto inteiro para forçar reatividade
      itensPorCotacao.value = novoItensPorCotacao
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

    const contarItensSelecionados = (cotacaoId) => {
      const itens = itensPorCotacao.value[cotacaoId]
      return itens ? itens.length : 0
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
        const { showWarning } = useErrorModal()
        showWarning('Você tem seleções não salvas. Todas as seleções serão perdidas.', {
          title: 'Cancelar Alterações?',
          confirmText: 'Sim, cancelar',
          cancelText: 'Continuar editando',
          onConfirm: () => router.push('/pedidos')
        })
        return
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
        logger.error('Erro ao carregar fornecedores:', error)
      } finally {
        carregandoFornecedores.value = false
      }
    }

    const carregarRascunhoExistente = async (rascunhoId) => {
      try {
        isLoading.value = true
        const rascunho = await rascunhoService.obterPorId(rascunhoId);

        wizardData.value.rascunho = {
          ...rascunho,
          itens: rascunho.itens || []
        }
      } catch (error) {
        logger.error('Erro ao carregar rascunho:', error)
        toastError('Erro ao carregar rascunho. Redirecionando...')
        router.push('/pedidos')
      } finally{
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
      itemComErro,
      temCotacoes,
      totalItensSelecionados,
      getTitulo,
      getSubtitulo,
      isEditando,
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
      contarItensSelecionados,
      getNomeItem,
      formatarPreco,
      cancelar,
      voltar,
      onRascunhoCreated,
      // Devolução para Edição
      modalDevolucaoAberto,
      motivoDevolucao,
      devolvendo,
      abrirModalDevolucao,
      fecharModalDevolucao,
      confirmarDevolucao
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

/* Wizard Container */
.wizard-container {
  max-width: 1000px;
  margin: 0 auto; /* Centraliza horizontalmente */
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
  background: linear-gradient(135deg, #dbeafe 0%, #eff6ff 100%);
  border: 2px solid #93c5fd;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.12);
  border-left: 4px solid #1F285F;
}

.step-selecao-cotacoes .info-box h4 {
  margin: 0 0 8px 0;
  color: #1e40af;
  font-size: 1.0625rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 8px;
}

.step-selecao-cotacoes .info-box p {
  margin: 0;
  color: #475569;
  font-size: 0.875rem;
  line-height: 1.5;
}

/* Lista de Cotações para Seleção */
.cotacoes-lista-selecao {
  display: flex;
  flex-direction: column;
  gap: 24px;
  position: relative;
}

.cotacao-selecao-card {
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.2s;
  position: relative;
  cursor: pointer;
}

.cotacao-selecao-card:not(:last-child)::after {
  content: '';
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  height: 1px;
  background: linear-gradient(to right, transparent, #cbd5e1, transparent);
}

.cotacao-selecao-card:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.cotacao-selecao-card.cotacao-selecionada {
  border-color: #10b981;
  border-width: 2px;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);
  transform: scale(1.01);
}

/* Header da Cotação */
.cotacao-selecao-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
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
  width: 20px !important;
  height: 20px !important;
  max-width: 20px;
  max-height: 20px;
  min-width: 20px;
  min-height: 20px;
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

.badge-success {
  padding: 4px 10px;
  background: #d1fae5;
  color: #065f46;
  border: 1px solid #6ee7b7;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
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
  gap: 10px;
  padding: 8px 10px;
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

.item-selecao.item-erro {
  background: #fef3c7;
  border-color: #f59e0b;
  animation: shake 0.5s;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

.item-erro-msg {
  font-size: 0.75rem;
  color: #92400e;
  font-weight: 600;
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-5px); }
  to { opacity: 1; transform: translateY(0); }
}

.item-checkbox-visual {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 18px;
  height: 18px;
  border: 2px solid #d1d5db;
  border-radius: 4px;
  background: white;
  transition: all 0.2s;
  color: white;
}

.item-checkbox-visual.checked {
  background: #10b981;
  border-color: #10b981;
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
  margin-top: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border: 2px solid #86efac;
  border-radius: 12px;
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.15);
}

.resumo-item {
  display: flex;
  gap: 10px;
  align-items: center;
  padding: 8px 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.resumo-icon {
  color: #059669;
  flex-shrink: 0;
}

.resumo-label {
  font-size: 0.875rem;
  color: #374151;
  font-weight: 500;
}

.resumo-valor {
  font-size: 1.25rem;
  font-weight: 700;
  color: #059669;
  min-width: 24px;
  text-align: center;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #6b7280;
}

/* Responsive */
@media (max-width: 1200px) {
  .wizard-container {
    max-width: 900px;
  }
}

@media (max-width: 1024px) {
  .content-area {
    margin-left: 0;
    padding: 20px;
  }

  .wizard-container {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .content-area {
    padding: 12px;
  }

  .wizard-container {
    max-width: 100%;
    margin: 0;
  }

  .wizard-card {
    border-radius: 12px;
  }

  .wizard-header {
    padding: 16px;
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .wizard-title {
    font-size: 1.25rem;
  }

  .wizard-subtitle {
    font-size: 0.875rem;
  }

  .wizard-body {
    padding: 16px;
  }

  .wizard-footer {
    padding: 12px;
    flex-direction: column-reverse;
    gap: 8px;
  }

  .footer-actions {
    width: 100%;
    flex-direction: column-reverse;
    gap: 8px;
  }

  .footer-actions button {
    width: 100%;
    justify-content: center;
  }

  /* Cotações em mobile */
  .cotacoes-lista-selecao {
    gap: 12px;
  }

  .cotacao-selecao-card {
    padding: 12px;
  }

  .cotacao-fornecedor-nome {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }

  /* Resumo em mobile */
  .resumo-final-container {
    padding: 12px;
  }

  .resumo-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  /* Breadcrumb em mobile */
  .breadcrumb {
    flex-wrap: wrap;
    gap: 8px;
  }

  .btn-voltar {
    font-size: 0.875rem;
  }
}

@media (max-width: 480px) {
  .content-area {
    padding: 8px;
  }

  .wizard-card {
    border-radius: 8px;
  }

  .wizard-header {
    padding: 12px;
  }

  .wizard-body {
    padding: 12px;
  }

  .wizard-footer {
    padding: 10px;
  }

  .wizard-title {
    font-size: 1.125rem;
  }

  .wizard-subtitle {
    font-size: 0.8125rem;
  }

  .cotacao-selecao-card {
    padding: 10px;
  }

  .footer-actions button {
    padding: 12px 16px;
    font-size: 0.875rem;
  }
}

/* Estilos para Devolução */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 20px;
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-container-small {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 540px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 24px 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #111827;
}

.btn-close {
  background: transparent;
  border: none;
  font-size: 1.75rem;
  cursor: pointer;
  color: #9ca3af;
  padding: 0;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.2s;
  line-height: 1;
}

.btn-close:hover {
  background: #f3f4f6;
  color: #374151;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
}

.modal-description {
  color: #6b7280;
  font-size: 0.9375rem;
  margin-bottom: 24px;
  line-height: 1.6;
  background: #f9fafb;
  padding: 12px 16px;
  border-radius: 8px;
  border-left: 3px solid #f59e0b;
}

.form-group {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 0.9375rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.form-textarea {
  width: 100%;
  padding: 12px 14px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 0.9375rem;
  font-family: inherit;
  resize: vertical;
  min-height: 120px;
  transition: all 0.2s;
  line-height: 1.5;
}

.form-textarea:focus {
  outline: none;
  border-color: #f59e0b;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
}

.form-textarea::placeholder {
  color: #9ca3af;
}

.form-hint {
  display: block;
  margin-top: 8px;
  color: #6b7280;
  font-size: 0.8125rem;
  text-align: right;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
  border-radius: 0 0 12px 12px;
}

.btn-cancel {
  padding: 11px 24px;
  background: white;
  color: #374151;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 0.9375rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background: #f9fafb;
  border-color: #d1d5db;
}

.btn-warning {
  padding: 11px 24px;
  background: #f59e0b;
  color: white;
  border: 2px solid #f59e0b;
  border-radius: 8px;
  font-size: 0.9375rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.btn-warning:hover:not(:disabled) {
  background: #d97706;
  border-color: #d97706;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.btn-warning:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.btn-warning:disabled {
  background: #d1d5db;
  border-color: #d1d5db;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}
</style>
