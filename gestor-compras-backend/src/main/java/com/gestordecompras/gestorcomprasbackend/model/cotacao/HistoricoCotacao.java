package com.gestordecompras.gestorcomprasbackend.model.cotacao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que armazena o histórico de edições de uma cotação.
 * Cada vez que uma cotação é editada, uma cópia da versão anterior é salva aqui.
 * Isso permite auditoria completa e rastreamento de negociações.
 */
@Entity
@Table(name = "historico_cotacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoCotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID da cotação original que foi editada
     */
    @Column(name = "cotacao_id", nullable = false)
    private Long cotacaoId;

    /**
     * Versão da cotação (1, 2, 3, etc.)
     */
    @Column(nullable = false)
    private Integer versao;

    /**
     * Dados da versão anterior
     */
    @Column(precision = 12, scale = 2)
    private BigDecimal precoAnterior;

    @Column(name = "prazo_anterior")
    private Integer prazoEmDiasUteisAnterior;

    @Column(name = "data_limite_anterior")
    private LocalDate dataLimiteAnterior;

    /**
     * Dados da nova versão
     */
    @Column(precision = 12, scale = 2)
    private BigDecimal precoNovo;

    @Column(name = "prazo_novo")
    private Integer prazoEmDiasUteisNovo;

    @Column(name = "data_limite_novo")
    private LocalDate dataLimiteNovo;

    /**
     * Informações de auditoria
     */
    @Column(name = "motivo_edicao", nullable = false, length = 500)
    private String motivoEdicao;

    @Column(name = "editado_por", length = 100)
    private String editadoPor;

    @Column(name = "data_edicao", nullable = false)
    private LocalDateTime dataEdicao;

    /**
     * Hash SHA-256 do PDF anterior (para auditoria sem duplicação)
     * Armazena apenas 64 bytes ao invés de MB completos do PDF
     * Permite verificar qual PDF estava anexado comparando com anexo_cotacao.hash_sha256
     */
    @Column(name = "hash_anexo_pdf_anterior", length = 64)
    private String hashAnexoPdfAnterior;

    /**
     * Hash SHA-256 do PDF novo (para auditoria sem duplicação)
     * Armazena apenas 64 bytes ao invés de MB completos do PDF
     * Permite verificar qual PDF foi anexado comparando com anexo_cotacao.hash_sha256
     */
    @Column(name = "hash_anexo_pdf_novo", length = 64)
    private String hashAnexoPdfNovo;

    // Getters explícitos para garantir compatibilidade (Lombok pode não gerar para campos novos)
    public String getHashAnexoPdfAnterior() {
        return hashAnexoPdfAnterior;
    }

    public void setHashAnexoPdfAnterior(String hashAnexoPdfAnterior) {
        this.hashAnexoPdfAnterior = hashAnexoPdfAnterior;
    }

    public String getHashAnexoPdfNovo() {
        return hashAnexoPdfNovo;
    }

    public void setHashAnexoPdfNovo(String hashAnexoPdfNovo) {
        this.hashAnexoPdfNovo = hashAnexoPdfNovo;
    }

    @PrePersist
    protected void onCreate() {
        if (dataEdicao == null) {
            dataEdicao = LocalDateTime.now();
        }
    }
}
