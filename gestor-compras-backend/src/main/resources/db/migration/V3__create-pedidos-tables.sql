-- Migration de pedidos
-- Autor: Sistema
-- Data: 2025-10-10
-- Descrição: Cria as tabelas de solicitações de pedido e itens de pedido

-- Tabela de solicitações de pedido
CREATE TABLE solicitacao_de_pedido (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(255),
    observacao VARCHAR(255),
    data_criacao TIMESTAMP
);

-- Tabela de itens de pedido
CREATE TABLE item_pedido (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    quantidade INT NOT NULL,
    descricao VARCHAR(255),
    observacao VARCHAR(255),
    solicitacao_de_pedido_id BIGINT,
    FOREIGN KEY (solicitacao_de_pedido_id) REFERENCES solicitacao_de_pedido(id)
);
