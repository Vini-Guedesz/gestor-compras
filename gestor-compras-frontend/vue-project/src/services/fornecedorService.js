import api from './api'

// Mock data para desenvolvimento
const fornecedoresProdutoMock = [
  {
    id: 1,
    razaoSocial: 'Tech Solutions Ltda',
    cnpj: '12345678000199',
    inscricaoEstadual: '123456789',
    contato: {
      email: 'contato@techsolutions.com',
      telefone: '1133334444'
    },
    endereco: {
      cidade: 'São Paulo',
      estado: 'SP'
    },
    status: 'ativo'
  },
  {
    id: 2,
    razaoSocial: 'Equipamentos Industriais S.A.',
    cnpj: '98765432000188',
    inscricaoEstadual: '987654321',
    contato: {
      email: 'vendas@equipindustriais.com',
      telefone: '1122223333'
    },
    endereco: {
      cidade: 'Campinas',
      estado: 'SP'
    },
    status: 'ativo'
  }
]

const fornecedoresServicoMock = [
  {
    id: 1,
    razaoSocial: 'Manutenção Express Ltda',
    cnpj: '11222333000177',
    inscricaoEstadual: '112233445',
    contato: {
      email: 'contato@manutencaoexpress.com',
      telefone: '1144445555'
    },
    endereco: {
      cidade: 'Rio de Janeiro',
      estado: 'RJ'
    },
    status: 'ativo'
  },
  {
    id: 2,
    razaoSocial: 'Consultoria Empresarial Ltda',
    cnpj: '33444555000166',
    inscricaoEstadual: '334455667',
    contato: {
      email: 'atendimento@consultoria.com',
      telefone: '1155556666'
    },
    endereco: {
      cidade: 'Belo Horizonte',
      estado: 'MG'
    },
    status: 'inativo'
  }
]

// Serviço para fornecedores de produto
export const fornecedorProdutoService = {
  listar: async () => {
    try {
      const response = await api.get('/api/fornecedores-de-produto')
      return response.data
    } catch (error) {
      console.error('Erro ao listar fornecedores de produto:', error)

      // Fallback para dados mock se a API falhar
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        console.warn('Usando dados mock - backend indisponível')
        await new Promise(resolve => setTimeout(resolve, 800))
        return fornecedoresProdutoMock
      }

      throw error
    }
  },

  obterPorId: async (id) => {
    try {
      const response = await api.get(`/api/fornecedores-de-produto/${id}`)
      return response.data
    } catch (error) {
      console.error(`Erro ao obter fornecedor de produto ID ${id}:`, error)

      // Fallback para dados mock
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        await new Promise(resolve => setTimeout(resolve, 500))
        return fornecedoresProdutoMock.find(f => f.id === id)
      }

      throw error
    }
  },

  criar: async (fornecedor) => {
    try {
      const response = await api.post('/api/fornecedores-de-produto', fornecedor)
      return response.data
    } catch (error) {
      console.error('Erro ao criar fornecedor de produto:', error)

      // Fallback para simulação se a API falhar
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        console.warn('Simulando criação - backend indisponível')
        await new Promise(resolve => setTimeout(resolve, 1000))
        return { ...fornecedor, id: Date.now() }
      }

      throw error
    }
  },

  atualizar: async (id, fornecedor) => {
    try {
      const response = await api.put(`/api/fornecedores-de-produto`, fornecedor)
      return response.data
    } catch (error) {
      console.error(`Erro ao atualizar fornecedor de produto ID ${id}:`, error)

      // Fallback para simulação
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        console.warn('Simulando atualização - backend indisponível')
        await new Promise(resolve => setTimeout(resolve, 1000))
        return { ...fornecedor, id }
      }

      throw error
    }
  },

  remover: async (id) => {
    try {
      await api.delete(`/api/fornecedores-de-produto/${id}`)
      return true
    } catch (error) {
      console.error(`Erro ao remover fornecedor de produto ID ${id}:`, error)

      // Fallback para simulação
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        console.warn('Simulando remoção - backend indisponível')
        await new Promise(resolve => setTimeout(resolve, 800))
        return true
      }

      throw error
    }
  }
}

// Serviço para fornecedores de serviço
export const fornecedorServicoService = {
  listar: async () => {
    try {
      const response = await api.get('/api/fornecedores-de-servico')
      return response.data
    } catch (error) {
      console.error('Erro ao listar fornecedores de serviço:', error)

      // Fallback para dados mock se a API falhar
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        console.warn('Usando dados mock - backend indisponível')
        await new Promise(resolve => setTimeout(resolve, 800))
        return fornecedoresServicoMock
      }

      throw error
    }
  },

  obterPorId: async (id) => {
    try {
      const response = await api.get(`/api/fornecedores-de-servico/${id}`)
      return response.data
    } catch (error) {
      console.error(`Erro ao obter fornecedor de serviço ID ${id}:`, error)

      // Fallback para dados mock
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        await new Promise(resolve => setTimeout(resolve, 500))
        return fornecedoresServicoMock.find(f => f.id === id)
      }

      throw error
    }
  },

  criar: async (fornecedor) => {
    try {
      const response = await api.post('/api/fornecedores-de-servico', fornecedor)
      return response.data
    } catch (error) {
      console.error('Erro ao criar fornecedor de serviço:', error)

      // Fallback para simulação
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        console.warn('Simulando criação - backend indisponível')
        await new Promise(resolve => setTimeout(resolve, 1000))
        return { ...fornecedor, id: Date.now() }
      }

      throw error
    }
  },

  atualizar: async (id, fornecedor) => {
    try {
      const response = await api.put(`/api/fornecedores-de-servico`, fornecedor)
      return response.data
    } catch (error) {
      console.error(`Erro ao atualizar fornecedor de serviço ID ${id}:`, error)

      // Fallback para simulação
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        console.warn('Simulando atualização - backend indisponível')
        await new Promise(resolve => setTimeout(resolve, 1000))
        return { ...fornecedor, id }
      }

      throw error
    }
  },

  remover: async (id) => {
    try {
      await api.delete(`/api/fornecedores-de-servico/${id}`)
      return true
    } catch (error) {
      console.error(`Erro ao remover fornecedor de serviço ID ${id}:`, error)

      // Fallback para simulação
      if (error.code === 'ECONNREFUSED' || error.response?.status === 500) {
        console.warn('Simulando remoção - backend indisponível')
        await new Promise(resolve => setTimeout(resolve, 800))
        return true
      }

      throw error
    }
  }
}

// Utilitários para manipulação de dados de fornecedor
export const fornecedorUtils = {
  mascaraCNPJ: (cnpj) => {
    if (!cnpj) return ''

    // Remove caracteres não numéricos
    const numeros = cnpj.replace(/\D/g, '')

    // Aplica a máscara de CNPJ: 00.000.000/0000-00
    return numeros.replace(
      /^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/,
      '$1.$2.$3/$4-$5'
    )
  },

  mascaraTelefone: (telefone) => {
    if (!telefone) return ''

    // Remove caracteres não numéricos
    const numeros = telefone.replace(/\D/g, '')

    // Verifica se é celular (11 dígitos) ou fixo (10 dígitos)
    if (numeros.length === 11) {
      // Celular: (00) 00000-0000
      return numeros.replace(
        /^(\d{2})(\d{5})(\d{4})$/,
        '($1) $2-$3'
      )
    } else {
      // Fixo: (00) 0000-0000
      return numeros.replace(
        /^(\d{2})(\d{4})(\d{4})$/,
        '($1) $2-$3'
      )
    }
  },

  gerarAvaliacaoAleatoria: () => {
    // Gera uma avaliação aleatória entre 1 e 5, com uma casa decimal
    return (Math.random() * 4 + 1).toFixed(1)
  }
}
