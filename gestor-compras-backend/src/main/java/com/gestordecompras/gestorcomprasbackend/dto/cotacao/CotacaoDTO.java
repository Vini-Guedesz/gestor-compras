package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CotacaoDTO(
        Long id,
        Integer fornecedorId,
        String tipoFornecedor,
        Long itemPedidoId,
        BigDecimal preco,
        Integer prazoEmDiasUteis,
        LocalDate dataLimite,
        String caminhoAnexo,
        byte[] anexoPdf
) {
}
