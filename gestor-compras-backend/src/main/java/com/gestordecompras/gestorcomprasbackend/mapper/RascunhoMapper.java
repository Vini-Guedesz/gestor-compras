package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.rascunho.RascunhoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.RascunhoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.RascunhoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.Rascunho;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {ItemRascunhoMapper.class})
public interface RascunhoMapper {

    @Mapping(source = "criador.id", target = "criadorId")
    RascunhoDTO toDTO(Rascunho rascunho);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criador", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataModificacao", ignore = true)
    @Mapping(target = "proximoNumeroItem", ignore = true)
    @Mapping(target = "numerosDisponiveis", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "pedidoGeradoId", ignore = true)
    Rascunho toEntity(RascunhoCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criador", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataModificacao", ignore = true)
    @Mapping(target = "proximoNumeroItem", ignore = true)
    @Mapping(target = "numerosDisponiveis", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "pedidoGeradoId", ignore = true)
    void updateEntityFromDTO(RascunhoUpdateDTO dto, @MappingTarget Rascunho rascunho);
}
