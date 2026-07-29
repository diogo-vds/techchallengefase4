resource "aws_lambda_permission" "eventbridge" {

  statement_id = "AllowExecutionFromScheduler"

  action = "lambda:InvokeFunction"

  function_name = aws_lambda_function.gerar_relatorios.function_name

  principal = "scheduler.amazonaws.com"

}

resource "aws_lambda_permission" "allow_scheduler" {

  statement_id = "AllowScheduler"

  action = "lambda:InvokeFunction"

  function_name = aws_lambda_function.gerar_relatorios.function_name

  principal = "scheduler.amazonaws.com"

  source_arn = aws_scheduler_schedule.relatorio_semanal.arn

}