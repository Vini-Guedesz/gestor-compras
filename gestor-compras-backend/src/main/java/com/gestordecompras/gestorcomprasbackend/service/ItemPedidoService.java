package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.itempedido.ItemPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.ItemPedidoMapper;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.repository.ItemPedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelo gerenciamento de itens de pedido.
 * <p>
 * Oferece funcionalidades para criar, recuperar, atualizar e remover itens de pedidos,
 * além de métodos utilitários para acesso direto a entidades quando necessário.
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
 */
@Service
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;
    private final ItemPedidoMapper itemPedidoMapper;

    /**
     * Construtor com injeção de dependências.
     *
     * @param itemPedidoRepository Repositório de itens de pedido.
     * @param itemPedidoMapper Mapper para conversão entre entidade e DTO.
     */
    public ItemPedidoService(ItemPedidoRepository itemPedidoRepository, ItemPedidoMapper itemPedidoMapper) {
        this.itemPedidoRepository = itemPedidoRepository;
        this.itemPedidoMapper = itemPedidoMapper;
    }

    /**
     * Recupera todos os itens de pedido cadastrados como DTOs com paginação.
     *
     * @param pageable Parâmetros de paginação e ordenação.
     * @return Página de DTOs representando os itens.
     */
    public Page<ItemPedidoDTO> getAllItens(Pageable pageable) {
        return itemPedidoRepository.findAll(pageable)
                .map(itemPedidoMapper::toDTO);
    }

    /**
     * Recupera todos os itens de pedido cadastrados como DTOs (sem paginação).
     * <p>
     * <b>Deprecated:</b> Use {@link #getAllItens(Pageable)} para melhor performance.
     * Mantido para compatibilidade com código legado.
     * </p>
     *
     * @return Lista de DTOs representando todos os itens.
     * @deprecated Use {@link #getAllItens(Pageable)} para evitar sobrecarga de memória.
     */
    @Deprecated
    public List<ItemPedidoDTO> getAllItens() {
        return itemPedidoRepository.findAll().stream()
                .map(itemPedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um item de pedido pelo ID.
     *
     * @param id Identificador único do item.
     * @return DTO do item encontrado.
     * @throws EntityNotFoundException Se o item não for encontrado.
     */
    public ItemPedidoDTO getItemById(Long id) {
        return itemPedidoRepository.findById(id)
                .map(itemPedidoMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Item de pedido não encontrado com ID: " + id));
    }

    /**
     * Cria um novo item de pedido.
     *
     * @param itemPedidoDTO DTO com os dados do novo item.
     * @return DTO do item criado e persistido.
     */
    public ItemPedidoDTO createItem(ItemPedidoDTO itemPedidoDTO) {
        ItemPedido itemPedido = itemPedidoMapper.toEntity(itemPedidoDTO);
        return itemPedidoMapper.toDTO(itemPedidoRepository.save(itemPedido));
    }

    /**
     * Atualiza um item de pedido existente.
     *
     * @param id Identificador do item a ser atualizado.
     * @param itemPedidoDTO DTO com os novos dados do item.
     * @return DTO do item atualizado.
     * @throws EntityNotFoundException Se o item não for encontrado.
     */
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

    /**
     * Exclui um item de pedido pelo ID.
     *
     * @param id Identificador único do item a ser excluído.
     * @throws EntityNotFoundException Se o item não for encontrado.
     */
    public void deleteItem(Long id) {
        if (!itemPedidoRepository.existsById(id)) {
            throw new EntityNotFoundException("Item de pedido não encontrado com ID: " + id);
        }
        itemPedidoRepository.deleteById(id);
    }

    /**
     * Recupera todos os itens de pedido diretamente como entidades.
     * <p>
     * Use com cautela, preferencialmente para operações internas que necessitem das entidades completas.
     * </p>
     *
     * @return Lista de entidades ItemPedido.
     */
    public List<ItemPedido> getAllItensEntities() {
        return itemPedidoRepository.findAll();
    }

    /**
     * Busca um item de pedido pelo ID retornando a entidade.
     *
     * @param id Identificador único do item.
     * @return A entidade ItemPedido encontrada.
     * @throws EntityNotFoundException Se o item não for encontrado.
     */
    public ItemPedido getItemEntityById(Long id) {
        return itemPedidoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Item de pedido não encontrado com ID: " + id));
    }
}