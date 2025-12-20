package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.config.ApiVersionConfig;
import com.gestordecompras.gestorcomprasbackend.dto.itempedido.ItemPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.service.ItemPedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciar Itens de Pedido.
 *
 * <p>Cada item pertence a uma solicitação e contém nome, quantidade e descrição.
 * Normalmente gerenciados através da solicitação pai.</p>
 *
 * <p><b>Autenticação:</b> JWT obrigatório | <b>Roles:</b> USER, ADMIN</p>
 *
 * @since 1.0.0
 * @see ItemPedidoService
 * @see ItemPedidoDTO
 */
@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/itens-pedido")
@Tag(name = "Itens de Pedido", description = "API para gerenciamento de itens de pedido (v1)")
@SecurityRequirement(name = "bearerAuth")
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    /** Construtor com injeção de dependência. */
    public ItemPedidoController(ItemPedidoService itemPedidoService) {
        this.itemPedidoService = itemPedidoService;
    }

    /** Lista todos os itens com paginação para melhor performance. */
    @GetMapping
    @Operation(summary = "Listar itens de pedido com paginação", description = "Retorna uma página com os itens de pedido cadastrados. Padrão: 20 itens por página ordenados por ID.")
    @ApiResponse(responseCode = "200", description = "Página de itens retornada com sucesso")
    public ResponseEntity<Page<ItemPedidoDTO>> getAllItens(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(itemPedidoService.getAllItens(pageable));
    }

    /** Busca item por ID incluindo solicitação associada. */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar item de pedido por ID", description = "Retorna um item de pedido específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<ItemPedidoDTO> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemPedidoService.getItemById(id));
    }

    /** Cria novo item validando quantidade > 0. */
    @PostMapping
    @Operation(summary = "Criar novo item de pedido", description = "Cria um novo item de pedido com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<ItemPedidoDTO> createItem(@RequestBody @Valid ItemPedidoDTO itemPedidoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemPedidoService.createItem(itemPedidoDTO));
    }

    /** Atualiza item existente. */
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

    /** Remove item permanentemente da solicitação. */
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