package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeServicoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeServicoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeServicoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.FornecedorDeServicoMapper;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeServico;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeServicoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela gestão de fornecedores de serviços.
 * <p>
 * Esta classe fornece funcionalidades para o ciclo de vida completo de fornecedores de serviços,
 * incluindo criação, atualização, recuperação (paginada e lista completa) e exclusão.
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
 */
@Service
public class FornecedorDeServicoService {

    private final FornecedorDeServicoRepository repository;
    private final FornecedorDeServicoMapper mapper;

    /**
     * Construtor com injeção de dependências.
     *
     * @param repository Repositório de fornecedores de serviços.
     * @param mapper Mapper para conversão entre entidade e DTO.
     */
    public FornecedorDeServicoService(FornecedorDeServicoRepository repository, FornecedorDeServicoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Recupera fornecedores de serviços de forma paginada.
     *
     * @param pageable Objeto contendo informações de paginação.
     * @return Uma página de DTOs de fornecedores de serviços.
     */
    public Page<FornecedorDeServicoDTO> getAllFornecedoresDeServico(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDTO);
    }

    /**
     * Recupera todos os fornecedores de serviços, carregando relacionamentos.
     *
     * @return Lista completa de DTOs de fornecedores de serviços.
     */
    public List<FornecedorDeServicoDTO> getAllFornecedoresDeServico() {
        return repository.findAllWithRelationships().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um fornecedor de serviço pelo ID, incluindo relacionamentos.
     *
     * @param id Identificador único do fornecedor.
     * @return DTO do fornecedor encontrado.
     * @throws EntityNotFoundException Se o fornecedor não for encontrado.
     */
    public FornecedorDeServicoDTO getFornecedorDeServicoById(Integer id) {
        return repository.findByIdWithRelationships(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor de serviço não encontrado com ID: " + id));
    }

    /**
     * Cria um novo fornecedor de serviço.
     *
     * @param dto DTO com os dados para criação do fornecedor.
     * @return DTO do fornecedor criado.
     */
    @Transactional
    public FornecedorDeServicoDTO createFornecedorDeServico(FornecedorDeServicoCreateDTO dto) {
        FornecedorDeServico fornecedor = mapper.toEntity(dto);
        FornecedorDeServico savedFornecedor = repository.save(fornecedor);
        return mapper.toDTO(savedFornecedor);
    }

    /**
     * Atualiza um fornecedor de serviço existente.
     *
     * @param dto DTO com os dados atualizados e o ID do fornecedor.
     * @return DTO do fornecedor atualizado.
     * @throws EntityNotFoundException Se o fornecedor não for encontrado.
     */
    @Transactional
    public FornecedorDeServicoDTO updateFornecedorDeServico(FornecedorDeServicoUpdateDTO dto) {
        FornecedorDeServico fornecedor = repository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor de serviço não encontrado com ID: " + dto.id()));

        mapper.updateEntityFromDTO(dto, fornecedor);
        return mapper.toDTO(repository.save(fornecedor));
    }

    /**
     * Exclui um fornecedor de serviço pelo ID.
     *
     * @param id Identificador único do fornecedor a ser excluído.
     * @throws EntityNotFoundException Se o fornecedor não for encontrado.
     */
    @Transactional
    public void deleteFornecedorDeServico(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Fornecedor de serviço não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}
