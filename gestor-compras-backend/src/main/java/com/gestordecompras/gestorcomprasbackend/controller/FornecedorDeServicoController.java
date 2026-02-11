package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.config.ApiVersionConfig;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller REST para gerenciar Fornecedores de Serviço.
 *
 * <p>Oferece operações CRUD completas com paginação. Fornecedores de serviço
 * possuem Inscrição Municipal e prestam serviços.</p>
 *
 * <p><b>Autenticação:</b> JWT obrigatório | <b>Roles:</b> ADMIN, COMPRADOR, USUARIO, APROVADOR</p>
 *
 * @since 1.0.0
 * @see FornecedorDeServicoService
 * @see FornecedorDeServicoDTO
 */
@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/fornecedores-de-servico")
@Tag(name = "Fornecedores de Serviço", description = "API para gerenciamento de fornecedores de serviço (v1)")
@SecurityRequirement(name = "bearerAuth")
public class FornecedorDeServicoController {

    private final FornecedorDeServicoService service;

    /** Construtor com injeção de dependência. */
    public FornecedorDeServicoController(FornecedorDeServicoService service) {
        this.service = service;
    }

    /** Lista todos os fornecedores de serviço com paginação (padrão: 20 por página). */
    @GetMapping
    @Operation(summary = "Listar todos os fornecedores de serviço", description = "Retorna uma lista com todos os fornecedores de serviço cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de fornecedores de serviço retornada com sucesso")
    public ResponseEntity<Page<FornecedorDeServicoDTO>> findAll(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<FornecedorDeServicoDTO> fornecedores = service.getAllFornecedoresDeServico(pageable);
        return ResponseEntity.ok(fornecedores);
    }

    /** Busca fornecedor por ID incluindo endereço e contato. */
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

    /** Cria novo fornecedor validando CNPJ e Inscrição Municipal. */
    @PostMapping
    @Operation(summary = "Criar novo fornecedor de serviço", description = "Cria um novo fornecedor de serviço com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fornecedor de serviço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<FornecedorDeServicoDTO> create(@RequestBody @Valid FornecedorDeServicoCreateDTO dto, UriComponentsBuilder uriBuilder) {
        FornecedorDeServicoDTO createdFornecedor = service.createFornecedorDeServico(dto);
        URI uri = uriBuilder.path(ApiVersionConfig.API_V1 + "/fornecedores-de-servico/{id}").buildAndExpand(createdFornecedor.id()).toUri();
        return ResponseEntity.created(uri).body(createdFornecedor);
    }

    /** Atualiza fornecedor existente (ID obrigatório no DTO). */
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

    /** Remove fornecedor permanentemente. Falha se houver cotações associadas. */
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

    /** Retorna o histórico de cotações de um fornecedor de serviço. */
    @GetMapping("/{id}/historico-cotacoes")
    @Operation(summary = "Histórico de cotações", description = "Retorna o histórico de cotações de um fornecedor de serviço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Fornecedor de serviço não encontrado")
    })
    public ResponseEntity<List<CotacaoDTO>> getHistoricoCotacoes(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getHistoricoCotacoes(id));
    }
}
