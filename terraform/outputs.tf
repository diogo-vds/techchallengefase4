output "aws_region" {
  description = "Região onde os recursos foram provisionados"
  value       = var.aws_region
}

output "project_name" {
  description = "Nome base do projeto"
  value       = var.project_name
}

output "ecs_cluster" {

  value = aws_ecs_cluster.cluster.name

}

output "ecr_api_avaliacoes" {

  value = aws_ecr_repository.api_avaliacoes.repository_url

}

output "ecr_api_relatorios" {

  value = aws_ecr_repository.api_relatorios.repository_url

}

output "sns_topic" {

  value = aws_sns_topic.avaliacoes.arn

}

output "fila_notificacao" {

  value = aws_sqs_queue.notificacao.id

}

output "fila_relatorios" {

  value = aws_sqs_queue.relatorios.id

}