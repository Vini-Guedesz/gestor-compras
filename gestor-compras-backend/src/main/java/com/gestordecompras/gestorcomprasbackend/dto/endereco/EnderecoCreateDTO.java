package com.gestordecompras.gestorcomprasbackend.dto.endereco;

import com.gestordecompras.gestorcomprasbackend.validation.CEP;
import jakarta.validation.constraints.NotBlank;

public record EnderecoCreateDTO(
        @NotBlank(message = "O CEP é obrigatório")
        @CEP
        String cep,

        @NotBlank(message = "O estado é obrigatório")
        String estado,

        @NotBlank(message = "A cidade é obrigatória")
        String cidade,

        @NotBlank(message = "O bairro é obrigatório")
        String bairro,

        @NotBlank(message = "A rua é obrigatória")
        String rua,

        @NotBlank(message = "O número é obrigatório")
        String numero,

        String complemento
) {
}
