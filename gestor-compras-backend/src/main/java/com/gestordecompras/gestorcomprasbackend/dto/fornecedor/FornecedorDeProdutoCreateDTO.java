package com.gestordecompras.gestorcomprasbackend.dto.fornecedor;

import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.endereco.EnderecoCreateDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CNPJ;

public record FornecedorDeProdutoCreateDTO(
        @NotBlank(message = "A razão social é obrigatória")
        @Size(min = 3, max = 200, message = "A razão social deve ter entre 3 e 200 caracteres")
        String razaoSocial,

        @NotBlank(message = "O CNPJ é obrigatório")
        @CNPJ(message = "CNPJ inválido")
        String cnpj,

        @NotNull(message = "O endereço é obrigatório")
        @Valid
        EnderecoCreateDTO endereco,

        @NotNull(message = "O contato é obrigatório")
        @Valid
        ContatoCreateDTO contato,

        @Size(max = 20, message = "A inscrição estadual deve ter no máximo 20 caracteres")
        String inscricaoEstadual
) {
}
