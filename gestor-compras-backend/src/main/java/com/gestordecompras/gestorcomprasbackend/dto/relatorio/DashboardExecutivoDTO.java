package com.gestordecompras.gestorcomprasbackend.dto.relatorio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardExecutivoDTO {
    private Long totalSolicitacoes;
    private Long solicitacoesAbertas;
    private Long solicitacoesFechadas;
    private Long solicitacoesPendentes;
    private Long solicitacoesEmAnalise;
    private Long solicitacoesEmAndamento;
    private Long solicitacoesAprovadas;
    private Long solicitacoesCanceladas;
    private Long totalItens;
    private Long totalCotacoes;
    private BigDecimal valorTotalEstimado;
    private BigDecimal ticketMedio;
}
