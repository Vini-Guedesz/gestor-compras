package com.gestordecompras.gestorcomprasbackend.model.rascunho;

import com.gestordecompras.gestorcomprasbackend.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_rascunho")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoRascunho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rascunho_id", nullable = false)
    private Rascunho rascunho;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User usuario;

    @Column(name = "data_modificacao", nullable = false)
    private LocalDateTime dataModificacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acao", nullable = false)
    private TipoAcaoRascunho tipoAcao;

    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "numero_item")
    private Integer numeroItem;

    @Column(name = "nome_item")
    private String nomeItem;

    @Column(name = "detalhes", columnDefinition = "TEXT")
    private String detalhes;

    @PrePersist
    public void prePersist() {
        this.dataModificacao = LocalDateTime.now();
    }

    public enum TipoAcaoRascunho {
        CRIACAO_RASCUNHO,
        ADICAO_ITEM,
        ATUALIZACAO_ITEM,
        REMOCAO_ITEM,
        ATUALIZACAO_OBSERVACAO,
        ADICAO_COTACAO,
        REMOCAO_COTACAO,
        CONVERSAO_PEDIDO
    }
}
