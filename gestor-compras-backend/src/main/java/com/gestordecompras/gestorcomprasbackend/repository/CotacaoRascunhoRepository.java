package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.rascunho.CotacaoRascunho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CotacaoRascunhoRepository extends JpaRepository<CotacaoRascunho, Long> {

    // Buscar cotações com itens (primeiro passo)
    @Query("SELECT DISTINCT c FROM CotacaoRascunho c LEFT JOIN FETCH c.itensRascunho WHERE c.rascunho.id = :rascunhoId")
    List<CotacaoRascunho> findByRascunhoIdWithItens(@Param("rascunhoId") Long rascunhoId);

    // Buscar cotação específica com itens e anexos (usar em duas queries para evitar produto cartesiano)
    @Query("SELECT DISTINCT c FROM CotacaoRascunho c LEFT JOIN FETCH c.itensRascunho WHERE c.id = :id")
    CotacaoRascunho findByIdWithItens(@Param("id") Long id);

    // Query auxiliar para carregar anexos separadamente
    @Query("SELECT DISTINCT c FROM CotacaoRascunho c LEFT JOIN FETCH c.anexos WHERE c.id IN :ids")
    List<CotacaoRascunho> findByIdsWithAnexos(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT c FROM CotacaoRascunho c LEFT JOIN FETCH c.itensRascunho ir WHERE ir.id = :itemRascunhoId")
    List<CotacaoRascunho> findByItemRascunhoId(@Param("itemRascunhoId") Long itemRascunhoId);

    void deleteByRascunhoId(Long rascunhoId);
}
