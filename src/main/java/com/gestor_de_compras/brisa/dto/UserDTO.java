package com.gestor_de_compras.brisa.dto;

import com.gestor_de_compras.brisa.model.user.User;
import com.gestor_de_compras.brisa.model.user.UserRole;

public record UserDTO(
        Integer id,
        String login,
        UserRole role,
        String email
) {
    public UserDTO(User user) {
        this(user.getId(), user.getLogin(), user.getRole(), user.getEmail());
    }
}