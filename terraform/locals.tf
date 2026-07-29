locals {

  cluster_name = "techchallenge-cluster"

  ###################################
  # ECS
  ###################################

  api_avaliacoes_service = "api-avaliacoes-service"

  api_relatorios_service = "api-relatorios-service"

  ###################################
  # ECR
  ###################################

  api_avaliacoes_repo = "api-avaliacoes"

  api_relatorios_repo = "api-relatorios"

  ###################################
  # DynamoDB
  ###################################

  tabela_avaliacoes = "avaliacoes"

  tabela_relatorios = "relatorios"

  ###################################
  # Lambda
  ###################################

  lambda_notificacao = "lambda-notificacao"

  lambda_relatorios = "lambda-relatorios"

  lambda_gerar_relatorios = "lambda-gerar-relatorios"

  ###################################
  # SNS
  ###################################

  topic_avaliacoes = "topic-avaliacoes"

  ###################################
  # SQS
  ###################################

  fila_notificacao = "fila-notificacao"

  fila_relatorios = "fila-relatorios"

}