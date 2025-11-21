package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CotacaoCreateDTO(
        @NotNull(message = "ID do fornecedor é obrigatório")
        Integer fornecedorId,

        @NotNull(message = "Tipo do fornecedor é obrigatório")
        String tipoFornecedor,

        @NotNull(message = "ID da solicitação de pedido é obrigatório")
        Long solicitacaoDePedidoId,

        @NotNull(message = "IDs dos itens do pedido são obrigatórios")
        @NotEmpty(message = "Deve selecionar pelo menos um item do pedido")
        List<Long> itensPedidoIds,

        @NotNull(message = "Preço é obrigatório")
        @Positive(message = "Preço deve ser maior que zero")  // Bug Fix #11
        BigDecimal preco,

        Integer prazoEmDiasUteis,
        LocalDate dataLimite,
        byte[] anexoPdf
) {
}
