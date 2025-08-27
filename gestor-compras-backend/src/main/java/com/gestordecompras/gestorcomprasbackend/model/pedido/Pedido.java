package com.gestordecompras.gestorcomprasbackend.model.pedido;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jdk.jshell.Snippet;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Column
    @NotBlank
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column
    private StatusPedido status;

    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist(){
        this.dataCriacao=LocalDateTime.now();

    }

}
