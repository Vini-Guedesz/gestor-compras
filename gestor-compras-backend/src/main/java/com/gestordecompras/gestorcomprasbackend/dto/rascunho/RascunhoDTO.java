package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import java.time.LocalDateTime;
import java.util.List;

public record RascunhoDTO(
    Long id,
    List<ItemRascunhoDTO> itens,
    Long criadorId,
    String observacao,
    LocalDateTime dataCriacao,
    LocalDateTime dataModificacao
) {}
