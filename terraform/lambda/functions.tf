resource "aws_lambda_function" "notificacao" {

  function_name = local.lambda_notificacao

  filename         = "../lambda/notificacao.zip"
  source_code_hash = filebase64sha256("../lambda/notificacao.zip")

  role = aws_iam_role.lambda_role.arn

  handler = "lambda_function.lambda_handler"

  runtime = "python3.13"

  timeout = 30

  memory_size = 256

}

resource "aws_lambda_function" "relatorios" {

  function_name = local.lambda_relatorios

  filename         = "../lambda/relatorios.zip"
  source_code_hash = filebase64sha256("../lambda/relatorios.zip")

  role = aws_iam_role.lambda_role.arn

  handler = "lambda_function.lambda_handler"

  runtime = "python3.13"

  timeout = 30

  memory_size = 256

}

resource "aws_lambda_function" "gerar_relatorios" {

  function_name = local.lambda_gerar_relatorios

  filename         = "../lambda/gerar-relatorios.zip"
  source_code_hash = filebase64sha256("../lambda/gerar-relatorios.zip")

  role = aws_iam_role.lambda_role.arn

  handler = "lambda_function.lambda_handler"

  runtime = "python3.13"

  timeout = 30

  memory_size = 256

}