package com.gestordecompras.gestorcomprasbackend.service.jasperreport;

import com.gestordecompras.gestorcomprasbackend.dto.relatorio.*;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.StatusPedido;
import com.gestordecompras.gestorcomprasbackend.repository.CotacaoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeProdutoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeServicoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.SolicitacaoDePedidoRepository;
import com.gestordecompras.gestorcomprasbackend.service.FornecedorDeProdutoService;
import com.gestordecompras.gestorcomprasbackend.service.ItemPedidoService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JasperReportService {

    private final FornecedorDeProdutoService fornecedorDeProdutoService;
    private final ItemPedidoService itemPedidoService;
    private final SolicitacaoDePedidoRepository solicitacaoRepository;
    private final CotacaoRepository cotacaoRepository;
    private final FornecedorDeProdutoRepository fornecedorProdutoRepository;
    private final FornecedorDeServicoRepository fornecedorServicoRepository;

    public JasperReportService(FornecedorDeProdutoService fornecedorDeProdutoService,
                               ItemPedidoService itemPedidoService,
                               SolicitacaoDePedidoRepository solicitacaoRepository,
                               CotacaoRepository cotacaoRepository,
                               FornecedorDeProdutoRepository fornecedorProdutoRepository,
                               FornecedorDeServicoRepository fornecedorServicoRepository) {
        this.fornecedorDeProdutoService = fornecedorDeProdutoService;
        this.itemPedidoService = itemPedidoService;
        this.solicitacaoRepository = solicitacaoRepository;
        this.cotacaoRepository = cotacaoRepository;
        this.fornecedorProdutoRepository = fornecedorProdutoRepository;
        this.fornecedorServicoRepository = fornecedorServicoRepository;
    }

    public byte[] gerarRelatorioFornecedores() throws JRException {
        List<FornecedorDeProduto> fornecedores = fornecedorDeProdutoService.getAllFornecedoresDeProdutoEntities();

        InputStream inputStream = getClass().getResourceAsStream("/relatorios/fornecedores.jrxml");
        if (inputStream == null) {
            throw new JRException("Arquivo de relatório não encontrado em /relatorios/fornecedores.jrxml");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(fornecedores);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);

        return out.toByteArray();
    }

    public byte[] gerarRelatorioItensPedido() throws JRException {
        List<ItemPedido> itens = itemPedidoService.getAllItensEntities();

        InputStream inputStream = getClass().getResourceAsStream("/relatorios/items_produtos.jrxml");
        if (inputStream == null) {
            throw new JRException("Arquivo de relatório não encontrado em /relatorios/items_produtos.jrxml");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(itens);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, out);

        return out.toByteArray();
    }

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

        BigDecimal valorTotalEstimado = cotacaoRepository.sumTotalPreco();
        if (valorTotalEstimado == null) {
            valorTotalEstimado = BigDecimal.ZERO;
        }

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

            return new ComparativoCotacaoDTO(
                    cotacao.getId().toString(),
                    cotacao.getItemPedido().getId(),
                    cotacao.getItemPedido().getNome(),
                    cotacao.getItemPedido().getDescricao(),
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
}