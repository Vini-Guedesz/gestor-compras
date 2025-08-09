package com.gestordecompras.brisa.dto.user;

import com.gestordecompras.brisa.model.user.UserRole;

import jakarta.validation.constraints.NotNull;

public record UserUpdateDTO(
        @NotNull(message = "O ID é obrigatório")
        Integer id,

        String username,

        String senha,

        UserRole role,

        String email
) {
}