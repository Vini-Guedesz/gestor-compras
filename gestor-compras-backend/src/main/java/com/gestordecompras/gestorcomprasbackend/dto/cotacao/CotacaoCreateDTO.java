package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CotacaoCreateDTO(
        @NotNull Integer fornecedorId,
        @NotNull String tipoFornecedor,
        @NotNull Long itemPedidoId,
        @NotNull BigDecimal preco,
        LocalDate prazoEntrega
) {
}
