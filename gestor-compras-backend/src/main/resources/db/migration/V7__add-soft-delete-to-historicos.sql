-- Migration V7: Adicionar soft delete às tabelas de histórico
-- Data: 2025-11-28
-- Descrição: Adiciona coluna deleted_at para implementar soft delete
--            nas tabelas historico_pedido e historico_rascunho

-- Adicionar coluna deleted_at em historico_pedido
ALTER TABLE historico_pedido
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;

-- Adicionar índice para melhorar performance de queries com soft delete
CREATE INDEX IF NOT EXISTS idx_historico_pedido_deleted_at
ON historico_pedido(deleted_at);

-- Adicionar coluna deleted_at em historico_rascunho
ALTER TABLE historico_rascunho
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;

-- Adicionar índice para melhorar performance de queries com soft delete
CREATE INDEX IF NOT EXISTS idx_historico_rascunho_deleted_at
ON historico_rascunho(deleted_at);

-- Comentários nas colunas para documentação
COMMENT ON COLUMN historico_pedido.deleted_at IS 'Data e hora em que o registro foi deletado (soft delete). NULL indica registro ativo.';
COMMENT ON COLUMN historico_rascunho.deleted_at IS 'Data e hora em que o registro foi deletado (soft delete). NULL indica registro ativo.';
