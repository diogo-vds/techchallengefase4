package com.feedback.report.infrastructure.adapter.in.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.feedback.report.application.port.in.GenerateReportUseCase;
import com.feedback.report.infrastructure.adapter.out.repository.DynamoDBFeedbackRepository;

import com.feedback.report.infrastructure.adapter.out.repository.SESNotificationAdapter;
import com.feedback.report.infrastructure.config.AwsConfig;
import com.feedback.report.application.service.WeeklyReportService;
import com.feedback.report.domain.model.WeeklyReport;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.ses.SesClient;

import java.time.LocalDate;

import java.util.Map;

@Slf4j
public class WeeklyReportHandler implements RequestHandler<Map<String, Object>, String> {

    private final GenerateReportUseCase reportUseCase;
    private final ObjectMapper objectMapper;

    public WeeklyReportHandler() {
        // Initialize AWS clients
        DynamoDbClient dynamoDbClient = AwsConfig.createDynamoDbClient();
        SesClient sesClient = AwsConfig.createSesClient();

        // Initialize repositories and adapters
        var feedbackRepository = new DynamoDBFeedbackRepository(dynamoDbClient);
        var notificationAdapter = new SESNotificationAdapter(sesClient);

        // Initialize service
        this.reportUseCase = new WeeklyReportService(feedbackRepository, notificationAdapter);

        // Initialize ObjectMapper
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        try {
            log.info("Starting weekly report generation");

            // Define weekly period (last 7 days)
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(7);

            log.info("Generating report for period: {} to {}", startDate, endDate);

            WeeklyReport report = reportUseCase.generateWeeklyReport(startDate, endDate);

            String jsonReport = objectMapper.writeValueAsString(report);
            log.info("Weekly report generated successfully");

            return jsonReport;

        } catch (Exception e) {
            log.error("Error generating weekly report", e);
            throw new RuntimeException("Failed to generate report", e);
        }
    }
}
