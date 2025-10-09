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

@Service
public class EnderecoService {

    private final EnderecoRepository repository;
    private final EnderecoMapper mapper;

    public EnderecoService(EnderecoRepository repository, EnderecoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<EnderecoDTO> getAllEnderecos() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public EnderecoDTO getEnderecoById(Integer id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado com ID: " + id));
    }

    @Transactional
    public EnderecoDTO createEndereco(EnderecoCreateDTO dto) {
        Endereco endereco = mapper.toEntity(dto);
        Endereco savedEndereco = repository.save(endereco);
        return mapper.toDTO(savedEndereco);
    }

    @Transactional
    public EnderecoDTO updateEndereco(EnderecoUpdateDTO dto) {
        Endereco endereco = repository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado com ID: " + dto.id()));

        mapper.updateEntityFromDTO(dto, endereco);
        return mapper.toDTO(repository.save(endereco));
    }

    @Transactional
    public void deleteEndereco(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Endereço não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}
