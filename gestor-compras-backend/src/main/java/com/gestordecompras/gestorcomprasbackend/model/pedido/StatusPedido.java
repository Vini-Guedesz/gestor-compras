package com.gestordecompras.gestorcomprasbackend.model.pedido;

/**
 * Status do ciclo de vida de um pedido.
 *
 * Fluxo normal:
 * 1. EM_NEGOCIACAO → Comprador negocia com fornecedores
 * 2. PENDENTE_APROVACAO → Aguarda aprovação do aprovador
 * 3. APROVADO → Pedido aprovado, pode prosseguir
 *
 * Fluxos alternativos:
 * - CANCELADO → Pedido cancelado (pode ser a qualquer momento)
 * - Devolução: PENDENTE_APROVACAO → EM_NEGOCIACAO (com motivo)
 */
public enum StatusPedido {
    /**
     * Pedido está sendo negociado pelo comprador com fornecedores.
     * Comprador pode editar, adicionar/remover itens, gerenciar cotações.
     */
    EM_NEGOCIACAO,

    /**
     * Pedido enviado para aprovação.
     * Aguardando decisão do aprovador.
     * Comprador não pode mais editar.
     */
    PENDENTE_APROVACAO,

    /**
     * Pedido aprovado pelo aprovador.
     * Pode prosseguir com a compra.
     * Estado final (não pode ser editado).
     */
    APROVADO,

    /**
     * Pedido cancelado.
     * Pode ser cancelado pelo comprador (EM_NEGOCIACAO) ou aprovador (PENDENTE_APROVACAO).
     * Estado final (não pode voltar).
     */
    CANCELADO,

    // Mantidos para compatibilidade com dados existentes
    @Deprecated
    PENDENTE,

    @Deprecated
    EM_ANALISE,

    @Deprecated
    EM_ANDAMENTO
}
