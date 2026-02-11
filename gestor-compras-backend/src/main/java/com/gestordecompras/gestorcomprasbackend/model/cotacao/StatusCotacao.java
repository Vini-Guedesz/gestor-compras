package com.gestordecompras.gestorcomprasbackend.model.cotacao;

/**
 * Define o status de uma cotação específica dentro de um processo de compra.
 */
public enum StatusCotacao {
    /**
     * Cotação recebida e sob análise do comprador.
     */
    EM_ANALISE,

    /**
     * Cotação selecionada/vencedora para os itens cotados.
     * Esta cotação fará parte do pedido final.
     */
    APROVADA,

    /**
     * Cotação não selecionada (preço alto, prazo ruim, etc).
     */
    REJEITADA,

    /**
     * Cotação cancelada ou inválida.
     */
    CANCELADA
}
