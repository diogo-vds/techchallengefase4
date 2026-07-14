package com.feedback.report.infrastructure.adapter.out.repository;


import com.feedback.report.application.port.out.FeedbackRepositoryPort;
import com.feedback.report.domain.model.Feedback;
import com.feedback.report.domain.model.UrgencyLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class DynamoDBFeedbackRepository implements FeedbackRepositoryPort {

    private final DynamoDbClient dynamoDbClient;
    private static final String TABLE_NAME = "Feedbacks";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public List<Feedback> findFeedbacksByDateRange(LocalDateTime start, LocalDateTime end) {
        log.info("Querying feedbacks from {} to {}", start, end);

        try {
            // Using GSI on dataEnvio for efficient date range queries
            Map<String, AttributeValue> expressionValues = Map.of(
                    ":start", AttributeValue.builder().s(start.format(DATE_TIME_FORMATTER)).build(),
                    ":end", AttributeValue.builder().s(end.format(DATE_TIME_FORMATTER)).build()
            );

            QueryRequest queryRequest = QueryRequest.builder()
                    .tableName(TABLE_NAME)
                    .indexName("dataEnvio-index")
                    .keyConditionExpression("dataEnvio BETWEEN :start AND :end")
                    .expressionAttributeValues(expressionValues)
                    .build();

            QueryResponse response = dynamoDbClient.query(queryRequest);

            return response.items().stream()
                    .map(this::mapToFeedback)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

        } catch (Exception e) {
            log.error("Error querying feedbacks", e);
            return Collections.emptyList();
        }
    }

    private Optional<Feedback> mapToFeedback(Map<String, AttributeValue> item) {
        try {
            return Optional.of(Feedback.builder()
                    .id(item.get("id").s())
                    .descricao(item.get("descricao").s())
                    .nota(Integer.parseInt(item.get("nota").n()))
                    .urgencia(UrgencyLevel.valueOf(item.get("urgencia").s()))
                    .dataEnvio(LocalDateTime.parse(item.get("dataEnvio").s(), DATE_TIME_FORMATTER))
                    .aulaId(item.get("aulaId").s())
                    .build());
        } catch (Exception e) {
            log.error("Error mapping feedback from DynamoDB", e);
            return Optional.empty();
        }
    }
}
