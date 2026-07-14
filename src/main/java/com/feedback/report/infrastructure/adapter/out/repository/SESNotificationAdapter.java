package com.feedback.report.infrastructure.adapter.out.repository;


import com.feedback.report.application.port.out.NotificationPort;
import com.feedback.report.domain.model.UrgencyLevel;
import com.feedback.report.domain.model.WeeklyReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class SESNotificationAdapter implements NotificationPort {

    private final SesClient sesClient;
    private static final String FROM_EMAIL = "reports@feedback-system.com";
    private static final String TO_EMAIL = "admin@feedback-system.com";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void sendWeeklyReport(WeeklyReport report) {
        try {
            log.info("Sending weekly report via SES");

            String subject = String.format("Relatório Semanal de Feedbacks - %s a %s",
                    report.getDataInicio().format(DATE_FORMATTER),
                    report.getDataFim().format(DATE_FORMATTER));

            String body = buildReportBody(report);

            SendEmailRequest request = SendEmailRequest.builder()
                    .source(FROM_EMAIL)
                    .destination(Destination.builder()
                            .toAddresses(TO_EMAIL)
                            .build())
                    .message(Message.builder()
                            .subject(Content.builder()
                                    .data(subject)
                                    .build())
                            .body(Body.builder()
                                    .html(Content.builder()
                                            .data(body)
                                            .build())
                                    .build())
                            .build())
                    .build();

            SendEmailResponse response = sesClient.sendEmail(request);
            log.info("Report email sent. Message ID: {}", response.messageId());

        } catch (Exception e) {
            log.error("Error sending email notification", e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }

    private String buildReportBody(WeeklyReport report) {
        StringBuilder html = new StringBuilder();

        html.append("<html><body style='font-family: Arial, sans-serif;'>");
        html.append("<h2>📊 Relatório Semanal de Feedbacks</h2>");
        html.append(String.format("<p><strong>Período:</strong> %s a %s</p>",
                report.getDataInicio().format(DATE_FORMATTER),
                report.getDataFim().format(DATE_FORMATTER)));

        html.append("<h3>📈 Métricas Gerais</h3>");
        html.append("<ul>");
        html.append(String.format("<li><strong>Total de Avaliações:</strong> %d</li>",
                report.getTotalAvaliacoes()));
        html.append(String.format("<li><strong>Média Geral:</strong> %.1f/10</li>",
                report.getMediaGeral()));
        html.append(String.format("<li><strong>Avaliações Urgentes:</strong> %d</li>",
                report.getTotalUrgentes()));
        html.append(String.format("<li><strong>Média das Urgentes:</strong> %.1f/10</li>",
                report.getMediaUrgentes()));
        html.append("</ul>");

        if (!report.getAvaliacoesPorDia().isEmpty()) {
            html.append("<h3>📅 Avaliações por Dia</h3>");
            html.append("<ul>");
            report.getAvaliacoesPorDia().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        html.append(String.format("<li>%s: %d avaliações</li>",
                                entry.getKey().format(DATE_FORMATTER),
                                entry.getValue()));
                    });
            html.append("</ul>");
        }

        if (!report.getAvaliacoesPorUrgencia().isEmpty()) {
            html.append("<h3>⚠️ Avaliações por Urgência</h3>");
            html.append("<ul>");
            report.getAvaliacoesPorUrgencia().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        html.append(String.format("<li><strong>%s:</strong> %d avaliações</li>",
                                entry.getKey().getDescricao(),
                                entry.getValue()));
                    });
            html.append("</ul>");
        }

        html.append("<p><hr>");
        html.append("<p style='color: #666; font-size: 12px;'>");
        html.append("Este é um relatório automático gerado pelo sistema de feedbacks.</p>");
        html.append("</body></html>");

        return html.toString();
    }
}
