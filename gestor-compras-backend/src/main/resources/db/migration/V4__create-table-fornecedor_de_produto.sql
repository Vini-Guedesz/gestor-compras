CREATE TABLE fornecedor_de_produto (
    id BIGSERIAL PRIMARY KEY,
    razao_social VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    endereco_id BIGINT NOT NULL,
    contato_id BIGINT NOT NULL,
    inscricao_estadual VARCHAR(255),
    FOREIGN KEY (endereco_id) REFERENCES endereco(id),
    FOREIGN KEY (contato_id) REFERENCES contato(id)
);
