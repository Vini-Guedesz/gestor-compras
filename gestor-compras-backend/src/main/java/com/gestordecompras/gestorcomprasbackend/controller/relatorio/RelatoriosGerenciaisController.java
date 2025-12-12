package com.gestordecompras.gestorcomprasbackend.controller.relatorio;

import com.gestordecompras.gestorcomprasbackend.dto.relatorio.SolicitacaoRelatorioRequestDTO;
import com.gestordecompras.gestorcomprasbackend.service.jasperreport.JasperReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller REST para geração de Relatórios Gerenciais e Executivos.
 *
 * <p>Gera PDFs executivos via JasperReports: dashboards, rankings, comparativos,
 * solicitações abertas/fechadas e itens para cotação.</p>
 *
 * <p><b>Autenticação:</b> Pública (sem JWT) | <b>Roles:</b> Todos</p>
 *
 * @since 1.0.0
 * @see JasperReportService
 */
@RestController
@RequestMapping("/relatorios")
@Tag(name = "Relatórios Gerenciais", description = "Endpoints para geração de relatórios executivos e gerenciais")
public class RelatoriosGerenciaisController {

    private static final Logger logger = LoggerFactory.getLogger(RelatoriosGerenciaisController.class);

    private final JasperReportService jasperReportService;

    /** Construtor com injeção de dependência. */
    public RelatoriosGerenciaisController(JasperReportService jasperReportService) {
        this.jasperReportService = jasperReportService;
    }

    /** Gera PDF com KPIs executivos: total pedidos, fornecedores, cotações, etc. */
    @GetMapping("/dashboard-executivo")
    @Operation(summary = "Dashboard Executivo", description = "Gera relatório com KPIs e métricas executivas do sistema")
    public ResponseEntity<byte[]> gerarDashboardExecutivo() {
        try {
            byte[] pdf = jasperReportService.gerarDashboardExecutivo();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "dashboard_executivo.pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (JRException e) {
            logger.error("Erro ao gerar dashboard executivo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** Gera PDF com ranking dos itens mais solicitados. */
    @GetMapping("/itens-mais-solicitados")
    @Operation(summary = "Itens Mais Solicitados", description = "Gera relatório com ranking dos itens mais solicitados")
    public ResponseEntity<byte[]> gerarRelatorioItensMaisSolicitados() {
        try {
            byte[] pdf = jasperReportService.gerarRelatorioItensMaisSolicitados();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "itens_mais_solicitados.pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (JRException e) {
            logger.error("Erro ao gerar relatório de itens mais solicitados: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** Gera PDF comparativo de todas as cotações para um item específico. */
    @GetMapping("/comparativo-cotacao/{itemPedidoId}")
    @Operation(summary = "Comparativo de Cotações por Item", description = "Gera relatório comparativo de todas as cotações para um item específico")
    public ResponseEntity<byte[]> gerarComparativoCotacao(@PathVariable Long itemPedidoId) {
        try {
            byte[] pdf = jasperReportService.gerarComparativoCotacaoPorItem(itemPedidoId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "comparativo_cotacao_item_" + itemPedidoId + ".pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (JRException e) {
            logger.error("Erro ao gerar comparativo de cotações para item {}: {}", itemPedidoId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** Gera PDF com solicitações PENDENTE ou EM_ANDAMENTO. */
    @GetMapping("/solicitacoes-abertas")
    @Operation(summary = "Solicitações Abertas", description = "Gera relatório com todas as solicitações de pedido com status PENDENTE ou EM_ANDAMENTO")
    public ResponseEntity<byte[]> gerarRelatorioSolicitacoesAbertas() {
        try {
            byte[] pdf = jasperReportService.gerarRelatorioSolicitacoesAbertas();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "solicitacoes_abertas.pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (JRException e) {
            logger.error("Erro ao gerar relatório de solicitações abertas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** Gera PDF com pedidos APROVADO ou CANCELADO. */
    @GetMapping("/pedidos-fechados")
    @Operation(summary = "Pedidos Fechados", description = "Gera relatório com todos os pedidos com status APROVADO ou CANCELADO")
    public ResponseEntity<byte[]> gerarRelatorioPedidosFechados() {
        try {
            byte[] pdf = jasperReportService.gerarRelatorioPedidosFechados();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "pedidos_fechados.pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (JRException e) {
            logger.error("Erro ao gerar relatório de pedidos fechados: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gera PDF com itens selecionados de um pedido para envio aos fornecedores.
     *
     * <p>Se nenhum ID de item for fornecido, inclui todos os itens da solicitação.</p>
     */
    @PostMapping("/itens-para-cotacao")
    @Operation(summary = "Itens para Cotação",
               description = "Gera relatório com os itens selecionados de uma solicitação para envio aos fornecedores. " +
                           "Se nenhum ID de item for fornecido, inclui todos os itens da solicitação.")
    public ResponseEntity<byte[]> gerarRelatorioItensParaCotacao(@RequestBody SolicitacaoRelatorioRequestDTO request) {
        try {
            byte[] pdf = jasperReportService.gerarRelatorioItensParaCotacao(request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "itens_para_cotacao_" + request.getSolicitacaoId() + ".pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("Solicitação não encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            logger.error("Dados inválidos na requisição: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (JRException e) {
            logger.error("Erro ao gerar relatório de itens para cotação: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gera PDF com itens selecionados de um rascunho para envio aos fornecedores.
     *
     * <p>Se nenhum ID de item for fornecido, inclui todos os itens do rascunho.</p>
     */
    @PostMapping("/itens-para-cotacao-rascunho")
    @Operation(summary = "Itens para Cotação (Rascunho)",
               description = "Gera relatório com os itens selecionados de um rascunho para envio aos fornecedores. " +
                           "Se nenhum ID de item for fornecido, inclui todos os itens do rascunho.")
    public ResponseEntity<byte[]> gerarRelatorioItensParaCotacaoRascunho(@RequestBody SolicitacaoRelatorioRequestDTO request) {
        try {
            byte[] pdf = jasperReportService.gerarRelatorioItensParaCotacaoRascunho(request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "itens_para_cotacao_rascunho_" + request.getSolicitacaoId() + ".pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("Rascunho não encontrado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            logger.error("Dados inválidos na requisição: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (JRException e) {
            logger.error("Erro ao gerar relatório de itens para cotação (rascunho): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
