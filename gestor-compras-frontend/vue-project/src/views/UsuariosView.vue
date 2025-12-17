<template>
  <div class="dashboard-layout">
    <!-- Header -->
    <DashboardHeader />

    <!-- Sidebar -->
    <DashboardSidebar />

    <!-- Conteúdo Principal -->
    <main class="main-content">
      <!-- Mensagem de Boas-vindas -->
      <div class="welcome-section">
        <div class="welcome-header">
          <div class="welcome-content">
            <h1 class="welcome-title">Gestão de Usuários 👥</h1>
            <p class="welcome-subtitle">
              Gerencie usuários e suas permissões de acesso ao sistema
            </p>
          </div>
          <div class="action-buttons">
            <button class="action-button" @click="abrirFormularioNovo">
              <svg class="action-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="white" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" />
              </svg>
              Novo Usuário
            </button>
          </div>
        </div>
      </div>

      <!-- Indicadores (Cards de Métricas Rápidas) -->
      <div class="metrics-section">
        <div class="metrics-grid">
          <!-- Total de Usuários -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon total">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white"
                    d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z" />
                </svg>
              </div>
              <span class="metric-label">Total de Usuários</span>
            </div>
            <div class="metric-value">{{ totalUsuarios }}</div>
            <div class="metric-growth positive">{{ novosUsuariosMes }} novos este mês</div>
          </div>

          <!-- Usuários Ativos -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon active">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white"
                    d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
                </svg>
              </div>
              <span class="metric-label">Usuários Ativos</span>
            </div>
            <div class="metric-value">{{ usuariosAtivos }}</div>
            <div class="metric-growth neutral">{{ percentualAtivos }}% do total</div>
          </div>

          <!-- Administradores -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon rating">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white"
                    d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z" />
                </svg>
              </div>
              <span class="metric-label">Administradores</span>
            </div>
            <div class="metric-value">{{ usuariosAdmin.length }}</div>
            <div class="metric-growth positive">Com acesso total</div>
          </div>

          <!-- Compradores/Aprovadores -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon value">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white"
                    d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z" />
                </svg>
              </div>
              <span class="metric-label">Compradores e Aprovadores</span>
            </div>
            <div class="metric-value">{{ usuariosEspeciais }}</div>
            <div class="metric-growth positive">Gestão de pedidos</div>
          </div>
        </div>
      </div>

      <!-- Filtros de Pesquisa -->
      <div class="search-section">
        <div class="search-container">
          <div class="search-input-container">
            <svg class="search-icon" viewBox="0 0 24 24" width="20" height="20">
              <path fill="currentColor"
                d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z" />
            </svg>
            <input type="text" v-model="searchQuery" placeholder="Pesquisar por nome ou e-mail..."
              class="search-input" @input="filtrarUsuarios" />
          </div>
          <div class="search-actions">
            <select v-model="filtroRole" @change="filtrarUsuarios" class="filter-select">
              <option value="">Todas as funções</option>
              <option value="ADMIN">Administrador</option>
              <option value="USUARIO">Usuário</option>
              <option value="COMPRADOR">Comprador</option>
              <option value="APROVADOR">Aprovador</option>
            </select>
            <select v-model="filtroStatus" @change="filtrarUsuarios" class="filter-select">
              <option value="">Todos os status</option>
              <option value="ativo">Ativo</option>
              <option value="inativo">Inativo</option>
            </select>
            <button class="filter-button" @click="limparFiltros">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor"
                  d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
              </svg>
              Limpar
            </button>
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="isLoading" class="loading-section">
        <div class="loading-spinner-large"></div>
        <p>Carregando usuários...</p>
      </div>

      <!-- Lista de Usuários (Tabela) -->
      <div v-else class="table-section">
        <div class="table-container">
          <table class="users-table" v-if="usuariosFiltrados.length > 0">
            <thead>
              <tr>
                <th>Usuário</th>
                <th>E-mail</th>
                <th>Função</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="usuario in usuariosFiltrados" :key="usuario.id" class="table-row">
                <td class="user-cell">
                  <div class="user-info">
                    <span class="user-name">{{ usuario.nome }}</span>
                  </div>
                </td>
                <td class="email-cell">
                  <span class="email">{{ usuario.email }}</span>
                </td>
                <td>
                  <span class="role-tag" :class="getRoleClass(usuario.role)">
                    {{ getRoleLabel(usuario.role) }}
                  </span>
                </td>
                <td>
                  <span class="status-badge" :class="getStatusClass(usuario.ativo)">
                    {{ getStatusLabel(usuario.ativo) }}
                  </span>
                </td>
                <td class="actions-cell">
                  <div class="action-buttons">
                    <button class="action-btn edit" @click="editarUsuario(usuario)" title="Editar">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                      </svg>
                    </button>
                    <button v-if="usuario.ativo" class="action-btn delete" @click="confirmarDesativacao(usuario)" title="Desativar">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11H7v-2h10v2z" />
                      </svg>
                    </button>
                    <button v-else class="action-btn reactivate" @click="confirmarReativacao(usuario)" title="Reativar">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z" />
                      </svg>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>

          <!-- Estado vazio -->
          <div v-else class="empty-state">
            <svg class="empty-icon" viewBox="0 0 24 24" width="64" height="64">
              <path fill="currentColor"
                d="M12 2c5.523 0 10 4.477 10 10s-4.477 10-10 10S2 17.523 2 12 6.477 2 12 2zm0 1.5a8.5 8.5 0 1 0 0 17 8.5 8.5 0 0 0 0-17zM12 7a5 5 0 1 1 0 10 5 5 0 0 1 0-10zm0 1.5a3.5 3.5 0 1 0 0 7 3.5 3.5 0 0 0 0-7z" />
            </svg>
            <h3>Nenhum usuário encontrado</h3>
            <p>Não há usuários que correspondam aos filtros aplicados.</p>
            <button class="btn-primary" @click="abrirFormularioNovo">
              Cadastrar Primeiro Usuário
            </button>
          </div>
        </div>
      </div>

      <!-- Modal de Usuário -->
      <UserForm
        :key="usuarioEditando?.id || 'novo'"
        :isVisible="showUserForm"
        :usuario="usuarioEditando"
        @close="fecharFormulario"
        @save="salvarUsuario"
      />

      <!-- Modal de Confirmação de Desativação -->
      <ConfirmModal
        :isVisible="showConfirmDesativacao"
        title="Confirmar Desativação"
        :message="`Tem certeza que deseja desativar o usuário '${usuarioParaDesativar?.nome}'? O usuário não poderá mais acessar o sistema.`"
        confirmText="Desativar"
        confirmClass="btn-danger"
        @confirm="desativarUsuario"
        @cancel="showConfirmDesativacao = false"
      />

      <!-- Modal de Confirmação de Reativação -->
      <ConfirmModal
        :isVisible="showConfirmReativacao"
        title="Confirmar Reativação"
        :message="`Tem certeza que deseja reativar o usuário '${usuarioParaReativar?.nome}'?`"
        confirmText="Reativar"
        confirmClass="btn-primary"
        @confirm="reativarUsuario"
        @cancel="showConfirmReativacao = false"
      />
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from '@/composables/useToast'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import UserForm from '@/features/users/components/UserForm.vue'
import ConfirmModal from '@/components/ui/modals/ConfirmModal.vue'
import userService, { userUtils } from '@/services/userService.js'
import logger from '@/utils/logger.js'

// Router
const route = useRoute()
const router = useRouter()
const { success, error: toastError } = useToast()

// Estados reativos
const isLoading = ref(true)
const showUserForm = ref(false)
const showConfirmDesativacao = ref(false)
const showConfirmReativacao = ref(false)
const usuarioEditando = ref(null)
const usuarioParaDesativar = ref(null)
const usuarioParaReativar = ref(null)

// Dados
const usuarios = ref([])

// Filtros
const searchQuery = ref('')
const filtroRole = ref('')
const filtroStatus = ref('')

// Computeds
const usuariosFiltrados = computed(() => {
  let resultado = usuarios.value

  // Filtro por texto
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase().trim()
    resultado = resultado.filter(u =>
      u.nome?.toLowerCase().includes(query) ||
      u.email?.toLowerCase().includes(query)
    )
  }

  // Filtro por role
  if (filtroRole.value) {
    resultado = resultado.filter(u => u.role === filtroRole.value)
  }

  // Filtro por status
  if (filtroStatus.value) {
    const ativo = filtroStatus.value === 'ativo'
    resultado = resultado.filter(u => u.ativo === ativo)
  }

  return resultado
})

const totalUsuarios = computed(() => usuarios.value.length)
const usuariosAtivos = computed(() =>
  usuarios.value.filter(u => u.ativo).length
)
const percentualAtivos = computed(() =>
  totalUsuarios.value > 0
    ? Math.round((usuariosAtivos.value / totalUsuarios.value) * 100)
    : 0
)
const usuariosAdmin = computed(() =>
  usuarios.value.filter(u => u.role === 'ADMIN')
)
const usuariosEspeciais = computed(() =>
  usuarios.value.filter(u => u.role === 'COMPRADOR' || u.role === 'APROVADOR').length
)
const novosUsuariosMes = computed(() => {
  return Math.floor(totalUsuarios.value * 0.1)
})

// Métodos de classificação
const getRoleLabel = (role) => {
  return userUtils.getRoleLabel(role)
}

const getRoleClass = (role) => {
  return userUtils.getRoleClass(role)
}

const getStatusLabel = (ativo) => {
  return userUtils.getStatusLabel(ativo)
}

const getStatusClass = (ativo) => {
  return userUtils.getStatusClass(ativo)
}

// Métodos de ação
const carregarUsuarios = async () => {
  isLoading.value = true
  try {
    const response = await userService.listarUsuarios()
    usuarios.value = response || []
  } catch (error) {
    logger.error('Erro ao carregar usuários:', error)
    toastError('Erro ao carregar usuários')
  } finally {
    isLoading.value = false
  }
}

const abrirFormularioNovo = async () => {
  showUserForm.value = false // Garante que o modal está fechado
  await nextTick()
  usuarioEditando.value = null
  await nextTick()
  showUserForm.value = true
}

const editarUsuario = async (usuario) => {
  showUserForm.value = false // Garante que o modal está fechado
  await nextTick()
  usuarioEditando.value = usuario
  await nextTick()
  showUserForm.value = true
}

const fecharFormulario = () => {
  showUserForm.value = false
}

const salvarUsuario = async (dadosUsuario) => {
  try {
    if (usuarioEditando.value) {
      // Atualizar usuário existente
      await userService.atualizarUsuario(usuarioEditando.value.id, dadosUsuario)
      success('Usuário atualizado com sucesso!')
    } else {
      // Criar novo usuário
      await userService.criarUsuario(dadosUsuario)
      success('Usuário cadastrado com sucesso!')
    }

    await carregarUsuarios()
    fecharFormulario()
  } catch (error) {
    logger.error('❌ Erro ao salvar usuário:', error)

    let mensagemErro = 'Erro ao salvar usuário.'

    // Tratamento especial para erros de validação
    if (error.type === 'VALIDATION_ERROR') {
      mensagemErro = `Erro de validação: ${error.message}`
      toastError(mensagemErro, { duration: 7000 })
      return // Não fechar o formulário para permitir correções
    }

    // Outros tipos de erro
    if (error.message.includes('401')) {
      mensagemErro = 'Erro de autenticação: Faça login novamente.'
    } else if (error.message.includes('400')) {
      mensagemErro = 'Dados inválidos: Verifique se todos os campos estão preenchidos corretamente.'
    } else if (error.message.includes('409')) {
      mensagemErro = 'Conflito: Já existe um usuário com este email.'
    } else if (error.message.includes('500')) {
      mensagemErro = 'Erro interno do servidor. Tente novamente em alguns instantes.'
    } else if (error.message) {
      mensagemErro = error.message
    }

    toastError(mensagemErro, { duration: 7000 })
  }
}

const confirmarDesativacao = (usuario) => {
  usuarioParaDesativar.value = usuario
  showConfirmDesativacao.value = true
}

const desativarUsuario = async () => {
  if (!usuarioParaDesativar.value) return

  try {
    await userService.desativarUsuario(usuarioParaDesativar.value.id)
    success('Usuário desativado com sucesso!')
    await carregarUsuarios()
  } catch (error) {
    logger.error('Erro ao desativar usuário:', error)
    toastError('Erro ao desativar usuário')
  } finally {
    showConfirmDesativacao.value = false
    usuarioParaDesativar.value = null
  }
}

const confirmarReativacao = (usuario) => {
  usuarioParaReativar.value = usuario
  showConfirmReativacao.value = true
}

const reativarUsuario = async () => {
  if (!usuarioParaReativar.value) return

  try {
    await userService.reativarUsuario(usuarioParaReativar.value.id)
    success('Usuário reativado com sucesso!')
    await carregarUsuarios()
  } catch (error) {
    logger.error('Erro ao reativar usuário:', error)
    toastError('Erro ao reativar usuário')
  } finally {
    showConfirmReativacao.value = false
    usuarioParaReativar.value = null
  }
}

const filtrarUsuarios = () => {
  // A filtragem é feita automaticamente via computed
}

const limparFiltros = () => {
  searchQuery.value = ''
  filtroRole.value = ''
  filtroStatus.value = ''
}

// Lifecycle
onMounted(() => {
  carregarUsuarios()
})
</script>

<style scoped>
/* Importar layout global */
@import '../assets/css/layout.css';

/* Welcome Section */
.welcome-section {
  margin-bottom: 32px;
  padding: 24px 0;
}

.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
}

.welcome-content {
  flex: 1;
}

.welcome-title {
  font-family: Arial, sans-serif;
  font-size: 28px;
  font-weight: 700;
  color: #1F285F;
  margin: 0 0 8px 0;
  line-height: 1.3;
}

.welcome-subtitle {
  font-family: Arial, sans-serif;
  font-size: 16px;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
}

.action-buttons {
  display: flex;
  gap: 12px;
  align-items: center;
}

.action-button {
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 8px !important;
  background: #3b82f6 !important;
  color: white !important;
  border: none !important;
  padding: 12px 24px !important;
  border-radius: 8px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  font-family: Arial, sans-serif !important;
  cursor: pointer !important;
  transition: all 0.2s !important;
  white-space: nowrap !important;
  min-height: 44px !important;
  min-width: 180px !important;
  line-height: 1.5 !important;
}

.action-button:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.action-icon {
  flex-shrink: 0;
}

/* Métricas */
.metrics-section {
  margin-bottom: 32px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.metric-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px -3px rgba(0, 0, 0, 0.1);
}

.metric-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.metric-icon.total {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
}

.metric-icon.active {
  background: linear-gradient(135deg, #10b981, #047857);
}

.metric-icon.rating {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.metric-icon.value {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
}

.metric-label {
  font-size: 0.875rem;
  color: #6b7280;
  font-weight: 500;
}

.metric-value {
  font-size: 2rem;
  font-weight: 700;
  color: #111827;
  margin-bottom: 8px;
}

.metric-growth {
  font-size: 0.875rem;
  font-weight: 500;
}

.metric-growth.positive {
  color: #10b981;
}

.metric-growth.neutral {
  color: #6b7280;
}

/* Seção de busca */
.search-section {
  margin-bottom: 32px;
}

.search-container {
  display: flex;
  gap: 16px;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}

.search-input-container {
  flex: 1;
  position: relative;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
}

.search-input {
  width: 100%;
  padding: 12px 12px 12px 44px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-actions {
  display: flex;
  gap: 12px;
}

.filter-select {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  min-width: 140px;
}

.filter-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: white;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-button:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

/* Loading */
.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px;
  color: #6b7280;
}

.loading-spinner-large {
  width: 48px;
  height: 48px;
  border: 4px solid #e5e7eb;
  border-top: 4px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Tabela */
.table-section {
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.table-container {
  overflow-x: auto;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th {
  background: #f9fafb;
  padding: 16px;
  text-align: left;
  font-weight: 600;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
  font-size: 0.875rem;
}

.users-table td {
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
}

.table-row:hover {
  background: #f9fafb;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-weight: 600;
  color: #111827;
}

.email {
  color: #6b7280;
  font-size: 0.875rem;
}

.role-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.role-tag.admin {
  background: #fef3c7;
  color: #d97706;
}

.role-tag.usuario {
  background: #dbeafe;
  color: #1d4ed8;
}

.role-tag.comprador {
  background: #d1fae5;
  color: #047857;
}

.role-tag.aprovador {
  background: #e0e7ff;
  color: #4338ca;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

.status-badge.active {
  background: #d1fae5;
  color: #047857;
}

.status-badge.inactive {
  background: #fee2e2;
  color: #dc2626;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.edit {
  background: #dbeafe;
  color: #1d4ed8;
}

.action-btn.edit:hover {
  background: #bfdbfe;
}

.action-btn.delete {
  background: #fee2e2;
  color: #dc2626;
}

.action-btn.delete:hover {
  background: #fecaca;
}

.action-btn.reactivate {
  background: #d1fae5;
  color: #047857;
}

.action-btn.reactivate:hover {
  background: #a7f3d0;
}

/* Estado vazio */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 32px;
  text-align: center;
}

.empty-icon {
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 8px 0;
}

.empty-state p {
  color: #6b7280;
  margin: 0 0 24px 0;
}

.btn-primary {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

/* Responsividade */
@media (max-width: 1024px) {
  .welcome-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .search-container {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .search-actions {
    justify-content: stretch;
  }

  .filter-select {
    flex: 1;
  }
}

@media (max-width: 768px) {
  .metrics-grid {
    grid-template-columns: 1fr;
  }
}
</style>
