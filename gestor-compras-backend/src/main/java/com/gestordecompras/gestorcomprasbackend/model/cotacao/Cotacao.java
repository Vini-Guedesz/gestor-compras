package com.gestordecompras.gestorcomprasbackend.model.cotacao;

import com.gestordecompras.gestorcomprasbackend.model.fornecedor.Fornecedor;
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

    @Column(name = "fornecedor_id")
    private Integer fornecedorId;

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
}
