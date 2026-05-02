# Gestor de Compras

Aplicacao fullstack para centralizar pedidos, cotacoes, fornecedores e aprovacoes.

## Status

- Ativo
- Versao atual: `3.2.2`

## Stack

### Backend

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Flyway
- Maven

### Frontend

- Vue 3
- Vite
- Pinia
- Vue Router

### Infra

- Docker e Docker Compose
- Nginx
- Prometheus e Grafana (monitoramento)

## Funcionalidades

- Gestao de rascunhos e pedidos de compra
- Cotacao por item
- Cadastro e manutencao de fornecedores
- Contatos e historico por fornecedor
- Historico de alteracoes em entidades principais
- Upload de anexos PDF com deduplicacao
- Dashboard com filtros e visualizacao detalhada

## Como executar

### Com Docker

```bash
docker-compose up -d db
```

### Backend (local)

```bash
cd gestor-compras-backend
./mvnw spring-boot:run
```

Windows:

```powershell
cd gestor-compras-backend
.\mvnw.cmd spring-boot:run
```

### Frontend (local)

```bash
cd gestor-compras-frontend/vue-project
npm install
npm run dev
```

## Enderecos

- Frontend dev: `http://localhost:5173`
- Backend: `http://localhost:8081`
- Swagger: `http://localhost:8081/swagger-ui.html`

## Qualidade

### Frontend

```bash
npx eslint .
npx oxlint . -D correctness --ignore-path .gitignore
npm run build
```

### Backend

```bash
mvn -q test
```

## Estrutura

- `gestor-compras-backend/`: API e regras de negocio
- `gestor-compras-frontend/vue-project/`: interface web
- `docs/`: documentacao tecnica
- `docker-compose*.yml`: cenarios de execucao e monitoramento

## Documentacao

- `docs/README.md`
- `docs/class-diagram.md`
- `docs/database-schema.md`

## Roadmap

- Expandir cobertura de testes no frontend
- Automatizar pipeline de deploy
- Evoluir trilha de auditoria por acao

## Autor

Desenvolvido por [Vinicius Guedes](https://github.com/Vini-Guedesz).
