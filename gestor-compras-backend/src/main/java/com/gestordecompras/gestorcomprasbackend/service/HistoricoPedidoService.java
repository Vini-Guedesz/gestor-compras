package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.pedido.HistoricoPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.HistoricoPedidoMapper;
import com.gestordecompras.gestorcomprasbackend.model.pedido.HistoricoPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.repository.HistoricoPedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelo registro e consulta do histórico de alterações em pedidos.
 * <p>
 * Esta classe mantém um log de auditoria para todas as operações significativas realizadas
 * em um pedido, permitindo rastrear quem fez o quê e quando.
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class HistoricoPedidoService {

    private final HistoricoPedidoRepository historicoPedidoRepository;
    private final HistoricoPedidoMapper historicoPedidoMapper;

    /**
     * Recupera o histórico de um pedido específico.
     *
     * @param pedidoId ID do pedido.
     * @return Lista de DTOs de histórico, ordenados por data de modificação (mais recente primeiro).
     */
    @Transactional(readOnly = true)
    public List<HistoricoPedidoDTO> getHistoricoPorPedido(Long pedidoId) {
        return historicoPedidoRepository.findBySolicitacaoDePedidoIdOrderByDataModificacaoDesc(pedidoId)
                .stream()
                .map(historicoPedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recupera o histórico de ações realizadas por um usuário específico.
     *
     * @param userId ID do usuário.
     * @return Lista de DTOs de histórico associados ao usuário.
     */
    @Transactional(readOnly = true)
    public List<HistoricoPedidoDTO> getHistoricoPorUsuario(Long userId) {
        return historicoPedidoRepository.findByUsuarioIdOrderByDataModificacaoDesc(userId)
                .stream()
                .map(historicoPedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Método genérico para registrar um evento no histórico.
     *
     * @param pedido Pedido associado ao evento.
     * @param usuario Usuário que realizou a ação.
     * @param tipoModificacao Tipo da modificação (enum).
     * @param campoModificado Nome do campo alterado (opcional).
     * @param valorAnterior Valor antes da alteração (opcional).
     * @param valorNovo Valor após a alteração (opcional).
     * @param observacao Descrição textual do evento.
     */
    @Transactional
    public void registrarHistorico(
            SolicitacaoDePedido pedido,
            User usuario,
            HistoricoPedido.TipoModificacao tipoModificacao,
            String campoModificado,
            String valorAnterior,
            String valorNovo,
            String observacao
    ) {
        HistoricoPedido historico = new HistoricoPedido();
        historico.setSolicitacaoDePedido(pedido);
        historico.setUsuario(usuario);
        historico.setTipoModificacao(tipoModificacao);
        historico.setCampoModificado(campoModificado);
        historico.setValorAnterior(valorAnterior);
        historico.setValorNovo(valorNovo);
        historico.setObservacao(observacao);

        historicoPedidoRepository.save(historico);
    }

    /**
     * Registra o evento de criação de um pedido.
     *
     * @param pedido Pedido criado.
     * @param usuario Usuário criador.
     */
    @Transactional
    public void registrarCriacao(SolicitacaoDePedido pedido, User usuario) {
        registrarHistorico(
                pedido,
                usuario,
                HistoricoPedido.TipoModificacao.CRIACAO,
                null,
                null,
                null,
                "Pedido criado"
        );
    }

    /**
     * Registra a mudança de status de um pedido.
     *
     * @param pedido Pedido alterado.
     * @param usuario Usuário que alterou o status.
     * @param statusAnterior Status anterior.
     * @param statusNovo Novo status.
     */
    @Transactional
    public void registrarMudancaStatus(
            SolicitacaoDePedido pedido,
            User usuario,
            String statusAnterior,
            String statusNovo
    ) {
        registrarHistorico(
                pedido,
                usuario,
                HistoricoPedido.TipoModificacao.MUDANCA_STATUS,
                "status",
                statusAnterior,
                statusNovo,
                "Status alterado de " + statusAnterior + " para " + statusNovo
        );
    }

    /**
     * Registra a adição de um item ao pedido.
     *
     * @param pedido Pedido alterado.
     * @param usuario Usuário que adicionou o item.
     * @param nomeItem Nome do item adicionado.
     */
    @Transactional
    public void registrarAdicaoItem(
            SolicitacaoDePedido pedido,
            User usuario,
            String nomeItem
    ) {
        registrarHistorico(
                pedido,
                usuario,
                HistoricoPedido.TipoModificacao.ADICAO_ITEM,
                "itens",
                null,
                nomeItem,
                "Item adicionado: " + nomeItem
        );
    }

    /**
     * Registra a remoção de um item do pedido.
     *
     * @param pedido Pedido alterado.
     * @param usuario Usuário que removeu o item.
     * @param nomeItem Nome do item removido.
     */
    @Transactional
    public void registrarRemocaoItem(
            SolicitacaoDePedido pedido,
            User usuario,
            String nomeItem
    ) {
        registrarHistorico(
                pedido,
                usuario,
                HistoricoPedido.TipoModificacao.REMOCAO_ITEM,
                "itens",
                nomeItem,
                null,
                "Item removido: " + nomeItem
        );
    }

    /**
     * Registra a atualização de um item do pedido.
     *
     * @param pedido Pedido alterado.
     * @param usuario Usuário que atualizou o item.
     * @param nomeItem Nome do item atualizado.
     * @param detalhes Detalhes da alteração.
     */
    @Transactional
    public void registrarAtualizacaoItem(
            SolicitacaoDePedido pedido,
            User usuario,
            String nomeItem,
            String detalhes
    ) {
        registrarHistorico(
                pedido,
                usuario,
                HistoricoPedido.TipoModificacao.ATUALIZACAO_ITEM,
                "itens",
                null,
                nomeItem,
                "Item atualizado: " + nomeItem + (detalhes != null ? " - " + detalhes : "")
        );
    }

    /**
     * Registra a adição de uma cotação ao pedido.
     *
     * @param pedido Pedido alterado.
     * @param usuario Usuário que adicionou a cotação.
     * @param nomeFornecedor Nome do fornecedor da cotação.
     */
    @Transactional
    public void registrarAdicaoCotacao(
            SolicitacaoDePedido pedido,
            User usuario,
            String nomeFornecedor
    ) {
        registrarHistorico(
                pedido,
                usuario,
                HistoricoPedido.TipoModificacao.ADICAO_COTACAO,
                "cotacoes",
                null,
                nomeFornecedor,
                "Cotação adicionada: " + nomeFornecedor
        );
    }

    /**
     * Registra a remoção de uma cotação do pedido.
     *
     * @param pedido Pedido alterado.
     * @param usuario Usuário que removeu a cotação.
     * @param nomeFornecedor Nome do fornecedor da cotação.
     */
    @Transactional
    public void registrarRemocaoCotacao(
            SolicitacaoDePedido pedido,
            User usuario,
            String nomeFornecedor
    ) {
        registrarHistorico(
                pedido,
                usuario,
                HistoricoPedido.TipoModificacao.REMOCAO_COTACAO,
                "cotacoes",
                nomeFornecedor,
                null,
                "Cotação removida: " + nomeFornecedor
        );
    }

    /**
     * Registra a edição de uma cotação existente.
     *
     * @param pedido Pedido associado.
     * @param usuario Usuário que editou a cotação.
     * @param nomeFornecedor Nome do fornecedor.
     * @param detalhes Detalhes sobre o que foi alterado.
     */
    @Transactional
    public void registrarEdicaoCotacao(
            SolicitacaoDePedido pedido,
            User usuario,
            String nomeFornecedor,
            String detalhes
    ) {
        registrarHistorico(
                pedido,
                usuario,
                HistoricoPedido.TipoModificacao.EDICAO_COTACAO,
                "cotacao",
                null,
                nomeFornecedor,
                "Cotação editada: " + nomeFornecedor + (detalhes != null ? " - " + detalhes : "")
        );
    }
}
