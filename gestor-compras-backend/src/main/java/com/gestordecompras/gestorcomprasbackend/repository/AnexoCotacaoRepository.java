package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.cotacao.AnexoCotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para gerenciar anexos de cotações
 * Suporta deduplificação através de busca por hash SHA-256
 */
@Repository
public interface AnexoCotacaoRepository extends JpaRepository<AnexoCotacao, Long> {

    /**
     * Busca um anexo pelo hash SHA-256 do conteúdo
     * Usado para deduplificação - verifica se o mesmo PDF já foi armazenado
     *
     * @param hashSha256 Hash SHA-256 do PDF (64 caracteres hexadecimais)
     * @return Optional contendo o anexo se encontrado
     */
    Optional<AnexoCotacao> findFirstByHashSha256(String hashSha256);

    /**
     * Busca todos os anexos com o mesmo hash (para identificar duplicatas)
     *
     * @param hashSha256 Hash SHA-256 do PDF
     * @return Lista de anexos com o mesmo hash
     */
    List<AnexoCotacao> findAllByHashSha256(String hashSha256);

    /**
     * Verifica se existe um anexo com o hash especificado
     *
     * @param hashSha256 Hash SHA-256 do PDF
     * @return true se existe, false caso contrário
     */
    boolean existsByHashSha256(String hashSha256);

    /**
     * Conta quantos PDFs diferentes estão armazenados (baseado em hash único)
     *
     * @return Número de PDFs únicos
     */
    @Query("SELECT COUNT(DISTINCT a.hashSha256) FROM AnexoCotacao a WHERE a.hashSha256 IS NOT NULL")
    Long countUniquePdfs();

    /**
     * Conta anexos usando nova arquitetura (PdfStorage)
     */
    Long countByPdfStorageIsNotNull();

    /**
     * Conta anexos ainda em modo legado (conteúdo inline)
     */
    Long countByConteudoLegacyIsNotNull();

    /**
     * Encontra PDFs duplicados (mesmo hash, múltiplas entradas)
     * Útil para relatórios de economia de espaço
     *
     * @return Lista de hashes que aparecem mais de uma vez
     */
    @Query("SELECT a.hashSha256, COUNT(a) as count FROM AnexoCotacao a " +
           "WHERE a.hashSha256 IS NOT NULL " +
           "GROUP BY a.hashSha256 " +
           "HAVING COUNT(a) > 1")
    List<Object[]> findDuplicateHashes();
}
