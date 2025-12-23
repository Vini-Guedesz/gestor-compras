package com.gestordecompras.gestorcomprasbackend.dto.pedido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO genérico para ações que requerem justificativa/observação.
 * Usado para: cancelamento, devolução para edição, aprovação com observações, etc.
 */
public record AcaoPedidoDTO(
    @Size(max = 1000, message = "A observação deve ter no máximo 1000 caracteres")
    String observacao
) {}
