package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import com.gestordecompras.gestorcomprasbackend.validation.PdfSize;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CotacaoUpdateDTO(
        @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "O preço deve ter no máximo 10 dígitos inteiros e 2 decimais")
        BigDecimal preco,

        @Min(value = 1, message = "O prazo deve ser de pelo menos 1 dia útil")
        Integer prazoEmDiasUteis,

        @FutureOrPresent(message = "A data limite não pode ser no passado")
        LocalDate dataLimite,

        /**
         * DEPRECATED: anexoPdf e caminhoAnexo estão deprecated.
         * PDFs são gerenciados via AnexoCotacao com deduplificação SHA-256.
         * Use o endpoint POST /api/cotacoes/{id}/anexos com multipart/form-data.
         * Mantido para compatibilidade com código existente.
         */
        @Deprecated
        @PdfSize(maxBytes = 10485760L, message = "PDF deve ter no máximo 10MB")
        byte[] anexoPdf,

        @Deprecated
        String caminhoAnexo
) {
}
