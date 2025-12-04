package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.config.ApiVersionConfig;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.CotacaoRascunhoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.CotacaoRascunhoDTO;
import com.gestordecompras.gestorcomprasbackend.service.CotacaoRascunhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/rascunhos/{rascunhoId}/cotacoes")
@Tag(name = "Cotações de Rascunho", description = "API para gerenciamento de cotações em rascunhos de pedidos (v1)")
@SecurityRequirement(name = "bearerAuth")
public class CotacaoRascunhoController {

    @Autowired
    private CotacaoRascunhoService cotacaoRascunhoService;

    @GetMapping
    @Operation(
        summary = "Listar cotações do rascunho",
        description = "Retorna todas as cotações associadas a um rascunho específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de cotações retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Rascunho não encontrado")
    })
    public ResponseEntity<List<CotacaoRascunhoDTO>> listarPorRascunho(
            @Parameter(description = "ID do rascunho", required = true)
            @PathVariable Long rascunhoId) {
        List<CotacaoRascunhoDTO> cotacoes = cotacaoRascunhoService.listarPorRascunho(rascunhoId);
        return ResponseEntity.ok(cotacoes);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar cotação por ID",
        description = "Retorna uma cotação específica de um rascunho"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cotação encontrada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cotação ou rascunho não encontrado")
    })
    public ResponseEntity<CotacaoRascunhoDTO> obterPorId(
            @Parameter(description = "ID do rascunho", required = true)
            @PathVariable Long rascunhoId,
            @Parameter(description = "ID da cotação", required = true)
            @PathVariable Long id) {
        CotacaoRascunhoDTO cotacao = cotacaoRascunhoService.obterPorId(id);
        return ResponseEntity.ok(cotacao);
    }

    @PostMapping
    @Operation(
        summary = "Criar nova cotação no rascunho",
        description = "Adiciona uma nova cotação a um rascunho existente. Suporta múltiplos anexos PDF."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cotação criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
        @ApiResponse(responseCode = "404", description = "Rascunho não encontrado")
    })
    public ResponseEntity<CotacaoRascunhoDTO> criar(
            @Parameter(description = "ID do rascunho", required = true)
            @PathVariable Long rascunhoId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados da cotação a ser criada",
                required = true,
                content = @Content(schema = @Schema(implementation = CotacaoRascunhoCreateDTO.class))
            )
            @Valid @RequestBody CotacaoRascunhoCreateDTO dto) {
        CotacaoRascunhoDTO cotacao = cotacaoRascunhoService.criar(rascunhoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cotacao);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir cotação do rascunho",
        description = "Remove uma cotação de um rascunho"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cotação excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cotação ou rascunho não encontrado")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do rascunho", required = true)
            @PathVariable Long rascunhoId,
            @Parameter(description = "ID da cotação", required = true)
            @PathVariable Long id) {
        cotacaoRascunhoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/anexo")
    @Operation(
        summary = "Obter primeiro anexo PDF da cotação",
        description = "Retorna o primeiro anexo PDF da cotação em formato binário"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "PDF retornado com sucesso",
            content = @Content(mediaType = "application/pdf")
        ),
        @ApiResponse(responseCode = "404", description = "Cotação ou anexo não encontrado")
    })
    public ResponseEntity<byte[]> obterAnexoPdf(
            @Parameter(description = "ID do rascunho", required = true)
            @PathVariable Long rascunhoId,
            @Parameter(description = "ID da cotação", required = true)
            @PathVariable Long id) {
        byte[] pdf = cotacaoRascunhoService.obterAnexoPdf(id);
        if (pdf == null || pdf.length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "cotacao-" + id + ".pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @GetMapping("/{id}/anexo/{index}")
    @Operation(
        summary = "Obter anexo PDF específico por índice",
        description = "Retorna um anexo PDF específico da cotação pelo seu índice (0-based)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "PDF retornado com sucesso",
            content = @Content(mediaType = "application/pdf")
        ),
        @ApiResponse(responseCode = "404", description = "Cotação ou anexo não encontrado no índice especificado")
    })
    public ResponseEntity<byte[]> obterAnexoPdfPorIndice(
            @Parameter(description = "ID do rascunho", required = true)
            @PathVariable Long rascunhoId,
            @Parameter(description = "ID da cotação", required = true)
            @PathVariable Long id,
            @Parameter(description = "Índice do anexo (começando em 0)", required = true)
            @PathVariable int index) {
        byte[] pdf = cotacaoRascunhoService.obterAnexoPdf(id, index);
        if (pdf == null || pdf.length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "cotacao-" + id + "-" + index + ".pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
