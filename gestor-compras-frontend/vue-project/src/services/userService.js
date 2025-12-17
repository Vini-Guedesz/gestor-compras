/**
 * @fileoverview Serviço de Gerenciamento de Usuários
 *
 * Módulo responsável por todas as operações CRUD relacionadas a usuários do sistema.
 * Oferece funcionalidades completas de gerenciamento incluindo criação, edição,
 * desativação/reativação e alteração de roles.
 *
 * @module services/userService
 * @requires ./api
 * @requires ../utils/logger
 *
 * @description
 * Este serviço implementa:
 * - CRUD completo para usuários
 * - Gerenciamento de roles (ADMIN, USUARIO, COMPRADOR, APROVADOR)
 * - Desativação/reativação de usuários (soft delete)
 * - Alteração de roles (exclusivo para ADMINs)
 * - Tratamento de erros de validação
 * - Extração de dados de respostas paginadas
 *
 * @example
 * // Listar todos os usuários
 * const usuarios = await userService.listarUsuarios()
 *
 * @example
 * // Criar novo usuário
 * const novoUsuario = {
 *   nome: 'João Silva',
 *   email: 'joao@empresa.com',
 *   senha: 'Senha@123',
 *   role: 'COMPRADOR'
 * }
 * await userService.criarUsuario(novoUsuario)
 *
 * @example
 * // Alterar role de um usuário (apenas ADMIN)
 * await userService.alterarRole(5, 'APROVADOR')
 *
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

import api from './api'
import logger from '../utils/logger.js'

/**
 * Role do usuário
 * @typedef {'ADMIN'|'USUARIO'|'COMPRADOR'|'APROVADOR'} UserRole
 */

/**
 * Usuário do sistema
 * @typedef {Object} User
 * @property {number} [id] - ID do usuário
 * @property {string} nome - Nome completo do usuário
 * @property {string} email - Email (usado para login)
 * @property {string} [senha] - Senha (apenas para criação/atualização)
 * @property {UserRole} role - Role/perfil do usuário
 * @property {boolean} ativo - Se o usuário está ativo no sistema
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
 * const response = { content: [user1, user2], totalPages: 1 }
 * const usuarios = extractContent(response) // [user1, user2]
 */
const extractContent = (response) => {
  if (response && typeof response === 'object' && 'content' in response) {
    return response.content || []
  }
  return Array.isArray(response) ? response : []
}

/**
 * Serviço principal de gerenciamento de usuários
 * @namespace userService
 */
const userService = {
  /**
   * Lista todos os usuários do sistema
   *
   * @async
   * @function listarUsuarios
   * @memberof userService
   * @returns {Promise<Array<User>>} Array de usuários
   * @throws {Error} Erro de comunicação com API
   *
   * @example
   * const usuarios = await userService.listarUsuarios()
   * console.log(`Total: ${usuarios.length}`)
   */
  async listarUsuarios() {
    try {
      const data = await api.get('/api/v1/users')
      return extractContent(data)
    } catch (error) {
      logger.error('❌ Erro ao listar usuários no backend:', error.message)
      throw error
    }
  },

  /**
   * Obtém um usuário específico por ID
   *
   * @async
   * @function obterUsuarioPorId
   * @memberof userService
   * @param {number} id - ID do usuário
   * @returns {Promise<User>} Dados completos do usuário
   * @throws {Error} Erro 404 se usuário não encontrado
   *
   * @example
   * const usuario = await userService.obterUsuarioPorId(123)
   */
  async obterUsuarioPorId(id) {
    try {
      const data = await api.get(`/api/v1/users/${id}`)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao obter usuário ID ${id} no backend:`, error.message)
      throw error
    }
  },

  /**
   * Cria um novo usuário no sistema
   *
   * @async
   * @function criarUsuario
   * @memberof userService
   * @param {User} usuario - Dados do usuário
   * @returns {Promise<User>} Usuário criado
   * @throws {ValidationError} Erro de validação (status 400)
   * @throws {Error} Outros erros de comunicação
   *
   * @example
   * const novoUsuario = {
   *   nome: 'Maria Santos',
   *   email: 'maria@empresa.com',
   *   senha: 'SenhaForte@123',
   *   role: 'USUARIO'
   * }
   * const criado = await userService.criarUsuario(novoUsuario)
   *
   * @description
   * Em caso de erro de validação (400), lança ValidationError com:
   * - message: Mensagem formatada amigável
   * - type: 'VALIDATION_ERROR'
   * - details: Objeto/string com detalhes do backend
   */
  async criarUsuario(usuario) {
    try {
      const data = await api.post('/api/v1/users', usuario)
      return data
    } catch (error) {
      logger.error('❌ Erro ao criar usuário no backend:', error.message)

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
   * Atualiza um usuário existente
   *
   * @async
   * @function atualizarUsuario
   * @memberof userService
   * @param {number} id - ID do usuário
   * @param {User} usuario - Dados atualizados (ID será adicionado automaticamente)
   * @returns {Promise<User>} Usuário atualizado
   * @throws {ValidationError} Erro de validação (status 400)
   * @throws {Error} Erro 404 se usuário não encontrado
   *
   * @example
   * const usuarioAtualizado = { nome: 'Novo Nome', email: 'novo@email.com' }
   * await userService.atualizarUsuario(123, usuarioAtualizado)
   *
   * @description
   * O ID é automaticamente incluído no body da requisição conforme exigido pelo DTO.
   */
  async atualizarUsuario(id, usuario) {
    try {
      // O ID deve ser incluído no body da requisição conforme o DTO
      const usuarioComId = { ...usuario, id: id }
      const data = await api.put('/api/v1/users', usuarioComId)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao atualizar usuário ID ${id} no backend:`, error.message)

      // Tratamento específico para erros de validação
      if (error.response && error.response.status === 400) {
        const validationErrors = error.response.data
        logger.error('📛 Erros de validação:', validationErrors)

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
   * Desativa um usuário (soft delete)
   *
   * @async
   * @function desativarUsuario
   * @memberof userService
   * @param {number} id - ID do usuário a ser desativado
   * @returns {Promise<boolean>} true se desativação bem-sucedida
   * @throws {Error} Erro 404 se usuário não encontrado
   *
   * @example
   * await userService.desativarUsuario(123)
   *
   * @description
   * Marca o usuário como inativo ao invés de deletá-lo permanentemente.
   * Usuários inativos não podem fazer login no sistema.
   */
  async desativarUsuario(id) {
    try {
      await api.delete(`/api/v1/users/${id}`)
      return true
    } catch (error) {
      logger.error(`❌ Erro ao desativar usuário ID ${id} no backend:`, error.message)
      throw error
    }
  },

  /**
   * Reativa um usuário previamente desativado
   *
   * @async
   * @function reativarUsuario
   * @memberof userService
   * @param {number} id - ID do usuário a ser reativado
   * @returns {Promise<User>} Usuário reativado
   * @throws {Error} Erro 404 se usuário não encontrado
   *
   * @example
   * const usuario = await userService.reativarUsuario(123)
   *
   * @description
   * Permite que usuários desativados voltem a ter acesso ao sistema.
   * Apenas ADMINs podem reativar usuários.
   */
  async reativarUsuario(id) {
    try {
      const data = await api.patch(`/api/v1/users/${id}/reactivate`)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao reativar usuário ID ${id} no backend:`, error.message)
      throw error
    }
  },

  /**
   * Altera a role de um usuário (exclusivo para ADMINs)
   *
   * @async
   * @function alterarRole
   * @memberof userService
   * @param {number} userId - ID do usuário
   * @param {UserRole} newRole - Nova role a ser atribuída
   * @returns {Promise<User>} Usuário com role atualizada
   * @throws {Error} Erro 403 se não for ADMIN
   * @throws {Error} Erro 404 se usuário não encontrado
   *
   * @example
   * const usuario = await userService.alterarRole(123, 'COMPRADOR')
   *
   * @description
   * Permite que ADMINs alterem o nível de acesso de outros usuários.
   * Roles disponíveis: ADMIN, USUARIO, COMPRADOR, APROVADOR
   */
  async alterarRole(userId, newRole) {
    try {
      const data = await api.patch('/api/v1/users/role', {
        userId,
        newRole
      })
      return data
    } catch (error) {
      logger.error(`❌ Erro ao alterar role do usuário ID ${userId} no backend:`, error.message)
      throw error
    }
  }
}

/**
 * Utilitários para formatação e labels de usuários
 *
 * @namespace userUtils
 */
export const userUtils = {
  /**
   * Retorna label amigável para role
   *
   * @function getRoleLabel
   * @memberof userUtils
   * @param {UserRole} role - Role do usuário
   * @returns {string} Label formatada
   *
   * @example
   * userUtils.getRoleLabel('COMPRADOR') // 'Comprador'
   */
  getRoleLabel(role) {
    const labels = {
      ADMIN: 'Administrador',
      USUARIO: 'Usuário',
      COMPRADOR: 'Comprador',
      APROVADOR: 'Aprovador'
    }
    return labels[role] || role
  },

  /**
   * Retorna classe CSS para badge de role
   *
   * @function getRoleClass
   * @memberof userUtils
   * @param {UserRole} role - Role do usuário
   * @returns {string} Nome da classe CSS
   *
   * @example
   * userUtils.getRoleClass('ADMIN') // 'admin'
   */
  getRoleClass(role) {
    const classes = {
      ADMIN: 'admin',
      USUARIO: 'usuario',
      COMPRADOR: 'comprador',
      APROVADOR: 'aprovador'
    }
    return classes[role] || 'usuario'
  },

  /**
   * Retorna label de status (ativo/inativo)
   *
   * @function getStatusLabel
   * @memberof userUtils
   * @param {boolean} ativo - Se usuário está ativo
   * @returns {string} Label do status
   *
   * @example
   * userUtils.getStatusLabel(true) // 'Ativo'
   */
  getStatusLabel(ativo) {
    return ativo ? 'Ativo' : 'Inativo'
  },

  /**
   * Retorna classe CSS para status
   *
   * @function getStatusClass
   * @memberof userUtils
   * @param {boolean} ativo - Se usuário está ativo
   * @returns {string} Nome da classe CSS
   *
   * @example
   * userUtils.getStatusClass(true) // 'active'
   */
  getStatusClass(ativo) {
    return ativo ? 'active' : 'inactive'
  }
}

/**
 * @exports userService
 * @exports userUtils
 */
export default userService
