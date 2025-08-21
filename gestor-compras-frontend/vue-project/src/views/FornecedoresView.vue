<template>
  <div class="dashboard-layout">
    <!-- Header -->
    <DashboardHeader />

    <!-- Sidebar -->
    <DashboardSidebar />

    <!-- Conteúdo Principal -->
    <main class="main-content">
      <!-- Seção de Título e Ação Rápida -->
      <div class="page-header">
        <div class="header-content">
          <div class="title-section">
            <h1 class="page-title">Fornecedores</h1>
            <p class="page-subtitle">Gerencie cadastro e avaliação de fornecedores</p>
          </div>
          <button class="action-button" @click="createNewSupplier">
            <svg class="action-icon" viewBox="0 0 24 24" width="20" height="20">
              <path fill="white" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
            </svg>
            Novo Fornecedor
          </button>
        </div>
      </div>

      <!-- Indicadores (Cards de Métricas Rápidas) -->
      <div class="metrics-section">
        <div class="metrics-grid">
          <!-- Total de Fornecedores -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon total">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M16 4c0-1.11.89-2 2-2s2 .89 2 2-.89 2-2 2-2-.89-2-2zm4 18v-6h2.5l-2.54-7.63A2 2 0 0 0 18.04 7H16c-.8 0-1.54.37-2.01.99L12 10l2.01-2.01C14.54 7.37 15.2 7 16 7h2.04c1.23 0 2.18 1.24 1.92 2.63l2.54 7.63H20v6h-4z"/>
                </svg>
              </div>
              <span class="metric-label">Total de Fornecedores</span>
            </div>
            <div class="metric-value">87</div>
            <div class="metric-growth positive">+3 novos este mês</div>
          </div>

          <!-- Fornecedores Ativos -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon active">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                </svg>
              </div>
              <span class="metric-label">Fornecedores Ativos</span>
            </div>
            <div class="metric-value">74</div>
            <div class="metric-growth neutral">85% do total</div>
          </div>

          <!-- Avaliação Média -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon rating">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                </svg>
              </div>
              <span class="metric-label">Avaliação Média</span>
            </div>
            <div class="metric-value">4.6 ⭐</div>
            <div class="metric-growth positive">Excelente</div>
          </div>

          <!-- Valor Transacionado -->
          <div class="metric-card">
            <div class="metric-header">
              <div class="metric-icon value">
                <svg viewBox="0 0 24 24" width="24" height="24">
                  <path fill="white" d="M7 15h2c0 1.08 1.37 2 3 2s3-.92 3-2c0-1.1-1.04-1.5-3.24-2.03C9.64 12.44 7 11.78 7 9c0-1.79 1.47-3.31 3.5-3.82V3h3v2.18C15.53 5.69 17 7.21 17 9h-2c0-1.08-1.37-2-3-2s-3 .92-3 2c0 1.1 1.04 1.5 3.24 2.03C14.36 11.56 17 12.22 17 15c0 1.79-1.47 3.31-3.5 3.82V21h-3v-2.18C8.47 18.31 7 16.79 7 15z"/>
                </svg>
              </div>
              <span class="metric-label">Valor Transacionado</span>
            </div>
            <div class="metric-value">R$ 3.2M</div>
            <div class="metric-growth positive">+25% vs ano anterior</div>
          </div>
        </div>
      </div>

      <!-- Filtros de Pesquisa -->
      <div class="search-section">
        <div class="search-container">
          <div class="search-input-container">
            <svg class="search-icon" viewBox="0 0 24 24" width="20" height="20">
              <path fill="currentColor" d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
            </svg>
            <input
              type="text"
              v-model="searchQuery"
              placeholder="Pesquisar por nome, categoria ou contato..."
              class="search-input"
            />
          </div>
          <div class="search-actions">
            <button class="search-button" @click="searchSuppliers">Pesquisar</button>
            <button class="filter-button" @click="toggleFilters">
              <svg viewBox="0 0 24 24" width="16" height="16">
                <path fill="currentColor" d="M14,12V19.88C14.04,20.18 13.94,20.5 13.71,20.71C13.32,21.1 12.69,21.1 12.3,20.71L10.29,18.7C10.06,18.47 9.96,18.16 10,17.87V12H9.97L4.21,4.62C3.87,4.19 3.95,3.56 4.38,3.22C4.57,3.08 4.78,3 5,3V3H19V3C19.22,3 19.43,3.08 19.62,3.22C20.05,3.56 20.13,4.19 19.79,4.62L14.03,12H14Z"/>
              </svg>
              Filtros
            </button>
          </div>
        </div>
      </div>

      <!-- Lista de Fornecedores (Tabela) -->
      <div class="table-section">
        <div class="table-container">
          <table class="suppliers-table">
            <thead>
              <tr>
                <th>Fornecedor</th>
                <th>Categoria</th>
                <th>Contato</th>
                <th>Avaliação</th>
                <th>Pedidos</th>
                <th>Valor Total</th>
                <th>Última Compra</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="supplier in suppliers" :key="supplier.id" class="table-row">
                <td class="supplier-cell">
                  <div class="supplier-info">
                    <span class="supplier-name">{{ supplier.name }}</span>
                    <span class="supplier-code">#{{ supplier.code }}</span>
                  </div>
                </td>
                <td>
                  <span class="category-tag" :class="getCategoryClass(supplier.category)">
                    {{ supplier.category }}
                  </span>
                </td>
                <td class="contact-cell">
                  <div class="contact-info">
                    <span class="contact-name">{{ supplier.contact.name }}</span>
                    <span class="contact-phone">{{ supplier.contact.phone }}</span>
                    <span class="contact-email">{{ supplier.contact.email }}</span>
                  </div>
                </td>
                <td class="rating-cell">
                  <div class="rating">
                    <span class="rating-value">{{ supplier.rating }}</span>
                    <div class="stars">
                      <span v-for="star in 5" :key="star" class="star" :class="{ filled: star <= Math.floor(supplier.rating) }">⭐</span>
                    </div>
                  </div>
                </td>
                <td class="orders-cell">{{ supplier.orders }}</td>
                <td class="value-cell">{{ formatCurrency(supplier.totalValue) }}</td>
                <td class="date-cell">{{ formatDate(supplier.lastPurchase) }}</td>
                <td class="actions-cell">
                  <div class="action-buttons">
                    <button class="action-btn view" @click="viewSupplier(supplier)" title="Visualizar">
                      👁️
                    </button>
                    <button class="action-btn edit" @click="editSupplier(supplier)" title="Editar">
                      ✏️
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import DashboardHeader from '../components/DashboardHeader.vue'
import DashboardSidebar from '../components/DashboardSidebar.vue'

const searchQuery = ref('')

// Dados mock dos fornecedores
const suppliers = ref([
  {
    id: 1,
    name: 'TechSolutions Ltda',
    code: 'TECH001',
    category: 'Tecnologia',
    contact: {
      name: 'João Silva',
      phone: '(11) 99999-9999',
      email: 'joao@techsolutions.com'
    },
    rating: 4.8,
    orders: 23,
    totalValue: 580000,
    lastPurchase: '2024-01-08'
  },
  {
    id: 2,
    name: 'Materiais Norte S.A.',
    code: 'MAT002',
    category: 'Materiais',
    contact: {
      name: 'Maria Santos',
      phone: '(11) 88888-8888',
      email: 'maria@materiaisnorte.com'
    },
    rating: 4.5,
    orders: 45,
    totalValue: 1200000,
    lastPurchase: '2024-01-15'
  },
  {
    id: 3,
    name: 'Serviços Premium',
    code: 'SERV003',
    category: 'Serviços',
    contact: {
      name: 'Carlos Oliveira',
      phone: '(11) 77777-7777',
      email: 'carlos@servicospremium.com'
    },
    rating: 4.2,
    orders: 12,
    totalValue: 350000,
    lastPurchase: '2024-01-20'
  },
  {
    id: 4,
    name: 'Equipamentos Pro',
    code: 'EQUIP004',
    category: 'Equipamentos',
    contact: {
      name: 'Ana Costa',
      phone: '(11) 66666-6666',
      email: 'ana@equipamentospro.com'
    },
    rating: 4.7,
    orders: 18,
    totalValue: 890000,
    lastPurchase: '2024-01-12'
  }
])

const createNewSupplier = () => {
  console.log('Criar novo fornecedor')
}

const searchSuppliers = () => {
  console.log('Pesquisar fornecedores:', searchQuery.value)
}

const toggleFilters = () => {
  console.log('Toggle filtros')
}

const viewSupplier = (supplier) => {
  console.log('Visualizar fornecedor:', supplier)
}

const editSupplier = (supplier) => {
  console.log('Editar fornecedor:', supplier)
}

const getCategoryClass = (category) => {
  const classes = {
    'Tecnologia': 'tech',
    'Materiais': 'materials',
    'Serviços': 'services',
    'Equipamentos': 'equipment'
  }
  return classes[category] || 'default'
}

const formatCurrency = (value) => {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(value)
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('pt-BR')
}
</script>

<style scoped>
.dashboard-layout {
  min-height: 100vh;
  background: #f8f9fa;
}

.main-content {
  margin-left: 280px;
  margin-top: 70px;
  padding: 32px;
  min-height: calc(100vh - 70px);
}

/* Page Header */
.page-header {
  margin-bottom: 32px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding-bottom: 24px;
  border-bottom: 1px solid #e0e6ed;
}

.title-section {
  flex: 1;
}

.page-title {
  font-family: Arial, sans-serif;
  font-size: 32px;
  font-weight: 700;
  color: #1F285F;
  margin: 0 0 8px 0;
  line-height: 1.2;
}

.page-subtitle {
  font-family: Arial, sans-serif;
  font-size: 16px;
  color: #6b7280;
  margin: 0;
  line-height: 1.4;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #1F285F;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-family: Arial, sans-serif;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 4px rgba(31, 40, 95, 0.2);
}

.action-button:hover {
  background: #162038;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(31, 40, 95, 0.3);
}

.action-icon {
  flex-shrink: 0;
}

/* Metrics Section */
.metrics-section {
  margin-bottom: 32px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.metric-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e0e6ed;
  transition: all 0.3s ease;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.metric-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.metric-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.metric-icon.total {
  background: #1F285F;
}

.metric-icon.active {
  background: #10b981;
}

.metric-icon.rating {
  background: #f59e0b;
}

.metric-icon.value {
  background: #6366f1;
}

.metric-label {
  font-family: Arial, sans-serif;
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.metric-value {
  font-family: Arial, sans-serif;
  font-size: 28px;
  font-weight: 700;
  color: #1F285F;
  margin-bottom: 8px;
  line-height: 1;
}

.metric-growth {
  font-family: Arial, sans-serif;
  font-size: 12px;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 6px;
  display: inline-block;
}

.metric-growth.positive {
  background: #dcfce7;
  color: #166534;
}

.metric-growth.neutral {
  background: #f3f4f6;
  color: #374151;
}

/* Search Section */
.search-section {
  margin-bottom: 32px;
}

.search-container {
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-input-container {
  flex: 1;
  position: relative;
  max-width: 500px;
}

.search-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #6b7280;
}

.search-input {
  width: 100%;
  height: 48px;
  padding: 0 20px 0 50px;
  border: 1px solid #e0e6ed;
  border-radius: 8px;
  font-family: Arial, sans-serif;
  font-size: 14px;
  background: white;
  transition: all 0.2s ease;
}

.search-input:focus {
  outline: none;
  border-color: #1F285F;
  box-shadow: 0 0 0 3px rgba(31, 40, 95, 0.1);
}

.search-actions {
  display: flex;
  gap: 12px;
}

.search-button,
.filter-button {
  padding: 12px 20px;
  border-radius: 8px;
  font-family: Arial, sans-serif;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-button {
  background: #1F285F;
  color: white;
  border: none;
}

.search-button:hover {
  background: #162038;
}

.filter-button {
  background: white;
  color: #6b7280;
  border: 1px solid #e0e6ed;
}

.filter-button:hover {
  background: #f8f9fa;
  color: #1F285F;
  border-color: #1F285F;
}

/* Table Section */
.table-section {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e0e6ed;
}

.table-container {
  overflow-x: auto;
}

.suppliers-table {
  width: 100%;
  border-collapse: collapse;
  font-family: Arial, sans-serif;
}

.suppliers-table th {
  background: #f8f9fa;
  color: #374151;
  font-weight: 600;
  font-size: 14px;
  text-align: left;
  padding: 16px 20px;
  border-bottom: 1px solid #e0e6ed;
  white-space: nowrap;
}

.suppliers-table td {
  padding: 16px 20px;
  border-bottom: 1px solid #f1f3f4;
  vertical-align: middle;
}

.table-row:hover {
  background: #f8f9fa;
}

.supplier-cell {
  min-width: 200px;
}

.supplier-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.supplier-name {
  font-weight: 600;
  color: #1F285F;
  font-size: 14px;
}

.supplier-code {
  font-size: 12px;
  color: #6b7280;
}

.category-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.category-tag.tech {
  background: #dbeafe;
  color: #1e40af;
}

.category-tag.materials {
  background: #dcfce7;
  color: #166534;
}

.category-tag.services {
  background: #fef3c7;
  color: #92400e;
}

.category-tag.equipment {
  background: #e0e7ff;
  color: #3730a3;
}

.contact-cell {
  min-width: 200px;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.contact-name {
  font-weight: 500;
  color: #374151;
  font-size: 14px;
}

.contact-phone,
.contact-email {
  font-size: 12px;
  color: #6b7280;
}

.rating-cell {
  min-width: 100px;
}

.rating {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.rating-value {
  font-weight: 600;
  color: #1F285F;
  font-size: 14px;
}

.stars {
  display: flex;
  gap: 2px;
}

.star {
  font-size: 12px;
  opacity: 0.3;
}

.star.filled {
  opacity: 1;
}

.orders-cell,
.value-cell,
.date-cell {
  font-weight: 500;
  color: #374151;
  font-size: 14px;
}

.value-cell {
  color: #10b981;
  font-weight: 600;
}

.actions-cell {
  min-width: 100px;
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
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.action-btn.view {
  background: #f0f9ff;
  color: #0369a1;
}

.action-btn.view:hover {
  background: #0369a1;
  color: white;
}

.action-btn.edit {
  background: #fef3c7;
  color: #92400e;
}

.action-btn.edit:hover {
  background: #92400e;
  color: white;
}

/* Responsividade */
@media (max-width: 1024px) {
  .main-content {
    margin-left: 0;
    padding: 20px;
  }

  .metrics-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 16px;
  }

  .search-container {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input-container {
    max-width: none;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .page-title {
    font-size: 24px;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
  }

  .table-container {
    overflow-x: scroll;
  }

  .suppliers-table {
    min-width: 800px;
  }
}
</style>
