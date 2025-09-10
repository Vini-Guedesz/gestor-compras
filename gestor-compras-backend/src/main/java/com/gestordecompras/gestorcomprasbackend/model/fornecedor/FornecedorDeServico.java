package com.gestordecompras.gestorcomprasbackend.model.fornecedor;

import com.gestordecompras.gestorcomprasbackend.model.contato.Contato;
import com.gestordecompras.gestorcomprasbackend.model.endereco.Endereco;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
@Table(name = "fornecedor_de_servico")
public class FornecedorDeServico extends Fornecedor {

    private String inscricaoMunicipal;

    public FornecedorDeServico(Integer id, String razaoSocial, String cnpj, Endereco endereco, Contato contato, String inscricaoMunicipal) {
        super(id, razaoSocial, cnpj, endereco, contato);
        this.inscricaoMunicipal = inscricaoMunicipal;
    }
}