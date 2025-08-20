package com.gestordecompras.gestorcomprasbackend.dto.endereco;

import jakarta.validation.constraints.NotNull;

public record EnderecoUpdateDTO(
        @NotNull(message = "O ID é obrigatório")
        Integer id,

        String cep,

        String estado,

        String cidade,

        String bairro,

        String rua,

        String numero,

        String complemento
) {
}
