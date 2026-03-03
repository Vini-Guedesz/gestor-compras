package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.model.contato.Contato;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para conversao entre Contato e seus DTOs.
 */
@Mapper(componentModel = "spring", uses = {ContatoAdicionalMapper.class})
public interface ContatoMapper {

    ContatoMapper INSTANCE = Mappers.getMapper(ContatoMapper.class);

    ContatoDTO toDTO(Contato contato);

    @Mapping(target = "id", ignore = true)
    Contato toEntity(ContatoCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(ContatoUpdateDTO dto, @MappingTarget Contato contato);

    @AfterMapping
    default void vincularContatosAdicionais(@MappingTarget Contato contato) {
        if (contato.getContatosAdicionais() != null) {
            contato.getContatosAdicionais().forEach(contatoAdicional -> contatoAdicional.setContato(contato));
        }
    }
}

