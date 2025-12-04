package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.cotacao.HistoricoCotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoCotacaoRepository extends JpaRepository<HistoricoCotacao, Long> {

    /**
     * Busca todo o histórico de edições de uma cotação específica,
     * ordenado da mais recente para a mais antiga
     */
    @Query("SELECT h FROM HistoricoCotacao h WHERE h.cotacaoId = :cotacaoId ORDER BY h.dataEdicao DESC")
    List<HistoricoCotacao> findByCotacaoIdOrderByDataEdicaoDesc(@Param("cotacaoId") Long cotacaoId);

    /**
     * Busca todas as cotações que foram editadas em um período específico
     */
    @Query("SELECT h FROM HistoricoCotacao h WHERE h.dataEdicao BETWEEN :dataInicio AND :dataFim ORDER BY h.dataEdicao DESC")
    List<HistoricoCotacao> findByDataEdicaoBetween(
        @Param("dataInicio") java.time.LocalDateTime dataInicio,
        @Param("dataFim") java.time.LocalDateTime dataFim
    );

    /**
     * Conta quantas vezes uma cotação foi editada
     */
    @Query("SELECT COUNT(h) FROM HistoricoCotacao h WHERE h.cotacaoId = :cotacaoId")
    Long countByCotacaoId(@Param("cotacaoId") Long cotacaoId);
}
