-- Migration de fornecedores
-- Autor: Sistema
-- Data: 2025-10-10
-- Descrição: Cria as tabelas de fornecedores de produtos e serviços

-- Tabela de fornecedores de produtos
CREATE TABLE fornecedor_de_produto (
    id BIGSERIAL PRIMARY KEY,
    razao_social VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    endereco_id BIGINT NOT NULL,
    contato_id BIGINT NOT NULL,
    inscricao_estadual VARCHAR(255),
    FOREIGN KEY (endereco_id) REFERENCES endereco(id),
    FOREIGN KEY (contato_id) REFERENCES contato(id)
);

-- Tabela de fornecedores de serviços
CREATE TABLE fornecedor_de_servico (
    id BIGSERIAL PRIMARY KEY,
    razao_social VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    endereco_id BIGINT NOT NULL,
    contato_id BIGINT NOT NULL,
    inscricao_municipal VARCHAR(255),
    FOREIGN KEY (endereco_id) REFERENCES endereco(id),
    FOREIGN KEY (contato_id) REFERENCES contato(id)
);
