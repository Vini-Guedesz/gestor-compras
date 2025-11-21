package com.gestordecompras.gestorcomprasbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token JWT retornado após autenticação bem-sucedida")
public record TokenResponseDTO(
    @Schema(description = "Token JWT para autenticação em endpoints protegidos",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String token
) {}
