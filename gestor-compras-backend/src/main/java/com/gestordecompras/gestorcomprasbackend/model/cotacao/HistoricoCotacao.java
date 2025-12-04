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
     * PDF completo da versão anterior (para auditoria)
     * Armazena o documento original da cotação antes da edição
     */
    @Column(name = "anexo_pdf_anterior", columnDefinition = "bytea")
    private byte[] anexoPdfAnterior;

    /**
     * Caminho do anexo anterior (se houver)
     */
    @Column(name = "caminho_anexo_anterior")
    private String caminhoAnexoAnterior;

    /**
     * PDF completo da nova versão (para comparação)
     * Armazena o novo documento anexado após a edição
     */
    @Column(name = "anexo_pdf_novo", columnDefinition = "bytea")
    private byte[] anexoPdfNovo;

    /**
     * Caminho do novo anexo (se houver)
     */
    @Column(name = "caminho_anexo_novo")
    private String caminhoAnexoNovo;

    @PrePersist
    protected void onCreate() {
        if (dataEdicao == null) {
            dataEdicao = LocalDateTime.now();
        }
    }
}
