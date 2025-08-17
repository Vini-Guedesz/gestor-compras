package com.gestordecompras.gestorcomprasbackend.model.fornecedor;

import com.gestordecompras.gestorcomprasbackend.model.contato.Contato;
import com.gestordecompras.gestorcomprasbackend.model.endereco.Endereco;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter

public abstract class Fornecedor {
    Integer id ;
    String razaoSocial;
    String cnpj;
    Endereco endereco;
    Contato contato;
}
