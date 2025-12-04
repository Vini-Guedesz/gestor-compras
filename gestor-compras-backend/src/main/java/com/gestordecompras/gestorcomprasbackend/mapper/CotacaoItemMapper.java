package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoItemCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoItemDTO;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.CotacaoItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para CotacaoItem usando MapStruct
 */
@Mapper(componentModel = "spring")
public interface CotacaoItemMapper {

    /**
     * Converte CotacaoItem para DTO de retorno
     */
    @Mapping(target = "cotacaoId", source = "cotacao.id")
    @Mapping(target = "itemPedidoId", source = "itemPedido.id")
    @Mapping(target = "nomeItem", source = "itemPedido.nome")
    @Mapping(target = "precoTotal", expression = "java(entity.calcularPrecoTotal())")
    CotacaoItemDTO toDTO(CotacaoItem entity);

    /**
     * Converte CreateDTO para entidade
     * Nota: cotacao e itemPedido devem ser setados manualmente no service
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cotacao", ignore = true)
    @Mapping(target = "itemPedido", ignore = true)
    CotacaoItem toEntity(CotacaoItemCreateDTO dto);
}
