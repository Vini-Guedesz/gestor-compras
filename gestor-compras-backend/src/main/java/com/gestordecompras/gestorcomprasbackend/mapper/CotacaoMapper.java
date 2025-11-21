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

        // Determinar o tipo de fornecedor e nome
        String tipoFornecedor = null;
        String nomeFornecedor = "";
        if (cotacao.getFornecedorProduto() != null) {
            tipoFornecedor = "PRODUTO";
            nomeFornecedor = cotacao.getFornecedorProduto().getRazaoSocial();
        } else if (cotacao.getFornecedorServico() != null) {
            tipoFornecedor = "SERVICO";
            nomeFornecedor = cotacao.getFornecedorServico().getRazaoSocial();
        }

        // Extrair IDs dos itens do pedido
        List<Long> itensPedidoIds = cotacao.getItensPedido() != null
                ? cotacao.getItensPedido().stream()
                    .map(ItemPedido::getId)
                    .collect(Collectors.toList())
                : List.of();

        // Calcular quantidade de anexos
        // Priorizar a nova estrutura de anexos múltiplos, caso contrário usar o campo legado
        int quantidadeAnexos = 0;
        if (cotacao.getAnexos() != null && !cotacao.getAnexos().isEmpty()) {
            // Nova estrutura: múltiplos anexos
            quantidadeAnexos = cotacao.getAnexos().size();
        } else if (cotacao.getAnexoPdf() != null && cotacao.getAnexoPdf().length > 0) {
            // Estrutura legada: apenas um anexo
            quantidadeAnexos = 1;
        }

        boolean temAnexo = quantidadeAnexos > 0;

        return new CotacaoDTO(
                cotacao.getId(),
                cotacao.getFornecedorId(),
                tipoFornecedor,
                nomeFornecedor,
                cotacao.getSolicitacaoDePedido() != null ? cotacao.getSolicitacaoDePedido().getId() : null,
                itensPedidoIds,
                cotacao.getPreco(),
                cotacao.getPrazoEmDiasUteis(),
                cotacao.getDataLimite(),
                cotacao.getCaminhoAnexo(),
                temAnexo,
                quantidadeAnexos
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
