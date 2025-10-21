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
            <h1 class="welcome-title">Gestão de Cotações 💰</h1>
            <p class="welcome-subtitle">
              Gerencie solicitações de cotações e compare propostas de fornecedores
            </p>
          </div>
          <div class="action-buttons">
            <button class="action-button secondary" @click="exportarRelatorio" :disabled="gerandoRelatorio">
              <svg class="action-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="white" d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
              </svg>
              <span v-if="gerandoRelatorio" class="loading-content">
                <span class="loading-spinner"></span>
                Gerando...
              </span>
              <span v-else>Gerar Relatório</span>
            </button>
            <button class="action-button" @click="abrirFormularioNova">
              <svg class="action-icon" viewBox="0 0 24 24" width="20" height="20">
                <path fill="white" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" />
              </svg>
              Nova Cotação
            </button>
          </div>
        </div>
      </div>

      <!-- Indicadores (Cards de Métricas Rápidas) -->
      <div class="metrics-section">
        <div class="metrics-grid">
          <!-- Cotações Abertas -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon total">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"/>
                </svg>
              </div>
              <span class="metric-label">Cotações Abertas</span>
            </div>
            <div class="metric-value">{{ resumo.abertas }}</div>
            <div class="metric-growth positive">{{ novasCotacoesMes }} novas este mês</div>
          </div>

          <!-- Em Análise -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon active">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
              <span class="metric-label">Em Análise</span>
            </div>
            <div class="metric-value">{{ resumo.aguardando }}</div>
            <div class="metric-growth neutral">Aguardando propostas</div>
          </div>

          <!-- Finalizadas -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon rating">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
              <span class="metric-label">Finalizadas</span>
            </div>
            <div class="metric-value">{{ resumo.finalizadas }}</div>
            <div class="metric-growth positive">{{ percentualFinalizadas }}% de sucesso</div>
          </div>

          <!-- Vencidas -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon value">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
                </svg>
              </div>
              <span class="metric-label">Vencidas</span>
            </div>
            <div class="metric-value">{{ resumo.vencidas }}</div>
            <div class="metric-growth negative">Requer atenção</div>
          </div>
        </div>
      </div>

      <!-- Controles e Filtros -->
      <div class="controls-section">
        <div class="search-container">
          <input
            type="text"
            v-model="termoBusca"
            placeholder="Buscar cotações por ID, fornecedor, item ou preço..."
            class="search-input"
            @input="buscarCotacoes"
          >
          <svg class="search-icon" viewBox="0 0 24 24" width="20" height="20">
            <path fill="currentColor" d="M9.5,3A6.5,6.5 0 0,1 16,9.5C16,11.11 15.41,12.59 14.44,13.73L14.71,14H15.5L20.5,19L19,20.5L14,15.5V14.71L13.73,14.44C12.59,15.41 11.11,16 9.5,16A6.5,6.5 0 0,1 3,9.5A6.5,6.5 0 0,1 9.5,3M9.5,5C7,5 5,7 5,9.5C5,12 7,14 9.5,14C12,14 14,12 14,9.5C14,7 12,5 9.5,5Z"/>
          </svg>
        </div>

        <div class="filter-controls">
          <select v-model="filtros.periodo" class="filter-select" @change="aplicarFiltros">
            <option value="">Todos os Períodos</option>
            <option value="semana">Última semana</option>
            <option value="mes">Último mês</option>
            <option value="trimestre">Último trimestre</option>
          </select>

          <select v-model="filtros.fornecedor" class="filter-select" @change="aplicarFiltros">
            <option value="">Todos os Fornecedores</option>
            <option v-for="fornecedor in fornecedoresUnicos" :key="`${fornecedor.id}-${fornecedor.tipo}`" :value="fornecedor.id">
              {{ fornecedor.nome }}
            </option>
          </select>

          <button @click="limparFiltros" class="clear-filters-btn">
            <svg viewBox="0 0 24 24" width="16" height="16">
              <path fill="currentColor" d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z"/>
            </svg>
            Limpar
          </button>
        </div>
      </div>

      <!-- Tabela de Cotações -->
      <div class="data-section">
        <div class="section-header">
          <h2 class="section-title">Lista de Cotações</h2>
          <div class="view-controls">
            <button
              :class="['view-button', { active: visualizacao === 'tabela' }]"
              @click="visualizacao = 'tabela'"
            >
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M3,3H21A2,2 0 0,1 23,5V19A2,2 0 0,1 21,21H3A2,2 0 0,1 1,19V5A2,2 0 0,1 3,3M5,7V5H7V7H5M9,5V7H11V7H9M13,5V7H15V7H13M17,5V7H19V7H17M5,9V11H7V9H5M9,9V11H11V9H9M13,9V11H15V9H13M17,9V11H19V9H17M5,13V15H7V13H5M9,13V15H11V13H9M13,13V15H15V13H13M17,13V15H19V13H17M5,17V19H7V17H5M9,17V19H11V17H9M13,17V19H15V17H13M17,17V19H19V17H17Z"/>
              </svg>
              Tabela
            </button>
            <button
              :class="['view-button', { active: visualizacao === 'cards' }]"
              @click="visualizacao = 'cards'"
            >
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M3,11H11V3H3M3,21H11V13H3M13,21H21V13H13M13,3V11H21V3"/>
              </svg>
              Cards
            </button>
          </div>
        </div>

        <!-- Visualização em Tabela -->
        <div v-if="visualizacao === 'tabela'" class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th @click="ordenar('id')" class="sortable">
                  ID
                  <svg v-if="ordenacao.campo === 'id'" class="sort-icon" viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" :d="ordenacao.direcao === 'asc' ? 'M7,10L12,15L17,10H7Z' : 'M7,15L12,10L17,15H7Z'"/>
                  </svg>
                </th>
                <th>Fornecedor</th>
                <th>Item do Pedido</th>
                <th @click="ordenar('preco')" class="sortable">
                  Preço
                  <svg v-if="ordenacao.campo === 'preco'" class="sort-icon" viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" :d="ordenacao.direcao === 'asc' ? 'M7,10L12,15L17,10H7Z' : 'M7,15L12,10L17,15H7Z'"/>
                  </svg>
                </th>
                <th @click="ordenar('prazoEmDiasUteis')" class="sortable">
                  Prazo de Entrega
                  <svg v-if="ordenacao.campo === 'prazoEmDiasUteis'" class="sort-icon" viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" :d="ordenacao.direcao === 'asc' ? 'M7,10L12,15L17,10H7Z' : 'M7,15L12,10L17,15H7Z'"/>
                  </svg>
                </th>
                <th @click="ordenar('dataLimite')" class="sortable">
                  Validade da Cotação
                  <svg v-if="ordenacao.campo === 'dataLimite'" class="sort-icon" viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor" :d="ordenacao.direcao === 'asc' ? 'M7,10L12,15L17,10H7Z' : 'M7,15L12,10L17,15H7Z'"/>
                  </svg>
                </th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              <!-- Loading State -->
              <tr v-if="carregandoCotacoes" class="loading-row">
                <td colspan="6" class="loading-cell">
                  <div class="loading-content">
                    <span class="loading-spinner"></span>
                    <span>Carregando cotações...</span>
                  </div>
                </td>
              </tr>
              <!-- Empty State -->
              <tr v-else-if="cotacoes.length === 0" class="empty-row">
                <td colspan="6" class="empty-cell">
                  <div class="empty-content">
                    <svg viewBox="0 0 24 24" width="48" height="48" class="empty-icon">
                      <path fill="#9CA3AF" d="M9,12L11,14L15,10M20,6C20.58,6 21.05,6.2 21.42,6.59C21.8,7 22,7.45 22,8V16C22,16.55 21.8,17 21.42,17.41C21.05,17.8 20.58,18 20,18H4C3.42,18 2.95,17.8 2.58,17.41C2.2,17 2,16.55 2,16V8C2,7.45 2.2,7 2.58,6.59C2.95,6.2 3.42,6 4,6H20M20,8H4V16H20V8Z"/>
                    </svg>
                    <h3>Nenhuma cotação encontrada</h3>
                    <p>Crie sua primeira cotação clicando no botão "Nova Cotação"</p>
                  </div>
                </td>
              </tr>
              <!-- Data Rows -->
              <tr v-else v-for="cotacao in cotacoesPaginadas" :key="cotacao.id" class="table-row">
                <td>
                  <span class="id-badge">{{ String(cotacao.id).padStart(3, '0') }}</span>
                </td>
                <td>
                  <div class="fornecedor-cell">
                    <span class="fornecedor-id">{{ getNomeFornecedor(cotacao.fornecedorId, cotacao.tipoFornecedor) }}</span>
                  </div>
                </td>
                <td>
                  <div class="item-cell">
                    <span class="item-id">Item #{{ cotacao.itemPedidoId || 'N/A' }}</span>
                  </div>
                </td>
                <td>
                  <div class="price-cell">
                    <span class="price-value">R$ {{ formatarPreco(cotacao.preco) }}</span>
                  </div>
                </td>
                <td>
                  <div class="prazo-cell">
                    <span class="prazo-value">{{ cotacao.prazoEmDiasUteis ? cotacao.prazoEmDiasUteis + ' dias úteis' : 'N/A' }}</span>
                  </div>
                </td>
                <td>
                  <div class="deadline-cell" :class="{ 'deadline-expired': isDataLimiteVencida(cotacao.dataLimite) }">
                    <div class="deadline-date">{{ formatarData(cotacao.dataLimite) }}</div>
                    <div v-if="cotacao.dataLimite" class="deadline-remaining">{{ getDiasRestantes(cotacao.dataLimite) }}</div>
                  </div>
                </td>
                <td>
                  <div class="actions-cell">
                    <button @click="visualizarCotacao(cotacao.id)" class="action-btn view" title="Visualizar">
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M12,9A3,3 0 0,0 9,12A3,3 0 0,0 12,15A3,3 0 0,0 15,12A3,3 0 0,0 12,9M12,17A5,5 0 0,1 7,12A5,5 0 0,1 12,7A5,5 0 0,1 17,12A5,5 0 0,1 12,17M12,4.5C7,4.5 2.73,7.61 1,12C2.73,16.39 7,19.5 12,19.5C17,19.5 21.27,16.39 23,12C21.27,7.61 17,4.5 12,4.5Z"/>
                      </svg>
                    </button>
                    <button
                      v-if="podeEditar()"
                      @click="editarCotacao(cotacao)"
                      class="action-btn edit"
                      title="Editar"
                    >
                      <svg viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M20.71,7.04C21.1,6.65 21.1,6 20.71,5.63L18.37,3.29C18,2.9 17.35,2.9 16.96,3.29L15.12,5.12L18.87,8.87M3,17.25V21H6.75L17.81,9.93L14.06,6.18L3,17.25Z"/>
                      </svg>
                    </button>

                    <button
                      v-if="podeDeletar()"
                      @click="deletarCotacao(cotacao.id)"
                      class="action-btn delete"
                      title="Excluir"
                      :disabled="operacaoEmAndamento"
                    >
                      <svg v-if="!operacaoEmAndamento" viewBox="0 0 24 24" width="16" height="16">
                        <path fill="currentColor" d="M19,4H15.5L14.5,3H9.5L8.5,4H5V6H19M6,19A2,2 0 0,0 8,21H16A2,2 0 0,0 18,19V7H6V19Z"/>
                      </svg>
                      <span v-else class="loading-spinner-small"></span>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Visualização em Cards -->
        <div v-if="visualizacao === 'cards'" class="cards-container">
          <div v-for="cotacao in cotacoesPaginadas" :key="cotacao.id" class="cotacao-card">
            <div class="card-header">
              <span class="card-id">{{ String(cotacao.id).padStart(3, '0') }}</span>
              <span class="card-price">R$ {{ formatarPreco(cotacao.preco) }}</span>
            </div>
            <div class="card-body">
              <h3 class="card-title">Cotação #{{ cotacao.id }}</h3>
              <p class="card-subtitle">{{ getNomeFornecedor(cotacao.fornecedorId, cotacao.tipoFornecedor) }}</p>
              <div class="card-meta">
                <div class="meta-item">
                  <span class="meta-label">Item do Pedido:</span>
                  <span class="meta-value">#{{ cotacao.itemPedidoId }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">Prazo de Entrega:</span>
                  <span class="meta-value">{{ cotacao.prazoEmDiasUteis ? cotacao.prazoEmDiasUteis + ' dias úteis' : 'N/A' }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">Validade da Cotação:</span>
                  <span class="meta-value" :class="{ expired: isDataLimiteVencida(cotacao.dataLimite) }">{{ formatarData(cotacao.dataLimite) }}</span>
                </div>
              </div>
            </div>
            <div class="card-actions">
              <button @click="visualizarCotacao(cotacao.id)" class="card-action-btn primary">
                Visualizar
              </button>
              <button
                v-if="podeEditar()"
                @click="editarCotacao(cotacao)"
                class="card-action-btn secondary"
              >
                Editar
              </button>

              <button
                v-if="podeDeletar()"
                @click="deletarCotacao(cotacao.id)"
                class="card-action-btn danger"
                title="Excluir Cotação"
                :disabled="operacaoEmAndamento"
              >
                <span v-if="operacaoEmAndamento" class="loading-spinner-small"></span>
                {{ operacaoEmAndamento ? 'Excluindo...' : 'Excluir' }}
              </button>
            </div>
          </div>
        </div>

        <!-- Paginação -->
        <div v-if="totalPaginas > 1" class="pagination">
          <div class="pagination-info">
            Mostrando {{ (paginaAtual - 1) * itensPorPagina + 1 }} -
            {{ Math.min(paginaAtual * itensPorPagina, totalItens) }} de {{ totalItens }} cotações
          </div>
          <div class="pagination-controls">
            <button
              @click="irParaPagina(paginaAtual - 1)"
              :disabled="paginaAtual === 1"
              class="pagination-btn"
            >
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M15.41,16.58L10.83,12L15.41,7.41L14,6L8,12L14,18L15.41,16.58Z"/>
              </svg>
            </button>

            <button
              v-for="pagina in paginasVisiveis"
              :key="pagina"
              @click="irParaPagina(pagina)"
              :class="['pagination-number', { active: pagina === paginaAtual }]"
            >
              {{ pagina }}
            </button>

            <button
              @click="irParaPagina(paginaAtual + 1)"
              :disabled="paginaAtual === totalPaginas"
              class="pagination-btn"
            >
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M8.59,16.58L13.17,12L8.59,7.41L10,6L16,12L10,18L8.59,16.58Z"/>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Modal de Detalhes da Cotação -->
      <div v-if="showDetalhesModal" class="modal-overlay" @click="fecharDetalhes">
        <div class="detalhes-modal" @click.stop>
          <!-- Header -->
          <div class="detalhes-header">
            <div class="header-title-group">
              <h2>Detalhes da Cotação #{{ String(cotacaoSelecionada?.id).padStart(3, '0') }}</h2>
            </div>
            <button @click="fecharDetalhes" class="close-button">&times;</button>
          </div>

          <div class="detalhes-body" v-if="cotacaoSelecionada">
            <!-- Informações do Item -->
            <div class="detalhe-section">
              <h3 class="section-title">Informações do Item</h3>
              <div v-if="itemSelecionado" class="info-grid">
                <div class="info-item full-width">
                  <span class="info-label">Nome do Item</span>
                  <span class="info-value highlight">{{ itemSelecionado.nome }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Quantidade</span>
                  <span class="info-value">{{ itemSelecionado.quantidade }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">ID do Item</span>
                  <span class="info-value">#{{ cotacaoSelecionada.itemPedidoId }}</span>
                </div>
                <div v-if="itemSelecionado.descricao" class="info-item full-width">
                  <span class="info-label">Descrição</span>
                  <span class="info-value">{{ itemSelecionado.descricao }}</span>
                </div>
                <div v-if="itemSelecionado.observacao" class="info-item full-width">
                  <span class="info-label">Observações</span>
                  <span class="info-value">{{ itemSelecionado.observacao }}</span>
                </div>
              </div>
              <div v-else class="loading-state">
                <div class="loading-spinner-small"></div>
                <span>Carregando detalhes do item...</span>
              </div>
            </div>

            <!-- Informações da Cotação -->
            <div class="detalhe-section">
              <h3 class="section-title">Dados da Cotação</h3>
              <div class="info-grid">
                <div class="info-item">
                  <span class="info-label">Fornecedor</span>
                  <span class="info-value">{{ getNomeFornecedor(cotacaoSelecionada.fornecedorId, cotacaoSelecionada.tipoFornecedor) }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">ID da Cotação</span>
                  <span class="info-value">#{{ cotacaoSelecionada.id }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Preço</span>
                  <span class="info-value price-highlight">R$ {{ formatarPreco(cotacaoSelecionada.preco) }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Prazo de Entrega</span>
                  <span class="info-value">{{ cotacaoSelecionada.prazoEmDiasUteis ? cotacaoSelecionada.prazoEmDiasUteis + ' dias úteis' : 'N/A' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">Validade da Cotação</span>
                  <span class="info-value" :class="{ 'expired': isDataLimiteVencida(cotacaoSelecionada.dataLimite) }">
                    {{ formatarData(cotacaoSelecionada.dataLimite) }}
                  </span>
                </div>
                <div class="info-item">
                  <span class="info-label">Status da Validade</span>
                  <span class="info-value" :class="{ 'expired': isDataLimiteVencida(cotacaoSelecionada.dataLimite) }">
                    {{ getDiasRestantes(cotacaoSelecionada.dataLimite) }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Comparação de Cotações -->
            <div v-if="cotacoesDoItem.length > 1" class="detalhe-section">
              <h3 class="section-title">Comparação de Cotações para este Item</h3>
              <div class="comparacao-table">
                <table class="cotacoes-comparativas">
                  <thead>
                    <tr>
                      <th>Cotação</th>
                      <th>Fornecedor</th>
                      <th>Preço</th>
                      <th>Prazo de Entrega</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr
                      v-for="(cotacao, index) in cotacoesDoItem"
                      :key="cotacao.id"
                      :class="{ 'cotacao-atual': cotacao.id === cotacaoSelecionada?.id, 'melhor-preco': index === 0 }"
                    >
                      <td>
                        <span class="cotacao-id">#{{ String(cotacao.id).padStart(3, '0') }}</span>
                      </td>
                      <td>{{ getNomeFornecedor(cotacao.fornecedorId, cotacao.tipoFornecedor) }}</td>
                      <td>
                        <span class="preco-comparativo" :class="{ 'destaque': index === 0 }">
                          R$ {{ formatarPreco(cotacao.preco) }}
                        </span>
                      </td>
                      <td>{{ cotacao.prazoEmDiasUteis ? cotacao.prazoEmDiasUteis + ' dias úteis' : 'N/A' }}</td>
                      <td>
                        <span v-if="index === 0" class="badge-melhor">Melhor Preço</span>
                        <span v-if="cotacao.id === cotacaoSelecionada?.id" class="badge-atual">Atual</span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- Botão de Relatório -->
            <div class="acoes-detalhes">
              <button
                @click="gerarRelatorioComparativo(cotacaoSelecionada.itemPedidoId)"
                class="btn-acao primary"
                :disabled="gerandoRelatorio"
              >
                <svg viewBox="0 0 24 24" width="18" height="18">
                  <path fill="white" d="M19,3H5C3.89,3 3,3.89 3,5V19A2,2 0 0,0 5,21H19A2,2 0 0,0 21,19V5C21,3.89 20.1,3 19,3M9,17H7V10H9V17M13,17H11V7H13V17M17,17H15V13H17V17Z"/>
                </svg>
                <span v-if="gerandoRelatorio">Gerando Relatório...</span>
                <span v-else>Gerar Relatório Comparativo</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading Spinner - REMOVIDO -->
      <!--
      <div v-if="carregando" class="loading-overlay">
        <div class="loading-content">
          <div class="loading-spinner"></div>
          <p>Carregando cotações...</p>
        </div>
      </div>
      -->
    </main>

    <!-- Modal de Nova Cotação -->
    <CotacaoForm
      :isVisible="showCotacaoForm"
      :cotacao="cotacaoSelecionada"
      @close="fecharFormulario"
      @save="salvarCotacao"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, defineAsyncComponent } from 'vue'
import DashboardHeader from '../components/DashboardHeader.vue'
import DashboardSidebar from '../components/DashboardSidebar.vue'
// Lazy loading para componente pesado
const CotacaoForm = defineAsyncComponent(() => import('../components/CotacaoForm.vue'))
import { cotacaoService } from '../services/cotacaoService.js'
import fornecedorService from '../services/fornecedorService.js'
import itemPedidoService from '../services/itemPedidoService.js'

// Estado reativo
const gerandoRelatorio = ref(false)
const showCotacaoForm = ref(false)
const showDetalhesModal = ref(false)
const cotacaoSelecionada = ref(null)
const itemSelecionado = ref(null)
const cotacoes = ref([])
const fornecedores = ref([])
const termoBusca = ref('')
const visualizacao = ref('tabela')
const paginaAtual = ref(1)
const itensPorPagina = ref(10)
const carregandoCotacoes = ref(false)
const operacaoEmAndamento = ref(false)

// Filtros
const filtros = ref({
  status: '',
  periodo: ''
})

// Ordenação
const ordenacao = ref({
  campo: 'id',
  direcao: 'desc'
})

// Dados calculados

const percentualFinalizadas = computed(() => {
  const total = cotacoes.value.length
  if (total === 0) return 0

  const finalizadas = resumo.value.finalizadas

  return Math.round((finalizadas / total) * 100)
})

const fornecedoresUnicos = computed(() => {
  const fornecedoresMap = new Map()
  cotacoes.value.forEach(c => {
    if (c.fornecedorId) {
      const key = `${c.fornecedorId}-${c.tipoFornecedor || ''}`
      if (!fornecedoresMap.has(key)) {
        fornecedoresMap.set(key, {
          id: c.fornecedorId,
          tipo: c.tipoFornecedor,
          nome: getNomeFornecedor(c.fornecedorId, c.tipoFornecedor)
        })
      }
    }
  })
  return Array.from(fornecedoresMap.values()).sort((a, b) => a.nome.localeCompare(b.nome))
})

// Computadas
const resumo = computed(() => {
  const total = cotacoes.value.length
  const comDataLimite = cotacoes.value.filter(c => c.dataLimite).length
  const hoje = new Date()
  hoje.setHours(0, 0, 0, 0)
  const vencidas = cotacoes.value.filter(c => {
    if (!c.dataLimite) return false
    const limite = new Date(c.dataLimite)
    limite.setHours(0, 0, 0, 0)
    return limite < hoje
  }).length

  return {
    abertas: total,
    aguardando: comDataLimite,
    finalizadas: total - vencidas,
    vencidas
  }
})

const cotacoesDoItem = computed(() => {
  if (!cotacaoSelecionada.value) return []

  return cotacoes.value
    .filter(c => c.itemPedidoId === cotacaoSelecionada.value.itemPedidoId)
    .sort((a, b) => parseFloat(a.preco) - parseFloat(b.preco)) // Ordenar por preço
})

const cotacoesFiltradas = computed(() => {
  let resultado = [...cotacoes.value]

  // Aplicar busca textual
  if (termoBusca.value) {
    const termo = termoBusca.value.toLowerCase()
    resultado = resultado.filter(c =>
      c.id.toString().includes(termo) ||
      c.fornecedorId?.toString().includes(termo) ||
      c.itemPedidoId?.toString().includes(termo) ||
      c.preco?.toString().includes(termo)
    )
  }

  // Aplicar filtros
  if (filtros.value.fornecedor) {
    resultado = resultado.filter(c => c.fornecedorId?.toString() === filtros.value.fornecedor)
  }

  if (filtros.value.periodo) {
    const hoje = new Date()
    const filtroData = new Date()

    switch (filtros.value.periodo) {
      case 'semana':
        filtroData.setDate(hoje.getDate() - 7)
        break
      case 'mes':
        filtroData.setMonth(hoje.getMonth() - 1)
        break
      case 'trimestre':
        filtroData.setMonth(hoje.getMonth() - 3)
        break
    }

    resultado = resultado.filter(c => c.dataLimite && new Date(c.dataLimite) >= filtroData)
  }

  // Aplicar ordenação
  resultado.sort((a, b) => {
    let valorA = a[ordenacao.value.campo]
    let valorB = b[ordenacao.value.campo]

    if (ordenacao.value.campo === 'dataLimite') {
      valorA = new Date(valorA)
      valorB = new Date(valorB)
    }

    if (typeof valorA === 'string') {
      valorA = valorA.toLowerCase()
      valorB = valorB.toLowerCase()
    }

    if (ordenacao.value.direcao === 'asc') {
      return valorA < valorB ? -1 : valorA > valorB ? 1 : 0
    } else {
      return valorA > valorB ? -1 : valorA < valorB ? 1 : 0
    }
  })

  return resultado
})

const totalItens = computed(() => cotacoesFiltradas.value.length)
const totalPaginas = computed(() => Math.ceil(totalItens.value / itensPorPagina.value))

const cotacoesPaginadas = computed(() => {
  const inicio = (paginaAtual.value - 1) * itensPorPagina.value
  const fim = inicio + itensPorPagina.value
  return cotacoesFiltradas.value.slice(inicio, fim)
})

const paginasVisiveis = computed(() => {
  const total = totalPaginas.value
  const atual = paginaAtual.value
  const delta = 2
  const range = []

  for (let i = Math.max(1, atual - delta); i <= Math.min(total, atual + delta); i++) {
    range.push(i)
  }

  return range
})

// Métodos
const carregarFornecedores = async () => {
  try {
    console.log('🔄 Carregando fornecedores...')
    const [produtoResponse, servicoResponse] = await Promise.all([
      fornecedorService.listarFornecedoresProduto(),
      fornecedorService.listarFornecedoresServico()
    ])

    const fornecedoresProduto = (produtoResponse || []).map(f => ({ ...f, tipo: 'produto' }))
    const fornecedoresServico = (servicoResponse || []).map(f => ({ ...f, tipo: 'servico' }))

    fornecedores.value = [...fornecedoresProduto, ...fornecedoresServico]
    console.log('✅ Fornecedores carregados:', fornecedores.value.length)
  } catch (error) {
    console.error('❌ Erro ao carregar fornecedores:', error)
    fornecedores.value = []
  }
}

const carregarCotacoes = async () => {
  try {
    carregandoCotacoes.value = true
    console.log('🔄 Carregando cotações...')

    // Chamar o serviço real de cotações
    const response = await cotacaoService.listar()

    // Verificar estrutura da resposta
    if (response && Array.isArray(response)) {
      cotacoes.value = response
    } else if (response && response.data && Array.isArray(response.data)) {
      cotacoes.value = response.data
    } else {
      console.warn('⚠️ Resposta do backend não é um array válido:', response)
      cotacoes.value = []
    }

    console.log('✅ Cotações carregadas:', cotacoes.value.length)
  } catch (error) {
    console.error('❌ Erro ao carregar cotações:', error)
    cotacoes.value = []
  } finally {
    carregandoCotacoes.value = false
  }
}

const buscarCotacoes = () => {
  paginaAtual.value = 1
}

const aplicarFiltros = () => {
  paginaAtual.value = 1
}

const limparFiltros = () => {
  filtros.value = {
    status: '',
    periodo: ''
  }
  termoBusca.value = ''
  paginaAtual.value = 1
}

const ordenar = (campo) => {
  if (ordenacao.value.campo === campo) {
    ordenacao.value.direcao = ordenacao.value.direcao === 'asc' ? 'desc' : 'asc'
  } else {
    ordenacao.value.campo = campo
    ordenacao.value.direcao = 'asc'
  }
}

const irParaPagina = (pagina) => {
  if (pagina >= 1 && pagina <= totalPaginas.value) {
    paginaAtual.value = pagina
  }
}



const formatarData = (data) => {
  if (!data) return 'Não informado'
  const date = new Date(data)
  if (isNaN(date.getTime())) return 'Data inválida'
  return date.toLocaleDateString('pt-BR')
}

const formatarPreco = (preco) => {
  if (!preco && preco !== 0) return '0,00'
  return Number(preco).toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const getDiasRestantes = (dataLimite) => {
  if (!dataLimite) return 'Não informado'
  const hoje = new Date()
  const limite = new Date(dataLimite)

  if (isNaN(limite.getTime())) return 'Data inválida'

  const diferenca = Math.ceil((limite - hoje) / (1000 * 60 * 60 * 24))

  if (diferenca < 0) return 'Vencido'
  if (diferenca === 0) return 'Hoje'
  if (diferenca === 1) return '1 dia'
  return `${diferenca} dias`
}

const isDataLimiteVencida = (dataLimite) => {
  if (!dataLimite) return false
  const hoje = new Date()
  hoje.setHours(0, 0, 0, 0)
  const limite = new Date(dataLimite)
  limite.setHours(0, 0, 0, 0)
  if (isNaN(limite.getTime())) return false
  return limite < hoje
}

const podeEditar = () => {
  // Como não há campo status no backend, permite editar sempre
  // Pode adicionar lógica baseada em data ou outros critérios se necessário
  return true
}

const podeDeletar = () => {
  // Permite deletar sempre (pode ser refinado conforme regras de negócio)
  return true
}

// Ações
const abrirFormularioNova = () => {
  cotacaoSelecionada.value = null
  showCotacaoForm.value = true
}

const editarCotacao = (cotacao) => {
  cotacaoSelecionada.value = cotacao
  showCotacaoForm.value = true
}

const fecharFormulario = () => {
  showCotacaoForm.value = false
  cotacaoSelecionada.value = null
}

const fecharDetalhes = () => {
  showDetalhesModal.value = false
  cotacaoSelecionada.value = null
  itemSelecionado.value = null
}

const getNomeFornecedor = (fornecedorId, tipoFornecedor) => {
  if (!fornecedorId) return 'Não informado'

  // Buscar fornecedor considerando o tipo
  const fornecedor = fornecedores.value.find(f => {
    // Comparar ID e tipo
    const tipoMatch = tipoFornecedor ?
      (f.tipo === tipoFornecedor || f.tipo === tipoFornecedor.toLowerCase()) :
      true
    return f.id === fornecedorId && tipoMatch
  })

  if (fornecedor) {
    return fornecedor.razaoSocial || `Fornecedor #${fornecedorId}`
  }

  return `Fornecedor #${fornecedorId}`
}

const salvarCotacao = async (dadosCotacao) => {
  try {
    operacaoEmAndamento.value = true
    console.log('🔄 CotacoesView: Salvando cotação recebida do formulário:', dadosCotacao)

    let response
    if (cotacaoSelecionada.value && cotacaoSelecionada.value.id) {
      // Editar cotação existente
      response = await cotacaoService.atualizar(cotacaoSelecionada.value.id, dadosCotacao)
      console.log('✅ Cotação atualizada com sucesso')
    } else {
      // Criar nova cotação
      response = await cotacaoService.criar(dadosCotacao)
      console.log('✅ Cotação criada com sucesso')
    }

    // Recarregar a lista de cotações
    await carregarCotacoes()

    console.log('Cotação salva:', response)
  } catch (error) {
    console.error('❌ Erro ao salvar cotação:', error)
    // O erro já é tratado no formulário, então não precisamos fazer nada aqui
    throw error
  } finally {
    operacaoEmAndamento.value = false
  }
}

const visualizarCotacao = async (id) => {
  try {
    console.log('🔍 Visualizando cotação ID:', id)

    // Buscar a cotação na lista local primeiro
    const cotacao = cotacoes.value.find(c => c.id === id)

    if (cotacao) {
      cotacaoSelecionada.value = cotacao

      // Buscar detalhes do item do pedido
      try {
        itemSelecionado.value = await itemPedidoService.buscarPorId(cotacao.itemPedidoId)
      } catch (error) {
        console.warn('⚠️ Não foi possível carregar detalhes do item:', error)
        itemSelecionado.value = null
      }

      showDetalhesModal.value = true
    } else {
      // Se não encontrar na lista, buscar no backend
      const response = await cotacaoService.buscarPorId(id)
      cotacaoSelecionada.value = response

      // Buscar detalhes do item do pedido
      try {
        itemSelecionado.value = await itemPedidoService.buscarPorId(response.itemPedidoId)
      } catch (error) {
        console.warn('⚠️ Não foi possível carregar detalhes do item:', error)
        itemSelecionado.value = null
      }

      showDetalhesModal.value = true
    }
  } catch (error) {
    console.error('❌ Erro ao buscar detalhes da cotação:', error)
    alert('Erro ao carregar detalhes da cotação.')
  }
}

const deletarCotacao = async (id) => {
  // Evitar múltiplas operações simultâneas
  if (operacaoEmAndamento.value) {
    alert('Aguarde a operação anterior ser concluída.')
    return
  }

  // Confirmação mais detalhada
  const confirmacao = confirm(
    'Tem certeza que deseja excluir esta cotação?\n\n' +
    'Esta ação não pode ser desfeita e todos os dados da cotação serão perdidos permanentemente.'
  )

  if (confirmacao) {
    try {
      operacaoEmAndamento.value = true
      console.log('🔄 Excluindo cotação ID:', id)

      // Validar se o ID é válido
      if (!id || isNaN(id)) {
        throw new Error('ID da cotação inválido')
      }

      await cotacaoService.deletar(id)
      console.log('✅ Cotação excluída com sucesso')

      // Recarregar lista após exclusão
      await carregarCotacoes()

      alert('Cotação excluída com sucesso!')
    } catch (error) {
      console.error('❌ Erro ao excluir cotação:', error)

      // Mensagens de erro mais específicas
      let mensagemErro = 'Erro ao excluir cotação.'

      if (error.response) {
        // Erro do servidor
        switch (error.response.status) {
          case 404:
            mensagemErro = 'Cotação não encontrada. Pode já ter sido excluída.'
            break
          case 403:
            mensagemErro = 'Você não tem permissão para excluir esta cotação.'
            break
          case 409:
            mensagemErro = 'Não é possível excluir esta cotação pois ela possui dependências.'
            break
          default:
            mensagemErro = `Erro no servidor: ${error.response.status}. Tente novamente.`
        }
      } else if (error.message) {
        mensagemErro = error.message
      }

      alert(mensagemErro)
    } finally {
      operacaoEmAndamento.value = false
    }
  }
}

const exportarRelatorio = async () => {
  try {
    gerandoRelatorio.value = true
    console.log('🔄 Iniciando geração de relatório de cotações...')

    // Verificar se há cotações para gerar relatório
    if (cotacoes.value.length === 0) {
      alert('Não há cotações para gerar relatório.')
      return
    }

    // Gerar relatório geral (dashboard executivo)
    await cotacaoService.gerarRelatorioCotacoes()

    console.log('✅ Relatório de cotações gerado com sucesso!')

  } catch (error) {
    console.error('❌ Erro ao gerar relatório:', error)

    let mensagemErro = 'Erro ao gerar relatório de cotações.'
    if (error.response?.status === 404) {
      mensagemErro = 'Serviço de relatórios não encontrado. Verifique se o backend está funcionando.'
    } else if (error.response?.status === 500) {
      mensagemErro = 'Erro interno do servidor ao gerar relatório.'
    } else if (error.message) {
      mensagemErro = `Erro: ${error.message}`
    }

    alert(mensagemErro)
  } finally {
    gerandoRelatorio.value = false
  }
}

const gerarRelatorioComparativo = async (itemPedidoId) => {
  try {
    gerandoRelatorio.value = true
    console.log('🔄 Gerando relatório comparativo para item:', itemPedidoId)

    if (!itemPedidoId) {
      alert('ID do item não encontrado para gerar relatório comparativo.')
      return
    }

    // Gerar relatório comparativo de cotações por item
    await cotacaoService.gerarRelatorioComparativo(itemPedidoId)

    console.log('✅ Relatório comparativo gerado com sucesso!')

  } catch (error) {
    console.error('❌ Erro ao gerar relatório comparativo:', error)

    let mensagemErro = 'Erro ao gerar relatório comparativo.'
    if (error.response?.status === 404) {
      mensagemErro = 'Não foram encontradas cotações para este item ou o serviço não está disponível.'
    } else if (error.response?.status === 500) {
      mensagemErro = 'Erro interno do servidor ao gerar relatório.'
    } else if (error.message) {
      mensagemErro = `Erro: ${error.message}`
    }

    alert(mensagemErro)
  } finally {
    gerandoRelatorio.value = false
  }
}

// Lifecycle
onMounted(async () => {
  await Promise.all([
    carregarCotacoes(),
    carregarFornecedores()
  ])
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
  display: flex;
  align-items: center;
  gap: 8px;
  background: #3b82f6;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.action-button.secondary {
  background: #6b7280;
}

.action-button.secondary:hover {
  background: #4b5563;
}

.action-button:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.action-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.action-icon {
  flex-shrink: 0;
}

.loading-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.loading-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
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

.metric-growth.negative {
  color: #ef4444;
}

/* Seção de busca e controles */
.controls-section {
  display: flex;
  gap: 16px;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  margin-bottom: 32px;
  flex-wrap: wrap;
}

.search-container {
  flex: 1;
  position: relative;
  min-width: 300px;
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

.filter-controls {
  display: flex;
  gap: 12px;
  align-items: center;
}

.filter-select {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  min-width: 140px;
}

.clear-filters-btn {
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

.clear-filters-btn:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

/* Seção de dados */
.data-section {
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e5e7eb;
}

.section-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.view-controls {
  display: flex;
  gap: 8px;
  background: #f3f4f6;
  padding: 4px;
  border-radius: 8px;
}

.view-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: transparent;
  border: none;
  border-radius: 6px;
  color: #6b7280;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.view-button.active {
  background: white;
  color: #1F285F;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* Tabela */
.table-container {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  background: #f9fafb;
  padding: 16px;
  text-align: left;
  font-weight: 600;
  color: #374151;
  border-bottom: 1px solid #e5e7eb;
  font-size: 0.875rem;
}

.data-table th.sortable {
  cursor: pointer;
  user-select: none;
  transition: background-color 0.2s;
}

.data-table th.sortable:hover {
  background: #f3f4f6;
}

.sort-icon {
  margin-left: 8px;
  vertical-align: middle;
}

.data-table td {
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
}

.table-row:hover {
  background: #f9fafb;
}

.id-badge {
  background: #f3f4f6;
  color: #374151;
  padding: 4px 12px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 0.875rem;
}

.description-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.description-title {
  font-weight: 600;
  color: #111827;
}

.description-subtitle {
  font-size: 0.875rem;
  color: #6b7280;
}

.suppliers-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.suppliers-count {
  font-weight: 600;
  color: #374151;
  font-size: 0.875rem;
}

.suppliers-avatars {
  display: flex;
  align-items: center;
}

.supplier-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1F285F, #4338ca);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 600;
  margin-left: -8px;
  border: 2px solid white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.supplier-avatar:first-child {
  margin-left: 0;
}

.suppliers-more {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e5e7eb;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 600;
  margin-left: -8px;
  border: 2px solid white;
}

/* Status badges */
.status-badge {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.status-enviada { background: #dbeafe; color: #1d4ed8; }
.status-em-analise { background: #fef3c7; color: #d97706; }
.status-selecionada { background: #dcfce7; color: #166534; }
.status-aprovada { background: #dcfce7; color: #166534; }
.status-cancelada { background: #fee2e2; color: #dc2626; }
.status-vencida { background: #fee2e2; color: #dc2626; }
.status-finalizada { background: #dcfce7; color: #166534; }

.deadline-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.deadline-date {
  font-weight: 500;
  color: #374151;
}

.deadline-remaining {
  font-size: 0.75rem;
  color: #6b7280;
}

.deadline-expired .deadline-date,
.deadline-expired .deadline-remaining {
  color: #dc2626;
  font-weight: 600;
}

/* Novas células */
.fornecedor-cell {
  display: flex;
  align-items: center;
}

.fornecedor-id {
  background: #f3f4f6;
  color: #374151;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
}

.item-cell {
  display: flex;
  align-items: center;
}

.item-id {
  background: #e0e7ff;
  color: #3730a3;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
}

.price-cell {
  display: flex;
  align-items: center;
}

.price-value {
  font-weight: 600;
  color: #059669;
  font-size: 0.875rem;
}

.date-cell {
  display: flex;
  align-items: center;
}

.date-value {
  color: #374151;
  font-size: 0.875rem;
}

.actions-cell {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: none;
  border: 1px solid #e5e7eb;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  color: #6b7280;
}

.action-btn:hover {
  background: #f3f4f6;
  color: #374151;
}

.action-btn.edit:hover {
  background: #dbeafe;
  color: #1d4ed8;
  border-color: #3b82f6;
}

.action-btn.delete:hover {
  background: #fee2e2;
  color: #dc2626;
  border-color: #f87171;
}

/* Cards */
.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  padding: 20px;
}

.cotacao-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
}

.cotacao-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px -3px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-id {
  background: #f3f4f6;
  color: #374151;
  padding: 4px 12px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 0.875rem;
}

.card-price {
  background: #dcfce7;
  color: #166534;
  padding: 4px 12px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 0.875rem;
}

.card-status {
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.card-body {
  margin-bottom: 20px;
}

.card-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #111827;
  margin: 0 0 8px 0;
}

.card-subtitle {
  color: #6b7280;
  margin: 0 0 16px 0;
}

.card-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.meta-label {
  font-size: 0.875rem;
  color: #6b7280;
}

.meta-value {
  font-weight: 600;
  color: #374151;
}

.meta-value.expired {
  color: #dc2626;
}

.card-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.card-action-btn {
  flex: 1;
  padding: 12px 16px;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 80px;
}

.card-action-btn.primary {
  background: #3b82f6;
  color: white;
}

.card-action-btn.primary:hover {
  background: #2563eb;
}

.card-action-btn.secondary {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #e5e7eb;
}

.card-action-btn.secondary:hover {
  background: #e5e7eb;
}

.card-action-btn.warning {
  background: #fef3c7;
  color: #d97706;
  border: 1px solid #f59e0b;
}

.card-action-btn.warning:hover {
  background: #fed7aa;
}

.card-action-btn.danger {
  background: #fee2e2;
  color: #dc2626;
  border: 1px solid #ef4444;
}

.card-action-btn.danger:hover {
  background: #fecaca;
}

.card-action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.card-action-btn:disabled:hover {
  background: inherit;
}

/* Loading Spinner Pequeno */
.loading-spinner-small {
  display: inline-block;
  width: 12px;
  height: 12px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 4px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Estados da Tabela */
.loading-row, .empty-row {
  background: #f9fafb;
}

.loading-cell, .empty-cell {
  text-align: center;
  padding: 48px 24px;
  border: none;
}

.loading-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #6b7280;
  font-size: 14px;
}

.empty-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #6b7280;
}

.empty-content h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #374151;
}

.empty-content p {
  margin: 0;
  font-size: 14px;
  color: #9ca3af;
}

.empty-icon {
  opacity: 0.5;
}

/* Paginação */
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
}

.pagination-info {
  color: #6b7280;
  font-size: 0.875rem;
}

.pagination-controls {
  display: flex;
  gap: 8px;
}

.pagination-btn,
.pagination-number {
  background: white;
  border: 1px solid #d1d5db;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.875rem;
}

.pagination-btn:hover:not(:disabled),
.pagination-number:hover {
  background: #f3f4f6;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-number.active {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

/* Loading - REMOVIDO */
/*
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.loading-content {
  text-align: center;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e5e7eb;
  border-top: 4px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}
*/

/* Responsividade */
@media (max-width: 768px) {
  .welcome-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .controls-section {
    flex-direction: column;
    align-items: stretch;
  }

  .search-container {
    min-width: auto;
  }

  .filter-controls {
    flex-wrap: wrap;
  }

  .filter-select {
    min-width: 120px;
  }

  .section-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .cards-container {
    grid-template-columns: 1fr;
    padding: 16px;
  }

  .pagination {
    flex-direction: column;
    gap: 16px;
  }

  .table-container {
    overflow-x: auto;
  }
}

@media (max-width: 480px) {
  .data-table th,
  .data-table td {
    padding: 12px 8px;
  }

  .actions-cell {
    flex-direction: column;
    gap: 4px;
  }

  .card-actions {
    flex-direction: column;
  }

  .main-content {
    padding: 16px;
  }
}

/* Modal de Detalhes */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.detalhes-modal {
  background: white;
  border-radius: 12px;
  max-width: 900px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.detalhes-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
}

.header-title-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detalhes-header h2 {
  margin: 0;
  color: #1F285F;
  font-size: 1.5rem;
  font-weight: 600;
  font-family: Arial, sans-serif;
}

.close-button {
  background: none;
  border: none;
  font-size: 28px;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
  line-height: 1;
}

.close-button:hover {
  background: #f3f4f6;
  color: #374151;
}

.detalhes-body {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* Seções de Detalhes */
.detalhe-section {
  background: #f9fafb;
  border-radius: 8px;
  padding: 20px;
  border: 1px solid #e5e7eb;
}

.section-title {
  margin: 0 0 16px 0;
  color: #1F285F;
  font-size: 1.125rem;
  font-weight: 600;
  padding-bottom: 12px;
  border-bottom: 2px solid #e5e7eb;
  font-family: Arial, sans-serif;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 12px;
  background: white;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-family: Arial, sans-serif;
}

.info-value {
  font-size: 0.9375rem;
  font-weight: 600;
  color: #1f2937;
  font-family: Arial, sans-serif;
}

.info-value.highlight {
  font-size: 1.125rem;
  color: #1F285F;
}

.info-value.price-highlight {
  font-size: 1.25rem;
  color: #059669;
  font-weight: 700;
}

.info-value.expired {
  color: #dc2626;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 20px;
  color: #6b7280;
}

.anexo-link {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.anexo-link:hover {
  color: #1d4ed8;
  text-decoration: underline;
}

/* Estilos para PDF */
.pdf-info {
  margin-bottom: 12px;
}

.pdf-type {
  background: #dbeafe;
  color: #1e40af;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
}

.pdf-controls {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.pdf-btn {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.pdf-btn:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.pdf-btn.view-pdf {
  background: #059669;
}

.pdf-btn.view-pdf:hover {
  background: #047857;
}

.pdf-btn.open-new-tab {
  background: #7c3aed;
}

.pdf-btn.open-new-tab:hover {
  background: #6d28d9;
}

.pdf-btn.download-pdf {
  background: #dc2626;
}

.pdf-btn.download-pdf:hover {
  background: #b91c1c;
}

/* Estilos para botão de relatório */
.relatorio-btn {
  background: #0ea5e9;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 160px;
  justify-content: center;
}

.relatorio-btn:hover:not(:disabled) {
  background: #0284c7;
  transform: translateY(-1px);
}

.relatorio-btn:disabled {
  background: #94a3b8;
  cursor: not-allowed;
  transform: none;
}

.pdf-preview {
  margin-top: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
  background: white;
}

.pdf-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.pdf-header h5 {
  margin: 0;
  color: #374151;
  font-size: 1rem;
  font-weight: 600;
}

.close-pdf-btn {
  background: none;
  border: none;
  color: #6b7280;
  font-size: 18px;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.close-pdf-btn:hover {
  background: #e5e7eb;
  color: #374151;
}

.pdf-container {
  position: relative;
  height: 500px;
  background: #f3f4f6;
}

.pdf-iframe {
  width: 100%;
  height: 100%;
  border: none;
  background: white;
}

.pdf-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 16px;
  color: #6b7280;
}

.pdf-error p {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 500;
}

.no-attachment-container {
  text-align: center;
  padding: 16px;
  background: #f9fafb;
  border: 2px dashed #d1d5db;
  border-radius: 8px;
}

.no-attachment {
  color: #6b7280;
  font-size: 0.875rem;
  display: block;
  margin-bottom: 4px;
}



@media (max-width: 768px) {
  .detalhes-grid {
    grid-template-columns: 1fr;
  }

  .modal-overlay {
    padding: 10px;
  }

  .detalhes-header,
  .detalhes-body {
    padding: 16px;
  }

  .pdf-controls {
    flex-direction: column;
  }

  .pdf-btn {
    justify-content: center;
  }

  .pdf-container {
    height: 300px;
  }

  .detalhes-modal {
    max-height: 95vh;
  }
}

/* Tabela de Comparação */
.comparacao-table {
  overflow-x: auto;
}

.cotacoes-comparativas {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 6px;
  overflow: hidden;
}

.cotacoes-comparativas thead {
  background: #1F285F;
  color: white;
}

.cotacoes-comparativas th {
  padding: 12px 16px;
  text-align: left;
  font-weight: 600;
  font-size: 0.875rem;
  font-family: Arial, sans-serif;
}

.cotacoes-comparativas tbody tr {
  border-bottom: 1px solid #e5e7eb;
  transition: background-color 0.2s;
}

.cotacoes-comparativas tbody tr:hover {
  background: #f9fafb;
}

.cotacoes-comparativas tbody tr.cotacao-atual {
  background: #eff6ff;
}

.cotacoes-comparativas tbody tr.melhor-preco {
  background: #f0fdf4;
}

.cotacoes-comparativas td {
  padding: 12px 16px;
  font-size: 0.875rem;
  color: #374151;
  font-family: Arial, sans-serif;
}

.cotacao-id {
  background: #f3f4f6;
  color: #1F285F;
  padding: 4px 10px;
  border-radius: 4px;
  font-weight: 600;
  font-family: Arial, sans-serif;
  font-size: 0.8125rem;
}

.preco-comparativo {
  font-weight: 600;
  color: #374151;
}

.preco-comparativo.destaque {
  color: #059669;
  font-weight: 700;
  font-size: 1rem;
}

.badge-melhor {
  background: #10b981;
  color: white;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  display: inline-block;
  margin-right: 6px;
}

.badge-atual {
  background: #1F285F;
  color: white;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  display: inline-block;
}

/* Ações do Modal */
.acoes-detalhes {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.btn-acao {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 0.9375rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  font-family: Arial, sans-serif;
}

.btn-acao.primary {
  background: #1F285F;
  color: white;
}

.btn-acao.primary:hover:not(:disabled) {
  background: #151f4a;
  transform: translateY(-1px);
}

.btn-acao:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}
</style>
