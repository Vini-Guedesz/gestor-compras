-- Criação da tabela rascunho
CREATE TABLE rascunho (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    observacao TEXT,
    data_criacao TIMESTAMP NOT NULL,
    data_modificacao TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Criação da tabela item_rascunho
CREATE TABLE item_rascunho (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    quantidade INTEGER NOT NULL,
    descricao TEXT,
    observacao TEXT,
    rascunho_id BIGINT NOT NULL,
    FOREIGN KEY (rascunho_id) REFERENCES rascunho(id) ON DELETE CASCADE
);

-- Índices para melhorar performance
CREATE INDEX idx_rascunho_user_id ON rascunho(user_id);
CREATE INDEX idx_rascunho_data_criacao ON rascunho(data_criacao);
CREATE INDEX idx_item_rascunho_rascunho_id ON item_rascunho(rascunho_id);
