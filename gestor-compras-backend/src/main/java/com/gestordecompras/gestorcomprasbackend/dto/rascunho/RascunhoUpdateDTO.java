package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RascunhoUpdateDTO(
    @Valid
    List<ItemRascunhoCreateDTO> itens,

    @Size(max = 1000, message = "A observação deve ter no máximo 1000 caracteres")
    String observacao
) {}
