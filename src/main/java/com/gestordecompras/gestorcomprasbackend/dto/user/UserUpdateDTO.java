package com.gestordecompras.gestorcomprasbackend.dto.user;

import com.gestordecompras.gestorcomprasbackend.model.user.UserRole;

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