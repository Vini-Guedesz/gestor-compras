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
                <Icon name="users" type="metric" :size="24" fill="white" />
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
                <Icon name="active" type="metric" :size="24" fill="white" />
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
                <Icon name="admin" type="metric" :size="24" fill="white" />
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
                <Icon name="clipboard" type="metric" :size="24" fill="white" />
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
        <!-- Versão Desktop: Tabela -->
        <div class="table-container desktop-only">
          <table class="users-table" v-if="usuariosFiltrados.length > 0">
            <thead>
              <tr>
                <th class="col-id">ID</th>
                <th>Usuário</th>
                <th>E-mail</th>
                <th>Função</th>
                <th>Cadastro</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="usuario in usuariosFiltrados" :key="usuario.id" class="table-row" @click="visualizarUsuario(usuario)">
                <td class="id-cell">
                  <span class="usuario-id">U-{{ usuario.id }}</span>
                </td>
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
                <td class="date-cell">
                  <span class="date-text">{{ formatarData(usuario.dataCadastro) }}</span>
                </td>
                <td>
                  <span class="status-badge" :class="getStatusClass(usuario.ativo)">
                    {{ getStatusLabel(usuario.ativo) }}
                  </span>
                </td>
                <td class="actions-cell" @click.stop>
                  <div class="action-buttons">
                    <button class="action-btn view" @click="visualizarUsuario(usuario)" title="Visualizar">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor"
                          d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" />
                      </svg>
                    </button>
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

        <!-- Versão Mobile: Cards -->
        <div class="usuarios-cards mobile-only">
          <div v-if="usuariosFiltrados.length > 0" class="cards-container">
            <div v-for="usuario in usuariosFiltrados" :key="usuario.id" class="usuario-card">
              <div class="card-header">
                <div class="card-header-left">
                  <span class="usuario-id-mobile">U-{{ usuario.id }}</span>
                  <span class="usuario-nome-mobile">{{ usuario.nome }}</span>
                  <span class="email-mobile">{{ usuario.email }}</span>
                  <span class="date-mobile">Cadastro: {{ formatarData(usuario.dataCadastro) }}</span>
                </div>
                <div class="card-header-right">
                  <span class="role-tag" :class="getRoleClass(usuario.role)">
                    {{ getRoleLabel(usuario.role) }}
                  </span>
                  <span class="status-badge" :class="getStatusClass(usuario.ativo)">
                    {{ getStatusLabel(usuario.ativo) }}
                  </span>
                </div>
              </div>

              <div class="card-actions">
                <button class="action-btn-mobile view" @click="visualizarUsuario(usuario)" title="Visualizar">
                  <svg viewBox="0 0 24 24" width="18" height="18">
                    <path fill="currentColor"
                      d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" />
                  </svg>
                  Ver
                </button>

                <button class="action-btn-mobile edit" @click="editarUsuario(usuario)" title="Editar">
                  <svg viewBox="0 0 24 24" width="18" height="18">
                    <path fill="currentColor"
                      d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                  </svg>
                  Editar
                </button>

                <button v-if="usuario.ativo" class="action-btn-mobile delete" @click="confirmarDesativacao(usuario)" title="Desativar">
                  <svg viewBox="0 0 24 24" width="18" height="18">
                    <path fill="currentColor"
                      d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 11H7v-2h10v2z" />
                  </svg>
                  Desativar
                </button>

                <button v-else class="action-btn-mobile reactivate" @click="confirmarReativacao(usuario)" title="Reativar">
                  <svg viewBox="0 0 24 24" width="18" height="18">
                    <path fill="currentColor"
                      d="M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z" />
                  </svg>
                  Reativar
                </button>
              </div>
            </div>
          </div>

          <!-- Estado vazio mobile -->
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

      <!-- Modal de Detalhes do Usuário -->
      <div v-if="showDetalhesModal" class="modal-overlay" @click="fecharDetalhes">
        <div class="detalhes-modal-usuario" @click.stop>
          <!-- Header com Avatar -->
          <div class="detalhes-header-usuario">
            <div class="header-content-usuario">
              <div class="usuario-avatar">
                <svg viewBox="0 0 24 24" width="40" height="40">
                  <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z"/>
                </svg>
              </div>
              <div class="usuario-header-info">
                <h2 class="usuario-nome">{{ usuarioSelecionado?.nome }}</h2>
                <p class="usuario-email">{{ usuarioSelecionado?.email }}</p>
                <div class="usuario-badges">
                  <span class="role-tag-modal" :class="getRoleClass(usuarioSelecionado?.role)">
                    {{ getRoleLabel(usuarioSelecionado?.role) }}
                  </span>
                  <span class="status-badge-modal" :class="getStatusClass(usuarioSelecionado?.ativo)">
                    {{ getStatusLabel(usuarioSelecionado?.ativo) }}
                  </span>
                </div>
              </div>
            </div>
            <button @click="fecharDetalhes" class="close-button-usuario">&times;</button>
          </div>

          <div class="detalhes-body-usuario" v-if="usuarioSelecionado">
            <!-- Card de Informações Básicas -->
            <div class="info-card">
              <div class="info-card-header">
                <svg viewBox="0 0 24 24" width="20" height="20">
                  <path fill="currentColor" d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                </svg>
                <h3>Informações do Usuário</h3>
              </div>
              <div class="info-card-body">
                <div class="info-row">
                  <div class="info-col">
                    <span class="info-label-new">ID do Usuário</span>
                    <span class="info-value-new">
                      <span class="info-badge">U-{{ usuarioSelecionado.id }}</span>
                    </span>
                  </div>
                  <div class="info-col">
                    <span class="info-label-new">Data de Cadastro</span>
                    <span class="info-value-new">{{ formatarDataCompleta(usuarioSelecionado.dataCadastro) }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Card de Permissões -->
            <div class="info-card">
              <div class="info-card-header">
                <svg viewBox="0 0 24 24" width="20" height="20">
                  <path fill="currentColor" d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm0 10.99h7c-.53 4.12-3.28 7.79-7 8.94V12H5V6.3l7-3.11v8.8z"/>
                </svg>
                <h3>Permissões e Acessos</h3>
              </div>
              <div class="info-card-body">
                <div class="permissions-grid">
                  <div class="permission-card" :class="usuarioSelecionado.role === 'ADMIN' ? 'active' : ''">
                    <div class="permission-icon-wrapper">
                      <svg viewBox="0 0 24 24" width="24" height="24">
                        <path fill="currentColor" d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm-2 16l-4-4 1.41-1.41L10 14.17l6.59-6.59L18 9l-8 8z"/>
                      </svg>
                    </div>
                    <div class="permission-content">
                      <span class="permission-title">Administrador</span>
                      <span class="permission-desc">Acesso total ao sistema</span>
                    </div>
                    <div class="permission-status-indicator" v-if="usuarioSelecionado.role === 'ADMIN'">
                      <svg viewBox="0 0 24 24" width="20" height="20">
                        <path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                      </svg>
                    </div>
                  </div>

                  <div class="permission-card" :class="['COMPRADOR', 'ADMIN'].includes(usuarioSelecionado.role) ? 'active' : ''">
                    <div class="permission-icon-wrapper">
                      <svg viewBox="0 0 24 24" width="24" height="24">
                        <path fill="currentColor" d="M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.08-.14.12-.31.12-.48 0-.55-.45-1-1-1H5.21l-.94-2H1zm16 16c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z"/>
                      </svg>
                    </div>
                    <div class="permission-content">
                      <span class="permission-title">Comprador</span>
                      <span class="permission-desc">Gerenciar compras e fornecedores</span>
                    </div>
                    <div class="permission-status-indicator" v-if="['COMPRADOR', 'ADMIN'].includes(usuarioSelecionado.role)">
                      <svg viewBox="0 0 24 24" width="20" height="20">
                        <path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                      </svg>
                    </div>
                  </div>

                  <div class="permission-card" :class="['APROVADOR', 'ADMIN'].includes(usuarioSelecionado.role) ? 'active' : ''">
                    <div class="permission-icon-wrapper">
                      <svg viewBox="0 0 24 24" width="24" height="24">
                        <path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                      </svg>
                    </div>
                    <div class="permission-content">
                      <span class="permission-title">Aprovador</span>
                      <span class="permission-desc">Aprovar e revisar pedidos</span>
                    </div>
                    <div class="permission-status-indicator" v-if="['APROVADOR', 'ADMIN'].includes(usuarioSelecionado.role)">
                      <svg viewBox="0 0 24 24" width="20" height="20">
                        <path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                      </svg>
                    </div>
                  </div>

                  <div class="permission-card active">
                    <div class="permission-icon-wrapper">
                      <svg viewBox="0 0 24 24" width="24" height="24">
                        <path fill="currentColor" d="M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z"/>
                      </svg>
                    </div>
                    <div class="permission-content">
                      <span class="permission-title">Usuário</span>
                      <span class="permission-desc">Criar solicitações de pedidos</span>
                    </div>
                    <div class="permission-status-indicator">
                      <svg viewBox="0 0 24 24" width="20" height="20">
                        <path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                      </svg>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Footer com ações -->
          <div class="detalhes-footer-usuario">
            <button @click="fecharDetalhes" class="btn-modal-secondary">
              <svg viewBox="0 0 24 24" width="18" height="18">
                <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
              </svg>
              Fechar
            </button>
            <button @click="editarUsuarioDetalhes" class="btn-modal-primary">
              <svg viewBox="0 0 24 24" width="18" height="18">
                <path fill="currentColor" d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
              </svg>
              Editar Usuário
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from '@/composables/useToast'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import Icon from '@/components/ui/Icon.vue'
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
const showDetalhesModal = ref(false)
const usuarioEditando = ref(null)
const usuarioParaDesativar = ref(null)
const usuarioParaReativar = ref(null)
const usuarioSelecionado = ref(null)

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
    resultado = resultado.filter(u => {
      const idPadronizado = `u-${u.id}`.toLowerCase()
      const idNumerico = u.id?.toString()
      // Permite buscar por: "1", "U-1", nome ou email
      return idNumerico?.includes(query) ||
             idPadronizado.includes(query) ||
             u.nome?.toLowerCase().includes(query) ||
             u.email?.toLowerCase().includes(query)
    })
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

const visualizarUsuario = (usuario) => {
  usuarioSelecionado.value = usuario
  showDetalhesModal.value = true
}

const fecharDetalhes = () => {
  showDetalhesModal.value = false
  usuarioSelecionado.value = null
}

const editarUsuarioDetalhes = async () => {
  const usuario = usuarioSelecionado.value
  fecharDetalhes()
  await nextTick()
  editarUsuario(usuario)
}

const formatarData = (data) => {
  if (!data) return 'N/A'
  try {
    return new Date(data).toLocaleDateString('pt-BR')
  } catch {
    return 'Data inválida'
  }
}

const formatarDataCompleta = (data) => {
  if (!data) return 'N/A'
  try {
    return new Date(data).toLocaleString('pt-BR')
  } catch {
    return 'Data inválida'
  }
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

  // Verificar se veio de uma busca global por ID de usuário
  const filtrarUsuarioId = route.query.filtrarUsuario
  if (filtrarUsuarioId) {
    searchQuery.value = filtrarUsuarioId
    // Limpar o query parameter da URL após aplicar o filtro
    router.replace({ query: {} })
  }
})

// Watch para mudanças na query string
watch(() => route.query.filtrarUsuario, (novoId) => {
  if (novoId) {
    searchQuery.value = novoId
    // Limpar o query parameter da URL após aplicar o filtro
    router.replace({ query: {} })
  }
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

.metric-icon svg {
  width: 24px;
  height: 24px;
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

.table-row {
  cursor: pointer;
  transition: all 0.2s;
}

.table-row:hover {
  background: #f9fafb;
  transform: translateX(2px);
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.col-id {
  width: 100px;
}

.usuario-id {
  font-weight: 600;
  color: #1F285F;
  font-size: 0.875rem;
  font-family: 'Courier New', monospace;
}

.user-name {
  font-weight: 600;
  color: #111827;
}

.email {
  color: #6b7280;
  font-size: 0.875rem;
}

.date-cell {
  white-space: nowrap;
}

.date-text {
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

/* Mobile Cards Layout */
.usuarios-cards {
  display: none;
}

.cards-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.usuario-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f3f4f6;
}

.card-header-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}

.card-header-right {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: flex-end;
}

.usuario-id-mobile {
  font-weight: 600;
  font-size: 0.875rem;
  color: #1F285F;
  font-family: 'Courier New', monospace;
  margin-bottom: 4px;
}

.usuario-nome-mobile {
  font-weight: 600;
  font-size: 1rem;
  color: #1f2937;
}

.email-mobile {
  font-size: 0.8125rem;
  color: #6b7280;
}

.date-mobile {
  font-size: 0.75rem;
  color: #9ca3af;
  margin-top: 4px;
}

.card-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.action-btn-mobile {
  flex: 1;
  min-width: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 14px;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
  background: white;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  color: #6b7280;
}

.action-btn-mobile:hover {
  background: #f3f4f6;
  color: #374151;
}

.action-btn-mobile.edit {
  color: #f59e0b;
  border-color: #f59e0b;
}

.action-btn-mobile.edit:hover {
  background: #fef3c7;
}

.action-btn-mobile.delete {
  color: #ef4444;
  border-color: #ef4444;
}

.action-btn-mobile.delete:hover {
  background: #fee2e2;
}

.action-btn-mobile.reactivate {
  color: #10b981;
  border-color: #10b981;
}

.action-btn-mobile.reactivate:hover {
  background: #d1fae5;
}

.action-btn-mobile.view {
  color: #3b82f6;
  border-color: #3b82f6;
}

.action-btn-mobile.view:hover {
  background: #dbeafe;
}

/* Visibility toggles */
.desktop-only {
  display: block;
}

.mobile-only {
  display: none;
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

  /* Toggle entre table e cards */
  .desktop-only {
    display: none !important;
  }

  .mobile-only {
    display: block !important;
  }

  .welcome-title {
    font-size: 22px;
  }

  .welcome-subtitle {
    font-size: 14px;
  }

  .action-button {
    padding: 10px 16px !important;
    min-width: 140px !important;
    font-size: 13px !important;
  }

  .search-container {
    padding: 16px;
  }

  .search-actions {
    flex-direction: column;
    gap: 10px;
  }

  .filter-select {
    min-width: 100%;
  }

  .filter-button {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .welcome-section {
    margin-bottom: 20px;
    padding: 16px 0;
  }

  .welcome-title {
    font-size: 20px;
  }

  .metrics-section {
    margin-bottom: 20px;
  }

  .metric-card {
    padding: 16px;
  }

  .metric-icon {
    width: 40px;
    height: 40px;
  }

  .metric-value {
    font-size: 1.5rem;
  }

  .search-section {
    margin-bottom: 20px;
  }

  .usuario-card {
    padding: 14px;
  }

  .card-actions {
    flex-direction: column;
  }

  .action-btn-mobile {
    width: 100%;
  }
}

/* Ajustes para telas muito pequenas (360px e menor) */
@media (max-width: 380px) {
  .welcome-section {
    margin-bottom: 12px;
    padding: 8px 0;
  }

  .welcome-title {
    font-size: 18px;
  }

  .welcome-subtitle {
    font-size: 12px;
  }

  .action-button {
    padding: 8px 14px !important;
    font-size: 12px !important;
    min-width: 120px !important;
  }

  .metrics-section {
    margin-bottom: 16px;
  }

  .metric-card {
    padding: 12px;
  }

  .metric-icon {
    width: 32px;
    height: 32px;
  }

  .metric-icon svg {
    width: 18px;
    height: 18px;
  }

  .metric-label {
    font-size: 0.6875rem;
  }

  .metric-value {
    font-size: 1.25rem;
  }

  .metric-growth {
    font-size: 0.625rem;
  }

  .search-section {
    margin-bottom: 16px;
  }

  .search-container {
    padding: 10px;
    gap: 10px;
  }

  .search-input {
    padding: 9px 9px 9px 36px;
    font-size: 14px;
  }

  .search-icon {
    width: 18px;
    height: 18px;
    left: 10px;
  }

  .filter-select {
    padding: 9px 8px;
    font-size: 13px;
  }

  .filter-button {
    padding: 9px 12px;
    font-size: 13px;
  }

  .table-section {
    border-radius: 8px;
  }

  .usuario-card {
    padding: 10px;
    gap: 8px;
    border-radius: 6px;
  }

  .card-header {
    gap: 8px;
    padding-bottom: 8px;
  }

  .usuario-id-mobile {
    font-size: 0.6875rem;
  }

  .usuario-nome-mobile {
    font-size: 0.8125rem;
  }

  .email-mobile {
    font-size: 0.6875rem;
  }

  .role-tag,
  .status-badge {
    padding: 2px 6px;
    font-size: 0.6rem;
  }

  .card-actions {
    gap: 6px;
  }

  .action-btn-mobile {
    padding: 7px 10px;
    font-size: 0.75rem;
    gap: 4px;
  }

  .action-btn-mobile svg {
    width: 16px;
    height: 16px;
  }

  .empty-state {
    padding: 24px 12px;
  }

  .empty-icon {
    width: 40px;
    height: 40px;
  }

  .empty-state h3 {
    font-size: 0.9375rem;
  }

  .empty-state p {
    font-size: 0.8125rem;
  }

  .btn-primary {
    padding: 10px 18px;
    font-size: 0.8125rem;
  }
}

/* Modal de Detalhes do Usuário - Estilização Melhorada */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 20px;
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.detalhes-modal-usuario {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-width: 800px;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* Header com Avatar */
.detalhes-header-usuario {
  background: linear-gradient(135deg, #1F285F 0%, #2d3b7c 100%);
  padding: 32px 28px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  position: relative;
  overflow: hidden;
}

.detalhes-header-usuario::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.header-content-usuario {
  display: flex;
  gap: 20px;
  align-items: center;
  flex: 1;
  z-index: 1;
}

.usuario-avatar {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 3px solid rgba(255, 255, 255, 0.3);
  flex-shrink: 0;
}

.usuario-avatar svg {
  fill: white;
}

.usuario-header-info {
  flex: 1;
}

.usuario-nome {
  margin: 0 0 6px 0;
  font-size: 1.75rem;
  font-weight: 700;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.usuario-email {
  margin: 0 0 12px 0;
  font-size: 0.95rem;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 400;
}

.usuario-badges {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.role-tag-modal,
.status-badge-modal {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  backdrop-filter: blur(10px);
}

.role-tag-modal.admin {
  background: rgba(251, 191, 36, 0.2);
  color: #fef3c7;
  border: 1px solid rgba(251, 191, 36, 0.3);
}

.role-tag-modal.usuario {
  background: rgba(96, 165, 250, 0.2);
  color: #dbeafe;
  border: 1px solid rgba(96, 165, 250, 0.3);
}

.role-tag-modal.comprador {
  background: rgba(52, 211, 153, 0.2);
  color: #d1fae5;
  border: 1px solid rgba(52, 211, 153, 0.3);
}

.role-tag-modal.aprovador {
  background: rgba(139, 92, 246, 0.2);
  color: #e0e7ff;
  border: 1px solid rgba(139, 92, 246, 0.3);
}

.status-badge-modal.active {
  background: rgba(52, 211, 153, 0.2);
  color: #d1fae5;
  border: 1px solid rgba(52, 211, 153, 0.3);
}

.status-badge-modal.inactive {
  background: rgba(248, 113, 113, 0.2);
  color: #fee2e2;
  border: 1px solid rgba(248, 113, 113, 0.3);
}

.close-button-usuario {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  font-size: 1.75rem;
  color: white;
  cursor: pointer;
  padding: 0;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s;
  z-index: 1;
}

.close-button-usuario:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: rotate(90deg);
}

/* Body */
.detalhes-body-usuario {
  padding: 28px;
  flex: 1;
  overflow-y: auto;
  background: #f9fafb;
}

.info-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  margin-bottom: 20px;
  overflow: hidden;
  transition: all 0.3s;
}

.info-card:hover {
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.info-card:last-child {
  margin-bottom: 0;
}

.info-card-header {
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  padding: 16px 20px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  gap: 12px;
}

.info-card-header svg {
  color: #1F285F;
}

.info-card-header h3 {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #111827;
}

.info-card-body {
  padding: 20px;
}

.info-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.info-col {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-label-new {
  font-size: 0.75rem;
  font-weight: 600;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.info-value-new {
  font-size: 1rem;
  color: #111827;
  font-weight: 500;
}

.info-badge {
  display: inline-block;
  background: linear-gradient(135deg, #1F285F 0%, #2d3b7c 100%);
  color: white;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 600;
}

/* Permissions Grid */
.permissions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.permission-card {
  background: #f9fafb;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  padding: 18px;
  display: flex;
  align-items: flex-start;
  gap: 14px;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.permission-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #e5e7eb;
  transition: all 0.3s;
}

.permission-card.active {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border-color: #86efac;
}

.permission-card.active::before {
  background: linear-gradient(180deg, #10b981 0%, #059669 100%);
}

.permission-card:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.permission-icon-wrapper {
  width: 48px;
  height: 48px;
  background: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.permission-card.active .permission-icon-wrapper {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.permission-icon-wrapper svg {
  color: #9ca3af;
}

.permission-card.active .permission-icon-wrapper svg {
  color: white;
}

.permission-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.permission-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: #111827;
}

.permission-desc {
  font-size: 0.8125rem;
  color: #6b7280;
  line-height: 1.4;
}

.permission-status-indicator {
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.permission-status-indicator svg {
  color: white;
}

/* Footer */
.detalhes-footer-usuario {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 28px;
  border-top: 1px solid #e5e7eb;
  background: white;
}

.btn-modal-secondary,
.btn-modal-primary {
  padding: 11px 22px;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
  border: none;
}

.btn-modal-secondary {
  background: #f3f4f6;
  color: #374151;
}

.btn-modal-secondary:hover {
  background: #e5e7eb;
  transform: translateY(-1px);
}

.btn-modal-primary {
  background: linear-gradient(135deg, #1F285F 0%, #2d3b7c 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(31, 40, 95, 0.4);
}

.btn-modal-primary:hover {
  box-shadow: 0 6px 16px rgba(31, 40, 95, 0.5);
  transform: translateY(-2px);
}

.btn-modal-secondary svg,
.btn-modal-primary svg {
  flex-shrink: 0;
}

.action-btn.view {
  background: #dbeafe;
  color: #1d4ed8;
}

.action-btn.view:hover {
  background: #bfdbfe;
}

/* Responsive */
@media (max-width: 768px) {
  .detalhes-header-usuario {
    padding: 24px 20px;
  }

  .header-content-usuario {
    flex-direction: column;
    align-items: flex-start;
  }

  .usuario-avatar {
    width: 64px;
    height: 64px;
  }

  .usuario-nome {
    font-size: 1.5rem;
  }

  .detalhes-body-usuario {
    padding: 20px;
  }

  .permissions-grid {
    grid-template-columns: 1fr;
  }

  .info-row {
    grid-template-columns: 1fr;
  }

  .detalhes-footer-usuario {
    flex-direction: column;
  }

  .btn-modal-secondary,
  .btn-modal-primary {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 640px) {
  .modal-overlay {
    padding: 0;
  }

  .detalhes-modal-usuario {
    max-width: 100%;
    width: 100%;
    border-radius: 0;
    max-height: 100vh;
    height: 100vh;
  }

  .detalhes-header-usuario {
    padding: 20px 16px;
  }

  .usuario-avatar {
    width: 56px;
    height: 56px;
  }

  .usuario-avatar svg {
    width: 32px;
    height: 32px;
  }

  .usuario-nome {
    font-size: 1.25rem;
  }

  .usuario-email {
    font-size: 0.875rem;
  }

  .usuario-badges {
    gap: 6px;
  }

  .role-tag-modal,
  .status-badge-modal {
    padding: 4px 10px;
    font-size: 0.7rem;
  }

  .detalhes-body-usuario {
    padding: 16px;
  }

  .info-card {
    border-radius: 12px;
  }

  .info-card-header {
    padding: 12px 16px;
  }

  .info-card-body {
    padding: 16px;
  }

  .permission-card {
    padding: 14px;
  }

  .permission-icon-wrapper {
    width: 40px;
    height: 40px;
  }

  .permission-icon-wrapper svg {
    width: 20px;
    height: 20px;
  }

  .permission-title {
    font-size: 0.875rem;
  }

  .permission-desc {
    font-size: 0.75rem;
  }

  .detalhes-footer-usuario {
    padding: 16px;
  }

  .btn-modal-secondary,
  .btn-modal-primary {
    padding: 10px 16px;
    font-size: 0.875rem;
  }
}
</style>
