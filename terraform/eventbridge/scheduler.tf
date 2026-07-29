#############################################
# Agendamento semanal
#############################################

resource "aws_scheduler_schedule" "relatorio_semanal" {

  name = "gerar-relatorio-semanal"

  flexible_time_window {
    mode = "OFF"
  }

  schedule_expression = "cron(0 0 ? * MON *)"

  target {

    arn = aws_lambda_function.gerar_relatorios.arn

    role_arn = aws_iam_role.scheduler_role.arn

  }

}