package com.gestordecompras.gestorcomprasbackend.model.cotacao;

import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeServico;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "cotacao")
@Getter
@Setter
@NoArgsConstructor
public class Cotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_produto_id")
    private FornecedorDeProduto fornecedorProduto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_servico_id")
    private FornecedorDeServico fornecedorServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_de_pedido_id")
    private SolicitacaoDePedido solicitacaoDePedido;

    /**
     * Itens desta cotação com preços individuais (Bug #5 corrigido)
     *
     * Relacionamento através da entidade intermediária CotacaoItem, que permite:
     * - Preço unitário específico por item
     * - Quantidade cotada (pode diferir da quantidade solicitada)
     * - Observações específicas sobre cada item
     *
     * Substituiu o relacionamento N:N direto anterior que tinha limitações.
     */
    @OneToMany(mappedBy = "cotacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CotacaoItem> itens = new ArrayList<>();

    /**
     * @deprecated Removido. Use itens e calcule o preço total a partir dos itens individuais.
     * Mantido temporariamente para compatibilidade durante migração.
     */
    @Deprecated
    @Column(name = "preco")
    private BigDecimal precoLegacy;

    private Integer prazoEmDiasUteis;

    private LocalDate dataLimite;

    @Column(columnDefinition = "bytea")
    private byte[] anexoPdf;

    private String caminhoAnexo;

    @OneToMany(mappedBy = "cotacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<AnexoCotacao> anexos = new ArrayList<>();

    /**
     * Campos de auditoria para rastreamento de edições
     */
    @Column(name = "numero_versao")
    private Integer numeroVersao = 1;

    @Column(name = "foi_editada")
    private Boolean foiEditada = false;

    @Column(name = "data_ultima_edicao")
    private java.time.LocalDateTime dataUltimaEdicao;

    @Column(name = "motivo_ultima_edicao", length = 500)
    private String motivoUltimaEdicao;

    @Column(name = "editado_por", length = 100)
    private String editadoPor;

    @PrePersist
    public void prePersist() {
        validarFornecedor();
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

    /**
     * Adiciona um item à cotação com relacionamento bidirecional
     */
    public void addItem(CotacaoItem item) {
        itens.add(item);
        item.setCotacao(this);
    }

    /**
     * Remove um item da cotação
     */
    public void removeItem(CotacaoItem item) {
        itens.remove(item);
        item.setCotacao(null);
    }

    /**
     * Calcula o preço total da cotação somando todos os itens
     */
    public BigDecimal calcularPrecoTotal() {
        if (itens == null || itens.isEmpty()) {
            return precoLegacy != null ? precoLegacy : BigDecimal.ZERO;
        }
        return itens.stream()
                .map(CotacaoItem::calcularPrecoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Retorna o preço total (calculado ou legacy)
     */
    public BigDecimal getPreco() {
        return calcularPrecoTotal();
    }

    /**
     * @deprecated Use addItem(CotacaoItem) para adicionar itens com preços individuais
     */
    @Deprecated
    public void setPreco(BigDecimal preco) {
        this.precoLegacy = preco;
    }
}
