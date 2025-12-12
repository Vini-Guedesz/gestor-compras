package com.gestordecompras.gestorcomprasbackend.dto.user;

import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.model.user.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta com dados do usuário")
public record UserDTO(
        @Schema(description = "ID único do usuário", example = "1")
        Integer id,

        @Schema(description = "Nome completo do usuário", example = "João da Silva")
        String nome,

        @Schema(description = "Perfil de acesso (USER ou ADMIN)", example = "ADMIN")
        UserRole role,

        @Schema(description = "Endereço de e-mail (usado para login)", example = "joao@email.com")
        String email
) {
    public UserDTO(User user) {
        this(user.getId(), user.getNome(), user.getRole(), user.getEmail());
    }
}