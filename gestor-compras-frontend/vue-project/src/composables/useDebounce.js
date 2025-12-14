/**
 * @fileoverview Composable de Debounce
 * 
 * Fornece funções utilitárias para debounce (atraso na execução) de valores
 * reativos e callbacks. Essencial para otimizar performance em campos de busca,
 * inputs que fazem requisições à API, e qualquer interação que precise de delay.
 * 
 * @module composables/useDebounce
 * @requires vue
 * 
 * @description
 * Este composable implementa:
 * - Debounce de valores reativos (Ref)
 * - Debounce de funções callback
 * - Cancelamento manual de debounce
 * - Configuração de delay customizado
 * 
 * @example
 * // Debounce de valor reativo
 * import { ref } from 'vue'
 * import { useDebounce } from '@/composables/useDebounce'
 * 
 * const searchTerm = ref('')
 * const debouncedSearch = useDebounce(searchTerm, 500)
 * 
 * watch(debouncedSearch, (newValue) => {
 *   // Executa após 500ms de inatividade
 *   fetchResults(newValue)
 * })
 * 
 * @example
 * // Debounce de função
 * import { useDebounceFn } from '@/composables/useDebounce'
 * 
 * const handleSearch = useDebounceFn((value) => {
 *   console.log('Searching:', value)
 * }, 300)
 * 
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

import { ref, watch } from 'vue'

/**
 * Cria um valor reativo debounced a partir de outro valor reativo
 * 
 * @function useDebounce
 * @param {import('vue').Ref} value - Ref do Vue para observar
 * @param {number} [delay=300] - Delay em milissegundos (padrão: 300ms)
 * @returns {import('vue').Ref} Ref com valor debounced
 * 
 * @example
 * const searchTerm = ref('')
 * const debouncedSearch = useDebounce(searchTerm, 500)
 * 
 * watch(debouncedSearch, (newValue) => {
 *   // Faz busca na API após 500ms sem digitação
 *   fetchResults(newValue)
 * })
 * 
 * @example
 * // Template
 * <input v-model="searchTerm" placeholder="Buscar..." />
 * 
 * // Script
 * const searchTerm = ref('')
 * const debouncedSearch = useDebounce(searchTerm, 800)
 * 
 * @description
 * Observa mudanças no valor original e atualiza o valor debounced
 * apenas após o delay especificado sem novas alterações.
 * Útil para evitar múltiplas requisições durante digitação.
 */
export function useDebounce(value, delay = 300) {
  const debouncedValue = ref(value.value)
  let timeout = null

  watch(value, (newValue) => {
    if (timeout) {
      clearTimeout(timeout)
    }

    timeout = setTimeout(() => {
      debouncedValue.value = newValue
    }, delay)
  })

  return debouncedValue
}

/**
 * Cria uma função debounced que executa após um delay
 * 
 * @function useDebounceFn
 * @param {Function} fn - Função para aplicar debounce
 * @param {number} [delay=300] - Delay em milissegundos (padrão: 300ms)
 * @returns {Function} Função debounced
 * 
 * @example
 * const handleSearch = useDebounceFn((searchTerm) => {
 *   fetchResults(searchTerm)
 * }, 500)
 * 
 * // Em template
 * <input @input="handleSearch($event.target.value)" />
 * 
 * @example
 * // Com múltiplos argumentos
 * const handleUpdate = useDebounceFn((id, data) => {
 *   updateItem(id, data)
 * }, 1000)
 * 
 * handleUpdate(123, { name: 'Novo nome' })
 * 
 * @description
 * Retorna uma nova função que, quando chamada repetidamente,
 * só executa a função original após o delay sem novas chamadas.
 * Mantém o contexto (this) e argumentos originais.
 */
export function useDebounceFn(fn, delay = 300) {
  let timeout = null

  return function (...args) {
    if (timeout) {
      clearTimeout(timeout)
    }

    timeout = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

/**
 * Cancela o debounce e limpa o timeout pendente
 * 
 * @function cancelDebounce
 * @param {Function} debouncedFn - Função debounced criada com useDebounceFn
 * 
 * @example
 * const debouncedFn = useDebounceFn(() => console.log('Executado'), 1000)
 * 
 * // Chama a função
 * debouncedFn()
 * 
 * // Cancela antes de executar
 * cancelDebounce(debouncedFn)
 * 
 * @example
 * // Em lifecycle hook
 * import { onUnmounted } from 'vue'
 * 
 * const handleSearch = useDebounceFn(searchFn, 500)
 * 
 * onUnmounted(() => {
 *   cancelDebounce(handleSearch)
 * })
 * 
 * @description
 * Útil para limpar timeouts pendentes ao desmontar componente
 * ou quando precisar cancelar uma execução agendada.
 */
export function cancelDebounce(debouncedFn) {
  if (debouncedFn.timeout) {
    clearTimeout(debouncedFn.timeout)
  }
}
