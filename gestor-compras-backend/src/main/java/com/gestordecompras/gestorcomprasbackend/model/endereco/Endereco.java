package com.gestordecompras.gestorcomprasbackend.model.endereco;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "endereco")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(nullable = false, length = 9)
    private String cep;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String estado;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String cidade;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String bairro;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String rua;

    @NotBlank
    @Column(nullable = false, length = 10)
    private String numero;

    @Column(length = 100)
    private String complemento;
}
