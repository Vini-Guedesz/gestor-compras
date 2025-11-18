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

                <!-- Item com pesquisa integrada -->
                <div class="form-group">
                  <label class="form-label">Item do Pedido *</label>
                  <div class="custom-select-wrapper" :class="{ disabled: !pedidoSelecionado }">
                    <div class="custom-select">
                      <input
                        v-model="pesquisaItem"
                        type="text"
                        class="custom-select-input"
                        :placeholder="pedidoSelecionado ? 'Pesquisar item...' : 'Primeiro selecione um pedido'"
                        @focus="openDropdown('item')"
                        @input="openDropdown('item')"
                        :disabled="!pedidoSelecionado"
                      />
                      <svg class="dropdown-icon" viewBox="0 0 24 24" width="20" height="20" @click="!pedidoSelecionado ? null : toggleDropdown('item')">
                        <path fill="currentColor" d="M7 10l5 5 5-5z"/>
                      </svg>
                    </div>
                    <div v-if="dropdownAberto === 'item'" class="dropdown-list">
                      <div
                        v-for="item in itensFiltradosComPesquisa"
                        :key="item.id"
                        class="dropdown-item"
                        @click="selecionarItem(item)"
                      >
                        <div class="item-main">#{{ item.id }} - {{ item.nome || 'Item sem nome' }}</div>
                        <div class="item-secondary">Qtd: {{ item.quantidade || 0 }}{{ item.descricao ? ' - ' + item.descricao : '' }}</div>
                      </div>
                      <div v-if="itensFiltradosComPesquisa.length === 0" class="dropdown-empty">
                        {{ pedidoSelecionado ? 'Nenhum item encontrado neste pedido' : 'Selecione um pedido primeiro' }}
                      </div>
                    </div>
                  </div>
                  <small class="form-hint">{{ itemSelecionadoNome || (itensFiltradosComPesquisa.length + ' itens disponíveis') }}</small>
                </div>

                <!-- Preço -->
                <div class="form-group">
                  <label class="form-label">Preço (R$) *</label>
                  <input
                    type="text"
                    v-model="precoFormatado"
                    @input="formatarPrecoInput"
                    class="form-input"
                    required
                    placeholder="0,00"
                    inputmode="numeric"
                  />
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
  itemPedidoId: null,
  preco: null,
  prazoEmDiasUteis: null,
  dataLimite: null,
  arquivoPdf: null
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
    console.warn('Pedido não encontrado:', pedidoSelecionado.value)
    return []
  }

  // Retornar os itens do pedido
  const itens = pedido.itens || []
  console.log(`📦 Itens do pedido #${pedido.id}:`, itens.length, itens)

  return itens
})

// Itens filtrados por pedido E pesquisa
const itensFiltradosComPesquisa = computed(() => {
  let resultado = itensFiltradosPorPedido.value

  if (pesquisaItem.value.trim()) {
    const query = pesquisaItem.value.toLowerCase().trim()
    resultado = resultado.filter(i =>
      i.id?.toString().includes(query) ||
      i.nome?.toLowerCase().includes(query) ||
      i.descricao?.toLowerCase().includes(query)
    )
  }

  return resultado
})

const formularioValido = computed(() => {
  return formData.value.fornecedorId &&
         formData.value.itemPedidoId &&
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
    console.log('📄 Arquivo PDF selecionado:', file.name, `(${formatarTamanhoArquivo(file.size)})`)
  }
}

const removerArquivo = () => {
  arquivoPdf.value = null
  formData.value.arquivoPdf = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
  console.log('🗑️ Arquivo PDF removido')
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
  console.log('🔍 Pedido selecionado:', pedido)
  console.log('📦 Itens do pedido:', pedido.itens?.length || 0, pedido.itens)

  pedidoSelecionado.value = pedido.id
  pedidoSelecionadoLabel.value = `Pedido #${pedido.id} - ${getStatusLabel(pedido.status)}`
  pesquisaPedido.value = pedido.id.toString() // Apenas o número do ID

  // Limpar item selecionado quando pedido muda
  formData.value.itemPedidoId = null
  itemSelecionadoNome.value = ''
  pesquisaItem.value = ''

  closeDropdown()

  // Log para verificar os itens filtrados
  setTimeout(() => {
    console.log('📋 Itens disponíveis após seleção:', itensFiltradosPorPedido.value)
  }, 100)
}

const selecionarItem = (item) => {
  formData.value.itemPedidoId = item.id
  itemSelecionadoNome.value = item.nome || 'Item sem nome'
  pesquisaItem.value = item.id.toString() // Apenas o número do ID
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



const handleSubmit = async () => {
  if (carregando.value) {
    console.log('⚠️ Já está processando uma cotação, aguarde...')
    return
  }

  try {
    carregando.value = true
    mensagemAlerta.value = ''

    // Validações do formulário
    if (!formularioValido.value) {
      mostrarAlerta('Por favor, preencha todos os campos obrigatórios', 'error')
      return
    }

    if (!tipoFornecedor.value) {
      mostrarAlerta('Por favor, selecione um tipo de fornecedor', 'error')
      return
    }

    console.log('🚀 Iniciando envio de cotação...')
    console.log('📋 Dados do formulário:', formData.value)

    const dadosParaSalvar = {
      fornecedorId: parseInt(formData.value.fornecedorId),
      tipoFornecedor: tipoFornecedor.value,
      itemPedidoId: parseInt(formData.value.itemPedidoId),
      preco: parseFloat(formData.value.preco),
      prazoEmDiasUteis: formData.value.prazoEmDiasUteis ? parseInt(formData.value.prazoEmDiasUteis) : null,
      dataLimite: formData.value.dataLimite || null,
      arquivoPdf: formData.value.arquivoPdf || null
    }

    if (dadosParaSalvar.arquivoPdf) {
      console.log('📄 Incluindo arquivo PDF:', dadosParaSalvar.arquivoPdf.name)
    }

    // Validar conversões
    if (isNaN(dadosParaSalvar.fornecedorId) || dadosParaSalvar.fornecedorId <= 0) {
      mostrarAlerta('Fornecedor inválido', 'error')
      return
    }

    if (isNaN(dadosParaSalvar.itemPedidoId) || dadosParaSalvar.itemPedidoId <= 0) {
      mostrarAlerta('Item do pedido inválido', 'error')
      return
    }

    if (isNaN(dadosParaSalvar.preco) || dadosParaSalvar.preco <= 0) {
      mostrarAlerta('Preço inválido', 'error')
      return
    }

    console.log(' Enviando dados para a view pai:', dadosParaSalvar)

    // Emitir os dados para a view pai processar
    emit('save', dadosParaSalvar)

    mostrarAlerta('Cotação enviada com sucesso!', 'success')

    // Aguardar um pouco para mostrar a mensagem antes de fechar
    setTimeout(() => {
      emit('close')
    }, 1000)

  } catch (error) {
    console.error('❌ Erro ao processar cotação:', error)
    mostrarAlerta('Erro interno ao processar cotação', 'error')
  } finally {
    carregando.value = false
  }
}

// Carregar dados
const carregarFornecedores = async () => {
  try {
    console.log('🔄 Carregando fornecedores para cotação...')
    const fornecedores = await fornecedorService.listarParaCotacao()
    fornecedoresDisponiveis.value = fornecedores || []
    console.log('✅ Fornecedores carregados:', fornecedores?.length || 0)

    if (!fornecedores || fornecedores.length === 0) {
      mostrarAlerta('Nenhum fornecedor encontrado no sistema', 'warning')
    }
  } catch (error) {
    console.error('❌ Erro ao carregar fornecedores:', error)
    fornecedoresDisponiveis.value = []
    mostrarAlerta('Erro ao carregar fornecedores. Verifique a conexão.', 'error')
  }
}

const carregarItens = async () => {
  try {
    console.log('🔄 Carregando itens de pedido...')
    const itens = await itemPedidoService.listarTodos()
    itensDisponiveis.value = itens || []
    console.log('✅ Itens carregados:', itens?.length || 0)

    if (!itens || itens.length === 0) {
      mostrarAlerta('Nenhum item de pedido encontrado', 'warning')
    }
  } catch (error) {
    console.error('❌ Erro ao carregar itens:', error)
    itensDisponiveis.value = []
    mostrarAlerta('Erro ao carregar itens. Verifique a conexão.', 'error')
  }
}

const carregarPedidos = async () => {
  try {
    console.log('🔄 Carregando pedidos...')
    const response = await pedidoService.listarTodos()

    // Processar pedidos - pode vir como array direto ou dentro de response.data
    const pedidos = Array.isArray(response) ? response : (response?.data || [])
    console.log('📋 Pedidos recebidos:', pedidos.length)

    // Verificar se os pedidos já vêm com itens
    if (pedidos.length > 0 && pedidos[0].itens) {
      console.log('✅ Pedidos já contêm itens')
      pedidosDisponiveis.value = pedidos
    } else {
      // Caso contrário, carregar itens separadamente e associar
      console.log('🔄 Carregando itens para associar aos pedidos...')
      const todosItens = itensDisponiveis.value.length > 0
        ? itensDisponiveis.value
        : await itemPedidoService.listarTodos()

      console.log('📦 Total de itens carregados:', todosItens.length)

      // Associar itens aos pedidos
      pedidosDisponiveis.value = pedidos.map(pedido => {
        const itensDoPedido = todosItens.filter(item => {
          // Tentar diferentes propriedades que podem indicar o pedido
          const pedidoId = item.solicitacaoDePedido?.id || item.solicitacaoDePedidoId || item.pedidoId
          const match = pedidoId === pedido.id
          if (match) {
            console.log(`  ✓ Item #${item.id} pertence ao pedido #${pedido.id}`)
          }
          return match
        })

        console.log(`📦 Pedido #${pedido.id}: ${itensDoPedido.length} itens associados`)

        return {
          ...pedido,
          itens: itensDoPedido
        }
      })
    }

    console.log('✅ Pedidos processados:', pedidosDisponiveis.value.length)
    console.log('📊 Estrutura dos pedidos:', pedidosDisponiveis.value.map(p => ({
      id: p.id,
      itens: p.itens?.length || 0
    })))

    if (pedidosDisponiveis.value.length === 0) {
      mostrarAlerta('Nenhum pedido encontrado', 'warning')
    }
  } catch (error) {
    console.error('❌ Erro ao carregar pedidos:', error)
    pedidosDisponiveis.value = []
    mostrarAlerta('Erro ao carregar pedidos. Verifique a conexão.', 'error')
  }
}

const inicializarFormulario = async () => {
  if (props.cotacao) {
    // Editando cotação existente
    console.log('📝 Editando cotação:', props.cotacao)

    formData.value = {
      fornecedorId: props.cotacao.fornecedorId,
      itemPedidoId: props.cotacao.itemPedidoId,
      preco: props.cotacao.preco,
      prazoEmDiasUteis: props.cotacao.prazoEmDiasUteis,
      dataLimite: props.cotacao.dataLimite
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

    // Preencher o pedido selecionado - precisa encontrar qual pedido contém o item
    const pedido = pedidosDisponiveis.value.find(p =>
      p.itens?.some(item => item.id === props.cotacao.itemPedidoId)
    )
    if (pedido) {
      pedidoSelecionado.value = pedido.id
      pedidoSelecionadoLabel.value = `Pedido #${pedido.id} - ${getStatusLabel(pedido.status)}`
      pesquisaPedido.value = pedido.id.toString() // Apenas o número do ID

      // Preencher o item selecionado
      const item = pedido.itens?.find(i => i.id === props.cotacao.itemPedidoId)
      if (item) {
        itemSelecionadoNome.value = item.nome || 'Item sem nome'
        pesquisaItem.value = item.id.toString() // Apenas o número do ID
      }
    }
  } else {
    // Nova cotação - resetar tudo
    formData.value = {
      fornecedorId: null,
      itemPedidoId: null,
      preco: null,
      prazoEmDiasUteis: null,
      dataLimite: null,
      arquivoPdf: null
    }
    tipoFornecedor.value = ''
    pedidoSelecionado.value = ''

    // Limpar labels e pesquisas
    fornecedorSelecionadoNome.value = ''
    pedidoSelecionadoLabel.value = ''
    itemSelecionadoNome.value = ''
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
  console.log('🔄 Inicializando formulário de cotação...')
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
}

.close-button:hover {
  background: #f3f4f6;
  color: #374151;
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
  font-family: Arial, sans-serif;
  transition: all 0.2s;
}

/* Garantir fonte Arial nos inputs de data */
.form-input[type="date"] {
  font-family: Arial, sans-serif;
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
