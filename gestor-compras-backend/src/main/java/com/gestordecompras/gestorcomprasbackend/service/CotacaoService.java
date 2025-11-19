package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.CotacaoMapper;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.repository.CotacaoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeProdutoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeServicoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.ItemPedidoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.SolicitacaoDePedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CotacaoService {

    private final CotacaoRepository cotacaoRepository;
    private final CotacaoMapper cotacaoMapper;
    private final FornecedorDeProdutoRepository fornecedorDeProdutoRepository;
    private final FornecedorDeServicoRepository fornecedorDeServicoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final SolicitacaoDePedidoRepository solicitacaoDePedidoRepository;

    public CotacaoService(CotacaoRepository cotacaoRepository, CotacaoMapper cotacaoMapper,
                         FornecedorDeProdutoRepository fornecedorDeProdutoRepository,
                         FornecedorDeServicoRepository fornecedorDeServicoRepository,
                         ItemPedidoRepository itemPedidoRepository,
                         SolicitacaoDePedidoRepository solicitacaoDePedidoRepository) {
        this.cotacaoRepository = cotacaoRepository;
        this.cotacaoMapper = cotacaoMapper;
        this.fornecedorDeProdutoRepository = fornecedorDeProdutoRepository;
        this.fornecedorDeServicoRepository = fornecedorDeServicoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.solicitacaoDePedidoRepository = solicitacaoDePedidoRepository;
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

        // Buscar a solicitação de pedido
        SolicitacaoDePedido solicitacaoDePedido = solicitacaoDePedidoRepository
                .findById(cotacaoCreateDTO.solicitacaoDePedidoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Solicitação de Pedido não encontrada com ID: " + cotacaoCreateDTO.solicitacaoDePedidoId()));

        cotacao.setSolicitacaoDePedido(solicitacaoDePedido);

        // Usa o tipo de fornecedor para buscar corretamente
        if ("PRODUTO".equals(cotacaoCreateDTO.tipoFornecedor())) {
            var fornecedorProduto = fornecedorDeProdutoRepository.findById(cotacaoCreateDTO.fornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor de Produto não encontrado"));
            cotacao.setFornecedorProduto(fornecedorProduto);
        } else if ("SERVICO".equals(cotacaoCreateDTO.tipoFornecedor())) {
            var fornecedorServico = fornecedorDeServicoRepository.findById(cotacaoCreateDTO.fornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor de Serviço não encontrado"));
            cotacao.setFornecedorServico(fornecedorServico);
        } else {
            throw new IllegalArgumentException("Tipo de fornecedor inválido: " + cotacaoCreateDTO.tipoFornecedor());
        }

        // Buscar e associar múltiplos itens do pedido
        Set<ItemPedido> itensPedido = new HashSet<>();
        for (Long itemId : cotacaoCreateDTO.itensPedidoIds()) {
            ItemPedido itemPedido = itemPedidoRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("ItemPedido não encontrado com ID: " + itemId));

            // Validar que o item pertence à solicitação de pedido informada
            if (!itemPedido.getSolicitacaoDePedido().getId().equals(cotacaoCreateDTO.solicitacaoDePedidoId())) {
                throw new IllegalArgumentException(
                        "Item " + itemId + " não pertence à solicitação de pedido " + cotacaoCreateDTO.solicitacaoDePedidoId());
            }

            itensPedido.add(itemPedido);
        }

        cotacao.setItensPedido(itensPedido);

        return cotacaoMapper.toDTO(cotacaoRepository.save(cotacao));
    }

    @Transactional
    public CotacaoDTO updateCotacao(Long id, CotacaoUpdateDTO cotacaoUpdateDTO) {
        return cotacaoRepository.findById(id)
                .map(existingCotacao -> {
                    if (cotacaoUpdateDTO.preco() != null) {
                        existingCotacao.setPreco(cotacaoUpdateDTO.preco());
                    }
                    if (cotacaoUpdateDTO.prazoEmDiasUteis() != null) {
                        existingCotacao.setPrazoEmDiasUteis(cotacaoUpdateDTO.prazoEmDiasUteis());
                    }
                    if (cotacaoUpdateDTO.dataLimite() != null) {
                        existingCotacao.setDataLimite(cotacaoUpdateDTO.dataLimite());
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
