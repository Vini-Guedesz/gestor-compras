package com.gestordecompras.gestorcomprasbackend.dto.relatorio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemMaisSolicitadoDTO {
    private String nomeItem;
    private Long quantidadeTotal;
    private Long numeroSolicitacoes;
    private String descricao;
}
