-- Criação da tabela historico_pedido
CREATE TABLE historico_pedido (
    id BIGSERIAL PRIMARY KEY,
    solicitacao_de_pedido_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    data_modificacao TIMESTAMP NOT NULL,
    tipo_modificacao VARCHAR(50) NOT NULL,
    campo_modificado VARCHAR(255),
    valor_anterior TEXT,
    valor_novo TEXT,
    observacao TEXT,
    FOREIGN KEY (solicitacao_de_pedido_id) REFERENCES solicitacao_de_pedido(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Índices para melhorar performance de consultas
CREATE INDEX idx_historico_pedido_solicitacao ON historico_pedido(solicitacao_de_pedido_id);
CREATE INDEX idx_historico_pedido_user ON historico_pedido(user_id);
CREATE INDEX idx_historico_pedido_data ON historico_pedido(data_modificacao);
CREATE INDEX idx_historico_pedido_tipo ON historico_pedido(tipo_modificacao);

-- Comentários para documentação
COMMENT ON TABLE historico_pedido IS 'Tabela para rastrear todas as modificações realizadas em pedidos';
COMMENT ON COLUMN historico_pedido.tipo_modificacao IS 'Tipos: CRIACAO, ATUALIZACAO, MUDANCA_STATUS, ADICAO_ITEM, REMOCAO_ITEM, ATUALIZACAO_ITEM, ADICAO_COTACAO, REMOCAO_COTACAO, CANCELAMENTO, APROVACAO';
