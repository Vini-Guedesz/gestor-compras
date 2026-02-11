package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoEditDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.cotacao.HistoricoCotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.exception.EntityNotFoundException;
import com.gestordecompras.gestorcomprasbackend.mapper.CotacaoMapper;
import com.gestordecompras.gestorcomprasbackend.mapper.HistoricoCotacaoMapper;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.AnexoCotacao;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.CotacaoItem;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.HistoricoCotacao;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeServico;
import com.gestordecompras.gestorcomprasbackend.model.pedido.ItemPedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.SolicitacaoDePedido;
import com.gestordecompras.gestorcomprasbackend.model.pedido.TipoItem;
import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class CotacaoService {

    private final CotacaoRepository cotacaoRepository;
    private final FornecedorDeProdutoRepository fornecedorProdutoRepository;
    private final FornecedorDeServicoRepository fornecedorServicoRepository;
    private final CotacaoMapper cotacaoMapper;
    private final ItemPedidoRepository itemPedidoRepository;
    private final HistoricoCotacaoRepository historicoCotacaoRepository;
    private final HistoricoCotacaoMapper historicoCotacaoMapper;
    private final PdfDeduplicationService pdfDeduplicationService;
    private final HistoricoPedidoService historicoPedidoService;
    private final UserRepository userRepository;

    public CotacaoService(CotacaoRepository cotacaoRepository,
                          FornecedorDeProdutoRepository fornecedorProdutoRepository,
                          FornecedorDeServicoRepository fornecedorServicoRepository,
                          CotacaoMapper cotacaoMapper,
                          ItemPedidoRepository itemPedidoRepository,
                          HistoricoCotacaoRepository historicoCotacaoRepository,
                          HistoricoCotacaoMapper historicoCotacaoMapper,
                          PdfDeduplicationService pdfDeduplicationService,
                          HistoricoPedidoService historicoPedidoService,
                          UserRepository userRepository) {
        this.cotacaoRepository = cotacaoRepository;
        this.fornecedorProdutoRepository = fornecedorProdutoRepository;
        this.fornecedorServicoRepository = fornecedorServicoRepository;
        this.cotacaoMapper = cotacaoMapper;
        this.itemPedidoRepository = itemPedidoRepository;
        this.historicoCotacaoRepository = historicoCotacaoRepository;
        this.historicoCotacaoMapper = historicoCotacaoMapper;
        this.pdfDeduplicationService = pdfDeduplicationService;
        this.historicoPedidoService = historicoPedidoService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<CotacaoDTO> getAllCotacoes(Pageable pageable) {
        return cotacaoRepository.findAll(pageable).map(cotacaoMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public CotacaoDTO getCotacaoById(Long id) {
        Cotacao cotacao = cotacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + id));
        return cotacaoMapper.toDTO(cotacao);
    }

    @Transactional
    public CotacaoDTO createCotacao(CotacaoCreateDTO cotacaoCreateDTO) {
        Cotacao cotacao = cotacaoMapper.toEntity(cotacaoCreateDTO);
        boolean isFornecedorProduto = false;

        if ("PRODUTO".equalsIgnoreCase(cotacaoCreateDTO.tipoFornecedor())) {
            FornecedorDeProduto fornecedor = fornecedorProdutoRepository.findById(cotacaoCreateDTO.fornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor de produto não encontrado"));
            cotacao.setFornecedorProduto(fornecedor);
            isFornecedorProduto = true;
        } else if ("SERVICO".equalsIgnoreCase(cotacaoCreateDTO.tipoFornecedor())) {
            FornecedorDeServico fornecedor = fornecedorServicoRepository.findById(cotacaoCreateDTO.fornecedorId())
                    .orElseThrow(() -> new EntityNotFoundException("Fornecedor de serviço não encontrado"));
            cotacao.setFornecedorServico(fornecedor);
            isFornecedorProduto = false;
        } else {
            throw new IllegalArgumentException("É necessário informar um tipo de fornecedor válido (PRODUTO ou SERVICO)");
        }

        // Configurar itens da cotação
        if (cotacao.getItens() != null) {
            // Validar se os itens correspondem ao tipo do fornecedor
            for (CotacaoItem item : cotacao.getItens()) {
                // Se o item ainda não tem o ItemPedido carregado (apenas ID no DTO), precisamos buscar
                // Mas aqui assumimos que o mapper ou lógica anterior já populou ou vamos popular agora
                // Se o mapper não populou o ItemPedido, não conseguimos validar aqui.
                // Vamos assumir que a validação ocorre no vínculo ou que o mapper já fez o trabalho básico.
                // Como o mapper toEntity não popula itens complexos, a validação real deve ocorrer
                // quando os itens são processados/vinculados.
                item.setCotacao(cotacao);
            }
        }

        // Calcular preço total se não informado
        if (cotacao.getPreco() == null && cotacao.getItens() != null) {
            BigDecimal total = cotacao.getItens().stream()
                    .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cotacao.setPreco(total);
        }

        cotacao.setDataCriacao(LocalDateTime.now()); // Adicionado data de criação
        cotacao.setNumeroVersao(1);
        cotacao.setFoiEditada(false);

        Cotacao savedCotacao = cotacaoRepository.save(cotacao);
        return cotacaoMapper.toDTO(savedCotacao);
    }

    @Transactional
    public CotacaoDTO updateCotacao(Long id, CotacaoUpdateDTO cotacaoUpdateDTO) {
        Cotacao cotacao = cotacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + id));

        // cotacaoMapper.updateEntityFromDTO(cotacaoUpdateDTO, cotacao); // Removido pois não existe no mapper
        // Atualização manual dos campos
        if (cotacaoUpdateDTO.prazoEmDiasUteis() != null) {
            cotacao.setPrazoEmDiasUteis(cotacaoUpdateDTO.prazoEmDiasUteis());
        }
        if (cotacaoUpdateDTO.dataLimite() != null) {
            cotacao.setDataLimite(cotacaoUpdateDTO.dataLimite());
        }
        if (cotacaoUpdateDTO.preco() != null) {
            cotacao.setPreco(cotacaoUpdateDTO.preco());
        }

        // Recalcular total se itens mudaram
        if (cotacao.getItens() != null) {
            cotacao.getItens().forEach(item -> item.setCotacao(cotacao));
            BigDecimal total = cotacao.getItens().stream()
                    .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cotacao.setPreco(total);
        }

        cotacao.setDataUltimaEdicao(LocalDateTime.now());
        cotacao.setNumeroVersao(cotacao.getNumeroVersao() + 1);
        cotacao.setFoiEditada(true);

        Cotacao updatedCotacao = cotacaoRepository.save(cotacao);
        return cotacaoMapper.toDTO(updatedCotacao);
    }

    @Transactional
    public void deleteCotacao(Long id) {
        if (!cotacaoRepository.existsById(id)) {
            throw new EntityNotFoundException("Cotação não encontrada com ID: " + id);
        }
        cotacaoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public byte[] obterAnexoPdf(Long id) {
        Cotacao cotacao = cotacaoRepository.findByIdWithAnexos(id)
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + id));

        if (cotacao.getAnexos() == null || cotacao.getAnexos().isEmpty()) {
            return null;
        }

        // Retorna o primeiro anexo
        return cotacao.getAnexos().get(0).getConteudo();
    }

    @Transactional(readOnly = true)
    public byte[] obterAnexoPdf(Long id, int index) {
        Cotacao cotacao = cotacaoRepository.findByIdWithAnexos(id)
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + id));

        if (cotacao.getAnexos() == null || index < 0 || index >= cotacao.getAnexos().size()) {
            return null;
        }

        return cotacao.getAnexos().get(index).getConteudo();
    }

    @Transactional
    public CotacaoDTO vincularItens(Long cotacaoId, List<Long> itensPedidoIds) {
        Cotacao cotacao = cotacaoRepository.findById(cotacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + cotacaoId));

        List<ItemPedido> itens = itemPedidoRepository.findAllById(itensPedidoIds);
        if (itens.isEmpty()) {
            throw new EntityNotFoundException("Nenhum item de pedido encontrado com os IDs fornecidos");
        }

        // Determinar o tipo de fornecedor da cotação
        boolean isFornecedorProduto = cotacao.getFornecedorProduto() != null;
        boolean isFornecedorServico = cotacao.getFornecedorServico() != null;

        if (!isFornecedorProduto && !isFornecedorServico) {
            throw new IllegalStateException("Cotação sem fornecedor vinculado");
        }

        // Atualizar itens da cotação com base nos itens do pedido
        List<CotacaoItem> novosItens = new ArrayList<>();
        for (ItemPedido itemPedido : itens) {
            // Validação de Regra de Negócio: Tipo de Item vs Tipo de Fornecedor
            if (isFornecedorProduto && itemPedido.getTipo() != TipoItem.PRODUTO) {
                throw new IllegalArgumentException("Fornecedor de Produto não pode cotar itens do tipo SERVICO: " + itemPedido.getNome());
            }
            if (isFornecedorServico && itemPedido.getTipo() != TipoItem.SERVICO) {
                throw new IllegalArgumentException("Fornecedor de Serviço não pode cotar itens do tipo PRODUTO: " + itemPedido.getNome());
            }

            CotacaoItem item = new CotacaoItem();
            item.setCotacao(cotacao);
            item.setItemPedido(itemPedido); // Vincula o item do pedido
            item.setQuantidade(itemPedido.getQuantidade());
            item.setPrecoUnitario(BigDecimal.ZERO); // Preço a ser preenchido depois
            novosItens.add(item);
        }

        if (cotacao.getItens() == null) {
            cotacao.setItens(new HashSet<>());
        }
        cotacao.getItens().addAll(novosItens);

        Cotacao savedCotacao = cotacaoRepository.save(cotacao);
        return cotacaoMapper.toDTO(savedCotacao);
    }

    /**
     * Edita uma cotação existente criando um registro de histórico para auditoria.
     *
     * @param editDTO DTO contendo os dados da edição.
     * @return DTO da cotação atualizada.
     */
    @Transactional
    public CotacaoDTO editarCotacao(CotacaoEditDTO editDTO) {
        Cotacao cotacao = cotacaoRepository.findByIdWithAnexos(editDTO.id())
            .orElseThrow(() -> new EntityNotFoundException("Cotação não encontrada com ID: " + editDTO.id()));

        // 1. Criar registro no histórico com os dados ATUAIS (antes da mudança)
        criarHistoricoCotacao(cotacao, editDTO);

        // 2. Aplicar as alterações na cotação
        if (editDTO.precoNovo() != null) {
            cotacao.setPreco(editDTO.precoNovo());
        } else if (editDTO.itens() != null && !editDTO.itens().isEmpty()) {
            // Se itens foram fornecidos, recalcular preço total
            // (Lógica de atualização de itens seria mais complexa aqui, simplificando para preço)
            cotacao.setPreco(editDTO.calcularPrecoTotal());
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
            // Pega o hash do anexo mais recente (maior ordem)
            AnexoCotacao anexoMaisRecente = cotacaoAnterior.getAnexos().stream()
                .max(java.util.Comparator.comparing(AnexoCotacao::getOrdem))
                .orElse(null);
            
            if (anexoMaisRecente != null) {
                historico.setHashAnexoPdfAnterior(anexoMaisRecente.getHashSha256());
                historico.setNomeArquivoAnterior(anexoMaisRecente.getNomeArquivo());
            }
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
                historicoMaisRecente.setNomeArquivoNovo(anexoMaisRecente.getNomeArquivo());
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
        Cotacao cotacao = cotacaoRepository.findByIdWithAnexos(historico.getCotacaoId())
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
        Cotacao cotacao = cotacaoRepository.findByIdWithAnexos(historico.getCotacaoId())
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
                historico.setNomeArquivoAnterior(anexoAnterior.getNomeArquivo());
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
                historico.setNomeArquivoNovo(anexoNovo.getNomeArquivo());
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

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        // Tenta buscar pelo username se o principal for string
        if (authentication != null && authentication.getName() != null) {
            return userRepository.findByEmail(authentication.getName()).orElse(null);
        }
        return null;
    }
}
