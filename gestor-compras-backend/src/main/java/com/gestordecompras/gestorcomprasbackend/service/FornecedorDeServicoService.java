package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeServicoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeServicoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeServicoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.FornecedorDeServicoMapper;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeServico;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeServicoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FornecedorDeServicoService {

    private final FornecedorDeServicoRepository repository;
    private final FornecedorDeServicoMapper mapper;

    public FornecedorDeServicoService(FornecedorDeServicoRepository repository, FornecedorDeServicoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<FornecedorDeServicoDTO> getAllFornecedoresDeServico() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public FornecedorDeServicoDTO getFornecedorDeServicoById(Integer id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor de serviço não encontrado com ID: " + id));
    }

    @Transactional
    public FornecedorDeServicoDTO createFornecedorDeServico(FornecedorDeServicoCreateDTO dto) {
        FornecedorDeServico fornecedor = mapper.toEntity(dto);
        FornecedorDeServico savedFornecedor = repository.save(fornecedor);
        return mapper.toDTO(savedFornecedor);
    }

    @Transactional
    public FornecedorDeServicoDTO updateFornecedorDeServico(FornecedorDeServicoUpdateDTO dto) {
        FornecedorDeServico fornecedor = repository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor de serviço não encontrado com ID: " + dto.id()));

        mapper.updateEntityFromDTO(dto, fornecedor);
        return mapper.toDTO(repository.save(fornecedor));
    }

    @Transactional
    public void deleteFornecedorDeServico(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Fornecedor de serviço não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}
