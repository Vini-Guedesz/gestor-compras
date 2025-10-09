-- Altera a tabela contato para separar telefone fixo e celular
ALTER TABLE contato RENAME COLUMN numero TO telefone_fixo;

-- Adiciona coluna para celular
ALTER TABLE contato ADD COLUMN celular VARCHAR(20);

-- Ajusta constraint para permitir pelo menos um telefone
ALTER TABLE contato ALTER COLUMN telefone_fixo DROP NOT NULL;
