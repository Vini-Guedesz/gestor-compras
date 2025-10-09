package com.gestordecompras.gestorcomprasbackend.dto.contato;

import com.gestordecompras.gestorcomprasbackend.validation.Celular;
import com.gestordecompras.gestorcomprasbackend.validation.TelefoneFixo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ContatoUpdateDTO(
        @NotNull(message = "O ID é obrigatório")
        Integer id,

        @TelefoneFixo
        String telefoneFixo,

        @Celular
        String celular,

        @Email(message = "E-mail inválido")
        String email
) {
}
