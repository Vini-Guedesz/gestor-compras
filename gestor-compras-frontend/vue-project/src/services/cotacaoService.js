import api from './api.js'
import axios from 'axios'
import logger from '../utils/logger.js'

const BASE_URL = '/api/v1/cotacoes'

// Configuração da URL base da API
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'

/**
 * Helper para extrair dados de respostas paginadas do Spring Data
 */
const extractContent = (response) => {
  if (response && typeof response === 'object' && 'content' in response) {
    return response.content || []
  }
  return Array.isArray(response) ? response : []
}

/**
 * Cria uma instância específica do Axios para relatórios
 * com configuração para receber dados binários (PDF)
 */
const relatorioClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000, // 30 segundos para relatórios que podem demorar mais
  responseType: 'blob' // Importante para receber dados binários (PDF)
})

/**
 * Interceptador para adicionar token de autenticação nos relatórios
 */
relatorioClient.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    logger.error('❌ Erro no interceptor de request:', error)
    return Promise.reject(error)
  }
)

/**
 * Interceptador de resposta para tratar erros nos relatórios
 */
relatorioClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      sessionStorage.removeItem('authToken')
      window.location.href = '/login'
      return Promise.reject(new Error('Sessão expirada. Faça login novamente.'))
    }
    return Promise.reject(error)
  }
)

export const cotacaoService = {
  // Listar todas as cotações
  async listar(filtros = {}) {
    try {
      const params = new URLSearchParams()

      if (filtros.status) params.append('status', filtros.status)
      if (filtros.fornecedor) params.append('fornecedor', filtros.fornecedor)
      if (filtros.periodo) params.append('periodo', filtros.periodo)
      if (filtros.busca) params.append('busca', filtros.busca)
      if (filtros.pagina) params.append('pagina', filtros.pagina)
      if (filtros.tamanho) params.append('tamanho', filtros.tamanho)

      const url = params.toString() ? `${BASE_URL}?${params}` : BASE_URL
      const response = await api.get(url)
      return extractContent(response)
    } catch (error) {
      logger.error('Erro ao listar cotações:', error)
      throw error
    }
  },

  // Buscar cotação por ID
  async buscarPorId(id) {
    try {
      const response = await api.get(`${BASE_URL}/${id}`)
      return response
    } catch (error) {
      logger.error('Erro ao buscar cotação:', error)
      throw error
    }
  },

  // Buscar cotações por fornecedor
  async buscarPorFornecedor(fornecedorId) {
    try {
      const response = await api.get(`${BASE_URL}`, {
        params: { fornecedorId }
      })
      return extractContent(response)
    } catch (error) {
      logger.error('Erro ao buscar cotações do fornecedor:', error)
      throw error
    }
  },

  // Listar cotações por pedido
  async listarPorPedido(pedidoId) {
    try {
      const response = await api.get(`${BASE_URL}/pedido/${pedidoId}`)
      return extractContent(response)
    } catch (error) {
      logger.error('❌ Erro ao buscar cotações do pedido:', error)
      // Bug Fix #12: Propagar erro ao invés de retornar array vazio silenciosamente
      throw new Error(`Erro ao carregar cotações do pedido: ${error.message}`)
    }
  },

  // Listar cotações por item
  async listarPorItem(itemPedidoId) {
    try {
      const response = await api.get(`${BASE_URL}/item/${itemPedidoId}`)
      return extractContent(response)
    } catch (error) {
      logger.error('❌ Erro ao buscar cotações do item:', error)
      // Bug Fix #12: Propagar erro ao invés de retornar array vazio silenciosamente
      throw new Error(`Erro ao carregar cotações do item: ${error.message}`)
    }
  },

  // Criar nova cotação
  async criar(dadosCotacao) {
    try {
      // Validação básica conforme CotacaoCreateDTO
      if (!dadosCotacao.fornecedorId) {
        throw new Error('Fornecedor é obrigatório')
      }

      if (!dadosCotacao.tipoFornecedor) {
        throw new Error('Tipo de fornecedor é obrigatório')
      }

      if (!dadosCotacao.solicitacaoDePedidoId) {
        throw new Error('ID da solicitação de pedido é obrigatório')
      }

      if (!dadosCotacao.itensPedidoIds || dadosCotacao.itensPedidoIds.length === 0) {
        throw new Error('Deve selecionar pelo menos um item do pedido')
      }

      if (!dadosCotacao.preco || dadosCotacao.preco <= 0) {
        throw new Error('Preço deve ser maior que zero')
      }


      // Preparar payload conforme CotacaoCreateDTO do backend
      const payload = {
        fornecedorId: dadosCotacao.fornecedorId,
        tipoFornecedor: dadosCotacao.tipoFornecedor, // PRODUTO ou SERVICO
        solicitacaoDePedidoId: dadosCotacao.solicitacaoDePedidoId,
        itensPedidoIds: dadosCotacao.itensPedidoIds,
        preco: parseFloat(dadosCotacao.preco),
        prazoEmDiasUteis: dadosCotacao.prazoEmDiasUteis ? parseInt(dadosCotacao.prazoEmDiasUteis) : null,
        dataLimite: dadosCotacao.dataLimite || null,
        anexoPdf: null
      }

      // Se houver anexo PDF, incluir no payload
      if (dadosCotacao.anexoPdf) {

        // Se já for um array de bytes, usar direto
        if (Array.isArray(dadosCotacao.anexoPdf)) {
          payload.anexoPdf = dadosCotacao.anexoPdf
        } else if (dadosCotacao.anexoPdf instanceof File) {
          // Validar arquivo PDF
          if (dadosCotacao.anexoPdf.type !== 'application/pdf') {
            throw new Error('Apenas arquivos PDF são permitidos')
          }

          const maxSize = 10 * 1024 * 1024 // 10MB
          if (dadosCotacao.anexoPdf.size > maxSize) {
            throw new Error('Arquivo muito grande. Máximo permitido: 10MB')
          }

          const bytesArray = await this.arquivoParaBytes(dadosCotacao.anexoPdf)
          payload.anexoPdf = bytesArray
        }
      }

      const response = await api.post(BASE_URL, payload)
      return response

    } catch (error) {
      logger.error('❌ Erro ao criar cotação:', error)
      throw error
    }
  },

  // Método de conveniência para salvar
  async salvar(dadosCotacao) {
    return this.criar(dadosCotacao)
  },

  // Atualizar cotação
  async atualizar(id, dadosCotacao) {
    try {
      // Validação básica
      if (!id) {
        throw new Error('ID da cotação é obrigatório')
      }

      // Preparar dados para envio (CotacaoUpdateDTO)
      const payload = {}

      // Incluir apenas campos válidos do CotacaoUpdateDTO
      if (dadosCotacao.preco !== undefined && dadosCotacao.preco !== null) {
        payload.preco = parseFloat(dadosCotacao.preco)
        if (payload.preco <= 0) {
          throw new Error('Preço deve ser maior que zero')
        }
      }

      if (dadosCotacao.prazoEntrega !== undefined && dadosCotacao.prazoEntrega !== null) {
        payload.prazoEntrega = parseInt(dadosCotacao.prazoEntrega)
        if (payload.prazoEntrega < 0) {
          throw new Error('Prazo de entrega deve ser positivo')
        }
      }

      if (dadosCotacao.anexoPdf !== undefined) {
        // Se o anexoPdf for um arquivo, validar e converter
        if (dadosCotacao.anexoPdf instanceof File) {
          // Validar tipo
          if (dadosCotacao.anexoPdf.type !== 'application/pdf') {
            throw new Error('Apenas arquivos PDF são permitidos')
          }

          // Validar tamanho (10MB)
          const maxSize = 10 * 1024 * 1024
          if (dadosCotacao.anexoPdf.size > maxSize) {
            throw new Error('Arquivo muito grande. Máximo permitido: 10MB')
          }

          // Converter para bytes
          const bytesArray = await this.arquivoParaBytes(dadosCotacao.anexoPdf)
          payload.anexoPdf = bytesArray
        } else {
          // Se já for array de bytes ou null, usar direto
          payload.anexoPdf = dadosCotacao.anexoPdf
        }
      }

      const response = await api.put(`${BASE_URL}/${id}`, payload)
      return response

    } catch (error) {
      logger.error('❌ Erro ao atualizar cotação:', error)
      throw error
    }
  },

  // Excluir cotação
  async deleteCotacao(id) {
    try {
      if (!id) {
        throw new Error('ID da cotação é obrigatório')
      }

      await api.delete(`${BASE_URL}/${id}`)
      return true
    } catch (error) {
      logger.error('❌ Erro ao excluir cotação:', error)
      throw error
    }
  },

  // Deletar cotação
  async deletar(id) {
    try {
      const response = await api.delete(`${BASE_URL}/${id}`)
      return response
    } catch (error) {
      logger.error('Erro ao deletar cotação:', error)
      throw error
    }
  },

  // Enviar cotação para fornecedores
  async enviar(id, fornecedoresIds) {
    try {
      const response = await api.post(`${BASE_URL}/${id}/enviar`, {
        fornecedores: fornecedoresIds
      })
      return response.data
    } catch (error) {
      logger.error('Erro ao enviar cotação:', error)
      throw error
    }
  },

  // Obter propostas de uma cotação
  async obterPropostas(id) {
    try {
      const response = await api.get(`${BASE_URL}/${id}/propostas`)
      return response.data
    } catch (error) {
      logger.error('Erro ao obter propostas:', error)
      throw error
    }
  },

  // Selecionar proposta vencedora
  async selecionarProposta(cotacaoId, propostaId, justificativa = '') {
    try {
      const response = await api.post(`${BASE_URL}/${cotacaoId}/selecionar-proposta`, {
        propostaId,
        justificativa
      })
      return response.data
    } catch (error) {
      logger.error('Erro ao selecionar proposta:', error)
      throw error
    }
  },

  // Aprovar cotação
  async aprovar(id, observacoes = '') {
    try {
      const response = await api.post(`${BASE_URL}/${id}/aprovar`, {
        observacoes
      })
      return response.data
    } catch (error) {
      logger.error('Erro ao aprovar cotação:', error)
      throw error
    }
  },

  // Cancelar cotação
  async cancelar(id, motivo = '') {
    try {
      const response = await api.post(`${BASE_URL}/${id}/cancelar`, {
        motivo
      })
      return response.data
    } catch (error) {
      logger.error('Erro ao cancelar cotação:', error)
      throw error
    }
  },

  // Reabrir cotação
  async reabrir(id) {
    try {
      const response = await api.post(`${BASE_URL}/${id}/reabrir`)
      return response.data
    } catch (error) {
      logger.error('Erro ao reabrir cotação:', error)
      throw error
    }
  },

  // Duplicar cotação
  async duplicar(id) {
    try {
      const response = await api.post(`${BASE_URL}/${id}/duplicar`)
      return response.data
    } catch (error) {
      logger.error('Erro ao duplicar cotação:', error)
      throw error
    }
  },

  // Adicionar item à cotação
  async adicionarItem(cotacaoId, item) {
    try {
      const response = await api.post(`${BASE_URL}/${cotacaoId}/itens`, item)
      return response.data
    } catch (error) {
      logger.error('Erro ao adicionar item:', error)
      throw error
    }
  },

  // Atualizar item da cotação
  async atualizarItem(cotacaoId, itemId, item) {
    try {
      const response = await api.put(`${BASE_URL}/${cotacaoId}/itens/${itemId}`, item)
      return response.data
    } catch (error) {
      logger.error('Erro ao atualizar item:', error)
      throw error
    }
  },

  // Remover item da cotação
  async removerItem(cotacaoId, itemId) {
    try {
      const response = await api.delete(`${BASE_URL}/${cotacaoId}/itens/${itemId}`)
      return response.data
    } catch (error) {
      logger.error('Erro ao remover item:', error)
      throw error
    }
  },

  // Adicionar anexo
  async adicionarAnexo(cotacaoId, arquivo, createHistory = true) {
    try {
      const formData = new FormData()
      formData.append('files', arquivo)  // Backend espera 'files' não 'arquivo'

      // IMPORTANTE: NÃO definir Content-Type manualmente!
      // O Axios detecta FormData e configura automaticamente com o boundary correto
      const response = await api.post(`${BASE_URL}/${cotacaoId}/anexos?createHistory=${createHistory}`, formData)
      return response.data
    } catch (error) {
      logger.error('Erro ao adicionar anexo:', error)
      throw error
    }
  },

  // Remover anexo
  async removerAnexo(cotacaoId, anexoId) {
    try {
      const response = await api.delete(`${BASE_URL}/${cotacaoId}/anexos/${anexoId}`)
      return response.data
    } catch (error) {
      logger.error('Erro ao remover anexo:', error)
      throw error
    }
  },

  // Gerar relatório comparativo de cotações por item
  async gerarRelatorioComparativo(itemPedidoId) {
    try {
      if (!itemPedidoId) {
        throw new Error('ID do item do pedido é obrigatório para gerar o relatório')
      }


      const response = await relatorioClient.get(`/relatorios/comparativo-cotacao/${itemPedidoId}`)


      // Verificar se a resposta existe e tem conteúdo
      if (!response.data || response.data.size === 0) {
        logger.error('❌ Resposta vazia ou sem dados do servidor')
        throw new Error('Não foi possível gerar o relatório - resposta vazia do servidor')
      }

      // Verificar se o status da resposta é ok
      if (response.status !== 200) {
        logger.error('❌ Status HTTP não ok:', response.status, response.statusText)
        throw new Error(`Erro no servidor: ${response.status} - ${response.statusText}`)
      }

      // Criar link para download
      const blob = new Blob([response.data], { type: 'application/pdf' })
      const downloadUrl = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = `comparativo_cotacao_item_${itemPedidoId}_${new Date().toISOString().split('T')[0]}.pdf`
      link.click()
      window.URL.revokeObjectURL(downloadUrl)

      return true
    } catch (error) {
      logger.error('❌ Erro ao gerar relatório comparativo:', error)

      // Log mais detalhado do erro
      if (error.response) {
        logger.error('❌ Detalhes do erro de resposta:', {
          status: error.response.status,
          statusText: error.response.statusText,
          data: error.response.data,
          headers: error.response.headers
        })
      }

      throw error
    }
  },

  // Gerar relatório geral de cotações (usando dashboard executivo como base)
  async gerarRelatorioCotacoes() {
    try {

      const response = await relatorioClient.get('/relatorios/dashboard-executivo')


      // Verificar se a resposta existe
      if (!response.data) {
        logger.error('❌ response.data é null ou undefined')
        throw new Error('Não foi possível gerar o relatório - dados ausentes')
      }

      // Para blob, verificar o tamanho
      if (response.data instanceof Blob) {
        if (response.data.size === 0) {
          logger.error('❌ Blob está vazio (size = 0)')
          throw new Error('Não foi possível gerar o relatório - arquivo vazio')
        }
      }

      // Verificar se o status da resposta é ok
      if (response.status !== 200) {
        logger.error('❌ Status HTTP não ok:', response.status, response.statusText)
        throw new Error(`Erro no servidor: ${response.status} - ${response.statusText}`)
      }

      // Criar link para download
      const blob = new Blob([response.data], { type: 'application/pdf' })
      const downloadUrl = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = `relatorio_cotacoes_${new Date().toISOString().split('T')[0]}.pdf`

      // Adicionar ao DOM, clicar e remover
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)

      // Limpar URL após um delay
      setTimeout(() => {
        window.URL.revokeObjectURL(downloadUrl)
      }, 100)

      return true
    } catch (error) {
      logger.error('❌ Erro ao gerar relatório de cotações:', error)

      // Log mais detalhado do erro
      if (error.response) {
        logger.error('❌ Detalhes do erro de resposta:', {
          status: error.response.status,
          statusText: error.response.statusText,
          data: error.response.data,
          headers: error.response.headers
        })

        // Se a resposta for um blob de erro, tentar ler como texto
        if (error.response.data instanceof Blob) {
          try {
            const text = await error.response.data.text()
            logger.error('❌ Conteúdo do erro (blob como texto):', text)
          } catch (e) {
            logger.error('❌ Não foi possível ler o blob de erro:', e)
          }
        }
      }

      throw error
    }
  },

  // Obter estatísticas do dashboard
  async obterEstatisticas(periodo = 'mes') {
    try {
      const response = await api.get(`${BASE_URL}/estatisticas?periodo=${periodo}`)
      return response.data
    } catch (error) {
      logger.error('Erro ao obter estatísticas:', error)
      throw error
    }
  },

  // Obter histórico de uma cotação
  async obterHistorico(id) {
    try {
      const response = await api.get(`${BASE_URL}/${id}/historico`)
      return response.data
    } catch (error) {
      logger.error('Erro ao obter histórico:', error)
      throw error
    }
  },

  // Adicionar comentário à cotação
  async adicionarComentario(cotacaoId, comentario) {
    try {
      const response = await api.post(`${BASE_URL}/${cotacaoId}/comentarios`, {
        comentario
      })
      return response.data
    } catch (error) {
      logger.error('Erro ao adicionar comentário:', error)
      throw error
    }
  },

  // Notificar fornecedores sobre prazo
  async notificarPrazo(cotacaoId, tipo = 'lembrete') {
    try {
      const response = await api.post(`${BASE_URL}/${cotacaoId}/notificar`, {
        tipo
      })
      return response.data
    } catch (error) {
      logger.error('Erro ao notificar fornecedores:', error)
      throw error
    }
  },

  // Validar cotação antes do envio
  async validar(cotacao) {
    try {
      const response = await api.post(`${BASE_URL}/validar`, cotacao)
      return response.data
    } catch (error) {
      logger.error('Erro ao validar cotação:', error)
      throw error
    }
  },

  // Obter modelos de cotação
  async obterModelos() {
    try {
      const response = await api.get(`${BASE_URL}/modelos`)
      return response.data
    } catch (error) {
      logger.error('Erro ao obter modelos:', error)
      throw error
    }
  },

  // Criar cotação a partir de modelo
  async criarDeModelo(modeloId, dados = {}) {
    try {
      const response = await api.post(`${BASE_URL}/modelos/${modeloId}/criar`, dados)
      return response.data
    } catch (error) {
      logger.error('Erro ao criar cotação de modelo:', error)
      throw error
    }
  },

  // ==================== ARQUIVOS PDF ====================

  // Converter arquivo para array de bytes
  async arquivoParaBytes(arquivo) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader()
      reader.onload = () => {
        const arrayBuffer = reader.result
        const uint8Array = new Uint8Array(arrayBuffer)
        resolve(Array.from(uint8Array))
      }
      reader.onerror = () => reject(reader.error)
      reader.readAsArrayBuffer(arquivo)
    })
  },

  // Upload de arquivo PDF para cotação existente
  async uploadArquivoPdf(cotacaoId, arquivo) {
    try {
      if (!arquivo) {
        throw new Error('Arquivo é obrigatório')
      }

      if (arquivo.type !== 'application/pdf') {
        throw new Error('Apenas arquivos PDF são permitidos')
      }

      const maxSize = 10 * 1024 * 1024 // 10MB
      if (arquivo.size > maxSize) {
        throw new Error('Arquivo muito grande. Máximo permitido: 10MB')
      }

      const bytesArray = await this.arquivoParaBytes(arquivo)

      const response = await api.put(`${BASE_URL}/${cotacaoId}`, {
        anexoPdf: bytesArray
      })

      return response

    } catch (error) {
      logger.error('❌ Erro no upload do PDF:', error)
      throw error
    }
  },

  // Obter anexo PDF da cotação
  async obterAnexoPdf(cotacaoId, pdfIndex = 0) {
    try {
      logger.debug(`📄 Buscando PDF da cotação ${cotacaoId}, índice ${pdfIndex}`)

      // Obter o token de autenticação
      const token = sessionStorage.getItem('authToken')

      // Usar o endpoint com índice para consistência
      const url = `${API_BASE_URL}/api/v1/cotacoes/${cotacaoId}/anexo/${pdfIndex}`
      logger.debug(`🔗 URL da requisição: ${url}`)

      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': token ? `Bearer ${token}` : ''
        }
      })

      logger.debug(`📡 Resposta HTTP: ${response.status} ${response.statusText}`)
      logger.debug(`📋 Content-Type: ${response.headers.get('content-type')}`)
      logger.debug(`📏 Content-Length: ${response.headers.get('content-length')}`)

      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('PDF não encontrado. Esta cotação pode não ter anexo.')
        }
        throw new Error(`Erro ao buscar PDF: ${response.status} ${response.statusText}`)
      }

      const blob = await response.blob()
      logger.debug(`✅ Blob criado: ${blob.size} bytes, tipo: ${blob.type}`)
      return blob
    } catch (error) {
      logger.error(`❌ Erro ao obter PDF da cotação ${cotacaoId}:`, error.message)
      throw error
    }
  },

  // Verificar se cotação tem anexo PDF
  async verificarAnexo(cotacaoId) {
    try {
      // Buscar os dados da cotação para verificar campos de anexo
      const cotacao = await this.buscarPorId(cotacaoId)

      return {
        temAnexo: !!(cotacao.caminhoAnexo || cotacao.anexoPdf),
        caminhoAnexo: cotacao.caminhoAnexo,
        temAnexoPdf: !!cotacao.anexoPdf
      }

    } catch (error) {
      logger.error('❌ Erro ao verificar anexo:', error)
      return {
        temAnexo: false,
        caminhoAnexo: null,
        temAnexoPdf: false
      }
    }
  },

  // Obter informações do anexo PDF
  async obterInfoAnexo(cotacaoId) {
    try {
      const info = await this.verificarAnexo(cotacaoId)

      if (!info.temAnexo) {
        throw new Error('Nenhum arquivo anexado a esta cotação')
      }

      return {
        disponivel: info.temAnexo,
        caminho: info.caminhoAnexo,
        tipo: info.caminhoAnexo ? 'arquivo' : 'blob',
        mensagem: info.caminhoAnexo
          ? 'Arquivo PDF disponível'
          : 'Arquivo PDF armazenado no banco de dados'
      }

    } catch (error) {
      logger.error('❌ Erro ao obter informações do anexo:', error)
      throw error
    }
  },

  // Salvar como modelo
  async salvarComoModelo(cotacaoId, nomeModelo) {
    try {
      const response = await api.post(`${BASE_URL}/${cotacaoId}/salvar-modelo`, {
        nome: nomeModelo
      })
      return response.data
    } catch (error) {
      logger.error('Erro ao salvar como modelo:', error)
      throw error
    }
  },

  // Vincular itens a uma cotação
  async vincularItens(cotacaoId, itensPedidoIds) {
    try {
      if (!cotacaoId) {
        throw new Error('ID da cotação é obrigatório')
      }

      if (!itensPedidoIds || itensPedidoIds.length === 0) {
        throw new Error('Deve fornecer pelo menos um item para vincular')
      }


      const response = await api.patch(`${BASE_URL}/${cotacaoId}/vincular-itens`, itensPedidoIds)

      return response

    } catch (error) {
      logger.error('❌ Erro ao vincular itens:', error)
      throw error
    }
  },

  /**
   * Edita uma cotação existente com auditoria
   */
  async editarCotacao(cotacaoId, editDTO) {
    try {
      if (!cotacaoId) {
        throw new Error('ID da cotação é obrigatório')
      }

      if (!editDTO.motivoEdicao || editDTO.motivoEdicao.trim().length < 10) {
        throw new Error('Motivo da edição deve ter no mínimo 10 caracteres')
      }

      if (!editDTO.editadoPor || editDTO.editadoPor.trim().length === 0) {
        throw new Error('Responsável pela edição é obrigatório')
      }

      logger.debug('🔧 Chamando API PUT /editar...')
      const response = await api.put(`${BASE_URL}/${cotacaoId}/editar`, editDTO)
      logger.debug('🔧 Response completo:', response)
      logger.debug('🔧 Response.data:', response.data)
      return response.data
    } catch (error) {
      logger.error('❌ Erro ao editar cotação:', error)
      throw error
    }
  },

  /**
   * Busca o histórico de edições de uma cotação
   */
  async buscarHistorico(cotacaoId) {
    try {
      if (!cotacaoId) {
        throw new Error('ID da cotação é obrigatório')
      }

      const data = await api.get(`${BASE_URL}/${cotacaoId}/historico`)
      return data
    } catch (error) {
      logger.error('❌ Erro ao buscar histórico:', error)
      throw error
    }
  },

  /**
   * Obtém PDF anterior do histórico de cotação para visualização
   */
  async obterPdfAnteriorHistorico(historicoId) {
    try {
      if (!historicoId) {
        throw new Error('ID do histórico é obrigatório')
      }

      const response = await relatorioClient.get(`${BASE_URL}/historico/${historicoId}/pdf/anterior`)
      return response.data
    } catch (error) {
      logger.error('❌ Erro ao obter PDF anterior do histórico:', error)
      throw error
    }
  },

  /**
   * Obtém PDF novo do histórico de cotação para visualização
   */
  async obterPdfNovoHistorico(historicoId) {
    try {
      if (!historicoId) {
        throw new Error('ID do histórico é obrigatório')
      }

      const response = await relatorioClient.get(`${BASE_URL}/historico/${historicoId}/pdf/novo`)
      return response.data
    } catch (error) {
      logger.error('❌ Erro ao obter PDF novo do histórico:', error)
      throw error
    }
  },

  /**
   * Baixa PDF anterior do histórico de cotação (download direto)
   */
  async baixarPdfAnteriorHistorico(historicoId) {
    try {
      const blob = await this.obterPdfAnteriorHistorico(historicoId)
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `historico-${historicoId}-anterior.pdf`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    } catch (error) {
      logger.error('❌ Erro ao baixar PDF anterior do histórico:', error)
      throw error
    }
  },

  /**
   * Baixa PDF novo do histórico de cotação (download direto)
   */
  async baixarPdfNovoHistorico(historicoId) {
    try {
      const blob = await this.obterPdfNovoHistorico(historicoId)
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `historico-${historicoId}-novo.pdf`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    } catch (error) {
      logger.error('❌ Erro ao baixar PDF novo do histórico:', error)
      throw error
    }
  },

  /**
   * Gera e baixa PDF de uma cotação específica
   */
  async gerarPDFCotacao(cotacaoId) {
    try {
      if (!cotacaoId) {
        throw new Error('ID da cotação é obrigatório')
      }

      logger.debug(`📄 Gerando PDF da cotação ${cotacaoId}`)

      const response = await relatorioClient.get(`/relatorios/cotacao/${cotacaoId}`)

      // Verificar se a resposta existe e tem conteúdo
      if (!response.data || response.data.size === 0) {
        logger.error('❌ Resposta vazia ou sem dados do servidor')
        throw new Error('Não foi possível gerar o relatório - resposta vazia do servidor')
      }

      // Verificar se o status da resposta é ok
      if (response.status !== 200) {
        logger.error('❌ Status HTTP não ok:', response.status, response.statusText)
        throw new Error(`Erro no servidor: ${response.status} - ${response.statusText}`)
      }

      // Criar link para download
      const blob = new Blob([response.data], { type: 'application/pdf' })
      const downloadUrl = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = `cotacao_${cotacaoId}_${new Date().toISOString().split('T')[0]}.pdf`

      // Adicionar ao DOM, clicar e remover
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)

      // Limpar URL após um delay
      setTimeout(() => {
        window.URL.revokeObjectURL(downloadUrl)
      }, 100)

      logger.debug(`✅ PDF da cotação ${cotacaoId} gerado com sucesso`)
      return true
    } catch (error) {
      logger.error('❌ Erro ao gerar PDF da cotação:', error)

      // Log mais detalhado do erro
      if (error.response) {
        logger.error('❌ Detalhes do erro de resposta:', {
          status: error.response.status,
          statusText: error.response.statusText,
          data: error.response.data,
          headers: error.response.headers
        })

        // Se a resposta for um blob de erro, tentar ler como texto
        if (error.response.data instanceof Blob) {
          try {
            const text = await error.response.data.text()
            logger.error('❌ Conteúdo do erro (blob como texto):', text)
          } catch (e) {
            logger.error('❌ Não foi possível ler o blob de erro:', e)
          }
        }
      }

      throw error
    }
  }
}

export default cotacaoService
