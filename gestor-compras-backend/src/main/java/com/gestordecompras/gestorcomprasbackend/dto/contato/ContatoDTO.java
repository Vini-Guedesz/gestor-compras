package com.gestordecompras.gestorcomprasbackend.dto.contato;

import com.gestordecompras.gestorcomprasbackend.model.contato.Contato;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta com detalhes de contato")
public record ContatoDTO(
        @Schema(description = "ID único do contato", example = "1")
        Integer id,

        @Schema(description = "Número de telefone fixo", example = "(88) 1234-5678")
        String telefoneFixo,

        @Schema(description = "Número de celular", example = "(88) 98765-4321")
        String celular,

        @Schema(description = "Endereço de e-mail", example = "contato@empresa.com")
        String email
) {
    public ContatoDTO(Contato contato) {
        this(contato.getId(), contato.getTelefoneFixo(), contato.getCelular(), contato.getEmail());
    }
}
