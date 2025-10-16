package com.gestordecompras.gestorcomprasbackend.dto.relatorio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComparativoCotacaoDTO {
    private String cotacaoId;
    private Long itemPedidoId;
    private String nomeItem;
    private String descricaoItem;
    private String fornecedor;
    private String cnpjFornecedor;
    private BigDecimal preco;
    private LocalDate prazoEntrega;
    private LocalDate dataCotacao;
    private String tipoFornecedor; // "PRODUTO" ou "SERVICO"
}
