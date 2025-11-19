package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.rascunho.NumeroItemDisponivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NumeroItemDisponivelRepository extends JpaRepository<NumeroItemDisponivel, Long> {

    @Query("SELECT n FROM NumeroItemDisponivel n WHERE n.rascunho.id = :rascunhoId ORDER BY n.numeroItem ASC")
    Optional<NumeroItemDisponivel> findFirstByRascunhoIdOrderByNumeroItemAsc(@Param("rascunhoId") Long rascunhoId);

    void deleteByRascunhoIdAndNumeroItem(Long rascunhoId, Integer numeroItem);
}
