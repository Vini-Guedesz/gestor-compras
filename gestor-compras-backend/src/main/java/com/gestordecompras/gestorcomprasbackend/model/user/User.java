package com.gestordecompras.gestorcomprasbackend.model.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entidade que representa um usuário do sistema.
 * <p>
 * Implementa {@link UserDetails} para integração direta com o Spring Security.
 * O campo {@code email} é utilizado como identificador de login (username).
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
 */
@Table(name = "users")
@Entity(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Nome completo do usuário (ex: "João Silva").
     * <p>
     * Utilizado para exibição na interface e relatórios. Não é usado para login.
     * </p>
     */
    private String nome;

    /**
     * Senha criptografada do usuário.
     */
    private String senha;

    /**
     * Papel ou perfil de acesso do usuário (ex: ADMIN, USER).
     */
    @Enumerated(EnumType.STRING)
    private UserRole role;

    /**
     * Endereço de email do usuário.
     * <p>
     * Serve como identificador único para login (username) no Spring Security.
     * </p>
     */
    private String email;

    /**
     * Retorna as autoridades concedidas ao usuário.
     *
     * @return Lista contendo a role do usuário prefixada com "ROLE_".
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Retorna a senha do usuário.
     *
     * @return A senha criptografada.
     */
    @Override
    public String getPassword() {
        return senha;
    }

    /**
     * Retorna o nome de usuário usado para autenticação.
     * <p>
     * <strong>IMPORTANTE:</strong> Retorna o email, pois é o identificador de login neste sistema.
     * </p>
     *
     * @return O email do usuário.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Obtém o nome completo do usuário para fins de exibição.
     *
     * @return O nome completo.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome completo do usuário.
     *
     * @param nome O novo nome completo.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}

