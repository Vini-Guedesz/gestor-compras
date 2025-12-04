package com.gestordecompras.gestorcomprasbackend.model.pedido;

import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import com.gestordecompras.gestorcomprasbackend.model.pedido.StatusPedido;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "solicitacao_de_pedido")
public class SolicitacaoDePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @OneToMany(mappedBy = "solicitacaoDePedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens;

    @OneToMany(mappedBy = "solicitacaoDePedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Cotacao> cotacoes;

    @Enumerated(EnumType.STRING)
    @Column
    private StatusPedido status;

    private String observacao;

    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist(){
        this.dataCriacao=LocalDateTime.now();
    }
}
