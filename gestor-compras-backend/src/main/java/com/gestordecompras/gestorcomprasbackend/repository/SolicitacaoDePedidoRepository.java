package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitacaoDePedidoRepository extends JpaRepository<SolicitacaoDePedido, Long> {
    @Query("SELECT count(s) FROM SolicitacaoDePedido s WHERE s.status in :statuses")
    long countByStatusIn(@Param("statuses") List<StatusPedido> statuses);

    @Query("SELECT sum(size(s.itens)) FROM SolicitacaoDePedido s")
    Long countTotalItens();

    @Query("SELECT s FROM SolicitacaoDePedido s LEFT JOIN FETCH s.itens WHERE s.status IN :statuses ORDER BY s.dataCriacao DESC")
    List<SolicitacaoDePedido> findByStatusInWithItens(@Param("statuses") List<StatusPedido> statuses);

    // Bug Fix #1: Query para carregar apenas itens (primeira query)
    @Query("SELECT DISTINCT s FROM SolicitacaoDePedido s " +
           "LEFT JOIN FETCH s.itens " +
           "WHERE s.id = :id")
    Optional<SolicitacaoDePedido> findByIdWithItens(@Param("id") Long id);

    // Queries auxiliares para carregar cotações separadamente (evitar produto cartesiano)
    @Query("SELECT DISTINCT s FROM SolicitacaoDePedido s " +
           "LEFT JOIN FETCH s.cotacoes c " +
           "LEFT JOIN FETCH c.fornecedorProduto " +
           "LEFT JOIN FETCH c.fornecedorServico " +
           "WHERE s.id = :id")
    Optional<SolicitacaoDePedido> findByIdWithCotacoes(@Param("id") Long id);

    // Query para carregar itens das cotações (atualizada após refatoração Bug #5)
    @Query("SELECT DISTINCT c FROM Cotacao c " +
           "LEFT JOIN FETCH c.itens " +
           "WHERE c.solicitacaoDePedido.id = :pedidoId")
    List<Cotacao> findCotacoesWithItensByPedidoId(@Param("pedidoId") Long pedidoId);

    // Método que será chamado no service combinando as queries acima
    @Query("SELECT DISTINCT s FROM SolicitacaoDePedido s " +
           "LEFT JOIN FETCH s.itens " +
           "WHERE s.id = :id")
    Optional<SolicitacaoDePedido> findByIdWithItensAndCotacoes(@Param("id") Long id);
}