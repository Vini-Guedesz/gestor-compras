package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.cotacao.Cotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface CotacaoRepository extends JpaRepository<Cotacao, Long> {
    @Query("SELECT SUM(c.preco) FROM Cotacao c")
    BigDecimal sumTotalPreco();

    @Query("SELECT MIN(c.preco) FROM Cotacao c")
    BigDecimal findMinPreco();

    @Query("SELECT MAX(c.preco) FROM Cotacao c")
    BigDecimal findMaxPreco();
}
