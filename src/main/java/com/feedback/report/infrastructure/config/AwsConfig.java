package com.feedback.report.infrastructure.config;


import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.ses.SesClient;

public class AwsConfig {

    private static final Region REGION = Region.US_EAST_1;

    public static DynamoDbClient createDynamoDbClient() {
        return DynamoDbClient.builder()
                .region(REGION)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public static SesClient createSesClient() {
        return SesClient.builder()
                .region(REGION)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
