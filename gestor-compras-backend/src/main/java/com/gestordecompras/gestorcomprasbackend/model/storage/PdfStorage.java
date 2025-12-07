package com.gestordecompras.gestorcomprasbackend.model.storage;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Armazenamento centralizado de PDFs com deduplicação
 *
 * Esta entidade implementa o padrão Content-Addressable Storage (CAS):
 * - PDFs são identificados pelo hash SHA-256 do conteúdo
 * - PDFs idênticos são armazenados apenas uma vez
 * - Múltiplos anexos podem referenciar o mesmo PdfStorage
 *
 * Benefícios:
 * - Economia de espaço em disco (30-78% em ambientes típicos)
 * - Economia de banda de rede (uploads duplicados são rejeitados)
 * - Integridade garantida (hash valida que conteúdo não foi corrompido)
 */
@Entity
@Table(name = "pdf_storage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdfStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Hash SHA-256 do conteúdo do PDF (64 caracteres hexadecimais)
     * Usado como chave de deduplicação - PDFs com mesmo hash são idênticos
     */
    @Column(name = "hash_sha256", length = 64, unique = true, nullable = false)
    private String hashSha256;

    /**
     * Conteúdo binário do PDF
     * Armazenado uma única vez, mesmo que múltiplos anexos referenciem
     */
    @Column(name = "conteudo", nullable = false)
    private byte[] conteudo;

    /**
     * Tamanho do PDF em bytes
     * Usado para estatísticas de economia de espaço
     */
    @Column(name = "tamanho_bytes", nullable = false)
    private Long tamanhoBytes;

    /**
     * Data de criação do registro (primeiro upload deste PDF)
     */
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    /**
     * Contador de referências (quantos anexos usam este PDF)
     * Útil para garbage collection no futuro
     */
    @Column(name = "contador_referencias", nullable = false)
    private Integer contadorReferencias = 0;

    /**
     * Construtor para criação de novo PDF
     */
    public PdfStorage(String hashSha256, byte[] conteudo) {
        this.hashSha256 = hashSha256;
        this.conteudo = conteudo;
        this.tamanhoBytes = conteudo != null ? (long) conteudo.length : 0L;
        this.dataCriacao = LocalDateTime.now();
        this.contadorReferencias = 0;
    }

    /**
     * Incrementa contador de referências quando anexo é criado
     */
    public void incrementarReferencias() {
        this.contadorReferencias++;
    }

    /**
     * Decrementa contador de referências quando anexo é deletado
     * Retorna true se pode ser deletado (sem mais referências)
     */
    public boolean decrementarReferencias() {
        if (this.contadorReferencias > 0) {
            this.contadorReferencias--;
        }
        return this.contadorReferencias == 0;
    }
}
