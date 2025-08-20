package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.service.ContatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/contatos")
@Tag(name = "Contatos", description = "API para gerenciamento de contatos")
public class ContatoController {

    private final ContatoService service;

    public ContatoController(ContatoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos os contatos", description = "Retorna uma lista com todos os contatos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de contatos retornada com sucesso")
    public ResponseEntity<List<ContatoDTO>> findAll() {
        List<ContatoDTO> contatos = service.findAll();
        return ResponseEntity.ok(contatos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar contato por ID", description = "Retorna um contato específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public ResponseEntity<ContatoDTO> findById(@PathVariable Integer id) {
        ContatoDTO contato = service.findById(id);
        return ResponseEntity.ok(contato);
    }

    @PostMapping
    @Operation(summary = "Criar novo contato", description = "Cria um novo contato com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contato criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<ContatoDTO> create(@RequestBody @Valid ContatoCreateDTO dto, UriComponentsBuilder uriBuilder) {
        ContatoDTO createdContato = service.create(dto);
        URI uri = uriBuilder.path("/api/contatos/{id}").buildAndExpand(createdContato.id()).toUri();
        return ResponseEntity.created(uri).body(createdContato);
    }

    @PutMapping
    @Operation(summary = "Atualizar contato", description = "Atualiza os dados de um contato existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contato atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public ResponseEntity<ContatoDTO> update(@RequestBody @Valid ContatoUpdateDTO dto) {
        ContatoDTO updatedContato = service.update(dto);
        return ResponseEntity.ok(updatedContato);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir contato", description = "Remove um contato pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contato excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
