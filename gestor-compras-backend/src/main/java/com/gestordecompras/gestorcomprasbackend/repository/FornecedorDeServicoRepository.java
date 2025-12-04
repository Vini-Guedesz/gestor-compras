package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FornecedorDeServicoRepository extends JpaRepository<FornecedorDeServico, Integer> {

    @Query("SELECT DISTINCT f FROM FornecedorDeServico f " +
           "LEFT JOIN FETCH f.endereco " +
           "LEFT JOIN FETCH f.contato")
    List<FornecedorDeServico> findAllWithRelationships();

    @Query("SELECT f FROM FornecedorDeServico f " +
           "LEFT JOIN FETCH f.endereco " +
           "LEFT JOIN FETCH f.contato " +
           "WHERE f.id = :id")
    Optional<FornecedorDeServico> findByIdWithRelationships(@Param("id") Integer id);
}
