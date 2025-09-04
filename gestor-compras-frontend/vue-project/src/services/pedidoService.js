/**
 * Serviço de Pedidos
 *
 * Gerencia todas as operações relacionadas aos pedidos de compra
 */

import api from './api.js';

// Mock data para desenvolvimento
const mockPedidos = [
  {
    id: 1,
    numero: '2024-001',
    requisitante: 'João Silva',
    unidadeFuncional: 'TI',
    objetivo: 'reposicao',
    observacoes: 'Equipamentos para substituição de hardware defeituoso',
    status: 'enviado',
    dataPedido: '2024-01-15',
    dataUltimaAtualizacao: '2024-01-15',
    itens: [
      {
        id: 1,
        descricao: 'Notebook Dell Inspiron 15',
        quantidade: 2,
        unidade: 'un',
        justificativa: 'Substituição de equipamentos defeituosos'
      },
      {
        id: 2,
        descricao: 'Mouse óptico USB',
        quantidade: 5,
        unidade: 'un',
        justificativa: 'Reposição de acessórios'
      }
    ]
  },
  {
    id: 2,
    numero: '2024-002',
    requisitante: 'Maria Santos',
    unidadeFuncional: 'RH',
    objetivo: 'consumo',
    observacoes: 'Material para treinamentos',
    status: 'aprovado',
    dataPedido: '2024-01-18',
    dataUltimaAtualizacao: '2024-01-20',
    itens: [
      {
        id: 3,
        descricao: 'Papel A4 75g',
        quantidade: 10,
        unidade: 'cx',
        justificativa: 'Material para impressão de documentos de treinamento'
      }
    ]
  },
  {
    id: 3,
    numero: '2024-003',
    requisitante: 'Carlos Oliveira',
    unidadeFuncional: 'Comercial',
    objetivo: 'projeto',
    observacoes: 'Equipamentos para novo projeto',
    status: 'em_cotacao',
    dataPedido: '2024-01-22',
    dataUltimaAtualizacao: '2024-01-23',
    itens: [
      {
        id: 4,
        descricao: 'Projetor Full HD',
        quantidade: 1,
        unidade: 'un',
        justificativa: 'Apresentações do projeto'
      },
      {
        id: 5,
        descricao: 'Tela de projeção 2x2m',
        quantidade: 1,
        unidade: 'un',
        justificativa: 'Suporte para apresentações'
      }
    ]
  },
  {
    id: 4,
    numero: '2024-004',
    requisitante: 'Ana Costa',
    unidadeFuncional: 'Financeiro',
    objetivo: 'consumo',
    observacoes: 'Material de escritório',
    status: 'rascunho',
    dataPedido: '2024-01-25',
    dataUltimaAtualizacao: '2024-01-25',
    itens: [
      {
        id: 6,
        descricao: 'Canetas esferográficas',
        quantidade: 50,
        unidade: 'un',
        justificativa: 'Reposição de material de escritório'
      }
    ]
  }
];

let nextId = 5;
let nextNumero = 5;

/**
 * Gera um novo número de pedido
 */
const gerarNumeroPedido = () => {
  const ano = new Date().getFullYear();
  return `${ano}-${String(nextNumero++).padStart(3, '0')}`;
};

/**
 * Lista todos os pedidos
 */
export const listarPedidos = async () => {
  try {
    // Em produção, fazer chamada real para a API
    // const response = await api.get('/pedidos');
    // return response.data;

    // Mock para desenvolvimento
    return new Promise(resolve => {
      setTimeout(() => {
        resolve([...mockPedidos]);
      }, 500);
    });
  } catch (error) {
    console.error('Erro ao listar pedidos:', error);
    throw error;
  }
};

/**
 * Busca um pedido específico por ID
 */
export const buscarPedido = async (id) => {
  try {
    // Em produção, fazer chamada real para a API
    // const response = await api.get(`/pedidos/${id}`);
    // return response.data;

    // Mock para desenvolvimento
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const pedido = mockPedidos.find(p => p.id === parseInt(id));
        if (pedido) {
          resolve({ ...pedido });
        } else {
          reject(new Error('Pedido não encontrado'));
        }
      }, 300);
    });
  } catch (error) {
    console.error('Erro ao buscar pedido:', error);
    throw error;
  }
};

/**
 * Cria um novo pedido
 */
export const criarPedido = async (dadosPedido) => {
  try {
    // Em produção, fazer chamada real para a API
    // const response = await api.post('/pedidos', dadosPedido);
    // return response.data;

    // Mock para desenvolvimento
    return new Promise(resolve => {
      setTimeout(() => {
        const novoPedido = {
          id: nextId++,
          numero: gerarNumeroPedido(),
          ...dadosPedido,
          status: 'rascunho',
          dataPedido: new Date().toISOString().split('T')[0],
          dataUltimaAtualizacao: new Date().toISOString().split('T')[0],
          itens: dadosPedido.itens.map((item, index) => ({
            id: Date.now() + index,
            ...item
          }))
        };

        mockPedidos.push(novoPedido);
        resolve({ ...novoPedido });
      }, 800);
    });
  } catch (error) {
    console.error('Erro ao criar pedido:', error);
    throw error;
  }
};

/**
 * Atualiza um pedido existente
 */
export const atualizarPedido = async (id, dadosPedido) => {
  try {
    // Em produção, fazer chamada real para a API
    // const response = await api.put(`/pedidos/${id}`, dadosPedido);
    // return response.data;

    // Mock para desenvolvimento
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const index = mockPedidos.findIndex(p => p.id === parseInt(id));

        if (index !== -1) {
          const pedidoAtualizado = {
            ...mockPedidos[index],
            ...dadosPedido,
            dataUltimaAtualizacao: new Date().toISOString().split('T')[0]
          };

          mockPedidos[index] = pedidoAtualizado;
          resolve({ ...pedidoAtualizado });
        } else {
          reject(new Error('Pedido não encontrado'));
        }
      }, 600);
    });
  } catch (error) {
    console.error('Erro ao atualizar pedido:', error);
    throw error;
  }
};

/**
 * Cancela um pedido
 */
export const cancelarPedido = async (id) => {
  try {
    // Em produção, fazer chamada real para a API
    // const response = await api.patch(`/pedidos/${id}/cancelar`);
    // return response.data;

    // Mock para desenvolvimento
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const index = mockPedidos.findIndex(p => p.id === parseInt(id));

        if (index !== -1) {
          mockPedidos[index].status = 'cancelado';
          mockPedidos[index].dataUltimaAtualizacao = new Date().toISOString().split('T')[0];
          resolve({ ...mockPedidos[index] });
        } else {
          reject(new Error('Pedido não encontrado'));
        }
      }, 400);
    });
  } catch (error) {
    console.error('Erro ao cancelar pedido:', error);
    throw error;
  }
};

/**
 * Envia um pedido para aprovação
 */
export const enviarPedido = async (id) => {
  try {
    // Em produção, fazer chamada real para a API
    // const response = await api.patch(`/pedidos/${id}/enviar`);
    // return response.data;

    // Mock para desenvolvimento
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const index = mockPedidos.findIndex(p => p.id === parseInt(id));

        if (index !== -1 && mockPedidos[index].status === 'rascunho') {
          mockPedidos[index].status = 'enviado';
          mockPedidos[index].dataUltimaAtualizacao = new Date().toISOString().split('T')[0];
          resolve({ ...mockPedidos[index] });
        } else {
          reject(new Error('Pedido não encontrado ou não pode ser enviado'));
        }
      }, 400);
    });
  } catch (error) {
    console.error('Erro ao enviar pedido:', error);
    throw error;
  }
};

/**
 * Remove um pedido (apenas rascunhos)
 */
export const removerPedido = async (id) => {
  try {
    // Em produção, fazer chamada real para a API
    // const response = await api.delete(`/pedidos/${id}`);
    // return response.data;

    // Mock para desenvolvimento
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const index = mockPedidos.findIndex(p => p.id === parseInt(id));

        if (index !== -1) {
          if (mockPedidos[index].status === 'rascunho') {
            mockPedidos.splice(index, 1);
            resolve({ message: 'Pedido removido com sucesso' });
          } else {
            reject(new Error('Apenas pedidos em rascunho podem ser removidos'));
          }
        } else {
          reject(new Error('Pedido não encontrado'));
        }
      }, 300);
    });
  } catch (error) {
    console.error('Erro ao remover pedido:', error);
    throw error;
  }
};

/**
 * Obtém estatísticas dos pedidos
 */
export const obterEstatisticas = async () => {
  try {
    // Em produção, fazer chamada real para a API
    // const response = await api.get('/pedidos/estatisticas');
    // return response.data;

    // Mock para desenvolvimento
    return new Promise(resolve => {
      setTimeout(() => {
        const stats = {
          total: mockPedidos.length,
          rascunho: mockPedidos.filter(p => p.status === 'rascunho').length,
          enviado: mockPedidos.filter(p => p.status === 'enviado').length,
          aprovado: mockPedidos.filter(p => p.status === 'aprovado').length,
          em_cotacao: mockPedidos.filter(p => p.status === 'em_cotacao').length,
          cancelado: mockPedidos.filter(p => p.status === 'cancelado').length
        };
        resolve(stats);
      }, 200);
    });
  } catch (error) {
    console.error('Erro ao obter estatísticas:', error);
    throw error;
  }
};

export default {
  listarPedidos,
  buscarPedido,
  criarPedido,
  atualizarPedido,
  cancelarPedido,
  enviarPedido,
  removerPedido,
  obterEstatisticas
};
