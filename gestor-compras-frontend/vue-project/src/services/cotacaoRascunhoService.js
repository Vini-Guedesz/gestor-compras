/**
 * Serviço de Cotações de Rascunho
 *
 * Gerencia todas as operações relacionadas às cotações de rascunhos
 */

import api from './api.js'

const cotacaoRascunhoService = {
  async listarPorRascunho(rascunhoId) {
    try {
      console.log(`Buscando cotações do rascunho ${rascunhoId}...`)
      const data = await api.get(`/api/rascunhos/${rascunhoId}/cotacoes`)
      console.log('Cotações do rascunho carregadas:', data.length)
      return data
    } catch (error) {
      console.error('Erro ao listar cotações do rascunho:', error.message)
      throw error
    }
  },

  async obterPorId(rascunhoId, cotacaoId) {
    try {
      console.log(`Buscando cotação ${cotacaoId} do rascunho ${rascunhoId}...`)
      const data = await api.get(`/api/rascunhos/${rascunhoId}/cotacoes/${cotacaoId}`)
      console.log('Cotação carregada')
      return data
    } catch (error) {
      console.error(`Erro ao obter cotação ${cotacaoId}:`, error.message)
      throw error
    }
  },

  async criar(rascunhoId, cotacao) {
    try {
      console.log(`Criando cotação para o rascunho ${rascunhoId}...`)
      console.log('📦 Dados da cotação a enviar:', JSON.stringify(cotacao, null, 2))

      // Validar estrutura de dados
      if (!cotacao.fornecedorId) {
        throw new Error('Fornecedor é obrigatório')
      }
      if (!cotacao.itensRascunhoIds || cotacao.itensRascunhoIds.length === 0) {
        throw new Error('Selecione pelo menos um item')
      }
      if (!cotacao.preco || cotacao.preco <= 0) {
        throw new Error('Preço deve ser maior que zero')
      }
      if (!cotacao.tipoFornecedor) {
        throw new Error('Tipo do fornecedor é obrigatório')
      }

      const data = await api.post(`/api/rascunhos/${rascunhoId}/cotacoes`, cotacao)
      console.log('Cotação criada:', data.id)
      return data
    } catch (error) {
      console.error('Erro ao criar cotação:', error.message)
      throw error
    }
  },

  async deletar(rascunhoId, cotacaoId) {
    try {
      console.log(`Removendo cotação ${cotacaoId} do rascunho ${rascunhoId}...`)
      await api.delete(`/api/rascunhos/${rascunhoId}/cotacoes/${cotacaoId}`)
      console.log('Cotação removida')
      return true
    } catch (error) {
      console.error(`Erro ao remover cotação ${cotacaoId}:`, error.message)
      throw error
    }
  },

  async obterAnexoPdf(rascunhoId, cotacaoId) {
    try {
      console.log(`Buscando PDF da cotação ${cotacaoId}...`)
      const response = await api.get(`/api/rascunhos/${rascunhoId}/cotacoes/${cotacaoId}/anexo`, {
        responseType: 'blob'
      })
      return response
    } catch (error) {
      console.error(`Erro ao obter PDF da cotação ${cotacaoId}:`, error.message)
      throw error
    }
  }
}

export default cotacaoRascunhoService
