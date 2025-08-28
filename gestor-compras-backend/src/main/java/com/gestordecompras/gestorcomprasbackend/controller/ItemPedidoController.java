package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.dto.itempedido.ItemPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.service.ItemPedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itens-pedido")
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    public ItemPedidoController(ItemPedidoService itemPedidoService) {
        this.itemPedidoService = itemPedidoService;
    }

    @GetMapping
    public ResponseEntity<List<ItemPedidoDTO>> getAllItens() {
        return ResponseEntity.ok(itemPedidoService.getAllItens());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemPedidoDTO> getItemById(@PathVariable Long id) {
        ItemPedidoDTO item = itemPedidoService.getItemById(id);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ItemPedidoDTO> createItem(@RequestBody ItemPedidoDTO itemPedidoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemPedidoService.createItem(itemPedidoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemPedidoDTO> updateItem(@PathVariable Long id, @RequestBody ItemPedidoDTO itemPedidoDTO) {
        ItemPedidoDTO updatedItem = itemPedidoService.updateItem(id, itemPedidoDTO);
        return updatedItem != null ? ResponseEntity.ok(updatedItem) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemPedidoService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}