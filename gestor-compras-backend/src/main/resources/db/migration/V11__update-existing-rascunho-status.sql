-- Migration V11: Atualizar pedidos com status RASCUNHO para PENDENTE
-- Como o status RASCUNHO foi removido do enum e substituído pela entidade Rascunho,
-- precisamos atualizar pedidos existentes que ainda têm esse status

-- Atualizar todos os pedidos com status RASCUNHO para PENDENTE
UPDATE solicitacao_de_pedido
SET status = 'PENDENTE'
WHERE status = 'RASCUNHO';
