-- ========================================
-- V19: Adicionar índices para otimização de performance
-- ========================================
-- Objetivo: Melhorar performance de queries frequentes
-- com índices em foreign keys e campos de busca
-- ========================================

-- Índices para tabela COTACAO
-- Melhora performance de JOINs com fornecedores e solicitações
CREATE INDEX IF NOT EXISTS idx_cotacao_solicitacao_pedido
    ON cotacao(solicitacao_de_pedido_id);

CREATE INDEX IF NOT EXISTS idx_cotacao_fornecedor_produto
    ON cotacao(fornecedor_produto_id);

CREATE INDEX IF NOT EXISTS idx_cotacao_fornecedor_servico
    ON cotacao(fornecedor_servico_id);

-- Índices para tabela ITEM_PEDIDO
-- Melhora performance de buscas por solicitação
CREATE INDEX IF NOT EXISTS idx_item_pedido_solicitacao
    ON item_pedido(solicitacao_de_pedido_id);

-- Índices para tabela COTACAO_ITEM
-- Melhora performance de JOINs e buscas
CREATE INDEX IF NOT EXISTS idx_cotacao_item_cotacao
    ON cotacao_item(cotacao_id);

CREATE INDEX IF NOT EXISTS idx_cotacao_item_pedido
    ON cotacao_item(item_pedido_id);

-- Índices para tabela SOLICITACAO_DE_PEDIDO
-- Melhora performance de filtros por status e ordenação por data
CREATE INDEX IF NOT EXISTS idx_solicitacao_status
    ON solicitacao_de_pedido(status);

CREATE INDEX IF NOT EXISTS idx_solicitacao_data_criacao
    ON solicitacao_de_pedido(data_criacao DESC);

-- Índices para tabela USERS
-- Melhora performance de busca por email e filtro por ativo
CREATE INDEX IF NOT EXISTS idx_user_email
    ON users(email);

CREATE INDEX IF NOT EXISTS idx_user_ativo
    ON users(ativo);

-- Índices para tabela HISTORICO_COTACAO
-- Melhora performance de consultas ao histórico
CREATE INDEX IF NOT EXISTS idx_historico_cotacao_id
    ON historico_cotacao(cotacao_id);

CREATE INDEX IF NOT EXISTS idx_historico_data_edicao
    ON historico_cotacao(data_edicao DESC);

-- Índices para tabela HISTORICO_PEDIDO
-- Melhora performance de consultas ao histórico
CREATE INDEX IF NOT EXISTS idx_historico_pedido_solicitacao
    ON historico_pedido(solicitacao_de_pedido_id);

CREATE INDEX IF NOT EXISTS idx_historico_pedido_data
    ON historico_pedido(data_modificacao DESC);

-- Índices compostos para queries mais complexas
-- Melhora performance de buscas por status específico ordenadas por data
CREATE INDEX IF NOT EXISTS idx_solicitacao_status_data
    ON solicitacao_de_pedido(status, data_criacao DESC);

-- Índices para anexos (se ainda não existirem)
CREATE INDEX IF NOT EXISTS idx_anexo_cotacao_cotacao
    ON anexo_cotacao(cotacao_id);

-- Comentários explicativos
COMMENT ON INDEX idx_cotacao_solicitacao_pedido IS 'Otimiza JOINs entre cotação e solicitação de pedido';
COMMENT ON INDEX idx_cotacao_fornecedor_produto IS 'Otimiza JOINs entre cotação e fornecedor de produto';
COMMENT ON INDEX idx_cotacao_fornecedor_servico IS 'Otimiza JOINs entre cotação e fornecedor de serviço';
COMMENT ON INDEX idx_item_pedido_solicitacao IS 'Otimiza busca de itens por solicitação';
COMMENT ON INDEX idx_cotacao_item_cotacao IS 'Otimiza busca de itens de cotação';
COMMENT ON INDEX idx_cotacao_item_pedido IS 'Otimiza JOIN entre cotacao_item e item_pedido';
COMMENT ON INDEX idx_solicitacao_status IS 'Otimiza filtros por status de solicitação';
COMMENT ON INDEX idx_solicitacao_data_criacao IS 'Otimiza ordenação por data de criação';
COMMENT ON INDEX idx_user_email IS 'Otimiza busca de usuários por email (login)';
COMMENT ON INDEX idx_user_ativo IS 'Otimiza filtros por usuários ativos/inativos';
COMMENT ON INDEX idx_historico_cotacao_id IS 'Otimiza busca de histórico por cotação';
COMMENT ON INDEX idx_historico_data_edicao IS 'Otimiza ordenação de histórico por data';
COMMENT ON INDEX idx_solicitacao_status_data IS 'Otimiza queries filtradas por status e ordenadas por data';
