# Class Diagram

```mermaid
classDiagram
    Contato "1" *-- "*" ContatoAdicional : possui
    FornecedorDeProduto "1" --> "1" Contato : usa
    FornecedorDeServico "1" --> "1" Contato : usa
    FornecedorDeProduto "1" --> "1" Endereco : usa
    FornecedorDeServico "1" --> "1" Endereco : usa

    User "1" --> "*" Rascunho : cria
    Rascunho "1" *-- "*" ItemRascunho : possui
    Rascunho "1" *-- "*" CotacaoRascunho : recebe
    Rascunho "1" *-- "*" HistoricoRascunho : registra

    SolicitacaoDePedido "1" *-- "*" ItemPedido : possui
    SolicitacaoDePedido "1" *-- "*" Cotacao : recebe
    SolicitacaoDePedido "1" *-- "*" HistoricoPedido : registra

    Cotacao "1" *-- "*" CotacaoItem : detalha
    Cotacao "1" *-- "*" AnexoCotacao : possui
    Cotacao "1" *-- "*" HistoricoCotacao : registra

    CotacaoRascunho "1" *-- "*" CotacaoRascunhoItem : detalha
    CotacaoRascunho "1" *-- "*" AnexoCotacaoRascunho : possui

    AnexoCotacao "*" --> "1" PdfStorage : referencia
    AnexoCotacaoRascunho "*" --> "1" PdfStorage : referencia

    Cotacao "*" --> "0..1" FornecedorDeProduto : produto
    Cotacao "*" --> "0..1" FornecedorDeServico : servico
    CotacaoRascunho "*" --> "0..1" FornecedorDeProduto : produto
    CotacaoRascunho "*" --> "0..1" FornecedorDeServico : servico

    class Contato {
        +Integer id
        +String telefoneFixo
        +String rotuloTelefoneFixo
        +String celular
        +String rotuloCelular
        +String email
        +String rotuloEmail
    }

    class ContatoAdicional {
        +Integer id
        +String nomeContato
        +TipoContatoAdicional tipoContato
        +String valorContato
        +Integer ordemExibicao
    }

    class Rascunho {
        +Long id
        +String status
        +String objetivo
        +String observacao
        +Long pedidoGeradoId
    }

    class SolicitacaoDePedido {
        +Long id
        +String status
        +String objetivo
        +String observacao
        +LocalDateTime dataCriacao
    }

    class Cotacao {
        +Long id
        +BigDecimal precoLegacy
        +Integer prazoEmDiasUteis
        +LocalDate dataLimite
        +Boolean gastoPrevisto
        +String projeto
        +BigDecimal calcularPrecoTotal()
    }

    class CotacaoItem {
        +Long id
        +BigDecimal precoUnitario
        +Integer quantidade
        +String observacao
        +BigDecimal calcularPrecoTotal()
    }

    class CotacaoRascunho {
        +Long id
        +BigDecimal precoLegacy
        +Integer prazoEmDiasUteis
        +LocalDate dataLimite
        +Boolean gastoPrevisto
        +String projeto
        +BigDecimal getPreco()
    }

    class CotacaoRascunhoItem {
        +Long id
        +BigDecimal precoUnitario
        +Integer quantidade
        +String observacao
        +BigDecimal calcularPrecoTotal()
    }

    class PdfStorage {
        +Long id
        +String hashSha256
        +byte[] conteudo
        +Long tamanhoBytes
        +Integer contadorReferencias
    }

    class HistoricoCotacao {
        +Long id
        +Integer versao
        +String motivoEdicao
        +String editadoPor
        +LocalDateTime dataEdicao
    }
```

## Notes

- `Cotacao` e `CotacaoRascunho` aceitam fornecedor de produto ou servico, nunca os dois ao mesmo tempo.
- O valor total das cotacoes pode vir do preco legado ou ser calculado a partir dos itens.
- O modelo de contato foi ampliado com rotulos nos contatos principais e lista de contatos adicionais.
- Anexos PDF usam armazenamento deduplicado via `PdfStorage`.

