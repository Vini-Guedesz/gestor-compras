package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CotacaoRepository extends JpaRepository<Cotacao, Long> {
    @Query("SELECT SUM(c.preco) FROM Cotacao c")
    BigDecimal sumTotalPreco();

    @Query("SELECT MIN(c.preco) FROM Cotacao c")
    BigDecimal findMinPreco();

    @Query("SELECT MAX(c.preco) FROM Cotacao c")
    BigDecimal findMaxPreco();

    @Query("SELECT DISTINCT c FROM Cotacao c " +
           "LEFT JOIN FETCH c.itensPedido ip " +
           "LEFT JOIN FETCH c.fornecedorProduto " +
           "LEFT JOIN FETCH c.fornecedorServico " +
           "WHERE :itemPedidoId MEMBER OF c.itensPedido")
    List<Cotacao> findByItemPedidoIdWithFornecedores(@Param("itemPedidoId") Long itemPedidoId);
}
