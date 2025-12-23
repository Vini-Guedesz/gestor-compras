package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.pedido.AcaoPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.pedido.DevolverPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido.SolicitacaoDePedidoDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.SolicitacaoDePedidoMapper;
import com.gestordecompras.gestorcomprasbackend.model.pedido.HistoricoPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.StatusPedido;
import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.repository.ItemPedidoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.SolicitacaoDePedidoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
@Slf4j
@Service
public class SolicitacaoDePedidoService {

    private final SolicitacaoDePedidoRepository solicitacaoDePedidoRepository;
    private final SolicitacaoDePedidoMapper solicitacaoDePedidoMapper;
    private final ItemPedidoRepository itemPedidoRepository;
    private final HistoricoPedidoService historicoPedidoService;
    private final UserRepository userRepository;

    /**
     * Construtor com injeção de dependências.
     *
     * @param solicitacaoDePedidoRepository Repositório de solicitações de pedido.
     * @param solicitacaoDePedidoMapper Mapper para conversão entre entidade e DTO.
     * @param itemPedidoRepository Repositório de itens de pedido.
     * @param historicoPedidoService Serviço de histórico de pedidos.
     * @param userRepository Repositório de usuários.
     */
    public SolicitacaoDePedidoService(SolicitacaoDePedidoRepository solicitacaoDePedidoRepository,
                                      SolicitacaoDePedidoMapper solicitacaoDePedidoMapper,
                                      ItemPedidoRepository itemPedidoRepository,
                                      HistoricoPedidoService historicoPedidoService,
                                      UserRepository userRepository) {
        this.solicitacaoDePedidoRepository = solicitacaoDePedidoRepository;
        this.solicitacaoDePedidoMapper = solicitacaoDePedidoMapper;
        this.itemPedidoRepository = itemPedidoRepository;
        this.historicoPedidoService = historicoPedidoService;
        this.userRepository = userRepository;
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

    // ===== MÉTODOS DE TRANSIÇÃO DE ESTADO =====

    /**
     * Obtém o usuário autenticado atual.
     */
    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // getName() retorna o email usado no login
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado: " + email));
        return user;
    }

    /**
     * Envia pedido para aprovação (EM_NEGOCIACAO → PENDENTE_APROVACAO).
     * Finaliza a fase de negociação e envia para o aprovador.
     *
     * @param pedidoId ID do pedido
     * @param dto DTO com observações opcionais
     * @return DTO do pedido atualizado
     */
    @Transactional
    public SolicitacaoDePedidoDTO enviarParaAprovacao(Long pedidoId, AcaoPedidoDTO dto) {
        SolicitacaoDePedido pedido = solicitacaoDePedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + pedidoId));

        if (pedido.getStatus() != StatusPedido.EM_NEGOCIACAO) {
            throw new IllegalStateException("Apenas pedidos em negociação podem ser enviados para aprovação. Status atual: " + pedido.getStatus());
        }

        // Validar que há pelo menos um item
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalStateException("Não é possível enviar um pedido vazio para aprovação");
        }

        pedido.setStatus(StatusPedido.PENDENTE_APROVACAO);
        if (dto.observacao() != null && !dto.observacao().isBlank()) {
            pedido.setObservacao(dto.observacao());
        }

        SolicitacaoDePedido pedidoSalvo = solicitacaoDePedidoRepository.save(pedido);

        // Registrar no histórico
        User usuario = getUsuarioAutenticado();
        historicoPedidoService.registrarHistorico(
                pedido,
                usuario,
                HistoricoPedido.TipoModificacao.ENVIO_PARA_APROVACAO,
                "status",
                "EM_NEGOCIACAO",
                "PENDENTE_APROVACAO",
                dto.observacao()
        );

        log.info("Pedido {} enviado para aprovação por {}", pedidoId, usuario.getNome());

        return solicitacaoDePedidoMapper.toDTO(pedidoSalvo);
    }

    /**
     * Aprova um pedido (PENDENTE_APROVACAO → APROVADO).
     * Ação restrita ao aprovador.
     *
     * @param pedidoId ID do pedido
     * @param dto DTO com observações opcionais
     * @return DTO do pedido aprovado
     */
    @Transactional
    public SolicitacaoDePedidoDTO aprovarPedido(Long pedidoId, AcaoPedidoDTO dto) {
        SolicitacaoDePedido pedido = solicitacaoDePedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + pedidoId));

        if (pedido.getStatus() != StatusPedido.PENDENTE_APROVACAO) {
            throw new IllegalStateException("Apenas pedidos pendentes de aprovação podem ser aprovados. Status atual: " + pedido.getStatus());
        }

        pedido.setStatus(StatusPedido.APROVADO);
        SolicitacaoDePedido pedidoSalvo = solicitacaoDePedidoRepository.save(pedido);

        // Registrar no histórico
        User usuario = getUsuarioAutenticado();
        historicoPedidoService.registrarHistorico(
                pedido,
                usuario,
                HistoricoPedido.TipoModificacao.APROVACAO,
                "status",
                "PENDENTE_APROVACAO",
                "APROVADO",
                dto.observacao()
        );

        log.info("Pedido {} aprovado por {}", pedidoId, usuario.getNome());

        return solicitacaoDePedidoMapper.toDTO(pedidoSalvo);
    }

    /**
     * Cancela um pedido.
     * Pode ser feito em EM_NEGOCIACAO (pelo comprador) ou PENDENTE_APROVACAO (pelo aprovador).
     *
     * @param pedidoId ID do pedido
     * @param dto DTO com observações opcionais
     * @return DTO do pedido cancelado
     */
    @Transactional
    public SolicitacaoDePedidoDTO cancelarPedido(Long pedidoId, AcaoPedidoDTO dto) {
        SolicitacaoDePedido pedido = solicitacaoDePedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + pedidoId));

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new IllegalStateException("Pedido já está cancelado");
        }

        if (pedido.getStatus() == StatusPedido.APROVADO) {
            throw new IllegalStateException("Pedidos aprovados não podem ser cancelados");
        }

        StatusPedido statusAnterior = pedido.getStatus();
        pedido.setStatus(StatusPedido.CANCELADO);
        SolicitacaoDePedido pedidoSalvo = solicitacaoDePedidoRepository.save(pedido);

        // Registrar no histórico
        User usuario = getUsuarioAutenticado();
        historicoPedidoService.registrarHistorico(
                pedido,
                usuario,
                HistoricoPedido.TipoModificacao.CANCELAMENTO,
                "status",
                statusAnterior.name(),
                "CANCELADO",
                dto.observacao()
        );

        log.info("Pedido {} cancelado por {} (status anterior: {})", pedidoId, usuario.getNome(), statusAnterior);

        return solicitacaoDePedidoMapper.toDTO(pedidoSalvo);
    }

    /**
     * Devolve pedido para edição (PENDENTE_APROVACAO → EM_NEGOCIACAO).
     * Permite que o aprovador devolva o pedido para o comprador fazer ajustes.
     *
     * @param pedidoId ID do pedido
     * @param dto DTO com motivo obrigatório
     * @return DTO do pedido devolvido
     */
    @Transactional
    public SolicitacaoDePedidoDTO devolverParaEdicao(Long pedidoId, DevolverPedidoDTO dto) {
        SolicitacaoDePedido pedido = solicitacaoDePedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + pedidoId));

        if (pedido.getStatus() != StatusPedido.PENDENTE_APROVACAO) {
            throw new IllegalStateException("Apenas pedidos pendentes de aprovação podem ser devolvidos para edição. Status atual: " + pedido.getStatus());
        }

        pedido.setStatus(StatusPedido.EM_NEGOCIACAO);
        SolicitacaoDePedido pedidoSalvo = solicitacaoDePedidoRepository.save(pedido);

        // Registrar no histórico
        User usuario = getUsuarioAutenticado();
        historicoPedidoService.registrarHistorico(
                pedido,
                usuario,
                HistoricoPedido.TipoModificacao.DEVOLUCAO_PARA_EDICAO,
                "status",
                "PENDENTE_APROVACAO",
                "EM_NEGOCIACAO",
                "Motivo da devolução: " + dto.motivo()
        );

        log.info("Pedido {} devolvido para edição por {}. Motivo: {}", pedidoId, usuario.getNome(), dto.motivo());

        return solicitacaoDePedidoMapper.toDTO(pedidoSalvo);
    }
}
