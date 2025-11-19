package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemRascunhoCreateDTO(
    @NotBlank(message = "Nome do item é obrigatório")
    String nome,

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    Integer quantidade,

    String descricao,
    String observacao
) {}
