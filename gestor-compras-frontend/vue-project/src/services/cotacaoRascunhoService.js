/**
 * @fileoverview Serviço de Gerenciamento de Cotações de Rascunho
 *
 * Módulo responsável por operações relacionadas às cotações vinculadas a rascunhos.
 * Permite adicionar cotações a rascunhos antes de convertê-los em pedidos oficiais.
 *
 * @module services/cotacaoRascunhoService
 * @requires ./api
 * @requires ../utils/logger
 *
 * @description
 * Este serviço implementa:
 * - Listagem de cotações por rascunho
 * - Criação de cotações em rascunhos (com upload de PDF)
 * - Deleção de cotações de rascunhos
 * - Download de anexos PDF de cotações
 * - Conversão de arquivos para bytes (base64)
 * - Validação de dados (fornecedor, itens, preço, tipo fornecedor)
 * - Validação de arquivos PDF (tipo e tamanho máximo 10MB)
 *
 * @example
 * const cotacoes = await cotacaoRascunhoService.listarPorRascunho(5)
 *
 * @example
 * const novaCotacao = {
 *   fornecedorId: 10,
 *   tipoFornecedor: 'PRODUTO',
 *   itens: [
 *     { itemRascunhoId: 1, precoUnitario: 10.50, quantidade: 5 },
 *     { itemRascunhoId: 2, precoUnitario: 20.00, quantidade: 3 }
 *   ],
 *   anexoPdf: arquivoPdf
 * }
 * await cotacaoRascunhoService.criar(5, novaCotacao)
 *
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

import api from './api.js'
import logger from '../utils/logger.js'

/**
 * Serviço de cotações de rascunho
 * @namespace cotacaoRascunhoService
 */
const cotacaoRascunhoService = {
  /**
   * Lista todas as cotações vinculadas a um rascunho
   *
   * @async
   * @function listarPorRascunho
   * @memberof cotacaoRascunhoService
   * @param {number} rascunhoId - ID do rascunho
   * @returns {Promise<Array>} Array de cotações do rascunho
   * @throws {Error} Erro de comunicação com API
   *
   * @example
   * const cotacoes = await cotacaoRascunhoService.listarPorRascunho(5)
   */
  async listarPorRascunho(rascunhoId) {
    try {
      const data = await api.get(`/api/v1/rascunhos/${rascunhoId}/cotacoes`)
      return data
    } catch (error) {
      logger.error('Erro ao listar cotações do rascunho:', error.message)
      throw error
    }
  },

  /**
   * Obtém uma cotação específica de um rascunho
   *
   * @async
   * @function obterPorId
   * @memberof cotacaoRascunhoService
   * @param {number} rascunhoId - ID do rascunho
   * @param {number} cotacaoId - ID da cotação
   * @returns {Promise<Object>} Dados da cotação
   * @throws {Error} Erro 404 se cotação não encontrada
   *
   * @example
   * const cotacao = await cotacaoRascunhoService.obterPorId(5, 10)
   */
  async obterPorId(rascunhoId, cotacaoId) {
    try {
      const data = await api.get(`/api/v1/rascunhos/${rascunhoId}/cotacoes/${cotacaoId}`)
      return data
    } catch (error) {
      logger.error(`Erro ao obter cotação ${cotacaoId}:`, error.message)
      throw error
    }
  },

  /**
   * Cria uma nova cotação para um rascunho
   *
   * @async
   * @function criar
   * @memberof cotacaoRascunhoService
   * @param {number} rascunhoId - ID do rascunho
   * @param {Object} cotacao - Dados da cotação
   * @param {number} cotacao.fornecedorId - ID do fornecedor (obrigatório)
   * @param {string} cotacao.tipoFornecedor - Tipo: 'PRODUTO' ou 'SERVICO' (obrigatório)
   * @param {Array<Object>} cotacao.itens - Itens cotados com preço e quantidade (mínimo 1)
   * @param {File} [cotacao.anexoPdf] - Arquivo PDF anexo (máx 10MB)
   * @returns {Promise<Object>} Cotação criada
   * @throws {Error} Erro de validação ou comunicação
   *
   * @example
   * const cotacao = {
   *   fornecedorId: 10,
   *   tipoFornecedor: 'PRODUTO',
   *   itens: [
   *     { itemRascunhoId: 1, precoUnitario: 10.50, quantidade: 5 },
   *     { itemRascunhoId: 2, precoUnitario: 20.00, quantidade: 3 }
   *   ]
   * }
   * await cotacaoRascunhoService.criar(5, cotacao)
   *
   * @description
   * Validações aplicadas:
   * - Fornecedor obrigatório
   * - Mínimo 1 item selecionado
   * - Itens devem ter preço unitário e quantidade > 0
   * - Tipo fornecedor obrigatório
   * - PDF: apenas application/pdf, máx 10MB
   */
  async criar(rascunhoId, cotacao) {
    try {

      // Validar estrutura de dados
      if (!cotacao.fornecedorId) {
        throw new Error('Fornecedor é obrigatório')
      }
      if (!cotacao.itens || cotacao.itens.length === 0) {
        throw new Error('Informe pelo menos um item cotado')
      }
      if (!cotacao.tipoFornecedor) {
        throw new Error('Tipo do fornecedor é obrigatório')
      }

      const itensInvalidos = cotacao.itens.filter(item =>
        !item.itemRascunhoId ||
        !item.precoUnitario || item.precoUnitario <= 0 ||
        !item.quantidade || item.quantidade <= 0
      )
      if (itensInvalidos.length > 0) {
        throw new Error('Todos os itens devem ter preço unitário e quantidade válidos')
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

  /**
   * Deleta uma cotação de um rascunho
   *
   * @async
   * @function deletar
   * @memberof cotacaoRascunhoService
   * @param {number} rascunhoId - ID do rascunho
   * @param {number} cotacaoId - ID da cotação a deletar
   * @returns {Promise<boolean>} true se deleção bem-sucedida
   * @throws {Error} Erro 404 ou comunicação
   *
   * @example
   * await cotacaoRascunhoService.deletar(5, 10)
   */
  async deletar(rascunhoId, cotacaoId) {
    try {
      await api.delete(`/api/v1/rascunhos/${rascunhoId}/cotacoes/${cotacaoId}`)
      return true
    } catch (error) {
      logger.error('Erro ao deletar cotação:', error.message)
      throw error
    }
  },

  /**
   * Obtém anexo PDF de uma cotação de rascunho
   *
   * @async
   * @function obterAnexoPdf
   * @memberof cotacaoRascunhoService
   * @param {number} rascunhoId - ID do rascunho
   * @param {number} cotacaoId - ID da cotação
   * @param {number} [pdfIndex=0] - Índice do PDF (múltiplos anexos)
   * @returns {Promise<Blob>} Arquivo PDF como blob
   * @throws {Error} Erro 404 se PDF não encontrado
   *
   * @example
   * const pdfBlob = await cotacaoRascunhoService.obterAnexoPdf(5, 10)
   * const url = window.URL.createObjectURL(pdfBlob)
   * window.open(url, '_blank')
   *
   * @description
   * Faz download do anexo PDF e retorna como blob para visualização ou download.
   */
  async obterAnexoPdf(rascunhoId, cotacaoId, pdfIndex = 0) {
    try {
      const endpoint = `/api/v1/rascunhos/${rascunhoId}/cotacoes/${cotacaoId}/anexo/${pdfIndex}`
      const blob = await api.getBlob(endpoint)

      if (!blob || blob.size === 0) {
        throw new Error('PDF retornado vazio pelo servidor.')
      }

      return blob
    } catch (error) {
      if (String(error?.message || '').includes('404')) {
        throw new Error('PDF não encontrado para esta cotação de rascunho.')
      }
      logger.error(`Erro ao obter PDF da cotação ${cotacaoId}:`, error.message)
      throw error
    }
  },

  // Converter arquivo para array de bytes
  /**
   * Converte arquivo File para array de bytes (base64)
   *
   * @async
   * @function arquivoParaBytes
   * @memberof cotacaoRascunhoService
   * @param {File} arquivo - Arquivo a ser convertido
   * @returns {Promise<Array<number>>} Array de bytes do arquivo
   * @throws {Error} Erro ao ler arquivo
   *
   * @example
   * const bytes = await cotacaoRascunhoService.arquivoParaBytes(pdfFile)
   *
   * @description
   * Utilitário interno para converter File em array de bytes.
   * Usa FileReader para ler o arquivo como ArrayBuffer e converter em array de números.
   */
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

/**
 * @exports cotacaoRascunhoService
 * @default
 */
export default cotacaoRascunhoService
