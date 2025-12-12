# Esquema do Banco de Dados

## Diagrama Entidade-Relacionamento (ER)

```mermaid
erDiagram
    %% ==================== USUÁRIOS ====================
    users {
        bigint id PK
        varchar username
        varchar email UK
        varchar password
        varchar role
    }

    %% ==================== ENDEREÇOS E CONTATOS ====================
    endereco {
        bigint id PK
        varchar cep
        varchar logradouro
        varchar numero
        varchar complemento
        varchar bairro
        varchar cidade
        varchar estado
    }

    contato {
        bigint id PK
        varchar nome_contato
        varchar telefone_fixo
        varchar celular
        varchar email
    }

    %% ==================== FORNECEDORES ====================
    fornecedor_de_produto {
        integer id PK
        varchar razao_social
        varchar cnpj UK
        varchar inscricao_estadual
        bigint endereco_id FK
        bigint contato_id FK
    }

    fornecedor_de_servico {
        integer id PK
        varchar razao_social
        varchar cnpj UK
        varchar inscricao_municipal
        bigint endereco_id FK
        bigint contato_id FK
    }

    %% ==================== RASCUNHOS ====================
    rascunho {
        bigint id PK
        bigint user_id FK
        text observacao
        timestamp data_criacao
        timestamp data_modificacao
        integer proximo_numero_item
        varchar status
        bigint pedido_gerado_id FK
    }

    item_rascunho {
        bigint id PK
        bigint rascunho_id FK
        integer numero_item
        varchar nome
        integer quantidade
        text descricao
        text observacao
    }

    numero_item_disponivel {
        bigint id PK
        bigint rascunho_id FK
        integer numero_item
    }

    cotacao_rascunho {
        bigint id PK
        bigint rascunho_id FK
        integer fornecedor_produto_id FK
        integer fornecedor_servico_id FK
        decimal preco
        integer prazo_em_dias_uteis
        date data_limite
        timestamp data_criacao
    }

    %% ==================== PDF STORAGE (CAS) ====================
    pdf_storage {
        bigint id PK
        varchar hash_sha256 UK "Content Addressable"
        bytea conteudo "Armazenado uma única vez"
        bigint tamanho_bytes
        timestamp data_criacao
        bigint contador_referencias
    }

    anexo_cotacao_rascunho {
        bigint id PK
        bigint cotacao_rascunho_id FK
        bigint pdf_storage_id FK "Referência CAS"
        integer ordem
        varchar nome_arquivo
    }

    cotacao_rascunho_item {
        bigint cotacao_rascunho_id FK,PK
        bigint item_rascunho_id FK,PK
    }

    %% ==================== PEDIDOS ====================
    solicitacao_de_pedido {
        bigint id PK
        varchar status
        text observacao
        timestamp data_criacao
        bigint version "Optimistic Locking"
    }

    item_pedido {
        bigint id PK
        bigint solicitacao_de_pedido_id FK
        varchar nome
        integer quantidade
        text descricao
        text observacao
    }

    %% ==================== COTAÇÕES ====================
    cotacao {
        bigint id PK
        bigint solicitacao_de_pedido_id FK
        integer fornecedor_produto_id FK
        integer fornecedor_servico_id FK
        decimal preco "Calculado automaticamente"
        integer prazo_em_dias_uteis
        date data_limite
        timestamp data_criacao
        bigint version "Optimistic Locking"
        boolean foi_editada
        integer numero_versao
        timestamp data_ultima_edicao
        varchar motivo_ultima_edicao
        varchar editado_por
    }

    cotacao_item {
        bigint id PK
        bigint cotacao_id FK
        bigint item_pedido_id FK
        decimal preco_unitario
        integer quantidade
        decimal subtotal
    }

    anexo_cotacao {
        bigint id PK
        bigint cotacao_id FK
        bigint pdf_storage_id FK "Referência CAS"
        integer ordem
        varchar nome_arquivo
    }

    cotacao_item_pedido {
        bigint cotacao_id FK,PK
        bigint item_pedido_id FK,PK
    }

    %% ==================== HISTÓRICO ====================
    historico_pedido {
        bigint id PK
        bigint solicitacao_de_pedido_id FK
        bigint user_id FK
        timestamp data_modificacao
        varchar tipo_modificacao
        varchar campo_modificado
        text valor_anterior
        text valor_novo
        text observacao
    }

    historico_rascunho {
        bigint id PK
        bigint rascunho_id FK
        bigint user_id FK
        timestamp data_modificacao
        varchar tipo_acao
        text descricao
        integer numero_item
        varchar nome_item
        text detalhes
    }

    historico_cotacao {
        bigint id PK
        bigint cotacao_id FK
        integer versao
        decimal preco_anterior
        integer prazo_anterior
        date data_limite_anterior
        decimal preco_novo
        integer prazo_novo
        date data_limite_novo
        varchar motivo_edicao
        varchar editado_por
        timestamp data_edicao
        varchar hash_anexo_pdf_anterior "Referência ao hash"
        varchar hash_anexo_pdf_novo "Referência ao hash"
    }

    %% ==================== RELACIONAMENTOS ====================

    %% PDF Storage
    anexo_cotacao }o--|| pdf_storage : referencia
    anexo_cotacao_rascunho }o--|| pdf_storage : referencia

    %% Fornecedores
    fornecedor_de_produto ||--o| endereco : possui
    fornecedor_de_produto ||--o| contato : possui
    fornecedor_de_servico ||--o| endereco : possui
    fornecedor_de_servico ||--o| contato : possui

    %% Rascunhos
    users ||--o{ rascunho : cria
    rascunho ||--o{ item_rascunho : contém
    rascunho ||--o{ numero_item_disponivel : pool
    rascunho ||--o{ cotacao_rascunho : possui
    rascunho ||--o| solicitacao_de_pedido : gera

    %% Cotação Rascunho
    cotacao_rascunho }o--o| fornecedor_de_produto : fornecedor
    cotacao_rascunho }o--o| fornecedor_de_servico : fornecedor
    cotacao_rascunho ||--o{ anexo_cotacao_rascunho : anexos
    cotacao_rascunho }o--o{ item_rascunho : cotacao_rascunho_item

    %% Pedidos
    solicitacao_de_pedido ||--|{ item_pedido : itens
    solicitacao_de_pedido ||--o{ cotacao : cotacoes

    %% Cotações
    cotacao }o--o| fornecedor_de_produto : fornecedor
    cotacao }o--o| fornecedor_de_servico : fornecedor
    cotacao ||--|{ cotacao_item : itens
    cotacao ||--o{ anexo_cotacao : anexos
    cotacao_item }o--|| item_pedido : referencia

    %% Histórico
    historico_pedido }o--|| solicitacao_de_pedido : pedido
    historico_pedido }o--|| users : usuario
    historico_rascunho }o--|| rascunho : rascunho
    historico_rascunho }o--|| users : usuario
    historico_cotacao }o--|| cotacao : cotacao_id
```

## Estrutura Detalhada das Tabelas

... (Tabelas users, endereco, contato, fornecedores, rascunho, item_rascunho, numero_item_disponivel, cotacao_rascunho mantidas iguais) ...

---

### 10. Tabela: `pdf_storage` (NOVO)
**Descrição:** Armazenamento centralizado de PDFs com Content-Addressable Storage (CAS)

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único |
| `hash_sha256` | VARCHAR(64) | NOT NULL, UNIQUE | Hash do conteúdo (chave do CAS) |
| `conteudo` | BYTEA | NOT NULL | Bytes do arquivo PDF |
| `tamanho_bytes` | BIGINT | NOT NULL | Tamanho do arquivo |
| `data_criacao` | TIMESTAMP | DEFAULT NOW() | Data de armazenamento |
| `contador_referencias` | BIGINT | DEFAULT 0 | Número de anexos usando este PDF |

**Índices:**
```sql
CREATE UNIQUE INDEX idx_pdf_storage_hash ON pdf_storage(hash_sha256);
```

**Deduplificação Real:**
- O conteúdo é armazenado **uma única vez** nesta tabela.
- `anexo_cotacao` e `anexo_cotacao_rascunho` apenas referenciam o ID daqui.
- Economia massiva de espaço e integridade garantida pelo hash.

---

### 11. Tabela: `anexo_cotacao_rascunho`
**Descrição:** Vínculo entre cotação de rascunho e PDF armazenado

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único |
| `cotacao_rascunho_id` | BIGINT | FK → cotacao_rascunho(id) | Cotação pai |
| `pdf_storage_id` | BIGINT | FK → pdf_storage(id) | Referência ao conteúdo |
| `ordem` | INTEGER | DEFAULT 0 | Ordem de exibição |
| `nome_arquivo` | VARCHAR(255) | | Nome original (metadado) |

**Nota:**
Múltiplos anexos (mesmo de cotações diferentes) podem apontar para o mesmo `pdf_storage_id` se o conteúdo for idêntico.

---

### 16. Tabela: `anexo_cotacao`
**Descrição:** Vínculo entre cotação formal e PDF armazenado

| Coluna | Tipo | Constraints | Descrição |
|--------|------|-------------|-----------|
| `id` | BIGSERIAL | PRIMARY KEY | Identificador único |
| `cotacao_id` | BIGINT | FK → cotacao(id) | Cotação pai |
| `pdf_storage_id` | BIGINT | FK → pdf_storage(id) | Referência ao conteúdo |
| `ordem` | INTEGER | DEFAULT 0 | Ordem de exibição |
| `nome_arquivo` | VARCHAR(255) | | Nome original (metadado) |

---

... (Restante das tabelas mantidas) ...

## Migrations Aplicadas

| Versão | Arquivo | Descrição |
|--------|---------|-----------|
| V1 | create-initial-schema.sql | Users, endereco, contato |
| V2 | create-fornecedores-tables.sql | Fornecedores produto/serviço |
| V3 | create-pedidos-rascunhos-cotacoes.sql | Pedidos, rascunhos, cotações |
| V4 | create-anexo-cotacao-rascunho-table.sql | Anexos rascunho (legado) |
| V5 | add-status-to-rascunho.sql | Status em rascunhos |
| V6 | create-anexo-cotacao-table.sql | Anexos cotação (legado) |
| V7 | add-soft-delete-to-historicos.sql | Soft delete históricos |
| V8 | refactor-cotacao-to-use-cotacao-item.sql | CotacaoItem (preços individuais) |
| V9 | add-version-columns-for-optimistic-locking.sql | Optimistic locking |
| V10 | add-version-to-cotacao-rascunho.sql | Version em rascunho |
| V11 | add-cotacao-audit-fields.sql | Auditoria de cotações |
| V13 | rename-username-to-nome.sql | Ajuste nome usuário |
| V14 | implement-content-addressable-storage-for-pdfs.sql | **Nova arquitetura CAS (pdf_storage)** |

---

**Próximo:** [API REST](./api-endpoints.md)