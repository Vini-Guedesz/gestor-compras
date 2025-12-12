package com.gestordecompras.gestorcomprasbackend.model.cotacao;

import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidade intermediária que representa um item específico dentro de uma cotação.
 * <p>
 * Resolve o problema de relacionamento N:N com atributos extras (Bug #5), permitindo
 * armazenar o preço unitário e a quantidade cotada especificamente para cada item
 * em cada cotação.
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
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

    /**
     * Cotação à qual este item pertence.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotacao_id", nullable = false)
    private Cotacao cotacao;

    /**
     * Item do pedido original que está sendo cotado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_pedido_id", nullable = false)
    private ItemPedido itemPedido;

    /**
     * Preço unitário do item ofertado nesta cotação.
     */
    @Column(name = "preco_unitario", nullable = false, precision = 19, scale = 2)
    private BigDecimal precoUnitario;

    /**
     * Quantidade cotada pelo fornecedor.
     * <p>
     * Pode diferir da quantidade solicitada se o fornecedor tiver restrições de estoque
     * ou venda mínima.
     * </p>
     */
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    /**
     * Observações específicas sobre este item nesta cotação (ex: marca, modelo, validade).
     */
    @Column(name = "observacao", length = 1000)
    private String observacao;

    /**
     * Calcula o subtotal para este item (preço unitário * quantidade).
     *
     * @return Valor total do item nesta cotação.
     */
    public BigDecimal calcularPrecoTotal() {
        if (precoUnitario == null || quantidade == null) {
            return BigDecimal.ZERO;
        }
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    /**
     * Método auxiliar para configurar o relacionamento bidirecional com segurança.
     *
     * @param cotacao A cotação pai.
     */
    public void setCotacao(Cotacao cotacao) {
        this.cotacao = cotacao;
        if (cotacao != null && !cotacao.getItens().contains(this)) {
            cotacao.getItens().add(this);
        }
    }
}
