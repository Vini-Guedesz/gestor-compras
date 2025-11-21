package com.gestordecompras.gestorcomprasbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para autenticação de usuário")
public record LoginRequestDTO(
    @Schema(description = "E-mail do usuário", example = "admin@gestorcompras.com", required = true)
    String email,

    @Schema(description = "Senha do usuário", example = "senha123", required = true)
    String senha
) {}
