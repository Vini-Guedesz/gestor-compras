package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

public record ItemRascunhoDTO(
    Long id,
    Integer numeroItem,
    String nome,
    Integer quantidade,
    String descricao,
    String observacao
) {}
