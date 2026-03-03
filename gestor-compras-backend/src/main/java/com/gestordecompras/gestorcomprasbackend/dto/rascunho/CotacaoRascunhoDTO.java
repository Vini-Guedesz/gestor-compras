package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CotacaoRascunhoDTO(
    Long id,
    Long rascunhoId,
    Integer fornecedorId,
    String tipoFornecedor,
    String nomeFornecedor,
    List<CotacaoRascunhoItemDTO> itens,
    List<Long> itensRascunhoIds,
    BigDecimal preco,
    Integer prazoEmDiasUteis,
    LocalDate dataLimite,
    Boolean gastoPrevisto,
    String projeto,
    boolean temAnexoPdf,
    int quantidadeAnexos,
    LocalDateTime dataCriacao
) {}
