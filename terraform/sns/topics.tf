resource "aws_sns_topic" "topic-avaliacoes" {
  name = "${var.project_name}-avaliacao-topic"

  tags = {
    Project = var.project_name
    Service = "topic-avaliacoes"
  }
}