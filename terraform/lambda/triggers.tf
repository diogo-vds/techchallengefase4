resource "aws_lambda_event_source_mapping" "notificacao" {

  event_source_arn = aws_sqs_queue.notificacao.arn

  function_name = aws_lambda_function.notificacao.arn

  batch_size = 1

}

resource "aws_lambda_event_source_mapping" "relatorios" {

  event_source_arn = aws_sqs_queue.relatorios.arn

  function_name = aws_lambda_function.relatorios.arn

  batch_size = 1

}