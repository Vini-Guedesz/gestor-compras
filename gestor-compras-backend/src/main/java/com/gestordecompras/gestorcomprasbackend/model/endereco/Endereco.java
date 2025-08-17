package com.gestordecompras.gestorcomprasbackend.model.endereco;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
@Entity(name="endereco")
@Table(name="endereco")
public class Endereco {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)

    private Integer id;
    private String cep;
    private String estado;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
    private String complemento;

}
