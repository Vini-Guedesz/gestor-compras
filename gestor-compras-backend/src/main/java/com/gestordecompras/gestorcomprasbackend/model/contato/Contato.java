package com.gestordecompras.gestorcomprasbackend.model.contato;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
@Entity (name="contato")
@Table (name="contato")
public class Contato {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    private String numero;
    private String email;
}
