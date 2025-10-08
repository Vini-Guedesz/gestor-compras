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
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de pedido não encontrada com o id: " + id));
    }

    public SolicitacaoDePedidoDTO createSolicitacao(SolicitacaoDePedidoDTO solicitacaoDePedidoDTO) {
        SolicitacaoDePedido solicitacaoDePedido = solicitacaoDePedidoMapper.toEntity(solicitacaoDePedidoDTO);

        // Handle existing ItemPedido entities
        if (solicitacaoDePedido.getItens() != null) {
            List<ItemPedido> managedItens = solicitacaoDePedido.getItens().stream()
                    .map(item -> {
                        if (item.getId() != null) {
                            // If item has an ID, fetch it from the database
                            return itemPedidoRepository.findById(item.getId())
                                    .orElseThrow(() -> new EntityNotFoundException("Item de pedido não encontrado com ID: " + item.getId()));
                        } else {
                            // If item does not have an ID, it's a new item, persist it
                            return itemPedidoRepository.save(item);
                        }
                    })
                    .collect(Collectors.toList());
            solicitacaoDePedido.setItens(managedItens);
        }

        return solicitacaoDePedidoMapper.toDTO(solicitacaoDePedidoRepository.save(solicitacaoDePedido));
    }

    public SolicitacaoDePedidoDTO updateSolicitacao(Long id, SolicitacaoDePedidoDTO solicitacaoDePedidoDTO) {
        return solicitacaoDePedidoRepository.findById(id)
                .map(solicitacao -> {
                    solicitacao.setObservacao(solicitacaoDePedidoDTO.observacao());
                    solicitacao.setStatus(solicitacaoDePedidoDTO.status());
                    // Itens are handled via their own endpoints, but you could add logic here to update them if needed.
                    return solicitacaoDePedidoMapper.toDTO(solicitacaoDePedidoRepository.save(solicitacao));
                })
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de pedido não encontrada com o id: " + id));
    }

    public void deleteSolicitacao(Long id) {
        if (!solicitacaoDePedidoRepository.existsById(id)) {
            throw new EntityNotFoundException("Solicitação de pedido não encontrada com o id: " + id);
        }
        solicitacaoDePedidoRepository.deleteById(id);
    }
}