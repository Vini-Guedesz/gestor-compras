-- Migration para adicionar ON DELETE CASCADE
-- Autor: Sistema
-- Data: 2025-10-20
-- Descrição: Adiciona ON DELETE CASCADE na foreign key de item_pedido para solicitacao_de_pedido
--           para garantir que os itens sejam deletados automaticamente quando o pedido for deletado

-- Primeiro, remover registros órfãos (itens sem pedido associado)
DELETE FROM item_pedido WHERE solicitacao_de_pedido_id IS NULL;

-- Remover a constraint existente
ALTER TABLE item_pedido
DROP CONSTRAINT IF EXISTS item_pedido_solicitacao_de_pedido_id_fkey;

-- Recriar a constraint com ON DELETE CASCADE
ALTER TABLE item_pedido
ADD CONSTRAINT item_pedido_solicitacao_de_pedido_id_fkey
FOREIGN KEY (solicitacao_de_pedido_id)
REFERENCES solicitacao_de_pedido(id)
ON DELETE CASCADE;

-- Tornar a coluna NOT NULL para garantir integridade
ALTER TABLE item_pedido
ALTER COLUMN solicitacao_de_pedido_id SET NOT NULL;
