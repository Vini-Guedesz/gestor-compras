package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import com.gestordecompras.gestorcomprasbackend.validation.PdfSize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para edição de cotação existente com auditoria.
 * Requer motivo obrigatório para rastreamento de negociações.
 */
public record CotacaoEditDTO(
        @NotNull(message = "ID da cotação é obrigatório")
        Long id,

        /**
         * Motivo da edição (obrigatório para auditoria)
         * Ex: "Negociação com fornecedor - desconto de 10%"
         *     "Correção de erro no prazo informado"
         *     "Atualização após renegociação"
         */
        @NotBlank(message = "Motivo da edição é obrigatório para auditoria")
        @Size(min = 10, max = 500, message = "Motivo deve ter entre 10 e 500 caracteres")
        String motivoEdicao,

        /**
         * Nome do usuário que está editando (para auditoria)
         */
        @NotBlank(message = "Nome do editor é obrigatório")
        @Size(max = 100, message = "Nome do editor deve ter no máximo 100 caracteres")
        String editadoPor,

        /**
         * Novos itens da cotação (pode adicionar/remover/modificar itens)
         */
        @Valid
        List<CotacaoItemCreateDTO> itens,

        /**
         * Novo preço total (opcional - usado quando não se edita itens mas apenas o valor total)
         */
        @Positive(message = "O preço deve ser maior que zero")
        BigDecimal precoNovo,

        /**
         * Novo prazo em dias úteis (opcional - só edita se fornecido)
         */
        @Min(value = 1, message = "O prazo deve ser de pelo menos 1 dia útil")
        Integer prazoEmDiasUteis,

        /**
         * Nova data limite (opcional - só edita se fornecido)
         */
        @FutureOrPresent(message = "A data limite não pode ser no passado")
        LocalDate dataLimite,

        /**
         * DEPRECATED: Novo anexo PDF (opcional - só atualiza se fornecido)
         * ATENÇÃO: Este campo está deprecated e será removido em versão futura.
         * Use o endpoint POST /api/cotacoes/{id}/anexos com multipart/form-data
         * que implementa deduplificação via PdfDeduplicationService.
         * Por enquanto mantido para compatibilidade com código existente.
         */
        @Deprecated
        @PdfSize(maxBytes = 10485760L, message = "PDF deve ter no máximo 10MB")
        byte[] anexoPdf
) {
    /**
     * Calcula o preço total a partir dos itens
     */
    public BigDecimal calcularPrecoTotal() {
        if (itens == null || itens.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return itens.stream()
                .map(item -> item.precoUnitario().multiply(BigDecimal.valueOf(item.quantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Verifica se há mudança real nos dados
     * NOTA: anexoPdf foi removido da verificação pois deve ser gerenciado via endpoint separado
     */
    public boolean temMudancas() {
        return (itens != null && !itens.isEmpty()) ||
               precoNovo != null ||
               prazoEmDiasUteis != null ||
               dataLimite != null;
    }
}
