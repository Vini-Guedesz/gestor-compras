package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeServicoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeServicoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeServicoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeServico;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {EnderecoMapper.class, ContatoMapper.class})
public interface FornecedorDeServicoMapper {

    FornecedorDeServicoMapper INSTANCE = Mappers.getMapper(FornecedorDeServicoMapper.class);

    FornecedorDeServicoDTO toDTO(FornecedorDeServico fornecedor);

    FornecedorDeServico toEntity(FornecedorDeServicoCreateDTO dto);

    void updateEntityFromDTO(FornecedorDeServicoUpdateDTO dto, @MappingTarget FornecedorDeServico fornecedor);
}
