package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.rascunho.CotacaoRascunho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CotacaoRascunhoRepository extends JpaRepository<CotacaoRascunho, Long> {

    // Buscar cotações com itens e fornecedores (otimizado para evitar N+1)
    @Query("SELECT DISTINCT c FROM CotacaoRascunho c " +
           "LEFT JOIN FETCH c.itensRascunho " +
           "LEFT JOIN FETCH c.fornecedorProduto fp " +
           "LEFT JOIN FETCH fp.endereco " +
           "LEFT JOIN FETCH fp.contato " +
           "LEFT JOIN FETCH c.fornecedorServico fs " +
           "LEFT JOIN FETCH fs.endereco " +
           "LEFT JOIN FETCH fs.contato " +
           "WHERE c.rascunho.id = :rascunhoId")
    List<CotacaoRascunho> findByRascunhoIdWithItens(@Param("rascunhoId") Long rascunhoId);

    // Buscar cotação específica com itens, fornecedores e relacionamentos (otimizado)
    @Query("SELECT DISTINCT c FROM CotacaoRascunho c " +
           "LEFT JOIN FETCH c.itensRascunho " +
           "LEFT JOIN FETCH c.fornecedorProduto fp " +
           "LEFT JOIN FETCH fp.endereco " +
           "LEFT JOIN FETCH fp.contato " +
           "LEFT JOIN FETCH c.fornecedorServico fs " +
           "LEFT JOIN FETCH fs.endereco " +
           "LEFT JOIN FETCH fs.contato " +
           "WHERE c.id = :id")
    CotacaoRascunho findByIdWithItens(@Param("id") Long id);

    // Query auxiliar para carregar anexos separadamente
    @Query("SELECT DISTINCT c FROM CotacaoRascunho c LEFT JOIN FETCH c.anexos WHERE c.id IN :ids")
    List<CotacaoRascunho> findByIdsWithAnexos(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT c FROM CotacaoRascunho c LEFT JOIN FETCH c.itensRascunho ir WHERE ir.id = :itemRascunhoId")
    List<CotacaoRascunho> findByItemRascunhoId(@Param("itemRascunhoId") Long itemRascunhoId);

    void deleteByRascunhoId(Long rascunhoId);
}
