package com.gestordecompras.gestorcomprasbackend.model.pedido;

import com.gestordecompras.gestorcomprasbackend.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitacao_de_pedido_id", nullable = false)
    private SolicitacaoDePedido solicitacaoDePedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    @Column(name = "data_modificacao", nullable = false)
    private LocalDateTime dataModificacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_modificacao", nullable = false)
    private TipoModificacao tipoModificacao;

    @Column(name = "campo_modificado")
    private String campoModificado;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "valor_novo", columnDefinition = "TEXT")
    private String valorNovo;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @PrePersist
    public void prePersist() {
        this.dataModificacao = LocalDateTime.now();
    }

    public enum TipoModificacao {
        CRIACAO,
        ATUALIZACAO,
        MUDANCA_STATUS,
        ADICAO_ITEM,
        REMOCAO_ITEM,
        ATUALIZACAO_ITEM,
        ADICAO_COTACAO,
        REMOCAO_COTACAO,
        CANCELAMENTO,
        APROVACAO
    }
}
