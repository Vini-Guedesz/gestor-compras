package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.CotacaoMapper;
import com.gestordecompras.gestorcomprasbackend.mapper.FornecedorDeProdutoMapper;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.repository.ContatoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.CotacaoRepository;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela gestão de fornecedores de produtos.
 * <p>
 * Esta classe fornece funcionalidades para o ciclo de vida completo de fornecedores de produtos,
 * incluindo criação, atualização, recuperação (paginada e lista completa) e exclusão.
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
 */
@Service
@Transactional(readOnly = true)
public class FornecedorDeProdutoService {

    private final FornecedorDeProdutoRepository repository;
    private final FornecedorDeProdutoMapper mapper;
    private final ContatoRepository contatoRepository;
    private final CotacaoRepository cotacaoRepository;
    private final CotacaoMapper cotacaoMapper;

    /**
     * Construtor com injeção de dependências.
     *
     * @param repository Repositório de fornecedores de produtos.
     * @param mapper Mapper para conversão entre entidade e DTO.
     * @param cotacaoRepository Repositório de cotações.
     * @param cotacaoMapper Mapper de cotações.
     */
    public FornecedorDeProdutoService(FornecedorDeProdutoRepository repository,
                                      FornecedorDeProdutoMapper mapper,
                                      ContatoRepository contatoRepository,
                                      CotacaoRepository cotacaoRepository,
                                      CotacaoMapper cotacaoMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.contatoRepository = contatoRepository;
        this.cotacaoRepository = cotacaoRepository;
        this.cotacaoMapper = cotacaoMapper;
    }

    /**
     * Recupera fornecedores de produtos de forma paginada.
     *
     * @param pageable Objeto contendo informações de paginação.
     * @return Uma página de DTOs de fornecedores de produtos.
     */
    public Page<FornecedorDeProdutoDTO> getAllFornecedoresDeProduto(Pageable pageable) {
        Page<FornecedorDeProduto> fornecedoresPage = repository.findAll(pageable);
        preloadContatosAdicionais(fornecedoresPage.getContent());
        return fornecedoresPage.map(mapper::toDTO);
    }

    /**
     * Recupera todos os fornecedores de produtos, carregando relacionamentos.
     *
     * @return Lista completa de DTOs de fornecedores de produtos.
     */
    public List<FornecedorDeProdutoDTO> getAllFornecedoresDeProduto() {
        return repository.findAllWithRelationships().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recupera todas as entidades de fornecedores de produtos.
     * <p>
     * Use com cautela para evitar carregamento excessivo de dados, preferindo DTOs quando possível.
     * </p>
     *
     * @return Lista de entidades FornecedorDeProduto.
     */
    public List<FornecedorDeProduto> getAllFornecedoresDeProdutoEntities() {
        return repository.findAllWithRelationships();
    }

    /**
     * Busca um fornecedor de produto pelo ID, incluindo relacionamentos.
     *
     * @param id Identificador único do fornecedor.
     * @return DTO do fornecedor encontrado.
     * @throws EntityNotFoundException Se o fornecedor não for encontrado.
     */
    public FornecedorDeProdutoDTO getFornecedorDeProdutoById(Integer id) {
        return repository.findByIdWithRelationships(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor de produto não encontrado com ID: " + id));
    }

    /**
     * Cria um novo fornecedor de produto.
     *
     * @param dto DTO com os dados para criação do fornecedor.
     * @return DTO do fornecedor criado.
     */
    @Transactional
    public FornecedorDeProdutoDTO createFornecedorDeProduto(FornecedorDeProdutoCreateDTO dto) {
        FornecedorDeProduto fornecedor = mapper.toEntity(dto);
        FornecedorDeProduto savedFornecedor = repository.save(fornecedor);
        return mapper.toDTO(savedFornecedor);
    }

    /**
     * Atualiza um fornecedor de produto existente.
     *
     * @param dto DTO com os dados atualizados e o ID do fornecedor.
     * @return DTO do fornecedor atualizado.
     * @throws EntityNotFoundException Se o fornecedor não for encontrado.
     */
    @Transactional
    public FornecedorDeProdutoDTO updateFornecedorDeProduto(FornecedorDeProdutoUpdateDTO dto) {
        FornecedorDeProduto fornecedor = repository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor de produto não encontrado com ID: " + dto.id()));

        mapper.updateEntityFromDTO(dto, fornecedor);
        return mapper.toDTO(repository.save(fornecedor));
    }

    /**
     * Exclui um fornecedor de produto pelo ID.
     *
     * @param id Identificador único do fornecedor a ser excluído.
     * @throws EntityNotFoundException Se o fornecedor não for encontrado.
     */
    @Transactional
    public void deleteFornecedorDeProduto(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Fornecedor de produto não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Busca o histórico de cotações de um fornecedor de produto.
     *
     * @param id ID do fornecedor.
     * @return Lista de DTOs de cotações.
     */
    @Transactional(readOnly = true)
    public List<CotacaoDTO> getHistoricoCotacoes(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Fornecedor de produto não encontrado com ID: " + id);
        }
        return cotacaoRepository.findByFornecedorProdutoId(id).stream()
                .map(cotacaoMapper::toDTO)
                .collect(Collectors.toList());
    }

    private void preloadContatosAdicionais(List<FornecedorDeProduto> fornecedores) {
        List<Integer> contatosIds = fornecedores.stream()
                .map(FornecedorDeProduto::getContato)
                .filter(Objects::nonNull)
                .map(contato -> contato.getId())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (!contatosIds.isEmpty()) {
            contatoRepository.findAllByIdInWithContatosAdicionais(contatosIds);
        }
    }
}
