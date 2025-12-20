/**
 * @fileoverview Utilitários para Manipulação de Tokens JWT
 *
 * Funções centralizadas para validação, decodificação e manipulação
 * de JSON Web Tokens (JWT). Evita duplicação de lógica entre serviços.
 *
 * @module utils/jwtUtils
 *
 * @description
 * Este módulo implementa:
 * - Decodificação segura de tokens JWT
 * - Validação de expiração de tokens
 * - Extração de informações do payload
 * - Verificação de formato de token
 *
 * @example
 * import { isTokenValid, decodeToken, extractUserInfo } from '@/utils/jwtUtils'
 *
 * const token = sessionStorage.getItem('authToken')
 * if (isTokenValid(token)) {
 *   const userInfo = extractUserInfo(token)
 *   console.log(userInfo.email, userInfo.role)
 * }
 *
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

/**
 * @typedef {Object} TokenPayload
 * @property {string} sub - Subject (email do usuário)
 * @property {string} [nome] - Nome do usuário
 * @property {number} exp - Timestamp de expiração
 * @property {number} iat - Timestamp de emissão
 * @property {Array<Object>} [roles] - Roles do usuário
 */

/**
 * @typedef {Object} UserInfo
 * @property {string} email - Email do usuário
 * @property {string} nome - Nome do usuário
 * @property {string} role - Role principal (ADMIN ou USER)
 */

/**
 * Verifica se uma string tem o formato de um JWT válido
 *
 * @function isValidJWTFormat
 * @param {string} token - Token a ser verificado
 * @returns {boolean} true se o formato é válido
 *
 * @example
 * isValidJWTFormat('eyJ0...') // true
 * isValidJWTFormat('invalid') // false
 *
 * @description
 * JWT válido deve ter 3 partes separadas por '.'
 * Formato: header.payload.signature
 */
export function isValidJWTFormat(token) {
  if (!token || typeof token !== 'string') {
    return false
  }

  const parts = token.split('.')
  return parts.length === 3 && parts.every(part => part.length > 0)
}

/**
 * Decodifica o payload de um token JWT
 *
 * @function decodeToken
 * @param {string} token - Token JWT a ser decodificado
 * @returns {TokenPayload|null} Payload decodificado ou null se inválido
 *
 * @example
 * const payload = decodeToken(token)
 * if (payload) {
 *   console.log('Email:', payload.sub)
 *   console.log('Expira em:', new Date(payload.exp * 1000))
 * }
 *
 * @description
 * Decodifica apenas o payload (parte 2 do JWT).
 * ATENÇÃO: Não verifica a assinatura do token.
 * A verificação de assinatura deve ser feita pelo backend.
 */
export function decodeToken(token) {
  if (!isValidJWTFormat(token)) {
    return null
  }

  try {
    const payload = token.split('.')[1]
    const decoded = JSON.parse(atob(payload))
    return decoded
  } catch (error) {
    console.warn('Erro ao decodificar token JWT:', error.message)
    return null
  }
}

/**
 * Verifica se um token JWT está expirado
 *
 * @function isTokenExpired
 * @param {string} token - Token JWT a ser verificado
 * @returns {boolean} true se o token está expirado ou inválido
 *
 * @example
 * if (isTokenExpired(token)) {
 *   console.log('Token expirado, faça login novamente')
 * }
 *
 * @description
 * Compara o campo 'exp' do token com o timestamp atual.
 * Retorna true se o token está expirado ou se não pode ser decodificado.
 */
export function isTokenExpired(token) {
  const payload = decodeToken(token)

  if (!payload || !payload.exp) {
    return true
  }

  const now = Math.floor(Date.now() / 1000)
  return payload.exp < now
}

/**
 * Verifica se um token JWT é válido (formato correto e não expirado)
 *
 * @function isTokenValid
 * @param {string} token - Token JWT a ser validado
 * @returns {boolean} true se o token é válido
 *
 * @example
 * const token = sessionStorage.getItem('authToken')
 * if (!isTokenValid(token)) {
 *   logout()
 * }
 *
 * @description
 * Validação client-side completa:
 * 1. Verifica formato do JWT (3 partes separadas por '.')
 * 2. Decodifica o payload
 * 3. Verifica se não está expirado
 *
 * ATENÇÃO: Não verifica a assinatura. Apenas o backend pode fazer isso.
 */
export function isTokenValid(token) {
  return isValidJWTFormat(token) && !isTokenExpired(token)
}

/**
 * Extrai informações do usuário do token JWT
 *
 * @function extractUserInfo
 * @param {string} token - Token JWT
 * @returns {UserInfo|null} Informações do usuário ou null se token inválido
 *
 * @example
 * const userInfo = extractUserInfo(token)
 * if (userInfo) {
 *   console.log(`${userInfo.nome} <${userInfo.email}>`)
 *   console.log(`Role: ${userInfo.role}`)
 * }
 *
 * @description
 * Extrai e formata informações do usuário do payload JWT:
 * - sub: usado como email
 * - nome: nome completo (fallback para email se não existir)
 * - roles: array convertido para string (ADMIN ou USER)
 *
 * Formato esperado de roles: [{"authority": "ROLE_ADMIN"}]
 */
export function extractUserInfo(token) {
  const payload = decodeToken(token)

  if (!payload) {
    return null
  }

  // Extrai role do JWT (formato: [{"authority": "ROLE_ADMIN"}])
  let userRole = 'USER'
  if (payload.roles && Array.isArray(payload.roles) && payload.roles.length > 0) {
    const roleObj = payload.roles[0]
    if (roleObj && roleObj.authority) {
      // Remove o prefixo "ROLE_" se existir
      userRole = roleObj.authority.replace('ROLE_', '')
    }
  }

  return {
    email: payload.sub,
    nome: payload.nome || payload.sub,
    role: userRole
  }
}

/**
 * Obtém o tempo restante até a expiração do token em segundos
 *
 * @function getTimeUntilExpiration
 * @param {string} token - Token JWT
 * @returns {number} Segundos até expiração (negativo se já expirado)
 *
 * @example
 * const seconds = getTimeUntilExpiration(token)
 * if (seconds > 0 && seconds < 300) {
 *   console.warn('Token expira em menos de 5 minutos!')
 * }
 *
 * @description
 * Útil para implementar avisos de expiração próxima ou
 * renovação automática de tokens.
 */
export function getTimeUntilExpiration(token) {
  const payload = decodeToken(token)

  if (!payload || !payload.exp) {
    return -1
  }

  const now = Math.floor(Date.now() / 1000)
  return payload.exp - now
}

/**
 * Verifica se o token expira em breve (dentro de X segundos)
 *
 * @function isTokenExpiringSoon
 * @param {string} token - Token JWT
 * @param {number} [thresholdSeconds=300] - Limite em segundos (padrão: 5 minutos)
 * @returns {boolean} true se token expira em breve
 *
 * @example
 * if (isTokenExpiringSoon(token, 600)) {
 *   console.warn('Token expira em menos de 10 minutos!')
 *   refreshToken()
 * }
 *
 * @description
 * Útil para implementar renovação automática de tokens
 * antes que expirem completamente.
 */
export function isTokenExpiringSoon(token, thresholdSeconds = 300) {
  const timeLeft = getTimeUntilExpiration(token)
  return timeLeft > 0 && timeLeft < thresholdSeconds
}
