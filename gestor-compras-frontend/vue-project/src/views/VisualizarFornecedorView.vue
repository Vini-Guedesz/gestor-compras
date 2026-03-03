<template>
  <div class="page-container">
    <DashboardHeader @toggle-sidebar="toggleSidebar" />

    <div class="main-content">
      <DashboardSidebar :isOpen="isSidebarOpen" @close="closeSidebar" />

      <div class="content-area">
        <!-- Breadcrumb -->
        <div class="breadcrumb">
          <button @click="voltar" class="btn-voltar">
            <svg viewBox="0 0 24 24" width="18" height="18">
              <path fill="currentColor" d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"/>
            </svg>
            Voltar
          </button>
          <span class="breadcrumb-separator">/</span>
          <router-link to="/dashboard" class="breadcrumb-home" aria-label="Início">
            <svg viewBox="0 0 24 24" width="16" height="16">
              <path fill="currentColor" d="M12 3l9 8h-3v9h-5v-6H11v6H6v-9H3l9-8z"/>
            </svg>
          </router-link>
          <span class="breadcrumb-separator">/</span>
          <router-link to="/fornecedores" class="breadcrumb-link">
            Fornecedores
          </router-link>
          <span class="breadcrumb-separator">/</span>
          <span class="breadcrumb-current">Visualizar Fornecedor</span>
        </div>

        <!-- Loading State -->
        <div v-if="isLoading" class="loading-container">
          <div class="loading-spinner"></div>
          <span>Carregando fornecedor...</span>
        </div>

        <!-- Conteúdo -->
        <div v-else-if="fornecedor" class="view-container">
          <!-- Header -->
          <div class="view-header">
            <div class="header-content">
              <h2 class="view-title">{{ fornecedor.razaoSocial }}</h2>
              <p class="view-subtitle">Informações completas do fornecedor</p>
              <!-- Resumo Rápido -->
              <div class="header-resumo">
                <div class="resumo-item">
                  <svg viewBox="0 0 24 24" width="16" height="16" class="resumo-icon">
                    <path fill="currentColor" d="M20,8H4V6H20M20,18H4V12H20M20,4H4C2.89,4 2,4.89 2,6V18A2,2 0 0,0 4,20H20A2,2 0 0,0 22,18V6C22,5.11 21.1,4 20,4Z"/>
                  </svg>
                  <span>{{ getTipoLabel(fornecedor.tipo) }}</span>
                </div>
                <span class="resumo-separator">•</span>
                <div class="resumo-item">
                  <svg viewBox="0 0 24 24" width="16" height="16" class="resumo-icon">
                    <path fill="currentColor" d="M9 11H7v2h2v-2zm4 0h-2v2h2v-2zm4 0h-2v2h2v-2zm2-7h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 1.99 2H19c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V9h14v11z"/>
                  </svg>
                  <span>{{ historicoCompras.total }} {{ historicoCompras.total === 1 ? 'cotação' : 'cotações' }}</span>
                </div>
              </div>
            </div>
            <div class="header-actions">
              <span class="status-badge" :class="getStatusClass(fornecedor.status || 'ativo')">
                {{ getStatusLabel(fornecedor.status || 'ativo') }}
              </span>
            </div>
          </div>

          <!-- Abas -->
          <div class="tabs-container">
            <button
              class="tab-button"
              :class="{ active: abaAtiva === 'informacoes' }"
              @click="abaAtiva = 'informacoes'"
            >
              <Icon name="info" type="tab" :size="18" />
              Informações Gerais
            </button>
            <button
              class="tab-button"
              :class="{ active: abaAtiva === 'historico' }"
              @click="abaAtiva = 'historico'"
            >
              <Icon name="history" type="tab" :size="18" />
              Histórico de Cotações
              <span class="tab-badge" v-if="historicoCompras.total > 0">{{ historicoCompras.total }}</span>
            </button>
          </div>

          <!-- Conteúdo da Aba: Informações Gerais -->
          <div v-show="abaAtiva === 'informacoes'">
          <!-- Seção: Dados Fiscais -->
          <div class="section-card">
            <h3 class="section-title">Dados Fiscais</h3>
            <div class="info-grid-enhanced">
              <div class="info-card">
                <div class="info-card-icon" style="background: #dbeafe;">
                  <svg viewBox="0 0 20 20" fill="#2563eb" width="20" height="20">
                    <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4z" clip-rule="evenodd"/>
                  </svg>
                </div>
                <div class="info-card-content">
                  <span class="info-card-label">CNPJ</span>
                  <span class="info-card-value">{{ formatarCNPJ(fornecedor.cnpj) }}</span>
                </div>
              </div>
              <div class="info-card" v-if="fornecedor.inscricaoEstadual">
                <div class="info-card-icon" style="background: #fef3c7;">
                  <svg viewBox="0 0 20 20" fill="#d97706" width="20" height="20">
                    <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z"/>
                    <path fill-rule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clip-rule="evenodd"/>
                  </svg>
                </div>
                <div class="info-card-content">
                  <span class="info-card-label">Inscrição Estadual</span>
                  <span class="info-card-value">{{ fornecedor.inscricaoEstadual }}</span>
                </div>
              </div>
              <div class="info-card" v-if="fornecedor.inscricaoMunicipal">
                <div class="info-card-icon" style="background: #e0e7ff;">
                  <svg viewBox="0 0 20 20" fill="#6366f1" width="20" height="20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                  </svg>
                </div>
                <div class="info-card-content">
                  <span class="info-card-label">Inscrição Municipal</span>
                  <span class="info-card-value">{{ fornecedor.inscricaoMunicipal }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Seção: Contato -->
          <div class="section-card">
            <h3 class="section-title">Informações de Contato</h3>
            <div v-if="gruposContato.length > 0" class="contato-grupos">
              <div v-for="grupo in gruposContato" :key="grupo.key" class="contato-grupo">
                <h4 class="contato-grupo-titulo">{{ grupo.titulo }}</h4>
                <div class="info-grid-enhanced contato-grid">
                  <button
                    v-for="contatoItem in grupo.itens"
                    :key="contatoItem.key"
                    type="button"
                    class="info-card info-card-copy"
                    :class="{ 'full-width': contatoItem.tipo === 'EMAIL' }"
                    :title="`Clique para copiar: ${contatoItem.valorExibicao}`"
                    :aria-label="`Copiar ${contatoItem.tipoLabel}: ${contatoItem.valorExibicao}`"
                    @click="copiarContato(contatoItem)"
                  >
                    <div class="info-card-icon" :style="{ background: contatoItem.iconeFundo }">
                      <svg v-if="contatoItem.tipo === 'EMAIL'" viewBox="0 0 20 20" :fill="contatoItem.iconeCor" width="20" height="20">
                        <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/>
                        <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/>
                      </svg>
                      <svg v-else-if="contatoItem.tipo === 'OUTRO'" viewBox="0 0 24 24" :fill="contatoItem.iconeCor" width="20" height="20">
                        <path d="M12 2a10 10 0 100 20 10 10 0 000-20zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
                      </svg>
                      <svg v-else viewBox="0 0 20 20" :fill="contatoItem.iconeCor" width="20" height="20">
                        <path d="M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V17a1 1 0 01-1 1h-2C7.82 18 2 12.18 2 5V3z"/>
                      </svg>
                    </div>
                    <div class="info-card-content">
                      <span class="info-card-label">{{ contatoItem.tipoLabel }}</span>
                      <span v-if="contatoItem.nomeContato" class="info-card-sub-label">{{ contatoItem.nomeContato }}</span>
                      <span class="info-card-value" :title="contatoItem.valor">
                        {{ contatoItem.valorExibicao }}
                      </span>
                    </div>
                  </button>
                </div>
              </div>
            </div>
            <div v-else class="info-grid-enhanced">
              <div class="info-card full-width">
                <div class="info-card-icon" style="background: #f3f4f6;">
                  <svg viewBox="0 0 24 24" fill="#64748b" width="20" height="20">
                    <path d="M12 2a10 10 0 100 20 10 10 0 000-20zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
                  </svg>
                </div>
                <div class="info-card-content">
                  <span class="info-card-label">Contato</span>
                  <span class="info-card-value">Não informado</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Seção: Endereço -->
          <div class="section-card" v-if="fornecedor.endereco">
            <h3 class="section-title">Endereço</h3>
            <div class="info-grid-enhanced">
              <div class="info-card full-width">
                <div class="info-card-icon" style="background: #f3e8ff;">
                  <svg viewBox="0 0 20 20" fill="#9333ea" width="20" height="20">
                    <path fill-rule="evenodd" d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z" clip-rule="evenodd"/>
                  </svg>
                </div>
                <div class="info-card-content">
                  <span class="info-card-label">Logradouro</span>
                  <span class="info-card-value endereco-completo">{{ getEnderecoLogradouro(fornecedor.endereco) }}</span>
                </div>
              </div>

              <div class="info-card">
                <div class="info-card-icon" style="background: #ede9fe;">
                  <svg viewBox="0 0 20 20" fill="#7c3aed" width="20" height="20">
                    <path d="M10 2a4 4 0 00-4 4v2H5a2 2 0 00-2 2v6a2 2 0 002 2h10a2 2 0 002-2v-6a2 2 0 00-2-2h-1V6a4 4 0 00-4-4zm-2 6V6a2 2 0 114 0v2H8z"/>
                  </svg>
                </div>
                <div class="info-card-content">
                  <span class="info-card-label">Bairro</span>
                  <span class="info-card-value">{{ fornecedor.endereco.bairro || 'Não informado' }}</span>
                </div>
              </div>

              <div class="info-card">
                <div class="info-card-icon" style="background: #dbeafe;">
                  <svg viewBox="0 0 20 20" fill="#2563eb" width="20" height="20">
                    <path d="M4 3h12a1 1 0 011 1v12a1 1 0 01-1 1H4a1 1 0 01-1-1V4a1 1 0 011-1zm1 3v8h10V6H5zm2 2h6v2H7V8z"/>
                  </svg>
                </div>
                <div class="info-card-content">
                  <span class="info-card-label">Cidade / UF</span>
                  <span class="info-card-value">{{ getEnderecoCidadeUf(fornecedor.endereco) }}</span>
                </div>
              </div>

              <div class="info-card">
                <div class="info-card-icon" style="background: #ccfbf1;">
                  <svg viewBox="0 0 20 20" fill="#0f766e" width="20" height="20">
                    <path d="M4 3h12a1 1 0 011 1v12a1 1 0 01-1 1H4a1 1 0 01-1-1V4a1 1 0 011-1zm1 4h10V5H5v2zm0 2v6h10V9H5z"/>
                  </svg>
                </div>
                <div class="info-card-content">
                  <span class="info-card-label">CEP</span>
                  <span class="info-card-value">{{ formatarCEP(fornecedor.endereco.cep) || 'Não informado' }}</span>
                </div>
              </div>

              <div class="info-card full-width" v-if="fornecedor.endereco.complemento">
                <div class="info-card-icon" style="background: #f1f5f9;">
                  <svg viewBox="0 0 24 24" fill="#475569" width="20" height="20">
                    <path d="M4 4h16v16H4V4zm2 2v12h12V6H6zm2 2h8v2H8V8zm0 4h6v2H8v-2z"/>
                  </svg>
                </div>
                <div class="info-card-content">
                  <span class="info-card-label">Complemento</span>
                  <span class="info-card-value">{{ fornecedor.endereco.complemento }}</span>
                </div>
              </div>
            </div>
          </div>
          </div><!-- Fim da aba de informações -->

          <!-- Conteúdo da Aba: Histórico de Cotações -->
          <div v-show="abaAtiva === 'historico'">
          <div class="section-card">
            <div class="historico-header">
              <div class="historico-title">
                <h3 class="section-title">Histórico de Cotações</h3>
                <p class="section-subtitle">Veja todas as cotações feitas com este fornecedor</p>
              </div>
              <div class="historico-actions" v-if="temFiltrosAtivos">
                <button @click="limparFiltros" class="btn-clear">
                  Limpar filtros
                </button>
              </div>
            </div>

            <!-- Stats Cards -->
            <div class="historico-stats">
              <div class="stat-card">
                <div class="stat-icon" style="background: #dbeafe;">
                  <svg viewBox="0 0 24 24" width="24" height="24" fill="#2563eb">
                    <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
                  </svg>
                </div>
                <div class="stat-content">
                  <span class="stat-value">{{ historicoCompras.total }}</span>
                  <span class="stat-label">Total de Cotações</span>
                </div>
              </div>

              <div class="stat-card">
                <div class="stat-icon" style="background: #fef3c7;">
                  <svg viewBox="0 0 24 24" width="24" height="24" fill="#d97706">
                    <path d="M9 11H7v2h2v-2zm4 0h-2v2h2v-2zm4 0h-2v2h2v-2zm2-7h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 1.99 2H19c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V9h14v11z"/>
                  </svg>
                </div>
                <div class="stat-content">
                  <span class="stat-value">{{ historicoCompras.ultimoPedido }}</span>
                  <span class="stat-label">Última Cotação</span>
                </div>
              </div>

              <div class="stat-card">
                <div class="stat-icon" style="background: #dcfce7;">
                  <svg viewBox="0 0 24 24" width="24" height="24" fill="#059669">
                    <path d="M12 4a8 8 0 100 16 8 8 0 000-16zm1 4v4.25l3 1.75-.75 1.25L11 13V8h2z"/>
                  </svg>
                </div>
                <div class="stat-content">
                  <span class="stat-value">{{ historicoCompras.total ? Math.ceil(historicoCompras.total / Math.max(totalPages, 1)) : 0 }}</span>
                  <span class="stat-label">Média por Página</span>
                </div>
              </div>
            </div>

            <!-- Filtros -->
            <div class="filters-bar">
              <div class="filter-group search-group">
                <label for="filtroId" class="filter-label">Buscar por ID</label>
                <div class="search-input-wrapper">
                  <Icon name="search" :size="16" class="search-icon" />
                  <input
                    id="filtroId"
                    v-model="filtroId"
                    type="text"
                    placeholder="Ex: 123"
                    class="filter-input"
                  />
                </div>
              </div>

              <div class="filter-group date-group">
                <label class="filter-label">Período</label>
                <div class="date-inputs">
                  <div class="date-input-wrapper">
                    <span class="date-label-small">De</span>
                    <input
                      v-model="filtroDataInicio"
                      type="date"
                      class="filter-input date-input"
                    />
                  </div>
                  <div class="date-input-wrapper">
                    <span class="date-label-small">Até</span>
                    <input
                      v-model="filtroDataFim"
                      type="date"
                      class="filter-input date-input"
                    />
                  </div>
                </div>
              </div>

            </div>

            <!-- Loading -->
            <div v-if="carregandoHistorico" class="loading-message">
              <div class="loading-spinner-small"></div>
              <p>Carregando histórico...</p>
            </div>

            <!-- Empty State -->
            <div v-else-if="cotacoesFiltradas.length === 0" class="empty-state">
              <svg viewBox="0 0 24 24" width="48" height="48" fill="#9ca3af">
                <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
              </svg>
              <p v-if="historicoCompras.cotacoes.length > 0">Nenhuma cotação encontrada com os filtros selecionados.</p>
              <p v-else>Nenhuma cotação encontrada para este fornecedor.</p>
            </div>

            <!-- Lista de Cotações -->
            <div v-else class="historico-lista">
              <div v-for="cotacao in paginatedCotacoes" :key="cotacao.id" class="cotacao-card">
                <!-- Header -->
                <div class="cotacao-header-padrao">
                  <div class="cotacao-info-principal">
                    <div class="fornecedor-linha">
                      <strong class="fornecedor-nome">Cotação {{ cotacao.numero }}</strong>
                      <span class="tipo-tag" v-if="cotacao.pedidoId">Pedido #{{ cotacao.pedidoId }}</span>
                      <span class="tipo-tag tipo-tag-data">{{ cotacao.data }}</span>
                      <!-- Tag de Status -->
                      <span class="status-badge-small" :class="getStatusCotacaoClass(cotacao.statusExibicao || cotacao.status)">
                        {{ getStatusCotacaoLabel(cotacao.statusExibicao || cotacao.status) }}
                      </span>
                    </div>
                  </div>
                  <div class="preco-destaque-box">
                    <span class="preco-label-small">VALOR TOTAL</span>
                    <span class="preco-valor-grande">{{ formatarMoeda(cotacao.valor) }}</span>
                  </div>
                </div>

                <div class="cotacao-body">
                  <!-- Grid de informações -->
                  <div class="cotacao-info-grid">
                    <div v-if="cotacao.prazoEntrega && cotacao.prazoEntrega !== '-'" class="info-box">
                      <svg class="info-box-icon" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clip-rule="evenodd"/>
                      </svg>
                      <div class="info-box-content">
                        <span class="info-box-label">Validade</span>
                        <span class="info-box-value">{{ cotacao.prazoEntrega }}</span>
                      </div>
                    </div>
                    <div v-if="cotacao.pedidoId" class="info-box">
                      <svg class="info-box-icon" viewBox="0 0 20 20" fill="currentColor">
                        <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z"/>
                        <path fill-rule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clip-rule="evenodd"/>
                      </svg>
                      <div class="info-box-content">
                        <span class="info-box-label">Pedido Relacionado</span>
                        <span class="info-box-value">Pedido #{{ cotacao.pedidoId }}</span>
                      </div>
                    </div>
                  </div>

                  <!-- Itens -->
                  <div class="cotacao-itens-section">
                    <div v-if="(cotacao.itensStatusDetalhados || []).length > 0" class="itens-detalhes-actions">
                      <button
                        type="button"
                        class="btn-itens-detalhes"
                        @click="toggleDetalhesItens(cotacao.id)"
                      >
                        <svg viewBox="0 0 20 20" width="14" height="14" fill="currentColor" class="btn-itens-icon">
                          <path
                            v-if="isDetalhesItensAberto(cotacao.id)"
                            fill-rule="evenodd"
                            d="M5.23 12.21a.75.75 0 001.06 0L10 8.5l3.71 3.71a.75.75 0 001.06-1.06l-4.24-4.24a.75.75 0 00-1.06 0L5.23 11.15a.75.75 0 000 1.06z"
                            clip-rule="evenodd"
                          />
                          <path
                            v-else
                            fill-rule="evenodd"
                            d="M14.77 7.79a.75.75 0 00-1.06 0L10 11.5 6.29 7.79a.75.75 0 00-1.06 1.06l4.24 4.24a.75.75 0 001.06 0l4.24-4.24a.75.75 0 000-1.06z"
                            clip-rule="evenodd"
                          />
                        </svg>
                        <span>
                          {{ `Status por Item (${cotacao.itensStatusDetalhados?.length || 0})` }}
                          {{ isDetalhesItensAberto(cotacao.id) ? ' - Ocultar detalhes' : ' - Ver detalhes' }}
                        </span>
                      </button>
                    </div>
                    <div
                      v-if="(cotacao.itensStatusDetalhados || []).length > 0 && isDetalhesItensAberto(cotacao.id)"
                      class="itens-status-lista"
                    >
                      <article
                        v-for="itemStatus in (cotacao.itensStatusDetalhados || [])"
                        :key="`${cotacao.id}-${itemStatus.chave || itemStatus.nome}-${itemStatus.status}`"
                        class="item-status-card"
                        :class="getStatusItemCardClass(itemStatus.status)"
                      >
                        <div class="item-status-header">
                          <div class="item-status-identificacao">
                            <span class="item-status-nome">{{ itemStatus.nome }}</span>
                            <span v-if="itemStatus.itemPedidoId" class="item-status-codigo">Item #{{ itemStatus.itemPedidoId }}</span>
                          </div>
                          <span
                            class="item-chip item-chip-status"
                            :class="getStatusItemClass(itemStatus.status)"
                          >
                            {{ getStatusItemLabel(itemStatus.status) }}
                          </span>
                        </div>

                        <div class="item-status-metricas">
                          <div class="item-status-metrica">
                            <span class="metrica-label">Qtd</span>
                            <span class="metrica-valor">{{ formatarQuantidade(itemStatus.quantidade) }}</span>
                          </div>
                          <div class="item-status-metrica">
                            <span class="metrica-label">Preco Unit.</span>
                            <span class="metrica-valor">{{ formatarMoeda(itemStatus.precoUnitario) }}</span>
                          </div>
                          <div class="item-status-metrica">
                            <span class="metrica-label">Subtotal</span>
                            <span class="metrica-valor">{{ formatarMoeda(itemStatus.total) }}</span>
                          </div>
                        </div>

                        <p v-if="itemStatus.descricao" class="item-status-detalhe">
                          <strong>Descricao:</strong> {{ itemStatus.descricao }}
                        </p>
                        <p v-if="itemStatus.observacao" class="item-status-detalhe">
                          <strong>Observacao:</strong> {{ itemStatus.observacao }}
                        </p>
                      </article>
                    </div>
                    <p
                      v-else-if="(cotacao.itensStatusDetalhados || []).length > 0"
                      class="itens-sem-detalhe"
                    >
                      Detalhes retraidos. Clique em "Ver detalhes de itens" para visualizar.
                    </p>
                    <p v-else class="itens-sem-detalhe">Nenhum item encontrado para esta cotacao.</p>
                    <div class="itens-resumo-status" v-if="cotacao.resumoItensStatus">
                      <span class="resumo-status-badge resumo-aprovado">
                        Aprovados: {{ cotacao.resumoItensStatus.aprovados }}
                      </span>
                      <span class="resumo-status-badge resumo-cotado-nao-selecionado">
                        Cotados nao selecionados: {{ cotacao.resumoItensStatus.cotadosNaoSelecionados }}
                      </span>
                      <span class="resumo-status-badge resumo-nao-cotado">
                        Nao cotados: {{ cotacao.resumoItensStatus.naoCotados }}
                      </span>
                    </div>
                  </div>

                  <!-- Observações -->
                  <div v-if="cotacao.itemObservacao || cotacao.pedidoObservacao" class="observacoes-section">
                    <h5 class="itens-section-title">Observações</h5>
                    <div class="observacao-content">
                      <p v-if="cotacao.itemObservacao"><strong>Item:</strong> {{ cotacao.itemObservacao }}</p>
                      <p v-if="cotacao.pedidoObservacao"><strong>Pedido:</strong> {{ cotacao.pedidoObservacao }}</p>
                    </div>
                  </div>
                </div>

                <!-- Ações: PDFs -->
                <div class="cotacao-actions" v-if="cotacao.temAnexoPdf || cotacao.quantidadeAnexos > 0">
                  <div class="pdf-buttons">
                    <button
                      v-for="indexPdf in cotacao.quantidadeAnexos || 1"
                      :key="`pdf-${cotacao.id}-${indexPdf - 1}`"
                      @click="togglePdfViewer(cotacao, indexPdf - 1)"
                      class="btn-pdf-primary"
                      :class="{ 'btn-pdf-primary-active': pdfAberto === `${cotacao.id}-${indexPdf - 1}` }"
                    >
                      <svg class="btn-icon" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4zm2 6a1 1 0 011-1h6a1 1 0 110 2H7a1 1 0 01-1-1zm1 3a1 1 0 100 2h6a1 1 0 100-2H7z" clip-rule="evenodd"/>
                      </svg>
                      {{ pdfAberto === `${cotacao.id}-${indexPdf - 1}` ? 'Fechar' : 'Ver' }} PDF {{ cotacao.quantidadeAnexos > 1 ? indexPdf : '' }}
                    </button>
                  </div>
                </div>

                <!-- Viewer de PDF -->
                <div v-if="pdfAberto && pdfAberto.startsWith(`${cotacao.id}-`)" class="pdf-viewer-container">
                  <div v-if="carregandoPdf" class="pdf-loading">
                    <div class="loading-spinner-small"></div>
                    <span>Carregando PDF...</span>
                  </div>
                  <div v-else-if="pdfUrl" class="pdf-viewer-wrapper">
                    <iframe :src="pdfUrl" width="100%" height="100%" frameborder="0"></iframe>
                  </div>
                </div>
              </div>

              <!-- Paginação -->
              <div v-if="totalPages > 1" class="pagination-container">
                <button
                  @click="prevPage"
                  class="pagination-btn"
                  :disabled="currentPage === 1"
                >
                  <Icon name="chevron-left" :size="20" />
                  Anterior
                </button>
                <span class="pagination-info">
                  Página <strong>{{ currentPage }}</strong> de <strong>{{ totalPages }}</strong>
                </span>
                <button
                  @click="nextPage"
                  class="pagination-btn"
                  :disabled="currentPage === totalPages"
                >
                  Próxima
                  <Icon name="chevron-right" :size="20" />
                </button>
              </div>
            </div>
          </div>
          </div><!-- Fim da aba de histórico -->
        </div>

        <!-- Erro -->
        <div v-else class="error-container">
          <svg viewBox="0 0 24 24" width="64" height="64" fill="#ef4444">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
          </svg>
          <h3>Fornecedor não encontrado</h3>
          <p>O fornecedor solicitado não existe ou foi removido.</p>
          <button @click="voltar" class="btn-primary">Voltar para Fornecedores</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import Icon from '@/components/ui/Icon.vue'
import { useToast } from '@/composables/useToast.js'
import { useClipboard } from '@/composables/useClipboard.js'
import fornecedorService from '@/services/fornecedorService.js'
import cotacaoService from '@/services/cotacaoService.js'
import itemPedidoService from '@/services/itemPedidoService.js'
import pedidoService from '@/services/pedidoService.js'
import logger from '@/utils/logger.js'

const route = useRoute()
const router = useRouter()
const { success, error: showError } = useToast()
const { copyText } = useClipboard()

// Estados
const isSidebarOpen = ref(false)
const isLoading = ref(true)
const fornecedor = ref(null)
const carregandoHistorico = ref(false)
const abaAtiva = ref('informacoes') // 'informacoes' ou 'historico'

// Filtros
const filtroId = ref('')
const filtroDataInicio = ref('')
const filtroDataFim = ref('')

// Paginação
const currentPage = ref(1)
const itemsPerPage = 5

// PDF
const pdfAberto = ref(null)
const carregandoPdf = ref(false)
const pdfUrl = ref(null)

// Histórico
const historicoCompras = ref({
  total: 0,
  valor: 0,
  ultimoPedido: '-',
  cotacoes: []
})
const detalhesItensAbertos = ref(new Set())

// Computeds
const temFiltrosAtivos = computed(() => {
  return filtroId.value !== '' || filtroDataInicio.value !== '' || filtroDataFim.value !== ''
})

const cotacoesFiltradas = computed(() => {
  let resultado = historicoCompras.value.cotacoes

  // Filtro por ID
  if (filtroId.value) {
    const termo = filtroId.value.toLowerCase()
    resultado = resultado.filter(cot =>
      cot.id.toString().includes(termo) ||
      (cot.pedidoId && cot.pedidoId.toString().includes(termo))
    )
  }

  // Filtro por Data Inicio
  if (filtroDataInicio.value) {
    const dataInicio = new Date(filtroDataInicio.value)
    dataInicio.setHours(0, 0, 0, 0)
    resultado = resultado.filter(cot => {
      if (!cot.dataRaw) return false
      const dataCot = new Date(cot.dataRaw)
      dataCot.setHours(0, 0, 0, 0)
      return dataCot >= dataInicio
    })
  }

  // Filtro por Data Fim
  if (filtroDataFim.value) {
    const dataFim = new Date(filtroDataFim.value)
    dataFim.setHours(23, 59, 59, 999)
    resultado = resultado.filter(cot => {
      if (!cot.dataRaw) return false
      const dataCot = new Date(cot.dataRaw)
      dataCot.setHours(0, 0, 0, 0)
      return dataCot <= dataFim
    })
  }

  return resultado
})

const totalPages = computed(() => {
  return Math.ceil(cotacoesFiltradas.value.length / itemsPerPage)
})

const paginatedCotacoes = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return cotacoesFiltradas.value.slice(start, end)
})

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    // Scroll to top of list
    const listElement = document.querySelector('.historico-lista')
    if (listElement) {
      listElement.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
  }
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    // Scroll to top of list
    const listElement = document.querySelector('.historico-lista')
    if (listElement) {
      listElement.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
  }
}

// Watchers
import { watch } from 'vue'
watch(cotacoesFiltradas, () => {
  currentPage.value = 1
})

// Caches
const cacheItensPedido = ref(new Map())
const cachePedidos = ref(new Map())
const cacheHistoricoCotacao = ref(new Map())

// Sidebar
const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value
}

const closeSidebar = () => {
  isSidebarOpen.value = false
}

// Navegação
const voltar = () => {
  router.push('/fornecedores')
}

// Ações de Filtro
const limparFiltros = () => {
  filtroId.value = ''
  filtroDataInicio.value = ''
  filtroDataFim.value = ''
}

const isDetalhesItensAberto = (cotacaoId) => detalhesItensAbertos.value.has(cotacaoId)

const toggleDetalhesItens = (cotacaoId) => {
  if (cotacaoId === null || cotacaoId === undefined) return

  const proximosAbertos = new Set(detalhesItensAbertos.value)
  if (proximosAbertos.has(cotacaoId)) {
    proximosAbertos.delete(cotacaoId)
  } else {
    proximosAbertos.add(cotacaoId)
  }

  detalhesItensAbertos.value = proximosAbertos
}

// Formatação
const formatarCNPJ = (cnpj) => {
  if (!cnpj) return 'Não informado'
  const numbers = cnpj.replace(/\D/g, '')
  return numbers.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, '$1.$2.$3/$4-$5')
}

const formatarTelefone = (telefone) => {
  if (!telefone) return ''
  const numbers = telefone.replace(/\D/g, '')
  if (numbers.length === 11) {
    return numbers.replace(/^(\d{2})(\d{5})(\d{4})$/, '($1) $2-$3')
  } else if (numbers.length === 10) {
    return numbers.replace(/^(\d{2})(\d{4})(\d{4})$/, '($1) $2-$3')
  }
  return telefone
}

const normalizarTipoContato = (tipoContato) => {
  const tipo = String(tipoContato || '').toUpperCase().trim()
  const tiposValidos = ['TELEFONE_FIXO', 'CELULAR', 'EMAIL', 'OUTRO']
  return tiposValidos.includes(tipo) ? tipo : 'OUTRO'
}

const getLabelTipoContato = (tipoContato) => {
  const labels = {
    TELEFONE_FIXO: 'Telefone Fixo',
    CELULAR: 'Celular',
    EMAIL: 'E-mail',
    OUTRO: 'Outro'
  }
  return labels[normalizarTipoContato(tipoContato)] || 'Contato'
}

const formatarValorContato = (tipoContato, valor) => {
  if (!valor) return ''
  const tipoNormalizado = normalizarTipoContato(tipoContato)

  if (tipoNormalizado === 'TELEFONE_FIXO' || tipoNormalizado === 'CELULAR') {
    return formatarTelefone(valor)
  }

  return valor
}

const getEstiloContato = (tipoContato) => {
  const tipoNormalizado = normalizarTipoContato(tipoContato)

  switch (tipoNormalizado) {
    case 'EMAIL':
      return { iconeFundo: '#dcfce7', iconeCor: '#16a34a' }
    case 'CELULAR':
      return { iconeFundo: '#e0f2fe', iconeCor: '#0284c7' }
    case 'TELEFONE_FIXO':
      return { iconeFundo: '#fce7f3', iconeCor: '#db2777' }
    default:
      return { iconeFundo: '#f1f5f9', iconeCor: '#64748b' }
  }
}

const copiarContato = async (contatoItem) => {
  const valorContato = String(contatoItem?.valor || '').trim()

  if (!valorContato) {
    showError('Não foi possível copiar esse contato.')
    return
  }

  const copied = await copyText(valorContato)

  if (copied) {
    success(`${contatoItem.tipoLabel} copiado para a área de transferência.`)
    return
  }

  showError('Falha ao copiar o contato. Tente novamente ou copie manualmente.')
}

const contatosFornecedor = computed(() => {
  const contato = fornecedor.value?.contato
  if (!contato) return []

  const contatos = []

  const adicionarContato = ({ key, tipo, label, valor, origem }) => {
    const valorNormalizado = String(valor || '').trim()
    if (!valorNormalizado) return

    const tipoNormalizado = normalizarTipoContato(tipo)
    const estilo = getEstiloContato(tipoNormalizado)
    const nomeContato = String(label || '').trim()
    const tipoLabel = getLabelTipoContato(tipoNormalizado)

    contatos.push({
      key,
      tipo: tipoNormalizado,
      tipoLabel,
      nomeContato: nomeContato && nomeContato.toLowerCase() !== tipoLabel.toLowerCase() ? nomeContato : '',
      valor: valorNormalizado,
      valorExibicao: formatarValorContato(tipoNormalizado, valorNormalizado),
      origem: origem || 'principal',
      ...estilo
    })
  }

  adicionarContato({
    key: 'contato-principal-telefone-fixo',
    tipo: 'TELEFONE_FIXO',
    label: contato.rotuloTelefoneFixo,
    valor: contato.telefoneFixo,
    origem: 'principal'
  })

  adicionarContato({
    key: 'contato-principal-celular',
    tipo: 'CELULAR',
    label: contato.rotuloCelular,
    valor: contato.celular,
    origem: 'principal'
  })

  adicionarContato({
    key: 'contato-principal-email',
    tipo: 'EMAIL',
    label: contato.rotuloEmail,
    valor: contato.email,
    origem: 'principal'
  })

  if (Array.isArray(contato.contatosAdicionais)) {
    contato.contatosAdicionais.forEach((contatoAdicional, index) => {
      adicionarContato({
        key: `contato-adicional-${contatoAdicional?.id || index}`,
        tipo: contatoAdicional?.tipoContato,
        label: contatoAdicional?.nomeContato,
        valor: contatoAdicional?.valorContato,
        origem: 'adicional'
      })
    })
  }

  return contatos
})

const contatosPrincipais = computed(() =>
  contatosFornecedor.value.filter(contato => contato.origem === 'principal')
)

const contatosAdicionais = computed(() =>
  contatosFornecedor.value.filter(contato => contato.origem === 'adicional')
)

const gruposContato = computed(() => {
  const grupos = []

  if (contatosPrincipais.value.length > 0) {
    grupos.push({
      key: 'principais',
      titulo: 'Contatos Principais',
      itens: contatosPrincipais.value
    })
  }

  if (contatosAdicionais.value.length > 0) {
    grupos.push({
      key: 'adicionais',
      titulo: 'Contatos Adicionais',
      itens: contatosAdicionais.value
    })
  }

  return grupos
})

const getEnderecoLogradouro = (endereco) => {
  if (!endereco) return 'Não informado'
  const rua = String(endereco.rua || '').trim()
  const numero = String(endereco.numero || '').trim()

  if (!rua && !numero) return 'Não informado'
  if (!rua) return numero
  if (!numero) return rua
  return `${rua}, ${numero}`
}

const getEnderecoCidadeUf = (endereco) => {
  if (!endereco) return 'Não informado'
  const cidade = String(endereco.cidade || '').trim()
  const estado = String(endereco.estado || '').trim()

  if (!cidade && !estado) return 'Não informado'
  if (!cidade) return estado
  if (!estado) return cidade
  return `${cidade}/${estado}`
}

const formatarCEP = (cep) => {
  if (!cep) return ''
  const numbers = cep.replace(/\D/g, '')
  return numbers.replace(/^(\d{5})(\d{3})$/, '$1-$2')
}

const formatarMoeda = (valor) => {
  if (valor === null || valor === undefined || valor === '' || Number.isNaN(Number(valor))) return '-'
  return `R$ ${Number(valor).toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })}`
}

const formatarQuantidade = (quantidade) => {
  if (quantidade === null || quantidade === undefined || quantidade === '' || Number.isNaN(Number(quantidade))) return '-'
  return Number(quantidade).toLocaleString('pt-BR')
}

const getTipoLabel = (tipo) => {
  const labels = {
    produto: 'Fornecedor de Produtos',
    servico: 'Fornecedor de Serviços'
  }
  return labels[tipo] || 'Fornecedor'
}

const getStatusClass = (status) => {
  const classes = {
    ativo: 'status-active',
    inativo: 'status-inactive',
    suspenso: 'status-suspended'
  }
  return classes[status] || 'status-active'
}

const getStatusLabel = (status) => {
  const labels = {
    ativo: 'Ativo',
    inativo: 'Inativo',
    suspenso: 'Suspenso'
  }
  return labels[status] || 'Ativo'
}

const normalizarStatusCotacao = (status) => String(status || '').toUpperCase().trim()

const getStatusCotacaoClass = (status) => {
  const key = normalizarStatusCotacao(status)
  const classes = {
    APROVADA: 'status-aprovada',
    SELECIONADA: 'status-aprovada',
    PARCIAL: 'status-parcial',
    REJEITADA: 'status-rejeitada',
    NAO_SELECIONADA: 'status-rejeitada',
    NAO_COTADA: 'status-rejeitada',
    CANCELADA: 'status-cancelada',
    EM_ANALISE: 'status-analise',
    PENDENTE: 'status-analise'
  }
  return classes[key] || 'status-analise'
}

const getStatusCotacaoLabel = (status) => {
  const key = normalizarStatusCotacao(status)
  const labels = {
    APROVADA: 'Aprovada',
    SELECIONADA: 'Selecionada',
    PARCIAL: 'Parcial',
    REJEITADA: 'Nao Selecionada',
    NAO_SELECIONADA: 'Nao Selecionada',
    NAO_COTADA: 'Nao Cotada',
    CANCELADA: 'Cancelada',
    EM_ANALISE: 'Em Analise',
    PENDENTE: 'Em Analise'
  }
  return labels[key] || 'Em Analise'
}

const getStatusItemLabel = (status) => {
  const labels = {
    APROVADO: 'Aprovado',
    COTADO_NAO_SELECIONADO: 'Cotado nao selecionado',
    NAO_COTADO: 'Nao cotado'
  }
  return labels[status] || status
}

const getStatusItemClass = (status) => {
  const classes = {
    APROVADO: 'item-status-aprovado',
    COTADO_NAO_SELECIONADO: 'item-status-cotado-nao-selecionado',
    NAO_COTADO: 'item-status-nao-cotado'
  }
  return classes[status] || 'item-status-nao-cotado'
}

const getStatusItemCardClass = (status) => {
  const classes = {
    APROVADO: 'item-card-aprovado',
    COTADO_NAO_SELECIONADO: 'item-card-cotado-nao-selecionado',
    NAO_COTADO: 'item-card-nao-cotado'
  }
  return classes[status] || 'item-card-nao-cotado'
}

const normalizarNomeItem = (nome) => String(nome || '')
  .normalize('NFD')
  .replace(/[\u0300-\u036f]/g, '')
  .replace(/[^\w\s]/g, ' ')
  .replace(/\s+/g, ' ')
  .trim()
  .toLowerCase()

const parseListaItensTexto = (texto) => {
  if (!texto || typeof texto !== 'string') return []
  return texto
    .split(/[,;|]/)
    .map(item => item.trim())
    .filter(Boolean)
}

const parseItensSnapshot = (snapshot) => {
  if (!snapshot) return []
  if (Array.isArray(snapshot)) return snapshot

  try {
    const parsed = JSON.parse(snapshot)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

const localizarItemPorNome = (nome, itens = [], mapa = new Map()) => {
  const chaveBusca = normalizarNomeItem(nome)
  if (!chaveBusca) return null

  if (mapa.has(chaveBusca)) {
    return mapa.get(chaveBusca)
  }

  return itens.find(item => {
    const nomeItem = normalizarNomeItem(item?.nome)
    return nomeItem && (nomeItem.includes(chaveBusca) || chaveBusca.includes(nomeItem))
  }) || null
}

const gerarChaveItem = (item = {}) => {
  if (item.itemPedidoId !== null && item.itemPedidoId !== undefined) {
    return `id-${item.itemPedidoId}`
  }

  const nomeNormalizado = normalizarNomeItem(item.nome)
  if (nomeNormalizado) {
    return `nome-${nomeNormalizado}`
  }

  return null
}

const normalizarDetalheItem = (item = {}) => {
  const quantidade = item.quantidade !== null && item.quantidade !== undefined && !Number.isNaN(Number(item.quantidade))
    ? Number(item.quantidade)
    : null

  const precoUnitario = item.precoUnitario !== null && item.precoUnitario !== undefined && !Number.isNaN(Number(item.precoUnitario))
    ? Number(item.precoUnitario)
    : null

  const total = item.total !== null && item.total !== undefined && !Number.isNaN(Number(item.total))
    ? Number(item.total)
    : (precoUnitario !== null && quantidade !== null ? precoUnitario * quantidade : null)

  return {
    chave: item.chave || gerarChaveItem(item),
    itemPedidoId: item.itemPedidoId !== null && item.itemPedidoId !== undefined ? Number(item.itemPedidoId) : null,
    nome: item.nome || (item.itemPedidoId ? `Item #${item.itemPedidoId}` : 'Item sem nome'),
    quantidade,
    precoUnitario,
    total,
    descricao: item.descricao || '',
    observacao: item.observacao || '',
    status: item.status || 'NAO_COTADO'
  }
}

const deduplicarItensDetalhados = (itens) => {
  const chaves = new Set()

  return (Array.isArray(itens) ? itens : [])
    .map(item => normalizarDetalheItem(item))
    .filter(item => {
      const chave = item.chave || gerarChaveItem(item)
      if (!chave || chaves.has(chave)) return false
      item.chave = chave
      chaves.add(chave)
      return true
    })
}

const aplicarStatusNosItens = (itens, status) => {
  return itens.map(item => ({
    ...item,
    status
  }))
}

const obterHistoricoCotacaoComCache = async (cotacaoId) => {
  if (!cotacaoId) return []

  if (cacheHistoricoCotacao.value.has(cotacaoId)) {
    return cacheHistoricoCotacao.value.get(cotacaoId)
  }

  try {
    const historico = await cotacaoService.buscarHistorico(cotacaoId)
    const lista = Array.isArray(historico) ? historico : []
    cacheHistoricoCotacao.value.set(cotacaoId, lista)
    return lista
  } catch (error) {
    logger.warn(`Erro ao buscar historico da cotacao ${cotacaoId}:`, error)
    cacheHistoricoCotacao.value.set(cotacaoId, [])
    return []
  }
}

const montarDetalhamentoItensCotacao = (cotacao, pedidoRelacionado, itensSelecionadosDetalhados, historicoComSelecao) => {
  const itensSelecionadosHistorico = parseListaItensTexto(historicoComSelecao?.itensSelecionados)
  const itensNaoSelecionadosHistorico = parseListaItensTexto(historicoComSelecao?.itensNaoSelecionados)
  const itensPedido = Array.isArray(pedidoRelacionado?.itens) ? pedidoRelacionado.itens : []

  const itensPedidoDetalhados = deduplicarItensDetalhados(
    itensPedido.map(item => {
      if (!item) return null
      return {
        chave: item.id ? `id-${item.id}` : null,
        itemPedidoId: item.id || null,
        nome: item.nome || (item.id ? `Item #${item.id}` : null),
        quantidade: item.quantidade ?? null,
        precoUnitario: null,
        total: null,
        descricao: item.descricao || '',
        observacao: item.observacao || '',
        status: 'NAO_COTADO'
      }
    })
  )
  const itensPedidoPorNome = new Map(
    itensPedidoDetalhados
      .filter(item => normalizarNomeItem(item.nome))
      .map(item => [normalizarNomeItem(item.nome), item])
  )

  const itensCotacao = deduplicarItensDetalhados(itensSelecionadosDetalhados || [])
  const itensCotacaoPorNome = new Map(
    itensCotacao
      .filter(item => normalizarNomeItem(item.nome))
      .map(item => [normalizarNomeItem(item.nome), item])
  )

  const itensSnapshotHistorico = deduplicarItensDetalhados(
    parseItensSnapshot(historicoComSelecao?.itensNovos).map(item => ({
      itemPedidoId: item?.itemPedidoId ?? item?.itemRascunhoId ?? null,
      nome: item?.nomeItem || item?.nome || ((item?.itemPedidoId || item?.itemRascunhoId) ? `Item #${item?.itemPedidoId ?? item?.itemRascunhoId}` : null),
      quantidade: item?.quantidade ?? null,
      precoUnitario: item?.precoUnitario ?? null,
      total: item?.total ?? null,
      descricao: item?.descricao || '',
      observacao: item?.observacao || ''
    }))
  )
  const itensSnapshotPorNome = new Map(
    itensSnapshotHistorico
      .filter(item => normalizarNomeItem(item.nome))
      .map(item => [normalizarNomeItem(item.nome), item])
  )

  const criarItemComNomeHistorico = (nome, status) => {
    const itemCotacao = localizarItemPorNome(nome, itensCotacao, itensCotacaoPorNome)
    const itemSnapshot = localizarItemPorNome(nome, itensSnapshotHistorico, itensSnapshotPorNome)
    const itemPedido = localizarItemPorNome(nome, itensPedidoDetalhados, itensPedidoPorNome)

    const quantidade = itemCotacao?.quantidade ?? itemSnapshot?.quantidade ?? itemPedido?.quantidade ?? null
    const precoUnitario = itemCotacao?.precoUnitario ?? itemSnapshot?.precoUnitario ?? null
    const total = itemCotacao?.total ?? itemSnapshot?.total
      ?? (precoUnitario !== null && quantidade !== null ? precoUnitario * quantidade : null)

    return normalizarDetalheItem({
      itemPedidoId: itemCotacao?.itemPedidoId ?? itemSnapshot?.itemPedidoId ?? itemPedido?.itemPedidoId ?? null,
      nome: nome || itemCotacao?.nome || itemSnapshot?.nome || itemPedido?.nome,
      quantidade,
      precoUnitario,
      total,
      descricao: itemCotacao?.descricao || itemSnapshot?.descricao || itemPedido?.descricao || '',
      observacao: itemCotacao?.observacao || itemSnapshot?.observacao || itemPedido?.observacao || '',
      status
    })
  }

  let itensAprovados = []
  if (itensSelecionadosHistorico.length > 0) {
    itensAprovados = deduplicarItensDetalhados(
      itensSelecionadosHistorico.map(nome => criarItemComNomeHistorico(nome, 'APROVADO'))
    )
  } else if (itensNaoSelecionadosHistorico.length === 0 && itensCotacao.length > 0) {
    itensAprovados = deduplicarItensDetalhados(aplicarStatusNosItens(itensCotacao, 'APROVADO'))
  }

  let itensCotadosNaoSelecionados = []
  if (itensNaoSelecionadosHistorico.length > 0) {
    itensCotadosNaoSelecionados = deduplicarItensDetalhados(
      itensNaoSelecionadosHistorico.map(nome => criarItemComNomeHistorico(nome, 'COTADO_NAO_SELECIONADO'))
    )
  } else if (itensSelecionadosHistorico.length > 0) {
    const chavesAprovadas = new Set(itensAprovados.map(item => item.chave).filter(Boolean))
    const nomesAprovados = new Set(itensAprovados.map(item => normalizarNomeItem(item.nome)).filter(Boolean))
    itensCotadosNaoSelecionados = deduplicarItensDetalhados(
      aplicarStatusNosItens(
        itensCotacao.filter(item => !chavesAprovadas.has(item.chave) && !nomesAprovados.has(normalizarNomeItem(item.nome))),
        'COTADO_NAO_SELECIONADO'
      )
    )
  }

  const itensClassificados = new Set([
    ...itensAprovados.map(item => item.chave).filter(Boolean),
    ...itensCotadosNaoSelecionados.map(item => item.chave).filter(Boolean)
  ])
  const nomesClassificados = new Set([
    ...itensAprovados.map(item => normalizarNomeItem(item.nome)).filter(Boolean),
    ...itensCotadosNaoSelecionados.map(item => normalizarNomeItem(item.nome)).filter(Boolean)
  ])

  const itensNaoCotados = aplicarStatusNosItens(
    itensPedidoDetalhados.filter(item => {
      const chave = item.chave || gerarChaveItem(item)
      const nomeNormalizado = normalizarNomeItem(item.nome)
      return !itensClassificados.has(chave) && !nomesClassificados.has(nomeNormalizado)
    }),
    'NAO_COTADO'
  )

  const itensStatusDetalhados = deduplicarItensDetalhados([
    ...aplicarStatusNosItens(itensAprovados, 'APROVADO'),
    ...aplicarStatusNosItens(itensCotadosNaoSelecionados, 'COTADO_NAO_SELECIONADO'),
    ...aplicarStatusNosItens(itensNaoCotados, 'NAO_COTADO')
  ])

  const resumoItensStatus = {
    aprovados: itensAprovados.length,
    cotadosNaoSelecionados: itensCotadosNaoSelecionados.length,
    naoCotados: itensNaoCotados.length
  }

  const statusOriginal = normalizarStatusCotacao(cotacao.statusSelecao || cotacao.status || 'EM_ANALISE')
  let statusExibicao = statusOriginal || 'EM_ANALISE'

  if (statusOriginal !== 'CANCELADA') {
    if (resumoItensStatus.aprovados > 0 && resumoItensStatus.cotadosNaoSelecionados === 0 && resumoItensStatus.naoCotados === 0) {
      statusExibicao = 'APROVADA'
    } else if (resumoItensStatus.aprovados > 0) {
      statusExibicao = 'PARCIAL'
    } else if (resumoItensStatus.cotadosNaoSelecionados > 0) {
      statusExibicao = 'NAO_SELECIONADA'
    } else if (resumoItensStatus.naoCotados > 0 && itensPedido.length > 0) {
      statusExibicao = 'NAO_COTADA'
    }
  }

  return {
    itensStatusDetalhados,
    resumoItensStatus,
    statusExibicao
  }
}

// PDF
const togglePdfViewer = async (cotacao, pdfIndex = 0) => {
  const pdfKey = `${cotacao.id}-${pdfIndex}`

  // Se o PDF já está aberto, fechar
  if (pdfAberto.value === pdfKey) {
    fecharPdfViewer()
    return
  }

  // Fechar PDF anterior se houver
  if (pdfAberto.value !== null) {
    fecharPdfViewer()
  }

  pdfAberto.value = pdfKey
  carregandoPdf.value = true

  try {
    const blob = await cotacaoService.obterAnexoPdf(cotacao.id, pdfIndex)

    if (blob && blob.size > 0) {
      pdfUrl.value = URL.createObjectURL(blob)
    }
  } catch (error) {
    logger.error('Erro ao carregar PDF:', error)
  } finally {
    carregandoPdf.value = false
  }
}

const fecharPdfViewer = () => {
  if (pdfUrl.value && pdfUrl.value.startsWith('blob:')) {
    URL.revokeObjectURL(pdfUrl.value)
  }
  pdfAberto.value = null
  pdfUrl.value = null
}

// Buscar com cache
const buscarItemPedidoComCache = async (itemPedidoId) => {
  if (!itemPedidoId) return null
  if (cacheItensPedido.value.has(itemPedidoId)) {
    return cacheItensPedido.value.get(itemPedidoId)
  }
  try {
    const item = await itemPedidoService.buscarPorId(itemPedidoId)
    if (item) {
      cacheItensPedido.value.set(itemPedidoId, item)
    }
    return item
  } catch (error) {
    logger.warn(`Erro ao buscar item ${itemPedidoId}:`, error)
    return null
  }
}

const buscarPedidoComCache = async (pedidoId) => {
  if (!pedidoId) return null
  if (cachePedidos.value.has(pedidoId)) {
    return cachePedidos.value.get(pedidoId)
  }
  try {
    const pedido = await pedidoService.obterPorId(pedidoId)
    if (pedido) {
      cachePedidos.value.set(pedidoId, pedido)
    }
    return pedido
  } catch (error) {
    logger.warn(`Erro ao buscar pedido ${pedidoId}:`, error)
    return null
  }
}

// Carregar fornecedor
const carregarFornecedor = async () => {
  try {
    isLoading.value = true
    const fornecedorId = route.params.id
    const tipo = route.query.tipo

    if (tipo === 'produto') {
      fornecedor.value = await fornecedorService.obterFornecedorProdutoPorId(fornecedorId)
      fornecedor.value.tipo = 'produto'
    } else if (tipo === 'servico') {
      fornecedor.value = await fornecedorService.obterFornecedorServicoPorId(fornecedorId)
      fornecedor.value.tipo = 'servico'
    } else {
      // Se não especificou o tipo, tentar buscar em ambos
      try {
        fornecedor.value = await fornecedorService.obterFornecedorProdutoPorId(fornecedorId)
        fornecedor.value.tipo = 'produto'
      } catch {
        // Se não encontrou em produtos, tentar em serviços
        try {
          fornecedor.value = await fornecedorService.obterFornecedorServicoPorId(fornecedorId)
          fornecedor.value.tipo = 'servico'
        } catch {
          throw new Error('Fornecedor não encontrado em produtos nem em serviços')
        }
      }
    }

    // Carregar histórico
    await carregarHistoricoFornecedor(fornecedorId)
  } catch (error) {
    logger.error('Erro ao carregar fornecedor:', error)
    fornecedor.value = null
  } finally {
    isLoading.value = false
  }
}

// Carregar histórico
const carregarHistoricoFornecedor = async (fornecedorId) => {
  try {
    carregandoHistorico.value = true
    detalhesItensAbertos.value = new Set()

    // Usar o endpoint específico de histórico do fornecedor
    // Isso garante que só venham cotações deste fornecedor específico
    // e evita o problema de IDs duplicados entre tabelas diferentes
    let cotacoes = []
    try {
      cotacoes = await fornecedorService.obterHistoricoCotacoes(fornecedorId, fornecedor.value.tipo)
    } catch (error) {
      logger.warn('Erro ao buscar histórico específico, tentando fallback:', error)
      // Fallback para listagem geral (legado, pode ter bugs de ID)
      const todasCotacoes = await cotacaoService.listar()
      cotacoes = todasCotacoes.filter(cot => {
        const fornecedorCotacaoId = cot.fornecedorProdutoId || cot.fornecedorServicoId || cot.fornecedorId
        // Verificar também o tipo para evitar conflito de IDs
        const tipoCotacao = cot.tipoFornecedor?.toLowerCase()
        if (tipoCotacao && tipoCotacao !== fornecedor.value.tipo) return false

        return fornecedorCotacaoId === Number(fornecedorId)
      })
    }

    if (cotacoes && cotacoes.length > 0) {
      const totalCotacoes = cotacoes.length
      const valorTotal = cotacoes.reduce((acc, cot) => acc + (parseFloat(cot.preco) || 0), 0)

      // Função auxiliar para obter a data da cotação
      const getDataCotacao = (cot) => {
        // Prioridade: dataCriacao (novo campo) -> dataUltimaEdicao -> data (fallback)
        const dateStr = cot.dataCriacao || cot.dataUltimaEdicao || cot.data;
        return dateStr ? new Date(dateStr) : null;
      }

      const cotacoesOrdenadas = [...cotacoes].sort((a, b) => {
        const dateA = getDataCotacao(a) || new Date(0)
        const dateB = getDataCotacao(b) || new Date(0)
        return dateB - dateA
      })

      const ultimaCotacao = cotacoesOrdenadas[0]
      const ultimaDataObj = getDataCotacao(ultimaCotacao)
      const dataUltima = ultimaDataObj
        ? ultimaDataObj.toLocaleDateString('pt-BR')
        : '-'

      const cotacoesComDetalhes = await Promise.all(
        cotacoesOrdenadas.map(async (cot) => {
          let pedido = null

          if (cot.solicitacaoDePedidoId) {
            pedido = await buscarPedidoComCache(cot.solicitacaoDePedidoId)
          }

          const itensSelecionadosDetalhados = await Promise.all(
            (cot.itens || []).map(async (itemCotacao) => {
              if (!itemCotacao) return null

              let itemPedido = null
              if (
                (!itemCotacao.nomeItem ||
                  itemCotacao.quantidade === null || itemCotacao.quantidade === undefined ||
                  !itemCotacao.descricao || !itemCotacao.observacao) &&
                itemCotacao.itemPedidoId
              ) {
                itemPedido = await buscarItemPedidoComCache(itemCotacao.itemPedidoId)
              }

              const quantidade = itemCotacao.quantidade !== null && itemCotacao.quantidade !== undefined
                ? Number(itemCotacao.quantidade)
                : (itemPedido?.quantidade !== null && itemPedido?.quantidade !== undefined ? Number(itemPedido.quantidade) : null)

              const precoUnitario = itemCotacao.precoUnitario !== null && itemCotacao.precoUnitario !== undefined
                ? Number(itemCotacao.precoUnitario)
                : null

              const total = itemCotacao.precoTotal !== null && itemCotacao.precoTotal !== undefined
                ? Number(itemCotacao.precoTotal)
                : (precoUnitario !== null && quantidade !== null ? precoUnitario * quantidade : null)

              return {
                itemPedidoId: itemCotacao.itemPedidoId || itemPedido?.id || null,
                nome: itemCotacao.nomeItem || itemPedido?.nome || itemPedido?.descricao || `Item #${itemCotacao.itemPedidoId || 'N/A'}`,
                quantidade,
                precoUnitario,
                total,
                descricao: itemCotacao.descricao || itemPedido?.descricao || '',
                observacao: itemCotacao.observacao || itemPedido?.observacao || ''
              }
            })
          )

          const itensSelecionadosValidos = itensSelecionadosDetalhados.filter(item => item && item.nome)
          const primeiroItem = itensSelecionadosValidos[0] || null
          const nomesItens = itensSelecionadosValidos.map(item => item.nome).join(', ')
          const quantidadeTotal = itensSelecionadosValidos.reduce((sum, item) => sum + (item.quantidade || 0), 0)

          const historicoCotacao = await obterHistoricoCotacaoComCache(cot.id)
          const historicoComSelecao = historicoCotacao.find(h => h?.itensSelecionados || h?.itensNaoSelecionados)
          const detalhesItens = montarDetalhamentoItensCotacao(
            cot,
            pedido,
            itensSelecionadosValidos,
            historicoComSelecao
          )

          const dataRaw = getDataCotacao(cot)

          return {
            id: cot.id,
            numero: cot.id?.toString() || '-',
            dataRaw: dataRaw,
            data: dataRaw
              ? dataRaw.toLocaleDateString('pt-BR')
              : '-',
            valor: parseFloat(cot.preco) || 0,
            status: normalizarStatusCotacao(cot.statusSelecao || cot.status || 'PENDENTE'),
            statusExibicao: detalhesItens.statusExibicao,
            prazoEntrega: cot.dataLimite
              ? new Date(cot.dataLimite).toLocaleDateString('pt-BR')
              : '-',
            itemNome: nomesItens || 'Item nao especificado',
            itemQuantidade: quantidadeTotal,
            itemDescricao: primeiroItem?.descricao || '',
            itemObservacao: primeiroItem?.observacao || '',
            pedidoId: pedido?.id || null,
            pedidoStatus: pedido?.status || null,
            pedidoObservacao: pedido?.observacao || '',
            pedidoData: pedido?.dataCriacao
              ? new Date(pedido.dataCriacao).toLocaleDateString('pt-BR')
              : '-',
            totalItens: detalhesItens.itensStatusDetalhados.length,
            itensStatusDetalhados: detalhesItens.itensStatusDetalhados,
            resumoItensStatus: detalhesItens.resumoItensStatus,
            temAnexoPdf: cot.temAnexoPdf || false,
            quantidadeAnexos: cot.quantidadeAnexos || 0
          }
        })
      )

      historicoCompras.value = {
        total: totalCotacoes,
        valor: valorTotal,
        ultimoPedido: dataUltima,
        cotacoes: cotacoesComDetalhes
      }
    } else {
      historicoCompras.value = {
        total: 0,
        valor: 0,
        ultimoPedido: '-',
        cotacoes: []
      }
    }
  } catch (error) {
    logger.error('Erro ao carregar histórico do fornecedor:', error)
    historicoCompras.value = {
      total: 0,
      valor: 0,
      ultimoPedido: '-',
      cotacoes: []
    }
  } finally {
    carregandoHistorico.value = false
  }
}

// Lifecycle
onMounted(() => {
  carregarFornecedor()
})

onBeforeUnmount(() => {
  fecharPdfViewer()
})
</script>

<style scoped>
@import '../assets/css/layout.css';
/* Layout base */
.page-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.main-content {
  display: flex;
  padding-top: 64px;
}

.content-area {
  flex: 1;
  padding: 24px;
  margin-left: 250px;
}

/* Loading */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  gap: 16px;
  color: #6b7280;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e5e7eb;
  border-top-color: #1F285F;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Breadcrumb */

.breadcrumb-link {
  color: #1F285F;
  text-decoration: none;
  font-weight: 600;
  white-space: nowrap;
  line-height: 1;
  flex-shrink: 0;
}

.breadcrumb-link:hover {
  text-decoration: underline;
}

/* View Container */
.view-container {
  max-width: 1000px;
  margin: 0 auto;
}

/* View Header */
.view-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  background: #1F285F;
  color: white;
  border-radius: 16px 16px 0 0;
  margin-bottom: 0;
}

.header-content {
  flex: 1;
}

.view-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.view-subtitle {
  font-size: 0.875rem;
  opacity: 0.9;
  margin: 0;
}

.header-resumo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  flex-wrap: wrap;
}

.resumo-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.875rem;
  opacity: 0.95;
}

.resumo-icon {
  flex-shrink: 0;
}

.resumo-separator {
  opacity: 0.5;
  font-size: 0.875rem;
}

.resumo-total {
  font-weight: 600;
}

.total-valor {
  font-weight: 600;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.tipo-badge {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.tipo-badge.produto {
  background: rgba(59, 130, 246, 0.2);
  color: #dbeafe;
}

.tipo-badge.servico {
  background: rgba(16, 185, 129, 0.2);
  color: #d1fae5;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.status-active {
  background: #d1fae5;
  color: #065f46;
}

.status-inactive {
  background: #fee2e2;
  color: #991b1b;
}

.status-suspended {
  background: #fef3c7;
  color: #92400e;
}

/* Section Card */
.section-card {
  background: white;
  padding: 28px 32px;
  border-left: 1px solid #e5e7eb;
  border-right: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
  position: relative;
}

.section-card:not(:first-of-type)::before {
  content: '';
  position: absolute;
  top: 0;
  left: 32px;
  right: 32px;
  height: 1px;
  background: linear-gradient(90deg, transparent, #e5e7eb 10%, #e5e7eb 90%, transparent);
}

.section-card:nth-child(even) {
  background: #fafafa;
}

.section-card:last-child {
  border-radius: 0 0 16px 16px;
  padding-bottom: 32px;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 20px 0;
  padding-bottom: 12px;
  border-bottom: 3px solid #e5e7eb;
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -3px;
  left: 0;
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #6366f1, #8b5cf6);
  border-radius: 2px;
}

/* Info Grid */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 0.75rem;
  color: #6b7280;
  font-weight: 500;
  text-transform: uppercase;
}

.info-value {
  font-size: 0.875rem;
  color: #1e293b;
  font-weight: 500;
}

/* Enhanced Info Grid */
.info-grid-enhanced {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.contato-grupos {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.contato-grupo {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contato-grupo-titulo {
  margin: 0;
  font-size: 0.875rem;
  font-weight: 700;
  color: #334155;
  text-transform: uppercase;
  letter-spacing: 0.4px;
}

.contato-grid {
  margin-bottom: 0;
}

.info-card {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s ease;
}

.info-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transform: translateY(-1px);
}

.info-card-copy {
  width: 100%;
  cursor: pointer;
  text-align: left;
  font: inherit;
  appearance: none;
}

.info-card-copy:focus-visible {
  outline: none;
  border-color: #93c5fd;
  box-shadow: 0 0 0 3px rgba(147, 197, 253, 0.28), 0 2px 8px rgba(0, 0, 0, 0.06);
}

.info-card-icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 2px;
}

.info-card-icon svg {
  width: 24px;
  height: 24px;
}

.info-card-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  min-width: 0;
}

.info-card-label {
  font-size: 0.6875rem;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 600;
}

.info-card-sub-label {
  font-size: 0.75rem;
  color: #64748b;
  font-weight: 500;
}

.info-card-value {
  font-size: 1rem;
  color: #1e293b;
  font-weight: 600;
}

.info-card.full-width {
  grid-column: 1 / -1;
}

.info-card.full-width .info-card-value {
  word-break: break-all;
  overflow-wrap: anywhere;
  line-height: 1.6;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #9ca3af;
}

.empty-state svg {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state h3 {
  font-size: 1.125rem;
  color: #6b7280;
  margin: 0 0 8px 0;
}

.empty-state p {
  font-size: 0.875rem;
  margin: 0;
}

/* Estilos adicionais específicos */
.endereco-completo {
  line-height: 1.6;
  word-break: normal;
  overflow-wrap: anywhere;
}

.full-width {
  grid-column: 1 / -1;
}

/* Histórico Stats */
.historico-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
  background: white;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
}

.stat-label {
  font-size: 0.875rem;
  color: #6b7280;
  font-weight: 500;
}

/* Lista de Cotações */
.historico-lista {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.cotacao-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.cotacao-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* Header Padronizado */
.cotacao-header-padrao {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e5e7eb;
  gap: 16px;
  background: #f8fafc;
}

.cotacao-info-principal {
  flex: 1;
}

.fornecedor-linha {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.fornecedor-nome {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.4;
}

.tipo-tag {
  font-size: 0.75rem;
  color: #ffffff;
  background: #1F285F;
  padding: 4px 10px;
  border-radius: 4px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.tipo-tag-data {
  background: #f1f5f9;
  color: #64748b;
}

.preco-destaque-box {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  padding: 10px 14px;
  border-radius: 8px;
  border: 1px solid #86efac;
}

.preco-label-small {
  font-size: 0.65rem;
  color: #059669;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 700;
}

.preco-valor-grande {
  font-size: 1.375rem;
  font-weight: 700;
  color: #059669;
  line-height: 1;
}

/* Body */
.cotacao-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* Grid de Informações */
.cotacao-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.info-box {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 12px 14px;
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.info-box:hover {
  background: #f3f4f6;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.info-box-icon {
  width: 20px;
  height: 20px;
  color: #6366f1;
  flex-shrink: 0;
  margin-top: 2px;
}

.info-box-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-box-label {
  font-size: 0.6875rem;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 600;
}

.info-box-value {
  font-size: 0.9375rem;
  color: #1e293b;
  font-weight: 600;
}

/* Seção de Itens */
.cotacao-itens-section {
  padding: 16px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.itens-section-title {
  font-size: 0.75rem;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 700;
  margin: 0 0 12px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #e5e7eb;
}

.itens-detalhes-actions {
  margin-bottom: 10px;
}

.btn-itens-detalhes {
  border: 1px solid #1F285F;
  background: linear-gradient(135deg, #1F285F 0%, #2b387a 100%);
  color: #ffffff;
  border-radius: 6px;
  padding: 8px 12px;
  font-size: 0.75rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 2px 6px rgba(31, 40, 95, 0.24);
}

.btn-itens-detalhes:hover {
  background: linear-gradient(135deg, #253173 0%, #334190 100%);
  border-color: #334190;
  transform: translateY(-1px);
  box-shadow: 0 4px 10px rgba(31, 40, 95, 0.28);
}

.btn-itens-detalhes:active {
  transform: translateY(0);
}

.btn-itens-icon {
  flex-shrink: 0;
}

.itens-status-lista {
  display: grid;
  gap: 10px;
  margin-bottom: 10px;
}

.item-status-card {
  border: 1px solid #e2e8f0;
  border-left: 4px solid #cbd5e1;
  border-radius: 8px;
  background: #ffffff;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.item-card-aprovado {
  border-left-color: #10b981;
  background: #f8fffb;
}

.item-card-cotado-nao-selecionado {
  border-left-color: #f97316;
  background: #fffaf5;
}

.item-card-nao-cotado {
  border-left-color: #94a3b8;
  background: #f8fafc;
}

.item-status-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 10px;
}

.item-status-identificacao {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.item-status-nome {
  font-size: 0.9rem;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.3;
}

.item-status-codigo {
  font-size: 0.75rem;
  font-weight: 600;
  color: #64748b;
}

.item-chip {
  background: #eef2ff;
  color: #3730a3;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 700;
  line-height: 1.4;
  border: 1px solid #c7d2fe;
}

.item-chip-status {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.item-status-aprovado {
  background: #ecfdf5;
  color: #065f46;
  border-color: #a7f3d0;
}

.item-status-cotado-nao-selecionado {
  background: #fff7ed;
  color: #9a3412;
  border-color: #fdba74;
}

.item-status-nao-cotado {
  background: #f3f4f6;
  color: #374151;
  border-color: #d1d5db;
}

.item-status-metricas {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 8px;
}

.item-status-metrica {
  padding: 8px 10px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.metrica-label {
  font-size: 0.625rem;
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.4px;
}

.metrica-valor {
  font-size: 0.875rem;
  font-weight: 700;
  color: #1f2937;
}

.item-status-detalhe {
  margin: 0;
  font-size: 0.8125rem;
  color: #334155;
  line-height: 1.5;
}

.itens-sem-detalhe {
  margin: 0 0 10px;
  font-size: 0.8125rem;
  color: #64748b;
}

.itens-resumo-status {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.resumo-status-badge {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 700;
}

.resumo-aprovado {
  background: #d1fae5;
  color: #065f46;
}

.resumo-cotado-nao-selecionado {
  background: #ffedd5;
  color: #9a3412;
}

.resumo-nao-cotado {
  background: #f3f4f6;
  color: #374151;
}

/* Seção de Observações */
.observacoes-section {
  padding: 16px;
  background: #fffbeb;
  border: 1px solid #fde68a;
  border-radius: 8px;
}

.observacao-content p {
  margin: 0 0 8px 0;
  font-size: 0.875rem;
  color: #78350f;
  line-height: 1.6;
}

.observacao-content p:last-child {
  margin-bottom: 0;
}

/* Ações e PDFs */
.cotacao-actions {
  padding: 12px 20px;
  margin-top: 0;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  background: #fafbfc;
}

.pdf-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.btn-pdf-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 0.875rem;
  cursor: pointer;
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  color: white;
  border: none;
  transition: all 0.3s ease;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.25);
}

.btn-pdf-primary .btn-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.btn-pdf-primary:hover {
  background: linear-gradient(135deg, #4f46e5 0%, #4338ca 100%);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.35);
  transform: translateY(-2px);
}

.btn-pdf-primary:active {
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(99, 102, 241, 0.3);
}

.btn-pdf-primary-active {
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.3);
}

.btn-pdf-primary-active:hover {
  background: linear-gradient(135deg, #0f172a 0%, #020617 100%);
}

.pdf-viewer-container {
  margin-top: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.pdf-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 12px;
  color: #6b7280;
}

.pdf-viewer-wrapper {
  height: 450px;
}

.cotacao-info h5 {
  margin: 0 0 4px 0;
  font-size: 1rem;
  font-weight: 600;
  color: #111827;
}

.cotacao-data {
  font-size: 0.875rem;
  color: #6b7280;
}

.cotacao-valor {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.valor-label {
  font-size: 0.75rem;
  color: #6b7280;
  margin-bottom: 2px;
}

.valor-amount {
  font-size: 1.125rem;
  font-weight: 700;
  color: #059669;
}

.status-badge-small {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 700;
  white-space: nowrap;
}

.status-aprovada {
  background: #d1fae5;
  color: #065f46;
}

.status-parcial {
  background: #ffedd5;
  color: #9a3412;
}

.status-rejeitada {
  background: #fee2e2;
  color: #991b1b;
}

.status-cancelada {
  background: #f3f4f6;
  color: #374151;
}

.status-analise {
  background: #e0e7ff;
  color: #4338ca;
}

.cotacao-details {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 12px 14px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
}

.detail-item.full-width {
  grid-column: 1 / -1;
}

.detail-label {
  font-size: 0.6875rem;
  font-weight: 600;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-value {
  font-size: 0.9375rem;
  color: #1e293b;
  line-height: 1.5;
  font-weight: 500;
}

/* Loading e Empty State */
.loading-message {
  text-align: center;
  padding: 40px;
  color: #6b7280;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.loading-spinner-small {
  width: 32px;
  height: 32px;
  border: 3px solid #e5e7eb;
  border-top: 3px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Tabs */
.tabs-container {
  display: flex;
  gap: 8px;
  background: white;
  padding: 16px 24px 0;
  border-left: 1px solid #e5e7eb;
  border-right: 1px solid #e5e7eb;
}

.tab-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: transparent;
  border: none;
  border-bottom: 3px solid transparent;
  color: #6b7280;
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.tab-button svg {
  flex-shrink: 0;
}

.tab-button:hover {
  color: #1F285F;
  background: #f9fafb;
}

.tab-button.active {
  color: #1F285F;
  border-bottom-color: #1F285F;
  font-weight: 600;
}

.tab-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  background: #1F285F;
  color: white;
  border-radius: 10px;
  font-size: 0.75rem;
  font-weight: 600;
  margin-left: 4px;
}

.tab-button.active .tab-badge {
  background: #2d3a7f;
}

.empty-state {
  text-align: center;
  padding: 60px 24px;
  color: #9ca3af;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.empty-state svg {
  width: 64px;
  height: 64px;
  margin-bottom: 8px;
  opacity: 0.5;
}

.empty-state h3 {
  font-size: 1.125rem;
  color: #6b7280;
  margin: 0;
}

.empty-state p {
  margin: 0;
  font-size: 0.875rem;
  color: #9ca3af;
}

/* Responsividade */
@media (max-width: 1024px) {
  .content-area {
    margin-left: 0;
  }
}

@media (max-width: 768px) {
  .historico-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .historico-actions {
    width: 100%;
  }

  .historico-actions .btn-clear {
    width: 100%;
    justify-content: center;
  }
  .content-area {
    margin-left: 0;
    padding: 16px;
  }

  .view-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-resumo {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .historico-stats {
    grid-template-columns: 1fr;
  }

  .cotacao-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .cotacao-valor {
    align-items: flex-start;
  }

  .detail-row {
    grid-template-columns: 1fr;
  }

  .info-grid,
  .info-grid-enhanced {
    grid-template-columns: 1fr;
  }

  .resumo-separator {
    display: none;
  }

  .tabs-container {
    padding: 12px 16px 0;
    gap: 4px;
  }

  .tab-button {
    padding: 10px 14px;
    font-size: 0.8125rem;
  }
}

@media (max-width: 480px) {
  .content-area {
    padding: 12px;
  }

  .breadcrumb {
    flex-wrap: wrap;
    gap: 8px;
    font-size: 0.8125rem;
  }

  .btn-voltar {
    padding: 6px 10px;
    font-size: 0.8125rem;
  }

  .view-header {
    padding: 18px 16px;
    border-radius: 12px 12px 0 0;
  }

  .view-title {
    font-size: 1.25rem;
    word-break: break-word;
  }

  .view-subtitle {
    font-size: 0.8125rem;
  }

  .header-resumo {
    margin-top: 10px;
    padding-top: 10px;
  }

  .resumo-item {
    font-size: 0.8125rem;
  }

  .section-card {
    padding: 20px 16px;
  }

  .section-title {
    font-size: 1rem;
    margin-bottom: 16px;
  }

  .info-card {
    padding: 12px;
  }

  .info-card-icon {
    width: 36px;
    height: 36px;
  }

  .info-card-icon svg {
    width: 18px;
    height: 18px;
  }

  .info-card-label {
    font-size: 0.625rem;
  }

  .info-card-value {
    font-size: 0.875rem;
  }

  .stat-card {
    padding: 14px;
  }

  .stat-icon {
    width: 44px;
    height: 44px;
  }

  .stat-icon svg {
    width: 20px;
    height: 20px;
  }

  .stat-value {
    font-size: 1.25rem;
  }

  .stat-label {
    font-size: 0.8125rem;
  }

  .tabs-container {
    padding: 10px 12px 0;
    overflow-x: auto;
  }

  .tab-button {
    padding: 8px 12px;
    font-size: 0.75rem;
    white-space: nowrap;
  }

  .tab-badge {
    min-width: 18px;
    height: 18px;
    font-size: 0.65rem;
  }

  .cotacao-card {
    border-radius: 10px;
  }

  .cotacao-header-padrao {
    flex-direction: column;
    align-items: flex-start;
    padding: 14px 16px;
    gap: 12px;
  }

  .fornecedor-nome {
    font-size: 1rem;
  }

  .preco-destaque-box {
    width: 100%;
    align-items: flex-start;
    padding: 10px 14px;
  }

  .preco-valor-grande {
    font-size: 1.25rem;
  }

  .cotacao-body {
    padding: 16px;
    gap: 14px;
  }

  .cotacao-info-grid {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .info-box {
    padding: 12px;
  }

  .cotacao-itens-section {
    padding: 12px;
  }

  .btn-itens-detalhes {
    width: 100%;
    text-align: center;
    padding: 8px 10px;
  }

  .item-status-card {
    padding: 10px;
    gap: 8px;
  }

  .item-status-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .item-status-metricas {
    grid-template-columns: 1fr;
  }

  .item-chip {
    padding: 5px 10px;
    font-size: 0.75rem;
  }

  .cotacao-actions {
    padding: 14px 16px;
  }

  .btn-pdf-primary {
    padding: 8px 14px;
    font-size: 0.8125rem;
  }

  .pdf-viewer-wrapper {
    height: 350px;
  }
}

/* Telas muito pequenas (380px e menor) */
@media (max-width: 380px) {
  .content-area {
    padding: 8px;
  }

  .breadcrumb {
    gap: 6px;
    margin-bottom: 16px;
    font-size: 0.75rem;
  }

  .btn-voltar {
    padding: 6px 8px;
    font-size: 0.75rem;
    gap: 4px;
  }

  .btn-voltar svg {
    width: 14px;
    height: 14px;
  }

  .breadcrumb-link,
  .breadcrumb-current {
    font-size: 0.75rem;
  }

  .breadcrumb-current {
    display: none;
  }

  .view-container {
    max-width: 100%;
  }

  .view-header {
    padding: 14px 12px;
    border-radius: 10px 10px 0 0;
  }

  .view-title {
    font-size: 1rem;
    line-height: 1.3;
    word-break: break-word;
    overflow-wrap: break-word;
  }

  .view-subtitle {
    font-size: 0.7rem;
    line-height: 1.3;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .header-actions {
    gap: 8px;
  }

  .status-badge {
    padding: 4px 8px;
    font-size: 0.65rem;
  }

  .header-resumo {
    margin-top: 8px;
    padding-top: 8px;
    gap: 6px;
  }

  .resumo-item {
    font-size: 0.7rem;
    gap: 4px;
  }

  .resumo-icon {
    width: 14px;
    height: 14px;
  }

  .total-valor {
    font-size: 0.75rem;
  }

  /* Tabs */
  .tabs-container {
    padding: 8px 8px 0;
    gap: 4px;
    flex-wrap: nowrap;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }

  .tab-button {
    padding: 8px 10px;
    font-size: 0.7rem;
    gap: 4px;
    flex-shrink: 0;
  }

  .tab-button svg {
    width: 14px;
    height: 14px;
  }

  .tab-badge {
    min-width: 16px;
    height: 16px;
    font-size: 0.6rem;
    padding: 0 4px;
  }

  /* Section Card */
  .section-card {
    padding: 14px 10px;
  }

  .section-card:last-child {
    border-radius: 0 0 10px 10px;
    padding-bottom: 16px;
  }

  .section-title {
    font-size: 0.875rem;
    margin-bottom: 12px;
    padding-bottom: 8px;
  }

  .section-title::after {
    width: 40px;
    height: 2px;
  }

  /* Info Cards */
  .info-grid-enhanced {
    gap: 10px;
  }

  .info-card {
    padding: 10px;
    gap: 10px;
    border-radius: 6px;
  }

  .info-card-icon {
    width: 32px;
    height: 32px;
    border-radius: 6px;
  }

  .info-card-icon svg {
    width: 16px;
    height: 16px;
  }

  .info-card-label {
    font-size: 0.5625rem;
    letter-spacing: 0.3px;
  }

  .info-card-value {
    font-size: 0.8125rem;
    word-break: break-word;
  }

  .info-card.full-width .info-card-value {
    font-size: 0.75rem;
    line-height: 1.5;
  }

  /* Histórico Stats */
  .historico-stats {
    gap: 10px;
    margin-bottom: 20px;
  }

  .stat-card {
    padding: 12px;
    gap: 12px;
    border-radius: 8px;
  }

  .stat-icon {
    width: 40px;
    height: 40px;
    border-radius: 8px;
  }

  .stat-icon svg {
    width: 18px;
    height: 18px;
  }

  .stat-value {
    font-size: 1.125rem;
  }

  .stat-label {
    font-size: 0.75rem;
  }

  /* Cotação Card */
  .historico-lista {
    gap: 16px;
  }

  .cotacao-card {
    border-radius: 8px;
  }

  .cotacao-header-padrao {
    padding: 12px;
    gap: 10px;
  }

  .fornecedor-linha {
    gap: 8px;
  }

  .fornecedor-nome {
    font-size: 0.875rem;
    line-height: 1.3;
  }

  .tipo-tag {
    padding: 3px 6px;
    font-size: 0.6rem;
    border-radius: 3px;
  }

  .preco-destaque-box {
    padding: 8px 10px;
    border-radius: 6px;
  }

  .preco-label-small {
    font-size: 0.55rem;
  }

  .preco-valor-grande {
    font-size: 1.125rem;
  }

  .cotacao-body {
    padding: 12px;
    gap: 12px;
  }

  .info-box {
    padding: 10px;
    gap: 10px;
    border-radius: 6px;
  }

  .info-box-icon {
    width: 16px;
    height: 16px;
  }

  .info-box-label {
    font-size: 0.5625rem;
  }

  .info-box-value {
    font-size: 0.8125rem;
  }

  .cotacao-itens-section {
    padding: 10px;
    border-radius: 6px;
  }

  .itens-section-title {
    font-size: 0.65rem;
    margin-bottom: 8px;
    padding-bottom: 6px;
  }

  .item-chip {
    padding: 4px 8px;
    font-size: 0.7rem;
    border-radius: 12px;
  }

  .btn-itens-detalhes {
    font-size: 0.6875rem;
    padding: 7px 8px;
  }

  .item-status-card {
    padding: 8px;
    border-radius: 6px;
  }

  .item-status-nome {
    font-size: 0.8125rem;
  }

  .item-status-codigo {
    font-size: 0.6875rem;
  }

  .item-status-metrica {
    padding: 6px 8px;
  }

  .metrica-label {
    font-size: 0.5625rem;
  }

  .metrica-valor {
    font-size: 0.75rem;
  }

  .item-status-detalhe,
  .itens-sem-detalhe {
    font-size: 0.75rem;
  }

  .observacoes-section {
    padding: 10px;
    border-radius: 6px;
  }

  .observacao-content p {
    font-size: 0.75rem;
  }

  .cotacao-actions {
    padding: 10px 12px;
  }

  .pdf-buttons {
    gap: 6px;
    width: 100%;
  }

  .btn-pdf-primary {
    padding: 8px 12px;
    font-size: 0.75rem;
    gap: 6px;
    flex: 1;
    justify-content: center;
    border-radius: 6px;
  }

  .btn-pdf-primary .btn-icon {
    width: 14px;
    height: 14px;
  }

  .pdf-viewer-container {
    margin-top: 12px;
    border-radius: 6px;
  }

  .pdf-viewer-wrapper {
    height: 280px;
  }

  .pdf-loading {
    padding: 30px;
    font-size: 0.8125rem;
    gap: 10px;
  }

  /* Empty State */
  .empty-state {
    padding: 40px 16px;
    gap: 12px;
  }

  .empty-state svg {
    width: 48px;
    height: 48px;
  }

  .empty-state h3 {
    font-size: 1rem;
  }

  .empty-state p {
    font-size: 0.8125rem;
  }

  /* Loading */
  .loading-container {
    padding: 40px 16px;
    gap: 12px;
  }

  .loading-spinner {
    width: 28px;
    height: 28px;
  }

  .loading-message {
    padding: 30px 16px;
    font-size: 0.8125rem;
  }

  .loading-spinner-small {
    width: 24px;
    height: 24px;
  }

  /* Error Container */
  .error-container svg {
    width: 48px;
    height: 48px;
  }

  .error-container h3 {
    font-size: 1rem;
  }

  .error-container p {
    font-size: 0.8125rem;
  }

  .btn-primary {
    padding: 10px 16px;
    font-size: 0.8125rem;
    width: 100%;
  }
}

/* Filters Bar */
.filters-bar {
  display: flex;
  gap: 20px;
  align-items: flex-end;
  background: white;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.historico-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.historico-title {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.section-subtitle {
  font-size: 0.875rem;
  color: #6b7280;
  margin: 0;
}

.historico-actions {
  display: flex;
  align-items: center;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.search-group {
  flex: 1;
  min-width: 200px;
}

.filter-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
}

.search-input-wrapper {
  position: relative;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
}

.filter-input {
  width: 100%;
  padding: 10px 12px 10px 36px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.filter-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.date-group {
  flex: 2;
  min-width: 300px;
}

.date-inputs {
  display: flex;
  gap: 12px;
  align-items: center;
}

.date-input-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.date-label-small {
  font-size: 0.8125rem;
  color: #6b7280;
  white-space: nowrap;
}

.date-input {
  padding: 10px 12px;
  cursor: pointer;
}

.filter-actions {
  display: flex;
  align-items: flex-end;
  padding-bottom: 2px;
}

.btn-clear {
  padding: 8px 16px;
  font-size: 0.875rem;
  color: #6b7280;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

/* hover definido acima */

.btn-clear:active {
  transform: translateY(1px);
}

.btn-clear:hover {
  background: #e5e7eb;
  color: #374151;
}

@media (max-width: 768px) {
  .filters-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .date-inputs {
    flex-direction: column;
    align-items: stretch;
  }
}

/* Pagination */
.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.pagination-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s;
}

.pagination-btn:hover:not(:disabled) {
  background: #f3f4f6;
  border-color: #9ca3af;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #f9fafb;
}

.pagination-info {
  font-size: 0.875rem;
  color: #6b7280;
}

.pagination-info strong {
  color: #111827;
}

/* Enhanced Card Separation */
.cotacao-card {
  border-left: 4px solid #1F285F; /* Distinct visual accent */
  margin-bottom: 24px; /* More spacing */
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.cotacao-card:last-child {
  margin-bottom: 0;
}
</style>



