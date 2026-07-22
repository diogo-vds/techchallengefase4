# ========================================================
# Script para levantar ambiente completo com RabbitMQ
# ========================================================
# Uso: .\run-local.ps1 [start|stop|build|test|clean]
# ========================================================

param(
    [Parameter(Position=0)]
    [ValidateSet("start", "stop", "build", "test", "clean", "logs", "status")]
    [string]$command = "start"
)

# Cores para output
$Green = "`e[32m"
$Red = "`e[31m"
$Yellow = "`e[33m"
$Cyan = "`e[36m"
$Reset = "`e[0m"

function Write-Header {
    param([string]$text)
    Write-Host ""
    Write-Host "=" * 60 -ForegroundColor Cyan
    Write-Host $text -ForegroundColor Cyan
    Write-Host "=" * 60 -ForegroundColor Cyan
}

function Write-Success {
    param([string]$text)
    Write-Host "✅ $text" -ForegroundColor Green
}

function Write-Error-Custom {
    param([string]$text)
    Write-Host "❌ $text" -ForegroundColor Red
}

function Write-Info {
    param([string]$text)
    Write-Host "ℹ️ $text" -ForegroundColor Yellow
}

# ========================================================
# FUNÇÃO: START
# ========================================================
function Start-Environment {
    Write-Header "🚀 Iniciando Ambiente com RabbitMQ"
    
    # Verificar Docker
    Write-Info "Verificando Docker..."
    if (!(docker ps 2>$null)) {
        Write-Error-Custom "Docker não está rodando. Inicie o Docker Desktop."
        exit 1
    }
    Write-Success "Docker OK"
    
    # Levantar RabbitMQ
    Write-Info "Levantando RabbitMQ..."
    docker-compose up -d
    if ($LASTEXITCODE -ne 0) {
        Write-Error-Custom "Erro ao levantar RabbitMQ"
        exit 1
    }
    
    # Aguardar RabbitMQ ficar pronto
    Write-Info "Aguardando RabbitMQ ficar pronto..."
    $attempts = 0
    while ($attempts -lt 30) {
        if (docker exec avaliacao-rabbitmq rabbitmq-diagnostics -q ping 2>$null) {
            Write-Success "RabbitMQ está pronto!"
            break
        }
        $attempts++
        Start-Sleep -Seconds 1
        Write-Host "." -NoNewline
    }
    
    if ($attempts -eq 30) {
        Write-Error-Custom "RabbitMQ não ficou pronto no tempo limite"
        exit 1
    }
    
    # Compilar
    Write-Info "Compilando projeto..."
    mvn clean compile -q
    if ($LASTEXITCODE -ne 0) {
        Write-Error-Custom "Erro na compilação"
        exit 1
    }
    Write-Success "Compilação OK"
    
    # Build SAM
    Write-Info "Building SAM..."
    sam build --quiet
    if ($LASTEXITCODE -ne 0) {
        Write-Error-Custom "Erro no build SAM"
        exit 1
    }
    Write-Success "SAM build OK"
    
    # Iniciar SAM local
    Write-Header "🌐 Iniciando SAM Local"
    Write-Info "API estará disponível em: http://127.0.0.1:3000/avaliacao"
    Write-Info "RabbitMQ Management: http://localhost:15672 (guest/guest)"
    Write-Info "Pressione Ctrl+C para parar"
    Write-Host ""
    
    sam local start-api
}

# ========================================================
# FUNÇÃO: STOP
# ========================================================
function Stop-Environment {
    Write-Header "🛑 Parando Ambiente"
    
    docker-compose down
    Write-Success "RabbitMQ parado"
}

# ========================================================
# FUNÇÃO: BUILD
# ========================================================
function Build-Project {
    Write-Header "🔨 Compilando Projeto"
    
    mvn clean package -q
    if ($LASTEXITCODE -ne 0) {
        Write-Error-Custom "Erro na compilação"
        exit 1
    }
    
    $jarFile = Get-Item "target/avaliacao-function.jar" -ErrorAction SilentlyContinue
    if ($jarFile) {
        $size = "{0:N2}" -f ($jarFile.Length / 1MB)
        Write-Success "JAR criado: $($jarFile.Name) ($size MB)"
    }
}

# ========================================================
# FUNÇÃO: TEST
# ========================================================
function Test-Project {
    Write-Header "🧪 Rodando Testes"
    
    mvn clean test -q
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Todos os testes passaram!"
    } else {
        Write-Error-Custom "Alguns testes falharam"
        exit 1
    }
}

# ========================================================
# FUNÇÃO: CLEAN
# ========================================================
function Clean-Environment {
    Write-Header "🧹 Limpando Ambiente"
    
    # Parar containers
    Write-Info "Parando containers..."
    docker-compose down -v
    Write-Success "Containers removidos"
    
    # Limpar Maven
    Write-Info "Limpando Maven..."
    mvn clean -q
    Write-Success "Diretório target removido"
    
    # Remover SAM build
    Write-Info "Limpando SAM..."
    Remove-Item -Path ".aws-sam" -Recurse -ErrorAction SilentlyContinue
    Remove-Item -Path "samconfig.toml" -ErrorAction SilentlyContinue
    Write-Success "Build SAM removido"
    
    Write-Success "Ambiente limpo!"
}

# ========================================================
# FUNÇÃO: LOGS
# ========================================================
function Show-Logs {
    Write-Header "📋 Logs do RabbitMQ"
    docker-compose logs -f rabbitmq
}

# ========================================================
# FUNÇÃO: STATUS
# ========================================================
function Show-Status {
    Write-Header "📊 Status do Ambiente"
    
    Write-Info "Docker:"
    docker ps --filter "name=avaliacao-rabbitmq" --format "{{.Names}}: {{.Status}}"
    
    Write-Info "Portas:"
    Write-Host "  - RabbitMQ AMQP: 5672"
    Write-Host "  - RabbitMQ Management: http://localhost:15672"
    Write-Host "  - SAM Local API: http://127.0.0.1:3000"
    
    Write-Info "URLs Importantes:"
    Write-Host "  - Management UI: http://localhost:15672 (guest/guest)"
    Write-Host "  - API Endpoint: POST http://127.0.0.1:3000/avaliacao"
}

# ========================================================
# MAIN EXECUTION
# ========================================================

switch ($command) {
    "start" { Start-Environment }
    "stop" { Stop-Environment }
    "build" { Build-Project }
    "test" { Test-Project }
    "clean" { Clean-Environment }
    "logs" { Show-Logs }
    "status" { Show-Status }
    default {
        Write-Host "Uso: .\run-local.ps1 [start|stop|build|test|clean|logs|status]" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Exemplos:" -ForegroundColor Cyan
        Write-Host "  .\run-local.ps1 start        # Levantar ambiente com RabbitMQ"
        Write-Host "  .\run-local.ps1 build        # Compilar projeto"
        Write-Host "  .\run-local.ps1 test         # Rodar testes"
        Write-Host "  .\run-local.ps1 stop         # Parar RabbitMQ"
        Write-Host "  .\run-local.ps1 clean        # Limpar ambiente"
        Write-Host "  .\run-local.ps1 logs         # Ver logs"
        Write-Host "  .\run-local.ps1 status       # Ver status"
    }
}
