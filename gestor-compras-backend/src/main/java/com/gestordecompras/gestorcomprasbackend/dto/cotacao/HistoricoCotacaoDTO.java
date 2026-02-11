package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para retornar histórico de edições de cotação
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

        // PDFs (não enviamos os bytes, apenas indicamos se existem)
        Boolean temAnexoAnterior,

        /**
         * DEPRECATED: caminhoAnexoAnterior e caminhoAnexoNovo estão deprecated.
         * PDFs são gerenciados via AnexoCotacao com deduplificação SHA-256.
         * Sempre retornam null.
         */
        @Deprecated
        String caminhoAnexoAnterior,

        Boolean temAnexoNovo,

        @Deprecated
        String caminhoAnexoNovo,

        String nomeArquivoAnterior,
        String nomeArquivoNovo
) {
    /**
     * Verifica se houve mudança de preço
     */
    public boolean houveMudancaPreco() {
        if (precoAnterior == null || precoNovo == null) return false;
        return precoAnterior.compareTo(precoNovo) != 0;
    }

    /**
     * Calcula o percentual de mudança no preço
     */
    public BigDecimal calcularPercentualMudancaPreco() {
        if (!houveMudancaPreco() || precoAnterior.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal diferenca = precoNovo.subtract(precoAnterior);
        return diferenca.divide(precoAnterior, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Verifica se houve mudança de prazo
     */
    public boolean houveMudancaPrazo() {
        if (prazoEmDiasUteisAnterior == null || prazoEmDiasUteisNovo == null) return false;
        return !prazoEmDiasUteisAnterior.equals(prazoEmDiasUteisNovo);
    }
}
