package br.com.postech.techchallenge.fase4.integration;

import java.net.URI;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

public final class AwsClientFactory {

    private AwsClientFactory() {
    }

    public static DynamoDbClient dynamoDbClient() {
        DynamoDbClientBuilder builder = DynamoDbClient.builder().region(region());
        endpoint("AWS_ENDPOINT_URL_DYNAMODB", builder::endpointOverride);
        return builder.build();
    }

    public static SqsClient sqsClient() {
        SqsClientBuilder builder = SqsClient.builder().region(region());
        endpoint("AWS_ENDPOINT_URL_SQS", builder::endpointOverride);
        return builder.build();
    }

    private static Region region() {
        return Region.of(env("AWS_REGION", "us-east-1"));
    }

    private static void endpoint(String variable, java.util.function.Consumer<URI> consumer) {
        String value = System.getenv(variable);
        if (value != null && !value.isBlank()) {
            consumer.accept(URI.create(value));
        }
    }

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
