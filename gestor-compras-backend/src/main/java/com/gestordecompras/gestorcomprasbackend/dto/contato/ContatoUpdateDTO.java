package com.gestordecompras.gestorcomprasbackend.dto.contato;

import com.gestordecompras.gestorcomprasbackend.validation.Celular;
import com.gestordecompras.gestorcomprasbackend.validation.TelefoneFixo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ContatoUpdateDTO(
        @NotNull(message = "O ID e obrigatorio")
        Integer id,

        @TelefoneFixo
        String telefoneFixo,

        @Size(max = 100, message = "O rótulo do telefone fixo deve ter no máximo 100 caracteres")
        String rotuloTelefoneFixo,

        @Celular
        String celular,

        @Size(max = 100, message = "O rótulo do celular deve ter no máximo 100 caracteres")
        String rotuloCelular,

        @Email(message = "E-mail invalido")
        String email,

        @Size(max = 100, message = "O rótulo do e-mail deve ter no máximo 100 caracteres")
        String rotuloEmail,

        List<@Valid ContatoAdicionalUpdateDTO> contatosAdicionais
) {
}
