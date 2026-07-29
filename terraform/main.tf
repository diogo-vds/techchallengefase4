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

terraform {

  required_version = ">= 1.5.0"

  required_providers {

    aws = {

      source  = "hashicorp/aws"

      version = "~> 5.0"

    }

  }

}

provider "aws" {

  region = var.aws_region

}