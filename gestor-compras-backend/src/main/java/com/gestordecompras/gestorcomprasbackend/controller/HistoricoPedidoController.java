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

/**
 * Controller REST para consultar Histórico de Pedidos.
 *
 * <p>Oferece endpoints de consulta (somente leitura) para auditoria de modificações
 * em solicitações de pedido. Registra criação, atualizações, mudanças de status, etc.</p>
 *
 * <p><b>Autenticação:</b> Pública (sem JWT) | <b>Roles:</b> Todos</p>
 *
 * @since 1.0.0
 * @see HistoricoPedidoService
 * @see HistoricoPedidoDTO
 */
@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/historico-pedidos")
@RequiredArgsConstructor
@Tag(name = "Histórico de Pedidos", description = "Consulta histórico de modificações em pedidos (v1)")
public class HistoricoPedidoController {

    private final HistoricoPedidoService historicoPedidoService;

    /** Retorna histórico completo de um pedido específico ordenado cronologicamente. */
    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Buscar histórico por pedido", description = "Retorna todo o histórico de modificações de um pedido específico")
    public ResponseEntity<List<HistoricoPedidoDTO>> getHistoricoPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(historicoPedidoService.getHistoricoPorPedido(pedidoId));
    }

    /** Retorna todas as modificações realizadas por um usuário específico. */
    @GetMapping("/usuario/{userId}")
    @Operation(summary = "Buscar histórico por usuário", description = "Retorna todas as modificações realizadas por um usuário específico")
    public ResponseEntity<List<HistoricoPedidoDTO>> getHistoricoPorUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(historicoPedidoService.getHistoricoPorUsuario(userId));
    }
}
