-- Adicionar coluna de status ao rascunho
ALTER TABLE rascunho ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ATIVO' NOT NULL;

-- Adicionar coluna para referenciar o pedido gerado
ALTER TABLE rascunho ADD COLUMN IF NOT EXISTS pedido_gerado_id BIGINT;

-- Criar índice para melhor performance
CREATE INDEX IF NOT EXISTS idx_rascunho_status ON rascunho(status);
CREATE INDEX IF NOT EXISTS idx_rascunho_pedido_gerado ON rascunho(pedido_gerado_id);
