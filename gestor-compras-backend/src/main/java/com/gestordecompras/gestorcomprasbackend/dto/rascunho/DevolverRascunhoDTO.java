package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para devolução de rascunho em cotação para edição.
 * Requer motivo obrigatório para rastreabilidade.
 */
public record DevolverRascunhoDTO(
    @NotBlank(message = "O motivo da devolução é obrigatório")
    @Size(min = 10, max = 500, message = "O motivo deve ter entre 10 e 500 caracteres")
    String motivo
) {}
