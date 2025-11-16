package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.dto.rascunho.ConverterRascunhoParaPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.RascunhoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.RascunhoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.RascunhoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido.SolicitacaoDePedidoDTO;
import com.gestordecompras.gestorcomprasbackend.service.RascunhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rascunhos")
@RequiredArgsConstructor
@Tag(name = "Rascunhos", description = "Gerenciamento de rascunhos de pedidos")
public class RascunhoController {

    private final RascunhoService rascunhoService;

    @GetMapping
    @Operation(summary = "Listar todos os rascunhos")
    public ResponseEntity<List<RascunhoDTO>> getAllRascunhos() {
        return ResponseEntity.ok(rascunhoService.getAllRascunhos());
    }

    @GetMapping("/usuario/{userId}")
    @Operation(summary = "Listar rascunhos por usuário")
    public ResponseEntity<List<RascunhoDTO>> getRascunhosPorUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(rascunhoService.getRascunhosPorUsuario(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar rascunho por ID")
    public ResponseEntity<RascunhoDTO> getRascunhoById(@PathVariable Long id) {
        return ResponseEntity.ok(rascunhoService.getRascunhoById(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo rascunho")
    public ResponseEntity<RascunhoDTO> createRascunho(@Valid @RequestBody RascunhoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rascunhoService.createRascunho(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar rascunho existente")
    public ResponseEntity<RascunhoDTO> updateRascunho(
            @PathVariable Long id,
            @Valid @RequestBody RascunhoUpdateDTO dto
    ) {
        return ResponseEntity.ok(rascunhoService.updateRascunho(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar rascunho")
    public ResponseEntity<Void> deleteRascunho(@PathVariable Long id) {
        rascunhoService.deleteRascunho(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/converter-para-pedido")
    @Operation(summary = "Converter rascunho em pedido", description = "Converte itens selecionados do rascunho em uma solicitação de pedido")
    public ResponseEntity<SolicitacaoDePedidoDTO> converterParaPedido(
            @PathVariable Long id,
            @Valid @RequestBody ConverterRascunhoParaPedidoDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rascunhoService.converterRascunhoParaPedido(id, dto));
    }
}
