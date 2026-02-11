package com.gestordecompras.gestorcomprasbackend.dto.itempedido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para itens de pedido")
public record ItemPedidoDTO(
    @Schema(description = "ID do item", example = "50")
    Long id,

    @Schema(description = "Nome do produto ou serviço", example = "Notebook Dell Latitude")
    @NotBlank(message = "O nome do item é obrigatório")
    @Size(min = 3, max = 200, message = "O nome deve ter entre 3 e 200 caracteres")
    String nome,

    @Schema(description = "Quantidade solicitada", example = "2")
    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    @Max(value = 999999, message = "A quantidade deve ser no máximo 999999")
    int quantidade,

    @Schema(description = "Descrição detalhada do item", example = "Processador i7, 16GB RAM, SSD 512GB")
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    String descricao,

    @Schema(description = "Observações adicionais", example = "Entregar no departamento de TI")
    @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres")
    String observacao,

    @Schema(description = "Tipo do item (PRODUTO ou SERVICO)", example = "PRODUTO", allowableValues = {"PRODUTO", "SERVICO"})
    String tipo
) {}