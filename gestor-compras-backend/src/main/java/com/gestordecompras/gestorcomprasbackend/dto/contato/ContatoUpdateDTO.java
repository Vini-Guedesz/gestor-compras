package com.gestordecompras.gestorcomprasbackend.dto.contato;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ContatoUpdateDTO(
        @NotNull(message = "O ID é obrigatório")
        Integer id,

        String numero,

        @Email(message = "E-mail inválido")
        String email
) {
}
