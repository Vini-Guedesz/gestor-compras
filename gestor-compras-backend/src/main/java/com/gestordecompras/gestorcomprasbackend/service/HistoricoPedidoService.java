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

@Service
@RequiredArgsConstructor
public class HistoricoPedidoService {

    private final HistoricoPedidoRepository historicoPedidoRepository;
    private final HistoricoPedidoMapper historicoPedidoMapper;

    @Transactional(readOnly = true)
    public List<HistoricoPedidoDTO> getHistoricoPorPedido(Long pedidoId) {
        return historicoPedidoRepository.findBySolicitacaoDePedidoIdOrderByDataModificacaoDesc(pedidoId)
                .stream()
                .map(historicoPedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HistoricoPedidoDTO> getHistoricoPorUsuario(Long userId) {
        return historicoPedidoRepository.findByUsuarioIdOrderByDataModificacaoDesc(userId)
                .stream()
                .map(historicoPedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

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
                HistoricoPedido.TipoModificacao.ATUALIZACAO,
                "cotacao",
                null,
                nomeFornecedor,
                "Cotação editada: " + nomeFornecedor + (detalhes != null ? " - " + detalhes : "")
        );
    }
}
