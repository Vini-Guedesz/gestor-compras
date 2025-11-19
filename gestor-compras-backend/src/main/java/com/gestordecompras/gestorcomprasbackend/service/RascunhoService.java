package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.rascunho.ConverterRascunhoParaPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.RascunhoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.RascunhoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.RascunhoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido.SolicitacaoDePedidoDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.RascunhoMapper;
import com.gestordecompras.gestorcomprasbackend.model.pedido.HistoricoPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.StatusPedido;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.ItemRascunho;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.Rascunho;
import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.repository.ItemRascunhoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.RascunhoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.SolicitacaoDePedidoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
        Rascunho rascunho = rascunhoMapper.toEntity(dto);

        // Obter usuário autenticado
        User criador = getUsuarioAutenticado();
        rascunho.setCriador(criador);

        // Associar itens ao rascunho
        if (rascunho.getItens() != null && !rascunho.getItens().isEmpty()) {
            rascunho.getItens().forEach(item -> item.setRascunho(rascunho));
        }

        Rascunho rascunhoSalvo = rascunhoRepository.save(rascunho);
        return rascunhoMapper.toDTO(rascunhoSalvo);
    }

    @Transactional
    public RascunhoDTO updateRascunho(Long id, RascunhoUpdateDTO dto) {
        Rascunho rascunho = rascunhoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rascunho não encontrado com ID: " + id));

        rascunho.setObservacao(dto.observacao());

        // Atualizar itens
        if (dto.itens() != null) {
            // Remover itens existentes
            rascunho.getItens().clear();

            // Adicionar novos itens
            dto.itens().forEach(itemDTO -> {
                ItemRascunho item = new ItemRascunho();
                item.setNome(itemDTO.nome());
                item.setQuantidade(itemDTO.quantidade());
                item.setDescricao(itemDTO.descricao());
                item.setObservacao(itemDTO.observacao());
                item.setRascunho(rascunho);
                rascunho.getItens().add(item);
            });
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

        // Criar novo pedido
        SolicitacaoDePedido pedido = new SolicitacaoDePedido();
        pedido.setObservacao(rascunho.getObservacao());
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setItens(new ArrayList<>());

        // Converter itens selecionados do rascunho para itens do pedido
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
        }

        // Salvar pedido
        SolicitacaoDePedido pedidoSalvo = solicitacaoDePedidoRepository.save(pedido);

        // Registrar no histórico
        User usuario = getUsuarioAutenticado();
        historicoPedidoService.registrarCriacao(pedidoSalvo, usuario);

        // Converter para DTO
        return convertToDTO(pedidoSalvo);
    }

    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new EntityNotFoundException("Usuário não encontrado: " + username);
        }
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
