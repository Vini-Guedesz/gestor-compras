package com.gestordecompras.gestorcomprasbackend.dto.user;

import com.gestordecompras.gestorcomprasbackend.model.user.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para atualização de usuários existentes.
 *
 * <p>Campos opcionais (apenas os fornecidos serão atualizados):</p>
 * <ul>
 *   <li>Nome: se fornecido, deve ter 3-100 caracteres</li>
 *   <li>E-mail: se fornecido, deve ser válido e ter máximo 255 caracteres</li>
 *   <li>Senha: se fornecida, deve ter 8-100 caracteres com validação forte</li>
 *   <li>Role: pode ser alterada</li>
 * </ul>
 */
public record UserUpdateDTO(
        @NotNull(message = "O ID é obrigatório")
        Integer id,

        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,

        @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,100}$",
                message = "A senha deve conter: maiúscula, minúscula, número e caractere especial (@$!%*?&)")
        String senha,

        UserRole role,

        @Email(message = "Digite um e-mail válido")
        @Size(max = 255, message = "O e-mail não pode ter mais de 255 caracteres")
        String email
) {
}