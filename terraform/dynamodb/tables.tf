#############################################
# Tabela de Avaliações
#############################################

resource "aws_dynamodb_table" "avaliacoes" {

  name         = local.tabela_avaliacoes
  billing_mode = "PAY_PER_REQUEST"

  hash_key = "id"

  attribute {
    name = "id"
    type = "S"
  }

  tags = {
    Projeto = "TechChallenge"
  }
}

#############################################
# Tabela de relatorio para listagem
#############################################

resource "aws_dynamodb_table" "relatorio" {

  name         = local.tabela_relatorio
  billing_mode = "PAY_PER_REQUEST"

  hash_key = "id"

  attribute {
    name = "id"
    type = "S"
  }

  tags = {
    Projeto = "TechChallenge"
  }
}

#############################################
# Tabela de Relatórios consolidados
#############################################

resource "aws_dynamodb_table" "relatorios" {

  name         = local.tabela_relatorios
  billing_mode = "PAY_PER_REQUEST"

  hash_key = "data"

  attribute {
    name = "data"
    type = "S"
  }

  tags = {
    Projeto = "TechChallenge"
  }
}