package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.config.ApiVersionConfig;
import com.gestordecompras.gestorcomprasbackend.dto.user.UpdateUserRoleDTO;
import com.gestordecompras.gestorcomprasbackend.dto.user.UserCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.user.UserDTO;
import com.gestordecompras.gestorcomprasbackend.dto.user.UserUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller REST para gerenciar usuários do sistema.
 *
 * <p>Oferece operações CRUD completas com validação e segurança.
 * Todos os endpoints requerem autenticação JWT e role ADMIN.</p>
 *
 * <p><b>Endpoints principais:</b></p>
 * <ul>
 *   <li>GET /api/v1/users - Listar todos os usuários</li>
 *   <li>GET /api/v1/users/{id} - Buscar por ID</li>
 *   <li>POST /api/v1/users - Criar novo usuário (Apenas ADMIN)</li>
 *   <li>PUT /api/v1/users - Atualizar usuário existente</li>
 *   <li>DELETE /api/v1/users/{id} - Deletar usuário</li>
 * </ul>
 *
 * <p><b>Autenticação:</b> JWT obrigatório</p>
 * <p><b>Roles permitidas:</b> ADMIN</p>
 *
 * @since 1.0.0
 * @author Equipe de Desenvolvimento
 * @see UserService
 * @see UserDTO
 * @see UserCreateDTO
 * @see UserUpdateDTO
 */
@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/users")
@Tag(name = "Usuários", description = "API para gerenciamento de usuários (v1)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService service;

    /**
     * Construtor com injeção de dependência do service.
     *
     * @param service Service de lógica de negócio para usuários
     */
    public UserController(UserService service) {
        this.service = service;
    }

    /**
     * Lista todos os usuários cadastrados no sistema com paginação.
     *
     * <p>Retorna uma página com usuários ativos e inativos.
     * Os resultados incluem ID, nome, email e role de cada usuário.
     * Padrão: 20 usuários por página ordenados por ID.</p>
     *
     * @param pageable Parâmetros de paginação e ordenação
     * @return ResponseEntity contendo página de UserDTO
     */
    @GetMapping
    @Operation(summary = "Listar usuários com paginação", description = "Retorna uma página com os usuários cadastrados. Padrão: 20 usuários por página ordenados por ID.")
    @ApiResponse(responseCode = "200", description = "Página de usuários retornada com sucesso")
    public ResponseEntity<Page<UserDTO>> findAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<UserDTO> users = service.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Busca um usuário específico por ID.
     *
     * <p>Retorna os detalhes completos do usuário identificado pelo ID fornecido.</p>
     *
     * @param id Identificador único do usuário
     * @return ResponseEntity contendo UserDTO com dados completos
     * @throws jakarta.persistence.EntityNotFoundException se usuário não encontrado com o ID fornecido
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserDTO> findById(@PathVariable Integer id) {
        UserDTO user = service.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Cria um novo usuário no sistema.
     *
     * <p>Valida os dados de entrada e persiste o novo usuário.
     * A senha fornecida é criptografada com BCrypt antes de ser armazenada.</p>
     *
     * <p><b>Validações realizadas:</b></p>
     * <ul>
     *   <li>Email único no sistema</li>
     *   <li>Email em formato válido</li>
     *   <li>Senha com mínimo 6 caracteres</li>
     *   <li>Role válido (USER ou ADMIN)</li>
     * </ul>
     *
     * @param dto Dados do usuário a ser criado
     * @param uriBuilder Builder para construção da URI de localização
     * @return ResponseEntity com UserDTO criado e status 201 (CREATED)
     * @throws com.gestordecompras.gestorcomprasbackend.exception.DataIntegrityConflictException se email já existe
     * @throws IllegalArgumentException se dados inválidos
     */
    @PostMapping
    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<UserDTO> create(@RequestBody @Valid UserCreateDTO dto, UriComponentsBuilder uriBuilder) {
        UserDTO createdUser = service.createUser(dto);
        URI uri = uriBuilder.path(ApiVersionConfig.API_V1 + "/users/{id}").buildAndExpand(createdUser.id()).toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }

    /**
     * Atualiza um usuário existente.
     *
     * <p>Atualiza os dados do usuário identificado pelo ID presente no DTO.
     * Apenas campos fornecidos serão atualizados. Se senha for fornecida,
     * será criptografada antes de armazenar.</p>
     *
     * @param dto Novos dados do usuário (incluindo ID)
     * @return ResponseEntity com UserDTO atualizado
     * @throws jakarta.persistence.EntityNotFoundException se usuário não encontrado
     * @throws com.gestordecompras.gestorcomprasbackend.exception.DataIntegrityConflictException se atualização causa conflito de email
     */
    @PutMapping
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserDTO> update(@RequestBody @Valid UserUpdateDTO dto) {
        UserDTO updatedUser = service.updateUser(dto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Desativa um usuário do sistema (soft delete).
     *
     * <p>Marca o usuário como inativo ao invés de excluí-lo permanentemente.
     * Usuários inativos não podem fazer login no sistema.</p>
     *
     * <p><b>IMPORTANTE:</b> O usuário permanece no banco de dados mas não pode
     * acessar o sistema. Use o endpoint PATCH /{id}/reactivate para reativar.</p>
     *
     * @param id ID do usuário a ser desativado
     * @return ResponseEntity com status 204 (NO_CONTENT)
     * @throws jakarta.persistence.EntityNotFoundException se usuário não encontrado
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar usuário", description = "Desativa um usuário impedindo seu acesso ao sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualiza a role de um usuário (exclusivo para ADMINs).
     *
     * <p>Este endpoint permite que administradores alterem o nível de acesso
     * de outros usuários no sistema. Apenas usuários com role ADMIN podem
     * acessar este endpoint.</p>
     *
     * <p><b>Roles disponíveis:</b></p>
     * <ul>
     *   <li>ADMIN - Acesso total ao sistema, incluindo gerenciamento de usuários</li>
     *   <li>USUARIO - Acesso básico ao sistema</li>
     *   <li>COMPRADOR - Permissões para criar e gerenciar pedidos de compra</li>
     *   <li>APROVADOR - Permissões para aprovar pedidos de compra</li>
     * </ul>
     *
     * @param dto DTO contendo o ID do usuário e a nova role
     * @return ResponseEntity com UserDTO atualizado
     * @throws jakarta.persistence.EntityNotFoundException se usuário não encontrado
     */
    @PatchMapping("/role")
    @Operation(summary = "Atualizar role de usuário", description = "Permite que ADMINs alterem a role de outros usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMINs podem alterar roles"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserDTO> updateUserRole(@RequestBody @Valid UpdateUserRoleDTO dto) {
        UserDTO updatedUser = service.updateUserRole(dto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Reativa um usuário previamente desativado (exclusivo para ADMINs).
     *
     * <p>Permite que usuários desativados voltem a ter acesso ao sistema.
     * Apenas usuários com role ADMIN podem acessar este endpoint.</p>
     *
     * @param id ID do usuário a ser reativado
     * @return ResponseEntity com UserDTO do usuário reativado
     * @throws jakarta.persistence.EntityNotFoundException se usuário não encontrado
     */
    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "Reativar usuário", description = "Reativa um usuário previamente desativado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário reativado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - apenas ADMINs podem reativar usuários"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserDTO> reactivateUser(@PathVariable Integer id) {
        UserDTO reactivatedUser = service.reactivateUser(id);
        return ResponseEntity.ok(reactivatedUser);
    }
}