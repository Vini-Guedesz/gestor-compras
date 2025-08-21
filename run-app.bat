@echo off
echo.
echo ========================================
echo  Iniciando Gestor de Compras
echo ========================================
echo.

REM Verificar se estamos no diretório correto
if not exist "gestor-compras-backend" (
    echo ❌ Backend não encontrado!
    echo Execute este script no diretório raiz do projeto.
    pause
    exit /b 1
)

if not exist "gestor-compras-frontend" (
    echo ❌ Frontend não encontrado!
    echo Execute este script no diretório raiz do projeto.
    pause
    exit /b 1
)

echo 📁 Navegando para o frontend...
cd gestor-compras-frontend\vue-project

echo 🔍 Verificando dependências...
if not exist "node_modules\concurrently" (
    echo 📦 Instalando dependências...
    npm install --save-dev concurrently
)

echo.
echo 🚀 Iniciando aplicação completa...
echo    • Backend: http://localhost:8081
echo    • Frontend: http://localhost:5173
echo    • Console H2: http://localhost:8081/h2-console
echo.
echo ⏳ Aguarde a inicialização...
echo.

REM Executar aplicação completa
npm run start:full

pause
