package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import com.gestordecompras.gestorcomprasbackend.validation.PdfSize;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CotacaoRascunhoCreateDTO(
    @NotNull(message = "ID do fornecedor é obrigatório")
    Integer fornecedorId,

    @NotNull(message = "Tipo do fornecedor é obrigatório")
    String tipoFornecedor,

    @NotNull(message = "Itens cotados são obrigatórios")
    @NotEmpty(message = "Deve informar pelo menos um item cotado")
    List<CotacaoRascunhoItemCreateDTO> itens,

    @Min(value = 1, message = "O prazo deve ser de pelo menos 1 dia útil")
    Integer prazoEmDiasUteis,

    @FutureOrPresent(message = "A data limite não pode ser no passado")
    LocalDate dataLimite,

    Boolean gastoPrevisto,

    String projeto,

    @PdfSize(maxBytes = 10485760L, message = "PDF deve ter no máximo 10MB")
    byte[] anexoPdf,

    // Nota: Validação de tamanho para anexosPdf será feita no service
    List<byte[]> anexosPdf
) {}
