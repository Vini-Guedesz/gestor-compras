package com.gestordecompras.gestorcomprasbackend.dto.user;

import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.model.user.UserRole;

public record UserDTO(
        Integer id,
        String username,
        UserRole role,
        String email
) {
    public UserDTO(User user) {
        this(user.getId(), user.getUsername(), user.getRole(), user.getEmail());
    }
}