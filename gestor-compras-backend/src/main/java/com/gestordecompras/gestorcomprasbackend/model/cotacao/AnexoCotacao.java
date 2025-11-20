package com.gestordecompras.gestorcomprasbackend.model.cotacao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "anexo_cotacao")
@Getter
@Setter
@NoArgsConstructor
public class AnexoCotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotacao_id", nullable = false)
    private Cotacao cotacao;

    @Column(name = "ordem")
    private Integer ordem;

    @Column(columnDefinition = "bytea", nullable = false)
    private byte[] conteudo;

    @Column(name = "nome_arquivo")
    private String nomeArquivo;

    public AnexoCotacao(Cotacao cotacao, byte[] conteudo, Integer ordem) {
        this.cotacao = cotacao;
        this.conteudo = conteudo;
        this.ordem = ordem;
    }
}
