package com.gestordecompras.brisa.dto.user;

import com.gestordecompras.brisa.model.user.User;
import com.gestordecompras.brisa.model.user.UserRole;

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