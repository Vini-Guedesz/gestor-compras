package com.gestordecompras.gestorcomprasbackend.model.pedido;

import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import com.gestordecompras.gestorcomprasbackend.model.pedido.StatusPedido;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Entidade que representa uma solicitação formal de compra.
 * <p>
 * Agrupa os itens que precisam ser adquiridos e gerencia o ciclo de vida do processo
 * de compra, desde a criação até a aprovação ou cancelamento.
 * </p>
 * <p>
 * Pode ser originada a partir da conversão de um {@link com.gestordecompras.gestorcomprasbackend.model.rascunho.Rascunho}.
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
 */
@Data
@Entity
@Table(name = "solicitacao_de_pedido")
public class SolicitacaoDePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Versão para controle de concorrência (Optimistic Locking).
     */
    @Version
    private Long version;

    /**
     * Lista de itens solicitados neste pedido.
     * <p>
     * O relacionamento possui CascadeType.ALL, significando que alterações na lista
     * são propagadas para o banco de dados.
     * </p>
     * <p>
     * BatchSize otimiza o lazy loading: ao carregar itens de uma solicitação,
     * Hibernate carrega também itens de até 25 outras solicitações em memória,
     * reduzindo o problema N+1.
     * </p>
     */
    @OneToMany(mappedBy = "solicitacaoDePedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 25)
    private List<ItemPedido> itens;

    /**
     * Conjunto de cotações recebidas para esta solicitação.
     * <p>
     * BatchSize otimiza o lazy loading de cotações quando múltiplas solicitações
     * são carregadas simultaneamente.
     * </p>
     */
    @OneToMany(mappedBy = "solicitacaoDePedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 25)
    private Set<Cotacao> cotacoes;

    /**
     * Status atual do processo de compra (ex: PENDENTE, APROVADO, CANCELADO).
     */
    @Enumerated(EnumType.STRING)
    @Column
    private StatusPedido status;

    /**
     * Observações gerais sobre a solicitação.
     */
    private String observacao;

    /**
     * Objetivo ou finalidade do pedido.
     * <p>
     * Copiado do rascunho quando o pedido é criado a partir dele.
     * Exemplo: "Compra de materiais para reforma do escritório"
     * </p>
     */
    @Column(name = "objetivo", columnDefinition = "TEXT")
    private String objetivo;

    /**
     * Data e hora em que a solicitação foi criada.
     */
    private LocalDateTime dataCriacao;

    /**
     * Método de callback executado antes da persistência.
     * Define a data de criação automaticamente.
     */
    @PrePersist
    public void prePersist(){
        this.dataCriacao=LocalDateTime.now();
    }
}
