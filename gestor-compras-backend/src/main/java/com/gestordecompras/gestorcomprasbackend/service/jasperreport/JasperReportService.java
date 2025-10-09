package com.gestordecompras.gestorcomprasbackend.service.jasperreport;

import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.service.FornecedorDeProdutoService;
import com.gestordecompras.gestorcomprasbackend.service.ItemPedidoService;
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
    private final ItemPedidoService itemPedidoService;

    public JasperReportService(FornecedorDeProdutoService fornecedorDeProdutoService, ItemPedidoService itemPedidoService) {
        this.fornecedorDeProdutoService = fornecedorDeProdutoService;
        this.itemPedidoService = itemPedidoService;
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
}