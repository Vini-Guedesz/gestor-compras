package com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido;

import com.gestordecompras.gestorcomprasbackend.dto.cotacao.CotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.itempedido.ItemPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.model.pedido.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que representa os dados completos de uma solicitação de pedido.
 * <p>
 * Utilizado para retornar informações detalhadas sobre pedidos, incluindo seus itens
 * e as cotações vinculadas.
 * </p>
 */
@Schema(description = "DTO de resposta com detalhes da solicitação de pedido")
public record SolicitacaoDePedidoDTO(
    @Schema(description = "ID único do pedido", example = "100")
    Long id,

    @Schema(description = "Lista de itens solicitados no pedido")
    List<ItemPedidoDTO> itens,

    @Schema(description = "Lista de cotações de fornecedores vinculadas ao pedido")
    List<CotacaoDTO> cotacoes,

    @Schema(description = "Status atual do fluxo de aprovação do pedido", example = "PENDENTE")
    @NotNull(message = "O status é obrigatório")
    StatusPedido status,

    @Schema(description = "Observações gerais ou justificativas do pedido", example = "Pedido urgente para o departamento de TI")
    @Size(max = 1000, message = "A observação deve ter no máximo 1000 caracteres")
    String observacao,

    @Schema(description = "Data e hora em que o pedido foi criado", example = "2025-10-01T10:00:00")
    LocalDateTime dataCriacao
) {}