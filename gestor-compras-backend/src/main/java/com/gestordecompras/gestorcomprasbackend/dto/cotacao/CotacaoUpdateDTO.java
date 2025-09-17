package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CotacaoUpdateDTO(
        BigDecimal preco,
        LocalDate prazoEntrega,
        byte[] anexoPdf,
        String caminhoAnexo
) {
}
