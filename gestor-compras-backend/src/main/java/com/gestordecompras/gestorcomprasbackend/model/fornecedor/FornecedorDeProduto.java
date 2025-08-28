package com.gestordecompras.gestorcomprasbackend.model.fornecedor;

import com.gestordecompras.gestorcomprasbackend.model.contato.Contato;
import com.gestordecompras.gestorcomprasbackend.model.endereco.Endereco;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "fornecedor_de_produto")
public class FornecedorDeProduto extends Fornecedor {

    private String inscricaoEstadual;

    public FornecedorDeProduto(Integer id, String razaoSocial, String cnpj, Endereco endereco, Contato contato, String inscricaoEstadual) {
        super(id, razaoSocial, cnpj, endereco, contato);
        this.inscricaoEstadual = inscricaoEstadual;
    }
}