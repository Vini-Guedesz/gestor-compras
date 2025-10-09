package com.gestordecompras.gestorcomprasbackend.model.cotacao;

import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeServico;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cotacao")
@Getter
@Setter
@NoArgsConstructor
public class Cotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_produto_id")
    private FornecedorDeProduto fornecedorProduto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_servico_id")
    private FornecedorDeServico fornecedorServico;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_pedido_id")
    private ItemPedido itemPedido;

    private BigDecimal preco;

    private LocalDate prazoEntrega;

    private LocalDate dataCotacao;

    @Column(columnDefinition = "bytea")
    private byte[] anexoPdf;

    private String caminhoAnexo;

    @PrePersist
    public void prePersist() {
        this.dataCotacao = LocalDate.now();
    }

    public Integer getFornecedorId() {
        if (fornecedorProduto != null) {
            return fornecedorProduto.getId();
        } else if (fornecedorServico != null) {
            return fornecedorServico.getId();
        }
        return null;
    }
}
