package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CotacaoRepository extends JpaRepository<Cotacao, Long> {
    /**
     * Queries de agregação de preço foram removidas após refatoração do Bug #5.
     * O preço agora é calculado a partir dos itens individuais (CotacaoItem).
     * Se necessário, reimplementar usando JOIN com cotacao_item e SUM dos preços unitários.
     */
    // @Query("SELECT SUM(c.preco) FROM Cotacao c")
    // BigDecimal sumTotalPreco();
    //
    // @Query("SELECT MIN(c.preco) FROM Cotacao c")
    // BigDecimal findMinPreco();
    //
    // @Query("SELECT MAX(c.preco) FROM Cotacao c")
    // BigDecimal findMaxPreco();

    /**
     * Busca cotações associadas a um item de pedido específico, carregando dados dos fornecedores.
     *
     * @param itemPedidoId ID do item de pedido
     * @return Lista de cotações
     */
    @Query("SELECT DISTINCT c FROM Cotacao c " +
           "LEFT JOIN FETCH c.itens ci " +
           "LEFT JOIN FETCH ci.itemPedido ip " +
           "LEFT JOIN FETCH c.fornecedorProduto " +
           "LEFT JOIN FETCH c.fornecedorServico " +
           "WHERE ip.id = :itemPedidoId")
    List<Cotacao> findByItemPedidoIdWithFornecedores(@Param("itemPedidoId") Long itemPedidoId);

    /**
     * Busca todas as cotações carregando relacionamentos para evitar problema N+1.
     * Bug Fix #9: Query otimizada.
     *
     * @return Lista de todas as cotações com relacionamentos carregados
     */
    @Query("SELECT DISTINCT c FROM Cotacao c " +
           "LEFT JOIN FETCH c.itens " +
           "LEFT JOIN FETCH c.fornecedorProduto " +
           "LEFT JOIN FETCH c.fornecedorServico " +
           "LEFT JOIN FETCH c.solicitacaoDePedido")
    List<Cotacao> findAllWithRelationships();
}
