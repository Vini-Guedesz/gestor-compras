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

@Service
@RequiredArgsConstructor
public class HistoricoRascunhoService {

    private final HistoricoRascunhoRepository historicoRascunhoRepository;

    @Transactional
    public void registrarCriacaoRascunho(Rascunho rascunho, User usuario) {
        HistoricoRascunho historico = new HistoricoRascunho();
        historico.setRascunho(rascunho);
        historico.setUsuario(usuario);
        historico.setTipoAcao(TipoAcaoRascunho.CRIACAO_RASCUNHO);
        historico.setDescricao("Rascunho criado");
        historicoRascunhoRepository.save(historico);
    }

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

    @Transactional(readOnly = true)
    public List<HistoricoRascunho> listarPorRascunho(Long rascunhoId) {
        return historicoRascunhoRepository.findByRascunhoIdWithUsuario(rascunhoId);
    }
}
