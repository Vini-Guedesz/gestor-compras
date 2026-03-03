package com.gestordecompras.gestorcomprasbackend.dto.contato;

import com.gestordecompras.gestorcomprasbackend.model.contato.Contato;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO de resposta com detalhes de contato")
public record ContatoDTO(
        @Schema(description = "ID unico do contato", example = "1")
        Integer id,

        @Schema(description = "Numero de telefone fixo", example = "(88) 1234-5678")
        String telefoneFixo,

        @Schema(description = "Rotulo do telefone fixo", example = "Comercial")
        String rotuloTelefoneFixo,

        @Schema(description = "Numero de celular", example = "(88) 98765-4321")
        String celular,

        @Schema(description = "Rotulo do celular", example = "Plantao")
        String rotuloCelular,

        @Schema(description = "Endereco de e-mail", example = "contato@empresa.com")
        String email,

        @Schema(description = "Rotulo do e-mail", example = "Financeiro")
        String rotuloEmail,

        @Schema(description = "Lista de contatos adicionais nomeados")
        List<ContatoAdicionalDTO> contatosAdicionais
) {
    public ContatoDTO(Contato contato) {
        this(
            contato.getId(),
            contato.getTelefoneFixo(),
            contato.getRotuloTelefoneFixo(),
            contato.getCelular(),
            contato.getRotuloCelular(),
            contato.getEmail(),
            contato.getRotuloEmail(),
            contato.getContatosAdicionais() != null
                ? contato.getContatosAdicionais().stream().map(ContatoAdicionalDTO::new).toList()
                : List.of()
        );
    }
}
