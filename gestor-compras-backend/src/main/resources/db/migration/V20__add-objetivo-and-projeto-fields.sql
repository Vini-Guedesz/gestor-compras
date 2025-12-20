-- Migration para adicionar campos de objetivo no rascunho/pedido e projeto na cotação

-- Adiciona campo 'objetivo' na tabela rascunho
-- Descreve o objetivo/finalidade do pedido que será criado
ALTER TABLE rascunho
ADD COLUMN objetivo TEXT;

-- Adiciona campo 'objetivo' na tabela solicitacao_de_pedido
-- Copiado do rascunho quando o pedido é criado
ALTER TABLE solicitacao_de_pedido
ADD COLUMN objetivo TEXT;

-- Adiciona campo 'gasto_previsto' na tabela cotacao
-- Indica se o gasto estava previsto no orçamento
ALTER TABLE cotacao
ADD COLUMN gasto_previsto BOOLEAN DEFAULT false;

-- Adiciona campo 'projeto' na tabela cotacao
-- Nome do projeto ao qual o gasto pertence (quando previsto)
ALTER TABLE cotacao
ADD COLUMN projeto VARCHAR(255);

-- Comentários para documentação
COMMENT ON COLUMN rascunho.objetivo IS 'Objetivo ou finalidade do pedido (ex: "Compra de materiais para reforma do escritório")';
COMMENT ON COLUMN solicitacao_de_pedido.objetivo IS 'Objetivo ou finalidade do pedido (copiado do rascunho)';
COMMENT ON COLUMN cotacao.gasto_previsto IS 'Indica se o gasto estava previsto no orçamento';
COMMENT ON COLUMN cotacao.projeto IS 'Nome do projeto ao qual o gasto pertence (obrigatório quando gasto_previsto = true)';
