# Guia de Implantação - AWS Lambda

## Resumo da Conversão

✅ **Conversão Concluída**: Azure Functions → AWS Lambda Java 21

### Principais Mudanças

1. **Dependências POM**
   - Removido: `azure-functions-java-library`
   - Adicionado: `aws-lambda-java-core`, `aws-lambda-java-events`
   - Build Plugin: Azure Functions → Maven Shade

2. **Código Java**
   - `AvaliacaoFunction` agora implementa `RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>`
   - Imports migrados de `com.microsoft.azure.functions` para `com.amazonaws.services.lambda.runtime`
   - Lógica de negócio preservada (AvaliacaoService, Validações, etc.)

3. **Configuração**
   - Novo arquivo: `template.yaml` (SAM para Infrastructure as Code)
   - Novo arquivo: `Dockerfile` (para container image deployment)
   - Novos eventos de teste em `events/`

## Pré-requisitos

- ✅ Java 21 JDK instalado
- ✅ Maven 3.9+ instalado
- AWS Account com permissões:
  - `lambda:*`
  - `apigateway:*`
  - `iam:*`
  - `logs:*`
  - `cloudformation:*`

## Instalação de Ferramentas

### AWS CLI
```bash
# Download: https://aws.amazon.com/cli/
# Depois verificar instalação
aws --version
```

### AWS SAM CLI
```bash
# Windows (via Chocolatey ou MSI)
choco install aws-sam-cli

# Ou fazer download do MSI
# https://github.com/aws/aws-sam-cli/releases

# Verificar
sam --version
```

### Docker (para testes locais)
```bash
# Download: https://www.docker.com/products/docker-desktop
docker --version
```

## Build

```bash
# Limpar e compilar
mvn clean package

# Resultado: target/avaliacao-function.jar
```

## Teste Local

### Opção 1: SAM Local (recomendado)

```bash
# Build com SAM
sam build

# Iniciar local API
sam local start-api

# Em outro terminal, testar
curl -X POST http://localhost:3000/avaliacao \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Excelente serviço",
    "nota": 9
  }'
```

### Opção 2: AWS Lambda Runtime Emulator

```bash
# Baixar runtime emulator
# https://github.com/aws/aws-lambda-runtime-interface-emulator

# Executar com Docker
docker run -p 9000:8080 \
  -v $(pwd)/target/avaliacao-function.jar:/var/task/avaliacao-function.jar \
  public.ecr.aws/lambda/java:21 \
  br.com.postech.techchallenge.fase4.function.AvaliacaoFunction::handleRequest

# Testar
curl -X POST "http://localhost:9000/2015-03-31/functions/function/invocations" \
  -H "Content-Type: application/json" \
  -d '{
    "body": "{\"descricao\":\"Teste\",\"nota\":8}"
  }'
```

## Deploy no AWS

### Via SAM (Recomendado - First Time)

```bash
# Build
sam build

# Deploy com guia interativo (cria stack CloudFormation)
sam deploy --guided

# Será solicitado:
# - Stack Name (ex: avaliacao-stack)
# - AWS Region (ex: us-east-1)
# - Confirmações

# Resultado: Stack criado no CloudFormation
```

### Via SAM (Subsequentes)

```bash
sam build
sam deploy
```

### Via AWS CLI (Alternativa)

```bash
# Build
mvn clean package

# Criar role IAM para Lambda (primeira vez)
aws iam create-role \
  --role-name lambda-role \
  --assume-role-policy-document '{
    "Version": "2012-10-17",
    "Statement": [{
      "Effect": "Allow",
      "Principal": {"Service": "lambda.amazonaws.com"},
      "Action": "sts:AssumeRole"
    }]
  }' \
  --region us-east-1

# Adicionar permissão básica
aws iam attach-role-policy \
  --role-name lambda-role \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

# Criar/Atualizar função Lambda
aws lambda update-function-code \
  --function-name avaliacao-function \
  --zip-file fileb://target/avaliacao-function.jar \
  --region us-east-1 2>/dev/null || \
aws lambda create-function \
  --function-name avaliacao-function \
  --runtime java21 \
  --role arn:aws:iam::ACCOUNT_ID:role/lambda-role \
  --handler br.com.postech.techchallenge.fase4.function.AvaliacaoFunction::handleRequest \
  --zip-file fileb://target/avaliacao-function.jar \
  --memory-size 512 \
  --timeout 60 \
  --region us-east-1

# Criar API Gateway (primeira vez)
aws apigateway create-rest-api \
  --name AvaliacaoAPI \
  --description "API para processamento de avaliações"

# (Referir-se à documentação AWS para integração completa)
```

## Verificar Deploy

```bash
# Listar funções
aws lambda list-functions --region us-east-1

# Verificar CloudFormation stack
aws cloudformation describe-stacks \
  --stack-name avaliacao-stack \
  --region us-east-1

# Testar função
aws lambda invoke \
  --function-name avaliacao-function \
  --payload '{
    "version": "2.0",
    "routeKey": "POST /avaliacao",
    "rawPath": "/avaliacao",
    "body": "{\"descricao\":\"Teste\",\"nota\":7}",
    "headers": {"content-type": "application/json"}
  }' \
  response.json \
  --region us-east-1

# Ver resposta
cat response.json
```

## Monitoramento

### CloudWatch Logs

```bash
# Ver logs da função
aws logs tail /aws/lambda/avaliacao-function --follow --region us-east-1

# Buscar erros
aws logs filter-log-events \
  --log-group-name /aws/lambda/avaliacao-function \
  --filter-pattern "ERROR" \
  --region us-east-1
```

### CloudWatch Metrics

Acessar AWS Console → CloudWatch → Metrics → Lambda

### X-Ray (Distributed Tracing)

Habilitar em `template.yaml`:

```yaml
AvaliacaoFunction:
  Type: AWS::Serverless::Function
  Properties:
    TracingConfig:
      Mode: Active
```

## Troubleshooting

### Erro: "Task timed out"
- Aumentar `Timeout` em `template.yaml`
- Otimizar código ou considerar cold start optimization

### Erro: "Permission Denied"
- Verificar IAM role da função
- Adicionar policies necessárias para acessar outros serviços (DynamoDB, S3, etc.)

### Erro: "OutOfMemory"
- Aumentar `MemorySize` em `template.yaml`

### Erro: "Cannot find class"
- Verificar se o JAR foi built corretamente
- Confirmar handler path está correto

### Cold Start Lento
- Usar x86_64 (mais rápido que arm64)
- Considerar usar nível de concorrência reservada
- Otimizar inicialização de beans/singletons

## Próximas Etapas Recomendadas

1. **Persistência**: Integrar com DynamoDB ou RDS
2. **Segurança**: Adicionar autenticação (API Key, OAuth, AWS IAM)
3. **CI/CD**: GitHub Actions ou AWS CodePipeline
4. **Infrastructure**: Usar Terraform ou CDK para IaC
5. **Observabilidade**: Configurar X-Ray, estruturar logs, definir alertas

## Custos Estimados (AWS Free Tier)

- **Lambda**: 1 milhão de requisições grátis/mês
- **API Gateway**: 1 milhão de requisições grátis/mês
- **CloudWatch Logs**: 5GB grátis/mês

Após free tier, preço aproximado:
- Lambda: $0.20 por 1M requisições + $0.0000166667 por GB-segundo
- API Gateway: $3.50 por 1M requisições

## Referências Úteis

- [AWS Lambda Console](https://console.aws.amazon.com/lambda)
- [AWS SAM CLI Guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/)
- [Java Runtime Guide](https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html)
- [API Gateway Integration](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html)

## Suporte

Dúvidas? Consultar:
- Documentação: https://docs.aws.amazon.com/lambda/
- Stack Overflow: tag `amazon-lambda`
- AWS Support (com conta AWS)
