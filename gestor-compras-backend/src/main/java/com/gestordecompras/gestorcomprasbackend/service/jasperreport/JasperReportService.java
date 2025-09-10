package com.gestordecompras.gestorcomprasbackend.service.jasperreport;

import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.service.FornecedorDeProdutoService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

@Service
public class JasperReportService {

    private final FornecedorDeProdutoService fornecedorDeProdutoService;

    public JasperReportService(FornecedorDeProdutoService fornecedorDeProdutoService) {
        this.fornecedorDeProdutoService = fornecedorDeProdutoService;
    }

    public byte[] gerarRelatorioFornecedores() throws JRException {
        List<FornecedorDeProduto> fornecedores = fornecedorDeProdutoService.findAllEntities();

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
}