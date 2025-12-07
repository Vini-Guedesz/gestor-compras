-- =====================================================================================
-- V14: Implementação de Content-Addressable Storage (CAS) para PDFs
-- =====================================================================================
--
-- OBJETIVO: Eliminar duplicação de PDFs através de armazenamento centralizado
--
-- PROBLEMA ANTERIOR:
--   - Ao converter rascunho → pedido, PDFs eram copiados (duplicados)
--   - Mesmo PDF armazenado múltiplas vezes desperdiçava espaço
--   - Sem deduplificação real (apenas detecção via hash)
--
-- SOLUÇÃO:
--   - Tabela central pdf_storage armazena cada PDF único apenas uma vez
--   - anexo_cotacao e anexo_cotacao_rascunho referenciam pdf_storage
--   - PDFs idênticos (mesmo hash SHA-256) compartilham mesmo storage
--
-- ECONOMIA ESPERADA: 30-78% de espaço em ambientes típicos
-- =====================================================================================

-- =====================================================================================
-- PASSO 1: Criar tabela centralizada de PDFs
-- =====================================================================================

CREATE TABLE pdf_storage (
    id BIGSERIAL PRIMARY KEY,

    -- Hash SHA-256 do conteúdo (chave de deduplicação)
    hash_sha256 VARCHAR(64) NOT NULL UNIQUE,

    -- Conteúdo binário do PDF (armazenado UMA VEZ)
    conteudo BYTEA NOT NULL,

    -- Metadados
    tamanho_bytes BIGINT NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Contagem de referências (garbage collection futuro)
    contador_referencias INTEGER NOT NULL DEFAULT 0
);

-- Índice para lookup rápido por hash
CREATE INDEX idx_pdf_storage_hash ON pdf_storage(hash_sha256);

-- Comentários para documentação
COMMENT ON TABLE pdf_storage IS 'Armazenamento centralizado de PDFs com deduplicação via Content-Addressable Storage (CAS)';
COMMENT ON COLUMN pdf_storage.hash_sha256 IS 'Hash SHA-256 do conteúdo - garante unicidade e integridade';
COMMENT ON COLUMN pdf_storage.contador_referencias IS 'Quantos anexos referenciam este PDF (para garbage collection)';

-- =====================================================================================
-- PASSO 2: Adicionar coluna de referência em anexo_cotacao
-- =====================================================================================

-- Adiciona coluna para referenciar pdf_storage
ALTER TABLE anexo_cotacao
ADD COLUMN pdf_storage_id BIGINT;

-- Cria índice para performance
CREATE INDEX idx_anexo_cotacao_storage ON anexo_cotacao(pdf_storage_id);

-- Renomeia coluna antiga para indicar que é legado
ALTER TABLE anexo_cotacao
RENAME COLUMN conteudo TO conteudo_legacy;

-- Remove constraint NOT NULL da coluna legada (novos anexos usarão pdf_storage_id)
ALTER TABLE anexo_cotacao
ALTER COLUMN conteudo_legacy DROP NOT NULL;

-- Adiciona foreign key (sem ON DELETE CASCADE para prevenir perda acidental)
ALTER TABLE anexo_cotacao
ADD CONSTRAINT fk_anexo_cotacao_pdf_storage
FOREIGN KEY (pdf_storage_id) REFERENCES pdf_storage(id);

-- =====================================================================================
-- PASSO 3: Adicionar coluna de referência em anexo_cotacao_rascunho
-- =====================================================================================

-- Adiciona coluna para referenciar pdf_storage
ALTER TABLE anexo_cotacao_rascunho
ADD COLUMN pdf_storage_id BIGINT;

-- Cria índice para performance
CREATE INDEX idx_anexo_cotacao_rascunho_storage ON anexo_cotacao_rascunho(pdf_storage_id);

-- Renomeia coluna antiga para indicar que é legado
ALTER TABLE anexo_cotacao_rascunho
RENAME COLUMN conteudo TO conteudo_legacy;

-- Remove constraint NOT NULL da coluna legada
ALTER TABLE anexo_cotacao_rascunho
ALTER COLUMN conteudo_legacy DROP NOT NULL;

-- Adiciona foreign key
ALTER TABLE anexo_cotacao_rascunho
ADD CONSTRAINT fk_anexo_cotacao_rascunho_pdf_storage
FOREIGN KEY (pdf_storage_id) REFERENCES pdf_storage(id);

-- =====================================================================================
-- PASSO 4: Migrar PDFs existentes para pdf_storage (opcional mas recomendado)
-- =====================================================================================
--
-- Esta seção migra PDFs existentes para a nova arquitetura
-- Se você preferir migrar gradualmente, pode comentar este bloco
-- Novos PDFs já usarão automaticamente a nova arquitetura
--

-- Migrar PDFs de anexo_cotacao
WITH unique_pdfs AS (
    -- Seleciona PDFs únicos de anexo_cotacao baseado no hash
    SELECT DISTINCT ON (hash_sha256)
        hash_sha256,
        conteudo_legacy,
        OCTET_LENGTH(conteudo_legacy) AS tamanho_bytes
    FROM anexo_cotacao
    WHERE conteudo_legacy IS NOT NULL
      AND hash_sha256 IS NOT NULL
),
inserted_storage AS (
    -- Insere PDFs únicos em pdf_storage
    INSERT INTO pdf_storage (hash_sha256, conteudo, tamanho_bytes, data_criacao, contador_referencias)
    SELECT
        hash_sha256,
        conteudo_legacy,
        tamanho_bytes,
        NOW(),
        0  -- Será atualizado a seguir
    FROM unique_pdfs
    ON CONFLICT (hash_sha256) DO NOTHING
    RETURNING id, hash_sha256
)
-- Atualiza anexo_cotacao para referenciar pdf_storage
UPDATE anexo_cotacao ac
SET pdf_storage_id = ps.id
FROM pdf_storage ps
WHERE ac.hash_sha256 = ps.hash_sha256
  AND ac.conteudo_legacy IS NOT NULL
  AND ac.pdf_storage_id IS NULL;

-- Migrar PDFs de anexo_cotacao_rascunho
WITH unique_pdfs_rascunho AS (
    SELECT DISTINCT ON (hash_sha256)
        hash_sha256,
        conteudo_legacy,
        OCTET_LENGTH(conteudo_legacy) AS tamanho_bytes
    FROM anexo_cotacao_rascunho
    WHERE conteudo_legacy IS NOT NULL
      AND hash_sha256 IS NOT NULL
),
inserted_storage_rascunho AS (
    INSERT INTO pdf_storage (hash_sha256, conteudo, tamanho_bytes, data_criacao, contador_referencias)
    SELECT
        hash_sha256,
        conteudo_legacy,
        tamanho_bytes,
        NOW(),
        0
    FROM unique_pdfs_rascunho
    ON CONFLICT (hash_sha256) DO NOTHING
    RETURNING id, hash_sha256
)
UPDATE anexo_cotacao_rascunho acr
SET pdf_storage_id = ps.id
FROM pdf_storage ps
WHERE acr.hash_sha256 = ps.hash_sha256
  AND acr.conteudo_legacy IS NOT NULL
  AND acr.pdf_storage_id IS NULL;

-- Atualizar contador de referências em pdf_storage
UPDATE pdf_storage ps
SET contador_referencias = (
    SELECT COUNT(*)
    FROM anexo_cotacao ac
    WHERE ac.pdf_storage_id = ps.id
) + (
    SELECT COUNT(*)
    FROM anexo_cotacao_rascunho acr
    WHERE acr.pdf_storage_id = ps.id
);

-- =====================================================================================
-- PASSO 5: Verificação e estatísticas
-- =====================================================================================

-- View para monitorar economia de espaço
CREATE OR REPLACE VIEW v_pdf_deduplication_stats AS
SELECT
    -- Estatísticas de PDFs únicos
    COUNT(DISTINCT ps.id) AS pdfs_unicos,
    SUM(ps.tamanho_bytes) AS espaco_real_usado_bytes,

    -- Estatísticas de anexos
    (SELECT COUNT(*) FROM anexo_cotacao WHERE pdf_storage_id IS NOT NULL) AS anexos_cotacao_nova_arquitetura,
    (SELECT COUNT(*) FROM anexo_cotacao WHERE conteudo_legacy IS NOT NULL) AS anexos_cotacao_legado,
    (SELECT COUNT(*) FROM anexo_cotacao_rascunho WHERE pdf_storage_id IS NOT NULL) AS anexos_rascunho_nova_arquitetura,
    (SELECT COUNT(*) FROM anexo_cotacao_rascunho WHERE conteudo_legacy IS NOT NULL) AS anexos_rascunho_legado,

    -- Economia calculada
    SUM(ps.tamanho_bytes * ps.contador_referencias) AS espaco_que_seria_usado_sem_dedup_bytes,
    SUM(ps.tamanho_bytes * ps.contador_referencias) - SUM(ps.tamanho_bytes) AS economia_bytes,
    ROUND(
        100.0 * (SUM(ps.tamanho_bytes * ps.contador_referencias) - SUM(ps.tamanho_bytes)) /
        NULLIF(SUM(ps.tamanho_bytes * ps.contador_referencias), 0),
        2
    ) AS percentual_economia
FROM pdf_storage ps;

COMMENT ON VIEW v_pdf_deduplication_stats IS 'Estatísticas de economia de espaço através de deduplicação de PDFs';

-- =====================================================================================
-- MIGRATION COMPLETA!
-- =====================================================================================
--
-- PRÓXIMOS PASSOS (automático via Hibernate):
--   1. PdfDeduplicationService agora criará/reutilizará PdfStorage
--   2. Conversão rascunho → pedido compartilhará PDFs (sem duplicação!)
--   3. PDFs legados continuam funcionando (conteudo_legacy)
--
-- PARA REMOVER DADOS LEGADOS (futuro, quando 100% migrado):
--   ALTER TABLE anexo_cotacao DROP COLUMN conteudo_legacy;
--   ALTER TABLE anexo_cotacao_rascunho DROP COLUMN conteudo_legacy;
--
-- =====================================================================================
