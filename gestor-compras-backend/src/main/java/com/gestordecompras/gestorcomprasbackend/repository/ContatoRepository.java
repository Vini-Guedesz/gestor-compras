package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.contato.Contato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ContatoRepository extends JpaRepository<Contato, Integer> {

    @Query("SELECT DISTINCT c FROM Contato c " +
           "LEFT JOIN FETCH c.contatosAdicionais " +
           "WHERE c.id IN :ids")
    List<Contato> findAllByIdInWithContatosAdicionais(@Param("ids") Collection<Integer> ids);
}
