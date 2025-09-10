package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.FornecedorDeProdutoMapper;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FornecedorDeProdutoService {

    private final FornecedorDeProdutoRepository repository;
    private final FornecedorDeProdutoMapper mapper;

    public FornecedorDeProdutoService(FornecedorDeProdutoRepository repository, FornecedorDeProdutoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<FornecedorDeProdutoDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<FornecedorDeProduto> findAllEntities() {
        return repository.findAll();
    }

    public FornecedorDeProdutoDTO findById(Integer id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor de produto não encontrado com ID: " + id));
    }

    @Transactional
    public FornecedorDeProdutoDTO create(FornecedorDeProdutoCreateDTO dto) {
        FornecedorDeProduto fornecedor = mapper.toEntity(dto);
        FornecedorDeProduto savedFornecedor = repository.save(fornecedor);
        return mapper.toDTO(savedFornecedor);
    }

    @Transactional
    public FornecedorDeProdutoDTO update(FornecedorDeProdutoUpdateDTO dto) {
        FornecedorDeProduto fornecedor = repository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor de produto não encontrado com ID: " + dto.id()));

        mapper.updateEntityFromDTO(dto, fornecedor);
        return mapper.toDTO(repository.save(fornecedor));
    }

    @Transactional
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Fornecedor de produto não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}
