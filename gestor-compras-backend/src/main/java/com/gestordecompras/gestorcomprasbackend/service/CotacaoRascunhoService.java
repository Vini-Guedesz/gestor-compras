package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.rascunho.CotacaoRascunhoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.rascunho.CotacaoRascunhoDTO;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeServico;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.AnexoCotacaoRascunho;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.CotacaoRascunho;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.ItemRascunho;
import com.gestordecompras.gestorcomprasbackend.model.rascunho.Rascunho;
import com.gestordecompras.gestorcomprasbackend.repository.CotacaoRascunhoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeProdutoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeServicoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.ItemRascunhoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.RascunhoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelo gerenciamento de cotações associadas a rascunhos.
 * <p>
 * Permite adicionar estimativas de preços e prazos aos itens de um rascunho
 * antes que ele seja transformado em um pedido formal.
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
 */
@Slf4j
@Service
public class CotacaoRascunhoService {

    @Autowired
    private CotacaoRascunhoRepository cotacaoRascunhoRepository;

    @Autowired
    private RascunhoRepository rascunhoRepository;

    @Autowired
    private ItemRascunhoRepository itemRascunhoRepository;

    @Autowired
    private FornecedorDeProdutoRepository fornecedorDeProdutoRepository;

    @Autowired
    private FornecedorDeServicoRepository fornecedorDeServicoRepository;

    @Autowired
    private PdfDeduplicationService pdfDeduplicationService;

    /**
     * Lista todas as cotações de rascunho associadas a um rascunho específico.
     *
     * @param rascunhoId Identificador do rascunho.
     * @return Lista de DTOs das cotações encontradas.
     */
    @Transactional(readOnly = true)
    public List<CotacaoRascunhoDTO> listarPorRascunho(Long rascunhoId) {
        // Carregar cotações com itens (primeira query)
        List<CotacaoRascunho> cotacoes = cotacaoRascunhoRepository.findByRascunhoIdWithItens(rascunhoId);

        // Se houver cotações, carregar anexos separadamente para evitar produto cartesiano
        if (!cotacoes.isEmpty()) {
            List<Long> ids = cotacoes.stream().map(CotacaoRascunho::getId).collect(Collectors.toList());
            cotacaoRascunhoRepository.findByIdsWithAnexos(ids); // Carrega anexos em segunda query
        }

        return cotacoes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Busca uma cotação de rascunho pelo seu ID.
     *
     * @param id Identificador da cotação de rascunho.
     * @return DTO da cotação encontrada.
     * @throws EntityNotFoundException Se a cotação não for encontrada.
     */
    @Transactional(readOnly = true)
    public CotacaoRascunhoDTO obterPorId(Long id) {
        CotacaoRascunho cotacao = cotacaoRascunhoRepository.findByIdWithItens(id);
        if (cotacao == null) {
            throw new EntityNotFoundException("Cotação não encontrada com ID: " + id);
        }
        // Carregar anexos separadamente
        cotacaoRascunhoRepository.findByIdsWithAnexos(List.of(id));
        return toDTO(cotacao);
    }

    /**
     * Cria uma nova cotação de rascunho.
     *
     * @param rascunhoId Identificador do rascunho pai.
     * @param dto Dados da nova cotação de rascunho.
     * @return DTO da cotação criada.
     * @throws EntityNotFoundException Se o rascunho ou fornecedor não forem encontrados.
     * @throws IllegalArgumentException Se o tipo de fornecedor for inválido ou itens não pertencerem ao rascunho.
     */
    @Transactional
    public CotacaoRascunhoDTO criar(Long rascunhoId, CotacaoRascunhoCreateDTO dto) {
        Rascunho rascunho = rascunhoRepository.findById(rascunhoId)
                .orElseThrow(() -> new EntityNotFoundException("Rascunho não encontrado com ID: " + rascunhoId));

        CotacaoRascunho cotacao = new CotacaoRascunho();
        cotacao.setRascunho(rascunho);
        cotacao.setPreco(dto.preco());
        cotacao.setPrazoEmDiasUteis(dto.prazoEmDiasUteis());
        cotacao.setDataLimite(dto.dataLimite());
        cotacao.setGastoPrevisto(dto.gastoPrevisto() != null ? dto.gastoPrevisto() : false);
        cotacao.setProjeto(dto.projeto());

        log.info("DEBUG: Criando cotação rascunho - gastoPrevisto recebido: {}, projeto: {}",
                dto.gastoPrevisto(), dto.projeto());
        log.info("DEBUG: Cotação após set - gastoPrevisto: {}, projeto: {}",
                cotacao.getGastoPrevisto(), cotacao.getProjeto());

        // Não usar mais anexoPdf legado - usar apenas a nova estrutura de anexos múltiplos

        // Definir fornecedor
        if ("PRODUTO".equalsIgnoreCase(dto.tipoFornecedor())) {
            FornecedorDeProduto fornecedor = fornecedorDeProdutoRepository.findById(dto.fornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor de produto não encontrado"));
            cotacao.setFornecedorProduto(fornecedor);
        } else if ("SERVICO".equalsIgnoreCase(dto.tipoFornecedor())) {
            FornecedorDeServico fornecedor = fornecedorDeServicoRepository.findById(dto.fornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor de serviço não encontrado"));
            cotacao.setFornecedorServico(fornecedor);
        } else {
            throw new IllegalArgumentException("Tipo de fornecedor inválido: " + dto.tipoFornecedor());
        }

        // Associar itens do rascunho
        Set<ItemRascunho> itens = new HashSet<>();
        for (Long itemId : dto.itensRascunhoIds()) {
            ItemRascunho item = itemRascunhoRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("Item de rascunho não encontrado com ID: " + itemId));

            // Validar que o item pertence ao rascunho
            if (!item.getRascunho().getId().equals(rascunhoId)) {
                throw new IllegalArgumentException("Item " + itemId + " não pertence ao rascunho " + rascunhoId);
            }
            itens.add(item);
        }
        cotacao.setItensRascunho(itens);

        // Processar múltiplos PDFs com deduplificação
        if (dto.anexosPdf() != null && !dto.anexosPdf().isEmpty()) {
            int ordem = 0;
            for (byte[] pdfBytes : dto.anexosPdf()) {
                if (pdfBytes != null && pdfBytes.length > 0) {
                    // Usar PdfDeduplicationService para criar ou reutilizar anexo
                    String nomeArquivo = String.format("anexo_%d.pdf", ordem);
                    AnexoCotacaoRascunho anexo = pdfDeduplicationService.createOrReuseRascunhoAnexo(
                        cotacao,
                        pdfBytes,
                        ordem++,
                        nomeArquivo
                    );
                    cotacao.getAnexos().add(anexo);
                }
            }
        } else if (dto.anexoPdf() != null && dto.anexoPdf().length > 0) {
            // Compatibilidade com PDF único usando deduplificação
            AnexoCotacaoRascunho anexo = pdfDeduplicationService.createOrReuseRascunhoAnexo(
                cotacao,
                dto.anexoPdf(),
                0,
                "anexo_0.pdf"
            );
            cotacao.getAnexos().add(anexo);
        }

        CotacaoRascunho salva = cotacaoRascunhoRepository.save(cotacao);
        return toDTO(salva);
    }

    /**
     * Exclui uma cotação de rascunho.
     *
     * @param id Identificador da cotação.
     * @throws EntityNotFoundException Se a cotação não for encontrada.
     */
    @Transactional
    public void deletar(Long id) {
        if (!cotacaoRascunhoRepository.existsById(id)) {
            throw new EntityNotFoundException("Cotação não encontrada com ID: " + id);
        }
        cotacaoRascunhoRepository.deleteById(id);
    }

    /**
     * Obtém o conteúdo do primeiro anexo PDF da cotação de rascunho.
     *
     * @param id Identificador da cotação.
     * @return Array de bytes do PDF.
     */
    @Transactional(readOnly = true)
    public byte[] obterAnexoPdf(Long id) {
        return obterAnexoPdf(id, 0);
    }

    /**
     * Obtém o conteúdo de um anexo específico da cotação de rascunho.
     *
     * @param id Identificador da cotação.
     * @param index Índice do anexo (0-based).
     * @return Array de bytes do PDF.
     * @throws EntityNotFoundException Se a cotação ou anexo não forem encontrados.
     */
    @Transactional(readOnly = true)
    public byte[] obterAnexoPdf(Long id, int index) {
        // Usar query que faz fetch dos anexos
        CotacaoRascunho cotacao = cotacaoRascunhoRepository.findByIdWithItens(id);
        if (cotacao == null) {
            throw new EntityNotFoundException("Cotação não encontrada com ID: " + id);
        }

        // Verificar se há anexos
        if (cotacao.getAnexos() != null && !cotacao.getAnexos().isEmpty()) {
            if (index >= 0 && index < cotacao.getAnexos().size()) {
                return cotacao.getAnexos().get(index).getConteudo();
            }
            throw new EntityNotFoundException("Anexo não encontrado no índice: " + index);
        }

        throw new EntityNotFoundException("Nenhum anexo encontrado para esta cotação");
    }

    private CotacaoRascunhoDTO toDTO(CotacaoRascunho cotacao) {
        String nomeFornecedor = "";
        if (cotacao.getFornecedorProduto() != null) {
            nomeFornecedor = cotacao.getFornecedorProduto().getRazaoSocial();
        } else if (cotacao.getFornecedorServico() != null) {
            nomeFornecedor = cotacao.getFornecedorServico().getRazaoSocial();
        }

        List<Long> itensIds = cotacao.getItensRascunho().stream()
                .map(ItemRascunho::getId)
                .collect(Collectors.toList());

        // Calcular quantidade de anexos
        int quantidadeAnexos = 0;
        if (cotacao.getAnexos() != null && !cotacao.getAnexos().isEmpty()) {
            // Contar apenas anexos que têm conteúdo
            quantidadeAnexos = (int) cotacao.getAnexos().stream()
                .filter(anexo -> anexo.getConteudo() != null && anexo.getConteudo().length > 0)
                .count();
        }

        boolean temAnexo = quantidadeAnexos > 0;

        return new CotacaoRascunhoDTO(
                cotacao.getId(),
                cotacao.getRascunho().getId(),
                cotacao.getFornecedorId(),
                cotacao.getTipoFornecedor(),
                nomeFornecedor,
                itensIds,
                cotacao.getPreco(),
                cotacao.getPrazoEmDiasUteis(),
                cotacao.getDataLimite(),
                cotacao.getGastoPrevisto(),
                cotacao.getProjeto(),
                temAnexo,
                quantidadeAnexos,
                cotacao.getDataCriacao()
        );
    }
}
