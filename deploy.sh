#!/bin/bash

# ==============================================================================
# SCRIPT DE DEPLOY - Gestor de Compras
# ==============================================================================
# Uso: ./deploy.sh
# ==============================================================================

set -e  # Parar em caso de erro

echo "======================================================================"
echo "  🚀 DEPLOY - Gestor de Compras"
echo "======================================================================"
echo ""

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# ==============================================================================
# 1. VERIFICAÇÕES PRÉ-DEPLOY
# ==============================================================================
echo "📋 Verificando pré-requisitos..."

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker não está instalado!${NC}"
    exit 1
fi

# Verificar se Docker Compose está instalado (plugin ou standalone)
if ! docker compose version &> /dev/null && ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ Docker Compose não está instalado!${NC}"
    exit 1
fi

# Verificar se arquivo .env existe
if [ ! -f .env ]; then
    echo -e "${YELLOW}⚠️  Arquivo .env não encontrado!${NC}"
    echo "📝 Criando .env a partir do template..."
    cp .env.production.template .env
    echo -e "${RED}❌ Configure o arquivo .env antes de continuar!${NC}"
    echo "   Edite: nano .env"
    exit 1
fi

echo -e "${GREEN}✅ Pré-requisitos OK${NC}"
echo ""

# ==============================================================================
# 2. BACKUP (se já existir deploy anterior)
# ==============================================================================
if [ "$(docker ps -aq -f name=gestor-compras)" ]; then
    echo "💾 Fazendo backup do banco de dados..."
    mkdir -p ./backups
    BACKUP_FILE="./backups/backup-$(date +%Y%m%d-%H%M%S).sql"
    
    docker exec gestor-compras-postgres pg_dump -U gestorcompras_user gestorcompras_prod > "$BACKUP_FILE" 2>/dev/null || true
    
    if [ -f "$BACKUP_FILE" ]; then
        echo -e "${GREEN}✅ Backup salvo em: $BACKUP_FILE${NC}"
    fi
    echo ""
fi

# ==============================================================================
# 3. PARAR CONTAINERS ANTIGOS
# ==============================================================================
echo "🛑 Parando containers antigos..."
docker compose -f docker-compose.prod.yml down || true
echo -e "${GREEN}✅ Containers parados${NC}"
echo ""

# ==============================================================================
# 4. BUILD DAS IMAGENS
# ==============================================================================
echo "🔨 Construindo imagens Docker..."
docker compose -f docker-compose.prod.yml build --no-cache

echo -e "${GREEN}✅ Imagens construídas${NC}"
echo ""

# ==============================================================================
# 5. INICIAR CONTAINERS
# ==============================================================================
echo "🚀 Iniciando containers..."
docker compose -f docker-compose.prod.yml up -d

echo -e "${GREEN}✅ Containers iniciados${NC}"
echo ""

# ==============================================================================
# 6. AGUARDAR SERVIÇOS FICAREM PRONTOS
# ==============================================================================
echo "⏳ Aguardando serviços ficarem prontos..."

# Aguardar PostgreSQL
echo "   - PostgreSQL..."
sleep 10
until docker exec gestor-compras-postgres pg_isready -U gestorcompras_user -d gestorcompras_prod &> /dev/null; do
    printf "."
    sleep 2
done
echo -e "${GREEN}   ✅ PostgreSQL pronto${NC}"

# Aguardar Backend
echo "   - Backend..."
sleep 20
until curl -f http://localhost:8081/actuator/health &> /dev/null; do
    printf "."
    sleep 3
done
echo -e "${GREEN}   ✅ Backend pronto${NC}"

# Aguardar Frontend
echo "   - Frontend..."
sleep 5
until curl -f http://localhost/health &> /dev/null; do
    printf "."
    sleep 2
done
echo -e "${GREEN}   ✅ Frontend pronto${NC}"

echo ""

# ==============================================================================
# 7. VERIFICAR STATUS
# ==============================================================================
echo "📊 Status dos containers:"
docker compose -f docker-compose.prod.yml ps

echo ""
echo "======================================================================"
echo -e "${GREEN}  ✅ DEPLOY CONCLUÍDO COM SUCESSO!${NC}"
echo "======================================================================"
echo ""
echo "🌐 Acessos:"
echo "   Frontend:     http://45.55.186.12"
echo "   Backend API:  http://45.55.186.12:8081/api/v1"
echo "   Swagger:      http://45.55.186.12:8081/swagger-ui/index.html"
echo "   Grafana:      http://45.55.186.12:3000 (admin/senha-do-.env)"
echo ""
echo "📝 Comandos úteis:"
echo "   Ver logs:           docker compose -f docker-compose.prod.yml logs -f"
echo "   Parar:              docker compose -f docker-compose.prod.yml down"
echo "   Reiniciar:          docker compose -f docker-compose.prod.yml restart"
echo "   Status:             docker compose -f docker-compose.prod.yml ps"
echo ""
echo "🔐 Usuários padrão:"
echo "   Admin: admin@gestor.com / admin123"
echo "   User:  user@gestor.com / user123"
echo ""
echo "⚠️  IMPORTANTE: Troque as senhas padrão em produção!"
echo "======================================================================"
