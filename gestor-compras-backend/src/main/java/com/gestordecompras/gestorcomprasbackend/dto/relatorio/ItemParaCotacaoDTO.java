package com.gestordecompras.gestorcomprasbackend.dto.relatorio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemParaCotacaoDTO {
    private Long id;
    private String nome;
    private String descricao;
    private Integer quantidade;
    private String observacao;
    private Long solicitacaoId;
}
