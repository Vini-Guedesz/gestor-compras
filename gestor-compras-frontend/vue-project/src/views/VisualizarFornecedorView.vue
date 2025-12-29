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
          <span class="breadcrumb-separator">|</span>
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
                <span class="resumo-separator" v-if="historicoCompras.valor > 0">•</span>
                <div class="resumo-item resumo-total" v-if="historicoCompras.valor > 0">
                  <svg viewBox="0 0 24 24" width="16" height="16" class="resumo-icon">
                    <path fill="currentColor" d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/>
                  </svg>
                  <span class="total-valor">Total Cotado: R$ {{ historicoCompras.valor.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) }}</span>
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
            <div class="info-grid-enhanced">
              <div class="info-card" v-if="fornecedor.contato?.telefoneFixo">
                <div class="info-card-icon" style="background: #fce7f3;">
                  <svg viewBox="0 0 20 20" fill="#db2777" width="20" height="20">
                    <path d="M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V17a1 1 0 01-1 1h-2C7.82 18 2 12.18 2 5V3z"/>
                  </svg>
                </div>
                <div class="info-card-content">
                  <span class="info-card-label">Telefone Fixo</span>
                  <span class="info-card-value">{{ formatarTelefone(fornecedor.contato.telefoneFixo) }}</span>
                </div>
              </div>
              <div class="info-card" v-if="fornecedor.contato?.celular">
                <div class="info-card-icon" style="background: #e0f2fe;">
                  <svg viewBox="0 0 20 20" fill="#0284c7" width="20" height="20">
                    <path d="M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V17a1 1 0 01-1 1h-2C7.82 18 2 12.18 2 5V3z"/>
                  </svg>
                </div>
                <div class="info-card-content">
                  <span class="info-card-label">Celular</span>
                  <span class="info-card-value">{{ formatarTelefone(fornecedor.contato.celular) }}</span>
                </div>
              </div>
              <div class="info-card full-width">
                <div class="info-card-icon" style="background: #dcfce7;">
                  <svg viewBox="0 0 20 20" fill="#16a34a" width="20" height="20">
                    <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/>
                    <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/>
                  </svg>
                </div>
                <div class="info-card-content">
                  <span class="info-card-label">E-mail</span>
                  <span class="info-card-value">{{ fornecedor.contato?.email || 'Não informado' }}</span>
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
                  <span class="info-card-label">Endereço Completo</span>
                  <span class="info-card-value endereco-completo">
                    {{ fornecedor.endereco.rua }}, {{ fornecedor.endereco.numero }}
                    <span v-if="fornecedor.endereco.complemento"> - {{ fornecedor.endereco.complemento }}</span>
                    <br>
                    {{ fornecedor.endereco.bairro }}
                    <br>
                    {{ fornecedor.endereco.cidade }}/{{ fornecedor.endereco.estado }}
                    <br>
                    CEP: {{ formatarCEP(fornecedor.endereco.cep) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          </div><!-- Fim da aba de informações -->

          <!-- Conteúdo da Aba: Histórico de Cotações -->
          <div v-show="abaAtiva === 'historico'">
          <div class="section-card">
            <h3 class="section-title">Histórico de Cotações</h3>

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
                <div class="stat-icon" style="background: #dcfce7;">
                  <svg viewBox="0 0 24 24" width="24" height="24" fill="#16a34a">
                    <path d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/>
                  </svg>
                </div>
                <div class="stat-content">
                  <span class="stat-value">R$ {{ historicoCompras.valor.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) }}</span>
                  <span class="stat-label">Valor Total Cotado</span>
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
            </div>

            <!-- Loading -->
            <div v-if="carregandoHistorico" class="loading-message">
              <div class="loading-spinner-small"></div>
              <p>Carregando histórico...</p>
            </div>

            <!-- Empty State -->
            <div v-else-if="historicoCompras.cotacoes.length === 0" class="empty-state">
              <svg viewBox="0 0 24 24" width="48" height="48" fill="#9ca3af">
                <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
              </svg>
              <p>Nenhuma cotação encontrada para este fornecedor.</p>
            </div>

            <!-- Lista de Cotações -->
            <div v-else class="historico-lista">
              <div v-for="cotacao in historicoCompras.cotacoes" :key="cotacao.id" class="cotacao-card">
                <!-- Header -->
                <div class="cotacao-header-padrao">
                  <div class="cotacao-info-principal">
                    <div class="fornecedor-linha">
                      <strong class="fornecedor-nome">Cotação {{ cotacao.numero }}</strong>
                      <span class="tipo-tag" v-if="cotacao.pedidoId">Pedido #{{ cotacao.pedidoId }}</span>
                      <span class="tipo-tag tipo-tag-data">{{ cotacao.data }}</span>
                    </div>
                  </div>
                  <div class="preco-destaque-box">
                    <span class="preco-label-small">VALOR TOTAL</span>
                    <span class="preco-valor-grande">R$ {{ cotacao.valor?.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) }}</span>
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
                    <h5 class="itens-section-title">Itens Contemplados ({{ cotacao.totalItens || 1 }})</h5>
                    <div class="itens-chips">
                      <span class="item-chip">{{ cotacao.itemNome }}</span>
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
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DashboardHeader from '@/features/dashboard/components/DashboardHeader.vue'
import DashboardSidebar from '@/features/dashboard/components/DashboardSidebar.vue'
import Icon from '@/components/ui/Icon.vue'
import fornecedorService from '@/services/fornecedorService.js'
import cotacaoService from '@/services/cotacaoService.js'
import itemPedidoService from '@/services/itemPedidoService.js'
import pedidoService from '@/services/pedidoService.js'
import logger from '@/utils/logger.js'

const route = useRoute()
const router = useRouter()

// Estados
const isSidebarOpen = ref(false)
const isLoading = ref(true)
const fornecedor = ref(null)
const carregandoHistorico = ref(false)
const abaAtiva = ref('informacoes') // 'informacoes' ou 'historico'

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

// Caches
const cacheItensPedido = ref(new Map())
const cachePedidos = ref(new Map())

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

const formatarCEP = (cep) => {
  if (!cep) return ''
  const numbers = cep.replace(/\D/g, '')
  return numbers.replace(/^(\d{5})(\d{3})$/, '$1-$2')
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
    logger.warn(`⚠️ Erro ao buscar item ${itemPedidoId}:`, error)
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
    logger.warn(`⚠️ Erro ao buscar pedido ${pedidoId}:`, error)
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
      } catch (erroProduto) {
        // Se não encontrou em produtos, tentar em serviços
        try {
          fornecedor.value = await fornecedorService.obterFornecedorServicoPorId(fornecedorId)
          fornecedor.value.tipo = 'servico'
        } catch (erroServico) {
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

    const todasCotacoes = await cotacaoService.listar()
    const cotacoes = todasCotacoes.filter(cot => {
      const fornecedorCotacaoId = cot.fornecedorProdutoId || cot.fornecedorServicoId || cot.fornecedorId
      return fornecedorCotacaoId === Number(fornecedorId)
    })

    if (cotacoes && cotacoes.length > 0) {
      const totalCotacoes = cotacoes.length
      const valorTotal = cotacoes.reduce((acc, cot) => acc + (parseFloat(cot.preco) || 0), 0)

      const cotacoesOrdenadas = [...cotacoes].sort((a, b) =>
        new Date(b.dataCotacao) - new Date(a.dataCotacao)
      )

      const ultimaCotacao = cotacoesOrdenadas[0]
      const dataUltima = ultimaCotacao?.dataCotacao
        ? new Date(ultimaCotacao.dataCotacao).toLocaleDateString('pt-BR')
        : '-'

      const cotacoesComDetalhes = await Promise.all(
        cotacoesOrdenadas.map(async (cot) => {
          let pedido = null

          // Buscar o pedido relacionado
          if (cot.solicitacaoDePedidoId) {
            pedido = await buscarPedidoComCache(cot.solicitacaoDePedidoId)
          }

          // Buscar todos os itens de pedido desta cotação
          const itensDetalhados = await Promise.all(
            (cot.itens || []).map(async (itemCotacao) => {
              if (itemCotacao.itemPedidoId) {
                const itemPedido = await buscarItemPedidoComCache(itemCotacao.itemPedidoId)
                return itemPedido
              }
              return null
            })
          )

          // Filtrar itens nulos e pegar o primeiro item para exibir (ou agregar todos)
          const itensValidos = itensDetalhados.filter(item => item !== null)
          const primeiroItem = itensValidos[0] || null

          // Se houver múltiplos itens, vamos concatenar as descrições (nome real do produto)
          const nomesItens = itensValidos.map(item => item.descricao || item.nome).join(', ')
          const quantidadeTotal = itensValidos.reduce((sum, item) => sum + (item.quantidade || 0), 0)

          return {
            id: cot.id,
            numero: cot.id?.toString() || '-',
            data: cot.dataUltimaEdicao
              ? new Date(cot.dataUltimaEdicao).toLocaleDateString('pt-BR')
              : '-',
            valor: parseFloat(cot.preco) || 0,
            status: 'cotacao',
            statusTexto: 'Cotação Enviada',
            prazoEntrega: cot.dataLimite
              ? new Date(cot.dataLimite).toLocaleDateString('pt-BR')
              : '-',
            itemNome: nomesItens || 'Item não especificado',
            itemQuantidade: quantidadeTotal,
            itemDescricao: primeiroItem?.descricao || '',
            itemObservacao: primeiroItem?.observacao || '',
            pedidoId: pedido?.id || null,
            pedidoStatus: pedido?.status || null,
            pedidoObservacao: pedido?.observacao || '',
            pedidoData: pedido?.dataCriacao
              ? new Date(pedido.dataCriacao).toLocaleDateString('pt-BR')
              : '-',
            totalItens: itensValidos.length,
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
.breadcrumb {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 24px;
  font-size: 0.875rem;
}

.btn-voltar {
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

.btn-voltar:hover {
  background: #e5e7eb;
}

.breadcrumb a {
  color: #1F285F;
  text-decoration: none;
  font-weight: 500;
}

.breadcrumb a:hover {
  text-decoration: underline;
}

.breadcrumb span {
  color: #6b7280;
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
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  white-space: nowrap;
}

.status-cotacao {
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
@media (max-width: 768px) {
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
}
</style>
