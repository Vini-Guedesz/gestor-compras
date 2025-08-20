package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeProduto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorDeProdutoRepository extends JpaRepository<FornecedorDeProduto, Integer> {
}
