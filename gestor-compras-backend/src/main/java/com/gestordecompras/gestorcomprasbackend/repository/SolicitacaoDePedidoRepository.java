package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoDePedidoRepository extends JpaRepository<SolicitacaoDePedido, Long> {
}