package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.rascunho.CotacaoRascunhoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.CotacaoRascunhoDTO;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeServico;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.AnexoCotacaoRascunho;
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
        // Carregar cotações com itens (primeira query)
        List<CotacaoRascunho> cotacoes = cotacaoRascunhoRepository.findByRascunhoIdWithItens(rascunhoId);

        // Se houver cotações, carregar anexos separadamente para evitar produto cartesiano
        if (!cotacoes.isEmpty()) {
            List<Long> ids = cotacoes.stream().map(CotacaoRascunho::getId).collect(Collectors.toList());
            cotacaoRascunhoRepository.findByIdsWithAnexos(ids); // Carrega anexos em segunda query
        }

        return cotacoes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CotacaoRascunhoDTO obterPorId(Long id) {
        CotacaoRascunho cotacao = cotacaoRascunhoRepository.findByIdWithItens(id);
        if (cotacao == null) {
            throw new EntityNotFoundException("Cotação não encontrada com ID: " + id);
        }
        // Carregar anexos separadamente
        cotacaoRascunhoRepository.findByIdsWithAnexos(List.of(id));
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
        // Não usar mais anexoPdf legado - usar apenas a nova estrutura de anexos múltiplos

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

        // Processar múltiplos PDFs
        if (dto.anexosPdf() != null && !dto.anexosPdf().isEmpty()) {
            int ordem = 0;
            for (byte[] pdfBytes : dto.anexosPdf()) {
                if (pdfBytes != null && pdfBytes.length > 0) {
                    AnexoCotacaoRascunho anexo = new AnexoCotacaoRascunho(cotacao, pdfBytes, ordem++);
                    cotacao.getAnexos().add(anexo);
                }
            }
        } else if (dto.anexoPdf() != null && dto.anexoPdf().length > 0) {
            // Compatibilidade com PDF único
            AnexoCotacaoRascunho anexo = new AnexoCotacaoRascunho(cotacao, dto.anexoPdf(), 0);
            cotacao.getAnexos().add(anexo);
        }

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
        return obterAnexoPdf(id, 0);
    }

    @Transactional(readOnly = true)
    public byte[] obterAnexoPdf(Long id, int index) {
        // Usar query que faz fetch dos anexos
        CotacaoRascunho cotacao = cotacaoRascunhoRepository.findByIdWithItens(id);
        if (cotacao == null) {
            throw new EntityNotFoundException("Cotação não encontrada com ID: " + id);
        }

        // Primeiro verificar se há anexos na nova estrutura
        if (cotacao.getAnexos() != null && !cotacao.getAnexos().isEmpty()) {
            if (index >= 0 && index < cotacao.getAnexos().size()) {
                return cotacao.getAnexos().get(index).getConteudo();
            }
            throw new EntityNotFoundException("Anexo não encontrado no índice: " + index);
        }

        // Fallback para o campo antigo (compatibilidade)
        if (index == 0 && cotacao.getAnexoPdf() != null) {
            return cotacao.getAnexoPdf();
        }

        throw new EntityNotFoundException("Nenhum anexo encontrado para esta cotação");
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

        // Calcular quantidade de anexos
        int quantidadeAnexos = 0;
        if (cotacao.getAnexos() != null && !cotacao.getAnexos().isEmpty()) {
            quantidadeAnexos = cotacao.getAnexos().size();
        } else if (cotacao.getAnexoPdf() != null && cotacao.getAnexoPdf().length > 0) {
            quantidadeAnexos = 1;
        }

        boolean temAnexo = quantidadeAnexos > 0;

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
                temAnexo,
                quantidadeAnexos,
                cotacao.getDataCriacao()
        );
    }
}
