CREATE TABLE solicitacao_de_pedido (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(255),
    observacao VARCHAR(255),
    data_criacao TIMESTAMP
);