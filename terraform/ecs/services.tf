resource "aws_ecs_service" "api_avaliacoes" {

  name = "api-avaliacoes-service"

  cluster = aws_ecs_cluster.cluster.id

  task_definition = aws_ecs_task_definition.api_avaliacoes.arn

  desired_count = 1

  launch_type = "FARGATE"

  network_configuration {

    assign_public_ip = true

    subnets = [
      var.subnet_id
    ]

    security_groups = [
      var.security_group
    ]

  }

}

resource "aws_ecs_service" "api_relatorios" {

  name = "api-relatorios-service"

  cluster = aws_ecs_cluster.cluster.id

  task_definition = aws_ecs_task_definition.api_relatorios.arn

  desired_count = 1

  launch_type = "FARGATE"

  network_configuration {

    assign_public_ip = true

    subnets = [
      var.subnet_id
    ]

    security_groups = [
      var.security_group
    ]

  }

}