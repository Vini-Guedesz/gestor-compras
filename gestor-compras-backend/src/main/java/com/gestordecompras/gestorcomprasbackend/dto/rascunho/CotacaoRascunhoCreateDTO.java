package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CotacaoRascunhoCreateDTO(
    @NotNull(message = "ID do fornecedor é obrigatório")
    Integer fornecedorId,

    @NotNull(message = "Tipo do fornecedor é obrigatório")
    String tipoFornecedor,

    @NotNull(message = "IDs dos itens do rascunho são obrigatórios")
    @NotEmpty(message = "Deve selecionar pelo menos um item do rascunho")
    List<Long> itensRascunhoIds,

    @NotNull(message = "Preço é obrigatório")
    BigDecimal preco,

    Integer prazoEmDiasUteis,
    LocalDate dataLimite,
    byte[] anexoPdf
) {}
