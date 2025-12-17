package com.gestordecompras.gestorcomprasbackend.dto.user;

import com.gestordecompras.gestorcomprasbackend.model.user.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para criação de novos usuários.
 *
 * <p>Validações aplicadas:</p>
 * <ul>
 *   <li>Nome: 3-100 caracteres</li>
 *   <li>E-mail: formato válido, máximo 255 caracteres</li>
 *   <li>Senha: 8-100 caracteres, com maiúscula, minúscula, número e especial</li>
 *   <li>Role: obrigatória</li>
 * </ul>
 */
public record UserCreateDTO(
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,100}$",
                message = "A senha deve conter: maiúscula, minúscula, número e caractere especial (@$!%*?&)")
        String senha,

        @NotNull(message = "A função do usuário é obrigatória")
        UserRole role,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Digite um e-mail válido")
        @Size(max = 255, message = "O e-mail não pode ter mais de 255 caracteres")
        String email,

        Boolean ativo  // Opcional - padrão será true se não fornecido
){}