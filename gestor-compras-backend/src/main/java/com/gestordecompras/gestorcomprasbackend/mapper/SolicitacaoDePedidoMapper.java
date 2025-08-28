package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido.SolicitacaoDePedidoDTO;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ItemPedidoMapper.class})
public interface SolicitacaoDePedidoMapper {
    SolicitacaoDePedidoDTO toDTO(SolicitacaoDePedido solicitacaoDePedido);
    SolicitacaoDePedido toEntity(SolicitacaoDePedidoDTO solicitacaoDePedidoDTO);
}