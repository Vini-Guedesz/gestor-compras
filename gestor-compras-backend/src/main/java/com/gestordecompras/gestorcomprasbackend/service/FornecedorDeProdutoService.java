package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.fornecedor.FornecedorDeProdutoUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.mapper.FornecedorDeProdutoMapper;
import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import com.gestordecompras.gestorcomprasbackend.repository.FornecedorDeProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<FornecedorDeProdutoDTO> getAllFornecedoresDeProduto(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDTO);
    }

    public List<FornecedorDeProdutoDTO> getAllFornecedoresDeProduto() {
        return repository.findAllWithRelationships().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<FornecedorDeProduto> getAllFornecedoresDeProdutoEntities() {
        return repository.findAllWithRelationships();
    }

    public FornecedorDeProdutoDTO getFornecedorDeProdutoById(Integer id) {
        return repository.findByIdWithRelationships(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor de produto não encontrado com ID: " + id));
    }

    @Transactional
    public FornecedorDeProdutoDTO createFornecedorDeProduto(FornecedorDeProdutoCreateDTO dto) {
        FornecedorDeProduto fornecedor = mapper.toEntity(dto);
        FornecedorDeProduto savedFornecedor = repository.save(fornecedor);
        return mapper.toDTO(savedFornecedor);
    }

    @Transactional
    public FornecedorDeProdutoDTO updateFornecedorDeProduto(FornecedorDeProdutoUpdateDTO dto) {
        FornecedorDeProduto fornecedor = repository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Fornecedor de produto não encontrado com ID: " + dto.id()));

        mapper.updateEntityFromDTO(dto, fornecedor);
        return mapper.toDTO(repository.save(fornecedor));
    }

    @Transactional
    public void deleteFornecedorDeProduto(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Fornecedor de produto não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}
