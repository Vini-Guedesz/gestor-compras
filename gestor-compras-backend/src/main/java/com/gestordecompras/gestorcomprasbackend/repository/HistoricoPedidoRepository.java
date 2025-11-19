package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.pedido.HistoricoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoPedidoRepository extends JpaRepository<HistoricoPedido, Long> {

    @Query("SELECT h FROM HistoricoPedido h WHERE h.solicitacaoDePedido.id = :pedidoId ORDER BY h.dataModificacao DESC")
    List<HistoricoPedido> findBySolicitacaoDePedidoIdOrderByDataModificacaoDesc(@Param("pedidoId") Long pedidoId);

    @Query("SELECT h FROM HistoricoPedido h WHERE h.usuario.id = :userId ORDER BY h.dataModificacao DESC")
    List<HistoricoPedido> findByUsuarioIdOrderByDataModificacaoDesc(@Param("userId") Long userId);
}
