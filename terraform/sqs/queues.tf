resource "aws_sqs_queue" "fila-relatorios" {
  name                      = "${var.project_name}-fila-relatorios"
  visibility_timeout_seconds = 30
  message_retention_seconds  = 86400

  tags = {
    Project = var.project_name
    Service = "relatorio"
  }
}

resource "aws_sqs_queue" "fila-notificacoes" {
  name                      = "${var.project_name}-fila-notificacoes"
  visibility_timeout_seconds = 30
  message_retention_seconds  = 86400

  tags = {
    Project = var.project_name
    Service = "notificacao"
  }
}

resource "aws_sns_topic_subscription" "relatorio_subscription" {
  topic_arn = arn:aws:sns:us-east-1:303956760468:topic-avaliacoes
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.relatorio_queue.arn
}

resource "aws_sns_topic_subscription" "avaliacao_subscription" {
  topic_arn = arn:aws:sns:us-east-1:303956760468:topic-avaliacoes
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.avaliacao_queue.arn
}