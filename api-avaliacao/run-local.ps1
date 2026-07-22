# ========================================================
# Script para levantar ambiente local com DynamoDB e SQS no LocalStack
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
    Write-Header "Iniciando ambiente AWS local"
    
    # Verificar Docker
    Write-Info "Verificando Docker..."
    if (!(docker ps 2>$null)) {
        Write-Error-Custom "Docker não está rodando. Inicie o Docker Desktop."
        exit 1
    }
    Write-Success "Docker OK"
    
    Write-Info "Levantando LocalStack..."
    docker-compose up -d
    if ($LASTEXITCODE -ne 0) {
        Write-Error-Custom "Erro ao levantar LocalStack"
        exit 1
    }
    
    Write-Info "Aguardando LocalStack ficar pronto..."
    $attempts = 0
    while ($attempts -lt 30) {
        $health = docker inspect --format='{{.State.Health.Status}}' avaliacao-localstack 2>$null
        if ($health -eq "healthy") {
            Write-Success "DynamoDB e SQS estão prontos!"
            break
        }
        $attempts++
        Start-Sleep -Seconds 1
        Write-Host "." -NoNewline
    }
    
    if ($attempts -eq 30) {
        Write-Error-Custom "LocalStack não ficou pronto no tempo limite"
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
    Write-Info "LocalStack: http://localhost:4566"
    Write-Info "Pressione Ctrl+C para parar"
    Write-Host ""
    
    sam local start-api --env-vars env.local.json
}

# ========================================================
# FUNÇÃO: STOP
# ========================================================
function Stop-Environment {
    Write-Header "🛑 Parando Ambiente"
    
    docker-compose down
    Write-Success "LocalStack parado"
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
    Write-Header "Logs do LocalStack"
    docker-compose logs -f localstack
}

# ========================================================
# FUNÇÃO: STATUS
# ========================================================
function Show-Status {
    Write-Header "📊 Status do Ambiente"
    
    Write-Info "Docker:"
    docker ps --filter "name=avaliacao-localstack" --format "{{.Names}}: {{.Status}}"
    
    Write-Info "Portas:"
    Write-Host "  - LocalStack AWS APIs: http://localhost:4566"
    Write-Host "  - SAM Local API: http://127.0.0.1:3000"
    
    Write-Info "URLs Importantes:"
    Write-Host "  - DynamoDB: tabela avaliacoes"
    Write-Host "  - SQS: filas avaliacoes e avaliacoes-dlq"
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
        Write-Host "  .\run-local.ps1 start        # Levantar DynamoDB e SQS locais"
        Write-Host "  .\run-local.ps1 build        # Compilar projeto"
        Write-Host "  .\run-local.ps1 test         # Rodar testes"
        Write-Host "  .\run-local.ps1 stop         # Parar LocalStack"
        Write-Host "  .\run-local.ps1 clean        # Limpar ambiente"
        Write-Host "  .\run-local.ps1 logs         # Ver logs"
        Write-Host "  .\run-local.ps1 status       # Ver status"
    }
}
