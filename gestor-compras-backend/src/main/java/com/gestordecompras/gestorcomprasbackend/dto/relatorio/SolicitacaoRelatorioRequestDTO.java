package com.gestordecompras.gestorcomprasbackend.dto.relatorio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoRelatorioRequestDTO {
    private Long solicitacaoId;
    private List<Long> itensIds; // Se vazio ou null, inclui todos os itens
}
