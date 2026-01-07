-- Adiciona coluna created_at para registrar data de cadastro dos usuários
-- V22: Add created_at column to users table

-- Adicionar coluna created_at (para novos usuários será preenchida automaticamente)
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

-- Para usuários existentes, usar last_modified_at como data de cadastro aproximada
UPDATE users SET created_at = last_modified_at WHERE created_at IS NULL;

-- Tornar a coluna NOT NULL após preencher os valores existentes
ALTER TABLE users ALTER COLUMN created_at SET NOT NULL;

-- Adicionar índice para consultas por data de cadastro
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);
