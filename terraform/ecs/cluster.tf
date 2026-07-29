resource "aws_ecs_cluster" "cluster" {

  name = "techchallenge-cluster"

  tags = {
    Projeto = "TechChallenge"
  }

}