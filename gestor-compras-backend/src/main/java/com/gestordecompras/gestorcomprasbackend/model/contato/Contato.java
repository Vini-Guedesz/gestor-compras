package com.gestordecompras.gestorcomprasbackend.model.contato;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "contato")
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "telefone_fixo", length = 20)
    private String telefoneFixo;

    @Column(name = "celular", length = 20)
    private String celular;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String email;
}
