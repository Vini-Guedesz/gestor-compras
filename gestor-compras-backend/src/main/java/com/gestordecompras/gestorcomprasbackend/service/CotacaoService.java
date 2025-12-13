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

/**
 * Serviço responsável pelo gerenciamento de cotações.
 * <p>
 * Gerencia o ciclo de vida completo das cotações, incluindo criação, atualização,
 * versionamento, auditoria, uploads de anexos e vinculação com pedidos.
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
 */
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
    private final PdfDeduplicationService pdfDeduplicationService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param cotacaoRepository Repositório de cotações.
     * @param cotacaoMapper Mapper para conversão entre entidade e DTO.
     * @param cotacaoItemMapper Mapper para itens de cotação.
     * @param fornecedorDeProdutoRepository Repositório de fornecedores de produto.
     * @param fornecedorDeServicoRepository Repositório de fornecedores de serviço.
     * @param itemPedidoRepository Repositório de itens de pedido.
     * @param solicitacaoDePedidoRepository Repositório de solicitações de pedido.
     * @param historicoPedidoService Serviço de histórico de pedidos.
     * @param userRepository Repositório de usuários.
     * @param historicoCotacaoRepository Repositório de histórico de cotações.
     * @param historicoCotacaoMapper Mapper para histórico de cotações.
     * @param pdfDeduplicationService Serviço de deduplicação de PDFs.
     */
    public CotacaoService(CotacaoRepository cotacaoRepository, CotacaoMapper cotacaoMapper,
                         CotacaoItemMapper cotacaoItemMapper,
                         FornecedorDeProdutoRepository fornecedorDeProdutoRepository,
                         FornecedorDeServicoRepository fornecedorDeServicoRepository,
                         ItemPedidoRepository itemPedidoRepository,
                         SolicitacaoDePedidoRepository solicitacaoDePedidoRepository,
                         HistoricoPedidoService historicoPedidoService,
                         UserRepository userRepository,
                         HistoricoCotacaoRepository historicoCotacaoRepository,
                         HistoricoCotacaoMapper historicoCotacaoMapper,
                         PdfDeduplicationService pdfDeduplicationService) {
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
        this.pdfDeduplicationService = pdfDeduplicationService;
    }

    /**
     * Obtém o usuário autenticado a partir do contexto de segurança do Spring.
     *
     * @return User autenticado ou null se não autenticado
     *
     * IMPORTANTE: authentication.getName() retorna o EMAIL do usuário,
     * pois User.getUsername() retorna o email (conforme interface UserDetails)
     */
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();  // getAuthentication(). getName() retorna o email
            return userRepository.findByEmail(email).orElse(null);
        }
        return null;
    }

    /**
     * Recupera todas as cotações de forma paginada.
     *
     * @param pageable Objeto contendo informações de paginação.
     * @return Página de DTOs de cotações.
     */
    @Transactional(readOnly = true)
    public Page<CotacaoDTO> getAllCotacoes(Pageable pageable) {
        // Bug Fix #9: Usar query otimizada para evitar N+1
        return cotacaoRepository.findAll(pageable).map(cotacaoMapper::toDTO);
    }

    /**
     * Busca uma cotação pelo ID.
     *
     * @param id Identificador da cotação.
     * @return DTO da cotação encontrada.
     * @throws EntityNotFoundException Se a cotação não for encontrada.
     */
    @Transactional(readOnly = true)
    public CotacaoDTO getCotacaoById(Long id) {
        return cotacaoRepository.findById(id)
                .map(cotacaoMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + id));
    }

    /**
     * Cria uma nova cotação.
     *
     * @param cotacaoCreateDTO DTO com os dados para criação.
     * @return DTO da cotação criada.
     * @throws EntityNotFoundException Se entidades relacionadas (pedido, fornecedor, itens) não forem encontradas.
     * @throws IllegalArgumentException Se houver inconsistências nos dados (ex: tipo de fornecedor inválido).
     */
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

    /**
     * Atualiza dados básicos de uma cotação.
     *
     * @param id Identificador da cotação.
     * @param cotacaoUpdateDTO Dados a atualizar (prazo, data limite).
     * @return DTO da cotação atualizada.
     * @throws EntityNotFoundException Se a cotação não for encontrada.
     */
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
                    // REMOVIDO: anexoPdf e caminhoAnexo (campos legados)
                    // PDFs gerenciados via AnexoCotacao com deduplificação

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

    /**
     * Exclui uma cotação.
     *
     * @param id Identificador da cotação.
     * @throws EntityNotFoundException Se a cotação não for encontrada.
     */
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

    /**
     * Obtém o conteúdo do primeiro anexo PDF da cotação.
     *
     * @param id Identificador da cotação.
     * @return Array de bytes do PDF.
     */
    @Transactional(readOnly = true)
    public byte[] obterAnexoPdf(Long id) {
        return obterAnexoPdf(id, 0);
    }

    /**
     * Obtém o conteúdo de um anexo específico da cotação.
     *
     * @param id Identificador da cotação.
     * @param index Índice do anexo (0-based).
     * @return Array de bytes do PDF.
     * @throws EntityNotFoundException Se a cotação ou o anexo não forem encontrados.
     */
    @Transactional(readOnly = true)
    public byte[] obterAnexoPdf(Long id, int index) {
        Cotacao cotacao = cotacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + id));

        // Verificar se há anexos
        if (cotacao.getAnexos() != null && !cotacao.getAnexos().isEmpty()) {
            if (index >= 0 && index < cotacao.getAnexos().size()) {
                return cotacao.getAnexos().get(index).getConteudo();
            }
            throw new EntityNotFoundException("Anexo não encontrado no índice: " + index);
        }

        throw new EntityNotFoundException("Nenhum anexo encontrado para esta cotação");
    }

    /**
     * Vincula itens de pedido a uma cotação existente.
     * <p>
     * Atualiza a lista de itens vinculados, mantendo preços se possível e removendo itens desvinculados.
     * Utiliza lógica de retry para lidar com concorrência (Optimistic Locking).
     * </p>
     *
     * @param cotacaoId ID da cotação.
     * @param itensPedidoIds Lista de IDs dos itens de pedido a vincular.
     * @return DTO da cotação atualizada.
     * @throws EntityNotFoundException Se a cotação ou itens não forem encontrados.
     */
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
     * Edita uma cotação existente com auditoria completa.
     * <p>
     * Cria registro no histórico antes de aplicar as mudanças e incrementa o número da versão.
     * </p>
     *
     * @param editDTO Dados da edição com motivo obrigatório.
     * @return DTO da cotação atualizada.
     * @throws IllegalArgumentException Se não houver mudanças ou motivo for inválido.
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

        // NOTA: Adição de anexos deve ser feita através de endpoint específico
        // usando PdfDeduplicationService para evitar duplicação
        // Por enquanto, comentado para não quebrar compilação
        // TODO: Criar endpoint POST /api/cotacoes/{id}/anexos com multipart/form-data

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
                // REMOVIDO: anexoPdf - gerenciado via endpoint separado com deduplificação

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
        // A versão no histórico representa a versão NOVA (resultado da edição), não a anterior
        // Exemplo: se cotação está na v1 e é editada, o histórico terá versão=2 (a nova versão criada)
        Integer versaoAnterior = cotacaoAnterior.getNumeroVersao() != null ? cotacaoAnterior.getNumeroVersao() : 0;
        historico.setVersao(versaoAnterior + 1);

        // Dados anteriores (antes da edição)
        historico.setPrecoAnterior(cotacaoAnterior.getPreco());
        historico.setPrazoEmDiasUteisAnterior(cotacaoAnterior.getPrazoEmDiasUteis());
        historico.setDataLimiteAnterior(cotacaoAnterior.getDataLimite());

        // Armazenar hash do PDF anterior (ao invés do PDF completo)
        if (cotacaoAnterior.getAnexos() != null && !cotacaoAnterior.getAnexos().isEmpty()) {
            // Pega o hash do primeiro anexo como referência
            historico.setHashAnexoPdfAnterior(cotacaoAnterior.getAnexos().get(0).getHashSha256());
        }

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

        // Armazenar hash do PDF novo (se fornecido no editDTO)
        // NOTA: anexoPdf foi removido do DTO - gerenciamento via endpoint separado
        // Se no futuro anexoPdf retornar ao DTO, descomentar:
        // if (editDTO.anexoPdf() != null) {
        //     String hashNovo = pdfHashService.calculateSHA256(editDTO.anexoPdf());
        //     historico.setHashAnexoPdfNovo(hashNovo);
        // }

        // Informações de auditoria
        historico.setMotivoEdicao(editDTO.motivoEdicao());
        historico.setEditadoPor(editDTO.editadoPor());
        historico.setDataEdicao(LocalDateTime.now());

        historicoCotacaoRepository.saveAndFlush(historico);
    }

    /**
     * Atualiza o histórico mais recente com os hashes dos PDFs recém-adicionados.
     * Chamado quando PDFs são adicionados como parte de uma edição (createHistory=false).
     */
    private void atualizarHistoricoComNovosPdfs(Cotacao cotacao, int quantidadeAdicionada) {
        // Buscar o histórico mais recente desta cotação
        List<HistoricoCotacao> historicos = historicoCotacaoRepository.findByCotacaoIdOrderByDataEdicaoDesc(cotacao.getId());

        if (historicos.isEmpty()) {
            // Nenhum histórico encontrado - não há nada para atualizar
            return;
        }

        HistoricoCotacao historicoMaisRecente = historicos.get(0);

        // Atualizar com o hash do PDF mais recente
        if (cotacao.getAnexos() != null && !cotacao.getAnexos().isEmpty()) {
            AnexoCotacao anexoMaisRecente = cotacao.getAnexos().stream()
                .max(java.util.Comparator.comparing(AnexoCotacao::getOrdem))
                .orElse(null);

            if (anexoMaisRecente != null) {
                historicoMaisRecente.setHashAnexoPdfNovo(anexoMaisRecente.getHashSha256());
                historicoCotacaoRepository.save(historicoMaisRecente);
            }
        }
    }

    /**
     * Busca o histórico completo de edições de uma cotação.
     *
     * @param cotacaoId ID da cotação.
     * @return Lista de DTOs do histórico ordenados por data (mais recente primeiro).
     */
    @Transactional(readOnly = true)
    public List<HistoricoCotacaoDTO> buscarHistoricoCotacao(Long cotacaoId) {
        return historicoCotacaoRepository.findByCotacaoIdOrderByDataEdicaoDesc(cotacaoId)
            .stream()
            .map(historicoCotacaoMapper::toDTO)
            .toList();
    }

    /**
     * Obtém PDF anterior do histórico usando o hash armazenado.
     * <p>
     * Busca o PDF real na tabela de deduplicação através do hash referenciado.
     * </p>
     *
     * @param historicoId ID do registro histórico.
     * @return Array de bytes do PDF.
     * @throws EntityNotFoundException Se o histórico ou o PDF não forem encontrados.
     */
    public byte[] obterPdfAnteriorHistorico(Long historicoId) {
        HistoricoCotacao historico = historicoCotacaoRepository.findById(historicoId)
            .orElseThrow(() -> new EntityNotFoundException("Histórico não encontrado: " + historicoId));

        String hash = historico.getHashAnexoPdfAnterior();
        if (hash == null) {
            throw new EntityNotFoundException("Nenhum hash de PDF anterior encontrado neste histórico");
        }

        // Busca o anexo pelo hash (deduplificação permite múltiplas cotações compartilharem o mesmo PDF)
        Cotacao cotacao = cotacaoRepository.findById(historico.getCotacaoId())
            .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada: " + historico.getCotacaoId()));

        // Busca no anexo da cotação pelo hash
        return cotacao.getAnexos().stream()
            .filter(anexo -> hash.equals(anexo.getHashSha256()))
            .findFirst()
            .map(AnexoCotacao::getConteudo)
            .orElseThrow(() -> new EntityNotFoundException(
                "PDF não encontrado com hash: " + hash + " (pode ter sido removido)"));
    }

    /**
     * Obtém PDF novo do histórico usando o hash armazenado.
     *
     * @param historicoId ID do registro histórico.
     * @return Array de bytes do PDF.
     * @throws EntityNotFoundException Se o histórico ou o PDF não forem encontrados.
     */
    public byte[] obterPdfNovoHistorico(Long historicoId) {
        HistoricoCotacao historico = historicoCotacaoRepository.findById(historicoId)
            .orElseThrow(() -> new EntityNotFoundException("Histórico não encontrado: " + historicoId));

        String hash = historico.getHashAnexoPdfNovo();
        if (hash == null) {
            throw new EntityNotFoundException("Nenhum hash de PDF novo encontrado neste histórico");
        }

        // Busca o anexo pelo hash
        Cotacao cotacao = cotacaoRepository.findById(historico.getCotacaoId())
            .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada: " + historico.getCotacaoId()));

        return cotacao.getAnexos().stream()
            .filter(anexo -> hash.equals(anexo.getHashSha256()))
            .findFirst()
            .map(AnexoCotacao::getConteudo)
            .orElseThrow(() -> new EntityNotFoundException(
                "PDF não encontrado com hash: " + hash + " (pode ter sido removido)"));
    }

    /**
     * Realiza o upload de múltiplos anexos PDF com deduplificação automática.
     * <p>
     * Valida tipo MIME e tamanho, e utiliza {@link PdfDeduplicationService} para otimizar armazenamento.
     * </p>
     *
     * @param cotacaoId ID da cotação.
     * @param files Array de arquivos recebidos na requisição.
     * @return DTO da cotação atualizada.
     * @throws IllegalArgumentException Se arquivos forem inválidos ou excederem o tamanho.
     * @throws EntityNotFoundException Se a cotação não for encontrada.
     */
    @Transactional
    public CotacaoDTO uploadAnexos(Long cotacaoId, org.springframework.web.multipart.MultipartFile[] files, boolean createHistory) {
        Cotacao cotacao = cotacaoRepository.findById(cotacaoId)
            .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + cotacaoId));

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("Nenhum arquivo foi enviado");
        }

        // Capturar estado anterior para histórico
        int quantidadeAnexosAnterior = cotacao.getAnexos() != null ? cotacao.getAnexos().size() : 0;

        int ordem = cotacao.getAnexos() != null ? cotacao.getAnexos().size() : 0;

        for (org.springframework.web.multipart.MultipartFile file : files) {
            // Validar tipo MIME
            String contentType = file.getContentType();
            if (contentType == null || !contentType.equals("application/pdf")) {
                throw new IllegalArgumentException(
                    "Arquivo " + file.getOriginalFilename() + " não é um PDF válido. Tipo detectado: " + contentType
                );
            }

            // Validar tamanho (10MB = 10485760 bytes)
            if (file.getSize() > 10485760L) {
                throw new IllegalArgumentException(
                    "Arquivo " + file.getOriginalFilename() + " excede o limite de 10MB. Tamanho: " +
                    (file.getSize() / 1024 / 1024) + "MB"
                );
            }

            try {
                byte[] bytes = file.getBytes();
                String nomeArquivo = file.getOriginalFilename();

                // Usar PdfDeduplicationService para criar ou reutilizar anexo
                AnexoCotacao anexo = pdfDeduplicationService.createOrReuseCotacaoAnexo(
                    cotacao,
                    bytes,
                    ordem++,
                    nomeArquivo
                );

                cotacao.getAnexos().add(anexo);

            } catch (java.io.IOException e) {
                throw new RuntimeException("Erro ao processar arquivo " + file.getOriginalFilename() + ": " + e.getMessage(), e);
            }
        }

        Cotacao cotacaoSalva = cotacaoRepository.save(cotacao);

        // Registrar no histórico a adição de anexos
        if (createHistory) {
            // Upload independente: criar novo registro de histórico
            registrarHistoricoAdicaoAnexos(cotacaoSalva, quantidadeAnexosAnterior, files.length);
        } else {
            // Upload como parte de edição: atualizar histórico mais recente com os PDFs
            atualizarHistoricoComNovosPdfs(cotacaoSalva, files.length);
        }

        return cotacaoMapper.toDTO(cotacaoSalva);
    }

    private void registrarHistoricoAdicaoAnexos(Cotacao cotacao, int quantidadeAnterior, int quantidadeAdicionada) {
        HistoricoCotacao historico = new HistoricoCotacao();

        historico.setCotacaoId(cotacao.getId());
        historico.setVersao(cotacao.getNumeroVersao() != null ? cotacao.getNumeroVersao() : 1);

        // Manter valores iguais (não houve alteração de preço/prazo)
        historico.setPrecoAnterior(cotacao.getPreco());
        historico.setPrecoNovo(cotacao.getPreco());
        historico.setPrazoEmDiasUteisAnterior(cotacao.getPrazoEmDiasUteis());
        historico.setPrazoEmDiasUteisNovo(cotacao.getPrazoEmDiasUteis());
        historico.setDataLimiteAnterior(cotacao.getDataLimite());
        historico.setDataLimiteNovo(cotacao.getDataLimite());

        // Armazenar hash do PDF anterior (último anexo antes da adição, se houver)
        if (quantidadeAnterior > 0 && cotacao.getAnexos() != null && cotacao.getAnexos().size() > quantidadeAnterior) {
            // Pega o último anexo que existia antes da adição
            AnexoCotacao anexoAnterior = cotacao.getAnexos().stream()
                .filter(a -> a.getOrdem() < quantidadeAnterior)
                .max(java.util.Comparator.comparing(AnexoCotacao::getOrdem))
                .orElse(null);
            if (anexoAnterior != null) {
                historico.setHashAnexoPdfAnterior(anexoAnterior.getHashSha256());
            }
        }

        // Armazenar hash do PDF novo (último anexo adicionado)
        if (cotacao.getAnexos() != null && !cotacao.getAnexos().isEmpty()) {
            // Pega o último anexo adicionado (maior ordem)
            AnexoCotacao anexoNovo = cotacao.getAnexos().stream()
                .max(java.util.Comparator.comparing(AnexoCotacao::getOrdem))
                .orElse(null);
            if (anexoNovo != null) {
                historico.setHashAnexoPdfNovo(anexoNovo.getHashSha256());
            }
        }

        // Motivo específico para adição de anexos
        String motivoEdicao = String.format("Adicionado %d anexo(s) PDF. Total de anexos: %d → %d",
            quantidadeAdicionada, quantidadeAnterior, quantidadeAnterior + quantidadeAdicionada);
        historico.setMotivoEdicao(motivoEdicao);

        // Pegar usuário autenticado
        User user = getAuthenticatedUser();
        historico.setEditadoPor(user != null ? user.getNome() : "Sistema");
        historico.setDataEdicao(java.time.LocalDateTime.now());

        historicoCotacaoRepository.saveAndFlush(historico);
    }
}
