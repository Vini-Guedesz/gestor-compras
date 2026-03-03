package com.gestordecompras.gestorcomprasbackend.model.rascunho;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidade intermediária que representa um item específico dentro de uma cotação de rascunho.
 * Permite armazenar preço unitário, quantidade e observações por item.
 */
@Entity
@Table(
    name = "cotacao_rascunho_item",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_cotacao_rascunho_item",
        columnNames = {"cotacao_rascunho_id", "item_rascunho_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CotacaoRascunhoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotacao_rascunho_id", nullable = false)
    private CotacaoRascunho cotacaoRascunho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_rascunho_id", nullable = false)
    private ItemRascunho itemRascunho;

    @Column(name = "preco_unitario", nullable = false, precision = 19, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "observacao", length = 1000)
    private String observacao;

    public BigDecimal calcularPrecoTotal() {
        if (precoUnitario == null || quantidade == null) {
            return BigDecimal.ZERO;
        }
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public void setCotacaoRascunho(CotacaoRascunho cotacaoRascunho) {
        this.cotacaoRascunho = cotacaoRascunho;
        if (cotacaoRascunho != null && !cotacaoRascunho.getItens().contains(this)) {
            cotacaoRascunho.getItens().add(this);
        }
    }
}
