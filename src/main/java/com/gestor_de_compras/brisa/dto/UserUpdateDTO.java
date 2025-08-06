package com.gestor_de_compras.brisa.dto;

import com.gestor_de_compras.brisa.model.user.UserRole;

import jakarta.validation.constraints.NotNull;

public record UserUpdateDTO(
        @NotNull(message = "O ID é obrigatório")
        Integer id,

        String login,

        String senha,

        UserRole role,

        String email
) {
}