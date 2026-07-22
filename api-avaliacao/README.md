# Tech Challenge - Fase 4

API AWS Lambda Java 21 para receber avaliacoes. Cada requisicao e persistida
no DynamoDB antes da publicacao de um evento na SQS.

## Ambiente local

Pre-requisitos: Java 21, Maven, Docker e, para executar a API, AWS SAM CLI.

```powershell
docker-compose up -d
mvn clean verify
sam build
sam local start-api --env-vars env.local.json
```

A API fica disponivel em `POST http://127.0.0.1:3000/avaliacao`.

Exemplo:

```json
{
  "descricao": "Atendimento demorado",
  "nota": 3
}
```

Para executar o consumidor:

```powershell
pip install -r consumer-requirements.txt
Copy-Item .env.example .env
python consumer.py
```

Recursos locais:

- DynamoDB: tabela `avaliacoes`.
- SQS: fila `avaliacoes` com DLQ `avaliacoes-dlq`.
- Endpoint LocalStack: `http://localhost:4566`.
