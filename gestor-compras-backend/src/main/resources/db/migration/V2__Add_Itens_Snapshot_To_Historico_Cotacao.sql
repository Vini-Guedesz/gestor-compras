-- Adiciona colunas para snapshot de itens no histórico de cotações
ALTER TABLE historico_cotacao ADD COLUMN itens_anteriores TEXT;
ALTER TABLE historico_cotacao ADD COLUMN itens_novos TEXT;
