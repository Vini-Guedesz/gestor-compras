package com.gestordecompras.gestorcomprasbackend.model.cotacao;

import com.gestordecompras.gestorcomprasbackend.model.storage.PdfStorage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "anexo_cotacao", indexes = {
    @Index(name = "idx_anexo_cotacao_hash", columnList = "hash_sha256"),
    @Index(name = "idx_anexo_cotacao_storage", columnList = "pdf_storage_id")
})
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

    /**
     * Referência ao storage centralizado de PDF (nova arquitetura)
     * Múltiplos anexos podem referenciar o mesmo PdfStorage (deduplicação)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdf_storage_id")
    private PdfStorage pdfStorage;

    /**
     * DEPRECATED: Conteúdo binário inline (arquitetura antiga)
     * Mantido temporariamente para compatibilidade durante migração
     * Novos anexos devem usar pdfStorage
     */
    @Deprecated
    @Transient
    private byte[] conteudoLegacy;

    @Column(name = "nome_arquivo")
    private String nomeArquivo;

    /**
     * Hash SHA-256 do conteúdo do PDF para deduplificação
     * Duplicado aqui para queries rápidas sem JOIN
     */
    @Column(name = "hash_sha256", length = 64)
    private String hashSha256;

    /**
     * Construtor compatível com código legado
     * DEPRECATED: Use construtor com PdfStorage
     */
    @Deprecated
    public AnexoCotacao(Cotacao cotacao, byte[] conteudo, Integer ordem) {
        this.cotacao = cotacao;
        this.conteudoLegacy = conteudo;
        this.ordem = ordem;
    }

    /**
     * Construtor com PdfStorage (nova arquitetura)
     */
    public AnexoCotacao(Cotacao cotacao, PdfStorage pdfStorage, Integer ordem, String nomeArquivo) {
        this.cotacao = cotacao;
        this.pdfStorage = pdfStorage;
        this.ordem = ordem;
        this.nomeArquivo = nomeArquivo;
        this.hashSha256 = pdfStorage != null ? pdfStorage.getHashSha256() : null;
    }

    /**
     * Obtém o conteúdo do PDF
     * Compatível com código legado - busca do PdfStorage ou conteúdo inline
     *
     * @return Bytes do PDF
     */
    public byte[] getConteudo() {
        // Nova arquitetura: busca do storage centralizado
        if (pdfStorage != null) {
            return pdfStorage.getConteudo();
        }
        // Fallback para dados legados inline
        return conteudoLegacy;
    }

    /**
     * Define o conteúdo do PDF (DEPRECATED)
     * Mantido para compatibilidade, mas novos códigos devem usar setPdfStorage
     */
    @Deprecated
    public void setConteudo(byte[] conteudo) {
        this.conteudoLegacy = conteudo;
    }

    /**
     * Verifica se este anexo usa a nova arquitetura de storage
     */
    @Transient
    public boolean usaNovaArquitetura() {
        return pdfStorage != null;
    }

    /**
     * Obtém o tamanho do PDF em bytes
     */
    @Transient
    public Long getTamanhoBytes() {
        if (pdfStorage != null) {
            return pdfStorage.getTamanhoBytes();
        }
        return conteudoLegacy != null ? (long) conteudoLegacy.length : 0L;
    }
}
