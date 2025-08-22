package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {EnderecoMapper.class, ContatoMapper.class})
public interface FornecedorDeProdutoMapper {

    FornecedorDeProdutoMapper INSTANCE = Mappers.getMapper(FornecedorDeProdutoMapper.class);

    FornecedorDeProdutoDTO toDTO(FornecedorDeProduto fornecedor);

    FornecedorDeProduto toEntity(FornecedorDeProdutoCreateDTO dto);

    void updateEntityFromDTO(FornecedorDeProdutoUpdateDTO dto, @MappingTarget FornecedorDeProduto fornecedor);
}
