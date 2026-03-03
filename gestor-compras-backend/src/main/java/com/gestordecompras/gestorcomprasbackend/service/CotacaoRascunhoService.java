package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.rascunho.CotacaoRascunhoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.CotacaoRascunhoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.CotacaoRascunhoItemDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.CotacaoRascunhoItemCreateDTO;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeServico;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.AnexoCotacaoRascunho;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.CotacaoRascunho;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.CotacaoRascunhoItem;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.ItemRascunho;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.Rascunho;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.StatusRascunho;
import com.gestordecompras.gestorcomprasbackend.repository.CotacaoRascunhoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeProdutoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeServicoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.ItemRascunhoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.RascunhoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servico responsavel pelo gerenciamento de cotacoes associadas a rascunhos.
 */
@Slf4j
@Service
@SuppressWarnings("deprecation")
public class CotacaoRascunhoService {

    @Autowired
    private CotacaoRascunhoRepository cotacaoRascunhoRepository;

    @Autowired
    private RascunhoRepository rascunhoRepository;

    @Autowired
    private ItemRascunhoRepository itemRascunhoRepository;

    @Autowired
    private FornecedorDeProdutoRepository fornecedorDeProdutoRepository;

    @Autowired
    private FornecedorDeServicoRepository fornecedorDeServicoRepository;

    @Autowired
    private PdfDeduplicationService pdfDeduplicationService;

    @Transactional(readOnly = true)
    public List<CotacaoRascunhoDTO> listarPorRascunho(Long rascunhoId) {
        List<CotacaoRascunho> cotacoes = cotacaoRascunhoRepository.findByRascunhoIdWithItens(rascunhoId);
        if (!cotacoes.isEmpty()) {
            List<Long> ids = cotacoes.stream().map(CotacaoRascunho::getId).collect(Collectors.toList());
            cotacaoRascunhoRepository.findByIdsWithAnexos(ids);
        }
        return cotacoes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CotacaoRascunhoDTO obterPorId(Long id) {
        CotacaoRascunho cotacao = cotacaoRascunhoRepository.findByIdWithItens(id);
        if (cotacao == null) {
            throw new EntityNotFoundException("Cotacao nao encontrada com ID: " + id);
        }
        cotacaoRascunhoRepository.findByIdsWithAnexos(List.of(id));
        return toDTO(cotacao);
    }

    @Transactional
    public CotacaoRascunhoDTO criar(Long rascunhoId, CotacaoRascunhoCreateDTO dto) {
        Rascunho rascunho = rascunhoRepository.findById(rascunhoId)
            .orElseThrow(() -> new EntityNotFoundException("Rascunho nao encontrado com ID: " + rascunhoId));

        CotacaoRascunho cotacao = new CotacaoRascunho();
        cotacao.setRascunho(rascunho);
        cotacao.setPrazoEmDiasUteis(dto.prazoEmDiasUteis());
        cotacao.setDataLimite(dto.dataLimite());
        cotacao.setGastoPrevisto(dto.gastoPrevisto() != null ? dto.gastoPrevisto() : false);
        cotacao.setProjeto(dto.projeto());

        log.info("DEBUG: Criando cotacao rascunho - gastoPrevisto recebido: {}, projeto: {}",
            dto.gastoPrevisto(), dto.projeto());

        if ("PRODUTO".equalsIgnoreCase(dto.tipoFornecedor())) {
            FornecedorDeProduto fornecedor = fornecedorDeProdutoRepository.findById(dto.fornecedorId())
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor de produto nao encontrado"));
            cotacao.setFornecedorProduto(fornecedor);
        } else if ("SERVICO".equalsIgnoreCase(dto.tipoFornecedor())) {
            FornecedorDeServico fornecedor = fornecedorDeServicoRepository.findById(dto.fornecedorId())
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor de servico nao encontrado"));
            cotacao.setFornecedorServico(fornecedor);
        } else {
            throw new IllegalArgumentException("Tipo de fornecedor invalido: " + dto.tipoFornecedor());
        }

        Set<CotacaoRascunhoItem> itens = new HashSet<>();
        for (CotacaoRascunhoItemCreateDTO itemDTO : dto.itens()) {
            ItemRascunho item = itemRascunhoRepository.findById(itemDTO.itemRascunhoId())
                .orElseThrow(() -> new EntityNotFoundException("Item de rascunho nao encontrado com ID: " + itemDTO.itemRascunhoId()));

            if (!item.getRascunho().getId().equals(rascunhoId)) {
                throw new IllegalArgumentException("Item " + itemDTO.itemRascunhoId() + " nao pertence ao rascunho " + rascunhoId);
            }

            CotacaoRascunhoItem itemCotado = new CotacaoRascunhoItem();
            itemCotado.setItemRascunho(item);
            itemCotado.setPrecoUnitario(itemDTO.precoUnitario());
            itemCotado.setQuantidade(itemDTO.quantidade() != null ? itemDTO.quantidade() : item.getQuantidade());
            itemCotado.setObservacao(itemDTO.observacao());
            itemCotado.setCotacaoRascunho(cotacao);
            itens.add(itemCotado);
        }
        cotacao.setItens(itens);

        if (dto.anexosPdf() != null && !dto.anexosPdf().isEmpty()) {
            int ordem = 0;
            for (byte[] pdfBytes : dto.anexosPdf()) {
                if (pdfBytes != null && pdfBytes.length > 0) {
                    String nomeArquivo = String.format("anexo_%d.pdf", ordem);
                    AnexoCotacaoRascunho anexo = pdfDeduplicationService.createOrReuseRascunhoAnexo(
                        cotacao,
                        pdfBytes,
                        ordem++,
                        nomeArquivo
                    );
                    cotacao.getAnexos().add(anexo);
                }
            }
        } else if (dto.anexoPdf() != null && dto.anexoPdf().length > 0) {
            AnexoCotacaoRascunho anexo = pdfDeduplicationService.createOrReuseRascunhoAnexo(
                cotacao,
                dto.anexoPdf(),
                0,
                "anexo_0.pdf"
            );
            cotacao.getAnexos().add(anexo);
        }

        CotacaoRascunho salva = cotacaoRascunhoRepository.save(cotacao);

        if (rascunho.getStatus() == StatusRascunho.ATIVO) {
            rascunho.setStatus(StatusRascunho.EM_COTACAO);
            rascunhoRepository.save(rascunho);
        }

        return toDTO(salva);
    }

    @Transactional
    public void deletar(Long id) {
        if (!cotacaoRascunhoRepository.existsById(id)) {
            throw new EntityNotFoundException("Cotacao nao encontrada com ID: " + id);
        }
        cotacaoRascunhoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public byte[] obterAnexoPdf(Long id) {
        return obterAnexoPdf(id, 0);
    }

    @Transactional(readOnly = true)
    public byte[] obterAnexoPdf(Long id, int index) {
        CotacaoRascunho cotacao = cotacaoRascunhoRepository.findByIdWithItens(id);
        if (cotacao == null) {
            throw new EntityNotFoundException("Cotacao nao encontrada com ID: " + id);
        }

        if (cotacao.getAnexos() != null && !cotacao.getAnexos().isEmpty()) {
            if (index >= 0 && index < cotacao.getAnexos().size()) {
                return cotacao.getAnexos().get(index).getConteudo();
            }
            throw new EntityNotFoundException("Anexo nao encontrado no indice: " + index);
        }

        throw new EntityNotFoundException("Nenhum anexo encontrado para esta cotacao");
    }

    private CotacaoRascunhoDTO toDTO(CotacaoRascunho cotacao) {
        String nomeFornecedor = "";
        if (cotacao.getFornecedorProduto() != null) {
            nomeFornecedor = cotacao.getFornecedorProduto().getRazaoSocial();
        } else if (cotacao.getFornecedorServico() != null) {
            nomeFornecedor = cotacao.getFornecedorServico().getRazaoSocial();
        }

        List<CotacaoRascunhoItemDTO> itens = cotacao.getItens().stream()
            .map(item -> new CotacaoRascunhoItemDTO(
                item.getId(),
                item.getItemRascunho() != null ? item.getItemRascunho().getId() : null,
                item.getItemRascunho() != null ? item.getItemRascunho().getNumeroItem() : null,
                item.getItemRascunho() != null ? item.getItemRascunho().getNome() : null,
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.calcularPrecoTotal(),
                item.getObservacao()
            ))
            .collect(Collectors.toList());

        List<Long> itensIds = itens.stream()
            .map(CotacaoRascunhoItemDTO::itemRascunhoId)
            .collect(Collectors.toList());

        int quantidadeAnexos = 0;
        if (cotacao.getAnexos() != null && !cotacao.getAnexos().isEmpty()) {
            quantidadeAnexos = (int) cotacao.getAnexos().stream()
                .filter(anexo -> anexo.getConteudo() != null && anexo.getConteudo().length > 0)
                .count();
        }

        boolean temAnexo = quantidadeAnexos > 0;

        return new CotacaoRascunhoDTO(
            cotacao.getId(),
            cotacao.getRascunho().getId(),
            cotacao.getFornecedorId(),
            cotacao.getTipoFornecedor(),
            nomeFornecedor,
            itens,
            itensIds,
            cotacao.getPreco(),
            cotacao.getPrazoEmDiasUteis(),
            cotacao.getDataLimite(),
            cotacao.getGastoPrevisto(),
            cotacao.getProjeto(),
            temAnexo,
            quantidadeAnexos,
            cotacao.getDataCriacao()
        );
    }
}
