package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.CotacaoMapper;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.repository.CotacaoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeProdutoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeServicoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.ItemPedidoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.SolicitacaoDePedidoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final HistoricoPedidoService historicoPedidoService;
    private final UserRepository userRepository;

    public CotacaoService(CotacaoRepository cotacaoRepository, CotacaoMapper cotacaoMapper,
                         FornecedorDeProdutoRepository fornecedorDeProdutoRepository,
                         FornecedorDeServicoRepository fornecedorDeServicoRepository,
                         ItemPedidoRepository itemPedidoRepository,
                         SolicitacaoDePedidoRepository solicitacaoDePedidoRepository,
                         HistoricoPedidoService historicoPedidoService,
                         UserRepository userRepository) {
        this.cotacaoRepository = cotacaoRepository;
        this.cotacaoMapper = cotacaoMapper;
        this.fornecedorDeProdutoRepository = fornecedorDeProdutoRepository;
        this.fornecedorDeServicoRepository = fornecedorDeServicoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.solicitacaoDePedidoRepository = solicitacaoDePedidoRepository;
        this.historicoPedidoService = historicoPedidoService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return userRepository.findByUsername(username);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<CotacaoDTO> getAllCotacoes() {
        // Bug Fix #9: Usar query otimizada para evitar N+1
        return cotacaoRepository.findAllWithRelationships().stream()
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
        // Bug Fix #6: Validar que a cotação tem itens
        if (cotacaoCreateDTO.itensPedidoIds() == null || cotacaoCreateDTO.itensPedidoIds().isEmpty()) {
            throw new IllegalArgumentException("Uma cotação deve ter pelo menos um item vinculado");
        }

        Cotacao cotacao = cotacaoMapper.toEntity(cotacaoCreateDTO);

        // Buscar a solicitação de pedido
        SolicitacaoDePedido solicitacaoDePedido = solicitacaoDePedidoRepository
                .findById(cotacaoCreateDTO.solicitacaoDePedidoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Solicitação de Pedido não encontrada com ID: " + cotacaoCreateDTO.solicitacaoDePedidoId()));

        cotacao.setSolicitacaoDePedido(solicitacaoDePedido);

        // Bug Fix #7: Validar tipo de fornecedor e garantir exclusividade
        if ("PRODUTO".equals(cotacaoCreateDTO.tipoFornecedor())) {
            var fornecedorProduto = fornecedorDeProdutoRepository.findById(cotacaoCreateDTO.fornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor de Produto não encontrado"));
            cotacao.setFornecedorProduto(fornecedorProduto);
            cotacao.setFornecedorServico(null); // Garantir exclusividade
        } else if ("SERVICO".equals(cotacaoCreateDTO.tipoFornecedor())) {
            var fornecedorServico = fornecedorDeServicoRepository.findById(cotacaoCreateDTO.fornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor de Serviço não encontrado"));
            cotacao.setFornecedorServico(fornecedorServico);
            cotacao.setFornecedorProduto(null); // Garantir exclusividade
        } else {
            throw new IllegalArgumentException("Tipo de fornecedor inválido: " + cotacaoCreateDTO.tipoFornecedor());
        }

        // Bug Fix #7: Validar que não há ambos os tipos de fornecedor
        if (cotacao.getFornecedorProduto() != null && cotacao.getFornecedorServico() != null) {
            throw new IllegalArgumentException("Uma cotação não pode ter simultaneamente fornecedor de produto e de serviço");
        }

        // Bug Fix #7: Validar que há pelo menos um fornecedor
        if (cotacao.getFornecedorProduto() == null && cotacao.getFornecedorServico() == null) {
            throw new IllegalArgumentException("Uma cotação deve ter um fornecedor (produto ou serviço)");
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

        Cotacao cotacaoSalva = cotacaoRepository.save(cotacao);

        // Registrar criação da cotação no histórico
        try {
            User usuario = getAuthenticatedUser();
            if (usuario != null) {
                String nomeFornecedor = cotacao.getFornecedorProduto() != null
                    ? cotacao.getFornecedorProduto().getRazaoSocial()
                    : cotacao.getFornecedorServico().getRazaoSocial();

                historicoPedidoService.registrarAdicaoCotacao(
                    solicitacaoDePedido,
                    usuario,
                    nomeFornecedor
                );
            }
        } catch (Exception e) {
            System.err.println("Erro ao registrar histórico: " + e.getMessage());
        }

        return cotacaoMapper.toDTO(cotacaoSalva);
    }

    @Transactional
    public CotacaoDTO updateCotacao(Long id, CotacaoUpdateDTO cotacaoUpdateDTO) {
        return cotacaoRepository.findById(id)
                .map(existingCotacao -> {
                    // Capturar valores anteriores para comparação
                    boolean houveAlteracao = false;
                    StringBuilder detalhes = new StringBuilder();

                    if (cotacaoUpdateDTO.preco() != null && !cotacaoUpdateDTO.preco().equals(existingCotacao.getPreco())) {
                        detalhes.append("Preço: ").append(existingCotacao.getPreco()).append(" → ").append(cotacaoUpdateDTO.preco());
                        existingCotacao.setPreco(cotacaoUpdateDTO.preco());
                        houveAlteracao = true;
                    }
                    if (cotacaoUpdateDTO.prazoEmDiasUteis() != null && !cotacaoUpdateDTO.prazoEmDiasUteis().equals(existingCotacao.getPrazoEmDiasUteis())) {
                        if (detalhes.length() > 0) detalhes.append(", ");
                        detalhes.append("Prazo: ").append(existingCotacao.getPrazoEmDiasUteis()).append(" → ").append(cotacaoUpdateDTO.prazoEmDiasUteis());
                        existingCotacao.setPrazoEmDiasUteis(cotacaoUpdateDTO.prazoEmDiasUteis());
                        houveAlteracao = true;
                    }
                    if (cotacaoUpdateDTO.dataLimite() != null && !cotacaoUpdateDTO.dataLimite().equals(existingCotacao.getDataLimite())) {
                        if (detalhes.length() > 0) detalhes.append(", ");
                        detalhes.append("Data Limite alterada");
                        existingCotacao.setDataLimite(cotacaoUpdateDTO.dataLimite());
                        houveAlteracao = true;
                    }
                    if (cotacaoUpdateDTO.anexoPdf() != null) {
                        if (detalhes.length() > 0) detalhes.append(", ");
                        detalhes.append("Anexo PDF atualizado");
                        existingCotacao.setAnexoPdf(cotacaoUpdateDTO.anexoPdf());
                        houveAlteracao = true;
                    }
                    if (cotacaoUpdateDTO.caminhoAnexo() != null) {
                        existingCotacao.setCaminhoAnexo(cotacaoUpdateDTO.caminhoAnexo());
                    }

                    Cotacao cotacaoSalva = cotacaoRepository.save(existingCotacao);

                    // Registrar no histórico se houve alteração
                    if (houveAlteracao) {
                        try {
                            User usuario = getAuthenticatedUser();
                            if (usuario != null) {
                                String nomeFornecedor = existingCotacao.getFornecedorProduto() != null
                                    ? existingCotacao.getFornecedorProduto().getRazaoSocial()
                                    : existingCotacao.getFornecedorServico().getRazaoSocial();

                                historicoPedidoService.registrarEdicaoCotacao(
                                    existingCotacao.getSolicitacaoDePedido(),
                                    usuario,
                                    nomeFornecedor,
                                    detalhes.toString()
                                );
                            }
                        } catch (Exception e) {
                            // Não falhar a operação se não conseguir registrar no histórico
                            System.err.println("Erro ao registrar histórico: " + e.getMessage());
                        }
                    }

                    return cotacaoMapper.toDTO(cotacaoSalva);
                })
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + id));
    }

    @Transactional
    public void deleteCotacao(Long id) {
        Cotacao cotacao = cotacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + id));

        // Capturar dados antes de deletar
        SolicitacaoDePedido pedido = cotacao.getSolicitacaoDePedido();
        String nomeFornecedor = cotacao.getFornecedorProduto() != null
            ? cotacao.getFornecedorProduto().getRazaoSocial()
            : cotacao.getFornecedorServico().getRazaoSocial();

        cotacaoRepository.deleteById(id);

        // Registrar remoção da cotação no histórico
        try {
            User usuario = getAuthenticatedUser();
            if (usuario != null) {
                historicoPedidoService.registrarRemocaoCotacao(
                    pedido,
                    usuario,
                    nomeFornecedor
                );
            }
        } catch (Exception e) {
            System.err.println("Erro ao registrar histórico: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public byte[] obterAnexoPdf(Long id) {
        return obterAnexoPdf(id, 0);
    }

    @Transactional(readOnly = true)
    public byte[] obterAnexoPdf(Long id, int index) {
        Cotacao cotacao = cotacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + id));

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

    // Bug Fix #10: Adicionar retry logic para lidar com conflitos de concorrência
    @Retryable(
        retryFor = {OptimisticLockException.class, OptimisticLockingFailureException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    @Transactional
    public CotacaoDTO vincularItens(Long cotacaoId, List<Long> itensPedidoIds) {
        Cotacao cotacao = cotacaoRepository.findById(cotacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + cotacaoId));

        // Bug Fix #4: Validar itens duplicados
        if (itensPedidoIds.size() != new HashSet<>(itensPedidoIds).size()) {
            throw new IllegalArgumentException("A lista de IDs de itens contém duplicatas");
        }

        // Bug Fix #2: Usar estratégia de diff ao invés de clear + add para evitar problemas de sincronização
        Set<Long> idsAtuais = cotacao.getItensPedido().stream()
                .map(ItemPedido::getId)
                .collect(java.util.stream.Collectors.toSet());

        Set<Long> idsNovos = new HashSet<>(itensPedidoIds);

        // Remover apenas os itens que não estão mais vinculados
        cotacao.getItensPedido().removeIf(item -> !idsNovos.contains(item.getId()));

        // Adicionar apenas os itens novos que não existiam antes
        for (Long itemId : idsNovos) {
            if (!idsAtuais.contains(itemId)) {
                ItemPedido item = itemPedidoRepository.findById(itemId)
                        .orElseThrow(() -> new EntityNotFoundException("ItemPedido não encontrado com ID: " + itemId));

                // Validar que o item pertence à mesma solicitação de pedido
                if (!item.getSolicitacaoDePedido().getId().equals(cotacao.getSolicitacaoDePedido().getId())) {
                    throw new IllegalArgumentException(
                            "Item " + itemId + " não pertence à solicitação de pedido " +
                            cotacao.getSolicitacaoDePedido().getId());
                }

                cotacao.getItensPedido().add(item);
            }
        }

        // Flush para garantir sincronização antes do save
        cotacaoRepository.flush();

        Cotacao cotacaoAtualizada = cotacaoRepository.save(cotacao);

        return cotacaoMapper.toDTO(cotacaoAtualizada);
    }
}
