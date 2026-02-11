package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.config.ApiVersionConfig;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.service.FornecedorDeProdutoService;
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
 * Controller REST para gerenciar Fornecedores de Produto.
 *
 * <p>Oferece operações CRUD completas com paginação. Fornecedores de produto
 * possuem Inscrição Estadual e fornecem mercadorias.</p>
 *
 * <p><b>Autenticação:</b> JWT obrigatório | <b>Roles:</b> ADMIN, COMPRADOR, USUARIO, APROVADOR</p>
 *
 * @since 1.0.0
 * @see FornecedorDeProdutoService
 * @see FornecedorDeProdutoDTO
 */
@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/fornecedores-de-produto")
@Tag(name = "Fornecedores de Produto", description = "API para gerenciamento de fornecedores de produto (v1)")
@SecurityRequirement(name = "bearerAuth")
public class FornecedorDeProdutoController {

    private final FornecedorDeProdutoService service;

    /** Construtor com injeção de dependência. */
    public FornecedorDeProdutoController(FornecedorDeProdutoService service) {
        this.service = service;
    }

    /** Lista todos os fornecedores de produto com paginação (padrão: 20 por página). */
    @GetMapping
    @Operation(summary = "Listar todos os fornecedores de produto", description = "Retorna uma lista com todos os fornecedores de produto cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de fornecedores de produto retornada com sucesso")
    public ResponseEntity<Page<FornecedorDeProdutoDTO>> findAll(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<FornecedorDeProdutoDTO> fornecedores = service.getAllFornecedoresDeProduto(pageable);
        return ResponseEntity.ok(fornecedores);
    }

    /** Busca fornecedor por ID incluindo endereço e contato. */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar fornecedor de produto por ID", description = "Retorna um fornecedor de produto específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fornecedor de produto encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Fornecedor de produto não encontrado")
    })
    public ResponseEntity<FornecedorDeProdutoDTO> findById(@PathVariable Integer id) {
        FornecedorDeProdutoDTO fornecedor = service.getFornecedorDeProdutoById(id);
        return ResponseEntity.ok(fornecedor);
    }

    /** Cria novo fornecedor validando CNPJ e Inscrição Estadual. */
    @PostMapping
    @Operation(summary = "Criar novo fornecedor de produto", description = "Cria um novo fornecedor de produto com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fornecedor de produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<FornecedorDeProdutoDTO> create(@RequestBody @Valid FornecedorDeProdutoCreateDTO dto, UriComponentsBuilder uriBuilder) {
        FornecedorDeProdutoDTO createdFornecedor = service.createFornecedorDeProduto(dto);
        URI uri = uriBuilder.path(ApiVersionConfig.API_V1 + "/fornecedores-de-produto/{id}").buildAndExpand(createdFornecedor.id()).toUri();
        return ResponseEntity.created(uri).body(createdFornecedor);
    }

    /** Atualiza fornecedor existente (ID obrigatório no DTO). */
    @PutMapping
    @Operation(summary = "Atualizar fornecedor de produto", description = "Atualiza os dados de um fornecedor de produto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fornecedor de produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Fornecedor de produto não encontrado")
    })
    public ResponseEntity<FornecedorDeProdutoDTO> update(@RequestBody @Valid FornecedorDeProdutoUpdateDTO dto) {
        FornecedorDeProdutoDTO updatedFornecedor = service.updateFornecedorDeProduto(dto);
        return ResponseEntity.ok(updatedFornecedor);
    }

    /** Remove fornecedor permanentemente. Falha se houver cotações associadas. */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir fornecedor de produto", description = "Remove um fornecedor de produto pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fornecedor de produto excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Fornecedor de produto não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deleteFornecedorDeProduto(id);
        return ResponseEntity.noContent().build();
    }

    /** Retorna o histórico de cotações de um fornecedor de produto. */
    @GetMapping("/{id}/historico-cotacoes")
    @Operation(summary = "Histórico de cotações", description = "Retorna o histórico de cotações de um fornecedor de produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Fornecedor de produto não encontrado")
    })
    public ResponseEntity<List<CotacaoDTO>> getHistoricoCotacoes(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getHistoricoCotacoes(id));
    }
}
