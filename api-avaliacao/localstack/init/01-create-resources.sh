#!/bin/bash
set -euo pipefail

awslocal dynamodb create-table \
  --table-name avaliacoes \
  --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST 2>/dev/null || true

awslocal sqs create-queue --queue-name avaliacoes-dlq >/dev/null
DLQ_URL=$(awslocal sqs get-queue-url \
  --queue-name avaliacoes-dlq --query 'QueueUrl' --output text)
DLQ_ARN=$(awslocal sqs get-queue-attributes \
  --queue-url "${DLQ_URL}" \
  --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)

awslocal sqs create-queue \
  --queue-name avaliacoes \
  --attributes "{\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"${DLQ_ARN}\\\",\\\"maxReceiveCount\\\":\\\"3\\\"}\"}" >/dev/null
