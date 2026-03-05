# Gestor de Compras

Versao atual: `3.2.2`

## Visao geral

O Gestor de Compras centraliza o fluxo de rascunhos, pedidos, cotacoes, fornecedores e aprovacoes em uma arquitetura separada entre backend Spring Boot e frontend Vue 3.

Principais capacidades:

- Criacao e acompanhamento de rascunhos e pedidos de compra.
- Cotacao por item, com precos unitarios, quantidades e observacoes por item.
- Gestao de fornecedores de produto e servico.
- Contatos principais com rotulos e contatos adicionais por fornecedor.
- Historico de alteracoes em pedidos, rascunhos e cotacoes.
- Upload de anexos PDF com armazenamento deduplicado.
- Interface web com dashboard, filtros, modais e visualizacao detalhada.

## Destaques da release 3.2.2

- Correcao do carregamento de PDF no viewer em ambiente de producao.
- Download de anexos PDF centralizado no cliente `api` com resposta `blob`.
- Ajuste de proxy no frontend para rotas `/api`, `/auth` e `/relatorios`.
- Tela "Sobre" atualizada com a versao `3.2.2`.

## Estrutura do repositorio

```text
gestor-compras/
|-- docs/
|   |-- README.md
|   |-- class-diagram.md
|   `-- database-schema.md
|-- gestor-compras-backend/
|   |-- src/main/java/
|   |-- src/main/resources/
|   `-- pom.xml
|-- gestor-compras-frontend/
|   `-- vue-project/
|       |-- src/
|       |-- public/
|       `-- package.json
|-- monitoring/
|-- nginx/
|-- docker-compose.yml
|-- docker-compose.prod.yml
`-- docker-compose-monitoring.yml
```

## Stack

### Backend

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Flyway
- Maven

### Frontend

- Node.js 20+
- Vue 3
- Vite
- Pinia
- Vue Router

## Configuracao de ambiente

O repositorio possui um arquivo `.env` na raiz com configuracoes de desenvolvimento para banco e backend. Ajuste os valores conforme seu ambiente antes de subir a stack.

Exemplo atual:

```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=admin
POSTGRES_DB=gestorcomprasbackend
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/gestorcomprasbackend
SERVER_PORT=8081
SPRING_PROFILES_ACTIVE=dev
JWT_SECRET=chave_secreta_padrao_desenvolvimento_gestor_compras_2026
```

## Como executar

### 1. Banco de dados

Via Docker:

```bash
docker-compose up -d db
```

Ou use uma instancia local de PostgreSQL e crie o banco configurado no `.env`.

### 2. Backend

No diretorio `gestor-compras-backend`:

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend padrao: `http://localhost:8081`

Swagger:

- `http://localhost:8081/swagger-ui.html`

### 3. Frontend

No diretorio `gestor-compras-frontend/vue-project`:

```bash
npm install
npm run dev
```

Frontend padrao: `http://localhost:5173`

## Validacao da release

Comandos usados na validacao da versao `3.2.2`:

```bash
# Frontend
npx eslint .
npx oxlint . -D correctness --ignore-path .gitignore
npm run build

# Backend
mvn -q test
```

Observacao:

- O frontend hoje nao possui arquivos de teste unitario ativos para o Vitest. `vitest --run` encerra informando que nao ha testes encontrados.

## Primeiro acesso

Usuario padrao de desenvolvimento:

- E-mail: `admin@admin.com`
- Senha: `admin`

## Documentacao tecnica

Os arquivos tecnicos principais estao em [`docs/`](./docs):

- [`docs/README.md`](./docs/README.md): indice da documentacao
- [`docs/class-diagram.md`](./docs/class-diagram.md): relacoes entre camadas e entidades
- [`docs/database-schema.md`](./docs/database-schema.md): visao do esquema de banco

## Monitoramento e infraestrutura

### Monitoramento

```bash
docker-compose -f docker-compose-monitoring.yml up -d
```

- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

### Producao com Nginx

```bash
docker-compose -f docker-compose.prod.yml up -d
```

Arquivos principais:

- [`nginx/nginx.conf`](./nginx/nginx.conf)
- [`nginx/conf.d/default.conf`](./nginx/conf.d/default.conf)

## Portas padrao

| Servico | Porta |
|---|---:|
| Frontend dev | 5173 |
| Frontend preview | 4173 |
| Backend | 8081 |
| PostgreSQL | 5432 |
| Nginx HTTP | 80 |
| Nginx HTTPS | 443 |
| Grafana | 3000 |
| Prometheus | 9090 |

## Branch principal atual

- Branch: `main`

