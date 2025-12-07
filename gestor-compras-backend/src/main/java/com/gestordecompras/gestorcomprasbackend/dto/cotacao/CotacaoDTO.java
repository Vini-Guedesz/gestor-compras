package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de retorno para Cotação
 *
 * Suporta dois formatos para compatibilidade durante migração:
 * - itensPedidoIds (legacy): apenas IDs dos itens
 * - itens (novo): itens com preços individuais
 */
public record CotacaoDTO(
        Long id,
        Integer fornecedorId,
        String tipoFornecedor,
        String nomeFornecedor,
        Long solicitacaoDePedidoId,

        /**
         * LEGACY: Use 'itens' para obter detalhes completos com preços.
         * Mantido para compatibilidade.
         */
        List<Long> itensPedidoIds,

        /**
         * Itens da cotação com preços individuais (formato novo - recomendado)
         */
        List<CotacaoItemDTO> itens,

        /**
         * Preço total calculado a partir dos itens
         */
        BigDecimal preco,

        Integer prazoEmDiasUteis,
        LocalDate dataLimite,

        /**
         * DEPRECATED: caminhoAnexo está deprecated.
         * Use AnexoCotacao com deduplificação SHA-256.
         * Sempre retorna null.
         */
        @Deprecated
        String caminhoAnexo,

        boolean temAnexoPdf,
        int quantidadeAnexos,

        /**
         * Campos de auditoria para controle de edições
         */
        Boolean foiEditada,
        Integer numeroVersao,
        LocalDateTime dataUltimaEdicao,
        String motivoUltimaEdicao,
        String editadoPor
) {
}
