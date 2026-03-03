package com.gestordecompras.gestorcomprasbackend.dto.contato;

import com.gestordecompras.gestorcomprasbackend.validation.Celular;
import com.gestordecompras.gestorcomprasbackend.validation.TelefoneFixo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ContatoCreateDTO(
        @TelefoneFixo
        String telefoneFixo,

        @Size(max = 100, message = "O rótulo do telefone fixo deve ter no máximo 100 caracteres")
        String rotuloTelefoneFixo,

        @Celular
        String celular,

        @Size(max = 100, message = "O rótulo do celular deve ter no máximo 100 caracteres")
        String rotuloCelular,

        @Email(message = "E-mail invalido")
        @NotBlank(message = "O e-mail e obrigatorio")
        String email,

        @Size(max = 100, message = "O rótulo do e-mail deve ter no máximo 100 caracteres")
        String rotuloEmail,

        List<@Valid ContatoAdicionalCreateDTO> contatosAdicionais
) {
}
