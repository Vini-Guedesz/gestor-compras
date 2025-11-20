package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.dto.rascunho.CotacaoRascunhoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.CotacaoRascunhoDTO;
import com.gestordecompras.gestorcomprasbackend.service.CotacaoRascunhoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rascunhos/{rascunhoId}/cotacoes")
public class CotacaoRascunhoController {

    @Autowired
    private CotacaoRascunhoService cotacaoRascunhoService;

    @GetMapping
    public ResponseEntity<List<CotacaoRascunhoDTO>> listarPorRascunho(@PathVariable Long rascunhoId) {
        List<CotacaoRascunhoDTO> cotacoes = cotacaoRascunhoService.listarPorRascunho(rascunhoId);
        return ResponseEntity.ok(cotacoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CotacaoRascunhoDTO> obterPorId(
            @PathVariable Long rascunhoId,
            @PathVariable Long id) {
        CotacaoRascunhoDTO cotacao = cotacaoRascunhoService.obterPorId(id);
        return ResponseEntity.ok(cotacao);
    }

    @PostMapping
    public ResponseEntity<CotacaoRascunhoDTO> criar(
            @PathVariable Long rascunhoId,
            @Valid @RequestBody CotacaoRascunhoCreateDTO dto) {
        CotacaoRascunhoDTO cotacao = cotacaoRascunhoService.criar(rascunhoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cotacao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long rascunhoId,
            @PathVariable Long id) {
        cotacaoRascunhoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/anexo")
    public ResponseEntity<byte[]> obterAnexoPdf(
            @PathVariable Long rascunhoId,
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
    public ResponseEntity<byte[]> obterAnexoPdfPorIndice(
            @PathVariable Long rascunhoId,
            @PathVariable Long id,
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
