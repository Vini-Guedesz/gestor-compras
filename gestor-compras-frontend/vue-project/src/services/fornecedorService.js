import api from './api'
import logger from '../utils/logger.js'

/**
 * Helper para extrair dados de respostas paginadas do Spring Data
 * Se a resposta tiver `content` (Page), retorna content
 * Caso contrário, retorna a resposta como está (array)
 */
const extractContent = (response) => {
  if (response && typeof response === 'object' && 'content' in response) {
    return response.content || []
  }
  return Array.isArray(response) ? response : []
}

const fornecedorService = {
  // ==================== FORNECEDORES DE PRODUTO ====================
  async listarFornecedoresProduto() {
    try {
      const data = await api.get('/api/v1/fornecedores-de-produto')
      return extractContent(data)
    } catch (error) {
      logger.error('❌ Erro ao listar fornecedores de produto no backend:', error.message)
      throw error
    }
  },

  async obterFornecedorProdutoPorId(id) {
    try {
      const data = await api.get(`/api/v1/fornecedores-de-produto/${id}`)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao obter fornecedor de produto ID ${id} no backend:`, error.message)
      throw error
    }
  },

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
  async listarFornecedoresServico() {
    try {
      const data = await api.get('/api/v1/fornecedores-de-servico')
      return extractContent(data)
    } catch (error) {
      logger.error('❌ Erro ao listar fornecedores de serviço no backend:', error.message)
      throw error
    }
  },

  async obterFornecedorServicoPorId(id) {
    try {
      const data = await api.get(`/api/v1/fornecedores-de-servico/${id}`)
      return data
    } catch (error) {
      logger.error(`❌ Erro ao obter fornecedor de serviço ID ${id} no backend:`, error.message)
      throw error
    }
  },

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

  // Métodos específicos para cotações - com marcação de tipo
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

  // Aliases para compatibilidade
  async listar() {
    return this.listarTodos()
  },

  async listarFornecedores() {
    return this.listarTodos()
  }
}

// Utilitários para formatação
export const fornecedorUtils = {
  formatarCNPJ(cnpj) {
    if (!cnpj) return ''
    return cnpj.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5')
  },

  formatarCEP(cep) {
    if (!cep) return ''
    return cep.replace(/(\d{5})(\d{3})/, '$1-$2')
  },

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

  validarCNPJ(cnpj) {
    if (!cnpj) return false
    const numbers = cnpj.replace(/\D/g, '')
    return numbers.length === 14
  },

  validarEmail(email) {
    if (!email) return false
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return emailRegex.test(email)
  },

  // ==================== MÉTODOS COMBINADOS ====================

  // Listar todos os fornecedores (produto + serviço) para uso em cotações
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

export default fornecedorService
