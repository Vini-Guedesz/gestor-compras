package com.gestordecompras.gestorcomprasbackend.dto.fornecedor;

import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.endereco.EnderecoUpdateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record FornecedorDeServicoUpdateDTO(
        @NotNull(message = "O ID é obrigatório")
        Integer id,

        String razaoSocial,

        String cnpj,

        @Valid
        EnderecoUpdateDTO endereco,

        @Valid
        ContatoUpdateDTO contato,

        String inscricaoMunicipal
) {
}
