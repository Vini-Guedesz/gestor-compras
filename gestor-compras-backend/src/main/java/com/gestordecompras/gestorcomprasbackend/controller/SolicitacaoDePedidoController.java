package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido.SolicitacaoDePedidoDTO;
import com.gestordecompras.gestorcomprasbackend.service.SolicitacaoDePedidoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ResponseEntity<List<SolicitacaoDePedidoDTO>> getAllSolicitacoes() {
        return ResponseEntity.ok(solicitacaoDePedidoService.getAllSolicitacoes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoDePedidoDTO> getSolicitacaoById(@PathVariable Long id) {
        SolicitacaoDePedidoDTO solicitacao = solicitacaoDePedidoService.getSolicitacaoById(id);
        return solicitacao != null ? ResponseEntity.ok(solicitacao) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<SolicitacaoDePedidoDTO> createSolicitacao(@RequestBody SolicitacaoDePedidoDTO solicitacaoDePedidoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitacaoDePedidoService.createSolicitacao(solicitacaoDePedidoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolicitacaoDePedidoDTO> updateSolicitacao(@PathVariable Long id, @RequestBody SolicitacaoDePedidoDTO solicitacaoDePedidoDTO) {
        SolicitacaoDePedidoDTO updatedSolicitacao = solicitacaoDePedidoService.updateSolicitacao(id, solicitacaoDePedidoDTO);
        return updatedSolicitacao != null ? ResponseEntity.ok(updatedSolicitacao) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSolicitacao(@PathVariable Long id) {
        solicitacaoDePedidoService.deleteSolicitacao(id);
        return ResponseEntity.noContent().build();
    }
}