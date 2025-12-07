-- Adiciona campos de auditoria na tabela cotacao
ALTER TABLE cotacao
ADD COLUMN numero_versao INTEGER DEFAULT 1,
ADD COLUMN foi_editada BOOLEAN DEFAULT FALSE,
ADD COLUMN data_ultima_edicao TIMESTAMP,
ADD COLUMN motivo_ultima_edicao VARCHAR(500),
ADD COLUMN editado_por VARCHAR(100);

-- Cria tabela para histórico de edições de cotações
CREATE TABLE historico_cotacao (
    id BIGSERIAL PRIMARY KEY,
    cotacao_id BIGINT NOT NULL,
    versao INTEGER NOT NULL,

    -- Dados da versão anterior
    preco_anterior DECIMAL(12, 2),
    prazo_anterior INTEGER,
    data_limite_anterior DATE,

    -- Dados da nova versão
    preco_novo DECIMAL(12, 2),
    prazo_novo INTEGER,
    data_limite_novo DATE,

    -- Informações de auditoria
    motivo_edicao VARCHAR(500) NOT NULL,
    editado_por VARCHAR(100),
    data_edicao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Hashes SHA-256 dos PDFs para auditoria (economia de 99.9% vs PDFs completos)
    hash_anexo_pdf_anterior VARCHAR(64),
    hash_anexo_pdf_novo VARCHAR(64),

    -- Índices para consultas eficientes
    CONSTRAINT fk_historico_cotacao_cotacao FOREIGN KEY (cotacao_id)
        REFERENCES cotacao(id) ON DELETE CASCADE
);

-- Índices para melhor performance
CREATE INDEX idx_historico_cotacao_cotacao_id ON historico_cotacao(cotacao_id);
CREATE INDEX idx_historico_cotacao_data_edicao ON historico_cotacao(data_edicao DESC);
CREATE INDEX idx_cotacao_foi_editada ON cotacao(foi_editada) WHERE foi_editada = TRUE;
CREATE INDEX idx_historico_cotacao_hash_anterior ON historico_cotacao(hash_anexo_pdf_anterior);
CREATE INDEX idx_historico_cotacao_hash_novo ON historico_cotacao(hash_anexo_pdf_novo);

-- Comentários para documentação
COMMENT ON TABLE historico_cotacao IS 'Armazena o histórico completo de edições de cotações para fins de auditoria';
COMMENT ON COLUMN historico_cotacao.versao IS 'Número da versão da cotação (1, 2, 3, etc.)';
COMMENT ON COLUMN historico_cotacao.motivo_edicao IS 'Motivo da edição fornecido pelo comprador (obrigatório para auditoria)';
COMMENT ON COLUMN historico_cotacao.hash_anexo_pdf_anterior IS 'Hash SHA-256 do PDF anterior (para auditoria sem duplicação). Reduz armazenamento de 10MB+ para 64 bytes.';
COMMENT ON COLUMN historico_cotacao.hash_anexo_pdf_novo IS 'Hash SHA-256 do PDF novo (para auditoria sem duplicação). Reduz armazenamento de 10MB+ para 64 bytes.';
COMMENT ON COLUMN cotacao.numero_versao IS 'Versão atual da cotação (incrementa a cada edição)';
COMMENT ON COLUMN cotacao.foi_editada IS 'Indica se a cotação já foi editada alguma vez';
