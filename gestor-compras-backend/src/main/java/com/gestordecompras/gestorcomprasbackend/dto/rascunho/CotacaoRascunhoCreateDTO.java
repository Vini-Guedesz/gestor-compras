package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import com.gestordecompras.gestorcomprasbackend.validation.PdfSize;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
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

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "O preço deve ter no máximo 10 dígitos inteiros e 2 decimais")
    BigDecimal preco,

    @Min(value = 1, message = "O prazo deve ser de pelo menos 1 dia útil")
    Integer prazoEmDiasUteis,

    @FutureOrPresent(message = "A data limite não pode ser no passado")
    LocalDate dataLimite,

    @PdfSize(maxBytes = 10485760L, message = "PDF deve ter no máximo 10MB")
    byte[] anexoPdf,

    // Nota: Validação de tamanho para anexosPdf será feita no service
    List<byte[]> anexosPdf
) {}
