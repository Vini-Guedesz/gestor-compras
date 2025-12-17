-- Migration para adicionar campo de controle de modificação em usuários
-- Permite invalidar tokens JWT quando usuário é editado

-- Adicionar coluna last_modified_at
ALTER TABLE users ADD COLUMN last_modified_at TIMESTAMP NOT NULL DEFAULT NOW();

-- Criar índice para melhorar performance em consultas de validação de token
CREATE INDEX idx_users_last_modified ON users(last_modified_at);

-- Comentário descrevendo o propósito
COMMENT ON COLUMN users.last_modified_at IS 'Timestamp da última modificação do usuário. Usado para invalidar tokens JWT existentes após edição.';
