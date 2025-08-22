package com.gestordecompras.gestorcomprasbackend.dto.contato;

import com.gestordecompras.gestorcomprasbackend.model.contato.Contato;

public record ContatoDTO(
        Integer id,
        String numero,
        String email
) {
    public ContatoDTO(Contato contato) {
        this(contato.getId(), contato.getNumero(), contato.getEmail());
    }
}
