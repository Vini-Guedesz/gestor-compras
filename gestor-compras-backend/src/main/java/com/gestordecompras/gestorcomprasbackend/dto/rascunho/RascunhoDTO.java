package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que representa os dados de um rascunho de pedido.
 * <p>
 * Contém informações sobre o rascunho, seus itens e metadados de criação/modificação.
 * Utilizado para listagem e detalhes de rascunhos.
 * </p>
 */
@Schema(description = "DTO de resposta com detalhes do rascunho de pedido")
public record RascunhoDTO(
    @Schema(description = "ID único do rascunho", example = "1")
    Long id,

    @Schema(description = "Lista de itens adicionados ao rascunho")
    List<ItemRascunhoDTO> itens,

    @Schema(description = "ID do usuário que criou o rascunho", example = "1")
    Long criadorId,

    @Schema(description = "Observações ou anotações sobre o rascunho", example = "Revisar preços antes de converter")
    String observacao,

    @Schema(description = "Data e hora de criação", example = "2025-10-01T10:00:00")
    LocalDateTime dataCriacao,

    @Schema(description = "Data e hora da última modificação", example = "2025-10-01T11:30:00")
    LocalDateTime dataModificacao,

    @Schema(description = "Status atual do rascunho (ATIVO, EM_COTACAO, FINALIZADO)", example = "ATIVO")
    String status,

    @Schema(description = "ID do pedido gerado a partir deste rascunho (apenas se status for FINALIZADO)", example = "101")
    Long pedidoGeradoId
) {}
