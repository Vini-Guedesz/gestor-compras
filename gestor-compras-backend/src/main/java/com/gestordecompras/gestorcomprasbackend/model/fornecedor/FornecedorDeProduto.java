package com.gestordecompras.gestorcomprasbackend.model.fornecedor;

import com.gestordecompras.gestorcomprasbackend.model.contato.Contato;
import com.gestordecompras.gestorcomprasbackend.model.endereco.Endereco;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fornecedor_de_produto")
@Entity(name = "fornecedor_de_produto")

public class FornecedorDeProduto extends Fornecedor {
    public FornecedorDeProduto(Integer id, String razaoSocial, String cnpj, Endereco endereco, Contato contato) {
        super(id, razaoSocial, cnpj, endereco, contato);
    }
private String inscricaoEstadual;
}
