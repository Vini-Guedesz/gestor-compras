-- Migration consolidada - Pedidos, Rascunhos, Cotações e Histórico
-- Autor: Sistema
-- Data: 2025-11-19
-- Descrição: Cria todas as tabelas relacionadas ao fluxo de pedidos

-- =====================================================
-- TABELAS DE RASCUNHO
-- =====================================================

-- Tabela de rascunhos (drafts)
CREATE TABLE rascunho (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    observacao TEXT,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_modificacao TIMESTAMP,
    proximo_numero_item INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Tabela de itens do rascunho
CREATE TABLE item_rascunho (
    id BIGSERIAL PRIMARY KEY,
    numero_item INTEGER NOT NULL,
    nome VARCHAR(255) NOT NULL,
    quantidade INTEGER NOT NULL,
    descricao TEXT,
    observacao TEXT,
    rascunho_id BIGINT NOT NULL,
    FOREIGN KEY (rascunho_id) REFERENCES rascunho(id) ON DELETE CASCADE,
    UNIQUE (rascunho_id, numero_item)
);

-- Tabela para controlar números de itens disponíveis para reutilização
CREATE TABLE numero_item_disponivel (
    id BIGSERIAL PRIMARY KEY,
    rascunho_id BIGINT NOT NULL,
    numero_item INTEGER NOT NULL,
    FOREIGN KEY (rascunho_id) REFERENCES rascunho(id) ON DELETE CASCADE,
    UNIQUE (rascunho_id, numero_item)
);

-- Índices para rascunho
CREATE INDEX idx_rascunho_user_id ON rascunho(user_id);
CREATE INDEX idx_rascunho_data_criacao ON rascunho(data_criacao);
CREATE INDEX idx_item_rascunho_rascunho_id ON item_rascunho(rascunho_id);
CREATE INDEX idx_numero_item_disponivel_rascunho ON numero_item_disponivel(rascunho_id);

-- =====================================================
-- TABELA DE HISTÓRICO DE RASCUNHO
-- =====================================================

-- Tabela de histórico de rascunhos
CREATE TABLE historico_rascunho (
    id BIGSERIAL PRIMARY KEY,
    rascunho_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    data_modificacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_acao VARCHAR(50) NOT NULL,
    descricao TEXT NOT NULL,
    numero_item INTEGER,
    nome_item VARCHAR(255),
    detalhes TEXT,
    FOREIGN KEY (rascunho_id) REFERENCES rascunho(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índices para histórico de rascunho
CREATE INDEX idx_historico_rascunho_rascunho_id ON historico_rascunho(rascunho_id);
CREATE INDEX idx_historico_rascunho_user_id ON historico_rascunho(user_id);
CREATE INDEX idx_historico_rascunho_data ON historico_rascunho(data_modificacao);

-- =====================================================
-- TABELAS DE COTAÇÃO DO RASCUNHO
-- =====================================================

-- Tabela de cotações para rascunhos
CREATE TABLE cotacao_rascunho (
    id BIGSERIAL PRIMARY KEY,
    rascunho_id BIGINT NOT NULL,
    fornecedor_produto_id INTEGER,
    fornecedor_servico_id INTEGER,
    preco DECIMAL(19, 2) NOT NULL,
    prazo_em_dias_uteis INTEGER,
    data_limite DATE,
    anexo_pdf BYTEA,
    caminho_anexo VARCHAR(500),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cotacao_rascunho_rascunho FOREIGN KEY (rascunho_id) REFERENCES rascunho(id) ON DELETE CASCADE,
    CONSTRAINT fk_cotacao_rascunho_fornecedor_produto FOREIGN KEY (fornecedor_produto_id) REFERENCES fornecedor_de_produto(id),
    CONSTRAINT fk_cotacao_rascunho_fornecedor_servico FOREIGN KEY (fornecedor_servico_id) REFERENCES fornecedor_de_servico(id),
    CONSTRAINT chk_cotacao_rascunho_fornecedor CHECK (
        (fornecedor_produto_id IS NOT NULL AND fornecedor_servico_id IS NULL) OR
        (fornecedor_produto_id IS NULL AND fornecedor_servico_id IS NOT NULL)
    )
);

-- Tabela de relacionamento entre cotações de rascunho e itens de rascunho (muitos para muitos)
CREATE TABLE cotacao_rascunho_item (
    cotacao_rascunho_id BIGINT NOT NULL,
    item_rascunho_id BIGINT NOT NULL,
    PRIMARY KEY (cotacao_rascunho_id, item_rascunho_id),
    CONSTRAINT fk_cotacao_rascunho_item_cotacao FOREIGN KEY (cotacao_rascunho_id) REFERENCES cotacao_rascunho(id) ON DELETE CASCADE,
    CONSTRAINT fk_cotacao_rascunho_item_item FOREIGN KEY (item_rascunho_id) REFERENCES item_rascunho(id) ON DELETE CASCADE
);

-- Índices para cotação de rascunho
CREATE INDEX idx_cotacao_rascunho_rascunho_id ON cotacao_rascunho(rascunho_id);
CREATE INDEX idx_cotacao_rascunho_fornecedor_produto ON cotacao_rascunho(fornecedor_produto_id);
CREATE INDEX idx_cotacao_rascunho_fornecedor_servico ON cotacao_rascunho(fornecedor_servico_id);

-- =====================================================
-- TABELAS DE PEDIDO (SOLICITAÇÃO)
-- =====================================================

-- Tabela de solicitações de pedido
CREATE TABLE solicitacao_de_pedido (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDENTE',
    observacao TEXT,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de itens de pedido
CREATE TABLE item_pedido (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    quantidade INT NOT NULL,
    descricao TEXT,
    observacao TEXT,
    solicitacao_de_pedido_id BIGINT NOT NULL,
    FOREIGN KEY (solicitacao_de_pedido_id) REFERENCES solicitacao_de_pedido(id) ON DELETE CASCADE
);

-- Índices para pedido
CREATE INDEX idx_item_pedido_solicitacao_id ON item_pedido(solicitacao_de_pedido_id);

-- =====================================================
-- TABELAS DE COTAÇÃO DO PEDIDO
-- =====================================================

-- Tabela de cotações do pedido
CREATE TABLE cotacao (
    id BIGSERIAL PRIMARY KEY,
    solicitacao_de_pedido_id BIGINT NOT NULL,
    fornecedor_produto_id INTEGER,
    fornecedor_servico_id INTEGER,
    preco DECIMAL(19, 2) NOT NULL,
    prazo_em_dias_uteis INTEGER,
    data_limite DATE,
    anexo_pdf BYTEA,
    caminho_anexo VARCHAR(500),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (solicitacao_de_pedido_id) REFERENCES solicitacao_de_pedido(id) ON DELETE CASCADE,
    FOREIGN KEY (fornecedor_produto_id) REFERENCES fornecedor_de_produto(id),
    FOREIGN KEY (fornecedor_servico_id) REFERENCES fornecedor_de_servico(id),
    CONSTRAINT chk_cotacao_fornecedor CHECK (
        (fornecedor_produto_id IS NOT NULL AND fornecedor_servico_id IS NULL) OR
        (fornecedor_produto_id IS NULL AND fornecedor_servico_id IS NOT NULL)
    )
);

-- Tabela de relacionamento entre cotações e itens de pedido (muitos para muitos)
CREATE TABLE cotacao_item_pedido (
    cotacao_id BIGINT NOT NULL,
    item_pedido_id BIGINT NOT NULL,
    PRIMARY KEY (cotacao_id, item_pedido_id),
    FOREIGN KEY (cotacao_id) REFERENCES cotacao(id) ON DELETE CASCADE,
    FOREIGN KEY (item_pedido_id) REFERENCES item_pedido(id) ON DELETE CASCADE
);

-- Índices para cotação
CREATE INDEX idx_cotacao_solicitacao_id ON cotacao(solicitacao_de_pedido_id);
CREATE INDEX idx_cotacao_fornecedor_produto ON cotacao(fornecedor_produto_id);
CREATE INDEX idx_cotacao_fornecedor_servico ON cotacao(fornecedor_servico_id);

-- =====================================================
-- TABELA DE HISTÓRICO
-- =====================================================

-- Tabela de histórico de pedidos
CREATE TABLE historico_pedido (
    id BIGSERIAL PRIMARY KEY,
    solicitacao_de_pedido_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    data_modificacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_modificacao VARCHAR(50) NOT NULL,
    campo_modificado VARCHAR(255),
    valor_anterior TEXT,
    valor_novo TEXT,
    observacao TEXT,
    FOREIGN KEY (solicitacao_de_pedido_id) REFERENCES solicitacao_de_pedido(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índices para histórico
CREATE INDEX idx_historico_pedido_solicitacao_id ON historico_pedido(solicitacao_de_pedido_id);
CREATE INDEX idx_historico_pedido_user_id ON historico_pedido(user_id);
CREATE INDEX idx_historico_pedido_data_modificacao ON historico_pedido(data_modificacao);
