package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import com.gestordecompras.gestorcomprasbackend.validation.PdfSize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para criação de cotação
 *
 * Suporta dois formatos para compatibilidade:
 * 1. LEGACY (será removido): itensPedidoIds + preco total
 * 2. NOVO (recomendado): itens com preços individuais
 *
 * Se 'itens' for fornecido, usa o novo formato e ignora itensPedidoIds e preco.
 */
public record CotacaoCreateDTO(
        @NotNull(message = "ID do fornecedor é obrigatório")
        Integer fornecedorId,

        @NotNull(message = "Tipo do fornecedor é obrigatório")
        String tipoFornecedor,

        @NotNull(message = "ID da solicitação de pedido é obrigatório")
        Long solicitacaoDePedidoId,

        // ========== FORMATO LEGACY (será removido em versão futura) ==========
        /**
         * LEGACY: Use 'itens' ao invés de itensPedidoIds.
         * Mantido para compatibilidade com frontend antigo.
         */
        List<Long> itensPedidoIds,

        /**
         * LEGACY: Use 'itens' com preços individuais ao invés de preço total.
         * Mantido para compatibilidade com frontend antigo.
         */
        @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "O preço deve ter no máximo 10 dígitos inteiros e 2 decimais")
        BigDecimal preco,

        // ========== FORMATO NOVO (recomendado) ==========
        /**
         * Itens da cotação com preços individuais (novo formato).
         * Se fornecido, sobrescreve itensPedidoIds e preco.
         */
        @Valid
        List<CotacaoItemCreateDTO> itens,

        // ========== CAMPOS COMUNS ==========
        @Min(value = 1, message = "O prazo deve ser de pelo menos 1 dia útil")
        Integer prazoEmDiasUteis,

        @FutureOrPresent(message = "A data limite não pode ser no passado")
        LocalDate dataLimite,

        /**
         * DEPRECATED: anexoPdf está deprecated.
         * PDFs devem ser adicionados via endpoint POST /api/cotacoes/{id}/anexos
         * após criar a cotação, para suportar múltiplos anexos e deduplificação SHA-256.
         * Mantido para compatibilidade com código existente.
         */
        @Deprecated
        @PdfSize(maxBytes = 10485760L, message = "PDF deve ter no máximo 10MB")
        byte[] anexoPdf
) {
    /**
     * Verifica se está usando o novo formato com itens individuais
     */
    public boolean usaNovoFormato() {
        return itens != null && !itens.isEmpty();
    }

    /**
     * Valida que pelo menos um formato foi fornecido
     */
    public void validar() {
        boolean temFormatoNovo = itens != null && !itens.isEmpty();
        boolean temFormatoLegacy = itensPedidoIds != null && !itensPedidoIds.isEmpty();

        if (!temFormatoNovo && !temFormatoLegacy) {
            throw new IllegalArgumentException(
                "Deve fornecer 'itens' (novo formato) ou 'itensPedidoIds' (legacy)"
            );
        }
    }
}
