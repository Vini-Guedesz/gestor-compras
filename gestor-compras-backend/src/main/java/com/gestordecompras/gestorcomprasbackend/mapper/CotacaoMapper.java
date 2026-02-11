package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoItemDTO;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.CotacaoItem;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.StatusCotacao;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper manual para conversão entre Cotacao e seus DTOs.
 * Implementado como classe para lidar com lógicas complexas de mapeamento (ex: itens legacy vs novos).
 */
@Component
public class CotacaoMapper {

    private final CotacaoItemMapper cotacaoItemMapper;

    public CotacaoMapper(CotacaoItemMapper cotacaoItemMapper) {
        this.cotacaoItemMapper = cotacaoItemMapper;
    }

    public CotacaoDTO toDTO(Cotacao cotacao) {
        if (cotacao == null) {
            return null;
        }

        // DEBUG: Verificar quantidade de itens antes do mapeamento
        if (cotacao.getItens() != null) {
            System.out.println("DEBUG: Mapeando Cotação ID " + cotacao.getId() + " - Total Itens no Set: " + cotacao.getItens().size());
        } else {
            System.out.println("DEBUG: Mapeando Cotação ID " + cotacao.getId() + " - Set de Itens é NULL");
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

        // Mapear itens com preços individuais (novo formato)
        List<CotacaoItemDTO> itensDTO = cotacao.getItens() != null
                ? cotacao.getItens().stream()
                    .map(cotacaoItemMapper::toDTO)
                    .collect(Collectors.toList())
                : Collections.emptyList();

        // Extrair IDs dos itens para compatibilidade (legacy)
        List<Long> itensPedidoIds = itensDTO.stream()
                .map(CotacaoItemDTO::itemPedidoId)
                .collect(Collectors.toList());

        // Calcular quantidade de anexos
        int quantidadeAnexos = 0;
        if (cotacao.getAnexos() != null && !cotacao.getAnexos().isEmpty()) {
            quantidadeAnexos = cotacao.getAnexos().size();
        }

        boolean temAnexo = quantidadeAnexos > 0;

        return new CotacaoDTO(
                cotacao.getId(),
                cotacao.getFornecedorId(),
                tipoFornecedor,
                nomeFornecedor,
                cotacao.getSolicitacaoDePedido() != null ? cotacao.getSolicitacaoDePedido().getId() : null,
                itensPedidoIds, // Legacy: apenas IDs
                itensDTO, // Novo: itens completos com preços
                cotacao.getPreco(), // Calculado automaticamente a partir dos itens
                cotacao.getPrazoEmDiasUteis(),
                cotacao.getDataLimite(),
                null, // caminhoAnexo deprecated - sempre null
                temAnexo,
                quantidadeAnexos,
                // Campos de auditoria
                cotacao.getFoiEditada(),
                cotacao.getNumeroVersao(),
                cotacao.getDataUltimaEdicao(),
                cotacao.getMotivoUltimaEdicao(),
                cotacao.getEditadoPor(),
                // Campos de projeto
                cotacao.getGastoPrevisto(),
                cotacao.getProjeto(),
                cotacao.getDataCriacao(),
                cotacao.getStatus() != null ? cotacao.getStatus() : StatusCotacao.EM_ANALISE
        );
    }

    public Cotacao toEntity(CotacaoCreateDTO cotacaoCreateDTO) {
        if (cotacaoCreateDTO == null) {
            return null;
        }

        Cotacao cotacao = new Cotacao();

        // Campos comuns
        cotacao.setPrazoEmDiasUteis(cotacaoCreateDTO.prazoEmDiasUteis());
        cotacao.setDataLimite(cotacaoCreateDTO.dataLimite());
        // REMOVIDO: anexoPdf - gerenciado via AnexoCotacao com deduplificação

        // Campos de projeto
        cotacao.setGastoPrevisto(cotacaoCreateDTO.gastoPrevisto() != null ? cotacaoCreateDTO.gastoPrevisto() : false);
        cotacao.setProjeto(cotacaoCreateDTO.projeto());

        // Preço Global: Sempre setar se disponível, independente do formato
        // A lógica de cálculo do modelo dará prioridade a este valor se a soma dos itens for zero
        if (cotacaoCreateDTO.preco() != null) {
            cotacao.setPrecoLegacy(cotacaoCreateDTO.preco());
        }

        // Nota: itens, fornecedor e solicitacaoDePedido são setados manualmente no service

        return cotacao;
    }
}
