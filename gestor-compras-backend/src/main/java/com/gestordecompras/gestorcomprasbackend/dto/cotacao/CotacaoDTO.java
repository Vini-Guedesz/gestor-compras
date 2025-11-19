package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CotacaoDTO(
        Long id,
        Integer fornecedorId,
        String tipoFornecedor,
        Long solicitacaoDePedidoId,
        List<Long> itensPedidoIds,
        BigDecimal preco,
        Integer prazoEmDiasUteis,
        LocalDate dataLimite,
        String caminhoAnexo,
        byte[] anexoPdf
) {
}
