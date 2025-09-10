package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.endereco.EnderecoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.endereco.EnderecoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.endereco.EnderecoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.model.endereco.Endereco;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    EnderecoMapper INSTANCE = Mappers.getMapper(EnderecoMapper.class);

    EnderecoDTO toDTO(Endereco endereco);

    @Mapping(target = "id", ignore = true)
    Endereco toEntity(EnderecoCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(EnderecoUpdateDTO dto, @MappingTarget Endereco endereco);
}
