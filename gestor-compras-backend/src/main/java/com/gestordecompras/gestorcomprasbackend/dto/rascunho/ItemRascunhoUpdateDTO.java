package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ItemRascunhoUpdateDTO(
    @NotBlank(message = "O nome do item é obrigatório")
    @Size(min = 3, max = 200, message = "O nome deve ter entre 3 e 200 caracteres")
    String nome,

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    @Max(value = 999999, message = "A quantidade deve ser no máximo 999999")
    Integer quantidade,

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    String descricao,

    @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres")
    String observacao
) {}
