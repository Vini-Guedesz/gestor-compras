package com.gestordecompras.gestorcomprasbackend.dto.fornecedor;

import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.endereco.EnderecoDTO;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO que representa os dados de um fornecedor de produtos.
 * <p>
 * Inclui informações cadastrais como razão social, CNPJ, inscrição estadual,
 * endereço e contato.
 * </p>
 */
@Schema(description = "DTO de resposta com detalhes do fornecedor de produto")
public record FornecedorDeProdutoDTO(
        @Schema(description = "ID único do fornecedor", example = "10")
        Integer id,

        @Schema(description = "Razão social ou nome da empresa", example = "ABC Suprimentos Ltda")
        String razaoSocial,

        @Schema(description = "Cadastro Nacional da Pessoa Jurídica (CNPJ)", example = "00.000.000/0001-00")
        String cnpj,

        @Schema(description = "Dados completos do endereço do fornecedor")
        EnderecoDTO endereco,

        @Schema(description = "Dados de contato do fornecedor (telefone, email)")
        ContatoDTO contato,

        @Schema(description = "Inscrição Estadual do fornecedor", example = "123456789")
        String inscricaoEstadual
) {
    /**
     * Construtor de conveniência que cria um DTO a partir da entidade {@link FornecedorDeProduto}.
     *
     * @param fornecedor A entidade fornecedor de produto a ser convertida.
     */
    public FornecedorDeProdutoDTO(FornecedorDeProduto fornecedor) {
        this(
                fornecedor.getId(),
                fornecedor.getRazaoSocial(),
                fornecedor.getCnpj(),
                new EnderecoDTO(fornecedor.getEndereco()),
                new ContatoDTO(fornecedor.getContato()),
                fornecedor.getInscricaoEstadual()
        );
    }
}
