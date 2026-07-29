# Tech Challenge - Fase 4


# Relatórios Lambda

Lambda responsável por receber mensagens do Amazon SNS e gravar os dados em uma tabela do Amazon DynamoDB.

---

# Arquitetura

SNS (topic-avaliacoes)
        │
        ▼
AWS Lambda
        │
        ▼
 DynamoDB (relatorios)

---

# Requisitos

- AWS CLI
- Python 3.13
- Conta AWS
- IAM com permissões:
    - SNS
    - Lambda
    - DynamoDB

---

# Criando a tabela DynamoDB

Nome:

relatorios

Partition Key:

id (Number)

Via AWS CLI:

```bash
aws dynamodb create-table \
--table-name relatorios \
--attribute-definitions AttributeName=id,AttributeType=N \
--key-schema AttributeName=id,KeyType=HASH \
--billing-mode PAY_PER_REQUEST
```

---

# Criando a Lambda

Runtime

Python 3.13

Handler

```
lambda_function.lambda_handler
```

Variável de ambiente

```
TABLE_NAME=relatorios
```

Permissões IAM

Adicionar:

- AmazonDynamoDBFullAccess

ou

```json
{
  "Effect": "Allow",
  "Action": [
    "dynamodb:PutItem"
  ],
  "Resource": "*"
}
```

---

# Configurando o SNS

Topic

```
topic-avaliacoes
```

ARN

```
arn:aws:sns:us-east-1:303956760468:topic-avaliacoes
```

Criar uma Subscription

Protocol

```
AWS Lambda
```

Endpoint

Selecionar a Lambda criada.

---

# Deploy

Instalar dependências

```bash
pip install -r requirements.txt -t .
```

Compactar

```bash
zip -r lambda.zip .
```

Enviar para AWS

```bash
aws lambda update-function-code \
--function-name relatorios-lambda \
--zip-file fileb://lambda.zip
```

---

# Teste Local

Execute:

```bash
python
```

```python
import json
from lambda_function import lambda_handler

with open("event.json") as f:
    event = json.load(f)

print(lambda_handler(event, None))
```

---

# Publicando uma mensagem no SNS

```bash
aws sns publish \
--topic-arn arn:aws:sns:us-east-1:303956760468:topic-avaliacoes \
--message '{
"id":1,
"descricao":"Produto excelente",
"nota":5,
"urgencia":"URGENTE",
"dataCadastro":"2026-07-20T20:00"
}'
```

---

# Exemplo de registro salvo

```json
{
    "id": 1,
    "descricao": "Produto excelente",
    "nota": 5,
    "urgencia": "URGENTE",
    "dataCadastro": "2026-07-20T20:00"
}
```

---

# Fluxo

1. SNS recebe uma mensagem.
2. A Lambda é acionada.
3. A mensagem é desserializada.
4. Os campos obrigatórios são validados.
5. Os dados são gravados no DynamoDB.
6. É retornado Status Code 200.

---

# Tratamento de erros

- Evento SNS inválido
- JSON inválido
- Campos obrigatórios ausentes
- Erros do DynamoDB
- Exceções inesperadas

Todos os erros são registrados utilizando o módulo logging.
