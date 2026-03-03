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
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "cotacao_rascunho")
@Getter
@Setter
@NoArgsConstructor
public class CotacaoRascunho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rascunho_id", nullable = false)
    private Rascunho rascunho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_produto_id")
    private FornecedorDeProduto fornecedorProduto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_servico_id")
    private FornecedorDeServico fornecedorServico;

    @OneToMany(mappedBy = "cotacaoRascunho", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CotacaoRascunhoItem> itens = new HashSet<>();

    /**
     * Preço total legado.
     * @deprecated Use {@link #getPreco()} calculado a partir dos itens.
     */
    @Deprecated
    @Column(name = "preco")
    private BigDecimal precoLegacy;

    private Integer prazoEmDiasUteis;

    private LocalDate dataLimite;

    @Column(name = "gasto_previsto")
    private Boolean gastoPrevisto = false;

    @Column(name = "projeto", length = 255)
    private String projeto;

    /**
     * Anexos PDF desta cotação de rascunho com deduplificação via hash SHA-256
     * Evita armazenar o mesmo PDF múltiplas vezes
     */
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

    public void addItem(CotacaoRascunhoItem item) {
        itens.add(item);
        item.setCotacaoRascunho(this);
    }

    public void removeItem(CotacaoRascunhoItem item) {
        itens.remove(item);
        item.setCotacaoRascunho(null);
    }

    public BigDecimal getPreco() {
        if (itens != null && !itens.isEmpty()) {
            return itens.stream()
                .map(CotacaoRascunhoItem::calcularPrecoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return precoLegacy != null ? precoLegacy : BigDecimal.ZERO;
    }

    @Deprecated
    public void setPreco(BigDecimal preco) {
        this.precoLegacy = preco;
    }
}
