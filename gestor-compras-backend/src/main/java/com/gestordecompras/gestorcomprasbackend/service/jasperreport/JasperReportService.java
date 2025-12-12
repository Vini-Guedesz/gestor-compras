package com.gestordecompras.gestorcomprasbackend.service.jasperreport;

import com.gestordecompras.gestorcomprasbackend.dto.relatorio.*;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.StatusPedido;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.Rascunho;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.ItemRascunho;
import com.gestordecompras.gestorcomprasbackend.repository.CotacaoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeProdutoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeServicoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.SolicitacaoDePedidoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.ItemPedidoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.RascunhoRepository;
import com.gestordecompras.gestorcomprasbackend.service.FornecedorDeProdutoService;
import com.gestordecompras.gestorcomprasbackend.service.ItemPedidoService;
import jakarta.persistence.EntityNotFoundException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela geração de relatórios PDF usando JasperReports.
 * <p>
 * Centraliza a lógica de carregamento, compilação de templates (.jrxml), busca de dados
 * e exportação para formato PDF. Utiliza cache de templates para otimizar a performance.
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
 */
@Service
public class JasperReportService {

    // Cache para templates compilados (melhoria de performance)
    private final Map<String, JasperReport> templateCache = new ConcurrentHashMap<>();

    private final FornecedorDeProdutoService fornecedorDeProdutoService;
    private final ItemPedidoService itemPedidoService;
    private final SolicitacaoDePedidoRepository solicitacaoRepository;
    private final CotacaoRepository cotacaoRepository;
    private final FornecedorDeProdutoRepository fornecedorProdutoRepository;
    private final FornecedorDeServicoRepository fornecedorServicoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final RascunhoRepository rascunhoRepository;

    public JasperReportService(FornecedorDeProdutoService fornecedorDeProdutoService,
                               ItemPedidoService itemPedidoService,
                               SolicitacaoDePedidoRepository solicitacaoRepository,
                               CotacaoRepository cotacaoRepository,
                               FornecedorDeProdutoRepository fornecedorProdutoRepository,
                               FornecedorDeServicoRepository fornecedorServicoRepository,
                               ItemPedidoRepository itemPedidoRepository,
                               RascunhoRepository rascunhoRepository) {
        this.fornecedorDeProdutoService = fornecedorDeProdutoService;
        this.itemPedidoService = itemPedidoService;
        this.solicitacaoRepository = solicitacaoRepository;
        this.cotacaoRepository = cotacaoRepository;
        this.fornecedorProdutoRepository = fornecedorProdutoRepository;
        this.fornecedorServicoRepository = fornecedorServicoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.rascunhoRepository = rascunhoRepository;
    }

    /**
     * Obtém um JasperReport compilado do cache ou compila e cacheia se necessário.
     *
     * @param templatePath Caminho do template .jrxml no classpath.
     * @return JasperReport compilado.
     * @throws JRException Se houver erro na leitura ou compilação do template.
     */
    private JasperReport getOrCompileTemplate(String templatePath) throws JRException {
        return templateCache.computeIfAbsent(templatePath, path -> {
            try {
                InputStream inputStream = getClass().getResourceAsStream(path);
                if (inputStream == null) {
                    throw new JRException("Arquivo de relatório não encontrado em " + path);
                }
                return JasperCompileManager.compileReport(inputStream);
            } catch (JRException e) {
                throw new RuntimeException("Erro ao compilar template: " + path, e);
            }
        });
    }

    /**
     * Limpa o cache de templates compilados.
     * <p>
     * Útil para recarregar templates após atualizações em tempo de execução sem reiniciar a aplicação.
     * </p>
     */
    public void clearTemplateCache() {
        templateCache.clear();
    }

    /**
     * Gera relatório de lista de todos os fornecedores de produto.
     *
     * @return Byte array do PDF gerado.
     * @throws JRException Em caso de erro na geração do relatório.
     */
    public byte[] gerarRelatorioFornecedores() throws JRException {
        List<FornecedorDeProduto> fornecedores = fornecedorDeProdutoService.getAllFornecedoresDeProdutoEntities();

        JasperReport jasperReport = getOrCompileTemplate("/relatorios/fornecedores.jrxml");
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(fornecedores);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);

        return out.toByteArray();
    }

    /**
     * Gera relatório de lista de todos os itens de pedido cadastrados.
     *
     * @return Byte array do PDF gerado.
     * @throws JRException Em caso de erro na geração do relatório.
     */
    public byte[] gerarRelatorioItensPedido() throws JRException {
        List<ItemPedido> itens = itemPedidoService.getAllItensEntities();

        JasperReport jasperReport = getOrCompileTemplate("/relatorios/items_produtos.jrxml");
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(itens);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);

        return out.toByteArray();
    }

    /**
     * Gera relatório detalhado de um item de pedido específico.
     *
     * @param id ID do item de pedido.
     * @return Byte array do PDF gerado.
     * @throws JRException Em caso de erro na geração do relatório.
     * @throws EntityNotFoundException Se o item não for encontrado.
     */
    public byte[] gerarRelatorioItemPedidoPorId(Long id) throws JRException {
        ItemPedido item = itemPedidoService.getItemEntityById(id);

        InputStream inputStream = getClass().getResourceAsStream("/relatorios/item_produto_detalhe.jrxml");
        if (inputStream == null) {
            throw new JRException("Arquivo de relatório não encontrado em /relatorios/item_produto_detalhe.jrxml");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(java.util.Collections.singletonList(item));

        java.util.Map<String, Object> parameters = new java.util.HashMap<>();
        parameters.put("ID_ITEM", id);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);

        return out.toByteArray();
    }

    // Novos métodos de relatório

    /**
     * Gera o dashboard executivo com indicadores (KPIs) de compras.
     *
     * @return Byte array do PDF gerado.
     * @throws JRException Em caso de erro na geração do relatório.
     */
    @Transactional(readOnly = true)
    public byte[] gerarDashboardExecutivo() throws JRException {
        DashboardExecutivoDTO dashboard = calcularDashboardExecutivo();

        InputStream inputStream = getClass().getResourceAsStream("/relatorios/dashboard_executivo.jrxml");
        if (inputStream == null) {
            throw new JRException("Arquivo de relatório não encontrado em /relatorios/dashboard_executivo.jrxml");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList(dashboard));

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);

        return out.toByteArray();
    }

    /**
     * Gera relatório dos itens mais solicitados (ranking).
     *
     * @return Byte array do PDF gerado.
     * @throws JRException Em caso de erro na geração do relatório.
     */
    @Transactional(readOnly = true)
    public byte[] gerarRelatorioItensMaisSolicitados() throws JRException {
        List<ItemMaisSolicitadoDTO> itens = calcularItensMaisSolicitados();

        InputStream inputStream = getClass().getResourceAsStream("/relatorios/itens_mais_solicitados.jrxml");
        if (inputStream == null) {
            throw new JRException("Arquivo de relatório não encontrado em /relatorios/itens_mais_solicitados.jrxml");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(itens);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);

        return out.toByteArray();
    }

    /**
     * Gera relatório comparativo de cotações para um item específico.
     *
     * @param itemPedidoId ID do item para comparação.
     * @return Byte array do PDF gerado.
     * @throws JRException Em caso de erro na geração do relatório.
     */
    @Transactional(readOnly = true)
    public byte[] gerarComparativoCotacaoPorItem(Long itemPedidoId) throws JRException {
        List<ComparativoCotacaoDTO> cotacoes = buscarCotacoesPorItem(itemPedidoId);

        InputStream inputStream = getClass().getResourceAsStream("/relatorios/comparativo_cotacao.jrxml");
        if (inputStream == null) {
            throw new JRException("Arquivo de relatório não encontrado em /relatorios/comparativo_cotacao.jrxml");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(cotacoes);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ITEM_PEDIDO_ID", itemPedidoId);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);

        return out.toByteArray();
    }

    /**
     * Gera relatório de solicitações de pedido em aberto.
     *
     * @return Byte array do PDF gerado.
     * @throws JRException Em caso de erro na geração do relatório.
     */
    @Transactional(readOnly = true)
    public byte[] gerarRelatorioSolicitacoesAbertas() throws JRException {
        List<SolicitacaoAbertaDTO> solicitacoes = buscarSolicitacoesAbertas();

        InputStream inputStream = getClass().getResourceAsStream("/relatorios/solicitacoes_abertas.jrxml");
        if (inputStream == null) {
            throw new JRException("Arquivo de relatório não encontrado em /relatorios/solicitacoes_abertas.jrxml");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(solicitacoes);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);

        return out.toByteArray();
    }

    /**
     * Gera relatório de pedidos fechados/concluídos.
     *
     * @return Byte array do PDF gerado.
     * @throws JRException Em caso de erro na geração do relatório.
     */
    @Transactional(readOnly = true)
    public byte[] gerarRelatorioPedidosFechados() throws JRException {
        List<PedidoFechadoDTO> pedidos = buscarPedidosFechados();

        InputStream inputStream = getClass().getResourceAsStream("/relatorios/pedidos_fechados.jrxml");
        if (inputStream == null) {
            throw new JRException("Arquivo de relatório não encontrado em /relatorios/pedidos_fechados.jrxml");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pedidos);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);

        return out.toByteArray();
    }

    // Métodos auxiliares para agregação de dados

    private DashboardExecutivoDTO calcularDashboardExecutivo() {
        long totalSolicitacoes = solicitacaoRepository.count();
        long solicitacoesPendentes = solicitacaoRepository.countByStatusIn(List.of(StatusPedido.PENDENTE));
        long solicitacoesEmAnalise = solicitacaoRepository.countByStatusIn(List.of(StatusPedido.EM_ANALISE));
        long solicitacoesEmAndamento = solicitacaoRepository.countByStatusIn(List.of(StatusPedido.EM_ANDAMENTO));
        long solicitacoesAprovadas = solicitacaoRepository.countByStatusIn(List.of(StatusPedido.APROVADO));
        long solicitacoesCanceladas = solicitacaoRepository.countByStatusIn(List.of(StatusPedido.CANCELADO));

        long solicitacoesAbertas = solicitacoesPendentes + solicitacoesEmAnalise + solicitacoesEmAndamento;
        long solicitacoesFechadas = solicitacoesAprovadas + solicitacoesCanceladas;

        Long totalItensLong = solicitacaoRepository.countTotalItens();
        long totalItens = totalItensLong != null ? totalItensLong : 0L;

        long totalCotacoes = cotacaoRepository.count();

        // Após refatoração Bug #5: calcular soma dos preços manualmente
        BigDecimal valorTotalEstimado = cotacaoRepository.findAll().stream()
                .map(Cotacao::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // NOTA: Este cálculo divide o valor total de TODAS as cotações pelo número de solicitações.
        // Se uma solicitação tiver múltiplas cotações para o mesmo item, isso pode gerar valores incorretos.
        // Considere revisar a lógica de negócio: ticket médio deve ser por solicitação ou por cotação aprovada?
        BigDecimal ticketMedio = totalSolicitacoes > 0
                ? valorTotalEstimado.divide(BigDecimal.valueOf(totalSolicitacoes), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new DashboardExecutivoDTO(
                totalSolicitacoes,
                solicitacoesAbertas,
                solicitacoesFechadas,
                solicitacoesPendentes,
                solicitacoesEmAnalise,
                solicitacoesEmAndamento,
                solicitacoesAprovadas,
                solicitacoesCanceladas,
                totalItens,
                totalCotacoes,
                valorTotalEstimado,
                ticketMedio
        );
    }

    private List<ItemMaisSolicitadoDTO> calcularItensMaisSolicitados() {
        List<ItemPedido> todosItens = itemPedidoService.getAllItensEntities();

        Map<String, List<ItemPedido>> itensPorNome = todosItens.stream()
                .collect(Collectors.groupingBy(ItemPedido::getNome));

        return itensPorNome.entrySet().stream()
                .map(entry -> {
                    String nome = entry.getKey();
                    List<ItemPedido> itens = entry.getValue();
                    long quantidadeTotal = itens.stream().mapToLong(ItemPedido::getQuantidade).sum();
                    long numeroSolicitacoes = itens.size();
                    String descricao = itens.get(0).getDescricao();
                    return new ItemMaisSolicitadoDTO(nome, quantidadeTotal, numeroSolicitacoes, descricao);
                })
                .sorted(Comparator.comparing(ItemMaisSolicitadoDTO::getQuantidadeTotal).reversed())
                .collect(Collectors.toList());
    }

    private List<ComparativoCotacaoDTO> buscarCotacoesPorItem(Long itemPedidoId) {
        List<Cotacao> cotacoes = cotacaoRepository.findByItemPedidoIdWithFornecedores(itemPedidoId);

        return cotacoes.stream().map(cotacao -> {
            String fornecedor = "";
            String cnpj = "";
            String tipo = "";

            if (cotacao.getFornecedorProduto() != null) {
                fornecedor = cotacao.getFornecedorProduto().getRazaoSocial();
                cnpj = cotacao.getFornecedorProduto().getCnpj();
                tipo = "PRODUTO";
            } else if (cotacao.getFornecedorServico() != null) {
                fornecedor = cotacao.getFornecedorServico().getRazaoSocial();
                cnpj = cotacao.getFornecedorServico().getCnpj();
                tipo = "SERVICO";
            }

            // Pegar o primeiro item do pedido associado à cotação (ou null se não houver)
            Long itemId = null;
            String itemNome = "";
            String itemDescricao = "";

            if (cotacao.getItens() != null && !cotacao.getItens().isEmpty()) {
                var primeiroCotacaoItem = cotacao.getItens().get(0);
                var primeiroItem = primeiroCotacaoItem.getItemPedido();
                itemId = primeiroItem.getId();
                itemNome = primeiroItem.getNome();
                itemDescricao = primeiroItem.getDescricao();
            }

            return new ComparativoCotacaoDTO(
                    cotacao.getId().toString(),
                    itemId,
                    itemNome,
                    itemDescricao,
                    fornecedor,
                    cnpj,
                    cotacao.getPreco(),
                    cotacao.getPrazoEmDiasUteis(),
                    cotacao.getDataLimite(),
                    tipo
            );
        }).collect(Collectors.toList());
    }

    private List<SolicitacaoAbertaDTO> buscarSolicitacoesAbertas() {
        List<StatusPedido> statusesAbertos = List.of(StatusPedido.PENDENTE, StatusPedido.EM_ANALISE, StatusPedido.EM_ANDAMENTO);
        List<SolicitacaoDePedido> solicitacoes = solicitacaoRepository.findByStatusInWithItens(statusesAbertos);

        return solicitacoes.stream().map(s -> {
            String itensResumidos = s.getItens() != null
                    ? s.getItens().stream().map(ItemPedido::getNome).collect(Collectors.joining(", "))
                    : "";
            int quantidadeItens = s.getItens() != null ? s.getItens().size() : 0;

            return new SolicitacaoAbertaDTO(
                    s.getId().toString(),
                    s.getStatus().toString(),
                    s.getDataCriacao(),
                    quantidadeItens,
                    s.getObservacao(),
                    itensResumidos
            );
        }).collect(Collectors.toList());
    }

    private List<PedidoFechadoDTO> buscarPedidosFechados() {
        List<StatusPedido> statusesFechados = List.of(StatusPedido.APROVADO, StatusPedido.CANCELADO);
        List<SolicitacaoDePedido> pedidos = solicitacaoRepository.findByStatusInWithItens(statusesFechados);

        return pedidos.stream().map(p -> {
            String itensResumidos = p.getItens() != null
                    ? p.getItens().stream().map(ItemPedido::getNome).collect(Collectors.joining(", "))
                    : "";
            int quantidadeItens = p.getItens() != null ? p.getItens().size() : 0;

            return new PedidoFechadoDTO(
                    p.getId().toString(),
                    p.getStatus().toString(),
                    p.getDataCriacao(),
                    quantidadeItens,
                    p.getObservacao(),
                    itensResumidos
            );
        }).collect(Collectors.toList());
    }

    /**
     * Gera relatório de itens selecionados para cotação (de uma solicitação).
     *
     * @param request Filtros para geração do relatório (ID da solicitação e opcionalmente IDs dos itens).
     * @return Byte array do PDF gerado.
     * @throws JRException Em caso de erro na geração do relatório.
     * @throws EntityNotFoundException Se a solicitação não for encontrada.
     * @throws IllegalArgumentException Se não houver itens correspondentes.
     */
    @Transactional(readOnly = true)
    public byte[] gerarRelatorioItensParaCotacao(SolicitacaoRelatorioRequestDTO request) throws JRException {
        // Validar e buscar a solicitação
        SolicitacaoDePedido solicitacao = solicitacaoRepository.findById(request.getSolicitacaoId())
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada: " + request.getSolicitacaoId()));

        // Buscar os itens da solicitação
        List<ItemPedido> itens;
        if (request.getItensIds() != null && !request.getItensIds().isEmpty()) {
            // Se IDs específicos foram fornecidos, buscar apenas esses itens
            itens = itemPedidoRepository.findAllById(request.getItensIds()).stream()
                    .filter(item -> item.getSolicitacaoDePedido().getId().equals(request.getSolicitacaoId()))
                    .collect(Collectors.toList());
        } else {
            // Se nenhum ID foi fornecido, buscar todos os itens da solicitação
            itens = solicitacao.getItens();
        }

        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Nenhum item encontrado para a solicitação informada");
        }

        // Converter para DTO
        List<ItemParaCotacaoDTO> itensDTO = itens.stream()
                .map(item -> new ItemParaCotacaoDTO(
                        item.getId(),
                        item.getNome(),
                        item.getDescricao(),
                        item.getQuantidade(),
                        item.getObservacao(),
                        solicitacao.getId()
                ))
                .collect(Collectors.toList());

        // Carregar template
        InputStream inputStream = getClass().getResourceAsStream("/relatorios/itens_para_cotacao.jrxml");
        if (inputStream == null) {
            throw new JRException("Arquivo de relatório não encontrado em /relatorios/itens_para_cotacao.jrxml");
        }

        // Compilar e preencher relatório
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(itensDTO);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("SOLICITACAO_ID", request.getSolicitacaoId());
        parameters.put("DATA_GERACAO", LocalDateTime.now());

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Exportar para PDF
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);

        return out.toByteArray();
    }

    /**
     * Gera relatório de itens para cotação a partir de um rascunho.
     *
     * @param request Dados da requisição contendo ID do rascunho e IDs opcionais dos itens.
     * @return Byte array do PDF gerado.
     * @throws JRException Se houver erro na geração do relatório.
     * @throws EntityNotFoundException Se o rascunho não for encontrado.
     * @throws IllegalArgumentException Se não houver itens correspondentes.
     */
    @Transactional(readOnly = true)
    public byte[] gerarRelatorioItensParaCotacaoRascunho(SolicitacaoRelatorioRequestDTO request) throws JRException {
        // Validar e buscar o rascunho
        Rascunho rascunho = rascunhoRepository.findById(request.getSolicitacaoId())
                .orElseThrow(() -> new EntityNotFoundException("Rascunho não encontrado: " + request.getSolicitacaoId()));

        // Buscar os itens do rascunho
        List<ItemRascunho> itens;
        if (request.getItensIds() != null && !request.getItensIds().isEmpty()) {
            // Se IDs específicos foram fornecidos, filtrar apenas esses itens
            itens = rascunho.getItens().stream()
                    .filter(item -> request.getItensIds().contains(item.getId()))
                    .collect(Collectors.toList());
        } else {
            // Se nenhum ID foi fornecido, buscar todos os itens do rascunho
            itens = rascunho.getItens();
        }

        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Nenhum item encontrado para o rascunho informado");
        }

        // Converter para DTO
        List<ItemParaCotacaoDTO> itensDTO = itens.stream()
                .map(item -> new ItemParaCotacaoDTO(
                        item.getId(),
                        item.getNome(),
                        item.getDescricao(),
                        item.getQuantidade(),
                        item.getObservacao(),
                        rascunho.getId()
                ))
                .collect(Collectors.toList());

        // Carregar template
        InputStream inputStream = getClass().getResourceAsStream("/relatorios/itens_para_cotacao.jrxml");
        if (inputStream == null) {
            throw new JRException("Arquivo de relatório não encontrado em /relatorios/itens_para_cotacao.jrxml");
        }

        // Compilar e preencher relatório
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(itensDTO);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("SOLICITACAO_ID", request.getSolicitacaoId());
        parameters.put("DATA_GERACAO", LocalDateTime.now());

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Exportar para PDF
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);

        return out.toByteArray();
    }
}