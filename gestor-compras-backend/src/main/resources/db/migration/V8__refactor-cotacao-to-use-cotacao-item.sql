-- Migration V8: Refatorar relacionamento de Cotacao para resolver Bug #5
-- Data: 2025-11-28
-- Descrição: Substitui relacionamento N:N direto por entidade intermediária CotacaoItem
--            que permite armazenar preço unitário e quantidade por item

-- =====================================================
-- ETAPA 1: Criar nova tabela cotacao_item
-- =====================================================

CREATE TABLE cotacao_item (
    id BIGSERIAL PRIMARY KEY,
    cotacao_id BIGINT NOT NULL,
    item_pedido_id BIGINT NOT NULL,
    preco_unitario DECIMAL(19, 2) NOT NULL,
    quantidade INTEGER NOT NULL,
    observacao VARCHAR(1000),

    CONSTRAINT fk_cotacao_item_cotacao FOREIGN KEY (cotacao_id)
        REFERENCES cotacao(id) ON DELETE CASCADE,
    CONSTRAINT fk_cotacao_item_item_pedido FOREIGN KEY (item_pedido_id)
        REFERENCES item_pedido(id) ON DELETE CASCADE,
    CONSTRAINT uk_cotacao_item UNIQUE (cotacao_id, item_pedido_id),
    CONSTRAINT chk_cotacao_item_preco_positivo CHECK (preco_unitario > 0),
    CONSTRAINT chk_cotacao_item_quantidade_positiva CHECK (quantidade > 0)
);

-- Índices para performance
CREATE INDEX idx_cotacao_item_cotacao_id ON cotacao_item(cotacao_id);
CREATE INDEX idx_cotacao_item_item_pedido_id ON cotacao_item(item_pedido_id);

-- =====================================================
-- ETAPA 2: Migrar dados existentes
-- =====================================================

-- Migra dados de cotacao_item_pedido para cotacao_item
-- Calcula preço unitário aproximado dividindo o preço total da cotação pelo número de itens
-- Usa a quantidade do item_pedido como quantidade cotada

INSERT INTO cotacao_item (cotacao_id, item_pedido_id, preco_unitario, quantidade, observacao)
SELECT
    cip.cotacao_id,
    cip.item_pedido_id,
    -- Calcula preço unitário: (preço total da cotação) / (número de itens na cotação)
    -- Se não houver preço ou houver divisão por zero, usa 0.01 como fallback
    COALESCE(
        c.preco / NULLIF((SELECT COUNT(*) FROM cotacao_item_pedido WHERE cotacao_id = c.id), 0),
        0.01
    ) as preco_unitario,
    -- Usa a quantidade do item_pedido
    ip.quantidade,
    -- Adiciona observação indicando que foi migrado
    'Migrado automaticamente de cotacao_item_pedido' as observacao
FROM cotacao_item_pedido cip
INNER JOIN cotacao c ON c.id = cip.cotacao_id
INNER JOIN item_pedido ip ON ip.id = cip.item_pedido_id;

-- =====================================================
-- ETAPA 3: Adicionar coluna version para otimistic locking
-- =====================================================

-- Adiciona coluna version na tabela cotacao (se não existir)
ALTER TABLE cotacao
ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0;

-- Atualiza todos os registros existentes para version = 0
UPDATE cotacao SET version = 0 WHERE version IS NULL;

-- =====================================================
-- ETAPA 4: Remover tabela antiga
-- =====================================================

-- Drop da tabela antiga de relacionamento N:N
DROP TABLE IF EXISTS cotacao_item_pedido;

-- =====================================================
-- ETAPA 5: Comentários para documentação
-- =====================================================

COMMENT ON TABLE cotacao_item IS 'Entidade intermediária entre Cotacao e ItemPedido. Resolve Bug #5 permitindo armazenar preço unitário e quantidade específicos por item em cada cotação.';
COMMENT ON COLUMN cotacao_item.preco_unitario IS 'Preço unitário do item nesta cotação específica';
COMMENT ON COLUMN cotacao_item.quantidade IS 'Quantidade cotada (pode diferir da quantidade solicitada)';
COMMENT ON COLUMN cotacao_item.observacao IS 'Observações específicas sobre este item nesta cotação';
COMMENT ON COLUMN cotacao.preco IS 'DEPRECATED: Preço total da cotação. Use o cálculo a partir dos itens individuais.';
COMMENT ON COLUMN cotacao.version IS 'Versão para otimistic locking (JPA @Version)';

-- =====================================================
-- VERIFICAÇÃO (comentado - descomente para debug)
-- =====================================================

-- SELECT
--     'Cotacoes migradas' as tipo,
--     COUNT(DISTINCT cotacao_id) as total
-- FROM cotacao_item
-- UNION ALL
-- SELECT
--     'Itens migrados' as tipo,
--     COUNT(*) as total
-- FROM cotacao_item;
