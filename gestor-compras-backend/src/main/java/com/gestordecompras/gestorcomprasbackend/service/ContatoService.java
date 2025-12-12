package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.contato.ContatoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.ContatoMapper;
import com.gestordecompras.gestorcomprasbackend.model.contato.Contato;
import com.gestordecompras.gestorcomprasbackend.repository.ContatoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de lógica de negócio para Contatos.
 *
 * <p>Responsável por:</p>
 * <ul>
 *   <li>Gerenciamento de contatos de fornecedores (CRUD)</li>
 *   <li>Validação de telefones (fixo e celular)</li>
 *   <li>Validação de formato de email</li>
 *   <li>Conversão entre entidades e DTOs via MapStruct</li>
 * </ul>
 *
 * <p><b>Transações:</b> Métodos de escrita são transacionais.
 * Em caso de erro, toda operação é revertida (rollback automático).</p>
 *
 * @since 1.0.0
 * @author Equipe de Desenvolvimento
 * @see Contato
 * @see ContatoDTO
 * @see ContatoRepository
 * @see ContatoMapper
 */
@Service
@Transactional
public class ContatoService {

    private final ContatoRepository repository;
    private final ContatoMapper mapper;

    /**
     * Construtor com injeção de dependências.
     *
     * @param repository Repository JPA para contatos
     * @param mapper Mapper MapStruct para conversão entidade ↔ DTO
     */
    public ContatoService(ContatoRepository repository, ContatoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Lista todos os contatos cadastrados no sistema.
     *
     * <p>Retorna lista com informações de contato incluindo nome,
     * telefones (fixo e celular) e email.</p>
     *
     * @return Lista de ContatoDTO com dados de todos os contatos
     */
    @Transactional(readOnly = true)
    public List<ContatoDTO> getAllContatos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um contato específico por ID.
     *
     * @param id ID do contato
     * @return ContatoDTO com dados do contato
     * @throws EntityNotFoundException se contato não encontrado com o ID fornecido
     */
    @Transactional(readOnly = true)
    public ContatoDTO getContatoById(Integer id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Contato não encontrado com ID: " + id));
    }

    /**
     * Cria um novo contato.
     *
     * <p><b>Validações realizadas (via DTO):</b></p>
     * <ul>
     *   <li>Pelo menos um telefone obrigatório (fixo ou celular)</li>
     *   <li>Telefone fixo: formato (XX) XXXX-XXXX</li>
     *   <li>Celular: formato (XX) XXXXX-XXXX</li>
     *   <li>Email: formato válido (validador customizado)</li>
     * </ul>
     *
     * @param dto Dados do novo contato
     * @return ContatoDTO com dados do contato criado incluindo ID gerado
     */
    public ContatoDTO createContato(ContatoCreateDTO dto) {
        Contato contato = mapper.toEntity(dto);
        Contato savedContato = repository.save(contato);
        return mapper.toDTO(savedContato);
    }

    /**
     * Atualiza um contato existente com merge parcial.
     *
     * <p>Apenas campos não-nulos do DTO serão atualizados.
     * Campos omitidos mantêm seus valores originais.</p>
     *
     * @param dto Dados de atualização (ID obrigatório, demais campos opcionais)
     * @return ContatoDTO atualizado
     * @throws EntityNotFoundException se contato não encontrado
     */
    public ContatoDTO updateContato(ContatoUpdateDTO dto) {
        Contato contato = repository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Contato não encontrado com ID: " + dto.id()));

        mapper.updateEntityFromDTO(dto, contato);
        return mapper.toDTO(repository.save(contato));
    }

    /**
     * Remove um contato permanentemente do sistema.
     *
     * <p>Esta operação não pode ser desfeita.
     * Contatos vinculados a fornecedores podem gerar erro de integridade.</p>
     *
     * @param id ID do contato a ser removido
     * @throws EntityNotFoundException se contato não encontrado
     */
    public void deleteContato(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Contato não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}
