resource "aws_ecs_task_definition" "api_avaliacoes" {

  family                   = "api-avaliacoes-task"
  requires_compatibilities = ["FARGATE"]

  network_mode = "awsvpc"

  cpu    = 256
  memory = 512

  execution_role_arn = aws_iam_role.ecs_execution_role.arn

  container_definitions = jsonencode([

    {

      name = "api-avaliacoes"

      image = "${aws_ecr_repository.api_avaliacoes.repository_url}:latest"

      essential = true

      portMappings = [

        {
          containerPort = 8080
          protocol      = "tcp"
        }

      ]

    }

  ])

}

resource "aws_ecs_task_definition" "api_relatorios" {

  family                   = "api-relatorios-task"

  requires_compatibilities = ["FARGATE"]

  network_mode = "awsvpc"

  cpu    = 256
  memory = 512

  execution_role_arn = aws_iam_role.ecs_execution_role.arn

  container_definitions = jsonencode([

    {

      name = "api-relatorios"

      image = "${aws_ecr_repository.api_relatorios.repository_url}:latest"

      essential = true

      portMappings = [

        {
          containerPort = 8081
          protocol      = "tcp"
        }

      ]

    }

  ])

}