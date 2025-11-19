package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.rascunho.CotacaoRascunhoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.CotacaoRascunhoDTO;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeServico;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.CotacaoRascunho;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.ItemRascunho;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.Rascunho;
import com.gestordecompras.gestorcomprasbackend.repository.CotacaoRascunhoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeProdutoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeServicoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.ItemRascunhoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.RascunhoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
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

    @Transactional(readOnly = true)
    public List<CotacaoRascunhoDTO> listarPorRascunho(Long rascunhoId) {
        List<CotacaoRascunho> cotacoes = cotacaoRascunhoRepository.findByRascunhoIdWithItens(rascunhoId);
        return cotacoes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CotacaoRascunhoDTO obterPorId(Long id) {
        CotacaoRascunho cotacao = cotacaoRascunhoRepository.findByIdWithItens(id);
        if (cotacao == null) {
            throw new EntityNotFoundException("Cotação não encontrada com ID: " + id);
        }
        return toDTO(cotacao);
    }

    @Transactional
    public CotacaoRascunhoDTO criar(Long rascunhoId, CotacaoRascunhoCreateDTO dto) {
        Rascunho rascunho = rascunhoRepository.findById(rascunhoId)
                .orElseThrow(() -> new EntityNotFoundException("Rascunho não encontrado com ID: " + rascunhoId));

        CotacaoRascunho cotacao = new CotacaoRascunho();
        cotacao.setRascunho(rascunho);
        cotacao.setPreco(dto.preco());
        cotacao.setPrazoEmDiasUteis(dto.prazoEmDiasUteis());
        cotacao.setDataLimite(dto.dataLimite());
        cotacao.setAnexoPdf(dto.anexoPdf());

        // Definir fornecedor
        if ("PRODUTO".equalsIgnoreCase(dto.tipoFornecedor())) {
            FornecedorDeProduto fornecedor = fornecedorDeProdutoRepository.findById(dto.fornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor de produto não encontrado"));
            cotacao.setFornecedorProduto(fornecedor);
        } else if ("SERVICO".equalsIgnoreCase(dto.tipoFornecedor())) {
            FornecedorDeServico fornecedor = fornecedorDeServicoRepository.findById(dto.fornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor de serviço não encontrado"));
            cotacao.setFornecedorServico(fornecedor);
        } else {
            throw new IllegalArgumentException("Tipo de fornecedor inválido: " + dto.tipoFornecedor());
        }

        // Associar itens do rascunho
        Set<ItemRascunho> itens = new HashSet<>();
        for (Long itemId : dto.itensRascunhoIds()) {
            ItemRascunho item = itemRascunhoRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("Item de rascunho não encontrado com ID: " + itemId));

            // Validar que o item pertence ao rascunho
            if (!item.getRascunho().getId().equals(rascunhoId)) {
                throw new IllegalArgumentException("Item " + itemId + " não pertence ao rascunho " + rascunhoId);
            }
            itens.add(item);
        }
        cotacao.setItensRascunho(itens);

        CotacaoRascunho salva = cotacaoRascunhoRepository.save(cotacao);
        return toDTO(salva);
    }

    @Transactional
    public void deletar(Long id) {
        if (!cotacaoRascunhoRepository.existsById(id)) {
            throw new EntityNotFoundException("Cotação não encontrada com ID: " + id);
        }
        cotacaoRascunhoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public byte[] obterAnexoPdf(Long id) {
        CotacaoRascunho cotacao = cotacaoRascunhoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + id));
        return cotacao.getAnexoPdf();
    }

    private CotacaoRascunhoDTO toDTO(CotacaoRascunho cotacao) {
        String nomeFornecedor = "";
        if (cotacao.getFornecedorProduto() != null) {
            nomeFornecedor = cotacao.getFornecedorProduto().getRazaoSocial();
        } else if (cotacao.getFornecedorServico() != null) {
            nomeFornecedor = cotacao.getFornecedorServico().getRazaoSocial();
        }

        List<Long> itensIds = cotacao.getItensRascunho().stream()
                .map(ItemRascunho::getId)
                .collect(Collectors.toList());

        return new CotacaoRascunhoDTO(
                cotacao.getId(),
                cotacao.getRascunho().getId(),
                cotacao.getFornecedorId(),
                cotacao.getTipoFornecedor(),
                nomeFornecedor,
                itensIds,
                cotacao.getPreco(),
                cotacao.getPrazoEmDiasUteis(),
                cotacao.getDataLimite(),
                cotacao.getAnexoPdf() != null && cotacao.getAnexoPdf().length > 0,
                cotacao.getDataCriacao()
        );
    }
}
