package com.gestordecompras.gestorcomprasbackend.controller.relatorio;

import com.gestordecompras.gestorcomprasbackend.service.jasperreport.JasperReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/relatorios")
@Tag(name = "Relatórios Gerenciais", description = "Endpoints para geração de relatórios executivos e gerenciais")
public class RelatoriosGerenciaisController {

    private final JasperReportService jasperReportService;

    public RelatoriosGerenciaisController(JasperReportService jasperReportService) {
        this.jasperReportService = jasperReportService;
    }

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
