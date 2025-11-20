package com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido;

import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.itempedido.ItemPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.model.pedido.StatusPedido;

import java.time.LocalDateTime;
import java.util.List;

public record SolicitacaoDePedidoDTO(
    Long id,
    List<ItemPedidoDTO> itens,
    List<CotacaoDTO> cotacoes,
    StatusPedido status,
    String observacao,
    LocalDateTime dataCriacao
) {}