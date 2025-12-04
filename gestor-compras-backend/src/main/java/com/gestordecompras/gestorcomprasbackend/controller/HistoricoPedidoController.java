package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.config.ApiVersionConfig;
import com.gestordecompras.gestorcomprasbackend.dto.pedido.HistoricoPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.service.HistoricoPedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/historico-pedidos")
@RequiredArgsConstructor
@Tag(name = "Histórico de Pedidos", description = "Consulta histórico de modificações em pedidos (v1)")
public class HistoricoPedidoController {

    private final HistoricoPedidoService historicoPedidoService;

    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Buscar histórico por pedido", description = "Retorna todo o histórico de modificações de um pedido específico")
    public ResponseEntity<List<HistoricoPedidoDTO>> getHistoricoPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(historicoPedidoService.getHistoricoPorPedido(pedidoId));
    }

    @GetMapping("/usuario/{userId}")
    @Operation(summary = "Buscar histórico por usuário", description = "Retorna todas as modificações realizadas por um usuário específico")
    public ResponseEntity<List<HistoricoPedidoDTO>> getHistoricoPorUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(historicoPedidoService.getHistoricoPorUsuario(userId));
    }
}
