package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

        // Extrair IDs dos itens do pedido
        List<Long> itensPedidoIds = cotacao.getItensPedido() != null
                ? cotacao.getItensPedido().stream()
                    .map(ItemPedido::getId)
                    .collect(Collectors.toList())
                : List.of();

        return new CotacaoDTO(
                cotacao.getId(),
                cotacao.getFornecedorId(),
                tipoFornecedor,
                cotacao.getSolicitacaoDePedido() != null ? cotacao.getSolicitacaoDePedido().getId() : null,
                itensPedidoIds,
                cotacao.getPreco(),
                cotacao.getPrazoEmDiasUteis(),
                cotacao.getDataLimite(),
                cotacao.getCaminhoAnexo(),
                cotacao.getAnexoPdf()
        );
    }

    public Cotacao toEntity(CotacaoCreateDTO cotacaoCreateDTO) {
        if (cotacaoCreateDTO == null) {
            return null;
        }

        Cotacao cotacao = new Cotacao();
        cotacao.setPreco(cotacaoCreateDTO.preco());
        cotacao.setPrazoEmDiasUteis(cotacaoCreateDTO.prazoEmDiasUteis());
        cotacao.setDataLimite(cotacaoCreateDTO.dataLimite());
        cotacao.setAnexoPdf(cotacaoCreateDTO.anexoPdf());

        return cotacao;
    }
}
