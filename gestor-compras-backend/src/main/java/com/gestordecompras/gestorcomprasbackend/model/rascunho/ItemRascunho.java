package com.gestordecompras.gestorcomprasbackend.model.rascunho;

import com.gestordecompras.gestorcomprasbackend.model.pedido.TipoItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_rascunho")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRascunho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_item", nullable = false)
    private Integer numeroItem;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "observacao")
    private String observacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoItem tipo = TipoItem.PRODUTO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rascunho_id", nullable = false)
    private Rascunho rascunho;
}
