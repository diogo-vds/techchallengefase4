# Tech Challenge - Plataforma de Feedback Acadêmico

## 1. Objetivo

Este projeto implementa uma plataforma de coleta de feedbacks de aulas utilizando uma arquitetura baseada em serviços AWS.

A solução permite:

* registrar avaliações dos alunos;
* processar eventos de forma assíncrona;
* notificar administradores quando avaliações críticas são recebidas;
* gerar relatórios diários e mensais automaticamente;
* armazenar os dados em banco NoSQL utilizando DynamoDB.

---

# 2. Arquitetura da solução

                Fluxo de Avaliações
──────────────────────────────────────────────────────────────────────────

                    API Avaliações
                          │
                          ▼
                    DynamoDB (avaliacoes)
                          │
                          ▼
                         SNS
                          │
                          ▼
                         SQS
                   ┌───── ┴ ───────┐
                   ▼               ▼
                Fila Notificação  Fila Relatórios
                   │               │
                   ▼               ▼
                Lambda         Lambda
                Notificação    Relatórios
                                    │
                                    ▼
                          DynamoDB (relatorios)
                          PK = yyyy-MM-dd


Fluxo Agendado
──────────────────────────────────────────────────────────────────────────

EventBridge Scheduler
          │
          ▼
Lambda Gerar Relatórios
          │
          ▼
DynamoDB (relatorios)
PK = yyyy-MM
```

---

## 3. Tecnologias utilizadas

| Tecnologia                   | Utilização                                                |
| ---------------------------- | --------------------------------------------------------- |
| Java 21                      | Desenvolvimento das APIs REST                             |
| Spring Boot                  | Framework para construção das APIs                        |
| Spring Security              | Autenticação e autorização dos endpoints                  |
| Docker                       | Containerização das aplicações                            |
| Amazon ECS Fargate           | Execução das APIs em containers                           |
| Amazon ECR                   | Armazenamento das imagens Docker                          |
| Amazon DynamoDB              | Persistência NoSQL das avaliações e relatórios            |
| Amazon SNS                   | Publicação de eventos                                     |
| Amazon SQS                   | Filas para processamento assíncrono                       |
| AWS Lambda                   | Processamento Serverless                                  |
| Amazon EventBridge Scheduler | Agendamento automático da geração de relatórios           |
| Amazon CloudWatch Logs       | Centralização dos logs das APIs e funções Lambda          |
| Amazon CloudWatch Alarms     | Monitoramento de erros das funções Serverless             |
| Amazon CloudWatch Dashboard  | Visualização consolidada de métricas da solução           |
| AWS IAM                      | Gerenciamento de permissões e segurança                   |
| GitHub Actions               | Pipeline de Integração Contínua e Deploy Contínuo (CI/CD) |
| Terraform                    | Infraestrutura como Código (IaC)                          |


---

# Componentes da solução

API Avaliações

Responsável por:

receber avaliações dos alunos;
validar os dados recebidos;
persistir as avaliações no DynamoDB;
publicar um evento no Amazon SNS.

A API é destinada aos alunos e seus endpoints são protegidos utilizando Spring Security, permitindo acesso apenas mediante autenticação.

Endpoint:

```
POST /avaliacoes
```

Exemplo:

```json
{
    "descricao":"Professor explicou muito bem",
    "nota":5
}
```

---

## Lambda Notificação

Função Serverless responsável por:

* consumir mensagens da fila de notificações;
* identificar avaliações críticas;
* simular o envio de e-mails aos administradores;
* registrar toda execução no CloudWatch.

---

## Lambda Relatórios

Responsável por:

* consumir mensagens da fila de relatórios;
* consolidar as avaliações recebidas;
* armazenar os dados no DynamoDB utilizando a chave do dia.

Exemplo:

```
PK = 2026-07-29
```

---

## Lambda Gerar Relatórios

Executada automaticamente pelo EventBridge Scheduler.

Responsável por:

* consultar os relatórios diários;
* consolidar os dados do mês;
* armazenar o relatório mensal.

Exemplo:

```
PK = 2026-S07
```

---

## API Relatórios

Disponibiliza consultas dos relatórios gerados.

Exemplo:

```
GET /relatorios/semanal/2026-07-28
```

---

# 5. Serviços AWS utilizados

| Serviço               | Finalidade                     |
| --------------------- | ------------------------------ |
| ECS Fargate           | Hospedagem das APIs            |
| ECR                   | Repositório das imagens Docker |
| DynamoDB              | Persistência NoSQL             |
| SNS                   | Publicação de eventos          |
| SQS                   | Filas assíncronas              |
| Lambda                | Processamento Serverless       |
| EventBridge Scheduler | Agendamento                    |
| CloudWatch            | Logs                           |
| IAM                   | Controle de acesso             |

---

# Monitoramento

O monitoramento da solução é realizado através do Amazon CloudWatch.

São registrados:

logs das APIs Spring Boot;
logs das funções Lambda;
execuções agendadas do EventBridge Scheduler;
processamento das filas SQS;
erros durante a execução das funções Serverless.

Além dos logs, foram configurados CloudWatch Alarms para monitoramento de falhas nas funções Lambda:

| Alarme                            | Finalidade                                                                               |
| --------------------------------- | ---------------------------------------------------------------------------------------- |
| **error-lambda-notificacao**      | Monitora erros de execução da função responsável pelas notificações.                     |
| **error-lambda-gerar-relatorios** | Monitora erros de execução da função responsável pela geração automática dos relatórios. |


Esses alarmes permitem identificar rapidamente falhas no processamento assíncrono e facilitam a atuação corretiva durante a operação da solução.

---

# 7. Segurança

A solução utiliza o modelo de segurança baseado em IAM e Spring Security, garantindo proteção tanto na infraestrutura quanto nas APIs.

Segurança da infraestrutura

Foram configuradas permissões específicas utilizando IAM para:

ECS acessar imagens armazenadas no Amazon ECR;
APIs publicarem mensagens no Amazon SNS;
funções Lambda consumirem mensagens das filas SQS;
funções Lambda acessarem as tabelas do DynamoDB;
EventBridge Scheduler executar a Lambda responsável pela geração dos relatórios.
Segurança das APIs

As APIs REST utilizam Spring Security para autenticação e autorização dos usuários.

Foram definidos perfis distintos de acesso:

Alunos: possuem acesso à API de Avaliações, responsável pelo envio dos feedbacks.
Administradores: possuem acesso à API de Relatórios, responsável pela consulta dos relatórios consolidados.

Essa separação garante que apenas usuários autorizados possam acessar informações administrativas da plataforma.

---

# 8. Pipeline CI/CD

Cada API possui um workflow independente no GitHub Actions.

Fluxo do deploy:

```
Push na branch main

↓

GitHub Actions

↓

Build Maven

↓

Build Docker

↓

Push para Amazon ECR

↓

Atualização automática do ECS Service
```

---

# 9. Como executar

## Pré-requisitos

* Java 21
* Maven
* Docker
* AWS CLI
* Terraform

---

## Execução local api-avaliacoes

```
mvn clean package
```

```
docker build -t api-avaliacoes .
```

```
docker run -p 8080:8080 api-avaliacoes
```

## Execução local api-relatorios

```
mvn clean package
```

```
docker build -t api-relatorios .
```

```
docker run -p 8081:8081 api-relatorios

---

# 10. Deploy

O deploy é realizado automaticamente através do GitHub Actions.

A infraestrutura da aplicação pode ser provisionada utilizando Terraform.

---

## Conclusão

A solução implementa uma arquitetura baseada em microsserviços e processamento assíncrono utilizando serviços gerenciados da AWS, explorando conceitos de computação em nuvem, Serverless, mensageria, infraestrutura como código e integração contínua. O uso combinado de ECS Fargate, Lambda, SNS, SQS, DynamoDB e EventBridge permite uma aplicação escalável, desacoplada e de fácil manutenção.

