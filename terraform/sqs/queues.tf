#############################################
# Fila de Notificações
#############################################

resource "aws_sqs_queue" "notificacao" {

  name = local.fila_notificacao

}

#############################################
# Fila de Relatórios
#############################################

resource "aws_sqs_queue" "relatorios" {

  name = local.fila_relatorios

}

#############################################
# SNS -> Fila Notificação
#############################################

resource "aws_sns_topic_subscription" "notificacao" {

  topic_arn = aws_sns_topic.avaliacoes.arn

  protocol = "sqs"

  endpoint = aws_sqs_queue.notificacao.arn

}

#############################################
# SNS -> Fila Relatórios
#############################################

resource "aws_sns_topic_subscription" "relatorios" {

  topic_arn = aws_sns_topic.avaliacoes.arn

  protocol = "sqs"

  endpoint = aws_sqs_queue.relatorios.arn

}

resource "aws_sqs_queue_policy" "notificacao" {

  queue_url = aws_sqs_queue.notificacao.id

  policy = jsonencode({

    Version = "2012-10-17"

    Statement = [

      {

        Effect = "Allow"

        Principal = "*"

        Action = "sqs:SendMessage"

        Resource = aws_sqs_queue.notificacao.arn

        Condition = {

          ArnEquals = {

            "aws:SourceArn" = aws_sns_topic.avaliacoes.arn

          }

        }

      }

    ]

  })

}

resource "aws_sqs_queue_policy" "relatorios" {

  queue_url = aws_sqs_queue.relatorios.id

  policy = jsonencode({

    Version = "2012-10-17"

    Statement = [

      {

        Effect = "Allow"

        Principal = "*"

        Action = "sqs:SendMessage"

        Resource = aws_sqs_queue.relatorios.arn

        Condition = {

          ArnEquals = {

            "aws:SourceArn" = aws_sns_topic.avaliacoes.arn

          }

        }

      }

    ]

  })

}