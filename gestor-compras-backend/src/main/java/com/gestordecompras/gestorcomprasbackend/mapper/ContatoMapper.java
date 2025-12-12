package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.model.contato.Contato;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para conversão entre Contato e seus DTOs.
 */
@Mapper(componentModel = "spring")
public interface ContatoMapper {

    ContatoMapper INSTANCE = Mappers.getMapper(ContatoMapper.class);

    ContatoDTO toDTO(Contato contato);

    @Mapping(target = "id", ignore = true)
    Contato toEntity(ContatoCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(ContatoUpdateDTO dto, @MappingTarget Contato contato);
}
