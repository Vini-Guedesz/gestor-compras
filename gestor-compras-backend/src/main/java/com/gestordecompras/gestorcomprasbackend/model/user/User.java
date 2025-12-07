package com.gestordecompras.gestorcomprasbackend.model.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


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
     * Nome completo do usuário (ex: "João Silva")
     * NÃO é usado para autenticação - apenas informação de perfil
     */
    private String nome;  // Antes: username

    private String senha;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    /**
     * Email do usuário (ex: "joao@email.com")
     * Este campo É usado para autenticação via UserDetails.getUsername()
     */
    private String email;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    /**
     * Retorna o identificador único para login (Spring Security UserDetails)
     * IMPORTANTE: Retorna EMAIL, não o campo 'nome'
     */
    @Override
    public String getUsername() {
        return email;  // LOGIN: usa email como username
    }

    /**
     * Getter para o nome completo do usuário
     * Este é o campo 'nome' da tabela, não o username de login
     */
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}

