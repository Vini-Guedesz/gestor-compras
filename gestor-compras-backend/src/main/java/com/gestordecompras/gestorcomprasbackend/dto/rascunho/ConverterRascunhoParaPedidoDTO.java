package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record ConverterRascunhoParaPedidoDTO(
    // Mapeamento de cotação ID -> lista de item IDs selecionados
    // Exemplo: { 10: [1, 2], 11: [3] } significa Cotação 10 contempla itens 1 e 2, Cotação 11 contempla item 3
    @NotNull(message = "O mapeamento de cotações para itens é obrigatório")
    @NotEmpty(message = "Deve selecionar pelo menos uma cotação com itens")
    Map<Long, List<Long>> cotacaoParaItens
) {
    public List<Long> getTodosItens() {
        return cotacaoParaItens.values().stream()
            .flatMap(List::stream)
            .distinct()
            .toList();
    }
}
