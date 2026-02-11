# Diagrama de Classes - Gestor de Compras

```mermaid
classDiagram
    %% Relacionamentos de Rascunho
    RascunhoController --> RascunhoService
    RascunhoService --> RascunhoRepository
    RascunhoService --> HistoricoRascunhoService
    Rascunho "1" *-- "*" ItemRascunho : possui
    Rascunho "1" --> "1" User : criador
    
    %% Relacionamentos de Pedido
    SolicitacaoDePedidoController --> SolicitacaoDePedidoService
    SolicitacaoDePedidoService --> SolicitacaoDePedidoRepository
    SolicitacaoDePedidoService --> HistoricoPedidoService
    SolicitacaoDePedido "1" *-- "*" ItemPedido : possui
    
    %% Relacionamentos de Cotação
    CotacaoController --> CotacaoService
    CotacaoService --> CotacaoRepository
    CotacaoService --> PdfDeduplicationService
    Cotacao "*" --> "1" SolicitacaoDePedido : vinculada_a
    Cotacao "*" --> "0..1" FornecedorDeProduto : fornece_produto
    Cotacao "*" --> "0..1" FornecedorDeServico : fornece_servico
    Cotacao "1" *-- "*" AnexoCotacao : possui
    AnexoCotacao "*" --> "1" PdfStorage : referencia
    
    %% Classes de Modelo
    class Rascunho {
        +Long id
        +StatusRascunho status
        +String objetivo
        +List~ItemRascunho~ itens
        +User criador
        +converterParaPedido()
    }
    
    class SolicitacaoDePedido {
        +Long id
        +StatusPedido status
        +LocalDateTime dataCriacao
        +List~ItemPedido~ itens
    }
    
    class Cotacao {
        +Long id
        +BigDecimal preco
        +Integer prazoEmDiasUteis
        +List~AnexoCotacao~ anexos
        +FornecedorDeProduto fornecedorProduto
        +FornecedorDeServico fornecedorServico
    }
    
    class PdfStorage {
        +Long id
        +String hashSha256
        +byte[] conteudo
        +Integer contadorReferencias
    }
    
    class FornecedorDeProduto {
        +Integer id
        +String nome
        +String cnpj
        +String inscricaoEstadual
    }

    class FornecedorDeServico {
        +Integer id
        +String nome
        +String cnpj
        +String inscricaoMunicipal
    }

    %% Serviços
    class RascunhoService {
        +createRascunho(dto)
        +adicionarItem(id, itemDto)
        +converterRascunhoParaPedido(id)
        +devolverParaEdicao(id, motivo)
    }

    class CotacaoService {
        +createCotacao(dto)
        +uploadAnexos(id, files)
        +editarCotacao(dto)
    }

    class PdfDeduplicationService {
        +storePdf(bytes)
        +removeReference(storageId)
        +generateDeduplicationReport()
    }
```

## Descrição da Arquitetura

- **Controller Layer**: Endpoints REST que validam DTOs e coordenam chamadas aos serviços.
- **Service Layer**: Contém a lógica de negócio, orquestração de transações e regras de segurança.
- **Repository Layer**: Interface com o banco de dados via Spring Data JPA.
- **Model Layer**: Entidades JPA que representam o domínio do sistema.
- **DTOs**: Objetos de transferência para comunicação entre Frontend e Backend.
