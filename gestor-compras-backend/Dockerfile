# ==============================================================================
# DOCKERFILE MULTI-STAGE - GESTOR DE COMPRAS BACKEND
# ==============================================================================
# Stage 1: Build da aplicação
# Stage 2: Runtime otimizado
# ==============================================================================

# ==============================================================================
# STAGE 1: BUILD
# ==============================================================================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar arquivos de configuração do Maven primeiro (melhor cache)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Baixar dependências (layer separada para cache)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Build da aplicação (pular testes para deploy mais rápido)
RUN mvn clean package -DskipTests

# ==============================================================================
# STAGE 2: RUNTIME
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine

# Metadados
LABEL maintainer="Gestor de Compras"
LABEL version="1.0"
LABEL description="Backend Spring Boot - Gestor de Compras"

# Criar usuário não-root por segurança
RUN addgroup -S spring && adduser -S spring -G spring

# Diretório de trabalho
WORKDIR /app

# Copiar JAR do stage de build
COPY --from=build /app/target/*.jar app.jar

# Criar diretório para logs
RUN mkdir -p /app/logs && chown -R spring:spring /app

# Mudar para usuário não-root
USER spring

# Expor porta da aplicação
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Variáveis de ambiente padrão (podem ser sobrescritas no docker-compose)
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=prod

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
