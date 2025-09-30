package com.gestordecompras.gestorcomprasbackend.controller.relatorio;

import com.gestordecompras.gestorcomprasbackend.service.jasperreport.JasperReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatorios")
@Tag(name = "Relatórios", description = "API para geração de relatórios")
@SecurityRequirement(name = "bearerAuth")
public class ItemPedidoRelatorioController {

    private final JasperReportService jasperReportService;

    public ItemPedidoRelatorioController(JasperReportService jasperReportService) {
        this.jasperReportService = jasperReportService;
    }

    @GetMapping("/itens-pedido")
    public ResponseEntity<byte[]> gerarRelatorioItensPedido() throws JRException {
        byte[] relatorio = jasperReportService.gerarRelatorioItensPedido();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=itens-pedido.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(relatorio);
    }

    @GetMapping("/itens-pedido/{id}")
    public ResponseEntity<byte[]> gerarRelatorioItemPedidoPorId(@PathVariable Long id) throws JRException {
        byte[] relatorio = jasperReportService.gerarRelatorioItemPedidoPorId(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=item-pedido-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(relatorio);
    }
}
