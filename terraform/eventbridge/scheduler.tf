resource "aws_eventbridge_scheduler_schedule" "daily_task" {
  name        = "${var.project_name}-daily-task"
  description = "Executa a task ECS diariamente às 02:00"

  flexible_time_window {
    mode = "OFF"
  }

  schedule_expression = "cron(0 2 * * ? *)"

  target {
    arn      = aws_ecs_cluster.this.arn
    role_arn = aws_iam_role.ecs_task_execution_role.arn
    input    = jsonencode({
      taskDefinitionArn = aws_ecs_task_definition.api_task.arn,
      launchType        = "FARGATE",
      networkConfiguration = {
        awsvpcConfiguration = {
          subnets         = var.private_subnets,
          securityGroups  = [var.api_security_group],
          assignPublicIp  = "DISABLED"
        }
      }
    })
  }
}