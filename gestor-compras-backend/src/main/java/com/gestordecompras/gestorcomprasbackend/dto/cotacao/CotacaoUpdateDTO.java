package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CotacaoUpdateDTO(
        @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
        BigDecimal preco,

        @Min(value = 1, message = "O prazo deve ser de pelo menos 1 dia útil")
        Integer prazoEmDiasUteis,

        @FutureOrPresent(message = "A data limite não pode ser no passado")
        LocalDate dataLimite,

        byte[] anexoPdf,
        String caminhoAnexo
) {
}
