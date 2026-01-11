@echo off
REM ==============================================================================
REM SCRIPT DE DEPLOY - Gestor de Compras (Windows)
REM ==============================================================================
REM Uso: deploy.bat
REM ==============================================================================

echo ======================================================================
echo   DEPLOY - Gestor de Compras
echo ======================================================================
echo.

REM ==============================================================================
REM 1. VERIFICACOES PRE-DEPLOY
REM ==============================================================================
echo Verificando pre-requisitos...

where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo ERRO: Docker nao esta instalado!
    exit /b 1
)

where docker-compose >nul 2>nul
if %errorlevel% neq 0 (
    echo ERRO: Docker Compose nao esta instalado!
    exit /b 1
)

if not exist .env (
    echo AVISO: Arquivo .env nao encontrado!
    echo Criando .env a partir do template...
    copy .env.production.template .env
    echo ERRO: Configure o arquivo .env antes de continuar!
    exit /b 1
)

echo OK: Pre-requisitos verificados
echo.

REM ==============================================================================
REM 2. PARAR CONTAINERS ANTIGOS
REM ==============================================================================
echo Parando containers antigos...
docker-compose -f docker-compose.prod.yml down
echo OK: Containers parados
echo.

REM ==============================================================================
REM 3. BUILD DAS IMAGENS
REM ==============================================================================
echo Construindo imagens Docker...
docker-compose -f docker-compose.prod.yml build --no-cache
echo OK: Imagens construidas
echo.

REM ==============================================================================
REM 4. INICIAR CONTAINERS
REM ==============================================================================
echo Iniciando containers...
docker-compose -f docker-compose.prod.yml up -d
echo OK: Containers iniciados
echo.

REM ==============================================================================
REM 5. AGUARDAR SERVICOS
REM ==============================================================================
echo Aguardando servicos ficarem prontos...
timeout /t 30 /nobreak >nul
echo.

REM ==============================================================================
REM 6. STATUS
REM ==============================================================================
echo Status dos containers:
docker-compose -f docker-compose.prod.yml ps

echo.
echo ======================================================================
echo   DEPLOY CONCLUIDO!
echo ======================================================================
echo.
echo Acessos:
echo    Frontend:     http://45.55.186.12
echo    Backend API:  http://45.55.186.12:8081/api/v1
echo    Swagger:      http://45.55.186.12:8081/swagger-ui/index.html
echo.
echo Comandos uteis:
echo    Ver logs:     docker-compose -f docker-compose.prod.yml logs -f
echo    Parar:        docker-compose -f docker-compose.prod.yml down
echo    Reiniciar:    docker-compose -f docker-compose.prod.yml restart
echo.
echo ======================================================================

pause
