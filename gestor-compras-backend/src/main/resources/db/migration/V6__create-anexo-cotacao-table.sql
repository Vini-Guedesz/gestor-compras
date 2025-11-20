-- Criar tabela para anexos múltiplos de cotação de pedido
CREATE TABLE IF NOT EXISTS anexo_cotacao (
    id BIGSERIAL PRIMARY KEY,
    cotacao_id BIGINT NOT NULL,
    ordem INTEGER DEFAULT 0,
    conteudo BYTEA NOT NULL,
    nome_arquivo VARCHAR(255),
    CONSTRAINT fk_anexo_cotacao FOREIGN KEY (cotacao_id)
        REFERENCES cotacao(id) ON DELETE CASCADE
);

-- Criar índice para melhor performance nas buscas
CREATE INDEX IF NOT EXISTS idx_anexo_cotacao_cotacao_id ON anexo_cotacao(cotacao_id);

-- Migrar dados existentes de anexo_pdf para a nova tabela
INSERT INTO anexo_cotacao (cotacao_id, ordem, conteudo)
SELECT id, 0, anexo_pdf
FROM cotacao
WHERE anexo_pdf IS NOT NULL;
