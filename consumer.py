#!/usr/bin/env python3
"""
Consumer de Avaliações - Consome mensagens da fila RabbitMQ
Uso: python consumer.py
"""

import pika
import json
import sys
from datetime import datetime

class AvaliacaoConsumer:
    def __init__(self, host='localhost', port=5672, user='guest', password='guest'):
        self.host = host
        self.port = port
        self.user = user
        self.password = password
        self.connection = None
        self.channel = None
        self.queue_name = 'avaliacao.queue'
        self.exchange_name = 'avaliacao.exchange'
        self.routing_key = 'avaliacao.routing.key'
    
    def conectar(self):
        """Conecta ao RabbitMQ"""
        try:
            credentials = pika.PlainCredentials(self.user, self.password)
            parameters = pika.ConnectionParameters(
                host=self.host,
                port=self.port,
                credentials=credentials,
                heartbeat=600,
                blocked_connection_timeout=300
            )
            
            self.connection = pika.BlockingConnection(parameters)
            self.channel = self.connection.channel()
            
            # Declarar exchange e fila (idempotente)
            self.channel.exchange_declare(
                exchange=self.exchange_name,
                exchange_type='direct',
                durable=True
            )
            
            self.channel.queue_declare(
                queue=self.queue_name,
                durable=True
            )
            
            self.channel.queue_bind(
                exchange=self.exchange_name,
                queue=self.queue_name,
                routing_key=self.routing_key
            )
            
            print(f"✅ Conectado ao RabbitMQ: {self.host}:{self.port}")
            return True
        
        except Exception as e:
            print(f"❌ Erro ao conectar: {e}")
            return False
    
    def processar_avaliacao(self, ch, method, properties, body):
        """Callback para processar cada avaliação"""
        try:
            avaliacao = json.loads(body)
            
            # Processar avaliação
            print("\n" + "="*60)
            print(f"📥 Nova Avaliação Recebida!")
            print(f"   ID: {avaliacao.get('id')}")
            print(f"   Descrição: {avaliacao.get('descricao')}")
            print(f"   Nota: {avaliacao.get('nota')}/10")
            print(f"   Urgência: {avaliacao.get('urgencia')}")
            print(f"   Data: {avaliacao.get('dataEnvio')}")
            print("="*60)
            
            # Aqui você implementaria a lógica de processamento
            # Por exemplo: salvar em banco de dados, enviar email, etc.
            self.salvar_em_banco_dados(avaliacao)
            
            # Reconhecer recebimento
            ch.basic_ack(delivery_tag=method.delivery_tag)
            print(f"✅ Avaliação processada com sucesso!")
        
        except json.JSONDecodeError as e:
            print(f"❌ Erro ao decodificar JSON: {e}")
            ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
        
        except Exception as e:
            print(f"❌ Erro ao processar avaliação: {e}")
            # Nack sem requeue envia para DLQ
            ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
    
    def salvar_em_banco_dados(self, avaliacao):
        """
        Simula salvamento em banco de dados
        TODO: Implementar integração real com DynamoDB/RDS
        """
        # Exemplo: salvar em arquivo
        with open('avaliacoes_processadas.jsonl', 'a') as f:
            entrada = {
                'processado_em': datetime.now().isoformat(),
                'avaliacao': avaliacao
            }
            f.write(json.dumps(entrada, ensure_ascii=False) + '\n')
    
    def iniciar_consumo(self):
        """Inicia o consumo de mensagens"""
        try:
            if not self.conectar():
                sys.exit(1)
            
            # Configurar consumer
            self.channel.basic_qos(prefetch_count=1)
            self.channel.basic_consume(
                queue=self.queue_name,
                on_message_callback=self.processar_avaliacao
            )
            
            print(f"\n🐰 Iniciando consumo da fila '{self.queue_name}'...")
            print("👂 Aguardando mensagens (Ctrl+C para parar)\n")
            
            self.channel.start_consuming()
        
        except KeyboardInterrupt:
            print("\n\n🛑 Interrompido pelo usuário")
            self.desconectar()
        
        except Exception as e:
            print(f"❌ Erro durante consumo: {e}")
            self.desconectar()
            sys.exit(1)
    
    def desconectar(self):
        """Desconecta do RabbitMQ"""
        if self.connection and not self.connection.is_closed:
            self.connection.close()
            print("✅ Desconectado do RabbitMQ")

def main():
    """Função principal"""
    import os
    
    # Ler variáveis de ambiente (com defaults para local)
    host = os.getenv('RABBITMQ_HOST', 'localhost')
    port = int(os.getenv('RABBITMQ_PORT', '5672'))
    user = os.getenv('RABBITMQ_USER', 'guest')
    password = os.getenv('RABBITMQ_PASSWORD', 'guest')
    
    print("="*60)
    print("🐰 RabbitMQ Avaliacao Consumer")
    print("="*60)
    print(f"Host: {host}")
    print(f"Port: {port}")
    print(f"User: {user}")
    print("="*60 + "\n")
    
    consumer = AvaliacaoConsumer(
        host=host,
        port=port,
        user=user,
        password=password
    )
    
    consumer.iniciar_consumo()

if __name__ == '__main__':
    main()
