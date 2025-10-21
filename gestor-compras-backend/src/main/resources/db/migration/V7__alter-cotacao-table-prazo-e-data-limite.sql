-- Remove campo antigo data_cotacao
ALTER TABLE cotacao DROP COLUMN IF EXISTS data_cotacao;

-- Remove campo antigo prazo_entrega
ALTER TABLE cotacao DROP COLUMN IF EXISTS prazo_entrega;

-- Adiciona novo campo prazo_em_dias_uteis (inteiro para armazenar dias úteis)
ALTER TABLE cotacao ADD COLUMN prazo_em_dias_uteis INTEGER;

-- Adiciona novo campo data_limite (data limite de validade da cotação)
ALTER TABLE cotacao ADD COLUMN data_limite DATE;
