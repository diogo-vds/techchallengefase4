resource "aws_iam_role" "ecs_task_role" {
  name = "${var.project_name}-ecsTaskRole"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

# Permissões para DynamoDB
resource "aws_iam_role_policy" "ecs_task_dynamodb_policy" {
  name   = "${var.project_name}-ecsTaskDynamoDBPolicy"
  role   = aws_iam_role.ecs_task_role.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = [
          "dynamodb:PutItem",
          "dynamodb:GetItem",
          "dynamodb:UpdateItem",
          "dynamodb:DeleteItem",
          "dynamodb:Scan",
          "dynamodb:Query"
        ],
        Resource = [
          aws_dynamodb_table.relatorio_table.arn,
          aws_dynamodb_table.avaliacao_table.arn
        ]
      }
    ]
  })
}

# Permissões para SNS e SQS
resource "aws_iam_role_policy" "ecs_task_sns_sqs_policy" {
  name   = "${var.project_name}-ecsTaskSNSSQSPolicy"
  role   = aws_iam_role.ecs_task_role.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = [
          "sns:Publish",
          "sqs:SendMessage",
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:GetQueueAttributes"
        ],
        Resource = [
          aws_sns_topic.relatorio_topic.arn,
          aws_sns_topic.avaliacao_topic.arn,
          aws_sqs_queue.relatorio_queue.arn,
          aws_sqs_queue.avaliacao_queue.arn
        ]
      }
    ]
  })
}