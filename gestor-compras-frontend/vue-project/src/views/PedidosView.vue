<template>
    <div class="dashboard-layout">
        <!-- Header -->
        <DashboardHeader />

        <!-- Sidebar -->
        <DashboardSidebar />

        <!-- Conteúdo Principal -->
        <main class="main-content">
            <!-- Cabeçalho da Página -->
            <div class="page-header">
                <div class="header-content">
                    <div class="title-section">
                        <h1 class="page-title">Pedidos de Compra 📋</h1>
                        <p class="page-subtitle">Gerencie pedidos de compra e acompanhe o fluxo de aprovação</p>
                    </div>
                    <div class="header-actions">
                        <button class="btn-secondary" @click="toggleView">
                            <svg class="btn-icon" viewBox="0 0 24 24" width="20" height="20">
                                <path fill="currentColor" d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"
                                    v-if="viewMode === 'form'" />
                                <path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" v-else />
                            </svg>
                            {{ viewMode === 'form' ? 'Ver Lista' : 'Novo Pedido' }}
                        </button>
                    </div>
                </div>
            </div>

            <!-- Formulário de Novo Pedido -->
            <div v-if="viewMode === 'form'" class="form-section">
                <PedidoForm :pedido="pedidoEditando" @save="salvarPedido" @cancel="cancelarEdicao" />
            </div>

            <!-- Lista de Pedidos -->
            <div v-else class="list-section">
                <!-- Filtros e Busca -->
                <div class="filters-section">
                    <div class="search-container">
                        <svg class="search-icon" viewBox="0 0 24 24" width="20" height="20">
                            <path fill="currentColor"
                                d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z" />
                        </svg>
                        <input type="text" v-model="searchQuery"
                            placeholder="Pesquisar por número, requisitante, objetivo..." class="search-input"
                            @input="filtrarPedidos" />
                    </div>
                    <div class="filter-actions">
                        <select v-model="filtroStatus" @change="filtrarPedidos" class="filter-select">
                            <option value="">Todos os status</option>
                            <option value="rascunho">Rascunho</option>
                            <option value="enviado">Enviado</option>
                            <option value="aprovado">Aprovado</option>
                            <option value="em_cotacao">Em Cotação</option>
                            <option value="cancelado">Cancelado</option>
                        </select>
                        <select v-model="filtroObjetivo" @change="filtrarPedidos" class="filter-select">
                            <option value="">Todos os objetivos</option>
                            <option value="reposicao">Reposição</option>
                            <option value="consumo">Consumo</option>
                            <option value="projeto">Projeto</option>
                        </select>
                        <button class="btn-filter" @click="limparFiltros">
                            <svg viewBox="0 0 24 24" width="16" height="16">
                                <path fill="currentColor"
                                    d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
                            </svg>
                            Limpar
                        </button>
                    </div>
                </div>

                <!-- Loading -->
                <div v-if="isLoading" class="loading-section">
                    <div class="loading-spinner-large"></div>
                    <p>Carregando pedidos...</p>
                </div>

                <!-- Lista de Pedidos -->
                <div v-else class="pedidos-grid">
                    <div v-for="pedido in pedidosFiltrados" :key="pedido.id" class="pedido-card"
                        @click="visualizarPedido(pedido)">
                        <div class="card-header">
                            <div class="pedido-numero">
                                <span class="numero-label">Pedido</span>
                                <span class="numero-value">#{{ pedido.numero }}</span>
                            </div>
                            <div class="status-badge" :class="getStatusClass(pedido.status)">
                                {{ getStatusLabel(pedido.status) }}
                            </div>
                        </div>

                        <div class="card-body">
                            <div class="pedido-info">
                                <div class="info-row">
                                    <span class="label">Requisitante:</span>
                                    <span class="value">{{ pedido.requisitante }}</span>
                                </div>
                                <div class="info-row">
                                    <span class="label">Data:</span>
                                    <span class="value">{{ formatarData(pedido.dataPedido) }}</span>
                                </div>
                                <div class="info-row">
                                    <span class="label">Objetivo:</span>
                                    <span class="value">{{ getObjetivoLabel(pedido.objetivo) }}</span>
                                </div>
                                <div class="info-row">
                                    <span class="label">Itens:</span>
                                    <span class="value">{{ pedido.itens?.length || 0 }} item(s)</span>
                                </div>
                            </div>
                        </div>

                        <div class="card-footer">
                            <div class="action-buttons">
                                <button class="btn-action edit" @click.stop="editarPedido(pedido)"
                                    :disabled="!podeEditar(pedido.status)" title="Editar pedido">
                                    <svg viewBox="0 0 24 24" width="16" height="16">
                                        <path fill="currentColor"
                                            d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" />
                                    </svg>
                                </button>
                                <button class="btn-action duplicate" @click.stop="duplicarPedido(pedido)"
                                    title="Duplicar pedido">
                                    <svg viewBox="0 0 24 24" width="16" height="16">
                                        <path fill="currentColor"
                                            d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z" />
                                    </svg>
                                </button>
                                <button class="btn-action cancel" @click.stop="cancelarPedido(pedido)"
                                    :disabled="!podeCancelar(pedido.status)" title="Cancelar pedido">
                                    <svg viewBox="0 0 24 24" width="16" height="16">
                                        <path fill="currentColor"
                                            d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
                                    </svg>
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- Estado vazio -->
                    <div v-if="pedidosFiltrados.length === 0 && !isLoading" class="empty-state">
                        <svg class="empty-icon" viewBox="0 0 24 24" width="64" height="64">
                            <path fill="currentColor"
                                d="M7 4V2C7 1.45 7.45 1 8 1H16C16.55 1 17 1.45 17 2V4H20C20.55 4 21 4.45 21 5S20.55 6 20 6H19V19C19 20.1 18.1 21 17 21H7C5.9 21 5 20.1 5 19V6H4C3.45 6 3 5.55 3 5S3.45 4 4 4H7ZM9 3V4H15V3H9ZM7 6V19H17V6H7Z" />
                        </svg>
                        <h3>Nenhum pedido encontrado</h3>
                        <p>Crie um novo pedido para começar a gerenciar suas compras.</p>
                        <button class="btn-primary" @click="viewMode = 'form'">
                            Criar Pedido
                        </button>
                    </div>
                </div>
            </div>

            <!-- Modal de Confirmação de Cancelamento -->
            <ConfirmModal :show="showConfirmCancel" title="Cancelar Pedido"
                :message="`Tem certeza que deseja cancelar o pedido #${pedidoParaCancelar?.numero}?`"
                confirm-text="Sim, Cancelar" cancel-text="Não" type="danger" @confirm="confirmarCancelamento"
                @cancel="showConfirmCancel = false" />
        </main>

        <!-- Notificações -->
        <div v-if="notification.show" class="notification" :class="notification.type">
            <div class="notification-content">
                <svg viewBox="0 0 24 24" width="20" height="20">
                    <path fill="currentColor" :d="getNotificationIcon" />
                </svg>
                <span>{{ notification.message }}</span>
            </div>
            <button @click="hideNotification" class="notification-close">
                <svg viewBox="0 0 24 24" width="16" height="16">
                    <path fill="currentColor"
                        d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" />
                </svg>
            </button>
        </div>
    </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import DashboardHeader from '@/components/DashboardHeader.vue'
import DashboardSidebar from '@/components/DashboardSidebar.vue'
import PedidoForm from '@/components/PedidoForm.vue'
import ConfirmModal from '@/components/ConfirmModal.vue'
import pedidoService from '@/services/pedidoService.js'

export default {
    name: 'PedidosView',
    components: {
        DashboardHeader,
        DashboardSidebar,
        PedidoForm,
        ConfirmModal
    },
    setup() {
        // Estados
        const isLoading = ref(true)
        const viewMode = ref('list') // 'list' ou 'form'
        const pedidos = ref([])
        const pedidoEditando = ref(null)
        const pedidoParaCancelar = ref(null)
        const showConfirmCancel = ref(false)

        // Filtros
        const searchQuery = ref('')
        const filtroStatus = ref('')
        const filtroObjetivo = ref('')

        // Notificações
        const notification = reactive({
            show: false,
            type: 'success',
            message: ''
        })

        // Dados carregados do serviço

        // Computeds
        const pedidosFiltrados = computed(() => {
            let resultado = pedidos.value

            if (searchQuery.value.trim()) {
                const query = searchQuery.value.toLowerCase()
                resultado = resultado.filter(pedido =>
                    pedido.numero.toLowerCase().includes(query) ||
                    pedido.requisitante.toLowerCase().includes(query) ||
                    getObjetivoLabel(pedido.objetivo).toLowerCase().includes(query)
                )
            }

            if (filtroStatus.value) {
                resultado = resultado.filter(pedido => pedido.status === filtroStatus.value)
            }

            if (filtroObjetivo.value) {
                resultado = resultado.filter(pedido => pedido.objetivo === filtroObjetivo.value)
            }

            return resultado
        })

        const getNotificationIcon = computed(() => {
            const icons = {
                success: 'M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z',
                error: 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v4z',
                warning: 'M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z'
            }
            return icons[notification.type] || icons.success
        })

        // Métodos
        const carregarPedidos = async () => {
            isLoading.value = true
            try {
                const dadosPedidos = await pedidoService.listarPedidos()
                pedidos.value = dadosPedidos
            } catch (error) {
                console.error('Erro ao carregar pedidos:', error)
                showNotification('error', 'Erro ao carregar pedidos')
            } finally {
                isLoading.value = false
            }
        }

        const toggleView = () => {
            if (viewMode.value === 'form') {
                viewMode.value = 'list'
                pedidoEditando.value = null
            } else {
                viewMode.value = 'form'
                pedidoEditando.value = null
            }
        }

        const editarPedido = (pedido) => {
            pedidoEditando.value = { ...pedido }
            viewMode.value = 'form'
        }

        const duplicarPedido = (pedido) => {
            const pedidoDuplicado = {
                ...pedido,
                id: null,
                numero: null,
                dataPedido: new Date(),
                status: 'rascunho'
            }
            pedidoEditando.value = pedidoDuplicado
            viewMode.value = 'form'
            showNotification('success', 'Pedido duplicado. Preencha os dados e salve.')
        }

        const visualizarPedido = (pedido) => {
            // Implementar modal de visualização ou navegar para página de detalhes
            console.log('Visualizar pedido:', pedido)
        }

        const cancelarPedido = (pedido) => {
            pedidoParaCancelar.value = pedido
            showConfirmCancel.value = true
        }

        const confirmarCancelamento = async () => {
            try {
                const pedido = pedidoParaCancelar.value
                await pedidoService.cancelarPedido(pedido.id)

                // Atualizar o status localmente
                const index = pedidos.value.findIndex(p => p.id === pedido.id)
                if (index !== -1) {
                    pedidos.value[index].status = 'cancelado'
                }

                showNotification('success', `Pedido #${pedido.numero} cancelado com sucesso`)
                showConfirmCancel.value = false
                pedidoParaCancelar.value = null
            } catch (error) {
                console.error('Erro ao cancelar pedido:', error)
                showNotification('error', 'Erro ao cancelar pedido')
            }
        }

        const salvarPedido = async (dadosPedido) => {
            try {
                if (pedidoEditando.value?.id) {
                    // Atualizar pedido existente
                    const pedidoAtualizado = await pedidoService.atualizarPedido(pedidoEditando.value.id, dadosPedido)
                    const index = pedidos.value.findIndex(p => p.id === pedidoEditando.value.id)
                    if (index !== -1) {
                        pedidos.value[index] = pedidoAtualizado
                    }
                    showNotification('success', 'Pedido atualizado com sucesso')
                } else {
                    // Criar novo pedido
                    const novoPedido = await pedidoService.criarPedido(dadosPedido)
                    pedidos.value.push(novoPedido)
                    showNotification('success', 'Pedido criado com sucesso')
                }

                viewMode.value = 'list'
                pedidoEditando.value = null
            } catch (error) {
                console.error('Erro ao salvar pedido:', error)
                showNotification('error', 'Erro ao salvar pedido')
            }
        }

        const cancelarEdicao = () => {
            viewMode.value = 'list'
            pedidoEditando.value = null
        }

        const filtrarPedidos = () => {
            // Os filtros são aplicados automaticamente através do computed
        }

        const limparFiltros = () => {
            searchQuery.value = ''
            filtroStatus.value = ''
            filtroObjetivo.value = ''
        }

        const showNotification = (type, message) => {
            notification.type = type
            notification.message = message
            notification.show = true

            setTimeout(() => {
                hideNotification()
            }, 5000)
        }

        const hideNotification = () => {
            notification.show = false
        }

        // Funções de formatação e utilidades
        const formatarData = (data) => {
            return new Date(data).toLocaleDateString('pt-BR')
        }

        const getStatusClass = (status) => {
            const classes = {
                rascunho: 'status-draft',
                enviado: 'status-sent',
                aprovado: 'status-approved',
                em_cotacao: 'status-quote',
                cancelado: 'status-cancelled'
            }
            return classes[status] || 'status-default'
        }

        const getStatusLabel = (status) => {
            const labels = {
                rascunho: 'Rascunho',
                enviado: 'Enviado',
                aprovado: 'Aprovado',
                em_cotacao: 'Em Cotação',
                cancelado: 'Cancelado'
            }
            return labels[status] || 'Indefinido'
        }

        const getObjetivoLabel = (objetivo) => {
            const labels = {
                reposicao: 'Reposição',
                consumo: 'Consumo',
                projeto: 'Projeto'
            }
            return labels[objetivo] || 'Não definido'
        }

        const podeEditar = (status) => {
            return ['rascunho', 'enviado'].includes(status)
        }

        const podeCancelar = (status) => {
            return ['rascunho', 'enviado', 'aprovado'].includes(status)
        }

        // Lifecycle
        onMounted(() => {
            carregarPedidos()
        })

        return {
            // Estados
            isLoading,
            viewMode,
            pedidos,
            pedidoEditando,
            pedidoParaCancelar,
            showConfirmCancel,

            // Filtros
            searchQuery,
            filtroStatus,
            filtroObjetivo,

            // Notificações
            notification,

            // Computeds
            pedidosFiltrados,
            getNotificationIcon,

            // Métodos
            toggleView,
            editarPedido,
            duplicarPedido,
            visualizarPedido,
            cancelarPedido,
            confirmarCancelamento,
            salvarPedido,
            cancelarEdicao,
            filtrarPedidos,
            limparFiltros,
            showNotification,
            hideNotification,
            formatarData,
            getStatusClass,
            getStatusLabel,
            getObjetivoLabel,
            podeEditar,
            podeCancelar
        }
    }
}
</script>

<style scoped>
/* Layout Dashboard */
.dashboard-layout {
    min-height: 100vh;
    background: #f8fafc;
}

.main-content {
    margin-left: 280px;
    margin-top: 70px;
    padding: 32px;
    min-height: calc(100vh - 70px);
    overflow-y: auto;
    background: #f8fafc;
}

/* Page Header */
.page-header {
    margin-bottom: 32px;
}

.header-content {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 24px;
}

.title-section {
    flex: 1;
}

.page-title {
    font-size: 28px;
    font-weight: 700;
    color: #1F285F;
    margin: 0 0 8px 0;
    line-height: 1.3;
}

.page-subtitle {
    font-size: 16px;
    color: #6b7280;
    margin: 0;
    line-height: 1.5;
}

.header-actions {
    display: flex;
    gap: 12px;
}

/* Botões */
.btn-primary {
    background: #3b82f6;
    color: white;
    border: none;
    padding: 12px 24px;
    border-radius: 8px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
    display: flex;
    align-items: center;
    gap: 8px;
}

.btn-primary:hover {
    background: #2563eb;
    transform: translateY(-1px);
}

.btn-secondary {
    background: white;
    color: #374151;
    border: 1px solid #d1d5db;
    padding: 12px 20px;
    border-radius: 8px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
    display: flex;
    align-items: center;
    gap: 8px;
}

.btn-secondary:hover {
    background: #f9fafb;
    border-color: #9ca3af;
}

.btn-icon {
    width: 20px;
    height: 20px;
}

/* Seção de Filtros */
.filters-section {
    background: white;
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 24px;
    border: 1px solid #e5e7eb;
    display: flex;
    gap: 16px;
    align-items: center;
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
    font-size: 14px;
    transition: all 0.2s;
}

.search-input:focus {
    outline: none;
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.filter-actions {
    display: flex;
    gap: 12px;
    align-items: center;
}

.filter-select {
    padding: 12px;
    border: 1px solid #d1d5db;
    border-radius: 8px;
    font-size: 14px;
    min-width: 150px;
    background: white;
}

.btn-filter {
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

.btn-filter:hover {
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

/* Grid de Pedidos */
.pedidos-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
    gap: 24px;
}

.pedido-card {
    background: white;
    border-radius: 12px;
    border: 1px solid #e5e7eb;
    overflow: hidden;
    transition: all 0.2s;
    cursor: pointer;
}

.pedido-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 25px -3px rgba(0, 0, 0, 0.1);
}

.card-header {
    padding: 20px 20px 0 20px;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
}

.pedido-numero {
    display: flex;
    flex-direction: column;
}

.numero-label {
    font-size: 12px;
    color: #6b7280;
    margin-bottom: 4px;
}

.numero-value {
    font-size: 18px;
    font-weight: 700;
    color: #111827;
}

.status-badge {
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
}

.status-draft {
    background: #f3f4f6;
    color: #374151;
}

.status-sent {
    background: #dbeafe;
    color: #1d4ed8;
}

.status-approved {
    background: #d1fae5;
    color: #047857;
}

.status-quote {
    background: #fef3c7;
    color: #d97706;
}

.status-cancelled {
    background: #fee2e2;
    color: #dc2626;
}

.card-body {
    padding: 16px 20px;
}

.pedido-info {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.info-row .label {
    font-size: 14px;
    color: #6b7280;
}

.info-row .value {
    font-size: 14px;
    color: #111827;
    font-weight: 500;
}

.card-footer {
    padding: 16px 20px 20px 20px;
    border-top: 1px solid #f3f4f6;
}

.action-buttons {
    display: flex;
    gap: 8px;
    justify-content: flex-end;
}

.btn-action {
    width: 36px;
    height: 36px;
    border: none;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s;
}

.btn-action:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.btn-action.edit {
    background: #dbeafe;
    color: #1d4ed8;
}

.btn-action.edit:hover:not(:disabled) {
    background: #bfdbfe;
}

.btn-action.duplicate {
    background: #e0e7ff;
    color: #4338ca;
}

.btn-action.duplicate:hover {
    background: #c7d2fe;
}

.btn-action.cancel {
    background: #fee2e2;
    color: #dc2626;
}

.btn-action.cancel:hover:not(:disabled) {
    background: #fecaca;
}

/* Estado vazio */
.empty-state {
    grid-column: 1 / -1;
    text-align: center;
    padding: 64px 32px;
    color: #6b7280;
}

.empty-icon {
    margin-bottom: 16px;
    color: #9ca3af;
}

.empty-state h3 {
    font-size: 18px;
    font-weight: 600;
    color: #374151;
    margin: 0 0 8px 0;
}

.empty-state p {
    margin: 0 0 24px 0;
}

/* Notificações */
.notification {
    position: fixed;
    top: 24px;
    right: 24px;
    z-index: 1200;
    padding: 16px 20px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    gap: 12px;
    box-shadow: 0 10px 25px -3px rgba(0, 0, 0, 0.1);
    max-width: 400px;
    animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
    from {
        transform: translateX(100%);
        opacity: 0;
    }

    to {
        transform: translateX(0);
        opacity: 1;
    }
}

.notification.success {
    background: #d1fae5;
    color: #047857;
    border: 1px solid #a7f3d0;
}

.notification.error {
    background: #fee2e2;
    color: #dc2626;
    border: 1px solid #fca5a5;
}

.notification.warning {
    background: #fef3c7;
    color: #d97706;
    border: 1px solid #fcd34d;
}

.notification-content {
    display: flex;
    align-items: center;
    gap: 8px;
    flex: 1;
}

.notification-close {
    background: none;
    border: none;
    padding: 4px;
    cursor: pointer;
    border-radius: 4px;
    opacity: 0.7;
    transition: opacity 0.2s;
}

.notification-close:hover {
    opacity: 1;
}

/* Responsividade */
@media (max-width: 1024px) {
    .main-content {
        margin-left: 0;
        padding: 20px;
    }

    .header-content {
        flex-direction: column;
        align-items: stretch;
    }

    .filters-section {
        flex-direction: column;
        align-items: stretch;
    }

    .search-container {
        min-width: auto;
    }

    .filter-actions {
        justify-content: flex-start;
    }

    .pedidos-grid {
        grid-template-columns: 1fr;
    }
}

@media (max-width: 768px) {
    .page-title {
        font-size: 24px;
    }

    .page-subtitle {
        font-size: 14px;
    }

    .pedido-card {
        margin-bottom: 16px;
    }
}
</style>
