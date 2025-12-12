package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitacaoDePedidoRepository extends JpaRepository<SolicitacaoDePedido, Long> {
    
    /**
     * Conta o número de solicitações em determinados status.
     *
     * @param statuses Lista de status para filtrar
     * @return Contagem de solicitações
     */
    @Query("SELECT count(s) FROM SolicitacaoDePedido s WHERE s.status in :statuses")
    long countByStatusIn(@Param("statuses") List<StatusPedido> statuses);

    /**
     * Calcula o total de itens em todas as solicitações.
     *
     * @return Total de itens
     */
    @Query("SELECT sum(size(s.itens)) FROM SolicitacaoDePedido s")
    Long countTotalItens();

    /**
     * Busca solicitações por status, carregando seus itens.
     * Ordenado por data de criação decrescente.
     *
     * @param statuses Lista de status para filtrar
     * @return Lista de solicitações
     */
    @Query("SELECT s FROM SolicitacaoDePedido s LEFT JOIN FETCH s.itens WHERE s.status IN :statuses ORDER BY s.dataCriacao DESC")
    List<SolicitacaoDePedido> findByStatusInWithItens(@Param("statuses") List<StatusPedido> statuses);

    /**
     * Busca uma solicitação por ID, carregando apenas seus itens.
     * Bug Fix #1: Primeira etapa da query otimizada para evitar produto cartesiano.
     *
     * @param id ID da solicitação
     * @return Solicitação encontrada (Optional)
     */
    @Query("SELECT DISTINCT s FROM SolicitacaoDePedido s " +
           "LEFT JOIN FETCH s.itens " +
           "WHERE s.id = :id")
    Optional<SolicitacaoDePedido> findByIdWithItens(@Param("id") Long id);

    /**
     * Query auxiliar para carregar cotações separadamente (evita produto cartesiano com itens).
     *
     * @param id ID da solicitação
     * @return Solicitação com cotações e fornecedores carregados
     */
    @Query("SELECT DISTINCT s FROM SolicitacaoDePedido s " +
           "LEFT JOIN FETCH s.cotacoes c " +
           "LEFT JOIN FETCH c.fornecedorProduto " +
           "LEFT JOIN FETCH c.fornecedorServico " +
           "WHERE s.id = :id")
    Optional<SolicitacaoDePedido> findByIdWithCotacoes(@Param("id") Long id);

    /**
     * Query para carregar itens das cotações (atualizada após refatoração Bug #5).
     * Busca todas as cotações de um pedido e carrega seus itens.
     *
     * @param pedidoId ID do pedido
     * @return Lista de cotações com itens carregados
     */
    @Query("SELECT DISTINCT c FROM Cotacao c " +
           "LEFT JOIN FETCH c.itens " +
           "WHERE c.solicitacaoDePedido.id = :pedidoId")
    List<Cotacao> findCotacoesWithItensByPedidoId(@Param("pedidoId") Long pedidoId);

    /**
     * Método combinado que carrega itens e pedido (usado como base).
     *
     * @param id ID da solicitação
     * @return Solicitação com itens carregados
     */
    @Query("SELECT DISTINCT s FROM SolicitacaoDePedido s " +
           "LEFT JOIN FETCH s.itens " +
           "WHERE s.id = :id")
    Optional<SolicitacaoDePedido> findByIdWithItensAndCotacoes(@Param("id") Long id);
}