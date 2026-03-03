package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoAdicionalCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoAdicionalDTO;
import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoAdicionalUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.model.contato.ContatoAdicional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContatoAdicionalMapper {

    ContatoAdicionalDTO toDTO(ContatoAdicional contatoAdicional);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contato", ignore = true)
    ContatoAdicional toEntity(ContatoAdicionalCreateDTO dto);

    @Mapping(target = "contato", ignore = true)
    ContatoAdicional toEntity(ContatoAdicionalUpdateDTO dto);
}

