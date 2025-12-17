-- Migration para adicionar campo 'ativo' na tabela users
-- Implementa soft delete - usuários são desativados ao invés de excluídos

-- Adiciona coluna 'ativo' com valor padrão TRUE
ALTER TABLE users
ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE;

-- Comentário: Usuários com ativo = FALSE não podem fazer login no sistema
-- O método isEnabled() do Spring Security usa este campo para validar acesso
