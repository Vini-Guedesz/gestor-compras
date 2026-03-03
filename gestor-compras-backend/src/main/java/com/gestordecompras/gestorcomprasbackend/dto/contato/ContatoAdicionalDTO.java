package com.gestordecompras.gestorcomprasbackend.dto.contato;

import com.gestordecompras.gestorcomprasbackend.model.contato.ContatoAdicional;
import com.gestordecompras.gestorcomprasbackend.model.contato.TipoContatoAdicional;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Contato adicional do fornecedor")
public record ContatoAdicionalDTO(
        @Schema(description = "ID do contato adicional", example = "12")
        Integer id,

        @Schema(description = "Nome/rotulo do contato", example = "Comercial")
        String nomeContato,

        @Schema(description = "Tipo de contato", example = "CELULAR")
        TipoContatoAdicional tipoContato,

        @Schema(description = "Valor do contato", example = "(11) 99999-8888")
        String valorContato,

        @Schema(description = "Ordem de exibicao", example = "1")
        Integer ordemExibicao
) {
    public ContatoAdicionalDTO(ContatoAdicional contatoAdicional) {
        this(
            contatoAdicional.getId(),
            contatoAdicional.getNomeContato(),
            contatoAdicional.getTipoContato(),
            contatoAdicional.getValorContato(),
            contatoAdicional.getOrdemExibicao()
        );
    }
}

