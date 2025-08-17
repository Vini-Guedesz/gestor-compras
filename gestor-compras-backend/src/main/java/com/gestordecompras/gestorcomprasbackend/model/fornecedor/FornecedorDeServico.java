package com.gestordecompras.gestorcomprasbackend.model.fornecedor;

import com.gestordecompras.gestorcomprasbackend.model.contato.Contato;
import com.gestordecompras.gestorcomprasbackend.model.endereco.Endereco;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="fonecedor_de_servico")
@Entity(name="fonecedor_de_servico")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class FornecedorDeServico extends Fornecedor{
    public FornecedorDeServico(Integer id, String razaoSocial, String cnpj, Endereco endereco, Contato contato) {
        super(id, razaoSocial, cnpj, endereco, contato);
    }
    private String inscricaoMunicipal;
}
