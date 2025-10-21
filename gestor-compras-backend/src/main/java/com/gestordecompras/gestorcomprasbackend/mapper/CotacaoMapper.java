package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import org.springframework.stereotype.Component;

@Component
public class CotacaoMapper {

    public CotacaoDTO toDTO(Cotacao cotacao) {
        if (cotacao == null) {
            return null;
        }

        // Determinar o tipo de fornecedor
        String tipoFornecedor = null;
        if (cotacao.getFornecedorProduto() != null) {
            tipoFornecedor = "PRODUTO";
        } else if (cotacao.getFornecedorServico() != null) {
            tipoFornecedor = "SERVICO";
        }

        return new CotacaoDTO(
                cotacao.getId(),
                cotacao.getFornecedorId(),
                tipoFornecedor,
                cotacao.getItemPedido() != null ? cotacao.getItemPedido().getId() : null,
                cotacao.getPreco(),
                cotacao.getPrazoEntrega(),
                cotacao.getDataCotacao(),
                cotacao.getCaminhoAnexo()
        );
    }

    public Cotacao toEntity(CotacaoCreateDTO cotacaoCreateDTO) {
        if (cotacaoCreateDTO == null) {
            return null;
        }

        Cotacao cotacao = new Cotacao();
        cotacao.setPreco(cotacaoCreateDTO.preco());
        cotacao.setPrazoEntrega(cotacaoCreateDTO.prazoEntrega());

        return cotacao;
    }
}
