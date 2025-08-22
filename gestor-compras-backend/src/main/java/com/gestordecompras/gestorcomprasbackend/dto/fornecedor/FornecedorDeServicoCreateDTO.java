package com.gestordecompras.gestorcomprasbackend.dto.fornecedor;

import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.endereco.EnderecoCreateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FornecedorDeServicoCreateDTO(
        @NotBlank(message = "A razão social é obrigatória")
        String razaoSocial,

        @NotBlank(message = "O CNPJ é obrigatório")
        String cnpj,

        @NotNull(message = "O endereço é obrigatório")
        @Valid
        EnderecoCreateDTO endereco,

        @NotNull(message = "O contato é obrigatório")
        @Valid
        ContatoCreateDTO contato,

        String inscricaoMunicipal
) {
}
