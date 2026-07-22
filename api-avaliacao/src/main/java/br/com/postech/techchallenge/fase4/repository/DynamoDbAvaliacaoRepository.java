package br.com.postech.techchallenge.fase4.repository;

import java.util.HashMap;
import java.util.Map;

import br.com.postech.techchallenge.fase4.integration.AwsClientFactory;
import br.com.postech.techchallenge.fase4.model.Avaliacao;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class DynamoDbAvaliacaoRepository implements AvaliacaoRepository {

    private final DynamoDbClient client;
    private final String tableName;

    public DynamoDbAvaliacaoRepository() {
        this(AwsClientFactory.dynamoDbClient(), env("DYNAMODB_TABLE", "avaliacoes"));
    }

    DynamoDbAvaliacaoRepository(DynamoDbClient client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    @Override
    public Avaliacao salvar(Avaliacao avaliacao) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", string(avaliacao.getId()));
        item.put("descricao", string(avaliacao.getDescricao()));
        item.put("nota", AttributeValue.builder().n(avaliacao.getNota().toString()).build());
        item.put("urgencia", string(avaliacao.getUrgencia().name()));
        item.put("dataEnvio", string(avaliacao.getDataEnvio().toString()));

        client.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .conditionExpression("attribute_not_exists(id)")
                .build());
        return avaliacao;
    }

    private static AttributeValue string(String value) {
        return AttributeValue.builder().s(value).build();
    }

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
