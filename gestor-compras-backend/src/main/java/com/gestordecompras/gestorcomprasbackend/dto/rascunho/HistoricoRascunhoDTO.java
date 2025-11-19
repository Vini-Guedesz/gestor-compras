package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import java.time.LocalDateTime;

public record HistoricoRascunhoDTO(
    Long id,
    Long rascunhoId,
    Integer usuarioId,
    String nomeUsuario,
    LocalDateTime dataModificacao,
    String tipoAcao,
    String descricao,
    Integer numeroItem,
    String nomeItem,
    String detalhes
) {}
