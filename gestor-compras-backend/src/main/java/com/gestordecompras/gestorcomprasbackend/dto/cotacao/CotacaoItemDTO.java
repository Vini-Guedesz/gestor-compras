package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import java.math.BigDecimal;

/**
 * DTO para retorno de itens de cotação com preços individuais
 */
public record CotacaoItemDTO(
        Long id,
        Long cotacaoId,
        Long itemPedidoId,
        String nomeItem,
        BigDecimal precoUnitario,
        Integer quantidade,
        BigDecimal precoTotal,
        String observacao
) {
}
