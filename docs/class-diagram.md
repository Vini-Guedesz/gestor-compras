# Diagramas de Classes

## Diagrama de Classes Completo

```mermaid
classDiagram
    %% ==================== USUÁRIOS ====================
    class User {
        <<Entity>>
        -Long id
        -String username
        -String email
        -String password
        -String role
        +getId() Long
        +getUsername() String
        +getEmail() String
    }

    %% ==================== FORNECEDORES ====================
    class Fornecedor {
        <<Abstract>>
        #Integer id
        #String razaoSocial
        #String cnpj
        #Endereco endereco
        #Contato contato
        +getId() Integer
        +getRazaoSocial() String
    }

    class FornecedorDeProduto {
        <<Entity>>
        -String inscricaoEstadual
        +getInscricaoEstadual() String
    }

    class FornecedorDeServico {
        <<Entity>>
        -String inscricaoMunicipal
        +getInscricaoMunicipal() String
    }

    class Endereco {
        <<Entity>>
        -Long id
        -String cep
        -String logradouro
        -String numero
        -String complemento
        -String bairro
        -String cidade
        -String estado
    }

    class Contato {
        <<Entity>>
        -Long id
        -String nomeContato
        -String telefoneFixo
        -String celular
        -String email
    }

    %% ==================== RASCUNHOS ====================
    class Rascunho {
        <<Entity>>
        -Long id
        -User criador
        -String observacao
        -LocalDateTime dataCriacao
        -LocalDateTime dataModificacao
        -StatusRascunho status
        -Long pedidoGeradoId
        -Integer proximoNumeroItem
        -List~ItemRascunho~ itens
        -List~NumeroItemDisponivel~ numerosDisponiveis
        +adicionarItem() void
        +removerItem() void
        +obterProximoNumero() Integer
    }

    class ItemRascunho {
        <<Entity>>
        -Long id
        -Integer numeroItem
        -String nome
        -Integer quantidade
        -String descricao
        -String observacao
        -Rascunho rascunho
    }

    class NumeroItemDisponivel {
        <<Entity>>
        -Long id
        -Rascunho rascunho
        -Integer numeroItem
    }

    class StatusRascunho {
        <<Enumeration>>
        ATIVO
        EM_COTACAO
        FINALIZADO
    }

    class CotacaoRascunho {
        <<Entity>>
        -Long id
        -Rascunho rascunho
        -FornecedorDeProduto fornecedorProduto
        -FornecedorDeServico fornecedorServico
        -BigDecimal preco
        -Integer prazoEmDiasUteis
        -LocalDate dataLimite
        -List~AnexoCotacaoRascunho~ anexos
        -LocalDateTime dataCriacao
    }

    class AnexoCotacaoRascunho {
        <<Entity>>
        -Long id
        -CotacaoRascunho cotacaoRascunho
        -Integer ordem
        -byte[] conteudo
        -String nomeArquivo
        -String hashSha256
        +getHashSha256() String
    }

    %% ==================== PEDIDOS ====================
    class SolicitacaoDePedido {
        <<Entity>>
        -Long id
        -List~ItemPedido~ itens
        -Set~Cotacao~ cotacoes
        -StatusPedido status
        -String observacao
        -LocalDateTime dataCriacao
        -Long version
        +calcularTotal() BigDecimal
        +adicionarCotacao() void
    }

    class ItemPedido {
        <<Entity>>
        -Long id
        -String nome
        -Integer quantidade
        -String descricao
        -String observacao
        -SolicitacaoDePedido solicitacao
    }

    class StatusPedido {
        <<Enumeration>>
        PENDENTE
        EM_ANALISE
        EM_ANDAMENTO
        APROVADO
        CANCELADO
    }

    %% ==================== COTAÇÕES ====================
    class Cotacao {
        <<Entity>>
        -Long id
        -SolicitacaoDePedido solicitacao
        -FornecedorDeProduto fornecedorProduto
        -FornecedorDeServico fornecedorServico
        -List~CotacaoItem~ itens
        -BigDecimal preco
        -Integer prazoEmDiasUteis
        -LocalDate dataLimite
        -List~AnexoCotacao~ anexos
        -LocalDateTime dataCriacao
        -Long version
        -Boolean foiEditada
        -Integer numeroVersao
        -LocalDateTime dataUltimaEdicao
        -String motivoUltimaEdicao
        -String editadoPor
        +calcularPrecoTotal() BigDecimal
        +getFornecedorId() Integer
    }

    class CotacaoItem {
        <<Entity>>
        -Long id
        -Cotacao cotacao
        -ItemPedido itemPedido
        -BigDecimal precoUnitario
        -Integer quantidade
        -BigDecimal subtotal
        +calcularSubtotal() BigDecimal
    }

    class AnexoCotacao {
        <<Entity>>
        -Long id
        -Cotacao cotacao
        -Integer ordem
        -byte[] conteudo
        -String nomeArquivo
        -String hashSha256
        +getHashSha256() String
        +setHashSha256(String) void
    }

    %% ==================== HISTÓRICO ====================
    class HistoricoPedido {
        <<Entity>>
        -Long id
        -SolicitacaoDePedido solicitacao
        -User usuario
        -LocalDateTime dataModificacao
        -String tipoModificacao
        -String campoModificado
        -String valorAnterior
        -String valorNovo
        -String observacao
    }

    class HistoricoRascunho {
        <<Entity>>
        -Long id
        -Rascunho rascunho
        -User usuario
        -LocalDateTime dataModificacao
        -String tipoAcao
        -String descricao
        -Integer numeroItem
        -String nomeItem
        -String detalhes
    }

    class HistoricoCotacao {
        <<Entity>>
        -Long id
        -Long cotacaoId
        -Integer versao
        -BigDecimal precoAnterior
        -Integer prazoEmDiasUteisAnterior
        -LocalDate dataLimiteAnterior
        -BigDecimal precoNovo
        -Integer prazoEmDiasUteisNovo
        -LocalDate dataLimiteNovo
        -String motivoEdicao
        -String editadoPor
        -LocalDateTime dataEdicao
        -String hashAnexoPdfAnterior
        -String hashAnexoPdfNovo
    }

    %% ==================== SERVICES ====================
    class PdfHashService {
        <<Service>>
        +calculateSHA256(byte[]) String
        +areIdentical(byte[], byte[]) boolean
    }

    class PdfDeduplicationService {
        <<Service>>
        -PdfHashService pdfHashService
        -AnexoCotacaoRepository repository
        +createOrReuseCotacaoAnexo(Cotacao, byte[], Integer, String) AnexoCotacao
        +createOrReuseRascunhoAnexo(CotacaoRascunho, byte[], Integer, String) AnexoCotacaoRascunho
        +convertRascunhoAnexoWithDeduplication(Cotacao, AnexoCotacaoRascunho) AnexoCotacao
    }

    class CotacaoService {
        <<Service>>
        -CotacaoRepository repository
        -PdfDeduplicationService pdfDeduplicationService
        -HistoricoCotacaoService historicoService
        +criar(CotacaoCreateDTO) CotacaoDTO
        +atualizar(Long, CotacaoUpdateDTO) CotacaoDTO
        +uploadAnexos(Long, MultipartFile[]) CotacaoDTO
        +vincularItens(Long, List~Long~) CotacaoDTO
    }

    class RascunhoService {
        <<Service>>
        -RascunhoRepository repository
        -PdfDeduplicationService pdfDeduplicationService
        +converterRascunhoParaPedido(Long, ConverterDTO) SolicitacaoDePedidoDTO
        +adicionarItem(Long, ItemRascunhoCreateDTO) ItemRascunhoDTO
        +removerItem(Long, Long) void
    }

    %% ==================== RELACIONAMENTOS ====================

    %% Herança
    Fornecedor <|-- FornecedorDeProduto
    Fornecedor <|-- FornecedorDeServico

    %% Composição Fornecedor
    Fornecedor "1" *-- "1" Endereco : possui
    Fornecedor "1" *-- "1" Contato : possui

    %% Rascunho
    User "1" --> "0..*" Rascunho : cria
    Rascunho "1" *-- "0..*" ItemRascunho : contém
    Rascunho "1" *-- "0..*" NumeroItemDisponivel : pool
    Rascunho "1" --> "0..*" CotacaoRascunho : cotacoes
    Rascunho --> StatusRascunho : status

    %% Cotação Rascunho
    CotacaoRascunho "0..*" --> "0..1" FornecedorDeProduto : fornecedor
    CotacaoRascunho "0..*" --> "0..1" FornecedorDeServico : fornecedor
    CotacaoRascunho "1" *-- "0..*" AnexoCotacaoRascunho : anexos

    %% Pedido
    SolicitacaoDePedido "1" *-- "1..*" ItemPedido : itens
    SolicitacaoDePedido "1" --> "0..*" Cotacao : cotacoes
    SolicitacaoDePedido --> StatusPedido : status

    %% Cotação
    Cotacao "0..*" --> "0..1" FornecedorDeProduto : fornecedor
    Cotacao "0..*" --> "0..1" FornecedorDeServico : fornecedor
    Cotacao "1" *-- "1..*" CotacaoItem : itens
    Cotacao "1" *-- "0..*" AnexoCotacao : anexos
    CotacaoItem "0..*" --> "1" ItemPedido : referencia

    %% Histórico
    HistoricoPedido "0..*" --> "1" SolicitacaoDePedido : pedido
    HistoricoPedido "0..*" --> "1" User : usuario
    HistoricoRascunho "0..*" --> "1" Rascunho : rascunho
    HistoricoRascunho "0..*" --> "1" User : usuario

    %% Services
    PdfDeduplicationService --> PdfHashService : usa
    PdfDeduplicationService --> AnexoCotacao : cria/reutiliza
    PdfDeduplicationService --> AnexoCotacaoRascunho : cria/reutiliza
    CotacaoService --> PdfDeduplicationService : usa
    RascunhoService --> PdfDeduplicationService : usa
```

## Diagrama de Entidades do Domínio

```mermaid
classDiagram
    class Fornecedor {
        <<Abstract>>
        +Integer id
        +String razaoSocial
        +String cnpj
    }

    class FornecedorDeProduto {
        +String inscricaoEstadual
    }

    class FornecedorDeServico {
        +String inscricaoMunicipal
    }

    class SolicitacaoDePedido {
        +Long id
        +StatusPedido status
        +String observacao
        +LocalDateTime dataCriacao
    }

    class ItemPedido {
        +Long id
        +String nome
        +Integer quantidade
        +String descricao
    }

    class Cotacao {
        +Long id
        +BigDecimal preco
        +Integer prazoEmDiasUteis
        +LocalDate dataLimite
        +Boolean foiEditada
        +Integer numeroVersao
    }

    class CotacaoItem {
        +Long id
        +BigDecimal precoUnitario
        +Integer quantidade
        +BigDecimal subtotal
    }

    Fornecedor <|-- FornecedorDeProduto
    Fornecedor <|-- FornecedorDeServico
    SolicitacaoDePedido "1" *-- "many" ItemPedido
    SolicitacaoDePedido "1" --> "many" Cotacao
    Cotacao "1" *-- "many" CotacaoItem
    Cotacao "many" --> "0..1" FornecedorDeProduto
    Cotacao "many" --> "0..1" FornecedorDeServico
    CotacaoItem "many" --> "1" ItemPedido
```

## Diagrama de Serviços e Camadas

```mermaid
classDiagram
    %% Controllers
    class CotacaoController {
        <<RestController>>
        -CotacaoService service
        +criar(CotacaoCreateDTO) ResponseEntity
        +atualizar(Long, CotacaoUpdateDTO) ResponseEntity
        +uploadAnexos(Long, MultipartFile[]) ResponseEntity
        +listar() ResponseEntity
        +buscarPorId(Long) ResponseEntity
    }

    class RascunhoController {
        <<RestController>>
        -RascunhoService service
        +criar(RascunhoCreateDTO) ResponseEntity
        +converterParaPedido(Long, ConverterDTO) ResponseEntity
        +adicionarItem(Long, ItemRascunhoCreateDTO) ResponseEntity
    }

    %% Services
    class CotacaoService {
        <<Service>>
        -CotacaoRepository repository
        -CotacaoMapper mapper
        -PdfDeduplicationService pdfDeduplicationService
        -HistoricoCotacaoService historicoService
        +criar(CotacaoCreateDTO) CotacaoDTO
        +atualizar(Long, CotacaoUpdateDTO) CotacaoDTO
        +uploadAnexos(Long, MultipartFile[]) CotacaoDTO
        -validarFornecedor() void
        -validarItens() void
        -calcularPrecoTotal() void
    }

    class RascunhoService {
        <<Service>>
        -RascunhoRepository repository
        -RascunhoMapper mapper
        -PdfDeduplicationService pdfDeduplicationService
        +converterRascunhoParaPedido(Long, ConverterDTO) SolicitacaoDePedidoDTO
        +adicionarItem(Long, ItemRascunhoCreateDTO) ItemRascunhoDTO
        -obterProximoNumero() Integer
        -migrarCotacoes() void
    }

    class PdfDeduplicationService {
        <<Service>>
        -PdfHashService pdfHashService
        -AnexoCotacaoRepository anexoRepository
        +createOrReuseCotacaoAnexo() AnexoCotacao
        +createOrReuseRascunhoAnexo() AnexoCotacaoRascunho
        -verificarDuplicacao() Optional~AnexoCotacao~
    }

    class PdfHashService {
        <<Service>>
        +calculateSHA256(byte[]) String
        +areIdentical(byte[], byte[]) boolean
    }

    %% Repositories
    class CotacaoRepository {
        <<Repository>>
        +findAllWithRelationships() List~Cotacao~
        +findByIdWithItens(Long) Optional~Cotacao~
        +findByIdWithCotacoes(Long) Optional~Cotacao~
    }

    class AnexoCotacaoRepository {
        <<Repository>>
        +findFirstByHashSha256(String) Optional~AnexoCotacao~
        +findAllByHashSha256(String) List~AnexoCotacao~
        +existsByHashSha256(String) boolean
    }

    %% Mappers
    class CotacaoMapper {
        <<Component>>
        -CotacaoItemMapper cotacaoItemMapper
        +toDTO(Cotacao) CotacaoDTO
        +toEntity(CotacaoCreateDTO) Cotacao
    }

    %% Relacionamentos
    CotacaoController --> CotacaoService : usa
    RascunhoController --> RascunhoService : usa

    CotacaoService --> CotacaoRepository : usa
    CotacaoService --> CotacaoMapper : usa
    CotacaoService --> PdfDeduplicationService : usa

    RascunhoService --> PdfDeduplicationService : usa

    PdfDeduplicationService --> PdfHashService : usa
    PdfDeduplicationService --> AnexoCotacaoRepository : usa
```

## Diagrama de DTOs

```mermaid
classDiagram
    %% DTOs de Entrada (Request)
    class CotacaoCreateDTO {
        <<Record>>
        +Integer fornecedorId
        +String tipoFornecedor
        +Long solicitacaoDePedidoId
        +List~Long~ itensPedidoIds
        +BigDecimal preco
        +List~CotacaoItemCreateDTO~ itens
        +Integer prazoEmDiasUteis
        +LocalDate dataLimite
        +byte[] anexoPdf @Deprecated
        +usaNovoFormato() boolean
        +validar() void
    }

    class CotacaoItemCreateDTO {
        <<Record>>
        +Long itemPedidoId
        +BigDecimal precoUnitario
        +Integer quantidade
    }

    class CotacaoUpdateDTO {
        <<Record>>
        +BigDecimal preco
        +Integer prazoEmDiasUteis
        +LocalDate dataLimite
        +byte[] anexoPdf @Deprecated
        +String caminhoAnexo @Deprecated
    }

    %% DTOs de Saída (Response)
    class CotacaoDTO {
        <<Record>>
        +Long id
        +Integer fornecedorId
        +String tipoFornecedor
        +String nomeFornecedor
        +Long solicitacaoDePedidoId
        +List~Long~ itensPedidoIds
        +List~CotacaoItemDTO~ itens
        +BigDecimal preco
        +Integer prazoEmDiasUteis
        +LocalDate dataLimite
        +String caminhoAnexo @Deprecated
        +Boolean temAnexo
        +Integer quantidadeAnexos
        +Boolean foiEditada
        +Integer numeroVersao
    }

    class CotacaoItemDTO {
        <<Record>>
        +Long id
        +Long cotacaoId
        +Long itemPedidoId
        +String nomeItem
        +BigDecimal precoUnitario
        +Integer quantidade
        +BigDecimal subtotal
    }

    class HistoricoCotacaoDTO {
        <<Record>>
        +Long id
        +Long cotacaoId
        +Integer versao
        +BigDecimal precoAnterior
        +Integer prazoEmDiasUteisAnterior
        +LocalDate dataLimiteAnterior
        +BigDecimal precoNovo
        +Integer prazoEmDiasUteisNovo
        +LocalDate dataLimiteNovo
        +String motivoEdicao
        +String editadoPor
        +LocalDateTime dataEdicao
        +String caminhoAnexoAnterior @Deprecated
        +String caminhoAnexoNovo @Deprecated
    }

    %% Relacionamentos
    CotacaoCreateDTO "1" *-- "many" CotacaoItemCreateDTO : itens
    CotacaoDTO "1" *-- "many" CotacaoItemDTO : itens
```

## Diagrama de Estados - Rascunho

```mermaid
stateDiagram-v2
    [*] --> ATIVO : Criar rascunho
    ATIVO --> ATIVO : Adicionar/Remover itens
    ATIVO --> EM_COTACAO : Finalizar rascunho
    EM_COTACAO --> EM_COTACAO : Adicionar cotações
    EM_COTACAO --> FINALIZADO : Converter para pedido
    FINALIZADO --> [*]

    note right of ATIVO
        Usuário pode editar livremente
        Adicionar/remover itens
        Modificar observações
    end note

    note right of EM_COTACAO
        Rascunho finalizado
        Recebendo cotações
        Não pode mais editar itens
    end note

    note right of FINALIZADO
        Convertido em pedido
        pedidoGeradoId preenchido
        Não pode mais ser editado
    end note
```

## Diagrama de Estados - Pedido

```mermaid
stateDiagram-v2
    [*] --> PENDENTE : Criar pedido
    PENDENTE --> EM_ANALISE : Iniciar análise
    EM_ANALISE --> EM_ANDAMENTO : Aprovar compra
    EM_ANALISE --> CANCELADO : Reprovar
    PENDENTE --> CANCELADO : Cancelar
    EM_ANDAMENTO --> APROVADO : Concluir
    EM_ANDAMENTO --> CANCELADO : Cancelar
    CANCELADO --> [*]
    APROVADO --> [*]

    note right of PENDENTE
        Aguardando análise
        Pode adicionar cotações
    end note

    note right of EM_ANALISE
        Em avaliação
        Comparando cotações
    end note

    note right of EM_ANDAMENTO
        Compra aprovada
        Em execução
    end note
```

## Diagrama de Sequência - Upload de PDF com Deduplificação

```mermaid
sequenceDiagram
    actor User
    participant Controller
    participant Service
    participant DedupService as PdfDeduplicationService
    participant HashService as PdfHashService
    participant Repository
    participant DB

    User->>Controller: POST /api/cotacoes/{id}/anexos
    Controller->>Service: uploadAnexos(id, files)

    loop Para cada arquivo
        Service->>Service: validarMimeType()
        Service->>Service: validarTamanho(10MB)

        Service->>DedupService: createOrReuseCotacaoAnexo(cotacao, bytes)
        DedupService->>HashService: calculateSHA256(bytes)
        HashService-->>DedupService: hash (64 chars)

        DedupService->>Repository: findFirstByHashSha256(hash)
        Repository->>DB: SELECT * FROM anexo_cotacao WHERE hash_sha256=?
        DB-->>Repository: anexo existente (ou null)

        alt PDF já existe
            DedupService->>DedupService: LOG: "PDF deduplificado! Economizando X bytes"
            DedupService->>DedupService: reusar conteúdo existente
        else PDF novo
            DedupService->>DedupService: LOG: "Novo PDF único"
            DedupService->>DedupService: criar novo anexo
        end

        DedupService-->>Service: AnexoCotacao
        Service->>Service: cotacao.anexos.add(anexo)
    end

    Service->>Repository: save(cotacao)
    Repository->>DB: INSERT INTO anexo_cotacao
    DB-->>Repository: OK

    Service-->>Controller: CotacaoDTO
    Controller-->>User: 201 Created
```

---

**Próximo:** [Banco de Dados](./database-schema.md)
