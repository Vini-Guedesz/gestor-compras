package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.rascunho.CotacaoRascunho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CotacaoRascunhoRepository extends JpaRepository<CotacaoRascunho, Long> {

    @Query("SELECT DISTINCT c FROM CotacaoRascunho c LEFT JOIN FETCH c.itensRascunho LEFT JOIN FETCH c.anexos WHERE c.rascunho.id = :rascunhoId")
    List<CotacaoRascunho> findByRascunhoIdWithItens(@Param("rascunhoId") Long rascunhoId);

    @Query("SELECT c FROM CotacaoRascunho c LEFT JOIN FETCH c.itensRascunho LEFT JOIN FETCH c.anexos WHERE c.id = :id")
    CotacaoRascunho findByIdWithItens(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM CotacaoRascunho c LEFT JOIN FETCH c.itensRascunho ir WHERE ir.id = :itemRascunhoId")
    List<CotacaoRascunho> findByItemRascunhoId(@Param("itemRascunhoId") Long itemRascunhoId);

    void deleteByRascunhoId(Long rascunhoId);
}
