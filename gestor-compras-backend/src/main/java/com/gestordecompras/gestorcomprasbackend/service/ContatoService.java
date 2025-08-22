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

@Service
public class ContatoService {

    private final ContatoRepository repository;
    private final ContatoMapper mapper;

    public ContatoService(ContatoRepository repository, ContatoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ContatoDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public ContatoDTO findById(Integer id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Contato não encontrado com ID: " + id));
    }

    @Transactional
    public ContatoDTO create(ContatoCreateDTO dto) {
        Contato contato = mapper.toEntity(dto);
        Contato savedContato = repository.save(contato);
        return mapper.toDTO(savedContato);
    }

    @Transactional
    public ContatoDTO update(ContatoUpdateDTO dto) {
        Contato contato = repository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Contato não encontrado com ID: " + dto.id()));

        mapper.updateEntityFromDTO(dto, contato);
        return mapper.toDTO(repository.save(contato));
    }

    @Transactional
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Contato não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}
