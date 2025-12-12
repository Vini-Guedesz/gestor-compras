# Variáveis de Ambiente

Este documento detalha as variáveis de ambiente utilizadas no backend do Gestor de Compras. As variáveis podem ser definidas no sistema operacional ou em um arquivo `.env` na raiz do diretório `gestor-compras-backend`.

## Configuração Básica

Copie o arquivo `.env.example` para `.env` para começar:

```bash
cp .env.example .env
```

## Variáveis Disponíveis

| Variável | Descrição | Obrigatório | Valor Padrão / Exemplo |
|----------|-----------|-------------|------------------------|
| `JWT_SECRET` | Chave secreta usada para assinar e validar tokens JWT. Deve ser uma string longa e aleatória (mínimo 256 bits). **Em produção, use uma chave forte e nunca a compartilhe.** | **Sim** (Prod) | `sua-chave-super-secreta-aqui...` |
| `SPRING_DATASOURCE_URL` | URL de conexão JDBC com o banco de dados PostgreSQL. | Não | `jdbc:postgresql://localhost:5432/gestorcomprasbackend` |
| `SPRING_DATASOURCE_USERNAME` | Nome de usuário do banco de dados. | Não | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco de dados. | Não | `admin` |
| `SERVER_PORT` | Porta onde o servidor backend será executado. | Não | `8081` |
| `SPRING_PROFILES_ACTIVE` | Perfil do Spring Boot ativo (ex: `dev`, `prod`). Afeta configurações específicas de ambiente. | Não | `dev` |
| `CORS_ALLOWED_ORIGINS` | Lista de origens permitidas para requisições CORS (Cross-Origin Resource Sharing), separadas por vírgula. | Não | `http://localhost:5173,https://meudominio.com` |

## Notas Importantes

1. **Segurança:** Nunca comite o arquivo `.env` com senhas ou chaves reais no controle de versão. O arquivo `.gitignore` já deve estar configurado para excluí-lo.
2. **Prioridade:** As variáveis de ambiente do sistema operacional geralmente têm precedência sobre as definidas no arquivo `.env` ou `application.properties`.
3. **Banco de Dados:** Se as variáveis `SPRING_DATASOURCE_*` não forem fornecidas, a aplicação tentará usar as configurações padrão definidas no `application.properties` ou `application-dev.properties` (dependendo do perfil ativo).

## Gerando uma Chave JWT Segura

Você pode gerar uma chave segura usando o OpenSSL (se disponível no seu terminal):

```bash
openssl rand -base64 64
```

Copie a saída e cole como valor de `JWT_SECRET`.
