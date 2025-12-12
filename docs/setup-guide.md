# Guia de Configuração e Instalação

Este guia fornece instruções detalhadas para configurar o ambiente de desenvolvimento e executar o projeto Gestor de Compras localmente.

## Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas em seu sistema:

### Backend
- **Java Development Kit (JDK) 21**: [Download JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)
- **Maven**: (Opcional, o projeto inclui o wrapper `mvnw`)
- **Docker & Docker Compose**: Para executar o banco de dados PostgreSQL. [Download Docker Desktop](https://www.docker.com/products/docker-desktop/)

### Frontend
- **Node.js (v18 ou superior)**: [Download Node.js](https://nodejs.org/)
- **npm**: Geralmente instalado junto com o Node.js.

### Ferramentas Gerais
- **Git**: [Download Git](https://git-scm.com/)
- **IDE**: Recomendamos [IntelliJ IDEA](https://www.jetbrains.com/idea/) (Backend) e [VS Code](https://code.visualstudio.com/) (Frontend).

## Passo a Passo

### 1. Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/gestor-compras.git
cd gestor-compras
```

### 2. Configurar o Banco de Dados

O projeto utiliza PostgreSQL. A maneira mais fácil de rodar é usando Docker Compose.

Na raiz do projeto (`gestor-compras/`), execute:

```bash
docker-compose up -d
```

Isso iniciará um container PostgreSQL na porta `5432` com o banco `gestorcomprasbackend` criado.

### 3. Configurar e Executar o Backend

1.  Navegue até a pasta do backend:
    ```bash
    cd gestor-compras-backend
    ```

2.  Configure as variáveis de ambiente (Opcional para Dev):
    - Copie o arquivo de exemplo: `cp .env.example .env`
    - Para desenvolvimento local padrão, as configurações em `src/main/resources/application-dev.properties` já apontam para o banco do Docker.

3.  Execute a aplicação:
    
    **Via Maven Wrapper (Linux/Mac):**
    ```bash
    ./mvnw spring-boot:run
    ```

    **Via Maven Wrapper (Windows):**
    ```cmd
    mvnw.cmd spring-boot:run
    ```

4.  O backend estará disponível em `http://localhost:8081`.
    - Documentação Swagger: `http://localhost:8081/swagger-ui/index.html`

### 4. Configurar e Executar o Frontend

1.  Navegue até a pasta do frontend:
    ```bash
    cd ../gestor-compras-frontend/vue-project
    ```

2.  Instale as dependências:
    ```bash
    npm install
    ```

3.  Inicie o servidor de desenvolvimento:
    ```bash
    npm run dev
    ```

4.  O frontend estará disponível em `http://localhost:5173`.

## Solução de Problemas Comuns

### Porta Ocupada
Se receber erro de porta em uso (`Address already in use`):
- Backend: Verifique se a porta 8081 está livre ou altere `server.port` no `application.properties`.
- Frontend: O Vite tentará automaticamente a próxima porta disponível (5174, etc).

### Erro de Conexão com Banco de Dados
- Verifique se o container Docker está rodando: `docker ps`.
- Verifique as credenciais no `application.properties` ou `.env`.

### Erros de Permissão (Linux/Mac)
Se o `mvnw` não executar, dê permissão de execução:
```bash
chmod +x mvnw
```

## Estrutura do Projeto para IDEs

### IntelliJ IDEA
- Abra o diretório `gestor-compras-backend` como um projeto.
- O IntelliJ deve detectar automaticamente o `pom.xml` e configurar o projeto Maven.

### VS Code
- Abra o diretório raiz `gestor-compras` ou `gestor-compras-frontend/vue-project`.
- Instale as extensões recomendadas para Vue.js (Vetur ou Volar) e Java.
