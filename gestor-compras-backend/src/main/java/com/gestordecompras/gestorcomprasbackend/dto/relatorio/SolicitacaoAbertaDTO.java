package com.gestordecompras.gestorcomprasbackend.dto.relatorio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoAbertaDTO {
    private String solicitacaoId;
    private String status;
    private LocalDateTime dataCriacao;
    private Integer quantidadeItens;
    private String observacao;
    private String itensResumidos; // Lista dos nomes dos itens separados por vírgula
}
