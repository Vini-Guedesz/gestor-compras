package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FornecedorDeProdutoRepository extends JpaRepository<FornecedorDeProduto, Integer> {

    @Query("SELECT DISTINCT f FROM FornecedorDeProduto f " +
           "LEFT JOIN FETCH f.endereco " +
           "LEFT JOIN FETCH f.contato")
    List<FornecedorDeProduto> findAllWithRelationships();

    @Query("SELECT f FROM FornecedorDeProduto f " +
           "LEFT JOIN FETCH f.endereco " +
           "LEFT JOIN FETCH f.contato " +
           "WHERE f.id = :id")
    Optional<FornecedorDeProduto> findByIdWithRelationships(@Param("id") Integer id);
}
