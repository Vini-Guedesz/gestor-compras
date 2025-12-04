package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO para criação de itens de cotação
 */
public record CotacaoItemCreateDTO(
        @NotNull(message = "Item de pedido é obrigatório")
        Long itemPedidoId,

        @NotNull(message = "Preço unitário é obrigatório")
        @Positive(message = "Preço unitário deve ser positivo")
        BigDecimal precoUnitario,

        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
        Integer quantidade,

        String observacao
) {
}
