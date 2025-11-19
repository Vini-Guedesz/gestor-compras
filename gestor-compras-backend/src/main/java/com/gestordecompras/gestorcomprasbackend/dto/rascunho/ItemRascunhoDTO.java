package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

public record ItemRascunhoDTO(
    Long id,
    String nome,
    Integer quantidade,
    String descricao,
    String observacao
) {}
