package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.rascunho.NumeroItemDisponivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para gerenciamento de números de itens disponíveis (pool de reutilização).
 * Quando um item é removido de um rascunho, seu número volta para cá para ser reutilizado.
 */
@Repository
public interface NumeroItemDisponivelRepository extends JpaRepository<NumeroItemDisponivel, Long> {

    /**
     * Encontra o primeiro número disponível para reutilização em um rascunho.
     *
     * @param rascunhoId ID do rascunho
     * @return Optional contendo o número disponível (se houver)
     */
    @Query("SELECT n FROM NumeroItemDisponivel n WHERE n.rascunho.id = :rascunhoId ORDER BY n.numeroItem ASC")
    Optional<NumeroItemDisponivel> findFirstByRascunhoIdOrderByNumeroItemAsc(@Param("rascunhoId") Long rascunhoId);

    /**
     * Remove um número específico do pool (quando ele é reutilizado).
     *
     * @param rascunhoId ID do rascunho
     * @param numeroItem Número do item
     */
    void deleteByRascunhoIdAndNumeroItem(Long rascunhoId, Integer numeroItem);
}
