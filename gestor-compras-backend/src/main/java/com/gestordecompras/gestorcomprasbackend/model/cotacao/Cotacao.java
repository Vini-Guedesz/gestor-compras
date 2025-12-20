package com.gestordecompras.gestorcomprasbackend.model.cotacao;

import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeServico;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa uma cotação de preços e prazos recebida de um fornecedor
 * para atender a uma solicitação de pedido.
 * <p>
 * Uma cotação pode ser de produtos ou serviços (exclusivo) e contém múltiplos itens
 * com preços individuais, além de anexos PDF (via Content-Addressable Storage).
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
 */
@Entity
@Table(name = "cotacao")
@Getter
@Setter
@NoArgsConstructor
public class Cotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Versão para controle de concorrência (Optimistic Locking).
     */
    @Version
    private Long version;

    /**
     * Fornecedor de produto associado à cotação (opcional, exclusivo com fornecedorServico).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_produto_id")
    private FornecedorDeProduto fornecedorProduto;

    /**
     * Fornecedor de serviço associado à cotação (opcional, exclusivo com fornecedorProduto).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_servico_id")
    private FornecedorDeServico fornecedorServico;

    /**
     * Solicitação de pedido à qual esta cotação pertence.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_de_pedido_id")
    private SolicitacaoDePedido solicitacaoDePedido;

    /**
     * Lista de itens cotados com preços individuais.
     * <p>
     * Relacionamento 1:N através da entidade intermediária {@link CotacaoItem}.
     * Permite definir preço unitário, quantidade e observações para cada item.
     * </p>
     * <p>
     * BatchSize otimiza o lazy loading: ao carregar itens de uma cotação,
     * Hibernate carrega também itens de até 25 outras cotações em memória,
     * reduzindo o problema N+1 de 25 queries para 1 query.
     * </p>
     */
    @OneToMany(mappedBy = "cotacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 25)
    private List<CotacaoItem> itens = new ArrayList<>();

    /**
     * Preço total legado.
     * @deprecated Use {@link #itens} e {@link #calcularPrecoTotal()} para obter o valor.
     */
    @Deprecated
    @Column(name = "preco")
    private BigDecimal precoLegacy;

    /**
     * Prazo de entrega em dias úteis informado pelo fornecedor.
     */
    private Integer prazoEmDiasUteis;

    /**
     * Data de validade da proposta.
     */
    private LocalDate dataLimite;

    /**
     * Anexos PDF desta cotação com deduplificação via hash SHA-256.
     * <p>
     * Utiliza a entidade {@link AnexoCotacao} que referencia o armazenamento centralizado.
     * </p>
     * <p>
     * BatchSize otimiza o lazy loading de anexos quando múltiplas cotações são carregadas,
     * reduzindo queries desnecessárias ao banco.
     * </p>
     */
    @OneToMany(mappedBy = "cotacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    @BatchSize(size = 25)
    private List<AnexoCotacao> anexos = new ArrayList<>();

    /**
     * Número da versão de edição (histórico).
     */
    @Column(name = "numero_versao")
    private Integer numeroVersao = 1;

    /**
     * Indica se a cotação sofreu edições manuais após a criação.
     */
    @Column(name = "foi_editada")
    private Boolean foiEditada = false;

    /**
     * Data e hora da última edição realizada.
     */
    @Column(name = "data_ultima_edicao")
    private java.time.LocalDateTime dataUltimaEdicao;

    /**
     * Motivo/Justificativa da última edição.
     */
    @Column(name = "motivo_ultima_edicao", length = 500)
    private String motivoUltimaEdicao;

    /**
     * Nome do usuário que realizou a última edição.
     */
    @Column(name = "editado_por", length = 100)
    private String editadoPor;

    /**
     * Validações executadas antes de persistir a entidade.
     */
    @PrePersist
    public void prePersist() {
        validarFornecedor();
    }

    /**
     * Validações executadas antes de atualizar a entidade.
     */
    @PreUpdate
    public void preUpdate() {
        validarFornecedor();
    }

    /**
     * Garante que a cotação tenha exatamente um tipo de fornecedor (produto XOR serviço).
     * @throws IllegalStateException Se nenhum ou ambos os fornecedores estiverem definidos.
     */
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

    /**
     * Retorna o ID do fornecedor, independentemente do tipo.
     * @return ID do fornecedor de produto ou serviço.
     */
    public Integer getFornecedorId() {
        if (fornecedorProduto != null) {
            return fornecedorProduto.getId();
        } else if (fornecedorServico != null) {
            return fornecedorServico.getId();
        }
        return null;
    }

    /**
     * Adiciona um item à cotação mantendo a consistência do relacionamento bidirecional.
     * @param item Item a ser adicionado.
     */
    public void addItem(CotacaoItem item) {
        itens.add(item);
        item.setCotacao(this);
    }

    /**
     * Remove um item da cotação e desfaz o vínculo.
     * @param item Item a ser removido.
     */
    public void removeItem(CotacaoItem item) {
        itens.remove(item);
        item.setCotacao(null);
    }

    /**
     * Calcula o valor total da cotação somando o subtotal de todos os itens.
     * <p>
     * Se não houver itens, tenta retornar o valor legado para compatibilidade.
     * </p>
     * @return Valor total calculado.
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
     * Obtém o preço total da cotação.
     * @return Valor total.
     */
    public BigDecimal getPreco() {
        return calcularPrecoTotal();
    }

    /**
     * Define o preço legado.
     * @param preco Valor total.
     * @deprecated Use {@link #addItem(CotacaoItem)} para adicionar itens com preços individuais.
     */
    @Deprecated
    public void setPreco(BigDecimal preco) {
        this.precoLegacy = preco;
    }
}
