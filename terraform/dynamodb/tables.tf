resource "aws_dynamodb_table" "relatorios" {
  name           = "${var.project_name}-relatorio"
  billing_mode   = "PAY_PER_REQUEST"
  hash_key       = "data"

  attribute {
    name = "data"
    type = "S"
  }

  tags = {
    Project = var.project_name
    Service = "relatorio"
  }
}

resource "aws_dynamodb_table" "avaliacoes" {
  name           = "${var.project_name}-avaliacao"
  billing_mode   = "PAY_PER_REQUEST"
  hash_key       = "id"

  attribute {
    name = "id"
    type = "S"
  }

  tags = {
    Project = var.project_name
    Service = "avaliacao"
  }
}
