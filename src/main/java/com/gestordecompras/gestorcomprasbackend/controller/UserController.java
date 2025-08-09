package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.dto.user.UserCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.user.UserDTO;
import com.gestordecompras.gestorcomprasbackend.dto.user.UserUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuários", description = "API para gerenciamento de usuários")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista com todos os usuários cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    public ResponseEntity<List<UserDTO>> findAll() {
        List<UserDTO> users = service.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserDTO> findById(@PathVariable Integer id) {
        UserDTO user = service.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<UserDTO> create(@RequestBody @Valid UserCreateDTO dto, UriComponentsBuilder uriBuilder) {
        UserDTO createdUser = service.create(dto);
        URI uri = uriBuilder.path("/api/users/{id}").buildAndExpand(createdUser.id()).toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }

    @PutMapping
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserDTO> update(@RequestBody @Valid UserUpdateDTO dto) {
        UserDTO updatedUser = service.update(dto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "Remove um usuário pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}