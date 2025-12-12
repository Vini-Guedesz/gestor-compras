package com.gestordecompras.gestorcomprasbackend.controller.relatorio;

import com.gestordecompras.gestorcomprasbackend.service.jasperreport.JasperReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST para geração de Relatórios de Fornecedores.
 *
 * <p>Gera PDF com lista completa de fornecedores de produto e serviço via JasperReports.</p>
 *
 * <p><b>Autenticação:</b> JWT obrigatório | <b>Roles:</b> USER, ADMIN</p>
 *
 * @since 1.0.0
 * @see JasperReportService
 */
@RestController
@RequestMapping("/relatorios")
@Tag(name = "Relatórios", description = "API para geração de relatórios")
@SecurityRequirement(name = "bearerAuth")
public class FornecedorRelatorioController {

    private final JasperReportService jasperReportService;

    /** Construtor com injeção de dependência. */
    public FornecedorRelatorioController(JasperReportService jasperReportService) {
        this.jasperReportService = jasperReportService;
    }

    /** Gera PDF com lista completa de fornecedores (produto e serviço). */
    @GetMapping("/fornecedores")
    public ResponseEntity<byte[]> gerarRelatorioFornecedores() throws JRException {
        byte[] relatorio = jasperReportService.gerarRelatorioFornecedores();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=fornecedores.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(relatorio);
    }
}