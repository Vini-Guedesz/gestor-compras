-- Altera a tabela cotacao para adicionar relacionamentos com fornecedores
ALTER TABLE cotacao ADD COLUMN fornecedor_produto_id INT;
ALTER TABLE cotacao ADD COLUMN fornecedor_servico_id INT;

-- Migra dados existentes de fornecedor_id para fornecedor_produto_id
-- Assume que registros existentes são de fornecedores de produto
UPDATE cotacao
SET fornecedor_produto_id = fornecedor_id
WHERE fornecedor_id IS NOT NULL;

-- Adiciona constraints de foreign key
ALTER TABLE cotacao ADD CONSTRAINT fk_cotacao_fornecedor_produto
    FOREIGN KEY (fornecedor_produto_id) REFERENCES fornecedor_de_produto(id);

ALTER TABLE cotacao ADD CONSTRAINT fk_cotacao_fornecedor_servico
    FOREIGN KEY (fornecedor_servico_id) REFERENCES fornecedor_de_servico(id);

-- Adiciona constraint para garantir que apenas um tipo de fornecedor seja preenchido
ALTER TABLE cotacao ADD CONSTRAINT check_one_fornecedor
    CHECK (
        (fornecedor_produto_id IS NOT NULL AND fornecedor_servico_id IS NULL) OR
        (fornecedor_produto_id IS NULL AND fornecedor_servico_id IS NOT NULL)
    );

-- Remove a coluna antiga fornecedor_id
ALTER TABLE cotacao DROP COLUMN fornecedor_id;
