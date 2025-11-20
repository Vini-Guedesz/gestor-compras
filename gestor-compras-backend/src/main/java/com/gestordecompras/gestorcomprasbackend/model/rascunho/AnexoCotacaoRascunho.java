package com.gestordecompras.gestorcomprasbackend.model.rascunho;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "anexo_cotacao_rascunho")
@Getter
@Setter
@NoArgsConstructor
public class AnexoCotacaoRascunho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotacao_rascunho_id", nullable = false)
    private CotacaoRascunho cotacaoRascunho;

    @Column(name = "ordem")
    private Integer ordem;

    @Column(columnDefinition = "bytea", nullable = false)
    private byte[] conteudo;

    @Column(name = "nome_arquivo")
    private String nomeArquivo;

    public AnexoCotacaoRascunho(CotacaoRascunho cotacaoRascunho, byte[] conteudo, Integer ordem) {
        this.cotacaoRascunho = cotacaoRascunho;
        this.conteudo = conteudo;
        this.ordem = ordem;
    }
}
