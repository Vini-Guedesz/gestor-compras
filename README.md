# Gestor de Compras (Spring Boot + Vue)

Gestor de Compras is a fullstack platform to manage purchase workflows, quotations, suppliers and approvals in a single product.

## ✨ Features

- 🧾 Purchase workflow management:
  - Draft creation and conversion into purchase orders
  - Order lifecycle tracking
- 💸 Quotation management:
  - Item-based quotations with quantity, unit price and notes
- 🏢 Supplier management:
  - Supplier records, contacts and history
- 📎 PDF attachments with deduplicated storage
- 📊 Dashboard-oriented UI for fast operational analysis

## 🛠️ Tech Stack

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

## 📦 Installation

```bash
# Clone repository
git clone https://github.com/Vini-Guedesz/gestor-compras.git

# Enter project folder
cd gestor-compras
```

### Run database (Docker)

```bash
docker-compose up -d db
```

### Run backend

```bash
cd gestor-compras-backend
./mvnw spring-boot:run
```

### Run frontend

```bash
cd gestor-compras-frontend/vue-project
npm install
npm run dev
```

Main URLs:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8081`
- Swagger: `http://localhost:8081/swagger-ui.html`

## 🧩 Project Structure

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

## 📌 Roadmap

- [ ] Increase frontend automated test coverage
- [ ] Add deployment pipeline with environment promotion
- [ ] Improve audit trail visualization in UI
- [ ] Add advanced filters and reporting exports
