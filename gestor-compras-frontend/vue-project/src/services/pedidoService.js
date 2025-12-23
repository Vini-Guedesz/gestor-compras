/**
 * @fileoverview Serviço de Gerenciamento de Pedidos de Compra
 *
 * Módulo responsável por todas as operações CRUD e lógica de negócios relacionadas
 * aos pedidos de compra (solicitações de pedido). Fornece interface unificada para
 * comunicação com o backend, validações de dados, transformações e utilitários
 * auxiliares para manipulação de pedidos.
 *
 * @module services/pedidoService
 * @requires ./api
 * @requires ../utils/logger
 *
 * @description
 * Este serviço implementa:
 * - CRUD completo de pedidos (criar, listar, buscar, atualizar, deletar)
 * - Alteração de status (aprovar, rejeitar, cancelar)
 * - Validações de regras de negócio (itens obrigatórios, limites de caracteres)
 * - Extração de dados de respostas paginadas do Spring Data
 * - Múltiplos aliases de métodos para retrocompatibilidade
 * - Utilitários para cálculo de totais, formatação e manipulação de status
 *
 * @example
 * // Listar todos os pedidos
 * const pedidos = await pedidoService.listar()
 *
 * @example
 * // Criar novo pedido
 * const novoPedido = {
 *   observacao: 'Urgente',
 *   itens: [
 *     { nome: 'Notebook', quantidade: 5, descricao: 'Dell XPS 15' }
 *   ]
 * }
 * const resultado = await pedidoService.criar(novoPedido)
 *
 * @example
 * // Aprovar pedido
 * await pedidoService.aprovar(123)
 *
 * @example
 * // Calcular total do pedido
 * const total = pedidoUtils.calcularTotalPedido(pedido)
 *
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

import api from './api.js'
import logger from '../utils/logger.js'

/**
 * Tipo de resposta paginada do Spring Data
 * @typedef {Object} SpringPageResponse
 * @property {Array<*>} content - Array de elementos da página
 * @property {number} totalPages - Total de páginas
 * @property {number} totalElements - Total de elementos
 * @property {number} size - Tamanho da página
 * @property {number} number - Número da página atual (0-indexed)
 */

/**
 * Item de um pedido de compra
 * @typedef {Object} ItemPedido
 * @property {number} [id] - ID do item (presente em itens já salvos)
 * @property {string} nome - Nome do produto (máx. 255 caracteres)
 * @property {number} quantidade - Quantidade solicitada (> 0)
 * @property {string} [descricao] - Descrição detalhada (máx. 500 caracteres)
 * @property {string} [observacao] - Observações adicionais (máx. 255 caracteres)
 * @property {number} [preco] - Preço unitário estimado
 */

/**
 * Pedido de compra (Solicitação de Pedido)
 * @typedef {Object} Pedido
 * @property {number} [id] - ID do pedido (presente em pedidos já salvos)
 * @property {string} [observacao] - Observação geral do pedido (máx. 1000 caracteres)
 * @property {string} [status] - Status do pedido (PENDENTE, APROVADO, REJEITADO, etc.)
 * @property {Array<ItemPedido>} itens - Lista de itens do pedido (mínimo 1)
 * @property {string} [dataCriacao] - Data de criação (ISO 8601)
 * @property {string} [dataAtualizacao] - Data da última atualização (ISO 8601)
 */

/**
 * Configuração de status com informações de apresentação
 * @typedef {Object} StatusConfig
 * @property {string} label - Label formatado para exibição
 * @property {string} class - Classe CSS para estilização (warning, success, danger, etc.)
 */

/**
 * Extrai conteúdo de respostas paginadas do Spring Data
 *
 * @function extractContent
 * @param {SpringPageResponse|Array<*>|*} response - Resposta da API
 * @returns {Array<*>} Array de elementos extraídos
 *
 * @description
 * Helper para normalizar respostas da API. O Spring Data REST retorna objetos
 * paginados com estrutura { content: [], totalPages, totalElements, ... }.
 * Esta função extrai o array 'content' ou retorna array vazio se não encontrado.
 *
 * @example
 * const response = { content: [pedido1, pedido2], totalPages: 1 }
 * const pedidos = extractContent(response) // [pedido1, pedido2]
 *
 * @example
 * const response = [pedido1, pedido2]
 * const pedidos = extractContent(response) // [pedido1, pedido2]
 */
const extractContent = (response) => {
  if (response && typeof response === 'object' && 'content' in response) {
    return response.content || []
  }
  return Array.isArray(response) ? response : []
}


/**
 * Serviço principal de gerenciamento de pedidos
 * @namespace pedidoService
 */
const pedidoService = {
  /**
   * Lista todos os pedidos de compra
   *
   * @async
   * @function listar
   * @memberof pedidoService
   * @returns {Promise<Array<Pedido>>} Array de pedidos
   * @throws {Error} Erro de comunicação com API
   *
   * @example
   * const pedidos = await pedidoService.listar()
   * console.log(`Total de pedidos: ${pedidos.length}`)
   *
   * @description
   * Busca todos os pedidos no endpoint /api/v1/solicitacoes-pedido.
   * Utiliza extractContent para normalizar resposta paginada do Spring Data.
   */
  async listar() {
    try {
      const data = await api.get(`/api/v1/solicitacoes-pedido`)
      return extractContent(data)
    } catch (error) {
      logger.error('❌ Erro ao listar pedidos no backend:', error.message)
      throw error
    }
  },

  /**
   * Alias para listar() - Mantido para retrocompatibilidade
   * @async
   * @function listarTodos
   * @memberof pedidoService
   * @returns {Promise<Array<Pedido>>} Array de pedidos
   * @see {@link pedidoService.listar}
   */
  async listarTodos() {
    return this.listar()
  },

  /**
   * Alias para listar() - Mantido para retrocompatibilidade
   * @async
   * @function listarPedidos
   * @memberof pedidoService
   * @returns {Promise<Array<Pedido>>} Array de pedidos
   * @see {@link pedidoService.listar}
   */
  async listarPedidos() {
    return this.listar()
  },

  /**
   * Obtém um pedido específico por ID
   *
   * @async
   * @function obterPorId
   * @memberof pedidoService
   * @param {number} id - ID do pedido
   * @returns {Promise<Pedido>} Dados completos do pedido
   * @throws {Error} Erro 404 se pedido não encontrado ou erro de comunicação
   *
   * @example
   * const pedido = await pedidoService.obterPorId(123)
   * console.log('Status:', pedido.status)
   */
  async obterPorId(id) {
    try {
      const data = await api.get(`/api/v1/solicitacoes-pedido/${id}`)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao obter pedido ID ${id} no backend:`, error.message)
      throw error
    }
  },

  /**
   * Alias para obterPorId() - Mantido para retrocompatibilidade
   * @async
   * @function buscarPedido
   * @memberof pedidoService
   * @param {number} id - ID do pedido
   * @returns {Promise<Pedido>} Dados do pedido
   * @see {@link pedidoService.obterPorId}
   */
  async buscarPedido(id) {
    return this.obterPorId(id)
  },

  /**
   * Cria um novo pedido de compra
   *
   * @async
   * @function criar
   * @memberof pedidoService
   * @param {Pedido} pedido - Dados do pedido a ser criado
   * @returns {Promise<{data: Pedido}>} Objeto contendo o pedido criado
   * @throws {Error} Erro de validação ou comunicação com API
   *
   * @example
   * const novoPedido = {
   *   observacao: 'Pedido urgente',
   *   itens: [
   *     { nome: 'Mouse', quantidade: 10, descricao: 'Mouse óptico USB' },
   *     { nome: 'Teclado', quantidade: 10 }
   *   ]
   * }
   * const resultado = await pedidoService.criar(novoPedido)
   *
   * @description
   * Valida regras de negócio antes de enviar ao backend:
   * - Pedido deve ter pelo menos 1 item
   * - Cada item deve ter nome (não vazio) e quantidade > 0
   * - Limites de caracteres: nome (255), descrição (500), observação item (255)
   * - Observação do pedido limitada a 1000 caracteres
   */
  async criar(pedido) {
    try {

      // Validar estrutura de dados
      if (!pedido.itens || pedido.itens.length === 0) {
        throw new Error('Pedido deve ter pelo menos um item')
      }

      pedido.itens.forEach((item, index) => {
        if (!item.nome || item.nome.trim() === '') {
          throw new Error(`Item ${index + 1}: Nome é obrigatório`)
        }
        if (!item.quantidade || item.quantidade <= 0) {
          throw new Error(`Item ${index + 1}: Quantidade deve ser maior que zero`)
        }
        // Validar limites de caracteres conforme backend
        if (item.nome && item.nome.length > 255) {
          throw new Error(`Item ${index + 1}: Nome deve ter no máximo 255 caracteres`)
        }
        if (item.descricao && item.descricao.length > 500) {
          throw new Error(`Item ${index + 1}: Descrição deve ter no máximo 500 caracteres`)
        }
        if (item.observacao && item.observacao.length > 255) {
          throw new Error(`Item ${index + 1}: Observação deve ter no máximo 255 caracteres`)
        }
      })

      // Validar observação do pedido
      if (pedido.observacao && pedido.observacao.length > 1000) {
        throw new Error('Observação do pedido deve ter no máximo 1000 caracteres')
      }

      // Usar API real do backend
      const data = await api.post(`/api/v1/solicitacoes-pedido`, pedido)
      return { data: data }
    } catch (error) {
      logger.error('❌ Erro ao criar pedido:', error.message)
      throw error
    }
  },

  /**
   * Alias para criar() - Mantido para retrocompatibilidade
   * @async
   * @function adicionarPedido
   * @memberof pedidoService
   * @param {Pedido} pedido - Dados do pedido
   * @returns {Promise<{data: Pedido}>} Pedido criado
   * @see {@link pedidoService.criar}
   */
  async adicionarPedido(pedido) {
    return this.criar(pedido)
  },

  /**
   * Atualiza um pedido existente
   *
   * @async
   * @function atualizar
   * @memberof pedidoService
   * @param {number} id - ID do pedido a ser atualizado
   * @param {Pedido} pedido - Dados completos atualizados do pedido
   * @returns {Promise<Pedido>} Pedido atualizado
   * @throws {Error} Erro 404 se pedido não encontrado ou erro de comunicação
   *
   * @example
   * const pedidoAtualizado = { ...pedidoExistente, observacao: 'Nova observação' }
   * await pedidoService.atualizar(123, pedidoAtualizado)
   *
   * @description
   * Substitui completamente os dados do pedido no servidor.
   * Use _updatePedido() para atualizações parciais.
   */
  async atualizar(id, pedido) {
    try {
      const data = await api.put(`/api/v1/solicitacoes-pedido/${id}`, pedido)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao atualizar pedido ID ${id} no backend:`, error.message)
      throw error
    }
  },

  /**
   * Alias para atualizar() - Mantido para retrocompatibilidade
   * @async
   * @function atualizarPedido
   * @memberof pedidoService
   * @param {number} id - ID do pedido
   * @param {Pedido} pedido - Dados atualizados
   * @returns {Promise<Pedido>} Pedido atualizado
   * @see {@link pedidoService.atualizar}
   */
  async atualizarPedido(id, pedido) {
    return this.atualizar(id, pedido)
  },

  /**
   * Atualiza parcialmente um pedido (uso interno)
   *
   * @async
   * @private
   * @function _updatePedido
   * @memberof pedidoService
   * @param {number} id - ID do pedido
   * @param {Partial<Pedido>} updates - Campos a serem atualizados
   * @returns {Promise<Pedido>} Pedido atualizado
   * @throws {Error} Erro ao buscar ou atualizar
   *
   * @description
   * Método interno que busca o pedido atual, mescla com as atualizações
   * parciais e envia PUT completo. Usado por alterarStatus() e rejeitar().
   */
  async _updatePedido(id, updates) {
    try {
      const pedidoAtual = await this.obterPorId(id)
      const pedidoAtualizado = { ...pedidoAtual, ...updates }
      const data = await api.put(`/api/v1/solicitacoes-pedido/${id}`, pedidoAtualizado)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao atualizar o pedido ID ${id}:`, error.message)
      throw error
    }
  },

  /**
   * Altera o status de um pedido
   *
   * @async
   * @function alterarStatus
   * @memberof pedidoService
   * @param {number} id - ID do pedido
   * @param {string} novoStatus - Novo status (PENDENTE, APROVADO, REJEITADO, etc.)
   * @returns {Promise<Pedido>} Pedido com status atualizado
   *
   * @example
   * await pedidoService.alterarStatus(123, 'APROVADO')
   */
  async alterarStatus(id, novoStatus) {
    return this._updatePedido(id, { status: novoStatus })
  },

  /**
   * Remove (deleta) um pedido
   *
   * @async
   * @function remover
   * @memberof pedidoService
   * @param {number} id - ID do pedido a ser removido
   * @returns {Promise<boolean>} true se remoção bem-sucedida
   * @throws {Error} Erro 404 se pedido não encontrado ou erro de comunicação
   *
   * @example
   * await pedidoService.remover(123)
   * console.log('Pedido removido com sucesso')
   *
   * @description
   * Deleta permanentemente o pedido do banco de dados.
   * Operação irreversível.
   */
  async remover(id) {
    try {
      await api.delete(`/api/v1/solicitacoes-pedido/${id}`)
      return true
    } catch (error) {
      logger.error(`❌ Erro ao remover pedido ID ${id} no backend:`, error.message)
      throw error
    }
  },

  /**
   * Alias para remover()
   * @async
   * @function excluir
   * @memberof pedidoService
   * @param {number} id - ID do pedido
   * @returns {Promise<boolean>} true se remoção bem-sucedida
   * @see {@link pedidoService.remover}
   */
  async excluir(id) {
    return this.remover(id)
  },

  /**
   * Alias para remover()
   * @async
   * @function removerPedido
   * @memberof pedidoService
   * @param {number} id - ID do pedido
   * @returns {Promise<boolean>} true se remoção bem-sucedida
   * @see {@link pedidoService.remover}
   */
  async removerPedido(id) {
    return this.remover(id)
  },

  /**
   * Alias para remover() - Mantido para retrocompatibilidade
   * @async
   * @function cancelarPedido
   * @memberof pedidoService
   * @param {number} id - ID do pedido
   * @returns {Promise<boolean>} true se remoção bem-sucedida
   * @see {@link pedidoService.remover}
   * @deprecated Use alterarStatus(id, 'CANCELADO') para cancelamento lógico
   */
  async cancelarPedido(id) {
    return this.remover(id)
  },

  /**
   * Aprova um pedido (altera status para APROVADO)
   *
   * @async
   * @function aprovar
   * @memberof pedidoService
   * @param {number} id - ID do pedido
   * @returns {Promise<Pedido>} Pedido aprovado
   *
   * @example
   * await pedidoService.aprovar(123)
   */
  async aprovar(id) {
    return this.alterarStatus(id, 'APROVADO')
  },

  /**
   * Alias para aprovar()
   * @async
   * @function aprovarPedido
   * @memberof pedidoService
   * @param {number} id - ID do pedido
   * @returns {Promise<Pedido>} Pedido aprovado
   * @see {@link pedidoService.aprovar}
   */
  async aprovarPedido(id) {
    return this.aprovar(id)
  },

  /**
   * Rejeita um pedido (altera status para REJEITADO)
   *
   * @async
   * @function rejeitar
   * @memberof pedidoService
   * @param {number} id - ID do pedido
   * @param {string} [motivo=''] - Motivo da rejeição (salvo na observação)
   * @returns {Promise<Pedido>} Pedido rejeitado
   *
   * @example
   * await pedidoService.rejeitar(123, 'Orçamento insuficiente')
   */
  async rejeitar(id, motivo = '') {
    return this._updatePedido(id, { status: 'REJEITADO', observacao: motivo })
  },

  /**
   * Alias para rejeitar()
   * @async
   * @function rejeitarPedido
   * @memberof pedidoService
   * @param {number} id - ID do pedido
   * @param {string} motivo - Motivo da rejeição
   * @returns {Promise<Pedido>} Pedido rejeitado
   * @see {@link pedidoService.rejeitar}
   */
  async rejeitarPedido(id, motivo) {
    return this.rejeitar(id, motivo)
  },

  /**
   * Alias para criar()
   * @async
   * @function criarPedido
   * @memberof pedidoService
   * @param {Pedido} pedido - Dados do pedido
   * @returns {Promise<{data: Pedido}>} Pedido criado
   * @see {@link pedidoService.criar}
   */
  async criarPedido(pedido) {
    return this.criar(pedido)
  },

  /**
   * Salva um pedido (cria se novo, atualiza se existente)
   *
   * @async
   * @function salvar
   * @memberof pedidoService
   * @param {Pedido} pedido - Dados do pedido (com ou sem ID)
   * @returns {Promise<Pedido>} Pedido salvo
   * @throws {Error} Erro de validação ou comunicação
   *
   * @example
   * // Criar novo pedido
   * const novoPedido = { observacao: 'Teste', itens: [...] }
   * const resultado = await pedidoService.salvar(novoPedido)
   *
   * @example
   * // Atualizar pedido existente
   * const pedidoExistente = { id: 123, observacao: 'Atualizado', itens: [...] }
   * const resultado = await pedidoService.salvar(pedidoExistente)
   *
   * @description
   * Método unificado que detecta automaticamente se deve criar ou atualizar:
   * - Se pedido.id existe: chama atualizar()
   * - Se pedido.id é undefined/null: chama criar()
   */
  async salvar(pedido) {
    try {

      if (pedido.id) {
        // Atualizar pedido existente
        return await this.atualizar(pedido.id, pedido)
      } else {
        // Criar novo pedido
        const resultado = await this.criar(pedido)
        return resultado.data || resultado
      }
    } catch (error) {
      logger.error('❌ Erro ao salvar pedido:', error)
      throw error
    }
  },

  /**
   * Envia pedido para aprovação (EM_NEGOCIACAO → PENDENTE_APROVACAO)
   *
   * @async
   * @function enviarParaAprovacao
   * @memberof pedidoService
   * @param {number} pedidoId - ID do pedido
   * @param {Object} [dados] - Dados opcionais
   * @param {string} [dados.observacao] - Observações sobre o envio
   * @returns {Promise<Pedido>} Pedido com status atualizado
   * @throws {Error} Erro se pedido não estiver em EM_NEGOCIACAO
   *
   * @example
   * await pedidoService.enviarParaAprovacao(5, { observacao: 'Pedido urgente' })
   */
  async enviarParaAprovacao(pedidoId, dados = {}) {
    try {
      const response = await api.post(`/api/v1/solicitacoes-pedido/${pedidoId}/enviar-para-aprovacao`, dados)
      logger.info('Pedido enviado para aprovação:', pedidoId)
      return response
    } catch (error) {
      logger.error('Erro ao enviar pedido para aprovação:', error.message)
      throw error
    }
  },

  /**
   * Aprova um pedido (PENDENTE_APROVACAO → APROVADO)
   *
   * @async
   * @function aprovarPedido
   * @memberof pedidoService
   * @param {number} pedidoId - ID do pedido
   * @param {Object} [dados] - Dados opcionais
   * @param {string} [dados.observacao] - Observações sobre a aprovação
   * @returns {Promise<Pedido>} Pedido aprovado
   * @throws {Error} Erro se pedido não estiver em PENDENTE_APROVACAO
   *
   * @example
   * await pedidoService.aprovarPedido(5, { observacao: 'Aprovado conforme orçamento' })
   */
  async aprovarPedidoWorkflow(pedidoId, dados = {}) {
    try {
      const response = await api.post(`/api/v1/solicitacoes-pedido/${pedidoId}/aprovar`, dados)
      logger.info('Pedido aprovado:', pedidoId)
      return response
    } catch (error) {
      logger.error('Erro ao aprovar pedido:', error.message)
      throw error
    }
  },

  /**
   * Cancela um pedido (EM_NEGOCIACAO ou PENDENTE_APROVACAO → CANCELADO)
   *
   * @async
   * @function cancelarPedidoWorkflow
   * @memberof pedidoService
   * @param {number} pedidoId - ID do pedido
   * @param {Object} [dados] - Dados opcionais
   * @param {string} [dados.observacao] - Motivo do cancelamento
   * @returns {Promise<Pedido>} Pedido cancelado
   * @throws {Error} Erro se pedido já estiver APROVADO ou CANCELADO
   *
   * @example
   * await pedidoService.cancelarPedidoWorkflow(5, { observacao: 'Fornecedor não disponível' })
   */
  async cancelarPedidoWorkflow(pedidoId, dados = {}) {
    try {
      const response = await api.post(`/api/v1/solicitacoes-pedido/${pedidoId}/cancelar`, dados)
      logger.info('Pedido cancelado:', pedidoId)
      return response
    } catch (error) {
      logger.error('Erro ao cancelar pedido:', error.message)
      throw error
    }
  },

  /**
   * Devolve pedido para edição (PENDENTE_APROVACAO → EM_NEGOCIACAO)
   * ATENÇÃO: Requer motivo obrigatório
   *
   * @async
   * @function devolverParaEdicao
   * @memberof pedidoService
   * @param {number} pedidoId - ID do pedido
   * @param {Object} dados - Dados da devolução
   * @param {string} dados.motivo - Motivo da devolução (10-1000 caracteres) - OBRIGATÓRIO
   * @returns {Promise<Pedido>} Pedido devolvido
   * @throws {Error} Erro se pedido não estiver em PENDENTE_APROVACAO ou motivo inválido
   *
   * @example
   * await pedidoService.devolverParaEdicao(5, {
   *   motivo: 'Necessário ajustar quantidades dos itens solicitados'
   * })
   */
  async devolverParaEdicao(pedidoId, dados) {
    try {
      if (!dados || !dados.motivo || dados.motivo.length < 10) {
        throw new Error('Motivo da devolução é obrigatório e deve ter no mínimo 10 caracteres')
      }
      const response = await api.post(`/api/v1/solicitacoes-pedido/${pedidoId}/devolver-para-edicao`, dados)
      logger.info('Pedido devolvido para edição:', pedidoId)
      return response
    } catch (error) {
      logger.error('Erro ao devolver pedido para edição:', error.message)
      throw error
    }
  }
}

/**
 * Utilitários para manipulação e formatação de pedidos
 *
 * @namespace pedidoUtils
 * @description
 * Coleção de funções auxiliares para cálculos, formatações e
 * manipulação de dados relacionados a pedidos.
 */
export const pedidoUtils = {
  /**
   * Calcula o valor total de um item (quantidade × preço)
   *
   * @function calcularTotalItem
   * @memberof pedidoUtils
   * @param {ItemPedido} item - Item do pedido
   * @returns {number} Valor total do item
   *
   * @example
   * const item = { quantidade: 5, preco: 10.50 }
   * const total = pedidoUtils.calcularTotalItem(item) // 52.50
   */
  calcularTotalItem(item) {
    return (item.quantidade || 0) * (item.preco || 0)
  },

  /**
   * Calcula o valor total de um pedido (soma de todos os itens)
   *
   * @function calcularTotalPedido
   * @memberof pedidoUtils
   * @param {Pedido} pedido - Pedido completo
   * @returns {number} Valor total do pedido
   *
   * @example
   * const pedido = {
   *   itens: [
   *     { quantidade: 5, preco: 10 },
   *     { quantidade: 2, preco: 25 }
   *   ]
   * }
   * const total = pedidoUtils.calcularTotalPedido(pedido) // 100
   */
  calcularTotalPedido(pedido) {
    if (!pedido.itens || !Array.isArray(pedido.itens)) return 0
    return pedido.itens.reduce((total, item) => total + this.calcularTotalItem(item), 0)
  },

  /**
   * Formata valor numérico para moeda brasileira (BRL)
   *
   * @function formatarMoeda
   * @memberof pedidoUtils
   * @param {number} valor - Valor a ser formatado
   * @returns {string} Valor formatado (ex: "R$ 1.234,56")
   *
   * @example
   * pedidoUtils.formatarMoeda(1234.56) // "R$ 1.234,56"
   * pedidoUtils.formatarMoeda(0) // "R$ 0,00"
   */
  formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(valor || 0)
  },

  /**
   * Formata data ISO 8601 para formato brasileiro
   *
   * @function formatarData
   * @memberof pedidoUtils
   * @param {string} data - Data em formato ISO 8601
   * @returns {string} Data formatada (dd/mm/aaaa) ou string vazia
   *
   * @example
   * pedidoUtils.formatarData('2024-01-15T10:30:00Z') // "15/01/2024"
   * pedidoUtils.formatarData(null) // ""
   */
  formatarData(data) {
    if (!data) return ''
    return new Date(data).toLocaleDateString('pt-BR')
  },

  /**
   * Mapeamento de status com configurações de apresentação
   *
   * @type {Object.<string, StatusConfig>}
   * @memberof pedidoUtils
   *
   * @description
   * Mapeia códigos de status para labels formatados e classes CSS.
   * Suporta status em UPPERCASE (padrão) e lowercase (legacy).
   *
   * Status disponíveis:
   * - PENDENTE/pendente: Aguardando aprovação
   * - APROVADO/aprovado: Aprovado para cotação
   * - REJEITADO/rejeitado: Rejeitado
   * - EM_ANDAMENTO/em_andamento: Em processo de cotação
   * - FINALIZADO/finalizado: Pedido finalizado
   * - CANCELADO/cancelado: Pedido cancelado
   */
  statusConfig: {
    // Novos status do fluxo
    EM_NEGOCIACAO: { label: 'Em Negociação', class: 'info' },
    PENDENTE_APROVACAO: { label: 'Pendente de Aprovação', class: 'warning' },
    APROVADO: { label: 'Aprovado', class: 'success' },
    CANCELADO: { label: 'Cancelado', class: 'danger' },
    // Status antigos (deprecated - mantidos para compatibilidade)
    PENDENTE: { label: 'Pendente', class: 'warning' },
    EM_ANDAMENTO: { label: 'Em Andamento', class: 'info' },
    EM_ANALISE: { label: 'Em Análise', class: 'info' },
    // Manter compatibilidade com status antigos (lowercase)
    pendente: { label: 'Pendente', class: 'warning' },
    aprovado: { label: 'Aprovado', class: 'success' },
    rejeitado: { label: 'Rejeitado', class: 'danger' },
    em_andamento: { label: 'Em Andamento', class: 'info' },
    finalizado: { label: 'Finalizado', class: 'success' },
    cancelado: { label: 'Cancelado', class: 'danger' }
  },

  /**
   * Obtém informações de apresentação para um status
   *
   * @function obterStatusInfo
   * @memberof pedidoUtils
   * @param {string} status - Código do status
   * @returns {StatusConfig} Objeto com label e class CSS
   *
   * @example
   * const info = pedidoUtils.obterStatusInfo('APROVADO')
   * console.log(info.label) // "Aprovado"
   * console.log(info.class) // "success"
   *
   * @example
   * // Status desconhecido retorna configuração padrão
   * const info = pedidoUtils.obterStatusInfo('STATUS_INVALIDO')
   * console.log(info) // { label: 'STATUS_INVALIDO', class: 'secondary' }
   */
  obterStatusInfo(status) {
    return this.statusConfig[status] || { label: status, class: 'secondary' }
  },

  /**
   * Envia pedido para aprovação (EM_NEGOCIACAO → PENDENTE_APROVACAO)
   *
   * @async
   * @function enviarParaAprovacao
   * @memberof pedidoService
   * @param {number} pedidoId - ID do pedido
   * @param {Object} [dados] - Dados opcionais
   * @param {string} [dados.observacao] - Observações sobre o envio
   * @returns {Promise<Pedido>} Pedido com status atualizado
   * @throws {Error} Erro se pedido não estiver em EM_NEGOCIACAO
   *
   * @example
   * await pedidoService.enviarParaAprovacao(5, { observacao: 'Pedido urgente' })
   */
  async enviarParaAprovacao(pedidoId, dados = {}) {
    try {
      const response = await api.post(`/api/v1/solicitacoes-pedido/${pedidoId}/enviar-para-aprovacao`, dados)
      logger.info('Pedido enviado para aprovação:', pedidoId)
      return response
    } catch (error) {
      logger.error('Erro ao enviar pedido para aprovação:', error.message)
      throw error
    }
  },

  /**
   * Aprova um pedido (PENDENTE_APROVACAO → APROVADO)
   *
   * @async
   * @function aprovarPedido
   * @memberof pedidoService
   * @param {number} pedidoId - ID do pedido
   * @param {Object} [dados] - Dados opcionais
   * @param {string} [dados.observacao] - Observações sobre a aprovação
   * @returns {Promise<Pedido>} Pedido aprovado
   * @throws {Error} Erro se pedido não estiver em PENDENTE_APROVACAO
   *
   * @example
   * await pedidoService.aprovarPedido(5, { observacao: 'Aprovado conforme orçamento' })
   */
  async aprovarPedido(pedidoId, dados = {}) {
    try {
      const response = await api.post(`/api/v1/solicitacoes-pedido/${pedidoId}/aprovar`, dados)
      logger.info('Pedido aprovado:', pedidoId)
      return response
    } catch (error) {
      logger.error('Erro ao aprovar pedido:', error.message)
      throw error
    }
  },

  /**
   * Cancela um pedido (EM_NEGOCIACAO ou PENDENTE_APROVACAO → CANCELADO)
   *
   * @async
   * @function cancelarPedido
   * @memberof pedidoService
   * @param {number} pedidoId - ID do pedido
   * @param {Object} [dados] - Dados opcionais
   * @param {string} [dados.observacao] - Motivo do cancelamento
   * @returns {Promise<Pedido>} Pedido cancelado
   * @throws {Error} Erro se pedido já estiver APROVADO ou CANCELADO
   *
   * @example
   * await pedidoService.cancelarPedido(5, { observacao: 'Fornecedor não disponível' })
   */
  async cancelarPedido(pedidoId, dados = {}) {
    try {
      const response = await api.post(`/api/v1/solicitacoes-pedido/${pedidoId}/cancelar`, dados)
      logger.info('Pedido cancelado:', pedidoId)
      return response
    } catch (error) {
      logger.error('Erro ao cancelar pedido:', error.message)
      throw error
    }
  },

  /**
   * Devolve pedido para edição (PENDENTE_APROVACAO → EM_NEGOCIACAO)
   * ATENÇÃO: Requer motivo obrigatório
   *
   * @async
   * @function devolverParaEdicao
   * @memberof pedidoService
   * @param {number} pedidoId - ID do pedido
   * @param {Object} dados - Dados da devolução
   * @param {string} dados.motivo - Motivo da devolução (10-1000 caracteres) - OBRIGATÓRIO
   * @returns {Promise<Pedido>} Pedido devolvido
   * @throws {Error} Erro se pedido não estiver em PENDENTE_APROVACAO ou motivo inválido
   *
   * @example
   * await pedidoService.devolverParaEdicao(5, {
   *   motivo: 'Necessário ajustar quantidades dos itens solicitados'
   * })
   */
  async devolverParaEdicao(pedidoId, dados) {
    try {
      if (!dados || !dados.motivo || dados.motivo.length < 10) {
        throw new Error('Motivo da devolução é obrigatório e deve ter no mínimo 10 caracteres')
      }
      const response = await api.post(`/api/v1/solicitacoes-pedido/${pedidoId}/devolver-para-edicao`, dados)
      logger.info('Pedido devolvido para edição:', pedidoId)
      return response
    } catch (error) {
      logger.error('Erro ao devolver pedido para edição:', error.message)
      throw error
    }
  }
}

/**
 * Export default do serviço de pedidos
 * @exports pedidoService
 * @default
 */
export default pedidoService
