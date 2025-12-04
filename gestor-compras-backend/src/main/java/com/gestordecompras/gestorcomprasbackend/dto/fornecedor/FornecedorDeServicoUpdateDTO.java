package com.gestordecompras.gestorcomprasbackend.dto.fornecedor;

import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.endereco.EnderecoUpdateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CNPJ;

public record FornecedorDeServicoUpdateDTO(
        @NotNull(message = "O ID é obrigatório")
        Integer id,

        @Size(min = 3, max = 200, message = "A razão social deve ter entre 3 e 200 caracteres")
        String razaoSocial,

        @CNPJ(message = "CNPJ inválido")
        String cnpj,

        @Valid
        EnderecoUpdateDTO endereco,

        @Valid
        ContatoUpdateDTO contato,

        @Size(max = 20, message = "A inscrição municipal deve ter no máximo 20 caracteres")
        String inscricaoMunicipal
) {
}
