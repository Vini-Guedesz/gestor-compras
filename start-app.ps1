# Script para iniciar toda a aplicação Gestor de Compras
# Executa Backend (Spring Boot) e Frontend (Vue.js) simultaneamente

Write-Host "🚀 Iniciando Aplicação Gestor de Compras..." -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Cyan

# Função para verificar se uma porta está em uso
function Test-Port {
    param([int]$Port)
    $connection = Test-NetConnection -ComputerName "localhost" -Port $Port -InformationLevel Quiet -WarningAction SilentlyContinue
    return $connection
}

# Verificar se as portas já estão em uso
$backendPort = 8081
$frontendPort = 5173

Write-Host "🔍 Verificando portas..." -ForegroundColor Yellow

if (Test-Port -Port $backendPort) {
    Write-Host "⚠️  Porta $backendPort (Backend) já está em uso!" -ForegroundColor Red
    $response = Read-Host "Deseja parar o processo existente? (s/n)"
    if ($response -eq 's' -or $response -eq 'S') {
        # Encontrar e parar processo na porta 8081
        $processId = (Get-NetTCPConnection -LocalPort $backendPort -ErrorAction SilentlyContinue).OwningProcess
        if ($processId) {
            Stop-Process -Id $processId -Force
            Write-Host "✅ Processo na porta $backendPort foi finalizado" -ForegroundColor Green
            Start-Sleep -Seconds 2
        }
    } else {
        Write-Host "❌ Script cancelado pelo usuário" -ForegroundColor Red
        exit 1
    }
}

if (Test-Port -Port $frontendPort) {
    Write-Host "⚠️  Porta $frontendPort (Frontend) já está em uso!" -ForegroundColor Red
    $response = Read-Host "Deseja parar o processo existente? (s/n)"
    if ($response -eq 's' -or $response -eq 'S') {
        # Encontrar e parar processo na porta 5173
        $processId = (Get-NetTCPConnection -LocalPort $frontendPort -ErrorAction SilentlyContinue).OwningProcess
        if ($processId) {
            Stop-Process -Id $processId -Force
            Write-Host "✅ Processo na porta $frontendPort foi finalizado" -ForegroundColor Green
            Start-Sleep -Seconds 2
        }
    } else {
        Write-Host "❌ Script cancelado pelo usuário" -ForegroundColor Red
        exit 1
    }
}

# Caminhos dos projetos
$backendPath = "gestor-compras-backend"
$frontendPath = "gestor-compras-frontend\vue-project"

# Verificar se os diretórios existem
if (-not (Test-Path $backendPath)) {
    Write-Host "❌ Diretório do backend não encontrado: $backendPath" -ForegroundColor Red
    exit 1
}

if (-not (Test-Path $frontendPath)) {
    Write-Host "❌ Diretório do frontend não encontrado: $frontendPath" -ForegroundColor Red
    exit 1
}

Write-Host "📁 Diretórios encontrados com sucesso!" -ForegroundColor Green

# Função para iniciar o backend em background
function Start-Backend {
    Write-Host "🔧 Iniciando Backend (Spring Boot)..." -ForegroundColor Cyan
    Set-Location $backendPath
    
    # Verificar se mvnw.cmd existe
    if (-not (Test-Path "mvnw.cmd")) {
        Write-Host "❌ mvnw.cmd não encontrado no diretório backend!" -ForegroundColor Red
        return $false
    }
    
    # Iniciar o backend em um novo processo
    $backendJob = Start-Process powershell -ArgumentList "-NoExit", "-Command", "& {Write-Host 'Iniciando Backend...' -ForegroundColor Green; .\mvnw.cmd spring-boot:run}" -PassThru
    Set-Location ..
    
    Write-Host "✅ Backend iniciado (PID: $($backendJob.Id))" -ForegroundColor Green
    return $backendJob
}

# Função para iniciar o frontend em background
function Start-Frontend {
    Write-Host "🎨 Iniciando Frontend (Vue.js)..." -ForegroundColor Cyan
    Set-Location $frontendPath
    
    # Verificar se package.json existe
    if (-not (Test-Path "package.json")) {
        Write-Host "❌ package.json não encontrado no diretório frontend!" -ForegroundColor Red
        return $false
    }
    
    # Verificar se node_modules existe
    if (-not (Test-Path "node_modules")) {
        Write-Host "📦 Instalando dependências do frontend..." -ForegroundColor Yellow
        npm install
    }
    
    # Iniciar o frontend em um novo processo
    $frontendJob = Start-Process powershell -ArgumentList "-NoExit", "-Command", "& {Write-Host 'Iniciando Frontend...' -ForegroundColor Green; npm run dev}" -PassThru
    Set-Location ..\..
    
    Write-Host "✅ Frontend iniciado (PID: $($frontendJob.Id))" -ForegroundColor Green
    return $frontendJob
}

# Iniciar os serviços
Write-Host ""
Write-Host "🚀 Iniciando serviços..." -ForegroundColor Green

$backendProcess = Start-Backend
if (-not $backendProcess) {
    Write-Host "❌ Falha ao iniciar o backend!" -ForegroundColor Red
    exit 1
}

# Aguardar um pouco antes de iniciar o frontend
Write-Host "⏳ Aguardando backend inicializar..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

$frontendProcess = Start-Frontend
if (-not $frontendProcess) {
    Write-Host "❌ Falha ao iniciar o frontend!" -ForegroundColor Red
    # Parar o backend se o frontend falhar
    if ($backendProcess) {
        Stop-Process -Id $backendProcess.Id -Force
    }
    exit 1
}

# Aguardar os serviços ficarem prontos
Write-Host ""
Write-Host "⏳ Aguardando serviços ficarem prontos..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Verificar se os serviços estão rodando
Write-Host ""
Write-Host "🔍 Verificando status dos serviços..." -ForegroundColor Cyan

$backendRunning = Test-Port -Port $backendPort
$frontendRunning = Test-Port -Port $frontendPort

if ($backendRunning) {
    Write-Host "✅ Backend rodando em: http://localhost:$backendPort" -ForegroundColor Green
} else {
    Write-Host "❌ Backend não está respondendo na porta $backendPort" -ForegroundColor Red
}

if ($frontendRunning) {
    Write-Host "✅ Frontend rodando em: http://localhost:$frontendPort" -ForegroundColor Green
} else {
    Write-Host "❌ Frontend não está respondendo na porta $frontendPort" -ForegroundColor Red
}

# Exibir informações finais
Write-Host ""
Write-Host "🎉 APLICAÇÃO INICIADA COM SUCESSO!" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Cyan
Write-Host "📊 Backend API: http://localhost:$backendPort" -ForegroundColor White
Write-Host "🌐 Frontend:    http://localhost:$frontendPort" -ForegroundColor White
Write-Host "🗄️  Console H2:  http://localhost:$backendPort/h2-console" -ForegroundColor White
Write-Host ""
Write-Host "👥 USUÁRIOS DE TESTE:" -ForegroundColor Yellow
Write-Host "   Admin: admin@gestor.com / admin123" -ForegroundColor White
Write-Host "   User:  user@gestor.com / user123" -ForegroundColor White
Write-Host ""
Write-Host "⚠️  Para parar a aplicação, feche este terminal ou pressione Ctrl+C" -ForegroundColor Red
Write-Host "   Os processos continuarão rodando em segundo plano." -ForegroundColor Red
Write-Host ""

# Manter o script rodando
Write-Host "💡 Pressione qualquer tecla para abrir o navegador..." -ForegroundColor Cyan
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

# Abrir o navegador
Start-Process "http://localhost:$frontendPort"

Write-Host ""
Write-Host "🖥️  Navegador aberto! Pressione qualquer tecla para sair..." -ForegroundColor Cyan
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
