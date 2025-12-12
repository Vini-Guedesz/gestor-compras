package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.config.ApiVersionConfig;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.*;
import com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido.SolicitacaoDePedidoDTO;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.HistoricoRascunho;
import com.gestordecompras.gestorcomprasbackend.service.HistoricoRascunhoService;
import com.gestordecompras.gestorcomprasbackend.service.RascunhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciar Rascunhos de Pedidos.
 *
 * <p>Rascunhos são versões preliminares de pedidos que podem ser editadas livremente antes
 * da conversão final. Sistema de numeração de itens com reuso automático de números disponíveis.</p>
 *
 * <p><b>Funcionalidades Especiais:</b></p>
 * <ul>
 *   <li>Reuso inteligente de números de itens removidos</li>
 *   <li>Conversão de rascunho para pedido final com migração de itens e cotações</li>
 *   <li>Gerenciamento individual de itens (adicionar/atualizar/remover)</li>
 *   <li>Histórico completo de todas as modificações</li>
 *   <li>Controle de status: ATIVO → EM_COTACAO → FINALIZADO</li>
 * </ul>
 *
 * <p><b>Autenticação:</b> Pública (sem JWT) | <b>Roles:</b> Todos</p>
 *
 * @since 1.0.0
 * @see RascunhoService
 * @see RascunhoDTO
 * @see HistoricoRascunhoService
 */
@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/rascunhos")
@RequiredArgsConstructor
@Tag(name = "Rascunhos", description = "Gerenciamento de rascunhos de pedidos (v1)")
public class RascunhoController {

    private final RascunhoService rascunhoService;
    private final HistoricoRascunhoService historicoRascunhoService;

    /** Lista todos os rascunhos com paginação (padrão: 20 por página). */
    @GetMapping
    @Operation(summary = "Listar todos os rascunhos")
    public ResponseEntity<Page<RascunhoDTO>> getAllRascunhos(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(rascunhoService.getAllRascunhos(pageable));
    }

    /** Lista rascunhos filtrados por usuário criador. */
    @GetMapping("/usuario/{userId}")
    @Operation(summary = "Listar rascunhos por usuário")
    public ResponseEntity<List<RascunhoDTO>> getRascunhosPorUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(rascunhoService.getRascunhosPorUsuario(userId));
    }

    /** Busca rascunho por ID incluindo todos os itens. */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar rascunho por ID")
    public ResponseEntity<RascunhoDTO> getRascunhoById(@PathVariable Long id) {
        return ResponseEntity.ok(rascunhoService.getRascunhoById(id));
    }

    /** Cria novo rascunho vazio com status ATIVO. */
    @PostMapping
    @Operation(summary = "Criar novo rascunho")
    public ResponseEntity<RascunhoDTO> createRascunho(@Valid @RequestBody RascunhoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rascunhoService.createRascunho(dto));
    }

    /** Atualiza dados gerais do rascunho (observação, etc). */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar rascunho existente")
    public ResponseEntity<RascunhoDTO> updateRascunho(
            @PathVariable Long id,
            @Valid @RequestBody RascunhoUpdateDTO dto
    ) {
        return ResponseEntity.ok(rascunhoService.updateRascunho(id, dto));
    }

    /** Remove rascunho permanentemente. */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar rascunho")
    public ResponseEntity<Void> deleteRascunho(@PathVariable Long id) {
        rascunhoService.deleteRascunho(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Converte rascunho em pedido final.
     *
     * <p>Migra itens e cotações selecionadas para SolicitacaoDePedido.
     * Marca rascunho como FINALIZADO e registra pedidoGeradoId.</p>
     */
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

    /** Adiciona item ao rascunho reutilizando números disponíveis ou gerando próximo sequencial. */
    @PostMapping("/{id}/itens")
    @Operation(summary = "Adicionar item ao rascunho", description = "Adiciona um novo item ao rascunho e salva imediatamente")
    public ResponseEntity<RascunhoDTO> adicionarItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemRascunhoCreateDTO itemDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rascunhoService.adicionarItem(id, itemDTO));
    }

    /** Atualiza item existente do rascunho preservando número. */
    @PutMapping("/{id}/itens/{itemId}")
    @Operation(summary = "Atualizar item do rascunho", description = "Atualiza um item existente do rascunho")
    public ResponseEntity<RascunhoDTO> atualizarItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @Valid @RequestBody ItemRascunhoUpdateDTO itemDTO
    ) {
        return ResponseEntity.ok(rascunhoService.atualizarItem(id, itemId, itemDTO));
    }

    /** Remove item do rascunho liberando o número para reutilização futura. */
    @DeleteMapping("/{id}/itens/{itemId}")
    @Operation(summary = "Remover item do rascunho", description = "Remove um item do rascunho e libera o número para reutilização")
    public ResponseEntity<RascunhoDTO> removerItem(
            @PathVariable Long id,
            @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(rascunhoService.removerItem(id, itemId));
    }

    /** Retorna histórico completo de modificações do rascunho (criação, itens, status). */
    @GetMapping("/{id}/historico")
    @Operation(summary = "Listar histórico do rascunho", description = "Retorna todas as ações realizadas no rascunho")
    public ResponseEntity<List<HistoricoRascunhoDTO>> listarHistorico(@PathVariable Long id) {
        List<HistoricoRascunho> historico = historicoRascunhoService.listarPorRascunho(id);
        List<HistoricoRascunhoDTO> historicoDTO = historico.stream()
                .map(h -> new HistoricoRascunhoDTO(
                        h.getId(),
                        h.getRascunho().getId(),
                        h.getUsuario() != null ? h.getUsuario().getId() : null,
                        h.getUsuario() != null ? h.getUsuario().getUsername() : "Sistema",
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

    /** Atualiza status do rascunho (ATIVO, EM_COTACAO, FINALIZADO). */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do rascunho", description = "Atualiza o status do rascunho (ATIVO, EM_COTACAO, FINALIZADO)")
    public ResponseEntity<RascunhoDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(rascunhoService.atualizarStatus(id, status));
    }
}
