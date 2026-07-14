package com.feedback.report.application.service;


import com.feedback.report.application.port.in.GenerateReportUseCase;
import com.feedback.report.application.port.out.FeedbackRepositoryPort;
import com.feedback.report.application.port.out.NotificationPort;
import com.feedback.report.domain.model.Feedback;
import com.feedback.report.domain.model.UrgencyLevel;
import com.feedback.report.domain.model.WeeklyReport;
import com.feedback.report.domain.exception.ReportGenerationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class WeeklyReportService implements GenerateReportUseCase {

    private final FeedbackRepositoryPort feedbackRepository;
    private final NotificationPort notificationPort;

    @Override
    public WeeklyReport generateWeeklyReport(LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Generating weekly report from {} to {}", startDate, endDate);

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            List<Feedback> feedbacks = feedbackRepository
                    .findFeedbacksByDateRange(startDateTime, endDateTime);

            if (feedbacks.isEmpty()) {
                log.warn("No feedbacks found for period {} to {}", startDate, endDate);
                return createEmptyReport(startDate, endDate);
            }

            WeeklyReport report = buildReport(startDate, endDate, feedbacks);
            notificationPort.sendWeeklyReport(report);

            log.info("Weekly report generated successfully with {} feedbacks",
                    report.getTotalAvaliacoes());

            return report;

        } catch (Exception e) {
            log.error("Error generating weekly report", e);
            throw new ReportGenerationException("Failed to generate weekly report", e);
        }
    }

    private WeeklyReport buildReport(LocalDate startDate, LocalDate endDate,
                                     List<Feedback> feedbacks) {
        Map<LocalDate, Long> avaliacoesPorDia = feedbacks.stream()
                .collect(Collectors.groupingBy(
                        f -> f.getDataEnvio().toLocalDate(),
                        Collectors.counting()
                ));

        Map<UrgencyLevel, Long> avaliacoesPorUrgencia = feedbacks.stream()
                .collect(Collectors.groupingBy(
                        Feedback::getUrgencia,
                        Collectors.counting()
                ));

        double mediaGeral = feedbacks.stream()
                .mapToInt(Feedback::getNota)
                .average()
                .orElse(0.0);

        List<Feedback> urgentes = feedbacks.stream()
                .filter(Feedback::isUrgent)
                .collect(Collectors.toList());

        double mediaUrgentes = urgentes.stream()
                .mapToInt(Feedback::getNota)
                .average()
                .orElse(0.0);

        return WeeklyReport.builder()
                .dataInicio(startDate)
                .dataFim(endDate)
                .avaliacoesPorDia(avaliacoesPorDia)
                .avaliacoesPorUrgencia(avaliacoesPorUrgencia)
                .mediaGeral(mediaGeral)
                .mediaUrgentes(mediaUrgentes)
                .totalAvaliacoes(feedbacks.size())
                .totalUrgentes(urgentes.size())
                .build();
    }

    private WeeklyReport createEmptyReport(LocalDate startDate, LocalDate endDate) {
        return WeeklyReport.builder()
                .dataInicio(startDate)
                .dataFim(endDate)
                .avaliacoesPorDia(Map.of())
                .avaliacoesPorUrgencia(Map.of())
                .mediaGeral(0.0)
                .mediaUrgentes(0.0)
                .totalAvaliacoes(0)
                .totalUrgentes(0)
                .build();
    }
}
