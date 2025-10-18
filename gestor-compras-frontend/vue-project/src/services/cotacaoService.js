import api from './api.js'
import axios from 'axios'

const BASE_URL = '/api/cotacoes'

// Configuração da URL base da API
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'

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
    const token = localStorage.getItem('authToken')
    console.log('🔑 Token para relatório:', token ? 'presente' : 'ausente')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    console.log('📡 Fazendo requisição para relatório:', config.url)
    return config
  },
  (error) => {
    console.error('❌ Erro no interceptor de request:', error)
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
      localStorage.removeItem('authToken')
      localStorage.removeItem('user')
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
      return response
    } catch (error) {
      console.error('Erro ao listar cotações:', error)
      throw error
    }
  },

  // Buscar cotação por ID
  async buscarPorId(id) {
    try {
      const response = await api.get(`${BASE_URL}/${id}`)
      return response
    } catch (error) {
      console.error('Erro ao buscar cotação:', error)
      throw error
    }
  },

  // Buscar cotações por fornecedor
  async buscarPorFornecedor(fornecedorId) {
    try {
      const response = await api.get(`${BASE_URL}`, {
        params: { fornecedorId }
      })
      return response
    } catch (error) {
      console.error('Erro ao buscar cotações do fornecedor:', error)
      throw error
    }
  },

  // Criar nova cotação
  async criar(dadosCotacao) {
    try {
      // Validação básica alinhada com CotacaoCreateDTO
      if (!dadosCotacao.fornecedorId) {
        throw new Error('Fornecedor é obrigatório')
      }

      if (!dadosCotacao.itemPedidoId) {
        throw new Error('Item do pedido é obrigatório')
      }

      if (!dadosCotacao.preco || dadosCotacao.preco <= 0) {
        throw new Error('Preço deve ser maior que zero')
      }

      console.log('📤 Enviando dados para backend:', dadosCotacao)

      const response = await api.post(BASE_URL, dadosCotacao)
      console.log('✅ Cotação criada no backend:', response)
      return response

    } catch (error) {
      console.error('❌ Erro ao criar cotação:', error)
      throw error
    }
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
        payload.anexoPdf = dadosCotacao.anexoPdf
      }

      console.log('📤 Atualizando cotação no backend:', { id, payload })
      const response = await api.put(`${BASE_URL}/${id}`, payload)
      console.log('✅ Cotação atualizada no backend:', response)
      return response

    } catch (error) {
      console.error('❌ Erro ao atualizar cotação:', error)
      throw error
    }
  },

  // Deletar cotação
  async deletar(id) {
    try {
      const response = await api.delete(`${BASE_URL}/${id}`)
      return response
    } catch (error) {
      console.error('Erro ao deletar cotação:', error)
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
      console.error('Erro ao enviar cotação:', error)
      throw error
    }
  },

  // Obter propostas de uma cotação
  async obterPropostas(id) {
    try {
      const response = await api.get(`${BASE_URL}/${id}/propostas`)
      return response.data
    } catch (error) {
      console.error('Erro ao obter propostas:', error)
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
      console.error('Erro ao selecionar proposta:', error)
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
      console.error('Erro ao aprovar cotação:', error)
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
      console.error('Erro ao cancelar cotação:', error)
      throw error
    }
  },

  // Reabrir cotação
  async reabrir(id) {
    try {
      const response = await api.post(`${BASE_URL}/${id}/reabrir`)
      return response.data
    } catch (error) {
      console.error('Erro ao reabrir cotação:', error)
      throw error
    }
  },

  // Duplicar cotação
  async duplicar(id) {
    try {
      const response = await api.post(`${BASE_URL}/${id}/duplicar`)
      return response.data
    } catch (error) {
      console.error('Erro ao duplicar cotação:', error)
      throw error
    }
  },

  // Adicionar item à cotação
  async adicionarItem(cotacaoId, item) {
    try {
      const response = await api.post(`${BASE_URL}/${cotacaoId}/itens`, item)
      return response.data
    } catch (error) {
      console.error('Erro ao adicionar item:', error)
      throw error
    }
  },

  // Atualizar item da cotação
  async atualizarItem(cotacaoId, itemId, item) {
    try {
      const response = await api.put(`${BASE_URL}/${cotacaoId}/itens/${itemId}`, item)
      return response.data
    } catch (error) {
      console.error('Erro ao atualizar item:', error)
      throw error
    }
  },

  // Remover item da cotação
  async removerItem(cotacaoId, itemId) {
    try {
      const response = await api.delete(`${BASE_URL}/${cotacaoId}/itens/${itemId}`)
      return response.data
    } catch (error) {
      console.error('Erro ao remover item:', error)
      throw error
    }
  },

  // Adicionar anexo
  async adicionarAnexo(cotacaoId, arquivo) {
    try {
      const formData = new FormData()
      formData.append('arquivo', arquivo)

      const response = await api.post(`${BASE_URL}/${cotacaoId}/anexos`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      return response.data
    } catch (error) {
      console.error('Erro ao adicionar anexo:', error)
      throw error
    }
  },

  // Remover anexo
  async removerAnexo(cotacaoId, anexoId) {
    try {
      const response = await api.delete(`${BASE_URL}/${cotacaoId}/anexos/${anexoId}`)
      return response.data
    } catch (error) {
      console.error('Erro ao remover anexo:', error)
      throw error
    }
  },

  // Gerar relatório comparativo de cotações por item
  async gerarRelatorioComparativo(itemPedidoId) {
    try {
      if (!itemPedidoId) {
        throw new Error('ID do item do pedido é obrigatório para gerar o relatório')
      }

      console.log('📊 Gerando relatório comparativo para item:', itemPedidoId)

      const response = await relatorioClient.get(`/relatorios/comparativo-cotacao/${itemPedidoId}`)

      console.log('🔍 Debug - Resposta completa do relatório comparativo:', {
        status: response.status,
        statusText: response.statusText,
        headers: response.headers,
        dataSize: response.data?.size || 'unknown',
        dataType: response.data?.type || 'unknown',
        dataExists: !!response.data,
        fullResponse: response
      })

      // Verificar se a resposta existe e tem conteúdo
      if (!response.data || response.data.size === 0) {
        console.error('❌ Resposta vazia ou sem dados do servidor')
        throw new Error('Não foi possível gerar o relatório - resposta vazia do servidor')
      }

      // Verificar se o status da resposta é ok
      if (response.status !== 200) {
        console.error('❌ Status HTTP não ok:', response.status, response.statusText)
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

      console.log('✅ Relatório comparativo gerado com sucesso')
      return true
    } catch (error) {
      console.error('❌ Erro ao gerar relatório comparativo:', error)

      // Log mais detalhado do erro
      if (error.response) {
        console.error('❌ Detalhes do erro de resposta:', {
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
      console.log('📊 Gerando relatório geral de cotações...')

      const response = await relatorioClient.get('/relatorios/dashboard-executivo')

      console.log('🔍 Debug - Resposta completa do relatório:', {
        status: response.status,
        statusText: response.statusText,
        headers: response.headers,
        dataSize: response.data?.size || 'unknown',
        dataType: response.data?.type || 'unknown',
        dataExists: !!response.data,
        isBlob: response.data instanceof Blob,
        constructor: response.data?.constructor?.name
      })

      // Verificar se a resposta existe
      if (!response.data) {
        console.error('❌ response.data é null ou undefined')
        throw new Error('Não foi possível gerar o relatório - dados ausentes')
      }

      // Para blob, verificar o tamanho
      if (response.data instanceof Blob) {
        if (response.data.size === 0) {
          console.error('❌ Blob está vazio (size = 0)')
          throw new Error('Não foi possível gerar o relatório - arquivo vazio')
        }
        console.log('✅ Blob válido com size:', response.data.size)
      }

      // Verificar se o status da resposta é ok
      if (response.status !== 200) {
        console.error('❌ Status HTTP não ok:', response.status, response.statusText)
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

      console.log('✅ Relatório de cotações gerado com sucesso')
      return true
    } catch (error) {
      console.error('❌ Erro ao gerar relatório de cotações:', error)

      // Log mais detalhado do erro
      if (error.response) {
        console.error('❌ Detalhes do erro de resposta:', {
          status: error.response.status,
          statusText: error.response.statusText,
          data: error.response.data,
          headers: error.response.headers
        })

        // Se a resposta for um blob de erro, tentar ler como texto
        if (error.response.data instanceof Blob) {
          try {
            const text = await error.response.data.text()
            console.error('❌ Conteúdo do erro (blob como texto):', text)
          } catch (e) {
            console.error('❌ Não foi possível ler o blob de erro:', e)
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
      console.error('Erro ao obter estatísticas:', error)
      throw error
    }
  },

  // Obter histórico de uma cotação
  async obterHistorico(id) {
    try {
      const response = await api.get(`${BASE_URL}/${id}/historico`)
      return response.data
    } catch (error) {
      console.error('Erro ao obter histórico:', error)
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
      console.error('Erro ao adicionar comentário:', error)
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
      console.error('Erro ao notificar fornecedores:', error)
      throw error
    }
  },

  // Validar cotação antes do envio
  async validar(cotacao) {
    try {
      const response = await api.post(`${BASE_URL}/validar`, cotacao)
      return response.data
    } catch (error) {
      console.error('Erro ao validar cotação:', error)
      throw error
    }
  },

  // Obter modelos de cotação
  async obterModelos() {
    try {
      const response = await api.get(`${BASE_URL}/modelos`)
      return response.data
    } catch (error) {
      console.error('Erro ao obter modelos:', error)
      throw error
    }
  },

  // Criar cotação a partir de modelo
  async criarDeModelo(modeloId, dados = {}) {
    try {
      const response = await api.post(`${BASE_URL}/modelos/${modeloId}/criar`, dados)
      return response.data
    } catch (error) {
      console.error('Erro ao criar cotação de modelo:', error)
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

      console.log('📤 Convertendo arquivo para bytes...')
      const bytesArray = await this.arquivoParaBytes(arquivo)

      console.log('📤 Fazendo upload do PDF para cotação:', cotacaoId)
      const response = await api.put(`${BASE_URL}/${cotacaoId}`, {
        anexoPdf: bytesArray
      })

      console.log('✅ Upload do PDF realizado com sucesso')
      return response

    } catch (error) {
      console.error('❌ Erro no upload do PDF:', error)
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
      console.error('❌ Erro ao verificar anexo:', error)
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
      console.error('❌ Erro ao obter informações do anexo:', error)
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
      console.error('Erro ao salvar como modelo:', error)
      throw error
    }
  }
}

export default cotacaoService
