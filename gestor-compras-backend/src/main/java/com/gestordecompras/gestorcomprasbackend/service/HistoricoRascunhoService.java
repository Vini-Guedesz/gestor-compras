package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.model.rascunho.HistoricoRascunho;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.HistoricoRascunho.TipoAcaoRascunho;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.ItemRascunho;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.Rascunho;
import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.repository.HistoricoRascunhoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável pelo registro e consulta do histórico de alterações em rascunhos.
 * <p>
 * Esta classe mantém um log de auditoria para todas as operações realizadas em um rascunho,
 * permitindo o acompanhamento da evolução do rascunho até sua conversão em pedido.
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class HistoricoRascunhoService {

    private final HistoricoRascunhoRepository historicoRascunhoRepository;

    /**
     * Registra a criação de um novo rascunho.
     *
     * @param rascunho O rascunho criado.
     * @param usuario O usuário que criou o rascunho.
     */
    @Transactional
    public void registrarCriacaoRascunho(Rascunho rascunho, User usuario) {
        HistoricoRascunho historico = new HistoricoRascunho();
        historico.setRascunho(rascunho);
        historico.setUsuario(usuario);
        historico.setTipoAcao(TipoAcaoRascunho.CRIACAO_RASCUNHO);
        historico.setDescricao("Rascunho criado");
        historicoRascunhoRepository.save(historico);
    }

    /**
     * Registra a adição de um item ao rascunho.
     *
     * @param rascunho O rascunho modificado.
     * @param item O item adicionado.
     * @param usuario O usuário que realizou a ação.
     */
    @Transactional
    public void registrarAdicaoItem(Rascunho rascunho, ItemRascunho item, User usuario) {
        HistoricoRascunho historico = new HistoricoRascunho();
        historico.setRascunho(rascunho);
        historico.setUsuario(usuario);
        historico.setTipoAcao(TipoAcaoRascunho.ADICAO_ITEM);
        historico.setDescricao("Item adicionado ao rascunho");
        historico.setNumeroItem(item.getNumeroItem());
        historico.setNomeItem(item.getNome());
        historico.setDetalhes(String.format("Quantidade: %d", item.getQuantidade()));
        historicoRascunhoRepository.save(historico);
    }

    /**
     * Registra a atualização de um item existente no rascunho.
     *
     * @param rascunho O rascunho modificado.
     * @param item O item atualizado.
     * @param usuario O usuário que realizou a ação.
     * @param detalhes Descrição das alterações feitas.
     */
    @Transactional
    public void registrarAtualizacaoItem(Rascunho rascunho, ItemRascunho item, User usuario, String detalhes) {
        HistoricoRascunho historico = new HistoricoRascunho();
        historico.setRascunho(rascunho);
        historico.setUsuario(usuario);
        historico.setTipoAcao(TipoAcaoRascunho.ATUALIZACAO_ITEM);
        historico.setDescricao("Item atualizado");
        historico.setNumeroItem(item.getNumeroItem());
        historico.setNomeItem(item.getNome());
        historico.setDetalhes(detalhes);
        historicoRascunhoRepository.save(historico);
    }

    /**
     * Registra a remoção de um item do rascunho.
     *
     * @param rascunho O rascunho modificado.
     * @param item O item removido.
     * @param usuario O usuário que realizou a ação.
     */
    @Transactional
    public void registrarRemocaoItem(Rascunho rascunho, ItemRascunho item, User usuario) {
        HistoricoRascunho historico = new HistoricoRascunho();
        historico.setRascunho(rascunho);
        historico.setUsuario(usuario);
        historico.setTipoAcao(TipoAcaoRascunho.REMOCAO_ITEM);
        historico.setDescricao("Item removido do rascunho");
        historico.setNumeroItem(item.getNumeroItem());
        historico.setNomeItem(item.getNome());
        historicoRascunhoRepository.save(historico);
    }

    /**
     * Registra a atualização da observação do rascunho.
     *
     * @param rascunho O rascunho modificado.
     * @param usuario O usuário que realizou a ação.
     * @param observacaoAnterior A observação antes da alteração.
     * @param observacaoNova A nova observação.
     */
    @Transactional
    public void registrarAtualizacaoObservacao(Rascunho rascunho, User usuario, String observacaoAnterior, String observacaoNova) {
        HistoricoRascunho historico = new HistoricoRascunho();
        historico.setRascunho(rascunho);
        historico.setUsuario(usuario);
        historico.setTipoAcao(TipoAcaoRascunho.ATUALIZACAO_OBSERVACAO);
        historico.setDescricao("Observação atualizada");
        historico.setDetalhes(String.format("De: %s | Para: %s",
            observacaoAnterior != null ? observacaoAnterior : "(vazio)",
            observacaoNova != null ? observacaoNova : "(vazio)"));
        historicoRascunhoRepository.save(historico);
    }

    /**
     * Registra a adição de uma cotação ao rascunho.
     *
     * @param rascunho O rascunho modificado.
     * @param usuario O usuário que realizou a ação.
     * @param nomeFornecedor Nome do fornecedor da cotação.
     */
    @Transactional
    public void registrarAdicaoCotacao(Rascunho rascunho, User usuario, String nomeFornecedor) {
        HistoricoRascunho historico = new HistoricoRascunho();
        historico.setRascunho(rascunho);
        historico.setUsuario(usuario);
        historico.setTipoAcao(TipoAcaoRascunho.ADICAO_COTACAO);
        historico.setDescricao("Cotação adicionada");
        historico.setDetalhes("Fornecedor: " + nomeFornecedor);
        historicoRascunhoRepository.save(historico);
    }

    /**
     * Registra a remoção de uma cotação do rascunho.
     *
     * @param rascunho O rascunho modificado.
     * @param usuario O usuário que realizou a ação.
     * @param nomeFornecedor Nome do fornecedor da cotação removida.
     */
    @Transactional
    public void registrarRemocaoCotacao(Rascunho rascunho, User usuario, String nomeFornecedor) {
        HistoricoRascunho historico = new HistoricoRascunho();
        historico.setRascunho(rascunho);
        historico.setUsuario(usuario);
        historico.setTipoAcao(TipoAcaoRascunho.REMOCAO_COTACAO);
        historico.setDescricao("Cotação removida");
        historico.setDetalhes("Fornecedor: " + nomeFornecedor);
        historicoRascunhoRepository.save(historico);
    }

    /**
     * Registra a conversão de um rascunho em pedido oficial.
     *
     * @param rascunho O rascunho convertido.
     * @param usuario O usuário que realizou a conversão.
     * @param pedidoId ID do pedido gerado.
     */
    @Transactional
    public void registrarConversaoPedido(Rascunho rascunho, User usuario, Long pedidoId) {
        HistoricoRascunho historico = new HistoricoRascunho();
        historico.setRascunho(rascunho);
        historico.setUsuario(usuario);
        historico.setTipoAcao(TipoAcaoRascunho.CONVERSAO_PEDIDO);
        historico.setDescricao("Rascunho convertido em pedido");
        historico.setDetalhes("ID do Pedido: " + pedidoId);
        historicoRascunhoRepository.save(historico);
    }

    /**
     * Registra a devolução de um rascunho em cotação para edição.
     *
     * @param rascunho O rascunho devolvido.
     * @param usuario O usuário que realizou a devolução.
     * @param motivo Motivo da devolução.
     */
    @Transactional
    public void registrarDevolucaoParaEdicao(Rascunho rascunho, User usuario, String motivo) {
        HistoricoRascunho historico = new HistoricoRascunho();
        historico.setRascunho(rascunho);
        historico.setUsuario(usuario);
        historico.setTipoAcao(TipoAcaoRascunho.DEVOLUCAO_EDICAO);
        historico.setDescricao("Rascunho devolvido para edição");
        historico.setDetalhes("Motivo: " + motivo);
        historicoRascunhoRepository.save(historico);
    }

    /**
     * Lista o histórico completo de um rascunho.
     *
     * @param rascunhoId ID do rascunho.
     * @return Lista de entidades de histórico associadas ao rascunho.
     */
    @Transactional(readOnly = true)
    public List<HistoricoRascunho> listarPorRascunho(Long rascunhoId) {
        return historicoRascunhoRepository.findByRascunhoIdWithUsuario(rascunhoId);
    }
}
