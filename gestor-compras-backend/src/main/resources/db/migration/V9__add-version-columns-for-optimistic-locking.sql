-- Adiciona colunas version para Optimistic Locking (JPA @Version)
-- Estas colunas são usadas pelo Hibernate para prevenir conflitos de atualização concorrente

-- Adiciona version à tabela solicitacao_de_pedido (somente se não existir)
ALTER TABLE solicitacao_de_pedido
    ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- Adiciona version à tabela cotacao (somente se não existir)
ALTER TABLE cotacao
    ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- Adiciona version à tabela rascunho (somente se não existir)
ALTER TABLE rascunho
    ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- Atualiza registros existentes para garantir que version não seja NULL
UPDATE solicitacao_de_pedido SET version = 0 WHERE version IS NULL;
UPDATE cotacao SET version = 0 WHERE version IS NULL;
UPDATE rascunho SET version = 0 WHERE version IS NULL;

-- Define as colunas como NOT NULL (somente se a coluna existir)
DO $$
BEGIN
    -- solicitacao_de_pedido.version
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'solicitacao_de_pedido' AND column_name = 'version') THEN
        ALTER TABLE solicitacao_de_pedido ALTER COLUMN version SET NOT NULL;
    END IF;

    -- cotacao.version
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'cotacao' AND column_name = 'version') THEN
        ALTER TABLE cotacao ALTER COLUMN version SET NOT NULL;
    END IF;

    -- rascunho.version
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'rascunho' AND column_name = 'version') THEN
        ALTER TABLE rascunho ALTER COLUMN version SET NOT NULL;
    END IF;
END $$;

-- Adiciona comentários (apenas documentação, não afeta se coluna já existir)
COMMENT ON COLUMN solicitacao_de_pedido.version IS 'Versão para controle de concorrência otimista (JPA @Version)';
COMMENT ON COLUMN cotacao.version IS 'Versão para controle de concorrência otimista (JPA @Version)';
COMMENT ON COLUMN rascunho.version IS 'Versão para controle de concorrência otimista (JPA @Version)';
