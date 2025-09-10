package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.itempedido.ItemPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {
    ItemPedidoDTO toDTO(ItemPedido itemPedido);

    @Mapping(target = "solicitacaoDePedido", ignore = true)
    ItemPedido toEntity(ItemPedidoDTO itemPedidoDTO);
}