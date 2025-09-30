package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.itempedido.ItemPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.ItemPedidoMapper;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.repository.ItemPedidoRepository;
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
                .orElse(null);
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
                .orElse(null);
    }

    public void deleteItem(Long id) {
        itemPedidoRepository.deleteById(id);
    }

    public List<ItemPedido> findAllEntities() {
        return itemPedidoRepository.findAll();
    }

    public ItemPedido findEntityById(Long id) {
        return itemPedidoRepository.findById(id).orElse(null);
    }
}