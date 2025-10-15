-- Migration de cotação
-- Autor: Sistema
-- Data: 2025-10-10
-- Descrição: Cria a tabela de cotações com relacionamentos para fornecedores

-- Tabela de cotações
CREATE TABLE cotacao (
    id BIGSERIAL PRIMARY KEY,
    item_pedido_id BIGINT NOT NULL,
    fornecedor_produto_id BIGINT,
    fornecedor_servico_id BIGINT,
    preco DECIMAL(19, 2) NOT NULL,
    prazo_entrega DATE,
    data_cotacao DATE,
    anexo_pdf BYTEA,
    caminho_anexo VARCHAR(255),
    FOREIGN KEY (item_pedido_id) REFERENCES item_pedido(id),
    FOREIGN KEY (fornecedor_produto_id) REFERENCES fornecedor_de_produto(id),
    FOREIGN KEY (fornecedor_servico_id) REFERENCES fornecedor_de_servico(id),
    -- Garante que apenas um tipo de fornecedor seja preenchido
    CONSTRAINT check_one_fornecedor CHECK (
        (fornecedor_produto_id IS NOT NULL AND fornecedor_servico_id IS NULL) OR
        (fornecedor_produto_id IS NULL AND fornecedor_servico_id IS NOT NULL)
    )
);
