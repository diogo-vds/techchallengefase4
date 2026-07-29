#############################################
# Repositório API Avaliações
#############################################

resource "aws_ecr_repository" "api_avaliacoes" {

  name = "api-avaliacoes"

  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Projeto = "TechChallenge"
  }
}

#############################################
# Repositório API Relatórios
#############################################

resource "aws_ecr_repository" "api_relatorios" {

  name = "api-relatorios"

  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Projeto = "TechChallenge"
  }
}