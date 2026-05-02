# Gestor de Compras (Spring Boot + Vue)

Gestor de Compras e uma plataforma fullstack para gerenciar fluxo de compras, cotacoes, fornecedores e aprovacoes em um unico produto.

## Funcionalidades

- Gestao do fluxo de compras:
  - criacao de rascunho e conversao em pedido
  - acompanhamento do ciclo de vida do pedido
- Gestao de cotacoes:
  - cotacao por item com quantidade, preco unitario e observacoes
- Gestao de fornecedores:
  - cadastro de fornecedores, contatos e historico
- Anexos PDF com armazenamento deduplicado
- Interface orientada a dashboard para analise operacional rapida

## Stack

### Backend

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Flyway

### Frontend

- Vue 3
- Vite
- Pinia
- Vue Router

### Infra

- Docker + Docker Compose
- Nginx
- Prometheus + Grafana

## Instalacao

```bash
# Clonar repositorio
git clone https://github.com/Vini-Guedesz/gestor-compras.git

# Entrar no projeto
cd gestor-compras
```

### Subir banco (Docker)

```bash
docker-compose up -d db
```

### Subir backend

```bash
cd gestor-compras-backend
./mvnw spring-boot:run
```

### Subir frontend

```bash
cd gestor-compras-frontend/vue-project
npm install
npm run dev
```

URLs principais:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8081`
- Swagger: `http://localhost:8081/swagger-ui.html`

## Estrutura do projeto

```text
gestor-compras/
 ├── docs/
 ├── gestor-compras-backend/
 ├── gestor-compras-frontend/
 │    └── vue-project/
 ├── nginx/
 ├── monitoring/
 ├── docker-compose.yml
 ├── docker-compose.prod.yml
 └── docker-compose-monitoring.yml
```
