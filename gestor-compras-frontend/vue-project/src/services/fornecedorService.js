/**
 * @fileoverview Serviço de Gerenciamento de Fornecedores
 *
 * Módulo responsável por todas as operações CRUD e lógica de negócios relacionadas
 * aos fornecedores de produtos e serviços. O sistema diferencia dois tipos de fornecedores:
 * - Fornecedores de Produto: Fornecem bens materiais
 * - Fornecedores de Serviço: Fornecem serviços
 *
 * @module services/fornecedorService
 * @requires ./api
 * @requires ../utils/logger
 *
 * @description
 * Este serviço implementa:
 * - CRUD completo para Fornecedores de Produto
 * - CRUD completo para Fornecedores de Serviço
 * - Métodos auxiliares para listagem combinada (produto + serviço)
 * - Tratamento especializado de erros de validação (400 Bad Request)
 * - Extração de dados de respostas paginadas do Spring Data
 * - Utilitários para formatação e validação de dados (CNPJ, CEP, telefone, email)
 *
 * @example
 * // Listar fornecedores de produto
 * const fornecedoresProduto = await fornecedorService.listarFornecedoresProduto()
 *
 * @example
 * // Criar novo fornecedor de serviço
 * const novoFornecedor = {
 *   nome: 'Empresa XYZ',
 *   cnpj: '12.345.678/0001-90',
 *   email: 'contato@xyz.com',
 *   telefone: '(11) 98765-4321'
 * }
 * await fornecedorService.criarFornecedorServico(novoFornecedor)
 *
 * @example
 * // Listar todos para cotação (com marcação de tipo)
 * const fornecedores = await fornecedorService.listarParaCotacao()
 * fornecedores.forEach(f => console.log(f.nome, f.tipo)) // tipo: 'PRODUTO' ou 'SERVICO'
 *
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

import api from './api'
import logger from '../utils/logger.js'

/**
 * Tipo de fornecedor
 * @typedef {'PRODUTO'|'SERVICO'} TipoFornecedor
 */

/**
 * Fornecedor de Produto ou Serviço
 * @typedef {Object} Fornecedor
 * @property {number} [id] - ID do fornecedor
 * @property {string} nome - Nome/Razão social
 * @property {string} cnpj - CNPJ formatado ou apenas números
 * @property {string} [email] - Email de contato
 * @property {string} [telefone] - Telefone de contato
 * @property {string} [endereco] - Endereço completo
 * @property {string} [cep] - CEP
 * @property {TipoFornecedor} [tipo] - Tipo do fornecedor (adicionado em métodos auxiliares)
 */

/**
 * Erro de validação com detalhes estruturados
 * @typedef {Error} ValidationError
 * @property {string} type - Sempre 'VALIDATION_ERROR'
 * @property {Object|string} details - Detalhes dos erros de validação
 */

/**
 * Extrai conteúdo de respostas paginadas do Spring Data
 *
 * @function extractContent
 * @param {Object|Array<*>} response - Resposta da API
 * @returns {Array<*>} Array de elementos extraídos
 *
 * @description
 * Helper para normalizar respostas da API. O Spring Data REST retorna objetos
 * paginados com estrutura { content: [], totalPages, totalElements, ... }.
 * Esta função extrai o array 'content' ou retorna array vazio se não encontrado.
 *
 * @example
 * const response = { content: [fornecedor1, fornecedor2], totalPages: 1 }
 * const fornecedores = extractContent(response) // [fornecedor1, fornecedor2]
 */
const extractContent = (response) => {
  if (response && typeof response === 'object' && 'content' in response) {
    return response.content || []
  }
  return Array.isArray(response) ? response : []
}

/**
 * Serviço principal de gerenciamento de fornecedores
 * @namespace fornecedorService
 */
const fornecedorService = {
  // ==================== FORNECEDORES DE PRODUTO ====================
  /**
   * Lista todos os fornecedores de produto
   *
   * @async
   * @function listarFornecedoresProduto
   * @memberof fornecedorService
   * @returns {Promise<Array<Fornecedor>>} Array de fornecedores de produto
   * @throws {Error} Erro de comunicação com API
   *
   * @example
   * const fornecedores = await fornecedorService.listarFornecedoresProduto()
   * console.log(`Total: ${fornecedores.length}`)
   */
  async listarFornecedoresProduto() {
    try {
      const data = await api.get('/api/v1/fornecedores-de-produto')
      return extractContent(data)
    } catch (error) {
      logger.error('❌ Erro ao listar fornecedores de produto no backend:', error.message)
      throw error
    }
  },

  /**
   * Obtém um fornecedor de produto específico por ID
   *
   * @async
   * @function obterFornecedorProdutoPorId
   * @memberof fornecedorService
   * @param {number} id - ID do fornecedor
   * @returns {Promise<Fornecedor>} Dados completos do fornecedor
   * @throws {Error} Erro 404 se fornecedor não encontrado
   *
   * @example
   * const fornecedor = await fornecedorService.obterFornecedorProdutoPorId(123)
   */
  async obterFornecedorProdutoPorId(id) {
    try {
      const data = await api.get(`/api/v1/fornecedores-de-produto/${id}`)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao obter fornecedor de produto ID ${id} no backend:`, error.message)
      throw error
    }
  },

  /**
   * Cria um novo fornecedor de produto
   *
   * @async
   * @function criarFornecedorProduto
   * @memberof fornecedorService
   * @param {Fornecedor} fornecedor - Dados do fornecedor
   * @returns {Promise<Fornecedor>} Fornecedor criado
   * @throws {ValidationError} Erro de validação (status 400)
   * @throws {Error} Outros erros de comunicação
   *
   * @example
   * const novoFornecedor = {
   *   nome: 'Fornecedor ABC',
   *   cnpj: '12345678000190',
   *   email: 'contato@abc.com',
   *   telefone: '11987654321'
   * }
   * const criado = await fornecedorService.criarFornecedorProduto(novoFornecedor)
   *
   * @description
   * Em caso de erro de validação (400), lança ValidationError com:
   * - message: Mensagem formatada amigável
   * - type: 'VALIDATION_ERROR'
   * - details: Objeto/string com detalhes do backend
   */
  async criarFornecedorProduto(fornecedor) {
    try {

      const data = await api.post('/api/v1/fornecedores-de-produto', fornecedor)
      return data
    } catch (error) {
      logger.error('❌ Erro ao criar fornecedor de produto no backend:', error.message)

      // Tratamento específico para erros de validação
      if (error.response && error.response.status === 400) {
        const validationErrors = error.response.data
        logger.error('📛 Erros de validação:', validationErrors)

        // Criar mensagem mais amigável baseada nos erros de validação
        let friendlyMessage = 'Erro de validação:\n'
        if (typeof validationErrors === 'object') {
          Object.keys(validationErrors).forEach(field => {
            friendlyMessage += `• ${field}: ${validationErrors[field]}\n`
          })
        } else if (typeof validationErrors === 'string') {
          friendlyMessage = validationErrors
        }

        const enhancedError = new Error(friendlyMessage)
        enhancedError.type = 'VALIDATION_ERROR'
        enhancedError.details = validationErrors
        throw enhancedError
      }

      throw error
    }
  },

  /**
   * Atualiza um fornecedor de produto existente
   *
   * @async
   * @function atualizarFornecedorProduto
   * @memberof fornecedorService
   * @param {number} id - ID do fornecedor
   * @param {Fornecedor} fornecedor - Dados atualizados (ID será adicionado automaticamente)
   * @returns {Promise<Fornecedor>} Fornecedor atualizado
   * @throws {Error} Erro 404 se fornecedor não encontrado
   *
   * @example
   * const fornecedorAtualizado = { nome: 'Novo Nome', cnpj: '...' }
   * await fornecedorService.atualizarFornecedorProduto(123, fornecedorAtualizado)
   *
   * @description
   * O ID é automaticamente incluído no body da requisição conforme exigido pelo DTO.
   */
  async atualizarFornecedorProduto(id, fornecedor) {
    try {
      // O ID deve ser incluído no body da requisição conforme o DTO
      const fornecedorComId = { ...fornecedor, id: id }
      const data = await api.put(`/api/v1/fornecedores-de-produto`, fornecedorComId)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao atualizar fornecedor de produto ID ${id} no backend:`, error.message)
      throw error
    }
  },

  /**
   * Remove (deleta) um fornecedor de produto
   *
   * @async
   * @function removerFornecedorProduto
   * @memberof fornecedorService
   * @param {number} id - ID do fornecedor a ser removido
   * @returns {Promise<boolean>} true se remoção bem-sucedida
   * @throws {Error} Erro 404 se fornecedor não encontrado
   *
   * @example
   * await fornecedorService.removerFornecedorProduto(123)
   */
  async removerFornecedorProduto(id) {
    try {
      await api.delete(`/api/v1/fornecedores-de-produto/${id}`)
      return true
    } catch (error) {
      logger.error(`❌ Erro ao remover fornecedor de produto ID ${id} no backend:`, error.message)
      throw error
    }
  },

  // ==================== FORNECEDORES DE SERVIÇO ====================
  /**
   * Lista todos os fornecedores de serviço
   *
   * @async
   * @function listarFornecedoresServico
   * @memberof fornecedorService
   * @returns {Promise<Array<Fornecedor>>} Array de fornecedores de serviço
   * @throws {Error} Erro de comunicação com API
   *
   * @example
   * const fornecedores = await fornecedorService.listarFornecedoresServico()
   */
  async listarFornecedoresServico() {
    try {
      const data = await api.get('/api/v1/fornecedores-de-servico')
      return extractContent(data)
    } catch (error) {
      logger.error('❌ Erro ao listar fornecedores de serviço no backend:', error.message)
      throw error
    }
  },

  /**
   * Obtém um fornecedor de serviço específico por ID
   *
   * @async
   * @function obterFornecedorServicoPorId
   * @memberof fornecedorService
   * @param {number} id - ID do fornecedor
   * @returns {Promise<Fornecedor>} Dados completos do fornecedor
   * @throws {Error} Erro 404 se fornecedor não encontrado
   *
   * @example
   * const fornecedor = await fornecedorService.obterFornecedorServicoPorId(456)
   */
  async obterFornecedorServicoPorId(id) {
    try {
      const data = await api.get(`/api/v1/fornecedores-de-servico/${id}`)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao obter fornecedor de serviço ID ${id} no backend:`, error.message)
      throw error
    }
  },

  /**
   * Cria um novo fornecedor de serviço
   *
   * @async
   * @function criarFornecedorServico
   * @memberof fornecedorService
   * @param {Fornecedor} fornecedor - Dados do fornecedor
   * @returns {Promise<Fornecedor>} Fornecedor criado
   * @throws {ValidationError} Erro de validação (status 400)
   * @throws {Error} Outros erros de comunicação
   *
   * @example
   * const novoFornecedor = {
   *   nome: 'Serviços ABC',
   *   cnpj: '98765432000199',
   *   email: 'contato@servicos.com',
   *   telefone: '11987654321'
   * }
   * const criado = await fornecedorService.criarFornecedorServico(novoFornecedor)
   *
   * @description
   * Em caso de erro de validação (400), lança ValidationError com mensagem amigável.
   */
  async criarFornecedorServico(fornecedor) {
    try {
      const data = await api.post('/api/v1/fornecedores-de-servico', fornecedor)
      return data
    } catch (error) {
      logger.error('❌ Erro ao criar fornecedor de serviço no backend:', error.message)

      // Tratamento específico para erros de validação
      if (error.response && error.response.status === 400) {
        const validationErrors = error.response.data
        logger.error('📛 Erros de validação:', validationErrors)

        // Criar mensagem mais amigável baseada nos erros de validação
        let friendlyMessage = 'Erro de validação:\n'
        if (typeof validationErrors === 'object') {
          Object.keys(validationErrors).forEach(field => {
            friendlyMessage += `• ${field}: ${validationErrors[field]}\n`
          })
        } else if (typeof validationErrors === 'string') {
          friendlyMessage = validationErrors
        }

        const enhancedError = new Error(friendlyMessage)
        enhancedError.type = 'VALIDATION_ERROR'
        enhancedError.details = validationErrors
        throw enhancedError
      }

      throw error
    }
  },

  /**
   * Atualiza um fornecedor de serviço existente
   *
   * @async
   * @function atualizarFornecedorServico
   * @memberof fornecedorService
   * @param {number} id - ID do fornecedor
   * @param {Fornecedor} fornecedor - Dados atualizados (ID será adicionado automaticamente)
   * @returns {Promise<Fornecedor>} Fornecedor atualizado
   * @throws {ValidationError} Erro de validação (status 400)
   * @throws {Error} Erro 404 se fornecedor não encontrado
   *
   * @example
   * const fornecedorAtualizado = { nome: 'Novo Nome Serviços', email: 'novo@email.com' }
   * await fornecedorService.atualizarFornecedorServico(456, fornecedorAtualizado)
   *
   * @description
   * O ID é automaticamente incluído no body da requisição conforme exigido pelo DTO.
   */
  /**
   * Atualiza um fornecedor de serviço existente
   *
   * @async
   * @function atualizarFornecedorServico
   * @memberof fornecedorService
   * @param {number} id - ID do fornecedor
   * @param {Fornecedor} fornecedor - Dados atualizados
   * @returns {Promise<Fornecedor>} Fornecedor atualizado
   * @throws {ValidationError} Erro de validação (status 400)
   * @throws {Error} Outros erros
   *
   * @example
   * const dados = { nome: 'Nome Atualizado', cnpj: '...' }
   * await fornecedorService.atualizarFornecedorServico(456, dados)
   */
  async atualizarFornecedorServico(id, fornecedor) {
    try {

      // O ID deve ser incluído no body da requisição conforme o DTO
      const fornecedorComId = { ...fornecedor, id: id }
      const data = await api.put(`/api/v1/fornecedores-de-servico`, fornecedorComId)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao atualizar fornecedor de serviço ID ${id} no backend:`, error.message)

      // Tratamento específico para erros de validação
      if (error.response && error.response.status === 400) {
        const validationErrors = error.response.data
        logger.error('📛 Erros de validação:', validationErrors)

        // Criar mensagem mais amigável baseada nos erros de validação
        let friendlyMessage = 'Erro de validação:\n'
        if (typeof validationErrors === 'object') {
          Object.keys(validationErrors).forEach(field => {
            friendlyMessage += `• ${field}: ${validationErrors[field]}\n`
          })
        } else if (typeof validationErrors === 'string') {
          friendlyMessage = validationErrors
        }

        const enhancedError = new Error(friendlyMessage)
        enhancedError.type = 'VALIDATION_ERROR'
        enhancedError.details = validationErrors
        throw enhancedError
      }

      throw error
    }
  },

  /**
   * Remove (deleta) um fornecedor de serviço
   *
   * @async
   * @function removerFornecedorServico
   * @memberof fornecedorService
   * @param {number} id - ID do fornecedor a ser removido
   * @returns {Promise<boolean>} true se remoção bem-sucedida
   * @throws {Error} Erro 404 se fornecedor não encontrado
   *
   * @example
   * await fornecedorService.removerFornecedorServico(456)
   */
  async removerFornecedorServico(id) {
    try {
      await api.delete(`/api/v1/fornecedores-de-servico/${id}`)
      return true
    } catch (error) {
      logger.error(`❌ Erro ao remover fornecedor de serviço ID ${id} no backend:`, error.message)
      throw error
    }
  },

  // ==================== MÉTODOS AUXILIARES ====================
  /**
   * Lista todos os fornecedores (produto + serviço) sem marcação de tipo
   *
   * @async
   * @function listarTodos
   * @memberof fornecedorService
   * @returns {Promise<Array<Fornecedor>>} Array combinado de todos os fornecedores
   * @throws {Error} Erro de comunicação com API
   *
   * @example
   * const todosFornecedores = await fornecedorService.listarTodos()
   * console.log(`Total: ${todosFornecedores.length}`)
   *
   * @description
   * Faz requisições paralelas aos endpoints de produto e serviço, retornando array único.
   */
  async listarTodos() {
    try {
      const [produtos, servicos] = await Promise.all([
        this.listarFornecedoresProduto(),
        this.listarFornecedoresServico()
      ])
      return [...produtos, ...servicos]
    } catch (error) {
      logger.error('❌ Erro ao listar todos os fornecedores:', error.message)
      throw error
    }
  },

  /**
   * Lista fornecedores de produto com marcação de tipo 'PRODUTO'
   *
   * @async
   * @function listarProdutos
   * @memberof fornecedorService
   * @returns {Promise<Array<Fornecedor>>} Fornecedores com tipo='PRODUTO'
   * @throws {Error} Erro de comunicação com API
   *
   * @example
   * const produtos = await fornecedorService.listarProdutos()
   * produtos.forEach(f => console.log(f.nome, f.tipo)) // tipo='PRODUTO'
   *
   * @description
   * Útil para cotações onde é necessário diferenciar o tipo de fornecedor.
   */
  async listarProdutos() {
    try {
      const produtos = await this.listarFornecedoresProduto()
      return produtos.map(fornecedor => ({
        ...fornecedor,
        tipo: 'PRODUTO'
      }))
    } catch (error) {
      logger.error('❌ Erro ao listar fornecedores de produto:', error.message)
      throw error
    }
  },

  /**
   * Lista fornecedores de serviço com marcação de tipo 'SERVICO'
   *
   * @async
   * @function listarServicos
   * @memberof fornecedorService
   * @returns {Promise<Array<Fornecedor>>} Fornecedores com tipo='SERVICO'
   * @throws {Error} Erro de comunicação com API
   *
   * @example
   * const servicos = await fornecedorService.listarServicos()
   * servicos.forEach(f => console.log(f.nome, f.tipo)) // tipo='SERVICO'
   *
   * @description
   * Útil para cotações onde é necessário diferenciar o tipo de fornecedor.
   */
  async listarServicos() {
    try {
      const servicos = await this.listarFornecedoresServico()
      return servicos.map(fornecedor => ({
        ...fornecedor,
        tipo: 'SERVICO'
      }))
    } catch (error) {
      logger.error('❌ Erro ao listar fornecedores de serviço:', error.message)
      throw error
    }
  },

  /**
   * Lista todos os fornecedores (produto + serviço) com marcação de tipo
   *
   * @async
   * @function listarParaCotacao
   * @memberof fornecedorService
   * @returns {Promise<Array<Fornecedor>>} Fornecedores com propriedade 'tipo' ('PRODUTO' ou 'SERVICO')
   * @throws {Error} Erro de comunicação com API
   *
   * @example
   * const fornecedores = await fornecedorService.listarParaCotacao()
   * fornecedores.forEach(f => {
   *   console.log(`${f.nome} - Tipo: ${f.tipo}`)
   * })
   *
   * @description
   * Método principal para uso em cotações. Combina produto e serviço com tipo diferenciado.
   */
  async listarParaCotacao() {
    try {
      const [produtos, servicos] = await Promise.all([
        this.listarProdutos(),
        this.listarServicos()
      ])
      return [...produtos, ...servicos]
    } catch (error) {
      logger.error('❌ Erro ao listar fornecedores para cotação:', error.message)
      throw error
    }
  },

  /**
   * Alias para listarTodos() - mantido para compatibilidade
   *
   * @async
   * @function listar
   * @memberof fornecedorService
   * @returns {Promise<Array<Fornecedor>>} Array de todos os fornecedores
   *
   * @example
   * const fornecedores = await fornecedorService.listar()
   */
  async listar() {
    return this.listarTodos()
  },

  /**
   * Alias para listarTodos() - mantido para compatibilidade
   *
   * @async
   * @function listarFornecedores
   * @memberof fornecedorService
   * @returns {Promise<Array<Fornecedor>>} Array de todos os fornecedores
   *
   * @example
   * const fornecedores = await fornecedorService.listarFornecedores()
   */
  async listarFornecedores() {
    return this.listarTodos()
  }
}

/**
 * Utilitários para formatação e validação de dados de fornecedores
 *
 * @namespace fornecedorUtils
 * @description
 * Conjunto de funções utilitárias para formatação e validação de:
 * - CNPJ (formatação e validação básica)
 * - CEP (formatação)
 * - Telefone (formatação para celular e fixo)
 * - Email (validação com regex)
 * - Listagem combinada para cotações (produto + serviço)
 */
export const fornecedorUtils = {
  /**
   * Formata CNPJ no padrão XX.XXX.XXX/XXXX-XX
   *
   * @function formatarCNPJ
   * @memberof fornecedorUtils
   * @param {string} cnpj - CNPJ apenas com números
   * @returns {string} CNPJ formatado ou string vazia
   *
   * @example
   * fornecedorUtils.formatarCNPJ('12345678000190') // '12.345.678/0001-90'
   */
  formatarCNPJ(cnpj) {
    if (!cnpj) return ''
    return cnpj.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5')
  },

  /**
   * Formata CEP no padrão XXXXX-XXX
   *
   * @function formatarCEP
   * @memberof fornecedorUtils
   * @param {string} cep - CEP apenas com números
   * @returns {string} CEP formatado ou string vazia
   *
   * @example
   * fornecedorUtils.formatarCEP('01310100') // '01310-100'
   */
  formatarCEP(cep) {
    if (!cep) return ''
    return cep.replace(/(\d{5})(\d{3})/, '$1-$2')
  },

  /**
   * Formata telefone no padrão (XX) XXXXX-XXXX ou (XX) XXXX-XXXX
   *
   * @function formatarTelefone
   * @memberof fornecedorUtils
   * @param {string} telefone - Telefone apenas com números
   * @returns {string} Telefone formatado ou string original se não corresponder ao padrão
   *
   * @example
   * fornecedorUtils.formatarTelefone('11987654321') // '(11) 98765-4321'
   * fornecedorUtils.formatarTelefone('1133334444')  // '(11) 3333-4444'
   *
   * @description
   * Suporta:
   * - Celular (11 dígitos): (XX) XXXXX-XXXX
   * - Fixo (10 dígitos): (XX) XXXX-XXXX
   */
  formatarTelefone(telefone) {
    if (!telefone) return ''
    if (telefone.length === 11) {
      return telefone.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3')
    }
    if (telefone.length === 10) {
      return telefone.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3')
    }
    return telefone
  },

  /**
   * Valida CNPJ (validação básica de tamanho)
   *
   * @function validarCNPJ
   * @memberof fornecedorUtils
   * @param {string} cnpj - CNPJ a ser validado
   * @returns {boolean} true se CNPJ tem 14 dígitos numéricos
   *
   * @example
   * fornecedorUtils.validarCNPJ('12.345.678/0001-90') // true
   * fornecedorUtils.validarCNPJ('123')                 // false
   *
   * @description
   * Realiza validação básica de tamanho (14 dígitos).
   * Não valida dígitos verificadores.
   */
  validarCNPJ(cnpj) {
    if (!cnpj) return false
    const numbers = cnpj.replace(/\D/g, '')
    return numbers.length === 14
  },

  /**
   * Valida formato de email
   *
   * @function validarEmail
   * @memberof fornecedorUtils
   * @param {string} email - Email a ser validado
   * @returns {boolean} true se email tem formato válido
   *
   * @example
   * fornecedorUtils.validarEmail('contato@empresa.com') // true
   * fornecedorUtils.validarEmail('invalido@')          // false
   *
   * @description
   * Valida formato básico: texto@texto.texto
   * Não verifica se o domínio existe.
   */
  validarEmail(email) {
    if (!email) return false
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return emailRegex.test(email)
  },

  /**
   * Lista todos os fornecedores (produto + serviço) com marcação de tipo
   *
   * @async
   * @function listarParaCotacao
   * @memberof fornecedorUtils
   * @returns {Promise<Array<Fornecedor>>} Fornecedores com propriedade 'tipo'
   * @throws {Error} Erro de comunicação com API
   *
   * @example
   * const fornecedores = await fornecedorUtils.listarParaCotacao()
   * fornecedores.forEach(f => {
   *   console.log(`${f.nome} - Tipo: ${f.tipo}`) // tipo: 'PRODUTO' ou 'SERVICO'
   * })
   *
   * @description
   * ATENÇÃO: Este método é duplicado de fornecedorService.listarParaCotacao().
   * Recomenda-se usar fornecedorService.listarParaCotacao() ao invés deste.
   * Mantido aqui por compatibilidade com código legado.
   */
  async listarParaCotacao() {
    try {

      const [fornecedoresProduto, fornecedoresServico] = await Promise.all([
        this.listarFornecedoresProduto(),
        this.listarFornecedoresServico()
      ])

      // Adicionar tipo aos fornecedores
      const fornecedoresComTipo = [
        ...(fornecedoresProduto || []).map(f => ({ ...f, tipo: 'PRODUTO' })),
        ...(fornecedoresServico || []).map(f => ({ ...f, tipo: 'SERVICO' }))
      ]


      return fornecedoresComTipo
    } catch (error) {
      logger.error('❌ Erro ao carregar fornecedores para cotação:', error)
      throw error
    }
  }
}

/**
 * @exports fornecedorService
 * @exports fornecedorUtils
 */
export default fornecedorService
