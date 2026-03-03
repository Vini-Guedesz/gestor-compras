package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import java.math.BigDecimal;

public record CotacaoRascunhoItemDTO(
    Long id,
    Long itemRascunhoId,
    Integer numeroItem,
    String nomeItem,
    Integer quantidade,
    BigDecimal precoUnitario,
    BigDecimal subtotal,
    String observacao
) {}
