package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.cotacao.*;
import com.gestordecompras.gestorcomprasbackend.mapper.CotacaoItemMapper;
import com.gestordecompras.gestorcomprasbackend.mapper.CotacaoMapper;
import com.gestordecompras.gestorcomprasbackend.mapper.HistoricoCotacaoMapper;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.AnexoCotacao;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.CotacaoItem;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.HistoricoCotacao;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final CotacaoItemMapper cotacaoItemMapper;
    private final FornecedorDeProdutoRepository fornecedorDeProdutoRepository;
    private final FornecedorDeServicoRepository fornecedorDeServicoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final SolicitacaoDePedidoRepository solicitacaoDePedidoRepository;
    private final HistoricoPedidoService historicoPedidoService;
    private final UserRepository userRepository;
    private final HistoricoCotacaoRepository historicoCotacaoRepository;
    private final HistoricoCotacaoMapper historicoCotacaoMapper;

    public CotacaoService(CotacaoRepository cotacaoRepository, CotacaoMapper cotacaoMapper,
                         CotacaoItemMapper cotacaoItemMapper,
                         FornecedorDeProdutoRepository fornecedorDeProdutoRepository,
                         FornecedorDeServicoRepository fornecedorDeServicoRepository,
                         ItemPedidoRepository itemPedidoRepository,
                         SolicitacaoDePedidoRepository solicitacaoDePedidoRepository,
                         HistoricoPedidoService historicoPedidoService,
                         UserRepository userRepository,
                         HistoricoCotacaoRepository historicoCotacaoRepository,
                         HistoricoCotacaoMapper historicoCotacaoMapper) {
        this.cotacaoRepository = cotacaoRepository;
        this.cotacaoMapper = cotacaoMapper;
        this.cotacaoItemMapper = cotacaoItemMapper;
        this.fornecedorDeProdutoRepository = fornecedorDeProdutoRepository;
        this.fornecedorDeServicoRepository = fornecedorDeServicoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.solicitacaoDePedidoRepository = solicitacaoDePedidoRepository;
        this.historicoPedidoService = historicoPedidoService;
        this.userRepository = userRepository;
        this.historicoCotacaoRepository = historicoCotacaoRepository;
        this.historicoCotacaoMapper = historicoCotacaoMapper;
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
    public Page<CotacaoDTO> getAllCotacoes(Pageable pageable) {
        // Bug Fix #9: Usar query otimizada para evitar N+1
        return cotacaoRepository.findAll(pageable).map(cotacaoMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public CotacaoDTO getCotacaoById(Long id) {
        return cotacaoRepository.findById(id)
                .map(cotacaoMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + id));
    }

    @Transactional
    public CotacaoDTO createCotacao(CotacaoCreateDTO cotacaoCreateDTO) {
        // Validar que foi fornecido pelo menos um formato (novo ou legacy)
        cotacaoCreateDTO.validar();

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

        // Associar itens: suporta formato novo (recomendado) e legacy
        if (cotacaoCreateDTO.usaNovoFormato()) {
            // NOVO FORMATO: Itens com preços individuais
            for (CotacaoItemCreateDTO itemDTO : cotacaoCreateDTO.itens()) {
                ItemPedido itemPedido = itemPedidoRepository.findById(itemDTO.itemPedidoId())
                        .orElseThrow(() -> new EntityNotFoundException("ItemPedido não encontrado com ID: " + itemDTO.itemPedidoId()));

                // Validar que o item pertence à solicitação
                if (!itemPedido.getSolicitacaoDePedido().getId().equals(cotacaoCreateDTO.solicitacaoDePedidoId())) {
                    throw new IllegalArgumentException(
                            "Item " + itemDTO.itemPedidoId() + " não pertence à solicitação de pedido " + cotacaoCreateDTO.solicitacaoDePedidoId());
                }

                // Criar CotacaoItem com preço individual
                CotacaoItem cotacaoItem = cotacaoItemMapper.toEntity(itemDTO);
                cotacaoItem.setItemPedido(itemPedido);
                cotacao.addItem(cotacaoItem);
            }
        } else {
            // LEGACY FORMATO: IDs dos itens + preço total (dividido igualmente)
            List<Long> itensPedidoIds = cotacaoCreateDTO.itensPedidoIds();
            int totalItens = itensPedidoIds.size();
            java.math.BigDecimal precoTotalLegacy = cotacaoCreateDTO.preco();
            java.math.BigDecimal precoUnitario = precoTotalLegacy.divide(
                    java.math.BigDecimal.valueOf(totalItens),
                    2,
                    java.math.RoundingMode.HALF_UP
            );

            for (Long itemId : itensPedidoIds) {
                ItemPedido itemPedido = itemPedidoRepository.findById(itemId)
                        .orElseThrow(() -> new EntityNotFoundException("ItemPedido não encontrado com ID: " + itemId));

                // Validar que o item pertence à solicitação
                if (!itemPedido.getSolicitacaoDePedido().getId().equals(cotacaoCreateDTO.solicitacaoDePedidoId())) {
                    throw new IllegalArgumentException(
                            "Item " + itemId + " não pertence à solicitação de pedido " + cotacaoCreateDTO.solicitacaoDePedidoId());
                }

                // Criar CotacaoItem com preço dividido
                CotacaoItem cotacaoItem = new CotacaoItem();
                cotacaoItem.setItemPedido(itemPedido);
                cotacaoItem.setPrecoUnitario(precoUnitario);
                cotacaoItem.setQuantidade(itemPedido.getQuantidade());
                cotacaoItem.setObservacao("Migrado de formato legacy com preço dividido igualmente");
                cotacao.addItem(cotacaoItem);
            }
        }

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

        // Validar itens duplicados
        if (itensPedidoIds.size() != new HashSet<>(itensPedidoIds).size()) {
            throw new IllegalArgumentException("A lista de IDs de itens contém duplicatas");
        }

        // Usar estratégia de diff com nova estrutura CotacaoItem
        Set<Long> idsAtuais = cotacao.getItens().stream()
                .map(ci -> ci.getItemPedido().getId())
                .collect(java.util.stream.Collectors.toSet());

        Set<Long> idsNovos = new HashSet<>(itensPedidoIds);

        // Remover apenas os CotacaoItem que não estão mais vinculados
        cotacao.getItens().removeIf(cotacaoItem -> !idsNovos.contains(cotacaoItem.getItemPedido().getId()));

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

                // Criar CotacaoItem com preço padrão (será atualizado depois)
                CotacaoItem cotacaoItem = new CotacaoItem();
                cotacaoItem.setItemPedido(item);
                cotacaoItem.setQuantidade(item.getQuantidade());
                cotacaoItem.setPrecoUnitario(java.math.BigDecimal.ZERO); // Placeholder
                cotacaoItem.setObservacao("Adicionado via vincularItens - atualizar preço");
                cotacao.addItem(cotacaoItem);
            }
        }

        // Flush para garantir sincronização antes do save
        cotacaoRepository.flush();

        Cotacao cotacaoAtualizada = cotacaoRepository.save(cotacao);

        return cotacaoMapper.toDTO(cotacaoAtualizada);
    }

    /**
     * Edita uma cotação existente com auditoria completa
     * Cria registro no histórico antes de aplicar as mudanças
     *
     * @param editDTO Dados da edição com motivo obrigatório
     * @return Cotação atualizada
     */
    @Transactional
    @Retryable(
        retryFor = {OptimisticLockingFailureException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 100)
    )
    public CotacaoDTO editarCotacao(CotacaoEditDTO editDTO) {
        // Busca cotação existente
        Cotacao cotacao = cotacaoRepository.findById(editDTO.id())
            .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada: " + editDTO.id()));

        // Verifica se há mudanças reais
        if (!editDTO.temMudancas()) {
            throw new IllegalArgumentException("Nenhuma mudança foi especificada na edição");
        }

        // Cria registro de histórico ANTES de aplicar mudanças
        criarHistoricoCotacao(cotacao, editDTO);

        // Atualiza dados da cotação
        if (editDTO.itens() != null && !editDTO.itens().isEmpty()) {
            // Remove itens antigos
            cotacao.getItens().clear();

            // Adiciona novos itens
            for (CotacaoItemCreateDTO itemDTO : editDTO.itens()) {
                ItemPedido itemPedido = itemPedidoRepository.findById(itemDTO.itemPedidoId())
                    .orElseThrow(() -> new EntityNotFoundException("Item de pedido não encontrado: " + itemDTO.itemPedidoId()));

                CotacaoItem novoItem = new CotacaoItem();
                novoItem.setItemPedido(itemPedido);
                novoItem.setPrecoUnitario(itemDTO.precoUnitario());
                novoItem.setQuantidade(itemDTO.quantidade());
                novoItem.setObservacao(itemDTO.observacao());

                cotacao.addItem(novoItem);
            }
            // Atualiza preço total baseado nos itens

        } else if (editDTO.precoNovo() != null) {
            // Atualiza apenas o preço total (sem modificar itens)
            // Se a cotação tem itens, distribui o novo preço proporcionalmente
            if (cotacao.getItens() != null && !cotacao.getItens().isEmpty()) {
                BigDecimal precoAtual = cotacao.getPreco();
                if (precoAtual.compareTo(BigDecimal.ZERO) > 0) {
                    // Calcula fator de multiplicação
                    BigDecimal fator = editDTO.precoNovo().divide(precoAtual, 10, RoundingMode.HALF_UP);

                    // Atualiza preço unitário de cada item proporcionalmente
                    for (CotacaoItem item : cotacao.getItens()) {
                        BigDecimal novoPrecoUnitario = item.getPrecoUnitario().multiply(fator);
                        item.setPrecoUnitario(novoPrecoUnitario);
                    }
                } else {
                    // Se preço atual é zero, divide igualmente entre os itens
                    int totalQuantidade = cotacao.getItens().stream()
                        .mapToInt(CotacaoItem::getQuantidade)
                        .sum();

                    if (totalQuantidade > 0) {
                        BigDecimal precoUnitario = editDTO.precoNovo()
                            .divide(BigDecimal.valueOf(totalQuantidade), 2, RoundingMode.HALF_UP);

                        for (CotacaoItem item : cotacao.getItens()) {
                            item.setPrecoUnitario(precoUnitario);
                        }
                    }
                }
            } else {
                // Se não tem itens, usa precoLegacy

            }
        }

        if (editDTO.prazoEmDiasUteis() != null) {
            cotacao.setPrazoEmDiasUteis(editDTO.prazoEmDiasUteis());
        }

        if (editDTO.dataLimite() != null) {
            cotacao.setDataLimite(editDTO.dataLimite());
        }

        if (editDTO.anexoPdf() != null) {
            cotacao.getAnexos().clear();
            AnexoCotacao novoAnexo = new AnexoCotacao(cotacao, editDTO.anexoPdf(), cotacao.getAnexos().size());
            cotacao.getAnexos().add(novoAnexo);
        }

        // Atualiza campos de auditoria
        cotacao.setNumeroVersao((cotacao.getNumeroVersao() != null ? cotacao.getNumeroVersao() : 0) + 1);
        cotacao.setFoiEditada(true);
        cotacao.setDataUltimaEdicao(LocalDateTime.now());
        cotacao.setMotivoUltimaEdicao(editDTO.motivoEdicao());
        cotacao.setEditadoPor(editDTO.editadoPor());

        // Salva cotação atualizada
        Cotacao cotacaoAtualizada = cotacaoRepository.save(cotacao);

        // Registra edição no histórico do pedido
        if (cotacaoAtualizada.getSolicitacaoDePedido() != null) {
            User usuario = getAuthenticatedUser();
            if (usuario != null) {
                String nomeFornecedor = cotacaoAtualizada.getFornecedorProduto() != null
                    ? cotacaoAtualizada.getFornecedorProduto().getRazaoSocial()
                    : (cotacaoAtualizada.getFornecedorServico() != null
                        ? cotacaoAtualizada.getFornecedorServico().getRazaoSocial()
                        : "Fornecedor não identificado");

                // Constrói detalhes do que foi modificado
                StringBuilder detalhes = new StringBuilder();
                if (editDTO.precoNovo() != null) {
                    detalhes.append("Preço alterado");
                }
                if (editDTO.prazoEmDiasUteis() != null) {
                    if (detalhes.length() > 0) detalhes.append(", ");
                    detalhes.append("Prazo alterado");
                }
                if (editDTO.dataLimite() != null) {
                    if (detalhes.length() > 0) detalhes.append(", ");
                    detalhes.append("Data limite alterada");
                }
                if (editDTO.anexoPdf() != null) {
                    if (detalhes.length() > 0) detalhes.append(", ");
                    detalhes.append("PDF anexado");
                }

                historicoPedidoService.registrarEdicaoCotacao(
                    cotacaoAtualizada.getSolicitacaoDePedido(),
                    usuario,
                    nomeFornecedor,
                    detalhes.length() > 0 ? detalhes.toString() : "Modificações diversas"
                );
            }
        }

        return cotacaoMapper.toDTO(cotacaoAtualizada);
    }

    /**
     * Cria registro no histórico de cotações
     */
    private void criarHistoricoCotacao(Cotacao cotacaoAnterior, CotacaoEditDTO editDTO) {
        HistoricoCotacao historico = new HistoricoCotacao();

        historico.setCotacaoId(cotacaoAnterior.getId());
        historico.setVersao(cotacaoAnterior.getNumeroVersao() != null ? cotacaoAnterior.getNumeroVersao() : 1);

        // Dados anteriores (antes da edição)
        historico.setPrecoAnterior(cotacaoAnterior.getPreco());
        historico.setPrazoEmDiasUteisAnterior(cotacaoAnterior.getPrazoEmDiasUteis());
        historico.setDataLimiteAnterior(cotacaoAnterior.getDataLimite());
        historico.setAnexoPdfAnterior(cotacaoAnterior.getAnexoPdf());
        historico.setCaminhoAnexoAnterior(cotacaoAnterior.getCaminhoAnexo());

        // Dados novos (após edição)
        // Determina o novo preço: usa precoNovo se fornecido, senão calcula dos itens, senão mantém o atual
        BigDecimal novoPreco;
        if (editDTO.precoNovo() != null) {
            novoPreco = editDTO.precoNovo();
        } else if (editDTO.itens() != null && !editDTO.itens().isEmpty()) {
            novoPreco = editDTO.calcularPrecoTotal();
        } else {
            novoPreco = cotacaoAnterior.getPreco();
        }
        historico.setPrecoNovo(novoPreco);

        historico.setPrazoEmDiasUteisNovo(
            editDTO.prazoEmDiasUteis() != null ? editDTO.prazoEmDiasUteis() : cotacaoAnterior.getPrazoEmDiasUteis()
        );
        historico.setDataLimiteNovo(
            editDTO.dataLimite() != null ? editDTO.dataLimite() : cotacaoAnterior.getDataLimite()
        );
        historico.setAnexoPdfNovo(editDTO.anexoPdf());

        // Informações de auditoria
        historico.setMotivoEdicao(editDTO.motivoEdicao());
        historico.setEditadoPor(editDTO.editadoPor());
        historico.setDataEdicao(LocalDateTime.now());

        historicoCotacaoRepository.saveAndFlush(historico);
    }



    /**
     * Busca o histórico completo de edições de uma cotação
     */
    @Transactional(readOnly = true)
    public List<HistoricoCotacaoDTO> buscarHistoricoCotacao(Long cotacaoId) {
        return historicoCotacaoRepository.findByCotacaoIdOrderByDataEdicaoDesc(cotacaoId)
            .stream()
            .map(historicoCotacaoMapper::toDTO)
            .toList();
    }

    /**
     * Obtém PDF anterior do histórico
     */
    public byte[] obterPdfAnteriorHistorico(Long historicoId) {
        HistoricoCotacao historico = historicoCotacaoRepository.findById(historicoId)
            .orElseThrow(() -> new EntityNotFoundException("Histórico não encontrado: " + historicoId));
        return historico.getAnexoPdfAnterior();
    }

    /**
     * Obtém PDF novo do histórico
     */
    public byte[] obterPdfNovoHistorico(Long historicoId) {
        HistoricoCotacao historico = historicoCotacaoRepository.findById(historicoId)
            .orElseThrow(() -> new EntityNotFoundException("Histórico não encontrado: " + historicoId));
        return historico.getAnexoPdfNovo();
    }
}
