package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeServicoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeServicoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeServicoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.service.FornecedorDeServicoService;
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
@RequestMapping("/api/fornecedores-de-servico")
@Tag(name = "Fornecedores de Serviço", description = "API para gerenciamento de fornecedores de serviço")
@SecurityRequirement(name = "bearerAuth")
public class FornecedorDeServicoController {

    private final FornecedorDeServicoService service;

    public FornecedorDeServicoController(FornecedorDeServicoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos os fornecedores de serviço", description = "Retorna uma lista com todos os fornecedores de serviço cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de fornecedores de serviço retornada com sucesso")
    public ResponseEntity<List<FornecedorDeServicoDTO>> findAll() {
        List<FornecedorDeServicoDTO> fornecedores = service.getAllFornecedoresDeServico();
        return ResponseEntity.ok(fornecedores);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar fornecedor de serviço por ID", description = "Retorna um fornecedor de serviço específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fornecedor de serviço encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Fornecedor de serviço não encontrado")
    })
    public ResponseEntity<FornecedorDeServicoDTO> findById(@PathVariable Integer id) {
        FornecedorDeServicoDTO fornecedor = service.getFornecedorDeServicoById(id);
        return ResponseEntity.ok(fornecedor);
    }

    @PostMapping
    @Operation(summary = "Criar novo fornecedor de serviço", description = "Cria um novo fornecedor de serviço com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fornecedor de serviço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<FornecedorDeServicoDTO> create(@RequestBody @Valid FornecedorDeServicoCreateDTO dto, UriComponentsBuilder uriBuilder) {
        FornecedorDeServicoDTO createdFornecedor = service.createFornecedorDeServico(dto);
        URI uri = uriBuilder.path("/api/fornecedores-de-servico/{id}").buildAndExpand(createdFornecedor.id()).toUri();
        return ResponseEntity.created(uri).body(createdFornecedor);
    }

    @PutMapping
    @Operation(summary = "Atualizar fornecedor de serviço", description = "Atualiza os dados de um fornecedor de serviço existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fornecedor de serviço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Fornecedor de serviço não encontrado")
    })
    public ResponseEntity<FornecedorDeServicoDTO> update(@RequestBody @Valid FornecedorDeServicoUpdateDTO dto) {
        FornecedorDeServicoDTO updatedFornecedor = service.updateFornecedorDeServico(dto);
        return ResponseEntity.ok(updatedFornecedor);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir fornecedor de serviço", description = "Remove um fornecedor de serviço pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fornecedor de serviço excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Fornecedor de serviço não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deleteFornecedorDeServico(id);
        return ResponseEntity.noContent().build();
    }
}
