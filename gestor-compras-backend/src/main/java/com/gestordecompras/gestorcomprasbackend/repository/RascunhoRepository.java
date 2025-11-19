package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.rascunho.Rascunho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RascunhoRepository extends JpaRepository<Rascunho, Long> {

    @Query("SELECT r FROM Rascunho r LEFT JOIN FETCH r.itens WHERE r.criador.id = :userId ORDER BY r.dataModificacao DESC")
    List<Rascunho> findAllByCriadorIdWithItens(@Param("userId") Long userId);

    @Query("SELECT r FROM Rascunho r LEFT JOIN FETCH r.itens WHERE r.id = :id")
    Rascunho findByIdWithItens(@Param("id") Long id);
}
