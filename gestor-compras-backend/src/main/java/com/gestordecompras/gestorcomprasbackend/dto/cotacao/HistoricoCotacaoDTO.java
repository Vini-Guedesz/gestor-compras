package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import com.gestordecompras.gestorcomprasbackend.model.cotacao.StatusCotacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para retornar historico de edicoes de cotacao.
 */
public record HistoricoCotacaoDTO(
        Long id,
        Long cotacaoId,
        Integer versao,

        // Dados anteriores
        BigDecimal precoAnterior,
        Integer prazoEmDiasUteisAnterior,
        LocalDate dataLimiteAnterior,

        // Dados novos
        BigDecimal precoNovo,
        Integer prazoEmDiasUteisNovo,
        LocalDate dataLimiteNovo,

        // Auditoria
        String motivoEdicao,
        String editadoPor,
        LocalDateTime dataEdicao,

        // PDFs (nao enviamos os bytes, apenas indicamos se existem)
        Boolean temAnexoAnterior,

        /**
         * DEPRECATED: caminhoAnexoAnterior e caminhoAnexoNovo estao deprecated.
         * PDFs sao gerenciados via AnexoCotacao com deduplicacao SHA-256.
         * Sempre retornam null.
         */
        @Deprecated
        String caminhoAnexoAnterior,

        Boolean temAnexoNovo,

        @Deprecated
        String caminhoAnexoNovo,

        String nomeArquivoAnterior,
        String nomeArquivoNovo,

        // Status e selecao de itens (auditoria)
        StatusCotacao statusFinal,
        String itensSelecionados,
        String itensNaoSelecionados,
        
        // Snapshots de itens
        String itensAnteriores,
        String itensNovos
) {
    public boolean houveMudancaPreco() {
        if (precoAnterior == null || precoNovo == null) return false;
        return precoAnterior.compareTo(precoNovo) != 0;
    }

    public BigDecimal calcularPercentualMudancaPreco() {
        if (!houveMudancaPreco() || precoAnterior.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal diferenca = precoNovo.subtract(precoAnterior);
        return diferenca.divide(precoAnterior, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public boolean houveMudancaPrazo() {
        if (prazoEmDiasUteisAnterior == null || prazoEmDiasUteisNovo == null) return false;
        return !prazoEmDiasUteisAnterior.equals(prazoEmDiasUteisNovo);
    }
}
