package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para conversão entre FornecedorDeProduto e seus DTOs.
 */
@Mapper(componentModel = "spring", uses = {EnderecoMapper.class, ContatoMapper.class})
public interface FornecedorDeProdutoMapper {

    FornecedorDeProdutoMapper INSTANCE = Mappers.getMapper(FornecedorDeProdutoMapper.class);

    FornecedorDeProdutoDTO toDTO(FornecedorDeProduto fornecedor);

    @Mapping(target = "id", ignore = true)
    FornecedorDeProduto toEntity(FornecedorDeProdutoCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(FornecedorDeProdutoUpdateDTO dto, @MappingTarget FornecedorDeProduto fornecedor);
}
