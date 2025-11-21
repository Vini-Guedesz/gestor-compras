# Refatoração: Relacionamento Cotação-Item

## Bug #5 - Limitação Arquitetural Atual

### Problema
Atualmente, o relacionamento entre `Cotacao` e `ItemPedido` é implementado como um `@ManyToMany` direto. Esta abordagem tem limitações:

1. **Sem preço por item**: O preço é armazenado apenas no nível da cotação inteira (`Cotacao.preco`), não por item individual
2. **Sem quantidade específica**: Não é possível registrar quantidades diferentes das solicitadas
3. **Sem metadados do par**: Não há como armazenar observações ou dados específicos sobre cada item dentro de uma cotação

### Design Atual
```
Cotacao (1) ----< cotacao_item_pedido >---- (N) ItemPedido
              (tabela de junção simples)
```

**Tabela atual:** `cotacao_item_pedido`
- `cotacao_id` (FK)
- `item_pedido_id` (FK)

### Quando Refatorar
Esta refatoração deve ser feita SE/QUANDO o sistema precisar de:
- Preço unitário por item em cada cotação
- Quantidade cotada diferente da quantidade solicitada
- Observações específicas sobre itens individuais na cotação
- Desconto ou markup por item
- Qualquer outro atributo específico do par cotação-item

### Design Proposto

#### Nova Entidade: CotacaoItem
```java
@Entity
@Table(name = "cotacao_item")
public class CotacaoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotacao_id", nullable = false)
    private Cotacao cotacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_pedido_id", nullable = false)
    private ItemPedido itemPedido;

    @Column(nullable = false)
    private BigDecimal precoUnitario;

    @Column(nullable = false)
    private Integer quantidadeCotada;

    private String observacao;

    // Preço total calculado
    public BigDecimal getPrecoTotal() {
        return precoUnitario.multiply(new BigDecimal(quantidadeCotada));
    }
}
```

#### Modificação na Entidade Cotacao
```java
@Entity
@Table(name = "cotacao")
public class Cotacao {
    // ... outros campos ...

    // REMOVER este relacionamento:
    // @ManyToMany
    // private Set<ItemPedido> itensPedido;

    // ADICIONAR este relacionamento:
    @OneToMany(mappedBy = "cotacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CotacaoItem> itens = new ArrayList<>();

    // O campo 'preco' pode ser mantido como preço total ou calculado dinamicamente
    public BigDecimal getPrecoTotal() {
        return itens.stream()
            .map(CotacaoItem::getPrecoTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

### Passos para Migração

#### 1. Criar Migration Flyway
```sql
-- V6__refactor_cotacao_item_relationship.sql

-- Criar nova tabela com campos adicionais
CREATE TABLE cotacao_item (
    id BIGSERIAL PRIMARY KEY,
    cotacao_id BIGINT NOT NULL REFERENCES cotacao(id) ON DELETE CASCADE,
    item_pedido_id BIGINT NOT NULL REFERENCES item_pedido(id) ON DELETE CASCADE,
    preco_unitario DECIMAL(19,2) NOT NULL,
    quantidade_cotada INTEGER NOT NULL,
    observacao TEXT,
    UNIQUE(cotacao_id, item_pedido_id)
);

-- Migrar dados existentes da tabela antiga para a nova
INSERT INTO cotacao_item (cotacao_id, item_pedido_id, preco_unitario, quantidade_cotada)
SELECT
    cip.cotacao_id,
    cip.item_pedido_id,
    COALESCE(c.preco / NULLIF(COUNT(*) OVER (PARTITION BY cip.cotacao_id), 0), 0) as preco_unitario,
    ip.quantidade as quantidade_cotada
FROM cotacao_item_pedido cip
INNER JOIN cotacao c ON c.id = cip.cotacao_id
INNER JOIN item_pedido ip ON ip.id = cip.item_pedido_id;

-- Dropar tabela antiga
DROP TABLE cotacao_item_pedido;

-- Criar índices
CREATE INDEX idx_cotacao_item_cotacao ON cotacao_item(cotacao_id);
CREATE INDEX idx_cotacao_item_item_pedido ON cotacao_item(item_pedido_id);
```

#### 2. Criar Entidade JPA

Criar `model/cotacao/CotacaoItem.java` com o código acima.

#### 3. Atualizar DTOs

**CotacaoItemDTO.java:**
```java
public record CotacaoItemDTO(
    Long id,
    Long itemPedidoId,
    String itemNome,
    BigDecimal precoUnitario,
    Integer quantidadeCotada,
    BigDecimal precoTotal,
    String observacao
) {}
```

**CotacaoDTO.java:**
Modificar para incluir lista de CotacaoItemDTO ao invés de apenas IDs.

#### 4. Atualizar Services

Modificar `CotacaoService` para trabalhar com `CotacaoItem` ao invés de relacionamento direto.

#### 5. Atualizar Frontend

Modificar formulários e visualizações para permitir entrada de preço unitário por item.

### Impacto Estimado

- **Backend**:
  - 1 nova entidade
  - 1 migration
  - Modificações em 3-4 services
  - Modificações em 2-3 DTOs
  - Atualização de mappers

- **Frontend**:
  - Modificação da interface de criação de cotação
  - Modificação da visualização de cotações
  - Atualização dos serviços de API

- **Testes**:
  - Testes unitários para nova entidade
  - Testes de integração para fluxo completo
  - Testes de migração de dados

### Compatibilidade
A migração pode ser feita de forma que mantenha compatibilidade com dados existentes, dividindo o preço total igualmente entre os itens ou usando lógica de negócio apropriada.

### Alternativa Temporária
Se apenas preço por item é necessário AGORA, uma solução mais simples seria:
- Manter o relacionamento N:N atual
- Adicionar uma tabela separada `cotacao_item_preco` com apenas `cotacao_id`, `item_pedido_id`, `preco_unitario`
- Esta é uma solução intermediária menos ideal mas com menor impacto

## Status Atual
✅ Documentado
⏸️ Não implementado (aguardando necessidade de negócio)
