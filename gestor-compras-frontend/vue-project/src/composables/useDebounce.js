import { ref, watch } from 'vue'

/**
 * Composable para debounce de valores reativos
 * Útil para campos de busca e inputs que fazem requisições
 *
 * @param {Ref} value - Ref do Vue para observar
 * @param {number} delay - Delay em milissegundos (padrão: 300ms)
 * @returns {Ref} - Ref com valor debounced
 *
 * @example
 * const searchTerm = ref('')
 * const debouncedSearch = useDebounce(searchTerm, 500)
 *
 * watch(debouncedSearch, (newValue) => {
 *   // Faz busca na API
 *   fetchResults(newValue)
 * })
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
 * Útil para funções de callback
 *
 * @param {Function} fn - Função para debounce
 * @param {number} delay - Delay em milissegundos (padrão: 300ms)
 * @returns {Function} - Função debounced
 *
 * @example
 * const handleSearch = useDebounceFn((searchTerm) => {
 *   fetchResults(searchTerm)
 * }, 500)
 *
 * // Em template
 * <input @input="handleSearch($event.target.value)" />
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
 * Cancela o debounce e limpa o timeout
 * Útil em onUnmounted ou quando precisar cancelar manualmente
 *
 * @param {Function} debouncedFn - Função debounced criada com useDebounceFn
 */
export function cancelDebounce(debouncedFn) {
  if (debouncedFn.timeout) {
    clearTimeout(debouncedFn.timeout)
  }
}
