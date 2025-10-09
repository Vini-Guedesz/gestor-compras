package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.itempedido.ItemPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.ItemPedidoMapper;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.repository.ItemPedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;
    private final ItemPedidoMapper itemPedidoMapper;

    public ItemPedidoService(ItemPedidoRepository itemPedidoRepository, ItemPedidoMapper itemPedidoMapper) {
        this.itemPedidoRepository = itemPedidoRepository;
        this.itemPedidoMapper = itemPedidoMapper;
    }

    public List<ItemPedidoDTO> getAllItens() {
        return itemPedidoRepository.findAll().stream()
                .map(itemPedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ItemPedidoDTO getItemById(Long id) {
        return itemPedidoRepository.findById(id)
                .map(itemPedidoMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Item de pedido não encontrado com ID: " + id));
    }

    public ItemPedidoDTO createItem(ItemPedidoDTO itemPedidoDTO) {
        ItemPedido itemPedido = itemPedidoMapper.toEntity(itemPedidoDTO);
        return itemPedidoMapper.toDTO(itemPedidoRepository.save(itemPedido));
    }

    public ItemPedidoDTO updateItem(Long id, ItemPedidoDTO itemPedidoDTO) {
        return itemPedidoRepository.findById(id)
                .map(itemPedido -> {
                    itemPedido.setNome(itemPedidoDTO.nome());
                    itemPedido.setQuantidade(itemPedidoDTO.quantidade());
                    itemPedido.setDescricao(itemPedidoDTO.descricao());
                    itemPedido.setObservacao(itemPedidoDTO.observacao());
                    return itemPedidoMapper.toDTO(itemPedidoRepository.save(itemPedido));
                })
                .orElseThrow(() -> new EntityNotFoundException("Item de pedido não encontrado com ID: " + id));
    }

    public void deleteItem(Long id) {
        if (!itemPedidoRepository.existsById(id)) {
            throw new EntityNotFoundException("Item de pedido não encontrado com ID: " + id);
        }
        itemPedidoRepository.deleteById(id);
    }

    public List<ItemPedido> getAllItensEntities() {
        return itemPedidoRepository.findAll();
    }

    public ItemPedido getItemEntityById(Long id) {
        return itemPedidoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Item de pedido não encontrado com ID: " + id));
    }
}