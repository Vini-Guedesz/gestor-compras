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

/**
 * Entidade que representa um rascunho de pedido.
 * <p>
 * Permite que o usuário monte uma lista de itens e realize cotações preliminares
 * antes de oficializar a solicitação de compra. Funciona como uma área de trabalho temporária.
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
 */
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

    /**
     * Versão para controle de concorrência (Optimistic Locking).
     */
    @Version
    private Long version;

    /**
     * Lista de itens contidos no rascunho.
     */
    @OneToMany(mappedBy = "rascunho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemRascunho> itens = new ArrayList<>();

    /**
     * Usuário que criou o rascunho.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User criador;

    /**
     * Observações ou anotações pessoais sobre o rascunho.
     */
    @Column(name = "observacao")
    private String observacao;

    /**
     * Data de criação do rascunho.
     */
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    /**
     * Data da última modificação.
     */
    @Column(name = "data_modificacao")
    private LocalDateTime dataModificacao;

    /**
     * Status atual do rascunho (ATIVO, EM_COTACAO, FINALIZADO).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusRascunho status = StatusRascunho.ATIVO;

    /**
     * ID do pedido gerado a partir deste rascunho (se convertido).
     */
    @Column(name = "pedido_gerado_id")
    private Long pedidoGeradoId;

    /**
     * Contador para gerar números sequenciais para os itens (ex: Item 1, Item 2).
     */
    @Column(name = "proximo_numero_item", nullable = false)
    private Integer proximoNumeroItem = 1;

    /**
     * Lista de números de itens que foram removidos e podem ser reutilizados.
     */
    @OneToMany(mappedBy = "rascunho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NumeroItemDisponivel> numerosDisponiveis = new ArrayList<>();

    /**
     * Callback executado antes de criar o registro.
     */
    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        this.dataModificacao = LocalDateTime.now();
        if (this.proximoNumeroItem == null) {
            this.proximoNumeroItem = 1;
        }
    }

    /**
     * Callback executado antes de atualizar o registro.
     */
    @PreUpdate
    public void preUpdate() {
        this.dataModificacao = LocalDateTime.now();
    }

    /**
     * Adiciona um item ao rascunho e configura o relacionamento bidirecional.
     * @param item Item a ser adicionado.
     */
    public void addItem(ItemRascunho item) {
        itens.add(item);
        item.setRascunho(this);
    }

    /**
     * Remove um item do rascunho.
     * @param item Item a ser removido.
     */
    public void removeItem(ItemRascunho item) {
        itens.remove(item);
        item.setRascunho(null);
    }
}
