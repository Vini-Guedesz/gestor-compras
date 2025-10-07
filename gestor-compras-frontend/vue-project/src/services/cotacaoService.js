import api from './api.js'

const BASE_URL = '/api/cotacoes'

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
      // Preparar dados para envio (CotacaoUpdateDTO)
      const payload = {
        preco: parseFloat(dadosCotacao.preco) || null,
        prazoEntrega: dadosCotacao.prazoEntrega || null,
        anexoPdf: null, // TODO: Implementar upload de PDF
        caminhoAnexo: dadosCotacao.caminhoAnexo || null
      }

      // MOCK: Simular resposta do backend
      console.log('📤 Atualizando cotação:', payload)
      await new Promise(resolve => setTimeout(resolve, 800))

      const mockResponse = {
        id: parseInt(id),
        ...payload,
        fornecedorId: dadosCotacao.fornecedorId,
        itemPedidoId: dadosCotacao.itemPedidoId,
        dataCotacao: new Date().toISOString().split('T')[0]
      }

      console.log('✅ Cotação atualizada (MOCK):', mockResponse)
      return mockResponse

      // TODO: Descomente quando o backend estiver funcionando
      // const response = await api.put(`${BASE_URL}/${id}`, payload)
      // return response

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

  // Exportar relatório PDF
  async exportarPDF(filtros = {}) {
    try {
      const params = new URLSearchParams()

      if (filtros.status) params.append('status', filtros.status)
      if (filtros.fornecedor) params.append('fornecedor', filtros.fornecedor)
      if (filtros.periodo) params.append('periodo', filtros.periodo)
      if (filtros.dataInicio) params.append('dataInicio', filtros.dataInicio)
      if (filtros.dataFim) params.append('dataFim', filtros.dataFim)

      const url = params.toString() ? `${BASE_URL}/relatorio/pdf?${params}` : `${BASE_URL}/relatorio/pdf`

      const response = await api.get(url, {
        responseType: 'blob'
      })

      // Criar link para download
      const blob = new Blob([response.data], { type: 'application/pdf' })
      const downloadUrl = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = `cotacoes_${new Date().toISOString().split('T')[0]}.pdf`
      link.click()
      window.URL.revokeObjectURL(downloadUrl)

      return true
    } catch (error) {
      console.error('Erro ao exportar PDF:', error)
      throw error
    }
  },

  // Exportar relatório Excel
  async exportarExcel(filtros = {}) {
    try {
      const params = new URLSearchParams()

      if (filtros.status) params.append('status', filtros.status)
      if (filtros.fornecedor) params.append('fornecedor', filtros.fornecedor)
      if (filtros.periodo) params.append('periodo', filtros.periodo)
      if (filtros.dataInicio) params.append('dataInicio', filtros.dataInicio)
      if (filtros.dataFim) params.append('dataFim', filtros.dataFim)

      const url = params.toString() ? `${BASE_URL}/relatorio/excel?${params}` : `${BASE_URL}/relatorio/excel`

      const response = await api.get(url, {
        responseType: 'blob'
      })

      // Criar link para download
      const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
      const downloadUrl = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = `cotacoes_${new Date().toISOString().split('T')[0]}.xlsx`
      link.click()
      window.URL.revokeObjectURL(downloadUrl)

      return true
    } catch (error) {
      console.error('Erro ao exportar Excel:', error)
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
