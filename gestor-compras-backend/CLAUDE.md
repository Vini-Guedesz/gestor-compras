# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot backend application for a procurement management system (Gestor de Compras). The application manages suppliers, purchase orders, quotations, and related business processes.

**Tech Stack:**
- Java 21
- Spring Boot 3.3.1
- PostgreSQL (primary database)
- H2 (for testing)
- Flyway (database migrations)
- JWT Authentication (Spring Security)
- MapStruct (entity-DTO mapping)
- JasperReports (PDF report generation)
- Lombok (boilerplate reduction)

## Build & Run Commands

**Build the project:**
```bash
./mvnw clean compile
```

**Run the application:**
```bash
./mvnw spring-boot:run
```

**Run with specific profile:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Package the application:**
```bash
./mvnw clean package
```

**Run Flyway migrations:**
```bash
./mvnw flyway:migrate
```

**Database credentials (dev profile):**
- URL: `jdbc:postgresql://localhost:5432/gestorcomprasbackend`
- Username: `postgres`
- Password: `admin`
- Port: `8081`

## Architecture

### Layered Architecture

The application follows a standard layered architecture:

1. **Controller Layer** (`controller/`): REST endpoints
2. **Service Layer** (`service/`): Business logic
3. **Repository Layer** (`repository/`): Data access (Spring Data JPA)
4. **Model Layer** (`model/`): JPA entities
5. **DTO Layer** (`dto/`): Data transfer objects for API contracts
6. **Mapper Layer** (`mapper/`): MapStruct interfaces for entity-DTO conversions
7. **Security Layer** (`security/`): JWT authentication and authorization

### Key Domain Concepts

**Fornecedor (Supplier) Hierarchy:**
- `Fornecedor` is an abstract `@MappedSuperclass` base class
- `FornecedorDeProduto` extends Fornecedor (Product Suppliers)
- `FornecedorDeServico` extends Fornecedor (Service Suppliers)
- Both supplier types have associated `Endereco` (address) and `Contato` (contact) entities with cascade operations

**Pedido (Order) System:**
- `SolicitacaoDePedido`: Purchase request entity containing multiple items
- `ItemPedido`: Individual items within a purchase request
- `StatusPedido`: Enum for order status tracking
- `Cotacao`: Quotation entity for price comparisons

**User Management:**
- `User` entity with role-based access control
- `UserRole` enum: USER, ADMIN
- JWT-based stateless authentication

### Security Configuration

Located in `security/SecurityConfig.java`:

**Public endpoints** (no authentication required):
- `/auth/login`
- `/v3/api-docs/**`, `/swagger-ui/**` (Swagger documentation)
- `/api/itens-pedido/**`, `/api/solicitacoes-pedido/**`, `/api/cotacoes/**`
- `/relatorios/**` (reports)
- `/api/fornecedores-de-produto/**`, `/api/fornecedores-de-servico/**`
- `POST /api/users` (user registration)

**Admin-only endpoints:**
- `/api/users/**` (except POST for registration)
- `/api/enderecos/**`
- `/api/contatos/**`

**Authentication:**
- JWT tokens with secret configured in `application-dev.properties`
- `JwtFilter` intercepts requests and validates tokens
- `JwtService` handles token generation and validation
- CORS configured for `http://localhost:5173` (frontend)

### Database Migrations

Flyway migrations are located in `src/main/resources/db/migration/`:
- `V1`: Initial schema (users, contato, endereco)
- `V2`: Fornecedores tables
- `V3`: Pedidos tables
- `V4`: Cotacao table
- `V5`: Test data

Migration naming: `V{number}__{description}.sql`

Always use `spring.jpa.hibernate.ddl-auto=none` and manage schema with Flyway.

### MapStruct Patterns

All mappers are interfaces in the `mapper/` package:
- Annotated with `@Mapper(componentModel = "spring")`
- Use `@Mapping(target = "id", ignore = true)` for create/update operations
- Nested mappers referenced via `uses = {OtherMapper.class}`
- Update methods use `@MappingTarget` to modify existing entities

Example pattern:
```java
@Mapper(componentModel = "spring", uses = {EnderecoMapper.class, ContatoMapper.class})
public interface FornecedorMapper {
    EntityDTO toDTO(Entity entity);

    @Mapping(target = "id", ignore = true)
    Entity toEntity(EntityCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(EntityUpdateDTO dto, @MappingTarget Entity entity);
}
```

### Report Generation

JasperReports service (`service/jasperreport/JasperReportService.java`):
- Report templates stored in `src/main/resources/relatorios/` as `.jrxml` files
- Compiles JRXML at runtime using `JasperCompileManager`
- Uses `JRBeanCollectionDataSource` to wrap entity collections
- Returns PDF as byte arrays

### Validation

Custom validators in `validation/` package:
- `@CEP` with `CEPValidator`: Brazilian postal code validation
- `@Celular` with `CelularValidator`: Mobile phone validation
- `@TelefoneFixo` with `TelefoneFixoValidator`: Landline phone validation

Use these annotations on DTO fields for automatic validation.

### Exception Handling

`RestExceptionHandler` provides centralized exception handling:
- Returns structured error responses
- `DataIntegrityConflictException` for business logic violations
- Custom handlers for `CustomAccessDeniedHandler` and `CustomAuthenticationEntryPoint`

## API Documentation

Swagger UI available at: `http://localhost:8081/swagger-ui.html`

Configuration in `config/SpringDocConfig.java`

## Development Notes

- The project uses Maven Wrapper (`mvnw`), no global Maven installation required
- Lombok requires annotation processing enabled in IDE
- MapStruct generates implementation classes during compilation
- Spring DevTools enabled for hot reload
- Active profile set to `dev` in `application.properties`
