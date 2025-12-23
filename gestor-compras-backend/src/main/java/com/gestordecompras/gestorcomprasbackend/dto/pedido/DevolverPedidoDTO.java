package com.gestordecompras.gestorcomprasbackend.dto.pedido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para devolução de pedido de PENDENTE_APROVACAO para EM_NEGOCIACAO.
 * Requer motivo obrigatório para rastreabilidade.
 */
public record DevolverPedidoDTO(
    @NotBlank(message = "O motivo da devolução é obrigatório")
    @Size(min = 10, max = 1000, message = "O motivo deve ter entre 10 e 1000 caracteres")
    String motivo
) {}
