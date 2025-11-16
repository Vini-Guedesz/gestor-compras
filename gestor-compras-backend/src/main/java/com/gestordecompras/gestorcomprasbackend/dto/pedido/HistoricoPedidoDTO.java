package com.gestordecompras.gestorcomprasbackend.dto.pedido;

import com.gestordecompras.gestorcomprasbackend.model.pedido.HistoricoPedido;

import java.time.LocalDateTime;

public record HistoricoPedidoDTO(
    Long id,
    Long solicitacaoDePedidoId,
    Long usuarioId,
    String nomeUsuario,
    LocalDateTime dataModificacao,
    HistoricoPedido.TipoModificacao tipoModificacao,
    String campoModificado,
    String valorAnterior,
    String valorNovo,
    String observacao
) {}
