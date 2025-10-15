-- Migration inicial - Tabelas base do sistema
-- Autor: Sistema
-- Data: 2025-10-10
-- Descrição: Cria as tabelas fundamentais do sistema (usuários, contatos e endereços)

-- Tabela de usuários do sistema
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(255) NOT NULL
);

-- Tabela de contatos
CREATE TABLE contato (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefone_fixo VARCHAR(20),
    celular VARCHAR(20)
);

-- Tabela de endereços
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
