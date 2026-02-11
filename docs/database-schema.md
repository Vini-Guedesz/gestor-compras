# Esquema do Banco de Dados

```mermaid
erDiagram
    users ||--o{ rascunho : cria
    users ||--o{ solicitacao_de_pedido : solicita
    users ||--o{ historico_pedido : realiza_acao
    users ||--o{ historico_rascunho : realiza_acao
    
    rascunho ||--|{ item_rascunho : possui
    rascunho ||--o{ numero_item_disponivel : gerencia_numeros
    rascunho ||--o{ cotacao_rascunho : recebe
    cotacao_rascunho ||--o{ cotacao_rascunho_item : vincula_itens
    item_rascunho ||--o{ cotacao_rascunho_item : vinculado_a
    
    solicitacao_de_pedido ||--|{ item_pedido : possui
    solicitacao_de_pedido ||--o{ cotacao : recebe
    cotacao ||--o{ cotacao_item_pedido : vincula_itens
    item_pedido ||--o{ cotacao_item_pedido : vinculado_a
    
    fornecedor_de_produto ||--o{ cotacao : fornece
    fornecedor_de_produto ||--o{ cotacao_rascunho : fornece
    fornecedor_de_servico ||--o{ cotacao : fornece
    fornecedor_de_servico ||--o{ cotacao_rascunho : fornece
    
    cotacao ||--o{ anexo_cotacao : possui
    cotacao_rascunho ||--o{ anexo_cotacao_rascunho : possui
    pdf_storage ||--o{ anexo_cotacao : armazena
    pdf_storage ||--o{ anexo_cotacao_rascunho : armazena

    users {
        bigint id PK
        varchar username UK
        varchar senha
        varchar email UK
        varchar role
        boolean ativo
        timestamp created_at
    }

    fornecedor_de_produto {
        int id PK
        varchar nome
        varchar cnpj UK
        varchar inscricao_estadual
        bigint endereco_id FK
        bigint contato_id FK
    }

    fornecedor_de_servico {
        int id PK
        varchar nome
        varchar cnpj UK
        varchar inscricao_municipal
        bigint endereco_id FK
        bigint contato_id FK
    }

    rascunho {
        bigint id PK
        bigint user_id FK
        varchar status
        text observacao
        text objetivo
        timestamp data_criacao
        timestamp data_modificacao
        integer proximo_numero_item
        bigint pedido_gerado_id
    }

    item_rascunho {
        bigint id PK
        integer numero_item
        varchar nome
        integer quantidade
        text descricao
        text observacao
        bigint rascunho_id FK
    }

    solicitacao_de_pedido {
        bigint id PK
        varchar status
        text observacao
        timestamp data_criacao
    }

    item_pedido {
        bigint id PK
        varchar nome
        integer quantidade
        text descricao
        text observacao
        bigint solicitacao_de_pedido_id FK
    }

    cotacao {
        bigint id PK
        bigint solicitacao_de_pedido_id FK
        integer fornecedor_produto_id FK
        integer fornecedor_servico_id FK
        decimal preco
        integer prazo_em_dias_uteis
        date data_limite
        integer numero_versao
        boolean foi_editada
        timestamp data_ultima_edicao
        varchar editado_por
    }

    pdf_storage {
        bigint id PK
        varchar hash_sha256 UK
        bytea conteudo
        bigint tamanho_bytes
        integer contador_referencias
    }

    anexo_cotacao {
        bigint id PK
        bigint cotacao_id FK
        bigint pdf_storage_id FK
        integer ordem
        varchar nome_arquivo
    }
```

## Descrição das Tabelas Principais

- **users**: Armazena os usuários do sistema com roles (ADMIN, COMPRADOR, USUARIO, APROVADOR).
- **rascunho**: Área de trabalho preliminar para montagem de pedidos.
- **solicitacao_de_pedido**: Pedidos oficializados no sistema.
- **cotacao / cotacao_rascunho**: Registros de preços e prazos de fornecedores.
- **pdf_storage**: Implementação de *Content-Addressable Storage* (CAS) para deduplicação de anexos.
- **fornecedor_de_produto / fornecedor_de_servico**: Especializações de fornecedores conforme o tipo de entrega.