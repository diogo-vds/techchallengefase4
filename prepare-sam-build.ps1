# Script PowerShell para preparar o SAM build
# Copiar arquivos necessários para o build SAM

# Criar diretório de dados se não existir
$buildDataDir = ".aws-sam\build\AvaliacaoFunction\data"
if (-not (Test-Path $buildDataDir)) {
    New-Item -ItemType Directory -Path $buildDataDir -Force | Out-Null
    Write-Host "✅ Diretório criado: $buildDataDir"
}

# Copiar o arquivo feedbacks.json
$sourceFile = "data\feedbacks.json"
$destFile = "$buildDataDir\feedbacks.json"

if (Test-Path $sourceFile) {
    Copy-Item -Path $sourceFile -Destination $destFile -Force
    Write-Host "✅ data\feedbacks.json copiado para o build SAM"
} else {
    Write-Host "⚠️ Arquivo data\feedbacks.json não encontrado, criando um vazio..."
    
    # Criar diretório data se não existir
    if (-not (Test-Path "data")) {
        New-Item -ItemType Directory -Path "data" -Force | Out-Null
    }
    
    # Criar arquivo vazio com array JSON
    "[]" | Out-File -FilePath $sourceFile -Encoding UTF8
    Copy-Item -Path $sourceFile -Destination $destFile -Force
    Write-Host "✅ Arquivo feedbacks.json criado e copiado"
}

Write-Host "✅ SAM build pronto!"
