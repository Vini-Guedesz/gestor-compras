/**
 * @fileoverview Sistema de Logging Condicional por Ambiente
 * 
 * Wrapper inteligente do console que controla logs baseado no ambiente
 * (desenvolvimento/produção). Facilita debug e prepara integração com
 * serviços de monitoramento como Sentry, LogRocket, etc.
 * 
 * @module utils/logger
 * @requires vite
 * 
 * @description
 * Este módulo implementa:
 * - Logs condicionais baseados em import.meta.env.DEV
 * - Supressão automática de logs em produção
 * - Preparação para integração com serviços de monitoramento
 * - Debug mode com flag VITE_ENABLE_DEBUG
 * - API idêntica ao console nativo (drop-in replacement)
 * 
 * @example
 * // Usar ao invés de console
 * import logger from '@/utils/logger'
 * 
 * logger.log('Info de desenvolvimento') // Só mostra em DEV
 * logger.error('Erro crítico') // Mostra em DEV, pode enviar para Sentry em PROD
 * logger.warn('Aviso importante') // Só mostra em DEV
 * logger.info('Info crítica') // Mostra sempre (DEV e PROD)
 * logger.debug('Debug detalhado') // Só com VITE_ENABLE_DEBUG=true
 * 
 * @example
 * // Configuração .env para debug
 * // .env.development
 * VITE_ENABLE_DEBUG=true
 * 
 * // .env.production
 * VITE_ENABLE_DEBUG=false
 * 
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

/**
 * Flag de ambiente de desenvolvimento
 * @constant {boolean}
 * @private
 */
const isDev = import.meta.env.DEV

/**
 * @namespace logger
 * @description
 * Objeto com métodos de logging inteligente.
 * Drop-in replacement para console com controle por ambiente.
 */
export const logger = {
  /**
   * Log informativo de desenvolvimento
   * 
   * @function log
   * @param {...*} args - Argumentos a serem logados
   * 
   * @example
   * logger.log('User:', user)
   * logger.log('Fetching data...', { url, params })
   * 
   * @description
   * Exibe apenas em desenvolvimento (DEV).
   * Em produção, não faz nada (performance).
   * Use para logs de debugging e desenvolvimento.
   */
  log: (...args) => {
    if (isDev) {
      console.log(...args)
    }
  },

  /**
   * Log de erro com suporte a monitoramento
   * 
   * @function error
   * @param {...*} args - Erro e informações adicionais
   * 
   * @example
   * logger.error('API Error:', error)
   * logger.error('Failed to fetch', { endpoint, status })
   * 
   * @description
   * Desenvolvimento: Exibe no console.error
   * Produção: Silencioso (pronto para integração com Sentry)
   * 
   * TODO: Integrar com serviço de monitoramento:
   * - Sentry.captureException(args[0])
   * - LogRocket.captureException(args[0])
   * - Bugsnag.notify(args[0])
   */
  error: (...args) => {
    if (isDev) {
      console.error(...args)
    } else {
      // TODO: Integrar com serviço de monitoramento (Sentry, LogRocket, etc)
      // Exemplo: Sentry.captureException(args[0])
    }
  },

  /**
   * Log de aviso
   * 
   * @function warn
   * @param {...*} args - Avisos e informações
   * 
   * @example
   * logger.warn('Deprecated API used')
   * logger.warn('Missing optional field:', field)
   * 
   * @description
   * Exibe apenas em desenvolvimento.
   * Use para avisos não críticos, APIs depreciadas, etc.
   */
  warn: (...args) => {
    if (isDev) {
      console.warn(...args)
    }
  },

  /**
   * Log de informação crítica
   * 
   * @function info
   * @param {...*} args - Informações a serem logadas
   * 
   * @example
   * logger.info('App initialized')
   * logger.info('User logged in:', userId)
   * 
   * @description
   * Exibe SEMPRE (desenvolvimento E produção).
   * Use APENAS para informações críticas que precisam
   * ser vistas em produção (ex: inicialização, login).
   */
  info: (...args) => {
    console.info(...args)
  },

  /**
   * Log de debug detalhado
   * 
   * @function debug
   * @param {...*} args - Informações de debug
   * 
   * @example
   * logger.debug('State changed:', oldState, newState)
   * logger.debug('API Response:', response.data)
   * 
   * @description
   * Exibe apenas em desenvolvimento E com flag VITE_ENABLE_DEBUG=true.
   * Use para logs muito detalhados que poluem o console.
   * 
   * Ativar: criar arquivo .env.development.local:
   * VITE_ENABLE_DEBUG=true
   */
  debug: (...args) => {
    if (isDev && import.meta.env.VITE_ENABLE_DEBUG) {
      console.debug(...args)
    }
  }
}

export default logger
