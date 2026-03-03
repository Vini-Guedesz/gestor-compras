package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.rascunho.CotacaoRascunho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CotacaoRascunhoRepository extends JpaRepository<CotacaoRascunho, Long> {

    /**
     * Busca cotações com itens e fornecedores (otimizado para evitar problema N+1).
     * Carrega os relacionamentos com fornecedor (produto ou serviço), endereço e contatos em uma única consulta.
     *
     * @param rascunhoId ID do rascunho
     * @return Lista de cotações com dados completos
     */
    @Query("SELECT DISTINCT c FROM CotacaoRascunho c " +
           "LEFT JOIN FETCH c.itens i " +
           "LEFT JOIN FETCH i.itemRascunho " +
           "LEFT JOIN FETCH c.fornecedorProduto fp " +
           "LEFT JOIN FETCH fp.endereco " +
           "LEFT JOIN FETCH fp.contato " +
           "LEFT JOIN FETCH c.fornecedorServico fs " +
           "LEFT JOIN FETCH fs.endereco " +
           "LEFT JOIN FETCH fs.contato " +
           "WHERE c.rascunho.id = :rascunhoId")
    List<CotacaoRascunho> findByRascunhoIdWithItens(@Param("rascunhoId") Long rascunhoId);

    /**
     * Busca uma cotação específica com itens, fornecedores e relacionamentos (otimizado).
     *
     * @param id ID da cotação
     * @return Cotação com dados completos
     */
    @Query("SELECT DISTINCT c FROM CotacaoRascunho c " +
           "LEFT JOIN FETCH c.itens i " +
           "LEFT JOIN FETCH i.itemRascunho " +
           "LEFT JOIN FETCH c.fornecedorProduto fp " +
           "LEFT JOIN FETCH fp.endereco " +
           "LEFT JOIN FETCH fp.contato " +
           "LEFT JOIN FETCH c.fornecedorServico fs " +
           "LEFT JOIN FETCH fs.endereco " +
           "LEFT JOIN FETCH fs.contato " +
           "WHERE c.id = :id")
    CotacaoRascunho findByIdWithItens(@Param("id") Long id);

    /**
     * Query auxiliar para carregar anexos separadamente.
     * Útil para evitar produto cartesiano ao carregar cotações com muitos anexos e itens simultaneamente.
     *
     * @param ids Lista de IDs das cotações
     * @return Lista de cotações com anexos carregados
     */
    @Query("SELECT DISTINCT c FROM CotacaoRascunho c LEFT JOIN FETCH c.anexos WHERE c.id IN :ids")
    List<CotacaoRascunho> findByIdsWithAnexos(@Param("ids") List<Long> ids);

    /**
     * Busca cotações que contêm um item de rascunho específico.
     *
     * @param itemRascunhoId ID do item de rascunho
     * @return Lista de cotações
     */
    @Query("SELECT DISTINCT c FROM CotacaoRascunho c LEFT JOIN FETCH c.itens i LEFT JOIN FETCH i.itemRascunho ir WHERE ir.id = :itemRascunhoId")
    List<CotacaoRascunho> findByItemRascunhoId(@Param("itemRascunhoId") Long itemRascunhoId);

    /**
     * Remove todas as cotações associadas a um rascunho.
     *
     * @param rascunhoId ID do rascunho
     */
    void deleteByRascunhoId(Long rascunhoId);
}
