package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.dto.itempedido.ItemPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.service.ItemPedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    @Operation(summary = "Listar todos os itens de pedido", description = "Retorna uma lista com todos os itens de pedido cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de itens retornada com sucesso")
    public ResponseEntity<List<ItemPedidoDTO>> getAllItens() {
        return ResponseEntity.ok(itemPedidoService.getAllItens());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar item de pedido por ID", description = "Retorna um item de pedido específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<ItemPedidoDTO> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemPedidoService.getItemById(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo item de pedido", description = "Cria um novo item de pedido com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<ItemPedidoDTO> createItem(@RequestBody @Valid ItemPedidoDTO itemPedidoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemPedidoService.createItem(itemPedidoDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar item de pedido", description = "Atualiza os dados de um item de pedido existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<ItemPedidoDTO> updateItem(@PathVariable Long id, @RequestBody @Valid ItemPedidoDTO itemPedidoDTO) {
        return ResponseEntity.ok(itemPedidoService.updateItem(id, itemPedidoDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir item de pedido", description = "Remove um item de pedido pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemPedidoService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}