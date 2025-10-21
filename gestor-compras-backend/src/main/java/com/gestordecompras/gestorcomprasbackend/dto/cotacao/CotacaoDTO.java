package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CotacaoDTO(
        Long id,
        Integer fornecedorId,
        String tipoFornecedor,
        Long itemPedidoId,
        BigDecimal preco,
        LocalDate prazoEntrega,
        LocalDate dataCotacao,
        String caminhoAnexo
) {
}
