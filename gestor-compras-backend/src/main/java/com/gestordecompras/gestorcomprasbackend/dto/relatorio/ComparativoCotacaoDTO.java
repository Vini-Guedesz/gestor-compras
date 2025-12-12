package com.gestordecompras.gestorcomprasbackend.dto.relatorio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de resultado para o relatório de comparativo de cotações")
public class ComparativoCotacaoDTO {
    @Schema(description = "ID da cotação", example = "20")
    private String cotacaoId;

    @Schema(description = "ID do item de pedido associado", example = "101")
    private Long itemPedidoId;

    @Schema(description = "Nome do item cotado", example = "Caneta Esferográfica")
    private String nomeItem;

    @Schema(description = "Descrição do item cotado", example = "Cor azul, ponta fina")
    private String descricaoItem;

    @Schema(description = "Nome do fornecedor", example = "Papelaria Beta")
    private String fornecedor;

    @Schema(description = "CNPJ do fornecedor", example = "11.222.333/0001-44")
    private String cnpjFornecedor;

    @Schema(description = "Preço total ou unitário cotado", example = "5.50")
    private BigDecimal preco;

    @Schema(description = "Prazo de entrega em dias úteis", example = "3")
    private Integer prazoEmDiasUteis;

    @Schema(description = "Data limite de validade da cotação", example = "2025-12-20")
    private LocalDate dataLimite;

    @Schema(description = "Tipo do fornecedor (PRODUTO ou SERVICO)", example = "PRODUTO")
    private String tipoFornecedor;
}
