package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.dto.itempedido.ItemPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.service.ItemPedidoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itens-pedido")
@Tag(name = "Itens de Pedido", description = "API para gerenciamento de itens de pedido")
@SecurityRequirement(name = "bearerAuth")
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
        return ResponseEntity.ok(itemPedidoService.getItemById(id));
    }

    @PostMapping
    public ResponseEntity<ItemPedidoDTO> createItem(@RequestBody ItemPedidoDTO itemPedidoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemPedidoService.createItem(itemPedidoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemPedidoDTO> updateItem(@PathVariable Long id, @RequestBody ItemPedidoDTO itemPedidoDTO) {
        return ResponseEntity.ok(itemPedidoService.updateItem(id, itemPedidoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemPedidoService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}