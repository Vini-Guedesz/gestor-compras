package com.gestordecompras.gestorcomprasbackend.dto.cotacao;

import com.gestordecompras.gestorcomprasbackend.model.cotacao.StatusCotacao;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que representa os dados completos de uma cotação.
 * <p>
 * Utilizado para retornar informações de cotações nas APIs de consulta.
 * Suporta dois formatos de itens para compatibilidade durante migração:
 * <ul>
 *   <li><strong>itensPedidoIds (legacy)</strong>: Lista simples de IDs.</li>
 *   <li><strong>itens (novo)</strong>: Lista detalhada com preços e quantidades.</li>
 * </ul>
 */
@Schema(description = "DTO de resposta com detalhes da cotação")
public record CotacaoDTO(
        @Schema(description = "ID único da cotação", example = "10")
        Long id,

        @Schema(description = "ID do fornecedor vinculado", example = "5")
        Integer fornecedorId,

        @Schema(description = "Tipo do fornecedor (PRODUTO ou SERVICO)", example = "PRODUTO", allowableValues = {"PRODUTO", "SERVICO"})
        String tipoFornecedor,

        @Schema(description = "Nome/Razão Social do fornecedor", example = "Papelaria Central Ltda")
        String nomeFornecedor,

        @Schema(description = "ID da solicitação de pedido vinculada", example = "100")
        Long solicitacaoDePedidoId,

        /**
         * LEGACY: Use 'itens' para obter detalhes completos com preços.
         * Mantido para compatibilidade.
         */
        @Schema(description = "Lista de IDs dos itens (Legacy - Use 'itens')", deprecated = true)
        List<Long> itensPedidoIds,

        /**
         * Itens da cotação com preços individuais (formato novo - recomendado)
         */
        @Schema(description = "Lista detalhada de itens com preços unitários e quantidades")
        List<CotacaoItemDTO> itens,

        /**
         * Preço total calculado a partir dos itens
         */
        @Schema(description = "Valor total da cotação", example = "1500.00")
        BigDecimal preco,

        @Schema(description = "Prazo de entrega em dias úteis", example = "5")
        Integer prazoEmDiasUteis,

        @Schema(description = "Data de validade da cotação", example = "2025-12-31")
        LocalDate dataLimite,

        /**
         * DEPRECATED: caminhoAnexo está deprecated.
         * Use AnexoCotacao com deduplificação SHA-256.
         * Sempre retorna null.
         */
        @Schema(description = "Caminho do anexo (Deprecated)", deprecated = true)
        String caminhoAnexo,

        @Schema(description = "Indica se possui anexos PDF", example = "true")
        boolean temAnexoPdf,

        @Schema(description = "Quantidade de anexos vinculados", example = "2")
        int quantidadeAnexos,

        /**
         * Campos de auditoria para controle de edições
         */
        @Schema(description = "Indica se a cotação foi editada após criação", example = "true")
        Boolean foiEditada,

        @Schema(description = "Número da versão da cotação", example = "2")
        Integer numeroVersao,

        @Schema(description = "Data e hora da última edição", example = "2025-10-10T14:30:00")
        LocalDateTime dataUltimaEdicao,

        @Schema(description = "Motivo da última alteração", example = "Ajuste de preço")
        String motivoUltimaEdicao,

        @Schema(description = "Nome do usuário que realizou a última edição", example = "Admin")
        String editadoPor,

        @Schema(description = "Indica se o gasto estava previsto no orçamento", example = "true")
        Boolean gastoPrevisto,

        @Schema(description = "Nome do projeto ao qual o gasto pertence", example = "Projeto Expansão 2025")
        String projeto,

        @Schema(description = "Data de criação da cotação", example = "2025-01-01T10:00:00")
        LocalDateTime dataCriacao,

        @Schema(description = "Status da cotação (EM_ANALISE, APROVADA, REJEITADA, CANCELADA)", example = "APROVADA")
        StatusCotacao status
) {
}
