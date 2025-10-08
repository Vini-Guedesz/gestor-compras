package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CotacaoUpdateDTO(
        @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
        BigDecimal preco,

        @FutureOrPresent(message = "O prazo de entrega não pode ser no passado")
        LocalDate prazoEntrega,

        byte[] anexoPdf,
        String caminhoAnexo
) {
}
