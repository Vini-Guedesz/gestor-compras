package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido.SolicitacaoDePedidoDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.SolicitacaoDePedidoMapper;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.repository.SolicitacaoDePedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.gestordecompras.gestorcomprasbackend.repository.ItemPedidoRepository;

/**
 * Serviço responsável pelo gerenciamento de solicitações de pedidos.
 * <p>
 * Gerencia o ciclo de vida das solicitações, incluindo criação, atualização,
 * consulta (simples e otimizada com relacionamentos) e exclusão.
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
 */
@Service
public class SolicitacaoDePedidoService {

    private final SolicitacaoDePedidoRepository solicitacaoDePedidoRepository;
    private final SolicitacaoDePedidoMapper solicitacaoDePedidoMapper;
    private final ItemPedidoRepository itemPedidoRepository;

    /**
     * Construtor com injeção de dependências.
     *
     * @param solicitacaoDePedidoRepository Repositório de solicitações de pedido.
     * @param solicitacaoDePedidoMapper Mapper para conversão entre entidade e DTO.
     * @param itemPedidoRepository Repositório de itens de pedido.
     */
    public SolicitacaoDePedidoService(SolicitacaoDePedidoRepository solicitacaoDePedidoRepository, SolicitacaoDePedidoMapper solicitacaoDePedidoMapper, ItemPedidoRepository itemPedidoRepository) {
        this.solicitacaoDePedidoRepository = solicitacaoDePedidoRepository;
        this.solicitacaoDePedidoMapper = solicitacaoDePedidoMapper;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    /**
     * Recupera solicitações de pedido de forma paginada.
     *
     * @param pageable Objeto contendo informações de paginação.
     * @return Página de DTOs de solicitações de pedido.
     */
    public Page<SolicitacaoDePedidoDTO> getAllSolicitacoes(Pageable pageable) {
        return solicitacaoDePedidoRepository.findAll(pageable)
                .map(solicitacaoDePedidoMapper::toDTO);
    }

    /**
     * Busca uma solicitação de pedido pelo ID.
     * <p>
     * Utiliza estratégia otimizada com múltiplas queries para carregar relacionamentos
     * (itens e cotações) evitando o problema de produto cartesiano (N+1).
     * </p>
     *
     * @param id Identificador da solicitação.
     * @return DTO da solicitação encontrada.
     * @throws EntityNotFoundException Se a solicitação não for encontrada.
     */
    public SolicitacaoDePedidoDTO getSolicitacaoById(Long id) {
        // Bug Fix #1: Carregar dados em múltiplas queries para evitar produto cartesiano

        // 1. Carregar pedido com itens
        SolicitacaoDePedido pedido = solicitacaoDePedidoRepository.findByIdWithItensAndCotacoes(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de pedido não encontrada com ID: " + id));

        // 2. Carregar cotações com fornecedores (segunda query)
        solicitacaoDePedidoRepository.findByIdWithCotacoes(id);

        // 3. Carregar itens das cotações (terceira query)
        solicitacaoDePedidoRepository.findCotacoesWithItensByPedidoId(id);

        return solicitacaoDePedidoMapper.toDTO(pedido);
    }

    /**
     * Cria uma nova solicitação de pedido.
     *
     * @param solicitacaoDePedidoDTO DTO com os dados para criação.
     * @return DTO da solicitação criada.
     * @throws IllegalArgumentException Se a solicitação não tiver itens.
     */
    public SolicitacaoDePedidoDTO createSolicitacao(SolicitacaoDePedidoDTO solicitacaoDePedidoDTO) {
        SolicitacaoDePedido solicitacaoDePedido = solicitacaoDePedidoMapper.toEntity(solicitacaoDePedidoDTO);

        // Validação: Pedido deve ter pelo menos um item
        if (solicitacaoDePedido.getItens() == null || solicitacaoDePedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("Não é possível criar um pedido sem itens");
        }

        // Associar os itens ao pedido antes de salvar
        if (solicitacaoDePedido.getItens() != null && !solicitacaoDePedido.getItens().isEmpty()) {
            solicitacaoDePedido.getItens().forEach(item -> {
                item.setSolicitacaoDePedido(solicitacaoDePedido);
            });
        }

        // Salvar a solicitação (o cascade ALL irá salvar os itens automaticamente)
        SolicitacaoDePedido solicitacaoSalva = solicitacaoDePedidoRepository.save(solicitacaoDePedido);

        return solicitacaoDePedidoMapper.toDTO(solicitacaoSalva);
    }

    /**
     * Atualiza uma solicitação de pedido existente.
     * <p>
     * Permite atualizar status, observação e modificar a lista de itens (adicionar, editar, remover).
     * </p>
     *
     * @param id Identificador da solicitação.
     * @param solicitacaoDePedidoDTO DTO com os novos dados.
     * @return DTO da solicitação atualizada.
     * @throws EntityNotFoundException Se a solicitação não for encontrada.
     */
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

    /**
     * Exclui uma solicitação de pedido.
     *
     * @param id Identificador da solicitação.
     * @throws EntityNotFoundException Se a solicitação não for encontrada.
     */
    public void deleteSolicitacao(Long id) {
        if (!solicitacaoDePedidoRepository.existsById(id)) {
            throw new EntityNotFoundException("Solicitação de pedido não encontrada com ID: " + id);
        }
        solicitacaoDePedidoRepository.deleteById(id);
    }
}