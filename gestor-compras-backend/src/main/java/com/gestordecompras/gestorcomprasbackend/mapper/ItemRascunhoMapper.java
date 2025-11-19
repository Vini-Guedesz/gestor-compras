package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.rascunho.ItemRascunhoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.ItemRascunhoDTO;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.ItemRascunho;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemRascunhoMapper {

    ItemRascunhoDTO toDTO(ItemRascunho itemRascunho);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rascunho", ignore = true)
    @Mapping(target = "numeroItem", ignore = true)
    ItemRascunho toEntity(ItemRascunhoCreateDTO dto);
}
