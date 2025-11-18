/**
 * Utilitários para determinar gênero baseado no usuário
 *
 * Este arquivo centraliza a lógica de determinação de gênero
 * para proporcionar tratamento adequado na aplicação.
 */

/**
 * Lista de nomes tipicamente femininos (amostra não exaustiva)
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
 * Determina o gênero baseado no usuário logado
 *
 * @param {Object} user - Objeto do usuário
 * @returns {string} - 'feminino', 'masculino' ou 'neutro'
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
 * Gera mensagem de boas-vindas com gênero correto
 *
 * @param {Object} user - Objeto do usuário
 * @param {string} tipo - Tipo de mensagem ('volta', 'simples')
 * @returns {string} - Mensagem formatada
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
 * Determina o cargo/role com gênero correto
 *
 * @param {Object} user - Objeto do usuário
 * @param {string} defaultRole - Role padrão caso não tenha especificado
 * @returns {string} - Role formatado com gênero correto
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
 * Gera mensagens de agradecimento com gênero correto
 *
 * @param {Object} user - Objeto do usuário
 * @returns {string} - Mensagem formatada
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
