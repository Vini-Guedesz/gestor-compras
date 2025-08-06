package com.gestor_de_compras.brisa.dto;

import com.gestor_de_compras.brisa.model.user.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateDTO(
        @NotBlank(message = "O login é obrigatório")
        String login,

        @NotBlank(message = "A senha é obrigatória")
        String senha,

        @NotNull(message = "O papel do usuário é obrigatório")
        UserRole role,

        @NotBlank(message = "O e-mail é obrigatório")
        String email
){}