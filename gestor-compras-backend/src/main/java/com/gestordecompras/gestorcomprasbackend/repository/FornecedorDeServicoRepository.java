package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Fornecedores de Serviço.
 */
@Repository
public interface FornecedorDeServicoRepository extends JpaRepository<FornecedorDeServico, Integer> {

    /**
     * Busca todos os fornecedores carregando seus relacionamentos (endereço e contato)
     * para evitar problema de N+1 queries.
     *
     * @return Lista de fornecedores com dados completos
     */
    @Query("SELECT DISTINCT f FROM FornecedorDeServico f " +
           "LEFT JOIN FETCH f.endereco " +
           "LEFT JOIN FETCH f.contato")
    List<FornecedorDeServico> findAllWithRelationships();

    /**
     * Busca um fornecedor por ID carregando seus relacionamentos.
     *
     * @param id ID do fornecedor
     * @return Optional com o fornecedor encontrado
     */
    @Query("SELECT f FROM FornecedorDeServico f " +
           "LEFT JOIN FETCH f.endereco " +
           "LEFT JOIN FETCH f.contato " +
           "WHERE f.id = :id")
    Optional<FornecedorDeServico> findByIdWithRelationships(@Param("id") Integer id);

    /**
     * Busca paginada de fornecedores com otimização de lazy loading usando EntityGraph.
     * <p>
     * Carrega antecipadamente endereço e contato para evitar problema N+1.
     * </p>
     *
     * @param pageable Parâmetros de paginação e ordenação
     * @return Página de fornecedores com relacionamentos carregados
     */
    @EntityGraph(attributePaths = {"endereco", "contato"})
    Page<FornecedorDeServico> findAll(Pageable pageable);

    /**
     * Busca fornecedor por ID com otimização de lazy loading usando EntityGraph.
     * <p>
     * Carrega antecipadamente endereço e contato em uma única query.
     * </p>
     *
     * @param id ID do fornecedor
     * @return Optional contendo o fornecedor com relacionamentos carregados
     */
    @EntityGraph(attributePaths = {"endereco", "contato"})
    Optional<FornecedorDeServico> findById(Integer id);
}
