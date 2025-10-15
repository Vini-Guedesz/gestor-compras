package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido.SolicitacaoDePedidoDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.SolicitacaoDePedidoMapper;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.repository.SolicitacaoDePedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.gestordecompras.gestorcomprasbackend.repository.ItemPedidoRepository;

@Service
public class SolicitacaoDePedidoService {

    private final SolicitacaoDePedidoRepository solicitacaoDePedidoRepository;
    private final SolicitacaoDePedidoMapper solicitacaoDePedidoMapper;
    private final ItemPedidoRepository itemPedidoRepository;

    public SolicitacaoDePedidoService(SolicitacaoDePedidoRepository solicitacaoDePedidoRepository, SolicitacaoDePedidoMapper solicitacaoDePedidoMapper, ItemPedidoRepository itemPedidoRepository) {
        this.solicitacaoDePedidoRepository = solicitacaoDePedidoRepository;
        this.solicitacaoDePedidoMapper = solicitacaoDePedidoMapper;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    public List<SolicitacaoDePedidoDTO> getAllSolicitacoes() {
        return solicitacaoDePedidoRepository.findAll().stream()
                .map(solicitacaoDePedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public SolicitacaoDePedidoDTO getSolicitacaoById(Long id) {
        return solicitacaoDePedidoRepository.findById(id)
                .map(solicitacaoDePedidoMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de pedido não encontrada com ID: " + id));
    }

    public SolicitacaoDePedidoDTO createSolicitacao(SolicitacaoDePedidoDTO solicitacaoDePedidoDTO) {
        SolicitacaoDePedido solicitacaoDePedido = solicitacaoDePedidoMapper.toEntity(solicitacaoDePedidoDTO);

        // Estabelece relacionamento bidirecional entre solicitacao e itens
        if (solicitacaoDePedido.getItens() != null) {
            solicitacaoDePedido.getItens().forEach(item -> {
                item.setSolicitacaoDePedido(solicitacaoDePedido);
            });
        }

        // O cascade ALL vai persistir os itens automaticamente
        return solicitacaoDePedidoMapper.toDTO(solicitacaoDePedidoRepository.save(solicitacaoDePedido));
    }

    public SolicitacaoDePedidoDTO updateSolicitacao(Long id, SolicitacaoDePedidoDTO solicitacaoDePedidoDTO) {
        return solicitacaoDePedidoRepository.findById(id)
                .map(solicitacao -> {
                    solicitacao.setObservacao(solicitacaoDePedidoDTO.observacao());
                    solicitacao.setStatus(solicitacaoDePedidoDTO.status());
                    // Itens são tratados através de seus próprios endpoints, mas você pode adicionar lógica aqui para atualizá-los se necessário.
                    return solicitacaoDePedidoMapper.toDTO(solicitacaoDePedidoRepository.save(solicitacao));
                })
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de pedido não encontrada com ID: " + id));
    }

    public void deleteSolicitacao(Long id) {
        if (!solicitacaoDePedidoRepository.existsById(id)) {
            throw new EntityNotFoundException("Solicitação de pedido não encontrada com ID: " + id);
        }
        solicitacaoDePedidoRepository.deleteById(id);
    }
}