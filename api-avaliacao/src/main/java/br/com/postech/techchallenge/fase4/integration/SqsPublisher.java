package br.com.postech.techchallenge.fase4.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.postech.techchallenge.fase4.model.Avaliacao;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class SqsPublisher implements QueuePublisher {

    private final SqsClient client;
    private final String queueUrl;
    private final ObjectMapper mapper;

    public SqsPublisher() {
        this(AwsClientFactory.sqsClient(), requiredEnv("SQS_QUEUE_URL"), new ObjectMapper());
    }

    SqsPublisher(SqsClient client, String queueUrl, ObjectMapper mapper) {
        this.client = client;
        this.queueUrl = queueUrl;
        this.mapper = mapper;
    }

    @Override
    public void publicar(Avaliacao avaliacao) {
        try {
            String body = mapper.writeValueAsString(new AvaliacaoRecebidaEvent(
                    avaliacao.getId(), "AVALIACAO_RECEBIDA"));
            client.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(body)
                    .build());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro ao serializar evento da avaliacao", e);
        }
    }

    private static String requiredEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Variavel de ambiente obrigatoria: " + name);
        }
        return value;
    }

    private record AvaliacaoRecebidaEvent(String avaliacaoId, String evento) {
    }
}
