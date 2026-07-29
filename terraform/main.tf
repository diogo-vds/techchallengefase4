module "ecr" {
  source = "./ecr"
}

module "dynamodb" {
  source = "./dynamodb"
}

module "sns" {
  source = "./sns"
}

module "sqs" {
  source = "./sqs"
}

module "ecs" {
  source = "./ecs"
}

module "iam" {
  source = "./iam"
}

module "eventbridge" {
  source = "./eventbridge"
}