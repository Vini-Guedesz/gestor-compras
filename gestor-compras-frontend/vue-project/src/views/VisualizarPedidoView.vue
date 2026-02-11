<template>
  <div class="page-container" :class="{ 'sidebar-collapsed': isCollapsed }">
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
          <router-link to="/pedidos" class="breadcrumb-link">
            Pedidos de Compra
          </router-link>
          <span class="breadcrumb-separator">/</span>
          <span class="breadcrumb-current">{{ isRascunho ? 'Visualizar Rascunho' : 'Visualizar Pedido' }}</span>
        </div>

        <!-- Loading State -->
        <div v-if="isLoading" class="loading-container">
          <div class="loading-spinner"></div>
          <span>Carregando...</span>
        </div>

        <!-- Conteúdo Unificado -->
        <div v-else class="view-container">
          <!-- Header -->
          <div class="view-header">
            <!-- Status Badge no topo -->
            <div class="header-status-row">
              <span class="status-badge" :class="getStatusClass()">
                {{ getStatusLabel() }}
              </span>
            </div>

            <!-- Conteúdo e Ações na mesma linha -->
            <div class="header-main-row">
              <div class="header-content">
                <h2 class="view-title">{{ getTitulo() }}</h2>
                <p class="view-subtitle">Detalhes completos do {{ isRascunho ? 'rascunho' : 'pedido' }}</p>
                <!-- Resumo Rápido -->
                <div class="header-resumo">
                  <div class="resumo-item">
                    <svg viewBox="0 0 24 24" width="16" height="16" class="resumo-icon">
                      <path fill="currentColor" d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
                    </svg>
                    <span>{{ pedido?.itens?.length || 0 }} {{ pedido?.itens?.length === 1 ? 'item' : 'itens' }}</span>
                  </div>
                  <span class="resumo-separator">•</span>
                  <div class="resumo-item">
                    <svg viewBox="0 0 24 24" width="16" height="16" class="resumo-icon">
                      <path fill="currentColor" d="M9 11H7v2h2v-2zm4 0h-2v2h2v-2zm4 0h-2v2h2v-2zm2-7h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 1.99 2H19c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V9h14v11z"/>
                    </svg>
                    <span>{{ cotacoes.length }} {{ cotacoes.length === 1 ? 'cotação' : 'cotações' }}</span>
                  </div>
                  <span class="resumo-separator" v-if="getTotalCotacoes() > 0">•</span>
                  <div class="resumo-item resumo-total" v-if="getTotalCotacoes() > 0">
                    <svg viewBox="0 0 24 24" width="16" height="16" class="resumo-icon">
                      <path fill="currentColor" d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/>
                    </svg>
                    <span class="total-valor">Total: R$ {{ formatarPreco(getTotalCotacoes()) }}</span>
                  </div>
                </div>
              </div>

              <!-- Action Buttons Container alinhado com o resumo -->
              <div class="action-buttons-group">
                <!-- Ações para Rascunhos -->
                <template v-if="isRascunho">
                  <button v-if="podeDevolverParaEdicao" @click="abrirModalDevolucao" class="btn-warning">
                    Devolver para Edição
                  </button>
                  <button v-if="podeEditarRascunho" @click="editarRascunho" class="btn-success">
                    Editar Rascunho
                  </button>
                </template>

                <!-- Ações para Pedidos -->
                <template v-if="!isRascunho">
                  <!-- EM_NEGOCIACAO ou PENDENTE (legado): Ação primária + secundária -->
                  <template v-if="pedido?.status === 'EM_NEGOCIACAO' || pedido?.status === 'PENDENTE'">
                    <button
                      v-if="permissions.canEnviarPedidoAprovacao"
                      @click="abrirModalEnviarAprovacao"
                      class="btn-action btn-action-primary"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M22 2L11 13"></path>
                        <path d="M22 2L15 22L11 13L2 9L22 2Z"></path>
                      </svg>
                      Enviar para Aprovação
                    </button>
                    <button
                      v-if="permissions.canEnviarPedidoAprovacao"
                      @click="abrirModalCancelar"
                      class="btn-action btn-action-secondary"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="15" y1="9" x2="9" y2="15"></line>
                        <line x1="9" y1="9" x2="15" y2="15"></line>
                      </svg>
                      Cancelar
                    </button>
                  </template>

                  <!-- PENDENTE_APROVACAO: Ações de aprovação -->
                  <template v-if="pedido?.status === 'PENDENTE_APROVACAO'">
                    <button
                      v-if="permissions.canAprovarPedido"
                      @click="abrirModalAprovar"
                      class="btn-action btn-action-success"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                        <polyline points="22 4 12 14.01 9 11.01"></polyline>
                      </svg>
                      Aprovar Pedido
                    </button>
                    <button
                      v-if="permissions.canDevolverPedido"
                      @click="abrirModalDevolverPedido"
                      class="btn-action btn-action-warning"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="9 14 4 9 9 4"></polyline>
                        <path d="M20 20v-7a4 4 0 0 0-4-4H4"></path>
                      </svg>
                      Devolver
                    </button>
                    <button
                      v-if="permissions.canCancelarPedido"
                      @click="abrirModalCancelar"
                      class="btn-action btn-action-secondary"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="15" y1="9" x2="9" y2="15"></line>
                        <line x1="9" y1="9" x2="15" y2="15"></line>
                      </svg>
                      Cancelar
                    </button>
                  </template>
                </template>
              </div>
            </div>
          </div>

          <!-- Seção: Informações Gerais -->
          <div class="section-card">
            <h3 class="section-title">Informações Gerais</h3>
            <div class="info-grid-enhanced">
              <div class="info-card">
                <div class="info-card-icon" style="background: #dbeafe;">
                  <svg viewBox="0 0 20 20" fill="#2563eb">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clip-rule="evenodd"/>
                  </svg>
                </div>
                <div class="info-card-content">
                  <span class="info-card-label">ID do Pedido</span>
                  <span class="info-card-value">#{{ isRascunho ? pedido?.rascunhoId : pedido?.id }}</span>
                </div>
              </div>
              <div class="info-card">
                <div class="info-card-icon" style="background: #fef3c7;">
                  <svg viewBox="0 0 20 20" fill="#d97706">
                    <path fill-rule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clip-rule="evenodd"/>
                  </svg>
                </div>
                <div class="info-card-content">
                  <span class="info-card-label">Data de Criação</span>
                  <span class="info-card-value">{{ formatarData(pedido?.dataCriacao) }}</span>
                </div>
              </div>
              <div class="info-card" v-if="pedido?.status">
                <div class="info-card-icon" style="background: #e0e7ff;">
                  <svg viewBox="0 0 20 20" fill="#6366f1">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                  </svg>
                </div>
                <div class="info-card-content">
                  <span class="info-card-label">Status</span>
                  <span class="info-card-value">{{ getStatusLabel() }}</span>
                </div>
              </div>
            </div>
            <div v-if="pedido?.objetivo" class="observacao-box" style="background: #eff6ff; border-color: #3b82f6;">
              <div class="observacao-header" style="color: #1e40af;">
                <svg class="observacao-icon" viewBox="0 0 20 20" fill="currentColor">
                  <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z"/>
                  <path fill-rule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clip-rule="evenodd"/>
                </svg>
                <span>Objetivo do Pedido</span>
              </div>
              <p class="observacao-text">{{ pedido.objetivo }}</p>
            </div>
            <div v-if="pedido?.observacao" class="observacao-box">
              <div class="observacao-header">
                <svg class="observacao-icon" viewBox="0 0 20 20" fill="currentColor">
                  <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/>
                </svg>
                <span>Observações Adicionais</span>
              </div>
              <p class="observacao-text">{{ pedido.observacao }}</p>
            </div>
          </div>

          <!-- Seção: Itens do Pedido -->
          <div class="section-card">
            <h3 class="section-title">Itens do Pedido</h3>
            <div class="itens-lista">
              <div v-for="(item, index) in pedido?.itens" :key="item.id" class="item-card">
                <div class="item-header">
                  <span class="item-numero">Item {{ index + 1 }}</span>
                  <span class="item-status" :class="{ 'cotado': itemTemCotacao(item) }">
                    <svg v-if="itemTemCotacao(item)" class="status-icon" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/>
                    </svg>
                    {{ itemTemCotacao(item) ? 'Cotado' : 'Sem cotação' }}
                  </span>
                </div>
                <div class="item-body">
                  <div class="item-nome">{{ item.nome }}</div>
                  <div class="item-detalhes">
                    <span>Quantidade: {{ item.quantidade }}</span>
                    <span v-if="item.descricao">{{ item.descricao }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-if="!pedido?.itens?.length" class="empty-state-small">
              <p>Nenhum item cadastrado</p>
            </div>
          </div>

          <!-- Seção: Cotações -->
          <div class="section-card">
            <h3 class="section-title">Cotações</h3>
            <div v-if="cotacoes.length > 0" class="cotacoes-lista">
              <div v-for="cotacao in cotacoes" :key="cotacao.id" class="cotacao-card">
                <!-- Header padronizado -->
                <div class="cotacao-header-padrao">
                  <div class="cotacao-info-principal">
                    <div class="fornecedor-linha">
                      <strong class="fornecedor-nome">{{ cotacao.nomeFornecedor || cotacao.fornecedorNome || 'Fornecedor' }}</strong>
                      <span class="tipo-tag">{{ cotacao.tipoFornecedor }}</span>
                      <span v-if="cotacao.foiEditada" class="badge-editada" title="Esta cotação foi editada">
                        <svg viewBox="0 0 20 20" width="14" height="14" fill="currentColor">
                          <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
                        </svg>
                        EDITADA
                      </span>
                    </div>
                  </div>
                  <div class="preco-destaque-box">
                    <span class="preco-label-small">VALOR TOTAL</span>
                    <span class="preco-valor-grande">R$ {{ formatarPreco(cotacao.preco) }}</span>
                  </div>
                </div>

                <div class="cotacao-body">
                  <!-- Grid de informações: Prazo e Validade -->
                  <div class="cotacao-info-grid">
                    <div v-if="cotacao.prazoEmDiasUteis" class="info-box">
                      <svg class="info-box-icon" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clip-rule="evenodd"/>
                      </svg>
                      <div class="info-box-content">
                        <span class="info-box-label">Prazo</span>
                        <span class="info-box-value">{{ cotacao.prazoEmDiasUteis }} dias úteis</span>
                      </div>
                    </div>
                    <div v-if="cotacao.dataLimite" class="info-box">
                      <svg class="info-box-icon" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clip-rule="evenodd"/>
                      </svg>
                      <div class="info-box-content">
                        <span class="info-box-label">Validade</span>
                        <span class="info-box-value">{{ formatarData(cotacao.dataLimite) }}</span>
                      </div>
                    </div>
                    <div class="info-box">
                      <svg class="info-box-icon" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M4 4a2 2 0 00-2 2v4a2 2 0 002 2V6h10a2 2 0 00-2-2H4zm2 6a2 2 0 012-2h8a2 2 0 012 2v4a2 2 0 01-2 2H8a2 2 0 01-2-2v-4zm6 4a2 2 0 100-4 2 2 0 000 4z" clip-rule="evenodd"/>
                      </svg>
                      <div class="info-box-content">
                        <span class="info-box-label">Gasto Previsto</span>
                        <span class="info-box-value">{{ cotacao.gastoPrevisto ? 'Sim' : 'Não' }}</span>
                      </div>
                    </div>
                    <div v-if="cotacao.gastoPrevisto && cotacao.projeto" class="info-box">
                      <svg class="info-box-icon" viewBox="0 0 20 20" fill="currentColor">
                        <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z"/>
                        <path fill-rule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clip-rule="evenodd"/>
                      </svg>
                      <div class="info-box-content">
                        <span class="info-box-label">Projeto</span>
                        <span class="info-box-value">{{ cotacao.projeto }}</span>
                      </div>
                    </div>
                  </div>

                  <!-- Seção de Contato -->
                  <div v-if="cotacao.fornecedorCompleto?.contato" class="cotacao-contato-section">
                    <h5 class="contato-section-title">Contato</h5>
                    <div class="contato-grid">
                      <a
                        v-if="cotacao.fornecedorCompleto.contato.telefone"
                        :href="`tel:${cotacao.fornecedorCompleto.contato.telefone}`"
                        class="contato-item-new"
                      >
                        <svg class="contato-item-icon" viewBox="0 0 20 20" fill="currentColor">
                          <path d="M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V17a1 1 0 01-1 1h-2C7.82 18 2 12.18 2 5V3z"/>
                        </svg>
                        <span>{{ formatarTelefone(cotacao.fornecedorCompleto.contato.telefone) }}</span>
                      </a>
                      <a
                        v-if="cotacao.fornecedorCompleto.contato.celular"
                        :href="`tel:${cotacao.fornecedorCompleto.contato.celular}`"
                        class="contato-item-new"
                      >
                        <svg class="contato-item-icon" viewBox="0 0 20 20" fill="currentColor">
                          <path d="M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V17a1 1 0 01-1 1h-2C7.82 18 2 12.18 2 5V3z"/>
                        </svg>
                        <span>{{ formatarTelefone(cotacao.fornecedorCompleto.contato.celular) }}</span>
                      </a>
                      <a
                        v-if="cotacao.fornecedorCompleto.contato.email"
                        :href="`mailto:${cotacao.fornecedorCompleto.contato.email}`"
                        class="contato-item-new contato-email"
                        :title="cotacao.fornecedorCompleto.contato.email"
                      >
                        <svg class="contato-item-icon" viewBox="0 0 20 20" fill="currentColor">
                          <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/>
                          <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/>
                        </svg>
                        <span class="email-text">{{ cotacao.fornecedorCompleto.contato.email }}</span>
                      </a>
                    </div>
                  </div>

                  <!-- Itens -->
                  <div class="cotacao-itens-section">
                    <h5 class="itens-section-title">Itens Contemplados</h5>
                    <div class="itens-chips">
                      <span
                        v-for="itemId in getItensIdsDaCotacao(cotacao)"
                        :key="itemId"
                        class="item-chip"
                      >
                        {{ getNomeItem(itemId) }}
                      </span>
                    </div>
                  </div>
                </div>

                <div class="cotacao-actions">
                  <!-- Botões de PDF (todos os anexos) -->
                  <div class="pdf-buttons" v-if="cotacao.temAnexoPdf || cotacao.quantidadeAnexos > 0">
                    <button
                      v-for="indexPdf in cotacao.quantidadeAnexos || 1"
                      :key="`pdf-${cotacao.id}-${indexPdf - 1}`"
                      @click="togglePdfViewer(cotacao, indexPdf - 1)"
                      class="btn-pdf-primary"
                      :class="{ 'btn-pdf-primary-active': pdfAberto === `${cotacao.id}-${indexPdf - 1}`, 'novo-anexo': cotacao.foiEditada }"
                    >
                      <svg class="btn-icon" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4zm2 6a1 1 0 011-1h6a1 1 0 110 2H7a1 1 0 01-1-1zm1 3a1 1 0 100 2h6a1 1 0 100-2H7z" clip-rule="evenodd"/>
                      </svg>
                      {{ pdfAberto === `${cotacao.id}-${indexPdf - 1}` ? 'Fechar' : 'Ver' }} PDF {{ cotacao.quantidadeAnexos > 1 ? indexPdf : '' }}
                      <span v-if="cotacao.foiEditada && indexPdf === cotacao.quantidadeAnexos" class="badge-atualizado">Atualizado</span>
                    </button>
                  </div>

                  <!-- Botões de Edição e Histórico (apenas para pedidos em negociação) -->
                  <div v-if="!isRascunho" class="cotacao-edit-buttons">
                    <button
                      v-if="permissions.canEditCotacao && pedido?.status === 'EM_NEGOCIACAO'"
                      @click="abrirModalEditarCotacao(cotacao)"
                      class="btn-edit-cotacao"
                      title="Editar cotação"
                    >
                      <svg viewBox="0 0 20 20" width="14" height="14" fill="currentColor">
                        <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
                      </svg>
                      Editar
                    </button>
                    <button
                      v-if="cotacao.foiEditada"
                      @click="abrirModalHistorico(cotacao)"
                      class="btn-history-cotacao"
                      title="Ver histórico de edições"
                    >
                      <svg viewBox="0 0 20 20" width="14" height="14" fill="currentColor">
                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clip-rule="evenodd"/>
                      </svg>
                      Histórico
                    </button>
                  </div>

                  <!-- Botão de Excluir (apenas para rascunhos não finalizados) -->
                  <button
                    v-if="isRascunho && !isFinalizado"
                    @click="excluirCotacao(cotacao)"
                    class="btn-delete-cotacao"
                    title="Excluir cotação"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <polyline points="3 6 5 6 21 6"></polyline>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                      <line x1="10" y1="11" x2="10" y2="17"></line>
                      <line x1="14" y1="11" x2="14" y2="17"></line>
                    </svg>
                  </button>
                </div>

                <!-- PDF Viewer -->
                <div v-if="pdfAberto && pdfAberto.startsWith(`${cotacao.id}-`)" class="pdf-viewer-container">
                  <div v-if="carregandoPdf" class="pdf-loading">
                    <div class="loading-spinner"></div>
                    <span>Carregando PDF...</span>
                  </div>
                  <div v-else-if="pdfUrl" class="pdf-viewer-wrapper">
                    <iframe :src="pdfUrl" class="pdf-iframe" frameborder="0"></iframe>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="empty-state-small">
              <p>Nenhuma cotação registrada</p>
            </div>
          </div>

          <!-- Seção: Histórico -->
          <div class="section-card">
            <div class="section-header-with-badge">
              <h3 class="section-title">📜 Histórico de Modificações</h3>
              <span v-if="historico.length > 0" class="count-badge">{{ historico.length }}</span>
            </div>
            <div v-if="historico.length > 0" class="historico-lista-melhorada">
              <div v-for="(registro, index) in historico" :key="registro.id" class="historico-item-melhorado">
                <!-- Timeline connector -->
                <div class="timeline-connector" v-if="index < historico.length - 1"></div>

                <div class="historico-icon-melhorado" :style="{ background: getTipoModificacaoColor(registro.tipoAcao || registro.tipoModificacao) }">
                  <svg viewBox="0 0 24 24" width="18" height="18">
                    <path fill="white" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                  </svg>
                </div>

                <div class="historico-content-melhorado">
                  <div class="historico-card">
                    <div class="historico-header-melhorado">
                      <div class="tipo-badge" :style="{
                        background: getTipoModificacaoColor(registro.tipoAcao || registro.tipoModificacao) + '20',
                        color: getTipoModificacaoColor(registro.tipoAcao || registro.tipoModificacao),
                        border: '1px solid ' + getTipoModificacaoColor(registro.tipoAcao || registro.tipoModificacao)
                      }">
                        {{ getTipoModificacaoLabel(registro.tipoAcao || registro.tipoModificacao) }}
                      </div>
                      <span class="historico-data-melhorada">{{ formatarDataHora(registro.dataModificacao) }}</span>
                    </div>

                    <div class="historico-usuario-melhorado" v-if="registro.nomeUsuario">
                      <svg class="usuario-icon" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"/>
                      </svg>
                      <span>{{ registro.nomeUsuario }}</span>
                    </div>

                    <div class="historico-descricao-melhorada" v-if="registro.descricao">
                      <div class="descricao-icon">
                        <svg viewBox="0 0 20 20" width="16" height="16" fill="currentColor">
                          <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/>
                        </svg>
                      </div>
                      <span>{{ registro.descricao }}</span>
                    </div>

                    <!-- Informações adicionais para itens -->
                    <div v-if="registro.nomeItem" class="historico-detalhes">
                      <div class="detalhe-item">
                        <strong>Item:</strong> {{ registro.nomeItem }}
                        <span v-if="registro.numeroItem" class="numero-item-badge">#{{ registro.numeroItem }}</span>
                      </div>
                    </div>

                    <!-- Detalhes extras se houver -->
                    <div v-if="registro.detalhes" class="historico-detalhes-extras">
                      {{ registro.detalhes }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="empty-state-small">
              <svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
              </svg>
              <p>Nenhum registro de histórico</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modais de Cotação -->
    <EditCotacaoModal
      :show="modalEditarCotacaoAberto"
      :cotacao="cotacaoSelecionadaParaEdicao || {}"
      @close="fecharModalEditarCotacao"
      @save="salvarEdicaoCotacao"
    />

    <HistoricoCotacaoModal
      :show="modalHistoricoAberto"
      :cotacao="cotacaoSelecionadaParaEdicao || {}"
      @close="fecharModalHistorico"
    />

    <!-- Modal Confirmação Antes de Devolver (quando há cotações) -->
    <div v-if="modalConfirmacaoDevolucaoAberto" class="modal-overlay" @click="fecharModalConfirmacaoDevolucao">
      <div class="modal-container-small" @click.stop>
        <div class="modal-header">
          <h3>⚠️ Atenção</h3>
          <button @click="fecharModalConfirmacaoDevolucao" class="btn-close">×</button>
        </div>
        <div class="modal-body">
          <p class="modal-description">
            <strong>ATENÇÃO:</strong> Este rascunho possui <strong>{{ quantidadeCotacoesParaDevolucao }} cotação(ões)</strong>.
            <br><br>
            Ao devolver para edição, <strong>TODAS as cotações serão REMOVIDAS permanentemente</strong>.
            <br><br>
            Deseja continuar?
          </p>
        </div>
        <div class="modal-footer">
          <button @click="fecharModalConfirmacaoDevolucao" class="btn-cancel">Cancelar</button>
          <button @click="confirmarAbrirDevolucao" class="btn-warning">
            OK
          </button>
        </div>
      </div>
    </div>

    <!-- Modal Devolução Rascunho para Edição -->
    <div v-if="modalDevolucaoAberto" class="modal-overlay" @click="fecharModalDevolucao">
      <div class="modal-container-small" @click.stop>
        <div class="modal-header">
          <h3>Devolver Rascunho para Edição</h3>
          <button @click="fecharModalDevolucao" class="btn-close">×</button>
        </div>
        <div class="modal-body">
          <p class="modal-description">
            ⚠️ Ao devolver o rascunho para edição, o status voltará para ATIVO e o criador poderá fazer alterações novamente.
            <br><br>
            <strong>ATENÇÃO:</strong> Todas as cotações existentes serão <strong>removidas permanentemente</strong> para evitar inconsistências com possíveis edições nos itens.
          </p>
          <div class="form-group">
            <label class="form-label">Motivo da Devolução *</label>
            <textarea
              v-model="motivoDevolucao"
              class="form-textarea"
              rows="4"
              placeholder="Descreva o motivo da devolução (mínimo 10 caracteres)..."
              maxlength="500"
              required
            ></textarea>
            <small class="form-hint">{{ motivoDevolucao.length }}/500 caracteres</small>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="fecharModalDevolucao" class="btn-cancel">Cancelar</button>
          <button
            @click="confirmarDevolucao"
            class="btn-warning"
            :disabled="motivoDevolucao.length < 10 || devolvendo"
          >
            {{ devolvendo ? 'Devolvendo...' : 'Devolver para Edição' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Modal Enviar para Aprovação -->
    <div v-if="modalEnviarAprovacaoAberto" class="modal-overlay" @click="fecharModalEnviarAprovacao">
      <div class="modal-container-small" @click.stop>
        <div class="modal-header">
          <h3>Enviar Pedido para Aprovação</h3>
          <button @click="fecharModalEnviarAprovacao" class="btn-close">×</button>
        </div>
        <div class="modal-body">
          <p class="modal-description">
            📋 Ao enviar o pedido para aprovação, você não poderá mais editá-lo.<br>
            O aprovador poderá aprovar, devolver para edição ou cancelar.
          </p>
          <div class="form-group">
            <label class="form-label">Observações (opcional)</label>
            <textarea
              v-model="observacaoPedido"
              class="form-textarea"
              rows="3"
              placeholder="Adicione observações sobre este envio (opcional)..."
              maxlength="1000"
            ></textarea>
            <small class="form-hint">{{ observacaoPedido.length }}/1000 caracteres</small>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="fecharModalEnviarAprovacao" class="btn-cancel">Cancelar</button>
          <button @click="confirmarEnviarAprovacao" class="btn-primary" :disabled="processando">
            {{ processando ? 'Enviando...' : 'Enviar para Aprovação' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Modal Aprovar Pedido -->
    <div v-if="modalAprovarAberto" class="modal-overlay" @click="fecharModalAprovar">
      <div class="modal-container-small" @click.stop>
        <div class="modal-header">
          <h3>Aprovar Pedido</h3>
          <button @click="fecharModalAprovar" class="btn-close">×</button>
        </div>
        <div class="modal-body">
          <p class="modal-description">
            ✅ Ao aprovar este pedido, ele será marcado como <strong>APROVADO</strong> e não poderá mais ser alterado.
          </p>
          <div class="form-group">
            <label class="form-label">Observações (opcional)</label>
            <textarea
              v-model="observacaoPedido"
              class="form-textarea"
              rows="3"
              placeholder="Adicione observações sobre a aprovação (opcional)..."
              maxlength="1000"
            ></textarea>
            <small class="form-hint">{{ observacaoPedido.length }}/1000 caracteres</small>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="fecharModalAprovar" class="btn-cancel">Cancelar</button>
          <button @click="confirmarAprovar" class="btn-success" :disabled="processando">
            {{ processando ? 'Aprovando...' : 'Aprovar Pedido' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Modal Devolver Pedido para Edição -->
    <div v-if="modalDevolverPedidoAberto" class="modal-overlay" @click="fecharModalDevolverPedido">
      <div class="modal-container-small" @click.stop>
        <div class="modal-header">
          <h3>Devolver Pedido para Edição</h3>
          <button @click="fecharModalDevolverPedido" class="btn-close">×</button>
        </div>
        <div class="modal-body">
          <p class="modal-description">
            ⚠️ Ao devolver o pedido para edição, o status voltará para <strong>EM_NEGOCIACAO</strong> e o comprador poderá fazer ajustes.
          </p>
          <div class="form-group">
            <label class="form-label">Motivo da Devolução *</label>
            <textarea
              v-model="motivoDevolucaoPedido"
              class="form-textarea"
              rows="4"
              placeholder="Descreva o motivo da devolução (mínimo 10 caracteres)..."
              maxlength="1000"
              required
            ></textarea>
            <small class="form-hint">{{ motivoDevolucaoPedido.length }}/1000 caracteres</small>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="fecharModalDevolverPedido" class="btn-cancel">Cancelar</button>
          <button
            @click="confirmarDevolverPedido"
            class="btn-warning"
            :disabled="motivoDevolucaoPedido.length < 10 || processando"
          >
            {{ processando ? 'Devolvendo...' : 'Devolver para Edição' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Modal Cancelar Pedido -->
    <div v-if="modalCancelarAberto" class="modal-overlay" @click="fecharModalCancelar">
      <div class="modal-container-small" @click.stop>
        <div class="modal-header">
          <h3>Cancelar Pedido</h3>
          <button @click="fecharModalCancelar" class="btn-close">×</button>
        </div>
        <div class="modal-body">
          <p class="modal-description">
            ❌ Ao cancelar este pedido, ele será marcado como <strong>CANCELADO</strong> permanentemente.<br>
            <strong>Esta ação não pode ser desfeita!</strong>
          </p>
          <div class="form-group">
            <label class="form-label">Motivo do Cancelamento (opcional)</label>
            <textarea
              v-model="observacaoPedido"
              class="form-textarea"
              rows="4"
              placeholder="Descreva o motivo do cancelamento (opcional)..."
              maxlength="1000"
            ></textarea>
            <small class="form-hint">{{ observacaoPedido.length }}/1000 caracteres</small>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="fecharModalCancelar" class="btn-cancel">Cancelar</button>
          <button @click="confirmarCancelar" class="btn-danger" :disabled="processando">
            {{ processando ? 'Cancelando...' : 'Confirmar Cancelamento' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useToast } from '@/composables/useToast.js'
import { useErrorModal } from '@/composables/useErrorModal.js'
import { usePermissions } from '@/composables/usePermissions.js'
import { useAuthStore } from '@/stores/auth'
import { useSidebar } from '@/composables/useSidebar'
import logger from '@/utils/logger.js'
import pedidoService from '@/services/pedidoService.js'
import rascunhoService from '@/services/rascunhoService.js'
import cotacaoRascunhoService from '@/services/cotacaoRascunhoService.js'
import cotacaoService from '@/services/cotacaoService.js'
import fornecedorService from '@/services/fornecedorService.js'
import historicoPedidoService, { tipoModificacaoConfig } from '@/services/historicoPedidoService.js'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import EditCotacaoModal from '@/features/pedidos/components/EditCotacaoModal.vue'
import HistoricoCotacaoModal from '@/features/pedidos/components/HistoricoCotacaoModal.vue'

export default {
  name: 'VisualizarPedidoView',
  components: {
    DashboardHeader,
    DashboardSidebar,
    EditCotacaoModal,
    HistoricoCotacaoModal
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const authStore = useAuthStore()
    const { permissions } = usePermissions()
    const { success, error: showError, warning } = useToast()
    const { isCollapsed } = useSidebar()

    // Sidebar
    const isSidebarOpen = ref(false)
    const toggleSidebar = () => { isSidebarOpen.value = !isSidebarOpen.value }
    const closeSidebar = () => { isSidebarOpen.value = false }

    // State
    const isLoading = ref(true)
    const pedido = ref(null)
    const cotacoes = ref([])
    const historico = ref([])
    const isRascunho = ref(false)
    const isFinalizado = ref(false)
    const isEditMode = ref(false)

    // Computed: Verifica se o usuário pode editar o rascunho
    const podeEditarRascunho = computed(() => {
      if (!pedido.value || !isRascunho.value || isFinalizado.value) {
        return false
      }

      // NOVA REGRA: Se tem cotações, NINGUÉM pode editar (botão desaparece para todos)
      const temCotacoes = cotacoes.value && cotacoes.value.length > 0
      if (temCotacoes) {
        return false
      }

      // Rascunhos em EM_COTACAO: apenas COMPRADOR e ADMIN podem editar
      if (pedido.value.status === 'EM_COTACAO') {
        return permissions.value.canEditRascunho
      }

      // Rascunhos ATIVO: COMPRADOR e ADMIN podem editar qualquer um
      // USUARIO pode editar apenas seus próprios rascunhos
      if (pedido.value.status === 'ATIVO') {
        if (permissions.value.canEditRascunho) {
          return true // COMPRADOR e ADMIN podem editar qualquer rascunho
        }
        // USUARIO pode editar apenas seus próprios rascunhos
        return pedido.value.usuarioId === authStore.user?.id
      }

      return false
    })

    // Computed: Verifica se pode devolver rascunho para edição
    const podeDevolverParaEdicao = computed(() => {
      if (!pedido.value || !isRascunho.value) {
        return false
      }

      // Deve estar em EM_COTACAO e ter permissão para cotar
      // NOTA: Pode ter cotações - ao devolver, as cotações serão apagadas
      return pedido.value.status === 'EM_COTACAO' && permissions.value.canCotarRascunho
    })

    // Modais de cotação
    const modalEditarCotacaoAberto = ref(false)
    const modalHistoricoAberto = ref(false)
    const cotacaoSelecionadaParaEdicao = ref(null)

    // Devolução para Edição
    const modalDevolucaoAberto = ref(false)
    const modalConfirmacaoDevolucaoAberto = ref(false)
    const quantidadeCotacoesParaDevolucao = ref(0)
    const motivoDevolucao = ref('')
    const devolvendo = ref(false)

    // PDF Viewer
    const pdfAberto = ref(null)
    const carregandoPdf = ref(false)
    const pdfUrl = ref(null)

    const getTitulo = () => {
      if (isRascunho.value) {
        return `Rascunho #${pedido.value?.rascunhoId || ''}`
      }
      return `Pedido #${pedido.value?.id || ''}`
    }

    const getStatusLabel = () => {
      const status = pedido.value?.status || 'RASCUNHO'
      const labels = {
        // Novos status do workflow
        'EM_NEGOCIACAO': 'Em Negociação',
        'PENDENTE_APROVACAO': 'Pendente de Aprovação',
        'APROVADO': 'Aprovado',
        'CANCELADO': 'Cancelado',
        // Status do backend
        'RASCUNHO': 'Rascunho',
        'ATIVO': 'Ativo',
        'EM_COTACAO': 'Em Cotação',
        'FINALIZADO': 'Rascunho Finalizado',
        'RASCUNHO_FINALIZADO': 'Rascunho Finalizado',
        'PENDENTE': 'Pendente',
        'EM_ANALISE': 'Em Análise',
        'EM_ANDAMENTO': 'Em Andamento',
        'REJEITADO': 'Rejeitado'
      }
      return labels[status] || status || 'Indefinido'
    }

    const getStatusClass = () => {
      const status = pedido.value?.status || 'RASCUNHO'
      const classes = {
        // Novos status do workflow
        'EM_NEGOCIACAO': 'status-negotiating',
        'PENDENTE_APROVACAO': 'status-pending-approval',
        'APROVADO': 'status-approved',
        'CANCELADO': 'status-canceled',
        // Status do backend
        'RASCUNHO': 'status-draft',
        'ATIVO': 'status-active',
        'EM_COTACAO': 'status-quoting',
        'FINALIZADO': 'status-draft-finished',
        'RASCUNHO_FINALIZADO': 'status-draft-finished',
        'PENDENTE': 'status-pending',
        'EM_ANALISE': 'status-progress',
        'EM_ANDAMENTO': 'status-progress',
        'REJEITADO': 'status-rejected'
      }
      return classes[status] || 'status-default'
    }

    const formatarData = (data) => {
      if (!data) return 'N/A'
      return new Date(data).toLocaleDateString('pt-BR')
    }

    const formatarDataHora = (data) => {
      if (!data) return 'N/A'
      return new Date(data).toLocaleString('pt-BR')
    }

    const formatarPreco = (preco) => {
      if (!preco) return '0,00'
      return parseFloat(preco).toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }

    const formatarTelefone = (telefone) => {
      if (!telefone) return ''
      // Remove caracteres não numéricos
      const numeros = telefone.replace(/\D/g, '')

      // Formata conforme tamanho
      if (numeros.length === 11) {
        // (XX) XXXXX-XXXX
        return `(${numeros.slice(0, 2)}) ${numeros.slice(2, 7)}-${numeros.slice(7)}`
      } else if (numeros.length === 10) {
        // (XX) XXXX-XXXX
        return `(${numeros.slice(0, 2)}) ${numeros.slice(2, 6)}-${numeros.slice(6)}`
      }
      return telefone
    }

    const getTotalCotacoes = () => {
      return cotacoes.value.reduce((total, cotacao) => {
        return total + (parseFloat(cotacao.preco) || 0)
      }, 0)
    }

    const itemTemCotacao = (item) => {
      return cotacoes.value.some(c => {
        // Para rascunhos: verificar itensRascunhoIds
        if (c.itensRascunhoIds && c.itensRascunhoIds.length > 0) {
          return c.itensRascunhoIds.includes(item.id)
        }
        // Para pedidos: verificar itensPedidoIds (array)
        if (c.itensPedidoIds && c.itensPedidoIds.length > 0) {
          return c.itensPedidoIds.includes(item.id)
        }
        // Também verificar se a cotação está diretamente no item
        if (item.cotacoes && item.cotacoes.length > 0) {
          return true
        }
        return false
      })
    }

    const getItensIdsDaCotacao = (cotacao) => {
      // ORDEM CORRIGIDA: Verificar pedidos PRIMEIRO (itens selecionados)
      // Para pedidos - array de IDs
      if (cotacao.itensPedidoIds && cotacao.itensPedidoIds.length > 0) {
        return cotacao.itensPedidoIds
      }
      // Para pedidos - ID único (compatibilidade)
      if (cotacao.itemPedidoId) {
        return [cotacao.itemPedidoId]
      }
      // Para rascunhos - SOMENTE se não houver itensPedidoIds
      if (cotacao.itensRascunhoIds && cotacao.itensRascunhoIds.length > 0) {
        return cotacao.itensRascunhoIds
      }
      return []
    }

    const getNomeItem = (itemId) => {
      const item = pedido.value?.itens?.find(i => i.id === itemId)
      return item ? item.nome : `Item #${itemId}`
    }

    const getTipoModificacaoLabel = (tipo) => {
      return tipoModificacaoConfig[tipo]?.label || tipo
    }

    const getTipoModificacaoColor = (tipo) => {
      return tipoModificacaoConfig[tipo]?.color || '#6b7280'
    }

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
        let blob
        // Usar o serviço correto baseado no tipo
        if (isRascunho.value) {
          blob = await cotacaoRascunhoService.obterAnexoPdf(
            pedido.value.rascunhoId,
            cotacao.id,
            pdfIndex
          )
        } else {
          // Para pedidos finais, usar o serviço de cotação
          blob = await cotacaoService.obterAnexoPdf(cotacao.id, pdfIndex)
        }

        if (blob && blob.size > 0) {
          pdfUrl.value = URL.createObjectURL(blob)
        } else {
          logger.warn('⚠️ PDF vazio ou não encontrado')
        }
      } catch (error) {
        logger.error('❌ Erro ao carregar PDF:', error)
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

    const carregarPedido = async () => {
      const id = route.params.id
      const tipo = route.query.tipo || 'pedido'

      try {
        isLoading.value = true

        if (tipo === 'rascunho') {
          isRascunho.value = true

          // Carregar rascunho
          const rascunhoCompleto = await rascunhoService.obterPorId(id)
          logger.info('Rascunho carregado:', rascunhoCompleto)

          // Carregar cotações (pode ser vazio se acabou de ser criado)
          const cotacoesRascunho = await cotacaoRascunhoService.listarPorRascunho(id).catch(err => {
            logger.warn('Erro ao carregar cotações do rascunho (pode não ter cotações ainda):', err)
            return []
          })

          // Carregar histórico (pode ser vazio)
          const historicoRascunho = await rascunhoService.listarHistorico(id).catch(err => {
            logger.warn('Erro ao carregar histórico do rascunho:', err)
            return []
          })

          // Carregar fornecedores para nomes
          const [fornecedoresProduto, fornecedoresServico] = await Promise.all([
            fornecedorService.listarProdutos().catch(() => []),
            fornecedorService.listarServicos().catch(() => [])
          ])

          const todosFornecedores = [...fornecedoresProduto, ...fornecedoresServico]
          const fornecedoresMap = {}
          todosFornecedores.forEach(f => {
            if (f?.id) fornecedoresMap[f.id] = f
          })

          // Mapear dados completos dos fornecedores nas cotações
          cotacoes.value = cotacoesRascunho.map(c => {
            const fornecedor = fornecedoresMap[c.fornecedorId]
            return {
              ...c,
              nomeFornecedor: fornecedor?.razaoSocial || `Fornecedor #${c.fornecedorId}`,
              fornecedorCompleto: fornecedor
            }
          })

          pedido.value = {
            ...rascunhoCompleto,
            rascunhoId: rascunhoCompleto.id,
            status: rascunhoCompleto.status || 'RASCUNHO'
          }

          // Atualizar isFinalizado baseado no status do rascunho
          isFinalizado.value = rascunhoCompleto.status === 'FINALIZADO'

          historico.value = historicoRascunho || []
        } else {
          isRascunho.value = false

          // Carregar pedido
          const pedidoCompleto = await pedidoService.obterPorId(id)
          pedido.value = pedidoCompleto

          // Carregar histórico
          const historicoPedido = await historicoPedidoService.listarPorPedido(id)
          historico.value = historicoPedido || []

          // Carregar fornecedores para informações de contato
          const [fornecedoresProduto, fornecedoresServico] = await Promise.all([
            fornecedorService.listarProdutos().catch(() => []),
            fornecedorService.listarServicos().catch(() => [])
          ])

          const todosFornecedores = [...fornecedoresProduto, ...fornecedoresServico]
          const fornecedoresMap = {}
          todosFornecedores.forEach(f => {
            if (f?.id) fornecedoresMap[f.id] = f
          })

          // Cotações vêm do objeto do pedido, enriquecer com dados completos do fornecedor
          cotacoes.value = (pedidoCompleto.cotacoes || []).map(c => {
            const fornecedorId = c.fornecedorProdutoId || c.fornecedorServicoId || c.fornecedorId
            const fornecedor = fornecedoresMap[fornecedorId]
            return {
              ...c,
              fornecedorCompleto: fornecedor
            }
          })
        }
      } catch (error) {
        logger.error('❌ Erro ao carregar pedido/rascunho:', error)
        logger.error('Detalhes do erro:', {
          message: error.message,
          response: error.response?.data,
          status: error.response?.status,
          id: id,
          tipo: tipo
        })
        showError(`Erro ao carregar dados: ${error.message || 'Erro desconhecido'}`)
        router.push('/pedidos')
      } finally {
        isLoading.value = false
      }
    }

    const voltar = () => {
      router.push('/pedidos')
    }

    const editarRascunho = () => {
      router.push(`/pedidos/novo/${pedido.value.rascunhoId}?step=1`)
    }

    // PDF no modal
    const pdfModalAberto = ref(null)
    const pdfModalUrl = ref(null)
    const carregandoPdfModal = ref(false)

    const togglePdfModal = async (cotacao, pdfIndex = 0) => {
      const pdfKey = `${cotacao.id}-${pdfIndex}`

      // Se o PDF já está aberto, fechar
      if (pdfModalAberto.value === pdfKey) {
        fecharPdfModal()
        return
      }

      // Fechar PDF anterior se houver
      if (pdfModalAberto.value !== null) {
        fecharPdfModal()
      }

      pdfModalAberto.value = pdfKey
      carregandoPdfModal.value = true

      try {
        let blob
        // Para pedidos finais, usar o serviço de cotação
        blob = await cotacaoService.obterAnexoPdf(cotacao.id, pdfIndex)

        if (blob && blob.size > 0) {
          pdfModalUrl.value = URL.createObjectURL(blob)
        }
      } catch (error) {
        logger.error('Erro ao carregar PDF:', error)
        showError('Erro ao carregar PDF')
      } finally {
        carregandoPdfModal.value = false
      }
    }

    const fecharPdfModal = () => {
      if (pdfModalUrl.value && pdfModalUrl.value.startsWith('blob:')) {
        URL.revokeObjectURL(pdfModalUrl.value)
      }
      pdfModalAberto.value = null
      pdfModalUrl.value = null
    }

    const excluirCotacao = async (cotacao) => {
      const { showWarning } = useErrorModal()

      showWarning(`Tem certeza que deseja excluir a cotação de ${cotacao.nomeFornecedor}?`, {
        title: 'Excluir Cotação?',
        confirmText: 'Sim, excluir',
        cancelText: 'Cancelar',
        onConfirm: async () => {
          try {
            const rascunhoId = pedido.value.rascunhoId || route.params.id

            if (isRascunho.value) {
              await cotacaoRascunhoService.deletar(rascunhoId, cotacao.id)
            } else {
              await cotacaoService.deleteCotacao(cotacao.id)
            }

            // Recarregar a página para atualizar as cotações
            await carregarPedido()
            success('Cotação excluída com sucesso!')
          } catch (error) {
            logger.error('Erro ao excluir cotação:', error)
            showError('Erro ao excluir cotação. Tente novamente.')
          }
        }
      })
    }

    // Métodos para edição de cotação (nova funcionalidade)
    const abrirModalEditarCotacao = (cotacao) => {
      cotacaoSelecionadaParaEdicao.value = cotacao
      modalEditarCotacaoAberto.value = true
    }

    const fecharModalEditarCotacao = () => {
      modalEditarCotacaoAberto.value = false
      cotacaoSelecionadaParaEdicao.value = null
    }

    const abrirModalHistorico = (cotacao) => {
      cotacaoSelecionadaParaEdicao.value = cotacao
      modalHistoricoAberto.value = true
    }

    const fecharModalHistorico = () => {
      modalHistoricoAberto.value = false
      cotacaoSelecionadaParaEdicao.value = null
    }

    const abrirModalDevolucao = async () => {
      try {
        // Verificar quantas cotações existem
        const quantidadeCotacoes = await rascunhoService.contarCotacoes(pedido.value.rascunhoId)

        if (quantidadeCotacoes > 0) {
          // Mostrar modal de confirmação estilizado
          quantidadeCotacoesParaDevolucao.value = quantidadeCotacoes
          modalConfirmacaoDevolucaoAberto.value = true
        } else {
          // Se não tem cotações, abrir direto o modal de devolução
          motivoDevolucao.value = ''
          modalDevolucaoAberto.value = true
        }
      } catch (err) {
        logger.error('Erro ao verificar cotações:', err)
        error('Erro ao verificar cotações do rascunho')
      }
    }

    const confirmarAbrirDevolucao = () => {
      modalConfirmacaoDevolucaoAberto.value = false
      motivoDevolucao.value = ''
      modalDevolucaoAberto.value = true
    }

    const fecharModalConfirmacaoDevolucao = () => {
      modalConfirmacaoDevolucaoAberto.value = false
      quantidadeCotacoesParaDevolucao.value = 0
    }

    const fecharModalDevolucao = () => {
      modalDevolucaoAberto.value = false
      motivoDevolucao.value = ''
    }

    const confirmarDevolucao = async () => {
      if (motivoDevolucao.value.length < 10) {
        error('O motivo deve ter pelo menos 10 caracteres')
        return
      }

      devolvendo.value = true
      try {
        await rascunhoService.devolverParaEdicao(pedido.value.rascunhoId, {
          motivo: motivoDevolucao.value
        })

        success('Rascunho devolvido para edição com sucesso!')
        fecharModalDevolucao()

        // Recarregar o rascunho para atualizar o status
        await carregarPedido()
      } catch (err) {
        logger.error('Erro ao devolver rascunho:', err)
        error(err.message || 'Erro ao devolver rascunho para edição')
      } finally {
        devolvendo.value = false
      }
    }

    const salvarEdicaoCotacao = async (dadosEdicao) => {
      try {
        // Preparar DTO para o backend (SEM PDFs - serão enviados separadamente)
        const editDTO = {
          id: dadosEdicao.id,
          motivoEdicao: dadosEdicao.motivoEdicao,
          editadoPor: dadosEdicao.editadoPor,
          itens: null, // Não editar itens, apenas preço total
          precoNovo: dadosEdicao.preco,
          prazoEmDiasUteis: dadosEdicao.prazoEmDiasUteis,
          dataLimite: dadosEdicao.dataLimite,
          anexoPdf: null // Backend ignora este campo - usar endpoint /anexos
        }

        // 1. Primeiro, editar os dados da cotação
        const resultado = await cotacaoService.editarCotacao(dadosEdicao.id, editDTO)

        // 2. Se há novos PDFs, fazer upload usando endpoint correto
        if (dadosEdicao.pdfFiles && dadosEdicao.pdfFiles.length > 0) {
          try {
            // Upload de cada PDF individualmente
            // createHistory=false para não criar registro duplicado no histórico
            for (const pdfFile of dadosEdicao.pdfFiles) {
              await cotacaoService.adicionarAnexo(dadosEdicao.id, pdfFile, false)
            }
            success(`Cotação editada com sucesso! ${dadosEdicao.pdfFiles.length} PDF(s) adicionado(s).`)
          } catch (pdfError) {
            logger.error('❌ Erro ao enviar PDFs:', pdfError)
            showError('Cotação editada, mas erro ao adicionar PDFs: ' + (pdfError.message || 'Erro desconhecido'))
          }
        } else {
          success('Cotação editada com sucesso!')
        }

        // Recarregar o pedido para mostrar as mudanças
        await carregarPedido()

        // Fechar modal
        fecharModalEditarCotacao()
      } catch (error) {
        logger.error('❌ Erro ao editar cotação:', error)
        logger.error('❌ Detalhes do erro:', error.response?.data || error.message)
        showError('Erro ao editar cotação: ' + (error.response?.data?.message || error.message || 'Erro desconhecido'))
      }
    }

    // ========================================
    // AÇÕES DE PEDIDO - Enviar, Aprovar, Devolver, Cancelar
    // ========================================

    // Modais de Pedido
    const modalEnviarAprovacaoAberto = ref(false)
    const modalAprovarAberto = ref(false)
    const modalDevolverPedidoAberto = ref(false)
    const modalCancelarAberto = ref(false)
    const observacaoPedido = ref('')
    const motivoDevolucaoPedido = ref('')
    const processando = ref(false)

    // Enviar para Aprovação
    const abrirModalEnviarAprovacao = () => {
      observacaoPedido.value = ''
      modalEnviarAprovacaoAberto.value = true
    }

    const fecharModalEnviarAprovacao = () => {
      modalEnviarAprovacaoAberto.value = false
      observacaoPedido.value = ''
    }

    const confirmarEnviarAprovacao = async () => {
      processando.value = true
      try {
        console.log('pedidoService:', pedidoService)
        console.log('enviarParaAprovacao:', pedidoService.enviarParaAprovacao)
        console.log('Todas as funções:', Object.keys(pedidoService))

        await pedidoService.enviarParaAprovacao(pedido.value.id, {
          observacao: observacaoPedido.value || undefined
        })

        success('Pedido enviado para aprovação com sucesso!')
        fecharModalEnviarAprovacao()

        // Recarregar o pedido para atualizar o status
        await carregarPedido()
      } catch (err) {
        logger.error('Erro ao enviar pedido para aprovação:', err)
        showError(err.message || 'Erro ao enviar pedido para aprovação')
      } finally {
        processando.value = false
      }
    }

    // Aprovar Pedido
    const abrirModalAprovar = () => {
      observacaoPedido.value = ''
      modalAprovarAberto.value = true
    }

    const fecharModalAprovar = () => {
      modalAprovarAberto.value = false
      observacaoPedido.value = ''
    }

    const confirmarAprovar = async () => {
      processando.value = true
      try {
        await pedidoService.aprovarPedidoWorkflow(pedido.value.id, {
          observacao: observacaoPedido.value || undefined
        })

        success('Pedido aprovado com sucesso!')
        fecharModalAprovar()

        // Recarregar o pedido para atualizar o status
        await carregarPedido()
      } catch (err) {
        logger.error('Erro ao aprovar pedido:', err)
        showError(err.message || 'Erro ao aprovar pedido')
      } finally {
        processando.value = false
      }
    }

    // Devolver Pedido para Edição
    const abrirModalDevolverPedido = () => {
      motivoDevolucaoPedido.value = ''
      modalDevolverPedidoAberto.value = true
    }

    const fecharModalDevolverPedido = () => {
      modalDevolverPedidoAberto.value = false
      motivoDevolucaoPedido.value = ''
    }

    const confirmarDevolverPedido = async () => {
      if (motivoDevolucaoPedido.value.length < 10) {
        showError('O motivo da devolução deve ter pelo menos 10 caracteres')
        return
      }

      processando.value = true
      try {
        await pedidoService.devolverParaEdicao(pedido.value.id, {
          motivo: motivoDevolucaoPedido.value
        })

        success('Pedido devolvido para edição com sucesso!')
        fecharModalDevolverPedido()

        // Recarregar o pedido para atualizar o status
        await carregarPedido()
      } catch (err) {
        logger.error('Erro ao devolver pedido para edição:', err)
        showError(err.message || 'Erro ao devolver pedido para edição')
      } finally {
        processando.value = false
      }
    }

    // Cancelar Pedido
    const abrirModalCancelar = () => {
      observacaoPedido.value = ''
      modalCancelarAberto.value = true
    }

    const fecharModalCancelar = () => {
      modalCancelarAberto.value = false
      observacaoPedido.value = ''
    }

    const confirmarCancelar = async () => {
      processando.value = true
      try {
        await pedidoService.cancelarPedidoWorkflow(pedido.value.id, {
          observacao: observacaoPedido.value || undefined
        })

        success('Pedido cancelado com sucesso!')
        fecharModalCancelar()

        // Recarregar o pedido para atualizar o status
        await carregarPedido()
      } catch (err) {
        logger.error('Erro ao cancelar pedido:', err)
        showError(err.message || 'Erro ao cancelar pedido')
      } finally {
        processando.value = false
      }
    }

    onMounted(() => {
      carregarPedido()
    })

    onBeforeUnmount(() => {
      fecharPdfViewer()
    })

    return {
      // Permissions
      permissions,
      podeEditarRascunho,
      podeDevolverParaEdicao,

      // Sidebar
      isSidebarOpen,
      toggleSidebar,
      closeSidebar,

      // State
      isLoading,
      pedido,
      cotacoes,
      historico,
      isRascunho,
      isFinalizado,

      // PDF
      pdfAberto,
      carregandoPdf,
      pdfUrl,

      // PDF Modal
      pdfModalAberto,
      pdfModalUrl,
      carregandoPdfModal,
      togglePdfModal,
      fecharPdfModal,

      // Gerenciar cotações
      excluirCotacao,

      // Methods
      getTitulo,
      getStatusLabel,
      getStatusClass,
      formatarData,
      formatarDataHora,
      getItensIdsDaCotacao,
      formatarPreco,
      formatarTelefone,
      getTotalCotacoes,
      itemTemCotacao,
      getNomeItem,
      getTipoModificacaoLabel,
      getTipoModificacaoColor,
      togglePdfViewer,
      voltar,
      editarRascunho,

      // Edição de cotação (nova funcionalidade)
      abrirModalEditarCotacao,
      abrirModalHistorico,
      fecharModalEditarCotacao,
      fecharModalHistorico,
      salvarEdicaoCotacao,
      modalEditarCotacaoAberto,
      modalHistoricoAberto,
      cotacaoSelecionadaParaEdicao,

      // Devolução para Edição (Rascunho)
      modalDevolucaoAberto,
      modalConfirmacaoDevolucaoAberto,
      quantidadeCotacoesParaDevolucao,
      motivoDevolucao,
      devolvendo,
      abrirModalDevolucao,
      confirmarAbrirDevolucao,
      fecharModalConfirmacaoDevolucao,
      fecharModalDevolucao,
      confirmarDevolucao,

      // Ações de Pedido
      modalEnviarAprovacaoAberto,
      modalAprovarAberto,
      modalDevolverPedidoAberto,
      modalCancelarAberto,
      observacaoPedido,
      motivoDevolucaoPedido,
      processando,
      abrirModalEnviarAprovacao,
      fecharModalEnviarAprovacao,
      confirmarEnviarAprovacao,
      abrirModalAprovar,
      fecharModalAprovar,
      confirmarAprovar,
      abrirModalDevolverPedido,
      fecharModalDevolverPedido,
      confirmarDevolverPedido,
      abrirModalCancelar,
      fecharModalCancelar,
      confirmarCancelar,
      isCollapsed
    }
  }
}
</script>

<style scoped>
@import '../assets/css/layout.css';
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
  transition: margin-left 0.35s ease;
}

.page-container.sidebar-collapsed .content-area {
  margin-left: 80px;
}

.breadcrumb > * {
  line-height: 1;
  display: inline-flex;
  align-items: center;
}

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

.breadcrumb-home svg {
  display: block;
  vertical-align: middle;
}

/* View Container */
.view-container {
  max-width: 1000px;
  margin: 0 auto;
}


.view-header {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 24px 28px;
  background: #1F285F;
  color: white;
  border-radius: 16px 16px 0 0;
  margin-bottom: 0;
}

.header-status-row {
  display: flex;
  align-items: center;
}

.header-main-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 24px;
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

.action-buttons-group {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px;
  background: linear-gradient(135deg, rgba(139, 149, 255, 0.15) 0%, rgba(99, 102, 241, 0.12) 100%);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(139, 149, 255, 0.3);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15), inset 0 1px 2px rgba(255, 255, 255, 0.1);
}

.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.status-pendente {
  background: #fef3c7;
  color: #92400e;
}

.status-aprovado {
  background: #d1fae5;
  color: #065f46;
}

.status-rejected {
  background: #fee2e2;
  color: #991b1b;
}

.status-rejeitado {
  background: #fee2e2;
  color: #991b1b;
}

.status-rascunho {
  background: #e0e7ff;
  color: #3730a3;
}

.status-draft {
  background: #e0e7ff;
  color: #3730a3;
}

/* Novos status do workflow */
.status-negotiating {
  background: #dbeafe;
  color: #1d4ed8;
  font-weight: 600;
}

.status-pending-approval {
  background: #fef3c7;
  color: #d97706;
  font-weight: 600;
}

.status-canceled {
  background: #fee2e2;
  color: #dc2626;
  font-weight: 600;
}

.status-active {
  background: #d1fae5;
  color: #047857;
  font-weight: 600;
}

.status-quoting {
  background: #fef3c7;
  color: #b45309;
}

.status-draft-finished {
  background: #e0e7ff;
  color: #3730a3;
  font-weight: 600;
}

.status-progress {
  background: #dbeafe;
  color: #1e40af;
}

.status-default {
  background: #f3f4f6;
  color: #6b7280;
}

/* Section Cards */
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

/* Buttons */
.btn-secondary {
  padding: 10px 20px;
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: #e5e7eb;
}

.btn-primary {
  padding: 10px 20px;
  background: #1F285F;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-primary:hover {
  background: #2d3a7f;
}

.btn-success {
  padding: 10px 20px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-success:hover {
  background: #059669;
}

/* Step 1 - Dados */
.info-section {
  margin-bottom: 24px;
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

.info-card {
  display: flex;
  gap: 12px;
  align-items: center;
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

.info-card-icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
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
}

.info-card-label {
  font-size: 0.6875rem;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 600;
}

.info-card-value {
  font-size: 1rem;
  color: #1e293b;
  font-weight: 600;
}

.observacao-box {
  background: #fffbeb;
  border: 1px solid #fde68a;
  border-radius: 8px;
  padding: 16px;
}

.observacao-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  color: #92400e;
  font-weight: 600;
  font-size: 0.875rem;
}

.observacao-icon {
  width: 18px;
  height: 18px;
}

.observacao-text {
  font-size: 0.875rem;
  color: #78350f;
  line-height: 1.5;
  margin: 0;
}

.itens-lista {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.item-card {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.item-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #f3f4f6;
  border-bottom: 1px solid #e5e7eb;
}

.item-numero {
  font-weight: 600;
  font-size: 0.875rem;
  color: #374151;
}

.item-status {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.75rem;
  padding: 4px 10px;
  border-radius: 4px;
  background: #fef3c7;
  color: #92400e;
  font-weight: 500;
}

.item-status .status-icon {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
}

.item-status.cotado {
  background: #d1fae5;
  color: #065f46;
}

.item-body {
  padding: 16px 20px;
}

.item-nome {
  font-weight: 500;
  color: #1e293b;
  margin-bottom: 4px;
}

.item-detalhes {
  display: flex;
  gap: 16px;
  font-size: 0.8125rem;
  color: #6b7280;
}

/* Step 2 - Cotações */
.cotacoes-lista {
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
  padding: 20px 24px;
  border-bottom: 2px solid #e5e7eb;
  gap: 20px;
}

.cotacao-info-principal {
  flex: 1;
}

.fornecedor-linha {
  display: flex;
  align-items: center;
  gap: 12px;
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
  color: #64748b;
  background: #f1f5f9;
  padding: 4px 10px;
  border-radius: 4px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.3px;
}

.preco-destaque-box {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  padding: 12px 18px;
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
  font-size: 1.5rem;
  font-weight: 700;
  color: #059669;
  line-height: 1;
}

/* Body */
.cotacao-body {
  padding: 24px;
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
  padding: 14px 16px;
  background: #f9fafb;
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

/* Seção de Contato */
.cotacao-contato-section {
  padding: 16px;
  background: #fafbfc;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.contato-section-title {
  font-size: 0.75rem;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 700;
  margin: 0 0 12px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #e5e7eb;
}

.contato-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.contato-item-new {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.8125rem;
  color: #475569;
  text-decoration: none;
  padding: 8px 12px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.contato-item-new:hover {
  background: #f8fafc;
  border-color: #6366f1;
  color: #4f46e5;
  transform: translateX(2px);
}

.contato-item-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.contato-email .email-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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

.itens-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.item-chip {
  background: linear-gradient(135deg, #e0e7ff 0%, #ddd6fe 100%);
  color: #4338ca;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.8125rem;
  font-weight: 600;
  line-height: 1.4;
  border: 1px solid #c7d2fe;
  transition: all 0.2s ease;
}

.item-chip:hover {
  background: linear-gradient(135deg, #ddd6fe 0%, #c7d2fe 100%);
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(67, 56, 202, 0.15);
}

.cotacao-actions {
  padding: 20px 24px;
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

/* Botão PDF Primário (Sólido) */
.btn-pdf-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
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

.novo-anexo {
  border: 2px solid gold !important;
}

.btn-delete-cotacao {
  padding: 6px 10px;
  border-radius: 4px;
  cursor: pointer;
  background: #fef2f2;
  color: #991b1b;
  border: 1px solid #fecaca;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 32px;
  height: 28px;
}

.btn-delete-cotacao:hover {
  background: #fee2e2;
  border-color: #fca5a5;
  color: #7f1d1d;
}

.btn-delete-cotacao:active {
  background: #fecaca;
}

.btn-delete-cotacao svg {
  width: 16px;
  height: 16px;
}

/* Badge de cotação editada */
.badge-editada {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: #fef3c7;
  color: #92400e;
  border: 1px solid #fde68a;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  margin-left: 8px;
}

.badge-editada svg {
  flex-shrink: 0;
}

/* Badge de PDF atualizado */
.badge-atualizado {
  display: inline-flex;
  align-items: center;
  padding: 2px 6px;
  background: #10b981;
  color: white;
  border-radius: 4px;
  font-size: 0.65rem;
  font-weight: 600;
  margin-left: 6px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Botões de edição e histórico */
.cotacao-edit-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.btn-edit-cotacao {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: #fff;
  color: #1F285F;
  border: 1px solid #1F285F;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-edit-cotacao:hover {
  background: #1F285F;
  color: #fff;
  box-shadow: 0 2px 4px rgba(31, 40, 95, 0.2);
}

.btn-edit-cotacao svg {
  flex-shrink: 0;
}

.btn-history-cotacao {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.btn-history-cotacao:hover {
  background: #e5e7eb;
  border-color: #9ca3af;
  color: #1f2937;
}

.btn-history-cotacao svg {
  flex-shrink: 0;
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

.pdf-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

/* Step 3 - Histórico */
.historico-lista {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.historico-item {
  display: flex;
  gap: 12px;
}

.historico-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.historico-content {
  flex: 1;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.historico-item:last-child .historico-content {
  border-bottom: none;
}

.historico-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.historico-tipo {
  font-weight: 600;
  font-size: 0.875rem;
  color: #1e293b;
}

.historico-data {
  font-size: 0.75rem;
  color: #6b7280;
}

.historico-usuario {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.8125rem;
  color: #6b7280;
  margin-bottom: 4px;
}

.usuario-icon {
  width: 14px;
  height: 14px;
  color: #9ca3af;
  flex-shrink: 0;
}

.historico-descricao {
  font-size: 0.8125rem;
  color: #374151;
}

/* Histórico Melhorado - Nova Visualização */
.section-header-with-badge {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.count-badge {
  background: #3b82f6;
  color: white;
  font-size: 0.75rem;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 12px;
  min-width: 24px;
  text-align: center;
}

.historico-lista-melhorada {
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;
}

.historico-item-melhorado {
  display: flex;
  gap: 16px;
  position: relative;
}

.timeline-connector {
  position: absolute;
  left: 19px;
  top: 40px;
  width: 2px;
  height: calc(100% + 8px);
  background: linear-gradient(to bottom, #e5e7eb 0%, #e5e7eb 100%);
  z-index: 0;
}

.historico-icon-melhorado {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1;
  position: relative;
}

.historico-content-melhorado {
  flex: 1;
  z-index: 1;
}

.historico-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: all 0.2s;
}

.historico-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.historico-header-melhorado {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  flex-wrap: wrap;
  gap: 8px;
}

.tipo-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 16px;
  font-size: 0.875rem;
  font-weight: 600;
  white-space: nowrap;
}

.historico-data-melhorada {
  font-size: 0.8125rem;
  color: #6b7280;
  white-space: nowrap;
}

.historico-usuario-melhorado {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.875rem;
  color: #4b5563;
  margin-bottom: 8px;
  padding: 8px 12px;
  background: #f9fafb;
  border-radius: 6px;
  border-left: 3px solid #3b82f6;
}

.historico-usuario-melhorado .usuario-icon {
  width: 16px;
  height: 16px;
  color: #6b7280;
}

.historico-descricao-melhorada {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  font-size: 0.875rem;
  color: #374151;
  line-height: 1.6;
  padding: 10px 12px;
  background: #fef3c7;
  border-radius: 6px;
  border-left: 3px solid #f59e0b;
}

.descricao-icon {
  flex-shrink: 0;
  color: #f59e0b;
  margin-top: 2px;
}

.historico-detalhes {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #e5e7eb;
}

.detalhe-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.875rem;
  color: #4b5563;
}

.numero-item-badge {
  background: #dbeafe;
  color: #1e40af;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.75rem;
  font-weight: 600;
}

.historico-detalhes-extras {
  margin-top: 8px;
  padding: 8px 12px;
  background: #f3f4f6;
  border-radius: 6px;
  font-size: 0.8125rem;
  color: #6b7280;
  font-style: italic;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #6b7280;
}

.empty-state p {
  margin: 12px 0 0;
}

.empty-state-small {
  text-align: center;
  padding: 20px;
  color: #9ca3af;
  font-size: 0.875rem;
}

.empty-state-small p {
  margin: 0;
}

/* Modal de Edição */
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
  z-index: 9999;
  padding: 20px;
}

.modal-container {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 800px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e293b;
}

.btn-close {
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
}

.btn-close:hover {
  background: #f3f4f6;
  color: #1e293b;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-section h4 {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
}

.form-section h5 {
  margin: 0 0 12px 0;
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
}

/* Seleção de Itens */
.itens-selecao {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.item-selecao-card {
  background: #f9fafb;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.item-selecao-card:hover {
  border-color: #1F285F;
  background: #f3f4f6;
}

.item-selecao-numero {
  font-size: 0.75rem;
  font-weight: 600;
  color: #6b7280;
  margin-bottom: 4px;
}

.item-selecao-nome {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
}

.item-selecao-qtd {
  font-size: 0.875rem;
  color: #6b7280;
  margin-bottom: 8px;
}

.item-selecao-status {
  font-size: 0.75rem;
  padding: 4px 8px;
  border-radius: 4px;
  background: #fef3c7;
  color: #92400e;
  display: inline-block;
}

.item-selecao-status.tem-cotacao {
  background: #d1fae5;
  color: #065f46;
}

/* Botão voltar */
.btn-voltar-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: all 0.2s;
  width: fit-content;
}

.btn-voltar-item:hover {
  background: #e5e7eb;
}

/* Item Selecionado */
.item-selecionado {
  background: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 8px;
  padding: 16px;
}

.item-selecionado h4 {
  margin: 0 0 4px 0;
  color: #0369a1;
}

.item-selecionado p {
  margin: 0;
  font-size: 0.875rem;
  color: #0c4a6e;
}

/* Cotações no Modal */
.cotacoes-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cotacoes-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.btn-toggle {
  padding: 6px 12px;
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.8125rem;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-toggle:hover {
  background: #e5e7eb;
  color: #1e293b;
}

.cotacoes-lista-modal {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cotacao-selecao-card {
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.2s;
  position: relative;
}

.cotacao-selecao-card:hover {
  border-color: #1F285F;
}

.cotacao-selecao-card.selecionada {
  border-color: #10b981;
  background: #f0fdf4;
}

.cotacao-selecao-card.vinculada-atual {
  border-color: #3b82f6;
  background: #eff6ff;
}

.badge-atual {
  background: #3b82f6;
  color: white;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  margin-bottom: 12px;
  display: inline-block;
}

.cotacao-acoes-modal {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
}

.pdf-viewer-inline {
  margin-top: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.pdf-viewer-inline .pdf-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  gap: 12px;
  color: #6b7280;
}

.pdf-viewer-inline .pdf-viewer-wrapper {
  height: 400px;
}

.pdf-viewer-inline .pdf-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

.btn-pdf-mini {
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 0.75rem;
  cursor: pointer;
  background: #f0f9ff;
  color: #0369a1;
  border: 1px solid #bae6fd;
  transition: all 0.2s ease;
  font-weight: 500;
}

.btn-pdf-mini:hover {
  background: #e0f2fe;
}

.cotacao-selecao-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.cotacao-selecao-fornecedor strong {
  display: block;
  font-size: 0.875rem;
  color: #1e293b;
  margin-bottom: 4px;
}

.cotacao-selecao-tipo {
  font-size: 0.75rem;
  color: #64748b;
  background: #f1f5f9;
  padding: 2px 8px;
  border-radius: 4px;
  display: inline-block;
}

.cotacao-selecao-preco {
  font-size: 1.125rem;
  font-weight: 600;
  color: #059669;
}

.cotacao-selecao-info {
  display: flex;
  gap: 16px;
  font-size: 0.8125rem;
  color: #64748b;
}

.cotacao-selecionada-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #10b981;
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
}

/* Formulário de Nova Cotação */
.form-nova-cotacao {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 20px;
  margin-top: 16px;
}

.form-nova-cotacao h6 {
  margin: 0 0 16px 0;
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.form-select,
.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.form-select:focus,
.form-input:focus {
  outline: none;
  border-color: #1F285F;
  box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.1);
}

.form-select:disabled {
  background: #f3f4f6;
  cursor: not-allowed;
}

.form-nova-cotacao small {
  font-size: 0.75rem;
  color: #6b7280;
  margin-top: 4px;
  display: block;
}

/* Form Groups */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
}

.form-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  font-family: inherit;
  resize: vertical;
  transition: all 0.2s;
}

.form-textarea:focus {
  outline: none;
  border-color: #1F285F;
  box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.1);
}

.char-count {
  font-size: 0.75rem;
  color: #6b7280;
  text-align: right;
}

/* Upload de Arquivo PDF */
.file-upload-container {
  display: flex;
  gap: 8px;
  align-items: center;
}

.file-input-hidden {
  display: none;
}

.file-upload-label {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: white;
  border: 2px dashed #d1d5db;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.875rem;
  color: #6b7280;
}

.file-upload-label:hover {
  border-color: #1F285F;
  background: #f9fafb;
}

.file-upload-label:active {
  transform: scale(0.98);
}

.file-icon {
  color: #9ca3af;
  flex-shrink: 0;
  transition: color 0.3s;
}

.file-upload-label:hover .file-icon {
  color: #1F285F;
}

.file-upload-text {
  font-weight: 500;
  transition: color 0.3s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-upload-text.selected {
  color: #1F285F;
  font-weight: 600;
}

.btn-remove-file {
  padding: 8px;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.btn-remove-file:hover {
  background: #dc2626;
  transform: scale(1.05);
}

.btn-remove-file:active {
  transform: scale(0.95);
}

.file-info {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  padding: 8px 12px;
  background: #f0f9ff;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  font-size: 0.75rem;
  color: #1e40af;
}

.file-info-icon {
  color: #3b82f6;
  flex-shrink: 0;
}

.file-size {
  font-weight: 500;
}

/* Autocomplete de Fornecedor */
.autocomplete-wrapper {
  position: relative;
}

.autocomplete-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  max-height: 200px;
  overflow-y: auto;
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  margin-top: 4px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.autocomplete-item {
  padding: 10px 12px;
  cursor: pointer;
  font-size: 0.875rem;
  transition: background 0.2s;
  border-bottom: 1px solid #f3f4f6;
}

.autocomplete-item:last-child {
  border-bottom: none;
}

.autocomplete-item:hover {
  background: #f9fafb;
}

.autocomplete-empty {
  padding: 12px;
  text-align: center;
  color: #9ca3af;
  font-size: 0.875rem;
  font-style: italic;
}

/* Estilos Inline (similar ao rascunho) */
.section-header-inline {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header-inline h5 {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #111827;
}

.btn-add-small {
  padding: 8px 16px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add-small:hover {
  background: #059669;
}

/* Formulário Inline */
.form-nova-cotacao-inline {
  background: #f9fafb;
  border: 2px dashed #d1d5db;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.form-row-2 {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 12px;
  margin-bottom: 12px;
}

.form-row-3 {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 12px;
}

.radio-group-inline {
  display: flex;
  gap: 16px;
}

.radio-label-inline {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 0.875rem;
}

.radio-label-inline input[type="radio"] {
  cursor: pointer;
}

.form-select-inline,
.form-input-inline {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.form-select-inline:focus,
.form-input-inline:focus {
  outline: none;
  border-color: #1F285F;
  box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.1);
}

.form-actions-inline {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-top: 12px;
}

.btn-cancel-inline {
  padding: 8px 16px;
  background: white;
  color: #6b7280;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel-inline:hover {
  background: #f3f4f6;
}

.btn-save-inline {
  padding: 8px 16px;
  background: #1F285F;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-save-inline:hover:not(:disabled) {
  background: #161d47;
}

.btn-save-inline:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Lista de Cotações Inline */
.loading-container-small {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px;
  color: #6b7280;
  font-size: 0.875rem;
}

.loading-spinner-small {
  width: 16px;
  height: 16px;
  border: 2px solid #e5e7eb;
  border-top-color: #1F285F;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

.empty-state-inline {
  padding: 24px;
  text-align: center;
  color: #6b7280;
  font-size: 0.875rem;
  background: #f9fafb;
  border-radius: 6px;
}

.cotacoes-lista-inline {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cotacao-card-inline {
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.cotacao-card-inline:hover {
  border-color: #1F285F;
  box-shadow: 0 2px 8px rgba(31, 40, 95, 0.1);
}

.cotacao-card-inline.selecionada {
  border-color: #1F285F;
  background: #f0f4ff;
}

.cotacao-card-inline.vinculada {
  border-color: #10b981;
  background: #f0fdf4;
}

.cotacao-badge-container {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.badge-vinculada {
  padding: 2px 8px;
  background: #10b981;
  color: white;
  font-size: 0.75rem;
  font-weight: 500;
  border-radius: 4px;
}

.badge-selecionada {
  padding: 2px 8px;
  background: #1F285F;
  color: white;
  font-size: 0.75rem;
  font-weight: 500;
  border-radius: 4px;
}

.cotacao-info-inline {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.fornecedor-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.fornecedor-info strong {
  font-size: 0.875rem;
  color: #111827;
}

.tipo-badge {
  padding: 2px 6px;
  background: #e5e7eb;
  color: #374151;
  font-size: 0.75rem;
  border-radius: 3px;
}

.preco-info {
  font-size: 1rem;
  font-weight: 600;
  color: #1F285F;
}

.cotacao-detalhes-inline {
  display: flex;
  gap: 16px;
  font-size: 0.75rem;
  color: #6b7280;
}

.form-textarea-inline {
  width: 100%;
  padding: 10px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  font-family: inherit;
  resize: vertical;
  transition: all 0.2s;
}

.form-textarea-inline:focus {
  outline: none;
  border-color: #1F285F;
  box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.1);
}

.char-count-inline {
  font-size: 0.75rem;
  color: #6b7280;
  text-align: right;
  margin-top: 4px;
}

/* Responsive */
@media (max-width: 1024px) {
  .content-area {
    margin-left: 0;
    padding: 20px;
  }

  .page-container.sidebar-collapsed .content-area {
    margin-left: 0;
  }

  .view-container {
    max-width: 100%;
  }

  .cotacao-header-padrao {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .preco-destaque-box {
    align-items: flex-start;
  }

  .info-grid-enhanced {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }
}

@media (max-width: 768px) {
  .content-area {
    padding: 12px;
  }

  .breadcrumb {
    flex-wrap: wrap;
    gap: 8px;
    font-size: 0.8125rem;
  }

  .btn-voltar {
    font-size: 0.8125rem;
    padding: 6px 10px;
  }

  .view-header {
    padding: 16px;
    border-radius: 12px;
  }

  .header-status-row {
    margin-bottom: 12px;
  }

  .header-main-row {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .action-buttons-group {
    width: 100%;
    flex-direction: column;
    gap: 8px;
  }

  .action-buttons-group .btn-action {
    width: 100%;
    justify-content: center;
    min-width: auto;
  }

  .header-resumo {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .resumo-separator {
    display: none;
  }

  /* Ocultar ícones decorativos do resumo em mobile */
  .resumo-item svg {
    display: none;
  }

  .view-title {
    font-size: 1.5rem;
  }

  .view-subtitle {
    font-size: 0.875rem;
    display: none;
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
  }

  .section-card {
    padding: 16px;
    border-radius: 12px;
  }

  .section-title {
    font-size: 1.125rem;
  }

  .info-grid-enhanced {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .info-card {
    padding: 12px;
  }

  .modal-container {
    max-width: 100%;
    max-height: 100vh;
    border-radius: 0;
  }

  .itens-selecao {
    grid-template-columns: 1fr;
  }

  /* Cotações responsive */
  .cotacao-header-padrao {
    padding: 16px;
  }

  .fornecedor-nome {
    font-size: 1rem;
  }

  .preco-valor-grande {
    font-size: 1.375rem;
  }

  .cotacao-body {
    padding: 16px;
  }

  .cotacao-info-grid {
    grid-template-columns: 1fr;
  }

  .contato-grid {
    grid-template-columns: 1fr;
  }

  .cotacao-actions {
    padding: 16px;
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }

  .pdf-buttons {
    width: 100%;
  }

  .btn-pdf-primary {
    width: 100%;
    justify-content: center;
  }

  /* Histórico responsive */
  .timeline-item {
    padding-left: 32px;
  }

  .timeline-dot {
    width: 10px;
    height: 10px;
    left: -5px;
  }
}

@media (max-width: 480px) {
  .content-area {
    padding: 8px;
  }

  .breadcrumb {
    font-size: 0.75rem;
  }

  .view-header {
    padding: 12px;
    border-radius: 8px;
  }

  .view-title {
    font-size: 1.25rem;
  }

  .view-subtitle {
    display: none;
  }

  .section-card {
    padding: 12px;
    border-radius: 8px;
  }

  .section-title {
    font-size: 1rem;
  }

  /* Ocultar emojis dos títulos em telas muito pequenas */
  .section-title::before {
    display: none;
  }

  .info-card {
    padding: 10px;
  }

  .info-card-label {
    font-size: 0.75rem;
  }

  .info-card-value {
    font-size: 0.875rem;
  }

  /* Ocultar ícones dos info-cards em mobile */
  .info-card-icon {
    display: none;
  }

  .fornecedor-nome {
    font-size: 0.9375rem;
  }

  .preco-valor-grande {
    font-size: 1.25rem;
  }

  .info-box {
    padding: 10px 12px;
  }

  .btn-action {
    padding: 10px 16px;
    font-size: 0.8125rem;
  }

  .observacao-box {
    padding: 10px;
  }

  .contato-item-new {
    font-size: 0.75rem;
    padding: 6px 10px;
  }

  /* Simplificar timeline do histórico */
  .timeline-dot {
    width: 8px;
    height: 8px;
  }

  .timeline-content {
    font-size: 0.8125rem;
  }
}

/* Estilos para Devolução */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 20px;
  animation: fadeInDevolucao 0.2s ease-out;
}

@keyframes fadeInDevolucao {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-container-small {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 540px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  animation: slideUpDevolucao 0.3s ease-out;
}

@keyframes slideUpDevolucao {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 24px 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #111827;
}

.btn-close {
  background: transparent;
  border: none;
  font-size: 1.75rem;
  cursor: pointer;
  color: #9ca3af;
  padding: 0;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.2s;
  line-height: 1;
}

.btn-close:hover {
  background: #f3f4f6;
  color: #374151;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
}

.modal-description {
  color: #6b7280;
  font-size: 0.9375rem;
  margin-bottom: 24px;
  line-height: 1.6;
  background: #f9fafb;
  padding: 12px 16px;
  border-radius: 8px;
  border-left: 3px solid #f59e0b;
}

.form-group {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 0.9375rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.form-textarea {
  width: 100%;
  padding: 12px 14px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 0.9375rem;
  font-family: inherit;
  resize: vertical;
  min-height: 120px;
  transition: all 0.2s;
  line-height: 1.5;
}

.form-textarea:focus {
  outline: none;
  border-color: #f59e0b;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
}

.form-textarea::placeholder {
  color: #9ca3af;
}

.form-hint {
  display: block;
  margin-top: 8px;
  color: #6b7280;
  font-size: 0.8125rem;
  text-align: right;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
  border-radius: 0 0 12px 12px;
}

.btn-cancel {
  padding: 12px 28px;
  background: white;
  color: #6b7280;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 0.9375rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
  letter-spacing: 0.01em;
}

.btn-cancel::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(107, 114, 128, 0.1);
  transform: translate(-50%, -50%);
  transition: width 0.6s ease-out, height 0.6s ease-out;
}

.btn-cancel:active::before {
  width: 300px;
  height: 300px;
}

.btn-cancel:hover {
  background: #f9fafb;
  border-color: #d1d5db;
  color: #374151;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);
  transform: translateY(-1px);
}

.btn-cancel:active {
  transform: translateY(0);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.btn-danger {
  padding: 12px 28px;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 50%, #b91c1c 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 0.9375rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow:
    0 4px 14px rgba(239, 68, 68, 0.4),
    0 1px 3px rgba(0, 0, 0, 0.12),
    inset 0 -2px 6px rgba(0, 0, 0, 0.1);
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.25);
  position: relative;
  overflow: hidden;
  letter-spacing: 0.01em;
}

.btn-danger::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s ease-out, height 0.6s ease-out;
}

.btn-danger:active::before {
  width: 400px;
  height: 400px;
}

.btn-danger::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 50%;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.15) 0%, transparent 100%);
  border-radius: 10px 10px 0 0;
  pointer-events: none;
}

.btn-danger:hover:not(:disabled) {
  background: linear-gradient(135deg, #f87171 0%, #ef4444 50%, #dc2626 100%);
  box-shadow:
    0 8px 24px rgba(239, 68, 68, 0.5),
    0 4px 12px rgba(239, 68, 68, 0.3),
    0 0 0 4px rgba(239, 68, 68, 0.1),
    inset 0 1px 2px rgba(255, 255, 255, 0.2);
  transform: translateY(-2px) scale(1.02);
}

.btn-danger:active:not(:disabled) {
  transform: translateY(0) scale(0.98);
  box-shadow:
    0 4px 14px rgba(239, 68, 68, 0.4),
    inset 0 2px 8px rgba(0, 0, 0, 0.15);
}

.btn-danger:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  filter: grayscale(0.3);
}

.btn-danger:disabled:hover {
  transform: none;
}

.btn-warning {
  padding: 11px 24px;
  background: #f59e0b;
  color: white;
  border: 2px solid #f59e0b;
  border-radius: 8px;
  font-size: 0.9375rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.btn-warning:hover:not(:disabled) {
  background: #d97706;
  border-color: #d97706;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.btn-warning:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.btn-warning:disabled {
  background: #d1d5db;
  border-color: #d1d5db;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.btn-danger-outline {
  padding: 11px 24px;
  background: white;
  color: #dc2626;
  border: 2px solid #dc2626;
  border-radius: 8px;
  font-size: 0.9375rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.btn-danger-outline:hover:not(:disabled) {
  background: #dc2626;
  color: white;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.btn-danger-outline:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}

.btn-danger-outline:disabled {
  background: white;
  color: #d1d5db;
  border-color: #d1d5db;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

/* ========================================
   BOTÕES DE AÇÃO - Workflow de Pedidos
   ======================================== */

.btn-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 14px 28px;
  border-radius: 12px;
  font-size: 0.9375rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  position: relative;
  overflow: hidden;
  letter-spacing: 0.01em;
  min-width: 180px;
  backdrop-filter: blur(8px);
}

/* Efeito ripple ao clicar */
.btn-action::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  transform: translate(-50%, -50%);
  transition: width 0.6s ease-out, height 0.6s ease-out;
  pointer-events: none;
}

.btn-action:active::before {
  width: 400px;
  height: 400px;
}

/* Brilho sutil no topo */
.btn-action::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 50%;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.15) 0%, transparent 100%);
  border-radius: 12px 12px 0 0;
  pointer-events: none;
  opacity: 1;
  transition: opacity 0.3s;
}

.btn-action:hover::after {
  opacity: 0.8;
}

.btn-action svg {
  flex-shrink: 0;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1));
  z-index: 1;
}

.btn-action:hover svg {
  transform: scale(1.15) rotate(5deg);
}

.btn-action:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  box-shadow: none;
  filter: grayscale(0.3);
}

.btn-action:disabled:hover {
  transform: none;
}

.btn-action:disabled svg {
  transform: none;
}

/* Primary - Enviar para Aprovação */
.btn-action-primary {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 50%, #4338ca 100%);
  color: white;
  box-shadow:
    0 4px 14px rgba(99, 102, 241, 0.4),
    0 1px 3px rgba(0, 0, 0, 0.12),
    inset 0 -2px 6px rgba(0, 0, 0, 0.1);
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.25);
}

.btn-action-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #818cf8 0%, #6366f1 50%, #4f46e5 100%);
  box-shadow:
    0 8px 24px rgba(99, 102, 241, 0.6),
    0 4px 12px rgba(99, 102, 241, 0.4),
    0 0 0 4px rgba(99, 102, 241, 0.1),
    inset 0 1px 2px rgba(255, 255, 255, 0.2);
  transform: translateY(-3px) scale(1.02);
}

.btn-action-primary:active:not(:disabled) {
  transform: translateY(-1px) scale(0.98);
  box-shadow:
    0 4px 14px rgba(99, 102, 241, 0.5),
    inset 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* Success - Aprovar Pedido */
.btn-action-success {
  background: linear-gradient(135deg, #10b981 0%, #059669 50%, #047857 100%);
  color: white;
  box-shadow:
    0 4px 14px rgba(16, 185, 129, 0.4),
    0 1px 3px rgba(0, 0, 0, 0.12),
    inset 0 -2px 6px rgba(0, 0, 0, 0.1);
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.25);
}

.btn-action-success:hover:not(:disabled) {
  background: linear-gradient(135deg, #34d399 0%, #10b981 50%, #059669 100%);
  box-shadow:
    0 8px 24px rgba(16, 185, 129, 0.5),
    0 4px 12px rgba(16, 185, 129, 0.3),
    0 0 0 4px rgba(16, 185, 129, 0.1),
    inset 0 1px 2px rgba(255, 255, 255, 0.2);
  transform: translateY(-3px) scale(1.02);
}

.btn-action-success:active:not(:disabled) {
  transform: translateY(-1px) scale(0.98);
  box-shadow:
    0 4px 14px rgba(16, 185, 129, 0.4),
    inset 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* Warning - Devolver para Edição */
.btn-action-warning {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 50%, #b45309 100%);
  color: white;
  box-shadow:
    0 4px 14px rgba(245, 158, 11, 0.4),
    0 1px 3px rgba(0, 0, 0, 0.12),
    inset 0 -2px 6px rgba(0, 0, 0, 0.1);
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.25);
}

.btn-action-warning:hover:not(:disabled) {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 50%, #d97706 100%);
  box-shadow:
    0 8px 24px rgba(245, 158, 11, 0.5),
    0 4px 12px rgba(245, 158, 11, 0.3),
    0 0 0 4px rgba(245, 158, 11, 0.1),
    inset 0 1px 2px rgba(255, 255, 255, 0.2);
  transform: translateY(-3px) scale(1.02);
}

.btn-action-warning:active:not(:disabled) {
  transform: translateY(-1px) scale(0.98);
  box-shadow:
    0 4px 14px rgba(245, 158, 11, 0.4),
    inset 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* Secondary (Ghost) - Cancelar */
.btn-action-secondary {
  background: linear-gradient(135deg, rgba(165, 180, 252, 0.18) 0%, rgba(139, 149, 255, 0.15) 100%);
  color: rgba(255, 255, 255, 0.95);
  border: 2px solid rgba(165, 180, 252, 0.3);
  box-shadow:
    0 2px 8px rgba(0, 0, 0, 0.1),
    inset 0 1px 2px rgba(255, 255, 255, 0.1);
  font-weight: 600;
  backdrop-filter: blur(12px);
}

.btn-action-secondary:hover:not(:disabled) {
  background: linear-gradient(135deg, rgba(248, 113, 113, 0.35) 0%, rgba(239, 68, 68, 0.3) 100%);
  color: #fef2f2;
  border-color: rgba(248, 113, 113, 0.5);
  box-shadow:
    0 6px 20px rgba(239, 68, 68, 0.35),
    0 2px 8px rgba(239, 68, 68, 0.2),
    0 0 0 4px rgba(239, 68, 68, 0.08),
    inset 0 1px 2px rgba(255, 255, 255, 0.15);
  transform: translateY(-2px) scale(1.02);
}

.btn-action-secondary:active:not(:disabled) {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.4) 0%, rgba(220, 38, 38, 0.35) 100%);
  transform: translateY(-1px) scale(0.98);
  box-shadow:
    0 3px 12px rgba(239, 68, 68, 0.3),
    inset 0 2px 6px rgba(0, 0, 0, 0.2);
}

.btn-action-secondary svg {
  opacity: 0.95;
  transition: opacity 0.3s, transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.btn-action-secondary:hover:not(:disabled) svg {
  opacity: 1;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

/* Responsividade dos botões de ação */
@media (max-width: 768px) {
  .btn-action {
    padding: 12px 20px;
    font-size: 0.875rem;
    gap: 8px;
    min-width: 150px;
  }

  .btn-action svg {
    width: 14px;
    height: 14px;
  }

  .btn-action:hover:not(:disabled) {
    transform: translateY(-2px) scale(1.01);
  }
}
</style>
