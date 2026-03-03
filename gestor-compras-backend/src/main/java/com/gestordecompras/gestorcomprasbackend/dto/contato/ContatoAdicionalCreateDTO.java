package com.gestordecompras.gestorcomprasbackend.dto.contato;

import com.gestordecompras.gestorcomprasbackend.model.contato.TipoContatoAdicional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContatoAdicionalCreateDTO(
        @Size(max = 100, message = "O nome do contato deve ter no maximo 100 caracteres")
        String nomeContato,

        @NotNull(message = "O tipo de contato adicional e obrigatorio")
        TipoContatoAdicional tipoContato,

        @NotBlank(message = "O valor do contato adicional e obrigatorio")
        @Size(max = 255, message = "O valor do contato deve ter no maximo 255 caracteres")
        String valorContato,

        Integer ordemExibicao
) {
}

