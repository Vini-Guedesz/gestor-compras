package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RascunhoCreateDTO(
    @Valid
    List<ItemRascunhoCreateDTO> itens,

    String observacao
) {}
