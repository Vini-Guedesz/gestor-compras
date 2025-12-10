/**
 * Serviço de Cotações de Rascunho
 *
 * Gerencia todas as operações relacionadas às cotações de rascunhos
 */

import api from './api.js'
import logger from '../utils/logger.js'

// Configuração da URL base da API
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'

const cotacaoRascunhoService = {
  async listarPorRascunho(rascunhoId) {
    try {
      const data = await api.get(`/api/v1/rascunhos/${rascunhoId}/cotacoes`)
      return data
    } catch (error) {
      logger.error('Erro ao listar cotações do rascunho:', error.message)
      throw error
    }
  },

  async obterPorId(rascunhoId, cotacaoId) {
    try {
      const data = await api.get(`/api/v1/rascunhos/${rascunhoId}/cotacoes/${cotacaoId}`)
      return data
    } catch (error) {
      logger.error(`Erro ao obter cotação ${cotacaoId}:`, error.message)
      throw error
    }
  },

  async criar(rascunhoId, cotacao) {
    try {

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

      // Validar e processar anexo PDF se fornecido
      if (cotacao.anexoPdf) {
        if (cotacao.anexoPdf instanceof File) {
          // Validar tipo
          if (cotacao.anexoPdf.type !== 'application/pdf') {
            throw new Error('Apenas arquivos PDF são permitidos')
          }

          // Validar tamanho (10MB)
          const maxSize = 10 * 1024 * 1024
          if (cotacao.anexoPdf.size > maxSize) {
            throw new Error('Arquivo muito grande. Máximo permitido: 10MB')
          }

          // Converter para bytes
          const bytesArray = await this.arquivoParaBytes(cotacao.anexoPdf)
          cotacao.anexoPdf = bytesArray
        }
      }

      // Validar e processar anexos múltiplos se fornecidos
      if (cotacao.anexosPdf && Array.isArray(cotacao.anexosPdf)) {
        const anexosProcessados = []
        for (const anexo of cotacao.anexosPdf) {
          if (anexo instanceof File) {
            // Validar tipo
            if (anexo.type !== 'application/pdf') {
              throw new Error('Apenas arquivos PDF são permitidos')
            }

            // Validar tamanho (10MB)
            const maxSize = 10 * 1024 * 1024
            if (anexo.size > maxSize) {
              throw new Error(`Arquivo ${anexo.name} muito grande. Máximo permitido: 10MB`)
            }

            // Converter para bytes
            const bytesArray = await this.arquivoParaBytes(anexo)
            anexosProcessados.push(bytesArray)
          } else {
            // Se já for array de bytes, usar direto
            anexosProcessados.push(anexo)
          }
        }
        cotacao.anexosPdf = anexosProcessados
      }

      const data = await api.post(`/api/v1/rascunhos/${rascunhoId}/cotacoes`, cotacao)
      return data
    } catch (error) {
      logger.error('Erro ao criar cotação:', error.message)
      throw error
    }
  },

  async deletar(rascunhoId, cotacaoId) {
    try {
      await api.delete(`/api/v1/rascunhos/${rascunhoId}/cotacoes/${cotacaoId}`)
      return true
    } catch (error) {
      logger.error(`Erro ao remover cotação ${cotacaoId}:`, error.message)
      throw error
    }
  },

  async obterAnexoPdf(rascunhoId, cotacaoId, pdfIndex = 0) {
    try {

      // Obter o token de autenticação
      const token = sessionStorage.getItem('authToken')

      // Sempre usar o endpoint com índice para consistência
      const url = `${API_BASE_URL}/api/v1/rascunhos/${rascunhoId}/cotacoes/${cotacaoId}/anexo/${pdfIndex}`


      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': token ? `Bearer ${token}` : ''
        }
      })

      if (!response.ok) {
        throw new Error(`Erro ao buscar PDF: ${response.status}`)
      }

      const blob = await response.blob()
      return blob
    } catch (error) {
      logger.error(`Erro ao obter PDF da cotação ${cotacaoId}:`, error.message)
      throw error
    }
  },

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
  }
}

export default cotacaoRascunhoService
