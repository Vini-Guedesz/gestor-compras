-- =================================================================================================
-- V1__Initial_Setup.sql
--
-- Schema consolidado do Gestor de Compras.
-- Inclui tabelas de usuários, fornecedores, pedidos, cotações, rascunhos e auditoria.
-- =================================================================================================

-- =====================================================
-- 1. TABELAS DE DOMÍNIO BÁSICO (Usuários, Contato, Endereço)
-- =====================================================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    last_modified_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE contato (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    telefone_fixo VARCHAR(20),
    celular VARCHAR(20)
);

CREATE TABLE endereco (
    id BIGSERIAL PRIMARY KEY,
    cep VARCHAR(9) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    cidade VARCHAR(50) NOT NULL,
    bairro VARCHAR(60) NOT NULL,
    rua VARCHAR(100) NOT NULL,
    numero VARCHAR(10) NOT NULL,
    complemento VARCHAR(100)
);

-- =====================================================
-- 2. TABELAS DE FORNECEDORES
-- =====================================================

CREATE TABLE fornecedor_de_produto (
    id BIGSERIAL PRIMARY KEY,
    razao_social VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    inscricao_estadual VARCHAR(255),
    endereco_id BIGINT NOT NULL,
    contato_id BIGINT NOT NULL,
    CONSTRAINT fk_fornecedor_produto_endereco FOREIGN KEY (endereco_id) REFERENCES endereco(id),
    CONSTRAINT fk_fornecedor_produto_contato FOREIGN KEY (contato_id) REFERENCES contato(id)
);

CREATE TABLE fornecedor_de_servico (
    id BIGSERIAL PRIMARY KEY,
    razao_social VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    inscricao_municipal VARCHAR(255),
    endereco_id BIGINT NOT NULL,
    contato_id BIGINT NOT NULL,
    CONSTRAINT fk_fornecedor_servico_endereco FOREIGN KEY (endereco_id) REFERENCES endereco(id),
    CONSTRAINT fk_fornecedor_servico_contato FOREIGN KEY (contato_id) REFERENCES contato(id)
);

-- =====================================================
-- 3. ARMAZENAMENTO DE ARQUIVOS (Deduplicação)
-- =====================================================

CREATE TABLE pdf_storage (
    id BIGSERIAL PRIMARY KEY,
    hash_sha256 VARCHAR(64) NOT NULL UNIQUE,
    conteudo BYTEA NOT NULL,
    tamanho_bytes BIGINT NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    contador_referencias INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_pdf_storage_hash ON pdf_storage(hash_sha256);

-- =====================================================
-- 4. TABELAS DE PEDIDOS (Solicitações)
-- =====================================================

CREATE TABLE solicitacao_de_pedido (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDENTE',
    observacao TEXT,
    objetivo TEXT,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE item_pedido (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    quantidade INT NOT NULL,
    descricao TEXT,
    observacao TEXT,
    tipo VARCHAR(20) DEFAULT 'PRODUTO' NOT NULL,
    solicitacao_de_pedido_id BIGINT NOT NULL,
    CONSTRAINT fk_item_pedido_solicitacao FOREIGN KEY (solicitacao_de_pedido_id) 
        REFERENCES solicitacao_de_pedido(id) ON DELETE CASCADE
);

CREATE TABLE historico_pedido (
    id BIGSERIAL PRIMARY KEY,
    solicitacao_de_pedido_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    data_modificacao TIMESTAMP NOT NULL DEFAULT NOW(),
    tipo_modificacao VARCHAR(50) NOT NULL,
    campo_modificado VARCHAR(255),
    valor_anterior TEXT,
    valor_novo TEXT,
    observacao TEXT,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_historico_pedido_solicitacao FOREIGN KEY (solicitacao_de_pedido_id) 
        REFERENCES solicitacao_de_pedido(id) ON DELETE CASCADE,
    CONSTRAINT fk_historico_pedido_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =====================================================
-- 5. TABELAS DE COTAÇÕES DO PEDIDO
-- =====================================================

CREATE TABLE cotacao (
    id BIGSERIAL PRIMARY KEY,
    solicitacao_de_pedido_id BIGINT NOT NULL,
    fornecedor_produto_id BIGINT,
    fornecedor_servico_id BIGINT,
    preco DECIMAL(19, 2), -- Deprecated, calculado via itens, mas mantido por compatibilidade
    prazo_em_dias_uteis INTEGER,
    data_limite DATE,
    data_criacao TIMESTAMP DEFAULT NOW(),
    gasto_previsto BOOLEAN DEFAULT false,
    projeto VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    status VARCHAR(20) DEFAULT 'EM_ANALISE' NOT NULL,

    -- Campos de auditoria de edição
    numero_versao INTEGER DEFAULT 1,
    foi_editada BOOLEAN DEFAULT FALSE,
    data_ultima_edicao TIMESTAMP,
    motivo_ultima_edicao VARCHAR(500),
    editado_por VARCHAR(100),

    CONSTRAINT fk_cotacao_solicitacao FOREIGN KEY (solicitacao_de_pedido_id) 
        REFERENCES solicitacao_de_pedido(id) ON DELETE CASCADE,
    CONSTRAINT fk_cotacao_fornecedor_produto FOREIGN KEY (fornecedor_produto_id) 
        REFERENCES fornecedor_de_produto(id),
    CONSTRAINT fk_cotacao_fornecedor_servico FOREIGN KEY (fornecedor_servico_id) 
        REFERENCES fornecedor_de_servico(id)
);

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
    CONSTRAINT uk_cotacao_item UNIQUE (cotacao_id, item_pedido_id)
);

CREATE TABLE anexo_cotacao (
    id BIGSERIAL PRIMARY KEY,
    cotacao_id BIGINT NOT NULL,
    pdf_storage_id BIGINT,
    ordem INTEGER DEFAULT 0,
    nome_arquivo VARCHAR(255),
    hash_sha256 VARCHAR(64),
    CONSTRAINT fk_anexo_cotacao_cotacao FOREIGN KEY (cotacao_id) 
        REFERENCES cotacao(id) ON DELETE CASCADE,
    CONSTRAINT fk_anexo_cotacao_storage FOREIGN KEY (pdf_storage_id) 
        REFERENCES pdf_storage(id)
);

CREATE TABLE historico_cotacao (
    id BIGSERIAL PRIMARY KEY,
    cotacao_id BIGINT NOT NULL,
    versao INTEGER NOT NULL,
    preco_anterior DECIMAL(12, 2),
    prazo_anterior INTEGER,
    data_limite_anterior DATE,
    preco_novo DECIMAL(12, 2),
    prazo_novo INTEGER,
    data_limite_novo DATE,
    motivo_edicao VARCHAR(500) NOT NULL,
    editado_por VARCHAR(100),
    data_edicao TIMESTAMP NOT NULL DEFAULT NOW(),
    hash_anexo_pdf_anterior VARCHAR(64),
    hash_anexo_pdf_novo VARCHAR(64),
    nome_arquivo_anterior VARCHAR(255),
    nome_arquivo_novo VARCHAR(255),
    CONSTRAINT fk_historico_cotacao_cotacao FOREIGN KEY (cotacao_id)
        REFERENCES cotacao(id) ON DELETE CASCADE
);

-- =====================================================
-- 6. TABELAS DE RASCUNHOS
-- =====================================================

CREATE TABLE rascunho (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    observacao TEXT,
    objetivo TEXT,
    status VARCHAR(20) DEFAULT 'ATIVO' NOT NULL,
    pedido_gerado_id BIGINT,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_modificacao TIMESTAMP,
    proximo_numero_item INTEGER NOT NULL DEFAULT 1,
    version BIGINT NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE item_rascunho (
    id BIGSERIAL PRIMARY KEY,
    rascunho_id BIGINT NOT NULL,
    numero_item INTEGER NOT NULL,
    nome VARCHAR(255) NOT NULL,
    quantidade INTEGER NOT NULL,
    descricao TEXT,
    observacao TEXT,
    tipo VARCHAR(20) DEFAULT 'PRODUTO' NOT NULL,
    CONSTRAINT fk_item_rascunho_rascunho FOREIGN KEY (rascunho_id)
        REFERENCES rascunho(id) ON DELETE CASCADE,
    UNIQUE (rascunho_id, numero_item)
);

CREATE TABLE numero_item_disponivel (
    id BIGSERIAL PRIMARY KEY,
    rascunho_id BIGINT NOT NULL,
    numero_item INTEGER NOT NULL,
    CONSTRAINT fk_numero_disp_rascunho FOREIGN KEY (rascunho_id) 
        REFERENCES rascunho(id) ON DELETE CASCADE
);

CREATE TABLE historico_rascunho (
    id BIGSERIAL PRIMARY KEY,
    rascunho_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    data_modificacao TIMESTAMP NOT NULL DEFAULT NOW(),
    tipo_acao VARCHAR(50) NOT NULL,
    descricao TEXT NOT NULL,
    numero_item INTEGER,
    nome_item VARCHAR(255),
    detalhes TEXT,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_historico_rascunho_rascunho FOREIGN KEY (rascunho_id) 
        REFERENCES rascunho(id) ON DELETE CASCADE,
    CONSTRAINT fk_historico_rascunho_user FOREIGN KEY (user_id) 
        REFERENCES users(id)
);

-- =====================================================
-- 7. TABELAS DE COTAÇÕES DO RASCUNHO
-- =====================================================

CREATE TABLE cotacao_rascunho (
    id BIGSERIAL PRIMARY KEY,
    rascunho_id BIGINT NOT NULL,
    fornecedor_produto_id BIGINT,
    fornecedor_servico_id BIGINT,
    preco DECIMAL(19, 2) NOT NULL,
    prazo_em_dias_uteis INTEGER,
    data_limite DATE,
    data_criacao TIMESTAMP DEFAULT NOW(),
    gasto_previsto BOOLEAN DEFAULT false,
    projeto VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_cotacao_rascunho_rascunho FOREIGN KEY (rascunho_id) 
        REFERENCES rascunho(id) ON DELETE CASCADE,
    CONSTRAINT fk_cotacao_rascunho_forn_prod FOREIGN KEY (fornecedor_produto_id) 
        REFERENCES fornecedor_de_produto(id),
    CONSTRAINT fk_cotacao_rascunho_forn_serv FOREIGN KEY (fornecedor_servico_id) 
        REFERENCES fornecedor_de_servico(id)
);

CREATE TABLE cotacao_rascunho_item (
    cotacao_rascunho_id BIGINT NOT NULL,
    item_rascunho_id BIGINT NOT NULL,
    PRIMARY KEY (cotacao_rascunho_id, item_rascunho_id),
    CONSTRAINT fk_cri_cotacao FOREIGN KEY (cotacao_rascunho_id) 
        REFERENCES cotacao_rascunho(id) ON DELETE CASCADE,
    CONSTRAINT fk_cri_item FOREIGN KEY (item_rascunho_id) 
        REFERENCES item_rascunho(id) ON DELETE CASCADE
);

CREATE TABLE anexo_cotacao_rascunho (
    id BIGSERIAL PRIMARY KEY,
    cotacao_rascunho_id BIGINT NOT NULL,
    pdf_storage_id BIGINT,
    ordem INTEGER DEFAULT 0,
    nome_arquivo VARCHAR(255),
    hash_sha256 VARCHAR(64),
    CONSTRAINT fk_acr_cotacao FOREIGN KEY (cotacao_rascunho_id) 
        REFERENCES cotacao_rascunho(id) ON DELETE CASCADE,
    CONSTRAINT fk_acr_storage FOREIGN KEY (pdf_storage_id) 
        REFERENCES pdf_storage(id)
);

