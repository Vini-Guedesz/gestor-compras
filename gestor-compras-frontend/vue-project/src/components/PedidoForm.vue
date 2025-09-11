<template>
  <div v-if="isVisible" class="modal-overlay" @click.self="fecharModal">
    <Transition name="modal" appear>
      <div class="modal-container">
        <div class="modal-header">
          <h2 class="modal-title">{{ pedido?.id ? 'Editar Pedido' : 'Novo Pedido de Compra' }}</h2>
          <button @click="fecharModal" class="close-button">&times;</button>
        </div>

        <div class="modal-body">
          <form @submit.prevent="salvarPedido" class="pedido-form">

            <!-- Dados Gerais -->
            <div class="form-section">
              <h3 class="section-title">Informações do Pedido</h3>

              <div class="form-grid">
                <div class="form-group">
                  <label for="requisitante" class="form-label">
                    Requisitante <span class="required">*</span>
                  </label>
                  <input
                    type="text"
                    id="requisitante"
                    v-model="formData.requisitante"
                    class="form-input"
                    placeholder="Nome do requisitante"
                    required
                  />
                </div>

                <div class="form-group">
                  <label for="unidadeFuncional" class="form-label">
                    Unidade Funcional <span class="required">*</span>
                  </label>
                  <select
                    id="unidadeFuncional"
                    v-model="formData.unidadeFuncional"
                    class="form-select"
                    required
                  >
                    <option value="">Selecione a unidade</option>
                    <option value="TI - Infraestrutura">TI - Infraestrutura</option>
                    <option value="TI - Desenvolvimento">TI - Desenvolvimento</option>
                    <option value="RH - Recursos Humanos">RH - Recursos Humanos</option>
                    <option value="RH - Treinamento">RH - Treinamento</option>
                    <option value="Financeiro">Financeiro</option>
                    <option value="Compras">Compras</option>
                    <option value="Manutenção">Manutenção</option>
                    <option value="Operacional">Operacional</option>
                    <option value="Administrativo">Administrativo</option>
                    <option value="Comercial">Comercial</option>
                  </select>
                </div>

                <div class="form-group">
                  <label for="centroCusto" class="form-label">
                    Centro de Custo
                  </label>
                  <select
                    id="centroCusto"
                    v-model="formData.centroCusto"
                    class="form-select"
                  >
                    <option value="">Selecione o centro de custo</option>
                    <option value="CC-001">CC-001 - Tecnologia da Informação</option>
                    <option value="CC-002">CC-002 - Recursos Humanos</option>
                    <option value="CC-003">CC-003 - Financeiro</option>
                    <option value="CC-004">CC-004 - Operações</option>
                    <option value="CC-005">CC-005 - Administrativo</option>
                    <option value="CC-006">CC-006 - Comercial</option>
                  </select>
                </div>

                <div class="form-group">
                  <label for="projeto" class="form-label">
                    Projeto/Atividade
                  </label>
                  <input
                    type="text"
                    id="projeto"
                    v-model="formData.projeto"
                    class="form-input"
                    placeholder="Nome do projeto ou atividade relacionada"
                  />
                </div>

                <div class="form-group">
                  <label for="objetivo" class="form-label">
                    Objetivo do Pedido <span class="required">*</span>
                  </label>
                  <select
                    id="objetivo"
                    v-model="formData.objetivo"
                    class="form-select"
                    required
                  >
                    <option value="">Selecione o objetivo</option>
                    <option value="Aquisição de Material">Aquisição de Material</option>
                    <option value="Contratação de Serviço">Contratação de Serviço</option>
                    <option value="Manutenção Preventiva">Manutenção Preventiva</option>
                    <option value="Manutenção Corretiva">Manutenção Corretiva</option>
                    <option value="Investimento">Investimento</option>
                    <option value="Custeio">Custeio</option>
                    <option value="Emergencial">Emergencial</option>
                  </select>
                </div>

                <div class="form-group">
                  <label for="status" class="form-label">
                    Status
                  </label>
                  <select
                    id="status"
                    v-model="formData.status"
                    class="form-select"
                    :disabled="!pedido?.id"
                  >
                    <option value="rascunho">Rascunho</option>
                    <option value="pendente">Pendente</option>
                    <option value="aprovado">Aprovado</option>
                    <option value="rejeitado">Rejeitado</option>
                    <option value="cancelado">Cancelado</option>
                  </select>
                </div>
              </div>

              <div class="form-group description-group">
                <label for="descricao" class="form-label">
                  Descrição do Pedido <span class="required">*</span>
                </label>
                <textarea
                  id="descricao"
                  v-model="formData.descricao"
                  class="form-textarea"
                  placeholder="Descreva detalhadamente o que está sendo solicitado..."
                  rows="4"
                  required
                ></textarea>
              </div>

              <div class="form-group observacoes-group">
                <label for="observacoes" class="form-label">
                  Observações / Justificativa
                </label>
                <textarea
                  id="observacoes"
                  v-model="formData.observacoes"
                  class="form-textarea"
                  placeholder="Justificativa para o pedido, observações importantes, prazos especiais..."
                  rows="3"
                ></textarea>
              </div>
            </div>

            <!-- Itens do Pedido -->
            <div class="form-section items-section">
              <div class="items-header">
                <h3 class="section-title">Itens do Pedido</h3>
                <button type="button" @click="adicionarItem" class="add-item-button">
                  <svg viewBox="0 0 24 24" width="16" height="16">
                    <path fill="white" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                  </svg>
                  Adicionar Item
                </button>
              </div>

              <!-- Lista de Itens -->
              <div v-if="formData.itens.length > 0">
                <div
                  v-for="(item, index) in formData.itens"
                  :key="index"
                  class="item-card"
                >
                  <div class="item-header">
                    <span class="item-title">Item #{{ index + 1 }}</span>
                    <button
                      type="button"
                      @click="removerItem(index)"
                      class="remove-item-button"
                      :disabled="formData.itens.length === 1"
                    >
                      Remover
                    </button>
                  </div>

                  <div class="form-grid">
                    <div class="form-group">
                      <label class="form-label">
                        Nome do Produto/Serviço <span class="required">*</span>
                      </label>
                      <input
                        type="text"
                        v-model="item.produto"
                        class="form-input"
                        placeholder="Ex: Notebook Dell Latitude 5520"
                        required
                      />
                    </div>

                    <div class="form-group">
                      <label class="form-label">
                        Quantidade <span class="required">*</span>
                      </label>
                      <input
                        type="number"
                        v-model.number="item.quantidade"
                        class="form-input"
                        placeholder="1"
                        min="1"
                        required
                      />
                    </div>

                    <div class="form-group">
                      <label class="form-label">
                        Unidade de Medida
                      </label>
                      <select v-model="item.unidadeMedida" class="form-select">
                        <option value="unidade">Unidade</option>
                        <option value="par">Par</option>
                        <option value="conjunto">Conjunto</option>
                        <option value="kg">Quilograma</option>
                        <option value="litro">Litro</option>
                        <option value="metro">Metro</option>
                        <option value="caixa">Caixa</option>
                        <option value="pacote">Pacote</option>
                        <option value="resma">Resma</option>
                        <option value="hora">Hora</option>
                        <option value="mes">Mês</option>
                        <option value="licenca">Licença</option>
                      </select>
                    </div>

                    <div class="form-group">
                      <label class="form-label">
                        Valor Estimado (R$)
                      </label>
                      <input
                        type="number"
                        v-model.number="item.valorEstimado"
                        class="form-input"
                        placeholder="0,00"
                        step="0.01"
                        min="0"
                      />
                    </div>
                  </div>

                  <div class="form-group especificacao-group">
                    <label class="form-label">
                      Especificação Técnica
                    </label>
                    <textarea
                      v-model="item.especificacao"
                      class="form-textarea"
                      placeholder="Descrição detalhada das especificações técnicas, modelo, marca preferencial..."
                      rows="2"
                    ></textarea>
                  </div>

                  <div class="form-group justificativa-group">
                    <label class="form-label">
                      Justificativa <span class="required">*</span>
                    </label>
                    <textarea
                      v-model="item.justificativa"
                      class="form-textarea"
                      placeholder="Justifique a necessidade deste item..."
                      rows="2"
                      required
                    ></textarea>
                  </div>

                  <div class="form-group observacoes-item-group">
                    <label class="form-label">
                      Observações
                    </label>
                    <textarea
                      v-model="item.observacao"
                      class="form-textarea"
                      placeholder="Observações adicionais sobre este item..."
                      rows="2"
                    ></textarea>
                  </div>
                </div>

                <!-- Resumo Final -->
                <div v-if="formData.itens.length > 0" class="resumo-final">
                  <div class="resumo-grid">
                    <div class="resumo-item">
                      <span class="resumo-label">Total de Itens:</span>
                      <span class="resumo-value">{{ totalItens }}</span>
                    </div>
                    <div class="resumo-item">
                      <span class="resumo-label">Quantidade Total:</span>
                      <span class="resumo-value">{{ quantidadeTotal }}</span>
                    </div>
                    <div class="resumo-item">
                      <span class="resumo-label">Valor Total Estimado:</span>
                      <span class="resumo-value">R$ {{ valorTotalEstimado }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <p v-else class="empty-message">Nenhum item adicionado ao pedido.</p>
            </div>

          </form>
        </div>

        <!-- Footer do Modal -->
        <div class="modal-footer">
          <div class="footer-actions">
            <button type="button" @click="fecharModal" class="btn-cancel">
              Cancelar
            </button>

            <button
              type="button"
              @click="salvarPedido"
              class="btn-save"
              :disabled="!podeSerSalvo || isLoading"
            >
              {{ isLoading ? 'Salvando...' : (pedido?.id ? 'Atualizar' : 'Criar Pedido') }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script>
import { ref, computed, watch, nextTick } from 'vue'

export default {
  name: 'PedidoForm',
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
    // Estados reativos
    const isLoading = ref(false)

    // Dados do formulário
    const formData = ref({
      requisitante: '',
      unidadeFuncional: '',
      centroCusto: '',
      projeto: '',
      objetivo: '',
      status: 'rascunho',
      descricao: '',
      observacoes: '',
      itens: []
    })

    // Template para novo item
    const novoItemTemplate = () => ({
      produto: '',
      quantidade: 1,
      unidadeMedida: 'unidade',
      valorEstimado: 0,
      especificacao: '',
      justificativa: '',
      observacao: ''
    })

    // Computed properties
    const totalItens = computed(() => formData.value.itens.length)

    const quantidadeTotal = computed(() => {
      return formData.value.itens.reduce((total, item) => total + (item.quantidade || 0), 0)
    })

    const valorTotalEstimado = computed(() => {
      const total = formData.value.itens.reduce((soma, item) => {
        const valor = (item.valorEstimado || 0) * (item.quantidade || 0)
        return soma + valor
      }, 0)
      return formatarValor(total)
    })

    // Validações
    const validationErrors = computed(() => {
      const errors = []

      if (!formData.value.requisitante?.trim()) {
        errors.push('Requisitante é obrigatório')
      }

      if (!formData.value.unidadeFuncional?.trim()) {
        errors.push('Unidade Funcional é obrigatória')
      }

      if (!formData.value.objetivo?.trim()) {
        errors.push('Objetivo do Pedido é obrigatório')
      }

      if (!formData.value.descricao?.trim()) {
        errors.push('Descrição do Pedido é obrigatória')
      }

      if (formData.value.itens.length === 0) {
        errors.push('Pelo menos um item deve ser adicionado')
      }

      formData.value.itens.forEach((item, index) => {
        if (!item.produto?.trim()) {
          errors.push(`Item ${index + 1}: Nome do produto é obrigatório`)
        }
        if (!item.quantidade || item.quantidade <= 0) {
          errors.push(`Item ${index + 1}: Quantidade deve ser maior que zero`)
        }
        if (!item.justificativa?.trim()) {
          errors.push(`Item ${index + 1}: Justificativa é obrigatória`)
        }
      })

      return errors
    })

    const podeSerSalvo = computed(() => {
      return validationErrors.value.length === 0
    })

    // Métodos
    const formatarValor = (valor) => {
      if (!valor || valor === 0) return '0,00'
      return parseFloat(valor).toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }

    const getStatusLabel = (status) => {
      const labels = {
        rascunho: 'Rascunho',
        pendente: 'Pendente',
        aprovado: 'Aprovado',
        rejeitado: 'Rejeitado',
        cancelado: 'Cancelado'
      }
      return labels[status] || status
    }

    const getStatusClass = (status) => {
      const classes = {
        rascunho: 'status-draft',
        pendente: 'status-pending',
        aprovado: 'status-approved',
        rejeitado: 'status-rejected',
        cancelado: 'status-cancelled'
      }
      return classes[status] || 'status-default'
    }

    const resetarFormulario = () => {
      formData.value = {
        requisitante: '',
        unidadeFuncional: '',
        centroCusto: '',
        projeto: '',
        objetivo: '',
        status: 'rascunho',
        descricao: '',
        observacoes: '',
        itens: []
      }
    }

    const carregarDadosPedido = () => {
      if (props.pedido) {
        formData.value = {
          requisitante: props.pedido.requisitante || '',
          unidadeFuncional: props.pedido.unidadeFuncional || '',
          centroCusto: props.pedido.centroCusto || '',
          projeto: props.pedido.projeto || '',
          objetivo: props.pedido.objetivo || '',
          status: props.pedido.status || 'rascunho',
          descricao: props.pedido.descricao || '',
          observacoes: props.pedido.observacoes || props.pedido.observacao || '',
          itens: props.pedido.itens ? [...props.pedido.itens] : []
        }
      } else {
        resetarFormulario()
      }
    }

    const adicionarItem = () => {
      formData.value.itens.push(novoItemTemplate())
      nextTick(() => {
        // Scroll para o último item adicionado
        const itemCards = document.querySelectorAll('.item-card')
        if (itemCards.length > 0) {
          itemCards[itemCards.length - 1].scrollIntoView({ behavior: 'smooth', block: 'center' })
        }
      })
    }

    const removerItem = (index) => {
      if (formData.value.itens.length > 1) {
        formData.value.itens.splice(index, 1)
      }
    }

    const fecharModal = () => {
      resetarFormulario()
      emit('close')
    }

    const salvarPedido = async () => {
      if (!podeSerSalvo.value) {
        alert('Por favor, corrija os erros antes de salvar.')
        return
      }

      try {
        isLoading.value = true

        const dadosParaEnvio = {
          ...formData.value,
          itens: formData.value.itens.map(item => ({
            ...item,
            quantidade: parseInt(item.quantidade) || 1,
            valorEstimado: parseFloat(item.valorEstimado) || 0
          }))
        }

        emit('save', dadosParaEnvio)
      } catch (error) {
        console.error('Erro ao salvar pedido:', error)
        alert('Erro ao salvar pedido. Tente novamente.')
      } finally {
        isLoading.value = false
      }
    }

    // Watchers
    watch(() => props.isVisible, (newVal) => {
      if (newVal) {
        carregarDadosPedido()
      }
    })

    watch(() => props.pedido, () => {
      if (props.isVisible) {
        carregarDadosPedido()
      }
    })

    return {
      // Estados
      isLoading,
      formData,

      // Computed
      totalItens,
      quantidadeTotal,
      valorTotalEstimado,
      validationErrors,
      podeSerSalvo,

      // Métodos
      formatarValor,
      getStatusLabel,
      getStatusClass,
      adicionarItem,
      removerItem,
      fecharModal,
      salvarPedido,
      resetarFormulario,
      carregarDadosPedido
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
}

.modal-container {
  background: white;
  border-radius: 12px;
  width: 95%;
  max-width: 900px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  position: relative;
  transform: translate(0, 0);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  background: #f8fafc;
  border-radius: 12px 12px 0 0;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #111827;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #9ca3af;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  transition: all 0.2s;
}

.close-button:hover {
  background: #e5e7eb;
  color: #374151;
}

.modal-body {
  padding: 24px;
  flex: 1;
  overflow-y: auto;
  max-height: calc(90vh - 140px);
}

/* Form Sections */
.form-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e5e7eb;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.description-group {
  margin-top: 24px;
}

.observacoes-group {
  margin-top: 20px;
}

.especificacao-group {
  margin-top: 20px;
}

.justificativa-group {
  margin-top: 16px;
}

.observacoes-item-group {
  margin-top: 16px;
}

.form-label {
  font-weight: 500;
  color: #374151;
  margin-bottom: 4px;
  font-size: 0.875rem;
}

.form-input,
.form-select,
.form-textarea {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
  width: 100%;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.required {
  color: #ef4444;
}

/* Itens do Pedido */
.items-section {
  margin-top: 32px;
}

.items-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.add-item-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #3b82f6;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.add-item-button:hover {
  background: #2563eb;
}

.item-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  background: #f9fafb;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.item-title {
  font-weight: 600;
  color: #374151;
}

.remove-item-button {
  background: #ef4444;
  color: white;
  border: none;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
  cursor: pointer;
  transition: all 0.2s;
}

.remove-item-button:hover {
  background: #dc2626;
}

.remove-item-button:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

/* Resumo */
.resumo-final {
  margin-top: 24px;
  padding: 16px;
  background: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 8px;
}

.resumo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.resumo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.resumo-label {
  font-weight: 500;
  color: #374151;
}

.resumo-value {
  font-weight: 600;
  color: #1f2937;
}

.empty-message {
  text-align: center;
  color: #6b7280;
  font-style: italic;
  padding: 32px;
  background: #f9fafb;
  border: 1px dashed #d1d5db;
  border-radius: 8px;
}

/* Modal Footer */
.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
  border-radius: 0 0 12px 12px;
}

.footer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-cancel {
  padding: 8px 16px;
  background: #f3f4f6;
  color: #374151;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background: #e5e7eb;
}

.btn-save {
  padding: 8px 16px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-save:hover {
  background: #059669;
}

.btn-save:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

/* Transitions */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.2s ease-out;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

/* Responsive */
@media (max-width: 768px) {
  .modal-container {
    width: 100%;
    height: 100%;
    max-height: 100vh;
    border-radius: 0;
  }

  .modal-header {
    border-radius: 0;
  }

  .modal-footer {
    border-radius: 0;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
