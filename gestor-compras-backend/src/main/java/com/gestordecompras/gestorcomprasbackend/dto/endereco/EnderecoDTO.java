package com.gestordecompras.gestorcomprasbackend.dto.endereco;

import com.gestordecompras.gestorcomprasbackend.model.endereco.Endereco;

public record EnderecoDTO(
        Integer id,
        String cep,
        String estado,
        String cidade,
        String bairro,
        String rua,
        String numero,
        String complemento
) {
    public EnderecoDTO(Endereco endereco) {
        this(endereco.getId(), endereco.getCep(), endereco.getEstado(), endereco.getCidade(), endereco.getBairro(), endereco.getRua(), endereco.getNumero(), endereco.getComplemento());
    }
}
