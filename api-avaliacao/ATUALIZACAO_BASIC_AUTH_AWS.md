# Atualização da API-Avaliação com Basic Authentication

Este documento registra as mudanças realizadas para autenticar estudantes na
API e descreve como atualizar a stack existente na AWS.

## 1. Objetivo

A API passou a permitir o cadastro de estudantes e a exigir autenticação HTTP
Basic no envio de avaliações.

O fluxo implementado é:

```text
POST /usuarios
    -> valida os dados
    -> aplica hash BCrypt à senha
    -> grava o estudante na tabela usuarios

POST /avaliacao + Authorization: Basic
    -> autentica o usuário na tabela usuarios
    -> exige o perfil ESTUDANTE
    -> vincula a avaliação ao estudante
    -> grava a avaliação
    -> publica AVALIACAO_RECEBIDA na SQS
```

Basic Authentication não usa JWT. O cliente envia e-mail e senha em todas as
requisições protegidas. Por isso, na AWS, a API deve ser acessada somente pelo
endpoint HTTPS fornecido pelo API Gateway.

## 2. Mudanças realizadas

### Código Java

Foram adicionados:

- modelo `Usuario`;
- enum `Perfil`, com `ESTUDANTE` e `ADMINISTRADOR`;
- contrato `UsuarioRequest`;
- `UsuarioRepository` e implementação em DynamoDB;
- `UsuarioService`, responsável pelo cadastro e hash BCrypt;
- `AuthenticationService`, responsável pela leitura do header Basic,
  autenticação e autorização por perfil;
- `UsuarioFunction`, handler do endpoint `POST /usuarios`;
- testes de cadastro, autenticação, senha inválida e perfil sem permissão.

A `AvaliacaoFunction` agora:

- exige o header `Authorization`;
- retorna `401 Unauthorized` quando as credenciais não são apresentadas ou são
  inválidas;
- retorna `403 Forbidden` quando o usuário autenticado não é estudante;
- obtém a identidade do estudante das credenciais, não do corpo da requisição.

A avaliação passou a armazenar:

- `estudanteId`;
- `estudanteEmail`.

O evento `AVALIACAO_RECEBIDA` enviado à SQS passou a conter:

```json
{
  "avaliacaoId": "uuid-da-avaliacao",
  "estudanteId": "uuid-do-estudante",
  "evento": "AVALIACAO_RECEBIDA"
}
```

### Segurança

- A senha não é persistida em texto puro.
- O hash é gerado com BCrypt.
- O cadastro público permite somente o perfil `ESTUDANTE`.
- O endpoint público não permite criar administradores.
- A comparação de senha é feita pelo BCrypt.

Administradores deverão ser provisionados por carga inicial ou por um futuro
endpoint administrativo protegido.

### Infraestrutura

O `template.yaml` passou a criar:

- Lambda `usuario-function`;
- rota `POST /usuarios`;
- tabela DynamoDB `usuarios`;
- permissões IAM para a Lambda de usuários acessar a tabela `usuarios`;
- permissão de leitura da tabela `usuarios` para a Lambda de avaliações;
- variável de ambiente `USUARIOS_TABLE`.

A tabela utiliza:

```text
Nome: usuarios
Partition key: email
Tipo da chave: String
Billing mode: PAY_PER_REQUEST
```

O ambiente LocalStack e o arquivo `env.local.json` também foram atualizados.

## 3. Impacto no contrato da API

### Novo endpoint de cadastro

```http
POST /usuarios
Content-Type: application/json
```

```json
{
  "nome": "Maria",
  "email": "maria@example.com",
  "senha": "senha123",
  "perfil": "ESTUDANTE"
}
```

Resposta esperada: `201 Created`.

```json
{
  "id": "uuid-do-estudante",
  "nome": "Maria",
  "email": "maria@example.com",
  "perfil": "ESTUDANTE"
}
```

### Endpoint de avaliação

O corpo de `POST /avaliacao` não mudou:

```json
{
  "descricao": "Atendimento demorado",
  "nota": 3
}
```

Entretanto, o endpoint agora exige:

```http
Authorization: Basic base64(email:senha)
```

Essa é uma mudança incompatível para clientes antigos. Chamadas sem
autenticação passarão a receber `401 Unauthorized`.

### Códigos de resposta relevantes

| Código | Situação |
|---|---|
| `201` | Estudante cadastrado |
| `202` | Avaliação recebida |
| `400` | JSON ou dados inválidos |
| `401` | Credenciais ausentes ou inválidas |
| `403` | Usuário autenticado sem o perfil necessário |
| `409` | E-mail já cadastrado |
| `500` | Erro interno |

## 4. Pré-requisitos para atualização

Na máquina responsável pelo deploy:

- Java 21;
- Maven;
- AWS CLI;
- AWS SAM CLI;
- credenciais AWS configuradas;
- permissão para atualizar CloudFormation, Lambda, API Gateway, DynamoDB, IAM,
  SQS, S3 e CloudWatch Logs.

Validar as ferramentas:

```powershell
java -version
mvn -version
aws --version
sam --version
aws sts get-caller-identity
```

Os exemplos seguintes consideram:

```text
Stack: techchallenge-avaliacao
Região: us-east-1
```

Altere esses valores se a stack atual usar outro nome ou região.

## 5. Verificar a stack antes da atualização

Confirme que a stack existe:

```powershell
aws cloudformation describe-stacks `
  --stack-name techchallenge-avaliacao `
  --region us-east-1
```

Confirme também que não existe uma tabela externa chamada `usuarios` na mesma
conta e região:

```powershell
aws dynamodb describe-table `
  --table-name usuarios `
  --region us-east-1
```

Se o comando encontrar uma tabela que não pertence à stack, o deploy falhará
por conflito de nome. Nesse caso, a tabela deve ser importada para a stack ou o
nome do recurso deve ser alterado antes do deploy.

## 6. Compilar e validar

Na raiz do projeto:

```powershell
mvn clean verify
sam validate --lint
sam build
```

Resultados esperados:

- Maven finaliza com `BUILD SUCCESS`;
- todos os testes passam;
- SAM informa que `template.yaml` é válido;
- os artefatos são gerados em `.aws-sam/build`.

Sempre execute `sam build` depois de alterar Java, `pom.xml` ou
`template.yaml`.

## 7. Visualizar as alterações da stack

Se já existir um `samconfig.toml` configurado para a stack:

```powershell
sam deploy --no-execute-changeset
```

O SAM criará um change set sem executá-lo. Revise no console do CloudFormation
ou com a AWS CLI antes de aplicar.

Caso seja necessário refazer a configuração:

```powershell
sam deploy --guided --no-execute-changeset
```

Valores recomendados:

```text
Stack Name: techchallenge-avaliacao
AWS Region: us-east-1
Parameter DynamoDbEndpoint: [vazio]
Parameter SqsEndpoint: [vazio]
Allow SAM CLI IAM role creation: Y
Disable rollback: N
Save arguments to configuration file: Y
```

Os endpoints de DynamoDB e SQS devem permanecer vazios na AWS. Eles são usados
somente para direcionar o ambiente local ao LocalStack.

Para listar os change sets:

```powershell
aws cloudformation list-change-sets `
  --stack-name techchallenge-avaliacao `
  --region us-east-1
```

## 8. Executar a atualização

Depois de revisar as alterações:

```powershell
sam deploy
```

Se não houver configuração salva:

```powershell
sam deploy --guided
```

Durante o deploy, o CloudFormation deverá:

1. criar a tabela `usuarios`;
2. criar a função `usuario-function`;
3. adicionar a rota `POST /usuarios`;
4. atualizar variáveis de ambiente e políticas IAM;
5. publicar a nova versão do código da `avaliacao-function`.

Aguarde a stack chegar a:

```text
UPDATE_COMPLETE
```

Consultar o status:

```powershell
aws cloudformation describe-stacks `
  --stack-name techchallenge-avaliacao `
  --region us-east-1 `
  --query "Stacks[0].StackStatus" `
  --output text
```

Se ocorrer uma falha:

```powershell
aws cloudformation describe-stack-events `
  --stack-name techchallenge-avaliacao `
  --region us-east-1 `
  --max-items 20
```

## 9. Obter o endpoint

```powershell
$apiEndpoint = aws cloudformation describe-stacks `
  --stack-name techchallenge-avaliacao `
  --region us-east-1 `
  --query "Stacks[0].Outputs[?OutputKey=='ApiEndpoint'].OutputValue" `
  --output text

$apiEndpoint
```

Formato esperado:

```text
https://xxxxxxxxxx.execute-api.us-east-1.amazonaws.com/dev/
```

## 10. Testes pós-deploy

### 10.1 Cadastrar um estudante

```powershell
$usuario = @{
  nome = "Maria"
  email = "maria@example.com"
  senha = "senha123"
  perfil = "ESTUDANTE"
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri "${apiEndpoint}usuarios" `
  -ContentType "application/json" `
  -Body $usuario
```

Resultado esperado: HTTP `201`.

### 10.2 Enviar uma avaliação autenticada

```powershell
$credenciais = [Convert]::ToBase64String(
  [Text.Encoding]::UTF8.GetBytes("maria@example.com:senha123")
)

$headers = @{
  Authorization = "Basic $credenciais"
}

$avaliacao = @{
  descricao = "Atendimento demorado"
  nota = 3
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri "${apiEndpoint}avaliacao" `
  -Headers $headers `
  -ContentType "application/json" `
  -Body $avaliacao
```

Resultado esperado: HTTP `202`.

### 10.3 Confirmar que autenticação é obrigatória

```powershell
Invoke-WebRequest `
  -Method Post `
  -Uri "${apiEndpoint}avaliacao" `
  -ContentType "application/json" `
  -Body $avaliacao `
  -SkipHttpErrorCheck
```

Resultado esperado: HTTP `401`.

### 10.4 Conferir o usuário no DynamoDB

```powershell
aws dynamodb get-item `
  --table-name usuarios `
  --key '{"email":{"S":"maria@example.com"}}' `
  --region us-east-1
```

O item deve conter `senhaHash`. Não deve existir um atributo com a senha em
texto puro.

### 10.5 Conferir a avaliação

```powershell
aws dynamodb scan `
  --table-name avaliacoes `
  --region us-east-1
```

As novas avaliações devem conter `estudanteId` e `estudanteEmail`.
Avaliações criadas antes da atualização não terão esses atributos.

### 10.6 Conferir a mensagem SQS

```powershell
$queueUrl = aws sqs get-queue-url `
  --queue-name avaliacoes `
  --region us-east-1 `
  --query QueueUrl `
  --output text

aws sqs receive-message `
  --queue-url $queueUrl `
  --region us-east-1
```

A mensagem deve conter `avaliacaoId`, `estudanteId` e
`AVALIACAO_RECEBIDA`.

Não exclua manualmente a mensagem se o consumidor oficial estiver ativo.

## 11. Logs e diagnóstico

Logs da Lambda de cadastro:

```powershell
aws logs tail /aws/lambda/usuario-function `
  --follow `
  --region us-east-1
```

Logs da Lambda de avaliações:

```powershell
aws logs tail /aws/lambda/avaliacao-function `
  --follow `
  --region us-east-1
```

Erros comuns:

| Erro | Verificação |
|---|---|
| `401` | Header Basic, e-mail e senha |
| `403` | Perfil do usuário no DynamoDB |
| `409` | E-mail já cadastrado |
| `Resource already exists` | Existência prévia da tabela `usuarios` |
| `AccessDeniedException` | Políticas IAM geradas pela stack |
| `USUARIOS_TABLE` ausente | Variáveis de ambiente da Lambda |

## 12. Rollback

O CloudFormation executa rollback automaticamente se a atualização falhar,
desde que `Disable rollback` permaneça como `N`.

Antes de reverter manualmente uma atualização já concluída:

1. faça backup da tabela `usuarios`;
2. confirme se existem estudantes cadastrados;
3. lembre que a versão anterior de `POST /avaliacao` não reconhece os novos
   campos de identidade;
4. coordene a reversão com os clientes que já passaram a enviar Basic Auth.

Como `usuarios` é um recurso novo da stack, a remoção ou reversão do template
pode excluir a tabela e seus dados. Para um ambiente que preserve dados reais,
adicione políticas de retenção e backup ao recurso antes de qualquer operação
de remoção.

## 13. Checklist

- [ ] Identidade e conta AWS conferidas.
- [ ] Nome e região da stack conferidos.
- [ ] Conflito com uma tabela `usuarios` existente descartado.
- [ ] `mvn clean verify` executado com sucesso.
- [ ] `sam validate --lint` executado com sucesso.
- [ ] `sam build` executado.
- [ ] Change set revisado.
- [ ] Stack em `UPDATE_COMPLETE`.
- [ ] Cadastro de estudante retornando `201`.
- [ ] Avaliação autenticada retornando `202`.
- [ ] Avaliação sem credenciais retornando `401`.
- [ ] Senha armazenada somente como BCrypt.
- [ ] Avaliação vinculada ao estudante.
- [ ] Evento SQS contendo `estudanteId`.
