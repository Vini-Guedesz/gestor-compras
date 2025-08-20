package com.gestordecompras.gestorcomprasbackend.dto.contato;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContatoCreateDTO(
        @NotBlank(message = "O número é obrigatório")
        String numero,

        @Email(message = "E-mail inválido")
        @NotBlank(message = "O e-mail é obrigatório")
        String email
) {
}
