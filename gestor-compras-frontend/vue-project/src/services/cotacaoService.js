import api from './api.js'

const BASE_URL = '/api/cotacoes'

export const cotacaoService = {
  // Listar todas as cotações
  async listar() {
    try {
      console.log('📋 Buscando todas as cotações do backend...')
      const response = await api.get(BASE_URL)
      console.log('✅ Cotações carregadas do backend:', response?.length || 0, 'itens')
      return response
    } catch (error) {
      console.error('❌ Erro ao listar cotações:', error)
      throw error
    }
  },

  // Buscar cotação por ID
  async buscarPorId(id) {
    try {
      console.log('🔍 Buscando cotação por ID:', id)
      const response = await api.get(`${BASE_URL}/${id}`)
      console.log('✅ Cotação encontrada:', response)
      return response
    } catch (error) {
      console.error('❌ Erro ao buscar cotação:', error)
      throw error
    }
  },

  // Criar nova cotação - Alinhado com CotacaoCreateDTO do backend
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

      // Preparar payload conforme CotacaoCreateDTO
      const payload = {
        fornecedorId: parseInt(dadosCotacao.fornecedorId),
        itemPedidoId: parseInt(dadosCotacao.itemPedidoId),
        preco: parseFloat(dadosCotacao.preco),
        prazoEntrega: dadosCotacao.prazoEntrega || null
      }

      console.log('📤 Criando cotação no backend:', payload)
      const response = await api.post(BASE_URL, payload)
      console.log('✅ Cotação criada no backend:', response)
      return response

    } catch (error) {
      console.error('❌ Erro ao criar cotação:', error)
      throw error
    }
  },

  // Atualizar cotação - Alinhado com CotacaoUpdateDTO do backend
  async atualizar(id, dadosCotacao) {
    try {
      // Preparar dados para envio conforme CotacaoUpdateDTO
      const payload = {
        preco: dadosCotacao.preco ? parseFloat(dadosCotacao.preco) : null,
        prazoEntrega: dadosCotacao.prazoEntrega || null,
        anexoPdf: dadosCotacao.anexoPdf || null,
        caminhoAnexo: dadosCotacao.caminhoAnexo || null
      }

      console.log('📤 Atualizando cotação no backend:', payload)
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
      console.log('🗑️ Deletando cotação:', id)
      const response = await api.delete(`${BASE_URL}/${id}`)
      console.log('✅ Cotação deletada com sucesso')
      return response
    } catch (error) {
      console.error('❌ Erro ao deletar cotação:', error)
      throw error
    }
  },

  // ===== FUNCIONALIDADES BÁSICAS ALINHADAS COM O BACKEND =====

  // Aliases para compatibilidade com código existente
  async obterPorId(id) {
    return this.buscarPorId(id)
  },

  async remover(id) {
    return this.deletar(id)
  },

  // ===== FUNCIONALIDADES FUTURAS (COMENTADAS) =====
  // As funcionalidades abaixo não existem no backend atual.
  // Descomente quando forem implementadas no backend.

  /*
  // Funcionalidades de workflow (não implementadas no backend)
  async aprovar(id, observacoes = '') {
    console.warn('⚠️ Funcionalidade "aprovar" não implementada no backend')
    throw new Error('Funcionalidade não disponível')
  },

  async cancelar(id, motivo = '') {
    console.warn('⚠️ Funcionalidade "cancelar" não implementada no backend')
    throw new Error('Funcionalidade não disponível')
  },

  async reabrir(id) {
    console.warn('⚠️ Funcionalidade "reabrir" não implementada no backend')
    throw new Error('Funcionalidade não disponível')
  },

  // Funcionalidades de anexos (não implementadas no backend)
  async adicionarAnexo(cotacaoId, arquivo) {
    console.warn('⚠️ Funcionalidade "adicionarAnexo" não implementada no backend')
    throw new Error('Funcionalidade não disponível')
  },

  async removerAnexo(cotacaoId, anexoId) {
    console.warn('⚠️ Funcionalidade "removerAnexo" não implementada no backend')
    throw new Error('Funcionalidade não disponível')
  },

  // Funcionalidades de relatórios (não implementadas no backend)
  async exportarPDF(filtros = {}) {
    console.warn('⚠️ Funcionalidade "exportarPDF" não implementada no backend')
    throw new Error('Funcionalidade não disponível')
  },

  async exportarExcel(filtros = {}) {
    console.warn('⚠️ Funcionalidade "exportarExcel" não implementada no backend')
    throw new Error('Funcionalidade não disponível')
  },

  // Funcionalidades de modelos (não implementadas no backend)
  async obterModelos() {
    console.warn('⚠️ Funcionalidade "obterModelos" não implementada no backend')
    throw new Error('Funcionalidade não disponível')
  },

  async criarDeModelo(modeloId, dados = {}) {
    console.warn('⚠️ Funcionalidade "criarDeModelo" não implementada no backend')
    throw new Error('Funcionalidade não disponível')
  },

  async salvarComoModelo(cotacaoId, nomeModelo) {
    console.warn('⚠️ Funcionalidade "salvarComoModelo" não implementada no backend')
    throw new Error('Funcionalidade não disponível')
  }
  */
}

export default cotacaoService
