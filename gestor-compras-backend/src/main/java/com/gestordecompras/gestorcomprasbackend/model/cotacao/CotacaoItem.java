package com.gestordecompras.gestorcomprasbackend.model.cotacao;

import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidade intermediária entre Cotacao e ItemPedido
 *
 * Resolve o Bug #5: Permite armazenar informações específicas do relacionamento
 * cotação-item, como preço unitário e quantidade cotada.
 *
 * Esta entidade substitui o relacionamento N:N direto anterior que não permitia
 * armazenar preços individuais por item.
 */
@Entity
@Table(name = "cotacao_item",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_cotacao_item",
        columnNames = {"cotacao_id", "item_pedido_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CotacaoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotacao_id", nullable = false)
    private Cotacao cotacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_pedido_id", nullable = false)
    private ItemPedido itemPedido;

    /**
     * Preço unitário do item nesta cotação específica
     */
    @Column(name = "preco_unitario", nullable = false, precision = 19, scale = 2)
    private BigDecimal precoUnitario;

    /**
     * Quantidade cotada (pode diferir da quantidade solicitada)
     */
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    /**
     * Observações específicas sobre este item nesta cotação
     */
    @Column(name = "observacao", length = 1000)
    private String observacao;

    /**
     * Calcula o preço total deste item (preço unitário * quantidade)
     */
    public BigDecimal calcularPrecoTotal() {
        if (precoUnitario == null || quantidade == null) {
            return BigDecimal.ZERO;
        }
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    /**
     * Método helper para configurar relacionamento bidirecional
     */
    public void setCotacao(Cotacao cotacao) {
        this.cotacao = cotacao;
        if (cotacao != null && !cotacao.getItens().contains(this)) {
            cotacao.getItens().add(this);
        }
    }
}
