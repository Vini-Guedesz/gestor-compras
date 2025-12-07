-- Criar tabela para anexos múltiplos de cotação de rascunho
CREATE TABLE IF NOT EXISTS anexo_cotacao_rascunho (
    id BIGSERIAL PRIMARY KEY,
    cotacao_rascunho_id BIGINT NOT NULL,
    ordem INTEGER DEFAULT 0,
    conteudo BYTEA NOT NULL,
    nome_arquivo VARCHAR(255),
    hash_sha256 VARCHAR(64), -- Hash SHA-256 para deduplificação
    CONSTRAINT fk_anexo_cotacao_rascunho FOREIGN KEY (cotacao_rascunho_id)
        REFERENCES cotacao_rascunho(id) ON DELETE CASCADE
);

-- Criar índices para melhor performance
CREATE INDEX IF NOT EXISTS idx_anexo_cotacao_rascunho_cotacao_id ON anexo_cotacao_rascunho(cotacao_rascunho_id);
CREATE INDEX IF NOT EXISTS idx_anexo_cotacao_rascunho_hash ON anexo_cotacao_rascunho(hash_sha256);

-- Comentário sobre deduplificação
COMMENT ON COLUMN anexo_cotacao_rascunho.hash_sha256 IS 'Hash SHA-256 do conteúdo do PDF para deduplificação. Permite verificar se o mesmo PDF já foi armazenado.';
