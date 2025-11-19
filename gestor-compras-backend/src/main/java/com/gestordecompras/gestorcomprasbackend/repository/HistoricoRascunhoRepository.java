package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.rascunho.HistoricoRascunho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoRascunhoRepository extends JpaRepository<HistoricoRascunho, Long> {

    @Query("SELECT h FROM HistoricoRascunho h WHERE h.rascunho.id = :rascunhoId ORDER BY h.dataModificacao DESC")
    List<HistoricoRascunho> findByRascunhoIdOrderByDataModificacaoDesc(@Param("rascunhoId") Long rascunhoId);

    @Query("SELECT h FROM HistoricoRascunho h JOIN FETCH h.usuario WHERE h.rascunho.id = :rascunhoId ORDER BY h.dataModificacao DESC")
    List<HistoricoRascunho> findByRascunhoIdWithUsuario(@Param("rascunhoId") Long rascunhoId);
}
