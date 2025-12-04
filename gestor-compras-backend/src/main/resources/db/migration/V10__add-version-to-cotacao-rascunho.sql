-- Adiciona coluna version em cotacao_rascunho para Optimistic Locking

ALTER TABLE cotacao_rascunho
    ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- Atualiza registros existentes
UPDATE cotacao_rascunho SET version = 0 WHERE version IS NULL;

-- Define como NOT NULL
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'cotacao_rascunho' AND column_name = 'version') THEN
        ALTER TABLE cotacao_rascunho ALTER COLUMN version SET NOT NULL;
    END IF;
END $$;

COMMENT ON COLUMN cotacao_rascunho.version IS 'Versão para controle de concorrência otimista (JPA @Version)';
