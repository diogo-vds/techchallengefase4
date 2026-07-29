#############################################
# Role utilizada pelas funções Lambda
#############################################

resource "aws_iam_role" "lambda_role" {

  name = "lambda-techchallenge-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"

        Principal = {
          Service = "lambda.amazonaws.com"
        }

        Action = "sts:AssumeRole"
      }
    ]
  })

}

#############################################
# Permissões básicas da Lambda
#############################################

resource "aws_iam_role_policy_attachment" "lambda_logs" {

  role       = aws_iam_role.lambda_role.name

  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"

}

#############################################
# DynamoDB
#############################################

resource "aws_iam_role_policy_attachment" "lambda_dynamodb" {

  role       = aws_iam_role.lambda_role.name

  policy_arn = "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"

}

#############################################
# SQS
#############################################

resource "aws_iam_role_policy_attachment" "lambda_sqs" {

  role       = aws_iam_role.lambda_role.name

  policy_arn = "arn:aws:iam::aws:policy/AmazonSQSFullAccess"

}

#############################################
# SNS
#############################################

resource "aws_iam_role_policy_attachment" "lambda_sns" {

  role       = aws_iam_role.lambda_role.name

  policy_arn = "arn:aws:iam::aws:policy/AmazonSNSFullAccess"

}

#############################################
# SES
#############################################

resource "aws_iam_role_policy_attachment" "lambda_ses" {

  role       = aws_iam_role.lambda_role.name

  policy_arn = "arn:aws:iam::aws:policy/AmazonSESFullAccess"

}

#############################################
# ECS Task Execution Role
#############################################

resource "aws_iam_role" "ecs_task_execution_role" {

  name = "ecsTaskExecutionRole"

  assume_role_policy = jsonencode({

    Version = "2012-10-17"

    Statement = [

      {

        Effect = "Allow"

        Principal = {

          Service = "ecs-tasks.amazonaws.com"

        }

        Action = "sts:AssumeRole"

      }

    ]

  })

}

resource "aws_iam_role_policy_attachment" "ecs_execution_policy" {

  role = aws_iam_role.ecs_task_execution_role.name

  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"

}

resource "aws_iam_role" "scheduler_role" {

  name = "eventbridge-scheduler-role"

  assume_role_policy = jsonencode({

    Version = "2012-10-17"

    Statement = [

      {

        Effect = "Allow"

        Principal = {

          Service = "scheduler.amazonaws.com"

        }

        Action = "sts:AssumeRole"

      }

    ]

  })

}

resource "aws_iam_role_policy" "scheduler_policy" {

  name = "scheduler-policy"

  role = aws_iam_role.scheduler_role.id

  policy = jsonencode({

    Version = "2012-10-17"

    Statement = [

      {

        Effect = "Allow"

        Action = [
          "lambda:InvokeFunction"
        ]

        Resource = "*"

      }

    ]

  })

}

resource "aws_iam_role" "lambda_role" {

  name = "lambda-techchallenge-role"

  assume_role_policy = jsonencode({

    Version = "2012-10-17"

    Statement = [

      {
        Effect = "Allow"

        Principal = {
          Service = "lambda.amazonaws.com"
        }

        Action = "sts:AssumeRole"
      }

    ]

  })

}

resource "aws_iam_role_policy_attachment" "lambda_logs" {

  role       = aws_iam_role.lambda_role.name

  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"

}

resource "aws_iam_role_policy_attachment" "lambda_dynamodb" {

  role       = aws_iam_role.lambda_role.name

  policy_arn = "arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess"

}

resource "aws_iam_role_policy_attachment" "lambda_sqs" {

  role       = aws_iam_role.lambda_role.name

  policy_arn = "arn:aws:iam::aws:policy/AmazonSQSFullAccess"

}

resource "aws_iam_role_policy_attachment" "lambda_sns" {

  role       = aws_iam_role.lambda_role.name

  policy_arn = "arn:aws:iam::aws:policy/AmazonSNSFullAccess"

}

resource "aws_iam_role_policy_attachment" "lambda_ses" {

  role       = aws_iam_role.lambda_role.name

  policy_arn = "arn:aws:iam::aws:policy/AmazonSESFullAccess"

}

resource "aws_iam_role" "ecs_execution_role" {

  name = "ecsTaskExecutionRole"

  assume_role_policy = jsonencode({

    Version = "2012-10-17"

    Statement = [

      {

        Effect = "Allow"

        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }

        Action = "sts:AssumeRole"

      }

    ]

  })

}

resource "aws_iam_role_policy_attachment" "ecs_execution" {

  role = aws_iam_role.ecs_execution_role.name

  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"

}

resource "aws_iam_role" "scheduler_role" {

  name = "eventbridge-scheduler-role"

  assume_role_policy = jsonencode({

    Version = "2012-10-17"

    Statement = [

      {

        Effect = "Allow"

        Principal = {

          Service = "scheduler.amazonaws.com"

        }

        Action = "sts:AssumeRole"

      }

    ]

  })

}

resource "aws_iam_policy" "invoke_lambda" {

  name = "invoke-gerar-relatorios"

  policy = jsonencode({

    Version = "2012-10-17"

    Statement = [

      {

        Effect = "Allow"

        Action = [
          "lambda:InvokeFunction"
        ]

        Resource = aws_lambda_function.gerar_relatorios.arn

      }

    ]

  })

}

resource "aws_iam_role_policy_attachment" "scheduler_policy" {

  role = aws_iam_role.scheduler_role.name

  policy_arn = aws_iam_policy.invoke_lambda.arn

}