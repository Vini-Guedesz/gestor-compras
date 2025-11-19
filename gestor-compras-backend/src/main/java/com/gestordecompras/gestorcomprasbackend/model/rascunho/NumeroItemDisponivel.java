package com.gestordecompras.gestorcomprasbackend.model.rascunho;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "numero_item_disponivel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NumeroItemDisponivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rascunho_id", nullable = false)
    private Rascunho rascunho;

    @Column(name = "numero_item", nullable = false)
    private Integer numeroItem;
}
