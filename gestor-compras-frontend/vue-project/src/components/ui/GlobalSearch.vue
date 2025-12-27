<template>
  <div class="global-search">
    <div class="search-container">
      <svg class="search-icon" viewBox="0 0 24 24" width="20" height="20">
        <path fill="currentColor" d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
      </svg>
      <input
        v-model="searchQuery"
        type="text"
        placeholder="Pesquisar pedidos e fornecedores..."
        class="search-input"
        @keyup.enter="handleSearch"
        @input="handleInput"
      />
      <button
        v-if="searchQuery"
        @click="clearSearch"
        class="clear-button"
        title="Limpar pesquisa"
      >
        <svg viewBox="0 0 24 24" width="18" height="18">
          <path fill="currentColor" d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z"/>
        </svg>
      </button>
    </div>

    <!-- Sugestões de pesquisa -->
    <div v-if="showSuggestions && suggestions.length > 0" class="suggestions-dropdown">
      <div
        v-for="suggestion in suggestions"
        :key="suggestion.id"
        @click="navigateToSuggestion(suggestion)"
        class="suggestion-item"
      >
        <div class="suggestion-icon" :class="suggestion.type">
          {{ suggestion.icon }}
        </div>
        <div class="suggestion-content">
          <div class="suggestion-title">{{ suggestion.title }}</div>
          <div class="suggestion-subtitle">{{ suggestion.subtitle }}</div>
        </div>
        <div class="suggestion-badge">{{ suggestion.badge }}</div>
      </div>
    </div>

    <!-- Dica de uso -->
    <div v-if="showTips && searchQuery" class="search-tips">
      <div class="tip-title">💡 Dica: Use prefixos para pesquisa rápida</div>
      <div class="tip-examples">
        <span class="tip-example">P-123</span> Pedido |
        <span class="tip-example">R-45</span> Rascunho |
        <span class="tip-example">F-10</span> Fornecedor |
        <span class="tip-example">C-5</span> Cotação
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from '@/composables/useToast'

export default {
  name: 'GlobalSearch',
  setup() {
    const router = useRouter()
    const { warning, error } = useToast()

    const searchQuery = ref('')
    const showSuggestions = ref(false)
    const showTips = ref(false)

    // Parser de IDs padronizados
    const parseSearchQuery = (query) => {
      const trimmed = query.trim().toUpperCase()

      // Padrões de ID: P-123, R-45, F-10, C-5, U-1, I-7
      const patterns = {
        pedido: /^P-(\d+)$/,
        rascunho: /^R-(\d+)$/,
        fornecedor: /^F-(\d+)$/,
        cotacao: /^C-(\d+)$/,
        usuario: /^U-(\d+)$/,
        item: /^I-(\d+)$/
      }

      for (const [type, pattern] of Object.entries(patterns)) {
        const match = trimmed.match(pattern)
        if (match) {
          return {
            type,
            id: match[1],
            fullId: trimmed
          }
        }
      }

      return null
    }

    // Sugestões baseadas no input
    const suggestions = computed(() => {
      if (!searchQuery.value || searchQuery.value.length < 2) {
        return []
      }

      const results = []
      const query = searchQuery.value.toLowerCase()

      // Verificar se começa com prefixo conhecido
      if (query.startsWith('p-')) {
        results.push({
          id: 'pedido-hint',
          type: 'pedido',
          icon: '📋',
          title: 'Pedido de Compra',
          subtitle: 'Digite P-[número] para buscar um pedido',
          badge: 'Pedido'
        })
      } else if (query.startsWith('r-')) {
        results.push({
          id: 'rascunho-hint',
          type: 'rascunho',
          icon: '📝',
          title: 'Rascunho',
          subtitle: 'Digite R-[número] para buscar um rascunho',
          badge: 'Rascunho'
        })
      } else if (query.startsWith('f-')) {
        results.push({
          id: 'fornecedor-hint',
          type: 'fornecedor',
          icon: '🏢',
          title: 'Fornecedor',
          subtitle: 'Digite F-[número] para buscar um fornecedor',
          badge: 'Fornecedor'
        })
      } else if (query.startsWith('c-')) {
        results.push({
          id: 'cotacao-hint',
          type: 'cotacao',
          icon: '💰',
          title: 'Cotação',
          subtitle: 'Digite C-[número] para abrir a cotação (abre em modal)',
          badge: 'Cotação'
        })
      } else if (query.startsWith('u-')) {
        results.push({
          id: 'usuario-hint',
          type: 'usuario',
          icon: '👤',
          title: 'Usuário',
          subtitle: 'Digite U-[número] para ir à lista de usuários',
          badge: 'Usuário'
        })
      }

      return results
    })

    // Navegar para a entidade encontrada
    const navigateToEntity = (parsed) => {
      // Rotas especiais que usam query params
      if (parsed.type === 'cotacao') {
        // Cotações abrem em modal na página de cotações
        router.push({
          path: '/cotacoes',
          query: { openCotacao: parsed.id }
        })
        searchQuery.value = ''
        showSuggestions.value = false
        return
      }

      const routes = {
        pedido: `/pedidos/visualizar/${parsed.id}`,
        rascunho: `/pedidos/visualizar/${parsed.id}?tipo=rascunho`,
        fornecedor: `/fornecedores/visualizar/${parsed.id}`,
        usuario: `/usuarios`, // Usuários não têm view individual, vai para lista
        item: `/pedidos` // Itens não têm view própria, vai para pedidos
      }

      const route = routes[parsed.type]
      if (route) {
        router.push(route)
        searchQuery.value = ''
        showSuggestions.value = false
      } else {
        warning(`Tipo de entidade "${parsed.type}" não reconhecido.`)
      }
    }

    const handleSearch = () => {
      const parsed = parseSearchQuery(searchQuery.value)

      if (parsed) {
        navigateToEntity(parsed)
      } else {
        // Pesquisa livre - redireciona para página de pedidos com query
        if (searchQuery.value.trim()) {
          router.push({
            path: '/pedidos',
            query: { q: searchQuery.value.trim() }
          })
          searchQuery.value = ''
        }
      }
    }

    const handleInput = () => {
      showSuggestions.value = searchQuery.value.length >= 2
      showTips.value = searchQuery.value.length >= 1 && searchQuery.value.length < 3
    }

    const navigateToSuggestion = (suggestion) => {
      // Sugestões são apenas hints, não navegam diretamente
      showSuggestions.value = false
    }

    const clearSearch = () => {
      searchQuery.value = ''
      showSuggestions.value = false
      showTips.value = false
    }

    // Fechar sugestões ao clicar fora
    watch(searchQuery, (newVal) => {
      if (!newVal) {
        showSuggestions.value = false
        showTips.value = false
      }
    })

    return {
      searchQuery,
      showSuggestions,
      showTips,
      suggestions,
      handleSearch,
      handleInput,
      navigateToSuggestion,
      clearSearch
    }
  }
}
</script>

<style scoped>
.global-search {
  position: relative;
  width: 100%;
  max-width: 600px;
}

.search-container {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 16px;
  color: #9ca3af;
  pointer-events: none;
  z-index: 1;
}

.search-input {
  width: 100%;
  padding: 12px 48px 12px 48px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  font-size: 0.9375rem;
  background: #f9fafb;
  transition: all 0.2s;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  background: white;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-input::placeholder {
  color: #9ca3af;
}

.clear-button {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  color: #9ca3af;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.clear-button:hover {
  background: #f3f4f6;
  color: #374151;
}

/* Sugestões */
.suggestions-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  max-height: 300px;
  overflow-y: auto;
  z-index: 1000;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
  border-bottom: 1px solid #f3f4f6;
}

.suggestion-item:last-child {
  border-bottom: none;
}

.suggestion-item:hover {
  background: #f9fafb;
}

.suggestion-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.suggestion-content {
  flex: 1;
  min-width: 0;
}

.suggestion-title {
  font-weight: 600;
  color: #111827;
  font-size: 0.9375rem;
}

.suggestion-subtitle {
  font-size: 0.8125rem;
  color: #6b7280;
  margin-top: 2px;
}

.suggestion-badge {
  padding: 4px 12px;
  background: #e5e7eb;
  color: #374151;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  flex-shrink: 0;
}

/* Dicas */
.search-tips {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.tip-title {
  font-weight: 600;
  color: #374151;
  font-size: 0.875rem;
  margin-bottom: 8px;
}

.tip-examples {
  font-size: 0.8125rem;
  color: #6b7280;
  line-height: 1.6;
}

.tip-example {
  display: inline-block;
  padding: 2px 8px;
  background: #dbeafe;
  color: #1d4ed8;
  border-radius: 4px;
  font-weight: 600;
  font-family: 'Courier New', monospace;
  margin: 0 4px;
}

/* Responsividade */
@media (max-width: 768px) {
  .global-search {
    max-width: 100%;
  }

  .search-input {
    padding: 10px 44px 10px 44px;
    font-size: 0.875rem;
  }
}
</style>
