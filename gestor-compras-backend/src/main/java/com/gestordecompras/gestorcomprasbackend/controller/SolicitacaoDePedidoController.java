package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido.SolicitacaoDePedidoDTO;
import com.gestordecompras.gestorcomprasbackend.service.SolicitacaoDePedidoService;
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
@RequestMapping("/api/solicitacoes-pedido")
@Tag(name = "Solicitações de Pedido", description = "API para gerenciamento de solicitações de pedido")
@SecurityRequirement(name = "bearerAuth")
public class SolicitacaoDePedidoController {

    private final SolicitacaoDePedidoService solicitacaoDePedidoService;

    public SolicitacaoDePedidoController(SolicitacaoDePedidoService solicitacaoDePedidoService) {
        this.solicitacaoDePedidoService = solicitacaoDePedidoService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as solicitações de pedido", description = "Retorna uma lista com todas as solicitações de pedido cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de solicitações retornada com sucesso")
    public ResponseEntity<List<SolicitacaoDePedidoDTO>> getAllSolicitacoes() {
        return ResponseEntity.ok(solicitacaoDePedidoService.getAllSolicitacoes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar solicitação de pedido por ID", description = "Retorna uma solicitação de pedido específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada")
    })
    public ResponseEntity<SolicitacaoDePedidoDTO> getSolicitacaoById(@PathVariable Long id) {
        return ResponseEntity.ok(solicitacaoDePedidoService.getSolicitacaoById(id));
    }

    @PostMapping
    @Operation(summary = "Criar nova solicitação de pedido", description = "Cria uma nova solicitação de pedido com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<SolicitacaoDePedidoDTO> createSolicitacao(@RequestBody @Valid SolicitacaoDePedidoDTO solicitacaoDePedidoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitacaoDePedidoService.createSolicitacao(solicitacaoDePedidoDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar solicitação de pedido", description = "Atualiza os dados de uma solicitação de pedido existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada")
    })
    public ResponseEntity<SolicitacaoDePedidoDTO> updateSolicitacao(@PathVariable Long id, @RequestBody @Valid SolicitacaoDePedidoDTO solicitacaoDePedidoDTO) {
        return ResponseEntity.ok(solicitacaoDePedidoService.updateSolicitacao(id, solicitacaoDePedidoDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir solicitação de pedido", description = "Remove uma solicitação de pedido pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Solicitação excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada")
    })
    public ResponseEntity<Void> deleteSolicitacao(@PathVariable Long id) {
        solicitacaoDePedidoService.deleteSolicitacao(id);
        return ResponseEntity.noContent().build();
    }
}