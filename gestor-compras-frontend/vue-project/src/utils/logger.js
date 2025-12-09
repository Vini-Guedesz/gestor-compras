/**
 * Sistema de Logging Condicional
 *
 * Logs são exibidos apenas em ambiente de desenvolvimento.
 * Em produção, erros podem ser enviados para serviço de monitoramento.
 */

const isDev = import.meta.env.DEV

export const logger = {
  /**
   * Log informativo (apenas em desenvolvimento)
   */
  log: (...args) => {
    if (isDev) {
      console.log(...args)
    }
  },

  /**
   * Log de erro
   * Em desenvolvimento: exibe no console
   * Em produção: pode ser enviado para serviço de monitoramento (Sentry, etc)
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
   */
  warn: (...args) => {
    if (isDev) {
      console.warn(...args)
    }
  },

  /**
   * Log de informação (sempre exibe, mesmo em produção)
   * Use apenas para informações críticas que precisam ser vistas
   */
  info: (...args) => {
    console.info(...args)
  },

  /**
   * Log de debug (apenas em desenvolvimento com flag específica)
   */
  debug: (...args) => {
    if (isDev && import.meta.env.VITE_ENABLE_DEBUG) {
      console.debug(...args)
    }
  }
}

export default logger
