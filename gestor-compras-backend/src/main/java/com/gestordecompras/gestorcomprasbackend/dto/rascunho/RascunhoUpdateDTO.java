package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import jakarta.validation.Valid;

import java.util.List;

public record RascunhoUpdateDTO(
    @Valid
    List<ItemRascunhoCreateDTO> itens,

    String observacao
) {}
