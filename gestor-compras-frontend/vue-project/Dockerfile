# ==============================================================================
# DOCKERFILE MULTI-STAGE - GESTOR DE COMPRAS FRONTEND
# ==============================================================================
# Stage 1: Build da aplicação Vue.js
# Stage 2: Servir com Nginx otimizado
# ==============================================================================

# ==============================================================================
# STAGE 1: BUILD
# ==============================================================================
FROM node:20-alpine AS build

WORKDIR /app

ARG VITE_API_BASE_URL=/
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

# Copiar package files primeiro (melhor cache)
COPY package*.json ./

# Instalar dependências
RUN npm ci --only=production=false

# Copiar código fonte
COPY . .

# Build de produção
RUN npm run build

# ==============================================================================
# STAGE 2: NGINX
# ==============================================================================
FROM nginx:1.25-alpine

# Metadados
LABEL maintainer="Gestor de Compras"
LABEL version="1.0"
LABEL description="Frontend Vue.js - Gestor de Compras"

# Remover configuração padrão do Nginx
RUN rm /etc/nginx/conf.d/default.conf

# Copiar configuração customizada do Nginx
COPY nginx.conf /etc/nginx/conf.d/

# Copiar build do stage anterior
COPY --from=build /app/dist /usr/share/nginx/html

# Criar usuário não-root
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /var/log/nginx && \
    touch /var/run/nginx.pid && \
    chown -R nginx:nginx /var/run/nginx.pid

# Mudar para usuário não-root
USER nginx

# Expor porta
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# Comando para iniciar Nginx
CMD ["nginx", "-g", "daemon off;"]
