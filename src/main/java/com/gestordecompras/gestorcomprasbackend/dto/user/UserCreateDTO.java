package com.gestordecompras.gestorcomprasbackend.dto.user;

import com.gestordecompras.gestorcomprasbackend.model.user.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateDTO(
        @NotBlank(message = "O nome de usuário é obrigatório")
        String username,

        @NotBlank(message = "A senha é obrigatória")
        String senha,

        @NotNull(message = "O papel do usuário é obrigatório")
        UserRole role,

        @NotBlank(message = "O e-mail é obrigatório")
        String email
){}