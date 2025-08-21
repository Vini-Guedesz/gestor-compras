# Script simplificado para iniciar toda a aplicação
# Executa Backend + Frontend + Banco de dados

Write-Host "🚀 Iniciando Aplicação Gestor de Compras Completa!" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host ""

# Verificar se estamos no diretório correto
if (-not (Test-Path "gestor-compras-backend") -or -not (Test-Path "gestor-compras-frontend")) {
    Write-Host "❌ Execute este script no diretório raiz do projeto!" -ForegroundColor Red
    Write-Host "   Diretório atual: $(Get-Location)" -ForegroundColor Yellow
    exit 1
}

Write-Host "📁 Navegando para o frontend..." -ForegroundColor Yellow
Set-Location "gestor-compras-frontend\vue-project"

Write-Host "🔍 Verificando dependências..." -ForegroundColor Yellow
if (-not (Test-Path "node_modules\concurrently")) {
    Write-Host "📦 Instalando dependências necessárias..." -ForegroundColor Cyan
    npm install --save-dev concurrently
}

Write-Host ""
Write-Host "🎯 Iniciando aplicação completa..." -ForegroundColor Green
Write-Host "   • Backend (Spring Boot) na porta 8081" -ForegroundColor White
Write-Host "   • Frontend (Vue.js) na porta 5173" -ForegroundColor White
Write-Host "   • Banco H2 em memória" -ForegroundColor White
Write-Host ""
Write-Host "⏳ Aguarde... isso pode levar alguns segundos..." -ForegroundColor Yellow
Write-Host ""

# Executar o comando que inicia backend e frontend simultaneamente
npm run start:full
