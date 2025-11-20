-- Criar tabela para anexos múltiplos de cotação de rascunho
CREATE TABLE IF NOT EXISTS anexo_cotacao_rascunho (
    id BIGSERIAL PRIMARY KEY,
    cotacao_rascunho_id BIGINT NOT NULL,
    ordem INTEGER DEFAULT 0,
    conteudo BYTEA NOT NULL,
    nome_arquivo VARCHAR(255),
    CONSTRAINT fk_anexo_cotacao_rascunho FOREIGN KEY (cotacao_rascunho_id)
        REFERENCES cotacao_rascunho(id) ON DELETE CASCADE
);

-- Criar índice para melhor performance nas buscas
CREATE INDEX IF NOT EXISTS idx_anexo_cotacao_rascunho_cotacao_id ON anexo_cotacao_rascunho(cotacao_rascunho_id);

-- Migrar dados existentes de anexo_pdf para a nova tabela
INSERT INTO anexo_cotacao_rascunho (cotacao_rascunho_id, ordem, conteudo)
SELECT id, 0, anexo_pdf
FROM cotacao_rascunho
WHERE anexo_pdf IS NOT NULL;
