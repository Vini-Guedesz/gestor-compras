package com.gestordecompras.gestorcomprasbackend.dto.contato;

import com.gestordecompras.gestorcomprasbackend.model.contato.Contato;

public record ContatoDTO(
        Integer id,
        String telefoneFixo,
        String celular,
        String email
) {
    public ContatoDTO(Contato contato) {
        this(contato.getId(), contato.getTelefoneFixo(), contato.getCelular(), contato.getEmail());
    }
}
