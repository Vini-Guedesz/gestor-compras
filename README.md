# 🛒 Gestor de Compras

## 📄 Resumo do Projeto

O **Gestor de Compras** é uma solução completa para gerenciamento de processos de aquisição, cotações, fornecedores e pedidos. Desenvolvido com uma arquitetura moderna separada em Backend (API REST) e Frontend (SPA).

O sistema permite:
- **Gestão de Pedidos**: Criação, edição e acompanhamento de status.
- **Cotações Inteligentes**: Comparativo de preços, upload de propostas e validação de itens.
- **Fornecedores**: Cadastro completo (PF/PJ) e histórico de fornecimento.
- **Relatórios**: Dashboards executivos e exportação em PDF.
- **Auditoria**: Rastreamento completo de alterações (quem, quando, o quê).

---

## 📦 Dependências e Requisitos

| Tecnologia | Versão Mínima | Uso |
|------------|---------------|-----|
| **Java** | 21 (JDK) | Backend Spring Boot |
| **Node.js** | 20.x (LTS) | Frontend Vue.js |
| **PostgreSQL**| 15+ | Banco de Dados Principal |
| **Docker** | 20.x | (Opcional) Containerização do Banco |

---

## 🛠️ Instalação das Ferramentas

Antes de rodar o projeto, você precisa preparar seu ambiente. Siga as instruções para seu sistema operacional:

### 🪟 Windows

1.  **Java JDK 21**:
    *   Baixe e instale o [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21) ou [OpenJDK](https://adoptium.net/).
    *   *Verifique:* Abra o CMD e digite `java -version`.

2.  **Node.js**:
    *   Baixe o instalador LTS em [nodejs.org](https://nodejs.org/).
    *   *Verifique:* `node -v` e `npm -v`.

3.  **PostgreSQL (Se não usar Docker)**:
    *   Baixe o instalador em [postgresql.org/download/windows](https://www.postgresql.org/download/windows/).
    *   Durante a instalação, defina a senha do usuário `postgres`.

### 🐧 Linux (Ubuntu/Debian)

1.  **Java JDK 21**:
    ```bash
    sudo apt update
    sudo apt install openjdk-21-jdk
    java -version
    ```

2.  **Node.js**:
    ```bash
    # Usando NVM (Recomendado)
    curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
    # (Reinicie o terminal)
    nvm install 20
    node -v
    ```

3.  **Docker (Recomendado para Banco)**:
    ```bash
    # Siga as instruções oficiais:
    # https://docs.docker.com/engine/install/ubuntu/
    ```

---

## ⚙️ Configuração do Ambiente (.env)

O backend utiliza variáveis de ambiente para configurações sensíveis. Crie um arquivo chamado `.env` na pasta `gestor-compras-backend/`.

### 1. Modelo de Configuração
Copie o conteúdo abaixo e substitua os textos explicativos pelos seus dados reais:

```env
# --- Segurança ---
# Chave secreta JWT (mínimo 256 bits)
JWT_SECRET=digite_sua_chave_secreta_aqui

# --- Banco de Dados (PostgreSQL) ---
POSTGRES_USER=digite_seu_usuario_postgres
POSTGRES_PASSWORD=digite_sua_senha_postgres
POSTGRES_DB=digite_o_nome_do_banco
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/digite_o_nome_do_banco

# --- Servidor ---
SERVER_PORT=8081
SPRING_PROFILES_ACTIVE=dev
```

### 2. Detalhes das Variáveis

| Variável | Descrição | Obrigatório | Exemplo |
|----------|-----------|-------------|---------|
| `JWT_SECRET` | Chave secreta para assinar tokens JWT. | **Sim** | `minha-chave-secreta-base64` |
| `POSTGRES_USER` | Usuário do banco de dados PostgreSQL. | **Sim** | `postgres` |
| `POSTGRES_PASSWORD` | Senha do banco de dados PostgreSQL. | **Sim** | `123456` |
| `POSTGRES_DB` | Nome do banco de dados. | **Sim** | `meu_banco_gestor` |
| `SPRING_DATASOURCE_URL` | URL JDBC completa. | Não | `jdbc:postgresql://localhost:5432/meu_banco_gestor` |
| `SERVER_PORT` | Porta do servidor backend. | Não | `8081` |
| `SPRING_PROFILES_ACTIVE` | Perfil ativo (dev ou prod). | Não | `dev` |

---

## 🚀 Executando o Projeto

### Passo 1: Banco de Dados

**Via Docker (Mais Fácil):**
Na raiz do projeto (`gestor-compras/`), suba o banco:
```bash
docker-compose up -d db
```

**Via Instalação Local:**
Certifique-se que o serviço PostgreSQL está rodando e crie o banco com o **mesmo nome** que você definiu no `.env`:
```sql
CREATE DATABASE digite_o_nome_do_banco;
```

### Passo 2: Backend (API)

Abra um terminal na pasta `gestor-compras-backend` e execute:

**🪟 Windows:**
```cmd
.\mvnw.cmd spring-boot:run
```

**🐧 Linux/Mac:**
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

*Sucesso:* O servidor iniciará na porta **8081**.
*Swagger:* Acesse [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

### Passo 3: Frontend (Web)

Abra **outro** terminal na pasta `gestor-compras-frontend/vue-project` e execute:

```bash
# Instalar dependências (apenas na 1ª vez)
npm install

# Rodar servidor de desenvolvimento
npm run dev
```

*Sucesso:* O sistema abrirá na porta **5173**.
*Acesse:* [http://localhost:5173](http://localhost:5173)

---

## 👥 Primeiro Acesso (Usuário Padrão)

Após iniciar o sistema pela primeira vez, o banco será populado automaticamente com um usuário administrador padrão:

| Campo | Valor |
|-------|-------|
| **E-mail** | `admin@admin.com` |
| **Senha** | `admin` |

> **IMPORTANTE:** Por questões de segurança, recomenda-se alterar esta senha ou criar um novo usuário administrador e desativar este após o primeiro login bem-sucedido.

---

## 🛠️ Utilitários

### 📊 Monitoramento
O projeto possui uma stack pronta com Prometheus e Grafana.
```bash
# Na raiz do projeto:
docker-compose -f docker-compose-monitoring.yml up -d
```
- **Prometheus:** [localhost:9090](http://localhost:9090)
- **Grafana:** [localhost:3000](http://localhost:3000) (Login: `admin` / `admin`)

### 📚 Documentação de Código (JavaDoc)
Gere a documentação técnica do backend:
```bash
cd gestor-compras-backend
./mvnw javadoc:javadoc
# Abra o arquivo: target/site/apidocs/index.html
```

### 🌐 Nginx (Reverse Proxy)

O projeto inclui uma configuração completa do **Nginx** como reverse proxy para rotear requisições entre frontend e backend.

#### ⚙️ Configurações Principais

**Arquivos:**
- [`nginx/nginx.conf`](nginx/nginx.conf) - Configuração global
- [`nginx/conf.d/default.conf`](nginx/conf.d/default.conf) - Configuração de roteamento

**Funcionalidades:**
- ✅ **Compressão Gzip** para otimização de tráfego
- ✅ **Connection Pooling** (keepalive) para backend/frontend
- ✅ **Security Headers** (X-Frame-Options, X-XSS-Protection, etc.)
- ✅ **Timeouts customizados** (relatórios têm 120s, API 60s)
- ✅ **Upload de arquivos** até 10MB
- ✅ **Preparado para SSL/HTTPS** (comentado no arquivo)

Em ambiente de produção com Docker, o Nginx é executado automaticamente:

```bash
# Subir todo o sistema com Nginx
docker-compose -f docker-compose.prod.yml up -d

# Verificar logs do Nginx
docker-compose -f docker-compose.prod.yml logs -f nginx
```

## 🔌 Portas e Serviços

Resumo das portas utilizadas pelo sistema:

| Serviço | Porta | Descrição |
|---------|-------|-----------|
| **Frontend** | `5173` | Aplicação Web (Vue.js) - Dev |
| **Backend** | `8081` | API REST (Spring Boot) |
| **PostgreSQL**| `5432` | Banco de Dados |
| **Nginx** | `80` | Reverse Proxy - Produção |
| **Nginx (HTTPS)** | `443` | Reverse Proxy - SSL (Opcional) |
| **Grafana** | `3000` | Dashboards |
| **Prometheus**| `9090` | Métricas |