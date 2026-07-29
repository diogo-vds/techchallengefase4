resource "aws_ecr_repository" "api_avaliacoes" {
  name = "api-avaliacoes"

  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_repository" "api_relatorios" {
  name = "api-relatorios"

  image_scanning_configuration {
    scan_on_push = true
  }
}