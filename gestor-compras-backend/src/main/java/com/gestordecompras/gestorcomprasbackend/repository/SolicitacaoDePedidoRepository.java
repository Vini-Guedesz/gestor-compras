package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoDePedidoRepository extends JpaRepository<SolicitacaoDePedido, Long> {
    @Query("SELECT count(s) FROM SolicitacaoDePedido s WHERE s.status in :statuses")
    long countByStatusIn(@Param("statuses") List<StatusPedido> statuses);

    @Query("SELECT sum(size(s.itens)) FROM SolicitacaoDePedido s")
    Long countTotalItens();

    @Query("SELECT s FROM SolicitacaoDePedido s LEFT JOIN FETCH s.itens WHERE s.status IN :statuses ORDER BY s.dataCriacao DESC")
    List<SolicitacaoDePedido> findByStatusInWithItens(@Param("statuses") List<StatusPedido> statuses);
}