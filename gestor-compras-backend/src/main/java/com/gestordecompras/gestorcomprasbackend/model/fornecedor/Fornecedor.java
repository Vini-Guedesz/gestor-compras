package com.gestordecompras.gestorcomprasbackend.model.fornecedor;

import com.gestordecompras.gestorcomprasbackend.model.contato.Contato;
import com.gestordecompras.gestorcomprasbackend.model.endereco.Endereco;
import jakarta.persistence.*;
import lombok.*;
import lombok.Data;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String razaoSocial;

    private String cnpj;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "contato_id")
    private Contato contato;
}
