package com.gestordecompras.gestorcomprasbackend.dto.itempedido;

public record ItemPedidoDTO(
    Long id,
    String nome,
    int quantidade,
    String descricao,
    String observacao
) {}