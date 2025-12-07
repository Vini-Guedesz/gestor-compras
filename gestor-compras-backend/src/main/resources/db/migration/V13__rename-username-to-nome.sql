-- =====================================================
-- Migration V13: Renomear 'username' para 'nome'
-- Data: 2025-12-05
-- Objetivo: Eliminar confusão entre username (nome da pessoa) e email (usado para login)
-- =====================================================

-- CONTEXTO:
-- O sistema usa 'email' como identificador de login (via UserDetails.getUsername())
-- O campo 'username' apenas armazena o nome da pessoa (ex: "João Silva")
-- Esta migration renomeia 'username' → 'nome' para deixar claro o propósito

-- Renomear coluna
ALTER TABLE users RENAME COLUMN username TO nome;

-- Atualizar comentário da coluna
COMMENT ON COLUMN users.nome IS 'Nome completo do usuário (não é usado para login)';
COMMENT ON COLUMN users.email IS 'Email do usuário (usado como identificador de login via UserDetails.getUsername())';

-- Log informativo
DO $$
BEGIN
    RAISE NOTICE '====================================================';
    RAISE NOTICE 'Migration V13: CONCLUÍDA';
    RAISE NOTICE 'Coluna renomeada: username → nome';
    RAISE NOTICE 'Motivo: Eliminar confusão semântica';
    RAISE NOTICE 'Login: Continua usando EMAIL (não alterado)';
    RAISE NOTICE '====================================================';
END $$;
