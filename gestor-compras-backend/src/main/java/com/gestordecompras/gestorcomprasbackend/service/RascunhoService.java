package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.rascunho.*;
import com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido.SolicitacaoDePedidoDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.RascunhoMapper;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.StatusPedido;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.*;
import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import com.gestordecompras.gestorcomprasbackend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RascunhoService {

    private final RascunhoRepository rascunhoRepository;
    private final ItemRascunhoRepository itemRascunhoRepository;
    private final SolicitacaoDePedidoRepository solicitacaoDePedidoRepository;
    private final UserRepository userRepository;
    private final RascunhoMapper rascunhoMapper;
    private final HistoricoPedidoService historicoPedidoService;
    private final HistoricoRascunhoService historicoRascunhoService;
    private final CotacaoRascunhoRepository cotacaoRascunhoRepository;
    private final CotacaoRepository cotacaoRepository;
    private final NumeroItemDisponivelRepository numeroItemDisponivelRepository;

    @Transactional(readOnly = true)
    public List<RascunhoDTO> getAllRascunhos() {
        return rascunhoRepository.findAll().stream()
                .map(rascunhoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RascunhoDTO> getRascunhosPorUsuario(Long userId) {
        return rascunhoRepository.findAllByCriadorIdWithItens(userId).stream()
                .map(rascunhoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RascunhoDTO getRascunhoById(Long id) {
        Rascunho rascunho = rascunhoRepository.findByIdWithItens(id);
        if (rascunho == null) {
            throw new EntityNotFoundException("Rascunho não encontrado com ID: " + id);
        }
        return rascunhoMapper.toDTO(rascunho);
    }

    @Transactional
    public RascunhoDTO createRascunho(RascunhoCreateDTO dto) {
        User criador = getUsuarioAutenticado();

        Rascunho rascunho = new Rascunho();
        rascunho.setCriador(criador);
        rascunho.setObservacao(dto.observacao());
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

    @Transactional
    public void deleteRascunho(Long id) {
        if (!rascunhoRepository.existsById(id)) {
            throw new EntityNotFoundException("Rascunho não encontrado com ID: " + id);
        }
        rascunhoRepository.deleteById(id);
    }

    @Transactional
    public SolicitacaoDePedidoDTO converterRascunhoParaPedido(Long rascunhoId, ConverterRascunhoParaPedidoDTO dto) {
        Rascunho rascunho = rascunhoRepository.findByIdWithItens(rascunhoId);
        if (rascunho == null) {
            throw new EntityNotFoundException("Rascunho não encontrado com ID: " + rascunhoId);
        }

        // Validar que os IDs dos itens pertencem ao rascunho
        List<Long> idsItensRascunho = rascunho.getItens().stream()
                .map(ItemRascunho::getId)
                .collect(Collectors.toList());

        for (Long itemId : dto.itemRascunhoIds()) {
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

        List<Long> itensSemCotacao = dto.itemRascunhoIds().stream()
                .filter(itemId -> !itensCotados.contains(itemId))
                .collect(Collectors.toList());

        if (!itensSemCotacao.isEmpty()) {
            throw new IllegalArgumentException("Os seguintes itens não possuem cotação: " + itensSemCotacao + ". Apenas itens cotados podem ser convertidos em pedido.");
        }

        // Criar novo pedido
        SolicitacaoDePedido pedido = new SolicitacaoDePedido();
        pedido.setObservacao(rascunho.getObservacao());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setItens(new ArrayList<>());

        // Converter itens selecionados do rascunho para itens do pedido
        Map<Long, ItemPedido> mapaItens = new HashMap<>();
        List<ItemRascunho> itensSelecionados = rascunho.getItens().stream()
                .filter(item -> dto.itemRascunhoIds().contains(item.getId()))
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

        // Converter cotações do rascunho para cotações do pedido
        for (CotacaoRascunho cotacaoRascunho : cotacoesRascunho) {
            Set<ItemPedido> itensPedidoCotacao = new HashSet<>();
            for (ItemRascunho itemRascunho : cotacaoRascunho.getItensRascunho()) {
                if (mapaItens.containsKey(itemRascunho.getId())) {
                    itensPedidoCotacao.add(mapaItens.get(itemRascunho.getId()));
                }
            }

            if (!itensPedidoCotacao.isEmpty()) {
                Cotacao cotacao = new Cotacao();
                cotacao.setSolicitacaoDePedido(pedidoSalvo);
                cotacao.setFornecedorProduto(cotacaoRascunho.getFornecedorProduto());
                cotacao.setFornecedorServico(cotacaoRascunho.getFornecedorServico());
                cotacao.setPreco(cotacaoRascunho.getPreco());
                cotacao.setPrazoEmDiasUteis(cotacaoRascunho.getPrazoEmDiasUteis());
                cotacao.setDataLimite(cotacaoRascunho.getDataLimite());
                cotacao.setAnexoPdf(cotacaoRascunho.getAnexoPdf());
                cotacao.setCaminhoAnexo(cotacaoRascunho.getCaminhoAnexo());
                cotacao.setItensPedido(itensPedidoCotacao);

                cotacaoRepository.save(cotacao);
            }
        }

        // Registrar no histórico do pedido
        User usuario = getUsuarioAutenticado();
        historicoPedidoService.registrarCriacao(pedidoSalvo, usuario);

        // Registrar no histórico do rascunho
        historicoRascunhoService.registrarConversaoPedido(rascunho, usuario, pedidoSalvo.getId());

        return convertToDTO(pedidoSalvo);
    }

    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + email));
        return user;
    }

    private SolicitacaoDePedidoDTO convertToDTO(SolicitacaoDePedido pedido) {
        return new SolicitacaoDePedidoDTO(
                pedido.getId(),
                pedido.getItens().stream()
                        .map(item -> new com.gestordecompras.gestorcomprasbackend.dto.itempedido.ItemPedidoDTO(
                                item.getId(),
                                item.getNome(),
                                item.getQuantidade(),
                                item.getDescricao(),
                                item.getObservacao()
                        ))
                        .collect(Collectors.toList()),
                pedido.getStatus(),
                pedido.getObservacao(),
                pedido.getDataCriacao()
        );
    }
}
