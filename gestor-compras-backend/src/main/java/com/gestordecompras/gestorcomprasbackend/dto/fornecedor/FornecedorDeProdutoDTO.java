package com.gestordecompras.gestorcomprasbackend.dto.fornecedor;

import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.endereco.EnderecoDTO;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;

public record FornecedorDeProdutoDTO(
        Integer id,
        String razaoSocial,
        String cnpj,
        EnderecoDTO endereco,
        ContatoDTO contato,
        String inscricaoEstadual
) {
    public FornecedorDeProdutoDTO(FornecedorDeProduto fornecedor) {
        this(
                fornecedor.getId(),
                fornecedor.getRazaoSocial(),
                fornecedor.getCnpj(),
                new EnderecoDTO(fornecedor.getEndereco()),
                new ContatoDTO(fornecedor.getContato()),
                fornecedor.getInscricaoEstadual()
        );
    }
}
