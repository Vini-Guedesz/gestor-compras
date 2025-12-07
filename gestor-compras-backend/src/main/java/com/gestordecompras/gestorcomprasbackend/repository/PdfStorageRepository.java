package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.storage.PdfStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para armazenamento centralizado de PDFs
 */
@Repository
public interface PdfStorageRepository extends JpaRepository<PdfStorage, Long> {

    /**
     * Busca um PDF pelo hash SHA-256
     * Usado para deduplicação - verifica se PDF já existe antes de armazenar
     *
     * @param hashSha256 Hash SHA-256 do PDF (64 caracteres hexadecimais)
     * @return Optional contendo o PdfStorage se encontrado
     */
    Optional<PdfStorage> findByHashSha256(String hashSha256);

    /**
     * Verifica se existe um PDF com o hash especificado
     *
     * @param hashSha256 Hash SHA-256 do PDF
     * @return true se existe, false caso contrário
     */
    boolean existsByHashSha256(String hashSha256);

    /**
     * Calcula espaço total ocupado por PDFs únicos
     *
     * @return Soma dos tamanhos de todos os PDFs em bytes
     */
    @Query("SELECT COALESCE(SUM(p.tamanhoBytes), 0) FROM PdfStorage p")
    Long calcularEspacoTotal();

    /**
     * Conta total de PDFs únicos armazenados
     *
     * @return Número de PDFs únicos
     */
    @Query("SELECT COUNT(p) FROM PdfStorage p")
    Long contarPdfsUnicos();

    /**
     * Soma total de referências (quantos anexos existem no total)
     *
     * @return Soma de todos os contadores de referências
     */
    @Query("SELECT COALESCE(SUM(p.contadorReferencias), 0) FROM PdfStorage p")
    Long contarTotalReferencias();
}
