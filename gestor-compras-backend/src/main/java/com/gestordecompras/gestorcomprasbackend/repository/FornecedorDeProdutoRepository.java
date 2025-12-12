package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Fornecedores de Produto.
 */
@Repository
public interface FornecedorDeProdutoRepository extends JpaRepository<FornecedorDeProduto, Integer> {

    /**
     * Busca todos os fornecedores carregando seus relacionamentos (endereço e contato)
     * para evitar problema de N+1 queries.
     *
     * @return Lista de fornecedores com dados completos
     */
    @Query("SELECT DISTINCT f FROM FornecedorDeProduto f " +
           "LEFT JOIN FETCH f.endereco " +
           "LEFT JOIN FETCH f.contato")
    List<FornecedorDeProduto> findAllWithRelationships();

    /**
     * Busca um fornecedor por ID carregando seus relacionamentos.
     *
     * @param id ID do fornecedor
     * @return Optional com o fornecedor encontrado
     */
    @Query("SELECT f FROM FornecedorDeProduto f " +
           "LEFT JOIN FETCH f.endereco " +
           "LEFT JOIN FETCH f.contato " +
           "WHERE f.id = :id")
    Optional<FornecedorDeProduto> findByIdWithRelationships(@Param("id") Integer id);
}
