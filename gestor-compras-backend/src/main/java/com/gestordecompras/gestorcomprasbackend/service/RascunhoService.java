package com.gestordecompras.gestorcomprasbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.*;
import com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido.SolicitacaoDePedidoDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.RascunhoMapper;
import com.gestordecompras.gestorcomprasbackend.mapper.SolicitacaoDePedidoMapper;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.StatusPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.TipoItem;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.*;
import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.model.user.UserRole;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.AnexoCotacao;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.HistoricoCotacao;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.StatusCotacao;
import com.gestordecompras.gestorcomprasbackend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelo gerenciamento de rascunhos de pedidos.
 * <p>
 * Permite que usuários criem rascunhos, adicionem itens, realizem cotações preliminares
 * e convertam esses rascunhos em solicitações de pedido formais.
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("deprecation")
public class RascunhoService {

    private final RascunhoRepository rascunhoRepository;
    private final ItemRascunhoRepository itemRascunhoRepository;
    private final SolicitacaoDePedidoRepository solicitacaoDePedidoRepository;
    private final UserRepository userRepository;
    private final RascunhoMapper rascunhoMapper;
    private final SolicitacaoDePedidoMapper solicitacaoDePedidoMapper;
    private final HistoricoPedidoService historicoPedidoService;
    private final HistoricoRascunhoService historicoRascunhoService;
    private final CotacaoRascunhoRepository cotacaoRascunhoRepository;
    private final CotacaoRepository cotacaoRepository;
    private final HistoricoCotacaoRepository historicoCotacaoRepository;
    private final NumeroItemDisponivelRepository numeroItemDisponivelRepository;
    private final PdfDeduplicationService pdfDeduplicationService;
    private final ObjectMapper objectMapper;

    /**
     * Recupera todos os rascunhos de forma paginada.
     *
     * @param pageable Objeto contendo informações de paginação.
     * @return Página de DTOs de rascunhos.
     */
    @Transactional(readOnly = true)
    public Page<RascunhoDTO> getAllRascunhos(Pageable pageable) {
        return rascunhoRepository.findAll(pageable).map(rascunhoMapper::toDTO);
    }

    /**
     * Recupera todos os rascunhos criados por um usuário específico.
     *
     * @param userId ID do usuário criador.
     * @return Lista de DTOs de rascunhos do usuário.
     */
    @Transactional(readOnly = true)
    public List<RascunhoDTO> getRascunhosPorUsuario(Long userId) {
        return rascunhoRepository.findAllByCriadorIdWithItens(userId).stream()
                .map(rascunhoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um rascunho pelo ID, incluindo seus itens.
     *
     * @param id Identificador do rascunho.
     * @return DTO do rascunho encontrado.
     * @throws EntityNotFoundException Se o rascunho não for encontrado.
     */
    @Transactional(readOnly = true)
    public RascunhoDTO getRascunhoById(Long id) {
        Rascunho rascunho = rascunhoRepository.findByIdWithItens(id);
        if (rascunho == null) {
            throw new EntityNotFoundException("Rascunho não encontrado com ID: " + id);
        }
        return rascunhoMapper.toDTO(rascunho);
    }

    /**
     * Cria um novo rascunho.
     *
     * @param dto DTO com os dados para criação.
     * @return DTO do rascunho criado.
     */
    @Transactional
    public RascunhoDTO createRascunho(RascunhoCreateDTO dto) {
        User criador = getUsuarioAutenticado();

        Rascunho rascunho = new Rascunho();
        rascunho.setCriador(criador);
        rascunho.setObservacao(dto.observacao());
        rascunho.setObjetivo(dto.objetivo());
        rascunho.setProximoNumeroItem(1);

        Rascunho rascunhoSalvo = rascunhoRepository.save(rascunho);

        // Registrar criação no histórico
        historicoRascunhoService.registrarCriacaoRascunho(rascunhoSalvo, criador);

        // Adicionar itens se existirem
        if (dto.itens() != null && !dto.itens().isEmpty()) {
            for (ItemRascunhoCreateDTO itemDTO : dto.itens()) {
                adicionarItemInterno(rascunhoSalvo, itemDTO, criador);
            }
        }

        return rascunhoMapper.toDTO(rascunhoSalvo);
    }

    /**
     * Atualiza o status de um rascunho.
     *
     * @param rascunhoId ID do rascunho.
     * @param status Novo status (ATIVO, EM_COTACAO, FINALIZADO).
     * @return DTO do rascunho atualizado.
     * @throws EntityNotFoundException Se o rascunho não for encontrado.
     * @throws IllegalArgumentException Se o status for inválido.
     */
    @Transactional
    public RascunhoDTO atualizarStatus(Long rascunhoId, String status) {
        Rascunho rascunho = rascunhoRepository.findById(rascunhoId)
                .orElseThrow(() -> new EntityNotFoundException("Rascunho não encontrado com ID: " + rascunhoId));

        try {
            StatusRascunho novoStatus = StatusRascunho.valueOf(status.toUpperCase());
            rascunho.setStatus(novoStatus);
            rascunhoRepository.save(rascunho);
            return rascunhoMapper.toDTO(rascunho);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + status + ". Use: ATIVO, EM_COTACAO ou FINALIZADO");
        }
    }

    /**
     * Conta quantas cotações existem para um rascunho.
     * Útil para avisar o usuário antes de devolver para edição.
     *
     * @param rascunhoId ID do rascunho.
     * @return Quantidade de cotações.
     */
    @Transactional(readOnly = true)
    public int contarCotacoes(Long rascunhoId) {
        List<CotacaoRascunho> cotacoes = cotacaoRascunhoRepository.findByRascunhoIdWithItens(rascunhoId);
        return cotacoes.size();
    }

    /**
     * Devolve um rascunho em cotação para edição (status volta para ATIVO).
     * Registra a ação no histórico com o motivo fornecido.
     * ATENÇÃO: Remove TODAS as cotações existentes para evitar inconsistências.
     *
     * @param rascunhoId ID do rascunho.
     * @param dto DTO contendo o motivo da devolução.
     * @return DTO do rascunho atualizado.
     * @throws EntityNotFoundException Se o rascunho não for encontrado.
     * @throws IllegalStateException Se o rascunho não estiver em EM_COTACAO.
     */
    @Transactional
    public RascunhoDTO devolverParaEdicao(Long rascunhoId, com.gestordecompras.gestorcomprasbackend.dto.rascunho.DevolverRascunhoDTO dto) {
        Rascunho rascunho = rascunhoRepository.findById(rascunhoId)
                .orElseThrow(() -> new EntityNotFoundException("Rascunho não encontrado com ID: " + rascunhoId));

        // Validar que o rascunho está em cotação
        if (rascunho.getStatus() != StatusRascunho.EM_COTACAO) {
            throw new IllegalStateException("Apenas rascunhos em cotação podem ser devolvidos para edição. Status atual: " + rascunho.getStatus());
        }

        // Verificar se há cotações existentes
        List<CotacaoRascunho> cotacoesExistentes = cotacaoRascunhoRepository.findByRascunhoIdWithItens(rascunhoId);

        if (!cotacoesExistentes.isEmpty()) {
            // Registrar no histórico a remoção das cotações
            User usuario = getUsuarioAutenticado();

            log.warn("Rascunho {} possui {} cotação(ões) que serão removidas ao devolver para edição",
                    rascunhoId, cotacoesExistentes.size());

            // Remover todas as cotações existentes para evitar inconsistências
            for (CotacaoRascunho cotacao : cotacoesExistentes) {
                historicoRascunhoService.registrarRemocaoCotacao(
                    rascunho,
                    usuario,
                    "Cotação ID " + cotacao.getId() + " removida automaticamente devido à devolução para edição"
                );
            }

            cotacaoRascunhoRepository.deleteByRascunhoId(rascunhoId);
            cotacaoRascunhoRepository.flush(); // Garante que a remoção seja efetivada antes de continuar
        }

        // Atualizar status para ATIVO
        rascunho.setStatus(StatusRascunho.ATIVO);
        Rascunho rascunhoSalvo = rascunhoRepository.save(rascunho);

        // Registrar no histórico
        User usuario = getUsuarioAutenticado();
        historicoRascunhoService.registrarDevolucaoParaEdicao(rascunho, usuario, dto.motivo());

        log.info("Rascunho {} devolvido para edição por {}. Motivo: {}. Cotações removidas: {}",
                rascunhoId, usuario.getUsername(), dto.motivo(), cotacoesExistentes.size());

        return rascunhoMapper.toDTO(rascunhoSalvo);
    }

    /**
     * Adiciona um novo item ao rascunho.
     *
     * @param rascunhoId ID do rascunho.
     * @param itemDTO DTO com os dados do item.
     * @return DTO do rascunho atualizado com o novo item.
     * @throws EntityNotFoundException Se o rascunho não for encontrado.
     */
    @Transactional
    public RascunhoDTO adicionarItem(Long rascunhoId, ItemRascunhoCreateDTO itemDTO) {
        Rascunho rascunho = rascunhoRepository.findById(rascunhoId)
                .orElseThrow(() -> new EntityNotFoundException("Rascunho não encontrado com ID: " + rascunhoId));

        User usuario = getUsuarioAutenticado();
        adicionarItemInterno(rascunho, itemDTO, usuario);

        return rascunhoMapper.toDTO(rascunho);
    }

    private void adicionarItemInterno(Rascunho rascunho, ItemRascunhoCreateDTO itemDTO, User usuario) {
        // Obter próximo número de item (reutilizar se disponível)
        Integer numeroItem = obterProximoNumeroItem(rascunho);

        ItemRascunho item = new ItemRascunho();
        item.setNumeroItem(numeroItem);
        item.setNome(itemDTO.nome());
        item.setQuantidade(itemDTO.quantidade());
        item.setDescricao(itemDTO.descricao());
        item.setObservacao(itemDTO.observacao());
        // Definir tipo
        if (itemDTO.tipo() != null) {
            try {
                item.setTipo(TipoItem.valueOf(itemDTO.tipo()));
            } catch (IllegalArgumentException e) {
                item.setTipo(TipoItem.PRODUTO); // Default
            }
        } else {
            item.setTipo(TipoItem.PRODUTO); // Default
        }
        item.setRascunho(rascunho);

        rascunho.getItens().add(item);
        itemRascunhoRepository.save(item);

        // Registrar no histórico
        historicoRascunhoService.registrarAdicaoItem(rascunho, item, usuario);
    }

    private Integer obterProximoNumeroItem(Rascunho rascunho) {
        // Primeiro, verificar se há números disponíveis para reutilização
        Optional<NumeroItemDisponivel> disponivel = numeroItemDisponivelRepository
                .findFirstByRascunhoIdOrderByNumeroItemAsc(rascunho.getId());

        if (disponivel.isPresent()) {
            Integer numero = disponivel.get().getNumeroItem();
            numeroItemDisponivelRepository.delete(disponivel.get());
            return numero;
        }

        // Se não há números disponíveis, usar o próximo sequencial
        Integer numero = rascunho.getProximoNumeroItem();
        rascunho.setProximoNumeroItem(numero + 1);
        rascunhoRepository.save(rascunho);
        return numero;
    }

    /**
     * Atualiza um item existente no rascunho.
     *
     * @param rascunhoId ID do rascunho.
     * @param itemId ID do item a ser atualizado.
     * @param itemDTO Novos dados do item.
     * @return DTO do rascunho atualizado.
     * @throws EntityNotFoundException Se o rascunho ou o item não forem encontrados.
     * @throws IllegalArgumentException Se o item não pertencer ao rascunho.
     */
    @Transactional
    public RascunhoDTO atualizarItem(Long rascunhoId, Long itemId, ItemRascunhoUpdateDTO itemDTO) {
        Rascunho rascunho = rascunhoRepository.findById(rascunhoId)
                .orElseThrow(() -> new EntityNotFoundException("Rascunho não encontrado com ID: " + rascunhoId));

        ItemRascunho item = itemRascunhoRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado com ID: " + itemId));

        if (!item.getRascunho().getId().equals(rascunhoId)) {
            throw new IllegalArgumentException("Item não pertence ao rascunho especificado");
        }

        User usuario = getUsuarioAutenticado();

        // Registrar alterações
        StringBuilder detalhes = new StringBuilder();
        if (!item.getNome().equals(itemDTO.nome())) {
            detalhes.append("Nome: ").append(item.getNome()).append(" -> ").append(itemDTO.nome()).append("; ");
        }
        if (!item.getQuantidade().equals(itemDTO.quantidade())) {
            detalhes.append("Quantidade: ").append(item.getQuantidade()).append(" -> ").append(itemDTO.quantidade()).append("; ");
        }

        // Atualizar item
        item.setNome(itemDTO.nome());
        item.setQuantidade(itemDTO.quantidade());
        item.setDescricao(itemDTO.descricao());
        item.setObservacao(itemDTO.observacao());

        itemRascunhoRepository.save(item);

        // Registrar no histórico
        if (detalhes.length() > 0) {
            historicoRascunhoService.registrarAtualizacaoItem(rascunho, item, usuario, detalhes.toString());
        }

        return rascunhoMapper.toDTO(rascunho);
    }

    /**
     * Remove um item do rascunho.
     *
     * @param rascunhoId ID do rascunho.
     * @param itemId ID do item a ser removido.
     * @return DTO do rascunho atualizado.
     * @throws EntityNotFoundException Se o rascunho ou o item não forem encontrados.
     * @throws IllegalArgumentException Se o item não pertencer ao rascunho.
     */
    @Transactional
    public RascunhoDTO removerItem(Long rascunhoId, Long itemId) {
        Rascunho rascunho = rascunhoRepository.findById(rascunhoId)
                .orElseThrow(() -> new EntityNotFoundException("Rascunho não encontrado com ID: " + rascunhoId));

        ItemRascunho item = itemRascunhoRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado com ID: " + itemId));

        if (!item.getRascunho().getId().equals(rascunhoId)) {
            throw new IllegalArgumentException("Item não pertence ao rascunho especificado");
        }

        User usuario = getUsuarioAutenticado();

        // Registrar no histórico antes de remover
        historicoRascunhoService.registrarRemocaoItem(rascunho, item, usuario);

        // Guardar número do item para reutilização
        NumeroItemDisponivel numeroDisponivel = new NumeroItemDisponivel();
        numeroDisponivel.setRascunho(rascunho);
        numeroDisponivel.setNumeroItem(item.getNumeroItem());
        numeroItemDisponivelRepository.save(numeroDisponivel);

        // Remover item
        rascunho.getItens().remove(item);
        itemRascunhoRepository.delete(item);

        return rascunhoMapper.toDTO(rascunho);
    }

    /**
     * Atualiza dados gerais do rascunho e seus itens em lote.
     *
     * @param id Identificador do rascunho.
     * @param dto DTO com os dados atualizados.
     * @return DTO do rascunho atualizado.
     * @throws EntityNotFoundException Se o rascunho não for encontrado.
     */
    @Transactional
    public RascunhoDTO updateRascunho(Long id, RascunhoUpdateDTO dto) {
        Rascunho rascunho = rascunhoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rascunho não encontrado com ID: " + id));

        User usuario = getUsuarioAutenticado();

        // Registrar atualização de observação se mudou
        if (dto.observacao() != null && !dto.observacao().equals(rascunho.getObservacao())) {
            historicoRascunhoService.registrarAtualizacaoObservacao(rascunho, usuario,
                rascunho.getObservacao(), dto.observacao());
        }

        rascunho.setObservacao(dto.observacao());
        rascunho.setObjetivo(dto.objetivo());

        // Atualizar itens se fornecidos
        if (dto.itens() != null) {
            // Limpar itens existentes e números disponíveis
            rascunho.getItens().clear();
            rascunho.getNumerosDisponiveis().clear();
            rascunho.setProximoNumeroItem(1);

            // Adicionar novos itens
            for (ItemRascunhoCreateDTO itemDTO : dto.itens()) {
                adicionarItemInterno(rascunho, itemDTO, usuario);
            }
        }

        Rascunho rascunhoAtualizado = rascunhoRepository.save(rascunho);
        return rascunhoMapper.toDTO(rascunhoAtualizado);
    }

    /**
     * Exclui um rascunho.
     *
     * @param id Identificador do rascunho.
     * @throws EntityNotFoundException Se o rascunho não for encontrado.
     */
    @Transactional
    public void deleteRascunho(Long id) {
        if (!rascunhoRepository.existsById(id)) {
            throw new EntityNotFoundException("Rascunho não encontrado com ID: " + id);
        }
        rascunhoRepository.deleteById(id);
    }

    /**
     * Converte um rascunho em uma solicitação de pedido oficial.
     * <p>
     * Cria um novo pedido com base nos itens selecionados e suas cotações, e marca o rascunho como finalizado.
     * </p>
     *
     * @param rascunhoId ID do rascunho.
     * @param dto DTO contendo a seleção de itens e mapeamento de cotações.
     * @return DTO do pedido criado.
     * @throws EntityNotFoundException Se o rascunho não for encontrado.
     * @throws SecurityException Se o usuário não for o dono do rascunho.
     * @throws IllegalArgumentException Se houver inconsistências na seleção de itens ou cotações.
     */
    @Transactional
    public SolicitacaoDePedidoDTO converterRascunhoParaPedido(Long rascunhoId, ConverterRascunhoParaPedidoDTO dto) {
        Rascunho rascunho = rascunhoRepository.findByIdWithItens(rascunhoId);
        if (rascunho == null) {
            throw new EntityNotFoundException("Rascunho não encontrado com ID: " + rascunhoId);
        }

        // Validação de permissão: Verificar se o usuário pode converter o rascunho
        // ADMIN e COMPRADOR podem converter qualquer rascunho
        // USUARIO pode converter apenas seus próprios rascunhos
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String emailUsuarioAutenticado = authentication.getName();
            User usuarioAutenticado = userRepository.findByEmail(emailUsuarioAutenticado)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário autenticado não encontrado"));

            boolean isAdminOuComprador = usuarioAutenticado.getRole() == UserRole.ADMIN ||
                                          usuarioAutenticado.getRole() == UserRole.COMPRADOR;

            // Se não for ADMIN nem COMPRADOR, verificar ownership
            if (!isAdminOuComprador) {
                String emailCriadorRascunho = rascunho.getCriador() != null ? rascunho.getCriador().getEmail() : null;

                if (emailCriadorRascunho == null || !emailCriadorRascunho.equals(emailUsuarioAutenticado)) {
                    throw new SecurityException("Você não tem permissão para converter este rascunho. Apenas o criador, ADMIN ou COMPRADOR podem converter.");
                }
            }
        }

        // Validação: Deve ter pelo menos um item selecionado
        List<Long> todosItensSelecionados = dto.getTodosItens();
        if (todosItensSelecionados == null || todosItensSelecionados.isEmpty()) {
            throw new IllegalArgumentException("Não é possível converter rascunho para pedido sem selecionar itens");
        }

        // Validar que os IDs dos itens pertencem ao rascunho
        List<Long> idsItensRascunho = rascunho.getItens().stream()
                .map(ItemRascunho::getId)
                .collect(Collectors.toList());

        for (Long itemId : todosItensSelecionados) {
            if (!idsItensRascunho.contains(itemId)) {
                throw new IllegalArgumentException("Item ID " + itemId + " não pertence ao rascunho " + rascunhoId);
            }
        }

        // Buscar cotações do rascunho
        List<CotacaoRascunho> cotacoesRascunho = cotacaoRascunhoRepository.findByRascunhoIdWithItens(rascunhoId);

        // Identificar quais itens estão presentes em cotações que serão convertidas
        // (Baseado no mapeamento enviado pelo DTO)
        Set<Long> idsItensComCotacaoValida = new HashSet<>();
        for (Map.Entry<Long, List<Long>> entry : dto.cotacaoParaItens().entrySet()) {
            idsItensComCotacaoValida.addAll(entry.getValue());
        }

        // Validação: Apenas itens que possuem cotação mapeada podem ir para o pedido
        List<Long> itensSemCotacaoNoMapeamento = todosItensSelecionados.stream()
                .filter(itemId -> !idsItensComCotacaoValida.contains(itemId))
                .collect(Collectors.toList());

        List<Long> itensFinaisParaPedido;
        if (!itensSemCotacaoNoMapeamento.isEmpty()) {
            log.warn("Removendo itens selecionados que não possuem cotação mapeada: {}", itensSemCotacaoNoMapeamento);
            // Filtrar apenas itens com cotação
            itensFinaisParaPedido = todosItensSelecionados.stream()
                    .filter(idsItensComCotacaoValida::contains)
                    .collect(Collectors.toList());
        } else {
            itensFinaisParaPedido = new ArrayList<>(todosItensSelecionados);
        }

        if (itensFinaisParaPedido.isEmpty()) {
            throw new IllegalArgumentException("Nenhum dos itens selecionados possui uma cotação válida mapeada. Não é possível criar um pedido sem itens cotados.");
        }

        // Criar novo pedido
        SolicitacaoDePedido pedido = new SolicitacaoDePedido();
        pedido.setObservacao(rascunho.getObservacao());
        pedido.setObjetivo(rascunho.getObjetivo()); // Copiar objetivo do rascunho
        pedido.setStatus(StatusPedido.EM_NEGOCIACAO); // Inicia em negociação com fornecedores
        pedido.setItens(new HashSet<>());

        // Converter apenas itens que têm cotação para itens do pedido
        Map<Long, ItemPedido> mapaItens = new HashMap<>();
        List<ItemRascunho> itensParaConverter = rascunho.getItens().stream()
                .filter(item -> itensFinaisParaPedido.contains(item.getId()))
                .collect(Collectors.toList());

        for (ItemRascunho itemRascunho : itensParaConverter) {
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setNome(itemRascunho.getNome());
            itemPedido.setQuantidade(itemRascunho.getQuantidade());
            itemPedido.setDescricao(itemRascunho.getDescricao());
            itemPedido.setObservacao(itemRascunho.getObservacao());
            itemPedido.setTipo(itemRascunho.getTipo()); // Copiar tipo do item
            itemPedido.setSolicitacaoDePedido(pedido);
            pedido.getItens().add(itemPedido);
            mapaItens.put(itemRascunho.getId(), itemPedido);
        }

        // Salvar pedido
        SolicitacaoDePedido pedidoSalvo = solicitacaoDePedidoRepository.save(pedido);

        // Converter cotações do rascunho para cotações do pedido.
        // Regra: cotações não selecionadas são migradas como REJEITADA.
        Set<Long> cotacoesSelecionadasIds = dto.cotacaoParaItens().keySet();
        if (cotacoesSelecionadasIds.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma cotação selecionada para conversão");
        }

        Set<Long> idsCotacoesDoRascunho = cotacoesRascunho.stream()
                .map(CotacaoRascunho::getId)
                .collect(Collectors.toSet());

        List<Long> cotacoesInvalidas = cotacoesSelecionadasIds.stream()
                .filter(id -> !idsCotacoesDoRascunho.contains(id))
                .toList();
        if (!cotacoesInvalidas.isEmpty()) {
            throw new IllegalArgumentException("Cotação(ões) não pertencem ao rascunho " + rascunhoId + ": " + cotacoesInvalidas);
        }

        int cotacoesConvertidas = 0;
        User usuario = getUsuarioAutenticado();

        for (CotacaoRascunho cotacaoRascunho : cotacoesRascunho) {
            boolean cotacaoFoiSelecionada = cotacoesSelecionadasIds.contains(cotacaoRascunho.getId());

            Map<Long, CotacaoRascunhoItem> itensCotados = cotacaoRascunho.getItens().stream()
                .filter(item -> item.getItemRascunho() != null)
                .collect(Collectors.toMap(item -> item.getItemRascunho().getId(), item -> item));

            List<Long> itensSelecionados = cotacaoFoiSelecionada
                ? dto.cotacaoParaItens().getOrDefault(cotacaoRascunho.getId(), Collections.emptyList())
                : Collections.emptyList();

            if (cotacaoFoiSelecionada && itensSelecionados.isEmpty()) {
                throw new IllegalArgumentException("Cotação " + cotacaoRascunho.getId() + " foi selecionada sem itens");
            }
            List<Long> itensInvalidos = itensSelecionados.stream()
                .filter(itemId -> !itensCotados.containsKey(itemId))
                .toList();
            if (!itensInvalidos.isEmpty()) {
                throw new IllegalArgumentException("Itens selecionados nao pertencem a cotacao " + cotacaoRascunho.getId() + ": " + itensInvalidos);
            }

            int totalCotados = itensCotados.size();
            int totalSelecionados = itensSelecionados.size();
            StatusCotacao status = totalSelecionados == 0
                ? StatusCotacao.REJEITADA
                : (totalSelecionados == totalCotados ? StatusCotacao.APROVADA : StatusCotacao.PARCIAL);

            Cotacao cotacao = new Cotacao();
            cotacao.setSolicitacaoDePedido(pedidoSalvo);
            cotacao.setFornecedorProduto(cotacaoRascunho.getFornecedorProduto());
            cotacao.setFornecedorServico(cotacaoRascunho.getFornecedorServico());
            cotacao.setPrazoEmDiasUteis(cotacaoRascunho.getPrazoEmDiasUteis());
            cotacao.setDataLimite(cotacaoRascunho.getDataLimite());
            cotacao.setGastoPrevisto(cotacaoRascunho.getGastoPrevisto());
            cotacao.setProjeto(cotacaoRascunho.getProjeto());
            cotacao.setStatus(status);
            if (!cotacaoFoiSelecionada) {
                // Preserva o valor cotado original para não exibir preço zerado.
                cotacao.setPrecoLegacy(cotacaoRascunho.getPreco());
            }

            for (Long itemId : itensSelecionados) {
                ItemPedido itemPedido = mapaItens.get(itemId);
                if (itemPedido == null) {
                    throw new IllegalArgumentException("Item selecionado nao encontrado no pedido: " + itemId);
                }
                CotacaoRascunhoItem itemCotado = itensCotados.get(itemId);
                com.gestordecompras.gestorcomprasbackend.model.cotacao.CotacaoItem cotacaoItem =
                    new com.gestordecompras.gestorcomprasbackend.model.cotacao.CotacaoItem();
                cotacaoItem.setItemPedido(itemPedido);
                cotacaoItem.setPrecoUnitario(itemCotado.getPrecoUnitario());
                cotacaoItem.setQuantidade(itemCotado.getQuantidade());
                cotacaoItem.setObservacao(itemCotado.getObservacao());
                cotacao.addItem(cotacaoItem);
            }

            if (cotacaoRascunho.getAnexos() != null && !cotacaoRascunho.getAnexos().isEmpty()) {
                for (AnexoCotacaoRascunho anexoRascunho : cotacaoRascunho.getAnexos()) {
                    AnexoCotacao anexoCotacao = pdfDeduplicationService
                        .convertRascunhoAnexoWithDeduplication(cotacao, anexoRascunho);
                    cotacao.getAnexos().add(anexoCotacao);
                }
            }

            Cotacao cotacaoSalva = cotacaoRepository.save(cotacao);
            registrarHistoricoConversao(cotacaoSalva, cotacaoRascunho, itensSelecionados, itensCotados, usuario);
            cotacoesConvertidas++;
        }

        // Bug Fix #3: Avisar se havia cotações no rascunho mas nenhuma foi migrada
        if (!cotacoesRascunho.isEmpty() && cotacoesConvertidas == 0) {
            log.warn("ATENÇÃO: Rascunho {} tinha {} cotação(ões) mas nenhuma foi convertida para o pedido {}. " +
                    "Possível perda de dados durante conversão.",
                    rascunhoId, cotacoesRascunho.size(), pedidoSalvo.getId());
        } else if (cotacoesConvertidas < cotacoesRascunho.size()) {
            log.warn("Rascunho {} tinha {} cotação(ões) mas apenas {} foram convertidas para o pedido {}.",
                    rascunhoId, cotacoesRascunho.size(), cotacoesConvertidas, pedidoSalvo.getId());
        }

        // Registrar no histórico do pedido
        historicoPedidoService.registrarCriacao(pedidoSalvo, usuario);

        // Registrar no histórico do rascunho
        historicoRascunhoService.registrarConversaoPedido(rascunho, usuario, pedidoSalvo.getId());

        // Marcar o rascunho como FINALIZADO ao invés de excluir
        rascunho.setStatus(StatusRascunho.FINALIZADO);
        rascunho.setPedidoGeradoId(pedidoSalvo.getId());
        rascunhoRepository.save(rascunho);

        return solicitacaoDePedidoMapper.toDTO(pedidoSalvo);
    }

    private void registrarHistoricoConversao(
        Cotacao cotacao,
        CotacaoRascunho cotacaoRascunho,
        List<Long> itensSelecionados,
        Map<Long, CotacaoRascunhoItem> itensCotados,
        User usuario
    ) {
        HistoricoCotacao historico = new HistoricoCotacao();
        historico.setCotacaoId(cotacao.getId());
        historico.setVersao(cotacao.getNumeroVersao() != null ? cotacao.getNumeroVersao() : 1);
        historico.setPrecoAnterior(cotacao.getPreco());
        historico.setPrecoNovo(cotacao.getPreco());
        historico.setPrazoEmDiasUteisAnterior(cotacao.getPrazoEmDiasUteis());
        historico.setPrazoEmDiasUteisNovo(cotacao.getPrazoEmDiasUteis());
        historico.setDataLimiteAnterior(cotacao.getDataLimite());
        historico.setDataLimiteNovo(cotacao.getDataLimite());
        historico.setMotivoEdicao("Conversao de rascunho");
        historico.setEditadoPor(usuario != null ? usuario.getNome() : "Sistema");
        historico.setStatusFinal(cotacao.getStatus());

        List<String> selecionados = itensSelecionados.stream()
            .map(itensCotados::get)
            .filter(Objects::nonNull)
            .map(item -> item.getItemRascunho().getNome())
            .toList();

        List<String> naoSelecionados = itensCotados.values().stream()
            .filter(item -> item.getItemRascunho() != null && !itensSelecionados.contains(item.getItemRascunho().getId()))
            .map(item -> item.getItemRascunho().getNome())
            .toList();

        historico.setItensSelecionados(String.join(", ", selecionados));
        historico.setItensNaoSelecionados(String.join(", ", naoSelecionados));

        // Snapshot completo dos itens cotados (inclui selecionados e nao selecionados) com preco por item.
        // Isso permite exibir no frontend os detalhes dos itens "cotados nao selecionados".
        try {
            List<Map<String, Object>> itensSnapshot = itensCotados.values().stream()
                .filter(item -> item.getItemRascunho() != null)
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    Long itemRascunhoId = item.getItemRascunho().getId();
                    boolean selecionado = itemRascunhoId != null && itensSelecionados.contains(itemRascunhoId);

                    map.put("itemRascunhoId", itemRascunhoId);
                    map.put("nomeItem", item.getItemRascunho().getNome());
                    map.put("quantidade", item.getQuantidade());
                    map.put("precoUnitario", item.getPrecoUnitario());
                    map.put("total", item.calcularPrecoTotal());
                    map.put("observacao", item.getObservacao());
                    map.put("statusItem", selecionado ? "APROVADO" : "COTADO_NAO_SELECIONADO");

                    return map;
                })
                .toList();

            historico.setItensNovos(objectMapper.writeValueAsString(itensSnapshot));
        } catch (JsonProcessingException e) {
            log.warn("Nao foi possivel serializar snapshot de itens da conversao para historico da cotacao {}: {}",
                cotacao.getId(), e.getMessage());
        }

        historicoCotacaoRepository.save(historico);
    }

    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        String email = authentication.getName();

        // Verificar se é usuário anônimo
        if ("anonymousUser".equals(email)) {
            throw new IllegalStateException("Usuário não autenticado - acesso anônimo não permitido para esta operação");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + email));
        return user;
    }
}

