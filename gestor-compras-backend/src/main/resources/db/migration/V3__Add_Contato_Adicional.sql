-- =================================================================================================
-- V3__Add_Contato_Adicional.sql
--
-- Adiciona suporte a contatos adicionais nomeados para fornecedores.
-- =================================================================================================

CREATE TABLE contato_adicional (
    id BIGSERIAL PRIMARY KEY,
    contato_id BIGINT NOT NULL,
    nome_contato VARCHAR(100),
    tipo_contato VARCHAR(30) NOT NULL,
    valor_contato VARCHAR(255) NOT NULL,
    ordem_exibicao INTEGER DEFAULT 0,
    CONSTRAINT fk_contato_adicional_contato FOREIGN KEY (contato_id)
        REFERENCES contato(id) ON DELETE CASCADE
);

CREATE INDEX idx_contato_adicional_contato_id ON contato_adicional(contato_id);
CREATE INDEX idx_contato_adicional_tipo ON contato_adicional(tipo_contato);

