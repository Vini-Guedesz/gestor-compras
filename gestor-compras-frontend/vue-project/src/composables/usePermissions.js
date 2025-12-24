/**
 * @fileoverview Composable de Permissões e Controle de Acesso
 *
 * Fornece sistema centralizado de verificação de permissões baseado em roles.
 * Implementa controle granular de acesso a funcionalidades do sistema.
 *
 * @module composables/usePermissions
 * @requires stores/auth
 *
 * @description
 * Sistema de roles:
 * - ADMIN: Acesso total ao sistema
 * - USUARIO: Criar fornecedores, visualizar rascunhos/pedidos (sem cotar/aprovar)
 * - COMPRADOR: Tudo de USUARIO + criar pedidos, cotações, gerenciar fornecedores
 * - APROVADOR: Tudo de USUARIO + aprovar/cancelar/devolver pedidos
 *
 * @example
 * import { usePermissions } from '@/composables/usePermissions'
 *
 * const { permissions, isAdmin } = usePermissions()
 *
 * if (permissions.value.canCreatePedido) {
 *   // Exibir botão "Novo Pedido"
 * }
 *
 * @author Sistema Gestor de Compras
 * @version 3.0.0
 */

import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

/**
 * Composable de Permissões
 *
 * @function usePermissions
 * @returns {Object} Objeto com funções e computed properties de permissões
 *
 * @description
 * Fornece acesso ao sistema de permissões baseado no role do usuário logado.
 * Todas as verificações são reativas e atualizam automaticamente quando o usuário muda.
 */
export function usePermissions() {
  const authStore = useAuthStore()

  /**
   * Verifica se o usuário possui um dos roles requeridos
   *
   * @function hasRole
   * @param {Array<string>} requiredRoles - Lista de roles aceitos
   * @returns {boolean} true se usuário tem um dos roles
   *
   * @example
   * hasRole(['ADMIN', 'COMPRADOR']) // true se user.role for ADMIN ou COMPRADOR
   */
  const hasRole = (requiredRoles) => {
    if (!authStore.user?.role) return false
    return requiredRoles.includes(authStore.user.role)
  }

  /**
   * Verifica se o usuário pode acessar uma rota específica
   *
   * @function canAccessRoute
   * @param {Object} route - Objeto de rota Vue Router
   * @returns {boolean} true se usuário pode acessar a rota
   *
   * @example
   * canAccessRoute({ meta: { requiredRoles: ['ADMIN'] } })
   */
  const canAccessRoute = (route) => {
    const requiredRoles = route.meta?.requiredRoles
    if (!requiredRoles) return true
    return hasRole(requiredRoles)
  }

  /**
   * Filtra lista de menu items baseado no role do usuário
   *
   * @function filterMenuByRole
   * @param {Array<Object>} menuItems - Lista de itens de menu
   * @returns {Array<Object>} Menu items filtrados
   *
   * @example
   * const visibleMenus = filterMenuByRole(allMenuItems)
   */
  const filterMenuByRole = (menuItems) => {
    return menuItems.filter(item => hasRole(item.roles))
  }

  /**
   * Computed: Verifica se usuário é ADMIN
   * @type {import('vue').ComputedRef<boolean>}
   */
  const isAdmin = computed(() => authStore.user?.role === 'ADMIN')

  /**
   * Computed: Verifica se usuário é USUARIO
   * @type {import('vue').ComputedRef<boolean>}
   */
  const isUsuario = computed(() => authStore.user?.role === 'USUARIO')

  /**
   * Computed: Verifica se usuário é COMPRADOR
   * @type {import('vue').ComputedRef<boolean>}
   */
  const isComprador = computed(() => authStore.user?.role === 'COMPRADOR')

  /**
   * Computed: Verifica se usuário é APROVADOR
   * @type {import('vue').ComputedRef<boolean>}
   */
  const isAprovador = computed(() => authStore.user?.role === 'APROVADOR')

  /**
   * Computed: Objeto com todas as permissões granulares do sistema
   *
   * @type {import('vue').ComputedRef<Object>}
   *
   * @description
   * Matriz de permissões completa baseada nos requisitos:
   *
   * ADMIN: Todas as permissões
   * USUARIO: Criar fornecedores, visualizar tudo, NÃO cotar/criar pedidos/aprovar
   * COMPRADOR: Tudo de USUARIO + criar pedidos, cotações, gerenciar fornecedores
   * APROVADOR: Tudo de USUARIO + aprovar/cancelar/devolver pedidos
   *
   * @example
   * <button v-if="permissions.canCreatePedido">Novo Pedido</button>
   * <button v-if="permissions.canAprovarPedido">Aprovar</button>
   */
  const permissions = computed(() => ({
    // ==================== FORNECEDORES ====================
    /**
     * Permissão para criar novos fornecedores
     * @type {boolean}
     * @roles ADMIN, USUARIO, COMPRADOR
     */
    canCreateFornecedor: hasRole(['ADMIN', 'USUARIO', 'COMPRADOR']),

    /**
     * Permissão para editar fornecedores existentes
     * @type {boolean}
     * @roles ADMIN, COMPRADOR
     */
    canEditFornecedor: hasRole(['ADMIN', 'COMPRADOR']),

    /**
     * Permissão para visualizar fornecedores
     * @type {boolean}
     * @roles ADMIN, USUARIO, COMPRADOR, APROVADOR
     */
    canViewFornecedor: hasRole(['ADMIN', 'USUARIO', 'COMPRADOR', 'APROVADOR']),

    /**
     * Permissão para excluir fornecedores
     * @type {boolean}
     * @roles ADMIN
     */
    canDeleteFornecedor: hasRole(['ADMIN']),

    // ==================== RASCUNHOS ====================
    /**
     * Permissão para criar rascunhos
     * @type {boolean}
     * @roles ADMIN, USUARIO, COMPRADOR
     */
    canCreateRascunho: hasRole(['ADMIN', 'USUARIO', 'COMPRADOR']),

    /**
     * Permissão para editar rascunhos
     * @type {boolean}
     * @roles ADMIN, COMPRADOR
     */
    canEditRascunho: hasRole(['ADMIN', 'COMPRADOR']),

    /**
     * Permissão para visualizar rascunhos e seus status
     * @type {boolean}
     * @roles ADMIN, USUARIO, COMPRADOR, APROVADOR
     */
    canViewRascunho: hasRole(['ADMIN', 'USUARIO', 'COMPRADOR', 'APROVADOR']),

    /**
     * Permissão para cotar rascunho (criar cotação)
     * IMPORTANTE: USUARIO NÃO PODE COTAR
     * @type {boolean}
     * @roles ADMIN, COMPRADOR
     */
    canCotarRascunho: hasRole(['ADMIN', 'COMPRADOR']),

    /**
     * Permissão para excluir rascunhos
     * @type {boolean}
     * @roles ADMIN, COMPRADOR
     */
    canDeleteRascunho: hasRole(['ADMIN', 'COMPRADOR']),

    // ==================== COTAÇÕES ====================
    /**
     * Permissão para criar cotações
     * @type {boolean}
     * @roles ADMIN, COMPRADOR
     */
    canCreateCotacao: hasRole(['ADMIN', 'COMPRADOR']),

    /**
     * Permissão para editar cotações
     * @type {boolean}
     * @roles ADMIN, COMPRADOR
     */
    canEditCotacao: hasRole(['ADMIN', 'COMPRADOR']),

    /**
     * Permissão para visualizar cotações
     * @type {boolean}
     * @roles ADMIN, COMPRADOR
     */
    canViewCotacao: hasRole(['ADMIN', 'COMPRADOR']),

    /**
     * Permissão para excluir cotações
     * @type {boolean}
     * @roles ADMIN
     */
    canDeleteCotacao: hasRole(['ADMIN']),

    // ==================== PEDIDOS ====================
    /**
     * Permissão para criar novos pedidos
     * @type {boolean}
     * @roles ADMIN, COMPRADOR
     */
    canCreatePedido: hasRole(['ADMIN', 'COMPRADOR']),

    /**
     * Permissão para editar pedidos
     * @type {boolean}
     * @roles ADMIN, COMPRADOR
     */
    canEditPedido: hasRole(['ADMIN', 'COMPRADOR']),

    /**
     * Permissão para visualizar pedidos e seus status
     * @type {boolean}
     * @roles ADMIN, USUARIO, COMPRADOR, APROVADOR
     */
    canViewPedido: hasRole(['ADMIN', 'USUARIO', 'COMPRADOR', 'APROVADOR']),

    /**
     * Permissão para aprovar pedidos
     * @type {boolean}
     * @roles ADMIN, APROVADOR
     */
    canAprovarPedido: hasRole(['ADMIN', 'APROVADOR']),

    /**
     * Permissão para cancelar pedidos
     * @type {boolean}
     * @roles ADMIN, APROVADOR
     */
    canCancelarPedido: hasRole(['ADMIN', 'APROVADOR']),

    /**
     * Permissão para devolver pedido para cotação
     * @type {boolean}
     * @roles ADMIN, APROVADOR
     */
    canDevolverPedido: hasRole(['ADMIN', 'APROVADOR']),

    /**
     * Permissão para excluir pedidos
     * @type {boolean}
     * @roles ADMIN
     */
    canDeletePedido: hasRole(['ADMIN']),

    // ==================== USUÁRIOS ====================
    /**
     * Permissão para gerenciar usuários (CRUD completo)
     * @type {boolean}
     * @roles ADMIN
     */
    canManageUsuarios: hasRole(['ADMIN']),

    /**
     * Permissão para criar usuários
     * @type {boolean}
     * @roles ADMIN
     */
    canCreateUsuario: hasRole(['ADMIN']),

    /**
     * Permissão para editar usuários
     * @type {boolean}
     * @roles ADMIN
     */
    canEditUsuario: hasRole(['ADMIN']),

    /**
     * Permissão para excluir usuários
     * @type {boolean}
     * @roles ADMIN
     */
    canDeleteUsuario: hasRole(['ADMIN']),

    /**
     * Permissão para alterar role de usuários
     * @type {boolean}
     * @roles ADMIN
     */
    canChangeUserRole: hasRole(['ADMIN']),

    // ==================== RELATÓRIOS ====================
    /**
     * Permissão para acessar relatórios gerenciais
     * @type {boolean}
     * @roles ADMIN, COMPRADOR, APROVADOR
     */
    canAccessRelatorios: hasRole(['ADMIN', 'COMPRADOR', 'APROVADOR']),

    /**
     * Permissão para exportar relatórios
     * @type {boolean}
     * @roles ADMIN, COMPRADOR
     */
    canExportRelatorios: hasRole(['ADMIN', 'COMPRADOR'])
  }))

  // Retorna API pública do composable
  return {
    // Funções de verificação
    hasRole,
    canAccessRoute,
    filterMenuByRole,

    // Verificações de role específicas
    isAdmin,
    isUsuario,
    isComprador,
    isAprovador,

    // Objeto de permissões granulares
    permissions
  }
}
