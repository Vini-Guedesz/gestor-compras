package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido.SolicitacaoDePedidoDTO;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import org.mapstruct.Mapper;

/**
 * Mapper para conversão entre SolicitacaoDePedido e seus DTOs.
 */
@Mapper(componentModel = "spring", uses = {ItemPedidoMapper.class, CotacaoMapper.class})
public interface SolicitacaoDePedidoMapper {
    SolicitacaoDePedidoDTO toDTO(SolicitacaoDePedido solicitacaoDePedido);
    SolicitacaoDePedido toEntity(SolicitacaoDePedidoDTO solicitacaoDePedidoDTO);
}