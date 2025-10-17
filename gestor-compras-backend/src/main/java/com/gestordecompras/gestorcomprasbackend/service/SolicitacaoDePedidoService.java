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

        // Primeiro, salvar a solicitação para obter o ID
        SolicitacaoDePedido solicitacaoSalva = solicitacaoDePedidoRepository.save(solicitacaoDePedido);

        // Trata entidades ItemPedido
        if (solicitacaoDePedidoDTO.itens() != null && !solicitacaoDePedidoDTO.itens().isEmpty()) {
            List<ItemPedido> managedItens = solicitacaoDePedidoDTO.itens().stream()
                    .map(itemDTO -> {
                        ItemPedido item;
                        if (itemDTO.id() != null) {
                            // Se o item tem ID, busca do banco de dados
                            item = itemPedidoRepository.findById(itemDTO.id())
                                    .orElse(new ItemPedido());
                        } else {
                            // Se o item não tem ID, é um novo item
                            item = new ItemPedido();
                        }

                        // Atualizar campos do item
                        item.setNome(itemDTO.nome());
                        item.setQuantidade(itemDTO.quantidade());
                        item.setDescricao(itemDTO.descricao());
                        item.setObservacao(itemDTO.observacao());
                        item.setSolicitacaoDePedido(solicitacaoSalva);

                        // Salvar o item
                        return itemPedidoRepository.save(item);
                    })
                    .collect(Collectors.toList());
            solicitacaoSalva.setItens(managedItens);
        }

        return solicitacaoDePedidoMapper.toDTO(solicitacaoSalva);
    }

    public SolicitacaoDePedidoDTO updateSolicitacao(Long id, SolicitacaoDePedidoDTO solicitacaoDePedidoDTO) {
        return solicitacaoDePedidoRepository.findById(id)
                .map(solicitacao -> {
                    solicitacao.setObservacao(solicitacaoDePedidoDTO.observacao());
                    solicitacao.setStatus(solicitacaoDePedidoDTO.status());

                    // Atualizar itens se fornecidos no DTO
                    if (solicitacaoDePedidoDTO.itens() != null) {
                        // Coletar IDs dos itens que vêm do DTO
                        List<Long> idsItensDTO = solicitacaoDePedidoDTO.itens().stream()
                                .map(itemDTO -> itemDTO.id())
                                .filter(itemId -> itemId != null)
                                .collect(Collectors.toList());

                        // Identificar itens que existem no banco mas não estão no DTO (foram removidos)
                        List<ItemPedido> itensParaRemover = solicitacao.getItens().stream()
                                .filter(item -> item.getId() != null && !idsItensDTO.contains(item.getId()))
                                .collect(Collectors.toList());

                        // Remover itens deletados
                        for (ItemPedido itemRemover : itensParaRemover) {
                            solicitacao.getItens().remove(itemRemover);
                            itemPedidoRepository.delete(itemRemover);
                        }

                        // Processar cada item do DTO (atualizar existentes e adicionar novos)
                        for (var itemDTO : solicitacaoDePedidoDTO.itens()) {
                            ItemPedido item;

                            if (itemDTO.id() != null) {
                                // Item existente - buscar na lista atual ou no banco
                                item = solicitacao.getItens().stream()
                                        .filter(i -> i.getId() != null && i.getId().equals(itemDTO.id()))
                                        .findFirst()
                                        .orElseGet(() -> {
                                            // Se não está na lista, buscar do banco
                                            return itemPedidoRepository.findById(itemDTO.id())
                                                    .orElse(new ItemPedido());
                                        });

                                // Atualizar campos do item existente
                                item.setNome(itemDTO.nome());
                                item.setQuantidade(itemDTO.quantidade());
                                item.setDescricao(itemDTO.descricao());
                                item.setObservacao(itemDTO.observacao());
                                item.setSolicitacaoDePedido(solicitacao);
                            } else {
                                // Novo item - criar e adicionar à lista
                                item = new ItemPedido();
                                item.setNome(itemDTO.nome());
                                item.setQuantidade(itemDTO.quantidade());
                                item.setDescricao(itemDTO.descricao());
                                item.setObservacao(itemDTO.observacao());
                                item.setSolicitacaoDePedido(solicitacao);

                                // Adicionar à lista se ainda não existir
                                if (solicitacao.getItens() == null) {
                                    solicitacao.setItens(new java.util.ArrayList<>());
                                }
                                solicitacao.getItens().add(item);
                            }
                        }
                    }

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