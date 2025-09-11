// Teste de importação dos serviços
import pedidoService from './src/services/pedidoService.js'
import { fornecedorProdutoService } from './src/services/fornecedorService.js'
import { authService } from './src/services/authService.js'

console.log('pedidoService:', pedidoService)
console.log('fornecedorProdutoService:', fornecedorProdutoService)
console.log('authService:', authService)

// Teste se os métodos existem
console.log('pedidoService.listarPedidos:', typeof pedidoService.listarPedidos)
console.log('pedidoService.criarPedido:', typeof pedidoService.criarPedido)
console.log('pedidoService.atualizarPedido:', typeof pedidoService.atualizarPedido)
console.log('pedidoService.cancelarPedido:', typeof pedidoService.cancelarPedido)
