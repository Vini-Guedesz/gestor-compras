package com.gestordecompras.gestorcomprasbackend.model.cotacao;

/**
 * Define o status de uma cotacao especifica dentro de um processo de compra.
 */
public enum StatusCotacao {
    /**
     * Cotacao recebida e sob analise do comprador.
     */
    EM_ANALISE,

    /**
     * Cotacao selecionada/vencedora para os itens cotados.
     */
    APROVADA,

    /**
     * Cotacao parcialmente selecionada (apenas alguns itens).
     */
    PARCIAL,

    /**
     * Cotacao nao selecionada.
     */
    REJEITADA,

    /**
     * Cotacao cancelada ou invalida.
     */
    CANCELADA
}
