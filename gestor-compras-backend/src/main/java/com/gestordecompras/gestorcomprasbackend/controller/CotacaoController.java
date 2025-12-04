package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.config.ApiVersionConfig;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoEditDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.HistoricoCotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.service.CotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/cotacoes")
@Tag(name = "Cotações", description = "API para gerenciamento de cotações de fornecedores (v1)")
@SecurityRequirement(name = "bearerAuth")
public class CotacaoController {

    private final CotacaoService cotacaoService;

    public CotacaoController(CotacaoService cotacaoService) {
        this.cotacaoService = cotacaoService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as cotações", description = "Retorna uma lista com todas as cotações cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de cotações retornada com sucesso")
    public ResponseEntity<Page<CotacaoDTO>> getAllCotacoes(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(cotacaoService.getAllCotacoes(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cotação por ID", description = "Retorna uma cotação específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cotação encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cotação não encontrada")
    })
    public ResponseEntity<CotacaoDTO> getCotacaoById(@PathVariable Long id) {
        return ResponseEntity.ok(cotacaoService.getCotacaoById(id));
    }

    @PostMapping
    @Operation(summary = "Criar nova cotação", description = "Cria uma nova cotação com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cotação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<CotacaoDTO> createCotacao(@Valid @RequestBody CotacaoCreateDTO cotacaoCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cotacaoService.createCotacao(cotacaoCreateDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cotação", description = "Atualiza os dados de uma cotação existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cotação atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Cotação não encontrada")
    })
    public ResponseEntity<CotacaoDTO> updateCotacao(@PathVariable Long id, @Valid @RequestBody CotacaoUpdateDTO cotacaoUpdateDTO) {
        return ResponseEntity.ok(cotacaoService.updateCotacao(id, cotacaoUpdateDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir cotação", description = "Remove uma cotação pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cotação excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cotação não encontrada")
    })
    public ResponseEntity<Void> deleteCotacao(@PathVariable Long id) {
        cotacaoService.deleteCotacao(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/anexo")
    @Operation(summary = "Obter anexo PDF da cotação", description = "Retorna o primeiro anexo PDF da cotação")
    public ResponseEntity<byte[]> obterAnexoPdf(@PathVariable Long id) {
        byte[] pdf = cotacaoService.obterAnexoPdf(id);
        if (pdf == null || pdf.length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "cotacao-" + id + ".pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @GetMapping("/{id}/anexo/{index}")
    @Operation(summary = "Obter anexo PDF por índice", description = "Retorna um anexo PDF específico da cotação pelo índice")
    public ResponseEntity<byte[]> obterAnexoPdfPorIndice(
            @PathVariable Long id,
            @PathVariable int index) {
        byte[] pdf = cotacaoService.obterAnexoPdf(id, index);
        if (pdf == null || pdf.length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "cotacao-" + id + "-" + index + ".pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @PatchMapping("/{cotacaoId}/vincular-itens")
    @Operation(summary = "Vincular itens à cotação", description = "Atualiza os itens vinculados a uma cotação específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Itens vinculados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cotação não encontrada")
    })
    public ResponseEntity<CotacaoDTO> vincularItens(
            @PathVariable Long cotacaoId,
            @RequestBody List<Long> itensPedidoIds) {
        return ResponseEntity.ok(cotacaoService.vincularItens(cotacaoId, itensPedidoIds));
    }

    @PutMapping("/{id}/editar")
    @Operation(
            summary = "Editar cotação com auditoria",
            description = "Edita uma cotação existente criando um registro de histórico para auditoria. " +
                    "Requer motivo da edição e registra quem editou."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cotação editada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Cotação não encontrada")
    })
    public ResponseEntity<CotacaoDTO> editarCotacao(@PathVariable Long id, @Valid @RequestBody CotacaoEditDTO cotacaoEditDTO) {
        return ResponseEntity.ok(cotacaoService.editarCotacao(cotacaoEditDTO));
    }

    @GetMapping("/{id}/historico")
    @Operation(
            summary = "Buscar histórico de edições",
            description = "Retorna todas as edições realizadas em uma cotação, incluindo preços anteriores, " +
                    "prazos, PDFs e motivos das alterações"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cotação não encontrada")
    })
    public ResponseEntity<List<HistoricoCotacaoDTO>> buscarHistoricoCotacao(@PathVariable Long id) {
        return ResponseEntity.ok(cotacaoService.buscarHistoricoCotacao(id));
    }

    @GetMapping("/historico/{historicoId}/pdf/anterior")
    @Operation(
            summary = "Obter PDF anterior do histórico",
            description = "Retorna o PDF da versão anterior armazenado no histórico de edições"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Histórico ou PDF não encontrado")
    })
    public ResponseEntity<byte[]> obterPdfAnteriorHistorico(@PathVariable Long historicoId) {
        byte[] pdf = cotacaoService.obterPdfAnteriorHistorico(historicoId);
        if (pdf == null || pdf.length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "historico-" + historicoId + "-anterior.pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @GetMapping("/historico/{historicoId}/pdf/novo")
    @Operation(
            summary = "Obter PDF novo do histórico",
            description = "Retorna o PDF da versão nova armazenado no histórico de edições"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Histórico ou PDF não encontrado")
    })
    public ResponseEntity<byte[]> obterPdfNovoHistorico(@PathVariable Long historicoId) {
        byte[] pdf = cotacaoService.obterPdfNovoHistorico(historicoId);
        if (pdf == null || pdf.length == 0) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "historico-" + historicoId + "-novo.pdf");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
