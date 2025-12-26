package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.rascunho.*;
import com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido.SolicitacaoDePedidoDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.RascunhoMapper;
import com.gestordecompras.gestorcomprasbackend.mapper.SolicitacaoDePedidoMapper;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.StatusPedido;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.*;
import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.model.user.UserRole;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.AnexoCotacao;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
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
    private final NumeroItemDisponivelRepository numeroItemDisponivelRepository;
    private final PdfDeduplicationService pdfDeduplicationService;

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

        // Validar que todos os itens selecionados possuem pelo menos uma cotação
        Set<Long> itensCotados = new HashSet<>();
        for (CotacaoRascunho cotacao : cotacoesRascunho) {
            for (ItemRascunho item : cotacao.getItensRascunho()) {
                itensCotados.add(item.getId());
            }
        }

        List<Long> itensSemCotacao = todosItensSelecionados.stream()
                .filter(itemId -> !itensCotados.contains(itemId))
                .collect(Collectors.toList());

        if (!itensSemCotacao.isEmpty()) {
            throw new IllegalArgumentException("Os seguintes itens não possuem cotação: " + itensSemCotacao + ". Apenas itens cotados podem ser convertidos em pedido.");
        }

        // Criar novo pedido
        SolicitacaoDePedido pedido = new SolicitacaoDePedido();
        pedido.setObservacao(rascunho.getObservacao());
        pedido.setObjetivo(rascunho.getObjetivo()); // Copiar objetivo do rascunho
        pedido.setStatus(StatusPedido.EM_NEGOCIACAO); // Inicia em negociação com fornecedores
        pedido.setItens(new ArrayList<>());

        // Converter itens selecionados do rascunho para itens do pedido
        Map<Long, ItemPedido> mapaItens = new HashMap<>();
        List<ItemRascunho> itensSelecionados = rascunho.getItens().stream()
                .filter(item -> todosItensSelecionados.contains(item.getId()))
                .collect(Collectors.toList());

        for (ItemRascunho itemRascunho : itensSelecionados) {
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setNome(itemRascunho.getNome());
            itemPedido.setQuantidade(itemRascunho.getQuantidade());
            itemPedido.setDescricao(itemRascunho.getDescricao());
            itemPedido.setObservacao(itemRascunho.getObservacao());
            itemPedido.setSolicitacaoDePedido(pedido);
            pedido.getItens().add(itemPedido);
            mapaItens.put(itemRascunho.getId(), itemPedido);
        }

        // Salvar pedido
        SolicitacaoDePedido pedidoSalvo = solicitacaoDePedidoRepository.save(pedido);

        // Bug Fix #3: Converter cotações do rascunho para cotações do pedido com logging
        int cotacoesConvertidas = 0;
        for (CotacaoRascunho cotacaoRascunho : cotacoesRascunho) {
            Set<ItemPedido> itensPedidoCotacao = new HashSet<>();

            // NOVO: Usar mapeamento específico se disponível
            if (dto.usaNovoPadrao() && dto.cotacaoParaItens().containsKey(cotacaoRascunho.getId())) {
                List<Long> itensDestaCotacao = dto.cotacaoParaItens().get(cotacaoRascunho.getId());
                for (Long itemId : itensDestaCotacao) {
                    if (mapaItens.containsKey(itemId)) {
                        itensPedidoCotacao.add(mapaItens.get(itemId));
                    }
                }
            } else {
                // LEGADO: Compatibilidade - adicionar todos os itens selecionados que estão na cotação
                for (ItemRascunho itemRascunho : cotacaoRascunho.getItensRascunho()) {
                    if (mapaItens.containsKey(itemRascunho.getId())) {
                        itensPedidoCotacao.add(mapaItens.get(itemRascunho.getId()));
                    }
                }
            }

            if (!itensPedidoCotacao.isEmpty()) {
                log.info("DEBUG: Convertendo cotação rascunho {} - gastoPrevisto: {}, projeto: {}",
                        cotacaoRascunho.getId(), cotacaoRascunho.getGastoPrevisto(), cotacaoRascunho.getProjeto());

                Cotacao cotacao = new Cotacao();
                cotacao.setSolicitacaoDePedido(pedidoSalvo);
                cotacao.setFornecedorProduto(cotacaoRascunho.getFornecedorProduto());
                cotacao.setFornecedorServico(cotacaoRascunho.getFornecedorServico());
                cotacao.setPreco(cotacaoRascunho.getPreco()); // Legacy - será ignorado, calculado dos itens
                cotacao.setPrazoEmDiasUteis(cotacaoRascunho.getPrazoEmDiasUteis());
                cotacao.setDataLimite(cotacaoRascunho.getDataLimite());
                cotacao.setGastoPrevisto(cotacaoRascunho.getGastoPrevisto());
                cotacao.setProjeto(cotacaoRascunho.getProjeto());

                log.info("DEBUG: Cotação pedido criada - gastoPrevisto: {}, projeto: {}",
                        cotacao.getGastoPrevisto(), cotacao.getProjeto());

                // REMOVIDO: anexoPdf e caminhoAnexo (campos legados) - gerenciados via AnexoCotacao com deduplificação

                // Criar CotacaoItem para cada ItemPedido (nova estrutura Bug #5)
                int totalItens = itensPedidoCotacao.size();
                java.math.BigDecimal precoTotal = cotacaoRascunho.getPreco();
                java.math.BigDecimal precoUnitario = precoTotal.divide(
                        java.math.BigDecimal.valueOf(totalItens),
                        2,
                        java.math.RoundingMode.HALF_UP
                );

                for (ItemPedido itemPedido : itensPedidoCotacao) {
                    com.gestordecompras.gestorcomprasbackend.model.cotacao.CotacaoItem cotacaoItem =
                            new com.gestordecompras.gestorcomprasbackend.model.cotacao.CotacaoItem();
                    cotacaoItem.setItemPedido(itemPedido);
                    cotacaoItem.setPrecoUnitario(precoUnitario);
                    cotacaoItem.setQuantidade(itemPedido.getQuantidade());
                    cotacaoItem.setObservacao("Convertido de rascunho - preço dividido igualmente");
                    cotacao.addItem(cotacaoItem);
                }

                // Copiar anexos múltiplos com deduplificação
                if (cotacaoRascunho.getAnexos() != null && !cotacaoRascunho.getAnexos().isEmpty()) {
                    for (AnexoCotacaoRascunho anexoRascunho : cotacaoRascunho.getAnexos()) {
                        // Usa PdfDeduplicationService para evitar duplicação de PDFs idênticos
                        AnexoCotacao anexoCotacao = pdfDeduplicationService
                            .convertRascunhoAnexoWithDeduplication(cotacao, anexoRascunho);
                        cotacao.getAnexos().add(anexoCotacao);
                    }
                }

                cotacaoRepository.save(cotacao);
                cotacoesConvertidas++;
            } else {
                log.warn("Cotação do rascunho {} não foi convertida pois nenhum item foi mapeado. " +
                        "Fornecedor: {}, Preço: {}",
                        rascunhoId,
                        cotacaoRascunho.getFornecedorProduto() != null
                            ? cotacaoRascunho.getFornecedorProduto().getRazaoSocial()
                            : cotacaoRascunho.getFornecedorServico().getRazaoSocial(),
                        cotacaoRascunho.getPreco());
            }
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
        User usuario = getUsuarioAutenticado();
        historicoPedidoService.registrarCriacao(pedidoSalvo, usuario);

        // Registrar no histórico do rascunho
        historicoRascunhoService.registrarConversaoPedido(rascunho, usuario, pedidoSalvo.getId());

        // Marcar o rascunho como FINALIZADO ao invés de excluir
        rascunho.setStatus(StatusRascunho.FINALIZADO);
        rascunho.setPedidoGeradoId(pedidoSalvo.getId());
        rascunhoRepository.save(rascunho);

        return solicitacaoDePedidoMapper.toDTO(pedidoSalvo);
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
