CREATE TABLE item_pedido (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    quantidade INT NOT NULL,
    descricao VARCHAR(255),
    observacao VARCHAR(255),
    solicitacao_de_pedido_id BIGINT,
    FOREIGN KEY (solicitacao_de_pedido_id) REFERENCES solicitacao_de_pedido(id)
);