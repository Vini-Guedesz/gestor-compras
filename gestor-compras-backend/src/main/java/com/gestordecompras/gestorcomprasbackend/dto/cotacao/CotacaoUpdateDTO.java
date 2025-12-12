package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import com.gestordecompras.gestorcomprasbackend.validation.PdfSize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para atualização de cotação existente.
 * <p>
 * Permite modificar preço, prazo e data limite de uma cotação.
 * </p>
 */
@Schema(description = "Dados para atualização de uma cotação existente")
public record CotacaoUpdateDTO(
        @Schema(description = "Novo preço total da cotação", example = "1600.00")
        @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "O preço deve ter no máximo 10 dígitos inteiros e 2 decimais")
        BigDecimal preco,

        @Schema(description = "Novo prazo de entrega em dias úteis", example = "7")
        @Min(value = 1, message = "O prazo deve ser de pelo menos 1 dia útil")
        Integer prazoEmDiasUteis,

        @Schema(description = "Nova data de validade da cotação", example = "2026-01-31")
        @FutureOrPresent(message = "A data limite não pode ser no passado")
        LocalDate dataLimite,

        /**
         * DEPRECATED: anexoPdf e caminhoAnexo estão deprecated.
         * PDFs são gerenciados via AnexoCotacao com deduplificação SHA-256.
         * Use o endpoint POST /api/cotacoes/{id}/anexos com multipart/form-data.
         * Mantido para compatibilidade com código existente.
         */
        @Deprecated
        @Schema(description = "Arquivo PDF anexo (Deprecated - Use endpoint específico de upload)", deprecated = true)
        @PdfSize(maxBytes = 10485760L, message = "PDF deve ter no máximo 10MB")
        byte[] anexoPdf,

        @Deprecated
        @Schema(description = "Caminho do anexo (Deprecated)", deprecated = true)
        String caminhoAnexo
) {
}
