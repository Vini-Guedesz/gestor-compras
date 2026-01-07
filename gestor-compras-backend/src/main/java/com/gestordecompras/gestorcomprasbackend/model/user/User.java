package com.gestordecompras.gestorcomprasbackend.model.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
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
    @Column(nullable = false, length = 100)
    private String nome;

    /**
     * Senha criptografada do usuário.
     */
    @Column(nullable = false, length = 100)
    private String senha;

    /**
     * Papel ou perfil de acesso do usuário (ex: ADMIN, USUARIO, COMPRADOR, APROVADOR).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /**
     * Endereço de email do usuário.
     * <p>
     * Serve como identificador único para login (username) no Spring Security.
     * </p>
     */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Indica se o usuário está ativo no sistema.
     * <p>
     * Usuários inativos não podem realizar login e são considerados desativados.
     * Este campo implementa soft delete - ao invés de excluir permanentemente,
     * o usuário é marcado como inativo.
     * </p>
     */
    @Column(nullable = false)
    private Boolean ativo = true;

    /**
     * Timestamp da criação do usuário no sistema.
     * <p>
     * Registrado automaticamente no momento da criação do usuário.
     * Valor imutável após a criação.
     * </p>
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Timestamp da última modificação nos dados do usuário.
     * <p>
     * Atualizado automaticamente sempre que qualquer dado do usuário é alterado
     * (nome, email, senha, role, status). Utilizado para invalidar tokens JWT
     * existentes quando o usuário é editado, forçando re-autenticação.
     * </p>
     */
    @Column(name = "last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt = LocalDateTime.now();

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

    /**
     * Verifica se o usuário está habilitado para autenticação.
     * <p>
     * Retorna o valor do campo {@code ativo}. Usuários inativos não podem fazer login.
     * </p>
     *
     * @return true se o usuário está ativo, false caso contrário
     */
    @Override
    public boolean isEnabled() {
        return ativo != null && ativo;
    }
}

