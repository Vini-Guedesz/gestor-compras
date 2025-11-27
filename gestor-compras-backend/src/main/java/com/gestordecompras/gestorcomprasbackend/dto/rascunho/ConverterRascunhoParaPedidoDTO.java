package com.gestordecompras.gestorcomprasbackend.dto.rascunho;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record ConverterRascunhoParaPedidoDTO(
    // DEPRECATED: Manter para retrocompatibilidade
    List<Long> itemRascunhoIds,

    // NOVO: Mapeamento de cotação ID -> lista de item IDs selecionados
    // Exemplo: { 10: [1, 2], 11: [3] } significa Cotação 10 contempla itens 1 e 2, Cotação 11 contempla item 3
    Map<Long, List<Long>> cotacaoParaItens
) {
    public boolean usaNovoPadrao() {
        return cotacaoParaItens != null && !cotacaoParaItens.isEmpty();
    }

    public List<Long> getTodosItens() {
        if (usaNovoPadrao()) {
            return cotacaoParaItens.values().stream()
                .flatMap(List::stream)
                .distinct()
                .toList();
        }
        return itemRascunhoIds;
    }
}
