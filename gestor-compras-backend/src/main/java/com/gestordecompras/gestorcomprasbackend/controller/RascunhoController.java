package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.dto.rascunho.*;
import com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido.SolicitacaoDePedidoDTO;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.HistoricoRascunho;
import com.gestordecompras.gestorcomprasbackend.service.HistoricoRascunhoService;
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
    private final HistoricoRascunhoService historicoRascunhoService;

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

    // Endpoints para gerenciamento individual de itens

    @PostMapping("/{id}/itens")
    @Operation(summary = "Adicionar item ao rascunho", description = "Adiciona um novo item ao rascunho e salva imediatamente")
    public ResponseEntity<RascunhoDTO> adicionarItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemRascunhoCreateDTO itemDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rascunhoService.adicionarItem(id, itemDTO));
    }

    @PutMapping("/{id}/itens/{itemId}")
    @Operation(summary = "Atualizar item do rascunho", description = "Atualiza um item existente do rascunho")
    public ResponseEntity<RascunhoDTO> atualizarItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @Valid @RequestBody ItemRascunhoUpdateDTO itemDTO
    ) {
        return ResponseEntity.ok(rascunhoService.atualizarItem(id, itemId, itemDTO));
    }

    @DeleteMapping("/{id}/itens/{itemId}")
    @Operation(summary = "Remover item do rascunho", description = "Remove um item do rascunho e libera o número para reutilização")
    public ResponseEntity<RascunhoDTO> removerItem(
            @PathVariable Long id,
            @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(rascunhoService.removerItem(id, itemId));
    }

    @GetMapping("/{id}/historico")
    @Operation(summary = "Listar histórico do rascunho", description = "Retorna todas as ações realizadas no rascunho")
    public ResponseEntity<List<HistoricoRascunhoDTO>> listarHistorico(@PathVariable Long id) {
        List<HistoricoRascunho> historico = historicoRascunhoService.listarPorRascunho(id);
        List<HistoricoRascunhoDTO> historicoDTO = historico.stream()
                .map(h -> new HistoricoRascunhoDTO(
                        h.getId(),
                        h.getRascunho().getId(),
                        h.getUsuario().getId(),
                        h.getUsuario().getUsername(),
                        h.getDataModificacao(),
                        h.getTipoAcao().name(),
                        h.getDescricao(),
                        h.getNumeroItem(),
                        h.getNomeItem(),
                        h.getDetalhes()
                ))
                .toList();
        return ResponseEntity.ok(historicoDTO);
    }
}
