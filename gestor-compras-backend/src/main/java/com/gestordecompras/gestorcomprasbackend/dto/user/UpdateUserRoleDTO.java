package com.gestordecompras.gestorcomprasbackend.dto.user;

import com.gestordecompras.gestorcomprasbackend.model.user.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para alteração de role de usuário por um ADMIN.
 *
 * <p>Utilizado exclusivamente pelo endpoint de atualização de roles,
 * que está restrito a usuários com perfil ADMIN.</p>
 *
 * @since 1.0.0
 */
@Schema(description = "DTO para atualização de role de usuário")
public record UpdateUserRoleDTO(
        @NotNull(message = "O ID do usuário é obrigatório")
        @Schema(description = "ID do usuário que terá a role alterada", example = "1")
        Integer userId,

        @NotNull(message = "A nova role é obrigatória")
        @Schema(description = "Nova role do usuário (ADMIN, USUARIO, COMPRADOR ou APROVADOR)", example = "COMPRADOR")
        UserRole newRole
) {
}
