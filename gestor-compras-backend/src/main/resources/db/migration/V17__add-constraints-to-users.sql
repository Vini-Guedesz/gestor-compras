-- Migration para adicionar constraints de tamanho e unicidade na tabela users
-- Garante integridade dos dados de usuários

-- Adicionar constraint de unicidade para email (se ainda não existir)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'users_email_unique'
    ) THEN
        ALTER TABLE users ADD CONSTRAINT users_email_unique UNIQUE (email);
    END IF;
END $$;

-- Comentários sobre as validações aplicadas:
-- nome: VARCHAR(100) NOT NULL - validado no DTO: 3-100 caracteres
-- email: VARCHAR(255) NOT NULL UNIQUE - validado no DTO: formato de email, máximo 255 caracteres
-- senha: VARCHAR(100) NOT NULL - validado no DTO: 8-100 caracteres com maiúscula, minúscula, número e especial
-- role: ENUM NOT NULL - validado no DTO: ADMIN, USUARIO, COMPRADOR ou APROVADOR
-- ativo: BOOLEAN NOT NULL DEFAULT TRUE - controla se usuário pode fazer login
