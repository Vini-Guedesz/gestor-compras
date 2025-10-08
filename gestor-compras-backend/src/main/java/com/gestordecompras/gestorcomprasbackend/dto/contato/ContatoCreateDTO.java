package com.gestordecompras.gestorcomprasbackend.dto.contato;

import com.gestordecompras.gestorcomprasbackend.validation.Celular;
import com.gestordecompras.gestorcomprasbackend.validation.TelefoneFixo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContatoCreateDTO(
        @TelefoneFixo
        String telefoneFixo,

        @Celular
        String celular,

        @Email(message = "E-mail inválido")
        @NotBlank(message = "O e-mail é obrigatório")
        String email
) {
}
