# Tech Challenge - Fase 4

API AWS Lambda Java 21 para cadastrar estudantes e receber avaliacoes
autenticadas. Cada avaliacao e persistida no DynamoDB antes da publicacao de
um evento na SQS.

## Ambiente local

Pre-requisitos: Java 21, Maven, Docker e, para executar a API, AWS SAM CLI.

```powershell
docker-compose up -d
mvn clean verify
sam build
sam local start-api --env-vars env.local.json
```

A API disponibiliza:

- `POST http://127.0.0.1:3000/usuarios`: cadastro publico de estudantes;
- `POST http://127.0.0.1:3000/avaliacao`: envio autenticado de avaliacao.

Cadastro:

```powershell
curl.exe -X POST http://127.0.0.1:3000/usuarios `
  -H "Content-Type: application/json" `
  -d '{\"nome\":\"Maria\",\"email\":\"maria@example.com\",\"senha\":\"senha123\",\"perfil\":\"ESTUDANTE\"}'
```

O cadastro publico aceita somente o perfil `ESTUDANTE`. Administradores devem
ser provisionados por um processo administrativo, nunca por esse endpoint.

Envio de avaliacao com HTTP Basic Authentication:

```powershell
curl.exe -X POST http://127.0.0.1:3000/avaliacao `
  -u "maria@example.com:senha123" `
  -H "Content-Type: application/json" `
  -d '{\"descricao\":\"Atendimento demorado\",\"nota\":3}'
```

Corpo da avaliacao:

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

- DynamoDB: tabelas `avaliacoes` e `usuarios`.
- SQS: fila `avaliacoes` com DLQ `avaliacoes-dlq`.
- Endpoint LocalStack: `http://localhost:4566`.

As senhas sao armazenadas como hash BCrypt. Basic Authentication deve ser
utilizada somente sobre HTTPS fora do ambiente local, pois Base64 nao
criptografa as credenciais.
