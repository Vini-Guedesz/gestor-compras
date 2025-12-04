package com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido;

import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.itempedido.ItemPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.model.pedido.StatusPedido;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record SolicitacaoDePedidoDTO(
    Long id,
    List<ItemPedidoDTO> itens,
    List<CotacaoDTO> cotacoes,

    @NotNull(message = "O status é obrigatório")
    StatusPedido status,

    @Size(max = 1000, message = "A observação deve ter no máximo 1000 caracteres")
    String observacao,

    LocalDateTime dataCriacao
) {}