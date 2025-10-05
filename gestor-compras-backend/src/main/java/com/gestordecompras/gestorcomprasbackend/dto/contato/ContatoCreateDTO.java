package com.gestordecompras.gestorcomprasbackend.dto.contato;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ContatoCreateDTO(
        @NotBlank(message = "O número é obrigatório")
        @Pattern(regexp = "^(?:\\+55\\s?)?(?:\\(?[1-9]{2}\\)?.\\s?)?(?:9\\d{4}-?\\d{4})$", message = "Formato de telefone inválido")
        String numero,

        @Email(message = "E-mail inválido")
        @NotBlank(message = "O e-mail é obrigatório")
        String email
) {
}
