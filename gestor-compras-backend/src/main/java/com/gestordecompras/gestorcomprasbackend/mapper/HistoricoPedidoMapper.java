package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.pedido.HistoricoPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.model.pedido.HistoricoPedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoricoPedidoMapper {

    @Mapping(source = "solicitacaoDePedido.id", target = "solicitacaoDePedidoId")
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.username", target = "nomeUsuario")
    HistoricoPedidoDTO toDTO(HistoricoPedido historicoPedido);
}
