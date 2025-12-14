/**
 * @fileoverview Utilitários de Determinação e Tratamento de Gênero
 * 
 * Fornece funções para inferir gênero do usuário e gerar mensagens
 * personalizadas com tratamento adequado (masculino/feminino/neutro).
 * Melhora a experiência do usuário com linguagem inclusiva e correta.
 * 
 * @module utils/genderUtils
 * 
 * @description
 * Este módulo implementa:
 * - Inferência de gênero por nome (lista de nomes femininos)
 * - Suporte a campo explícito de gênero no usuário
 * - Mensagens de boas-vindas personalizadas
 * - Cargos/roles com gênero correto
 * - Mensagens de agradecimento personalizadas
 * - Fallback para gênero neutro quando indefinido
 * 
 * @example
 * // Uso básico
 * import { getUserGender, getWelcomeMessage } from '@/utils/genderUtils'
 * 
 * const user = { username: 'Maria Silva' }
 * const genero = getUserGender(user) // 'feminino'
 * const mensagem = getWelcomeMessage(user, 'volta') // 'Bem-vinda de volta'
 * 
 * @example
 * // Com campo explícito
 * const user = { username: 'João', genero: 'masculino' }
 * getUserGender(user) // 'masculino'
 * getWelcomeMessage(user, 'simples') // 'Bem-vindo'
 * 
 * @author Sistema Gestor de Compras
 * @version 1.0.0
 */

/**
 * Lista de nomes tipicamente femininos para inferência de gênero
 * 
 * @constant {string[]}
 * @private
 * 
 * @description
 * Array com +50 nomes femininos comuns no Brasil.
 * Usado como fallback quando usuário não tem campo de gênero explícito.
 * Lista não exaustiva - priorize sempre campo explícito no backend.
 */
const NOMES_FEMININOS = [
  'ana', 'maria', 'joana', 'patricia', 'fernanda', 'carla', 'lucia', 'juliana',
  'amanda', 'gabriela', 'beatriz', 'carolina', 'daniela', 'barbara', 'cristina',
  'sandra', 'monica', 'andrea', 'paula', 'renata', 'claudia', 'simone',
  'vanessa', 'tatiana', 'marcia', 'silvia', 'rosana', 'adriana', 'michele',
  'fabiana', 'priscila', 'raquel', 'sabrina', 'lilian', 'viviane', 'camila',
  'roberta', 'eliane', 'rosangela', 'marlene', 'regina', 'vera', 'sonia',
  'rita', 'marta', 'rosa', 'fatima', 'terezinha', 'antonia', 'francisca',
  'apparecida', 'conceicao', 'isabel', 'irene', 'jose', 'janete', 'cleide'
]

/**
 * Determina o gênero do usuário
 * 
 * @function getUserGender
 * @param {Object} user - Objeto do usuário autenticado
 * @param {string} [user.genero] - Gênero explícito do usuário (preferencial)
 * @param {string} [user.gender] - Campo alternativo de gênero
 * @param {string} [user.username] - Nome de usuário para inferência
 * @param {string} [user.name] - Nome alternativo para inferência
 * @returns {'feminino'|'masculino'|'neutro'} Gênero determinado
 * 
 * @example
 * // Com campo explícito (melhor prática)
 * const user = { username: 'João', genero: 'masculino' }
 * getUserGender(user) // 'masculino'
 * 
 * @example
 * // Inferência por nome
 * const user = { username: 'Maria Silva' }
 * getUserGender(user) // 'feminino'
 * 
 * @example
 * // Sem informação suficiente
 * getUserGender(null) // 'neutro'
 * getUserGender({ username: 'Admin' }) // 'masculino' (default)
 * 
 * @description
 * Estratégia de determinação (ordem de prioridade):
 * 1. Campo user.genero (backend)
 * 2. Campo user.gender (alternativo)
 * 3. Inferência pelo primeiro nome
 * 4. Default para 'masculino'
 * 5. 'neutro' se user for null/undefined
 */
export function getUserGender(user) {
  if (!user) return 'neutro'

  // Se houver campo de gênero específico no usuário, usar ele
  if (user.genero) {
    return user.genero.toLowerCase()
  }

  if (user.gender) {
    return user.gender.toLowerCase()
  }

  // Caso contrário, inferir pelo nome
  const nomeCompleto = (user.username || user.name || '').toLowerCase()
  const primeiroNome = nomeCompleto.split(' ')[0]

  // Verificar se o primeiro nome está na lista de nomes femininos
  if (NOMES_FEMININOS.some(nome => primeiroNome.includes(nome))) {
    return 'feminino'
  }

  // Default para masculino se não conseguir determinar
  return 'masculino'
}

/**
 * Gera mensagem de boas-vindas personalizada
 * 
 * @function getWelcomeMessage
 * @param {Object} user - Objeto do usuário autenticado
 * @param {'volta'|'simples'} [tipo='volta'] - Tipo de mensagem
 * @returns {string} Mensagem formatada com gênero correto
 * 
 * @example
 * // Mensagem de retorno (default)
 * const user = { username: 'Maria' }
 * getWelcomeMessage(user) // 'Bem-vinda de volta'
 * getWelcomeMessage(user, 'volta') // 'Bem-vinda de volta'
 * 
 * @example
 * // Mensagem simples
 * const user = { username: 'João' }
 * getWelcomeMessage(user, 'simples') // 'Bem-vindo'
 * 
 * @example
 * // Gênero indefinido
 * const user = { username: 'Admin' }
 * getWelcomeMessage(user, 'volta') // 'Bem-vindo(a) de volta'
 * 
 * @description
 * Retorna mensagens de boas-vindas adequadas ao gênero:
 * - Feminino: "Bem-vinda" / "Bem-vinda de volta"
 * - Masculino: "Bem-vindo" / "Bem-vindo de volta"
 * - Neutro: "Bem-vindo(a)" / "Bem-vindo(a) de volta"
 */
export function getWelcomeMessage(user, tipo = 'volta') {
  const genero = getUserGender(user)

  if (tipo === 'volta') {
    if (genero === 'feminino') {
      return 'Bem-vinda de volta'
    } else if (genero === 'masculino') {
      return 'Bem-vindo de volta'
    }
    return 'Bem-vindo(a) de volta'
  }

  // Tipo simples
  if (genero === 'feminino') {
    return 'Bem-vinda'
  } else if (genero === 'masculino') {
    return 'Bem-vindo'
  }
  return 'Bem-vindo(a)'
}

/**
 * Determina o cargo com gênero adequado
 * 
 * @function getUserRole
 * @param {Object} user - Objeto do usuário autenticado
 * @param {string} [user.role] - Role do backend (ADMIN/USER)
 * @param {string} [defaultRole='Compras'] - Cargo padrão quando não especificado
 * @returns {string} Cargo formatado com gênero correto
 * 
 * @example
 * // Com role do backend
 * const admin = { username: 'Maria', role: 'ADMIN' }
 * getUserRole(admin) // 'Administrador' (não aplica gênero)
 * 
 * @example
 * // Role de Compras (default)
 * const user = { username: 'João' }
 * getUserRole(user) // 'Gestor de Compras'
 * 
 * const user2 = { username: 'Ana' }
 * getUserRole(user2) // 'Gestora de Compras'
 * 
 * @example
 * // Role customizada
 * const user = { username: 'Maria' }
 * getUserRole(user, 'Vendas') // 'Vendas' (retorna sem alteração)
 * 
 * @description
 * Lógica de determinação:
 * 1. Se user.role = 'ADMIN' → 'Administrador'
 * 2. Se user.role = 'USER' → 'Usuário'
 * 3. Se defaultRole = 'Compras' → aplica gênero (Gestor/Gestora)
 * 4. Caso contrário → retorna defaultRole sem alteração
 */
export function getUserRole(user, defaultRole = 'Compras') {
  // Se o usuário tem role do backend (ADMIN ou USER), retornar ele
  if (user?.role === 'ADMIN') {
    return 'Administrador'
  } else if (user?.role === 'USER') {
    return 'Usuário'
  }

  const genero = getUserGender(user)

  if (defaultRole === 'Compras') {
    if (genero === 'feminino') {
      return 'Gestora de Compras'
    } else if (genero === 'masculino') {
      return 'Gestor de Compras'
    }
    return 'Gestor(a) de Compras'
  }

  return defaultRole
}

/**
 * Gera mensagem de agradecimento personalizada
 * 
 * @function getThankYouMessage
 * @param {Object} user - Objeto do usuário autenticado
 * @returns {string} Mensagem formatada com gênero correto
 * 
 * @example
 * const user = { username: 'Maria' }
 * getThankYouMessage(user) // 'Obrigada'
 * 
 * @example
 * const user = { username: 'João' }
 * getThankYouMessage(user) // 'Obrigado'
 * 
 * @example
 * const user = { username: 'Admin' }
 * getThankYouMessage(user) // 'Obrigado(a)'
 * 
 * @description
 * Retorna agradecimento adequado ao gênero:
 * - Feminino: "Obrigada"
 * - Masculino: "Obrigado"
 * - Neutro: "Obrigado(a)"
 */
export function getThankYouMessage(user) {
  const genero = getUserGender(user)

  if (genero === 'feminino') {
    return 'Obrigada'
  } else if (genero === 'masculino') {
    return 'Obrigado'
  }
  return 'Obrigado(a)'
}
