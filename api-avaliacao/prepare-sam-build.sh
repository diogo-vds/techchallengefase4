#!/bin/bash
# Script para copiar arquivos necessários para o SAM build

# Criar diretório de dados se não existir
mkdir -p .aws-sam/build/AvaliacaoFunction/data

# Copiar o arquivo feedbacks.json
if [ -f "data/feedbacks.json" ]; then
    cp data/feedbacks.json .aws-sam/build/AvaliacaoFunction/data/feedbacks.json
    echo "✅ data/feedbacks.json copiado para o build SAM"
else
    echo "⚠️ Arquivo data/feedbacks.json não encontrado"
    # Criar um arquivo vazio se não existir
    mkdir -p data
    echo "[]" > data/feedbacks.json
    cp data/feedbacks.json .aws-sam/build/AvaliacaoFunction/data/feedbacks.json
    echo "✅ Arquivo feedbacks.json criado"
fi
