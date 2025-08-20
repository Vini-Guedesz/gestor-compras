package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.fornecedor.FornecedorDeServico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorDeServicoRepository extends JpaRepository<FornecedorDeServico, Integer> {
}
