-- Migration para adicionar campos gasto_previsto e projeto na tabela cotacao_rascunho

-- Adiciona campo 'gasto_previsto' na tabela cotacao_rascunho
-- Indica se o gasto estava previsto no orçamento
ALTER TABLE cotacao_rascunho
ADD COLUMN gasto_previsto BOOLEAN DEFAULT false;

-- Adiciona campo 'projeto' na tabela cotacao_rascunho
-- Nome do projeto ao qual o gasto pertence (quando previsto)
ALTER TABLE cotacao_rascunho
ADD COLUMN projeto VARCHAR(255);

-- Comentários para documentação
COMMENT ON COLUMN cotacao_rascunho.gasto_previsto IS 'Indica se o gasto estava previsto no orçamento';
COMMENT ON COLUMN cotacao_rascunho.projeto IS 'Nome do projeto ao qual o gasto pertence (obrigatório quando gasto_previsto = true)';
