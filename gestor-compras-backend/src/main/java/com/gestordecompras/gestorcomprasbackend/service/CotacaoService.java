package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.CotacaoMapper;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.repository.CotacaoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeProdutoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeServicoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.ItemPedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CotacaoService {

    private final CotacaoRepository cotacaoRepository;
    private final CotacaoMapper cotacaoMapper;
    private final FornecedorDeProdutoRepository fornecedorDeProdutoRepository;
    private final FornecedorDeServicoRepository fornecedorDeServicoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public CotacaoService(CotacaoRepository cotacaoRepository, CotacaoMapper cotacaoMapper, FornecedorDeProdutoRepository fornecedorDeProdutoRepository, FornecedorDeServicoRepository fornecedorDeServicoRepository, ItemPedidoRepository itemPedidoRepository) {
        this.cotacaoRepository = cotacaoRepository;
        this.cotacaoMapper = cotacaoMapper;
        this.fornecedorDeProdutoRepository = fornecedorDeProdutoRepository;
        this.fornecedorDeServicoRepository = fornecedorDeServicoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<CotacaoDTO> getAllCotacoes() {
        return cotacaoRepository.findAll().stream()
                .map(cotacaoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CotacaoDTO getCotacaoById(Long id) {
        return cotacaoRepository.findById(id)
                .map(cotacaoMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + id));
    }

    @Transactional
    public CotacaoDTO createCotacao(CotacaoCreateDTO cotacaoCreateDTO) {
        Cotacao cotacao = cotacaoMapper.toEntity(cotacaoCreateDTO);

        // Tenta buscar como fornecedor de produto primeiro
        var fornecedorProduto = fornecedorDeProdutoRepository.findById(cotacaoCreateDTO.fornecedorId());
        if (fornecedorProduto.isPresent()) {
            cotacao.setFornecedorProduto(fornecedorProduto.get());
        } else {
            // Se não encontrar, busca como fornecedor de serviço
            var fornecedorServico = fornecedorDeServicoRepository.findById(cotacaoCreateDTO.fornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor not found"));
            cotacao.setFornecedorServico(fornecedorServico);
        }

        ItemPedido itemPedido = itemPedidoRepository.findById(cotacaoCreateDTO.itemPedidoId())
                .orElseThrow(() -> new EntityNotFoundException("ItemPedido not found"));

        cotacao.setItemPedido(itemPedido);

        return cotacaoMapper.toDTO(cotacaoRepository.save(cotacao));
    }

    @Transactional
    public CotacaoDTO updateCotacao(Long id, CotacaoUpdateDTO cotacaoUpdateDTO) {
        return cotacaoRepository.findById(id)
                .map(existingCotacao -> {
                    if (cotacaoUpdateDTO.preco() != null) {
                        existingCotacao.setPreco(cotacaoUpdateDTO.preco());
                    }
                    if (cotacaoUpdateDTO.prazoEntrega() != null) {
                        existingCotacao.setPrazoEntrega(cotacaoUpdateDTO.prazoEntrega());
                    }
                    if (cotacaoUpdateDTO.anexoPdf() != null) {
                        existingCotacao.setAnexoPdf(cotacaoUpdateDTO.anexoPdf());
                    }
                    if (cotacaoUpdateDTO.caminhoAnexo() != null) {
                        existingCotacao.setCaminhoAnexo(cotacaoUpdateDTO.caminhoAnexo());
                    }
                    return cotacaoMapper.toDTO(cotacaoRepository.save(existingCotacao));
                })
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + id));
    }

    @Transactional
    public void deleteCotacao(Long id) {
        if (!cotacaoRepository.existsById(id)) {
            throw new EntityNotFoundException("Cotação não encontrada com ID: " + id);
        }
        cotacaoRepository.deleteById(id);
    }
}
