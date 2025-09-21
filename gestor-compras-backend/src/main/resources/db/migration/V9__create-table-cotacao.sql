CREATE TABLE cotacao (
    id BIGSERIAL PRIMARY KEY,
    fornecedor_id INT NOT NULL,
    item_pedido_id BIGINT NOT NULL,
    preco DECIMAL(19, 2) NOT NULL,
    prazo_entrega DATE,
    data_cotacao DATE,
    anexo_pdf BYTEA,
    caminho_anexo VARCHAR(255),
    FOREIGN KEY (item_pedido_id) REFERENCES item_pedido(id)
);
