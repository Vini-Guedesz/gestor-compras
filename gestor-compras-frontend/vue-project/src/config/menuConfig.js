/**
 * @fileoverview Configuração de Menu Items com Controle de Permissões
 *
 * Define a estrutura do menu lateral (sidebar) com controle de acesso baseado em roles.
 * Cada item de menu especifica quais roles têm permissão para visualizá-lo.
 *
 * @module config/menuConfig
 *
 * @description
 * Sistema de roles implementado:
 * - ADMIN: Acesso total ao sistema
 * - USUARIO: Acesso básico (fornecedores, visualização de pedidos/rascunhos)
 * - COMPRADOR: Gestão de compras (criar pedidos, cotações, gerenciar fornecedores)
 * - APROVADOR: Aprovação de pedidos (visualizar e aprovar/cancelar/devolver pedidos)
 *
 * @example
 * import { menuItems } from '@/config/menuConfig'
 * import { usePermissions } from '@/composables/usePermissions'
 *
 * const { filterMenuByRole } = usePermissions()
 * const visibleMenus = filterMenuByRole(menuItems)
 *
 * @author Sistema Gestor de Compras
 * @version 3.0.0
 */

/**
 * @typedef {Object} MenuItem
 * @property {string} id - Identificador único do menu item
 * @property {string} label - Texto exibido no menu
 * @property {string} route - Rota Vue Router de destino
 * @property {string} icon - Nome do ícone (referência para SVG)
 * @property {Array<string>} roles - Roles permitidos a visualizar este menu
 * @property {string} [description] - Descrição opcional do menu item
 */

/**
 * Configuração de itens do menu principal
 *
 * @type {Array<MenuItem>}
 * @const
 *
 * @description
 * Lista de todos os itens disponíveis no menu lateral.
 * Filtrados dinamicamente com base no role do usuário autenticado.
 *
 * Distribuição de menus por role:
 * - ADMIN: Dashboard, Fornecedores, Pedidos, Cotações, Usuários (todos)
 * - COMPRADOR: Dashboard, Fornecedores, Pedidos, Cotações
 * - APROVADOR: Dashboard, Fornecedores, Pedidos
 * - USUARIO: Dashboard, Fornecedores, Pedidos
 */
export const menuItems = [
  {
    id: 'dashboard',
    label: 'Dashboard',
    route: '/dashboard',
    icon: 'dashboard',
    roles: ['ADMIN', 'USUARIO', 'COMPRADOR', 'APROVADOR'],
    description: 'Visão geral do sistema com métricas e estatísticas'
  },
  {
    id: 'fornecedores',
    label: 'Fornecedores',
    route: '/fornecedores',
    icon: 'users',
    roles: ['ADMIN', 'USUARIO', 'COMPRADOR', 'APROVADOR'],
    description: 'Gestão de fornecedores de produtos e serviços'
  },
  {
    id: 'pedidos',
    label: 'Pedidos de Compra',
    route: '/pedidos',
    icon: 'shopping-cart',
    roles: ['ADMIN', 'USUARIO', 'COMPRADOR', 'APROVADOR'],
    description: 'Gestão de pedidos de compra e solicitações'
  },
  {
    id: 'cotacoes',
    label: 'Cotações',
    route: '/cotacoes',
    icon: 'clipboard-check',
    roles: ['ADMIN', 'COMPRADOR'],
    description: 'Gestão de cotações e comparação de preços'
  },
  {
    id: 'usuarios',
    label: 'Usuários',
    route: '/usuarios',
    icon: 'user-cog',
    roles: ['ADMIN'],
    description: 'Gerenciamento de usuários do sistema'
  }
]

/**
 * Mapa de ícones SVG para os menu items
 *
 * @type {Object<string, string>}
 * @const
 *
 * @description
 * Paths SVG para cada tipo de ícone usado nos menus.
 * Permite renderização inline de ícones sem dependências externas.
 */
export const menuIcons = {
  dashboard: 'M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z',
  users: 'M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z',
  'shopping-cart': 'M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.08-.14.12-.31.12-.48 0-.55-.45-1-1-1H5.21l-.94-2H1zm16 16c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z',
  'clipboard-check': 'M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm-2 14l-4-4 1.41-1.41L10 14.17l6.59-6.59L18 9l-8 8z',
  'user-cog': 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z'
}

/**
 * Obtém o path SVG para um ícone específico
 *
 * @function getIconPath
 * @param {string} iconName - Nome do ícone
 * @returns {string} Path SVG do ícone
 *
 * @example
 * const dashboardIcon = getIconPath('dashboard')
 * // Returns: "M3 13h8V3H3v10zm0 8h8v-6H3v6z..."
 */
export const getIconPath = (iconName) => {
  return menuIcons[iconName] || menuIcons.dashboard
}
