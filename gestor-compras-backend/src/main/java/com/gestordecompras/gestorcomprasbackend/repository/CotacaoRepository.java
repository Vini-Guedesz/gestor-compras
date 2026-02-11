package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CotacaoRepository extends JpaRepository<Cotacao, Long> {
    /**
     * Queries de agregação de preço foram removidas após refatoração do Bug #5.
     * O preço agora é calculado a partir dos itens individuais (CotacaoItem).
     * Se necessário, reimplementar usando JOIN com cotacao_item e SUM dos preços unitários.
     */
    // @Query("SELECT SUM(c.preco) FROM Cotacao c")
    // BigDecimal sumTotalPreco();
    //
    // @Query("SELECT MIN(c.preco) FROM Cotacao c")
    // BigDecimal findMinPreco();
    //
    // @Query("SELECT MAX(c.preco) FROM Cotacao c")
    // BigDecimal findMaxPreco();

    /**
     * Busca cotações associadas a um item de pedido específico.
     * <p>
     * <b>OTIMIZAÇÃO:</b> Query simplificada sem múltiplos JOIN FETCH para evitar produto cartesiano.
     * Use @EntityGraph ou lazy loading com @BatchSize para carregar relacionamentos adicionais.
     * </p>
     *
     * @param itemPedidoId ID do item de pedido
     * @return Lista de cotações
     */
    @Query("SELECT DISTINCT c FROM Cotacao c " +
           "JOIN c.itens ci " +
           "WHERE ci.itemPedido.id = :itemPedidoId")
    @EntityGraph(attributePaths = {"fornecedorProduto", "fornecedorServico"})
    List<Cotacao> findByItemPedidoIdWithFornecedores(@Param("itemPedidoId") Long itemPedidoId);

    /**
     * Busca paginada de cotações com otimização de lazy loading usando EntityGraph.
     * <p>
     * Carrega antecipadamente os fornecedores e a solicitação de pedido para evitar
     * problema N+1 (múltiplas queries para cada cotação da página).
     * </p>
     * <p>
     * <b>Performance:</b> Reduz de N+1 queries para apenas 1 query com JOINs.
     * Exemplo: Página com 20 cotações = 1 query ao invés de 21 queries.
     * </p>
     *
     * @param pageable Parâmetros de paginação e ordenação
     * @return Página de cotações com relacionamentos carregados
     */
    @EntityGraph(attributePaths = {"fornecedorProduto", "fornecedorServico", "solicitacaoDePedido"})
    Page<Cotacao> findAll(Pageable pageable);

    /**
     * Busca cotação por ID com otimização de lazy loading usando EntityGraph.
     * <p>
     * Carrega antecipadamente os fornecedores e a solicitação de pedido em uma única query.
     * </p>
     *
     * @param id ID da cotação
     * @return Optional contendo a cotação com relacionamentos carregados
     */
    @EntityGraph(attributePaths = {"fornecedorProduto", "fornecedorServico", "solicitacaoDePedido"})
    Optional<Cotacao> findById(Long id);

    /**
     * Busca cotação por ID carregando também os anexos e o PdfStorage associado.
     * Necessário para evitar LazyInitializationException ao acessar anexos fora da transação.
     */
    @EntityGraph(attributePaths = {"fornecedorProduto", "fornecedorServico", "solicitacaoDePedido", "anexos", "anexos.pdfStorage"})
    @Query("SELECT c FROM Cotacao c WHERE c.id = :id")
    Optional<Cotacao> findByIdWithAnexos(@Param("id") Long id);

    /**
     * Busca cotações de um fornecedor de produto específico.
     *
     * @param fornecedorId ID do fornecedor de produto
     * @return Lista de cotações
     */
    @EntityGraph(attributePaths = {"solicitacaoDePedido"})
    @Query("SELECT c FROM Cotacao c WHERE c.fornecedorProduto.id = :fornecedorId")
    List<Cotacao> findByFornecedorProdutoId(@Param("fornecedorId") Integer fornecedorId);

    /**
     * Busca cotações de um fornecedor de serviço específico.
     *
     * @param fornecedorId ID do fornecedor de serviço
     * @return Lista de cotações
     */
    @EntityGraph(attributePaths = {"solicitacaoDePedido"})
    @Query("SELECT c FROM Cotacao c WHERE c.fornecedorServico.id = :fornecedorId")
    List<Cotacao> findByFornecedorServicoId(@Param("fornecedorId") Integer fornecedorId);
}
