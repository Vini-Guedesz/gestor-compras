package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.endereco.EnderecoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.endereco.EnderecoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.endereco.EnderecoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.EnderecoMapper;
import com.gestordecompras.gestorcomprasbackend.model.endereco.Endereco;
import com.gestordecompras.gestorcomprasbackend.repository.EnderecoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela gestão de endereços no sistema.
 * <p>
 * Esta classe fornece métodos para criar, ler, atualizar e excluir endereços,
 * além de realizar as conversões necessárias entre entidades e DTOs.
 * </p>
 *
 * @author Gestor de Compras
 * @since 1.0
 */
@Service
public class EnderecoService {

    private final EnderecoRepository repository;
    private final EnderecoMapper mapper;

    /**
     * Construtor com injeção de dependências.
     *
     * @param repository Repositório de endereços.
     * @param mapper Mapper para conversão entre entidade e DTO.
     */
    public EnderecoService(EnderecoRepository repository, EnderecoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Recupera todos os endereços cadastrados.
     *
     * @return Lista de DTOs representando todos os endereços.
     */
    public List<EnderecoDTO> getAllEnderecos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um endereço pelo seu ID.
     *
     * @param id Identificador único do endereço.
     * @return DTO do endereço encontrado.
     * @throws EntityNotFoundException Se o endereço não for encontrado.
     */
    public EnderecoDTO getEnderecoById(Integer id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado com ID: " + id));
    }

    /**
     * Cria um novo endereço.
     *
     * @param dto DTO contendo os dados para criação do endereço.
     * @return DTO do endereço criado e persistido.
     */
    @Transactional
    public EnderecoDTO createEndereco(EnderecoCreateDTO dto) {
        Endereco endereco = mapper.toEntity(dto);
        Endereco savedEndereco = repository.save(endereco);
        return mapper.toDTO(savedEndereco);
    }

    /**
     * Atualiza um endereço existente.
     *
     * @param dto DTO contendo os dados atualizados e o ID do endereço.
     * @return DTO do endereço atualizado.
     * @throws EntityNotFoundException Se o endereço não for encontrado.
     */
    @Transactional
    public EnderecoDTO updateEndereco(EnderecoUpdateDTO dto) {
        Endereco endereco = repository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado com ID: " + dto.id()));

        mapper.updateEntityFromDTO(dto, endereco);
        return mapper.toDTO(repository.save(endereco));
    }

    /**
     * Exclui um endereço pelo ID.
     *
     * @param id Identificador único do endereço a ser excluído.
     * @throws EntityNotFoundException Se o endereço não for encontrado.
     */
    @Transactional
    public void deleteEndereco(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Endereço não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}
