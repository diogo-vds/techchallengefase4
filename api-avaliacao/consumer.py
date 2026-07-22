#!/usr/bin/env python3
"""Consumidor SQS de avaliacoes persistidas no DynamoDB."""

import json
import os
import signal

import boto3
from dotenv import load_dotenv


class AvaliacaoConsumer:
    def __init__(self):
        region = os.getenv("AWS_REGION", "us-east-1")
        sqs_endpoint = os.getenv("AWS_ENDPOINT_URL_SQS")
        dynamodb_endpoint = os.getenv("AWS_ENDPOINT_URL_DYNAMODB")

        self.queue_url = os.getenv(
            "SQS_QUEUE_URL",
            "http://localhost:4566/000000000000/avaliacoes",
        )
        self.table_name = os.getenv("DYNAMODB_TABLE", "avaliacoes")
        self.sqs = boto3.client("sqs", region_name=region, endpoint_url=sqs_endpoint)
        self.table = boto3.resource(
            "dynamodb", region_name=region, endpoint_url=dynamodb_endpoint
        ).Table(self.table_name)
        self.running = True

    def processar(self, message):
        event = json.loads(message["Body"])
        avaliacao_id = event["avaliacaoId"]

        response = self.table.get_item(Key={"id": avaliacao_id})
        avaliacao = response.get("Item")
        if not avaliacao:
            raise ValueError(f"Avaliacao {avaliacao_id} nao encontrada")

        print("\nAvaliacao recebida")
        print(f"  ID: {avaliacao['id']}")
        print(f"  Descricao: {avaliacao['descricao']}")
        print(f"  Nota: {avaliacao['nota']}/10")
        print(f"  Urgencia: {avaliacao['urgencia']}")

        self.sqs.delete_message(
            QueueUrl=self.queue_url, ReceiptHandle=message["ReceiptHandle"]
        )
        print("Avaliacao processada com sucesso")

    def iniciar(self):
        print(f"Consumindo SQS: {self.queue_url}")
        print(f"Consultando DynamoDB: {self.table_name}")
        while self.running:
            response = self.sqs.receive_message(
                QueueUrl=self.queue_url,
                MaxNumberOfMessages=1,
                WaitTimeSeconds=20,
                VisibilityTimeout=60,
            )
            for message in response.get("Messages", []):
                try:
                    self.processar(message)
                except Exception as error:
                    # Sem delete: a mensagem retorna a fila e, apos 3 tentativas,
                    # segue para a DLQ configurada.
                    print(f"Erro ao processar avaliacao: {error}")

    def parar(self, *_):
        self.running = False


def main():
    load_dotenv()
    consumer = AvaliacaoConsumer()
    signal.signal(signal.SIGINT, consumer.parar)
    signal.signal(signal.SIGTERM, consumer.parar)
    consumer.iniciar()


if __name__ == "__main__":
    main()
