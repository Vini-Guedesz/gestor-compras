package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO que representa um item individual dentro de um rascunho.
 * <p>
 * Contém detalhes como nome, quantidade e descrição do produto ou serviço desejado.
 * </p>
 */
@Schema(description = "DTO para itens de rascunho de pedido")
public record ItemRascunhoDTO(
    @Schema(description = "ID único do item de rascunho", example = "10")
    Long id,

    @Schema(description = "Número sequencial do item dentro do rascunho (exibição)", example = "1")
    Integer numeroItem,

    @Schema(description = "Nome do produto ou serviço", example = "Caneta Esferográfica Azul")
    String nome,

    @Schema(description = "Quantidade desejada", example = "100")
    Integer quantidade,

    @Schema(description = "Descrição detalhada ou especificações técnicas", example = "Modelo X, ponta fina 0.7mm")
    String descricao,

    @Schema(description = "Observações adicionais para o comprador", example = "Para uso no escritório administrativo")
    String observacao,

    @Schema(description = "Tipo do item (PRODUTO ou SERVICO)", example = "PRODUTO", allowableValues = {"PRODUTO", "SERVICO"})
    String tipo
) {}
