-- Adicionar referência da cotação ao pedido
ALTER TABLE cotacao ADD COLUMN solicitacao_de_pedido_id BIGINT;
ALTER TABLE cotacao ADD CONSTRAINT fk_cotacao_solicitacao
    FOREIGN KEY (solicitacao_de_pedido_id) REFERENCES solicitacao_de_pedido(id) ON DELETE CASCADE;

-- Criar tabela associativa para relacionamento N:N entre cotação e itens do pedido
CREATE TABLE cotacao_item_pedido (
    cotacao_id BIGINT NOT NULL,
    item_pedido_id BIGINT NOT NULL,
    PRIMARY KEY (cotacao_id, item_pedido_id),
    FOREIGN KEY (cotacao_id) REFERENCES cotacao(id) ON DELETE CASCADE,
    FOREIGN KEY (item_pedido_id) REFERENCES item_pedido(id) ON DELETE CASCADE
);

-- Migrar dados existentes: copiar item_pedido_id para a tabela associativa
INSERT INTO cotacao_item_pedido (cotacao_id, item_pedido_id)
SELECT id, item_pedido_id
FROM cotacao
WHERE item_pedido_id IS NOT NULL;

-- Preencher solicitacao_de_pedido_id baseado no item_pedido_id existente
UPDATE cotacao c
SET solicitacao_de_pedido_id = (
    SELECT ip.solicitacao_de_pedido_id
    FROM item_pedido ip
    WHERE ip.id = c.item_pedido_id
)
WHERE c.item_pedido_id IS NOT NULL;

-- Remover a coluna antiga item_pedido_id (relacionamento 1:1)
ALTER TABLE cotacao DROP CONSTRAINT IF EXISTS cotacao_item_pedido_id_fkey;
ALTER TABLE cotacao DROP COLUMN item_pedido_id;

-- Criar índices para melhorar performance
CREATE INDEX idx_cotacao_solicitacao_pedido ON cotacao(solicitacao_de_pedido_id);
CREATE INDEX idx_cotacao_item_pedido_cotacao ON cotacao_item_pedido(cotacao_id);
CREATE INDEX idx_cotacao_item_pedido_item ON cotacao_item_pedido(item_pedido_id);

-- Comentários
COMMENT ON TABLE cotacao_item_pedido IS 'Tabela associativa para relacionamento N:N entre cotações e itens de pedido';
