<template>
  <div v-if="isVisible" class="modal-overlay" @click="fecharModal">
    <div class="pedido-modal" @click.stop>
      <!-- Header do Modal -->
      <div class="modal-header">
        <div class="header-content">
          <h2 class="modal-title">
            {{ pedido?.id ? 'Editar Pedido' : 'Novo Pedido de Compra' }}
          </h2>
          <p class="modal-subtitle">
            {{ pedido?.id ? `Pedido #${pedido.numero || pedido.id}` : 'Preencha os dados abaixo para criar um novo pedido' }}
          </p>
        </div>
        <button @click="fecharModal" class="close-button">
          <svg viewBox="0 0 24 24" width="24" height="24">
            <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>
      </div>

      <!-- Tabs de Navegação -->
      <div class="modal-tabs">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          :class="['tab-button', { active: activeTab === tab.id }]"
          @click="activeTab = tab.id"
        >
          <svg v-html="tab.icon" class="tab-icon" viewBox="0 0 24 24" width="20" height="20"></svg>
          {{ tab.label }}
        </button>
      </div>

      <!-- Conteúdo do Modal -->
      <div class="modal-body">
        <form @submit.prevent="salvarPedido" class="pedido-form">

          <!-- Aba: Dados Gerais -->
          <div v-if="activeTab === 'dados'" class="tab-content">
            <div class="form-section">
              <h3 class="section-title">
                <svg class="section-icon" viewBox="0 0 24 24" width="20" height="20">
                  <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                </svg>
                Informações do Pedido
              </h3>

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
                    class="form-input"
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
                    class="form-input"
                  >
                    <option value="">Selecione o centro de custo</option>
                    <option value="CC-001">CC-001 - Tecnologia da Informação</option>
                    <option value="CC-002">CC-002 - Recursos Humanos</option>
                    <option value="CC-003">CC-003 - Financeiro</option>
                    <option value="CC-004">CC-004 - Operações</option>
                    <option value="CC-005">CC-005 - Administrativo</option>
                    <option value="CC-006">CC-006 - Manutenção</option>
                  </select>
                </div>

                <div class="form-group">
                  <label for="projeto" class="form-label">
                    Projeto
                  </label>
                  <input
                    type="text"
                    id="projeto"
                    v-model="formData.projeto"
                    class="form-input"
                    placeholder="Nome ou código do projeto"
                  />
                </div>

                <div class="form-group">
                  <label for="objetivo" class="form-label">
                    Objetivo do Pedido <span class="required">*</span>
                  </label>
                  <select
                    id="objetivo"
                    v-model="formData.objetivo"
                    class="form-input"
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
                    class="form-input"
                    :disabled="!pedido?.id"
                  >
                    <option value="rascunho">Rascunho</option>
                    <option value="pendente">Pendente</option>
                    <option value="aprovado">Aprovado</option>
                    <option value="rejeitado">Rejeitado</option>
                    <option value="cancelado">Cancelado</option>
                  </select>
                  <small class="form-help" v-if="!pedido?.id">
                    Novos pedidos são criados como rascunho
                  </small>
                </div>
              </div>

              <div class="form-group full-width">
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

              <div class="form-group full-width">
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
          </div>

          <!-- Aba: Itens do Pedido -->
          <div v-if="activeTab === 'itens'" class="tab-content">
            <div class="form-section">
              <div class="section-header">
                <h3 class="section-title">
                  <svg class="section-icon" viewBox="0 0 24 24" width="20" height="20">
                    <path fill="currentColor" d="M19 7h-8l-2-2H5c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V9c0-1.1-.9-2-2-2z"/>
                  </svg>
                  Itens do Pedido
                </h3>
                <button type="button" @click="adicionarItem" class="btn-add-item">
                  <svg viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                  </svg>
                  Adicionar Item
                </button>
              </div>

              <!-- Lista de Itens -->
              <div class="itens-container" v-if="formData.itens.length > 0">
                <div
                  v-for="(item, index) in formData.itens"
                  :key="index"
                  class="item-card"
                >
                  <div class="item-header">
                    <span class="item-numero">Item #{{ index + 1 }}</span>
                    <button
                      type="button"
                      @click="removerItem(index)"
                      class="btn-remove-item"
                      :disabled="formData.itens.length === 1"
                    >
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
                      </svg>
                    </button>
                  </div>

                  <div class="item-form">
                    <div class="item-form-grid">
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
                        <select v-model="item.unidadeMedida" class="form-input">
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

                    <div class="form-group">
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

                    <div class="form-group">
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

                    <div class="form-group">
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
                </div>
              </div>

              <!-- Estado vazio -->
              <div v-else class="empty-itens">
                <svg class="empty-icon" viewBox="0 0 24 24" width="48" height="48">
                  <path fill="currentColor" d="M19 7h-8l-2-2H5c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V9c0-1.1-.9-2-2-2z"/>
                </svg>
                <h4>Nenhum item adicionado</h4>
                <p>Adicione pelo menos um item ao pedido</p>
                <button type="button" @click="adicionarItem" class="btn-primary">
                  Adicionar Primeiro Item
                </button>
              </div>

              <!-- Resumo dos Itens -->
              <div v-if="formData.itens.length > 0" class="itens-resumo">
                <h4>Resumo</h4>
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
                    <span class="resumo-label">Valor Estimado:</span>
                    <span class="resumo-value">R$ {{ valorTotalEstimado }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Aba: Revisão -->
          <div v-if="activeTab === 'revisao'" class="tab-content">
            <div class="form-section">
              <h3 class="section-title">
                <svg class="section-icon" viewBox="0 0 24 24" width="20" height="20">
                  <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                </svg>
                Revisão do Pedido
              </h3>

              <!-- Dados Gerais - Revisão -->
              <div class="revisao-section">
                <h4 class="revisao-title">Dados Gerais</h4>
                <div class="revisao-grid">
                  <div class="revisao-item">
                    <span class="revisao-label">Requisitante:</span>
                    <span class="revisao-value">{{ formData.requisitante || 'Não informado' }}</span>
                  </div>
                  <div class="revisao-item">
                    <span class="revisao-label">Unidade Funcional:</span>
                    <span class="revisao-value">{{ formData.unidadeFuncional || 'Não informado' }}</span>
                  </div>
                  <div class="revisao-item">
                    <span class="revisao-label">Centro de Custo:</span>
                    <span class="revisao-value">{{ formData.centroCusto || 'Não informado' }}</span>
                  </div>
                  <div class="revisao-item">
                    <span class="revisao-label">Projeto:</span>
                    <span class="revisao-value">{{ formData.projeto || 'Não informado' }}</span>
                  </div>
                  <div class="revisao-item">
                    <span class="revisao-label">Objetivo:</span>
                    <span class="revisao-value">{{ formData.objetivo || 'Não informado' }}</span>
                  </div>
                  <div class="revisao-item">
                    <span class="revisao-label">Status:</span>
                    <span class="revisao-value">
                      <span class="status-badge" :class="getStatusClass(formData.status)">
                        {{ getStatusLabel(formData.status) }}
                      </span>
                    </span>
                  </div>
                </div>

                <div class="revisao-item full-width">
                  <span class="revisao-label">Descrição:</span>
                  <p class="revisao-description">{{ formData.descricao || 'Não informado' }}</p>
                </div>

                <div class="revisao-item full-width" v-if="formData.observacoes">
                  <span class="revisao-label">Observações:</span>
                  <p class="revisao-description">{{ formData.observacoes }}</p>
                </div>
              </div>

              <!-- Itens - Revisão -->
              <div class="revisao-section">
                <h4 class="revisao-title">Itens do Pedido</h4>
                <div class="revisao-itens" v-if="formData.itens.length > 0">
                  <div v-for="(item, index) in formData.itens" :key="index" class="revisao-item-card">
                    <div class="revisao-item-header">
                      <span class="item-numero">Item #{{ index + 1 }}</span>
                      <span class="item-quantidade">{{ item.quantidade }}{{ item.unidadeMedida ? ` ${item.unidadeMedida}` : '' }}</span>
                    </div>
                    <h5 class="item-name">{{ item.produto || 'Produto não informado' }}</h5>
                    <p v-if="item.especificacao" class="item-spec">
                      <strong>Especificação:</strong> {{ item.especificacao }}
                    </p>
                    <p v-if="item.justificativa" class="item-justification">
                      <strong>Justificativa:</strong> {{ item.justificativa }}
                    </p>
                    <p v-if="item.observacao" class="item-observation">
                      <strong>Observação:</strong> {{ item.observacao }}
                    </p>
                    <p v-if="item.valorEstimado" class="item-value">
                      <strong>Valor Estimado:</strong> R$ {{ formatarValor(item.valorEstimado) }}
                    </p>
                  </div>
                </div>
                <p v-else class="empty-message">Nenhum item adicionado ao pedido.</p>

                <!-- Resumo Final -->
                <div class="resumo-final" v-if="formData.itens.length > 0">
                  <div class="resumo-final-grid">
                    <div class="resumo-final-item">
                      <span class="resumo-final-label">Total de Itens:</span>
                      <span class="resumo-final-value">{{ totalItens }}</span>
                    </div>
                    <div class="resumo-final-item">
                      <span class="resumo-final-label">Quantidade Total:</span>
                      <span class="resumo-final-value">{{ quantidadeTotal }}</span>
                    </div>
                    <div class="resumo-final-item">
                      <span class="resumo-final-label">Valor Total Estimado:</span>
                      <span class="resumo-final-value valor-destaque">R$ {{ valorTotalEstimado }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Alertas de Validação -->
              <div class="validation-alerts" v-if="validationErrors.length > 0">
                <h4 class="alert-title">⚠️ Atenção</h4>
                <ul class="alert-list">
                  <li v-for="error in validationErrors" :key="error" class="alert-item">
                    {{ error }}
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </form>
      </div>

      <!-- Footer do Modal -->
      <div class="modal-footer">
        <div class="footer-navigation">
          <button
            type="button"
            @click="voltarTab"
            class="btn-secondary"
            :disabled="activeTab === 'dados'"
          >
            <svg viewBox="0 0 24 24" width="16" height="16">
              <path fill="currentColor" d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.42-1.41L7.83 13H20v-2z"/>
            </svg>
            Voltar
          </button>

          <div class="footer-actions">
            <button type="button" @click="fecharModal" class="btn-cancel">
              Cancelar
            </button>

            <button
              v-if="activeTab !== 'revisao'"
              type="button"
              @click="proximaTab"
              class="btn-next"
              :disabled="!podeProximaTab"
            >
              Continuar
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M4 11h12.17l-5.59-5.59L12 4l8 8-8 8-1.42-1.41L16.17 13H4v-2z"/>
              </svg>
            </button>

            <button
              v-if="activeTab === 'revisao'"
              type="button"
              @click="salvarPedido"
              class="btn-save"
              :disabled="!podeSerSalvo || isLoading"
            >
              <svg v-if="isLoading" class="loading-icon" viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M12 4V2A10 10 0 0 0 2 12h2a8 8 0 0 1 8-8z"/>
              </svg>
              <svg v-else viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
              {{ isLoading ? 'Salvando...' : (pedido?.id ? 'Atualizar' : 'Criar Pedido') }}
            </button>
          </div>
        </div>
      </div>
    </div>
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
    const activeTab = ref('dados')

    // Definição das tabs
    const tabs = ref([
      {
        id: 'dados',
        label: 'Dados Gerais',
        icon: '<path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>'
      },
      {
        id: 'itens',
        label: 'Itens',
        icon: '<path fill="currentColor" d="M19 7h-8l-2-2H5c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V9c0-1.1-.9-2-2-2z"/>'
      },
      {
        id: 'revisao',
        label: 'Revisão',
        icon: '<path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>'
      }
    ])

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

    const podeProximaTab = computed(() => {
      switch (activeTab.value) {
        case 'dados':
          return formData.value.requisitante?.trim() &&
                 formData.value.unidadeFuncional?.trim() &&
                 formData.value.objetivo?.trim() &&
                 formData.value.descricao?.trim()
        case 'itens':
          return formData.value.itens.length > 0 &&
                 formData.value.itens.every(item =>
                   item.produto?.trim() &&
                   item.quantidade > 0 &&
                   item.justificativa?.trim()
                 )
        default:
          return true
      }
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
        'rascunho': 'Rascunho',
        'pendente': 'Pendente',
        'aprovado': 'Aprovado',
        'rejeitado': 'Rejeitado',
        'cancelado': 'Cancelado'
      }
      return labels[status] || status || 'Indefinido'
    }

    const getStatusClass = (status) => {
      const classes = {
        'rascunho': 'status-draft',
        'pendente': 'status-pending',
        'aprovado': 'status-approved',
        'rejeitado': 'status-rejected',
        'cancelado': 'status-canceled'
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
      activeTab.value = 'dados'
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
          const lastCard = itemCards[itemCards.length - 1]
          lastCard.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
        }
      })
    }

    const removerItem = (index) => {
      if (formData.value.itens.length > 1) {
        formData.value.itens.splice(index, 1)
      }
    }

    const proximaTab = () => {
      const tabIndex = tabs.value.findIndex(tab => tab.id === activeTab.value)
      if (tabIndex < tabs.value.length - 1) {
        activeTab.value = tabs.value[tabIndex + 1].id
      }
    }

    const voltarTab = () => {
      const tabIndex = tabs.value.findIndex(tab => tab.id === activeTab.value)
      if (tabIndex > 0) {
        activeTab.value = tabs.value[tabIndex - 1].id
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

        // Preparar dados para envio
        const dadosParaEnvio = {
          ...formData.value,
          dataCriacao: props.pedido?.dataCriacao || new Date().toISOString(),
          dataUltimaAtualizacao: new Date().toISOString()
        }

        // Emitir evento para o componente pai
        emit('save', dadosParaEnvio)

      } catch (error) {
        console.error('Erro ao salvar pedido:', error)
        alert('Erro ao salvar pedido. Tente novamente.')
      } finally {
        isLoading.value = false
      }
    }

    // Watchers
    watch(() => props.isVisible, (novoValor) => {
      if (novoValor) {
        carregarDadosPedido()
        // Garantir que pelo menos um item existe
        if (formData.value.itens.length === 0) {
          adicionarItem()
        }
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
      activeTab,
      tabs,
      formData,

      // Computed
      totalItens,
      quantidadeTotal,
      valorTotalEstimado,
      validationErrors,
      podeProximaTab,
      podeSerSalvo,

      // Métodos
      formatarValor,
      getStatusLabel,
      getStatusClass,
      adicionarItem,
      removerItem,
      proximaTab,
      voltarTab,
      fecharModal,
      salvarPedido,
      resetarFormulario,
      carregarDadosPedido
    }
  }
}
</script>

<style scoped>
/* Modal Overlay */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.pedido-modal {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-width: 1000px;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  animation: modalIn 0.3s ease-out;
}

@keyframes modalIn {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

/* Modal Header */
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.header-content {
  flex: 1;
}

.modal-title {
  margin: 0 0 0.5rem 0;
  font-size: 1.75rem;
  font-weight: 700;
}

.modal-subtitle {
  margin: 0;
  font-size: 1rem;
  opacity: 0.9;
  font-weight: 400;
}

.close-button {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 50%;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-button:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

/* Modal Tabs */
.modal-tabs {
  display: flex;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.tab-button {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem 1.5rem;
  border: none;
  background: none;
  cursor: pointer;
  font-weight: 500;
  color: #718096;
  transition: all 0.3s ease;
  border-bottom: 3px solid transparent;
  position: relative;
}

.tab-button.active {
  color: #667eea;
  background: white;
  border-bottom-color: #667eea;
}

.tab-button:hover:not(.active) {
  background: #edf2f7;
  color: #4a5568;
}

.tab-icon {
  width: 20px;
  height: 20px;
  fill: currentColor;
}

/* Modal Body */
.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
}

.tab-content {
  animation: tabIn 0.3s ease-out;
}

@keyframes tabIn {
  from {
    opacity: 0;
    transform: translateX(10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* Form Sections */
.form-section {
  margin-bottom: 2rem;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin: 0 0 1.5rem 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #2d3748;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid #e2e8f0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.section-icon {
  width: 20px;
  height: 20px;
  fill: currentColor;
}

/* Form Grid */
.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

/* Form Groups */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group.full-width {
  grid-column: 1 / -1;
}

.form-label {
  font-weight: 600;
  color: #2d3748;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.required {
  color: #e53e3e;
  font-weight: 700;
}

.form-input,
.form-textarea {
  padding: 0.875rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: white;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
}

.form-help {
  font-size: 0.8rem;
  color: #718096;
  font-style: italic;
}

/* Itens Section */
.btn-add-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 0.75rem 1.25rem;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.btn-add-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.itens-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.item-card {
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  border-radius: 16px;
  padding: 1.5rem;
  transition: all 0.3s ease;
}

.item-card:hover {
  border-color: #cbd5e0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.item-numero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
}

.btn-remove-item {
  background: #fed7d7;
  color: #e53e3e;
  border: none;
  border-radius: 8px;
  padding: 0.5rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-remove-item:hover:not(:disabled) {
  background: #feb2b2;
  transform: scale(1.1);
}

.btn-remove-item:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.item-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.item-form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

/* Empty States */
.empty-itens {
  text-align: center;
  padding: 3rem 2rem;
  color: #718096;
}

.empty-icon {
  color: #cbd5e0;
  margin-bottom: 1rem;
}

.empty-itens h4 {
  margin: 0 0 0.5rem 0;
  color: #4a5568;
  font-size: 1.25rem;
}

.empty-itens p {
  margin: 0 0 2rem 0;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 0.875rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
}

/* Resumo */
.itens-resumo {
  background: #edf2f7;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 1.5rem;
  margin-top: 2rem;
}

.itens-resumo h4 {
  margin: 0 0 1rem 0;
  color: #2d3748;
  font-size: 1.1rem;
  font-weight: 600;
}

.resumo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.resumo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.resumo-label {
  font-weight: 500;
  color: #4a5568;
}

.resumo-value {
  font-weight: 600;
  color: #2d3748;
}

/* Revisão Section */
.revisao-section {
  margin-bottom: 2rem;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 1.5rem;
}

.revisao-title {
  margin: 0 0 1rem 0;
  color: #2d3748;
  font-size: 1.1rem;
  font-weight: 600;
}

.revisao-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
  margin-bottom: 1rem;
}

.revisao-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.revisao-item.full-width {
  grid-column: 1 / -1;
}

.revisao-label {
  font-weight: 600;
  color: #4a5568;
  font-size: 0.9rem;
}

.revisao-value {
  color: #2d3748;
  font-weight: 500;
}

.revisao-description {
  color: #2d3748;
  line-height: 1.6;
  margin: 0.5rem 0 0 0;
  background: white;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

/* Status Badges */
.status-badge {
  padding: 0.375rem 0.75rem;
  border-radius: 8px;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  display: inline-block;
}

.status-draft {
  background: #f7fafc;
  color: #4a5568;
  border: 1px solid #e2e8f0;
}

.status-pending {
  background: #fef5e7;
  color: #d69e2e;
  border: 1px solid #fbb047;
}

.status-approved {
  background: #f0fff4;
  color: #38a169;
  border: 1px solid #68d391;
}

.status-rejected {
  background: #fed7d7;
  color: #e53e3e;
  border: 1px solid #fc8181;
}

.status-canceled {
  background: #edf2f7;
  color: #718096;
  border: 1px solid #cbd5e0;
}

/* Revisão Itens */
.revisao-itens {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.revisao-item-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 1.5rem;
}

.revisao-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.item-name {
  margin: 0 0 1rem 0;
  color: #2d3748;
  font-size: 1.1rem;
  font-weight: 600;
}

.item-spec,
.item-justification,
.item-observation,
.item-value {
  margin: 0.75rem 0;
  color: #4a5568;
  line-height: 1.6;
}

.item-spec strong,
.item-justification strong,
.item-observation strong,
.item-value strong {
  color: #2d3748;
}

/* Resumo Final */
.resumo-final {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  padding: 1.5rem;
  margin-top: 1.5rem;
}

.resumo-final-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.resumo-final-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.resumo-final-label {
  font-weight: 500;
  opacity: 0.9;
}

.resumo-final-value {
  font-weight: 600;
  font-size: 1.1rem;
}

.valor-destaque {
  font-size: 1.25rem !important;
  font-weight: 700 !important;
}

/* Validation Alerts */
.validation-alerts {
  background: #fed7d7;
  border: 1px solid #fc8181;
  border-radius: 12px;
  padding: 1.5rem;
  margin-top: 1.5rem;
}

.alert-title {
  margin: 0 0 1rem 0;
  color: #c53030;
  font-size: 1.1rem;
  font-weight: 600;
}

.alert-list {
  margin: 0;
  padding-left: 1.5rem;
}

.alert-item {
  color: #c53030;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.empty-message {
  text-align: center;
  color: #718096;
  font-style: italic;
  padding: 2rem;
}

/* Modal Footer */
.modal-footer {
  padding: 1.5rem 2rem;
  border-top: 1px solid #e2e8f0;
  background: #f8fafc;
}

.footer-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.footer-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

/* Botões */
.btn-secondary {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: #edf2f7;
  color: #4a5568;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  padding: 0.75rem 1.25rem;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary:hover:not(:disabled) {
  background: #e2e8f0;
  border-color: #cbd5e0;
}

.btn-secondary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-cancel {
  background: white;
  color: #718096;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  padding: 0.75rem 1.25rem;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
  color: #4a5568;
}

.btn-next {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 0.75rem 1.25rem;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.btn-next:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.btn-next:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.2);
}

.btn-save {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #38a169 0%, #2f855a 100%);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 0.75rem 1.25rem;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(56, 161, 105, 0.3);
}

.btn-save:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(56, 161, 105, 0.4);
}

.btn-save:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: 0 4px 15px rgba(56, 161, 105, 0.2);
}

/* Loading Icon */
.loading-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Responsividade */
@media (max-width: 768px) {
  .modal-overlay {
    padding: 0.5rem;
  }

  .pedido-modal {
    max-height: 95vh;
  }

  .modal-header {
    padding: 1.5rem;
  }

  .modal-title {
    font-size: 1.5rem;
  }

  .modal-tabs {
    overflow-x: auto;
  }

  .tab-button {
    min-width: 120px;
    white-space: nowrap;
  }

  .modal-body {
    padding: 1.5rem;
  }

  .form-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .item-form-grid {
    grid-template-columns: 1fr;
  }

  .resumo-grid,
  .resumo-final-grid,
  .revisao-grid {
    grid-template-columns: 1fr;
  }

  .footer-navigation {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
  }

  .footer-actions {
    justify-content: space-between;
  }

  .section-header {
    flex-direction: column;
    align-items: stretch;
  }
}

@media (max-width: 640px) {
  .modal-header {
    padding: 1rem;
  }

  .modal-body {
    padding: 1rem;
  }

  .modal-footer {
    padding: 1rem;
  }

  .item-card {
    padding: 1rem;
  }

  .revisao-section {
    padding: 1rem;
  }

  .footer-actions {
    flex-direction: column;
    gap: 0.75rem;
  }

  .btn-secondary,
  .btn-cancel,
  .btn-next,
  .btn-save {
    width: 100%;
    justify-content: center;
  }
}
</style>
