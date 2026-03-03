-- =================================================================================================
-- V4__Add_Rotulos_Contato_Principal.sql
--
-- Adiciona rótulos opcionais para os contatos principais (fixo, celular e e-mail).
-- =================================================================================================

ALTER TABLE contato
    ADD COLUMN IF NOT EXISTS rotulo_telefone_fixo VARCHAR(100),
    ADD COLUMN IF NOT EXISTS rotulo_celular VARCHAR(100),
    ADD COLUMN IF NOT EXISTS rotulo_email VARCHAR(100);

