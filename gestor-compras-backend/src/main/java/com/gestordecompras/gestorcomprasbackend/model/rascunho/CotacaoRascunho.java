package com.gestordecompras.gestorcomprasbackend.model.rascunho;

import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeServico;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "cotacao_rascunho")
@Getter
@Setter
@NoArgsConstructor
public class CotacaoRascunho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rascunho_id", nullable = false)
    private Rascunho rascunho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_produto_id")
    private FornecedorDeProduto fornecedorProduto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_servico_id")
    private FornecedorDeServico fornecedorServico;

    @ManyToMany
    @JoinTable(
        name = "cotacao_rascunho_item",
        joinColumns = @JoinColumn(name = "cotacao_rascunho_id"),
        inverseJoinColumns = @JoinColumn(name = "item_rascunho_id")
    )
    private Set<ItemRascunho> itensRascunho = new HashSet<>();

    @Column(nullable = false)
    private BigDecimal preco;

    private Integer prazoEmDiasUteis;

    private LocalDate dataLimite;

    @Column(columnDefinition = "bytea")
    private byte[] anexoPdf;

    private String caminhoAnexo;

    @OneToMany(mappedBy = "cotacaoRascunho", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<AnexoCotacaoRascunho> anexos = new ArrayList<>();

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        validarFornecedor();
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        validarFornecedor();
    }

    private void validarFornecedor() {
        boolean temProduto = fornecedorProduto != null;
        boolean temServico = fornecedorServico != null;

        if (!temProduto && !temServico) {
            throw new IllegalStateException("Cotação deve ter um fornecedor (produto ou serviço)");
        }
        if (temProduto && temServico) {
            throw new IllegalStateException("Cotação não pode ter ambos os tipos de fornecedor simultaneamente");
        }
    }

    public Integer getFornecedorId() {
        if (fornecedorProduto != null) {
            return fornecedorProduto.getId();
        } else if (fornecedorServico != null) {
            return fornecedorServico.getId();
        }
        return null;
    }

    public String getTipoFornecedor() {
        if (fornecedorProduto != null) {
            return "PRODUTO";
        } else if (fornecedorServico != null) {
            return "SERVICO";
        }
        return null;
    }
}
