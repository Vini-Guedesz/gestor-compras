package com.gestordecompras.gestorcomprasbackend.dto.endereco;

import com.gestordecompras.gestorcomprasbackend.model.endereco.Endereco;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta com detalhes de endereço")
public record EnderecoDTO(
        @Schema(description = "ID único do endereço", example = "1")
        Integer id,

        @Schema(description = "CEP (Código de Endereçamento Postal)", example = "60000-000")
        String cep,

        @Schema(description = "Estado (sigla)", example = "CE")
        String estado,

        @Schema(description = "Cidade", example = "Fortaleza")
        String cidade,

        @Schema(description = "Bairro", example = "Centro")
        String bairro,

        @Schema(description = "Nome da rua", example = "Rua Principal")
        String rua,

        @Schema(description = "Número do imóvel", example = "123")
        String numero,

        @Schema(description = "Complemento (apartamento, sala, etc.)", example = "Apto 401")
        String complemento
) {
    public EnderecoDTO(Endereco endereco) {
        this(endereco.getId(), endereco.getCep(), endereco.getEstado(), endereco.getCidade(), endereco.getBairro(), endereco.getRua(), endereco.getNumero(), endereco.getComplemento());
    }
}
