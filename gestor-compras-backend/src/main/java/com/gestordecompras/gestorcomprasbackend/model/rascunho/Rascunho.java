package com.gestordecompras.gestorcomprasbackend.model.rascunho;

import com.gestordecompras.gestorcomprasbackend.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rascunho")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rascunho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "rascunho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemRascunho> itens = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User criador;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_modificacao")
    private LocalDateTime dataModificacao;

    @Column(name = "proximo_numero_item", nullable = false)
    private Integer proximoNumeroItem = 1;

    @OneToMany(mappedBy = "rascunho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NumeroItemDisponivel> numerosDisponiveis = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        this.dataModificacao = LocalDateTime.now();
        if (this.proximoNumeroItem == null) {
            this.proximoNumeroItem = 1;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.dataModificacao = LocalDateTime.now();
    }

    public void addItem(ItemRascunho item) {
        itens.add(item);
        item.setRascunho(this);
    }

    public void removeItem(ItemRascunho item) {
        itens.remove(item);
        item.setRascunho(null);
    }
}
