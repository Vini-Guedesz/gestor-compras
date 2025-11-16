package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ConverterRascunhoParaPedidoDTO(
    @NotNull(message = "IDs dos itens são obrigatórios")
    @NotEmpty(message = "Deve selecionar pelo menos um item")
    List<Long> itemRascunhoIds
) {}
