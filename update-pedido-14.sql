-- Atualizar pedido #14 para status EM_NEGOCIACAO
UPDATE solicitacoes_de_pedido
SET status = 'EM_NEGOCIACAO'
WHERE id = 14;

-- Verificar a atualização
SELECT id, status, criado_em, atualizado_em
FROM solicitacoes_de_pedido
WHERE id = 14;
