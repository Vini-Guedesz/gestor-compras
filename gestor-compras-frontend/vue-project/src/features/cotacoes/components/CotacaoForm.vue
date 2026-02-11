<template>
  <Transition name="modal" appear>
    <div v-if="isVisible" class="modal-overlay" @click.self="$emit('close')">
      <div class="modal-container">
        <div class="modal-header">
          <h2 class="modal-title">{{ cotacao ? 'Editar Cotação' : 'Nova Cotação' }}</h2>
          <button @click="$emit('close')" class="close-button">&times;</button>
        </div>
        <div class="modal-body">
          <!-- Alertas -->
          <div v-if="mensagemAlerta" :class="['alert', tipoAlerta]">
            {{ mensagemAlerta }}
          </div>

          <form @submit.prevent="handleSubmit">
            <!-- Dados da Cotação -->
            <div class="form-section">
              <h3 class="section-title">Dados da Cotação</h3>
              <div class="form-grid">
                <!-- Tipo de Fornecedor -->
                <div class="form-group">
                  <label class="form-label">Tipo de Fornecedor *</label>
                  <select v-model="tipoFornecedor" class="form-select" required @change="handleTipoFornecedorChange">
                    <option value="">Selecione o tipo...</option>
                    <option value="PRODUTO">Fornecedor de Produto</option>
                    <option value="SERVICO">Fornecedor de Serviço</option>
                  </select>
                </div>

                <!-- Fornecedor com pesquisa integrada -->
                <div class="form-group">
                  <label class="form-label">Fornecedor *</label>
                  <div class="custom-select-wrapper" :class="{ disabled: !tipoFornecedor }">
                    <div class="custom-select">
                      <input
                        v-model="pesquisaFornecedor"
                        type="text"
                        class="custom-select-input"
                        :placeholder="tipoFornecedor ? 'Pesquisar fornecedor...' : 'Primeiro selecione o tipo'"
                        @focus="openDropdown('fornecedor')"
                        @input="openDropdown('fornecedor')"
                        :disabled="!tipoFornecedor"
                      />
                      <svg class="dropdown-icon" viewBox="0 0 24 24" width="20" height="20" @click="!tipoFornecedor ? null : toggleDropdown('fornecedor')">
                        <path fill="currentColor" d="M7 10l5 5 5-5z"/>
                      </svg>
                    </div>
                    <div v-if="dropdownAberto === 'fornecedor'" class="dropdown-list">
                      <div
                        v-for="fornecedor in fornecedoresFiltradosComPesquisa"
                        :key="fornecedor.id"
                        class="dropdown-item"
                        @click="selecionarFornecedor(fornecedor)"
                      >
                        <div class="item-main">{{ fornecedor.razaoSocial }}</div>
                        <div class="item-secondary">{{ fornecedor.cnpj }} - {{ fornecedor.tipo }}</div>
                      </div>
                      <div v-if="fornecedoresFiltradosComPesquisa.length === 0" class="dropdown-empty">
                        Nenhum fornecedor encontrado
                      </div>
                    </div>
                  </div>
                  <small class="form-hint">{{ fornecedorSelecionadoNome || (fornecedoresFiltradosComPesquisa.length + ' fornecedores disponíveis') }}</small>
                </div>

                <!-- Pedido com pesquisa integrada -->
                <div class="form-group">
                  <label class="form-label">Pedido *</label>
                  <div class="custom-select-wrapper">
                    <div class="custom-select">
                      <input
                        v-model="pesquisaPedido"
                        type="text"
                        class="custom-select-input"
                        placeholder="Pesquisar pedido..."
                        @focus="openDropdown('pedido')"
                        @input="openDropdown('pedido')"
                      />
                      <svg class="dropdown-icon" viewBox="0 0 24 24" width="20" height="20" @click="toggleDropdown('pedido')">
                        <path fill="currentColor" d="M7 10l5 5 5-5z"/>
                      </svg>
                    </div>
                    <div v-if="dropdownAberto === 'pedido'" class="dropdown-list">
                      <div
                        v-for="pedido in pedidosFiltrados"
                        :key="pedido.id"
                        class="dropdown-item"
                        @click="selecionarPedido(pedido)"
                      >
                        <div class="item-main">Pedido #{{ pedido.id }}</div>
                        <div class="item-secondary">{{ getStatusLabel(pedido.status) }} - {{ pedido.itens?.length || 0 }} itens</div>
                      </div>
                      <div v-if="pedidosFiltrados.length === 0" class="dropdown-empty">
                        Nenhum pedido encontrado
                      </div>
                    </div>
                  </div>
                  <small class="form-hint">{{ pedidoSelecionadoLabel || (pedidosFiltrados.length + ' pedidos disponíveis') }}</small>
                </div>

                <!-- Lista de Itens do Pedido (Seleção Múltipla) -->
                <div class="form-group" v-if="pedidoSelecionado">
                  <div class="section-header">
                    <label class="form-label">Itens do Pedido *</label>
                    <button 
                      type="button" 
                      @click="toggleSelecionarTodos" 
                      class="btn-text-action"
                      v-if="itensFiltradosPorPedido.length > 0"
                    >
                      {{ todosItensMarcados ? 'Desmarcar Todos' : 'Selecionar Todos' }}
                    </button>
                  </div>
                  <div class="itens-selecao-container">
                    <div v-if="itensFiltradosPorPedido.length === 0" class="empty-items">
                      Nenhum item disponível neste pedido.
                    </div>
                    <div v-else class="itens-list">
                      <div v-for="item in itensFiltradosPorPedido" :key="item.id" class="item-checkbox-row">
                        <div class="checkbox-wrapper">
                          <input
                            type="checkbox"
                            :id="`item-${item.id}`"
                            :value="item.id"
                            v-model="formData.itensSelecionadosIds"
                            class="form-checkbox"
                          />
                          <label :for="`item-${item.id}`" class="item-label">
                            <span class="item-name">{{ item.nome }}</span>
                            <span class="item-qtd"> (Qtd: {{ item.quantidade }})</span>
                          </label>
                        </div>
                        
                        <!-- Preço Unitário Opcional -->
                        <div class="item-price-input" v-if="formData.itensSelecionadosIds.includes(item.id)">
                          <span class="currency-symbol">R$</span>
                          <input
                            type="text"
                            v-model="formData.precosUnitarios[item.id]"
                            @input="(e) => formatarPrecoUnitario(e, item.id)"
                            placeholder="Unitário (Opcional)"
                            class="form-input-small"
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                  <small class="form-hint">Selecione os itens incluídos nesta cotação.</small>
                </div>

                <!-- Preço Total -->
                <div class="form-group">
                  <label class="form-label">Preço Total da Cotação (R$) *</label>
                  <input
                    type="text"
                    v-model="precoFormatado"
                    @input="formatarPrecoInput"
                    class="form-input"
                    required
                    placeholder="0,00"
                    inputmode="numeric"
                  />
                  <small class="form-hint">Valor total da proposta (obrigatório).</small>
                </div>

                <!-- Prazo de Entrega -->
                <div class="form-group">
                  <label class="form-label">Prazo de Entrega (Dias Úteis)</label>
                  <input
                    type="number"
                    v-model.number="formData.prazoEmDiasUteis"
                    class="form-input"
                    min="1"
                    placeholder="Ex: 20"
                  />
                  <small class="form-hint">
                    Prazo para entrega do produto/serviço em dias úteis
                  </small>
                </div>

                <!-- Validade da Cotação -->
                <div class="form-group">
                  <label class="form-label">Validade da Cotação</label>
                  <input
                    type="date"
                    v-model="formData.dataLimite"
                    class="form-input"
                  />
                  <small class="form-hint">
                    Data limite de vencimento desta cotação (até quando o preço é válido)
                  </small>
                </div>

                <!-- Gasto Previsto -->
                <div class="form-group">
                  <label class="form-label checkbox-label">
                    <input
                      type="checkbox"
                      v-model="formData.gastoPrevisto"
                      class="form-checkbox"
                    />
                    Gasto Previsto no Orçamento
                  </label>
                  <small class="form-hint">
                    Marque se este gasto já estava previsto no orçamento
                  </small>
                </div>

                <!-- Projeto (apenas se gasto previsto) -->
                <div v-if="formData.gastoPrevisto" class="form-group">
                  <label class="form-label">Projeto</label>
                  <input
                    type="text"
                    v-model="formData.projeto"
                    class="form-input"
                    placeholder="Nome do projeto (ex: Projeto Expansão 2025)"
                    maxlength="255"
                  />
                  <small class="form-hint">
                    Informe o nome do projeto ao qual este gasto pertence
                  </small>
                </div>

                <!-- Upload de PDF -->
                <div class="form-group">
                  <label class="form-label">PDF da Cotação</label>
                  <div class="file-upload-wrapper">
                    <input
                      type="file"
                      ref="fileInput"
                      @change="handleFileUpload"
                      accept="application/pdf"
                      class="file-input"
                      id="pdf-upload"
                    />
                    <label for="pdf-upload" class="file-label">
                      <svg class="upload-icon" viewBox="0 0 24 24" width="20" height="20">
                        <path fill="currentColor" d="M9 16v-6H5l7-7 7 7h-4v6H9zm-4 2h14v2H5v-2z"/>
                      </svg>
                      <span v-if="!arquivoPdf">Escolher arquivo PDF</span>
                      <span v-else class="file-selected">{{ arquivoPdf.name }}</span>
                    </label>
                  </div>
                  <small class="form-hint">
                    Faça upload do PDF com a cotação do fornecedor (opcional)
                  </small>
                  <div v-if="arquivoPdf" class="file-info">
                    <span class="file-name">📄 {{ arquivoPdf.name }}</span>
                    <span class="file-size">({{ formatarTamanhoArquivo(arquivoPdf.size) }})</span>
                    <button type="button" @click="removerArquivo" class="btn-remove-file">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z"/>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>


          </form>
        </div>

        <!-- Footer do Modal -->
        <div class="modal-footer">
          <button type="button" @click="$emit('close')" class="btn-secondary">
            Cancelar
          </button>
          <div class="footer-actions">
            <button
              type="button"
              @click="gerarRelatorio"
              class="btn-relatorio"
              :disabled="!pedidoSelecionado || gerandoRelatorio"
              title="Gerar relatório de itens para enviar aos fornecedores"
            >
              <svg v-if="!gerandoRelatorio" class="icon" viewBox="0 0 24 24" width="18" height="18">
                <path fill="currentColor" d="M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z"/>
              </svg>
              <span v-if="gerandoRelatorio" class="loading-spinner"></span>
              Gerar Relatório
            </button>
            <button
              type="button"
              @click.stop="handleSubmit"
              class="btn-primary"
              :disabled="carregando || !formularioValido"
            >
              <span v-if="carregando" class="loading-spinner"></span>
              {{ cotacao ? 'Atualizar Cotação' : 'Criar Cotação' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import fornecedorService from '@/services/fornecedorService.js'
import itemPedidoService from '@/services/itemPedidoService.js'
import pedidoService from '@/services/pedidoService.js'
import relatorioService from '@/services/relatorioService.js'
import logger from '@/utils/logger.js'

// Props
const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  cotacao: {
    type: Object,
    default: null
  }
})

// Emits
const emit = defineEmits(['close', 'save'])

// Estado reativo
const carregando = ref(false)
const gerandoRelatorio = ref(false)
const mensagemAlerta = ref('')
const tipoAlerta = ref('error') // 'error', 'success', 'warning'
const tipoFornecedor = ref('')
const pedidoSelecionado = ref('')
const dropdownAberto = ref(null) // Controla qual dropdown está aberto

// Campos de pesquisa
const pesquisaFornecedor = ref('')
const pesquisaPedido = ref('')
const pesquisaItem = ref('')

// Labels dos itens selecionados
const fornecedorSelecionadoNome = ref('')
const pedidoSelecionadoLabel = ref('')
const itemSelecionadoNome = ref('')

// Campo de preço formatado
const precoFormatado = ref('')

// Arquivo PDF
const arquivoPdf = ref(null)
const fileInput = ref(null)

// Dados do formulário - alinhado com CotacaoCreateDTO/CotacaoUpdateDTO
const formData = ref({
  fornecedorId: null,
  itensSelecionadosIds: [], // Array de IDs
  precosUnitarios: {}, // Mapa { itemId: valorFormatado }
  preco: null,
  prazoEmDiasUteis: null,
  dataLimite: null,
  arquivoPdf: null,
  gastoPrevisto: false,
  projeto: null
})

// Lista de fornecedores, itens e pedidos
const fornecedoresDisponiveis = ref([])
const itensDisponiveis = ref([])
const pedidosDisponiveis = ref([])

// Computadas
const fornecedoresFiltrados = computed(() => {
  if (!tipoFornecedor.value) return []
  return fornecedoresDisponiveis.value.filter(f => f.tipo === tipoFornecedor.value)
})

// Fornecedores filtrados por tipo E pesquisa
const fornecedoresFiltradosComPesquisa = computed(() => {
  let resultado = fornecedoresFiltrados.value

  if (pesquisaFornecedor.value.trim()) {
    const query = pesquisaFornecedor.value.toLowerCase().trim()
    resultado = resultado.filter(f =>
      f.razaoSocial?.toLowerCase().includes(query) ||
      f.cnpj?.toLowerCase().includes(query)
    )
  }

  return resultado
})

// Pedidos filtrados por pesquisa
const pedidosFiltrados = computed(() => {
  let resultado = pedidosDisponiveis.value

  if (pesquisaPedido.value.trim()) {
    const query = pesquisaPedido.value.toLowerCase().trim()
    resultado = resultado.filter(p =>
      p.id?.toString().includes(query) ||
      p.observacao?.toLowerCase().includes(query)
    )
  }

  return resultado
})

// Itens filtrados pelo pedido selecionado
const itensFiltradosPorPedido = computed(() => {
  if (!pedidoSelecionado.value) {
    return []
  }

  // Buscar o pedido selecionado
  const pedido = pedidosDisponiveis.value.find(p => p.id === parseInt(pedidoSelecionado.value))

  if (!pedido) {
    logger.warn('Pedido não encontrado:', pedidoSelecionado.value)
    return []
  }

  // Retornar os itens do pedido
  return pedido.itens || []
})

const todosItensMarcados = computed(() => {
  return itensFiltradosPorPedido.value.length > 0 && 
         formData.value.itensSelecionadosIds.length === itensFiltradosPorPedido.value.length
})

const toggleSelecionarTodos = () => {
  if (todosItensMarcados.value) {
    formData.value.itensSelecionadosIds = []
  } else {
    formData.value.itensSelecionadosIds = itensFiltradosPorPedido.value.map(i => i.id)
  }
}

const formularioValido = computed(() => {
  return formData.value.fornecedorId &&
         formData.value.itensSelecionadosIds.length > 0 && // Pelo menos um item
         formData.value.preco &&
         formData.value.preco > 0 &&
         pedidoSelecionado.value
})

// Métodos de formatação de preço
const formatarPrecoInput = (event) => {
  let valor = event.target.value.replace(/\D/g, '') // Remove tudo que não é dígito

  if (!valor) {
    precoFormatado.value = ''
    formData.value.preco = null
    return
  }

  // Converte para número e divide por 100 para ter os centavos
  const numero = parseInt(valor) / 100

  // Formata com 2 casas decimais
  precoFormatado.value = numero.toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })

  // Atualiza o valor numérico no formData
  formData.value.preco = numero
}

const formatarPrecoUnitario = (event, itemId) => {
  let valor = event.target.value.replace(/\D/g, '')
  
  if (!valor) {
    formData.value.precosUnitarios[itemId] = ''
    return
  }

  const numero = parseInt(valor) / 100
  formData.value.precosUnitarios[itemId] = numero.toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const parsePrecoBR = (valorFormatado) => {
  if (!valorFormatado) return 0;
  return parseFloat(valorFormatado.replace(/\./g, '').replace(',', '.'));
}

const formatarPrecoExistente = (preco) => {
  if (!preco && preco !== 0) {
    precoFormatado.value = ''
    return
  }

  const numero = parseFloat(preco)
  precoFormatado.value = numero.toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// Métodos de manipulação de arquivo
const handleFileUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    // Validar tipo de arquivo
    if (file.type !== 'application/pdf') {
      mostrarAlerta('Por favor, selecione apenas arquivos PDF', 'error')
      event.target.value = ''
      return
    }

    // Validar tamanho (máximo 10MB)
    const maxSize = 10 * 1024 * 1024 // 10MB em bytes
    if (file.size > maxSize) {
      mostrarAlerta('O arquivo deve ter no máximo 10MB', 'error')
      event.target.value = ''
      return
    }

    arquivoPdf.value = file
    formData.value.arquivoPdf = file
  }
}

const removerArquivo = () => {
  arquivoPdf.value = null
  formData.value.arquivoPdf = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const formatarTamanhoArquivo = (bytes) => {
  if (bytes === 0) return '0 Bytes'

  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))

  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}

// Métodos de UI
const mostrarAlerta = (mensagem, tipo = 'error') => {
  mensagemAlerta.value = mensagem
  tipoAlerta.value = tipo
  setTimeout(() => {
    mensagemAlerta.value = ''
  }, 5000)
}


// Métodos de fornecedor
const handleTipoFornecedorChange = () => {
  formData.value.fornecedorId = null // Reset fornecedor quando tipo muda
  fornecedorSelecionadoNome.value = ''
  pesquisaFornecedor.value = ''
}

// Métodos para controlar dropdowns
const toggleDropdown = (tipo) => {
  if (dropdownAberto.value === tipo) {
    dropdownAberto.value = null
  } else {
    dropdownAberto.value = tipo
  }
}

const openDropdown = (tipo) => {
  dropdownAberto.value = tipo
}

const closeDropdown = () => {
  dropdownAberto.value = null
}

// Métodos de seleção
const selecionarFornecedor = (fornecedor) => {
  formData.value.fornecedorId = fornecedor.id
  fornecedorSelecionadoNome.value = fornecedor.razaoSocial
  pesquisaFornecedor.value = fornecedor.id.toString() // Apenas o número do ID
  closeDropdown()
}

const selecionarPedido = (pedido) => {

  pedidoSelecionado.value = pedido.id
  pedidoSelecionadoLabel.value = `Pedido #${pedido.id} - ${getStatusLabel(pedido.status)}`
  pesquisaPedido.value = pedido.id.toString() // Apenas o número do ID

  // Limpar seleção anterior
  formData.value.itensSelecionadosIds = []
  formData.value.precosUnitarios = {}

  closeDropdown()
}

// Método para obter label do status
const getStatusLabel = (status) => {
  const labels = {
    'RASCUNHO': 'Rascunho',
    'PENDENTE': 'Pendente',
    'EM_ANALISE': 'Em Análise',
    'EM_ANDAMENTO': 'Em Andamento',
    'APROVADO': 'Aprovado',
    'CANCELADO': 'Cancelado'
  }
  return labels[status] || status || 'Indefinido'
}



const gerarRelatorio = async () => {
  if (!pedidoSelecionado.value) {
    mostrarAlerta('Selecione um pedido primeiro', 'warning')
    return
  }

  try {
    gerandoRelatorio.value = true

    // Enviar todos os IDs selecionados
    const itensIds = formData.value.itensSelecionadosIds

    await relatorioService.visualizarRelatorioItensParaCotacao(
      parseInt(pedidoSelecionado.value),
      itensIds
    )

    mostrarAlerta('Relatório gerado com sucesso!', 'success')
  } catch (error) {
    logger.error('❌ Erro ao gerar relatório:', error)
    mostrarAlerta('Erro ao gerar relatório. Tente novamente.', 'error')
  } finally {
    gerandoRelatorio.value = false
  }
}

const handleSubmit = async () => {
  if (carregando.value) {
    return
  }

  try {
    carregando.value = true
    mensagemAlerta.value = ''

    // Validações do formulário
    if (!formularioValido.value) {
      mostrarAlerta('Por favor, preencha todos os campos obrigatórios e selecione pelo menos um item', 'error')
      return
    }

    if (!tipoFornecedor.value) {
      mostrarAlerta('Por favor, selecione um tipo de fornecedor', 'error')
      return
    }

    // Montar lista de itens
    const itens = formData.value.itensSelecionadosIds.map(itemId => {
      const precoUnitarioStr = formData.value.precosUnitarios[itemId];
      const precoUnitario = parsePrecoBR(precoUnitarioStr);
      
      // Encontrar quantidade original do item
      const itemOriginal = itensFiltradosPorPedido.value.find(i => i.id === itemId);
      const quantidade = itemOriginal ? itemOriginal.quantidade : 1;

      return {
        itemPedidoId: itemId,
        precoUnitario: precoUnitario > 0 ? precoUnitario : 0, // Se não preenchido, envia 0
        quantidade: quantidade,
        observacao: null
      };
    });

    const dadosParaSalvar = {
      fornecedorId: parseInt(formData.value.fornecedorId),
      tipoFornecedor: tipoFornecedor.value,
      solicitacaoDePedidoId: parseInt(pedidoSelecionado.value),
      preco: parseFloat(formData.value.preco),
      itens: itens, // Envia lista detalhada
      prazoEmDiasUteis: formData.value.prazoEmDiasUteis ? parseInt(formData.value.prazoEmDiasUteis) : null,
      dataLimite: formData.value.dataLimite || null,
      arquivoPdf: formData.value.arquivoPdf || null,
      gastoPrevisto: formData.value.gastoPrevisto || false,
      projeto: formData.value.projeto || null
    }

    // Validar conversões
    if (isNaN(dadosParaSalvar.fornecedorId) || dadosParaSalvar.fornecedorId <= 0) {
      mostrarAlerta('Fornecedor inválido', 'error')
      return
    }

    if (isNaN(dadosParaSalvar.preco) || dadosParaSalvar.preco <= 0) {
      mostrarAlerta('Preço inválido', 'error')
      return
    }


    // Emitir os dados para a view pai processar
    emit('save', dadosParaSalvar)

    mostrarAlerta('Cotação enviada com sucesso!', 'success')

    // Aguardar um pouco para mostrar a mensagem antes de fechar
    setTimeout(() => {
      emit('close')
    }, 1000)

  } catch (error) {
    logger.error('❌ Erro ao processar cotação:', error)
    mostrarAlerta('Erro interno ao processar cotação', 'error')
  } finally {
    carregando.value = false
  }
}

// Carregar dados
const carregarFornecedores = async () => {
  try {
    const fornecedores = await fornecedorService.listarParaCotacao()
    fornecedoresDisponiveis.value = fornecedores || []

    if (!fornecedores || fornecedores.length === 0) {
      mostrarAlerta('Nenhum fornecedor encontrado no sistema', 'warning')
    }
  } catch (error) {
    logger.error('❌ Erro ao carregar fornecedores:', error)
    fornecedoresDisponiveis.value = []
    mostrarAlerta('Erro ao carregar fornecedores. Verifique a conexão.', 'error')
  }
}

const carregarItens = async () => {
  try {
    const itens = await itemPedidoService.listarTodos()
    itensDisponiveis.value = itens || []

    if (!itens || itens.length === 0) {
      mostrarAlerta('Nenhum item de pedido encontrado', 'warning')
    }
  } catch (error) {
    logger.error('❌ Erro ao carregar itens:', error)
    itensDisponiveis.value = []
    mostrarAlerta('Erro ao carregar itens. Verifique a conexão.', 'error')
  }
}

const carregarPedidos = async () => {
  try {
    const response = await pedidoService.listarTodos()

    // Processar pedidos - pode vir como array direto ou dentro de response.data
    const pedidos = Array.isArray(response) ? response : (response?.data || [])

    // Verificar se os pedidos já vêm com itens
    if (pedidos.length > 0 && pedidos[0].itens) {
      pedidosDisponiveis.value = pedidos
    } else {
      // Caso contrário, carregar itens separadamente e associar
      const todosItens = itensDisponiveis.value.length > 0
        ? itensDisponiveis.value
        : await itemPedidoService.listarTodos()


      // Associar itens aos pedidos
      pedidosDisponiveis.value = pedidos.map(pedido => {
        const itensDoPedido = todosItens.filter(item => {
          // Tentar diferentes propriedades que podem indicar o pedido
          const pedidoId = item.solicitacaoDePedido?.id || item.solicitacaoDePedidoId || item.pedidoId
          const match = pedidoId === pedido.id
          if (match) {
          }
          return match
        })


        return {
          ...pedido,
          itens: itensDoPedido
        }
      })
    }


    if (pedidosDisponiveis.value.length === 0) {
      mostrarAlerta('Nenhum pedido encontrado', 'warning')
    }
  } catch (error) {
    logger.error('❌ Erro ao carregar pedidos:', error)
    pedidosDisponiveis.value = []
    mostrarAlerta('Erro ao carregar pedidos. Verifique a conexão.', 'error')
  }
}

const inicializarFormulario = async () => {
  if (props.cotacao) {
    // Editando cotação existente

    // Preencher IDs dos itens selecionados
    // Se cotacao.itens (novo formato) existir, usa ele. Se não, usa itensPedidoIds (legacy)
    const idsSelecionados = props.cotacao.itens 
        ? props.cotacao.itens.map(i => i.itemPedidoId)
        : (props.cotacao.itensPedidoIds || []);

    // Preencher preços unitários se existirem
    const precosMap = {};
    if (props.cotacao.itens) {
        props.cotacao.itens.forEach(i => {
            if (i.precoTotal > 0) {
                // Calcular unitário (total / quantidade) ou usar direto se disponível
                const unitario = i.precoUnitario || (i.quantidade > 0 ? i.precoTotal / i.quantidade : 0);
                precosMap[i.itemPedidoId] = unitario.toLocaleString('pt-BR', {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2
                });
            }
        });
    }

    formData.value = {
      fornecedorId: props.cotacao.fornecedorId,
      itensSelecionadosIds: idsSelecionados,
      precosUnitarios: precosMap,
      preco: props.cotacao.preco,
      prazoEmDiasUteis: props.cotacao.prazoEmDiasUteis,
      dataLimite: props.cotacao.dataLimite,
      gastoPrevisto: props.cotacao.gastoPrevisto || false,
      projeto: props.cotacao.projeto || null
    }

    // Formatar preço existente
    formatarPrecoExistente(props.cotacao.preco)

    tipoFornecedor.value = props.cotacao.tipoFornecedor || ''

    // Preencher o fornecedor selecionado
    const fornecedor = fornecedoresDisponiveis.value.find(f =>
      f.id === props.cotacao.fornecedorId &&
      (f.tipo === props.cotacao.tipoFornecedor || f.tipo === props.cotacao.tipoFornecedor?.toLowerCase())
    )
    if (fornecedor) {
      fornecedorSelecionadoNome.value = fornecedor.razaoSocial
      pesquisaFornecedor.value = fornecedor.id.toString() // Apenas o número do ID
    }

    // Preencher o pedido selecionado - precisa encontrar qual pedido contém um dos itens
    const primeiroItemId = idsSelecionados[0];
    const pedido = pedidosDisponiveis.value.find(p =>
      p.itens?.some(item => item.id === primeiroItemId)
    )
    if (pedido) {
      pedidoSelecionado.value = pedido.id
      pedidoSelecionadoLabel.value = `Pedido #${pedido.id} - ${getStatusLabel(pedido.status)}`
      pesquisaPedido.value = pedido.id.toString()
    }
  } else {
    // Nova cotação - resetar tudo
    formData.value = {
      fornecedorId: null,
      itensSelecionadosIds: [],
      precosUnitarios: {},
      preco: null,
      prazoEmDiasUteis: null,
      dataLimite: null,
      arquivoPdf: null,
      gastoPrevisto: false,
      projeto: null
    }
    tipoFornecedor.value = ''
    pedidoSelecionado.value = ''

    // Limpar labels e pesquisas
    fornecedorSelecionadoNome.value = ''
    pedidoSelecionadoLabel.value = ''
    pesquisaFornecedor.value = ''
    pesquisaPedido.value = ''
    pesquisaItem.value = ''
    precoFormatado.value = ''

    // Limpar arquivo
    arquivoPdf.value = null
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }
}

// Lifecycle
onMounted(async () => {
  await Promise.all([carregarFornecedores(), carregarItens(), carregarPedidos()])
  inicializarFormulario()

  // Adicionar listener para fechar dropdown ao clicar fora
  document.addEventListener('click', (e) => {
    if (!e.target.closest('.custom-select-wrapper')) {
      closeDropdown()
    }
  })
})

// Watchers
watch(() => props.isVisible, (newVal) => {
  if (newVal) {
    inicializarFormulario()
    mensagemAlerta.value = ''
  }
})

watch(() => props.cotacao, () => {
  if (props.isVisible) {
    inicializarFormulario()
  }
})
</script>

<style scoped>
/* Modal Base */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
  overflow-y: auto;
}

.modal-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  width: 100%;
  max-width: 800px;
  max-height: calc(100vh - 40px);
  margin: 20px auto;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 24px;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
  outline: none;
  line-height: 1;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-button:hover {
  background: #f3f4f6;
  color: #374151;
}

.close-button:focus {
  outline: none;
  box-shadow: 0 0 0 2px #e5e7eb;
}

.modal-body {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
  border-radius: 0 0 12px 12px;
}

.footer-actions {
  display: flex;
  gap: 12px;
}

/* Form Sections */
.form-section {
  margin-bottom: 32px;
}

.form-subsection {
  margin-bottom: 24px;
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #111827;
  margin: 0 0 16px 0;
}

.subsection-title {
  font-size: 1rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 12px 0;
}

.btn-text-action {
  background: none;
  border: none;
  color: #3b82f6;
  font-size: 0.75rem;
  font-weight: 600;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s;
  text-transform: uppercase;
}

.btn-text-action:hover {
  background-color: #eff6ff;
  color: #2563eb;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

/* Form Elements */
.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.search-grid {
  background: #f8fafc;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

/* Custom Select com Pesquisa Integrada */
.custom-select-wrapper {
  position: relative;
}

.custom-select-wrapper.disabled {
  opacity: 0.6;
  pointer-events: none;
}

.custom-select {
  position: relative;
  cursor: pointer;
}

.custom-select-input {
  width: 100%;
  padding: 12px 40px 12px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
  background: white;
  cursor: pointer;
}

.custom-select-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  cursor: text;
}

.custom-select-input:disabled {
  background: #f3f4f6;
  cursor: not-allowed;
}

.dropdown-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #6b7280;
  cursor: pointer;
}

.dropdown-list {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  right: 0;
  max-height: 300px;
  overflow-y: auto;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  z-index: 1000;
}

.dropdown-item {
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 1px solid #f3f4f6;
}

.dropdown-item:last-child {
  border-bottom: none;
}

.dropdown-item:hover {
  background: #f8fafc;
}

.dropdown-item .item-main {
  font-size: 0.875rem;
  font-weight: 500;
  color: #111827;
  margin-bottom: 4px;
}

.dropdown-item .item-secondary {
  font-size: 0.75rem;
  color: #6b7280;
}

.dropdown-empty {
  padding: 24px;
  text-align: center;
  color: #9ca3af;
  font-size: 0.875rem;
  font-style: italic;
}

.form-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
}

.form-input,
.form-select,
.form-textarea {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  font-family: inherit;
  transition: all 0.2s;
}

/* Garantir fonte Arial nos inputs de data */
.form-input[type="date"] {
  font-family: inherit;
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

.field-help {
  font-size: 0.75rem;
  color: #6b7280;
  margin-top: 4px;
}

/* Checkboxes */
.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* Itens Seleção */
.itens-selecao-container {
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background: white;
  max-height: 300px;
  overflow-y: auto;
}

.itens-list {
  display: flex;
  flex-direction: column;
}

.item-checkbox-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-bottom: 1px solid #f3f4f6;
  transition: background-color 0.2s;
}

.item-checkbox-row:last-child {
  border-bottom: none;
}

.item-checkbox-row:hover {
  background-color: #f9fafb;
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
}

.form-checkbox {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #3b82f6;
}

.item-label {
  cursor: pointer;
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.item-name {
  font-weight: 500;
  color: #374151;
  font-size: 0.9rem;
}

.item-qtd {
  font-size: 0.75rem;
  color: #6b7280;
}

.item-price-input {
  display: flex;
  align-items: center;
  gap: 4px;
  width: 140px;
}

.currency-symbol {
  font-size: 0.8rem;
  color: #6b7280;
}

.form-input-small {
  width: 100%;
  padding: 6px 8px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 0.85rem;
  text-align: right;
}

.form-input-small:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.empty-items {
  padding: 20px;
  text-align: center;
  color: #9ca3af;
  font-style: italic;
  font-size: 0.875rem;
}

.checkbox-option {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.checkbox-option input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.checkbox-label {
  font-size: 0.875rem;
  color: #374151;
  cursor: pointer;
}



.upload-help {
  font-size: 0.75rem;
  color: #6b7280;
}

.anexos-lista {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.anexo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
}

.anexo-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.anexo-nome {
  font-weight: 500;
  color: #374151;
}

.anexo-tamanho {
  font-size: 0.75rem;
  color: #6b7280;
}

.btn-remove-anexo {
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.btn-remove-anexo:hover {
  background: #fee2e2;
  color: #dc2626;
}

/* Warning Messages */
.warning-message {
  background: #fef3c7;
  color: #d97706;
  padding: 12px;
  border-radius: 6px;
  border: 1px solid #fbbf24;
  font-size: 0.875rem;
  margin-top: 12px;
}

/* Buttons */
.btn-add-item {
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

.btn-add-item:hover {
  background: #2563eb;
}

.btn-primary {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-primary:hover {
  background: #2563eb;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-relatorio {
  background: #10b981;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.875rem;
}

.btn-relatorio:hover {
  background: #059669;
}

.btn-relatorio:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background: #9ca3af;
}

.btn-relatorio .icon {
  flex-shrink: 0;
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  padding: 12px 24px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: #e5e7eb;
}

.btn-outline {
  background: white;
  color: #3b82f6;
  border: 1px solid #3b82f6;
  padding: 12px 24px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-outline:hover {
  background: #eff6ff;
}

.btn-add-first {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  padding: 12px 24px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add-first:hover {
  background: #e5e7eb;
}

/* Items */
.items-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.item-card {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.item-number {
  font-weight: 600;
  color: #374151;
}

.btn-remove-item {
  background: #fee2e2;
  color: #dc2626;
  border: none;
  padding: 6px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-remove-item:hover {
  background: #fecaca;
}

.item-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* Empty States */
.empty-items,
.empty-fornecedores {
  text-align: center;
  padding: 40px 20px;
  color: #6b7280;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

/* Fornecedores */
.fornecedores-lista {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.fornecedor-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.fornecedor-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1F285F, #4338ca);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.875rem;
}

.fornecedor-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.fornecedor-nome {
  font-weight: 600;
  color: #111827;
}

.fornecedor-cnpj {
  font-size: 0.75rem;
  color: #6b7280;
}

.btn-remove-fornecedor {
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.btn-remove-fornecedor:hover {
  background: #fee2e2;
  color: #dc2626;
}

/* Loading */
.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Modal Transition */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-container,
.modal-leave-to .modal-container {
  transform: translateY(-20px) scale(1.05);
  opacity: 0;
}

/* Responsividade */
@media (max-width: 768px) {
  .modal-overlay {
    padding: 10px;
  }

  .modal-container {
    margin: 10px auto;
    max-height: calc(100vh - 20px);
  }

  .modal-header,
  .modal-body,
  .modal-footer {
    padding: 16px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .section-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .modal-footer {
    flex-direction: column;
    gap: 12px;
  }

  .footer-actions {
    width: 100%;
    justify-content: space-between;
  }
}

/* Alertas */
.alert {
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  font-size: 0.875rem;
  font-weight: 500;
}

.alert.error {
  background-color: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.alert.success {
  background-color: #f0f9ff;
  color: #059669;
  border: 1px solid #a7f3d0;
}

.alert.warning {
  background-color: #fffbeb;
  color: #d97706;
  border: 1px solid #fed7aa;
}

/* Badge para tipo de fornecedor */
.badge {
  display: inline-block;
  padding: 2px 6px;
  background: #e5e7eb;
  color: #374151;
  font-size: 0.75rem;
  border-radius: 4px;
  margin-left: 8px;
}



.form-hint {
  color: #6b7280;
  font-size: 0.75rem;
  margin-top: 4px;
  font-style: italic;
}

.data-calculada {
  color: #059669;
  font-weight: 500;
  font-style: normal;
}

/* Upload de arquivo */
.file-upload-wrapper {
  position: relative;
}

.file-input {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
}

.file-label {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  border: 2px dashed #d1d5db;
  border-radius: 6px;
  background: #f9fafb;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.875rem;
  font-weight: 500;
}

.file-label:hover {
  border-color: #3b82f6;
  background: #eff6ff;
  color: #3b82f6;
}

.upload-icon {
  color: currentColor;
}

.file-selected {
  color: #059669;
  font-weight: 600;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  padding: 8px 12px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 6px;
  font-size: 0.875rem;
}

.file-name {
  flex: 1;
  color: #059669;
  font-weight: 500;
}

.file-size {
  color: #6b7280;
  font-size: 0.75rem;
}

.btn-remove-file {
  background: none;
  border: none;
  color: #dc2626;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-remove-file:hover {
  background: #fee2e2;
}

.btn-remove-file svg {
  display: block;
}
</style>
