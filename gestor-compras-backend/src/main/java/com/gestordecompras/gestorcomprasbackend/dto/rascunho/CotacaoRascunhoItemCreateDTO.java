package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CotacaoRascunhoItemCreateDTO(
    @NotNull(message = "ID do item do rascunho é obrigatório")
    Long itemRascunhoId,

    @NotNull(message = "Preço unitário é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço unitário deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "O preço unitário deve ter no máximo 10 dígitos inteiros e 2 decimais")
    BigDecimal precoUnitario,

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser de pelo menos 1")
    Integer quantidade,

    String observacao
) {}
