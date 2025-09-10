package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.dto.endereco.EnderecoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.endereco.EnderecoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.endereco.EnderecoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.service.EnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/enderecos")
@Tag(name = "Endereços", description = "API para gerenciamento de endereços")
@SecurityRequirement(name = "bearerAuth")
public class EnderecoController {

    private final EnderecoService service;

    public EnderecoController(EnderecoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos os endereços", description = "Retorna uma lista com todos os endereços cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de endereços retornada com sucesso")
    public ResponseEntity<List<EnderecoDTO>> findAll() {
        List<EnderecoDTO> enderecos = service.findAll();
        return ResponseEntity.ok(enderecos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar endereço por ID", description = "Retorna um endereço específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    })
    public ResponseEntity<EnderecoDTO> findById(@PathVariable Integer id) {
        EnderecoDTO endereco = service.findById(id);
        return ResponseEntity.ok(endereco);
    }

    @PostMapping
    @Operation(summary = "Criar novo endereço", description = "Cria um novo endereço com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<EnderecoDTO> create(@RequestBody @Valid EnderecoCreateDTO dto, UriComponentsBuilder uriBuilder) {
        EnderecoDTO createdEndereco = service.create(dto);
        URI uri = uriBuilder.path("/api/enderecos/{id}").buildAndExpand(createdEndereco.id()).toUri();
        return ResponseEntity.created(uri).body(createdEndereco);
    }

    @PutMapping
    @Operation(summary = "Atualizar endereço", description = "Atualiza os dados de um endereço existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    })
    public ResponseEntity<EnderecoDTO> update(@RequestBody @Valid EnderecoUpdateDTO dto) {
        EnderecoDTO updatedEndereco = service.update(dto);
        return ResponseEntity.ok(updatedEndereco);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir endereço", description = "Remove um endereço pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereço excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
