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
     * Bug #5 - Limitação de Design: Relacionamento N:N direto
     *
     * ATENÇÃO: Este relacionamento N:N direto tem limitações arquiteturais.
     * Ele não permite armazenar informações específicas do par cotação-item, como:
     * - Preço unitário do item nesta cotação específica
     * - Quantidade cotada (que pode diferir da quantidade solicitada)
     * - Observações específicas sobre o item nesta cotação
     *
     * QUANDO REFATORAR: Se o sistema precisar de qualquer das funcionalidades acima,
     * este relacionamento deve ser convertido em uma entidade intermediária:
     * CotacaoItem { cotacao_id, item_pedido_id, preco_unitario, quantidade_cotada, observacao }
     *
     * Por enquanto, este design é suficiente pois o preço é armazenado no nível da cotação
     * inteira, não por item individual.
     */
    @ManyToMany
    @JoinTable(
        name = "cotacao_item_pedido",
        joinColumns = @JoinColumn(name = "cotacao_id"),
        inverseJoinColumns = @JoinColumn(name = "item_pedido_id")
    )
    private Set<ItemPedido> itensPedido = new HashSet<>();

    private BigDecimal preco;

    private Integer prazoEmDiasUteis;

    private LocalDate dataLimite;

    @Column(columnDefinition = "bytea")
    private byte[] anexoPdf;

    private String caminhoAnexo;

    @OneToMany(mappedBy = "cotacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<AnexoCotacao> anexos = new ArrayList<>();

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
}
